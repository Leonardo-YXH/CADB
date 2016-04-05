package cn.npt.net.tcp.test;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class EchoClientPushHandler extends ChannelOutboundHandlerAdapter {

	public void write(ChannelHandlerContext ctx, Object msg,
			ChannelPromise promise){
		System.out.println("ecph w");
		try {
			super.write(ctx, msg, promise);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//ctx.writeAndFlush("hello server Outbound write!",promise);
	}
	public void read(ChannelHandlerContext ctx){
		System.out.println("ecph r");
		try {
			super.read(ctx);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//ctx.writeAndFlush("hello server Outbound read!");
	}
}
