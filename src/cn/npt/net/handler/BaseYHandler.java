package cn.npt.net.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
/**
 * handler类型推断，若handler无成员，则可以设置为Sharable,否则重写deepClone函数重新创建新对象
 * @author Leonardo
 *
 */
public abstract class BaseYHandler extends ChannelHandlerAdapter implements ChannelInboundHandler {
	/**
	 * 深度克隆
	 * @return
	 */
	public abstract BaseYHandler deepClone();
	
	@Override
	public void channelActive(ChannelHandlerContext paramChannelHandlerContext)
			throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void channelInactive(ChannelHandlerContext paramChannelHandlerContext)
			throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void channelReadComplete(
			ChannelHandlerContext paramChannelHandlerContext) throws Exception {
		// TODO Auto-generated method stub
		
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
	public void channelWritabilityChanged(
			ChannelHandlerContext paramChannelHandlerContext) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void userEventTriggered(
			ChannelHandlerContext paramChannelHandlerContext, Object paramObject)
			throws Exception {
		// TODO Auto-generated method stub
		
	}
}
