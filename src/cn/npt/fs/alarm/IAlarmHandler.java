package cn.npt.fs.alarm;
/**
 * 警报处理接口
 * @author Leonardo
 *
 */
public interface IAlarmHandler {

	/**
	 * 处理警报
	 * @param sensorAlarmPack 警报信息
	 */
	public void handlerAlarm(SensorAlarmPack sensorAlarmPack);
}
