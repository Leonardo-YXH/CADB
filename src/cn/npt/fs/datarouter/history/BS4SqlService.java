package cn.npt.fs.datarouter.history;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import cn.npt.db.util.NptDBUtil;

/**
 * 从数据库获取累积量
 * @author Leonardo
 *
 */
public class BS4SqlService {

	/**
	 * 
	 * @param sensorId
	 * @param table
	 * @param startTime
	 * @param endTime
	 * @param dx 微分单元,此处指单位时长
	 * @return
	 */
	public static double getSumFromDB(long sensorId,String table,String startTime,String endTime,double dx){
		String sql="select * from "+table+" where sensorId="+sensorId
					+" and createdTime>='"+startTime
					+"' and createdTime<'"+endTime+"'";
		double sum=0;
		List<Map<String, Object>> rs=NptDBUtil.find(sql);
		for(Map<String,Object> row:rs){
			double avgV=Double.parseDouble(row.get("avgV").toString());
			sum+=avgV*dx;
		}
		return sum;
	}
	/**
	 * 该方法只限与1s-1month的表统计
	 * @param sensorId
	 * @param table
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static double getSumFromDB(long sensorId,String table,String startTime,String endTime){
		double dx=1;
		if(table.equals("iot_cvalue_inmonth")){
			String sql="select * from "+table+" where sensorId="+sensorId
					+" and createdTime>='"+startTime
					+"' and createdTime<'"+endTime+"'";
			double sum=0;
			List<Map<String, Object>> rs=NptDBUtil.find(sql);
			for(Map<String,Object> row:rs){
				double avgV=Double.parseDouble(row.get("avgV").toString());
				String millisStr=row.get("createdTime").toString();
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
				try {
					sdf.parse(millisStr);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				dx=86400*sdf.getCalendar().getActualMaximum(Calendar.DAY_OF_MONTH);//当月的天数*86400s
				sum+=avgV*dx;
			}
			return sum;
		}
		else{
			dx=getDxAsTime(table);
			return getSumFromDB(sensorId, table, startTime, endTime, dx);
		}
	}
	/**
	 * 从表名获取单位时长,以秒为单位
	 * @param table
	 * @return
	 */
	private static double getDxAsTime(String table){
		double dx=-1;
		switch(table){
		case "iot_cvalue_insecond":dx=1;
			break;
		case "iot_cvalue_intensecond":dx=10;
			break;
		case "iot_cvalue_inminute":dx=60;
			break;
		case "iot_cvalue_intenminute":dx=600;
			break;
		case "iot_cvalue_inhour":dx=3600;
			break;
		case "iot_cvalue_inday":dx=86400;
			break;
		case "iot_cvalue_inweek":dx=604800;
			break;
		default:dx=1;
			break;
		}
		return dx;
	}
	
	public static void main(String[] args) {
		String startTime="2016-02-01";
		String endTime="2016-04-01";
		System.out.println(getSumFromDB(1, "iot_cvalue_inmonth", startTime, endTime));
	}
}
