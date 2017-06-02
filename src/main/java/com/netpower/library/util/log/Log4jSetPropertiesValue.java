package com.netpower.library.util.log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.netpower.library.util.date.DateUtil;

/**
 * 程序异常日志文件类
 * @author ddn
 * 
 */
public class Log4jSetPropertiesValue implements ServletContextListener,Runnable{
	private ServletContextEvent contextEvent;
	
	public Log4jSetPropertiesValue() {
		
	}
	public Log4jSetPropertiesValue(ServletContextEvent contextEvent) {
		this.contextEvent = contextEvent;
	}

	public void contextDestroyed(ServletContextEvent arg0) {
		
	}

	public void contextInitialized(ServletContextEvent contextEvent) {
		Thread thread = new Thread(new Log4jSetPropertiesValue(contextEvent));
		thread.start();
	}
	
	public void createLog4j(ServletContextEvent contextEvent) {
		String path = Log4jSetPropertiesValue.class.getResource("/").getPath();//得到工程根目录
		try {
			path = URLDecoder.decode(path, "utf-8");
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		}
		String file = contextEvent.getServletContext().getInitParameter("log4jConfigLocation");//得到log4j.properties在根目录下的路径
		if(file.indexOf("classpath:") != -1) {//替换classpath:为空字符串
			file = file.replace("classpath:", "".trim());
		}
		Properties properties = new Properties();
		FileInputStream in = null;
		FileOutputStream out = null;
		File file2 = null;
		try {
			StringBuffer propertiesFileName = new StringBuffer(path);//old文件名
			//propertiesFileName.append("WEB-INF\\classes\\");
			propertiesFileName.append(file);
			file2 = new File(propertiesFileName.toString());//log4j.properties的全路径
			in = new FileInputStream(file2);
			properties.load(in);//加载properties文件
			
			String log4jPath = contextEvent.getServletContext().getRealPath("");
			String projectName = "";
			boolean iswin = System.getProperty("os.name").contains("Windows");
			if(iswin){
			    projectName = log4jPath.substring(log4jPath.lastIndexOf("\\")+1);
			    log4jPath = log4jPath.substring(0, log4jPath.lastIndexOf("\\"));
			    
			}
			else{
			    projectName = log4jPath.substring(log4jPath.lastIndexOf("/")+1);
			    log4jPath = log4jPath.substring(0, log4jPath.lastIndexOf("/"));
			    
			}
			StringBuffer logFileName = new StringBuffer(log4jPath.split("webapps")[0]);//日志文件路径
			logFileName.append(projectName).append("_Log4j/");
			logFileName.append(DateUtil.formatDateTime(new Date(),"yyyy-MM"));
			File file3 = new File(logFileName.toString());
			
			if(!file3.exists()) {
				file3.mkdirs();
			}
			logFileName.append("/")
			.append(DateUtil.formatDateTime(new Date(),"yyyy-MM-dd"))
			.append("_").append(projectName).append(".log");
			properties.setProperty("log4j.appender.logfile.File", logFileName.toString());
			new File(logFileName.toString()).createNewFile();
			
			out = new FileOutputStream(file2);
			properties.store(out, properties.toString());
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(out != null) {
					out.close();
				}
				if(in != null) {
					in.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public void run() {
		System.out.println("开始创建日志文件。。。。。");
		int temp = 1;
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		while(true) {
			try {
				this.createLog4j(this.contextEvent);
				if(temp == 1) {
					Date startDate = format.parse(format.format(new Date()));
					Date endDate = format.parse("23:59:59");
					long firstSleepTime = DateUtil.calculateTimesOfDatesBetween(startDate, endDate);
					Thread.sleep(firstSleepTime + 1);//休眠时间要多加1秒
					temp++;
				} else {
					Thread.sleep(86400);//休眠一天继续
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
	
	//public static void main(String[] args) {
	//	System.out.println(System.getProperties());
	//}
}
