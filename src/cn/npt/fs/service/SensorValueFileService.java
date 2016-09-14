package cn.npt.fs.service;

import java.util.List;
import java.util.Map;

import cn.npt.fs.CachePoolFactory;
import cn.npt.fs.cache.BaseMemoryCache;
import cn.npt.fs.config.CacheBlockCfg;
import cn.npt.fs.config.CachePoolTreeCfg;
import cn.npt.util.math.SensorFileKit;
/**
 * 读取原始文件
 * @author Leonardo
 *
 */
public class SensorValueFileService {

	/**
	 * 获取从time时刻开始的size个数据
	 * @param sensorId
	 * @param time
	 * @param size
	 * @return 若缓存未配置或配置错误则返回null
	 */
	public static List<Double> getSensorValue(long sensorId,long time,int size){
		CachePoolTreeCfg tcfg=getCfgBySensorId(sensorId);
		if(tcfg==null){
			return null;
		}
		CacheBlockCfg cfg=new CacheBlockCfg(tcfg.getBlockIntervalInMs(), tcfg.getSize(), tcfg.getfileHandler());
		return getSensorValue(cfg, sensorId, time, size, tcfg.getDataDir());
	}
	/**
	 * 获取从time时刻开始的size个数据
	 * <br>区别于getSensorValue(long sensorId,long time,int size)，避免递归调用getCfgBySensorId(long sensorId)
	 * @param cfg
	 * @param sensorId
	 * @param time
	 * @param size
	 * @return
	 * @see #getSensorValue(long, long, int)
	 */
	private static List<Double> getSensorValue(CacheBlockCfg cfg,long sensorId,long time,int size,String dataDir){
		
		String fileName=dataDir+SensorFileKit.getFileNameBySensorId(sensorId)+SensorFileKit.getFileNameByTime(time, cfg.getCapacity(),cfg.timeUnit)+"/sensor.dat";
		List<Double> temp=SensorFileKit.read(fileName);
		if(temp.size()==0){//文件不存在，填充NaN
			for(int i=0;i<cfg.persistenceSize;i++){
				temp.add(Double.NaN);
			}
		}
		int startIndex=SensorFileKit.getIndexAtFileFinal(time, cfg.persistenceSize, cfg.blockInterval);
		int length=temp.size()-startIndex;
		if(length>=size){
			return temp.subList(startIndex, startIndex+size);
		}
		else{
			time+=length*cfg.blockInterval;
			size-=length;
			//递归调用
			List<Double> rs=temp.subList(startIndex, temp.size());
			rs.addAll(getSensorValue(cfg,sensorId, time, size, dataDir));
			return rs;
		}
	}
	/**
	 * 通过SensorId获取缓存配置
	 * @param sensorId
	 * @return null表示该sensor没有加入缓存池
	 */
	public static CachePoolTreeCfg getCfgBySensorId(long sensorId){
		Map<String, BaseMemoryCache> pools=CachePoolFactory.getCachePools();
		for(BaseMemoryCache pool:pools.values()){
			if(pool.getSensorFragments().containsKey(sensorId)){
				return pool.getCptcClone();
			}
		}
		return null;
	}
}
