package com.netpower.library.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestServlet extends HttpServlet {
	 @Override
	    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
	        System.out.println(">>>>>>>>>>doGet()<<<<<<<<<<<");
	        doPost(req, resp);
	    }

	    @Override
	    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
	        System.out.println(">>>>>>>>>>doPost()<<<<<<<<<<<");
	        resp.setContentType("text/html");
	        resp.setCharacterEncoding("UTF-8");
	        PrintWriter out = resp.getWriter();  
	        out.println("<html>");  
	        out.println("<head>");  
	        out.println("<title>Hello World</title>");  
	        out.println("</head>");  
	        out.println("<body>");  
	        out.println("<h1>大家好，我的名字叫Servlet</h1>");  
	        out.println("</body>");  
	        out.println("</html>");
	        out.flush();
	    }
}
