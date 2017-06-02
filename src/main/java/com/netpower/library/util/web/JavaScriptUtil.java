package com.netpower.library.util.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author song
 * @date 2009
 * @version 1.0
 * 
 */
public class JavaScriptUtil {

	public static void alertAndMethod(HttpServletResponse response,
			String message, String jsMethod) {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/javascript; charset=UTF-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.println("<script type=\"text/javascript\" language=\"javascript\">");
			out.print(jsMethod);
			out.println(";alert('" + message + "');");
			out.println("</script>");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			out.close();
		}
	}

	public static void responseText(HttpServletResponse response,
			String message, String responseType) {
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/" + responseType + "; charset=utf-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.print(message);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			out.close();
		}
	}
}
