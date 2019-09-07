package com.redcms.servlet.admin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import com.redcms.servlet.core.myAction;
/*
 * 作用
 * 从index界面跳转到admin/welcome.jsp
 * 
 */


@WebServlet("/admin/foward")
public class myForwardServlet extends myAction{
	
	public void index() throws ServletException, IOException
	{
		String page=this.getString("page");
		forword(page);
		
	}

}
