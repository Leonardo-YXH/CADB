package cn.npt.fs.ext.event;

import java.util.Map;

import cn.npt.fs.CachePoolFactory;
import cn.npt.fs.cache.BaseMemoryCache;
import cn.npt.fs.cache.CachePool;

/**
 * 监视数据是否正常接收
 * @author Leonardo
 *
 */
public class SimulateEmitHandler {
	/**
	 * 失效时长，默认10分钟
	 */
	private static long expiryTime=600000L;
	
	public static void checkTimeout(CachePool<Double> cache,double defaultValue,long currentTime){
		if(currentTime-cache.getCurrentTime()>=expiryTime){
			cache.setValue(defaultValue, currentTime);
		}
	}
	/**
	 * 监视所有的传感器，如果有超时未接收到数据的则触发虚拟数据
	 */
	@SuppressWarnings("unchecked")
	public static void setMonitorSensor(){
		long currentTime=System.currentTimeMillis();
		Map<String,BaseMemoryCache> pools=CachePoolFactory.getCachePools();
		for(BaseMemoryCache cache:pools.values()){
			Map<Long, CachePool<?>> sensorPools=cache.getSensorFragments();
			for(CachePool<?> pool:sensorPools.values()){
				checkTimeout((CachePool<Double>)pool, 0, currentTime);
			}
		}
	}
}
