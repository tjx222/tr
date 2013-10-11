package com.ywh.train;

import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;

/**
 * 资源管理器
 * @author Tmser
 * @since 2011-12-21 
 * @version 1.0
 */
public class ResManager {
	private static final String BUNDLE_NAME = "resources.ticketrob";
	private static final String IMAGES_PATH = "/resources/images/";
	protected static HashMap<String,MessageFormat> formats = new HashMap<String,MessageFormat>();
	
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private ResManager() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
	
	public static String getString(String key,Object[] args) {
		try {
			return formatMsg(RESOURCE_BUNDLE.getString(key),args);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}

	public static ImageIcon createImageIcon(String filename) {
		return new ImageIcon(getFileURL(filename));
	}
	
	public static URL getFileURL(String filename) {
		String path = IMAGES_PATH + filename;
		return ClassLoader.class.getResource(path);
	}
	
	private static String formatMsg(final String msg,Object[] args){
		String rs = "";
		MessageFormat format = null;
		if(msg!=null && !"".equals(msg.trim())){
			synchronized (formats) {
			    format = (MessageFormat) formats.get(msg);
				if (format == null) {
				    	format = new MessageFormat(msg);
				    	formats.put(msg, format);
				} 
				rs = format.format(args);
			}
		}
		return rs;
	}
}
