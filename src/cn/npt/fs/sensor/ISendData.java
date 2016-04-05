package cn.npt.fs.sensor;

public interface ISendData {
	/**
	 * 收集到传感器数据后对其后续的操作
	 */
	public void write();
}
