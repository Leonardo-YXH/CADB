package cn.npt.net;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.npt.fs.sensor.IReceiveData;
import cn.npt.fs.sensor.ISendData;

public abstract class BaseNetWork implements IReceiveData{
	/**
	 * 协议
	 */
	protected String protocol;
	protected List<Long> sensorIds;
	/**
	 * 数据后处理
	 */
	protected List<ISendData> sendHandlers;
	/**
	 * 上下文环境
	 */
	protected Object context;
	//protected List<>
	
	public BaseNetWork(String protocol,List<Long> sensorIds){
		this.protocol=protocol;
		this.sensorIds=sensorIds;
		this.sendHandlers=new ArrayList<ISendData>();
	}
	/**
	 * 解析接收的数据,格式转成Map &lt;Long,Double&gt; sensorValues
	 */
	public abstract void parseData();
	@Override
	public Map<Long, Double> read(){
		
		
		return null;
		
	}
	/**
	 * 数据来源,在每次接收数据后循环sendhandler
	 */
	public abstract void receive();
	public void addListener(ISendData sendHandler){
		this.sendHandlers.add(sendHandler);
	}
}
