package cn.npt.fs.config;

import cn.npt.util.data.PropertyFileParse;

public class CachePoolCfg {

	/**
	 * 文件存放根路径
	 */
	public String dataDir;
	/**
	 * 原始传感器的池配置
	 */
	public CacheBlockCfg sensorValuePoolCfg;
	/**
	 *  保存原始文件
	 */
	public boolean SaveOLHandler;
	/**
	 * 第一级基本统计
	 */
	public boolean FirstBSHandler;
	/**
	 * 每分钟的统计池的配置
	 */
	public CacheBlockCfg bsSensorPoolCfg;
	/**
	 *  第二级基本统计
	 */
	public boolean SecondBSHandler;
	
	/**
	 * 10min of 2day的统计池
	 */
	public CacheBlockCfg bsSensorPoolChild2Cfg;
	/**
	 *  第三级基本统计
	 */
	public boolean ThirdBSHandler;
	/**
	 *  保存第二级基本统计缓存到数据库
	 */
	public boolean SaveSecondBS2DBHandler;
	/**
	 * 1d of 2year的统计池
	 */
	public CacheBlockCfg bsSensorPoolChild3Cfg;
	
	
	/**
	 * 
	 * @param pfp
	 */
	public CachePoolCfg(PropertyFileParse pfp){
		
		if(pfp.getValue("CacheCfg", "dataDir")!=null){
			this.dataDir=pfp.getValue("CacheCfg", "dataDir");
		}
		
		this.sensorValuePoolCfg=new CacheBlockCfg(Long.parseLong(pfp.getValue("SensorValuePoolCfg", "blockInterval_in_ms")),
				Integer.parseInt(pfp.getValue("SensorValuePoolCfg", "size")), Integer.parseInt(pfp.getValue("SensorValuePoolCfg", "persistenceSize")));
		this.SaveOLHandler=Boolean.parseBoolean(pfp.getValue("SensorValuePoolCfg", "SaveOLHandler"));
		this.FirstBSHandler=Boolean.parseBoolean(pfp.getValue("SensorValuePoolCfg", "FirstBSHandler"));
		
		this.bsSensorPoolCfg=new CacheBlockCfg(Long.parseLong(pfp.getValue("BSSensorPoolCfg", "blockInterval_in_ms")), 
				Integer.parseInt(pfp.getValue("BSSensorPoolCfg", "size")), Integer.parseInt(pfp.getValue("BSSensorPoolCfg", "persistenceSize")));
		this.SecondBSHandler=Boolean.parseBoolean(pfp.getValue("BSSensorPoolCfg", "SecondBSHandler"));
		
		this.bsSensorPoolChild2Cfg=new CacheBlockCfg(Long.parseLong(pfp.getValue("BSSensorPoolChild2Cfg", "blockInterval_in_ms")), 
				Integer.parseInt(pfp.getValue("BSSensorPoolChild2Cfg", "size")), Integer.parseInt(pfp.getValue("BSSensorPoolChild2Cfg", "persistenceSize")));
		this.ThirdBSHandler=Boolean.parseBoolean(pfp.getValue("BSSensorPoolChild2Cfg", "ThirdBSHandler"));
		this.SaveSecondBS2DBHandler=Boolean.parseBoolean(pfp.getValue("BSSensorPoolChild2Cfg", "SaveSecondBS2DBHandler"));
		
		this.bsSensorPoolChild3Cfg=new CacheBlockCfg(Long.parseLong(pfp.getValue("BSSensorPoolChild3Cfg", "blockInterval_in_ms")), 
				Integer.parseInt(pfp.getValue("BSSensorPoolChild3Cfg", "size")), Integer.parseInt(pfp.getValue("BSSensorPoolChild3Cfg", "persistenceSize")));
		
		
	}
	
}
