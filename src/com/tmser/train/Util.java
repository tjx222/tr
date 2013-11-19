package com.tmser.train;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Attribute;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.NodeVisitor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.tmser.train.bean.Page;
import com.tmser.train.bean.TrainQueryInfo;
import com.tmser.train.bean.UserInfo;

/**
 * 功能描述
 * 
 * @author Tmser
 * @since 2011-11-24
 * @version 1.0
 */
public class Util {
	
	private static Map<String, String> cityName2Code = new HashMap<String, String>();
	public static final Parser parser = new Parser();
	static {
		String city[] = Constants.CITYS.split("@");
		for (String tmp : city) {
			if (tmp.isEmpty())
				continue;
			String temp[] = tmp.split("\\|");
			cityName2Code.put(temp[1], temp[2]);
		}
	}
	public final static String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_PART_FORMAT = "yyyy-MM-dd";
	public static final String TIME_PART_FORMAT = "HH:mm:ss.SSS";

	public final static DateFormat default_date_format = new SimpleDateFormat(
			DATE_PART_FORMAT);

	/**
	 * 返回当前日期时间
	 * 
	 * @return e.g. 2006-06-06 12:12:50
	 */
	public static String getCurDateTime() {
		return getCurDateTime(DEFAULT_PATTERN);
	}
	
	/**
	 * 返回可以订票最后日期
	 * 
	 * @return e.g. 2006-06-06 12:12:50
	 */
	public static String getRoundDate() {
	    Calendar c = Calendar.getInstance();
	    c.setTime(new Date());
	    c.add(Calendar.DAY_OF_YEAR, 19);
	    SimpleDateFormat sdf = new SimpleDateFormat(DATE_PART_FORMAT);
		return sdf.format(c.getTime());
	}

	/**
	 * 返回当前日期
	 * 
	 * @return e.g. 2006-06-06
	 */
	public static String getCurDate() {
		return getCurDateTime(DATE_PART_FORMAT);
	}

	/**
	 * 根据给定的格式返回当前日期或时间
	 * 
	 * @param formatStr
	 * @return
	 */
	public static String getCurDateTime(String formatStr) {
		SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
		String now = sdf.format(new Date());
		return now;
	}

	public static String getCityCode(String cityName) {
		return cityName2Code.get(cityName);
	}

	/**
	 * 查询返回字符对象化
	 * 
	 * @param response
	 * @return
	 */
	public static List<TrainQueryInfo> parserQueryInfo(String response,
			String startDate) {
		List<TrainQueryInfo> tqis = new ArrayList<TrainQueryInfo>();
		response = response.replaceAll("&nbsp;", "");
		String st[] = response.split(",");
		for (int i = 0; i < st.length;) {
			if (st[i].trim().startsWith("<span")) {
				TrainQueryInfo tqi = new TrainQueryInfo();
				tqi.setTrainDate(startDate);
				try {
					i+=4;
					tqi.setBuss_seat(parserFont(st[i++]));
					tqi.setBest_seat(parserFont(st[i++]));
					tqi.setOne_seat(parserFont(st[i++]));
					tqi.setTwo_seat(parserFont(st[i++]));
					tqi.setVag_sleeper(parserFont(st[i++]));
					tqi.setSoft_sleeper(parserFont(st[i++]));
					tqi.setHard_sleeper(parserFont(st[i++]));
					tqi.setSoft_seat(parserFont(st[i++]));
					tqi.setHard_seat(parserFont(st[i++]));
					tqi.setNone_seat(parserFont(st[i++]));
					tqi.setOther_seat(parserFont(st[i++]));
					parserInput(st[i++], tqi);
					if(tqi.getTrainNo() != null){
						tqis.add(tqi);
					}
					
				} catch (ParserException e) {
					e.printStackTrace();
				}
			} else {
				i++;
			}
		}
		return tqis;
	}
	
	/**
	 * 查询返回字符对象化
	 * 
	 * @param response
	 * @return
	 */
	public static Page<UserInfo> parserUserInfo(String response) {
		List<UserInfo> tqis = new ArrayList<UserInfo>();
		int total = 0;
		if(response != null && response.startsWith("{")){
			try {
				JSONObject rs = new JSONObject(response);
				total = rs.getInt("recordCount");
				JSONArray contacts = rs.getJSONArray("rows");
				for(int i= 0;i<contacts.length();i++){
					JSONObject jo = contacts.getJSONObject(i);
					UserInfo u = new UserInfo();
					u.setCardType(jo.getString("passenger_id_type_code"));
					u.setID(jo.getString("passenger_id_no"));
					u.setName(jo.getString("passenger_name"));
					u.setPhone(jo.getString("mobile_no"));
					u.setTickType(jo.getString("passenger_type"));
					tqis.add(u);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return new Page<UserInfo>(tqis,total);
	}

	/**
	 * 解析font节点
	 * 
	 * @param font
	 * @return
	 * @throws ParserException
	 */
	private static String parserFont(String font) {
		String ans = font;
		if (font.startsWith("<font")) {
			int beginIndex = font.indexOf("'>");
			int endIndex = font.indexOf("</");
			ans = font.substring(beginIndex + 2, endIndex);
		}

		return ans;
	}

	/**
	 * 解析span节点
	 * 
	 * @param responseBody
	 * @param tqi
	 * @throws ParserException
	 */
	public static String parserTagValue(String responseBody,String tag,String name)
			throws ParserException {
		parser.setInputHTML(responseBody);
		NodeFilter tagfilter = new TagNameFilter(tag);
		NodeFilter attrfilter = new HasAttributeFilter("name", name );
		NodeFilter filter = new AndFilter(tagfilter, attrfilter);
        NodeList nodes = parser.extractAllNodesThatMatch(filter); 
/*        NodeVisitor visitor = new NodeVisitor() {
			public void visitTag(Tag tag) {
				System.out.println(tag.getAttribute("value"));
			}
        };*/
        String rs = "";
        if(nodes!=null) {
            for (int i = 0; i < nodes.size(); i++) {
                Tag textnode = (Tag) nodes.elementAt(i);
                rs = textnode.getAttribute("value");
            }
        }
        return rs;
	}

	/**
	 * 解析input节点
	 * 
	 * @param responseBody
	 * @param tqi
	 * @throws ParserException
	 */
	private static void parserInput(String responseBody, final TrainQueryInfo tqi)
			throws ParserException {
		parser.setInputHTML(responseBody);
		NodeVisitor visitor = new NodeVisitor() {
			public void visitTag(Tag tag) {
				Vector<?> atts = tag.getAttributesEx();
				for (int i = 0; i < atts.size(); i++) {
					Attribute att = (Attribute) atts.get(i);
					String name = att.getName();
					if ("onclick".equals(name)) {
						String temp[] = att.getValue().split("'");
						String subTemp[] = temp[1].split("#");
						tqi.setTrainNo(subTemp[0]);
						tqi.setTakeTime(subTemp[1]);
						tqi.setStartTime(subTemp[2]);
						tqi.setTrainCode(subTemp[3]);
						tqi.setFromStationCode(subTemp[4]);
						tqi.setToStationCode(subTemp[5]);
						tqi.setEndTime(subTemp[6]);
						tqi.setFromStation(subTemp[7]);
						tqi.setToStation(subTemp[8]);
						tqi.setFrom_station_no(subTemp[9]);
						tqi.setTo_station_no(subTemp[10]);
						tqi.setYpInfoDetail(subTemp[11]);
						tqi.setMmStr(subTemp[12]);
						tqi.setLocationCode(subTemp[13]);
						
					}
				}
			}
		};
		parser.visitAllNodesWith(visitor);
	}
	/**
	 * 移除html标签
	 * 
	 * @param content
	 * @return
	 */
	public static String removeTagFromHtml(String content) {
		// 定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script> }
		String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";
		// 定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style> }
		String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";
		// 定义HTML标签的正则表达式
		String regEx_html = "<[^>]+>";

		String temp = content;
		if (content == null || content.isEmpty()) {
			return "ERROR";
		}
		// 去除js
		temp = temp.replaceAll(regEx_script, "");
		// 去除style
		temp = temp.replaceAll(regEx_style, "");
		// 去除html
		temp = temp.replaceAll(regEx_html, "");
		// 合并空格
		temp = temp.replaceAll("\\s+", " ");

		return temp.trim();
	}

	public static String getMessageFromHtml(String content) {
//		String regEx_msg = "[\\s]*?var\\s+message\\s+=\\s+\"([\\S]*?)\"";
		String regEx_msg = "var\\s+message\\s+=\\s+\"([\\S|\\s]*?)\"";
		if (content == null) {
			return "ERROR";
		}
		String temp = content.trim();
		Pattern p = null;
		Matcher m = null;
		p = Pattern.compile(regEx_msg);
		m = p.matcher(temp);
		try {
			while (m.find()) {
				temp = m.group(1);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return temp;
	}

	public static String getLoginErrorMessage(String content) {
		int beginIndex = content.indexOf("换一张 &nbsp; ");
		int endIndex = content.indexOf(" &nbsp; &nbsp; 登录");
		String subStr = "ERROR";
		if (beginIndex + 11 < endIndex) {
			subStr = content.substring(beginIndex + 11, endIndex);
		}
		return subStr;
	}

	public static int getHour2Min(String hour) {
		int min = 0;
		String hm[] = hour.split(":");
		if (hm.length < 2) {
			min = Integer.parseInt(hour);
		} else {
			int h = Integer.parseInt(hm[0]) * 60;
			int m = Integer.parseInt(hm[1]);
			min = h + m;
		}
		return min;
	}

	public static String formatInfo(String info) {
		return getCurDateTime() + " : " + info + "\n";
	}
	
	
	public static String StrFormat(String pattern, Object... arguments) {
		Object argumentStr[] = new String[arguments.length];
		for (int i=0; i<argumentStr.length; i++) {
			argumentStr[i] = arguments[i].toString();
		}
		return MessageFormat.format(pattern, argumentStr);
	}

    public static boolean isBlank(String str)
    {
        int strLen;
        if(str == null || (strLen = str.length()) == 0)
            return true;
        for(int i = 0; i < strLen; i++)
            if(!Character.isWhitespace(str.charAt(i)))
                return false;

        return true;
    }

	public static void main(String[] args) throws Exception {
		//String html="0,<span id='id_240000T14500' class='base_txtdiv' onmouseover=javascript:onStopHover('240000T14500#BJP#PXG') onmouseout='onStopOut()'>T145</span>,<img src='/otsweb/images/tips/first.gif'>北京<br>12:09,萍乡<br>06:51,18:42,--,--,--,--,--,<font color='darkgray'>无</font>,<font color='darkgray'>无</font>,--,<font color='#008800'>有</font>,<font color='#008800'>有</font>,--,<input type='button' class='yuding_u' onmousemove=this.className='yuding_u_over' onmousedown=this.className='yuding_u_down' onmouseout=this.className='yuding_u' onclick=javascript:getSelected('T145#18:42#12:09#240000T14500#BJP#PXG#06:51#北京#萍乡#1*****30754*****00001*****04293*****0000#8950D43445E29A496A7F868A209DD2295FAFE47A61734FCF8D3B98E1') value='预订'></input>";
		//String s = "{\"errMsg\":\"Y\"}";
		//System.out.println(s.contains("Y"));
		//System.out.println(getRoundDate());
		//System.out.println(parserQueryInfo(html,"20121102").get(0).toString());
//		File f = new File("d:\\html.txt");
//		BufferedReader fis = new BufferedReader(new FileReader(f));
//		StringBuilder sb = new StringBuilder();
//		String line = null;
//		while((line = fis.readLine())!= null){
//			sb.append(line);
//		}
//		String response = "{\"recordCount\":8,\"rows\":[{\"address\":\"\",\"born_date\":{\"date\":6,\"day\":1,\"hours\":0,\"minutes\":0,\"month\":7,\"seconds\":0,\"time\":460569600000,\"timezoneOffset\":-480,\"year\":84},\"code\":\"1\",\"country_code\":\"CN\",\"email\":\"\",\"first_letter\":\"LSQ\",\"isUserSelf\":\"N\",\"mobile_no\":\"13120194361\",\"old_passenger_id_no\":\"\",\"old_passenger_id_type_code\":\"\",\"old_passenger_name\":\"\",\"passenger_flag\":\"0\",\"passenger_id_no\":\"360321198408065032\",\"passenger_id_type_code\":\"1\",\"passenger_id_type_name\":\"二代身份证\",\"passenger_name\":\"刘松青\",\"passenger_type\":\"1\",\"passenger_type_name\":\"成人\",\"phone_no\":\"\",\"postalcode\":\"\",\"recordCount\":\"8\",\"sex_code\":\"M\",\"sex_name\":\"男\",\"studentInfo\":null},{\"address\":\"\",\"born_date\":{\"date\":17,\"day\":2,\"hours\":11,\"minutes\":9,\"month\":0,\"seconds\":32,\"time\":1326769772493,\"timezoneOffset\":-480,\"year\":112},\"code\":\"2\",\"country_code\":\"\",\"email\":\"\",\"first_letter\":\"WDJ\",\"isUserSelf\":\"N\",\"mobile_no\":\"13671246705\",\"old_passenger_id_no\":\"\",\"old_passenger_id_type_code\":\"\",\"old_passenger_name\":\"\",\"passenger_flag\":\"0\",\"passenger_id_no\":\"411424198410249236\",\"passenger_id_type_code\":\"1\",\"passenger_id_type_name\":\"二代身份证\",\"passenger_name\":\"王东杰\",\"passenger_type\":\"1\",\"passenger_type_name\":\"成人\",\"phone_no\":\"\",\"postalcode\":\"\",\"recordCount\":\"8\",\"sex_code\":\"\",\"sex_name\":\"\",\"studentInfo\":null}]}";
//		List<UserInfo> ls = parserUserInfo(response);
//		for(UserInfo u : ls){
//			System.out.println(u.toString());
//		}
		//System.out.println(parserTagValue(sb.toString(),"input","org.apache.struts.taglib.html.TOKEN"));
	}
}
