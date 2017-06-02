package com.netpower.library.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.netpower.library.util.file.FileUtil;
import com.netpower.library.util.string.CodeUtil;



public class PictureViewServlet extends HttpServlet{
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
		
		String filePath = CodeUtil.decodeAuto(request.getParameter("filePath"));
		String fileUrl = CodeUtil.decodeAuto(request.getParameter("fileUrl"));
		String fileName = CodeUtil.decodeAuto(request.getParameter("fileName"));
		
		if(fileUrl == null || "".equals(fileUrl) || "null".equals(fileUrl)) {
			fileUrl = String.valueOf(request.getAttribute("fileUrl"));
			fileName = String.valueOf(request.getAttribute("fileName"));
		}
		
		if(filePath == null || "".equals(filePath) || "null".equals(filePath)) {
			filePath = String.valueOf(request.getAttribute("filePath"));
		}
		
		if(filePath == null || "".equals(filePath) || "null".equals(filePath)) {
			filePath = fileUrl;
		}
		
		filePath = filePath.replace("\\", "/");
		
		if(fileUrl == null || "".equals(fileUrl) || "null".equals(fileUrl)) {
			fileUrl = filePath;
			fileName = fileUrl.substring(fileUrl.lastIndexOf("/")+1);
		}
		
		if(fileName == null || "".equals(fileName) || "null".equals(fileName)) {
			fileName = filePath.substring(filePath.lastIndexOf("/")+1);
		}
		
		String fileShortUrl = fileUrl + FileUtil.DEFAULT_FILE_SUFFIX + "/" + fileUrl.substring(fileUrl.lastIndexOf("/"));
		try {
			//如果网络地址fileUrl不是http://开头,且与真实filePath地址相同那么就是真实的磁盘路径需要转换，否则图片不能正常在网页上显示
			if(!fileUrl.startsWith("http://") && fileUrl.equals(filePath))
			{
				//重置图片网络路径
				fileUrl = loadImageAction+"filePath="+fileUrl+"&fileName="+fileName;
				//重置图片缩略图网络路径
				fileShortUrl = loadImageAction+"filePath="+fileShortUrl+"&fileName="+fileName;
			}
			
			JSONArray jar = new JSONArray();
			JSONObject jobj = new JSONObject();
			jobj.put("fileUrl", fileUrl);
			jobj.put("fileName", fileName);		
			jobj.put("fileShortUrl",fileShortUrl);		
			jar.put(jobj);
			request.setAttribute("picArr", jar.toString());
		} catch (JSONException e) {			
			e.printStackTrace();
			request.setAttribute("picArr",new JSONArray().toString());
		}		
		response.setCharacterEncoding("UTF-8");
		request.getRequestDispatcher(new StringBuffer("/pictureView/picshow.jsp").toString()).forward(request, response);
	}

	public void init() throws ServletException {
	}
}
