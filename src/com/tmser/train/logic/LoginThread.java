package com.tmser.train.logic;

import java.text.MessageFormat;

import org.apache.log4j.Logger;

import com.tmser.train.Config;
import com.tmser.train.Constants;
import com.tmser.train.NetConnectException;
import com.tmser.train.ResManager;
import com.tmser.train.bean.Result;
import com.tmser.train.gui.RobTicket;

/**
 * 登陆线程
 * 
 * @author tmser
 * @since 2013-10-07
 * @version 1.0
 */
public class LoginThread extends BaseThread {
	private static final Logger log = Logger.getLogger(LoginThread.class);
	private volatile Thread blinker = this;
	/**
	 * 构造函数
	 * 
	 * @param robTicket
	 */
	public LoginThread(RobTicket rob) {
		super(rob);
	}

	/**
	 * override 方法<p>
	 * 登陆线程，登陆成功后才进行购票。
	 */
	@Override
	public void run() {
		Thread thisThread = Thread.currentThread();
		try {
				Result rs = new Result();
				int count = 0;
				if (!Constants.isLoginSuc) { //如果还未登录，获取登录时验证码和随机数
					rob.console(ResManager.getString("LoginThread.0", new String[]{rob.getUsername()})); 
					Constants.randCode = getRandCodeDailog(Constants.LOGIN_CODE_URL);
					//String randstr=client.getStr(Constants.RANDSTR_URL);
					//Constants.randstr=randstr.substring(14,randstr.indexOf("\","));
				}
				
				while (!Constants.isLoginSuc && blinker == thisThread) {//循环登录
					if(Constants.randCode == null){
						rob.console(ResManager.getString("LoginThread.5"));
						break;
					}
					rs = client.login(rob.getUsername(), rob.getPassword(),	Constants.randCode);
					if (rs.getState() == Result.SUCC) {
						rob.console("");
						if(rob.needRemberMe())
							rob.writeUserInfo();
						Constants.isLoginSuc = true;
					} else if (rs.getState() == Result.RAND_CODE_ERROR) {
						 rob.console(Constants.CODE_ERROR);
						 Constants.randCode = getRandCodeDailog(Constants.LOGIN_CODE_URL);
						 String randstr = client.getStr(Constants.RANDSTR_URL);
						 Constants.randstr = randstr.substring(14,randstr.indexOf("\","));
						
					} else if (rs.getState() == Result.ACC_ERROR
							|| rs.getState() == Result.PWD_ERROR) {
						rob.console(Constants.USER_ERR);
						break;
					} else {
						rob.console(rs.getMsg());
					}
					if(count > 0)Thread.sleep(Config.getSleepTime());
					count++;
				}
				
				if (Constants.isLoginSuc) {//登录成功
					rob.changePanel(RobTicket.LOGIN_SUCC);
					if (count == 0) {
						rob.console(ResManager.getString("LoginThread.1")); 
					} else if (count < 10) {
						rob.console(MessageFormat.format(ResManager.getString("LoginThread.2"),count)); 
					} else {
						rob.console(MessageFormat.format(ResManager.getString("LoginThread.3"),count)); 
					}
				}
		
			} catch(NetConnectException e) {
				rob.console(ResManager.getString("RobTicket.err.net"));
			} catch(Exception e){
				log.error(e);
				e.printStackTrace();
				rob.console(ResManager.getString("RobTicket.err.unkwnow"));
			}finally {
				rob.console(ResManager.getString("RobTicket.txtLogin.End")); 	
				rob.changePanel(RobTicket.LOGIN_BEGIN);
			}
		}

	/**
	 * @param isEnd
	 *            The isEnd to set.
	 */
	public void setEnd(boolean isEnd) {
		 blinker = null;
	}
	
	@Override
	public boolean getIsAuto() {
		return Config.isUseDama();
	}
}
