package com.netpower.library.servlet;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.netpower.library.util.DocConverter;
import com.netpower.library.util.config.Config;
import com.netpower.library.util.file.FileType;
import com.netpower.library.util.file.FileUtil;
import com.netpower.library.util.string.CodeUtil;
import com.netpower.library.util.web.JavaScriptUtil;
/**
 * 
 * 文件名称：ConvertServlet.java    
 * 功能描述：  文件转换接口，该接口是暴露给A8的。A8通过调用该Servlet完成
 * 具体的文件转换工作
 * 创建人：zhiyanqiang  
 * 创建时间：2017年1月12日
 */

@SuppressWarnings("serial")
public class ConvertServlet extends HttpServlet {
	private static final Log log = LogFactory.getLog(ConvertServlet.class);
	/**
	 * Constructor of the object.
	 */
	public ConvertServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String[] filePaths = request.getParameterValues("filePath");
		try {
			if(null != filePaths && filePaths.length > 0)
			{
				for(int i= 0; i<filePaths.length; i++)
				{
					String pathFromA8=filePaths[i];//从A8端传来的相对路径
					String[] pathFormA8Array=pathFromA8.split(";");
					String officeTransDirPass=pathFormA8Array[0];
					File officetransDirFile=new File(officeTransDirPass);
					StringBuffer filePathBuffer=new StringBuffer();
					if(officetransDirFile.exists())
					{
						filePathBuffer.append(officeTransDirPass);
						//Config.OfficeTrans_Root_Dir中保存的是转换根目录
						Config.OfficeTrans_Root_Dir=officeTransDirPass;
					}
					else
					{
						filePathBuffer.append(Config.OfficeTrans_Root_Dir);
					}
					Long fileId=null;
					for(int j=1;j<pathFormA8Array.length;j++)
					{
						filePathBuffer.append(File.separator).append(pathFormA8Array[j]);
						//文件传递规则：baseCacheDir;date;fileId;fileName
						if(j==(pathFormA8Array.length-2))
						{
							String fileIdStr=pathFormA8Array[j];
							fileId=Long.valueOf(fileIdStr);
						}
					}
					if(fileId!=null)
					{
						if(Config.fileIdsWatingTrans.size()>=500)
						{
							log.info("转换队列中，待转换文件超过了500;Dumping");
							log.info(Config.fileIdsWatingTrans.toString());
							Config.fileIdsWatingTrans.clear();
						}
						log.info("在转换中的文件数量："+Config.fileIdsWatingTrans.size());
						boolean hasTrans=Config.fileIdsWatingTrans.contains(fileId);
						if(hasTrans)
						{
							//改文件正在转换，不需要再次转换
							log.info("fileId=" + fileId +",正在转换；不需要重复转换,已经跳过");
							continue;
						}
					}
					String filePath =  CodeUtil.decodeAuto((filePathBuffer.toString()));
					if (FileUtil.fileExists(filePath)) 
					{
						Config.fileIdsWatingTrans.add(fileId);
						convertFile(request, filePath);//调用文件转换（带递归）
						
					}else
					{
						log.error("没有找到文件："+filePath);
					}
				}
			}
			//输出到页面
			//JavaScriptUtil.responseText(response, String.valueOf(msgArray), "json");
		} catch (JSONException e) {
			log.error(e.fillInStackTrace());
		}
	}

	/**
	 * 文件转换（带递归）
	 * @param request
	 * @param filePath
	 * @throws IOException
	 */
	private void convertFile(final HttpServletRequest request, final String filePath) throws IOException {
		if (FileUtil.fileExists(filePath)) 
		{
			final String extName = FileUtil.getExtName(filePath);//获取文件后缀扩展名
			
			//开启线程池多线程的模式来转换文档类型文件
			Config.cachedThreadPool.execute(new Runnable() {
	    	    public void run() {
	    	    	convertDocFile(filePath, extName);//转换文档文件
	    	    }
	    	});
			
			//开启线程池多线程的模式来转换图片类型文件（生成缩略图）
			Config.cachedThreadPool.execute(new Runnable() {
	    	    public void run() {
	    	    	convertPicFile(filePath, extName);//转换图片文件（生成缩略图）
	    	    }
	    	});
			
			//开启线程池多线程的模式来转换视频类型文件
			Config.cachedThreadPool.execute(new Runnable() {
	    	    public void run() {
	    	    	convertVideoFile(extName);//转换视频文件
	    	    }
	    	});
			
			//开启线程池多线程的模式来递归解压缩文件
			Config.cachedThreadPool.execute(new Runnable() {
	    	    public void run() {
	    	    	try{
	    	    		unZipFile(request, filePath, extName);//递归解压缩文件
	    	    	}catch(IOException exception)
	    	    	{
	    	    		System.out.println("解压缩文件异常! 该文件路径："+filePath);
	    	    	}
	    	    }
	    	});
		}
	}

	private void unZipFile(HttpServletRequest request, final String filePath,
			final String extName) throws IOException {
		if (FileType.ZIPTYPES.contains(extName.toLowerCase()) || "dir".equals(extName.toLowerCase())) {// 压缩文件或其他格式的文件
			// 默认压缩文件同级目录下有一个相应的解压好的同名文件夹
			String dirPath = new StringBuffer(filePath).append(FileUtil.DEFAULT_FILE_SUFFIX).toString();
			if (FileType.ZIPTYPES.contains(extName.toLowerCase())) {// 压缩文件
				if (!FileUtil.fileExists(dirPath)) {// 如果目录不存在,证明尚未解压过该文件
					// 将压缩包解压到当前目录下
				//	ZipUtil zipUtil = new ZipUtil();
					try{
				//		zipUtil.unZip(filePath, dirPath);
					}catch (Exception e) {
						System.out.println("自动解压压缩包失败！源文件是:"+filePath);
					}
				}
			}
			//递归处理，解压后的所有文件及子文件夹
			List<File> list = FileUtil.getFiles(dirPath, true);//递归获取指定路径下的所有文件及其子文件夹
			String childFilePath = null;
			for(File file : list)
			{
				childFilePath = file.getAbsolutePath();
				if(childFilePath.endsWith(FileUtil.DEFAULT_FILE_SUFFIX))
				{//过滤掉系统自动转换文档生成的中间临时文件
					continue;
				}
				convertFile(request, childFilePath);//递归调用文件转换
			}
		}
	}

	private void convertVideoFile(final String extName) {
		if (FileType.VIDEOTYPES.contains(extName.toLowerCase())) {// 视频音频文件
			//TODO 如果视频需要转码的话此处可以考虑插入，处理代码
		}
	}

	private void convertPicFile(final String filePath, final String extName) {
		if (FileType.PICTYPES.contains(extName.toLowerCase())) {// 图片文件
			// 产出缩略图
			try{
				//MinPictureUtil.createCustomSizePic(filePath, null, null);
			}catch (Exception e) {
				System.out.println("自动生成图片文件的缩略图失败！源文件是:"+filePath);
			}
		}
	}

	/**
	 * 转换文档文件
	 * @param filePath
	 * @param extName
	 */
	private void convertDocFile(String filePath, String extName) {
		if (FileType.DOCTYPES.contains(extName.toLowerCase())
				|| FileType.CODETYPES.contains(extName.toLowerCase())) {// 文本文档或者源代码文件
			DocConverter docconv = new DocConverter();
			File docFile = new File(filePath);
			File pdfFile = null;
			if (!"pdf".equals(extName.toLowerCase())) {// 如果文档不是pdf格式的文档，则将其转换为pdf文档
				try{
					pdfFile = docconv.doc2pdf(docFile);
				}catch (Exception e) {
					System.out.println("自动转换为PDF文档失败！源文件是:"+docFile.getAbsolutePath());
				}
			} else {// 源文档就是pdf格式文档，无需再次装换
				pdfFile = docFile;
			}
			if(filePath.toLowerCase().endsWith(".xls")||(filePath.toLowerCase().endsWith(".xlsx")))
			{
				//对于excel的文件不需要进行后面的转换了
				return;
			}
			pdfFile = FileUtil.updatePDFFilePopedom(pdfFile, null);//获取页数之前先修改PDF文件权限
			int totalPage = FileUtil.getPDFFileTotalPage(pdfFile);//获取pdf文件总页数
			if(totalPage > 1)
			{//多页PDF文档转化为SWF
				for(int i=1; i<=totalPage; i++)
				{
					try{
						docconv.pdf2swf(pdfFile, false, String.valueOf(i));
					}catch (Exception e) {
						System.out.println("自动转换为SWF文档失败！源文件是:"+pdfFile.getAbsolutePath());
					}
				}
			}else
			{//单页PDF文档转化为SWF
				try{
					docconv.pdf2swf(pdfFile, false, null);
				}catch (Exception e) {
					System.out.println("自动转换为SWF文档失败！源文件是:"+pdfFile.getAbsolutePath());
				}
			}
		}
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	@Override
	public void init() throws ServletException {
		super.init();
	}
}