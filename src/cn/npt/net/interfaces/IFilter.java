package cn.npt.net.interfaces;

import java.util.List;

/**
 * 数据过滤,转换接口
 * @author Leonardo
 *
 */
public interface IFilter {

	/**
	 * 过滤数据
	 * @param src
	 * @return
	 */
	public <T> T filter(List<T> src);
}
