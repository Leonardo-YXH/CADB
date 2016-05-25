package cn.npt.db.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import cn.npt.util.data.PathKit;
/**
 * 配置druid
 * @author Leonardo
 *
 */
public class NptDBUtil {

	private static DataSource ds;
	private static Logger log=Logger.getLogger(NptDBUtil.class);
	static{
		initDruid();
	}
	public static Connection openConnection() throws SQLException{
		return ds.getConnection();
	}
	private static void initDruid(){
		InputStream in=null;
		try {
			in = new FileInputStream(new File(PathKit.getRootClassPath()+"/npfsdatabase.properties"));
			Properties pros=new Properties();
			pros.load(in);
			ds=DruidDataSourceFactory.createDataSource(pros);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		catch ( IOException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		
	}
	/**
	 * 执行inert，delete，update
	 * @param sql
	 * @return -1表示执行失败
	 */
	public static int update(String sql){
		try {
			Connection conn=openConnection();
			Statement statement=conn.createStatement();
			int rs = statement.executeUpdate(sql);
			statement.close();
			conn.close();
			return rs;
		} catch (SQLException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		return -1;
	}
	/**
	 *执行查询操作
	 * @param sql
	 * @return
	 */
	public static List<Map<String,Object>> find(String sql){
		List<Map<String,Object>> result=new ArrayList<Map<String,Object>>();
		try {
			Connection conn=openConnection();
			Statement statement=conn.createStatement();
			ResultSet rs=statement.executeQuery(sql);
			ResultSetMetaData rsmd=rs.getMetaData();
			int count=rsmd.getColumnCount();
			while(rs.next()){
				Map<String,Object> row=new HashMap<String, Object>();
				for(int i=1;i<=count;i++){//column索引从1开始
					row.put(rsmd.getColumnName(i), rs.getObject(i));
				}
				result.add(row);
			}
			
			rs.close();
			statement.close();
			conn.close();//druid是否会管理连接
		} catch (SQLException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		
		return result;
	}
	/**
	 * 获取数据源
	 * @return
	 */
	public static DataSource getDataSource(){
		return ds;
	}
	/**
	 * test for insert,delete,update,find
	 * @param args
	 */
	/*public static void main(String[] args){
		String sql="select * from cc_furmula";
		//String sql2="insert into city values(3,'上海',232)";
		//String sql2="update city set name='北京' where id=3";
		//String sql2="delete from city where id=3";
		//update(sql2);
		List<Map<String,Object>> rs=find(sql);
		//rs.stream().forEach((x)->(x.forEach((z,y)->System.out.println(z+":"+y.toString()))));
		System.out.println(rs.size());
	}*/
}
