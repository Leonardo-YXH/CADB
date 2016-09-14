package cn.npt.net.websocket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import cn.npt.net.BaseNetServer;
import cn.npt.net.handler.BaseYHandler;
import cn.npt.net.handler.test.EchoWebSocketServerHandler;
/**
 * WebSocketServer简单样例
 * @author Leonardo
 *
 */
public class SimpleWebSocketServer extends BaseNetServer {

	/**
	 * 
	 * @param port 绑定本地端口
	 * @param wsHandler 具体的业务逻辑
	 * @param ssl 是否加密
	 * @param serverName 默认名SimpleWebSocketServer
	 */
	public SimpleWebSocketServer(int port,
			BaseYHandler wsHandler, boolean ssl,
			String serverName) {
		super(port, wsHandler, ssl, serverName);
		if(this.serverName==null){
			this.serverName="SimpleWebSocketServer";
		}
	}
	/**
	 * 
	 * @param port 绑定本地端口
	 * @param wsHandler 具体的业务逻辑
	 * @param workerThreadCount 工作线程数
	 * @param ssl 是否加密
	 * @param serverName 默认名SimpleWebSocketServer
	 */
	public SimpleWebSocketServer(int port,
			BaseYHandler wsHandler, int workerThreadCount, boolean ssl,
			String serverName) {
		super(port, wsHandler, workerThreadCount, ssl, serverName);
		if(this.serverName==null){
			this.serverName="SimpleWebSocketServer";
		}
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
                p.addLast(new HttpServerCodec());
                p.addLast(new HttpObjectAggregator(65536));
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
		EchoWebSocketServerHandler handler=new EchoWebSocketServerHandler("ws", false);
		SimpleWebSocketServer server=new SimpleWebSocketServer(8800, handler, false, "serverName");
		new Thread(server).start();
	}
}
