package com.netpower.library.util.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/***********************************************************************   
 *   
 *   ApplicationListener.java     
 *   @copyright       Copyright:   2009-2012     
 *   @creator         黄奎<br/>   
 *   @create-time   Oct 26, 2009   2:33:35 PM   
 *   @revision         $Id:     *   
 ***********************************************************************/
public class ApplicationListener   implements ServletContextListener {
	public static String WEBAPPROOTKEY = "";//应用系统名称
	
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub
	}

	public void contextInitialized(ServletContextEvent sce) {
		// TODO Auto-generated method stub
		String webAppRootKey = sce.getServletContext().getInitParameter("webAppRootKey");
		WEBAPPROOTKEY = webAppRootKey;
		String webAppRootPath = sce.getServletContext().getRealPath("/");
	    System.setProperty(webAppRootKey , webAppRootPath);
	    String path =System.getProperty(webAppRootKey);
	    System.out.println("系统"+webAppRootKey+"的绝对路径是:"+path);
	}
}