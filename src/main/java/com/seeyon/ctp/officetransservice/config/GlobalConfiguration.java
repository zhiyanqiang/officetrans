package com.seeyon.ctp.officetransservice.config;

import java.util.List;
import java.util.concurrent.Semaphore;

import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * 全局配置类，默认从application.properties文件中加载注入配置项。
 * 从文件微服务借鉴来的，暂时没有使用到
 * @author zhiyanqiang
 * 
 * createDate: 2017-1-12
 */
@ConfigurationProperties(prefix = "global") 
public class GlobalConfiguration {
	
	
	
	private String baseDir;
	
	private int maxDownloadNum;
	
	private boolean downloadJsonResp;
	
	private List<String> authorizedIps;
	
	public List<String> getAuthorizedIps() {
		return authorizedIps;
	}

	public void setAuthorizedIps(List<String> authorizedIps) {
		this.authorizedIps = authorizedIps;
	}

	public int getMaxDownloadNum() {
		return maxDownloadNum;
	}

	private Semaphore semaphore;

	public Semaphore getSemaphore() {
		return semaphore;
	}

	public String getBaseDir() {
		return baseDir;
	}

	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}
	
	
	public synchronized void setMaxDownloadNum(int maxDownloadNum) {
		if(semaphore!=null && semaphore.availablePermits()<this.maxDownloadNum){
			semaphore.release(this.maxDownloadNum);
		}
		this.maxDownloadNum = maxDownloadNum;
		semaphore = new Semaphore(maxDownloadNum);
		
	}

	public boolean isDownloadJsonResp() {
		return downloadJsonResp;
	}

	public void setDownloadJsonResp(boolean downloadJsonResp) {
		this.downloadJsonResp = downloadJsonResp;
	}
	
	
}
