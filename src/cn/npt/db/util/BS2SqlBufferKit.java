package cn.npt.db.util;
/**
 * 
 * @author Leonardo
 * @see BaseBS2SqlBufferKit
 */
@Deprecated
public class BS2SqlBufferKit {
	private static StringBuffer sb=new StringBuffer();
	private static StringBuffer sb2=new StringBuffer();
	private static String header="insert into iot_sensor_inminute(sensorId,createdTime,startV,endV,maxV,maxTime,minV,minTime,avgV,sdV) values";
	private static String header2="insert into iot_sensor_intenminute(sensorId,createdTime,startV,endV,maxV,maxTime,minV,minTime,avgV,sdV) values";
	private static int size=0;
	private static int size2=0;
	//private static Logger log=Logger.getLogger(BS2SqlBufferKit.class);
	/**
	 * 1min的统计结果
	 * @param sql_bs
	 */
	public static synchronized void addBS(StringBuilder sql_bs){
		sb.append(sql_bs);
		
		size++;
		if(size>=1000){
			String sql=header+sb.substring(0, sb.length()-1);
			NptDBUtil.update(sql);
			//log.info(sql);
			sb=new StringBuffer();//置空
			size-=1000;
		}
	}
	/**
	 * 10min的统计结果
	 * @param sql_bs
	 */
	public static synchronized void addBS_2(StringBuilder sql_bs){
		sb2.append(sql_bs);
		
		size2++;
		if(size2>=1000){
			String sql=header2+sb2.substring(0, sb2.length()-1);
			NptDBUtil.update(sql);
			//log.info(sql);
			sb2=new StringBuffer();//置空
			size2-=1000;
		}
	}
}
