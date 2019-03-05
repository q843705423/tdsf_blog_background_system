package com.tiandisifang.db;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class DataBaseUtil {

    private Connection con ;
    private Statement stmt  ;
    private boolean transactionIsOpen;

    public DataBaseUtil() {
        try {
            transactionIsOpen = false;
            Class.forName("com.mysql.jdbc.Driver");
            con = getConnection();
        } catch (ClassNotFoundException e) {
            showError("please go to download the drive about mysql for java!!!");
            e.printStackTrace();
        }
    }


    /**
     * please return user about your database.
     * @return
     */
    protected abstract String getUser();
    /**
     * your can overwrite this method ,and return your database server IP, default is 127.0.0.1
     * @return
     */
    protected  String getIp() {
        return "127.0.0.1";
    }
    /**
     * your can overwrite this method ,and return your database server port, default is 3306
     * @return
     */
    protected  Integer getPort() {
        return 3306;
    }
    /**
     * your must overwrite it,this is your connected database name
     * @return
     */
    protected abstract String getDatabaseName() ;
    /**
     * this is your database password,please overwrite it
     * @return
     */
    protected abstract String getPassword();

    /**
     *
     * @param ObjectProperty
     * @return
     */

    protected String objectProperty2tableFieldMap(String ObjectProperty) {

        return hump2Underscore(ObjectProperty);
    }

    protected String tableField2ObjectPropertyMap(String tableField) {

        return underscore2hump(tableField);
    }


    private String getUrl() {
        return "jdbc:mysql://${ip}:${port}/${databaseName}?useSSL=true&&characterEncoding=utf8"
                .replace("${ip}", getIp())
                .replace("${port}", getPort().toString())
                .replace("${databaseName}", getDatabaseName());
    }


    private void showError(String message) {
        System.err.println(message);
        System.exit(0);
    }
    private Connection getConnection() {

        if(con==null) {
            try {
                con =  DriverManager.getConnection(getUrl(), getUser(), getPassword());
            } catch (SQLException e) {
                showError("database config error,please check it!!!");
                e.printStackTrace();
            }
        }
        return con;
    }

    private Statement getStatement() {
        if(stmt==null) {
            try {
                stmt = con.createStatement();
            } catch (SQLException e) {
                showError("sql is error");
                e.printStackTrace();
            }
        }

        return stmt;
    }

    public void insert(Object obj,String tableName) {


        ArrayList<String>  tableFileds =getTableFieldsBySql("select * from "+tableName);
        Map<String,Object> map = object2Map(obj);
        HashMap<String,Object>insertMap = new HashMap<>();
        for(String tableField:tableFileds) {
            if(map.keySet().contains(tableField2ObjectPropertyMap(tableField))) {
                insertMap.put(tableField, map.get(tableField2ObjectPropertyMap(tableField)));
            }
        }
        insert(insertMap,tableName);

    }
    public String object2String(Object object) {
        if(object instanceof Integer||object instanceof Long) {
            return object.toString();
        }else if(object instanceof String) {
            return "'${data}'".replace("${data}", object.toString());
        }
        return object.toString();
    }

    public void insert(HashMap<String,Object>map,String tableName) {
        Set<String> keySet = map.keySet();
        String fields = "";
        String values = "";
        boolean first = true;
        for(String s:keySet) {
            if(first) {
                first=  false;
                fields += s;
                values +=object2String(map.get(s));
            }else {
                fields += ","+s;
                values += ","+object2String(map.get(s));
            }

        }
        String sql = "insert into ${tableName}(${fields}) values(${values})"
                .replace("${tableName}", tableName)
                .replace("${fields}", fields)
                .replace("${values}", values);
        try {
            con = getConnection();
            stmt=stmt==null||stmt.isClosed()?(Statement) con.createStatement():stmt;
           System.out.println(sql);
            // stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(sql);
            //showError("your sql is error,please to check!!!");
            e.printStackTrace();
        }
    }

    private  Map<String, Object> object2Map(Object obj){
        Map<String,Object>map = new HashMap<>();
        if(obj == null) {
            return map;
        }
        Class<?>clazz = obj.getClass();
        Field []objFields = clazz.getDeclaredFields();
        String getMethodName = null;
        try {
            for(Field f:objFields) {
                getMethodName = getGetMethodName(f.getName());
                Method getMethod = clazz.getDeclaredMethod(getMethodName);
                Object o = getMethod.invoke(obj);
                if(o!=null) {
                    map.put( f.getName() , o);
                }

            }
        }catch (NoSuchMethodException e) {
            showError("your don't have a method named :"+getMethodName);
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            showError("please check your method about "+getMethodName+".");
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return map;
    }
    private String getGetMethodName(String objectProperty) {
        String getMethodName = "get" + objectProperty.substring(0, 1).toUpperCase()+objectProperty.substring(1);
        return getMethodName;

    }
    private String getSetMethodName(String objectProperty) {
        return objectProperty;
    }
    public List<Map<String,String>>select(String sql){
        List<String>tableFields = getTableFieldsBySql(sql);
        List<Map<String,String>> list = new ArrayList<Map<String,String>>();
        con = getConnection();
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()) {
                Map<String,String>map = new HashMap<>();
                for(String f:tableFields) {
                    map.put(f, rs.getString(f));
                }
                list.add(map);

            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return list;

    }

    /*public static String tableField2ObjectProperty(String tableField) {
        return "";
    }
    public static String objProperty2tableField(String objectProperty) {
        return objectProperty;
    }*/
    public static String hump2Underscore(String humpString){
        String underscoreString = "";
        for(int i=0;i<humpString.length();i++) {
            char c = humpString.charAt(i);
            underscoreString += c>='A'&&c<='Z'?"_"+(char)(c-'A'+'a'):c;
        }
        return underscoreString;
    }
    public static String underscore2hump(String underscoreString) {
        String humpString = "";
        boolean now2Up = false;
        for(int i=0;i<underscoreString.length();i++) {
            char c = underscoreString.charAt(i);
            if(now2Up) {
                humpString += c>='a'&&c<='z'?(char)(c-'a'+'A'):c;
                now2Up = false;
            }else {
                if(c=='_') {
                    now2Up = true;
                }else {
                    humpString+=c;
                }
            }

        }
        return humpString;
    }


    private   ArrayList<String> getTableFieldsBySql(String sql){
        ArrayList<String> ls = new ArrayList<String>();
        Connection ct = null;
        try {
            ct = getConnection();
            stmt=stmt==null||stmt.isClosed()?(Statement) ct.createStatement():stmt;
            ResultSet rs = stmt.executeQuery(sql);
            ResultSetMetaData data = (ResultSetMetaData) rs.getMetaData();
            int columnCount = data.getColumnCount();
            for(int i=1;i<=columnCount;i++){
                String label = data.getColumnLabel(i);
                ls.add(label);
            }
        } catch (SQLException e) {
            return ls;
        }finally{

            closeConnection();

        }

        return ls;

    }

    private void closeConnection(){
        if(transactionIsOpen)return;
        try{

            if (!(this.con==null))this.con.close();

        }catch(SQLException e){

            e.printStackTrace();

        }

        this.con = null;
    }



}
class DB extends DataBaseUtil{

    @Override
    protected String getUser() {
        return "man";
    }

    @Override
    protected String getDatabaseName() {
        return "man";
    }

    @Override
    protected String getPassword() {
        return "man";
    }

    @Override
    protected String getIp() {
        return "www.skythinking.cn";
    }
}