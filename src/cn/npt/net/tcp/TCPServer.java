package cn.npt.net.tcp;

import java.util.ArrayList;
import java.util.List;

import cn.npt.net.BaseReceiveServer;
import cn.npt.net.tcp.test.EchoServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.CharsetUtil;

/**
 * @deprecated
 * @author Leonardo
 * @see SimpleTCPServer
 *
 */
public class TCPServer extends BaseReceiveServer{
	private int port;
	private boolean ssl;
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	private ChannelFuture cf;
	private List<ChannelHandlerAdapter> channelHandlers;
	/**
	 * 
	 * @param port 端口
	 * @param channelHandlers Inbound and Outbound事件，主要是decoder和encoder,最后一个handler是业务逻辑处理代码(必须有)
	 * @param ssl 是否加密，默认false
	 */
	public TCPServer(int port,List<ChannelHandlerAdapter> channelHandlers,boolean ssl){
		this.port=port;
		this.ssl=ssl;
		this.channelHandlers=channelHandlers;
	}
	private void init(final List<ChannelHandlerAdapter> channelHandlers) throws Exception{
		// Configure SSL.
        final SslContext sslCtx;
        if (ssl) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }

        // Configure the server.
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
         .channel(NioServerSocketChannel.class)
         .option(ChannelOption.SO_BACKLOG, 100)
         .handler(new LoggingHandler(LogLevel.INFO))
         .childHandler(new ChannelInitializer<SocketChannel>() {
             @Override
             public void initChannel(SocketChannel ch) throws Exception {
                 ChannelPipeline p = ch.pipeline();
                 if (sslCtx != null) {
                     p.addLast(sslCtx.newHandler(ch.alloc()));
                 }
                 //p.addLast(new LoggingHandler(LogLevel.INFO));
                 for(ChannelHandlerAdapter handler:channelHandlers){
                	p.addLast(handler); 
                 }
             }
         });

        // Start the server.
        cf = b.bind(this.port).sync();

        // Wait until the server socket is closed.
        cf.channel().closeFuture().sync();
  
        
	}
	
	@Override
	public void start() {
		try {
			init(channelHandlers);
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			stop();
		}
	}
	/**
	 * 好像并没有什么用
	 */
	@Override
	public void stop() {
		// Shut down all event loops to terminate all threads.
		bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        System.out.println("closing server on port "+this.port+"...");
	}
	/**
	 * test 
	 * @param port
	 */
	public static void test(int port){
		List<ChannelHandlerAdapter> channelHandlers=new ArrayList<ChannelHandlerAdapter>();
		channelHandlers.add(new StringDecoder(CharsetUtil.UTF_8));
		channelHandlers.add(new StringEncoder(CharsetUtil.UTF_8));
		//channelHandlers.add(new EchoInboundFire(1));
		//channelHandlers.add(new EchoInbound(2));
		//channelHandlers.add(new EchoOutbound(3));
		//channelHandlers.add(new EchoOutbound(4));
		
		channelHandlers.add(new EchoServerHandler());
		TCPServer s=new TCPServer(port, channelHandlers, false);
		new Thread(s).start();
		
		System.out.println("server closing...");
	}
}
