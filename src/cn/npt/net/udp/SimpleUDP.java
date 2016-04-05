package cn.npt.net.udp;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;

import cn.npt.net.BaseNetUDP;
import cn.npt.net.handler.test.EchoUDPHandler;
import cn.npt.util.data.PathKit;
/**
 * UDP协议端
 * @author Leonardo
 *
 */
public class SimpleUDP extends BaseNetUDP {

	/**
	 * 
	 * @param localPort
	 * @param remoteAddr
	 * @param remotePort
	 * @param channelHandlers
	 * @param ssl
	 * @param clientName
	 */
	public SimpleUDP(int localPort,String remoteAddr, int remotePort,
			List<ChannelHandlerAdapter> channelHandlers, boolean ssl,
			String clientName) {
		super(localPort,remoteAddr, remotePort, channelHandlers, ssl, clientName);
		//this.option(ChannelOption.SO_BROADCAST, true);
	}
	public static void main(String[] args) {
		PropertyConfigurator.configure(PathKit.getRootClassPath()+"/log4j.properties");
		List<Long> sensorIds=Arrays.asList(1l,2l,3l);
		String addr="192.168.20.84";
		int port=10945;
		List<ChannelHandlerAdapter> chs=new ArrayList<ChannelHandlerAdapter>();
		chs.add(new StringDecoder(CharsetUtil.UTF_8));
		chs.add(new StringEncoder(CharsetUtil.UTF_8));
		chs.add(new EchoUDPHandler("cache.properties", sensorIds));
		//chs.add(new SimpleUDPHandler("cache.properties",sensorIds));
		SimpleUDP udp=new SimpleUDP(10944, addr, port, chs, false, "clientName");
		new Thread(udp).start();
		
		
		Long time=System.currentTimeMillis();
		while(true){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
			//udp.send("hello"+i);
			udp.send("{\"time\":"+time+",\"values\":[1,2.3,3.2]}",addr,port);
		
			time+=1000;
		}
	}
}
