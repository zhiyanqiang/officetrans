package com.netpower.library.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.netpower.library.util.config.Config;
import com.netpower.library.util.convert.MsOfficeConverter;
import com.netpower.library.util.convert.OpenOfficeConverter;
import com.netpower.library.util.convert.PDFConverter;
import com.netpower.library.util.convert.WPSConverter;
import com.netpower.library.util.file.FileUtil;
import com.netpower.library.util.os.ServerUitl;


public class DocConverter extends Common{
	private static final Log log = LogFactory.getLog(DocConverter.class); 
	public DocConverter(){
	}
	
	/** 
     * Doc源文件转为PDF 
     * @param file 
	 * @throws IOException 
     */  
    public File doc2pdf(File docFile) throws IOException{
    	log.info("开始进行文件转换：filePath=" + docFile.getAbsolutePath());
    	String pathDoc = docFile.getParent();//doc源文件所在目录
		
		String pdfFilePath	= pathDoc + File.separator + docFile.getName() + ".pdf" + FileUtil.DEFAULT_FILE_SUFFIX;//此处默认在doc源文件所在目录下生成同名pdf文件
		File pdfFile = new File(pdfFilePath);
        if (docFile.exists()) {  
            if (!pdfFile.exists() || pdfFile.length() == 0) {  
            	//如果PDF文件不存在，或是空文件，则进行转换
            	pdfFile = FileUtil.makeDirFile(pdfFilePath);
            	
            	//调用doc文档转换服务组件
            	boolean flag = false;
            	String ConvertTool = "微软Office";//默认采用微软Office转换工具
            	PDFConverter pdfConverter = null;
            	//如果是Windows操作系统
            	if(Config.isWin()){
            		if(false)
    				{
            			//使用wps转换文件时存在各种格式上的问题，还是走office比较好
            			//Windows Server操作系统下面用word2013转换 word97-2003的文件存在格式兼容性问题，所以改为WPS转换方案
            			String suffix =  FileUtil.getExtName(docFile);
                		if(null != suffix)
                		{
                			suffix = suffix.toLowerCase();//文件扩展名转化为小写
                			if(suffix.equals("doc"))
                			{
                				pdfConverter = new WPSConverter();
        	                	flag = pdfConverter.doc2pdf(docFile, pdfFile);//调用WPS转换
        	                	ConvertTool = "在操作系统"+Config.OS+"下，金山WPS";
                			}
                			
                		}
    				}else
    				{
    					//考虑到文档最终转换效果，首先调用微软Office转换
    	            	pdfConverter = new MsOfficeConverter();
    	            	flag = pdfConverter.doc2pdf(docFile, pdfFile);//调用微软Office转换
    				}
	            	
	            	
	            	if(!flag)
	            	{
	            		pdfConverter = new WPSConverter();
	                	flag = pdfConverter.doc2pdf(docFile, pdfFile);//调用WPS转换
	                	ConvertTool = "金山WPS";
	            	}
	            	
	            	if(!flag)
	            	{
	            		pdfConverter = new OpenOfficeConverter();
	                	flag = pdfConverter.doc2pdf(docFile, pdfFile);//调用OpenOffice转换
	                	ConvertTool = "开源OpenOffice";
	            	} 
	            	
	            	//开发中只编了代码没有实际调试PdfCreator软件的转换，暂时注释掉，需要时再调试。
//	            	if(!flag)
//	            	{
//	            		pdfConverter = new PdfCreatorConverter();
//	                	flag = pdfConverter.doc2pdf(docFile, pdfFile);//调用PdfCreator软件服务转换
//	            		ConvertTool = "PdfCreator软件";
//	            	}
            	}else
            	{//Linux其他操作系统
            		if(!flag)
	            	{
	            		pdfConverter = new OpenOfficeConverter();
	                	flag = pdfConverter.doc2pdf(docFile, pdfFile);//调用OpenOffice转换
	                	ConvertTool = "开源OpenOffice";
	            	}
            	}
            	
            	if(flag)
            	{
            		log.info("采用"+ConvertTool+"方案，成功转换文档:"+docFile.getAbsolutePath());
            	}
            } else {  
            	log.info("****已经转换为pdf，不需要再进行转化****");  
            }
            String fileName=docFile.getName();
    		String[] fileNameArray=fileName.split("\\.");
    		String fileIdStr=fileNameArray[0];
    		Long fileId=Long.valueOf(fileIdStr);
    		Config.fileIdsWatingTrans.remove(fileId);
            
        } else {  
        	log.info("****文档转换异常，需要转换的文档不存在，无法转换****");  
        }  
        return pdfFile;
    }

    /**
     * PDF源文件转为SWF
     * @param pdfFile
     * @param isPdfSrcFile 是否源文件是PDF文件
     * @param page
     * @return
     * @throws IOException
     */
	public String pdf2swf(File pdfFile, boolean isPdfSrcFile, String page) throws IOException {
		File docFile = null;//最原始的源文档
		String pdfFilePath = pdfFile.getAbsolutePath();
		int dot = pdfFilePath.lastIndexOf('.'); 
        if ((dot >-1) && (dot < (pdfFilePath.length() - 1))) { 
        	String docFilePath = pdfFilePath.substring(0, dot);
        	if(isPdfSrcFile){//如果源文件是PDF文件，主要是系统在磁盘上转换好的pdf.seeyon文档又手动将改名为pdf文档。
        		docFilePath = pdfFilePath;
        	}
        	if(!pdfFilePath.endsWith(FileUtil.DEFAULT_FILE_SUFFIX))
    		{//待转换的PDF文件应该是最原始的文件
        		docFile = new File(docFilePath);
    		}else
    		{//如果待转换的PDF文件是系统自动生成的临时文件,那么证明最原始的文件就不是PDF,需要继续截取文件路径获取最原始文件
    			int docDot = docFilePath.lastIndexOf('.'); 
    	        if ((docDot >-1) && (docDot < (docFilePath.length() - 1))) { 
    	        	docFilePath = docFilePath.substring(0, docDot);
    	        	docFile = new File(docFilePath);
    	        }
    		}
        }
        
        String pdfFilePathSysauto = null;//系统默认自动转换后的PDF源文件路径
        if(!pdfFilePath.endsWith(FileUtil.DEFAULT_FILE_SUFFIX))
		{
        	pdfFilePathSysauto = pdfFile.getAbsolutePath()+FileUtil.DEFAULT_FILE_SUFFIX;//系统默认自动转换后的PDF源文件路径
		}else
		{
			pdfFilePathSysauto = pdfFile.getAbsolutePath();//系统默认自动转换后的PDF源文件路径
		}
        
        File pdfFileSysauto = new File(pdfFilePathSysauto);//系统默认自动转换后的PDF源文件
        
		if(!pdfFile.exists() && !pdfFileSysauto.exists() && docFile.exists())
		{//如果PDF源文件不存在,系统默认自动转换后的PDF源文件也不存在,且最原始的文档存在。那么就通过最原始的Doc文档转换为PDF文档
            doc2pdf(docFile);
		}else if(!pdfFile.exists() && pdfFileSysauto.exists())
		{//如果PDF源文件不存在,且系统默认自动转换后的PDF源文件存在。那么就不需要再次转换
			pdfFilePath = pdfFilePathSysauto;
			pdfFile = pdfFileSysauto;
		}
		
		if(pdfFile.exists() && !docFile.exists())
		{//如果PDF源文件存在，而最原始的文档不存在，那么证明PDF源文件就是最原始的文档
			docFile = pdfFile;
			if(docFile.getAbsolutePath().endsWith(FileUtil.DEFAULT_FILE_SUFFIX))
			{
				docFile = new File(docFile.getAbsolutePath().replace(FileUtil.DEFAULT_FILE_SUFFIX, ""));
			}
		}
		
		JArray commands = getConverCommands();
		String command = commands.get("cmd.conversion.splitpages");
		String swfFilePath	= null;
		if(null == page)
		{
			page = "";
			command = commands.get("cmd.conversion.singledoc");
			swfFilePath	= docFile.getAbsolutePath() + ".swf" + FileUtil.DEFAULT_FILE_SUFFIX;//此处默认在最原始源文件所在目录下生成同名SWF分页文件
		}else if(page.contains("[*,"))
		{//FlexPaper传递的参数是这种格式的页码
			try {
				page = page.replace("[*,", "").replace("]", "");
				page =  String.valueOf(Integer.parseInt(page) + 1);
			} catch (Exception ex) {
				return "转换SWF文档格式文件时,传递的参数类型错误！应该是整数行的页码：" + ex.toString();
			}
			swfFilePath	= docFile.getAbsolutePath() + "_" + page + ".swf" + FileUtil.DEFAULT_FILE_SUFFIX;//此处默认在最原始源文件所在目录下生成同名SWF分页文件
		}else
		{
			swfFilePath	= docFile.getAbsolutePath() + "_" + page + ".swf" + FileUtil.DEFAULT_FILE_SUFFIX;//此处默认在最原始源文件所在目录下生成同名SWF分页文件
		}
		File swfFile = new File(swfFilePath);

		try {
			if (!isNotConverted(pdfFile, swfFile)) {
				return "[Converted]";
			}
		} catch (Exception ex) {
			return "错误," + ex.toString();
		}
		
		command = command.replace("{path.pdf}", Config.separate(pdfFilePath));
		command = command.replace("{path.swf}", Config.separate(docFile.getAbsolutePath()));//此处默认在最原始源文件所在目录下生成同名SWF分页文件

		boolean return_var = false;
		if(!"".equals(page.trim())){
			String pagecmd = command.replace("%", page);
			pagecmd = pagecmd + " -p " + page;

			return_var = ServerUitl.exec(pagecmd);
		}else{
			return_var = ServerUitl.exec(command);
		}
		String s = "错误： 在PDF转化为SWF的时候, 可能是SWFTools工具没有安装，或者转存SWF文件的目录没有分配读写权限";
		if(return_var) {
			s="[Converted]";
		}
		return s;
	}
	
	/**
	 * PDF源文件转为json
	 * Method:render page as image
	 * @param pdfFile
	 * @param page
	 * @return
	 * @throws IOException 
	 */
	public String pdf2json(File pdfFile, String page) throws IOException
	{
		File docFile = null;//最原始的源文档
		String pdfFilePath = pdfFile.getAbsolutePath();
		int dot = pdfFilePath.lastIndexOf('.'); 
        if ((dot >-1) && (dot < (pdfFilePath.length() - 1))) { 
        	docFile = new File(pdfFilePath.substring(0, dot));
        }
		if(!pdfFile.exists() && docFile.exists())
		{//如果PDF源文件不存在,且最原始的文档存在。那么就通过最原始的Doc文档转换为PDF文档
            doc2pdf(docFile);
		}else if(pdfFile.exists() && !docFile.exists())
		{//如果PDF源文件存在，而最原始的文档不存在，那么证明PDF源文件就是最原始的文档
			docFile = pdfFile;
		}
		
		JArray commands = getConverCommands();
		String command = commands.get("cmd.conversion.splitjsonfile");
		if(null == page)
		{
			page = "";
			command = commands.get("cmd.conversion.jsonfile");
		}
		
		command = command.replace("{path.pdf}", Config.separate(pdfFilePath));
		command = command.replace("{path.swf}", Config.separate(docFile.getAbsolutePath()));//此处默认在最原始源文件所在目录下生成同名json分页文件
		if(ServerUitl.exec(command)){
			return "[OK]";
		}else{
			return "[错误： 在PDF转换为JSON的时候, 请检查你的磁盘目录是否有读写权限和配置！]";
		}
	}

	public boolean isNotConverted(File pdfFile, File swfFile) throws Exception {
		if (!pdfFile.exists()) {
			throw new Exception("需要转换为SWF的PDF源文档不存在！");
		}
		if (swfFile == null) {
			throw new Exception("转换后的SWF文件输出路径没有设置！");
		} else {
			if (!swfFile.exists()) {
				return true;
			} else {
				if(pdfFile.lastModified() > swfFile.lastModified()) 
					return true;
			}
		}
		return false;
	}
	
	/**
	 * 渲染加载SWF
	 * @param swfdoc
	 * @param page
	 * @return
	 */
	public String swfRender(String swfdoc, String page)
	{
		try {
			JArray commands = getConverCommands();
			String command = commands.get("cmd.conversion.rendersplitpage");
			if(null != page)
			{
				command = commands.get("cmd.conversion.renderpage");
				command = command.replace("{page}", page);
			}
			command = command.replace("{path.swf}", Config.separate(swfdoc));
			if(ServerUitl.exec(command)){
				return "[OK]";
			}else{
				return "[错误： 在渲染转换SWF的时候, 请检查你的磁盘目录是否有读写权限和配置！]";
			}
		} catch (Exception ex) {
			return ex.toString();
		}
	}
	
	/**
	 * 获取SWF尺寸大小
	 * @param swfFilePath
	 * @param mode
	 * @return
	 */
	public String swfSize(String swfFilePath, String mode)
	{
		String ret = "";
		try {
			JArray commands = getConverCommands();
			String command = commands.get("cmd.query.swfwidth");
			
			if("height".equals(mode)){
				command = commands.get("cmd.query.swfheight");			
			}
			
			command = command.replace("{path.swf}", Config.separate(swfFilePath));
			ret = ServerUitl.execMuch(command);
			if(ret != null){
				return Config.strip_non_numerics(ret);
			}else{
				return "[Error Extracting]";
			}
		} catch (Exception ex) {
			return ex.toString();
		}
	}
}