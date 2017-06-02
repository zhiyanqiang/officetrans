package com.netpower.library.servlet.flexpaper;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.netpower.library.util.DocConverter;
import com.netpower.library.util.string.CodeUtil;

public class SwfSizeServlet extends HttpServlet {
	
	/**
	 * Constructor of the object.
	 */
	public SwfSizeServlet() {
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
		String doc = CodeUtil.decodeAuto(request.getParameter("doc"));
		String page = request.getParameter("page");
		String callback = request.getParameter("callback");
		if (doc == null)
			return;
		
		String swfFilePath = doc + ".swf";//此处默认在最原始源文件所在目录下生成同名SWF分页文件
		if (null != page && !"".equals(page.trim())) {
			swfFilePath = doc + "_" + page + ".swf";//此处默认在最原始源文件所在目录下生成同名SWF分页文件
		}
		
		if(callback == null)
		{
			callback = "";
		}
		
		DocConverter docConverter = new DocConverter();
		response.setContentType("application/json");
		String outs = "({\"height\":" + docConverter.swfSize(swfFilePath,"height");
		outs += ",\"width\":" + docConverter.swfSize(swfFilePath,"width");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.print(callback + outs + ")}");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			out.close();
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
