/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package cn.npt.net.websocket;

import cn.npt.net.BaseSendServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

/**
 * A HTTP server which serves Web Socket requests at:
 *
 * http://localhost:8080/websocket
 *
 * Open your browser at http://localhost:8080/, then the demo page will be loaded and a Web Socket connection will be
 * made automatically.
 *
 * This server illustrates support for the different web socket specification versions and will work with:
 *
 * <ul>
 * <li>Safari 5+ (draft-ietf-hybi-thewebsocketprotocol-00)
 * <li>Chrome 6-13 (draft-ietf-hybi-thewebsocketprotocol-00)
 * <li>Chrome 14+ (draft-ietf-hybi-thewebsocketprotocol-10)
 * <li>Chrome 16+ (RFC 6455 aka draft-ietf-hybi-thewebsocketprotocol-17)
 * <li>Firefox 7+ (draft-ietf-hybi-thewebsocketprotocol-10)
 * <li>Firefox 11+ (RFC 6455 aka draft-ietf-hybi-thewebsocketprotocol-17)
 * </ul>
 * @author modified by Leonardo
 */
public class WebSocketServer extends BaseSendServer{

    private int port;
    private boolean ssl;
    private String webSocketPath;
    private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
    /**
     * 
     * @param port
     * @param ssl 默认false
     * @param webSocketPath 
     * 
     */
    public WebSocketServer(int port,boolean ssl,String webSocketPath){
    	this.port=port;
    	this.ssl=ssl;
    	this.webSocketPath=webSocketPath;
    	
    }
    private void init(int port,boolean ssl,String webSocketPath) throws Exception{
    	final SslContext sslCtx;
        if (this.ssl) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }

        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .handler(new LoggingHandler(LogLevel.INFO))
             .childHandler(new WebSocketServerInitializer(sslCtx, webSocketPath, ssl));

            Channel ch = b.bind(this.port).sync().channel();

            ch.closeFuture().sync();//阻塞函数
        } 
        finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
        System.out.println("start websocket...");
    }
    
	@Override
	public void start() {
		try {
			init(port, ssl, webSocketPath);;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void stop() {
		bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
	}
	/**
	 * test
	 * @param args
	 */
	public static void main(String[] args){
    	new WebSocketServer(8080, false, "/ws").start();
        
    }
}
