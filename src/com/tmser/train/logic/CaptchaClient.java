/**
 * 
 */
package com.tmser.train.logic;

/**
 *
 * @author tjx
 * @version 2.0
 * 2014-9-27
 */
public interface CaptchaClient {
	/**
	 * 获取验证码图片字节
	 * @return
	 */
	byte[] getCodeByte(String url);
}
