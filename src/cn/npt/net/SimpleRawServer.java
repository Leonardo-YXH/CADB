package cn.npt.net;

import cn.npt.net.handler.BaseYHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
/**
 * 不包含encode和decode,直接解析byte数据
 * @author Leonardo
 *
 */
public class SimpleRawServer extends BaseNetServer {

	public SimpleRawServer(int port,BaseYHandler handler,boolean ssl,String serverName) {
		super(port, handler, ssl, serverName);
		this.option(ChannelOption.SO_BACKLOG, 64);//设置默认最大socket连接数64
	}
	
	@Override
	public void init() throws Exception {
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
        this.handler();
        this.serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline p = ch.pipeline();
                if (sslCtx != null) {
                    p.addLast(sslCtx.newHandler(ch.alloc()));
                }
            	if(handler.isSharable()){
            		p.addLast(handler); 
            	}
            	else{
            		p.addLast(handler.deepClone());
            	}
                
            }
        });
        cf = this.serverBootstrap.bind(this.port).sync();
        cf.channel().closeFuture().sync();
	}

}
