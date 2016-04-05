package cn.npt.fs.clear;

import org.apache.log4j.Logger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

public class SensorFileClearService {

	private static Logger log=Logger.getLogger(SensorFileClearService.class);
	public static void start(){
		SchedulerFactory sf = new StdSchedulerFactory();
		Scheduler sched=null;
		try {
			log.info("sensor原始文件清理服务启动...");
			sched=sf.getScheduler();
			sched.start();
		} catch (SchedulerException e) {
			e.printStackTrace();
			log.error(e.getMessage());
			if(sched!=null){
				try {
					sched.shutdown();
				} catch (SchedulerException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	public static void main(String[] args) {
		start();
	}
}
