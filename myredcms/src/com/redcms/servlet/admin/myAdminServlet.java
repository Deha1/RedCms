package com.redcms.servlet.admin;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import org.apache.commons.dbutils.handlers.BeanHandler;


import com.redcms.beans.myAdmin;
import com.redcms.beans.myStudent;
import com.redcms.db.Db;
import com.redcms.servlet.core.myAction;
import com.redcms.util.Md5Encrypt;

@WebServlet("/admin/login")
//思考:request对象的生命周期，两个Servlet操作不可以共享同一个request对象
//但是30分钟内，request对象会保留第一次发起请求时内存所分配的地址，所以可能一样
public class myAdminServlet extends myAction{

	@Override
	public void index() throws ServletException, IOException {
		//跳转到登陆界面
		this.forword("admin/login.jsp");
		
	}
	
	public void logout() throws ServletException, IOException
	{
		req.getSession().removeAttribute("loged");
		req.getSession().invalidate();
		redirect("login");
		
	}
	
	
	
	
	public void checkLogin() throws  ServletException, IOException 
	{
		//生成Bean对象
		myAdmin admin =new myAdmin();
		this.getBean(admin);
		System.out.println((String)req.getSession().getAttribute("randomCode"));
		System.out.println(this.getString("rand"));
		 String serverrand=(String)req.getSession().getAttribute("randomCode");
		 String rand=this.getString("rand");
		 if(rand.equals(serverrand))
		 {
			
		 String sql="select * from admin where uname=? and upwd=? limit 1";
		 
		 try {
			
			myAdmin loged=Db.query(sql, new BeanHandler<myAdmin>(myAdmin.class),admin.getUname(),Md5Encrypt.md5(admin.getUpwd()));
			System.out.println(loged.getUname()+"----------------------->");
			if(null!=loged&&loged.getUname().equals(admin.getUname()))
			{
				req.getSession().setAttribute("loged", loged);
				//成功
				System.out.println("11111111111");
				redirect("index");
			   
			}else
			{
				System.out.println("---3-------------------->");
				setAttr("err", "用户名和密码不正确！");
				index();
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 }else
		 {
			 setAttr("err", "用户名和密码不正确！");
			 index();
		 }

		
	}
	

}
