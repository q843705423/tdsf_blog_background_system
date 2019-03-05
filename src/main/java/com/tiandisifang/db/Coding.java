package com.tiandisifang.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//利用数据库语句，生成所有操作
public class Coding {
	
	static String projectPath = "D:\\code\\java\\idea_workspace\\cartoon_javaEE\\";
	//指定Controller，Service,mapper层的位置
	static String controllerDir = projectPath+"src\\main\\java\\cn\\edu\\hziee\\controller\\";
	static String serviceDir = projectPath+"src\\main\\java\\cn\\edu\\hziee\\service\\";
	static String mapperDir = projectPath+"src\\main\\java\\cn\\edu\\hziee\\mapper\\";
	static String htmlDir = "C:\\Users\\lenovo\\Documents\\HBuilderProjects\\manhua\\index.html";
	
	static String method  = "get_all_";//方法名
	
	public static void main(String[]args) throws Exception {
		
		
		
	
		//总文件路径
		String filePath = "D:\\data\\cartoon_javaee\\my.txt";
		Scanner sc = new Scanner(System.in);
		System.out.println("请输入你的方法名");
		
		
		method = sc.next();
		System.out.println("你要注入的方法要哪一层为止");
		System.out.println("1.Mapper 2.Service 3.Controller 4.Html");
		int op = sc.nextInt();
		
		
		System.out.println("方法名输入成功");
		if(op>=3)System.out.println("你的Controller层路径为"+controllerDir);
		if(op>=2)System.out.println("你的Service层路径为"+serviceDir);
		if(op>=2)System.out.println("你的Mapper层路径为"+mapperDir);
		if(op>=1)System.out.println("你的前端路径为"+htmlDir);
		System.out.println("你的sql文件路径为"+filePath);
		System.out.println("你确定了吗?yes/no");
		String s = sc.next();
		if(!s.equals("yes")) {
			System.out.println("你没有输入yes，程序结束，欢迎您的使用");
			sc.close();
			return;
		}
		
		
	
		
		
		
		
		File file = new File(filePath);
		BufferedReader br = new BufferedReader(new FileReader(file));
		String sql = "";
		String str = br.readLine();
		while(true) {
			if(str==null)break;
			sql += str+" ";
			str = br.readLine();
			
		}
		br.close();
		init(sql);
		tableName =getTableName(sql);
		boolean ok = getParamAndType(sql);
		changeSql(sql);
		
		if(ok==true) {
			
			
			if(op>=1)dealMapper(sql);
			if(op>=2)dealSerivce();
			if(op>=3)dealController();
			if(op>=4)dealHtml();
			System.out.println("方法注入成功");
		}else {
			System.out.println("出现了异常，你需要保证sql语句中动态变化的量，用 #{paramName,paramType}代替");
			
		}
		sc.close();
		
		
		
		
		
			
	}


	
	static	String tableName = "dictionary_data";		
	static	String taBleName = dealMethodName(tableName);
	static	String className = taBleName.substring(0,1).toUpperCase()+taBleName.substring(1);	
	static	String controller = className +"Controller";
	static	String service =  className+"Service";
	static	String mapper = className+"Mapper";
	static	String controllerName = controller+".java";
	static	String serviceName = service+".java";
	static	String mapperName = mapper+".java";
	static	String controllerUrl = controllerDir+controllerName;
	static	String serviceUrl = serviceDir+serviceName;
	static	String mapperUrl = mapperDir+mapperName;
	private static String changeSql(String sql) {
		String newSql = sql;
		List<String>list = matcher(sql,"\\{[^}]*?\\}" , false);
		for(int i=0;i<list.size();i++) {
		
			newSql = newSql.replace(list.get(i), "{"+param.get(i)+"}");
			
		}

		
		return newSql;
		
		
	}
	
	
	private static void init(String sql) {
		
		tableName = getTableName(sql);
		 taBleName = dealMethodName(tableName);
		 className = taBleName.substring(0,1).toUpperCase()+taBleName.substring(1);	
		
		 controller = className +"Controller";
		 service =  className+"Service";
		 mapper = className+"Mapper";
		 controllerName = controller+".java";
		 serviceName = service+".java";
		 mapperName = mapper+".java";
		 controllerUrl = controllerDir+controllerName;
		 serviceUrl = serviceDir+serviceName;
		 mapperUrl = mapperDir+mapperName;
		controller = className +"Controller";
		service =  className+"Service";
		mapper = className+"Mapper";
		controllerName = controller+".java";
		serviceName = service+".java";
		mapperName = mapper+".java";
		controllerUrl = controllerDir+controllerName;
		serviceUrl = serviceDir+serviceName;
		mapperUrl = mapperDir+mapperName;
		
		/*taBleName = dealMethodName(tableName);
		className = taBleName.substring(0,1).toUpperCase()+taBleName.substring(1);*/
		
	}
	static List<String> param = new ArrayList<String>();
	static List<String> paramType= new ArrayList<String>();
	
	
	private static boolean getParamAndType(String sql) {
		
		List<String>list = matcher(sql,"\\{[^}]*?\\}",false);
		for(String s:list) {
			boolean ok = put(s);
			if(ok==false)return false;
		}
	
		return true;
	}
	private static boolean put(String info) {
		String name = "";
		String type = "";
		boolean left = true;
		for(int i=0;i<info.length();i++) {
			char c = info.charAt(i);
			if(c==',') {
				left = false;
			}else if(c=='{'||c=='}'||c==' ') {
				continue;
				
			}else {
				if(left) {
					name +=c;
				}else {
					type+=c;
				}
				
			}
			
			
		}
		param.add(name);
		paramType.add(type);
		
		String can[] = new String[] {
				"Integer","int","String"
		};
		for(int i=0;i<can.length;i++) {
			if(can[i].equals(type))return true;
			
		}
		
		
		return false;
		
	}
	
	private static String getTableName(String sql) {
		int cnt = sql.indexOf("FROM");
		if(cnt==-1) {
			cnt = sql.indexOf("from");
		}
		
		sql = sql.substring(cnt+4);
		
		String tableName = "";
		boolean begin = false;
		
		for(int i=0;i<sql.length();i++) {
			char s = sql.charAt(i);
			if(!begin) {
				if(s!=' ') {
					begin = true;
				}
			}
			if(begin) {
				if(s==' ') {
					break;
				}else {
					tableName += s;
				}
			}
			
		}

		return tableName;
	}
	public static void dealMapper(String sql) throws Exception {
		
		
		//mapper层
		String	content =readFile(mapperUrl);
		content  = content.substring(0,content.lastIndexOf("}")-1);
		content +="\t@Select(\"";
		content += changeSql(sql);
		
		content +="\")\r\n" ;
		
		content += "\tList<HashMap<String,String>> "+dealMethodName(method)+"(";
		for(int i=0;i<param.size();i++) {
			if(i!=0)content += ",";
			content += "@Param(\""+param.get(i)+"\")"+paramType.get(i)+" "+param.get(i);
			
		}
		
		content +=");\r\n\r\n";
		content+="}";
		writeFile(mapperUrl, content);
		
	}
	public static void dealController() throws Exception {
		//Controller
		
		String content =readFile(controllerUrl);
		content  = content.substring(0,content.lastIndexOf("}")-1);
		content += "\t@RequestMapping(\""+method+"\")\r\n\r\n";
		content += "\tpublic String "+dealMethodName(method)+"(HttpServletRequest request,HttpServletResponse response){\r\n\r\n";
		
		content += forInControllerRequestion();
		
		content += "\t\tList<?>list = "+service+"."+dealMethodName(method)+"(";
		content += forInControllerFunctionParam();
		content +=");\r\n\r\n";
		content += "\t\treturn ajaxReturn(response,1,\"success\",list);\r\n";
		content += "\t}\r\n\r\n";
		content += "}\r\n";
		writeFile(controllerUrl, content);
	
		
	}
	private static String forInControllerFunctionParam() {
		String content = "";
		for(int i=0;i<param.size();i++) {
			if(i!=0)content+=",";
			if(param.get(i).equals("userId")) {
				content += "u";
				
			}else {
				content+= param.get(i);
			}
			
			
		}
		
		return content;
		
	}
	public static void dealSerivce() throws Exception {
	
		
		//Service		
		String content =readFile(serviceUrl);
		content  = content.substring(0,content.lastIndexOf("}")-1);
		content +="\tpublic List<?> "+dealMethodName(method)+"(";
		
		content += forInServiceChangeFunctionName();//参数列表
		
		
		content +=") {\r\n" ;
		
		content +=	"\t\treturn mapper."+dealMethodName(method)+"(";
		
		content += forInServiceChangeFunctionParam();//方法
		
		content +=");\r\n" ;
		content +=	"\t}\r\n\r\n";
		content +=  "}";
		writeFile(serviceUrl, content);
		
	}
	
	private static String forInControllerRequestion() {
		String content = "";
		for(int i=0;i<param.size();i++) {
			if(param.get(i).equals("userId")) {
				content += "\t\tUserInfo u = userInfoService.getUserInfo(request.getParameter(\"token\"));\r\n";
			}else if(paramType.get(i).equals("Integer")) {
				content += "\t\tint "+param.get(i)+" =Integer.valueOf( request.getParameter(\""+param.get(i)+"\"));\r\n";
			}else if(paramType.get(i).equals("String")){
				content += "\t\tString "+param.get(i)+" = request.getParameter(\""+param.get(i)+"\");\r\n";
			} 
			
			
		}
		
		return content ;
	}
	
	private static String forInServiceChangeFunctionName() {
		String content = "";
		for(int i=0;i<param.size();i++) {
			if(i!=0)content +=",";
			
			
			if(param.get(i).equals("userId")) {
				content += "UserInfo u";
				
			}else {
				content += paramType.get(i)+" "+param.get(i);
			}
			
			
			
			
		}
		
		return content ;
	}
	
	private static String forInServiceChangeFunctionParam() {
		String content = "";
		for(int i=0;i<param.size();i++) {
			if(i!=0)content +=",";
			if(param.get(i).equals("first")) {//first-> (first-1)*each
				
				content += " (first-1)*each";
				
			}else if(param.get(i).equals("userId")) {//userId -> u.getUserId()
				content += "u.getUserId()";
				
			}else {
				content += param.get(i);
				
			}
			
			
			
			
		}
		
		
		return content;
		
	}
	
	
	private static String  dealHtml() throws Exception {
		String content = readFile(htmlDir);
		
		content = content.substring(0,content.lastIndexOf("</script>"));
		
		content += "\tfunction "+method+"(){\r\n" + 
				"\t\t$.ajax({\r\n" + 
				"\t\t\ttype:\"get\",\r\n" + 
				"\t\t\turl:controllerUrl+\""+tableName+"/"+method+"\",\r\n" + 
				"\t\t\tasync:true,\r\n" + 
				"\t\t\tdataType:\"json\",\r\n" + 
				"\t\t\tdata:{\r\n" ;
		for(int i=0;i<param.size();i++) {
			content += "\t\t\t\t"+param.get(i)+":"+param.get(i)+",\r\n";
			
		}
		
		
		content+=		"				\r\n" + 
				"\t\t\t},success:function(data){\r\n" + 
				"\t\t\t\tconsole.log(data);\r\n" + 
				"\t\t\t},error:function(data){\r\n" + 
				"				\r\n" + 
				"\t\t\t}\r\n" + 
				"\t\t});		\r\n" + 
				"\t}\r\n\r\n";
		content += "</script>";

		writeFile(htmlDir, content);
		return null;
	}
	
	private static String dealMethodName(String method) {
		String ans = "";
		int flag = 0;
		for(int i=0;i<method.length();i++) {
			char c = method.charAt(i);
			if(c=='_') {
				flag = 1;
			}else {
				ans += (char)(flag==1?c-'a'+'A':c);
				flag = 0;
			}
			
			
			
		}
		return ans;
		
	}
	private static String readFile(String fileName) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
		String str = br.readLine();
		String content = "";
		while(true) {
			
			if(str==null) {
				
				break;
			}
			content += str+"\r\n";
			str = br.readLine();
			
			
		}
		br.close();
		return content;
	}
	private static void   writeFile(String fileName,String content)throws Exception{
		
		File f = new File(fileName);
		FileWriter fw = new FileWriter(f);
		fw.append(content);
		fw.close();
	}
	private static List<String>matcher(String source,String regex ,boolean show){
		 
		Pattern pattern = Pattern.compile(regex);  
	    
		Matcher matcher = pattern.matcher(source);  
	    
		List<String> list = new ArrayList<String>();  
	  
		while (matcher.find()) {  
	    	
	        list.add(matcher.group());  
	    }  
	  
		if(show) {
			
			for(String s:list) {
				System.out.println(s);
				
			}
			System.out.println(list.size());
		}
		
		return list;  
		
		
	}
	
	
	
	
}
