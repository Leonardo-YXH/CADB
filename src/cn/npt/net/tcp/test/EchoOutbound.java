package cn.npt.net.tcp.test;

import java.util.Calendar;
import java.util.Random;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class EchoOutbound extends ChannelOutboundHandlerAdapter {

	private int id;
	public EchoOutbound(int id){
		this.id=id;
	}
	public void write(ChannelHandlerContext ctx, Object msg,
			ChannelPromise promise){
		System.out.println("before Outbound w"+this.id);
//		try {
//			super.write(ctx, msg, promise);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		ctx.write("Outbound write"+this.id,promise);
		System.out.println("after Outbound w"+this.id);
	}
	public void read(ChannelHandlerContext ctx){
		System.out.println("before Outbound r"+this.id);
		ctx.read();
		System.out.println("after Outbound r"+this.id);
		
		boolean p1=true;
		long time=System.currentTimeMillis();
		while(true){
			
//			String request=emitData(1, time);
//			
//			ctx.writeAndFlush(request);
//			request=emitData(2, time);
//			
//			ctx.writeAndFlush(request);
			
			if(p1){
				String request=emitData(1, time);
				//System.out.println(request);
				ctx.writeAndFlush(request);
				p1=false;
			}
			else{
				String request=emitData(2, time);
				//System.out.println(request);
				ctx.writeAndFlush(request);
				p1=true;time+=1000;
			}
			
			
			
			try {
				Thread.sleep(500);
				//Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//ctx.writeAndFlush("Outbound read"+this.id);
	}
	private String emitData(int flag,long time){
		Calendar c=Calendar.getInstance();
		c.setTimeInMillis(time);
		Random r=new Random();
		String head=""
				+c.get(Calendar.YEAR)+"|"
				+(c.get(Calendar.MONTH)+1)+"|"
				+c.get(Calendar.DAY_OF_MONTH)+"|"
				+c.get(Calendar.HOUR_OF_DAY)+"|"
				+c.get(Calendar.MINUTE)+"|"
				+c.get(Calendar.SECOND)+"|"
				+flag+"|"+1;
		if(flag==1){
			int i=0;
			StringBuilder sb=new StringBuilder();
			while(i<12){
				sb.append("|").append(r.nextFloat()*100);
				i++;
			}
			return head+sb.toString();
		}
		else if(flag==2){
			int i=0;
			StringBuilder sb=new StringBuilder();
			while(i<36){
				sb.append("|").append(r.nextFloat()*100);
				i++;
			}
			return head+sb.toString();
		}
		return null;
	}
}
