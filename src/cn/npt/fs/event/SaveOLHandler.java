package cn.npt.fs.event;

import java.util.Date;

import org.apache.log4j.Logger;

import cn.npt.fs.cache.CachePool;
import cn.npt.fs.cache.SensorValuePool;
import cn.npt.fs.config.CacheBlockCfg;
import cn.npt.util.math.SensorFileKit;
/**
 * 
 * @author Leonardo
 *
 */
public class SaveOLHandler extends SensorHandler {
	
	private static Logger log=Logger.getLogger(SaveOLHandler.class);
	@Override
	public <T extends CachePool<?>> void execute(T data,int index,long currentTime) {
		SensorValuePool fragment=(SensorValuePool) data;
		int toIndex=index+1;
		CacheBlockCfg cfg=fragment.getCbCfg();
		int persistenceSize=cfg.persistenceSize;
		if(toIndex%persistenceSize==0){
			//System.out.println("current time:"+new Date(currentTime).toString());
			String fileName=fragment.getDataDir()+SensorFileKit.getFileNameBySensorId(fragment.getSensorId())
					+SensorFileKit.getFileNameByTime(currentTime, cfg.getCapacity(),cfg.timeUnit)+"/sensor.dat";
			
			if(SensorFileKit.write(fragment.getValues(), toIndex-persistenceSize, toIndex, fileName)){
				log.info("save "+fileName);
			}
			else{
				log.info("["+new Date().toString()+"]--save "+fileName+" failed!");
			}
		}
		
	}

}
