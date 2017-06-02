package com.netpower.library.util.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.regexp.RE;

import com.netpower.library.util.JArray;

public class Config {
	protected static JArray config = null;
	public static String AppPath = null;
	public static ScheduledExecutorService deleteFileExecutor=Executors.newScheduledThreadPool(1);
	public static ConcurrentSkipListSet<Long> fileIdsWatingTrans=new ConcurrentSkipListSet<Long>();//暂存正在转换的文件的ID，防止同一个文件多次转换
	public static String xpdfPath=null;
	public final static String PDF_PASSWORD = "SeeYon";//打开转化后的PDF文档密码 
	private static String ROOT = System.getProperty("user.home");
	public static String OS = System.getProperty("os.name");
	public static String Office_Max_Thread="10";
	public static ExecutorService cachedThreadPool=null;
	public static String DIRECTORY_SEPARATOR = System.getProperty("file.separator");
	protected static int len = 0;
	
	public static String OpenOffice_TOOL_PATH = "";//OpenOffice系统工具安装路径
	public static String OpenOffice_SERVER_IP = "127.0.0.1";//OpenOffice文档转换服务IP地址
	public static String OpenOffice_SERVER_PORT = "8100";//OpenOffice文档转换服务端口
	
	public static String PDF2SWF_TOOL_PATH = "";//PDF转换为SWF文件系统工具安装路径
	public static String PDF2JSON_TOOL_PATH = "";//PDF转换为JSON文件系统工具安装路径
	public static String OfficeTrans_Root_Dir="";
	public static String File_Retains_Day="30";//保留的天数
	
	public Config() {
		if(isChange()) {
			config = parse_ini_file(getConfigFilename());
		}
	}

	public boolean isChange(){
		File f = new File(getConfigFilename());
		if(!f.isFile() || !f.canRead()){
			config = null;
			return false;
		}
		if(f.length() == len)
			return false;
		len = (int) f.length();
		return true;
	}

	public static boolean isWin(){
		if(OS.contains("Win"))
			return true;
		return false;
	}
	
	public static String getExeSuffix() {
		String exe = "";//默认Linux
		if(isWin()){
			exe = ".exe";
		}
		return exe;
	}

	public String getConfig(String key) {
		key = key.trim();
		if(key == null || key.length() <= 0 || config == null)
			return null;
		return config.get(key);
    }

	public String getConfig(String key, String def) {
		key = key.trim();
		if(key == null || key.length() <= 0 || config == null)
			return def;
		return config.get(key, def);
    }

	public JArray getConfigs(){
		if(config == null)return null;
		return (JArray) config.clone();
	}

	public String getConfigFilename(){
		String configPath = ROOT + DIRECTORY_SEPARATOR + "jspConfig" + DIRECTORY_SEPARATOR;
		File f = new File(configPath);
		if(!f.isDirectory())
			f.mkdirs();
		return configPath + "jspconfig.ini";
	}

	public boolean saveConfig(JArray ht){
		if(!write_ini(ht, getConfigFilename()))
			return false;
		config = ht;
		return true;
	}

	public boolean write_ini(JArray ht, String fname) {
		BufferedWriter bufferedWriter = null;
		FileWriter f = null;
		int tab = 30;
        try {
        	f = new FileWriter(fname);
            bufferedWriter = new BufferedWriter(f);
            for(int i = 0; i < ht.len(); i++){
            	String index = ht.getIndex(i);
            	bufferedWriter.write(index);
            	String data = ht.get(i).trim();
            	if(data.length() > 0){
            		for(int j = 0; j < tab - index.length(); j++)
            			bufferedWriter.write(" ");
        			bufferedWriter.write("= ");
	            	bufferedWriter.write(data);
            	}
            	bufferedWriter.write("\r\n");
            }
        	bufferedWriter.close();
        	f.close();
		} catch (Exception e) {
			try {
				if(f != null)
					f.close();
				if(bufferedWriter != null)
					bufferedWriter.close();
			} catch (IOException e1) {
			}
			e.printStackTrace();
			return false;
		}
        return true;
	}

	public JArray parse_ini_file(String fname){
		File f = new File(fname);
		if(!f.isFile() || !f.canRead())
			return null;
		JArray ret = new JArray();
		try {
			FileInputStream fstream = new FileInputStream(fname);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				String[] ri = strLine.split("=");
				String data = ri[0].trim();
				String value = null;
				if(ri.length > 1) {
					value = ri[1].trim();
				}
				ret.add(data, value);
			}
			in.close();
			fstream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static String separate(String path){
		if(isWin())
			return (path.trim()).replace("\\\\", "\\").replace("\\", "/");
		else
			return (path.trim()).replace("//", "/");
	}

	public void setConfig(String key, String data){
		if(key == null || key.trim().length() == 0)
			return;
		config.set(key, data);
	}

	public static String strip_non_numerics(String string) {
		echo ("***Config 	strip_non_numerices  string = " + string);
		String pattern = "[\\D]";
		RE r = new RE(pattern);
		string = r.subst(string, "");
		return string;
	}

	public static void echo(Object echo){
		System.out.println(echo);
	}
	
	/**
	 * 文档渲染类型
	 * @author 黄奎
	 * 
	 */
	public enum RenderType {
		flash {
			@Override
			public String getValue() {
				return "flash";
			}
		},
		html {
			@Override
			public String getValue() {
				return "html";
			}
		},
		html5 {
			@Override
			public String getValue() {
				return "html5";
			}
		};
		public abstract String getValue();
	}
	
	/**
	 * 写配置文件
	 * @param srcFilePath
	 * @param tagFilePath
	 * @param configs
	 * @throws Exception
	 */
	public static void writeConfig(String srcFilePath, String tagFilePath, Map configs)throws Exception{
		//读取配置文件
		StringBuilder temp = new StringBuilder();
		InputStream ins = null;
		BufferedReader br = null;
		try {
			ins = new FileInputStream(srcFilePath);
			br = new BufferedReader(new InputStreamReader(ins,"UTF-8"));
			String k;
			while ((k = br.readLine()) != null) {
				temp.append(k);
				temp.append("\r\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			br.close();
			ins.close();
		}
		
		//更新配置文件内容
		String content = temp.toString();
		content = content.replace("${AppPath}", String.valueOf(configs.get("AppPath")));	
		
		//保存更新后的配置文件
		FileOutputStream fos = null;
		try{
			fos = new FileOutputStream(new File(tagFilePath));
			byte[] buffer = content.getBytes("UTF-8");
			fos.write(buffer);
			fos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(null != fos)
				fos.close();
		}
	}
}