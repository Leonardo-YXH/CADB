package cn.npt.fs.clear;
/**
 * 原始文件清理接口，包括压缩，备份，删除
 * @author Leonardo
 *
 */
public interface IClear {

	/**
	 * 执行清理动作，包括压缩，备份，删除
	 */
	public void clear();
}
