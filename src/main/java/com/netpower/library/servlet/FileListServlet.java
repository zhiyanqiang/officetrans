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

import com.netpower.library.util.file.FileOperatorType;
import com.netpower.library.util.file.FileType;
import com.netpower.library.util.file.FileUtil;

/**
 * 文件列表Servlet
 * @author 郭志东
 * 
 */
@SuppressWarnings("serial")
public class FileListServlet extends HttpServlet {

	public void destroy() {
		super.destroy();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//获取服务器网络地址
		String path = request.getContextPath(); 
		String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
		String loadImageAction = basePath+"LoadImageServlet?";//配置动态加载图片网络地址
		
		//封装需要传递的参数
		String filePath = request.getAttribute("filePath") == null ? null : String.valueOf(request.getAttribute("filePath"));
		String fileUrl = request.getAttribute("fileUrl") == null ? null : String.valueOf(request.getAttribute("fileUrl"));
		String fileName = request.getAttribute("fileName") == null ? null : String.valueOf(request.getAttribute("fileName"));
		String fileSize = request.getAttribute("fileSize") == null ? null : String.valueOf(request.getAttribute("fileSize"));
		String fileSuffix = request.getAttribute("fileSuffix") == null ? null : String.valueOf(request.getAttribute("fileSuffix"));
		String isChild = request.getAttribute("isChild") == null ? null : String.valueOf(request.getAttribute("isChild")); // 是否为根节点
		
		request.setAttribute("times", request.getParameter("times") == null || "".equals(request.getParameter("times")) ? "1" : "0");
		
		if(null == fileUrl || "".equals(fileUrl.trim()))
		{
			fileUrl = filePath.replace("\\", "/");
		}
		
		com.netpower.library.util.file.File pFile = new com.netpower.library.util.file.File();
		pFile.setFilePath(filePath.replace("\\", "/"));
		pFile.setFileName(fileName);
		pFile.setFileUrl(fileUrl);
		pFile.setFileSize(fileSize);
		
		request.setAttribute("pFile", pFile);

		String dirPath = filePath;
		String childFileUrl = fileUrl;
		if(FileType.ZIPTYPES.contains(fileSuffix.toLowerCase())) {
			// 默认压缩文件同级目录下有一个相应的解压好的同名文件夹
			dirPath = new StringBuffer(filePath).append(FileUtil.DEFAULT_FILE_SUFFIX).toString();
			if(fileUrl.endsWith("/"))
			{//去掉最后一个"/"分隔符
				fileUrl = fileUrl.substring(0, fileUrl.length() - 1);
			}
			childFileUrl = new StringBuffer(fileUrl).append(FileUtil.DEFAULT_FILE_SUFFIX).toString();
		}
		List<File> files = FileUtil.getFiles(dirPath);
		List<com.netpower.library.util.file.File> childFileList = new ArrayList<com.netpower.library.util.file.File>();
		com.netpower.library.util.file.File cFile = null;
		for(File file : files) {
			if(file.getAbsolutePath().endsWith(FileUtil.DEFAULT_FILE_SUFFIX)) {
				continue;
			}
			cFile = new com.netpower.library.util.file.File();			
			cFile.setFilePath(file.getPath().replace("\\", "/"));
			cFile.setFileUrl(new StringBuffer(childFileUrl).append(childFileUrl.endsWith("/") ? "" : "/").append(file.getName()).toString());
			cFile.setFileShortUrl(new StringBuffer(childFileUrl).append(childFileUrl.endsWith("/") ? "" : "/").append(file.getName())
					.append(FileUtil.DEFAULT_FILE_SUFFIX).append("/").append(file.getName()).toString());
			cFile.setFileName(cFile.getFileName());
			cFile.setFileSize(FileUtil.formatSize(FileUtil.getFileSize(file.getPath())));
			// 图片格式的特殊处理
			if (cFile.getFileOperatorType().equals(FileOperatorType.PIC)) {
				//产出缩略图
				//MinPictureUtil.createCustomSizePic(cFile.getFilePath(), null, null);
				//如果网络地址fileUrl不是http://开头,且与真实filePath地址相同那么就是真实的磁盘路径需要转换，否则图片不能正常在网页上显示
				if(!cFile.getFileUrl().startsWith("http://") && cFile.getFileUrl().equals(cFile.getFilePath()))
				{
					//重置图片网络路径
					cFile.setFileUrl(loadImageAction+"filePath="+cFile.getFileUrl()+"&fileName="+cFile.getFileName());
					//重置图片缩略图网络路径
					cFile.setFileShortUrl(loadImageAction+"filePath="+cFile.getFileShortUrl()+"&fileName="+cFile.getFileName());
				}
			}
			childFileList.add(cFile);
		}
		
		request.setAttribute("childFileList", childFileList);
		
		ServletContext servletContext = getServletContext();
		
		String dispatchJsp = "/filelist/child.jsp";		
		if(isChild == null || "".equals(isChild) || "null".equals(isChild)) {
			dispatchJsp = "/filelist/root.jsp";
		}		
		RequestDispatcher rd = servletContext.getRequestDispatcher(dispatchJsp);//跳转到对应业务处理的Servlet
		rd.forward(request, response);
	}

	public void init() throws ServletException {
	}
}