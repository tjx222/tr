package com.ywh.train;

import java.io.File;

import cn.smy.dama2.Dama2;

public abstract class DamaUtil {

	private static Dama2 dama2 = new Dama2();
	
	protected static String softName = "Tmser领卡器";
	
	protected static String softID = "694f40021a34f601b069a415cabf4d5d";
	
	public static boolean notInit = true;
	
	/**
	 * 成功
	 */
	public static final int SUCC = 0;
	
	/**
	 * 没有配置，需在打码配置中配置用户名和密码
	 */
	public static final int ERR_NOT_INIT = -999;
	
	/**
	 * 失败
	 */
	public static final int ERR_FAILED = -1000;
	
	/**
	 * 无效的卡号
	 */
	public static final int ERR_INVALID_CARDNO = Dama2.ERR_CC_INVALID_CARDNO;
	
	public static int init(){
		int ret = dama2.init(softName, softID);
		if(ret == SUCC || ret == Dama2.ERR_CC_ALREADY_INIT_PARAM){
			notInit = false;
		}
		return ret;
	}
	
	/**
	 * 反初始化
	 * @return
	 */
	public static int uninit(){
		int ret = SUCC;
		if(!DamaUtil.notInit){
			ret = dama2.uninit();
			if(ret == SUCC){
				notInit = true;
			}
		}
		return ret;
	}
	
	/**
	 * 利用配置的用户名和密码登录打码兔
	 * @return
	 */
	public static int login(){
		String userName = Config.getUsername();
		String userPassword = Config.getPassword();
		return login(userName, userPassword);
	}
	
	/**
	 * 登录打码兔
	 * @param userName
	 * @param userPassword
	 * @return
	 */
	public static int login(String userName,String userPassword){
		if(notInit){
			init();
		}
		if(notInit) 
			return ERR_FAILED;
		
		if(Util.isBlank(userName) || Util.isBlank(userPassword)){
			return ERR_NOT_INIT;
		}
		
		int ret = dama2.login(userName, userPassword, "", new String[1], new String[1]);
		if(ret == Dama2.ERR_CC_USER_NAME_ERR || ret == Dama2.ERR_CC_USER_PASSWORD_ERR){
			return ERR_NOT_INIT;
		}
		return ret;
	}
	
	/**
	　功能：　　　　用户充值
	　函数名：　　　Recharge
	　返回值：　　　0 成功, 其它失败，
	　参数：　　　    cardNo - 充值卡号，32字节
	　@return  返回用户充值后的余额(题分)， -108 卡号无效。
	*/
	public static int recharge(String cardNo){
		String userName = Config.getUsername();
		return recharge(userName,cardNo);
	}
	
	/**
	　功能：　　　　用户充值
	　函数名：　　　Recharge
	　返回值：　　　0 成功, 其它失败，
	　参数：　　　　[in]pszUserName - 充值用户名，最大32字节
	　　　　　　　　[in]pszCardNo - 充值卡号，32字节
	　@return  返回用户充值后的余额(题分)， -108 卡号无效。
	*/
	public static int recharge(String userName, String cardNo){
		if(Util.isBlank(userName) || Util.isBlank(cardNo)){
			return ERR_NOT_INIT;
		}
		
		long[] balance = new long[1];
		int ret = dama2.recharge(userName,cardNo,balance);
		if(ret == 0){
			ret = Integer.valueOf(balance[0]+"");
		}
		
		return ret;
	}
	
	/**
	　功能：　　　　查询余额
	　@return  返回用户充值后的余额(题分)，小于0为失败
	*/
	public static int queryBalance(){
		long[] balance = new long[1];
		int ret = dama2.queryBalance(balance);
		if(ret == 0){
			ret = Integer.valueOf(balance[0]+"");
		}
		return ret;
	}
	
	/**
	 * 打码
	 * @param image
	 * @param retVCodeText
	 * @return 成功返回打码id
	 */
	public static int d2Buf(byte[] image,String[] retVCodeText){
		String userName = Config.getUsername();
		String userPassword = Config.getPassword();
		if(Util.isBlank(userName) || Util.isBlank(userPassword)){
			return ERR_NOT_INIT;
		}
		long codetype = Long.valueOf(Config.getProperty("dama.codetype"));
		return dama2.d2Buf(softID, userName,userPassword, image,(short)20,codetype,retVCodeText);
	}
	
	/**
	 * 报告错误打码
	 * @param vcodeID
	 * @return
	 */
	public void reportResult(long vcodeID){
		dama2.reportResult(vcodeID,false);
	}
	
	public static void main(String[] args) {
		File f = new File(DamaUtil.class.getResource("").getPath(),"FakeCA.cer");
		System.out.println(f.getAbsolutePath());
	}
}
