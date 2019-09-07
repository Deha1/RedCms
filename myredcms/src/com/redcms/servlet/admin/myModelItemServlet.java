package com.redcms.servlet.admin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import org.apache.catalina.util.MIME2Java;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.redcms.beans.Model;
import com.redcms.beans.ModelItem;
import com.redcms.db.Db;
import com.redcms.servlet.core.myAction;


@WebServlet("/admin/modelItem")

public class myModelItemServlet extends myAction{

	@Override
	public void index() throws ServletException, IOException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * �����ֶλ�����Ŀ�ֶι���
	 * @throws ServletException
	 * @throws IOException
	 */
	public void channelList() throws ServletException, IOException
	{
		//Ϊ0��ʾ���ݣ�Ϊ1��ʾ��Ŀ
		int ischannel=null!=req.getAttribute("is_channel")?(Integer)req.getAttribute("is_channel"):this.getInt("ischannel");
		int modelId = null!=req.getAttribute("id")?((Long)req.getAttribute("id")).intValue():this.getInt("id");
		//����modelId,���õ����е�model_Item,���Դ����㴦�����򲻴�����ֵʱ��ʾ������ֵ�Ļ��Ͳ���ʾ��
	if(modelId>0)
	{
		try {
			String sql="select * from model_item where model_id=? and is_channel=?  order by id";
			
			List<ModelItem> showlist=Db.query(sql, new BeanListHandler<ModelItem>(ModelItem.class),modelId,ischannel);
			
			//?
			 setAttr("showlist", showlist);
		     setAttr("ischannel", ischannel);
		     
		     Model model=Db.query("select * from model where id=?", new BeanHandler<Model>(Model.class),modelId);
		     setAttr("model", model);
		     
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.forword("admin/modelItem_list.jsp");
	}
	
	}
	
	/**
	 * ����һ���û��Զ�����ֶ�
	 * @throws ServletException
	 * @throws IOException
	 */
	public void addSave() throws ServletException, IOException 
	{
		   ModelItem mi=new ModelItem();
		   this.getBean(mi);
		   if(mi.getIs_display()==-1)mi.setIs_display(0);
		   if(mi.getIs_required()==-1)mi.setIs_required(0);
		   if(mi.getIs_single()==-1)mi.setIs_single(0);
		System.out.println(mi.getModel_id()+"---------------->");
			try {
				String sql="insert into model_item(model_id,field,field_dis,priority,def_value,opt_value,txt_size,help_info,data_type,is_single,is_channel,is_custom,is_display,is_required) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			    Db.update(sql,mi.getModel_id(),mi.getField(),mi.getField_dis(),mi.getPriority(),mi.getDef_value(),mi.getOpt_value(),mi.getTxt_size(),mi.getHelp_info(),mi.getData_type(),mi.getIs_single(),mi.getIs_channel(),mi.getIs_custom(),mi.getIs_display(),mi.getIs_required());
				System.out.println("123");
			    setAttr("ischannel", mi.getIs_channel());
				setAttr("id", mi.getModel_id());
				setAttr("msg", "�����Զ����ֶγɹ���");
			} catch (SQLException e) {
				setAttr("err", "�����Զ����ֶ�ʧ�ܣ�");
				e.printStackTrace();
			}
			
			 channelList();
		
		
	}
	

	/**
	 * �޸�ÿ���ֶ�
	 * @throws ServletException
	 * @throws IOException
	 */
	public void update() throws ServletException, IOException
	{
		ModelItem mi=new ModelItem();
		 this.getBean(mi);
		if(mi.getIs_display()==-1)
			mi.setIs_display(0);
		if(mi.getIs_required()==-1)
			mi.setIs_required(0);
		if(mi.getIs_single()==-1)
			mi.setIs_single(0);
		
		try {
			String sql="update model_item set model_id=?,field=?,field_dis=?,priority=?,def_value=?,opt_value=?,txt_size=?,help_info=?,data_type=?,is_single=?,is_channel=?,is_custom=?,is_display=?,is_required=? where id=?";
			Db.update(sql,mi.getModel_id(),mi.getField(),mi.getField_dis(),mi.getPriority(),mi.getDef_value(),mi.getOpt_value(),mi.getTxt_size(),mi.getHelp_info(),mi.getData_type(),mi.getIs_single(),mi.getIs_channel(),mi.getIs_custom(),mi.getIs_display(),mi.getIs_required(),mi.getId());
			
			setAttr("ischannel",mi.getIs_channel());
			setAttr("id",mi.getId());
			setAttr("msg","�޸��Զ����ֶγɹ���");
			
		} catch (Exception e) {
			setAttr("err", "�޸��Զ����ֶ�ʧ�ܣ�");
			e.printStackTrace();
		}
		
		 channelList();
	}
	
	/**
	 * ��������
	 * @throws ServletException
	 * @throws IOException
	 */
	public void updateBatchId() throws ServletException, IOException
	{
		
		//action=updateBatchId&modelId=1&ischannel=0&miid=42&miid=43
		try {
			//ǿ��ת��Ϊ��ά����
			String[] values = req.getParameterValues("miid");
			//��ʼ����ά����
			Object[][] params =  new Object[values.length][];
			for(int i=0;i<params.length;i++)
			{
				//��ʼ����ά����
				params[i] = new Object[] {Integer.parseInt(values[i])};
			}
			
			//����������
			Db.batch("update model_item set is_display=0 where id=?", params);
		
			
			System.out.println(req.getRequestURL());
			setAttr("ischannel",this.getInt("ischannel"));
			//��Ϊ���ݿ��У�i��bigInt��
			setAttr("id", Long.parseLong(this.getString("modelId")));
			
			setAttr("err", "�������سɹ���");
		} catch (Exception e) {
			setAttr("err", "��������ʧ�ܣ�");
			e.printStackTrace();
		}
		
		channelList();
		
		
	}
}
