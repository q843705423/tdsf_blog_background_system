package com.tiandisifang.db;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;
import java.util.Map.Entry;

import org.springframework.stereotype.Component;


@Component
//this java is for connect mysql,param if want to change,please go to db.properties
public class DBUtil {

	private static String url;

	private static String driver;
	
	private static String username;
	
	private static String password;
	
	private Connection con = null;
	
	private Statement stmt  = null;
	
	private boolean transactionIsOpen = false;
	
	/**
	 * 利用db.properties 获取数据库连接参数
	 */
	public DBUtil(){
	
		try {
	
			
			
			ResourceBundle rb = ResourceBundle.getBundle("db");
			
			driver   = rb.getString("driver");//com.mysql.jdbc.Driver
			
			username = rb.getString("username");
		 
			password = rb.getString("password");
		
			url      = rb.getString("url");
			
			Class.forName(driver);
		
			con = (Connection) DriverManager.getConnection(url, username, password);
	
			stmt = (Statement) con.createStatement();
	
		} catch (SQLException e) {
	
			e.printStackTrace();
	
		} catch (ClassNotFoundException e) {
			
			e.printStackTrace();
			
		}
		
	}
	public DBUtil(String driver,String username,String password,String url) {
		
		
		try {
			Class.forName(driver);
			System.out.println(url);
			con = DriverManager.getConnection(url, username,password);
			stmt = (Statement) con.createStatement();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	/**
	 * 获得数据库连接
	 * @return
	 * @throws SQLException 
	 */
	private  Connection getConnection() throws SQLException{
	
		if(con!=null&&!con.isClosed())return con;
		
		try {
		
			con = (Connection) DriverManager.getConnection(url, username, password);
	
		} catch (SQLException e) {
	
			e.printStackTrace();
	
		}
	
		return con;

	}
	/**
	 * 关闭数据库链接
	 */
	private void closeConnection(){
		if(transactionIsOpen)return;//如果事务已经打开，就先不关闭连接了，保证是同一个连接
		
		try{
			
			if (!(this.con==null))this.con.close();
				
		}catch(SQLException e){
		
			e.printStackTrace();
		
		}
	
		this.con = null;
	}

	protected String tableField2ObjProperty(String field){
		
		
		String property = "";
		boolean can = false;
		for(int i=0;i<field.length();i++){
			char c = field.charAt(i);
			if(c=='_'){
				can = true;
				continue;
			}else{
				if(can&&c>='a'&&c<='z'){
					can = false;
					property += (char)(c-'a'+'A');
				}else{
					property += c;
				}
			}
			
			
			
			
		}
		
		return property;
	}
	/**
	 * 
	 * @param sql
	 * @return
	*/
	public List<HashMap<String,String>> select (String sql){
		
		return select(sql,true);
		
	}
	public List<HashMap<String,String>> select (String sql,boolean zhuan){
		
		
		List<HashMap<String, String>> ls= new ArrayList<HashMap<String,String>>();
		try{
			
			ArrayList<String> tableFields = getTableFieldsBySql(sql);
			
			 Connection ct = getConnection();
			
			 stmt=stmt==null||stmt.isClosed()?(Statement) ct.createStatement():stmt;
					
			ResultSet rs =	stmt.executeQuery(sql);
		
			while(rs.next()){
				
				HashMap<String,String>map = new HashMap<String, String>();
				
				for(String tfield:tableFields){
					
					
					String property = zhuan?tableField2ObjProperty(tfield):tfield;
					
					String value = rs.getString(tfield);
					
					if(value!=null)
						map.put(property, value);
					
					
					
				}
				ls.add(map);
		
			}
			closeConnection();
			
			
			
		}catch (NullPointerException e) {
			System.out.println("资源访问过于频繁,请重试");
		}
		
		catch(Exception e){
			e.printStackTrace();
		}
		
		
		return ls;
		
	}
	public List<HashMap<String,String>> selectHasException (String sql,boolean zhuan)throws Exception{
		
		
		List<HashMap<String, String>> ls= new ArrayList<HashMap<String,String>>();

			
		ArrayList<String> tableFields = getTableFieldsBySql(sql);
		
		
		Connection ct = getConnection();
		
		 
		stmt=stmt==null||stmt.isClosed()?(Statement) ct.createStatement():stmt;
				
		ResultSet rs =	stmt.executeQuery(sql);
	
		while(rs.next()){
			
			HashMap<String,String>map = new HashMap<String, String>();
			
			for(String tfield:tableFields){
				
				
				String property = zhuan?tableField2ObjProperty(tfield):tfield;
				
				String value = rs.getString(tfield);
				
				if(value!=null)
					map.put(property, value);
				
				
				
			}
			ls.add(map);
	
		}
		closeConnection();
			
			
	
		
		
		return ls;
		
	}
	
	public  <T> List<T> select(String sql,Class<T> cs,HashMap<String,String>hm){
		
		ArrayList<T>objs = new ArrayList<T>();
		
		try{
			
			HashMap<String, String> hasObjectField = new HashMap<String, String>();	
			
			System.out.println("db162="+sql);
			
			ArrayList<String> tableFields = getTableFieldsBySql(sql);
			
			Field[] objectfields = cs.getDeclaredFields();
			

			for(Field f:objectfields){
				
				hasObjectField.put(f.getName(), "1");
				
			}
			
			ArrayList<String>selectField = new ArrayList<String>();
			
			for(String tbfield:tableFields){
				
				if(hasObjectField.get(tableField2ObjProperty(tbfield))!=null){
					
					selectField.add(tbfield);
					
				}
				
			}

			Connection ct = getConnection();

			stmt=stmt==null||stmt.isClosed()?(Statement) ct.createStatement():stmt;
			
			ResultSet rs =	stmt.executeQuery(sql);
			
			while(rs.next()){
			
				@SuppressWarnings("deprecation")
				T obj = cs.newInstance();
				
				for(String tbfield :selectField){
					
					setObjectValueFromRs(rs, obj, tbfield,tableField2ObjProperty(tbfield) );
					
				}

				objs.add(obj);
			}
	
		//	closeConnection();
		
		}catch (Exception e) {
		
			e.printStackTrace();
		
		}
		
		return objs;
	}
	

	
	/**
	 * 取出当前resultSet里当前位置的tbField表字段，设置给obj对象的objField对象字段
	 * @param rs
	 * @param obj
	 * @param tbfield
	 * @param objfield
	 */
	private <T> void setObjectValueFromRs(ResultSet rs,  T obj,String tbfield,String objfield)  {
		
		Class<? extends Object> cs = obj.getClass();

		Method method  = null;

		String methodName = "set"+objfield.substring(0,1).toUpperCase()+objfield.substring(1,objfield.length());
		
		
		
		try{
			Field f = cs.getDeclaredField(objfield);
			
			Class<?> type = f.getType();
			
			method = cs.getDeclaredMethod(methodName, type);
			
		
		
			if(type.equals(boolean.class)||type.equals(Boolean.class)){
				
				method.invoke(obj, rs.getBoolean(tbfield));
				
			}else if(type.equals(byte.class)||type.equals(Byte.class)){
				
				method.invoke(obj, rs.getByte(tbfield));
				
			}else if(type.equals(short.class)||type.equals(Short.class)){
				
				method.invoke(obj, rs.getShort(tbfield));
				
			}else if(type.equals(int.class)||type.equals(Integer.class)){
				
				
				if(type.equals(int.class)){
					method.invoke(obj, rs.getInt(tbfield));
				}else{
					method.invoke(obj, Integer.valueOf(rs.getInt(tbfield)));
				}
				
				
			}else if(type.equals(float.class)||type.equals(Float.class)){
				
				method.invoke(obj, rs.getFloat(tbfield));
				
			}else if(type.equals(double.class)||type.equals(Double.class)){
				
				method.invoke(obj, rs.getDouble(tbfield));
				
			}else if(type.equals(long.class)||type.equals(Long.class)){
				
				method.invoke(obj, rs.getLong(tbfield));
				
			}else{
				
				method.invoke(obj, rs.getString(tbfield));
				
			}
		
		}catch(Exception e){
			
			e.printStackTrace();
			System.out.println("字段类型映射错误");
		}
	}

	/**
	 * hashmap 对象的属性映射到表的字段名
	 * @param obj   要插入表内的对象
	 * @param table 表的名字
	 * @param map   对象属性到表的字段的映射
	 * @throws SQLException 
	 */
	public  void  insert(Object obj,String table,HashMap<String,String>map) throws SQLException{
		
		Map<String, String> m = new HashMap<String, String>();
		
		Class<? extends Object> cs = obj.getClass();
	
		Field []objFields = cs.getDeclaredFields();
	
		ArrayList<String>  tableFiled =getTableFieldsBySql("select * from "+table);
	
		if(map==null){
		
			map = new HashMap<String, String>();
		
			for(int i=0;i<objFields.length;i++){
	
				map.put(objFields[i].getName(), objFields[i].getName());
	
			}
	
		}
		
		for(int i=0;i<tableFiled.size();i++){
		
			m.put(tableFiled.get(i).toString().toUpperCase(), "1");
	
		}
	
		HashMap<String, String>mym = new HashMap<String, String>();
	
		for(int i=0;i<objFields.length;i++){
	
			String name = objFields[i].getName();
		
			if(map.get(name)==null){
		
				if("1".equals(m.get(name.toUpperCase()))){
		
					mym.put(name, name);
			
				}
			
			}else{
			
				if("1".equals(m.get(map.get(name).toUpperCase()))){
			
					mym.put(name,map.get(name) );
			
				}
		
			}
		
		}
		
		Iterator<Entry<String, String>> entries = mym.entrySet().iterator();  

		ArrayList<String> values = new ArrayList<String>();
	
		String fields = "";
	
		while (entries.hasNext()) {  
			
		    Entry<String, String> entry = entries.next();  
		    
		    try {
		    	
				Method method = cs.getDeclaredMethod("get"+entry.getKey().substring(0, 1).toUpperCase() + entry.getKey().substring(1));
				
				String ans = 	 method.invoke(obj).toString();
				
				if(ans!=null){
					
					if(values.size()!=0)fields+=",";
				
					fields += entry.getValue();
				
					values.add(ans);

				}
				
		    } catch (SecurityException e) {
			
		    	System.out.println("反射解析错误");
				
		    	e.printStackTrace();
			
		    } catch (NoSuchMethodException e) {
			
		    	System.out.println("方法名错误");
			
		    	e.printStackTrace();
			
		    } catch (IllegalArgumentException e) {
		
		    	e.printStackTrace();
			
		    } catch (IllegalAccessException e) {
			
		    	e.printStackTrace();
			
		    } catch (InvocationTargetException e) {
			
		    	e.printStackTrace();
			
		    }
		   
		}
		
		String []valuess = new String[values.size()];
		
		for(int j=0;j<values.size();j++){
		
			valuess[j] = values.get(j);
	
		}
	
		insert(table, fields, valuess);
	
		
	}
	/**
	 * 插入
	 * @param table 被插入的表名
	 * @param fields 要插入的字段 
	 * @param values 字段的值
	 * @return
	 * @throws SQLException 
	 */
	public  boolean insert2(String table,String fields,Object[]values) throws SQLException{
		
		Connection ct = null;		
		
		try {
		
			Class.forName(driver);
		
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		
		}
	
		ct = getConnection();
			
		stmt=stmt==null||stmt.isClosed()?(Statement) ct.createStatement():stmt;
			
		String sql = "insert into "+ table+"("+fields+")values(";
		
		for(int i=0;i<values.length;i++){
		
			if(i!=0)sql+=",";
			
			
			String x = "'"+values[i]+"'";
			
			if(x.equals("'true'")){
				
				x = "'1'";
			}else if(x.equals("'false'")){
				
				x="'0'";
			}
		
			sql += x;
			
			
			
		}
		
		sql += ")";

		stmt.executeUpdate(sql);
		
		closeConnection();
		
		return true;
	}
	
	
	
	/**
	 * 
	 * @param table
	 * @param fields
	 * @param values
	 * @param notToPut 不想被插入的值,多個可以用逗號隔開
	 * @return
	 * @throws SQLException
	 */
	public  boolean insert2(String table,String fields,Object[]values,String notToPut) throws SQLException{
		
		Connection ct = null;
		
		boolean ok[] = new boolean[fields.length()];
		
		for(int i=0;i<ok.length;i++){
			
			ok[i] = true;
			
		}
		
		String[]nots = notToPut.split(",");
		String[]fieldss = fields.split(",");
	
		if(nots!=null){
			
			for(int i=0;i<fieldss.length;i++){
				
				for(String n:nots){
					
					if(n.equals(fieldss[i])){
						
						ok[i] = false;
						
					}
					
				}
				
			}
			
		}
		
		
		
		try {
		
			Class.forName(driver);
		
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		
		}
	
		ct = getConnection();
			
		stmt=stmt==null||stmt.isClosed()?(Statement) ct.createStatement():stmt;
			
		String sql = "insert into "+ table+"("+fields+")values(";
		
		for(int i=0;i<values.length;i++){
		
			
			if(i!=0)sql+=",";
			
			
			
			String x = "'"+values[i]+"'";
			
			if(!ok[i]){
				
				x="null";
				
			}
			
			if(x.equals("'true'")){
				
				x = "'1'";
			}else if(x.equals("'false'")){
				
				x="'0'";
			}
				
//			sql += "'"+values[i]+"'";
				
			sql += x;
			
			
			
		}

		sql += ")";

		stmt.executeUpdate(sql);
		
		closeConnection();
		
		return true;
	}
	
	
	
	public void insert2(Object obj,String table) throws SQLException{
		
		insert2(obj, table, (HashMap)null,"");
	
	}
	public void insert2(Object obj,String table,String notPut) throws SQLException{
		
		insert2(obj,table,(HashMap)null,notPut);
		
	
	}

	/**
	 * 增强版的insert  可以插入的不仅仅是String类型
	 * 
	 * @param obj
	 * @param table
	 * @param map 映射 
	 * @param notToput 存放不想被插入的字段名 多个可以用逗号隔开
	 * @throws SQLException
	 */
	public  void  insert2(Object obj,String table,HashMap<String,String>map,String notToput) throws SQLException{
		
		Map<String, String> m = new HashMap<String, String>();
		
		Class<? extends Object> cs = obj.getClass();
	
		Field []objFields = cs.getDeclaredFields();
	
		ArrayList<String>  tableFiled =getTableFieldsBySql("select * from "+table);
		
		String nots[]  = notToput.split(",");
	
		if(map==null){
		
			map = new HashMap<String, String>();
		
			for(int i=0;i<objFields.length;i++){
	
				if(nots==null)
				
					map.put(objFields[i].getName(), objFields[i].getName());
	
				else{
					
					boolean canPut = true;
					
					for(String str:nots){
						
						if(str.equals(objFields[i].getName())){
							
							canPut = false;
							
						}
						
					}
					
					if(canPut){
					
						map.put(objFields[i].getName(), objFields[i].getName());
						
					}
					
				}
				
			}
	
		}
		
		for(int i=0;i<tableFiled.size();i++){
		
			m.put(tableFiled.get(i).toString().toUpperCase(), "1");
	
		}
	
		HashMap<String, String>mym = new HashMap<String, String>();
	
		for(int i=0;i<objFields.length;i++){
	
			String name = objFields[i].getName();
		
			if(map.get(name)==null){
		
				if("1".equals(m.get(name.toUpperCase()))){
		
					mym.put(name, name);
			
				}
			
			}else{
			
				if("1".equals(m.get(map.get(name).toUpperCase()))){
			
					mym.put(name,map.get(name) );
			
				}
		
			}
		
		}
		
		Iterator<Entry<String, String>> entries = mym.entrySet().iterator();  

		ArrayList<Object> values = new ArrayList<Object>();
	
		String fields = "";
	
		while (entries.hasNext()) {  
			
		    Entry<String, String> entry = entries.next();  
		    
		    try {
		    	
		    	String key = entry.getKey().length()>2&&entry.getKey().substring(0,2).equals("is")
		    			&&entry.getKey().charAt(2)>='A'&&entry.getKey().charAt(2)<='Z'
		    			?entry.getKey():"get"+entry.getKey().substring(0, 1).toUpperCase() + entry.getKey().substring(1);
		    	
		    	
				Method method = cs.getDeclaredMethod(key,null);
				
				Object o = method.invoke(obj);
				
				
				
				Object ans =  o;
				
				if(ans!=null){
					
					if(values.size()!=0)fields+=",";
				
					fields += entry.getValue();
				
					values.add(ans);

				}
				
		    } catch (SecurityException e) {
			
		    	System.out.println("反射解析错误");
				
		    	e.printStackTrace();
			
		    } catch (NoSuchMethodException e) {
			
		    	System.out.println("方法名错误");
			
		    	e.printStackTrace();
			
		    } catch (IllegalArgumentException e) {
		
		    	e.printStackTrace();
			
		    } catch (IllegalAccessException e) {
			
		    	e.printStackTrace();
			
		    } catch (InvocationTargetException e) {
			
		    	e.printStackTrace();
			
		    }
		   
		}
		
		Object []valuess = new Object[values.size()];
		
		for(int j=0;j<values.size();j++){
		
			valuess[j] = values.get(j);
	
		}
	
		insert2(table, fields, valuess,notToput);
	
		
	}
	
	
	public  void  insert(Object obj,String table) throws SQLException{
		
		 insert(obj,table,null);

	}
	
	public  <T> List<T> select(String sql,Class<T> cs){

		return  select(sql, cs, null);

	}
	
	
	
	/**
	 * 插入
	 * @param table 被插入的表名
	 * @param fields 要插入的字段 
	 * @param values 字段的值
	 * @return
	 * @throws SQLException 
	 */
	public  boolean insert(String table,String fields,String[]values) throws SQLException{
		
		Connection ct = null;		
		
		try {
		
			Class.forName(driver);
		
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		
		}
	
		ct = getConnection();
			
		stmt=stmt==null||stmt.isClosed()?(Statement) ct.createStatement():stmt;
			
		String sql = "insert into "+ table+"("+fields+")values(";
		
		for(int i=0;i<values.length;i++){
		
			if(i!=0)sql+=",";
			
			sql += "'"+values[i]+"'";
			
		}
		
		sql += ")";

		stmt.executeUpdate(sql);
		
		closeConnection();
		
		return true;
	}
	/**
	 * DB.delete(class_info,"classID="+classID);
	 * @param table
	 * @param condition
	 */
	public  void delete(String table,String condition){
		Connection ct = null;
		
		try {
		
			Class.forName(driver);
		
			ct = getConnection();
		
			stmt=(Statement) ct.createStatement();
		
			String sql = "delete from "+ table;
			
			sql += " where "+condition;
			
			stmt.executeUpdate(sql);
			
		} catch (ClassNotFoundException e) {
			
			e.printStackTrace();
		
		} catch (SQLException e) {
		
			e.printStackTrace();
		
		}finally{
		
			closeConnection();
	
		}

	}
	public  void update(String table,String condition,Object obj) throws Exception{
		
		update(table, condition, obj, null);
	
	}
	public  void update(String table,String condition,Object obj,HashMap<String, String>hm) throws Exception{
		
		Map<String, String> m = new HashMap<String, String>();
		
		ArrayList<String> ls_tableFields = getTableFieldsBySql("select * from "+table);
		
		ArrayList<String> ls_obj = getClassFields(obj.getClass());
		
		for(int i=0;i<ls_tableFields.size();i++){
		
			m.put(ls_tableFields.get(i).toUpperCase(), "1");
	
		}
		
		if(hm==null){
		
			hm = new HashMap<String, String>();
		
			for(int i=0;i<ls_obj.size();i++){
		
				hm.put(ls_obj.get(i),ls_obj.get(i));
		
			}
		
		}
		
		HashMap<String, String>mym = new HashMap<String, String>();
		
		for(int i = 0;i < ls_obj.size();i++){
			
			String name = ls_obj.get(i);
		
			if(hm.get(name)==null){
				
				if("1".equals(m.get(name.toUpperCase()))){
			
					mym.put(name, name);
			
				}
				
			}else{
				
				if("1".equals(m.get(hm.get(name).toUpperCase()))){
				
					mym.put(name,hm.get(name) );
			
				}
				
			}
		}
		
		String fields = "";
	
		ArrayList<String>vals = new ArrayList<String>();
		
		Iterator<Entry<String, String>> entries = mym.entrySet().iterator();

		Class<? extends Object> cs = obj.getClass();
		
		
		
		while (entries.hasNext()) {  
		
			Entry<String, String> entry = entries.next();
		
			String key = entry.getKey();
		
			String value = entry.getValue();
		
			String field = key.substring(0,1).toUpperCase()+key.substring(1);
		
			Method method = cs.getDeclaredMethod("get"+field,null);
		
			String ans  = (String) method.invoke(obj);
		
			if(ans!=null){
		
				fields += fields.length()==0?value:","+value;
			
				vals.add(ans);
		
			}

		}
		
		String values []= new String[vals.size()];
	
		for(int i=0;i<values.length;i++){
	
			values[i]  = vals.get(i);
	
		}
	
		System.out.println("db354="+fields);
	
		System.out.println("db355="+vals);
	
		if(vals.size()!=0)update(table, condition, fields, values);
		
	}

	
	public  void update(String table,String condition,String fields,String values[])throws Exception{
		
		Connection ct = null;
		
		Class.forName(driver);
		
		ct = getConnection();
		
		//stmt=(Statement) ct.createStatement();
		stmt=stmt==null||stmt.isClosed()?(Statement) ct.createStatement():stmt;
		
		String sql = "update "+ table + " set ";
		
		String []fs = fields.split(",");
		
		for(int i=0;i<fs.length;i++){
			
			if(i!=0)sql+=",";
			
			sql += fs[i]+"='"+values[i]+"'";
			
		}
		
		sql += " where "+condition;

		System.out.println(sql);
	
		stmt.executeUpdate(sql);
			
		closeConnection();

	}
	/**
	 * 
	 * @param sql  查询的sql语句
	 * @param cs   返回值的类型
	 * @return 返回符合条件的第一个对象
	 * @throws Exception 
	 */
	public  <T> T selectOne(String sql,Class<T>cs){

		List<T>ls =  select(sql, cs);
		
		if(ls==null||ls.size()==0)return null;
			
		return ls.get(0);
	}

	public HashMap<String,String> selectOne(String sql){

		 List<HashMap<String,String>>ls =  select(sql);
		
		if(ls==null||ls.size()==0)return null;
			
		return ls.get(0);
	}
	
	/**
	 * 
	 * @param cs 类的class
	 * @return 该类的所有属性名(不包含它的父类)
	 */
	private static <T> ArrayList<String> getClassFields(Class<T> cs){
		
		ArrayList<String>ls = new ArrayList<String>(); 
		
		Field[] fields = cs.getDeclaredFields();
		
		for(int i=0;i<fields.length;i++){
			
			ls.add(fields[i].getName());
	
		}
	
		return ls;
		
	}
	
	/**
	 * 获取该sql语句的条数
	 * @param sql
	 * @return
	 */
	public  int getRowsCountBySql(String sql){
		
		int num = 0;
	
		try{
		
			Class.forName(driver);
		
			Connection ct = getConnection();
		
			stmt = (Statement) ct.createStatement();
		
			ResultSet rs =	stmt.executeQuery(sql);
			
			while(rs.next()){
		
				num++;
			
			}
			
		}catch (Exception e) {
			
			e.printStackTrace();
			
			return -1;
		
		}finally{
		
			closeConnection();
		
		}
		
		return num;

	}
	
	/**
	 * 
	 * @param sql sql语句
	 * @return 该sql查询表的所有字段，可以使用联合查询
	 */

	private   ArrayList<String> getTableFieldsBySql(String sql){
		
		ArrayList<String> ls = new ArrayList<String>();
	
		Connection ct = null;
		
		

		try {
		
			Class.forName(driver);
		
			ct = getConnection();
		
	
			stmt=stmt==null||stmt.isClosed()?(Statement) ct.createStatement():stmt;
	
			ResultSet rs = stmt.executeQuery(sql);
		
			ResultSetMetaData data = (ResultSetMetaData) rs.getMetaData();
		
			int columnCount = data.getColumnCount();
			for(int i=1;i<=1000;i++){
				String label = data.getColumnLabel(i);
				String columnName = data.getColumnName(i);
				
			//	ls.add(columnName);
				ls.add(label);
				
			}
			

			
		} catch (SQLException e) {
			
		//	e.printStackTrace();
			return ls;
		} catch (ClassNotFoundException e) {
		
			e.printStackTrace();
		
		}finally{
	
			closeConnection();
	
		}

		return ls;
	
	}
	
	 
	/**
	 * 创建数据库
	 * @param dbName
	 * @return
	 * @throws Exception
	 */
	public static boolean CreateDB(String dbName) throws Exception{
		
		Class.forName(driver);
	   	
	 	Connection conn=(Connection) DriverManager.getConnection(url,username,password);
	 
	 	String sql = "create database if not exists "+ dbName +" Character Set UTF8;";
	 
	 	Statement stmt= (Statement) conn.createStatement();
	 
	 	stmt.executeUpdate(sql);
	 	
	 	conn.close();
	
	 	stmt.close();
	
	 	return true;
	
	}

	/**
	 * 简单的sql查询
	 * @param sqlTxt
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public ResultSet querySql(String sqlTxt) throws SQLException, ClassNotFoundException {

		Statement stmt = null;

		ResultSet rs = null;
		
		System.out.println(sqlTxt);
		
		try{
			
			Class.forName(driver);
		
			this.con = (Connection) DriverManager.getConnection(url, username, password);
			
			stmt = (Statement) this.con.createStatement();
			
			rs = stmt.executeQuery(sqlTxt);
		
		}catch(SQLException e){
		
			e.printStackTrace();
		
		}
		
		return rs;
	
	}
	

	/**
	 * 多个sql语句  增加 删除 修改
	 * @param sqlTxt
	 * @return
	 * @throws SQLException
	 */
	public int excuteSql(String[] sqlTxt) throws SQLException	{
	
		int flag = 0;
		
		Statement stmt = null;
		
		try {

			this.con.setAutoCommit(false);
			
			stmt = this.con.createStatement();

			for (int i = 0; i < sqlTxt.length; i++)
			
				flag = stmt.executeUpdate(sqlTxt[i]);
				
			this.con.commit();
		
		}catch (SQLException s) {
		
			s.printStackTrace();

		} finally {
			
			this.closeConnection();
	
		}
		
		return flag;
	
	}
	public int excuteSql(List<String> sqlTxt) throws SQLException	{
		
		int flag = 0;
		
		Statement stmt = null;
		
		try {

			con = getConnection();
			
			this.con.setAutoCommit(false);
			
			stmt = this.con.createStatement();

			for (int i = 0; i < sqlTxt.size(); i++)
			
				flag = stmt.executeUpdate(sqlTxt.get(i));
				
			this.con.commit();
		
		}catch (SQLException s) {
		
			s.printStackTrace();

		} finally {
			
			this.closeConnection();
	
		}
		
		return flag;
	
	}
	
	
	/**
	 * 打开事务
	 */
	public void openTransaction(){
		
		
		try {
		
			//事务开始，不关闭连接，保证出错时能够正常回滚
			transactionIsOpen = true;  
			
			this.con.setAutoCommit(false);
		
			stmt = (Statement) this.con.createStatement();

		} catch (SQLException e) {
		
			e.printStackTrace();
		}
		
	}
	/**
	 * 提交并且关闭事务
	 */
	public void commitAndCloseTransaction(){
		
		try {
			
			
			this.con.commit();
			
			transactionIsOpen = false;		//事务结束,可以断开连接
			
			this.con.setAutoCommit(true);
			
		} catch (Exception e) {
			
			e.printStackTrace();

		}finally{
			
			closeConnection();
		
		}
		
	}
	/**
	 * 回滚
	 */
	public void  rollback(){
	
		try {
		
			this.con.rollback();
			
			this.con.setAutoCommit(true);
		
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
	
	public  Table getTableInfo(String table) throws Exception {  
	       
        Connection conn =  getConnection();
        Statement stmt = (Statement) conn.createStatement();  
        
            
        ResultSet rs = stmt.executeQuery("show full columns from " + table);  
        
        List<FieldInfo> ls = new ArrayList<FieldInfo>();
        int cnt = 1;
     

       
        while (rs.next()) { 
        	
        	FieldInfo t = new FieldInfo();
        	
        	t.setIsAuto(rs.getString(7).contains("auto_increment")?"YES":"NO");

        	t.setCommit(rs.getString("Comment"));
        	
        	t.setFieldName(rs.getString("Field"));
        	
        	
        	String typeDate = rs.getString("type");
        	String type = "";
        	String len = "";
        	
        	for(int i=0;i<typeDate.length();i++){
        		
        		if(typeDate.charAt(i)!='('){
        			type += typeDate.charAt(i);
        			
        		}else{
        			break;
        		}
        		
        	}
        	for(int i=0;i<typeDate.length();i++){
        		
        		if(typeDate.charAt(i)>='0'&&typeDate.charAt(i)<='9'){
        			len += typeDate.charAt(i);
        			
        		}
        		
        	}

        	t.setIsKey(rs.getString(5).contains("PRI")?"YES":"NO");
        	
        	
            t.setType(type);
            
            t.setLength(len);
        	
        	t.setDefultValue(rs.getString("default"));
        	
        	t.setCanNull(rs.getString(4));
        	
        	
        	
        	ls.add(t);
        	
        	cnt++;
        }
        
        stmt.close();  
        conn.close();
        rs.close();
        
		return new Table(table, ls);

    }

	protected String tableType2objType(String type) {
		if(type.contains("INT")||type.contains("int")) {
			return "Integer";
		}else if(type.equals("varchar")||type.equals("VARCHAR")) {
			return "String";
		}else if(type.contains("double")||type.contains("DOUBLE")) {
			return "Double";	
		}
		
		return "error";
	}



	
	//get all tables info
	protected List<Table> getTables() {
		List<Table> list = new ArrayList<Table>();
		DatabaseMetaData dbMetaData;
		try {
			dbMetaData = con.getMetaData();
			ResultSet rs = dbMetaData.getTables(null, null, null, new String[] { "TABLE" });
			while (rs.next()) {
				System.out.println();
				String table  =rs.getString("TABLE_NAME");
				list.add(getTableInfo(table));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		return list;
	}
	public static void main(String[] args) throws SQLException {

	}
	public static String namingOfHump(String field, boolean firstUp) {
		String f = "";
		boolean ok = firstUp;
		for (int i = 0; i < field.length(); i++) {
			char c = field.charAt(i);
			if (c == '_')
				ok = true;
			else if (ok) {
				ok = false;
				if (c >= 'a' && c <= 'z')
					f += (char) (c - 'a' + 'A');
				else
					f += c;
			} else {
				f += c;
			}

		}

		return f;

	}
}


