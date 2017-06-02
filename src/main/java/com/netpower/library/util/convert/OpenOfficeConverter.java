package com.netpower.library.util.convert;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.artofsolving.jodconverter.DefaultDocumentFormatRegistry;
import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.DocumentFormat;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeException;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;
import com.artofsolving.jodconverter.openoffice.converter.StreamOpenOfficeDocumentConverter;
import com.netpower.library.util.Common;
import com.netpower.library.util.config.Config;
import com.netpower.library.util.file.FileType;
import com.netpower.library.util.file.FileUtil;
import com.netpower.library.util.os.ServerUitl;

public class OpenOfficeConverter extends Common implements PDFConverter{
	private static final Log log = LogFactory.getLog(OpenOfficeConverter.class);
	public synchronized boolean doc2pdf(File docFile, File pdfFile) {
		boolean flag = true;
		//判断OpenOffice服务soffice.exe或者soffice.bin是否已经启动如果没有启动，就开启该服务
		StringBuffer sofficeCmd=new StringBuffer("soffice");
		if(Config.isWin())
		{
			sofficeCmd.append(".exe");
		}
		else
		{
			sofficeCmd.append(".bin");
		}
		if(!ServerUitl.isRunningApp("soffice"))
		{
			//转换前先启动OpenOffice文档转换服务
			try{
				log.info("启动命令:"+ Config.OpenOffice_TOOL_PATH + sofficeCmd.toString());
				startOpenOfficeServer(Config.OpenOffice_TOOL_PATH + sofficeCmd.toString());
			}catch(Exception e)
			{
				flag = false;
				log.error("启动openoffice服务时出错了:" + e.fillInStackTrace());
				return flag;
			}
		}
		
		OpenOfficeConnection connection = new SocketOpenOfficeConnection(Config.OpenOffice_SERVER_IP, Integer.parseInt(Config.OpenOffice_SERVER_PORT));  
		try {  
			DocumentConverter converter = null;
			try{
		        connection.connect();  
		        try{
		        	converter = new OpenOfficeDocumentConverter(connection); 
		        } catch (OpenOfficeException e) { 
		        	//报次异常可以采取文档流的转换方式com.artofsolving.jodconverter.openoffice.connection.OpenOfficeException: conversion failed: could not load input document
		        	converter = new StreamOpenOfficeDocumentConverter(connection);
		        	log.error("OpenOfficeException" + e.fillInStackTrace());
		        	flag = false;
		        }
		    } catch (java.net.ConnectException e) {  
		    	log.error("****文档转换时，openoffice服务未启动！尝试启动服务...****");  
		        try{
		            //尝试启动OpenOffice文档转换服务
		        	startOpenOfficeServer(Config.OpenOffice_TOOL_PATH + sofficeCmd.toString());
		        	connection.connect();  
		            try{
		            	converter = new OpenOfficeDocumentConverter(connection); 
		            }catch (OpenOfficeException exc) { 
		            	//报次异常可以采取文档流的转换方式com.artofsolving.jodconverter.openoffice.connection.OpenOfficeException: conversion failed: could not load input document
		            	converter = new StreamOpenOfficeDocumentConverter(connection);
		            	flag = false;
		            	log.error(exc.fillInStackTrace());
		            }
		        }catch (Exception ex) {  
		        	log.error("****尝试启动OpenOffice文档转换服务，失败！可能是OpenOffice软件在服务器端已经被界面化打开了。****"); 
		        	flag = false;
		        }
		        flag = false;
		    }
		    
		    DefaultDocumentFormatRegistry formatReg = new DefaultDocumentFormatRegistry();
		    DocumentFormat pdf = formatReg.getFormatByFileExtension("pdf"); 
		    
		    //根据文件后缀名，获取不同文件类型的转换格式对象
		    String extName = FileUtil.getExtName(docFile);
		    if("stw".equals(extName) || "sxd".equals(extName) || "odf".equals(extName))
		    {
		        DocumentFormat df = formatReg.getFormatByFileExtension(extName); 
		        try{
		        	converter.convert(docFile, df, pdfFile, pdf);
		        	flag = true;
		        }catch (OpenOfficeException exc) { 
		        	flag = false;
		        	//报次异常可以采取文档流的转换方式com.artofsolving.jodconverter.openoffice.connection.OpenOfficeException: conversion failed: could not load input document
		        	converter = new StreamOpenOfficeDocumentConverter(connection);
		        	converter.convert(docFile, df, pdfFile, pdf);
		        	flag = true;
		        }
		    }else if(FileType.CODETYPES.contains(extName.toLowerCase()))
		    {
		    	DocumentFormat txt = formatReg.getFormatByFileExtension("txt"); 
		    	try{
		        	converter.convert(docFile, txt, pdfFile, pdf);
		        	flag = true;
		        }catch (OpenOfficeException exc) { 
		        	flag = false;
		        	//报次异常可以采取文档流的转换方式com.artofsolving.jodconverter.openoffice.connection.OpenOfficeException: conversion failed: could not load input document
		        	converter = new StreamOpenOfficeDocumentConverter(connection);
		        	converter.convert(docFile, txt, pdfFile, pdf);
		        	flag = true;
		        }
		    }
		    else if((extName.toLowerCase().equals("xls"))||(extName.toLowerCase().equals("xlsx")))
		    {
		    	//excel文件的话转换成excel
		    	String toPdfName=pdfFile.getName();
				String[] toPdfNameArray=toPdfName.split("\\.");
				String htmlName=toPdfNameArray[0] + ".html";
				DocumentFormat html= formatReg.getFormatByFileExtension("html");
				DocumentFormat df = formatReg.getFormatByFileExtension("xls"); 
				try{
		        	converter.convert(docFile, df,new File(pdfFile.getParentFile(),htmlName),html);
		        	flag = true;
		        }catch (OpenOfficeException exc) { 
		        	flag = false;
		        	//报次异常可以采取文档流的转换方式com.artofsolving.jodconverter.openoffice.connection.OpenOfficeException: conversion failed: could not load input document
		        	converter = new StreamOpenOfficeDocumentConverter(connection);
		        	converter.convert(docFile, new File(pdfFile.getParentFile(),htmlName));
		        	flag = true;
		        }
				//return excel2html(docFile.getAbsolutePath(),new File(pdfFile.getParentFile(),htmlName).getAbsolutePath());
		    }
		    else
		    {
		    	DocumentFormat sourceDf= null;
		    	if((extName.toLowerCase().equals("doc"))||(extName.toLowerCase().equals("docx")))
		    	{
		    		sourceDf= formatReg.getFormatByFileExtension("doc");
		    	}
		    	if((extName.toLowerCase().equals("ppt"))||(extName.toLowerCase().equals("pptx")))
                {
		    		sourceDf= formatReg.getFormatByFileExtension("ppt");
	            }
		    	try{
		        	converter.convert(docFile, sourceDf,pdfFile, pdf);
		        	flag = true;
		        }catch (OpenOfficeException exc) { 
		        	flag = false;
		        	//报次异常可以采取文档流的转换方式com.artofsolving.jodconverter.openoffice.connection.OpenOfficeException: conversion failed: could not load input document
		        	converter = new StreamOpenOfficeDocumentConverter(connection);
		        	converter.convert(docFile,sourceDf,pdfFile, pdf);
		        	flag = true;
		        }
		    }
		    // close the connection  
		    connection.disconnect();  
		    log.info("****pdf转换成功，PDF输出：" + pdfFile.getPath()+ "****");  
		} catch (OpenOfficeException e) {  
			flag = false;
			log.error(e.fillInStackTrace());  
		    throw e;  
		} catch (Exception e) {  
			flag = false;
			log.error(e.fillInStackTrace());  
		}finally {
		    try{ if(connection != null){connection.disconnect(); connection = null;}}catch(Exception e){}
		}
		return flag;
	}  
}
