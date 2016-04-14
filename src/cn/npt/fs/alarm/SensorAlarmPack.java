package cn.npt.fs.alarm;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 警报阈值
 * @author Leonardo
 *
 */
public class SensorAlarmPack {
	/**
	 * 警报ID
	 */
	private long sensorId;
	/**
	 * 警报开始时间
	 */
	private long startTime;
	/**
	 * 警报结束时间
	 */
	private long endTime;
	/**
	 * 最大警报等级
	 */
	private int maxAlarmLevel;
	/**
	 * 达到的极限值
	 */
	private double maxValue;
	/**
	 * 是否为新产生的警报
	 */
	private boolean isNew;
	/**
	 * 警报详情
	 */
	private String alarmInfo;
	public SensorAlarmPack(long sensorId) {
		this.sensorId=sensorId;
		this.isNew=true;
	}
	
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	
	public int getMaxAlarmLevel() {
		return maxAlarmLevel;
	}
	public void setMaxAlarmLevel(int maxAlarmLevel) {
		this.maxAlarmLevel = maxAlarmLevel;
	}
	public String getAlarmInfo() {
		return alarmInfo;
	}
	public void setAlarmInfo(String alarmInfo) {
		this.alarmInfo = alarmInfo;
	}

	public long getSensorId() {
		return sensorId;
	}

	public void setSensorId(long sensorId) {
		this.sensorId = sensorId;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}
	
	public double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}

	/**
	 * 获取邮件标题
	 * @return
	 */
	public String getTitle(){
		
		return "#"+this.alarmInfo+this.maxAlarmLevel+"级警报#";
	}
	/**
	 * 获取邮件内容
	 * @return
	 */
	public String getContent(){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		StringBuilder sb=new StringBuilder();
		sb.append("<p>故障设备传感器：").append(this.alarmInfo).append("</p>")
			.append("<p>警报开始时间：").append(sdf.format(new Date(this.startTime))).append("</p>")
			.append("<p style='color:red;'>警报等级：").append(this.maxAlarmLevel).append("</p>")
			.append("<p style='color:red;'>警报峰值：").append(this.maxValue).append("</p>")
			;
		return sb.toString();
	}
}
