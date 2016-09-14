package cn.npt.fs.alarm;

import java.util.List;

import javax.mail.MessagingException;

import cn.npt.util.mail.MailUtil;

public class AlarmNotifyByMail implements IAlarmHandler{

	/**
	 * 目标邮箱列表
	 */
	private List<String> toMailList;
	
	public AlarmNotifyByMail(List<String> toMailList) {
		this.toMailList=toMailList;
	}
	@Override
	public void handlerAlarm(SensorAlarmPack sensorAlarmPack) {
		String title=sensorAlarmPack.getTitle();
		String content=sensorAlarmPack.getContent();
		for(String toMail:this.toMailList){
			try {
				MailUtil.sendRichTextMail(toMail, title, content);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
	}

}
