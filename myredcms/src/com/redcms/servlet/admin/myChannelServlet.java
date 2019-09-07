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
			//查出数据并传到前端
			try {
				Model mo=Db.query("select * from model where id=?", new BeanHandler<Model>(Model.class),mid);

				List<ModelItem> modelItems=Db.query("select * from model_item where model_id=? and is_channel=1 and is_display=1 order by priority", new BeanListHandler<ModelItem>(ModelItem.class),mid);
				List<Channel> parentchannel=Db.query("select * from channel where parent_id=0 or parent_id is null", new BeanListHandler<Channel>(Channel.class));
				
				List<Model> models=Db.query("select * from model order by priority", new BeanListHandler<Model>(Model.class));
				//channel_add中的属性
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
			//得到channel的最后id
			Object obj = Db.query("select LAST_INSERT_ID() from Dual", new ArrayHandler())[0];
			long lastid = 0;
			if(obj instanceof Long)
			{
				lastid = (Long)obj;
			}else if(obj instanceof BigInteger)
			{
				lastid = ((BigInteger)obj).longValue();
			}
			//第二步：更改pictures表  图集
			
			//需要操作图集
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
			
			//第三步：增加额外字段
			
			//还需要增加扩展字段
			//查询吧些是自定义字段
			//然后获取请求参数
			//如果有值，就写入扩展数据表中
			String sqlmol="select * from model_item where model_id=? and is_channel=1 and is_custom=1 order by priority";
			List<ModelItem> modelitemlist=Db.query(sqlmol, new BeanListHandler<ModelItem>(ModelItem.class),channel.getModel_id());
			if(null!=modelitemlist&&modelitemlist.size()>0)
			{
				 List<Object[]> attrlist=new ArrayList<Object[]>();
				for(ModelItem mi: modelitemlist)
				{
					//attr是自定义字段的表
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
			
			
			setAttr("msg", "增加栏目成功");
		} catch (Exception e) {
			setAttr("err", "增加栏目失败");
			e.printStackTrace();
		}
		index();
	}
	
	
	//跳转到修改界面
	//将数据库中查出来的值传给前端并显示
	public void channeledit() throws ServletException, IOException 
	{
		int id = this.getInt("id");
		try {
			if(id>0)
			{
				Channel channel = Db.query("select * from channel where id=?",  new BeanHandler<Channel>(Channel.class),id);
				Model mo = Db.query("select * from Model where id=?", new BeanHandler<Model>(Model.class),id);
				//栏目字段
				List<ModelItem> modelItems=Db.query("select * from model_item where model_id=? and is_channel=1 and is_display=1 order by priority", new BeanListHandler<ModelItem>(ModelItem.class),mo.getId());
				//额外字段
				List<ChannelAttr> mapattr = Db.query("select * from channel_attr where channel_id=?",new BeanListHandler<ChannelAttr>(ChannelAttr.class),id);
				
				Map<String,String> channalattr = new HashMap<String,String>();
				//如果扩展字段不为空
				if(null!=mapattr)
				{
					for(ChannelAttr ca:mapattr)
					{
						channalattr.put(ca.getField_name(), ca.getField_value());
						
					}
				}
				//父channel
				List<Channel> parentchannel = Db.query("select * from channel where parent_id=0 or parent_id is null", new BeanListHandler<Channel>(Channel.class));
				//图集一
				List<Pictures> pics1=Db.query("select * from pictures where channel_id=? and sequ=1 ", new BeanListHandler<Pictures>(Pictures.class),id);
				//图集二
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
	
	//修改编辑 
		public void editsave() throws ServletException, IOException 
		{
			try {
				//修改栏目的值
				Channel channel = new Channel();
				this.getBean(channel);
				channel.setCreate_time(new Date());
				String sql="update channel set model_id=?,name=?,title=?,keywords=?,description=?,parent_id=?,pic01=?,pic02=?,priority=?,links=?,t_name=?,index_tem=?,list_tem=?,content_tem=?,create_time=?,txt=?,txt1=?,txt2=?,num01=?,num02=?,date1=?,date2=? where id=?";
				Object rowparam[]=new Object[] {channel.getModel_id(),channel.getName(),channel.getTitle(),channel.getKeywords(),channel.getDescription(),channel.getParent_id(),channel.getPic01(),channel.getPic02(),channel.getPriority(),channel.getLinks(),channel.getT_name(),channel.getIndex_tem(),channel.getList_tem(),channel.getContent_tem(),channel.getCreate_time(),
						channel.getTxt(),channel.getTxt1(),channel.getTxt2(),channel.getNum01(),channel.getNum02(),channel.getDate1(),channel.getDate2(),channel.getId()};
				Db.update(sql,rowparam);
				
				//删除扩展字段
				Db.update("delete from channel_attr where channel_id=?",channel.getId());
				
				//修改扩展字段
				String sqlmol="select * from model_item where model_id=? and is_channel=1 and is_custom=1 order by priority";
				List<ModelItem> modelitemlist = Db.query(sqlmol, new BeanListHandler<ModelItem>(ModelItem.class),channel.getModel_id());
				
				//如果存在拓展字段
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
				
				
				//需要操作图集
				
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
				
				setAttr("msg", "修改成功!");
			} catch (Exception e) {
				e.printStackTrace();
			}
			index();
			
		}
	
		
		//删除栏目
		
		public void channeldel() throws ServletException, IOException 
		{
			int id=this.getInt("id");
			if(id>0) 
			{
				try {
					
					//删除栏目扩展字段
					Db.update("delete from channel_attr where channel_id=?",id);
					
					//删除图集
					Db.update("delete from pictures where channel_id=?",id);
					
					//删除栏目子栏目
					Db.update("delete from channel where parent_id=?",id);
					
					Db.update("delete from channel where id=?",id);
					setAttr("msg", "删除成功!");
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			index();
		}

}
