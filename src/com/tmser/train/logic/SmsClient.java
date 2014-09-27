package com.tmser.train.logic;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.tmser.train.Config;
import com.tmser.train.JSONUtil;
import com.tmser.train.NetConnectException;
import com.tmser.train.UnRepairException;
import com.tmser.train.bean.Result;

/**
 * 短信发送
 */
public class SmsClient implements CaptchaClient{
	public static final String CAPTCHA_URL = "http://map.so.com/app/sms.php?act=captcha";
	public static final String SMS_URL = "http://map.so.com/app/sms.php";
	private static final String smsCodeUrl = "http://map.so.com/app/sms.php?new=1&pguid=89323ef910bacf13&ctype=poi";
	private String pguid = "89323ef910bacf13";
	
	private final static Logger log = Logger.getLogger(SmsClient.class);
	private HttpClient httpclient = null;
	
	/**
	 * 构造函数 
	 */
	public SmsClient(HttpClient client) {
		this.httpclient = client;
		client.getParams().setParameter(HTTP.USER_AGENT,
		"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36"); 
		
	}

	public SignBean getToken(){
		SignBean token = null;
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("new", "1"));
		parameters.add(new BasicNameValuePair("ctype", "poi"));
		parameters.add(new BasicNameValuePair("pguid",getPguid()));
		HttpGet post = new HttpGet(smsCodeUrl+"?" + URLEncodedUtils.format(parameters, Consts.UTF_8));
		post.setHeader("Host","map.so.com");
		post.setHeader("Accept-Language","zh-CN,zh;q=0.8");
		post.setHeader("Connection","keep-alive");
		post.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		String responseBody;
		try {
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			responseBody = httpclient.execute(post, responseHandler);
			log.info(responseBody);
			JSONObject json = new JSONObject(responseBody);
			Integer data = JSONUtil.getInt(json,"succ");
			if(data == 1){
				token = new SignBean();
				token.setSign(JSONUtil.getString(json,"sign"));
				token.setContent(JSONUtil.getString(json, "content"));
				token.setTime(JSONUtil.getLong(json,"time"));
			}
		}catch(UnknownHostException e){
			throw new NetConnectException(e);
		}catch (Exception e) {
			throw new UnRepairException(e);
		}
		return token;
	}

	public String getPguid() {
		String pid = Config.getProperty("sms.pguid");
		return pid != null && !"".equals(pid) ? pid:pguid;
	}

	/**
	 * @param captcha  验证码
	 * @param sign 签名
	 * @param 手机号
	 */
	public Result send(String captcha,SignBean sign,String phone) {
		log.debug("-------------------submit order start-------------------");
		Result rs = new Result();
		HttpPost post = new HttpPost(SMS_URL);
		//act:send
		//sign:989609569c0f042a7240da63910a304e
		//content:北京火车西站 北京市丰台区莲花池东路 010-51824261,点击:http://t.m.so.com/t/ncz5d 
		//phones[]:13671246705
		//time:1411808378
		//captcha:97j6
		
		List<NameValuePair> forms = new ArrayList<NameValuePair>();
		forms.add(new BasicNameValuePair("captcha", captcha));
		forms.add(new BasicNameValuePair("sign", sign.getSign()));
		forms.add(new BasicNameValuePair("act", "send"));
		forms.add(new BasicNameValuePair("time", String.valueOf(sign.getTime())));
		forms.add(new BasicNameValuePair("phones[]",phone));
		forms.add(new BasicNameValuePair("content",sign.getContent()));
		log.info(post.getURI().toASCIIString());
		String responseBody = null;
		try {
			UrlEncodedFormEntity uef = new UrlEncodedFormEntity(forms, Consts.UTF_8);
			post.setEntity(uef);
			post.setHeader("Referer","http://map.so.com/");
			post.setHeader("Host","map.so.com");
			post.setHeader("Connection","keep-alive");
			post.setHeader("Accept-Language","zh-CN,zh;q=0.8");
			post.setHeader("Accept","application/json, text/javascript, */*; q=0.01");
			
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			responseBody = httpclient.execute(post, responseHandler);
			JSONObject json = null;
			log.info(responseBody);
			json = new JSONObject(responseBody);
			if(JSONUtil.getInt(json, "succ") == 1){
				rs.setState(Result.SUCC);
			}else{
				rs.setMsg(JSONUtil.getString(json,"errcode"));
			}
		}catch(UnknownHostException e){
			throw new NetConnectException(e);
		}catch (Exception e) {
			throw new UnRepairException(e);
		}
		return rs;
	}
	
	/**
	 * 获取指定url的验证码图片字节信息
	 * @param url
	 * @return
	 */
	
	public byte[] getCodeByte(String url) {
		log.debug("-------------------get randcode start-------------------");
		HttpGet get = new HttpGet(url);
		get.setHeader("Host","map.so.com");
		get.setHeader("Accept-Language","zh-CN,zh");
		get.setHeader("Connection","keep-alive");
		get.setHeader("Accept-Charset","GBK,utf-8;q=0.7,*;q=0.3");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] content = null;
		try {
			HttpResponse response = httpclient.execute(get);
		 
			HttpEntity entity = response.getEntity();
			log.debug(response.getStatusLine());
			if (entity != null) {
				InputStream is = entity.getContent();
				byte[] buf = new byte[1024];
				int len = -1;
				while ((len = is.read(buf)) > -1) {
					baos.write(buf, 0, len);
				}
			}
			content = baos.toByteArray();
		//	writeToFolder(content);
		}catch(UnknownHostException e){
			throw new NetConnectException(e);
		}catch (Exception e) {
			throw new UnRepairException(e);
		}
		log.debug("-------------------get randcode end-------------------");
		return content;
	}
	
	public void clear(){
		if(httpclient != null)
			httpclient.getConnectionManager().shutdown();
	}
	
	public static void main(String[] args) throws JSONException {
		String msg = " 4001:t=\"\u53c2\u6570\u9519\u8bef\";break;case 4002:t=\"\u624b\u673a\u53f7\u4e3a\u7a7a\";break;case 4003:t=\"\u624b\u673a\u53f7\u683c\u5f0f\u9519\u8bef\";" +
				"break;case 4004:t=\"\u9a8c\u8bc1\u7801\u4e3a\u7a7a\";break;case 4005:t=\"\u9a8c\u8bc1\u7801\u9519\u8bef\";break;case 4010:t=\"\u6821\u9a8c\u7801\u9a8c\u8bc1\u9519\u8bef\";break;case 4011:t=\"\u65f6\u95f4\u8fc7\u671f\";break;case 5001:t=\"\u5355\u4e2aip\u6bcf\u5929\u6700\u591a\u53d1\u900150\u6761\";break;case 5002:t=\"\u6bcf\u4e2a\u624b\u673a\u53f7\u6bcf\u5929\u6700\u591a\u63a5\u65362\u6761\u540c\u6837\u7684\u77ed\u4fe1\";break;case 5003:t=\"\u6bcf\u4e2a\u624b\u673a\u53f7\u6bcf\u5929\u6700\u591a\u63a5\u53d715\u6761\u77ed\u4fe1\";break;case 5010:t=\"\u77ed\u4fe1\u53d1\u9001\u5931\u8d25\";break;case 6001:t=\"\u89e3\u6790\u5185\u5bb9\u5931\u8d25";
		System.out.println(msg);
	}
	
}