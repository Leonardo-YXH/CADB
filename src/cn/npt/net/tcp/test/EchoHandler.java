package cn.npt.net.tcp.test;

import java.util.Arrays;
import java.util.List;

import cn.npt.net.handler.BaseTCPClientHandler;
import cn.npt.net.handler.BaseYHandler;
import io.netty.channel.ChannelHandlerContext;

public class EchoHandler extends BaseTCPClientHandler {

	private int id;
	public EchoHandler(int id){
		super("cache.properties", Arrays.asList(1l));
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
	@Override
	public <T> List<T> filter(List<T> src) {
		// TODO Auto-generated method stub
		return src;
	}
	@Override
	public boolean convert(Object msg) {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public BaseYHandler deepClone() {
		// TODO Auto-generated method stub
		return new EchoHandler(id);
	}
}
