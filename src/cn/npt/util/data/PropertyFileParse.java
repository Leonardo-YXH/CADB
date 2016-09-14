package cn.npt.util.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.apache.log4j.Logger;


public class PropertyFileParse {

	static Logger logger = Logger.getLogger(PropertyFileParse.class);

	  private HashMap<String, String> mapConfContent;
	  private static HashMap<String, PropertyFileParse> instances=new HashMap<String, PropertyFileParse>();
	  /**
	   * 配置文件名。eg:cache.properties
	   */
	  private String fileName;
	  /**
	   * 只需输入文件名，不用绝对或相对路径。eg:config.properties
	   * @param fileName
	   * 
	   */
	  public static PropertyFileParse getInstance(String fileName)
	  {
		  
		  PropertyFileParse instance=instances.get(fileName);
		  if(instance==null){
			  instance = new PropertyFileParse(fileName); 
			  instances.put(fileName, instance);
		  }
	    return instance;
	  }

	  private PropertyFileParse(String config) {
		  this.mapConfContent=new HashMap<String, String>();
		  this.fileName=config;
		  try {
			parseConfig(config);
		} catch (IOException e) {
			e.printStackTrace();
		}
	  }
	private void parseConfig(String fileName) throws IOException{
		String path = PathKit.getRootClassPath();
		File file=new File(path+"/"+fileName);
		BufferedReader in = null;
	    String sectionName = null;
	    if ((file.isFile()) && (fileName.endsWith(".properties")))
	    {
	      in = new BufferedReader(new FileReader(file));
	      String line = null;
	      while ((line = in.readLine()) != null) {
	        line = line.trim();

	        if (!line.startsWith("#"))
	        {
	          if (!line.isEmpty())
	          {
	            if ((line.startsWith("[")) && (line.endsWith("]="))) {
	              sectionName = line.substring(1, line.length() - 2);
	            }
	            else
	            {
	              int firstEquPos = line.indexOf('=');
	              if (-1 == firstEquPos) {
	                logger.error(fileName + "  " + sectionName + " key-value configuration is not right format!!!");
	               
	              }
	              String key = line.substring(0, firstEquPos);
	              String value = line.substring(firstEquPos + 1);
	              if (sectionName == null) {
	                logger.error(fileName + "  have not sectionname for key " + key);
	               
	              }
	              mapConfContent.put(sectionName+"."+key, value);
	            }
	          }
	        }
	      }
	    }
	}
	  public String getValue(String sectionName, String key) {
	    return mapConfContent.get(sectionName+"."+key);
	  }
	  /**
	   * 获取配置文件名
	   * @return
	   */
	  public String getFileName(){
		  return this.fileName;
	  }
}
