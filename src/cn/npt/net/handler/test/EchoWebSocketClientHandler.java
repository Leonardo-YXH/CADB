package cn.npt.net.handler.test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import org.apache.log4j.Logger;

import cn.npt.net.handler.BaseWebSocketClientHandler;
import cn.npt.net.handler.BaseYHandler;

import com.alibaba.fastjson.JSONArray;
import com.google.common.io.Files;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
/**
 * 
 * @author Leonardo
 *
 */
public class EchoWebSocketClientHandler extends BaseWebSocketClientHandler {

	private JSONArray robotcontent;
	private String filePath;
	private static Logger log=Logger.getLogger(EchoWebSocketClientHandler.class);
	public EchoWebSocketClientHandler(WebSocketClientHandshaker handshaker) {
		super(handshaker);
		this.robotcontent=new JSONArray();
		this.filePath="F:/datas/temp.json";
	}

	public EchoWebSocketClientHandler(URI uri,String filePath) {
		super(uri);
		this.robotcontent=new JSONArray();
		this.filePath=filePath;
	}
	@Override
	public void doReceive(String text) {
		//System.out.println(text);
		JSONArray arr=JSONArray.parseArray(text);
		this.robotcontent.addAll(arr);
		System.out.println("========="+filePath+" size:"+this.robotcontent.size());
	}

	@Override
    public void channelInactive(ChannelHandlerContext ctx) {
    	super.channelInactive(ctx);
    	try {
			Files.write(this.robotcontent.toJSONString(), new File(filePath), StandardCharsets.UTF_8);
			log.info("json file save successfully:"+filePath);
		} catch (IOException e) {
			log.error("json file save failed:"+filePath);
			e.printStackTrace();
		}
    }

	@Override
	public BaseYHandler deepClone() {
		return new EchoWebSocketClientHandler(handshaker);
	}
}
