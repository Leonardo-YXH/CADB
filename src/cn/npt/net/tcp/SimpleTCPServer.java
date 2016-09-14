package cn.npt.net.tcp;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.CharsetUtil;
import cn.npt.net.BaseNetServer;
import cn.npt.net.handler.BaseYHandler;
import cn.npt.net.handler.SimpleTCPServerHandler;
/**
 * tcp服务器
 * @author Leonardo
 *
 */
public class SimpleTCPServer extends BaseNetServer {
	
	/**
	 * 
	 * @param port 端口
	 * @param handler 业务逻辑处理代码
	 * @param ssl 是否加密，默认false
	 * @param serverName 若null则默认localBaseNetServer
	 */
	public SimpleTCPServer(int port,
			BaseYHandler handler, boolean ssl,
			String serverName) {
		super(port, handler, ssl, serverName);
		this.option(ChannelOption.SO_BACKLOG, 64);//设置默认最大socket连接数64
	}
	/**
	 * 
	 * @param port 端口
	 * @param handler 业务逻辑处理代码
	 * @param workerThreadCount 工作线程数，默认为cpu核数
	 * @param ssl 是否加密，默认false
	 * @param serverName 若null则默认localBaseNetServer
	 */
	public SimpleTCPServer(int port,
			BaseYHandler handler, int workerThreadCount, boolean ssl,
			String serverName) {
		super(port, handler, workerThreadCount, ssl, serverName);
		this.option(ChannelOption.SO_BACKLOG, 64);//设置默认最大socket连接数64
	}

	public void init() throws Exception{
		// Configure SSL.
        final SslContext sslCtx;
        if (ssl) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }
       
        
        this.serverBootstrap.group(bossGroup, workerGroup)
        	.channel(NioServerSocketChannel.class);
        //this.option();
        this.handler();
        this.serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline p = ch.pipeline();
                if (sslCtx != null) {
                    p.addLast(sslCtx.newHandler(ch.alloc()));
                }
                //p.addLast(new LoggingHandler(LogLevel.INFO));
                p.addLast(new StringDecoder(CharsetUtil.UTF_8));
                p.addLast(new StringEncoder(CharsetUtil.UTF_8));
            	if(handler.isSharable()){
            		p.addLast(handler); 
            	}
            	else{
            		p.addLast(handler.deepClone());
            	}
                
            }
        });
     // Start the server.
        cf = this.serverBootstrap.bind(this.port).sync();

        // Wait until the server socket is closed.
        cf.channel().closeFuture().sync();
	}
	public static void main(String[] args) {
		//EchoInbound handler=new EchoInbound(1);
		SimpleTCPServerHandler handler=new SimpleTCPServerHandler();
		SimpleTCPServer server=new SimpleTCPServer(8008, handler, false, "localserver");
		new Thread(server).start();
		try {
			Thread.sleep(60000);//一分钟后关闭
			server.close();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
}
