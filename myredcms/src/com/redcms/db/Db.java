package com.redcms.db;

import java.sql.Connection;

import java.sql.SQLException;
import java.util.ResourceBundle;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

import org.apache.log4j.Logger;

import com.alibaba.druid.pool.DruidDataSource;

//�������ݿ�Ĺ�����
public class Db { //extends QueryRunner 
	
	 private static Logger log=Logger.getLogger( Db.class);
	 private static QueryRunner run=new QueryRunner();
	 private static DruidDataSource ds = null;
	 
	 private static ThreadLocal<Connection> conn = new ThreadLocal<Connection>();
	 
	 static{
		  //��ʼ�����ӳ�
		   try {
				ResourceBundle res=ResourceBundle.getBundle("jdbc");
				
				ds=new DruidDataSource();
				ds.setUrl(res.getString("url"));
				ds.setDriverClassName(res.getString("driverClassName"));
				ds.setUsername(res.getString("username"));
				ds.setPassword(res.getString("password"));
				ds.setFilters(res.getString("filters"));
				ds.setMaxActive(Integer.parseInt(res.getString("maxActive")));
				ds.setInitialSize(Integer.parseInt(res.getString("initialSize")));
				ds.setMaxWait(Long.parseLong(res.getString("maxWait")));
				ds.setMinIdle(Integer.parseInt(res.getString("minIdle")));
				ds.setValidationQuery("SELECT 'x'");
				ds.setTestWhileIdle(true);
				ds.setTestOnBorrow(false);
				ds.setTestOnReturn(false);
				ds.setTimeBetweenEvictionRunsMillis(600000);
				//ds.setMaxIdle(Integer.parseInt(res.getString("maxIdle")));

				//ds.setTimeBetweenEvictionRunsMillis(Long.parseLong(res.getString("timeBetweenEvictionRunsMillis")));
				//ds.setMinEvictableIdleTimeMillis(Long.parseLong(res.getString("minEvictableIdleTimeMillis")));
				//ds.setValidationQuery(res.getString("validationQuery"));
				
				
			} catch (Exception e) {
				log.error("com.redcms.db.Db.ERROR_003_��ʼ�����ӳ�ʧ��");
			} 
	   }
	 
	 
	  /**
	     * ͨ��DataSource�õ�Connection  
	     * @return
	     * @throws SQLException
	     */
	 public static Connection getConnection() throws SQLException{
		//�õ�ThreadLocal�е�connection
		 Connection con = conn.get();
		 
		 if(null==con||con.isClosed())
	        {
	        	con=ds.getConnection();
	        	conn.set(con);
	        }
		 return con;
		 
	 }
	  /**
	     * ��������  
	     * @throws SQLException
	     */
	 public static void beginTransaction() throws SQLException {
		//�����Ϊ�գ���������  
		 Connection con = getConnection(); 
		//���������ύΪ�ֶ�
		 con.setAutoCommit(false);
		//�ѵ�ǰ�������������ThreadLocal�� 
		 conn.set(con);
		 
	 }
	 
	   /**
	     * �ύ����  
	     * @throws SQLException
	     */
	  public static void commitTransaction() throws SQLException{
		  //�õ�ThreadLocal�е�connection  
		  Connection con =getConnection();
		  //�ж�con�Ƿ�Ϊ�գ����Ϊ�գ���˵��û�п�������  
		  if(con==null) {
			  throw new SQLException("û�п�������,�����ύ����");  
		  }
		  //���con��Ϊ��,�ύ����  
	        con.commit();  
	        //�����ύ�󣬹ر�����  
	        con.close();  
	        //�������Ƴ�ThreadLocal  
	        conn.remove(); 
	  }
	  
	   /**
	     * �ع�����  
	     * @throws SQLException
	     */
	  
	  public static void rollbackTransaction() {
		  try {
			//�õ�ThreadLocal�е�connection  
			Connection con = conn.get();  
			//�ж�con�Ƿ�Ϊ�գ����Ϊ�գ���˵��û�п�������Ҳ�Ͳ��ܻع�����  
			if(con == null){  
			    throw new SQLException("û�п�������,���ܻع�����");  
			}  
			//����ع�  
			con.rollback();  
			//����ع��󣬹ر�����  
			con.close();  
			//�������Ƴ�ThreadLocal  
			conn.remove();
		  } catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
		  
	  }
	  /**
	     * �ر�����  
	     * @param connection
	     * @throws SQLException
	     */
	  
	  public static void releaseConnection(Connection connection) throws SQLException {
		//�õ�ThreadLocal�е�connection  
		  Connection con = conn.get();
		  //������������뵱ǰ�������Ӳ���ȣ���˵���������Ӳ����������ӣ����Թرգ�����������ر�  
		  if(connection!=null&&con!=connection) {
			  if(!connection.isClosed()) {
				  //�������û�б��رգ��ر�֮  
				  connection.close();
			  }
		  } 
	  }
	  public static void closeDataSource() {
		  if(null!=ds) {
			  ds.close();
		  }
	  }
	  
	  //---------------------��дQueryRuner�еķ���------------------------
	  
	  
	    public static  int[] batch(String sql, Object[][] params)  throws SQLException 
	    {  
	        Connection conn = getConnection();  
	        int[] result = run.batch(conn, sql, params);  
	        releaseConnection(conn);  
	        return result;  
	    }  
	  
	 
	    public static <T> T query(String sql, ResultSetHandler<T> rsh,  
	            Object... params) throws SQLException {  
	        Connection conn = getConnection();  
	        T result =  run.query(conn, sql, rsh, params);  
	        releaseConnection(conn);  
	        return result;  
	    }  
	    
	    public static <T> T query(String sql, ResultSetHandler<T> rsh) throws SQLException
	    {
	    	Connection conn = getConnection(); 
	    	T result = run.query(conn, sql, rsh);
	    	releaseConnection(conn);
	    	return result;
	    	
	    }
	    
	    public static int update (String sql,Object...params) throws SQLException
	    {
	    	Connection conn = getConnection(); 
	    	int result = run.update(conn,sql,params);
	    	releaseConnection(conn);
	    	return result;
	    	
	    }
	    
	    public static int update (String sql,Object params ) throws SQLException
	    {
	    	Connection conn = getConnection(); 
	    	int result = run.update(conn,sql,params);
	    	releaseConnection(conn);
	    	return result;
	    	
	    }
	    
	    public static int update (String sql ) throws SQLException
	    {
	    	Connection conn = getConnection(); 
	    	int result = run.update(conn,sql);
	    	releaseConnection(conn);
	    	return result;
	    	
	    }
	  
	  
	  
	  
	  
	  
	

}
