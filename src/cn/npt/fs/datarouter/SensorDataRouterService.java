package cn.npt.fs.datarouter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.npt.fs.CachePoolFactory;
import cn.npt.fs.cache.BaseMemoryCache;
import cn.npt.fs.datarouter.history.BS4SqlService;
import cn.npt.fs.datarouter.real.CacheDataService;
import cn.npt.fs.service.SensorValueFileService;

/**
 * 数据访问
 * @author Leonardo
 *
 */
public class SensorDataRouterService {
	
	public static String timeOfLong2String(long time){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date(time));
	}
	public static long timeOfString2Long(String time){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return sdf.parse(time).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return -1;
		}
	}
	/**
	 * ========================原始数据==================================================
	 */
	/**
	 * 获取从time时刻开始后的size个数据
	 * @param sensorId
	 * @param time
	 * @param size
	 * @return
	 * @see #getSensorDataBefore(long, long, int)
	 */
	public static List<Double> getSensorDataAfter(long sensorId,long time,int size){
		List<Double> rs = null;
		BaseMemoryCache cache=CachePoolFactory.getCachePool(sensorId);
		if(cache!=null){
			rs=cache.getSensorValue(sensorId, time, size);
			if(rs.size()<size){//从文件中获取余下的数据
				time-=rs.size()*cache.getCachePoolCfg().blockInterval;
				size-=rs.size();
				rs.addAll(SensorValueFileService.getSensorValue(sensorId, time, size));
			}
		}
		
		return rs;
	}
	/**
	 * 获取从time时刻之前的size个数据
	 * <br>方法:先将time=time-size*blockInterval,然后调用getSensorDataAfter(long sensorId,long time,int size)
	 * @param sensorId
	 * @param time
	 * @param size
	 * @return
	 * @see #getSensorDataAfter(long, long, int)
	 */
	public static List<Double> getSensorDataBefore(long sensorId,long time,int size){
		BaseMemoryCache cache=CachePoolFactory.getCachePool(sensorId);
		if(cache!=null){
			time-=size*cache.getCachePoolCfg().blockInterval;
			return getSensorDataAfter(sensorId, time, size);
		}
		return null;
	}
	
	/**
	 * ==============================截止累积量======================================================
	 */
	/**
	 * 截止到当天累积量
	 * @param dx
	 * @return
	 */
	public static double getSumOfCurrentDay(long sensorId,double dx){
		Calendar calendar=Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		String startTime=timeOfLong2String(calendar.getTimeInMillis());
		long currentTime=System.currentTimeMillis();
		String endTime=timeOfLong2String(currentTime);
		double sum=0;
		//小时累积
		sum+=BS4SqlService.getSumFromDB(sensorId, "iot_cvalue_inhour", startTime, endTime, dx);
		
		//10minute累积
		calendar.setTimeInMillis(currentTime);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		startTime=timeOfLong2String(calendar.getTimeInMillis());
		sum+=BS4SqlService.getSumFromDB(sensorId, "iot_cvalue_intenminute", startTime, endTime, dx);
		
		//10minute以内实时累积
		calendar.setTimeInMillis(currentTime);
		int minute=calendar.get(Calendar.MINUTE);
		minute-=minute%10;
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		sum+=CacheDataService.getSumFromCache(sensorId, calendar.getTimeInMillis(), dx);
		
		return sum;
	}
	/**
	 * 截止到本周累积
	 * @param sensorId
	 * @param dx
	 * @return
	 */
	public static double getSumOfCurrentWeek(long sensorId,double dx){
		Calendar calendar=Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_WEEK, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		String startTime=timeOfLong2String(calendar.getTimeInMillis());
		long currentTime=System.currentTimeMillis();
		String endTime=timeOfLong2String(currentTime);
		double sum=0;
		sum+=BS4SqlService.getSumFromDB(sensorId, "iot_cvalue_inday", startTime, endTime, dx);
		sum+=getSumOfCurrentDay(sensorId, dx);
		return sum;
	}
	/**
	 * 截止到本月累积
	 * @param sensorId
	 * @param dx
	 * @return
	 */
	public static double getSumOfCurrentMonth(long sensorId,double dx){
		Calendar calendar=Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		String startTime=timeOfLong2String(calendar.getTimeInMillis());
		long currentTime=System.currentTimeMillis();
		String endTime=timeOfLong2String(currentTime);
		double sum=0;
		sum+=BS4SqlService.getSumFromDB(sensorId, "iot_cvalue_inday", startTime, endTime, dx);
		sum+=getSumOfCurrentDay(sensorId, dx);
		return sum;
	}
	/**
	 * ===================================时间段的第一个数据==================================================
	 */
	/**
	 * 获取当天的第一个数据
	 * @param sensorId
	 * @return
	 */
	public static double getFirstDataOfDay(long sensorId){
		Calendar calendar=Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		List<Double> rs = SensorValueFileService.getSensorValue(sensorId, calendar.getTimeInMillis(), 1);
		if(rs!=null){
			return rs.get(0);
		}
		return -1;
	}
	/**
	 * 获取本周的第一个数据
	 * @param sensorId
	 * @return
	 */
	public static double getFirstDataOfWeek(long sensorId){
		Calendar calendar=Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_WEEK, 1);//Sunday作为一周的第一天
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		List<Double> rs = SensorValueFileService.getSensorValue(sensorId, calendar.getTimeInMillis(), 1);
		if(rs!=null){
			return rs.get(0);
		}
		return -1;
	}
	/**
	 * 获取当月的第一个数据
	 * @param sensorId
	 * @return
	 */
	public static double getFirstDataOfMonth(long sensorId){
		Calendar calendar=Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		List<Double> rs = SensorValueFileService.getSensorValue(sensorId, calendar.getTimeInMillis(), 1);
		if(rs!=null){
			return rs.get(0);
		}
		return -1;
	}
}
