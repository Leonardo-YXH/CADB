package cn.npt.test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import com.google.common.io.Files;

import cn.npt.fs.CachePoolFactory;
import cn.npt.fs.bean.BSSensor;
import cn.npt.fs.bootstrap.NPServerBootstrap;
import cn.npt.fs.cache.BSSensorPool;
import cn.npt.fs.cache.BaseMemoryCache;
import cn.npt.net.handler.SimpleTCPServerHandler;
import cn.npt.net.tcp.SimpleTCPServer;
import cn.npt.net.websocket.WebSocketServer;
import cn.npt.fs.service.SensorValueFileService;
import cn.npt.util.data.FileOperator;
import cn.npt.util.math.SensorFileKit;

public class FSTest {
	public static double model=0;
	public static void main(String[] args){
		//System.out.println(System.currentTimeMillis());
		test8();
		//test3();
		//test();
		//emitSensorId();
//		Calendar cal=Calendar.getInstance();
//		cal.set(2015, 8, 14, 17, 8 ,0);
		//long a=0x0102000300040007L;
		//System.out.println(a);
		//test1();
//		Date date=new Date(1442977138701L);
//		System.out.println("year:"+(date.getYear()+1900)+" month:"+(date.getMonth()+1)+" day:"+date.getDate()+" HH:"+(date.getHours())+":"+(date.getMinutes())+":"+date.getSeconds()+" \n"+date.toString());
//		//test4();
		//TCPServer.test(9000);
		//TCPClient.unitTest("127.0.0.1", 8080);
		
		//test8();
	}
	/**
	 * 打印文件中的Sensorvalue
	 */
	@SuppressWarnings({ "unused", "deprecation" })
	public static void test2(){
		long sensorId=0x0001000100010001L;
		
		Date date=new Date(116, 2, 2, 23, 12, 0);
		//Date date=new Date(1457393595968L);
		System.out.println(date.toString());
		long time=date.getTime();
		int blockInterval=600000;
		initPool();
		System.out.println(SensorFileKit.getFileNameByTime(time, 2, TimeUnit.DAYS));
		//int size=10;
//		print(sensorId, time, 8, "================17:08:00 10==============");
//		time+=10*blockInterval;
//		print(sensorId, time, 8, "================17:08:00 10==============");
		
	}
	@SuppressWarnings("unused")
	private static void print(long sensorId,long time,int size,String description){
		List<Double> rs=SensorValueFileService.getSensorValue(sensorId, time, size);
		System.out.println(description);
		for(double v:rs){
			System.out.println(v);
		}
	}
	private static List<Long> emitSensorId(int factorySize,int groupSize,int equipSize,int endSize){
		long factoryId=0L;
		long groupId=0L;
		long equipId=0L;
		long endId=0L;
		List<Long> sensorIds=new ArrayList<Long>();
		for(int i=0;i<factorySize;i++){
			
			for(int j=0;j<groupSize;j++){
				
				for(int k=0;k<equipSize;k++){
					
					for(int h=0;h<endSize;h++){
						long sensorId=(factoryId<<48&0xffff000000000000L)
								+(groupId<<32&0x0000ffff00000000L)
								+(equipId<<16&0x00000000ffff0000L)+endId;
//						sensorId=(sensorId<<16)+groupId;
//						sensorId=(sensorId<<16)+equipId;
//						sensorId=(sensorId<<16)+endId;
						//System.out.println(SensorFileKit.getFileNameBySensorId(sensorId));
						sensorIds.add(sensorId);
						endId=h+1;
					}
					equipId=k+1;
				}
				groupId=j+1;
			}
			factoryId++;
		}
		return sensorIds;
	}
	private static List<Long> emitSensorId(long startId,int size){
		List<Long> sensorIds=new ArrayList<Long>();
		for(int i=0;i<size;i++){
			sensorIds.add(startId+i);
		}
		return sensorIds;
	}
	public static void initPool(){
		BaseMemoryCache cache=CachePoolFactory.build("cache.properties");
		List<Long> sensorIds=emitSensorId(1,1,1,2);
		for(long sensorId:sensorIds){
			cache.addSensor(sensorId);
		}
			
	}
	public static void test3(){
		
		Timer timer=new Timer();
		timer.schedule(new TimerTask() {
			BaseMemoryCache cache=CachePoolFactory.build("cache.properties");
			List<Long> sensorIds=emitSensorId(1,1,1,1);
			
			//List<Long> sensorIds=Arrays.asList(0x0002000300040006L,0x0002000300040007L);
			Map<Long,Double> sensorValues=new HashMap<Long, Double>();
			
			//long time=System.currentTimeMillis();
			@SuppressWarnings("deprecation")
			long time=new Date(116, 3, 7, 0, 0, 0).getTime();
			{
				
				//System.out.println(0x0002000300040007L+":");
				//long a=Long.parseLong("0x0002000300040007L",16);
				for(long sensorId:sensorIds){
					cache.addSensor(sensorId);
					
				}
				//添加虚拟传感器
//				CCSensor cSensor=new CCSensor(562962838585352L, "{562962838585350}+{562962838585351}");
//				cache.addCCSensor(cSensor, CacheCfg.SensorValuePoolCfg.size);
//				cache.setCCTroggleState(true);
			}
			@Override
			public void run() {
				cache.execute(time, emit(sensorIds,sensorValues));
				time+=cache.getCachePoolCfg().blockInterval;
			}
		}, 1000,100);
	}
	/**
	 * 
	 */
	public static void test6(){
		
		Timer timer=new Timer();
		timer.schedule(new TimerTask() {
			BaseMemoryCache cache1=CachePoolFactory.build("config1.properties");
			BaseMemoryCache cache2=CachePoolFactory.build("config2.properties");
			List<Long> sensorIds=emitSensorId(2,3,3,3);
			
			//List<Long> sensorIds=Arrays.asList(0x0002000300040006L,0x0002000300040007L);
			Map<Long,Double> sensorValues=new HashMap<Long, Double>();
			
			long time=System.currentTimeMillis();
			{
				
				//System.out.println(0x0002000300040007L+":");
				//long a=Long.parseLong("0x0002000300040007L",16);
				for(long sensorId:sensorIds){
					cache1.addSensor(sensorId);
					cache2.addSensor(sensorId);
					
				}
				//添加虚拟传感器
//				CCSensor cSensor=new CCSensor(562962838585352L, "{562962838585350}+{562962838585351}");
//				cache.addCCSensor(cSensor, CacheCfg.SensorValuePoolCfg.size);
//				cache.setCCTroggleState(true);
			}
			@Override
			public void run() {
				time+=1000;
				cache1.execute(time, emit(sensorIds,sensorValues));
				cache2.execute(time, emit(sensorIds,sensorValues));
				
			}
		}, 1000,1000);
	}
	public static void test(){
		//PropertyConfigurator.configure("./resource/log4j.properties");
		Timer timer=new Timer();
		timer.schedule(new TimerTask() {
			BaseMemoryCache cache=CachePoolFactory.build("config.properties");
			
			//List<Long> sensorIds=Arrays.asList(0x0102000300040006L,0x0102000300040007L);
			List<Long> sensorIds=Arrays.asList(0x0102000300040006L);
			Map<Long,Double> sensorValues=new HashMap<Long, Double>();
			
			long time=System.currentTimeMillis();
			{
				//System.out.println(0x0002000300040007L+":");
				//long a=Long.parseLong("0x0002000300040007L",16);
				for(long sensorId:sensorIds){
					cache.addSensor(sensorId);
					//sensorValues.put(key, value)
				}
//				CCSensor cSensor=new CCSensor(72620556876513288L, "{72620556876513286}+{72620556876513287}");
//				cache.addCCSensor(cSensor, CacheCfg.SensorValuePoolCfg.size);
//				cache.setCCTroggleState(true);
			}
			@Override
			public void run() {
				time+=1000;
				cache.execute(time, emit(sensorIds,sensorValues));
			}
		}, 1000,1000);
	}
	public static List<Double> emit(int size){
		List<Double> sensorValues=new ArrayList<Double>();
		
		int i=0;
		
		while(i<size){
			//sensorValues.add(100*Math.random());
			sensorValues.add(model);
			i++;
		}
		model++;
		return sensorValues;
	}
	public static Map<Long,Double> emit(List<Long> sensorIds,Map<Long,Double> sensorValues){
		for(Long id:sensorIds){
			sensorValues.put(id, model);
		}
		model++;
		return sensorValues;
	}
	public static void test4(){
		List<Double> data=Arrays.asList(1.0,2d,3d,6d,8d);
		BSSensor s0=new BSSensor(data, 0, data.size());
		
		BSSensor s1=new BSSensor(data, 0, 2);
		BSSensor s2=new BSSensor(data, 2, 4);
		BSSensor s12=new BSSensor(data, 4, data.size());
		
		BSSensor s3=new BSSensor(s1, s2);
		BSSensor s5=new BSSensor(s3, s12);
		List<BSSensor> sensors=Arrays.asList(s1,s2,s12);
		
		BSSensor s4=new BSSensor(sensors);
		System.out.println("\ns0:");s0.print();
		System.out.println("\ns3:");s3.print();
		System.out.println("\ns4:");s4.print();
		System.out.println("\ns5:");s5.print();
		System.out.println("\ns1:");s1.print();
		System.out.println("\ns2:");s2.print();
		System.out.println("\ns12:");s12.print();
	}
	public static void test5(){
		List<Long> sensorIds=emitSensorId(2,6,6,6);
		long interval=600000L;
		int size=30*24*6;byte[] from=null;
		try {
			from=java.nio.file.Files.readAllBytes(Paths.get("F:/sensor.dat"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		List<String> sensorDirs=new ArrayList<String>();
		for(Long sensorId:sensorIds){
			sensorDirs.add(SensorFileKit.getFileNameBySensorId(sensorId));
		}
		long time=System.currentTimeMillis();
		for(int i=0;i<size;i++){
			String timeVal=SensorFileKit.getFileNameByTime(time, 10, TimeUnit.MINUTES);
			for(String sensorDir:sensorDirs){
				String fileName="F:/data"+sensorDir+timeVal+"/sensor.dat";
				
				try {
					Files.write(from, FileOperator.createFile(fileName));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			time+=interval;
			System.out.println(i);
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void test7(){
		List<Long> sensorIds=emitSensorId(2,3,3,3);
		Date date=new Date();
		date.setHours(11);
		date.setMinutes(28);
		date.setSeconds(3);
		//System.out.println(date.toString());
		BaseMemoryCache cache=CachePoolFactory.build("cache.properties");
		for(long sensorId:sensorIds){
			cache.addSensor(sensorId);
		}
		int size=6;
		List<Double> data=SensorValueFileService.getSensorValue(sensorIds.get(0), date.getTime(), size);
		int i=0;
		for(Double d:data){
			System.out.println(i+":"+d);
			i++;
		}
		
	}
	/**
	 * 
	 */
	public static void test8(){
		//List<Long> sensorIds=emitSensorId(1, 1, 1, 4);
		int sensorSize=1;
		List<Long> sensorIds=emitSensorId(1, sensorSize);
		BSSensorPool.oneInsertSize=sensorSize;
		
		BaseMemoryCache cachePool=CachePoolFactory.build("cache_1.json");
		for(Long sensorId:sensorIds){
			cachePool.addSensor(sensorId);
		}
		
		WebSocketServer sServer=new WebSocketServer(8443, false, "/ws");
		
		NPServerBootstrap bootstrap=new NPServerBootstrap();
		bootstrap.addServer(sServer);
		
		SimpleTCPServerHandler handler=new SimpleTCPServerHandler();
		SimpleTCPServer server=new SimpleTCPServer(8180, handler, false, "数据采集服务器");
		bootstrap.addServer(server);
	}

}
