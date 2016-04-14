package cn.npt.net.handler;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.npt.fs.cache.BaseMemoryCache;
/**
 * <br>处理TCP包的数据的简单样例
 * <br>包的格式为:{time:123456789L,values:[1,2...]}
 * @author Leonardo
 *
 */
public class SimpleTCPClientHandler extends BaseTCPClientHandler {

	public SimpleTCPClientHandler(BaseMemoryCache cachePool, List<Long> sensorIds) {
		super(cachePool, sensorIds);
	}
	public SimpleTCPClientHandler(String cachePropertyName, List<Long> sensorIds) {
		super(cachePropertyName, sensorIds);
	}

	@Override
	public <T> List<T> filter(List<T> src) {
		//do your filter.这里不做任何处理
		return src;
	}

	@Override
	public boolean convert(Object msg) {
		String msg_=(String) (msg);
		JSONObject obj=JSONObject.parseObject(msg_);
		if(obj.containsKey("time")&&obj.containsKey("values")){
			this.sensorTime=Long.parseLong(obj.getString("time"));
			this.values=JSONArray.parseArray(obj.getJSONArray("values").toString(), Double.class);
			
			return true;
		}
		return false;
	}
	
	@Override
	public BaseYHandler deepClone() {
		return new SimpleTCPClientHandler(cachePool, sensorIds);
	}

}
