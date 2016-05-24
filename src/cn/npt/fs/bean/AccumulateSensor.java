package cn.npt.fs.bean;

public class AccumulateSensor {
	/**
	 * 虚拟量程Id
	 */
	private long sensorId;
	/**
	 * 需要计算的目标Id
	 */
	private long srcSensorId;

	public AccumulateSensor(long sensorId, long srcSensorId) {
		this.sensorId = sensorId;
		this.srcSensorId = srcSensorId;
	}

	public long getSensorId() {
		return sensorId;
	}

	public void setSensorId(long sensorId) {
		this.sensorId = sensorId;
	}

	public long getSrcSensorId() {
		return srcSensorId;
	}

	public void setSrcSensorId(long srcSensorId) {
		this.srcSensorId = srcSensorId;
	}
	
	
}
