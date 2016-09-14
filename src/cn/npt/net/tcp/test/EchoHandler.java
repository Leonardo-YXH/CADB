package cn.npt.net.tcp.test;

import cn.npt.net.handler.BaseYHandler;
import io.netty.channel.ChannelHandlerContext;

public class EchoHandler extends BaseYHandler {

	private int id;
	public EchoHandler(int id){
		this.id=id;
	}
	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
		String v=(String)msg;
    	System.out.println(v);
    	if(v.equals("close")){
    		ctx.close();
    	}
    }
	public void channelActive(ChannelHandlerContext paramChannelHandlerContext) throws Exception {
		System.out.println("connected success");
	}
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		System.out.println("连接异常，已断开。。。");
	}
	@Override
	public BaseYHandler deepClone() {
		// TODO Auto-generated method stub
		return new EchoHandler(id);
	}
}
