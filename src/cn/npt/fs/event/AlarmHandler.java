package cn.npt.fs.event;

import java.util.List;

import cn.npt.fs.CachePoolFactory;
import cn.npt.fs.alarm.IAlarmHandler;
import cn.npt.fs.alarm.SensorAlarmPack;
import cn.npt.fs.bean.AlarmSensor;
import cn.npt.fs.cache.CachePool;
import cn.npt.fs.cache.SensorValuePool;
/**
 * 警报事件
 * @author Leonardo
 *
 */
public class AlarmHandler extends SensorHandler {

//	/**
//	 * 监控的传感器ID
//	 */
//	private long sensorId;
//	/**
//	 * 传感器设备信息
//	 */
//	private String sensorInfo;
//	/**
//	 * 警报持续时间多长才报警
//	 */
//	private long duration;
	/**
	 * 警报传感器信息
	 */
	private AlarmSensor alarmSensor;
	/**
	 * 警报包信息
	 */
	private SensorAlarmPack sensorAlarmPack;
	/**
	 * 警报处理
	 */
	private List<IAlarmHandler> alarmHandlers;
	
	public AlarmHandler(AlarmSensor alarmSensor, List<IAlarmHandler> alarmHandlers) {
		this.alarmSensor=alarmSensor;
		this.sensorAlarmPack=null;
		
		this.alarmHandlers=alarmHandlers;
	}
	
	@Override
	public <T extends CachePool<?>> void execute(T fragment, int index,
			long currentTime) {
		SensorValuePool svp=(SensorValuePool)fragment;
		int alarmLevel=svp.getValues().get(index).intValue();
		if(alarmLevel!=0){//非正常值
			if(this.alarmSensor.getDuration()==0){//非持续性警报
				this.sensorAlarmPack=new SensorAlarmPack(svp.getSensorId());
				this.sensorAlarmPack.setStartTime(currentTime);
				this.sensorAlarmPack.setEndTime(currentTime);
				this.sensorAlarmPack.setMaxAlarmLevel(alarmLevel);
				long sensorId=this.alarmSensor.getSensorIds().get(this.alarmSensor.getSensorIds().size()-1);
				double maxValue=(double) CachePoolFactory.getCachePool(sensorId).getSensorFragments().get(sensorId).getValues().get(index);
				this.sensorAlarmPack.setMaxValue(maxValue);
				this.sensorAlarmPack.setAlarmInfo(this.alarmSensor.getSensorInfo());
				
				//处理警报
				handler();
			}
			else{
				if(this.sensorAlarmPack==null){//开始警报
					this.sensorAlarmPack=new SensorAlarmPack(svp.getSensorId());
					this.sensorAlarmPack.setStartTime(currentTime);
					this.sensorAlarmPack.setMaxAlarmLevel(alarmLevel);
					long sensorId=this.alarmSensor.getSensorIds().get(this.alarmSensor.getSensorIds().size()-1);
					double maxValue=(double) CachePoolFactory.getCachePool(sensorId).getSensorFragments().get(sensorId).getValues().get(index);
					this.sensorAlarmPack.setMaxValue(maxValue);
					this.sensorAlarmPack.setAlarmInfo(this.alarmSensor.getSensorInfo());
					this.sensorAlarmPack.setNew(true);
				}
				else{//持续警报
					this.sensorAlarmPack.setEndTime(currentTime);
					if(Math.abs(alarmLevel)>Math.abs(this.sensorAlarmPack.getMaxAlarmLevel())){//升级警报等级(|-2|>|+1|)
						this.sensorAlarmPack.setMaxAlarmLevel(alarmLevel);
						long sensorId=this.alarmSensor.getSensorIds().get(this.alarmSensor.getSensorIds().size()-1);
						double maxValue=(double) CachePoolFactory.getCachePool(sensorId).getSensorFragments().get(sensorId).getValues().get(index);
						this.sensorAlarmPack.setMaxValue(maxValue);
					}
					if(currentTime-this.sensorAlarmPack.getStartTime()>=this.alarmSensor.getDuration()){//确认为一个警报
						
						//处理警报
						handler();
						
						//处理后将警报设置为历史警报
						this.sensorAlarmPack.setNew(false);
					}
				}
			}
		}
		else{//解除警报
//			if(this.sensorAlarmPack!=null){//处理上次警报
//				handler();
//			}
			this.sensorAlarmPack=null;
		}
	}

	private void handler(){
		for(IAlarmHandler ah:this.alarmHandlers){
			ah.handlerAlarm(this.sensorAlarmPack);
		}
	}
}
