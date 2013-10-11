package cn.smy.dama2;


public class Dama2 {
	{
		System.loadLibrary("Dama2Interface");
	}
	

	/**
	　功能：　　　　获取原错误码
	　函数名：　　　GetOrigError
	    用户在函数调用发生错误时会通过本函数会返回一个错误码编号，供开发人员提交给平台服务商查找错误源代码。
	　返回值：　　　0 成功 其它 - 原错误码
	　参数：　　　　无
	*/
	public native int getOrigError();
	/**
	　功能：　　　　软件初始化
	　函数名：　　　Init
	　返回值：　　　0 成功, 其它失败，详见错误码定义 http://wiki.dama2.com/index.php?n=ApiDoc.ErrDef 
	　参数：　　　　pszSoftwareName（最大31个字符）
	　　　　　　　　pszSoftwareID（32个16hex字符组成）
	*/
	public native int init(String softwareName, String softwareID);
	
	/**
	　功能：　　　　软件反初始化，释放系统资源
	　函数名：　　　Uninit
	　返回值：　　　0 成功, 其它失败，详见错误码定义 http://wiki.dama2.com/index.php?n=ApiDoc.ErrDef 
	　参数：　　　　无
	*/
	public native int uninit();
	

	/**
	　功能：　　　　用户登录
	　函数名：　　　Login
	    注意：   本函数只需要调用一次即可，Login出错除非是报用户有关的错误（如用户名错误、密码等）外，
	    其他不用管，后续调用请求打码时内部会进行再次登陆。如果需要切换用户，
	    可调用Logoff后再登录新的用户。打码兔提供给开发者测试账户名：test，密码：test，但单个软件仅限使用2000题分。
	DLL调用
	　返回值：　　　0 成功, 其它失败，详见错误码定义 http://wiki.dama2.com/index.php?n=ApiDoc.ErrDef 
	　参数：　　　　[in]pszUserName（用户名，最大31字节）
	　　　　　　　　[in]pszUserPassword（密码，最大16字节）
	　　　　　　　　[in]pDyncVerificationCode（动态验证码，没有动态验证码可直接传NULL）
	　　　　　　　　[out]pszSysAnnouncementURL（返回打码兔平台公告URL，传入的缓冲区建议512字节，开发者可自行决定是否在界面上显示）
	　　　　　　　　[out]pszAppAnnouncementURL（返回打码兔开发者后台自已定义的公告URL，传入的缓冲区建议512字节）
	*/
	public native int login(String userName, String userPassword, String dyncVCode, String [] retSysAnnouncement, String [] retAppAnnouncement);
	
	/**
	　功能：　　　　用户登出、用户注销
	　函数名：　　　Logoff
	　返回值：　　　0 成功, 其它失败，详见错误码定义 http://wiki.dama2.com/index.php?n=ApiDoc.ErrDef 
	　参数：　　　　无
	*/
	public native int logoff();
	

	/**
	　功能：　　　　用户注册
	　函数名：　　　Register
	    注意：     动态码发送方式分为：1、手机；2、邮箱；3、手机加邮箱，
	    此功能有效地防止用户账号被盗用，如果用户异地登录或进行重要操作时，
	    会需要使用动态验证码验证，确保用户账户安全！
	DLL调用
	　返回值：　　　0 成功, 其它失败，详见错误码定义 http://wiki.dama2.com/index.php?n=ApiDoc.ErrDef 
	　参数：　　　　pszUserName - 用户名，最大31个字节
	　　　　　　　　pszUserPassword - 密码，最大16字节
	　　　　　　　　pszQQ - QQ号码，可为空，最大16字节
	　　　　　　　　pszTelNo - 手机号码，最大16字节，如果动态码发送方式为1或3，手机号则必填
	　　　　　　　　pszEmail - 邮箱，最大48字节，如果动态码发送方式为2或3，则邮箱必填
	　　　　　　　　nDyncSendMode - 动态码发送方式，1手机 2邮箱 3手机加邮箱
	*/
	public native int register(String userName, String userPassword, String qq, String telNo, String email, int nDyncVCodeSendMode);
	
	/**
	　功能：　　　　用户充值
	　函数名：　　　Recharge
	　返回值：　　　0 成功, 其它失败，详见错误码定义 http://wiki.dama2.com/index.php?n=ApiDoc.ErrDef 
	　参数：　　　　[in]pszUserName - 充值用户名，最大32字节
	　　　　　　　　[in]pszCardNo - 充值卡号，32字节
	　　　　　　　　[out]pulBalance - 返回用户充值后的余额
	*/
	public native int recharge(String userName, String cardNo, long[] balance);
	
	/**
	　功能：　　　　查询用户余额
	　函数名：　　　QueryBalance
	　返回值：　　　0 成功, 其它失败，详见错误码定义 http://wiki.dama2.com/index.php?n=ApiDoc.ErrDef 
	　参数：　　　　[out]pulBalance（返回用户余额题分）
	*/
	public native int queryBalance(long [] balance);
	
	/**
	　功能：　　　　读取用户信息
	　函数名：　　　ReadInfo
	     读取用户信息，包括QQ号、手机、邮箱、动态码发送方式等信息。
	　返回值：　　　0 成功, 其它失败，详见错误码定义 http://wiki.dama2.com/index.php?n=ApiDoc.ErrDef 
	　参数：　　　　[out]pszUserName - 用户名，传入缓冲区最小需32字节
	　　　　　　　　[out]pszQQ - QQ号码，传入缓冲区最小需16字节
	　　　　　　　　[out]pszTelNo - 手机号码，传入缓冲区最小需16字节
	　　　　　　　　[out]pszEmail - 邮箱，传入缓冲区最小需48字节
	　　　　　　　　[out]pDyncSendMode - 动态码发送方式
	*/
	public native int readInfo(String [] userName, String [] qq, String [] telNo, String [] email, int []nDyncVCodeSendMode);
	


	/**
	　功能：　　　　修改登录用户信息
	　函数名：　　　ChangeInfo
	    可使用本函数实现修改用户资料、改密等功能。
	修改用户信息属于重要操作，为了用户账号安全，需要校验用户动态验证码，所以要进行两次调用。
	在调用时需特别注意：
	第一次：pszDyncVCode传空调用修改资料，平台会返回DAMA2_RET_NEED_DYNC_VCODE的错误码，并对用户发送动态验证码，开发者此时需要提示用户输入动态验证码。
	第二次：将用户填入的动态验证码填到pszDyncVCode再次调用本函数。
	　返回值：　　　0 成功, 其它失败，详见错误码定义 http://wiki.dama2.com/index.php?n=ApiDoc.ErrDef 
	　参数：　　　　pUserOldPassword - 旧密码，最大16字节
	　　　　　　　　pUserNewPassword - 新密码，最大16字节
	　　　　　　　　pszQQ - QQ号码，可为空，最大16字节
	　　　　　　　　pszTelNo - 手机号码，最大16字节，如果动态码发送方式为1或3，手机号则必填
	　　　　　　　　pszEmail - 邮箱，最大48字节，如果动态码发送方式为2或3，则邮箱必填
	　　　　　　　　pszDyncVCode - 动态验证码，第一次调用可传NULL，当有动态验证码后，填入用户输入的动态码再次调用。
	　　　　　　　　nDyncSendMode - 动态码发送方式，1手机 2邮箱 3手机加邮箱
	*/
	public native int changeInfo(String oldPassword, String newPassword, String qq, String telNo, String email, String dyncVCode, int nDyncVCodeSendMode);
	
	

	/**
	　功能：　　　　请求打码
	    函数名：　　　Decode
	    方式一、Decode 通过传入验证码图片URL地址请求打码，由打码兔控件负责下载上传，快速省时省心
	　返回值：　　　0 成功, 其它失败，详见错误码定义 http://wiki.dama2.com/index.php?n=ApiDoc.ErrDef 
	　参数：　　　　[in]pszFileURL - 验证码图片URL，最长511
	　　　　　　　　[in]pszCookie - 获取验证码所需Cookie，最长4095字节
	　　　　　　　　[in]pszReferer - 获取验证码所需Referer，最长511字节
	　　　　　　　　[in]ucVerificationCodeLen - 验证码长度，传入正确的验证码长度，将优先被识别。如果长度不定，可传0
	　　　　　　　　[in]usTimeout - 验证码超时时间，即过多久验证码将失效。单位秒。推荐120
	　　　　　　　　[in]ulVCodeTypeID - 验证码类型ID，请通过打码兔开发者后台您添加的软件中添加自己软件可能用到的验证码类型，并获取生成的ID
	　　　　　　　　[in]bDownloadPictureByLocalMachine - 是否本机下载，因为有些验证码绑定IP，不允许远程获取，如果此标志为TRUE，则打码兔控件将在您机器上自动下载图片并上传。
	　　　　　　　　　　　　　　　　　　　　　　　　对于没有此限制的验证码，将会由打码用户端下载，效率更高！建议填FALSE
	　　　　　　　　[out]pulRequestID - 返回请求ID，为后面的GetResult取打码结果所用。
	*/
	public native int decode(String url, String cookie, String referer, byte vcodeLen, short timeout, long vcodeTypeID, boolean downloadFromLocalMachine, long [] requestID);


	/**
	　功能：　　　　请求打码
	　函数名：　　　DecodeBuf
	     方式二、DecodeBuf 1、通过传入验证码图片数据流请求打码，开发者需要自行下载并打开验证码图片，获得图片数据后调用本函数请求打码；2、识别文本，如3+5=？
	　返回值：　　　0 成功, 其它失败，详见错误码定义 http://wiki.dama2.com/index.php?n=ApiDoc.ErrDef 
	　参数：　　　　[in]pImageData - 验证码图片数据 （此处也可直接填写文本，如：3+8+3=？或 中国的首都是哪里？ 但注意pszExtName就得固定填写"TXT"。）
	　　　　　　　　[in]dwDataLen - 验证码图片数据或验证码文本数据长度，即pImageData大小，限制4M
	　　　　　　　　[in]pszExtName - 图片扩展名，如JPEG、BMP、PNG、GIF（如果pImageData不是图片数据而是文本，则填写"TXT"。）
	　　　　　　　　[in]ucVerificationCodeLen - 验证码长度，传入正确的验证码长度，将优先被识别。如果长度不定，可传0
	　　　　　　　　[in]usTimeout - 验证码超时时间，即过多久验证码将失效。单位秒。推荐120
	　　　　　　　　[in]ulVCodeTypeID - 验证码类型ID，请通过打码兔开发者后台您添加的软件中添加自己软件可能用到的验证码类型，并获取生成的ID
	　　　　　　　　[out]pulRequestID - 返回请求ID，为后面的GetResult取打码结果所用。
	*/
	public native int decodeBuf(byte [] data, String extName, byte vcodeLen, short timeout, long vcodeTypeID, long [] requestID);
	

	/**
	　功能：　　　　请求打码
	　函数名：　　　DecodeWnd
	方式三、DecodeWnd 传入窗口定义串，打码兔负责帮您在指定窗口截图并上传请求打码
	　返回值：　　　0 成功, 其它失败，详见错误码定义 http://wiki.dama2.com/index.php?n=ApiDoc.ErrDef 
	　参数：　　　　[in]pszWndDef - 窗口定义字串，详见下面描述
	　　　　　　　　[in]lpRect - 要截取的窗口内容矩形(相对于窗口最左上角),NULL表示截取整个窗口内容
	　　　　　　　　[in]ucVerificationCodeLen - 验证码长度，传入正确的验证码长度，将优先被识别。如果长度不定，可传0
	　　　　　　　　[in]usTimeout - 验证码超时时间，即过多久验证码将失效。单位秒。推荐120
	　　　　　　　　[in]ulVCodeTypeID - 验证码类型ID，请通过打码兔开发者后台您添加的软件中添加自己软件可能用到的验证码类型，并获取生成的ID
	　　　　　　　　[out]pulRequestID - 返回请求ID，为后面的GetResult取打码结果所用。
	*/
	public native int decodeWnd(String wndDef, int x, int y, int cx, int cy, byte vcodeLen, short timeout, long vcodeTypeID, long [] requestID);


	/**
	　功能：　　　　取验证码识别结果
	　函数名：　　　GetResult
	    注意：取识别结果之前需要开发者需调用Decode等请求打码的方法，获取到请求ID(ulRequestID)，
	    通过请求ID获取验证码识别结果。
	　返回值：　　　0 成功, 其它失败，详见错误码定义 http://wiki.dama2.com/index.php?n=ApiDoc.ErrDef 
	　参数：　　　　[in]ulRequestID - 验证码请求ID，由Decode、DecodeBuf、DecodeWnd等函数返回
	　　　　　　　　[in]ulTimeout - GetResult函数等待超时时间(单位为毫秒)，如果填0函数将不阻塞立即返回，如果返回值为ERR_CC_NO_RESULT，则需由开发者循环调用直到返回成功或其它错误。
	　　　　　　　　　　　　　　　　如果填有效超时时间，函数将阻塞等待结果，如果等到结果会立即返回，没等到将在超时时间后返回。
	　　　　　　　　[out]pszVCode - 验证码识别结果，将通过本参数返回识别结果
	　　　　　　　　[in]ulVCodeBufLen - 接收验证码识别结果缓冲区大小，即pszVCode缓冲区大小
	　　　　　　　　[out]pulVCodeID - 返回验证码ID，如果调用成功取到验证码结果，开发者需保存此验证码ID，用于ReportResult函数报告验证码结果的成功失败状态。
	　　　　　　　　[out]pszReturnCookie - 传回下载验证码图片时返回的Cookie
	　　　　　　　　[in]ulCookieBufferLen - 接收传回cookie的缓冲区大小，即pszReturnCookie的大小
	*/
	public native int getResult(long requestID, long waitTimeout, String [] vcode, long [] vcodeID, String [] retCookie);
	

	/**
	　功能：　　　　报告验证码结果正确性，失败才报，成功可以不用报告 
	     注意：在GetResult成功后，开发者能获取到验证码ID，使用验证码ID来报告验证码的正确性，bCorrect如果为TRUE，代表验证码正确，为FALSE则代表验证码错误。
	　函数名：　　　ReportResult
	　返回值：　　　0 成功, 其它失败，详见错误码定义 http://wiki.dama2.com/index.php?n=ApiDoc.ErrDef 
	　参数：　　　　ulVCodeID - 验证码ID，使用GetResult函数返回的验证码ID
	　　　　　　　　bCorrect - TRUE正确 FALSE 错误
	*/
	public native int reportResult(long vcodeID, boolean correct);
	
	/**
	　功能：　　　　一键式通过图片数据请求打码，调用此函数之前，无需再调用初始化、登录等函数
	　函数名：　　　D2Buf
	　返回值：　　　>0 成功，返回验证码ID（用于调用ReportResult）, <0失败，详见错误码定义 http://wiki.dama2.com/index.php?n=ApiDoc.ErrDef 
	　　　　　　　　应该停机处理的错误码包括：-1~-199（参数错误、用户错误）、-208（软件禁用）、-210（非法用户）、-301（配置错误、DLL找不到）
	　参数：　　　　[in]pszSoftwareID - 软件KEY（获取方法：http://wiki.dama2.com/index.php?n=ApiDoc.GetSoftIDandKEY）
	　　　　　　　　[in]pszUserName - 打码兔用户名（注意是用户账号，而不是开发者账号）
	　　　　　　　　[in]pszUserPassword - 打码兔用户密码
	　　　　　　　　[in]pImageData - 验证码图片数据字节集 
	　　　　　　　　[in]dwDataLen - 验证码图片数据或验证码文本数据长度，即pImageData大小，限制4M
	　　　　　　　　[in]usTimeout - 验证码超时时间，即过多久验证码将失效。单位秒。推荐60，如果验证码识别成功，函数立即返回，否则函数会阻塞一直到超时时间返回
	　　　　　　　　[in]ulVCodeTypeID - 验证码类型ID，请从验证码类型表中找到您验证码的类型ID：http://wiki.dama2.com/index.php?n=ApiDoc.GetSoftIDandKEY 没有适合您的请联系打码兔补充
	　　　　　　　　[out]pszVCodeText - 返回验证码结果字符，建议传入30字节缓冲区
	*/
	public native int d2Buf(String softwareID, String userName, String userPassword, byte [] data, short timeout, long vcodeTypeID, String [] retVCodeText);
	
	/**
	　功能：　　　　一键式通过本机图片文件名请求打码，调用此函数之前，无需再调用初始化、登录等函数
	　函数名：　　　D2File
	　返回值：　　　>0 成功，返回验证码ID（用于调用ReportResult）, <0失败，详见错误码定义 http://wiki.dama2.com/index.php?n=ApiDoc.ErrDef 
	　　　　　　　　应该停机处理的错误码包括：-1~-199（参数错误、用户错误）、-208（软件禁用）、-210（非法用户）、-301（配置错误、DLL找不到）
	　参数：　　　　[in]pszSoftwareID - 软件KEY（获取方法：http://wiki.dama2.com/index.php?n=ApiDoc.GetSoftIDandKEY）
	　　　　　　　　[in]pszUserName - 打码兔用户名（注意是用户账号，而不是开发者账号）
	　　　　　　　　[in]pszUserPassword - 打码兔用户密码
	　　　　　　　　[in]pszFilePath - 本机图片文件路径 如：c:\a.jpg，请开发者注意，图片文件截取时请截取验证码区域即可，太大的图片文件严重影响识别速度
	　　　　　　　　[in]usTimeout - 验证码超时时间，即过多久验证码将失效。单位秒。推荐60，如果验证码识别成功，函数立即返回，否则函数会阻塞一直到超时时间返回
	　　　　　　　　[in]ulVCodeTypeID - 验证码类型ID，请从验证码类型表中找到您验证码的类型ID：http://wiki.dama2.com/index.php?n=ApiDoc.GetSoftIDandKEY 没有适合您的请联系打码兔补充
	　　　　　　　　[out]pszVCodeText - 返回验证码结果字符，建议传入30字节缓冲区
	*/
	public native int d2File(String softwareID, String userName, String userPassword, String fileName, short timeout, long vcodeTypeID, String [] retVCodeText);
	
//error code definition	
	//success code
	static final int ERR_CC_SUCCESS					= 0;
		//parameter error
	static final int ERR_CC_SOFTWARE_NAME_ERR		=-1;
	static final int ERR_CC_SOFTWARE_ID_ERR			=-2;
	static final int ERR_CC_FILE_URL_ERR			=-3;
	static final int ERR_CC_COOKIE_ERR				=-4;
	static final int ERR_CC_REFERER_ERR				=-5;
	static final int ERR_CC_VCODE_LEN_ERR			=-6;
	static final int ERR_CC_VCODE_TYPE_ID_ERR		=-7;
	static final int ERR_CC_POINTER_ERROR			=-8;
	static final int ERR_CC_TIMEOUT_ERR				=-9;
	static final int ERR_CC_INVALID_SOFTWARE		=-10;
	static final int ERR_CC_COOKIE_BUFFER_TOO_SMALL	=-11;
	static final int ERR_CC_PARAMETER_ERROR			=-12;
		//user error
	static final int ERR_CC_USER_ALREADY_EXIST		=-100;
	static final int ERR_CC_BALANCE_NOT_ENOUGH		=-101;
	/**
	 * 用户名不存在
	 */
	static final int ERR_CC_USER_NAME_ERR			=-102;
	/**
	 * 密码错误
	 */
	static final int ERR_CC_USER_PASSWORD_ERR		=-103;
	static final int ERR_CC_QQ_NO_ERR				=-104;
	static final int ERR_CC_EMAIL_ERR				=-105;
	static final int ERR_CC_TELNO_ERR				=-106;
	static final int ERR_CC_DYNC_VCODE_SEND_MODE_ERR=-107;
	
	/**
	 * 无效充值卡卡号
	 */
	static final int ERR_CC_INVALID_CARDNO			=-108;
	static final int ERR_CC_DYNC_VCODE_OVERFLOW		=-109;
	static final int ERR_CC_DYNC_VCODE_TIMEOUT		=-110;
	
	/**
	 * 用户被禁止使用该软件
	 */
	static final int ERR_CC_USER_SOFTWARE_NOT_MATCH	=-111;
	static final int ERR_CC_NEED_DYNC_VCODE			=-112;
		//logic error
	/**
	 * 用户未登录
	 */
	static final int ERR_CC_NOT_LOGIN				=-201;
	
	/**
	 * 用户已登录
	 */
	static final int ERR_CC_ALREADY_LOGIN			=-202;
	
	/**
	 * invalid request id, perhaps request is timeout
	 */
	static final int ERR_CC_INVALID_REQUEST_ID		=-203;	
	
	/**
	 * invalid captcha id, perhaps request is timeout
	 */
	static final int ERR_CC_INVALID_VCODE_ID		=-204;	
	static final int ERR_CC_NO_RESULT				=-205;
	
	/**
	 * 未初始化，请调用Init方法
	 */
	static final int ERR_CC_NOT_INIT_PARAM			=-206;
	/**
	 * 
	 */
	static final int ERR_CC_ALREADY_INIT_PARAM		=-207;
	
	/**
	 * 软件无效
	 */
	static final int ERR_CC_SOFTWARE_DISABLED		=-208;
	
	/**
	 * 需要重新登录
	 */
	static final int ERR_CC_NEED_RELOGIN			=-209;
	static final int EER_CC_ILLEGAL_USER			=-210;
	
	/**
	 * concurrent request is too much
	 */
	static final int EER_CC_REQUEST_TOO_MUCH		=-211;

	//system error
	static final int ERR_CC_CONFIG_ERROR			=-301;
	static final int ERR_CC_NETWORK_ERROR			=-302;
	static final int ERR_CC_DOWNLOAD_FILE_ERR		=-303;
	static final int ERR_CC_CONNECT_SERVER_FAIL		=-304;
	static final int ERR_CC_MEMORY_OVERFLOW			=-305;
	static final int ERR_CC_SYSTEM_ERR				=-306;
	static final int ERR_CC_SERVER_ERR				=-307;
	static final int ERR_CC_VERSION_ERROR			=-308;
	static final int ERR_CC_READ_FILE				=-309;
	
}
