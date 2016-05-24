package cn.npt.net.websocket;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import cn.npt.net.BaseNetClient;
import cn.npt.net.NPTChannelStatus;
import cn.npt.net.handler.BaseYHandler;
import cn.npt.net.handler.test.EchoWebSocketClientHandler;

public class SimpleWebSocketClient extends BaseNetClient {

	private Logger log=Logger.getLogger(SimpleWebSocketClient.class);
	public SimpleWebSocketClient(String remoteAddr, int remotePort,
			BaseYHandler wsChannelHandler, boolean ssl,
			String clientName) {
		super(remoteAddr, remotePort, wsChannelHandler, ssl, clientName);
		if(clientName!=null&&!clientName.isEmpty()){
			this.clientName=clientName;
		}
		else{
			this.clientName="SimpleWebSocketClient";
		}
	}

	public void init() throws Exception{
		final SslContext sslCtx;
        if (ssl) {
            sslCtx = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }
        
        this.bootstrap.channel(NioSocketChannel.class)
        	.handler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     ChannelPipeline p = ch.pipeline();
                     if (sslCtx != null) {
                         p.addLast(sslCtx.newHandler(ch.alloc(), remoteAddr, remotePort));
                     }
                     //p.addLast(new LoggingHandler(LogLevel.INFO));
                     p.addFirst(new ChannelInboundHandlerAdapter(){
                    	 @Override
                         public void channelInactive(ChannelHandlerContext ctx) throws Exception {//断开的时候检测重连
                             super.channelInactive(ctx);
                             if(!status.equals(NPTChannelStatus.CLOSED_INITIATIVE)){//如果不是主动关闭则需重连
                            	 ctx.channel().eventLoop().schedule(new reConnect(), 5, TimeUnit.SECONDS);//5秒检测重连
                             }
                    	 }
                     });
                     p.addLast(new HttpClientCodec());
                     p.addLast(new HttpObjectAggregator(8192));
                 	if(handler.isSharable()){
                 		p.addLast(handler); 
                 	}
                 	else{
                 		p.addLast(handler.deepClone());
                 	}
                 }
             });
//        this.future=this.bootstrap.connect(remoteAddr, remotePort);
//        this.future.channel().closeFuture().sync();
        new Thread(new reConnect()).start();
	}
	private class reConnect implements Runnable{

		@Override
		public void run() {
			future=bootstrap.connect(remoteAddr, remotePort);
			future.addListener(new ChannelFutureListener() {
	            public void operationComplete(ChannelFuture f) throws Exception {
	                if (f.isSuccess()) {
	                	future=f;
	                }
	                else{
	                	log.info("正在重连到server["+remoteAddr+":"+remotePort+"]");
	                	f.channel().eventLoop().schedule(new reConnect(), 5, TimeUnit.SECONDS);
	                }
	            }
	        });
		}
		
	}
	/**
	 * ping连通性
	 */
	public void ping(){
		this.send(new PingWebSocketFrame(Unpooled.wrappedBuffer(new byte[] { 8, 1, 8, 1 })));
	}
	/**
	 * 关闭websocket连接
	 */
	public void close(){
		this.send(new CloseWebSocketFrame());
	}
	public static void main(String[] args) {
		String addr="192.168.20.84";
		int port=8181;
		
		SimpleWebSocketClient client1=test(addr, port, "F://datas/robotsimData/robot1.json");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		client1.send(new TextWebSocketFrame("{'flag':'nptrobot','timeInterval':1000}"));
//		SimpleWebSocketClient client2=test("192.168.20.72", port+1,"F://datas/robotsimData/robot2.json");
//		SimpleWebSocketClient client3=test("192.168.20.73", port+2,"F://datas/robotsimData/robot3.json");
		
		/* BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
         while (true) {
             String msg=null;
			try {
				msg = console.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
            if (msg==null||"b".equals(msg.toLowerCase())) {
                 //ch.writeAndFlush(new CloseWebSocketFrame());
                 //ch.closeFuture().sync();
            	 client1.close();
//            	 client2.close();
//            	 client3.close();
                 break;
             } else if ("ping".equals(msg.toLowerCase())) {
                 WebSocketFrame frame = new PingWebSocketFrame(Unpooled.wrappedBuffer(new byte[] { 8, 1, 8, 1 }));
                 //ch.writeAndFlush(frame);
                 client1.send(frame);
//                 client2.send(frame);
//                 client3.send(frame);
             } else {
                 WebSocketFrame frame = new TextWebSocketFrame(msg);
                 //ch.writeAndFlush(frame);
                 client1.send(frame);
//                 client2.send(frame);
//                 client3.send(frame);
             }
         }*/
	}
	public static SimpleWebSocketClient test(String addr,int port,String filePath){
		URI uri=null;
		try {
			uri=new URI("ws://"+addr+":"+port+"/ws");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		EchoWebSocketClientHandler ch=new EchoWebSocketClientHandler(uri,filePath);
		
		SimpleWebSocketClient client=new SimpleWebSocketClient(addr, port, ch, false, "clientName"+port);
		new Thread(client).start();
		return client;
	}
}
