package com.tmser.train.logic;

import org.apache.log4j.Logger;

import com.tmser.train.Config;
import com.tmser.train.bean.Result;
import com.tmser.train.gui.RobTicket;

/**
 * 短信发送线程
 * 
 * @author Tmser
 * @since 2014-09-27
 * @version 1.0
 */
public class SmsThread extends BaseThread {
	private SmsClient smsClient;
	public SmsThread(RobTicket rob) {
		super(rob);
		smsClient =  new SmsClient(rob.getHttpClient());
	}

	private static final Logger log = Logger.getLogger(SmsThread.class);
	@Override
	public void run() {
		try {
			Thread.sleep(2000);
			SignBean sign = smsClient.getToken();
			if(sign == null)
				return ;
			Thread.sleep(2000);
			String captcha = getRandCodeDailog(SmsClient.CAPTCHA_URL);
			if(captcha != null && !"".equals(captcha)){
				Thread.sleep(200);
				Result rs = smsClient.send(captcha, sign, Config.getProperty("sms.phone"));
				if(rs.getState() != Result.SUCC){
					rob.console("error code:"+rs.getMsg());
				}
			}
			
		}catch(InterruptedException e){
			log.error(e);
		}
	}

	@Override
	public boolean getIsAuto() {
		return rob.isAutocode();
	}
}
