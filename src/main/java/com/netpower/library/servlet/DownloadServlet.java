package com.netpower.library.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.netpower.library.util.file.FileDownload;
import com.netpower.library.util.file.FileUtil;
import com.netpower.library.util.string.CodeUtil;

/**
 * 下载Servlet
 * 
 * @author gzd
 * 
 */
@SuppressWarnings("serial")
public class DownloadServlet extends HttpServlet {

	public void destroy() {
		super.destroy();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String filePath = request.getAttribute("filePath") == null ? CodeUtil.decodeAuto(request.getParameter("filePath")) : String.valueOf(request.getAttribute("filePath"));
		String fileName = request.getAttribute("fileName") == null ? CodeUtil.decodeAuto(request.getParameter("fileName")) : String.valueOf(request.getAttribute("fileName"));
		String msg = null;
		// 判断文件是否存在
		if (FileUtil.isFile(filePath)) { // 存在
			try {
				FileDownload.downloadFile(filePath, fileName, response);
			} catch (Exception e) {
				msg = "下载失败，请刷新后再试！";
			}
		} else { // 不存在
			msg = "此文件已不存在，请与管理员联系！";
		}
		request.setAttribute("msg", msg);
		request.getRequestDispatcher("/download/download.jsp").forward(request, response);
	}

	public void init() throws ServletException {
	}
}