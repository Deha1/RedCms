package com.redcms.servlet.admin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import com.redcms.servlet.core.myAction;
/*
 * ר����ת��indexҳ���
 */


@WebServlet("/admin/index")
public class myAdminMainServlet extends myAction{
	
	public void index()throws ServletException, IOException {
		// TODO Auto-generated method stub
       forword("admin/index.jsp");
	}


}
