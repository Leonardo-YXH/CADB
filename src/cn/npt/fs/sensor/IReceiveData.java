package cn.npt.fs.sensor;

import java.util.Map;

public interface IReceiveData {
	/**
	 * 接收传感器的数据
	 */
	public Map<Long,Double> read();
}
