package cn.npt.net;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.util.List;

import org.apache.log4j.Logger;

import cn.npt.net.interfaces.INetClient;
import cn.npt.net.interfaces.IUDP;
/**
 * UDP协议基类
 * @author Leonardo
 *
 */
public class BaseNetUDP implements INetClient, IUDP, Runnable {

	/**
	 * 绑定本地端口
	 */
	protected int localPort;
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
	protected List<ChannelHandlerAdapter> channelHandlers;
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
	 * 
	 */
	protected boolean closed;
	/**
	 * 启动器
	 */
	protected Bootstrap bootstrap;
	/**
	 * 客户端名称
	 */
	protected String clientName;
	
	
	private static Logger log=Logger.getLogger(BaseNetClient.class);
	/**
	 * 
	 * @param remoteAddr 远程server地址
	 * @param remotePort 远程server端口
	 * @param channelHandlers Inbound and Outbound事件，主要是decoder和encoder,最后一个handler是业务逻辑处理代码(必须有)
	 * @param ssl 是否加密
	 * @param clientName 客户端名称
	 */
	public BaseNetUDP(int localPort,String remoteAddr,int remotePort,List<ChannelHandlerAdapter> channelHandlers,boolean ssl,String clientName) {
		this.localPort=localPort;
		this.remoteAddr=remoteAddr;
		this.remotePort=remotePort;
		this.channelHandlers=channelHandlers;
		this.ssl=ssl;
		this.clientName=clientName;
		
		this.group=new NioEventLoopGroup();
		this.bootstrap=new Bootstrap();
		this.bootstrap.group(group);
	}
	
	public void init() throws Exception{
		final SslContext sslCtx;
        if (ssl) {
            sslCtx = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }
        
        this.bootstrap.channel(NioDatagramChannel.class)
        	.handler(new ChannelInitializer<DatagramChannel>() {
                 @Override
                 public void initChannel(DatagramChannel ch) throws Exception {
                     ChannelPipeline p = ch.pipeline();
                     if (sslCtx != null) {
                         p.addLast(sslCtx.newHandler(ch.alloc(), remoteAddr, remotePort));
                     }
                     //p.addLast(new LoggingHandler(LogLevel.INFO));
                     for(ChannelHandlerAdapter handler:channelHandlers){
                    	 p.addLast(handler);
                     }
                 }
             });
       
        this.future=this.bootstrap.bind(localPort).sync();
        this.future.channel().closeFuture().sync();
	}
	/**
	 * 设置udp的运行参数
	 * @param option
	 * @param optionValue
	 */
	public <T> void option(ChannelOption<T> option,T optionValue){
		this.bootstrap.option(option, optionValue);
	}
	@Override
	public void run() {
		try {
			log.info(this.clientName+" bind port:"+this.localPort+" successfully");
			init();	
		} catch (Exception e) {
			log.error(this.clientName+" bind port:"+this.localPort+" failed");
			log.error(e.getMessage());
			e.printStackTrace();
		}
		finally{
			this.group.shutdownGracefully();
			log.error(this.clientName+" shutdownGracefully...");
		}

	}
	/**
	 * 单向发送,无返回值
	 * @param msg 要发送的信息
	 */
	public void send(String msg){
		Channel ch=this.future.channel();
		ch.writeAndFlush(new DatagramPacket(
                Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8),
                new InetSocketAddress(this.remoteAddr, this.remotePort)));
	}
	/**
	 * 单向发送,无返回值
	 * @param msg 要发送的信息
	 * @param remoteAddr 远程IP地址
	 * @param remotePort 远程端口
	 */
	public void send(String msg,String remoteAddr,int remotePort){
		Channel ch=this.future.channel();
		ch.writeAndFlush(new DatagramPacket(
				Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8),
				new InetSocketAddress(remoteAddr, remotePort)));
	}
}
