package com.redcms.servlet.admin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.log4j.Logger;

import com.redcms.beans.myAdmin;
import com.redcms.db.Db;
import com.redcms.servlet.core.myAction;
import com.redcms.util.Md5Encrypt;


@WebServlet("/admin/admin")
public class myAdminManagerServlet extends myAction{
	
	private static Logger log = Logger.getLogger(myAdminManagerServlet.class);

	@Override
	public void index() throws ServletException, IOException {
		
		// ������еĹ���Ա��
		List<myAdmin> list = null;
		
		
		try {
			list = Db.query("select * from admin", new BeanListHandler<myAdmin>(myAdmin.class));
			setAttr("list",list);
		} catch (SQLException e) {
			setAttr("err","��ѯ����Աʧ��");
			
		}
		this.forword("admin/admin_manager.jsp");
	}
	
	public void saveAdd() throws ServletException, IOException {
		
		myAdmin admin = new myAdmin();
		admin.setActive((byte)1);
		this.getBean(admin);
		admin.setUpwd(Md5Encrypt.md5(admin.getUpwd()));
		
		try {
			Db.update("insert into admin(uname,upwd,upur,active) values(?,?,?,?)", admin.getUname(),admin.getUpwd(),admin.getUpur(),admin.getActive());
			this.setAttr("msg", "���ӹ���Ա�ɹ�");
		} catch (SQLException e) {
			setAttr("err","���ӹ���Աʧ��");
			log.error("ERROR_ADMIN_001_���ӹ���Աʧ��!"+e.toString());
			e.printStackTrace();
		}
		index();
		
	}
	
	public void update() throws ServletException, IOException
	{
		myAdmin admin = new myAdmin();
		admin.setActive((byte)1);
		this.getBean(admin);
		String newpwd = this.getString("newpwd");
		if(!"".equals(newpwd))
		{
			admin.setUpwd(Md5Encrypt.md5(admin.getUpwd()));
		}
		 try {
			Db.update("update admin set uname=?,upwd=?,upur=? where id=?", admin.getUname(),admin.getUpwd(),admin.getUpur(),admin.getId());
			setAttr("msg","�޸ĳɹ�");
		 } catch (SQLException e) {
			 setAttr("err","�޸�ʧ��");
			 log.error("ERROR_ADMIN_002_�޸Ĺ���Աʧ��!"+e.toString());
			 e.printStackTrace();
		}
		
		 index();
	}
	
	public void del() throws ServletException, IOException
	{
		int id = this.getInt("id");
		if(id>0)
		{
			try {
				Db.update("delete from admin where id=?", id);
				 setAttr("msg", "ɾ������Ա�ɹ�!");
			} catch (SQLException e) {
				 setAttr("err","ɾ������Աʧ��");
				 log.error("ERROR_ADMIN_003_ɾ������Աʧ��!"+e.toString());
				e.printStackTrace();
			}
		}
		index();
		
	}
	

}
