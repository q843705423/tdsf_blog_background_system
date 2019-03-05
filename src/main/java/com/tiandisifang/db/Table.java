package com.tiandisifang.db;

import java.util.List;

public class Table {

	
	
	private String tableName;
	
	private List<FieldInfo> field;
	
	
	public Table(String name,List<FieldInfo>field) {
		this.tableName = name;
		this.field = field;
	}
	
	public String getTableName() {
		return tableName;
	}
	
	public List<FieldInfo> getFieldInfos(){
		
		
		return field;
		
	}
	
}
