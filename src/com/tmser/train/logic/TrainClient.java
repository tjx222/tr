package com.tmser.train.logic;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import static com.tmser.train.JSONUtil.*;
import com.tmser.train.Constants;
import com.tmser.train.NetConnectException;
import com.tmser.train.ResManager;
import com.tmser.train.UnRepairException;
import com.tmser.train.Util;
import com.tmser.train.bean.Page;
import com.tmser.train.bean.Result;
import com.tmser.train.bean.TokenAndTicket;
import com.tmser.train.bean.TrainQueryInfo;
import com.tmser.train.bean.UserInfo;

/**
 * 车票订购网络处理core
 */
public class TrainClient implements CaptchaClient{
	public  String jSessionId = null;
	public  String BIGipServerotn = null;
	

	public void setBIGipServerotn(String bIGipServerotn) {
		BIGipServerotn = bIGipServerotn;
	}
	
	public String getBIGipServerotn() {
		return BIGipServerotn;
	}

	
	public String getjSessionId() {
		return jSessionId;
	}


	public void setjSessionId(String jSessionId) {
		this.jSessionId = jSessionId;
	}


	private final static Logger log = Logger.getLogger(TrainClient.class);
	private HttpClient httpclient = null;
	
	/**
	 * 构造函数 
	 */
	public TrainClient(HttpClient client) {
		this.httpclient = client;
		client.getParams().setParameter(HTTP.USER_AGENT,
		"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36"); 

	}

	/**
	 * 获取令牌
	 * @return
	 */
	 
	public String getTokenAndLeftTicket(HttpEntity entity) {
		log.debug("-------------------get token start-------------------");
		//HttpGet get = new HttpGet(Constants.GET_TOKEN_URL);
		String token = "";
		String ticket = "";
		String keyIsChange = "";
		String tourFlag="";
		String trainLocation ="";
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(
					entity.getContent() , "UTF-8"));
			String line = null;	
			while ((line = br.readLine()) != null) {
				if(line.contains("globalRepeatSubmitToken")){
					log.info("token Line: "+ line);
					token = line.substring(line.indexOf("'")+1,line.length() -2);
				}
				//if(line.contains("ticketInfoForPassengerForm")){
				int index = -1;
				if((index = line.indexOf("ticketInfoForPassengerForm=")) > -1){
					log.info("tikect info:"+line);
					String tiketMsg = line.substring(index+27,line.length()-1);
					JSONObject json = new JSONObject(tiketMsg);
					ticket = getString(json, "leftTicketStr");
					keyIsChange = getString(json,"key_check_isChange");
					tourFlag = getString(json,"tour_flag");
					trainLocation = getString(json, "train_location");
				}
			}
			log.info("Token: "+ token+", ticket: "+ ticket+",key_is_change: "+ keyIsChange);
		} catch (Exception e) {
			throw new UnRepairException(e);
		}finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		log.debug("-------------------get token end-------------------");
		return new StringBuilder(token).append(",").append(ticket).append(",").append(keyIsChange)
				.append(",").append(tourFlag).append(",").append(trainLocation).toString();
	}

	/** 
	 * 预订车票

	 * @param rangDate
	 * @param startDate
	 * @param train
	 * @return
	 */
	public Result book(String ticketType, String startDate, TrainQueryInfo train) {		
		log.debug("-------------------book start-------------------");
		Result rs = new Result();
		try {
		if(checkIsLogin()){
			Thread.sleep(2000);
			StringBuilder url = new StringBuilder(Constants.BOOK_URL);
			url.append("?back_train_date=").append(Util.getCurDate())
			.append("&train_date=").append(startDate)
			.append("&tour_flag=dc").append("&purpose_codes=").append(getSearchType(ticketType))
			.append("&query_to_station_name=").append(train.getToStationName())
			.append("&query_from_station_name=").append(train.getFromStationName())
			.append("&secretStr=").append(train.getSecretStr());
			HttpGet get = new HttpGet(url.toString());
			
			/*List<NameValuePair> formparams = new ArrayList<NameValuePair>();
			formparams.add(new BasicNameValuePair("back_train_date", Util.getCurDate())); //"2011-11-23"
			formparams.add(new BasicNameValuePair("train_date", startDate)); //"2011-11-28"
			formparams.add(new BasicNameValuePair("tour_flag", "dc"));
			formparams.add(new BasicNameValuePair("purpose_codes", getSearchType(ticketType))); //"ADULT"
			formparams.add(new BasicNameValuePair("query_from_station_name", train.getFromStationName()));
			formparams.add(new BasicNameValuePair("query_to_station_name", train.getToStationName()));
			formparams.add(new BasicNameValuePair("secretStr", train.getSecretStr()));
			
			if(log.isInfoEnabled()){
				for(NameValuePair n:formparams){
					log.info(n.getName()+":"+ n.getValue());
				}
			}
			UrlEncodedFormEntity uef = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
				post.setEntity(uef);
				post.setHeader("Accept,","**");
				post.setHeader("Referer","https://kyfw.12306.cn/otn/leftTicket/init");
				post.setHeader("Origin","https://kyfw.12306.cn");
				post.setHeader("Host","kyfw.12306.cn");
				post.setHeader("X-Requested-With","XMLHttpRequest");
				post.setHeader("Accept-Language","zh-CN,zh;q=0.8");
				post.setHeader("Connection","keep-alive");
				post.setHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
				*/
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				String response = httpclient.execute(get,responseHandler);
			//	HttpEntity entity = response.getEntity();
				
				//int statusCode = response.getStatusLine().getStatusCode();
				// HttpClient对于要求接受后继服务的请求，象POST和PUT等不能自动处理转发
				// 301或者302
			//	if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY || 
			//		statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
			//		log.info(statusCode + " TO " );
				    // 从头中取出转向的地址
			//	    Header locationHeader = response.getFirstHeader("Location");
			//	    String location = null;
			//	    if (locationHeader != null) {
			//	     location = locationHeader.getValue();
			//	     log.info(location);
				    /* if(entity!=null)
				    	 entity.getContent().close();
				     HttpGet get = new HttpGet(location);
				     response = httpclient.execute(get);
				     entity =response.getEntity();
				    } else {
				     log.error("Location field value is null.");
				    }*/
			//	    }
			//	}
				
				log.info(" 提交预定：" + response);
				
				
				/*br = new BufferedReader(new InputStreamReader(entity.getContent() , "UTF-8"));
				String line="";
				while ((line = br.readLine()) != null) {
					System.out.println(line);
				}*/
				//rs.setMsg(getTokenAndLeftTicket(entity));
				JSONObject json = new JSONObject(response);
				if(getBoolean(json,"status")){
					rs.setMsg(getToken());
					rs.setState(Result.SUCC);
				}else{
					String errmsg = getErrMsgString(json, "messages");
					rs.setMsg(errmsg);
					if(errmsg.contains("未处理的订单")){
						rs.setState(Result.HAVE_NO_PAY_TICKET);
					}else{
						rs.setState(Result.FAIL);
					}
				}

			}else{
				rs.setState(Result.UNLOGIN);
			}
		}catch(UnknownHostException e){
			throw new NetConnectException(e);
		}catch (Exception e) {
			e.printStackTrace();
			throw new UnRepairException(e);
		} 
		log.debug("-------------------book end-------------------");
		return rs;
		
	}
	
	public String getToken(){
		String token = null;
/*		BasicClientCookie fromStation = new BasicClientCookie("_jc_save_fromStation","%u5317%u4EAC%2CBJP");
		fromStation.setPath("/");
		fromStation.setDomain("kyfw.12306.cn");
		BasicClientCookie toStation = new BasicClientCookie("_jc_save_toStation","%u840D%u4E61%2CPXG;");
		toStation.setPath("/");
		toStation.setDomain("kyfw.12306.cn");
		BasicClientCookie fromDate = new BasicClientCookie("_jc_save_fromDate","2014-01-08");
		fromDate.setPath("/");
		fromDate.setDomain("kyfw.12306.cn");
		BasicClientCookie toDate = new BasicClientCookie("_jc_save_toDate","2014-01-05");
		toDate.setPath("/");
		toDate.setDomain("kyfw.12306.cn");
		BasicClientCookie wfDc = new BasicClientCookie("_jc_save_wfdc_flag","dc");
		wfDc.setPath("/");
		wfDc.setDomain("kyfw.12306.cn");
		
		BasicClientCookie sid = new BasicClientCookie("JSESSIONID",getjSessionId());
		sid.setPath("/otn");
		sid.setDomain("kyfw.12306.cn");
		BasicClientCookie big = new BasicClientCookie("BIGipServerotn",getBIGipServerotn());
		big.setPath("/");
		big.setDomain("kyfw.12306.cn");
		CookieStore store = new BasicCookieStore(); 
		store.addCookie(fromStation);
		store.addCookie(toStation);
		store.addCookie(fromDate);
		store.addCookie(toDate);
		store.addCookie(wfDc);
		store.addCookie(sid);
		store.addCookie(big);*/
/*		for (Cookie cookie : cookies) {
			System.out.println(cookie.getName()+" : "+ cookie.getValue());
		}*/
		HttpGet post = new HttpGet(Constants.TOKEN_URL);
		
		//post.setHeader("Referer","https://kyfw.12306.cn/otn/leftTicket/init");
		//post.setHeader("Origin","https://kyfw.12306.cn");
		post.setHeader("Host","kyfw.12306.cn");
		post.setHeader("Accept-Language","zh-CN,zh;q=0.8");
		post.setHeader("Connection","keep-alive");
		post.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		try {
			//List <NameValuePair> nvps = new ArrayList <NameValuePair>();
			//nvps.add(new BasicNameValuePair("_json_att", ""));
			//post.setEntity(new UrlEncodedFormEntity(nvps));
			//((DefaultHttpClient) httpclient).setCookieStore(store);
			HttpEntity entity = httpclient.execute(post).getEntity();
			if(entity != null ){
				token = getTokenAndLeftTicket(entity);
			}
	/*		for (Cookie cookie : store.getCookies()) {
				System.out.println(cookie.getName()+" : "+ cookie.getValue());
			}*/
		}catch(UnknownHostException e){
			throw new NetConnectException(e);
		}catch (Exception e) {
			throw new UnRepairException(e);
		}
		return token;
	}


	public List<NameValuePair> setOrderForm(String randCode, TokenAndTicket token,final List<UserInfo> users, final TrainQueryInfo train){
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		log.info("UserSize:"+users.size());
		/*
		 * 
cancel_flag:2
bed_level_order_num:000000000000000000000000000000
passengerTicketStr:1,0,1,陈花,1,360321199009255044,,N_1,0,1,陈艳锋,1,360321199007205043,13671246705,N_1,0,1,刘松青,1,360321198408065032,13120194361,N_1,0,1,谭金凤,1,360321198812015029,13671246705,N
oldPassengerStr:陈花,1,360321199009255044,1_陈艳锋,1,360321199007205043,1_刘松青,1,360321198408065032,1_谭金凤,1,360321198812015029,1_
tour_flag:dc
randCode:pt79
_json_att:
REPEAT_SUBMIT_TOKEN:bcd98b8c13878d64ecebf8a9da77b532
		key_check_isChange:354286D6B6BDA053BD70FCC9499D1AA1720ADDAE186864D0B5349B63
		leftTicketStr:1020103210405660000030355000001020100000
		train_location:P2
		*/
		
		StringBuilder passengerTicketStr = new StringBuilder();
		StringBuilder oldPassengerStr = new StringBuilder();

		for (UserInfo user : users) {
			passengerTicketStr.append(user.getText()).append("_");
			oldPassengerStr.append(user.getSimpleText()).append(",").append(user.getTickType()).append("_");
		}
		
		formparams.add(new BasicNameValuePair("REPEAT_SUBMIT_TOKEN", token.getToken())); 
		formparams.add(new BasicNameValuePair("passengerTicketStr",	passengerTicketStr.substring(0,passengerTicketStr.length() -1))); //"上海虹桥"
		formparams.add(new BasicNameValuePair("oldPassengerStr", oldPassengerStr.toString())); //"AOH"
		formparams.add(new BasicNameValuePair("randCode", randCode));
		formparams.add(new BasicNameValuePair("_json_att",""));

		
		for(NameValuePair nv : formparams){
			log.info(nv.getName()+": "+nv.getValue());
		}
		return formparams;
	}
	/**
	 * 提交订单
	 * @param randCode
	 * @param token
	 * @param user
	 * @param train
	 * @return
	 */
	public Result checkOrder(List<NameValuePair> formparams,TokenAndTicket token) {
		log.debug("-------------------submit order start-------------------");
		Result rs = new Result();
		HttpPost post = new HttpPost(Constants.CHECK_ORDER_URL);
		List<NameValuePair> forms = new ArrayList<NameValuePair>(formparams);
		forms.add(new BasicNameValuePair("cancel_flag", "2"));
		forms.add(new BasicNameValuePair("bed_level_order_num", "000000000000000000000000000000"));
		forms.add(new BasicNameValuePair("tour_flag", token.getTourFlag()));
		
		String responseBody = null;
		try {
			UrlEncodedFormEntity uef = new UrlEncodedFormEntity(forms, Consts.UTF_8);
			post.setEntity(uef);
			post.setHeader("Referer","https://kyfw.12306.cn/otn/confirmPassenger/initDc");
			post.setHeader("Origin","https://kyfw.12306.cn");
			post.setHeader("Host","kyfw.12306.cn");
			//post.setHeader("Accept-Encoding","gzip,deflat"); 加了会乱码
			post.setHeader("Accept-Language","zh-CN,zh");
			post.setHeader("Connection","keep-alive");
			post.setHeader("Accept-Charset","utf-8");
			
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			responseBody = httpclient.execute(post, responseHandler);
			JSONObject json = new JSONObject(responseBody);
			JSONObject data = getJSONObject(json,"data");
			log.info(responseBody);
			if(getBoolean(data,"submitStatus")){
				rs.setState(Result.SUCC);
				log.info("提交订单成功");
			}else{
				rs.setState(Result.FAIL);
				log.info("提交订单失败");
			}
		
		}catch(UnknownHostException e){
			throw new NetConnectException(e);
		}catch (Exception e) {
			throw new UnRepairException(e);
		}
		return rs;
	}

	/**
	 * 检查用户是否已登录
	 */
	public boolean checkIsLogin(){
		HttpPost post = new HttpPost(Constants.CHECK_LOGIN_URL);
		post.setHeader("Referer","https://kyfw.12306.cn/otn/leftTicket/init");
		post.setHeader("Host","kyfw.12306.cn");
		//post.setHeader("Accept-Encoding","gzip,deflat"); 加了会乱码
		post.setHeader("Accept-Language","zh-CN,zh;q=0.8");
		post.setHeader("Connection","keep-alive");
		post.setHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
		post.setHeader("If-Modified-Since","0");
		post.setHeader("X-Requested-With","XMLHttpRequest");
		String responseBody;
		try {
			List <NameValuePair> nvps = new ArrayList <NameValuePair>();
			nvps.add(new BasicNameValuePair("_json_att", ""));
			post.setEntity(new UrlEncodedFormEntity(nvps));
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			responseBody = httpclient.execute(post, responseHandler);
			log.info(responseBody);
			JSONObject json = new JSONObject(responseBody);
			JSONObject data = getJSONObject(json,"data");
			if(responseBody != null && data.getBoolean("flag")){
				Constants.isLoginSuc = true;
				return true;
			}
			
		}catch(UnknownHostException e){
			throw new NetConnectException(e);
		}catch (Exception e) {
			e.printStackTrace();
			throw new UnRepairException(e);
		}
		Constants.isLoginSuc = false;
		return false;
	}
	
	/**
	 * 退出登录
	 */
	public void loginOut(){
		HttpGet get = new HttpGet(Constants.LOGIN_OUT_URL);
		get.setHeader("Referer","https://kyfw.12306.cn/otn/queryOrder/initNoComplete");
		get.setHeader("Host","kyfw.12306.cn");
		//post.setHeader("Accept-Encoding","gzip,deflat"); 加了会乱码
		get.setHeader("Accept-Language","zh-CN,zh;q=0.8");
		get.setHeader("Connection","keep-alive");
		String response;
		try {
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			response = httpclient.execute(get, responseHandler);
			log.debug(response);
		}catch(UnknownHostException e){
			throw new NetConnectException(e);
		}catch (Exception e) {
			throw new UnRepairException(e);
		}
	}
	
	/**
	 * 排队拿号
	 * @param randCode
	 * @param token
	 * @param user
	 * @param train
	 * @return
	 */
	public Result getCount(TokenAndTicket token,String seat,String tiketType, final TrainQueryInfo train) {
		log.debug("-------------------submit order start-------------------");
		/*train_date:Wed Jan 08 2014 00:00:00 GMT+0800 (中国标准时间)
		train_no:240000T1450U
		stationTrainCode:T145
		seatType:1
		fromStationTelecode:BJP
		toStationTelecode:PXG
		leftTicket:1020103210405660000030355000001020100000
		purpose_codes:00
		_json_att:
		REPEAT_SUBMIT_TOKEN:bbc93c86899a3fc03a96dd3f1a0f2fbf*/
	//	train_date=Wed+Jan+08+2014+00%3A00%3A00+GMT%2B0800+(%E4%B8%AD%E5%9B%BD%E6%A0%87%E5%87%86%E6%97%B6%E9%97%B4)&train_no=240000T1450U&stationTrainCode=T145&seatType=1&fromStationTelecode=BJP&toStationTelecode=PXG&leftTicket=1020103190405660000010201000003035500000&purpose_codes=00&_json_att=&REPEAT_SUBMIT_TOKEN=93b172860e2e6b1717e4dced5f448c25
		Result rs = new Result();
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("train_date", Util.formatDate(train.getStartTrainDate())));
		parameters.add(new BasicNameValuePair("stationTrainCode", train.getStationTrainCode()));
		
		parameters.add(new BasicNameValuePair("REPEAT_SUBMIT_TOKEN",token.getToken()));
		parameters.add(new BasicNameValuePair("seatType", seat));
		parameters.add(new BasicNameValuePair("fromStationTelecode", train.getFromStationTelecode()));
		parameters.add(new BasicNameValuePair("toStationTelecode", train.getToStationTelecode()));
		parameters.add(new BasicNameValuePair("train_no", train.getTrainNo()));
		
		parameters.add(new BasicNameValuePair("leftTicket",token.getTicket()));
		parameters.add(new BasicNameValuePair("purpose_codes",getSearchCode(tiketType)));
		parameters.add(new BasicNameValuePair("_json_att",""));
		
/*		for(NameValuePair nv : parameters){
			log.info(nv.getName()+": "+nv.getValue());
		}*/
		
		HttpPost get = new HttpPost(Constants.GET_COUNT_URL);
		String responseBody = null;
		try {
			get.setEntity(new UrlEncodedFormEntity(parameters));
			get.setHeader("Referer","https://kyfw.12306.cn/otn/confirmPassenger/initDc");
			get.setHeader("Host","kyfw.12306.cn");
			//post.setHeader("Accept-Encoding","gzip,deflat"); 加了会乱码
			get.setHeader("Accept-Language","zh-CN,zh;q=0.8");
			get.setHeader("Connection","keep-alive");
			get.setHeader("Accept","application/json, text/javascript, */*; q=0.01");
			
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			responseBody = httpclient.execute(get, responseHandler);
			JSONObject json = null;
			log.info(responseBody);
			json = new JSONObject(responseBody);
			if(getBoolean(json, "status")){
				if("Y".equals(getString(json,"isRelogin"))){
					Constants.isLoginSuc = false;
					rs.setMsg(ResManager.getString("LogicThread.34"));
					rs.setState(Result.UNLOGIN);
					return rs;
				}
				
				if("true".equals(getString(json,"op_2"))){
					rs.setMsg(ResManager.getString("LogicThread.111"));
					rs.setState(Result.FAIL);
					return rs;
				}
				
				JSONObject ticket = getJSONObject(json,"data");
				if(ticket != null){
					int tkcount = getTicketCountDesc(getString(ticket,"ticket"), seat);
					if(tkcount > 0 ){
							rs.setState(Result.SUCC);
							rs.setMsg("Has Ticket");
							rs.setWaitTime(tkcount);
					}
				}
			}else{
				rs.setMsg(getErrMsgString(json, "messages"));
				rs.setState(Result.FAIL);
			}
		}catch(UnknownHostException e){
			throw new NetConnectException(e);
		}catch (Exception e) {
			throw new UnRepairException(e);
		} 
		return rs;
	}
	
	
	/**
	 * 排队拿号
	 * @param randCode
	 * @param token
	 * @param user
	 * @param train
	 * @return
	 */
	public Result queryOrderQueue(final List<NameValuePair> formparams,TokenAndTicket token,String ticketType) {
		log.debug("-------------------submit order start-------------------");
		Result rs = new Result();
		HttpPost post = new HttpPost(Constants.QUERY_ORDER_QUEE);
		List<NameValuePair> forms = new ArrayList<NameValuePair>(formparams);
		forms.add(new BasicNameValuePair("key_check_isChange", token.getKeyIsChange()));
		forms.add(new BasicNameValuePair("train_location", token.getTrainLocation()));
		forms.add(new BasicNameValuePair("leftTicketStr",token.getTicket()));
		forms.add(new BasicNameValuePair("purpose_codes",getSearchCode(ticketType)));
		log.info(post.getURI().toASCIIString());
		/*
		 * 
		 passengerTicketStr:1,0,1,陈花,1,360321199009255044,,N
		oldPassengerStr:陈花,1,360321199009255044,1_
		randCode:77xm
		purpose_codes:00
		key_check_isChange:354286D6B6BDA053BD70FCC9499D1AA1720ADDAE186864D0B5349B63
		leftTicketStr:1020103210405660000030355000001020100000
		train_location:P2
		_json_att:
		REPEAT_SUBMIT_TOKEN:bbc93c86899a3fc03a96dd3f1a0f2fbf
		 */
		String responseBody = null;
		try {
			UrlEncodedFormEntity uef = new UrlEncodedFormEntity(forms, Consts.UTF_8);
			post.setEntity(uef);
			post.setHeader("Referer","https://kyfw.12306.cn/otn/confirmPassenger/initDc");
			post.setHeader("Host","kyfw.12306.cn");
			//post.setHeader("Accept-Encoding","gzip,deflat"); 加了会乱码
			post.setHeader("Connection","keep-alive");
			post.setHeader("Accept-Language","zh-CN,zh;q=0.8");
			post.setHeader("Accept","application/json, text/javascript, */*; q=0.01");
			
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			responseBody = httpclient.execute(post, responseHandler);
			JSONObject json = null;
			log.info(responseBody);
			json = new JSONObject(responseBody);
			if(getBoolean(json, "status")){
					JSONObject data = getJSONObject(json,"data");
					boolean canSubmit = getBoolean(data,"submitStatus");
					if(canSubmit){
							rs.setState(Result.SUCC);
							rs.setMsg("Has Ticket");
					}else{
							rs.setState(Result.REPEAT);
							rs.setMsg(getString(data,"errMsg"));
					}
			}else{
				rs.setMsg("Empty Content");
			}
		}catch(UnknownHostException e){
			throw new NetConnectException(e);
		}catch (Exception e) {
			throw new UnRepairException(e);
		}
		return rs;
	}
	
	
	/**
	 * 排队拿号
	 * @param randCode
	 * @param token
	 * @param user
	 * @param train
	 * @return
	 */
	public Result queryWaitTime(TokenAndTicket token) {
		log.debug("-------------------submit order start-------------------");
		Result rs = new Result();
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("REPEAT_SUBMIT_TOKEN", token.getToken()));
		parameters.add(new BasicNameValuePair("tourFlag", "dc"));
		parameters.add(new BasicNameValuePair("random", String.valueOf((new Date()).getTime())));
		parameters.add(new BasicNameValuePair("_json_att", ""));
		HttpGet get = new HttpGet(Constants.SUBMIT_WAIT_URL+"?" + URLEncodedUtils.format(parameters, Consts.UTF_8));
		log.info(get.getURI().toASCIIString());
		String responseBody = null;
		try {
			get.setHeader("Referer","https://kyfw.12306.cn/otn/confirmPassenger/initDc");
			get.setHeader("Host","kyfw.12306.cn");
			//post.setHeader("Accept-Encoding","gzip,deflat"); 加了会乱码
			get.setHeader("Accept-Language","zh-CN,zh");
			get.setHeader("Connection","keep-alive");
			get.setHeader("Accept-Charset","GBK,utf-8;q=0.7,*;q=0.3");
			
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			responseBody = httpclient.execute(get, responseHandler);
			JSONObject json = null;
			if(responseBody != null){
				log.info(responseBody);
				json = new JSONObject(responseBody);
				JSONObject data = getJSONObject(json,"data");
				int waitTime = data.getInt("waitTime");
				String orderid = getString(data,"orderId");
				String msg = getString(data,"msg");
				
				//"data":{"queryOrderWaitTimeStatus":true,"count":0,"waitTime":4,"requestId":5824745403582708711,"waitCount":1,"tourFlag":"dc","orderId":null}
				if(orderid != null && !"".equals(orderid)){
						rs.setState(Result.SUCC);
						rs.setMsg(orderid);
				}else if("".equals(orderid) && !"".equals(msg)) {
					 rs.setState(Result.HASORDER);
					 rs.setMsg(msg);
				}else{
					rs.setMsg(getErrMsgString(json, "messages"));
				}
				rs.setWaitTime(waitTime);
			}else{
				rs.setMsg("Empty Content");
			}
		} catch(UnknownHostException e){
			throw new NetConnectException(e);
		}catch (Exception e) {
			throw new UnRepairException(e);
		}
		return rs;
	}
	
	public static int getTicketCountDesc(String mark, String seat) {
		int seat_1 = -1;
		int seat_2 = -1;
		int i = 0;
		while (i < mark.length()) {
			String s = mark.substring(i, 10+i);
			String c_seat = s.substring(0, 1);
			if (c_seat.equals(seat)) {
				String count = s.substring(6, 10);
				while (count.length() > 1 && "0".equals(count.substring(0, 1))) {
					count = count.substring(1, count.length());
				}
				int c = Integer.parseInt(count);
				if (c < 3000) {
					seat_1 = c;
				} else {
					seat_2 = (c - 3000);
				}
			}
			i = i + 10;
		}
		return Math.max(seat_1,seat_2);
	}
	
	/**
	 * 提交订单到付款
	 * @param randCode
	 * @param token
	 * @param user
	 * @param train
	 * @return
	 */
	public Result payOrder(String orderid) {
		log.debug("-------------------submit order start-------------------");
		Result rs = new Result();
		HttpPost post = new HttpPost(Constants.SUBMIT_URL);
		try {
			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
			formparams.add(new BasicNameValuePair("orderSequence_no",orderid));
			UrlEncodedFormEntity uef = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
			post.setEntity(uef);
			post.setHeader("Referer","https://kyfw.12306.cn/otn/confirmPassenger/initDc");
			post.setHeader("Origin","https://kyfw.12306.cn");
			post.setHeader("Host","kyfw.12306.cn");
			//post.setHeader("Accept-Encoding","gzip,deflat"); 加了会乱码
			post.setHeader("Accept-Language","zh-CN,zh");
			post.setHeader("Connection","keep-alive");
			post.setHeader("Accept-Charset","GBK,utf-8;q=0.7,*;q=0.3");
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String response = httpclient.execute(post, responseHandler);
			if(response != null && response.indexOf("{")>-1){
				JSONObject json = new JSONObject(response);
				if (getBoolean(json,"status")){
					JSONObject data = getJSONObject(json,"data");
					if (getBoolean(data,"submitStatus")) {
						rs.setState(Result.SUCC);
					}
				}
			}
			log.info(response);
		}catch(UnknownHostException e){
			throw new NetConnectException(e);
		}catch (Exception e) {
			throw new UnRepairException(e);
		}
		return rs;
	}
	
	/**
	 * 查询列车信息
	 * @param from
	 * @param to
	 * @param startDate
	 * @param rangDate
	 * @return
	 */
	public List<TrainQueryInfo> queryTrain(String from, String to, String startDate, String rangDate, String landDate,String ticketType) {
		log.debug("-------------------query train start-------------------");
		if (rangDate == null || rangDate.isEmpty()) {
			rangDate = "00:00--24:00";
		}
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("leftTicketDTO.train_date", startDate));
		parameters.add(new BasicNameValuePair("leftTicketDTO.from_station", Util.getCityCode(from)));
		parameters.add(new BasicNameValuePair("leftTicketDTO.to_station", Util.getCityCode(to)));
		parameters.add(new BasicNameValuePair("purpose_codes", getSearchType(ticketType)));
		HttpGet get = new HttpGet(Constants.QUERY_TRAIN_URL + URLEncodedUtils.format(parameters, Consts.UTF_8));
		log.info(Constants.QUERY_TRAIN_URL + URLEncodedUtils.format(parameters, Consts.UTF_8));
		get.setHeader("Referer","https://kyfw.12306.cn/otn/leftTicket/init");
		get.setHeader("Host","kyfw.12306.cn");
		get.setHeader("Content-Type","application/json;charset=UTF-8");
		get.setHeader("Accept-Language","zh-CN,zh;q=0.8");
		get.setHeader("Connection","keep-alive");
		get.setHeader("Accept,","*/*");
		get.setHeader("If-Modified-Since","0");
		get.setHeader("X-Requested-With","XMLHttpRequest");
		String responseBody = null;
		List<TrainQueryInfo> all = Collections.emptyList();
		try {
			HttpResponse response = httpclient.execute(get);
			//log.info(response.getStatusLine().getStatusCode());
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			responseBody = responseHandler.handleResponse(response);
			log.info(responseBody);
			all = parserQueryInfo(responseBody, rangDate, landDate); 
		}catch(UnknownHostException e){
			throw new NetConnectException(e);
		}catch (Exception e) {
			throw new UnRepairException(e);
		}
		log.debug("-------------------query train end-------------------");
		return all;
	}
	
	
	private String getSearchType(String ticketType){
		if("3".equals(ticketType)){
			return "0X00";
		}
		return "ADULT";
	}
	
	private String getSearchCode(String ticketType){
		if("3".equals(ticketType)){
			return "0X00";
		}
		return "00";
	}
	
	public List<TrainQueryInfo> parserQueryInfo(String response,
			String range,String land) throws JSONException {
		if(response == null  || response.isEmpty() || response.indexOf("{")==-1){
			return Collections.emptyList();
		}
		
		List<TrainQueryInfo> tqis = new ArrayList<TrainQueryInfo>();
		JSONObject rtn = new JSONObject(response);
		JSONArray data = getJSONArray(rtn,"data");
		for (int i = 0; data!=null && i < data.length(); i++) {

			TrainQueryInfo tqi = new TrainQueryInfo();
			JSONObject trainInfo = data.getJSONObject(i);
			JSONObject train = trainInfo.getJSONObject("queryLeftNewDTO");
			tqi.setStartTime(train.getString("start_time"));
			tqi.setArriveTime(getString(train,"arrive_time"));
			
			if(expireRange(range,tqi.getStartTime())){
				continue;
			}
			
			if(expireRange(land,tqi.getArriveTime())){
				continue;
			}
			
			tqi.setTrainNo(getString(train,"train_no"));
			tqi.setStationTrainCode(getString(train,"station_train_code"));
			tqi.setBest_seat(getString(train,"tz_num","--"));
			tqi.setOne_seat(getString(train,"zy_num","--"));
			tqi.setTwo_seat(getString(train,"ze_num","--"));
			tqi.setVag_sleeper(getString(train,"vag_sleeper","--"));
			tqi.setBuss_seat(getString(train,"swz_num","--"));
			tqi.setSoft_sleeper(getString(train,"rw_num","--"));
			tqi.setHard_sleeper(getString(train,"yw_num","--"));
			tqi.setSoft_seat(getString(train,"rz_num","--"));
			tqi.setHard_seat(getString(train,"yz_num","--"));
			tqi.setNone_seat(getString(train,"wz_num","--"));
			tqi.setOther_seat(getString(train,"qt_num","--"));
			
			tqi.setStartStationTelecode(getString(train,"start_station_telecode"));
			tqi.setStartStationName(getString(train,"start_station_name"));
			tqi.setEndStationName(getString(train,"end_station_name"));
			tqi.setEndStationTelecode(getString(train,"end_station_telecode"));
			tqi.setFromStationName(getString(train,"from_station_name"));
			tqi.setFromStationTelecode(getString(train,"from_station_telecode"));
			tqi.setToStationName(getString(train,"to_station_name"));
			tqi.setToStationTelecode(getString(train,"to_station_telecode"));
			
			tqi.setDayDifference(getString(train,"day_difference"));
			tqi.setTrainClassName(getString(train,"train_class_name"));
			tqi.setLishi(getString(train,"lishi"));
			tqi.setCanWebBuy(getString(train,"canWebBuy"));
			tqi.setLishiValue(getString(train,"lishiValue"));
			tqi.setControlDay(train.getInt("control_day"));
			tqi.setControlTrainDay(getString(train,"control_train_day"));
			tqi.setYpInfo(getString(train,"yp_info"));
			tqi.setStartTrainDate(getString(train,"start_train_date"));
			tqi.setSeatFeature(getString(train,"seat_feature"));
			tqi.setTrainSeatFeature(getString(train,"train_seat_feature"));
			tqi.setSeatTypes(getString(train,"seat_types"));
			tqi.setLocationCode(getString(train,"location_code"));
			tqi.setFromStationNo(getString(train,"from_station_no"));
			tqi.setToStationNo(getString(train,"to_station_no"));
			tqi.setSaleTime(getString(train,"sale_time"));
			tqi.setIsSupportCard(getString(train,"is_support_card"));
			
			tqi.setSecretStr(getString(trainInfo,"secretStr"));
			
			
			if(tqi.getTrainNo() != null){
						tqis.add(tqi);
				log.info(tqi.toString());
			}
					
		}
		return tqis;
	}

	//是否超期，超过 true
	private boolean expireRange(String range,String starttime){
		String[] ranges = range.split("--");
		DateFormat df = new SimpleDateFormat("HH:ss");
		try {
			long start = df.parse(ranges[0]).getTime();
			long end = df.parse(ranges[1]).getTime();
			long now = df.parse(starttime).getTime();
			if(now >= start && now <= end){
				return false;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return true;
	}
	/**
	 * 查询常用联系表
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public Page<UserInfo> loadContacts(int pageIndex,int pageSize) {
		log.debug("-------------------load contacts start-------------------");
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("pageIndex", String.valueOf(pageIndex)));
		parameters.add(new BasicNameValuePair("pageSize", String.valueOf(pageSize)));
		
		UrlEncodedFormEntity uef = new UrlEncodedFormEntity(parameters,Consts.UTF_8);
		
		HttpPost httppost = new HttpPost(Constants.TOP_CONTACTS_URL);
		httppost.setEntity(uef);
		httppost.setHeader("Referer","//kyfw.12306.cn/otn/passengers/init");
		httppost.setHeader("Host","kyfw.12306.cn");
		httppost.setHeader("Content-Type","application/x-www-form-urlencoded");
		httppost.setHeader("Accept-Language","zh-CN,zh");
		httppost.setHeader("Connection","keep-alive");
		httppost.setHeader("Accept-Charset","GBK,utf-8;q=0.7,*;q=0.3");
		
		String responseBody = null;
		Page<UserInfo> all = null;
		try {
			HttpResponse response = httpclient.execute(httppost);
			//log.info(response.getStatusLine().getStatusCode());
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			responseBody = responseHandler.handleResponse(response);
			log.info(responseBody);
			if(responseBody.startsWith("-")){
				checkIsLogin();
			}
			all = Util.parserUserInfo(responseBody); 
			all.setPageSize(pageSize);
			all.setCurrentPage(pageIndex);
		}catch(UnknownHostException e){
			throw new NetConnectException(e);
		}catch (Exception e) {
			throw new UnRepairException(e);
		}
		log.debug("-------------------query train end-------------------");
		return all;
		
	}
	
	/**
	 * 登录
	 * @param username
	 * @param password
	 * @param randCode
	 * @return
	 */
/*	loginRand	86
	loginUser.user_name	chenmgx
	nameErrorFocus	
	passwordErrorFocus	
	randCode	ttsf
	randErrorFocus	
	refundFlag	Y
	refundLogin	N
	user.password	saffsadfa
	*/
	
	
	public Result login(String username, String password, String randCode){
		log.debug("-----------------login validate-----------------------");
		Result rs = new Result();
		if(!checkRandCode(randCode)){
			rs.setState(Result.RAND_CODE_ERROR);
			return rs;
		}
		
		HttpPost httppost = new HttpPost(Constants.LOGIN_VALIDATE);
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("loginUserDTO.user_name", username));
		parameters.add(new BasicNameValuePair("randCode", randCode));
		parameters.add(new BasicNameValuePair("userDTO.password", password));
		String responseBody = null;
		HttpResponse response = null;
		try {
			UrlEncodedFormEntity uef = new UrlEncodedFormEntity(parameters,Consts.UTF_8);
			httppost.setEntity(uef);
			//OLD httppost.setHeader("Referer","https://dynamic.12306.cn/otsweb/loginAction.do?method=login");
			httppost.setHeader("Referer","https://kyfw.12306.cn/otn/login/init");
			
			//OLD httppost.setHeader("Host","dynamic.12306.cn");
			httppost.setHeader("Host","kyfw.12306.cn");
			//post.setHeader("Accept-Encoding","gzip,deflat"); 加了会乱码
			httppost.setHeader("Accept-Language","zh-CN,zh");
			httppost.setHeader("Connection","keep-alive");
			httppost.setHeader("Accept-Charset","utf-8");
			response = httpclient.execute(httppost);
			if(log.isDebugEnabled()){
				Header[] hds = response.getAllHeaders();
				for(Header hd : hds){
					log.debug("返回头部信息:"+hd.getName() + " = " + hd.getValue());
				}
			}
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			responseBody = responseHandler.handleResponse(response);
			log.debug(responseBody);
			JSONObject json = new JSONObject(responseBody);

			JSONObject dataObject = null;
			try {
				dataObject = getJSONObject(json,"data");
			} catch (Exception e) {
			}
			JSONArray msgs = json.getJSONArray("messages");
			if(dataObject != null && "Y".equals(getString(dataObject, "loginCheck"))){
				rs.setState(Result.SUCC);
				// 将Session信息到静态变量中，方便代理服务器获取
				List<Cookie> cookies = ((DefaultHttpClient) httpclient).getCookieStore().getCookies();
				for (Cookie cookie : cookies) {
					String name = cookie.getName();
					if ("JSESSIONID".equals(name)) {
						setjSessionId(cookie.getValue());
					} else if ("BIGipServerotn".equals(name)) {
						setBIGipServerotn(cookie.getValue());
					}
				}
			log.info("BIG = "+getBIGipServerotn()+"  SID = "+getjSessionId());
			}else{
				rs.setState(Result.FAIL);
				StringBuilder sb = new StringBuilder();
				
				for(int i =0; i<msgs.length();i++){
					sb.append(msgs.getString(i));
					sb.append(",");
				}
				rs.setMsg(sb.toString());
			}
			log.debug("------------------login end-----------------------");

		}catch(UnknownHostException e){
			throw new NetConnectException(e);
		}catch (Exception e) {
			throw new UnRepairException(e);
		}
		return rs;
	}
	
	
	private boolean checkRandCode(String randCode){
		HttpPost httppost = new HttpPost(Constants.CHECK_RAND);
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("randCode", randCode));
		parameters.add(new BasicNameValuePair("rand", "sjrand"));
		String responseBody = null;
		HttpResponse response = null;
		try {
			UrlEncodedFormEntity uef = new UrlEncodedFormEntity(parameters,Consts.UTF_8);
			httppost.setEntity(uef);
			//OLD httppost.setHeader("Referer","https://dynamic.12306.cn/otsweb/loginAction.do?method=login");
			httppost.setHeader("Referer","https://kyfw.12306.cn/otn/login/init");
			
			httppost.setHeader("Host","kyfw.12306.cn");
			httppost.setHeader("Accept-Language","zh-CN,zh");
			httppost.setHeader("Connection","keep-alive");
			httppost.setHeader("Accept-Charset","utf-8");
			response = httpclient.execute(httppost);
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			responseBody = responseHandler.handleResponse(response);
			log.debug(responseBody);
			JSONObject json = new JSONObject(responseBody);
			if("Y".equals(getString(json, "data"))){
				return true;
			}
		}catch(UnknownHostException e){
			throw new NetConnectException(e);
		}catch (Exception e) {
			throw new UnRepairException(e);
		}
		return false;
		
	}
	
	/**
	 * 查询预订信息
	 * 
	 * @param httpclient
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public Result queryOrder() {		
		log.debug("-------------------query order start-------------------");
		Result rs = new Result();
		HttpGet get = new HttpGet(Constants.QUERY_ORDER_URL);
		get.setHeader("Referer","https://kyfw.12306.cn/otn/confirmPassenger/initDc");
		get.setHeader("Host","kyfw.12306.cn");
		//post.setHeader("Accept-Encoding","gzip,deflat"); 加了会乱码
		get.setHeader("Accept-Language","zh-CN,zh");
		get.setHeader("Connection","keep-alive");
		try {
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String response = httpclient.execute(get,responseHandler);
			log.info(response);
			JSONObject json = new JSONObject(response);
			if(getBoolean(json, "status")){
				JSONObject data = null;
				 try {
					data = getJSONObject(json,"data");
				} catch (Exception e) {
				}
				if(data != null){
					rs.setState(Result.SUCC);
				}
			}
		}catch(UnknownHostException e){
			throw new NetConnectException(e);
		}catch (Exception e) {
			throw new UnRepairException(e);
		}
		log.debug("-------------------query order end---------------------");
		return rs;
	}

	/**
	 * 弹窗显示指定url的验证码并手工输入
	 * @param url
	 * @return
	 * @throws IOException
	 */
	
	String getCode(String url) throws IOException {
		JFrame frame = new JFrame("验证码");
		JLabel label = new JLabel(new ImageIcon(getCodeByte(url)),
				JLabel.CENTER);
		frame.add(label);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300, 200);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		String rd = br.readLine();
		frame.dispose();
		return rd;
	}

	/**
	 * 获取指定url的验证码图片字节信息
	 * @param url
	 * @return
	 */
	
	public byte[] getCodeByte(String url) {
		log.debug("-------------------get randcode start-------------------");
		HttpGet get = new HttpGet(url);
		if(Constants.LOGIN_CODE_URL.equals(url)){
			get.setHeader("Referer","https://kyfw.12306.cn/otn/login/init");
		}else{
			get.setHeader("Referer","https://kyfw.12306.cn/otn/login/init");
		}
		get.setHeader("Host","kyfw.12306.cn");
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
	
/*	private static int count = 0;
	private void writeToFolder(byte[] content){
		File f = new File("D:\\builder\\"
				+(count < 100 ? count < 10 ? "00"+count:"0"+count:count)
				+".jpg");
		count++;
		FileOutputStream fos = null;
		if(!f.exists()){
			try {
				f.createNewFile();
				fos = new FileOutputStream(f);
				fos.write(content);
				fos.flush();
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}finally{
				if(fos != null){
					try {
						fos.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		}
	}*/
	
	/**
	 * 获取指定url的Str字节信息
	 * @param url
	 * @return   此处用来返回一个随机数	 randstr
	 */
	public String  getStr(String url) {
		log.debug("-------------------get randstr start-------------------");
		String s="";
		HttpGet get = new HttpGet(url);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
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
		}catch(UnknownHostException e){
			throw new NetConnectException(e);
		}catch (Exception e) {
			throw new UnRepairException(e);
		}
		s = baos.toString();
		
		log.debug("-------------------get randstr end-------------------"+s.toString());
		return s;
	}
	
	public void clear(){
		if(httpclient != null)
			httpclient.getConnectionManager().shutdown();
	}
	
	public static void main(String[] args) throws JSONException {
/*		try {
			PoolingClientConnectionManager tcm = new PoolingClientConnectionManager();
			tcm.setMaxTotal(10);
			SSLContext ctx = SSLContext.getInstance("TLS"); 
			X509TrustManager tm = new X509TrustManager() {
				
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(
						java.security.cert.X509Certificate[] chain,
						String authType)
						throws java.security.cert.CertificateException {

				}

				public void checkServerTrusted(
						java.security.cert.X509Certificate[] chain,
						String authType)
						throws java.security.cert.CertificateException {
				}
			};
			ctx.init(null, new TrustManager[] { tm }, null);
			SSLSocketFactory ssf = new SSLSocketFactory(ctx,
					SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			Scheme sch = new Scheme("https", 443, ssf); 
			tcm.getSchemeRegistry().register(sch);
			HttpClient httpClient = new DefaultHttpClient(tcm);
			if (Config.isUseProxy()) {
				HttpHost proxy = new HttpHost(Config.getProxyIp(),
						Config.getProxyPort(), HttpHost.DEFAULT_SCHEME_NAME);
				httpClient.getParams().setParameter(
						ConnRoutePNames.DEFAULT_PROXY, proxy);
			}
			//this.httpClient.getParams().setParameter(HTTP.USER_AGENT,
			//		"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; TEN)"); 
			TrainClient client = new TrainClient(httpClient);
			//client.queryTrain("BJP","PXG","2013-10-08","00:00--24:00");
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}*/
		//System.out.println(expireRange("00:00--12:00", "12:01"));
	}

}