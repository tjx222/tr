package com.ywh.train;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * 功能描述
 * @author Tmser
 * @since 2013-10-10 
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
	
	public static boolean isUseDama() {
		return "true".equals(getProperty("damaset"));
	}
	
	public static String getProxyIp() {
		return getProperty("proxyhost");
	}
	
	public static int getProxyPort() {
		return Integer.parseInt(getProperty("proxyport"));
	}
	
	public static int getSleepTime() {
		return Integer.parseInt(getProperty("sleeptime"));
	}
	
	public static String getPassword() {
		return getProperty("dama.password").trim();
	}
	
	public static String getUserName() {
		return getProperty("dama.username").trim();
	}
	
	public static int getPageSize() {
		int pageSize = 10;
		try{
			pageSize =Integer.parseInt(getProperty("pageSize"));
		}catch (Exception e) {
			// use default
		}
		return pageSize;
	}

	public static void main(String[] args) {
		System.out.println(isUseProxy());
		System.out.println(getProxyIp());
		System.out.println(getProxyPort());
		System.out.println(getSleepTime());
		System.out.println(getPassword());
	}
}
