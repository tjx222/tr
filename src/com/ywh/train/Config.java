package com.ywh.train;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * 功能描述
 * @author YAOWENHAO
 * @since 2011-12-20 
 * @version 1.0
 */
public class Config {
	private static Properties prop = new Properties();
	
	static {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream("config.properties");
			InputStreamReader isr = new InputStreamReader(fis,"UTF-8");
			prop.load(isr);
			isr.close();
		} catch (IOException e) {
			System.err.println("加载配置文件出错  " + e);
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static String getProperty(String key) {
		return prop.getProperty(key,"");
	}
	
	public static boolean isUseProxy() {
		return "true".equals(getProperty("proxyset"));
	}
	
	public static String getProxyIp() {
		return getProperty("proxyhost");
	}
	
	public static int getProxyPort() {
		return Integer.parseInt(getProperty("proxyport"));
	}
	
	public static String getTrainCode() {
		return getProperty("traincode").trim();
	}
	
	public static int getSleepTime() {
		return Integer.parseInt(getProperty("sleeptime"));
	}
	
	public static String getAccount() {
		return getProperty("account").trim();
	}
	
	public static String getPassword() {
		return getProperty("password").trim();
	}
	
	public static String[] getUserInfoNames() {
		String s = getProperty("userinfonames").trim();
		if(s.equals("")) return null;
		else return s.split("\\|");
	}
	
	public static String[] getUserInfoIDs() {
		String s = getProperty("userinfoids").trim();
		if(s.equals("")) return null;
		else return s.split("\\|");
	}
	
	public static String[] getUserInfoPhones() {
		String s = getProperty("userinfophones").trim();
		if(s.equals("")) return null;
		else return s.split("\\|");
	}
	
	public static String[] getUserInfoCardTypes() {
		String s = getProperty("userinfocardtype").trim();
		if(s.equals("")) return null;
		else return s.split("\\|");
	}
	
	public static String getRangTime() {
		return getProperty("rangtime").trim();
	}
	
	public static String getTicketType() {
		return getProperty("tickettype").trim();
	}
	
	public static String getStartDate() {
		return getProperty("date").trim();
	}
	
	public static String getFromStation() {
		return getProperty("from").trim();
	}
	
	public static String getToStation() {
		return getProperty("to").trim();
	}
	
	public static void main(String[] args) {
		System.out.println(isUseProxy());
		System.out.println(getProxyIp());
		System.out.println(getProxyPort());
		System.out.println(getSleepTime());
		System.out.println(getTrainCode());
		System.out.println(getAccount());
		System.out.println(getPassword());
		System.out.println(getUserInfoNames());
		System.out.println(getUserInfoIDs());
		System.out.println(getUserInfoPhones());
		System.out.println(getUserInfoCardTypes());
		System.out.println(getRangTime());
		System.out.println(getStartDate());
		System.out.println(getFromStation());
		System.out.println(getToStation());
	}
}
