package com.tmser.train.logic;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.tmser.train.Config;
import com.tmser.train.Constants;
import com.tmser.train.NetConnectException;
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
public class TrainClient {
	public static String JSESSIONID = null;
	public static String BIGipServerotsweb = null;
	private final static Logger log = Logger.getLogger(TrainClient.class);
	private HttpClient httpclient = null;
	
	/**
	 * 构造函数 
	 */
	public TrainClient(HttpClient client) {
		this.httpclient = client;
		client.getParams().setParameter(HTTP.USER_AGENT,
		"Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.4 (KHTML, like Gecko) Chrome/22.0.1229.94 Safari/537.4"); 

	}

	/**
	 * 获取令牌
	 * @return
	 */
	 
	public String getTokenAndLeftTicket(HttpEntity entity) {
		log.debug("-------------------get token start-------------------");
		//HttpGet get = new HttpGet(Constants.GET_TOKEN_URL);
		String token = "";
		BufferedReader br = null;
		String leftTicket = "";
		try {
			br = new BufferedReader(new InputStreamReader(
					entity.getContent() , "UTF-8"));
			String line = null;	
			StringBuilder content = new StringBuilder();
			while ((line = br.readLine()) != null) {
				content.append(line);
			}
			//log.info(content);
			token = Util.parserTagValue(content.toString(),"input","org.apache.struts.taglib.html.TOKEN");
			log.info("Token: "+ token);
			
			leftTicket = Util.parserTagValue(content.toString(),"input","leftTicketStr");
			log.info("LeftTicket: "+ leftTicket);
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
		return token+","+leftTicket;
	}

	/** 
	 * 预订车票

	 * @param rangDate
	 * @param startDate
	 * @param train
	 * @return
	 */
	public Result book(String rangDate, String startDate, TrainQueryInfo train) {		
		log.debug("-------------------book start-------------------");
		Result rs = new Result();
		HttpPost post = new HttpPost(Constants.BOOK_URL);
/* 参数列表
	station_train_code :T145
	train_date :2013-01-28
	seattype_num:
	from_station_telecode :BJP
	to_station_telecode :PXG
	include_student :00
	from_station_telecode_name :北京
	to_station_telecode_name :萍乡
	round_train_date :2013-01-28
	round_start_time_str :00:00--24:00
	single_round_type :1
	train_pass_type :QB
	train_class_arr :QB#D#Z#T#K#QT#
	start_time_str :00:00--24:00
	lishi :18:44
	train_start_time :12:09
	trainno4 :240000T1450S
	arrive_time :06:53
	from_station_name :北京
	to_station_name :萍乡
	from_station_no:01
	to_station_no:18
	ypInfoDetail :1*****30384*****00001*****00003*****0000
	mmStr :A56DFAF99B851F29E8D36198405F9987C0A6C50BE3DA92E6D069D7AD
	locationCode :P3
*/
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("from_station_telecode", train.getFromStationCode())); //"AOH"
		formparams.add(new BasicNameValuePair("from_station_telecode_name",	train.getFromStation()));//"上海"
		formparams.add(new BasicNameValuePair("include_student", "00"));
		formparams.add(new BasicNameValuePair("lishi", train.getTakeTime())); //"553"
		formparams.add(new BasicNameValuePair("round_start_time_str", rangDate)); //"00:00--24:00"
		formparams.add(new BasicNameValuePair("round_train_date", Util.getRoundDate())); //"2011-11-23"
		formparams.add(new BasicNameValuePair("seattype_num", ""));
		formparams.add(new BasicNameValuePair("single_round_type", "1"));
		formparams.add(new BasicNameValuePair("start_time_str", rangDate)); //"00:00--24:00"
		formparams.add(new BasicNameValuePair("station_train_code",	 train.getTrainNo())); //"T145"
		formparams.add(new BasicNameValuePair("to_station_telecode", train.getToStationCode())); //"CSQ"
		formparams.add(new BasicNameValuePair("to_station_telecode_name", train.getToStation())); //"长沙"
		
		formparams.add(new BasicNameValuePair("from_station_no", train.getFrom_station_no())); //01
		formparams.add(new BasicNameValuePair("to_station_no", train.getTo_station_no())); //18
		
		formparams.add(new BasicNameValuePair("train_class_arr", "QB#D#Z#T#K#QT#"));
		formparams.add(new BasicNameValuePair("train_date", startDate)); //"2011-11-28"
		formparams.add(new BasicNameValuePair("train_pass_type", "QB"));
		formparams.add(new BasicNameValuePair("train_start_time", train.getStartTime())); //"09:08"
		formparams.add(new BasicNameValuePair("trainno4",train.getTrainCode()));//2004T145
		formparams.add(new BasicNameValuePair("arrive_time", train.getEndTime()));
		formparams.add(new BasicNameValuePair("from_station_name", train.getFromStation()));
		formparams.add(new BasicNameValuePair("to_station_name", train.getToStation()));
		formparams.add(new BasicNameValuePair("ypInfoDetail", train.getYpInfoDetail()));
		formparams.add(new BasicNameValuePair("mmStr", train.getMmStr()));
		formparams.add(new BasicNameValuePair("locationCode", train.getLocationCode()));
		
		if(log.isInfoEnabled()){
			for(NameValuePair n:formparams){
				log.info(n.getName()+":"+ n.getValue());
			}
		}
		try {
			UrlEncodedFormEntity uef = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
			post.setEntity(uef);
			post.setHeader("Referer","https://dynamic.12306.cn/otsweb/order/querySingleAction.do?method=init");
			post.setHeader("Origin","https://dynamic.12306.cn");
			post.setHeader("Host","dynamic.12306.cn");
			//post.setHeader("Accept-Encoding","gzip,deflat"); 加了会乱码
			post.setHeader("Accept-Language","zh-CN,zh");
			post.setHeader("Connection","keep-alive");
			post.setHeader("Accept-Charset","GBK,utf-8;q=0.7,*;q=0.3");
			post.setHeader("Content-Type","application/x-www-form-urlencoded");
			//post.setHeader("Cookie",JSESSIONID+";"+BIGipServerotsweb);
			
			
			HttpResponse response = httpclient.execute(post);
			HttpEntity entity = response.getEntity();
			
			if(log.isDebugEnabled()){
				Header[] hds = response.getAllHeaders();
				for(Header hd : hds){
					log.debug("返回头部信息:"+hd.getName() + " = " + hd.getValue());
				}
			}
			
			int statusCode = response.getStatusLine().getStatusCode();
			// HttpClient对于要求接受后继服务的请求，象POST和PUT等不能自动处理转发
			// 301或者302
			if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY || 
				statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
				log.info(statusCode + " TO " );
			    // 从头中取出转向的地址
			    Header locationHeader = response.getFirstHeader("Location");
			    String location = null;
			    if (locationHeader != null) {
			     location = locationHeader.getValue();
			     log.info(location);
			     if(entity!=null)
			    	 entity.getContent().close();
			     HttpGet get = new HttpGet(location);
			     response = httpclient.execute(get);
			     entity =response.getEntity();
			    } else {
			     log.error("Location field value is null.");
			    }
			}
			
			log.info(statusCode + " 提交预定："+response.getStatusLine());
			
			/*br = new BufferedReader(new InputStreamReader(entity.getContent() , "UTF-8"));
			String line="";
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}*/
			rs.setMsg(getTokenAndLeftTicket(entity));
			rs.setState(Result.SUCC);
		}catch(UnknownHostException e){
			throw new NetConnectException(e);
		}catch (Exception e) {
			throw new UnRepairException(e);
		} 
		log.debug("-------------------book end-------------------");
		return rs;
		
	}

	public List<NameValuePair> setOrderForm(String randCode, TokenAndTicket token,final List<UserInfo> users, final TrainQueryInfo train){
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		int n=1;
		log.info("UserSize:"+users.size());
		/*
		 * org.apache.struts.taglib.html.TOKEN  :5ca4c22b419bfa1c9d888cb897364607
			leftTicketStr:1020103043405660000010201000003035500000
			textfield:中文或拼音首字母
			checkbox0:0
			orderRequest.train_date :2013-01-28
			orderRequest.train_no :240000T1450S
			orderRequest.station_train_code :T145
			orderRequest.from_station_telecode :BJP
			orderRequest.to_station_telecode :PXG
			orderRequest.seat_type_code :
			orderRequest.ticket_type_order_num:
			orderRequest.bed_level_order_num:000000000000000000000000000000
			orderRequest.start_time:12:09
			orderRequest.end_time:06:53
			orderRequest.from_station_name:北京
			orderRequest.to_station_name:萍乡
			orderRequest.cancel_flag:1
			orderRequest.id_mode:Y
			passengerTickets:1,undefined,1,陈艳锋,1,360321199007205043,15010757194,Y
			oldPassengers:陈艳锋,1,360321199007205043
			passenger_1_seat:1
			passenger_1_ticket:1
			passenger_1_name:陈艳锋
			passenger_1_cardtype:1
			passenger_1_cardno:360321199007205043
			passenger_1_mobileno:15010757194
			checkbox9:Y
			oldPassengers:
			checkbox9:Y
			oldPassengers:
			checkbox9:Y
			oldPassengers:
			checkbox9:Y
			oldPassengers:
			checkbox9:Y
			randCode :mddg
			orderRequest.reserve_flag :A
			tFlag :dc
		*/
		for (UserInfo user : users) {
			formparams.add(new BasicNameValuePair("checkbox9", "N"));
			formparams.add(new BasicNameValuePair("oldPassengers", user.getSimpleText())); 
			formparams.add(new BasicNameValuePair("passengerTickets", user.getText())); //
			
			formparams.add(new BasicNameValuePair("passenger_"+n+"_seat", user.getSeatType())); //"O"
			formparams.add(new BasicNameValuePair("passenger_"+n+"_seat_detail_select", "0"));
			formparams.add(new BasicNameValuePair("passenger_"+n+"_seat_detail", "0"));
			formparams.add(new BasicNameValuePair("passenger_"+n+"_ticket", user.getTickType())); //"1"
			formparams.add(new BasicNameValuePair("passenger_"+n+"_name", user.getName())); //
			formparams.add(new BasicNameValuePair("passenger_"+n+"_cardtype", user.getCardType())); //"1"
			formparams.add(new BasicNameValuePair("passenger_"+n+"_cardno",	user.getID())); //
			formparams.add(new BasicNameValuePair("passenger_"+n+"_mobileno", user.getPhone())); //
			n++;
		}
		
		for (int k=users.size()+1; k<=5; k++) {
			formparams.add(new BasicNameValuePair("passenger_"+k+"_seat_detail", "0"));
			formparams.add(new BasicNameValuePair("checkbox9", "N"));
			formparams.add(new BasicNameValuePair("oldPassengers", ""));
		}
		
		formparams.add(new BasicNameValuePair("orderRequest.bed_level_order_num", "000000000000000000000000000000"));
		formparams.add(new BasicNameValuePair("orderRequest.cancel_flag","1"));
		formparams.add(new BasicNameValuePair("orderRequest.end_time", train.getEndTime())); //"18:21"
		formparams.add(new BasicNameValuePair("orderRequest.from_station_name",	train.getFromStation())); //"上海虹桥"
		formparams.add(new BasicNameValuePair("orderRequest.from_station_telecode", train.getFromStationCode())); //"AOH"
		formparams.add(new BasicNameValuePair("orderRequest.id_mode", "Y")); //"Y"
		formparams.add(new BasicNameValuePair("orderRequest.reserve_flag", "A"));
		formparams.add(new BasicNameValuePair("orderRequest.seat_type_code", ""));
		formparams.add(new BasicNameValuePair("orderRequest.seat_detail_type_code", ""));
		formparams.add(new BasicNameValuePair("orderRequest.start_time", train.getStartTime()));//"09:08"
		formparams.add(new BasicNameValuePair("orderRequest.station_train_code", train.getTrainNo())); //"D105"
		formparams.add(new BasicNameValuePair("orderRequest.ticket_type_order_num", ""));
		formparams.add(new BasicNameValuePair("orderRequest.to_station_name", train.getToStation())); //"长沙"
		formparams.add(new BasicNameValuePair("orderRequest.to_station_telecode", train.getToStationCode())); //"CSQ"
		formparams.add(new BasicNameValuePair("orderRequest.train_date", train.getTrainDate()));  //"2011-11-28"
		formparams.add(new BasicNameValuePair("orderRequest.train_no", train.getTrainCode())); // "5l0000D10502"
		
		log.info("TokenAndLeftTicket: "+token);
		formparams.add(new BasicNameValuePair("org.apache.struts.taglib.html.TOKEN", token.getToken()));
		formparams.add(new BasicNameValuePair("leftTicketStr", token.getTicket()));
		formparams.add(new BasicNameValuePair("randCode", randCode.toUpperCase()));
		formparams.add(new BasicNameValuePair("orderRequest.reserve_flag", "A"));
		formparams.add(new BasicNameValuePair("tFlag", "dc"));
		
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
	public Result checkOrder(List<NameValuePair> formparams,String randCode) {
		log.debug("-------------------submit order start-------------------");
		Result rs = new Result();
		HttpPost post = new HttpPost(Constants.CHECK_ORDER_URL+randCode.toUpperCase());
		String responseBody = null;
		try {
			UrlEncodedFormEntity uef = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
			post.setEntity(uef);
			post.setHeader("Referer","https://dynamic.12306.cn/otsweb/order/confirmPassengerAction.do?method=init");
			post.setHeader("Origin","https://dynamic.12306.cn");
			post.setHeader("Host","dynamic.12306.cn");
			//post.setHeader("Accept-Encoding","gzip,deflat"); 加了会乱码
			post.setHeader("Accept-Language","zh-CN,zh");
			post.setHeader("Connection","keep-alive");
			post.setHeader("Accept-Charset","utf-8");
			
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			responseBody = httpclient.execute(post, responseHandler);
			JSONObject json = new JSONObject(responseBody);
			if(responseBody != null && "Y".equals(getString(json,"errMsg")) 
					&& !"N".equals(getString(json,"check608"))
					&& !"N".equals(getString(json,"checkHuimd"))){
				rs.setState(Result.SUCC);
				log.info("提交订单成功");
			}else{
				rs.setState(Result.FAIL);
				log.info("提交订单失败");
				log.info(responseBody);
			}
			rs.setMsg(responseBody);
		
//			String ans = Util.getMessageFromHtml(responseBody);
			
/*			if (ans.isEmpty()) {
				rs.setState(Result.UNCERTAINTY);
				rs.setMsg("好像订票成功了");
			} else {
				if (ans.contains("由于您取消次数过多")) {
					rs.setState(Result.CANCEL_TIMES_TOO_MUCH);
					rs.setMsg(ans);
				} else if (ans.contains("验证码不正确")){
					rs.setState(Result.RAND_CODE_ERROR);
					rs.setMsg(ans);
				} else if (ans.contains("售票实行实名制")){
					rs.setState(Result.REPEAT_BUY_TICKET);
					rs.setMsg(ans);
				} else if (ans.contains("号码输入有误")) {
					rs.setState(Result.ERROR_CARD_NUMBER);
					rs.setMsg(ans);
				} else {
					rs.setState(Result.OTHER);	
					rs.setMsg(ans);
				}
			}*/
		//	log.debug(ans);		
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
	public void checkIsLogin(){
		HttpGet get = new HttpGet(Constants.CHECK_LOGIN_URL);
		get.setHeader("Referer","https://dynamic.12306.cn/otsweb/order/confirmPassengerAction.do?method=init");
		get.setHeader("Host","dynamic.12306.cn");
		//post.setHeader("Accept-Encoding","gzip,deflat"); 加了会乱码
		get.setHeader("Accept-Language","zh-CN,zh");
		get.setHeader("Connection","keep-alive");
		get.setHeader("Accept-Charset","GBK,utf-8;q=0.7,*;q=0.3");
		String response;
		try {
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			response = httpclient.execute(get, responseHandler);
			log.debug(response);
			if(response !=  null && !response.contains(Constants.CHENPIAO_YUDING)){
				Constants.isLoginSuc = false;
			}
		}catch(UnknownHostException e){
			throw new NetConnectException(e);
		}catch (Exception e) {
			throw new UnRepairException(e);
		}
	}
	
	/**
	 * 退出登录
	 */
	public void loginOut(){
		HttpGet get = new HttpGet(Constants.LOGIN_OUT_URL);
		get.setHeader("Referer","https://dynamic.12306.cn/otsweb/order/confirmPassengerAction.do?method=init");
		get.setHeader("Host","dynamic.12306.cn");
		//post.setHeader("Accept-Encoding","gzip,deflat"); 加了会乱码
		get.setHeader("Accept-Language","zh-CN,zh");
		get.setHeader("Connection","keep-alive");
		get.setHeader("Accept-Charset","GBK,utf-8;q=0.7,*;q=0.3");
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
	public Result getCount(TokenAndTicket token,String seat, final TrainQueryInfo train) {
		log.debug("-------------------submit order start-------------------");
		Result rs = new Result();
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("train_date", train.getTrainDate()));
		parameters.add(new BasicNameValuePair("train_no",  train.getTrainCode()));
		
		parameters.add(new BasicNameValuePair("ticket", token.getTicket()));
		parameters.add(new BasicNameValuePair("seat", seat));
		parameters.add(new BasicNameValuePair("from", train.getFromStationCode()));
		parameters.add(new BasicNameValuePair("to", train.getToStationCode()));
		parameters.add(new BasicNameValuePair("station", train.getTrainNo()));
		
		HttpGet get = new HttpGet(Constants.GET_COUNT_URL+URLEncodedUtils.format(parameters, Consts.UTF_8));
		log.info(get.getURI().toASCIIString());
		String responseBody = null;
		try {
			get.setHeader("Referer","https://dynamic.12306.cn/otsweb/order/confirmPassengerAction.do?method=init");
			get.setHeader("Host","dynamic.12306.cn");
			//post.setHeader("Accept-Encoding","gzip,deflat"); 加了会乱码
			get.setHeader("Accept-Language","zh-CN,zh");
			get.setHeader("Connection","keep-alive");
			get.setHeader("Accept-Charset","utf-8");
			
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			responseBody = httpclient.execute(get, responseHandler);
			JSONObject json = null;
			if(responseBody != null){
				log.info(responseBody);
				json = new JSONObject(responseBody);
				String ticket = getString(json,"ticket");
				if(ticket != null){
					int tkcount = getTicketCountDesc(ticket, seat);
					if(tkcount > 0 ){
						rs.setState(Result.SUCC);
						rs.setMsg("Has Ticket");
						rs.setWaitTime(tkcount);
					}
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
	public Result queryOrderQueue(final List<NameValuePair> formparams) {
		log.debug("-------------------submit order start-------------------");
		Result rs = new Result();
		HttpPost post = new HttpPost(Constants.QUERY_ORDER_QUEE);
		log.info(post.getURI().toASCIIString());
		String responseBody = null;
		try {
			UrlEncodedFormEntity uef = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
			post.setEntity(uef);
			post.setHeader("Referer","https://dynamic.12306.cn/otsweb/order/confirmPassengerAction.do?method=init");
			post.setHeader("Host","dynamic.12306.cn");
			//post.setHeader("Accept-Encoding","gzip,deflat"); 加了会乱码
			post.setHeader("Accept-Language","zh-CN,zh");
			post.setHeader("Connection","keep-alive");
			post.setHeader("Accept-Charset","utf-8");
			
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			responseBody = httpclient.execute(post, responseHandler);
			JSONObject json = null;
			if(responseBody != null){
				log.info(responseBody);
				json = new JSONObject(responseBody);
				String errMsg = getString(json,"errMsg");
				if(errMsg != null){
					if("Y".equals(errMsg)){
						rs.setState(Result.SUCC);
						rs.setMsg("Has Ticket");
					}
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
	public Result queryWaitTime() {
		log.debug("-------------------submit order start-------------------");
		Result rs = new Result();

		HttpGet get = new HttpGet(Constants.SUBMIT_WAIT_URL);
		log.info(get.getURI().toASCIIString());
		String responseBody = null;
		try {
			get.setHeader("Referer","https://dynamic.12306.cn/otsweb/order/confirmPassengerAction.do?method=init");
			get.setHeader("Host","dynamic.12306.cn");
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
				int waitTime = json.getInt("waitTime");
				String orderid = getString(json,"orderId");
				
				//{"tourFlag":"dc","waitTime":5,"waitCount":1,"requestId":5695012781303200743,"count":0}
				if(!"".equals(orderid)){
						rs.setState(Result.SUCC);
						rs.setMsg(orderid);
				}else{
					rs.setMsg(getString(json, "msg"));
				}
				rs.setWaitTime(waitTime/60);
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
		String rt = "";
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

		if (seat_1 > -1) {
			rt += "<font color='red'>" + seat_1 + "</font>张";
		}
		if (seat_2 > -1) {
			rt += ",无座<font color='red'>" + seat_2 + "</font>张";
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
	public Result payOrder(final List<NameValuePair> formparams,String orderid) {
		log.debug("-------------------submit order start-------------------");
		Result rs = new Result();
		HttpPost post = new HttpPost(Constants.SUBMIT_URL+orderid);
		try {
			UrlEncodedFormEntity uef = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
			post.setEntity(uef);
			post.setHeader("Referer","https://dynamic.12306.cn/otsweb/order/confirmPassengerAction.do?method=init");
			post.setHeader("Origin","https://dynamic.12306.cn");
			post.setHeader("Host","dynamic.12306.cn");
			//post.setHeader("Accept-Encoding","gzip,deflat"); 加了会乱码
			post.setHeader("Accept-Language","zh-CN,zh");
			post.setHeader("Connection","keep-alive");
			post.setHeader("Accept-Charset","utf-8");
			
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String response = httpclient.execute(post, responseHandler);
			log.debug(response);
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
	public List<TrainQueryInfo> queryTrain(String from, String to, String startDate, String rangDate) {
		log.debug("-------------------query train start-------------------");
		if (rangDate == null || rangDate.isEmpty()) {
			rangDate = "00:00--24:00";
		}
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("method", "queryLeftTicket"));
		parameters.add(new BasicNameValuePair("orderRequest.train_date", startDate));
		parameters.add(new BasicNameValuePair("orderRequest.from_station_telecode", Util.getCityCode(from)));
		parameters.add(new BasicNameValuePair("orderRequest.to_station_telecode", Util.getCityCode(to)));
		parameters.add(new BasicNameValuePair("orderRequest.train_no", ""));
		parameters.add(new BasicNameValuePair("trainPassType", "QB"));
		parameters.add(new BasicNameValuePair("trainClass", "QB#D#Z#T#K#QT#"));
		parameters.add(new BasicNameValuePair("includeStudent", "00"));
		parameters.add(new BasicNameValuePair("seatTypeAndNum", ""));
		parameters.add(new BasicNameValuePair("orderRequest.start_time_str", rangDate));
		HttpGet get = new HttpGet(Constants.QUERY_TRAIN_URL + URLEncodedUtils.format(parameters, Consts.UTF_8));
		//log.info(Constants.QUERY_TRAIN_URL + URLEncodedUtils.format(parameters, Consts.UTF_8));
		get.setHeader("Referer","https://dynamic.12306.cn/otsweb/order/querySingleAction.do?method=init");
		get.setHeader("Host","dynamic.12306.cn");
		get.setHeader("Content-Type","application/x-www-form-urlencoded");
		get.setHeader("Accept-Language","zh-CN,zh");
		get.setHeader("Connection","keep-alive");
		get.setHeader("Accept-Charset","GBK,utf-8;q=0.7,*;q=0.3");
		
		String responseBody = null;
		List<TrainQueryInfo> all = Collections.emptyList();
		try {
			HttpResponse response = httpclient.execute(get);
			//log.info(response.getStatusLine().getStatusCode());
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			responseBody = responseHandler.handleResponse(response);
			if(responseBody.startsWith("-")){
				checkIsLogin();
			}
	//		log.info(responseBody);
			all = Util.parserQueryInfo(responseBody, startDate); 
//			for(TrainQueryInfo tInfo : all) {
//				System.out.println(tInfo);
//			}
		}catch(UnknownHostException e){
			throw new NetConnectException(e);
		}catch (Exception e) {
			throw new UnRepairException(e);
		}
		log.debug("-------------------query train end-------------------");
		return all;
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
		parameters.add(new BasicNameValuePair("pageIndex", pageIndex+""));
		parameters.add(new BasicNameValuePair("pageSize", pageSize+""));
		
		UrlEncodedFormEntity uef = new UrlEncodedFormEntity(parameters,Consts.UTF_8);
		
		HttpPost httppost = new HttpPost(Constants.TOP_CONTACTS_URL);
		httppost.setEntity(uef);
		httppost.setHeader("Referer","https://dynamic.12306.cn/otsweb/passengerAction.do?method=initUsualPassenger12306");
		httppost.setHeader("Host","dynamic.12306.cn");
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
			//log.info(responseBody);
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
	
	public Result login(String username, String password, String randCode,String randstr) {
		log.debug("-----------------login start-----------------------");
		Result rs = new Result();
		HttpPost httppost = new HttpPost(Constants.LOGIN_URL);
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("method", "login"));
		parameters.add(new BasicNameValuePair("loginRand", randstr));
		parameters.add(new BasicNameValuePair("loginUser.user_name", username));
		parameters.add(new BasicNameValuePair("nameErrorFocus", ""));
		parameters.add(new BasicNameValuePair("passwordErrorFocus", ""));
		parameters.add(new BasicNameValuePair("randCode", randCode));
		parameters.add(new BasicNameValuePair("randErrorFocus", ""));
		parameters.add(new BasicNameValuePair("refundFlag", "Y"));
		parameters.add(new BasicNameValuePair("refundLogin", "N"));
		parameters.add(new BasicNameValuePair("user.password", password));
		String responseBody = null;
		HttpResponse response = null;
		try {
			UrlEncodedFormEntity uef = new UrlEncodedFormEntity(parameters,Consts.UTF_8);
			httppost.setEntity(uef);
			httppost.setHeader("Referer","https://dynamic.12306.cn/otsweb/loginAction.do?method=login");
			httppost.setHeader("Host","dynamic.12306.cn");
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
			//int statusCode = responseHandler.;
			String info = Util.removeTagFromHtml(responseBody);
			//log.info(info);
			log.debug("-----------------------------------------------------\n\n\n\n\n");
		 if(responseBody.contains(Constants.USER_NOT_EXIST)){
				log.error("用户:"  + username + Constants.USER_NOT_EXIST);
				rs.setState(Result.ACC_ERROR);
				rs.setMsg(Constants.USER_NOT_EXIST);
			}else if(responseBody.contains(Constants.USER_PWD_ERR)){
				log.error("用户:"  + username + Constants.USER_PWD_ERR);
				rs.setState(Result.PWD_ERROR);
				rs.setMsg(Constants.USER_PWD_ERR);
			}else if (info.contains(Constants.USER_SUCC_INFO)) {
				int index = responseBody.indexOf("-->");
				log.debug(responseBody.substring(index + 4));
				rs.setState(Result.SUCC);
				rs.setMsg(Constants.LOGIN_SUC);
				
				// 将Session信息到静态变量中，方便代理服务器获取
				List<Cookie> cookies = ((DefaultHttpClient) httpclient).getCookieStore().getCookies();
				for (Cookie cookie : cookies) {
					String name = cookie.getName();
					if ("JSESSIONID".equals(name)) {
						JSESSIONID = cookie.getValue();
					} else if ("BIGipServerotsweb".equals(name)) {
						BIGipServerotsweb = cookie.getValue();
					}
				}
				log.info("JSESSIONID=" + TrainClient.JSESSIONID + ",BIGipServerotsweb=" + TrainClient.BIGipServerotsweb);
			}else if(responseBody.contains(Constants.CODE_ERROR)){
				log.warn("用户:"  + username + Constants.CODE_ERROR);
				rs.setState(Result.RAND_CODE_ERROR);
				rs.setMsg(Constants.CODE_ERROR);
			}else if(responseBody.contains(Constants.LOGIN_LOSTS_POEPLE)){
				log.info("用户:"  + username + Constants.LOGIN_LOSTS_POEPLE);
				rs.setState(Result.LOST_OF_PEOPLE);	
				rs.setMsg(Constants.LOGIN_LOSTS_POEPLE);
			}else{
				log.info("用户:"  + username + Constants.UNKNOW_ERROR);
				rs.setState(Result.OTHER);
				rs.setMsg(Constants.UNKNOW_ERROR);
				log.info(responseBody);
			}
		}catch(UnknownHostException e){
			throw new NetConnectException(e);
		}catch (Exception e) {
			throw new UnRepairException(e);
		}
		log.debug("-------------------login end---------------------");
		return rs;
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
		get.setHeader("Referer","https://dynamic.12306.cn/otsweb/order/querySingleAction.do?method=init");
		get.setHeader("Host","dynamic.12306.cn");
		//post.setHeader("Accept-Encoding","gzip,deflat"); 加了会乱码
		get.setHeader("Accept-Language","zh-CN,zh");
		get.setHeader("Connection","keep-alive");
		get.setHeader("Accept-Charset","GBK,utf-8;q=0.7,*;q=0.3");
		StringBuilder responseBody = new StringBuilder();
		BufferedReader br = null;
		try {
			HttpResponse response = httpclient.execute(get);
			HttpEntity entity = response.getEntity();
			br = new BufferedReader(new InputStreamReader(
					entity.getContent(), "UTF-8"));
			String line = null;
			while ((line = br.readLine()) != null) {
				responseBody.append(line);
			}
			String msg = Util.removeTagFromHtml(responseBody.toString());
//			System.out.println(msg);
			if (!msg.isEmpty()) {
				int index = msg.indexOf("-->");
				msg = msg.substring(index + 4);
				String[] allInfo = msg.split("！");
				if (allInfo.length > 1) {
					String usefulInfo = allInfo[1];
//					System.out.println(usefulInfo);
					if (usefulInfo.contains("取消订单")) {
						rs.setState(Result.HAVE_NO_PAY_TICKET);
						rs.setMsg(usefulInfo);
					} else if (usefulInfo.contains("取消次数过多")) {
						rs.setState(Result.CANCEL_TIMES_TOO_MUCH);
						rs.setMsg(usefulInfo);
					} else {
						rs.setMsg(usefulInfo);
					}
				} else {
					rs.setState(Result.NO_BOOKED_TICKET);
					rs.setMsg(msg);
				}
			} else {
				rs.setMsg(msg);
			}
		}catch(UnknownHostException e){
			throw new NetConnectException(e);
		}catch (Exception e) {
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
			get.setHeader("Referer","https://dynamic.12306.cn/otsweb/order/confirmPassengerAction.do?method=init");
		}else{
			get.setHeader("Referer","https://dynamic.12306.cn/otsweb/order/querySingleAction.do?method=init");
		}
		get.setHeader("Host","dynamic.12306.cn");
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
	
	public static void main(String[] args) {
		try {
			PoolingClientConnectionManager tcm = new PoolingClientConnectionManager();
			tcm.setMaxTotal(10);
			//**
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
			// */
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
			client.queryTrain("BJP","PXG","2013-10-08","00:00--24:00");
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private String getString(JSONObject json, String key){
			try {
				return json.getString(key) == null ? "" : json.getString(key);
			} catch (JSONException e) {
				return "";
			}
		}
}