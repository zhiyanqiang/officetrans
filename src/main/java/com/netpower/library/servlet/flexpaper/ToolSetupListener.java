package com.netpower.library.servlet.flexpaper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.netpower.library.util.Common;
import com.netpower.library.util.config.Config;
import com.netpower.library.util.os.ServerUitl;
import com.seeyon.ctp.officetransservice.timer.DeleteOfficeTransTask;
/**
 * Servlet启动时文件转换服务需要完成一些初始化工作
 * 文件名称：ToolSetupListener.java    
 * 创建人：zhiyanqiang  
 * 创建时间：2017年1月12日
 */
public class ToolSetupListener  implements ServletContextListener {
	private static final Log log = LogFactory.getLog(ToolSetupListener.class);
	public void contextInitialized(ServletContextEvent contextEvent) {
		Config.AppPath = contextEvent.getServletContext().getRealPath("/").replace("\\", "/");//记录应用程序路径
		String classPathInJar = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();//jar所在路径
		log.info("classPath="+classPathInJar);
		String[] jarPathArray=classPathInJar.split("!");
		String jarPath=jarPathArray[0];
		File seeyonOfficeTransRootDir=null;
		File DLLDir=null;
		try{
			URL jarURL=new URL(jarPath);
			File jarFile=new File(jarURL.toURI());
			seeyonOfficeTransRootDir=jarFile.getParentFile();
			DLLDir=new File(seeyonOfficeTransRootDir,"dll");
			Config.xpdfPath=new File(DLLDir,"xpdf").getAbsolutePath();
		}
		catch(Exception e)
		{
			log.error(e.fillInStackTrace());
		}
		//log.info("SeeyonOfficeTransRootDir="+ seeyonOfficeTransRootDir.toString());
		File configureFileDir=new File(seeyonOfficeTransRootDir,"config");
		File configureFile=new File(configureFileDir,"SeeyonOfficeTrans.properties");
		//获取配置文件
		//String configPath = contextEvent.getServletContext().getRealPath("/WEB-INF/classes/config/JLibrary.properties");
		//InputStream input=this.getClass().getClassLoader().getResourceAsStream("config/JLibrary.properties");
		//PropertiesUtil propertiesUtil = new PropertiesUtil(configPath);
		FileInputStream input;
		try {
			input = new FileInputStream(configureFile);
		} catch (FileNotFoundException e2) {
				log.error("！！！！！！！！SeeyonOfficeTrans.properties配置文件不存在,请联系管理员！");
				log.error(e2.fillInStackTrace());
				return;
		}
		Properties propertiesUtil=new Properties();
		try {
			propertiesUtil.load(input);
			Config.OpenOffice_SERVER_IP = propertiesUtil.getProperty("OpenOffice_Server_IP");
			Config.OpenOffice_SERVER_PORT = propertiesUtil.getProperty("OpenOffice_Server_Port");
			Config.OpenOffice_TOOL_PATH=propertiesUtil.getProperty("OpenOffice_TOOL_PATH");
			Config.PDF2SWF_TOOL_PATH=propertiesUtil.getProperty("PDF2SWF_TOOL_PATH");
			Config.PDF2JSON_TOOL_PATH=propertiesUtil.getProperty("PDF2JSON_TOOL_PATH");
			Config.OfficeTrans_Root_Dir=propertiesUtil.getProperty("OfficeTrans_Root_Dir");
			String maxThreadStr=propertiesUtil.getProperty("Office_Max_Thread");
			Config.File_Retains_Day=propertiesUtil.getProperty("File_Retains_Day");
			int maxThread=10;
			if(maxThreadStr!=null)
			{
				maxThread=Integer.valueOf(maxThreadStr);
			}
			Config.cachedThreadPool=Executors.newFixedThreadPool(maxThread);
			log.info("Office转换的根目录是:" + Config.OfficeTrans_Root_Dir);
			if(Config.isWin()){
				//Config.OpenOffice_TOOL_PATH = "C:\\Program Files (x86)\\LibreOffice 5\\program\\";
				//Config.PDF2SWF_TOOL_PATH	= "C:\\Program Files\\SWFTools\\";
				//Config.PDF2JSON_TOOL_PATH 	= "C:\\Program Files\\PDF2JSON\\";
				
				if(!Common.doc2pdfEnabled(Config.OpenOffice_TOOL_PATH + "soffice.exe")){
					//Config.OpenOffice_TOOL_PATH = "C:\\Program Files (x86)\\OpenOffice 4\\program\\";
					if(!Common.doc2pdfEnabled(Config.OpenOffice_TOOL_PATH + "soffice.exe")){
						Config.OpenOffice_TOOL_PATH = propertiesUtil.getProperty("OpenOffice_TOOL_PATH");
					}
				}
				
				if(!Common.pdf2swfEnabled(Config.PDF2SWF_TOOL_PATH + "pdf2swf.exe")){
					//Config.PDF2SWF_TOOL_PATH = "C:\\Program Files (x86)\\SWFTools\\";
					if(!Common.pdf2swfEnabled(Config.PDF2SWF_TOOL_PATH + "pdf2swf.exe")){
						Config.PDF2SWF_TOOL_PATH = propertiesUtil.getProperty("PDF2SWF_TOOL_PATH");
					}
				}
				log.info("SWFTools软件安装的路径是："+Config.PDF2SWF_TOOL_PATH);
				
				if(!Common.pdf2jsonEnabled(Config.PDF2JSON_TOOL_PATH + "pdf2json.exe")){
					//Config.PDF2JSON_TOOL_PATH = "C:\\Program Files (x86)\\PDF2JSON\\";
					if(!Common.pdf2jsonEnabled(Config.PDF2JSON_TOOL_PATH + "pdf2json.exe")){
						Config.PDF2JSON_TOOL_PATH = propertiesUtil.getProperty("PDF2JSON_TOOL_PATH");
					}
				}
				//判断OpenOffice服务soffice.exe或者soffice.bin是否已经启动如果没有启动，就开启该服务
	        	if(!ServerUitl.isRunningApp("soffice."))
	        	{
	        		log.info("正在尝试启动，OpenOffice软件,文档转换服务...");
	        		//转换前先启动OpenOffice文档转换服务
	        		try{
		        		Common.startOpenOfficeServer(Config.OpenOffice_TOOL_PATH + "soffice.exe");
		        		log.info("OpenOffice软件,文档转换服务启动成功！该服务的IP是："+Config.OpenOffice_SERVER_IP+";服务端口为："+Config.OpenOffice_SERVER_PORT);
	        		}catch(Exception e)
	        		{
	        			log.error("OpenOffice软件,文档转换服务启动失败！错误原因："+e.fillInStackTrace());
	        		}
	        	}else
	        	{
	        		log.info("OpenOffice软件,文档转换服务已经启动！");
	        	}
	        	/***********************Start 配置采用jacob.jar、jacob.dll动态链接库技术调用MS Office、WPS、PdfCreator等软件进行文档转换*********************/
	        	//System.out.println("当前JVM的类库路径是:"+System.getProperty("java.library.path"));
	        	//URL x86DLLFileURL=this.getClass().getClassLoader().getResource("config/jacob/jacob-1.17-M2-x86.dll");
	        	File x86DLLFile=new File(DLLDir,"jacob-1.17-M2-x86.dll");
	        	File x86DLLFile2=new File(DLLDir,"jacob-1.18-x86.dll");
	        	//URL x64DLLFileURL=this.getClass().getClassLoader().getResource("config/jacob/jacob-1.17-M2-x64.dll");
	        	File x64DLLFile=new File(DLLDir,"jacob-1.17-M2-x64.dll");
	        	File x64DLLFile2=new File(DLLDir,"jacob-1.18-x64.dll");
	        	String libraryPath = System.getProperty("java.library.path");
	        	if(libraryPath.contains(";"))
	        	{//取出的值可能存在多个，比如jvm;tomcat等
	        		String[] array = libraryPath.split(";");
	        		for(String str : array)
	        		{
	        			if(str.contains("jdk"))
	        			{//找到jvm的路径,这个更加保险
	        				libraryPath = str;
	        				break;
	        			}
	        		}
	        	}
	        	String jacob32Dll = "jacob-1.17-M2-x86.dll";
	        	String jacob32Dll2 = "jacob-1.18-x86.dll";
	        	String jacob64Dll = "jacob-1.17-M2-x64.dll";
	        	String jacob64Dll2 = "jacob-1.18-x64.dll";
	        	FileUtils.copyFile(x86DLLFile, new File(libraryPath,jacob32Dll));
	        	FileUtils.copyFile(x64DLLFile, new File(libraryPath,jacob64Dll));
	        	//FileUtil.copeFile(jacobPath, libraryPath, jacob32Dll, jacob32Dll);
	        	//FileUtil.copeFile(jacobPath, libraryPath, jacob64Dll, jacob64Dll);
	        	
	        	String System32Path = "C:\\Windows\\System32";
	        	String SysWOW64Path = "C:\\Windows\\SysWOW64";
	        	//FileUtil.copeFile(jacobPath, System32Path, jacob32Dll, jacob32Dll);
	        	//FileUtil.copeFile(jacobPath, System32Path, jacob64Dll, jacob64Dll);
	        	//FileUtil.copeFile(jacobPath, SysWOW64Path, jacob64Dll, jacob64Dll);
	        	FileUtils.copyFile(x86DLLFile, new File(System32Path,jacob32Dll));
	        	FileUtils.copyFile(x86DLLFile, new File(SysWOW64Path,jacob32Dll));
	        	FileUtils.copyFile(x64DLLFile, new File(SysWOW64Path,jacob64Dll));
	        	FileUtils.copyFile(x64DLLFile, new File(System32Path,jacob64Dll));
	        	
	        	FileUtils.copyFile(x86DLLFile2, new File(System32Path,jacob32Dll2));
	        	FileUtils.copyFile(x86DLLFile2, new File(SysWOW64Path,jacob32Dll2));
	        	FileUtils.copyFile(x64DLLFile2, new File(SysWOW64Path,jacob64Dll2));
	        	FileUtils.copyFile(x64DLLFile2, new File(System32Path,jacob64Dll2));
	        	/***********************End 配置采用jacob.jar、jacob.dll动态链接库技术调用MS Office、WPS、PdfCreator等软件进行文档转换*********************/
	        	
			}
			
			//装载字体文件
//			log.info("装载文档转换所需的特殊中文字体...");
//			try {
//				Map configs = new HashMap();
//				configs.put("AppPath", Config.AppPath.subSequence(0, Config.AppPath.length()-1));
//				//String xpdfrc = contextEvent.getServletContext().getRealPath("/WEB-INF/classes/config/xpdf/xpdfrc");
//				URL xpdfrcURL=this.getClass().getClassLoader().getResource("config/xpdf/xpdfrc");
//				String xpdfrc=new File(xpdfrcURL.toURI()).getAbsolutePath();
//				Config.writeConfig(xpdfrc, xpdfrc, configs);
//				
//				URL sample_xpdfrcURL=this.getClass().getClassLoader().getResource("config/xpdf/sample-xpdfrc");
//				String sample_xpdfrc =new File(sample_xpdfrcURL.toURI()).getAbsolutePath(); 
//				Config.writeConfig(xpdfrc, sample_xpdfrc, configs);
//				
//				URL add_to_xpdfrcURL=this.getClass().getClassLoader().getResource("config/xpdf/chinese-simplified/add-to-xpdfrc");
//				String add_to_xpdfrc = new File(add_to_xpdfrcURL.toURI()).getAbsolutePath();
//				Config.writeConfig(xpdfrc, add_to_xpdfrc, configs);
//			} catch (Exception e) {
//				log.error("存储装载文档转换所需的特殊中文字体配置文件时出错！可能的原因："+e.fillInStackTrace());
//			}
		} catch (Exception e1) {
			log.error(e1.fillInStackTrace());
		}
		finally {
			try {
				input.close();
			} catch (IOException e) {
				log.error(e.fillInStackTrace());
			}
		}
		Config.deleteFileExecutor.scheduleAtFixedRate(new DeleteOfficeTransTask(),0, 5, TimeUnit.MINUTES);
		
	}
	
	public void contextDestroyed(ServletContextEvent arg0) {
	}
}
