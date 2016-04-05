package cn.npt.net;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 接收数据的server
 * @author Administrator
 *
 */
public abstract class BaseServer {
	private final static AtomicInteger serverId=new AtomicInteger(1);
	protected int id;
	public BaseServer(){
		this.id=serverId.getAndIncrement();
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * 启动server
	 */
	public abstract void start();
	/**
	 * 停止server
	 */
	public abstract void stop();
}
