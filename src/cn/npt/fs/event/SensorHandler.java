package cn.npt.fs.event;

import cn.npt.fs.cache.CachePool;

public abstract class SensorHandler{
	public abstract<T extends CachePool<?>> void execute(T fragment,int index,long currentTime);
}
