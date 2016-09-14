package cn.npt.net.handler;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import cn.npt.fs.CachePoolFactory;
import cn.npt.fs.cache.BaseMemoryCache;
import io.netty.channel.ChannelHandlerContext;
/**
 * 处理TCP Server包的数据
 * @author Leonardo
 *
 */
public abstract class BaseTCPServerHandler extends BaseYHandler {
	
	/**
	 * 对应的缓存池
	 */
	protected BaseMemoryCache cachePool;
	
	/**
	 * 采集传感器数据的时刻
	 */
	protected long sensorTime;
	/**
	 * 采集的sensor数据
	 */
	protected Map<Long,Double> sensorValues;
	/**
	 * 采集的间隔，单位ms
	 */
	protected long interval;
	/**
	 * 是否是第一次接收数据并初始化相关信息
	 */
	protected boolean isFirstInit;
	/**
	 * 是否需要过滤掉。eg：每3s加入一次，而client每1s发送一次数据，则需要过滤掉中间的两次
	 */
	protected boolean isFilter;
	/**
	 * 是否包含正确的sensorId的数据，检测所有的sensorId是否在同一个缓存池里面.
	 * <br>0--正确;1--包含不存在的sensorId;2--所接收的sensorIds不在同一个缓存池里面
	 */
	protected int formatFlag;
	protected static Logger log=Logger.getLogger(BaseTCPServerHandler.class);
	
	public BaseTCPServerHandler() {
		this.isFirstInit=true;
		this.isFilter=false;
		this.formatFlag=0;
	}
	/**
	 * 判断是否第一次接收数据并检测所有的sensorId是否在同一个缓存池里面
	 */
	public boolean initPool(){
		if(this.isFirstInit){
			this.cachePool=null;
			Iterator<Entry<Long, Double>> it=this.sensorValues.entrySet().iterator();
			boolean flag=true;
			while(it.hasNext()){
				Entry<Long, Double> entry=it.next();
				long sensorId=entry.getKey();
				if(flag){
					this.cachePool=CachePoolFactory.getCachePool(sensorId);
					if(this.cachePool!=null){
						this.isFirstInit=false;
					}
					else{
						this.formatFlag=1;
						return false;
					}
					flag=false;
				}
				else{
					BaseMemoryCache cp=CachePoolFactory.getCachePool(sensorId);
					if(cp==null){
						this.formatFlag=1;
						log.error("服务器端缓存池内不存在sensorId:"+sensorId);
						return false;
					}
					else{
						if(!this.cachePool.equals(cp)){
							this.formatFlag=2;
							return false;
						}
					}
					
				}
			}
		}
		return true;
	}
	
	/**
	 * 转换数据，将接收到的数据解析到sensorValues
	 * @return false表示转换失败
	 */
	public abstract boolean convert(Object msg);

	/**
	 * 将数据缓存起来
	 */
	public void doCache(){
		this.cachePool.execute(sensorTime, sensorValues);
	}
	@Override
	public void channelActive(ChannelHandlerContext ctx)
			throws Exception {
		log.info("来自["+ctx.channel().remoteAddress().toString()+"]的成功连接,等待传感器数据采集");
	}
	@Override
	public void channelRead(ChannelHandlerContext ctx,Object msg) {
		if(convert(msg)){
			if(initPool()){
				if(!this.isFilter){
					doCache();
				}
				else{
					this.isFilter=false;
				}
			}
			else{
				if(this.formatFlag==1){
					log.error("包含不存在的sensorId,数据来自["+ctx.channel().remoteAddress().toString()+"],请检查你的sensorId是否与服务器对应");
				}
				else if(this.formatFlag==2){
					log.error("所接收的sensorIds不在同一个缓存池里面,数据来自["+ctx.channel().remoteAddress().toString()+"],请检查你的sensorId是否与服务器对应");
				}
				ctx.close();
			}
		}
		else{
			log.error("未知格式的数据,来自["+ctx.channel().remoteAddress().toString()+"]");
			ctx.close();
		}
	}
	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        log.error("与远程主机["+ctx.channel().remoteAddress().toString()+"]连接异常，连接已断开");
        ctx.close();
    }

}
