package com.redcms.servlet.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;


/*
 * ���Action���Ƿ�װ��Servlet�г��õĲ���
 * ����תҳ�棬���ò��������ֵ֮���
 * 
 */
public abstract class myAction extends HttpServlet 
{
	private static final long serialVersionUID = -5111943848202016062L;
	protected HttpServletRequest req;
	protected HttpServletResponse resp;
	protected SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		//ͨ��url��action=������()���Ի�ȡ���е�����ֵ��Ҳ�ɻ��html����ֵ
		String method= null!=req.getParameter("action")?req.getParameter("action"):"index";
		Class[]param=new Class[] {HttpServletRequest.class,HttpServletResponse.class};
	 
		Class clazz=this.getClass();
		try {
			Method m=clazz.getDeclaredMethod(method, new Class[] {});
			if(null!=m)
			{
				this.req=req;
				this.resp=resp;
				m.invoke(this, new Object[] {});
			}
		} catch (Exception e) {
			e.printStackTrace();
			 System.out.println("ERROR_001_�Ҳ���Ŀ�귽����");
		}
	}
	
	
	//Ĭ��Ҫʵ�ֵķ���
	public abstract void index() throws ServletException, IOException;
	//ȡ�ַ���
	public String getString(String param)
	{
		return null!=req.getParameter(param)?req.getParameter(param):"";
	}
	
	//ȡ�ַ�������
	public String[] getStringArray(String param)
	{
		return req.getParameterValues(param);
	}
	
	//����һ�����ֲ���
	public int getInt(String param)
	{
		int re=-1;
		String str=this.getString(param);
		if(str.matches("\\d+"))
		{
			re=Integer.parseInt(str);
		}
		return re;
		
	}
	public long getLong(String param)
	{
		long re=0;
		String str=this.getString(param);
		if(str.matches("\\d+"))
		{
			re=Long.parseLong(str);
		}
		return re;
	}
	//������������Bean����
	public void getBean(Object bean)
	{
		//����bean��������ʱ�����ͣ�����clazz
		Class clazz=bean.getClass();
		//�õ�������͵������ֶ�
		Field [] all=clazz.getDeclaredFields();
		if(null!=all&&all.length>0)
		{
		try {
			for(Field f:all)
			{
				f.setAccessible(true);
				String fname= f.getName();//���ش� Field �����ʾ���ֶε����ơ�
				Class types = f.getType();//����һ�� Class ��������ʶ�˴� Field ��������ʾ�ֶε��������͡�
				String paramv=this.getString(fname);
				if(types==String.class)//�����String����
				{
					f.set(bean, paramv);//��ָ����������ϴ� Field �����ʾ���ֶ�����Ϊָ������ֵ
				}else if(types==Integer.class||types==int.class||types==Integer.TYPE)
				{
					f.set(bean, this.getInt(fname));
				}else if(types==Long.class||types==long.class||types==Long.TYPE){
					if(paramv.matches("\\d+"))
					f.set(bean, Long.parseLong(paramv));
				}else if(types==Date.class)
				{
					if(paramv.matches("\\d{4}[-]\\d{2}[-]\\d{2}[ ]\\d{2}[:]\\d{2}[:]\\d{2}"))
					{
						f.set(bean, sf.parse(paramv));
					}
				}
			}
		} catch (Exception e) {
			 System.out.println("ERROR_002_������ֵ����������");
		} 
	}
}
	
	//���ò���
	public void setAttr(String key,Object value)
	{
		req.setAttribute(key, value);
	}
	//��ת
	public void forword(String path) throws ServletException, IOException
	{
		req.getRequestDispatcher("/WEB-INF/"+path).forward(req, resp);
	}
	//��ת
	public void redirect(String path) throws ServletException, IOException
	{	
			resp.sendRedirect(path);
	}
	//ֱ�����ֵ
	public void randText(String msg) throws IOException
	{
		resp.setContentType("text/html;charset=UTF-8");
		
		PrintWriter out = resp.getWriter();
		out.println(msg);
		out.close();
		
	}
	//���json
	public void randJson(Object obj) throws IOException
	{
		resp.setContentType("application/json;charset=utf-8");
		
		PrintWriter out = resp.getWriter();
		
		out.print(JSON.toJSONString(obj));
		
		out.close();
		
	}
	
	
	

}
