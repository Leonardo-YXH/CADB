package cn.npt.net.handler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONObject;
/**
 * 数据格式{time:"",values:[sensorId_1:"",...,sensorId_N:""]}
 * @author Leonardo
 *
 */
public class SimpleTCPServerHandler extends BaseTCPServerHandler {

	@Override
	public boolean convert(Object msg) {
		String content=(String) msg;
		JSONObject obj=JSONObject.parseObject(content);
		
		if(obj.containsKey("time")&&obj.containsKey("values")){
			if(this.isFirstInit){
				this.sensorTime=obj.getLongValue("time");
			}
			else{
				long time=obj.getLongValue("time");
				if(time-this.sensorTime<this.cachePool.getCachePoolCfg().blockInterval){//只接收超过指定间隔的数据
					this.isFilter=true;
				}
				else{
					this.sensorTime=time;
				}
			}
			this.sensorValues=new HashMap<Long, Double>();
			JSONObject valuesObj=obj.getJSONObject("values");
			
			Iterator<Entry<String, Object>> it=valuesObj.entrySet().iterator();//要把key转成Long,value转成Double
			while(it.hasNext()){
				Entry<String, Object> e=it.next();
				Long sensorId=Long.parseLong(e.getKey());
				Double value=Double.parseDouble(e.getValue().toString());
				this.sensorValues.put(sensorId, value);
			}
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public BaseYHandler deepClone() {
		return new SimpleTCPServerHandler();
	}

}
