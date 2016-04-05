package cn.npt.fs.event;

import cn.npt.db.event.BaseBS2DBHandler;
import cn.npt.fs.bean.BSSensor;
import cn.npt.fs.cache.BSSensorPool;
import cn.npt.fs.cache.CachePool;
/**
 * 统计下一层的BS,除了root级不一样，从第二层开始后面的池都可以通用
 * @author Leonardo
 * @see FirstBSHandler
 * @see BaseBS2DBHandler
 */
public class BaseBSHandler extends SensorHandler {

	@Override
	public <T extends CachePool<?>> void execute(T fragment,int index,long currentTime) {
		BSSensorPool root=(BSSensorPool)fragment;
		int blockSize=(int) (root.getChild().getBlockInterval()/root.getBlockInterval());
		int toIndex=index+1;
		if(toIndex%blockSize==0){
			BSSensorPool bsp=root.getChild();
			BSSensor bss=new BSSensor(root.getValues().subList(toIndex-blockSize, toIndex));
			//bss.print();
			bsp.setValue(bss, currentTime);
		}

	}

}
