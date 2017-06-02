package com.netpower.library.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.netpower.library.util.file.FileUtil;
import com.netpower.library.util.string.CodeUtil;

/**
 * 视频Servlet
 * 
 * @author gzd
 * 
 */
@SuppressWarnings("serial")
public class VideoViewServlet extends HttpServlet {

	public void destroy() {
		super.destroy();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String fileUrl = String.valueOf(request.getAttribute("fileUrl"));
		if(fileUrl == null || "".equals(fileUrl) || "null".equals(fileUrl)) {
			fileUrl = CodeUtil.decodeAuto(request.getParameter("fileUrl"));
		}
		String extName = FileUtil.getExtName(fileUrl);
		request.setAttribute("fileUrl", fileUrl);
		request.setAttribute("extName", extName);
		response.setCharacterEncoding("UTF-8");
		request.getRequestDispatcher(new StringBuffer("/videoview/video.jsp").toString()).forward(request, response);
	}

	public void init() throws ServletException {
	}
}