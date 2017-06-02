package com.netpower.library.servlet.flexpaper;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.netpower.library.util.Common;
import com.netpower.library.util.DocConverter;
import com.netpower.library.util.file.FileUtil;
import com.netpower.library.util.string.CodeUtil;

public class DocViewServlet extends HttpServlet {
	
	/**
	 * Constructor of the object.
	 */
	public DocViewServlet() {
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
		this.doPost(request, response);
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
		BufferedOutputStream outs = new BufferedOutputStream(response.getOutputStream());
		Common conf = new Common();
		String doc = CodeUtil.decodeAuto(request.getParameter("doc"));
		String page = request.getParameter("page");
		String totalPage = request.getParameter("totalPage");
		String format = request.getParameter("format");
		String resolution = request.getParameter("resolution");//分辨率
		String callback = request.getParameter("callback");
		String allowcache = request.getParameter("allowcache");

		if (doc == null)
			return;
		if (format == null) {
			format = "swf";
		}
		if (callback == null) {
			callback = "";
		}
		if (allowcache == null) {
			allowcache = "true";
		}
		
		String pdfFilePath = doc;
		boolean isPdfSrcFile = false;//判断源文件是否是PDF文件 
		if(!doc.toLowerCase().trim().endsWith(".pdf") && !doc.toLowerCase().trim().endsWith(".pdf"+FileUtil.DEFAULT_FILE_SUFFIX))
		{
			pdfFilePath = doc + ".pdf";
		}else if(doc.toLowerCase().trim().endsWith(".pdf")){
			isPdfSrcFile = true;//判断源文件是PDF文件 
		}
		
		//如果源文件不是是PDF文件，且没有打上公司文件后缀就要加上后缀
		if(!isPdfSrcFile && !pdfFilePath.toLowerCase().trim().endsWith(".pdf"+FileUtil.DEFAULT_FILE_SUFFIX))
		{
			File pdfFileSysauto = new File(pdfFilePath+FileUtil.DEFAULT_FILE_SUFFIX);
			if(FileUtil.fileExists(pdfFileSysauto.getAbsolutePath()) && pdfFileSysauto.length() > 0)
			{
				//doc = doc+FileUtil.DEFAULT_FILE_SUFFIX;
				pdfFilePath = pdfFileSysauto.getAbsolutePath();
			}
		}
		
		String swfFilePath = doc + ".swf";
		String jsonFilePath = doc + ".js";
		String pngFilePath = doc + ".png";
		String jpgCachePath = doc + "_res_" + resolution + ".jpg";
		if(null != totalPage && !"".equals(totalPage.trim()) && Integer.parseInt(totalPage) > 1)
		{//总页数存在多页时才进行文件名称分页
			if (null != page && !"".equals(page.trim())) {
				swfFilePath = doc + "_" + page + ".swf";
				jsonFilePath = doc + "_" + page + ".js";
				pngFilePath = doc + "_" + page + ".png";
				jpgCachePath = doc + "_" + page + "_res_" + resolution + ".jpg";
			}
		}else
		{//单页文档
			page = null;
		}
		
		String messages = "";
		if ("true".equals(allowcache)) {
			conf.setCacheHeaders(response);
		}
		if ("swf".equals(format) || "jpg".equals(format) || "png".equals(format) || "pdf".equals(format)) 
		{
			String swfFilePathSysauto = swfFilePath+FileUtil.DEFAULT_FILE_SUFFIX;//系统默认自动转换后的SWF源文件路径
			if (!FileUtil.fileExists(swfFilePath) && !FileUtil.fileExists(swfFilePathSysauto)) 
			{//如果转换好的SWF文件不存,且系统默认自动转换后的SWF源文件也不存在。就立刻重新转换源文件
				conf.clearCacheHeaders(response);//转换好的SWF文件不存，那么就不能让浏览器缓存
				DocConverter docconv = new DocConverter();
				messages = docconv.pdf2swf(new File(pdfFilePath), isPdfSrcFile, page);
				//修改BUG,新转换SWF文件之后，也需要把文件名改过来，否则FileUtil.readFile(文档路径不正确)，导致第一次FlexPaper读取不到文档流
				swfFilePath = swfFilePathSysauto;
			}else if(!FileUtil.fileExists(swfFilePath) && FileUtil.fileExists(swfFilePathSysauto)) 
			{
				swfFilePath = swfFilePathSysauto;
			}
			
			if ("png".equals(format) || "jpg".equals(format)) 
			{//图片文件
				if (!FileUtil.fileExists(pngFilePath)) {
					DocConverter docconv = new DocConverter();
					docconv.swfRender(swfFilePath, page);
				}

				if ("false".equals(allowcache) || ("true".equals(allowcache) && conf.endOrRespond(request, response))) 
				{
					if ("jpg".equals(format)) 
					{
						response.setContentType("image/jpeg");
						outs.write(FileUtil.readFile(new File(conf.generateImage(pngFilePath, jpgCachePath, resolution, "jpg"))));
					}else {
						response.setContentType("image/png");
						outs.write(FileUtil.readFile(new File(pngFilePath)));
					}
				}
			} else if ("pdf".equals(format)) 
			{//PDF文件
				response.setContentType("application/pdf");
				outs.write(FileUtil.readFile(new File(pdfFilePath)));
			}else if ("swf".equals(format)) 
			{//SWF文件
				if ("false".equals(allowcache) || ("true".equals(allowcache) && conf.endOrRespond(request, response))) 
				{
					response.setContentType("application/x-shockwave-flash");
					response.setHeader("Accept-Ranges", "bytes");
					byte[] content = FileUtil.readFile(new File(swfFilePath));
					response.setContentLength(content.length);
					outs.write(content);
				}
			}
		} else if ("json".equals(format) || "jsonp".equals(format)) 
		{
			if (!FileUtil.fileExists(jsonFilePath)) {
				DocConverter docconv = new DocConverter();
				messages = docconv.pdf2json(new File(pdfFilePath), page);
			}
			if ("false".equals(allowcache) || ("true".equals(allowcache) && conf.endOrRespond(request, response))) 
			{
				response.setContentType("text/javascript");
				if ("json".equals(format)) {
					outs.write(FileUtil.readFile(new File(jsonFilePath)));
				}else if ("jsonp".equals(format)) {
					outs.write((callback + "(").getBytes());
					outs.write(FileUtil.readFile(new File(jsonFilePath)));
					outs.write((");").getBytes());
				}
			}
		}

		if (messages.length() > 0 && !"[OK]".equals(messages) && !"[Converted]".equals(messages) && !"png".equals(format)) 
		{
			outs.write(("错误:" + messages.substring(1, messages.length() - 2)).getBytes());
		}
		outs.flush();
		outs.close();
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
