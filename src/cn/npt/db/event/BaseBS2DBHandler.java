package cn.npt.db.event;

import java.util.ArrayList;
import java.util.List;

import cn.npt.db.util.BS2SqlBufferKitFactory;
import cn.npt.db.util.BaseBS2SqlBufferKit;
import cn.npt.fs.bean.BSSensor;
import cn.npt.fs.cache.BSSensorPool;
import cn.npt.fs.cache.CachePool;
import cn.npt.fs.event.SensorHandler;
/**
 * 将基本统计的缓存数据保存到数据库
 * @author Leonardo
 *
 */
public class BaseBS2DBHandler extends SensorHandler {

	private BaseBS2SqlBufferKit bufferKit;
	private int blockSize;
	private int offset;
	/**
	 * 保存统计结果到数据库
	 * @param table 表名
	 * @param size 累计size条在插入到数据库
	 * @param blockSize 统计多少个元素
	 * 
	 */
	public BaseBS2DBHandler(String table,int size,int blockSize,int offset) {
		this.bufferKit=BS2SqlBufferKitFactory.create(table, size);
		this.blockSize=blockSize;
		this.offset=offset;
	}
	@Override
	public <T extends CachePool<?>> void execute(T fragment, int index,
			long currentTime) {
		BSSensorPool bsp=(BSSensorPool) fragment;
		int toIndex=index+1;
		if((toIndex+this.offset)%blockSize==0){
			int startIndex=toIndex-blockSize;
			List<BSSensor> bssp=new ArrayList<BSSensor>();
			if(startIndex<0){
				startIndex+=bsp.getSize();
				bssp.addAll(bsp.getValues().subList(startIndex, bsp.getSize()));
				bssp.addAll(bsp.getValues().subList(0, toIndex));
			}
			else{
				bssp.addAll(bsp.getValues().subList(startIndex, toIndex));
			}
			BSSensor bss=new BSSensor(bssp);
			StringBuilder sb=new StringBuilder();
			sb.append("(").append(bsp.getSensorId()).append(",")
				.append(bss.toSqlInsert())
				.append("),");
			this.bufferKit.addSqlValue(sb);
		}
	}
}
