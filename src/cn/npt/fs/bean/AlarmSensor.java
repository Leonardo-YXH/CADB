package cn.npt.fs.bean;

import java.util.List;

import cn.npt.fs.alarm.IThresholdValue;

/**
 * 报警传感器
 * @author Leonardo
 *
 */
public class AlarmSensor {

	/**
	 * 虚拟的传感器Id
	 */
	private long sensorId;
	/**
	 * 依赖的传感器,最后一个即为监控的传感器id,前面的N-1个为条件判断项
	 */
	private List<Long> sensorIds;
	/**
	 * 持续时间
	 */
	private long duration;
	/**
	 * 条件表达式
	 */
	private IThresholdValue<Double> thresholdCondition;
	/**
	 * 传感器信息
	 */
	private String sensorInfo;
	
	public AlarmSensor(long sensorId, List<Long> sensorIds,long duration,
			IThresholdValue<Double> thresholdCondition,String sensorInfo) {
		this.sensorId = sensorId;
		this.sensorIds = sensorIds;
		this.sensorInfo=sensorInfo;
		this.duration=duration;
		this.thresholdCondition = thresholdCondition;
	}
	
	public double doCheck(List<Double> values){
		return this.thresholdCondition.compare(values);
	}

	public long getSensorId() {
		return sensorId;
	}

	public void setSensorId(long sensorId) {
		this.sensorId = sensorId;
	}

	public List<Long> getSensorIds() {
		return sensorIds;
	}

	public void setSensorIds(List<Long> sensorIds) {
		this.sensorIds = sensorIds;
	}

	public IThresholdValue<Double> getThresholdCondition() {
		return thresholdCondition;
	}

	public void setThresholdCondition(IThresholdValue<Double> thresholdCondition) {
		this.thresholdCondition = thresholdCondition;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getSensorInfo() {
		return sensorInfo;
	}

	public void setSensorInfo(String sensorInfo) {
		this.sensorInfo = sensorInfo;
	}
	
	
}
