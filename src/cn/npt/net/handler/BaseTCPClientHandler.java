package cn.npt.net.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import cn.npt.fs.CachePoolFactory;
import cn.npt.fs.cache.BaseMemoryCache;
import io.netty.channel.ChannelHandlerContext;

/**
 * 处理TCP client包的数据
 * @author Leonardo
 *
 */
public abstract class BaseTCPClientHandler extends BaseYHandler {

	/**
	 * 对应的缓存池
	 */
	protected BaseMemoryCache cachePool;
	/**
	 * 按照传输顺序排列的sensorId集合
	 */
	protected List<Long> sensorIds;
	/**
	 * 采集传感器数据的时刻
	 */
	protected long sensorTime;
	/**
	 * 采集的数据
	 */
	protected List<Double> values;
	
	
	protected static Logger log=Logger.getLogger(BaseTCPClientHandler.class);
	
	public BaseTCPClientHandler(String cachePropertyName,List<Long> sensorIds){
		this.cachePool=CachePoolFactory.build(cachePropertyName);
		this.sensorIds=sensorIds;
	}
	public BaseTCPClientHandler(BaseMemoryCache cachePool,List<Long> sensorIds){
		this.cachePool=cachePool;
		this.sensorIds=sensorIds;
	}
	/**
	 * 过滤数据
	 * @param src
	 * @return
	 */
	public abstract <T> List<T> filter(List<T> src);
	/**
	 * 转换数据，将接收到的数据解析到sensorTime和values
	 * @return false表示转换失败
	 */
	public abstract boolean convert(Object msg);

	/**
	 * 将数据缓存起来
	 */
	public void doCache(){
		if(this.sensorIds.size()>this.values.size()){
			log.error("传输的数据量少于需要采集的数据量");
		}
		else if(this.sensorIds.size()<this.values.size()){
			log.error("传输的数据量多于需要采集的数据量");
		}
		else{
			Map<Long,Double> sensorValues=new HashMap<Long, Double>();
			for(int i=0;i<this.sensorIds.size();i++){
				sensorValues.put(this.sensorIds.get(i), this.values.get(i));
			}
			this.cachePool.execute(sensorTime, sensorValues);
		}
	}
	
	@Override
	public void channelRead(
			ChannelHandlerContext ctx,
			Object msg) {
		
		if(convert(msg)){
			this.values=filter(this.values);
			doCache();
		}
		else{
			log.error("未知格式的数据,来自["+ctx.channel().remoteAddress().toString()+"]");
		}
		
	}
	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        log.error("与远程主机["+ctx.channel().remoteAddress().toString()+"]连接异常，连接已断开");
        ctx.close();
    }
	/**
	 * 添加一个需要监控存储的传感器ID
	 * @param sensorId
	 * @param index
	 */
	public void addSensor(long sensorId,int index){
		this.sensorIds.add(index, sensorId);
	}
	/**
	 * 添加一个需要监控存储的传感器ID
	 * @param sensorId
	 */
	public void addSensor(long sensorId){
		this.sensorIds.add(sensorId);
	}
	/**
	 * 移除一个不需要监控存储的传感器ID
	 * @param sensorId
	 * @return
	 */
	public boolean removeSensor(long sensorId){
		return this.sensorIds.remove(sensorId);
	}
}
