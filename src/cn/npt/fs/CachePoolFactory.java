package cn.npt.fs;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.npt.fs.cache.BaseMemoryCache;
import cn.npt.util.data.PathKit;

public class CachePoolFactory {
	public static Date firstCreatedTime;
	private static Map<String,BaseMemoryCache> pools=new HashMap<String, BaseMemoryCache>();
	/**
	 * 通过配置文件名创建一个缓存池
	 * @param propertyFileName
	 * @return null表示创建失败
	 */
	public static BaseMemoryCache build(String propertyFileName){
		if(firstCreatedTime==null){
			firstCreatedTime=new Date();
		}
		BaseMemoryCache cache=pools.get(propertyFileName);
		if(cache==null){
			File file=new File(PathKit.getRootClassPath()+"/"+propertyFileName);
			if(file.exists()){
				cache=new BaseMemoryCache(propertyFileName);
				pools.put(propertyFileName, cache);
			}
		}
		return cache;
	}
	public static Map<String,BaseMemoryCache> getCachePools(){
		return pools;
	}
	/**
	 * 获取所有缓存池的根路径(要去重)
	 * @return
	 */
	public static List<String> getAllDataDirs(){
		List<String> dataDirs=new ArrayList<String>();
		for(BaseMemoryCache cache:pools.values()){
			if(cache.isRoot()){
				boolean isContain=false;
				String dir=cache.getCptc().getDataDir();
				for(String v:dataDirs){//去重
					if(v.equals(dir)){
						isContain=true;
						break;
					}
				}
				if(!isContain){
					dataDirs.add(dir);
				}
			}
		}
		return dataDirs;
	}
	/**
	 * 根据SensorId获取其所在的CachePool
	 * @param sensorId
	 * @return 没找到则返回null
	 */
	public static BaseMemoryCache getCachePool(long sensorId){
		for(BaseMemoryCache cache:pools.values()){
			if(cache.getSensorFragments().containsKey(sensorId)){
				return cache;
			}
		}
		return null;
	}
	/**
	 * 获取缓存池里面sensor的数量
	 * @return
	 */
	public static int getSensorCount(){
		int count=0;
    	for(BaseMemoryCache cachePool:pools.values()){
    		count+=cachePool.getSensorFragments().size();
    	}
    	return count;
	}
}
