package cn.npt.fs.config;

import java.util.concurrent.TimeUnit;

/**
 * 
 * @author Leonardo
 */
public class CacheBlockCfg {

	/**
	 * 缓存池的大小
	 */
	public int size;
	
	/**
	 * 单位块的时长，单位毫秒
	 */
	public long blockInterval;
	/**
	 * 持久化的频率（原始数据需要，bs不用）
	 */
	public int persistenceSize;
	
	/**
	 * 持久化文件粒度所在单位制内的大小
	 */
	public int capacity;
	/**
	 * 存文件的粒度
	 */
	public TimeUnit timeUnit;
	/**
	 *
	 * @param blockInterval
	 * @param size
	 * @param persistenceSize -1表示不使用
	 */
	public CacheBlockCfg(long blockInterval,int size,int persistenceSize){
		this.blockInterval=blockInterval;
		this.size=size;
		this.persistenceSize=persistenceSize;
		setUnits(persistenceSize*blockInterval);
		setCapacity();
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public long getBlockInterval() {
		return blockInterval;
	}

	public void setBlockInterval(long blockInterval) {
		this.blockInterval = blockInterval;
	}

	public int getPersistenceSize() {
		return persistenceSize;
	}

	public void setPersistenceSize(int persistenceSize) {
		this.persistenceSize = persistenceSize;
	}
	/**
	 * persistenceSize*blockInterval/60000;文件大小（minute的整数倍）
	 * <br>!!!只有原始数据可以使用该方法
	 * @return
	 */
	public int getCapacity(){
		return this.capacity;
	}
	private void setCapacity(){
		long countInMS=this.persistenceSize*this.blockInterval;
		this.capacity=(int)(countInMS/getUnits(countInMS));
	}
	/**
	 * 理论上支持1ms--1day的粒度，但是ms级的cpu处理能力跟不上
	 * @param countInMS 毫秒级单位数
	 * @return 默认分钟级
	 */
	private long getUnits(long countInMS){
		if(countInMS>0&&countInMS<1000){//毫秒级
			return 1;
		}
		else if(countInMS<60000){//秒级
			return 1000;
		}
		else if(countInMS<3600000){//分钟级
			return 60000;
		}
		else if(countInMS<86400000){//小时级
			return 3600000;
		}
		else if(countInMS<864000000){//天级
			return 86400000;
		}
		return 60000;
	}
	/**
	 * 理论上支持1ms--1day的粒度，但是ms级的cpu处理能力跟不上
	 * @param countInMS 毫秒级单位数
	 * @return 默认分钟级
	 */
	private void setUnits(long countInMS){
		if(countInMS>0&&countInMS<1000){//毫秒级
			this.timeUnit=TimeUnit.MICROSECONDS;
		}
		else if(countInMS<60000){//秒级
			this.timeUnit=TimeUnit.SECONDS;
		}
		else if(countInMS<3600000){//分钟级
			this.timeUnit=TimeUnit.MINUTES;
		}
		else if(countInMS<86400000){//小时级
			this.timeUnit=TimeUnit.HOURS;
		}
		else if(countInMS<864000000){//天级
			this.timeUnit=TimeUnit.DAYS;
		}
	}
}
