package cn.npt.net;
/**
 * server或client状态
 * @author Leonardo
 *
 */
public enum NPTChannelStatus {
	/**
	 * 未知错误
	 */
	ERROR,
	/**
	 * 未连接，处于关闭状态
	 */
	CLOSED,
	/**
	 * 连接中
	 */
	CONNECTING,
	/**
	 * 成功连接
	 */
	CONNECTED,
	/**
	 * 主动关闭
	 */
	CLOSED_INITIATIVE
	;
}
