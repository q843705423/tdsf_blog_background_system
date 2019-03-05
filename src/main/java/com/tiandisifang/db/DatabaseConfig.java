package com.tiandisifang.db;

import java.io.File;
import java.util.ResourceBundle;

public class DatabaseConfig {

	protected static DatabaseConfig config;
	protected String username;
	protected String password;
	protected String driver;
	protected String projectPath;
	protected String model;
	protected String mapper4j;
	protected String mapper4x;
	protected String service;
	protected String controller;
	protected String basePackage;
	protected String prefix;
	protected String javaPath;
	protected static ResourceBundle rb = null;
	protected DatabaseConfig() {

	}
	public static DatabaseConfig getConfig(String propertePath) {
		if (config == null) {
			config = new DatabaseConfig();
			config.username = "root";
			config.password = "root";
			config.driver = "com.mysql.jdbc.Driver";
			config.model = "model";
			config.service = "service";
			config.controller = "controller";
			config.mapper4x = "mapper";
			config.mapper4j = "mapper";
			config.javaPath = "src/main/java";
			config.basePackage = "cn.edu.hziee";
			config.projectPath = new File("").getAbsolutePath() + "\\\\";
			config.projectPath = "D:\\code\\java\\idea_workspace\\tdsf_blog_background_system";
			
			rb = ResourceBundle.getBundle(propertePath);
			config.projectPath = getParam("projectPath")==null?config.projectPath:getParam("projectPath");
			config.username =  getParam("username")==null?config.username:getParam("username");
			config.password =  getParam("password")==null?config.password:getParam("password");
			config.driver =	   getParam("driver")==null?config.driver:getParam("driver");
			config.controller= getParam("controller")==null?config.controller:getParam("controller");
			config.service	 = getParam("service")==null?config.service:getParam("service");
			config.mapper4x =  getParam("mapper4x")==null?config.mapper4x:getParam("mapper4x");
			config.mapper4j =  getParam("mapper4j")==null?config.mapper4j:getParam("mapper4j");
			config.model	 = getParam("model")==null?config.model:getParam("model");
			config.javaPath =  getParam("javaPath")==null?config.javaPath:getParam("javaPath");
			config.prefix = getParam("prefix")==null?"": getParam("prefix");
			config.basePackage = getParam("basePackage")==null?config.basePackage:getParam("basePackage");
		}
		return config;
	}
	public static DatabaseConfig getConfig() {
		return getConfig("db");

	}
	private static String getParam(String key) {
		try {
			return rb.getString(key);
		}catch (Exception e) {
			
			return null;
		}

	}

	public static void main(String[] args) {
		DatabaseConfig config = DatabaseConfig.getConfig("dbs");
		System.out.println(config);
	}

	@Override
	public String toString() {
		return " username=" + username + "\r\n password=" + password + "\r\n driver=" + driver
				+ "\r\n projectPath=" + projectPath + "\r\n model=" + model + "\r\n mapper4j=" + mapper4j + "\r\n mapper4x="
				+ mapper4x + "\r\n service=" + service + "\r\n controller=" + controller + "\r\n basePackage=" + basePackage
				+ "\r\n prefix=" + prefix + "\r\n javaPath=" + javaPath + "";
	}
	public void testProperties(String properties) {
		try {
			ResourceBundle rb =  ResourceBundle.getBundle(properties);
		}catch (Exception e) {
			System.err.println("hello! your are wrong on a properties that DatabaseConfig can't find "+properties+".properties");
			System.exit(0);
		}
		
		
		
		
	}
	public static final int MODEL = 1;
	public static final int MAP4J = 2;
	public static final int MAP4X = 3;
	public static final int SERVICE = 4;
	public static final int CONTROLLER = 5;
	
	public  String getAbsoulePosition(int type) {
		String typeReal=null;
		if(type==MODEL) {
			typeReal = config.model;
		}else if(type==MAP4J) {
			typeReal =  config.mapper4j;
		}else if(type==MAP4X) {
			typeReal =  config.mapper4x;
		}else if(type==SERVICE) {
			typeReal =  config.service;
		}else if(type==CONTROLLER) {
			typeReal = config.controller;
		}
		String absoluteModelUrl = config.projectPath+File.separator+config.javaPath+"."+config.basePackage+"."+typeReal+File.separator;
		absoluteModelUrl = absoluteModelUrl.replace(".", File.separator);
		absoluteModelUrl = absoluteModelUrl.replace("/", File.separator);
		absoluteModelUrl = absoluteModelUrl.replace("//", File.separator);
		absoluteModelUrl = absoluteModelUrl.replace("///", File.separator);
		absoluteModelUrl = absoluteModelUrl.replace("\\\\\\", File.separator);
		absoluteModelUrl = absoluteModelUrl.replace("\\\\", File.separator);
		absoluteModelUrl = absoluteModelUrl.replace("\\", File.separator);
		return absoluteModelUrl;
	}
	

}
