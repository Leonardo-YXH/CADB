package cn.npt.db.util;

import org.apache.log4j.Logger;

/**
 * 降低数据库插入语句的频率
 * @author Leonardo
 *
 */
public class BaseBS2SqlBufferKit {

	private int size;
	private int count;
	private String head;
	private StringBuffer sb;
	
	private static Logger log=Logger.getLogger(BaseBS2SqlBufferKit.class);
	
	public BaseBS2SqlBufferKit(String head,int size){
		this.head=head;
		this.size=size;
		this.count=0;
		this.sb=new StringBuffer();
	}
	/**
	 * 添加一条记录
	 * @param sqlValue 格式:  (sensorId,createdTime,startV,endV,maxV,maxTime,minV,minTime,avgV,sdV),   切勿忘记最后的逗号
	 */
	public synchronized void addSqlValue(StringBuilder sqlValue){
		this.sb.append(sqlValue);
		this.count++;
		if(this.count>=this.size){
			String sql=this.head+sb.substring(0, sb.length()-1);
			if(-1==NptDBUtil.update(sql)){
				log.error("保存统计数据失败!");
			};
			sb=new StringBuffer();//置空
			this.count=0;
		}
	}
}
