package cn.npt.fs.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.greenpineyu.fel.context.FelContext;

import cn.npt.db.event.BaseBS2DBHandler;
import cn.npt.fs.alarm.IAlarmHandler;
import cn.npt.fs.bean.AccumulateSensor;
import cn.npt.fs.bean.AlarmSensor;
import cn.npt.fs.bean.BSSensor;
import cn.npt.fs.bean.CCSensor;
import cn.npt.fs.config.CacheBlockCfg;
import cn.npt.fs.config.CachePoolTreeCfg;
import cn.npt.fs.event.AlarmHandler;
import cn.npt.fs.event.SaveOLHandler;
import cn.npt.fs.event.SensorHandler;
import cn.npt.util.algorithm.graph.UnionFind;
/**
 * 缓存BS结构的数据
 * @author Leonardo 
 */
public class BaseMemoryCache {
	/**
	 * 缓存
	 */
	private Map<Long,CachePool<?>> SensorFragments;
	/**
	 * 通道合并传感器
	 */
	private List<CCSensor> CCSensors;
	/**
	 * cc执行控制开关
	 */
	private boolean CCTroggleState;
	/**
	 * 警报传感器
	 */
	private List<AlarmSensor> alarmSensors;
	/**
	 * 里程传感器
	 */
	private List<AccumulateSensor> accumulateSensors;
	/**
	 * alarm执行控制开关
	 */
	private boolean alarmTroggleState;
	/**
	 * 缓存池配置参数
	 */
	private CachePoolTreeCfg cptc;
	/**
	 * 根是否是原始数据，否则是BS数据
	 */
	private boolean isRoot;
	/**
	 * 新建一个缓存池时请使用<b>CachePoolFactory.build(String )</b>方法,否则后续的查询操作失效
	 * @param propertyFileName
	 */
	public BaseMemoryCache(String propertyFileName){
		this.cptc=new CachePoolTreeCfg(propertyFileName);
		this.isRoot=this.cptc.isRoot();
		this.SensorFragments=new HashMap<Long, CachePool<?>>();
		this.CCSensors=new ArrayList<CCSensor>();
		this.CCTroggleState=false;
		this.alarmSensors=new ArrayList<AlarmSensor>();
		this.alarmTroggleState=false;
		
		this.accumulateSensors=new ArrayList<AccumulateSensor>();
	}

	/**
	 * 缓存数据
	 * @param time
	 * @param sensorValues [{sensorId,sensorValue},...]
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void execute(long time,Map<Long,Double> sensorValues){
		executeAccumulateSensor(time, sensorValues);
		
		Iterator<Long> it=sensorValues.keySet().iterator();
		while(it.hasNext()){
			long sensorId=it.next();
			double sensorValue=sensorValues.get(sensorId);
			CachePool svp=SensorFragments.get(sensorId);
			svp.setValue(sensorValue, time);
			//log.info("sensorId:"+sensorId+" setValue:"+sensorValue+"  getValue:"+svp.getValue(time));
		}
		if(this.CCTroggleState){
			executeCCSensor(time, sensorValues);
		}
		if(this.alarmTroggleState){
			executeAlarmSensor(time, sensorValues);
		}
	} 
	/**
	 * 量程计算(必须先于其他通道执行，获取先前pv)
	 * @param time
	 * @param sensorValues
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void executeAccumulateSensor(long time,Map<Long,Double> sensorValues){
		for(AccumulateSensor as:this.accumulateSensors){
			Double v=sensorValues.get(as.getSrcSensorId());
			if(v!=null){
				Double pv=(Double)this.SensorFragments.get(as.getSrcSensorId()).getCurrentValue();
				if(pv.isNaN()){
					CachePool pool=this.SensorFragments.get(as.getSensorId());
					Double apv=(Double) pool.getCurrentValue();
					if(apv.isNaN()){
						pool.setValue(0D, time);
					}
					else{
						pool.setValue(apv, time);
					}
				}
				else{
					CachePool pool=this.SensorFragments.get(as.getSensorId());
					Double apv=(Double) pool.getCurrentValue();
					pool.setValue(apv+Math.abs(pv-v), time);
				}
			}
		}
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void executeCCSensor(long time,Map<Long,Double> sensorValues){
		for(CCSensor cSensor:CCSensors){
			List<Long> cSensorIds=cSensor.getSensorIds();
			FelContext ctx=cSensor.getFelCtx();
			boolean flag=true;
			for(Long cSensorId:cSensorIds){
				Double v=sensorValues.get(cSensorId);
				if(v==null){
					v=(Double) SensorFragments.get(cSensorId).getValue(time);
					if(v!=null){
						//sensorValues.put(cSensorId, v);//暂时缓存!ccSensor不能缓存，sensorValues是引用
						//log.info("[1-pool]time:"+new Date(time).toString()+"  sensorId:"+cSensorId+"  value:"+v);
						ctx.set("_"+cSensorId, v);
					}
					else{//数据不齐全
						flag=false;
						break;
					}
				}
				else{
					//sensorValues.put(cSensorId, v);//暂时缓存
					//log.info("[0-src]time:"+new Date(time).toString()+"  sensorId:"+cSensorId+"  value:"+v);
					ctx.set("_"+cSensorId, v);
				}
			}
			if(flag){
				long sensorId=cSensor.getSensorId();
				Double value=cSensor.eval(ctx);
				CachePool cp=SensorFragments.get(sensorId);
				cp.setValue(value, time);
				//log.info("[0-pool-cc]time:"+new Date(time).toString()+"  sensorId:"+sensorId+"  value:"+SensorFragments.get(sensorId).getValue(time)+"sValue:"+value);
			}
		}
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void executeAlarmSensor(long time,Map<Long,Double> sensorValues){
		for(AlarmSensor aSensor:alarmSensors){
			List<Long> aSensorIds=aSensor.getSensorIds();
			List<Double> alarmValues=new ArrayList<Double>();
			boolean flag=true;
			for(Long aSensorId:aSensorIds){
				Double v=sensorValues.get(aSensorId);
				if(v==null){
					v=(Double) SensorFragments.get(aSensorId).getValue(time);
					if(v!=null){
						alarmValues.add(v);
					}
					else{//数据不齐全
						flag=false;
						break;
					}
				}
				else{
					alarmValues.add(v);
				}
			}
			if(flag){
				long sensorId=aSensor.getSensorId();
				Double value=aSensor.doCheck(alarmValues);
				CachePool cp=SensorFragments.get(sensorId);
				cp.setValue(value, time);
			}
		}
	}
	
	public void addSensor(long sensorId){
		if(this.cptc.isRoot()){
			CacheBlockCfg cbCfg=new CacheBlockCfg(this.cptc.getBlockIntervalInMs(), this.cptc.getSize(), this.cptc.getfileHandler());
			SensorValuePool pool=new SensorValuePool(sensorId, cbCfg,this.cptc.getDataDir());//
			SaveOLHandler handler=new SaveOLHandler();
			pool.addListener(handler);
			JSONArray sqlHandlers=this.cptc.getSqlHandlers();
			for(int i=0;i<sqlHandlers.size();i++){
				JSONObject item=sqlHandlers.getJSONObject(i);
				BaseBS2DBHandler sqlHandler=new BaseBS2DBHandler(item.getString("table"), BSSensorPool.oneInsertSize, item.getIntValue("blockSize"), item.getIntValue("offset"));
				pool.addListener(sqlHandler);
			}
			this.SensorFragments.put(sensorId, pool);
			if(this.cptc.hasChild()){
				this.cptc.next();
				BSSensorPool bsp=new BSSensorPool(sensorId, cptc);
				pool.setBsp(bsp);
			}
		}
		else{
			BSSensorPool bsp=new BSSensorPool(sensorId, cptc);
			this.SensorFragments.put(sensorId, bsp);
		}
		//重置指针
		this.cptc.resume();
		
	}
	/**
	 * 添加sensor到缓存池，但不加任何操作
	 * @param sensorId
	 */
	public void addSensorWithoutHandler(long sensorId){
		if(this.cptc.isRoot()){
			CacheBlockCfg cbCfg=new CacheBlockCfg(this.cptc.getBlockIntervalInMs(), this.cptc.getSize(), this.cptc.getfileHandler());
			SensorValuePool pool=new SensorValuePool(sensorId, cbCfg,this.cptc.getDataDir());//
			this.SensorFragments.put(sensorId, pool);
		}
	}
	/**
	 * 移除Sensor及依赖于它的CCSensor和alarmSensor(递归查找其依赖链，未作全面测试)
	 * @param sensorId
	 */
	public void removeSensor(long sensorId){
		//需要移除的依赖项
		List<AlarmSensor> ases=new ArrayList<AlarmSensor>();
		List<CCSensor> cses=new ArrayList<CCSensor>();
		
		//判断是否是alarmSensor
		for(int i=0;i<this.alarmSensors.size();i++){
			AlarmSensor as=this.alarmSensors.get(i);
			if(as.getSensorId()==sensorId){//该sensor是alarmSensor
				removeAlarmSensor(sensorId, i, ases);
				for(AlarmSensor item:ases){
					this.alarmSensors.remove(item);
				}
				return;
			}
		}
				
		//判断是否是ccSensor
		for(int i=0;i<this.CCSensors.size();i++){
			CCSensor cs=this.CCSensors.get(i);
			if(cs.getSensorId()==sensorId){//该sensor是CCSensor
				removeCCSensor(sensorId, i, ases, cses);
				
				for(AlarmSensor item:ases){
					this.alarmSensors.remove(item);
				}
				
				for(CCSensor item:cses){
					this.CCSensors.remove(item);
				}
				return;
			}
		}
		//普通Sensor
		_removeSensor(sensorId, ases, cses);
		for(AlarmSensor item:ases){
			this.alarmSensors.remove(item);
		}
		
		for(CCSensor item:cses){
			this.CCSensors.remove(item);
		}
	}
	/**
	 * alarmSensor,ccSensor都可以依赖于Sensor，所以在移除其同时要移除依赖于它的alarmSensor,ccSensor
	 * @param sensorId
	 * @param ases
	 * @param cses
	 */
	private void _removeSensor(long sensorId,List<AlarmSensor> ases,List<CCSensor> cses){
		this.SensorFragments.remove(sensorId);
		//System.out.println("normal:"+sensorId);
		//移除依赖于它的alarmSensor
		for(int i=0;i<this.alarmSensors.size();i++){
			AlarmSensor as=this.alarmSensors.get(i);
			for(long sId:as.getSensorIds()){
				if(sId==sensorId){
					removeAlarmSensor(as.getSensorId(),i,ases);
					break;
				}
			}	
		}
		
		//移除依赖于它的ccSensor
		for(int i=0;i<this.CCSensors.size();i++){
			CCSensor cs=this.CCSensors.get(i);
			for(long sId:cs.getSensorIds()){
				if(sId==sensorId){
					removeCCSensor(cs.getSensorId(),i,ases,cses);
					break;
				}
			}	
		}
	}
	/**
	 * ccSensor可以依赖于ccSensor,Sensor,所以在移除其同时要移除依赖于它的alarmSensor,ccSensor
	 * @param sensorId
	 * @param index
	 * @param ases 要移除的alarmSensor
	 * @param cses 要移除的ccSensor
	 */
	private void removeCCSensor(long sensorId,int index,List<AlarmSensor> ases,List<CCSensor> cses){
		this.SensorFragments.remove(sensorId);
		if(!cses.contains(this.CCSensors.get(index))){
			cses.add(this.CCSensors.get(index));
			//System.out.println("cc:"+sensorId);
		}
		
		//移除依赖于它的alarmSensor
		for(int i=0;i<this.alarmSensors.size();i++){
			AlarmSensor as=this.alarmSensors.get(i);
			for(long sId:as.getSensorIds()){
				if(sId==sensorId){
					removeAlarmSensor(as.getSensorId(),i,ases);
					break;
				}
			}	
		}
		//移除依赖于它的ccSensor
		for(int i=0;i<this.CCSensors.size();i++){
			CCSensor cs=this.CCSensors.get(i);
			for(long sId:cs.getSensorIds()){
				if(sId==sensorId){
					removeCCSensor(cs.getSensorId(),i,ases,cses);
					break;
				}
			}	
		}
	}

	/**
	 * alarmSensor可以依赖于alarmSensor，ccSensor,Sensor,所以在移除其同时要移除依赖于它的alarmSensor
	 * @param sensorId
	 * @param index
	 * @param ases 要移除的alarmSensor
	 * @return 
	 */
	private List<AlarmSensor> removeAlarmSensor(long sensorId,int index,List<AlarmSensor> ases){
		this.SensorFragments.remove(sensorId);
		if(!ases.contains(this.alarmSensors.get(index))){
			ases.add(this.alarmSensors.get(index));
			//System.out.println("alarm:"+sensorId);
		}
		
		//移除依赖于它的alarmSensor
		for(int i=0;i<this.alarmSensors.size();i++){
			AlarmSensor as=this.alarmSensors.get(i);
			for(long sId:as.getSensorIds()){
				if(sId==sensorId){
					removeAlarmSensor(as.getSensorId(),i,ases);
					break;
				}
			}	
		}
		return ases;
	}
	/**
	 * 添加之前需要对所有的CCSensor做成环检测，如果有环存在则不应该添加
	 * @param ccSensor
	 * @see UnionFind 环检测
	 */
	public void addCCSensor(CCSensor ccSensor){
		this.CCSensors.add(ccSensor);
		addSensor(ccSensor.getSensorId());
	}
	/**
	 * 添加报警虚拟传感器
	 * @param alarmSensor
	 * @param alarmHandlers 警报处理Handler
	 */
	public void addAlarmSensor(AlarmSensor alarmSensor,List<IAlarmHandler> alarmHandlers){
		this.alarmSensors.add(alarmSensor);
		addSensorWithoutHandler(alarmSensor.getSensorId());
		//add alarm handler
		AlarmHandler ah=new AlarmHandler(alarmSensor, alarmHandlers);
		addHandler(alarmSensor.getSensorId(), ah, 0);
	}
	
	public void addAccumulateSensor(AccumulateSensor accumulateSensor){
		this.accumulateSensors.add(accumulateSensor);
		addSensor(accumulateSensor.getSensorId());
	}
	
	/**
	 * 添加用户自定义事件，处理基本统计数据
	 * @param sensorId
	 * @param handler
	 * @param depth 池的深度 0--根；1--child;2--child.child...
	 */
	public void addHandler(long sensorId,SensorHandler handler,int depth){
		if(isRoot){
			SensorValuePool svp=(SensorValuePool) this.SensorFragments.get(sensorId);
			if(depth==0){
				svp.addListener(handler);
			}
			else if(svp!=null){
				BSSensorPool bsp=svp.getBsp();
				while(depth>1&&bsp!=null){
					bsp=bsp.getChild();
					depth--;
				}
				bsp.addListener(handler);
			}
		}
		else{
			BSSensorPool bsp=(BSSensorPool) this.SensorFragments.get(sensorId);
			while(depth>0&&bsp!=null){
				bsp=bsp.getChild();
				depth--;
			}
			bsp.addListener(handler);
		}
	}
	/**
	 * 为所有的sensor添加用户自定义事件，处理基本统计数据
	 * @param handler
	 * @param depth 池的深度 0--根；1--child;2--child.child...
	 */
	public void addHandler4All(SensorHandler handler,int depth){
		Iterator<Entry<Long, CachePool<?>>> it=SensorFragments.entrySet().iterator();
		int depthit=depth;
		while(it.hasNext()){
			depthit=depth;
			Entry<Long, CachePool<?>> entry=it.next();
			addHandler(entry.getKey(), handler, depthit);
		}
	}
	/**
	 * 获取从time之后的size个数据
	 * @param sensorId
	 * @param time
	 * @param size
	 * @param depth 
	 * @return
	 */
	public List<BSSensor> getBSValue(long sensorId,long time,int size,int depth){
		BSSensorPool pool=(BSSensorPool) getCachePool(sensorId, depth);
		return pool.getSensorValuesAfter(time, size);
	}
	/**
	 * 获取从time到当前时刻的统计值
	 * @param sensorId
	 * @param time
	 * @param depth
	 * @return
	 */
	public List<BSSensor> getBSValue(long sensorId,long time,int depth){
		BSSensorPool pool=(BSSensorPool) getCachePool(sensorId, depth);
		return pool.getSensorValuesAfter(time);
	}
	/**
	 * 获取当前时刻的统计值
	 * @param sensorId
	 * @param depth
	 * @return
	 */
	public BSSensor getCurrentBSValue(long sensorId,int depth){
		BSSensorPool pool=(BSSensorPool) getCachePool(sensorId, depth);
		return pool.getCurrentValue();
	}
	/**
	 * 获取从time之后的size个数据，在调用该方法之前请确保是该缓存池是包含原始数据的
	 * @param sensorId
	 * @param time
	 * @param size
	 * @return
	 */
	public List<Double> getSensorValue(long sensorId,long time,int size){
		SensorValuePool pool=(SensorValuePool) SensorFragments.get(sensorId);
		return pool.getSensorValuesAfter(time, size);
	}
	/**
	 * 获取从time到当前时刻的数据，在调用该方法之前请确保是该缓存池是包含原始数据的
	 * @param sensorId
	 * @param time
	 * @return
	 */
	public List<Double> getSensorValue(long sensorId,long time){
		SensorValuePool pool=(SensorValuePool) SensorFragments.get(sensorId);
		return pool.getSensorValuesAfter(time);
	}
	/**
	 * 获取当前时刻的原始数据
	 * @param sensorId
	 * @return
	 */
	public double getCurrentSensorValue(long sensorId){
		SensorValuePool pool=(SensorValuePool) SensorFragments.get(sensorId);
		return pool.getCurrentValue();
	}
	
	
	/**
	 * 获取sensor池
	 * @param sensorId
	 * @param depth 池的深度 0--根；1--child;2--child.child...
	 * @return
	 */
	public CachePool<?> getCachePool(long sensorId,int depth){
		if(isRoot){
			SensorValuePool svp=(SensorValuePool) this.SensorFragments.get(sensorId);
			if(depth==0){
				return svp;
			}
			else if(svp!=null){
				BSSensorPool bsp=svp.getBsp();
				while(depth>1&&bsp!=null){
					bsp=bsp.getChild();
					depth--;
				}
				return bsp;
			}
		}
		else{
			BSSensorPool bsp=(BSSensorPool) this.SensorFragments.get(sensorId);
			while(depth>0&&bsp!=null){
				bsp=bsp.getChild();
				depth--;
			}
			return bsp;
		}
		return null;
	}
	/**
	 * 获取池的深度
	 * @return
	 */
	public int getDepth(long sensorId){
		if(isRoot){
			SensorValuePool pool=(SensorValuePool) this.SensorFragments.get(sensorId);
			return pool.getBsp().getDepth()+1;
		}
		else{
			BSSensorPool pool=(BSSensorPool) this.SensorFragments.get(sensorId);
			return pool.getDepth();
		}
	}
	public boolean isCCTroggleState() {
		return CCTroggleState;
	}
	/**
	 * 设置CC处理开关
	 * @param cCTroggleState
	 */
	public void setCCTroggleState(boolean cCTroggleState) {
		CCTroggleState = cCTroggleState;
	}
	
	public List<CCSensor> getCCSensors() {
		return CCSensors;
	}
	public void setCCSensors(List<CCSensor> cCSensors) {
		CCSensors = cCSensors;
	}

	/**
	 * 设置警报处理开关
	 * @param cCTroggleState
	 */
	public void setAlarmTroggleState(boolean alarmTroggleState) {
		this.alarmTroggleState = alarmTroggleState;
	}
	public Map<Long, CachePool<?>> getSensorFragments() {
		return SensorFragments;
	}

	public void setSensorFragments(Map<Long, CachePool<?>> sensorFragments) {
		SensorFragments = sensorFragments;
	}
	/**
	 * 获取配置信息（但不能修改该配置信息,包括移动指针，否则在同时添加缓存sensor和获取sensor数据的时候可能出错）
	 * @return
	 * @see #getCptcClone()
	 */
	public final CachePoolTreeCfg getCptc() {
		return cptc;
	}
	/**
	 * 获取配置信息（可修改配置信息）
	 * @return
	 * @see BaseMemoryCache#getCptc()
	 */
	public CachePoolTreeCfg getCptcClone() {
		return cptc.clone();
	}
	
	public void setCptc(CachePoolTreeCfg cptc) {
		this.cptc = cptc;
	}
	/**
	 * 根节点是否是原始数据池(否则是基本统计池)
	 * @return
	 */
	public boolean isRoot() {
		return isRoot;
	}
	/**
	 * 获取根节点的配置信息
	 * @return
	 */
	public CacheBlockCfg getCachePoolCfg() {
		CacheBlockCfg cbc=new CacheBlockCfg(this.cptc.getBlockIntervalInMs(), this.cptc.getSize(), this.cptc.getfileHandler());
		return cbc;
	}
	
}
