package cn.npt.db.event;

import java.util.List;

import cn.npt.db.util.BS2SqlBufferKit;
import cn.npt.fs.bean.BSSensor;
import cn.npt.fs.cache.BSSensorPool;
import cn.npt.fs.cache.CachePool;
import cn.npt.fs.event.SensorHandler;
/**
 * 保存10min的统计结果到数据库
 * @author Administrator
 *
 */
@SuppressWarnings("deprecation")
public class SaveSecordBS2DBHandler extends SensorHandler {

	@Override
	public <T extends CachePool<?>> void execute(T fragment, int index,
			long currentTime) {
		BSSensorPool bsp=(BSSensorPool) fragment;
		int blockSize=bsp.getCbCfg().getPersistenceSize();
		int toIndex=index+1;
		if(toIndex%blockSize==0){
			StringBuilder sb=new StringBuilder();
			List<BSSensor> bses=bsp.getValues();
			for(int i=toIndex-blockSize;i<toIndex;i++){
				BSSensor bs=bses.get(i);
				if(bs!=null){
					sb.append("(").append(bsp.getSensorId()).append(",")
						.append(bs.toSqlInsert())
						.append("),");
					BS2SqlBufferKit.addBS_2(sb);
				}
				
			}
		}
	}

}
