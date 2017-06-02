package com.seeyon.ctp.officetransservice;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.netpower.library.servlet.ConvertServlet;
import com.netpower.library.servlet.TestServlet;
import com.netpower.library.servlet.flexpaper.ToolSetupListener;
import com.seeyon.ctp.officetransservice.config.GlobalConfiguration;

@SpringBootApplication
@EnableConfigurationProperties({ GlobalConfiguration.class })
@EnableScheduling
public class CtpOfficeTransServiceApplication {
  /**
   * 注册ServletListerner，在服务启动时需要完成一些初始化的工作。
   * ToolSetupListener中来完成这些工作
   * 创建人:zhiyanqiang	
   * 功能描述:   
   * 创建时间：2017年1月12日 上午10:16:31    
   */
   @Bean
   public ServletListenerRegistrationBean<ToolSetupListener> servletListenerRegistrationBean(){
	        ServletListenerRegistrationBean<ToolSetupListener> servletListenerRegistrationBean = new ServletListenerRegistrationBean();
	        servletListenerRegistrationBean.setListener(new ToolSetupListener());
	        return servletListenerRegistrationBean;
	    }
   /**
    * 注册文件转换的Servlet，该Servlet是A8与文件转换服务的接口
    * A8通过该Servlet调用文件转换服务的相关内容
    * ConvertServlet中来完成这些工作
    * 创建人:zhiyanqiang	
    * 创建时间：2017年1月12日 上午10:14:36    
    */
	@Bean
	public ServletRegistrationBean ConverServletBean() {
		return new ServletRegistrationBean(new ConvertServlet(), "/ConvertServlet");
	}
	@Bean
	public ServletRegistrationBean servletTestBean() {
		return new ServletRegistrationBean(new TestServlet(), "/testServlet");
	}
	/**
	 * 微服务启动的入口
	 * 创建人:zhiyanqiang	
	 * 功能描述:   
	 * 创建时间：2017年1月12日 上午10:18:06    
	 */
	public static void main(String[] args) {
		
		SpringApplication.run(CtpOfficeTransServiceApplication.class, args);
	}
}
