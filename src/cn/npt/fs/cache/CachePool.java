package cn.npt.fs.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import com.alibaba.fastjson.JSONObject;

import cn.npt.fs.event.SensorHandler;
import cn.npt.fs.thread.BaseThreadPool;
import cn.npt.fs.thread.HandlerProcess;
/**
 * 
 * @author Leonardo
 *
 * @param <T>
 */
public abstract class CachePool<T> {
	/**
	 * 时区偏移量。eg.beijin东8区=8*3600*1000=28800000ms
	 */
	private static long timeZoneOffset=TimeZone.getDefault().getRawOffset();
	
	/**
	 * 详细数据
	 */
	protected List<T> values;
	/**
	 * 当前时刻，其实是最后填充数据的时刻
	 */
	protected long currentTime;
	/**
	 * 上一个填充数据的索引
	 */
	protected int index;
	/**
	 * 数据块的数量
	 */
	protected int size;
	/**
	 * 每块所占时间，以毫秒为单位
	 */
	protected long blockInterval;
	/**
	 * 要执行的事件队列
	 */
	protected List<SensorHandler> handlers; 
	
	//private static Logger log=Logger.getLogger(CachePool.class);
	/**
	 * 
	 * @param size
	 * @param blockInterval
	 */
	public CachePool(int size,long blockInterval){
		this.index=0;
		this.currentTime=0;
		this.size=size;
		this.blockInterval=blockInterval;
		this.values=new ArrayList<T>();
		T t = null;
		for(int i=0;i<size;i++){
			values.add(t);
		}
		this.handlers=new ArrayList<SensorHandler>();
	}
	/**
	 * synchronized是否同步锁资源
	 * @param value
	 * @param time
	 */
	public void setValue(T value,long time){
		int gIndex=getCurrentIndex(time);
		if(-2==gIndex){//1.系统启动;2.重置数据.
			//log.info("缓存系统启动,开始缓存数据...");
			this.currentTime=time;
			this.index=parseIndex(currentTime);
			afterReStart();
			//log.info("缓存预处理结束,开始实时处理");
			this.currentTime-=this.blockInterval;
			this.index--;
			receiveValue(value);
			onHandler();
		}
		else if(-1==gIndex){//time<currentTime
			;
		}
		else{//可控范围
			if(gIndex>this.index){
				while(this.index<gIndex){
					receiveValue(value);//都使用同一个数据填充
					onHandler();
				}
			}
			else if(gIndex<this.index){//
				int i=this.index;
				while(i<this.size){
					receiveValue(value);
					onHandler();
					i++;
				}
				i=0;
				while(i<gIndex){
					receiveValue(value);
					onHandler();
					i++;
				}
			}
			else if(this.index==gIndex){//单纯地覆盖之前的值
				values.set(index, value);
			}
		}
	}
	/**
	 * 添加一条记录:当前时间+1s;轮询所有可能触发的事件;索引后移一位
	 * @param value
	 */
	protected void receiveValue(T value){
		this.currentTime+=this.blockInterval;
		index++;
		if(index>=size){//索引循环后移
			index-=size;
		}
		values.set(index, value);
	}
	public void onHandler(){
		//用线程池执行
		BaseThreadPool.getInstance().addTask(new HandlerProcess<CachePool<?>>(handlers, this, this.index,this.currentTime));
	}
	/**
	 * 获取当前时刻数据索引
	 * 
	 * @param time time>currentTime大于上一时刻
	 * @return -2:reset;-1:abort or ignore;other:destIndex
	 */
	private int getCurrentIndex(long time){
		int offset=(int)((time-currentTime)/this.blockInterval);
		if(offset>size){//重置
			this.currentTime=0;
			return -2;
			/*Date date=new Date(time);
			int minute=date.getMinutes();
			if(date.getSeconds()==0&&minute%(size/120)==0){//10分钟的整数倍
				this.currentTime=0;
				return -2;
			}
			else{
				return -3;
			}*/
		}
		else if(offset<0){
			return -1;
		}
		else{
			offset+=index;
			if(offset>=size){
				return offset-size;
			}
			else{
				return offset;
			}
		}
	}
	
	/**
	 * 获取time时刻的SensorValue，NaN表示不在缓存数据范围内
	 * @param time
	 * @return
	 */
	public T getValue(long time){
		int dest=getDestIndex(time);
		if(dest!=-1){
			return values.get(dest);
		}
		return null;
	}
	/**
	 * 获取当前索引的数据
	 * @return
	 */
	public T getCurrentValue(){
		return values.get(index);
	}
	/**
	 * 获取time之前的length个数据(假定数据已经走了一圈)
	 * @param time
	 * @param length
	 * @return 返回结果以当前时间为第一个(time-->from,按时间倒排)
	 */
	public List<T> getSensorValuesBefore(long time,int length){
		if(length>size){
			length=size;
		}
		List<T> rs=new ArrayList<T>();
		int gIndex=getDestIndex(time);
		for(int i=0;i<length;i++){
			if(gIndex<0){
				gIndex+=size;
			}
			rs.add(values.get(gIndex));
			gIndex--;
		}
		return rs;
	}
	/**
	 * 获取time之后的length个数据(假定数据已经走了一圈)
	 * @param time
	 * @param length
	 * @return 返回结果以当前时间为第一个(time-->from)
	 */
	public List<T> getSensorValuesAfter(long time,int length){
		if(length>size){
			length=size;
		}
		List<T> rs=new ArrayList<T>();
		int gIndex=getDestIndex(time);
		if(gIndex<0){
			return rs;
		}
		for(int i=0;i<length;i++){
			if(gIndex>=size){
				gIndex-=size;
			}
			rs.add(values.get(gIndex));
			gIndex++;
		}
		return rs;
	}
	/**
	 * 获取time之后到当前时刻的数据(假定数据已经走了一圈)
	 * @param time
	 * @return 返回结果以当前时间为第一个(time-->currentTime)
	 */
	public List<T> getSensorValuesAfter(long time){
		List<T> rs=new ArrayList<T>();
		int gIndex=getDestIndex(time);
		if(gIndex<0){
			return rs;
		}
		if(gIndex>index){
			while(gIndex<size){
				rs.add(values.get(gIndex));
				gIndex++;
			}
			gIndex=0;
		}
		while(gIndex<=index){
			rs.add(values.get(gIndex));
			gIndex++;
		}
		return rs;
	}
	/**
	 * 获取历史数据索引
	 * @param time time<=currentTime
	 * @return -1表示超出范围
	 */
	private int getDestIndex(long time){
		if(time<=currentTime){
			int offset=(int)((currentTime-time)/this.blockInterval);
			if(offset<size){
				offset=index-offset;
				if(offset>=0){
					return offset;
				}
				else{
					return offset+size;
				}
			}
		}
		return -1;
	}
	/**
	 * 添加事件监听
	 * @param handler
	 */
	public void addListener(SensorHandler handler){
		this.handlers.add(handler);
	}
	/**
	 * 解析time时刻所在缓存空间的索引
	 * <br>原本的0表示1970-01-01 08:00:00，为了将起始时间改为1970-01-01 00:00:00，time+=8*3600*1000ms
	 * @param time
	 * @return
	 */
	protected int parseIndex(long time){
		time+=timeZoneOffset;
		long rs=time/this.blockInterval;
		return (int) (rs%size);
	}
	
	public List<T> getValues() {
		return values;
	}
	public void setValues(List<T> values) {
		this.values = values;
	}
	public long getCurrentTime() {
		return currentTime;
	}
	public void setCurrentTime(long currentTime) {
		this.currentTime = currentTime;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
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
	/**
	 * 系统启动或重置时需要做的工作
	 */
	public abstract void afterReStart();
	public abstract void setCase2(Object paras);
	public abstract void setCase3(Object paras);
	public abstract void setCase4(Object paras);
	/**
	 * 将当前值转成json
	 * @return
	 */
	public abstract JSONObject currentV2JSON();
	
}
