package cn.npt.net.tcp;

import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSONObject;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.util.CharsetUtil;
import cn.npt.net.BaseNetClient;
import cn.npt.net.NPTChannelStatus;
import cn.npt.net.handler.BaseYHandler;
import cn.npt.net.tcp.test.EchoHandler;
/**
 * tcp客户端
 * @author Leonardo
 *
 */
public class SimpleTCPClient extends BaseNetClient {

	/**
	 * 
	 * @param remoteAddr 远程server地址
	 * @param remotePort 远程server端口
	 * @param handler 业务逻辑处理代码
	 * @param ssl 是否加密
	 * @param clientName 客户端名称
	 */
	public SimpleTCPClient(String remoteAddr, int remotePort,
			BaseYHandler handler, boolean ssl,
			String clientName) {
		super(remoteAddr, remotePort, handler, ssl, clientName);
		if(clientName!=null&&!clientName.isEmpty()){
			this.clientName=clientName;
		}
		else{
			this.clientName="SimpleTCPClient";
		}
		this.bootstrap.option(ChannelOption.TCP_NODELAY, true);
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
                     
                     p.addFirst(new ChannelInboundHandlerAdapter(){
                    	 @Override
                         public void channelInactive(ChannelHandlerContext ctx) throws Exception {//断开的时候检测重连
                             super.channelInactive(ctx);
                             if(!status.equals(NPTChannelStatus.CLOSED_INITIATIVE)){//如果不是主动关闭则需重连
                            	 ctx.channel().eventLoop().schedule(new reConnect(), 5, TimeUnit.SECONDS);//5秒检测重连
                             }
                    	 }
                     });
                     
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
	                	f.channel().eventLoop().schedule(new reConnect(), 5, TimeUnit.SECONDS);
	                }
	            }
	        });
		}
		
	}
	
	public static void main(String[] args) {
		SimpleTCPClient client=new SimpleTCPClient("192.168.20.84", 8008, new EchoHandler(1), false, "clientName");
		
		new Thread(client).start();
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Long time=System.currentTimeMillis();
		JSONObject values=new JSONObject();
		values.put("2", 1);
		values.put("3", 5);
		JSONObject obj=new JSONObject();
		int i=0;
		while(true){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
			
			obj.put("time", time);
			obj.put("values", values);
			client.send(obj.toJSONString());
//			if(client.status.equals(NPTChannelStatus.CLOSED)){
//				break;
//			}
			time+=2000;
			i++;
			if(i>20){
				client.close();
				break;
			}
			
		}
	}
}
