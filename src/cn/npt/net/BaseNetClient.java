package cn.npt.net;

import org.apache.log4j.Logger;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import cn.npt.net.handler.BaseYHandler;
import cn.npt.net.interfaces.INetClient;
import cn.npt.net.interfaces.ITCP;
/**
 * 客户端基类
 * @author Leonardo
 *
 */
public abstract class BaseNetClient implements INetClient, ITCP, Runnable {

	/**
	 * 远程server地址
	 */
	protected String remoteAddr;
	/**
	 * 远程server端口
	 */
	protected int remotePort;
	/**
	 * 处理逻辑
	 */
	protected BaseYHandler handler;
	/**
	 * 是否加密
	 */
	protected boolean ssl;
	/**
	 * 事件处理线程池
	 */
	protected EventLoopGroup group;
	/**
	 * Handler执行状态
	 */
	protected ChannelFuture future;
	
	/**
	 * 启动器
	 */
	protected Bootstrap bootstrap;
	/**
	 * 客户端名称
	 */
	protected String clientName;
	/**
	 * 客户端状态
	 */
	protected NPTChannelStatus status;
	
	private static Logger log=Logger.getLogger(BaseNetClient.class);
	/**
	 * 
	 * @param remoteAddr 远程server地址
	 * @param remotePort 远程server端口
	 * @param handler  业务逻辑处理代码
	 * @param ssl 是否加密
	 * @param clientName 客户端名称
	 */
	public BaseNetClient(String remoteAddr,int remotePort,BaseYHandler handler,boolean ssl,String clientName) {
		this.remoteAddr=remoteAddr;
		this.remotePort=remotePort;
		this.handler=handler;
		this.ssl=ssl;
		this.status=NPTChannelStatus.CLOSED;
		if(clientName!=null&&!clientName.isEmpty()){
			this.clientName=clientName;
		}
		else{
			this.clientName="BaseNetClient";
		}
		
		this.group=new NioEventLoopGroup(2);
		this.bootstrap=new Bootstrap();
		this.bootstrap.group(group);
	}
	public BaseNetClient() {
		this.status=NPTChannelStatus.CLOSED;
	}
	public abstract void init() throws Exception;
	
	/**
	 * 设置tcp的运行参数
	 * @param option
	 * @param optionValue
	 */
	public <T> void option(ChannelOption<T> option,T optionValue){
		this.bootstrap.option(option, optionValue);
	}
	@Override
	public void run() {
		try {
			log.info(this.clientName+" connecting to "+this.remoteAddr+":"+this.remotePort);
			init();
			//this.future.channel().closeFuture().sync();
		} catch (Exception e) {
			log.error(this.clientName+" connecting to "+this.remoteAddr+":"+this.remotePort+" failed");
			log.error(e.getMessage());
			e.printStackTrace();
		}
//		finally{
//			this.status=NPTChannelStatus.CLOSED;
//			this.group.shutdownGracefully();
//			log.info(this.clientName+"["+this.future.channel().localAddress().toString()+"] shutdownGracefully...");
//		}

	}
	/**
	 * 发送信息
	 * @param msg
	 */
	public void send(Object msg){
		this.future.channel().writeAndFlush(msg);
	}
	/**
	 * 主动关闭连接
	 */
	public void close(){
		this.status=NPTChannelStatus.CLOSED_INITIATIVE;
		this.future.channel().close();
	}
}
