package com.tmser.train.bean;

/**
 * 请求返回结果
 * @author Tmser
 * @since 2011-11-26 
 * @version 1.0
 */
public class Result {
	
	public final static byte SUCC = 100;
	public final static byte FAIL = 101;
	public final static byte REPEAT = 102;//重复提交
	public final static byte HASORDER = 107;
	public final static byte OTHER = 103;
	public final static byte RAND_CODE_ERROR = 104;	
	public final static byte UNCERTAINTY = 105; // 不确定
	public final static byte UNLOGIN = 106; // 未登陆或过期
	
	public final static byte NO_BOOKED_TICKET = 11;
	public final static byte HAVE_NO_PAY_TICKET = 12;
	public final static byte CANCEL_TIMES_TOO_MUCH = 13;
	public final static byte REPEAT_BUY_TICKET = 14;
	public final static byte ERROR_CARD_NUMBER = 15;

	public final static byte ACC_ERROR = 111;//用户名不存在
	public final static byte PWD_ERROR = 112;//密码错误
	public final static byte LOGIN_ERROR=113;//网页返回内容包含<title>登录</title>
	public final static byte LOST_OF_PEOPLE=114;//当前访问用户过多

	
	private byte state = FAIL; // 请求处理状态 
	private int waitTime = 0; // 请求处理状态 
	private String msg = "未知错误"; // 有效提示信息

	public byte getState() {
		return state;
	}
	
	public void setState(byte state) {
		this.state = state;
	}
	
	
	public int getWaitTime() {
		return waitTime;
	}

	public void setWaitTime(int waitTime) {
		this.waitTime = waitTime;
	}

	/**
	 * @return Returns the msg.
	 */
	public String getMsg() {
		return msg;
	}
	/**
	 * @param msg The msg to set.
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	/**
	 * override 方法
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Result [state=").append(state).append(", msg=")
				.append(msg).append("]");
		return builder.toString();
	}
	
	
}
