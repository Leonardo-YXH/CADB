package cn.npt.net.tcp.test;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

public class EchoServerUDPHandler extends SimpleChannelInboundHandler<DatagramPacket>  {

	@Override
	protected void channelRead0(
			ChannelHandlerContext ctx,
			DatagramPacket paramI) throws Exception {
		 ctx.write(new DatagramPacket(
             Unpooled.copiedBuffer(paramI.content().toString(CharsetUtil.UTF_8), CharsetUtil.UTF_8), paramI.sender()));
		
		 //ctx.write(new DatagramPacket(paramI.content().toString(), paramI.sender()));
		 
		 
	}

}
