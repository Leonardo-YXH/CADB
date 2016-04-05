package cn.npt.fs.event;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import cn.npt.fs.CacheCfg;
import cn.npt.fs.cache.BSSensorPool;
import cn.npt.fs.cache.CachePool;
import cn.npt.util.math.SensorFileKit;
/**
 * @deprecated
 * @author Administrator
 *
 */
public class SaveFirstBSHandler extends SensorHandler {

	private static Logger log = Logger.getLogger(SaveFirstBSHandler.class);
	@Override
	public <T extends CachePool<?>> void execute(T fragment,int index,long currentTime) {
		BSSensorPool bsp=(BSSensorPool) fragment;
		int blockSize=bsp.getCbCfg().getPersistenceSize();
		int toIndex=index+1;
		if(toIndex%blockSize==0){
			int timeInterval=blockSize/60;
			//log.info("SaveFirstBS sensorId:"+bsp.getSensorId()+"  timeInterval:"+bsp.getIndex()+"  timeInterval:"+timeInterval);
			String fileName=CacheCfg.dataDir+"/"+SensorFileKit.getFileNameBySensorId(bsp.getSensorId())
					+SensorFileKit.getFileNameByTime(bsp.getCurrentTime(), timeInterval,TimeUnit.MINUTES)+"/sensor.json";
			if(!SensorFileKit.writeFirstBS(bsp.getValues(), toIndex-blockSize, toIndex, fileName)){
				log.info("save "+fileName+" failed!");
			};
		}
	}

}
