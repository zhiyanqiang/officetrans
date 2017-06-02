package com.netpower.library.util.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class PropertiesUtil {

	private Properties properties = null;
	private static final String defaultEncoding = "UTF-8";
	private String encode;
	
	public PropertiesUtil(String path) {
		properties = new Properties();
		try {
			properties.load(new FileInputStream(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getProperty(String key)
	{	
		String msg = properties.getProperty(key);
		if (null == msg || msg.length() < 1) {
			return "";
		}
		try {
			if(this.encode != null&&this.encode.length()>0)
			{
				return new String(msg.getBytes("ISO8859-1"), encode);
			}
			return new String(msg.getBytes("ISO8859-1"), defaultEncoding);
		} catch (UnsupportedEncodingException ex) {
			return null;
		}
	}
	
	public Map<String, String> getPropertys()
	{	
		Map<String, String> map = new HashMap<String, String>();
		Set<Object> keys = properties.keySet();
		if (null != keys) {
			for(Object key : keys)
			{
				map.put(String.valueOf(key), getProperty(String.valueOf(key)));
			}
		}
		return map;
	}
	
	public void setProperty(String key, String value)
	{	
		properties.setProperty(key, value);
	}
	
	public void save(OutputStream outputStream)
	{	
		try {
			properties.store(outputStream, "Copyright (C) SeeYon");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void save(String path)
	{
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(path);
			save(fileOutputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public String getEncode() {
		return encode;
	}

	public void setEncode(String encode) {
		this.encode = encode;
	}
}
