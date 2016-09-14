package cn.npt.fs.thread;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 默认最多开启32个线程
 * @author leonardo_yang
 *
 */
public class BaseThreadPool {

	private static BaseThreadPool instance=null;
	private ExecutorService bladedBatch;
	private int maxRunSize;
	
	private BaseThreadPool(int maxSize){
		this.maxRunSize=maxSize;
		this.bladedBatch=Executors.newFixedThreadPool(this.maxRunSize); 
	}
	public static BaseThreadPool getInstance(){
		if(null==instance){
			instance=new BaseThreadPool(32);//默认最多启动32个线程
		}
		return instance;
	}
	public int getMaxRunSize() {
		return maxRunSize;
	}
	public synchronized void setMaxRunSize(int maxRunSize) {
		this.maxRunSize = maxRunSize;
	}
	public synchronized void addTask(Runnable task){
		this.bladedBatch.execute(task);
	}
	public synchronized void addTask(List<Runnable> taskList){
		while(taskList.size()>0){
			this.bladedBatch.execute(taskList.remove(0));
		}	
	}
	public synchronized void stop(){
		this.bladedBatch.shutdown();
	}
	/**
	 * 重新更改线程池的大小
	 * @param maxSize
	 */
	public synchronized void resume(int maxSize){
		this.maxRunSize=maxSize;
		if(this.bladedBatch.isTerminated()){
			this.bladedBatch=Executors.newFixedThreadPool(maxSize);
		}
		else{
			List<Runnable> tasks=this.bladedBatch.shutdownNow();
			this.bladedBatch=Executors.newFixedThreadPool(maxSize);
			addTask(tasks);
		}
	}
}
