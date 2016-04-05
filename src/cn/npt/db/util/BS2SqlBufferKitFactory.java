package cn.npt.db.util;

import java.util.HashMap;
import java.util.Map;

public class BS2SqlBufferKitFactory {

	private static Map<String,BaseBS2SqlBufferKit> bufferKits=new HashMap<String, BaseBS2SqlBufferKit>();
	
	private BS2SqlBufferKitFactory(){
		
	}
	/**
	 * 
	 * @param table 表名
	 * @param size 累计size条再插入到数据库，默认为存储sensor点的个数
	 * @return
	 */
	public static BaseBS2SqlBufferKit create(String table,int size){
		BaseBS2SqlBufferKit bk=bufferKits.get(table);
		if(bk==null){
			String head="insert into "+table+"(sensorId,createdTime,startV,endV,maxV,maxTime,minV,minTime,avgV,sdV) values";
			bk=new BaseBS2SqlBufferKit(head, size);
			bufferKits.put(table, bk);
		}
		return bk;
	}
}
