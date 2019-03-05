package com.tiandisifang.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseComparor {

	public static void main(String[] args) throws Exception {
		DBUtil d = new DBUtil("com.mysql.jdbc.Driver", "root", "root", "jdbc:mysql://localhost:3306/mchp?useSSL=false");
		List<FieldInfo> list = d.getTableInfo("yh").getFieldInfos();
		for(FieldInfo f:list) {
			System.out.println(f);
		}
	}
	
	
	public static void compareTwoTable(Table before,Table after) {
	
		if(before.getTableName().equals(after.getTableName())) {
			boolean same = true;
			List<FieldInfo>beforeField = before.getFieldInfos();
			List<FieldInfo>afterField = after.getFieldInfos();
			List<FieldInfo>delteField = new ArrayList<FieldInfo>();//字段删除清单
			List<FieldInfo>addField = new ArrayList<FieldInfo>();//字段增加清单
			List<FieldInfo>updateField = new ArrayList<FieldInfo>();//字段修改清单
			
			HashMap<String,FieldInfo>hashMap  = new HashMap<String,FieldInfo>();
			
			for(FieldInfo be:beforeField) {
				boolean has = false;
				for(FieldInfo af:afterField) {
					if(be.getFieldName().equals(af.getFieldName())) {
						has = true;
					}
				}
				if(has==false) {
					delteField.add(be);
				}
			}
			
			for(FieldInfo af:afterField) {
				boolean has = false;
				for(FieldInfo be:beforeField) {
					if(be.getFieldName().equals(af.getFieldName())) {
						has = true;
					}
				}
				if(has==false) {
					addField.add(af);
				}
			}
			
			
			
			
			
			
			
		}else {
			
			System.out.println("两张表名字不同，无法比较");
		}
		
		
	}
	
}
