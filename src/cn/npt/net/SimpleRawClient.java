package cn.npt.net;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.util.concurrent.TimeUnit;

import cn.npt.net.handler.BaseYHandler;

public class SimpleRawClient extends BaseNetClient {
	/**
	 * 
	 * @param remoteAddr 远程server地址
	 * @param remotePort 远程server端口
	 * @param handler 业务逻辑处理代码
	 * @param ssl 是否加密
	 * @param clientName 客户端名称
	 */
	public SimpleRawClient(String remoteAddr, int remotePort,
			BaseYHandler handler, boolean ssl,
			String clientName) {
		super(remoteAddr, remotePort, handler, ssl, clientName);
		if(clientName!=null&&!clientName.isEmpty()){
			this.clientName=clientName;
		}
		else{
			this.clientName="SimpleRawClient";
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
}
