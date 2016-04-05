package cn.npt.net.tcp.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class EchoOutboundFire extends ChannelOutboundHandlerAdapter {

	private int id;
	public EchoOutboundFire(int id){
		this.id=id;
	}
	public void write(ChannelHandlerContext ctx, Object msg,
			ChannelPromise promise){
		System.out.println("Outbound w"+this.id);
		String response = "Server said："+ctx.channel().remoteAddress().toString()+"\n";  
        ByteBuf buf = Unpooled.wrappedBuffer(response.getBytes());
        ctx.writeAndFlush(buf);  
        buf.release();
//		try {
//			super.write(ctx, msg, promise);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		//ctx.write("Outbound write"+this.id,promise);
	}
}
