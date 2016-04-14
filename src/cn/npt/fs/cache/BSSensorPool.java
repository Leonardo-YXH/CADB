package cn.npt.fs.cache;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.npt.db.event.BaseBS2DBHandler;
import cn.npt.fs.bean.BSSensor;
import cn.npt.fs.config.CacheBlockCfg;
import cn.npt.fs.config.CachePoolTreeCfg;
import cn.npt.fs.event.BaseBSHandler;

public class BSSensorPool extends CachePool<BSSensor> {
	/**
	 * 累计多少条之后再插入数据库
	 */
	public static int oneInsertSize=5000;
	private long sensorId;
	private BSSensorPool child;
	private CacheBlockCfg cbCfg;
	public BSSensorPool(long sensorId,CacheBlockCfg cbCfg) {
		super(cbCfg.size,cbCfg.blockInterval);
		this.sensorId=sensorId;
		this.cbCfg=cbCfg;
		this.child=null;
	}
	public BSSensorPool(long sensorId,CachePoolTreeCfg cptc){
		super(cptc.getSize(), cptc.getBlockIntervalInMs());
		this.cbCfg=new CacheBlockCfg(cptc.getBlockIntervalInMs(), cptc.getSize(), -1);//
		this.sensorId=sensorId;
		addSqlHandler(cptc.getSqlHandlers());
		if(cptc.hasChild()){
			cptc.next();
			this.setChild(new BSSensorPool(sensorId, cptc));
		}
	}
	private void addSqlHandler(JSONArray sqlHandlers){
		for(int i=0;i<sqlHandlers.size();i++){
			JSONObject item=sqlHandlers.getJSONObject(i);
			BaseBS2DBHandler handler=new BaseBS2DBHandler(item.getString("table"), oneInsertSize, item.getIntValue("blockSize"), item.getIntValue("offset"));
			this.addListener(handler);
		}
	}
	/**
	 * 
	 * @return
	 */
	@Deprecated
	public JSONObject getHandlerTree(){
		JSONObject rs=new JSONObject();
		
		
		return rs;
	}
	public void setValue(BSSensor value,long time){
		super.setValue(value, time);
	}
	public BSSensorPool getChild() {
		return child;
	}
	public void setChild(BSSensorPool child) {
		this.child = child;
		BaseBSHandler handler=new BaseBSHandler();
		this.addListener(handler);
	}
	public long getSensorId() {
		return sensorId;
	}

	public void setSensorId(long sensorId) {
		this.sensorId = sensorId;
	}

	public CacheBlockCfg getCbCfg() {
		return cbCfg;
	}
	public void setCbCfg(CacheBlockCfg cbCfg) {
		this.cbCfg = cbCfg;
	}
	@Override
	public void afterReStart() {
		
	}

	@Override
	public void setCase2(Object paras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCase3(Object paras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCase4(Object paras) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public JSONObject currentV2JSON() {
		JSONObject obj=new JSONObject();
		obj.put(String.valueOf(this.sensorId), this.getCurrentValue());
		return obj;
	}
	/**
	 * 获取缓存池的深度
	 * <br>0--只有原始数据
	 * <br>1--有一层基本统计池
	 * <br>n--有n层基本统计池
	 * @return 缓存池的深度
	 */
	public int getDepth(){
		int depth=0;
		BSSensorPool bsp=this.child;
		while(bsp!=null){
			depth++;
			bsp=bsp.getChild();
		}
		return depth;
	}

}
