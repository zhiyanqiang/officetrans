package com.netpower.library.util.os;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.netpower.library.util.config.Config;

public class ServerUitl {
	
	/**
	 * 启动或停止致远文库服务器
	 * @param op 参数只能为：start或者stop 如果op为空默认为开启报表服务
	 * @return
	 */
	public static String startOrStopJLibrary(String op) {
		if(null == op || "".equals(op.trim()))
			op = "start";
		if("start".equals(op.toLowerCase().trim()) || "stop".equals(op.toLowerCase().trim()))
			return ServerUitl.execSingle("@C:/Windows/System32/wbem/wmic.exe service where name=\"JLibrary\" "+op.toLowerCase().trim()+"service");
		else
			return null;
	}

	/**
	 * 验证应用程序是否正在运行
	 * @appName 如：appName=memcached.exe
	 * @return
	 */
	public static boolean isRunningApp(String appName) {
		if(Config.isWin())
		{
			StringBuilder sb = null;
			try {
				Process process = Runtime.getRuntime().exec("cmd /c tasklist");//获取任务列表
				InputStream taskListInStream = process.getInputStream();
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(taskListInStream, "GBK"));
				String str = null;
				while((str = bufferedReader.readLine()) != null) {
					if(null == sb)
					{
						sb = new StringBuilder();
					}
					sb.append(str);
					sb.append("\r\n");
				}
				String taskListStr = sb == null ? null : sb.toString();
				
				if(null == taskListStr || "".equals(taskListStr.trim()))
				{//Windows 2008 R2 64位 企业版获取任务列表的方式
					process = Runtime.getRuntime().exec("cmd /c C:\\Windows\\SysWOW64\\tasklist");//获取任务列表
					taskListInStream = process.getInputStream();
					bufferedReader = new BufferedReader(
							new InputStreamReader(taskListInStream, "GBK"));
					str = null;
					while((str = bufferedReader.readLine()) != null) {
						if(null == sb)
						{
							sb = new StringBuilder();
						}
						sb.append(str);
						sb.append("\r\n");
					}
					taskListStr = sb == null ? null : sb.toString();
				}
				//System.out.println("操作系统当前正在运行的服务有：\n"+taskListStr);
				if(null != taskListStr && taskListStr.indexOf(appName) != -1) {
					return true;//如果在当前任务列表中找得到该程序证明改程序正在运行
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	/**
	 * 判断是否安装致远文库服务
	 * @return
	 */
	public static boolean isInstallJLibrary() {
		try {
			Process proc = Runtime.getRuntime().exec("cmd /c SC QUERY JLibrary");
		    int errorLevel = proc.waitFor();
		    if(errorLevel != 1060)
		    {//如果返回错误代码为1060证明没有安装服务
		    	return true;
		    }
		} catch (IOException e) {
			System.out.println("判断是否安装报表服务异常："+e.getMessage());
		} catch (InterruptedException e) {
			System.out.println("判断是否安装报表服务异常："+e.getMessage());
		}
		return false;
	}
	
	/**
	 * 执行单条cmd命令行的命令
	 * @param command 可执命令
	 * @return
	 */
	public static String execSingle(String command) {
		StringBuilder sb = null;
		command = "cmd /c " + command;//加上命令头
		try {
			Process process = Runtime.getRuntime().exec(command);
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(process.getInputStream(), "GBK"));
			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				if(null == sb)
				{
					sb = new StringBuilder();
				}
				sb.append(str);
				sb.append("\r\n");
			}
			process.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String result = sb == null ? null : sb.toString();
//		System.out.println(result);
		return result;
	}
	
	public static List commandLineAsList(String commandLine) {
		List commands = new ArrayList();
		String elt = "";
		boolean insideString = false;

		for (int i = 0; i < commandLine.length(); i++) {
			char c = commandLine.charAt(i);
			if (!insideString && (c == ' ' || c == '\t')) {
				if (elt.length() > 0) {
					commands.add(elt);
					elt = "";
				}
				continue;
			} else if (c == '"') {
				insideString = !insideString;
			}

			elt += c;
		}
		if (elt.length() > 0) {
			commands.add(elt);
		}

		return commands;
	}
	
	public static void execmd(String execString) {
		try {
			System.out.println("正在执行命令: " + execString);

			List commands = commandLineAsList(execString);

			ProcessBuilder pb = new ProcessBuilder(commands);
			pb.redirectErrorStream(true);
			Process p = pb.start();
		} catch (IOException e) {
			System.out.println(e.fillInStackTrace());
		}
	}
	
	public static boolean exec(String execString) {
		boolean result = true;
		try {
			System.out.println("正在执行命令: " + execString);

			List commands = commandLineAsList(execString);
			if(!Config.isWin())
			{
				for(int i=0;i<commands.size();i++)
				{
					try{
						String paramStr=commands.get(i).toString();
						String newParamStr=paramStr.replace("\"","");
						commands.set(i, newParamStr);
					}
					catch(Exception e)
					{
						System.out.println(e.fillInStackTrace());
					}
				}
			}
			System.out.println(commands.toString());

			ProcessBuilder pb = new ProcessBuilder(commands);
			pb.redirectErrorStream(true);
			Process p = pb.start();

			BufferedReader is = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String line;
			while ((line = is.readLine()) != null) {
				if (line.toLowerCase().startsWith("warning")) {
					System.err.println("\tWARNING: " + line);
				} else if (line.toLowerCase().startsWith("error")) {
					System.err.println("\tERROR: " + line);
					result = false;
				} else if (line.toLowerCase().startsWith("fatal")) {
					System.err.println("\tFATAL ERROR: " + line);
					result = false;
				} else {
					System.out.println("\t" + line);
				}
			}
			try {
				p.waitFor();
			} catch (InterruptedException e) {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return result;
	}
	
	public static String execMuch(String execString) {
		String ret = "";
		try {
			List commands = commandLineAsList(execString);

			ProcessBuilder pb = new ProcessBuilder(commands);
			pb.redirectErrorStream(true);
			Process p = pb.start();

			BufferedReader is = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String line;
			while ((line = is.readLine()) != null) {
				ret += "	" + line;
			}
			try {
				p.waitFor();
			} catch (InterruptedException e) {
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return ret;
	}
	
	/**
	 * 执行exe或bat等可执行文件
	 * @param execFilePath 可执行文件路径
	 * @return
	 */
	public static String execFile(String execFilePath) {
		StringBuilder sb = null;
		String command = "cmd /c start " + execFilePath;
		try {
			Process process = Runtime.getRuntime().exec(command);
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(process.getInputStream(), "GBK"));
			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				if(null == sb)
				{
					sb = new StringBuilder();
				}
				sb.append(str);
				sb.append("\r\n");
			}
			process.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String result = sb == null ? null : sb.toString();
//		System.out.println(result);
		return result;
	}
}
