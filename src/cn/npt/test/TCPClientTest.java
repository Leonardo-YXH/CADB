package cn.npt.test;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import cn.npt.net.tcp.test.EchoHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class TCPClientTest {
    private volatile EventLoopGroup workerGroup;

    private volatile Bootstrap bootstrap;

    private ChannelFuture future;
    private volatile boolean closed = false;

    private final String remoteHost;

    private final int remotePort;

    public TCPClientTest(String remoteHost, int remotePort) {
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }

    public void send(Object msg){
    	this.future.channel().writeAndFlush(msg);
    }
    public void close() {
        closed = true;
        workerGroup.shutdownGracefully();
        System.out.println("Stopped Tcp Client: " + getServerInfo());
    }

    public void init() {
        closed = false;

        workerGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(workerGroup);
        bootstrap.channel(NioSocketChannel.class);

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addFirst(new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                        super.channelInactive(ctx);
                        System.out.println("reconnect...");
                        ctx.channel().eventLoop().schedule(new doConnect(), 1, TimeUnit.SECONDS);
                    }
                });

                //todo: add more handler
                pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
                pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
                pipeline.addLast(new EchoHandler(1));
            }
        });

        new Thread(new doConnect()).start();;
    }

    private class doConnect implements Runnable {
        
			@Override
			public void run() {
				if (closed) {
		            return;
		        }
		        System.out.println("do connect...");
		        future = bootstrap.connect(new InetSocketAddress(remoteHost, remotePort));

		        future.addListener(new ChannelFutureListener() {
		            public void operationComplete(ChannelFuture f) throws Exception {
		                if (f.isSuccess()) {
		                	future=f;
		                    System.out.println("Started Tcp Client: " + getServerInfo());
		                } else {
		                    System.out.println("Started Tcp Client Failed: " + getServerInfo());
		                    f.channel().eventLoop().schedule(new doConnect(), 1, TimeUnit.SECONDS);
		                }
		            }
				});
			}
    }

    private String getServerInfo() {
        return String.format("RemoteHost=%s RemotePort=%d",
                remotePort,
                remotePort);
    }
	public static void main(String[] args) {
		TCPClientTest client=new TCPClientTest("192.168.20.84", 8008);
		client.init();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		int i=0;
		while(true){
			client.send("echo");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			i++;
			if(i>5){
				client.close();
			}
		}
	}
}
