package cn.npt.net.handler.test;

import cn.npt.net.handler.BaseWebSocketServerHandler;
import cn.npt.net.handler.BaseYHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
/**
 * 简单测试BaseWebSocketServerHandler
 * @author Leonardo
 *
 */
public class EchoWebSocketServerHandler extends BaseWebSocketServerHandler {

	private int visitCount;
	public EchoWebSocketServerHandler(String webSocketPath, boolean ssl) {
		super(webSocketPath, ssl);
		this.visitCount=0;
	}
	@Override
	public void doReceive(String req, ChannelHandlerContext ctx) {
		System.out.println("receive:"+req);
		String msg=req+"_"+this.visitCount;
		this.visitCount++;
		ctx.writeAndFlush(new TextWebSocketFrame(msg));
	}

	@Override
	public BaseYHandler deepClone() {
		return new EchoWebSocketServerHandler(webSocketPath, ssl);
	}

}
