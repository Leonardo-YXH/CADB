package cn.npt.util.math;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import cn.npt.fs.bean.BSSensor;
import cn.npt.util.data.FileOperator;

import com.alibaba.fastjson.JSON;
/**
 * sensorvalue文件读写
 * @author Administrator
 *
 */
public class SensorFileKit {
	private static Logger log=Logger.getLogger(SensorFileKit.class);
	public static boolean write(List<Double> data,String fileName){
		try {
			DataOutputStream out=new DataOutputStream(new BufferedOutputStream(new FileOutputStream(FileOperator.createFile(fileName))));
			for(double v:data){
				out.writeDouble(v);
			}
			out.close();
			return true;
		} catch (FileNotFoundException e) {
			log.info(e.getMessage());
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			log.info(e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * 
	 * @param data
	 * @param fromIndex
	 * @param toIndex
	 * @param fileName
	 * @return
	 */
	public static boolean write(List<Double> data,int fromIndex,int toIndex,String fileName){
		try {
			DataOutputStream out=new DataOutputStream(new BufferedOutputStream(new FileOutputStream(FileOperator.createFile(fileName))));
			for(int i=fromIndex;i<toIndex;i++){
				out.writeDouble(data.get(i));
			}
			out.close();
			return true;
		} catch (FileNotFoundException e) {
			log.info(e.getMessage());
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			log.info(e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	public static List<Double> read(String fileName){
		List<Double> rs=new ArrayList<Double>();
		File file=new File(fileName);
		if(!file.exists()){
			return rs;
		}
		try {
			DataInputStream in=new DataInputStream(new BufferedInputStream(new FileInputStream(fileName)));
			while (in.available()>0) {
				rs.add(in.readDouble());
			}
			in.close();
		} catch (IOException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return rs;
	}

	/**
	 * 将time转成日期格式的目录(其所在的10minute的文件名)
	 * @param time
	 * @param capacity 以minute为单位
	 * @return 2015-9-10 11:2x:xx=>/2015/9/10/11/20
	 */
	@SuppressWarnings("deprecation")
	public static String getFileNameByTime(long time,int capacity,TimeUnit timeUnit){
		Date date=new Date(time);
		StringBuilder sb=new StringBuilder();
		sb.append("/").append((date.getYear()+1900))
			.append("/").append((date.getMonth()+1));
		if(timeUnit.equals(TimeUnit.DAYS)){
			int day=date.getDate();
			day-=day%capacity;
			sb.append("/").append(day);
		}
		else if(timeUnit.equals(TimeUnit.HOURS)){
			int hour=date.getHours();
			hour-=hour%capacity;
			sb.append("/").append(date.getDate())
				.append("/").append(hour);
		}
		else if(timeUnit.equals(TimeUnit.MINUTES)){
			int minute=date.getMinutes();
			minute-=minute%capacity;
			sb.append("/").append(date.getDate())
			.append("/").append(date.getHours())
			.append("/").append(minute);
		}
		else if(timeUnit.equals(TimeUnit.SECONDS)){
			int second=date.getSeconds();
			second-=second%capacity;
			sb.append("/").append(date.getDate())
			.append("/").append(date.getHours())
			.append("/").append(date.getMinutes())
			.append("/").append(second);
		}
		return sb.toString();
	}
	/**
	 * 获取time时刻在文件中的索引位置
	 * @param time
	 * @param capacity
	 * @return
	 */
	@Deprecated
	public static int getIndexAtFile(long time,int capacity,long blockInterval){
		Date date=new Date(time);
		int minute=date.getMinutes();
		return (int) ((minute%capacity*60+date.getSeconds())*1000/blockInterval);
	}
	/**
	 * 获取time时刻在文件中的索引位置
	 * @param time
	 * @param persistenceSize
	 * @param blockInterval
	 * @return
	 */
	public static int getIndexAtFileFinal(long time,int persistenceSize,long blockInterval){
		return (int)((time%(persistenceSize*blockInterval))/blockInterval);
	}
	/**
	 * 
	 * @param sensorId
	 * @return 0x0102030405060708=>/1/2/3/4/5/6/7/8
	 */
	public static String getFileNameBySensorId(long sensorId){
		StringBuilder sb=new StringBuilder();
		sb.append("/").append(int2Hex(sensorId>>>56&0xff))
			.append("/").append(int2Hex(sensorId>>>48&0xff))
			.append("/").append(int2Hex(sensorId>>>40&0xff))
			.append("/").append(int2Hex(sensorId>>>32&0xff))
			.append("/").append(int2Hex(sensorId>>>24&0xff))
			.append("/").append(int2Hex(sensorId>>>16&0xff))
			.append("/").append(int2Hex(sensorId>>>8&0xff))
			.append("/").append(int2Hex(sensorId&0xff));
		return sb.toString();
//		return "/"+(sensorId>>>56&0xff)+"/"+(sensorId>>>48&0xff)+"/"+(sensorId>>>40&0xff)+"/"+(sensorId>>>32&0xff)+"/"
//				+(sensorId>>>24&0xff)+"/"+(sensorId>>>16&0xff)+"/"+(sensorId>>>8&0xff)+"/"+(sensorId&0xff);
	}
	public static boolean writeFirstBS(List<BSSensor> bses,int fromIndex,int toIndex,String fileName){
		List<String> content=new ArrayList<String>();
		content.add(JSON.toJSONString(bses.subList(fromIndex, toIndex)));
		FileOperator.createFile(fileName);
		try {
			Files.write(Paths.get(fileName), content, StandardCharsets.UTF_8);
			return true;        
		} catch (IOException e) {
			e.printStackTrace();
			
			return false;
		}
	}
	/**
	 * 8bit
	 * @param number [0,255]
	 * @return
	 */
	private static String int2Hex(long number){
		if(number<16){
			return "0"+Long.toHexString(number);
		}
		else if(number<256){
			return Long.toHexString(number);
		}
		return "ff";
	}
}
