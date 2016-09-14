package cn.npt.net.tcp;

import java.util.ArrayList;
import java.util.List;

import cn.npt.net.BaseReceiveServer;
import cn.npt.net.tcp.test.EchoOutbound;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.util.CharsetUtil;
/**
 * @deprecated
 * @author Leonardo
 * @see SimpleTCPClient 
 *
 */
public class TCPClient extends BaseReceiveServer{

	private String host;
	private int port;
	private boolean ssl;
	private EventLoopGroup group;
	private ChannelFuture fclient;
	private boolean closed;
	private Bootstrap bootstrap;
	
	public TCPClient(String host,int port,boolean ssl,List<ChannelHandlerAdapter> channelHandlers){
		this.host=host;
		this.port=port;
		this.ssl=ssl;
		try {
			init(channelHandlers);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void init(final List<ChannelHandlerAdapter> channelHandlers) throws Exception {
		this.closed=false;
        // Configure SSL.git
        final SslContext sslCtx;
        if (ssl) {
            sslCtx = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }

        // Configure the client.
        group = new NioEventLoopGroup();
        
            bootstrap = new Bootstrap();
            bootstrap.group(group)
             .channel(NioSocketChannel.class)
             .option(ChannelOption.TCP_NODELAY, true)
             .handler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     ChannelPipeline p = ch.pipeline();
                     if (sslCtx != null) {
                         p.addLast(sslCtx.newHandler(ch.alloc(), host, port));
                     }
                     //p.addLast(new LoggingHandler(LogLevel.INFO));
                     for(ChannelHandlerAdapter handler:channelHandlers){
                    	 p.addLast(handler);
                     }
                    
                 }
             });

            // Start the client.
            //new Thread(new doConnect()).start();
    }
	public void start(){
		if (closed) {
            return;
        }

        fclient = bootstrap.connect(host,port);
        try {
			fclient.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        finally{
			stop();
		}
	}
	public void stop(){
		this.close();
	}

	/**
	 * 不用显式调用，在stop里面处理
	 */
	public void close(){
		this.closed=true;
		group.shutdownGracefully();
        System.out.println("disconnect to "+this.host+" on port "+this.port+" and closing client...");
	}
	/**
	 * rfid模拟数据
	 * @param ip
	 * @param port
	 */
	public static void unitTest(String ip,int port){
		List<ChannelHandlerAdapter> chs=new ArrayList<ChannelHandlerAdapter>();
		chs.add(new StringDecoder(CharsetUtil.UTF_8));
		chs.add(new StringEncoder(CharsetUtil.UTF_8));
		
		//chs.add(new EchoInbound(1));
		//chs.add(new EchoInbound(2));
		//chs.add(new EchoOutbound(3));
		
		//chs.add(new EchoInboundFire(2));
		
		//chs.add(new EchoOutbound(4));
		chs.add(new EchoOutbound(1));
		TCPClient client=new TCPClient(ip, port, false, chs);
		new Thread(client).start();
		//System.out.println("exit...");
		//new TCPClient("192.168.20.126", 8007, false, chs);
		//new TCPClient("127.0.0.1", 9000, false, chs);
	}
	public static void main(String[] args) {
		unitTest("127.0.0.1", 9000);
	}
}
