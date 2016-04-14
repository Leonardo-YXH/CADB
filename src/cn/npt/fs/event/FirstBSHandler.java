package cn.npt.fs.event;

import cn.npt.fs.bean.BSSensor;
import cn.npt.fs.cache.BSSensorPool;
import cn.npt.fs.cache.CachePool;
import cn.npt.fs.cache.SensorValuePool;
/**
 * 一阶基本统计，统计原始数据
 * @author Leonardo
 * @see BaseBSHandler
 */
public class FirstBSHandler extends SensorHandler {

	@Override
	public <T extends CachePool<?>> void execute(T fragment,int index,long currentTime) {
		
		SensorValuePool svp=(SensorValuePool)fragment;
		int blockSize=(int) (svp.getBsp().getBlockInterval()/svp.getBlockInterval());
		int toIndex=index+1;
		if(toIndex%blockSize==0){
			BSSensorPool bsp=svp.getBsp();
			BSSensor bss=new BSSensor(svp.getValues(), toIndex-blockSize, toIndex, currentTime, index, svp.getBlockInterval());
			bsp.setValue(bss, currentTime);
			//getValue虽然可以避免new BSSensor的开销，但是不会执行setValue的事件
			//bsp.getValue(svp.getCurrentTime())
				//.compute(svp.getValues(), toIndex-blockSize, toIndex, svp.getCurrentTime(), svp.getIndex());
		}
	}

}
