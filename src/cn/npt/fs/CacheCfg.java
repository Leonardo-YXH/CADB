package cn.npt.fs;

import java.io.File;

import cn.npt.util.data.PathKit;
import cn.npt.util.data.PropertyFileParse;
@Deprecated
public class CacheCfg {

	public static void init(){
		File file=new File(PathKit.getRootClassPath()+"/config.properties");
		if(file.exists()){
			PropertyFileParse pfp=PropertyFileParse.getInstance("config.properties");
			if(pfp.getValue("CacheCfg", "dataDir")!=null){
				CacheCfg.dataDir=pfp.getValue("CacheCfg", "dataDir");
			}
			if(pfp.getValue("SensorValuePoolCfg", "size")!=null){
				SensorValuePoolCfg.size=Integer.parseInt(pfp.getValue("SensorValuePoolCfg", "size"));
			}
			if(pfp.getValue("SensorValuePoolCfg", "blockInterval")!=null){
				SensorValuePoolCfg.blockInterval=Long.parseLong(pfp.getValue("SensorValuePoolCfg", "blockInterval_in_ms"));
			}
			if(pfp.getValue("BSSensorPoolCfg", "size")!=null){
				BSSensorPoolCfg.size=Integer.parseInt(pfp.getValue("BSSensorPoolCfg", "size"));
			}
			if(pfp.getValue("BSSensorPoolCfg", "blockSize")!=null){
				BSSensorPoolCfg.blockSize=Integer.parseInt(pfp.getValue("BSSensorPoolCfg", "blockSize"));
				BSSensorPoolCfg.blockInterval=BSSensorPoolCfg.blockSize*SensorValuePoolCfg.blockInterval;
			}	
			if(pfp.getValue("BSSensorPoolCfg", "saveFileSize")!=null){
				BSSensorPoolCfg.saveFileSize=Integer.parseInt(pfp.getValue("BSSensorPoolCfg", "saveFileSize"));
			}	
			
			BSSensorPoolChild2Cfg.blockInterval=BSSensorPoolChild2Cfg.blockSize*BSSensorPoolCfg.blockInterval;
			BSSensorPoolChild3Cfg.blockInterval=BSSensorPoolChild3Cfg.blockSize*BSSensorPoolChild2Cfg.blockInterval;
		}
		
	}
	public static String dataDir="F://data";
	/**
	 * 原始传感器的池配置
	 * @author Administrator
	 *
	 */
	public static class SensorValuePoolCfg{
		/**
		 * 缓存池的大小
		 */
		public static int size=1200;
		/**
		 * 单位块的时长，单位毫秒
		 */
		public static long blockInterval=1000L;
	}
	/**
	 * 每分钟的统计池的配置
	 * @author Administrator
	 *
	 */
	public static class BSSensorPoolCfg{
		/**
		 * 统计上一级数据块的大小
		 */
		public static int blockSize=60;
		/**
		 * 缓存池的大小
		 */
		public static int size=SensorValuePoolCfg.size/blockSize;
		
		/**
		 * 单位块的时长，单位毫秒
		 */
		public static long blockInterval=blockSize*SensorValuePoolCfg.blockInterval;
		public static int saveFileSize=SensorValuePoolCfg.size/(2*blockSize);
	}
	/**
	 * 10min of 2day的统计池
	 * @author Administrator
	 *
	 */
	public static class BSSensorPoolChild2Cfg{
		/**
		 * 统计上一级数据块的大小
		 */
		public static int blockSize=10;
		/**
		 * 缓存池的大小(10min of 2day:288)
		 */
		public static int size=288;
		
		/**
		 * 单位块的时长，单位毫秒
		 */
		public static long blockInterval=blockSize*BSSensorPoolCfg.blockInterval;
		/**
		 * 持久化的频率（暂时不用）
		 */
		public static int saveFileSize=SensorValuePoolCfg.size/(2*blockSize);
	}
	/**
	 * 1d of 2year的统计池
	 * @author Administrator
	 *
	 */
	public static class BSSensorPoolChild3Cfg{
		/**
		 * 统计上一级数据块的大小
		 */
		public static int blockSize=6*24;
		/**
		 * 缓存池的大小(1d of 2year:730)
		 */
		public static int size=730;
		
		/**
		 * 单位块的时长，单位毫秒
		 */
		public static long blockInterval=blockSize*BSSensorPoolChild2Cfg.blockInterval;
		/**
		 * 持久化的频率（暂时不用）
		 */
		public static int saveFileSize=SensorValuePoolCfg.size/(2*blockSize);
	}
	
}

