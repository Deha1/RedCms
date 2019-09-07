package com.redcms.db;

import java.sql.SQLException;


/*
 * 对事务的支持
 * 
 */
public class Test {
	public static void main(String[] args) throws SQLException
	{
		try {
			Db.beginTransaction();
			Db.update("insert into admin(uname,upwd) values(?,?)", "asds@qq.com",10);
			//Db.update("insert into t2(f1,f2) values(?,?)", "ddd81",20);
			Db.commitTransaction();
		} catch (Exception e) {
		    Db.rollbackTransaction();
			e.printStackTrace();
		}
	}

}
