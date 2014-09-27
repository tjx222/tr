/**
 * 
 */
package com.tmser.train.logic;

/**
 * 短信发送接口
 * @author tjx
 * @version 2.0
 * 2014-9-27
 */
public interface SmsSender {

	/**
	 * 发送端消息到客户端
	 * @param phone
	 * @return
	 */
	boolean sendSms(String phone);
	
}
