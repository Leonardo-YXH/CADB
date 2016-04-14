package cn.npt.net.handler;

import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

import java.util.List;

import cn.npt.fs.cache.BaseMemoryCache;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
/**
 * <br>处理UDP包的数据的简单样例
 * <br>包的格式为:{time:123456789L,values:[1,2...]}
 * @author Leonardo
 *
 */
public class SimpleUDPHandler extends BaseUDPHandler {

	public SimpleUDPHandler(String cachePropertyName, List<Long> sensorIds) {
		super(cachePropertyName, sensorIds);
	}
	public SimpleUDPHandler(BaseMemoryCache cachePool, List<Long> sensorIds) {
		super(cachePool, sensorIds);
	}

	@Override
	public <T> List<T> filter(List<T> src) {
		//do your filter.这里不做任何处理
		return src;
	}

	@Override
	public boolean convert(DatagramPacket paramI) {
		String msg=paramI.content().toString(CharsetUtil.UTF_8);
		JSONObject obj=JSONObject.parseObject(msg);
		if(obj.containsKey("time")&&obj.containsKey("values")){
			this.sensorTime=Long.parseLong(obj.getString("time"));
			this.values=JSONArray.parseArray(obj.getJSONArray("values").toString(), Double.class);
			
			return true;
		}
		return false;
	}
	@Override
	public BaseYHandler deepClone() {
		return new SimpleUDPHandler(cachePool, sensorIds);
	}

}
