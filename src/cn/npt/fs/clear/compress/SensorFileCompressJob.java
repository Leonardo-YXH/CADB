package cn.npt.fs.clear.compress;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import cn.npt.fs.CachePoolFactory;
import cn.npt.util.algorithm.compress.ZipUtil4Npt;
/**
 * 定期压缩原始文件
 * @author Leonardo
 *
 */
public class SensorFileCompressJob implements Job {

	/**
	 * 原始文件基路径集合
	 */
	private List<String> dataDirs;
	/**
	 * 压缩后是否删除源文件
	 */
	private boolean isDelete;
	/**
	 * 倒退时长,单位毫秒
	 */
	private long backTime;
	
	public SensorFileCompressJob() {
		
	}
	
	@Override
	public void execute(JobExecutionContext ctx) throws JobExecutionException {
		this.isDelete=ctx.getJobDetail().getJobDataMap().getBoolean("isDelete");
		this.backTime=ctx.getJobDetail().getJobDataMap().getLong("backTime");
		this.dataDirs=CachePoolFactory.getAllDataDirs();
		
		Calendar calendar=Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis()-this.backTime);
		int year=calendar.get(Calendar.YEAR);
		int month=calendar.get(Calendar.MONTH);//压缩上个月的
		
		for(String dataDir:this.dataDirs){
			doCompress(year,month,"0x",0,new File(dataDir));
		}
	}
	
	private void doCompress(int year,int month,String baseName,int depth,File destFile){
		File[] listFile=destFile.listFiles();
		//System.out.println(destFile.getAbsolutePath()+":"+depth);
		if(depth==8){
			File sourceFile=new File(destFile.getAbsolutePath()+File.separator+year+File.separator+month);
			if(sourceFile.exists()){
				baseName+="_"+year+"_"+month+".zip";
				baseName=destFile.getAbsolutePath()+File.separator+year+File.separator+baseName;
				//System.out.println(baseName);
				ZipUtil4Npt.zip(sourceFile.getAbsolutePath(), baseName);
				if(this.isDelete){
					sourceFile.delete();
				}
			}
		}
		else if(depth<8){
			for(File f:listFile){
				if(f.isDirectory()){
					doCompress(year, month, baseName+f.getName(), depth+1, f);
				}
			}
		}
	}
}
