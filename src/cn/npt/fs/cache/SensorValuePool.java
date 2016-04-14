package cn.npt.fs.cache;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

import cn.npt.fs.config.CacheBlockCfg;
import cn.npt.fs.event.FirstBSHandler;
import cn.npt.fs.service.SensorValueFileService;
import cn.npt.util.math.SensorFileKit;
/**
 * 原始数据缓存池
 * @author Leonardo
 *
 */
public class SensorValuePool extends CachePool<Double> {
	
	private long sensorId;
	private BSSensorPool bsp;
	private CacheBlockCfg cbCfg;
	private String dataDir;
	public SensorValuePool(long sensorId,CacheBlockCfg cbCfg,String dataDir) {
		super(cbCfg.size, cbCfg.blockInterval);
		this.sensorId=sensorId;
		this.cbCfg=cbCfg;
		this.dataDir=dataDir;
	}
	
	public long getSensorId() {
		return sensorId;
	}

	public void setSensorId(long sensorId) {
		this.sensorId = sensorId;
	}

	public BSSensorPool getBsp() {
		return bsp;
	}

	public void setBsp(BSSensorPool bsp) {
		this.bsp = bsp;
		FirstBSHandler handler=new FirstBSHandler();
		this.addListener(handler);
	}

	public CacheBlockCfg getCbCfg() {
		return cbCfg;
	}

	public void setCbCfg(CacheBlockCfg cbCfg) {
		this.cbCfg = cbCfg;
	}

	public String getDataDir() {
		return dataDir;
	}

	public void setDataDir(String dataDir) {
		this.dataDir = dataDir;
	}

	/**
	 * 从文件中读取恢复数据，大小为一个存储文件的大小
	 * @param time
	 * @return
	 */
	public List<Double> preDataFromDB(long time){
		String fileName=dataDir+SensorFileKit.getFileNameBySensorId(sensorId)
				+SensorFileKit.getFileNameByTime(time, cbCfg.getCapacity(),cbCfg.timeUnit)
				+"/sensor.dat";
		
		return SensorFileKit.read(fileName);
	}
	/**
	 * 
	 */
	public void afterReStart(){
		long previousTime=currentTime-this.blockInterval*this.size;
		List<Double> preValues=SensorValueFileService.getSensorValue(sensorId, previousTime, size);
		for(Double v:preValues){
			if(this.index>=this.size){
				this.index=0;
			}
			this.values.set(index, v);
			this.index++;
		}
	}
	@Override
	public void setCase2(Object paras) {
		
		
	}
	@Override
	public void setCase3(Object paras) {
		
		
	}
	@Override
	public void setCase4(Object paras) {
		
		
	}
	@Override
	public JSONObject currentV2JSON() {
		JSONObject obj=new JSONObject();
		obj.put(String.valueOf(this.sensorId), this.getCurrentValue());
		return obj;
	}
	
}
