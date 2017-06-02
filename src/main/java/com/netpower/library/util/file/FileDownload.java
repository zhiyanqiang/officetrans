package com.netpower.library.util.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletResponse;

/**
 * 下载
 * @author gzd
 */
public class FileDownload {

	/**
	 * 文件下载
	 * 
	 * @param downFileName
	 *            待下载的文件实际名称
	 * @param targetPath
	 *            文件路径
	 * @param fileName
	 *            显示名称
	 * @param response
	 *            HttpServletResponse对象      
	 * @throws MyException
	 */
	public static void downloadFile(String downFileName, String targetPath,
			String fileName, HttpServletResponse response){
		downloadFile(new StringBuffer(targetPath).append(downFileName).toString(), targetPath, fileName, response);
	}
	
	/**
	 * 文件下载
	 * 
	 * @param downFileFullName
	 *            待下载文件全路径
	 * @param fileName
	 *            文件名称
	 * @param response
	 *            ttpServletResponse对象
	 */
	public static void downloadFile(String downFileFullName, String fileName,
			HttpServletResponse response) {
		/// <summary>
		/// 文件块的大小
		/// </summary>
		int BLOCKSIZE = 16 * 1024;//每次拷贝文件块时重新指定文件块的大小（因此不能设为全局变量）
		try {
			fileName = URLEncoder.encode(fileName, "UTF-8");
			StringBuffer attachment = new StringBuffer("attachment;filename=");
			attachment.append(fileName);
			response.addHeader("content-Disposition", attachment.toString());
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		OutputStream outp = null;
		BufferedInputStream in = null;
		try {
			outp = response.getOutputStream();
			
			//源文件
		    File srcFile = new File(downFileFullName);
		    FileInputStream fileinputstream = new FileInputStream(srcFile);
			long l = srcFile.length();
			int j = 0;
			byte abyte0[] = new byte[BLOCKSIZE];
			response.setContentLength((int) l);
			while ((long) j < l) {
				int k = fileinputstream.read(abyte0, 0, BLOCKSIZE);
				j += k;
				outp.write(abyte0, 0, k);
			}
			fileinputstream.close();
		} catch (Exception e) {
		} finally {
			if (outp != null) {
				try {
					outp.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				outp = null;
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				in = null;
			}
		}
	}
}