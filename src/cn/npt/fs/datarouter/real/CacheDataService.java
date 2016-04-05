package cn.npt.fs.datarouter.real;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import cn.npt.fs.CachePoolFactory;

public class CacheDataService {
	/**
	 * 获取从startTime到当前时刻的累积值
	 * @param sensorId
	 * @param startTime eg:2016-02-01 12:25:30
	 * @param dx 积分因子,若无量纲则设1
	 * @return
	 */
	public static double getSumFromCache(long sensorId,String startTime,double dx){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			sdf.parse(startTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long time=sdf.getCalendar().getTimeInMillis();
		
		return getSumFromCache(sensorId, time, dx);
	}
	/**
	 * 获取从startTime到当前时刻的累积值
	 * @param sensorId
	 * @param startTime
	 * @param dx
	 * @return
	 */
	public static double getSumFromCache(long sensorId,long startTime,double dx){
		List<Double> datas=CachePoolFactory.getCachePool(sensorId).getSensorValue(sensorId, startTime);
		double sum=0;
		for(double v:datas){
			sum+=v;
		}
		sum*=dx;
		return sum;
	}
	
}
