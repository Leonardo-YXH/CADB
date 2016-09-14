package cn.npt.fs.thread;

import java.util.List;

import cn.npt.fs.cache.CachePool;
import cn.npt.fs.event.SensorHandler;
/**
 * 
 * @author Leonardo
 *
 * @param <T>
 */
public class HandlerProcess<T extends CachePool<?>> implements Runnable {
	private int index;
	private long currentTime;
	private List<SensorHandler> handlers;
	private T fragment;
	public HandlerProcess(List<SensorHandler> handlers,T t,int index,long currentTime) {
		this.handlers=handlers;
		this.fragment=t;
		this.index=index;
		this.currentTime=currentTime;
	}
	@Override
	public void run() {
		for(SensorHandler handler:this.handlers){
			handler.execute(fragment,this.index,currentTime);
		}
	}

}
