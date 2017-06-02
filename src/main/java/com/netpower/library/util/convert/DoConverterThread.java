package com.netpower.library.util.convert;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.netpower.library.servlet.ConvertServlet;
import com.netpower.library.util.DocConverter;
import com.netpower.library.util.config.Config;
import com.netpower.library.util.file.FileType;
import com.netpower.library.util.file.FileUtil;

/**
 * 转换消费者，用来遍历待转换的文件
 * 项目名称：microservice-officetrans-trunk    
 * 文件名称：DoConverterThread.java    
 * 功能描述：   
 * 创建人：zhiyanqiang  
 * 创建时间：2017年3月9日
 */
public class DoConverterThread extends Thread{
	private static final Log log = LogFactory.getLog(DoConverterThread.class);
	@Override
	public void run() {
//		while(true)
//		{
//			for(String filePathQuery:Config.fileIdsWatingTrans)
//			{
//				if (FileUtil.fileExists(filePathQuery)) 
//				{
//					final String extName = FileUtil.getExtName(filePathQuery);//获取文件后缀扩展名
//					final String filePath=filePathQuery;
//					//开启线程池多线程的模式来转换文档类型文件
//					Config.cachedThreadPool.execute(new Runnable() {
//			    	    public void run() {
//			    			if (FileType.DOCTYPES.contains(extName.toLowerCase())
//			    					|| FileType.CODETYPES.contains(extName.toLowerCase())) {// 文本文档或者源代码文件
//			    				DocConverter docconv = new DocConverter();
//			    				File docFile = new File(filePath);
//			    				File pdfFile = null;
//			    				if (!"pdf".equals(extName.toLowerCase())) {// 如果文档不是pdf格式的文档，则将其转换为pdf文档
//			    					try{
//			    						pdfFile = docconv.doc2pdf(docFile);
//			    					}catch (Exception e) {
//			    						System.out.println("自动转换为PDF文档失败！源文件是:"+docFile.getAbsolutePath());
//			    					}
//			    				} else {// 源文档就是pdf格式文档，无需再次装换
//			    					pdfFile = docFile;
//			    				}
//			    				if(filePath.toLowerCase().endsWith(".xls")||(filePath.toLowerCase().endsWith(".xlsx")))
//			    				{
//			    					//对于excel的文件不需要进行后面的转换了
//			    					return;
//			    				}
//			    				pdfFile = FileUtil.updatePDFFilePopedom(pdfFile, null);//获取页数之前先修改PDF文件权限
//			    				int totalPage = FileUtil.getPDFFileTotalPage(pdfFile);//获取pdf文件总页数
//			    				if(totalPage > 1)
//			    				{//多页PDF文档转化为SWF
//			    					for(int i=1; i<=totalPage; i++)
//			    					{
//			    						try{
//			    							docconv.pdf2swf(pdfFile, false, String.valueOf(i));
//			    						}catch (Exception e) {
//			    							System.out.println("自动转换为SWF文档失败！源文件是:"+pdfFile.getAbsolutePath());
//			    						}
//			    					}
//			    				}else
//			    				{//单页PDF文档转化为SWF
//			    					try{
//			    						docconv.pdf2swf(pdfFile, false, null);
//			    					}catch (Exception e) {
//			    						System.out.println("自动转换为SWF文档失败！源文件是:"+pdfFile.getAbsolutePath());
//			    					}
//			    				}
//			    			}
//			    		
//			    	    }
//			    	});
//				}
//				Config.fileIdsWatingTrans.remove(filePathQuery);
//			}
//			try {
//	                sleep(2 * 1000);
//	            } catch (InterruptedException e) {
//	            	log.error(e, e.fillInStackTrace());
//	            }
        }
	
}
