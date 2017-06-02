package com.netpower.library.util.error;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * @copyright (C) 2003-2013 成都网威技术有限公司版权所有。
 * @author 周 讯
 * @date 2013-10-25
 * @time 下午01:04:20
 * @package com.netpower.library.util.error
 * @desc:异常处理过滤器
 */
public class ExceptionFilter implements Filter{
	private String errorPage = "";

	public void destroy() {
		
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		try {
			chain.doFilter(request, response);
		} catch(RuntimeException re) {
			//报错跳转到指定错误页面
			try{
			request.getRequestDispatcher(errorPage).forward(request, response);
			}catch (Exception e) {
			}
		}		
	}

	public void init(FilterConfig config) throws ServletException {
			errorPage = config.getInitParameter("errorPage");
			if("".equals(errorPage)) {
				errorPage = "/error/FileNotFind.jsp";
			}
	}
	
}
