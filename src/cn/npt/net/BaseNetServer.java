package cn.npt.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.log4j.Logger;

import cn.npt.net.handler.BaseYHandler;
import cn.npt.net.interfaces.INetServer;
/**
 * 服务端基类
 * @author Leonardo
 *
 */
public abstract class BaseNetServer implements INetServer ,Runnable{

	/**
	 * server端口号
	 */
	protected int port;
	/**
	 * 是否加密
	 */
	protected boolean ssl;
	/**
	 * selector线程池
	 */
	protected EventLoopGroup bossGroup;
	/**
	 * 事件处理线程池
	 */
	protected EventLoopGroup workerGroup;
	/**
	 * 工作线程数量
	 */
	protected int workerThreadCount;
	/**
	 * 启动器
	 */
	protected ServerBootstrap serverBootstrap;
	/**
	 * Handler执行状态
	 */
	protected ChannelFuture cf;
	/**
	 * 处理逻辑
	 */
	protected BaseYHandler handler;
	/**
	 * 服务器名称
	 */
	protected String serverName;
	
	
	private static Logger log=Logger.getLogger(BaseNetServer.class);
	
	/**
	 * 
	 * @param port 端口
	 * @param handler Inbound and Outbound事件，业务逻辑处理代码
	 * @param ssl 是否加密，默认false
	 * @param serverName 若null则默认localBaseNetServer
	 */
	public BaseNetServer(int port,BaseYHandler handler,boolean ssl,String serverName) {
		this.port=port;
		this.handler=handler;
		this.ssl=ssl;
		this.serverName=serverName;
		if(this.serverName==null){
			this.serverName="localBaseNetServer";
		}
		// Configure the server.
        this.bossGroup = new NioEventLoopGroup(1);
        this.workerGroup = new NioEventLoopGroup();
        this.workerThreadCount=Runtime.getRuntime().availableProcessors();
        this.serverBootstrap=new ServerBootstrap();
	}
	/**
	 * 
	 * @param port 端口
	 * @param handler Inbound and Outbound事件，业务逻辑处理代码
	 * @param workerThreadCount 工作线程数
	 * @param ssl 是否加密，默认false
	 * @param serverName 若null则默认localBaseNetServer
	 */
	public BaseNetServer(int port,BaseYHandler handler,int workerThreadCount,boolean ssl,String serverName) {
		this.port=port;
		this.handler=handler;
		this.ssl=ssl;
		this.serverName=serverName;
		if(this.serverName==null){
			this.serverName="localBaseNetServer";
		}
		// Configure the server.
		this.bossGroup = new NioEventLoopGroup(1);
		this.workerThreadCount=workerThreadCount;
		this.workerGroup = new NioEventLoopGroup(this.workerThreadCount);
		this.serverBootstrap=new ServerBootstrap();
	}
	public BaseNetServer() {
		
	}
	/**
	 * 初始化所有的运行参数
	 * @throws Exception
	 */
	public abstract void init() throws Exception;
	public void run(){
		try {
			log.info(this.serverName+" init at port "+this.port);
			init();	
		} catch (Exception e) {
			log.error(this.serverName+" init failed at port "+this.port);
			log.error(e.getMessage());
			e.printStackTrace();
		}
		finally{
			this.bossGroup.shutdownGracefully();
			this.workerGroup.shutdownGracefully();
			log.error(this.serverName+" shutdownGracefully...");
		}
	}
	/**
	 * 设置tcp的运行参数
	 * @param option
	 * @param optionValue
	 */
	public <T> void option(ChannelOption<T> option,T optionValue){
		this.serverBootstrap.option(option, optionValue);
	}
	/**
	 * 设置log级别,子类可选择覆盖
	 */
	protected void handler(){
		this.serverBootstrap.handler(new LoggingHandler(LogLevel.INFO));
	}
	public EventLoopGroup getBossGroup() {
		return bossGroup;
	}
	public void setBossGroup(EventLoopGroup bossGroup) {
		this.bossGroup = bossGroup;
	}
	public ChannelFuture getCf() {
		return cf;
	}
	public void setCf(ChannelFuture cf) {
		this.cf = cf;
	}
	public void close(){
		this.cf.channel().close();
	}
}
