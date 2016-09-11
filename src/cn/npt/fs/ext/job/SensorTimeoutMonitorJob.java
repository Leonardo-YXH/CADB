package cn.npt.fs.ext.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import cn.npt.fs.ext.event.SimulateEmitHandler;

public class SensorTimeoutMonitorJob implements Job {

	@Override
	public void execute(JobExecutionContext ctx) throws JobExecutionException {
		SimulateEmitHandler.setMonitorSensor();
	}

}
