/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package cn.npt.net.websocket;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import cn.npt.fs.CachePoolFactory;
import cn.npt.fs.cache.BaseMemoryCache;
import cn.npt.fs.cache.CachePool;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.CharsetUtil;
import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpMethod.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;

/**
 * <br>实时显示传感器的数据,目前只支持单个缓存池取数据,所以尽量同一厂间的传感器数据放在同一缓存池中
 * <br>前台websocket访问数据格式{cmd:getSensorValue,depth:0,timeInterval:1000,sensorIds:[...]}
 * <br>cmd:请求的类型，目前包括getSensorValue,updateSensorValue,getStartTime,getSensorCount
 * <br>depth:0--原始数据;1--第一层BS;2--第二层BS;3--第三层BS
 * <br>timeInterval:传输频率,单位ms
 * <br>sensorIds:需要显示的传感器ID
 * @author Leonardo
 *
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

    private String webSocketPath;
    private WebSocketServerHandshaker handshaker;
    private Timer timer;
    /**
     * 定时刷新
     */
    private WebSocketTimerTask timerTask;
    /**
     * 有数据才刷新
     */
    private WebSocketTimerTask1 timerTask1;
    private boolean ssl;
    
    private static Logger log=Logger.getLogger(WebSocketServerHandler.class);
    
    public WebSocketServerHandler(String webSocketPath,boolean ssl) {
		this.webSocketPath=webSocketPath;
		this.ssl=ssl;
	}
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx){
    	this.timer.cancel();
    	this.timer=null;
    	log.info(ctx.channel()+" websocket disconnected!");
    	ctx.close();
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx){
    	this.timer=new Timer();
    	log.info(ctx.channel()+" websocket connected!");
    }
    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        // Handle a bad request.
        if (!req.getDecoderResult().isSuccess()) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST));
            return;
        }

        // Allow only GET methods.
        if (req.getMethod() != GET) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN));
            return;
        }

        // Send the demo page and favicon.ico
        if ("/".equals(req.getUri())) {
            ByteBuf content = WebSocketServerIndexPage.getContent(getWebSocketLocation(req));
            FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, OK, content);

            res.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");
            HttpHeaders.setContentLength(res, content.readableBytes());

            sendHttpResponse(ctx, req, res);
            return;
        }
        if ("/favicon.ico".equals(req.getUri())) {
            FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND);
            sendHttpResponse(ctx, req, res);
            return;
        }

        // Handshake
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                getWebSocketLocation(req), null, true);
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
        }
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {

        // Check for closing frame
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass()
                    .getName()));
        }

        //请求的数据格式{cmd:getSensorValue,depth:0,timeInterval:1000,sensorIds:[...]},取的是最新的数据，并不一定是当前时刻的数据
        String request = ((TextWebSocketFrame) frame).text();
        JSONObject reqObj=JSON.parseObject(request);
        if(reqObj.containsKey("cmd")){
        	String cmd=reqObj.getString("cmd");
        	switch(cmd){
	        	case "getStartTime":
	        		getStartTime(ctx);
	        		break;
	        	case "getSensorCount":
	        		getSensorCount(ctx);
	        		break;
	        	case "getSensorValue"://主要
	        		getSensorValue(reqObj, ctx);
	        		break;
	        	case "updateSensorValue"://主要
	        		updateSensorValue(reqObj, ctx);
	        		break;
	        	case "getCachePoolDepth":
	        		getCachePoolDepth(reqObj, ctx);
	        		break;
	        	case "getSensorHandlers":
	        		getSensorHandlers(reqObj, ctx);
	        		break;
	        	default:
	        		ctx.writeAndFlush(new TextWebSocketFrame("未知的请求命令"));
	        		break;
        	}
        }
        else{
        	log.warn("unknown command");
        	ctx.writeAndFlush(new TextWebSocketFrame("错误的请求格式"));
        }
        
        //ctx.executor().scheduleAtFixedRate(paramRunnable, paramLong1, paramLong2, paramTimeUnit)

    }
    /**
     * 开始创建缓存池的时间
     * @param ctx
     */
    private void getStartTime(ChannelHandlerContext ctx){
    	JSONObject rs=new JSONObject();
    	rs.put("firstCreatedTime", CachePoolFactory.firstCreatedTime.toString());
    	ctx.writeAndFlush(new TextWebSocketFrame(rs.toString()));
    }
    /**
     * 获取缓存池里面sensor的数量
     * @param ctx
     */
    private void getSensorCount(ChannelHandlerContext ctx){
    	int count=CachePoolFactory.getSensorCount();
    	JSONObject rs=new JSONObject();
    	rs.put("sensorCount", count);
    	ctx.writeAndFlush(new TextWebSocketFrame(rs.toString()));
    }
    
    
    /**
     * <br>前台websocket访问数据格式{depth:0,timeInterval:1000,sensorIds:[...]}
	 * <br>depth:0--原始数据;1--第一层BS;2--第二层BS;3--第三层BS
	 * <br>timeInterval:传输频率,单位ms
	 * <br>sensorIds:需要显示的传感器ID
     * @param reqObj
     * @param ctx
     * @see #updateSensorValue(JSONObject, ChannelHandlerContext)
     */
    private void getSensorValue(JSONObject reqObj,ChannelHandlerContext ctx){
    	if(reqObj.containsKey("timeInterval")&&reqObj.containsKey("depth")&&reqObj.containsKey("sensorIds")){
    		int timeInterval=reqObj.getIntValue("timeInterval");
    		if(timeInterval<50){
    			ctx.writeAndFlush(new TextWebSocketFrame("请求的频率过高,参数timeInterval必须大于等于50"));
    		}
    		else{
    			if(this.timerTask!=null){
	            	this.timerTask.cancel();
	            }
    			if(this.timerTask1!=null){//
    				this.timerTask1.cancel();
    			}
	            this.timerTask=new WebSocketTimerTask(ctx, reqObj);
	            this.timer.schedule(this.timerTask, 0, timeInterval);
    		}
    	}
    	else{
    		//log.warn("");
    		ctx.writeAndFlush(new TextWebSocketFrame("未知的请求格式,您可能是想发送{cmd:'getSensorValue',depth:0,timeInterval:1000,sensorIds:[...]}"));
    	}
    }
    /**
     * 有新数据才推送
     * @param reqObj
     * @param ctx
     * @see #getSensorValue(JSONObject, ChannelHandlerContext)
     */
    private void updateSensorValue(JSONObject reqObj,ChannelHandlerContext ctx){
    	if(reqObj.containsKey("timeInterval")&&reqObj.containsKey("depth")&&reqObj.containsKey("sensorIds")){
    		int timeInterval=reqObj.getIntValue("timeInterval");
    		if(timeInterval<50){
    			ctx.writeAndFlush(new TextWebSocketFrame("请求的频率过高,参数timeInterval必须大于等于50"));
    		}
    		else{
    			if(this.timerTask1!=null){
    				this.timerTask1.cancel();
    			}
    			if(this.timerTask!=null){
    				this.timerTask.cancel();
    			}
    			this.timerTask1=new WebSocketTimerTask1(ctx, reqObj);
    			this.timer.schedule(this.timerTask1, 0, timeInterval);
    		}
    	}
    	else{
    		//log.warn("");
    		ctx.writeAndFlush(new TextWebSocketFrame("未知的请求格式,您可能是想发送{cmd:'getSensorValue',depth:0,timeInterval:1000,sensorIds:[...]}"));
    	}
    }
    /**
     * 实时推送当前value
     * @author Leonardo
     *
     */
    private class WebSocketTimerTask extends TimerTask{
    	private ChannelHandlerContext ctx;
    	private JSONObject reqObj;
    	private JSONArray sensorIds;
    	private int depth;
    	private final List<CachePool<?>> pools;//只读模式
    	private StringBuilder errorInfo;
    	public WebSocketTimerTask(ChannelHandlerContext ctx, JSONObject reqObj){
    		this.ctx=ctx;
    		this.reqObj=reqObj;
    		this.sensorIds=this.reqObj.getJSONArray("sensorIds");
    		this.depth=this.reqObj.getIntValue("depth");
    		this.pools=new ArrayList<CachePool<?>>();
    		this.errorInfo=new StringBuilder();
    		for(int i=0;i<sensorIds.size();i++){
	        	boolean sensorExist=false;
	        	long sensorId=sensorIds.getLongValue(i);
	        	
	        	for(BaseMemoryCache cachePool:CachePoolFactory.getCachePools().values()){
	        		CachePool<?> cp=cachePool.getCachePool(sensorId, depth);
	        		if(cp!=null){
	        			this.pools.add(cp);
	        			sensorExist=true;
	        			break;
	        		}
	        	}
	        	if(!sensorExist){
	        		if(depth==0){
	        			this.errorInfo.append("sensorId:")
	        						.append(sensorId)
	        						.append(" 不存在\n");
	        		}
	        		else{//逻辑不完备(假定sensorId存在)
	        			this.errorInfo.append("sensorId:")
										.append(sensorId)
										.append(" 不存在或者不存在深度为")
	        							.append(depth)
	        							.append("的统计池\n");
	        		}
	        	}
	        }
    	}
		@Override
		public void run() {
			if(this.errorInfo.length()==0){//无错误信息
				JSONObject rs=new JSONObject();
				
				for(CachePool<?> pool:this.pools){
					rs.putAll(pool.currentV2JSON());
				}
				ctx.channel().writeAndFlush(new TextWebSocketFrame(rs.toJSONString()));
			}
			else{
				ctx.channel().writeAndFlush(new TextWebSocketFrame(this.errorInfo.toString()));
				this.cancel();
			}
		}
    }
    /**
     * 实时推送最新value（有更新就推送，没有就不处理）
     * @author Leonardo
     *
     */
    private class WebSocketTimerTask1 extends TimerTask{
    	private ChannelHandlerContext ctx;
    	private JSONObject reqObj;
    	private JSONArray sensorIds;
    	private int depth;
    	private final List<CachePool<?>> pools;//只读模式
    	/**
    	 * 上一次发送的索引，用来判断数据是否有更新
    	 */
    	private List<Integer> sensorPreviousIndexs;
    	private StringBuilder errorInfo;
    	public WebSocketTimerTask1(ChannelHandlerContext ctx, JSONObject reqObj){
    		this.ctx=ctx;
    		this.reqObj=reqObj;
    		this.sensorIds=this.reqObj.getJSONArray("sensorIds");
    		this.depth=this.reqObj.getIntValue("depth");
    		this.pools=new ArrayList<CachePool<?>>();
    		this.sensorPreviousIndexs=new ArrayList<Integer>();
    		this.errorInfo=new StringBuilder();
    		for(int i=0;i<sensorIds.size();i++){
    			boolean sensorExist=false;
    			long sensorId=sensorIds.getLongValue(i);
    			
    			for(BaseMemoryCache cachePool:CachePoolFactory.getCachePools().values()){
    				CachePool<?> cp=cachePool.getCachePool(sensorId, depth);
    				if(cp!=null){
    					this.pools.add(cp);
    					this.sensorPreviousIndexs.add(cp.getIndex());
    					sensorExist=true;
    					break;
    				}
    			}
    			if(!sensorExist){
    				if(depth==0){
    					this.errorInfo.append("sensorId:")
    					.append(sensorId)
    					.append(" 不存在\n");
    				}
    				else{//逻辑不完备(假定sensorId存在)
    					this.errorInfo.append("sensorId:")
    					.append(sensorId)
    					.append(" 不存在或者不存在深度为")
    					.append(depth)
    					.append("的统计池\n");
    				}
    			}
    		}
    	}
    	@Override
    	public void run() {
    		if(this.errorInfo.length()==0){//无错误信息
    			JSONObject rs=new JSONObject();
    			
    			for(int i=0;i<this.pools.size();i++){
    				CachePool<?> pool=this.pools.get(i);
    				if(!this.sensorPreviousIndexs.get(i).equals(pool.getIndex())){
    					rs.putAll(pool.currentV2JSON());
    				}
    				
    			}
    			ctx.channel().writeAndFlush(new TextWebSocketFrame(rs.toJSONString()));
    		}
    		else{
    			ctx.channel().writeAndFlush(new TextWebSocketFrame(this.errorInfo.toString()));
    			this.cancel();
    		}
    	}
    }
    
    private void getCachePoolDepth(JSONObject reqObj,ChannelHandlerContext ctx){
    	Long sensorId=reqObj.getLong("sensorId");
    	int depth=CachePoolFactory.getCachePool(sensorId).getDepth(sensorId);
    	ctx.writeAndFlush(new TextWebSocketFrame("depth:"+depth));
    }
    private void getSensorHandlers(JSONObject reqObj,ChannelHandlerContext ctx){
    	
    	ctx.writeAndFlush(new TextWebSocketFrame("待完成"));
    }
    private static void sendHttpResponse(
            ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
        // Generate an error page if response getStatus code is not OK (200).
        if (res.getStatus().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
            HttpHeaders.setContentLength(res, res.content().readableBytes());
        }

        // Send the response and close the connection if necessary.
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!HttpHeaders.isKeepAlive(req) || res.getStatus().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    	log.error(cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }

    private String getWebSocketLocation(FullHttpRequest req) {
        String location =  req.headers().get(HOST) + this.webSocketPath;
        if (this.ssl) {
            return "wss://" + location;
        } else {
            return "ws://" + location;
        }
    }
}
