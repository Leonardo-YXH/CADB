package cn.npt.net.tcp.test;

import cn.npt.net.handler.BaseYHandler;
import io.netty.channel.ChannelHandlerContext;

public class EchoInbound extends BaseYHandler {

	private int id;
	public EchoInbound(int id){
		this.id=id;
	}
	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ctx.writeAndFlush(""+msg+this.id);
    }
	@Override
	public void channelActive(ChannelHandlerContext ctx){
	    System.out.println(ctx.channel().toString()+" socket connected!");
    }
	@Override
    public void channelInactive(ChannelHandlerContext ctx){
    	System.out.println(ctx.channel()+" websocket disconnected!");
    	ctx.close();
    }
	@Override
	public void channelRegistered(
			ChannelHandlerContext paramChannelHandlerContext) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void channelUnregistered(
			ChannelHandlerContext paramChannelHandlerContext) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void userEventTriggered(
			ChannelHandlerContext paramChannelHandlerContext, Object paramObject)
			throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void channelWritabilityChanged(
			ChannelHandlerContext paramChannelHandlerContext) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public BaseYHandler deepClone() {
		return new EchoInbound(id);
	}
	@Override
	public void channelReadComplete(
			ChannelHandlerContext paramChannelHandlerContext) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
