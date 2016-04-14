package cn.npt.fs.alarm;

import java.util.List;

/**
 * 阈值比较接口
 * @author Leonardo
 *
 */
public interface IThresholdValue<T extends Number&Comparable<T>>  {
	/**
	 * 执行阈值比较，返回警报等级
	 * @param values 传感器的值
	 * @return 0表示正常，负数表示低警，正数表示高警
	 */
	public int compare(List<T> values);
}
