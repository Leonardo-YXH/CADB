package cn.npt.fs.event;

import cn.npt.fs.bean.BSSensor;
import cn.npt.fs.cache.BSSensorPool;
import cn.npt.fs.cache.CachePool;
/**
 * 1day of 2year BS
 * @author Administrator
 *
 */
@Deprecated
public class ThirdBSHandler extends SensorHandler {

	@Override
	public <T extends CachePool<?>> void execute(T fragment, int index,
			long currentTime) {
		BSSensorPool root=(BSSensorPool)fragment;
		int blockSize=(int) (root.getChild().getBlockInterval()/root.getBlockInterval());
		int toIndex=index+1;
		if(toIndex%blockSize==0){
			BSSensorPool bsp=root.getChild();
			BSSensor bss=new BSSensor(root.getValues().subList(toIndex-blockSize, toIndex));
			bsp.setValue(bss, currentTime);
		}

	}

}
