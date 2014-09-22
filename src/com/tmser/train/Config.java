package com.tmser.train;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Properties;

/**
 * 功能描述
 * @author Tmser
 * @since 2013-10-10 
 * @version 1.0
 */
public class Config {
	private static Properties prop = new Properties();
	private static final String DEFAULT_PROPERTIES = "config.properties";
	
	static {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(DEFAULT_PROPERTIES);
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
	
	public static void setProperty(String key,String value) {
		prop.setProperty(key,value);
	}
	
	public static boolean isUseProxy() {
		return "true".equals(getProperty("proxy.set"));
	}
	
	public static void setUseProxy(Boolean isUseProxy){
		prop.setProperty("proxy.set", isUseProxy.toString());
	}
	
	public static boolean isUseDama() {
		return "true".equals(getProperty("dama.set")) 
				&& !Util.isBlank(getUsername())	&& !Util.isBlank(getPassword());
	}
	
	public static void setUseDama(Boolean isUseDama){
		prop.setProperty("dama.set", isUseDama.toString());
	}
	
	public static String getProxyIp() {
		return getProperty("proxy.host");
	}
	
	public static void setProxyIp(String ip){
		prop.setProperty("proxy.host", ip);
	}
	
	public static int getProxyPort() {
		return Integer.parseInt(getProperty("proxy.port"));
	}
	
	public static void setProxyPort(String port){
		prop.setProperty("proxy.port", port);
	}
	
	public static int getSleepTime() {
		return Integer.parseInt(getProperty("rob.sleeptime"));
	}
	
	public static String getPassword() {
		return getProperty("dama.password").trim();
	}
	
	public static void setPassword(String pass){
		prop.setProperty("dama.password", pass);
	}
	
	public static String getUsername() {
		return getProperty("dama.username").trim();
	}
	
	public static void setUsername(String pass){
		prop.setProperty("dama.username", pass);
	}
	
	public static int getPageSize() {
		int pageSize = 10;
		try{
			pageSize =Integer.parseInt(getProperty("rob.pageSize"));
		}catch (Exception e) {
			// use default
		}
		return pageSize;
	}

    
    /**
     * 保存配置文件
     * @return true 成功
     */
    public static boolean saveConfig() {
    	 boolean rs = false;
         OutputStream fos = null;
         File file = null;
         try {
        	 file = new File(DEFAULT_PROPERTIES);
             if (!file.exists())
                 file.createNewFile();
             fos = new FileOutputStream(file);
             prop.store(fos,"");
             rs = true;
         } catch (IOException e) {
             System.err.println("Visit " + DEFAULT_PROPERTIES + " for updating   error");
         } finally{
             try {
                 if(fos!=null)fos.close();
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }
         return rs;
    }
    
	public static void main(String[] args) {
	}
}
