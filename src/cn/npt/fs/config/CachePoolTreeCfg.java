package cn.npt.fs.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.npt.util.data.PathKit;

public class CachePoolTreeCfg {

	/**
	 * 原始数据
	 */
	private JSONObject rawObj;
	/**
	 * 实际操作的数据副本
	 */
	private JSONObject objTemp;
	
	public CachePoolTreeCfg(String propertyFileName) {
		String fileName=PathKit.getRootClassPath()+"/"+propertyFileName;
		byte[] bytes = null;
		try {
			bytes = Files.readAllBytes(Paths.get(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		String text=new String(bytes,StandardCharsets.UTF_8);
		this.rawObj=JSONObject.parseObject(text);
		this.objTemp=this.rawObj;
	}
	public CachePoolTreeCfg(JSONObject rawObj,JSONObject objTemp) {
		this.rawObj=rawObj;
		this.objTemp=objTemp;
	}
	public boolean hasChild(){
		if(this.objTemp.containsKey("child")){
			return true;
		}
		return false;
	}
	/**
	 * 指针往下一个child移动
	 */
	public void next(){
		this.objTemp=this.objTemp.getJSONObject("child");
	}
	/**
	 * 缓存池的容量
	 * @return
	 */
	public int getSize(){
		return this.objTemp.getJSONObject("cache").getIntValue("size");
	}
	/**
	 * 缓存池块的单位大小
	 * @return
	 */
	public int getBlockIntervalInMs(){
		return this.objTemp.getJSONObject("cache").getIntValue("blockInterval_in_ms");
	}
	/**
	 * sqlHandlers:[{"table":"iot_cvalue_intenmin","blockSize":1},{}...]
	 * @return 
	 */
	public JSONArray getSqlHandlers(){
		return this.objTemp.getJSONObject("handler").getJSONArray("sqlHandlers");
	}
	/**
	 * 原始数据存文件的大小
	 * @return
	 */
	public int getfileHandler(){
		return this.objTemp.getJSONObject("handler").getJSONObject("fileHandler").getIntValue("persistenceSize");
	}
	public boolean isRoot(){
		boolean root=false;
		if(this.objTemp.containsKey("root")){
			root=this.objTemp.getBooleanValue("root");
		}
		return root;
	}
	public String getDataDir(){
		return this.objTemp.getString("dataDir");
	}
	/**
	 * 将指针回到起始点
	 */
	public void resume(){
		this.objTemp=this.rawObj;
	}
	
	public JSONObject getRawObj() {
		return rawObj;
	}
	public void setRawObj(JSONObject rawObj) {
		this.rawObj = rawObj;
	}
	public JSONObject getObjTemp() {
		return objTemp;
	}
	public void setObjTemp(JSONObject objTemp) {
		this.objTemp = objTemp;
	}
	/**
	 * 深度拷贝对象数据
	 */
	public CachePoolTreeCfg clone(){
		CachePoolTreeCfg cptc=new CachePoolTreeCfg(JSONObject.parseObject(this.rawObj.toJSONString()),JSONObject.parseObject(this.rawObj.toJSONString()));
		return cptc;
	}
}
