package com.netpower.library.servlet;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.netpower.library.util.string.CodeUtil;



public class LoadImageServlet extends HttpServlet{
	public void destroy() {
		super.destroy();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String filePath = CodeUtil.decodeAuto(request.getParameter("filePath"));
		String fileName = CodeUtil.decodeAuto(request.getParameter("fileName"));
		if(filePath == null || "".equals(filePath) || "null".equals(filePath)) {
			filePath = String.valueOf(request.getAttribute("filePath"));
			fileName = String.valueOf(request.getAttribute("fileName"));
		}
		filePath = filePath.replace("\\", "/");
		if(fileName == null || "".equals(fileName) || "null".equals(fileName)) {
			fileName = filePath.substring(filePath.lastIndexOf("/")+1);
		}
		
		
		File file = new File(filePath);
		if(!file.exists())
		{//如果文件不存在直接返回
			return;
		}
		//读取文件
	    InputStream in = new FileInputStream(file);  
	    BufferedOutputStream bout = new BufferedOutputStream(response.getOutputStream());  
	    try {  
	        byte b[] = new byte[1024];  
	        int len = in.read(b);
	        while (len > 0) {  
	            bout.write(b, 0, len);  
	            len = in.read(b);  
	        }  
	    } catch (Exception e) {  
	        if (e.getClass().getName().equals("org.apache.catalina.connector.ClientAbortException")) {  
	            // 用户取消下载任务报的错，无需处理  
	        } else {  
	            //TODO
	        	System.out.println("动态加载图片文件出错！该图片路径："+filePath+"\n 该图片真实名称: "+fileName);
	        }  
	    } finally {  
	        bout.close();  
	        in.close();  
	    }  
	}

	public void init() throws ServletException {
	}
}
