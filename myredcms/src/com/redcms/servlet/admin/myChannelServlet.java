package com.redcms.servlet.admin;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.redcms.beans.Channel;
import com.redcms.beans.ChannelAttr;
import com.redcms.beans.Model;
import com.redcms.beans.ModelItem;
import com.redcms.beans.Pictures;
import com.redcms.db.Db;
import com.redcms.servlet.core.myAction;
@WebServlet("/admin/channel")
public class myChannelServlet extends myAction{

	@Override
	public void index() throws ServletException, IOException {
		
		try {
			List<Model> models=Db.query("select * from model order by priority", new BeanListHandler<Model>(Model.class));
			setAttr("models", models);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		forword("admin/channel_list.jsp");
	}
	public void toadd() throws ServletException, IOException
	{
		int mid = this.getInt("mid");
		if(mid>0)
		{
			//������ݲ�����ǰ��
			try {
				Model mo=Db.query("select * from model where id=?", new BeanHandler<Model>(Model.class),mid);

				List<ModelItem> modelItems=Db.query("select * from model_item where model_id=? and is_channel=1 and is_display=1 order by priority", new BeanListHandler<ModelItem>(ModelItem.class),mid);
				List<Channel> parentchannel=Db.query("select * from channel where parent_id=0 or parent_id is null", new BeanListHandler<Channel>(Channel.class));
				
				List<Model> models=Db.query("select * from model order by priority", new BeanListHandler<Model>(Model.class));
				//channel_add�е�����
				setAttr("mo",mo);
				setAttr("modelItems",modelItems);
				setAttr("parentchannel",parentchannel);
				setAttr("models",models);
				
				forword("admin/channel_add.jsp");
			} catch (Exception e) {
				e.printStackTrace();
				index();
			}
			
		}else
		{
			index();
		}
	}
	
	public void addsave() throws ServletException, IOException
	{
		String sql = "insert into channel(model_id,name,title,keywords,description,parent_id,pic01,pic02,priority,links,t_name,index_tem,list_tem,content_tem,create_time,txt,txt1,txt2,num01,num02,date1,date2) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Channel channel = new Channel();
		this.getBean(channel);
		channel.setCreate_time(new Date());
		try {
			Object rowparam[] = new Object[] {channel.getModel_id(),channel.getName(),channel.getTitle(),channel.getKeywords(),channel.getDescription(),channel.getParent_id(),channel.getPic01(),channel.getPic02(),channel.getPriority(),channel.getLinks(),channel.getT_name(),channel.getIndex_tem(),channel.getList_tem(),channel.getContent_tem(),channel.getCreate_time(),
					channel.getTxt(),channel.getTxt1(),channel.getTxt2(),channel.getNum01(),channel.getNum02(),channel.getDate1(),channel.getDate2()};
		
			Db.update(sql,rowparam);
			//�õ�channel�����id
			Object obj = Db.query("select LAST_INSERT_ID() from Dual", new ArrayHandler())[0];
			long lastid = 0;
			if(obj instanceof Long)
			{
				lastid = (Long)obj;
			}else if(obj instanceof BigInteger)
			{
				lastid = ((BigInteger)obj).longValue();
			}
			//�ڶ���������pictures��  ͼ��
			
			//��Ҫ����ͼ��
			for(int i=0;i<3;i++)
			{
				String [] ids = req.getParameterValues("pics"+i+"_ids");
				String [] prio = req.getParameterValues("pics"+i+"prio");
				String [] diss = req.getParameterValues("pics"+i+"dis");
				
				if(null!=ids&&null!=prio&&null!=diss&&ids.length==prio.length&&ids.length==diss.length)
				{
					String sqlba="";
					Object [][] parasm=new Object[ids.length][];
					for(int z=0;z<ids.length;z++)
					{
						Object[] row = new Object[5];
						row[0]=lastid;
						row[1]=diss[z];
						row[2]=Integer.parseInt(prio[z]);
						row[3]=i;
						row[4]=ids[z];
						
						parasm[z] = row;
					}
					Db.batch(sqlba, parasm);
					
				}
			}
			
			//�����������Ӷ����ֶ�
			
			//����Ҫ������չ�ֶ�
			//��ѯ��Щ���Զ����ֶ�
			//Ȼ���ȡ�������
			//�����ֵ����д����չ���ݱ���
			String sqlmol="select * from model_item where model_id=? and is_channel=1 and is_custom=1 order by priority";
			List<ModelItem> modelitemlist=Db.query(sqlmol, new BeanListHandler<ModelItem>(ModelItem.class),channel.getModel_id());
			if(null!=modelitemlist&&modelitemlist.size()>0)
			{
				 List<Object[]> attrlist=new ArrayList<Object[]>();
				for(ModelItem mi: modelitemlist)
				{
					//attr���Զ����ֶεı�
					 String insersql="insert into channel_attr(channel_id,field_name,field_value) values(?,?,?)";
					 String value = req.getParameter(mi.getField());
					 System.out.println(value);
					 System.out.println(mi.getField());
					 Object []row = new Object[3];
					 row[0]=lastid;
					 row[1]=mi.getField();
					 row[2]=value;
					 
					 attrlist.add(row);
					 Db.update(insersql,row);
					 
				}
			}
			
			
			setAttr("msg", "������Ŀ�ɹ�");
		} catch (Exception e) {
			setAttr("err", "������Ŀʧ��");
			e.printStackTrace();
		}
		index();
	}
	
	
	//��ת���޸Ľ���
	//�����ݿ��в������ֵ����ǰ�˲���ʾ
	public void channeledit() throws ServletException, IOException 
	{
		int id = this.getInt("id");
		try {
			if(id>0)
			{
				Channel channel = Db.query("select * from channel where id=?",  new BeanHandler<Channel>(Channel.class),id);
				Model mo = Db.query("select * from Model where id=?", new BeanHandler<Model>(Model.class),id);
				//��Ŀ�ֶ�
				List<ModelItem> modelItems=Db.query("select * from model_item where model_id=? and is_channel=1 and is_display=1 order by priority", new BeanListHandler<ModelItem>(ModelItem.class),mo.getId());
				//�����ֶ�
				List<ChannelAttr> mapattr = Db.query("select * from channel_attr where channel_id=?",new BeanListHandler<ChannelAttr>(ChannelAttr.class),id);
				
				Map<String,String> channalattr = new HashMap<String,String>();
				//�����չ�ֶβ�Ϊ��
				if(null!=mapattr)
				{
					for(ChannelAttr ca:mapattr)
					{
						channalattr.put(ca.getField_name(), ca.getField_value());
						
					}
				}
				//��channel
				List<Channel> parentchannel = Db.query("select * from channel where parent_id=0 or parent_id is null", new BeanListHandler<Channel>(Channel.class));
				//ͼ��һ
				List<Pictures> pics1=Db.query("select * from pictures where channel_id=? and sequ=1 ", new BeanListHandler<Pictures>(Pictures.class),id);
				//ͼ����
				List<Pictures> pics2=Db.query("select * from pictures where channel_id=? and sequ=2 ", new BeanListHandler<Pictures>(Pictures.class),id);
				setAttr("channalattr", channalattr);
				setAttr("pics1", pics1);
				setAttr("pics2", pics2);
				setAttr("mo", mo);
				setAttr("modelItems", modelItems);
				setAttr("parentchannel", parentchannel);
				setAttr("channel",channel);
				
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
		this.forword("admin/channel_edit.jsp");
	}
	
	//�޸ı༭ 
		public void editsave() throws ServletException, IOException 
		{
			try {
				//�޸���Ŀ��ֵ
				Channel channel = new Channel();
				this.getBean(channel);
				channel.setCreate_time(new Date());
				String sql="update channel set model_id=?,name=?,title=?,keywords=?,description=?,parent_id=?,pic01=?,pic02=?,priority=?,links=?,t_name=?,index_tem=?,list_tem=?,content_tem=?,create_time=?,txt=?,txt1=?,txt2=?,num01=?,num02=?,date1=?,date2=? where id=?";
				Object rowparam[]=new Object[] {channel.getModel_id(),channel.getName(),channel.getTitle(),channel.getKeywords(),channel.getDescription(),channel.getParent_id(),channel.getPic01(),channel.getPic02(),channel.getPriority(),channel.getLinks(),channel.getT_name(),channel.getIndex_tem(),channel.getList_tem(),channel.getContent_tem(),channel.getCreate_time(),
						channel.getTxt(),channel.getTxt1(),channel.getTxt2(),channel.getNum01(),channel.getNum02(),channel.getDate1(),channel.getDate2(),channel.getId()};
				Db.update(sql,rowparam);
				
				//ɾ����չ�ֶ�
				Db.update("delete from channel_attr where channel_id=?",channel.getId());
				
				//�޸���չ�ֶ�
				String sqlmol="select * from model_item where model_id=? and is_channel=1 and is_custom=1 order by priority";
				List<ModelItem> modelitemlist = Db.query(sqlmol, new BeanListHandler<ModelItem>(ModelItem.class),channel.getModel_id());
				
				//���������չ�ֶ�
				if(null!=modelitemlist&&modelitemlist.size()>0)
				{
					String insersql="insert into channel_attr(channel_id,field_name,field_value) values(?,?,?)";
					for(ModelItem mi:modelitemlist)
					{
						String value = req.getParameter(mi.getField());
						Object[] row = new Object[3];
						row[0]=channel.getId();
						row[1]=mi.getField();
						row[2]=value;
						Db.update(insersql,row);
					}
					
					
				}
				
				
				//��Ҫ����ͼ��
				
				for(int i=1;i<3;i++)
				{
					
					String [] ids=req.getParameterValues("pics"+i+"_ids");
					String [] prio=req.getParameterValues("pics"+i+"_priority");
					String [] diss=req.getParameterValues("pics"+i+"_dis");
					
					if(null!=ids&&null!=prio&&null!=diss&&ids.length==prio.length&&ids.length==diss.length)
					{
						
						String sqlba = "";
						Object[][]parasm = new Object[ids.length][];
						for(int z=0;z<ids.length;z++)
						{	
							Object[] row = new Object[5];
							row[0]=channel.getId();
							row[1]=diss[z];
							row[2]=Integer.parseInt(prio[z]);
							row[3]=i;
							row[4]=ids[z];
							
							parasm[z]=row;
							
							
						}
						
						Db.batch(sqlba, parasm);
					}

				}
				
				setAttr("msg", "�޸ĳɹ�!");
			} catch (Exception e) {
				e.printStackTrace();
			}
			index();
			
		}
	
		
		//ɾ����Ŀ
		
		public void channeldel() throws ServletException, IOException 
		{
			int id=this.getInt("id");
			if(id>0) 
			{
				try {
					
					//ɾ����Ŀ��չ�ֶ�
					Db.update("delete from channel_attr where channel_id=?",id);
					
					//ɾ��ͼ��
					Db.update("delete from pictures where channel_id=?",id);
					
					//ɾ����Ŀ����Ŀ
					Db.update("delete from channel where parent_id=?",id);
					
					Db.update("delete from channel where id=?",id);
					setAttr("msg", "ɾ���ɹ�!");
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			index();
		}

}
