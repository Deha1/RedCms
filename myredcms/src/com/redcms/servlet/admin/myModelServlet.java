package com.redcms.servlet.admin;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.redcms.beans.ChannelField;
import com.redcms.beans.DataField;
import com.redcms.beans.Model;
import com.redcms.beans.ModelItem;
import com.redcms.db.Db;
import com.redcms.servlet.core.myAction;
@WebServlet("/admin/model")
/*
 * 
 * ģ�͹���
 * 
 */
public class myModelServlet extends myAction{

	@Override
	public void index() throws ServletException, IOException 
	{

		try {
			List<Model> list=Db.query("select * from model order by priority", new BeanListHandler<Model>(Model.class));
		    setAttr("list", list);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		this.forword("admin/model_list.jsp");
		
	}
	
	public void add() throws ServletException, IOException
	{
		try {
			List<Model> list = Db.query("select * from model order by priority", new BeanListHandler<Model>(Model.class));
			setAttr("list",list);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		this.forword("admin/model_add.jsp");
	}
	
	public void addSave() throws ServletException, IOException
	{
		try {
			Db.beginTransaction();
			
			Model mod = new Model();
			this.getBean(mod);
			/*
			 * �޸ĺ�����
			 * �ڱ�model�в���һ���µ�����
			 */
			String addmod = "insert into model(name,path,title_width,title_height,content_width,content_height,priority,has_content,is_def,template) values(?,?,?,?,?,?,?,?,?,?)";
			Db.update(addmod,mod.getName(),mod.getPath(),mod.getTitle_width(),mod.getTitle_height(),mod.getContent_width(),mod.getContent_height(),mod.getPriority(),mod.getHas_content(),mod.getIs_def(),mod.getTemplate());
			
			
			//�õ�model��������������ݵĵ�idֵ
			Object obj = Db.query("select LAST_INSERT_ID() from dual", new ArrayHandler())[0];
			long insertedId = 0;
			//���obj �� Long ��һ��ʵ��������true
			if(obj instanceof Long)
			{
				insertedId=(Long)obj;
			}else if(obj instanceof BigInteger)
			{
				insertedId=((BigInteger)obj).longValue();	
			}
			
			/*
			 *���ո����ӵ����ݵ�channel_field����Ŀ�ֶε��ֶΣ� �����ݲ���modelItem��
			*modelItem����channel_field����Ŀ�ֶε��ֶΣ���data_field�������ֶε��ֶΣ��ֶε�ֵ
			 */
			//�ó�ģ������Ŀ������ֶε���������
			List<ChannelField> cfields = Db.query("select * from channel_field order by id", new BeanListHandler<ChannelField>(ChannelField.class));
			//�ó�ģ�͹���(��Ŀ�ֶκ������ֶ�)���ֶ�
			
			List<ModelItem> milist = new ArrayList<ModelItem>();
			
			for(ChannelField cf:cfields)
			{
				ModelItem mi = new ModelItem();
				mi.setModel_id(insertedId);
				mi.setData_type(cf.getData_type());
	    		mi.setDef_value(cf.getDef_value());
	    		mi.setField(cf.getField());
	    		mi.setField_dis(cf.getField_dis());
	    		mi.setHelp_info(cf.getHelp_info());
	    		mi.setIs_channel(cf.getIs_channel());
	    		mi.setIs_custom(cf.getIs_custom());
	    		mi.setIs_display(cf.getIs_display());
	    		mi.setIs_required(cf.getIs_required());
	    		mi.setIs_single(cf.getIs_single());
	    		mi.setOpt_value(cf.getOpt_value());
	    		mi.setPriority(cf.getPriority());
	    		mi.setTxt_size(cf.getTxt_size());
	    		
	    		milist.add(mi);
			}
			
			
			/*
			 *���ո����ӵ����ݵ�data_field�������ֶε��ֶΣ� �����ݲ���model_Item��
			 *
			 */
			List<DataField> dfields=Db.query("select * from data_field order by id", new BeanListHandler<DataField>(DataField.class));
			
			for(DataField df:dfields)
			{
				ModelItem mi=new ModelItem();
	    		mi.setModel_id(insertedId);
	    		mi.setData_type(df.getData_type());
	    		mi.setDef_value(df.getDef_value());
	    		mi.setField(df.getField());
	    		mi.setField_dis(df.getField_dis());
	    		mi.setHelp_info(df.getHelp_info());
	    		mi.setIs_channel(df.getIs_channel());
	    		mi.setIs_custom(df.getIs_custom());
	    		mi.setIs_display(df.getIs_display());
	    		mi.setIs_required(df.getIs_required());
	    		mi.setIs_single(df.getIs_single());
	    		mi.setOpt_value(df.getOpt_value());
	    		mi.setPriority(df.getPriority());
	    		mi.setTxt_size(df.getTxt_size());
	    		milist.add(mi);
			}
			
	    	String misql="insert into model_item(model_id,field,field_dis,priority,def_value,opt_value,txt_size,help_info,data_type,is_single,is_channel,is_custom,is_display,is_required) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	    	//ת��ά����,Ϊ��ƥ��batch�����Ĳ�����ת��
	    	Object[][] params= new Object[milist.size()][];
			
			for(int i=0;i<milist.size();i++)
			{
				ModelItem mi=milist.get(i);
				Object[]row = new Object[14];
				 row[0]=mi.getModel_id();
            	 row[1]=mi.getField();
            	 row[2]=mi.getField_dis();
            	 row[3]=mi.getPriority();
            	 row[4]=mi.getDef_value();
            	 row[5]=mi.getOpt_value();
            	 row[6]=mi.getTxt_size();
            	 row[7]=mi.getHelp_info();
            	 row[8]=mi.getData_type();
            	 row[9]=mi.getIs_single();
            	 row[10]=mi.getIs_channel();
            	 row[11]=mi.getIs_custom();
            	 row[12]=mi.getIs_display();
            	 row[13]=mi.getIs_required();
            	 params[i]=row;
			}
			
			Db.batch(misql, params);
			Db.commitTransaction();
			setAttr("msg","����ģ�ͳɹ�");
			
		} catch (Exception e) {
			Db.rollbackTransaction();
			 setAttr("err", "����ģ��ʧ��!");
			e.printStackTrace();

		}
		
		index();
		}
	
	
	
	public void  delModel() throws ServletException, IOException
	{
		int id = this.getInt("id");
		if(id>0)
		{
			try {
				Db.update("delete from model_item where model_id=?",id);
				Db.update("delete from model where id=?",id);
				setAttr("msg", "ɾ��ģ�ͳɹ�!");
			} catch (Exception e) {
				setAttr("err", "ɾ��ģ��ʧ��!");
				e.printStackTrace();
			}
		}
		index();
	}
	
	
	public void edit() throws ServletException, IOException 
	{
       int id=this.getInt("id");
       if(id>0)
       {
		try {
			Model model=Db.query("select * from model where id=?", new BeanHandler<Model>(Model.class),id);
		    setAttr("model", model);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.forword("admin/model_edit.jsp");
       }else
       {
    	   index();
       }
		
		
	}
	
	
	public void editSave() throws ServletException, IOException 
	{
       int id=this.getInt("id");
       if(id>0)
       {
    	
		try {
			String sql="update model set name=?,path=?,title_width=?,title_height=?,content_width=?,content_height=?,priority=?,has_content=?,is_def=?,template=? where id=?";
			Model mod=new Model();
			mod.setId(id*1L);
			this.getBean(mod);
			
			Db.update(sql,mod.getName(),mod.getPath(),mod.getTitle_width(),mod.getTitle_height(),mod.getContent_width(),mod.getContent_height(),mod.getPriority(),mod.getHas_content(),mod.getIs_def(),mod.getTemplate(),mod.getId());

            setAttr("msg", "�޸�ģ�ͳɹ�!");
			
		} catch (SQLException e) {
			  setAttr("err", "�޸�ģ��ʧ��!");
			e.printStackTrace();
		}
		
       }
    	   index();
	}

}
