package com.netpower.library.servlet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.netpower.library.util.DocConverter;
import com.netpower.library.util.config.Config;
import com.netpower.library.util.file.FileType;
import com.netpower.library.util.file.FileUtil;
import com.netpower.library.util.string.CodeUtil;

@SuppressWarnings("serial")
public class ViewServlet extends HttpServlet {
	
	/**
	 * Constructor of the object.
	 */
	public ViewServlet() {
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
		String filePath =  CodeUtil.decodeAuto(request.getParameter("filePath"));
		String fileUrl = CodeUtil.decodeAuto(request.getParameter("fileUrl"));//文件网络下载路径
		String fileName = CodeUtil.decodeAuto(request.getParameter("fileName"));//文件真实名称
		String isChild = request.getParameter("isChild");//是否为文件夹层级子节点
	    if(isChild != null && !"".equals(isChild)) {
	    	filePath = CodeUtil.decode(filePath);
	    	fileUrl = CodeUtil.decode(fileUrl);
	    	fileName = CodeUtil.decode(fileName);
	    }
	    String ServletPath = null;
		if (!FileUtil.fileExists(filePath)) {// 指定的文件不存在
			ServletPath = "/error/FileNotFind.jsp";
		} else {
			
			if (null == fileName || "".equals(fileName.trim())) {// 如果没有传递文件原始真实的名称
				fileName = FileUtil.getFileName(filePath);//设置为当前文件名称
			}
			
			String fileSize = FileUtil.formatSize(FileUtil.getFileSize(filePath));//文件大小
			String extName = FileUtil.getExtName(filePath);//获取文件后缀扩展名
			if (FileType.DOCTYPES.contains(extName.toLowerCase())
					|| FileType.CODETYPES.contains(extName.toLowerCase())) {// 文本文档或者源代码文件
				//直接跳转到flexpaper/document.jsp
				ServletPath = "/flexpaper/document.jsp";
				
				String isMin = request.getParameter("isMin");//是否为最小版简单化的显示
			    if(isMin != null && "true".equals(isMin.toLowerCase().trim())) {
			    	//如果是想要简单版的显示文档阅读器
			    	ServletPath = "/flexpaper/document.min.jsp";
			    }
				
				DocConverter docconv = new DocConverter();
				File docFile = new File(filePath);
				File pdfFile = null;
				if (!"pdf".equals(extName.toLowerCase())) {// 如果文档不是pdf格式的文档，则将其转换为pdf文档
					pdfFile = docconv.doc2pdf(docFile);
				} else {// 源文档就是pdf格式文档，无需再次装换
					pdfFile = docFile;
				}
				pdfFile = FileUtil.updatePDFFilePopedom(pdfFile, null);//获取页数之前先修改PDF文件权限
				int totalPage = FileUtil.getPDFFileTotalPage(pdfFile);//获取pdf文件总页数
				request.setAttribute("totalPage", totalPage);
			} else if (FileType.PICTYPES.contains(extName.toLowerCase())) {// 图片文件
				// 产出缩略图
				//MinPictureUtil.createCustomSizePic(filePath, null, null);
				// 直接跳转到PictureViewServlet
				ServletPath = "/PictureViewServlet";
			} else if (FileType.VIDEOTYPES.contains(extName.toLowerCase())
					|| FileType.RADIOTYPES.contains(extName.toLowerCase())
					|| FileType.FLASHTYPES.contains(extName.toLowerCase())) {// 视频，音频，Flash动画文件
				// 直接跳转到VideoViewServlet
				ServletPath = "/VideoViewServlet";
			} else if (FileType.ZIPTYPES.contains(extName.toLowerCase()) || "dir".equals(extName.toLowerCase())) {// 压缩文件或其他格式的文件
				// 默认压缩文件同级目录下有一个相应的解压好的同名文件夹
				String dirPath = new StringBuffer(filePath).append(FileUtil.DEFAULT_FILE_SUFFIX).toString();
				if (FileType.ZIPTYPES.contains(extName.toLowerCase())) {// 压缩文件
					if (!FileUtil.fileExists(dirPath)) {// 如果目录不存在,证明尚未解压过该文件
						// 将压缩包解压到当前目录下
						//ZipUtil zipUtil = new ZipUtil();
						//zipUtil.unZip(filePath, dirPath);
					}
				}
				ServletPath = "/FileListServlet";//跳转到文件列表Servlet
			} else {
				com.netpower.library.util.file.File pFile = new com.netpower.library.util.file.File();
				pFile.setFilePath(filePath.replace("\\", "/"));
				pFile.setFileName(fileName);
				pFile.setFileUrl(fileUrl);
				pFile.setFileSize(fileSize);
				request.setAttribute("pFile", pFile);
				List<com.netpower.library.util.file.File> childFileList = new ArrayList<com.netpower.library.util.file.File>();
				childFileList.add(pFile);
				request.setAttribute("childFileList", childFileList);
				ServletPath = "/filelist/root.jsp";
			}

			//封装需要传递的参数
			request.setAttribute("filePath", Config.separate(filePath));
			request.setAttribute("fileUrl", fileUrl);
			request.setAttribute("fileName", fileName);
			request.setAttribute("fileSuffix", extName);
			request.setAttribute("fileSize", fileSize);	
			request.setAttribute("isChild", isChild);
		}
		
		ServletContext servletContext = getServletContext();
		RequestDispatcher rd = servletContext.getRequestDispatcher(ServletPath);//跳转到对应业务处理的Servlet
		rd.forward(request, response);
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