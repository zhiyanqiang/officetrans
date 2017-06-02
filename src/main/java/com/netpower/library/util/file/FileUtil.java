package com.netpower.library.util.file;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.misc.BASE64Encoder;

import com.itextpdf.text.pdf.PdfEncryptor;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.netpower.library.util.config.Config;

/**
 * 文件辅助类
 * 
 * @author gzd
 * 
 */
public class FileUtil {
	
	/** 默认文件后缀名(系统生成文件后缀名，如解压缩文件等) */
	public static final String DEFAULT_FILE_SUFFIX = ".seeyon";
	
	/** size of buffer to use for byte[] operations - defaults to 1024 */
	protected static int BUFFER_SIZE = 1024 * 100;
	private static final Log log = LogFactory.getLog(FileUtil.class);
	/**
	 * 格式化文件大小
	 * 
	 * @param size
	 * @return
	 */
	public static String formatSize(long size) {
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSzie = null;
		if (size < 1024) {
			fileSzie = df.format((double) size) + "byte";
		} else if (size < 1048576) {
			fileSzie = df.format((double) size / 1024) + "KB";
		} else if (size < 1073741824) {
			fileSzie = df.format((double) size / 1048576) + "MB";
		} else {
			fileSzie = df.format((double) size / 1073741824) + "GB";
		}
		return fileSzie;
	}
	
	/**
	 * 获取文件大小
	 * @param Path 文件路径
	 * @return
	 */
	public static long getFileSize(String Path) {
		File file = new File(Path);
		long size = 0;
		// 文件存在时
		if(file.exists()) {
			if(file.isFile()) { // 当前文件为单个文件时,直接返回当前文件大小
				size = file.length();
			} else { // 当前文件为文件夹时,统计文件夹及所有子级文件、文件夹大小
				size = getChildFileSize(file.listFiles(), size);
			}
		}
		return size;
	}
	
	/**
	 * 循环获取文件夹内所有文件、文件夹大小
	 * 
	 * @param childFiles
	 *            文件列表
	 * @param size
	 *            统计大小
	 * @return
	 */
	private static long getChildFileSize(File[] childFiles, long size) {
		for (int i = 0; i <childFiles.length; i++) {
			// 文件存在时统计
			if(childFiles[i].exists()) {
				// 当前文件为系统产出文件不统计
				if(childFiles[i].getName().endsWith(FileUtil.DEFAULT_FILE_SUFFIX)) {
					continue;
				}
				// 文件为单个文件时，直接累加当前文件大小
				if(childFiles[i].isFile()) {
					size += childFiles[i].length();
					continue;
				}
				// 当前为文件夹时统计子级文件大小
				size = getChildFileSize(childFiles[i].listFiles(), size);
			}
		}
		return size;
	}
	
	/**
	 * @param String
	 *            jarPath E:\a.jar or /opt/a.jar
	 * @param String
	 *            dirName 文件夹名称 (可以传空字符串得到整个jar包或者zip包大小)如：sources文件夹
	 * @param String
	 *            fileName 文件名称 (可以传空字符串得到整个jar包或者zip包中指定dirName目录的大小)
	 * @return long 该工程总大小
	 * */
	public static long getFileSizeInJarOrZip(String jarPath, String dirName, String fileName) {
		long projectSize = 0;
		File currentArchive = new File(jarPath);
		ZipFile zf = null;
		try {
			zf = new ZipFile(currentArchive);
			int size = zf.size();
			Enumeration<?> entries = zf.entries();

			String product = dirName + "/" + fileName;
			for (int i = 0; i < size; i++) {
				ZipEntry entry = (ZipEntry) entries.nextElement();

				if (entry.getName().startsWith(product)) {
					projectSize += entry.getSize();
				}
			}
			zf.close();
		} catch (Exception e) {
			System.out.println(e);
			if (zf != null) {
				try {
					zf.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
		return projectSize;
	}

	/**
	 * 读文件
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static String readFile(String path) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(path));
		StringBuilder b = new StringBuilder();
		String t;
		while ((t = br.readLine()) != null) {
			b.append(t);
		}
		return b.toString();
	}
	
	/**
	 * 读文件
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static byte[] readFile(File file) throws IOException {
		byte[] contents = {0};
		if(file == null || !file.exists())
			return contents;
		if(!file.isFile() || !file.canRead())
			return contents;
		FileInputStream fstream = new FileInputStream(file);
		contents = new byte[(int) file.length()];
		fstream.read(contents);
		fstream.close();
		return contents;
	}

	/**
	 * 读取二进制文件用Base64编码并以字符串格式返回
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static String readBinaryFile(File file) throws IOException {
		String contents = null;
		byte[] b = null;
		InputStream in = null;
		try {
		    in = new FileInputStream(file);
		    b = new byte[in.available()];
		    in.read(b);
		    in.close();
		    if(null != b)
		    {
		    	 BASE64Encoder encode = new BASE64Encoder();
		    	 contents = encode.encode(b);
		    }
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
		return contents;
	}
	
	/**
	 * 判断一个目录是否有写的权限
	 * @param path
	 * @return
	 */
	public static boolean canWritAble(String path){
		File f = new File(path.trim());
		if(!f.isDirectory() || !f.canWrite())
			return false;
		return true;
	}
	
	/**
	 * 判断一个文件是否有读的权限
	 * @param path
	 * @return
	 */
	public static boolean canReadAble(String path){
		File f = new File(path.trim());
		if(!f.isFile() || !f.canRead())
			return false;
		return true;
	}
	
	/**
	 * 判断一个文件是否有执行（运行）的权限
	 * @param path
	 * @return
	 */
	public static boolean canExecuteAble(String path){
		File f = new File(path.trim());
		if(!f.isFile() || !f.canExecute())
			return false;
		return true;
	}
	
	/**
	 * 写文件(带缓存区的写文件方式)
	 * @param filePath	
	 * 			文件路径，
	 * @param data
	 * 			源数据，字符串
	 * @return
	 */
	public static File writFile(File file, String data, String encode) throws IOException
	{
		FileOutputStream fos = null;
		DataOutputStream dos = null;
		try
        {
			file = FileUtil.makeDirFile(file);
			fos = new FileOutputStream(file);
			dos = new DataOutputStream(fos);
			byte[] bytes = data.getBytes(encode);
	        int length = bytes.length;
	        int pos = 0;
	        while (pos < length) {
	          int count = length - pos;
	          if (count > BUFFER_SIZE) {
	            count = BUFFER_SIZE;
	          }
	          dos.write(bytes, pos, count);
	          dos.flush();
	          pos += count;
	        }
        }finally
        {
        	if (null != dos) 
        		dos.close();
        }
		return file;
	}
	
	/**
	 * 写文件(带缓存区的写文件方式)
	 * @param filePath	
	 * 			文件路径，
	 * @param data
	 * 			源数据，字符串
	 * @return
	 */
	public static File writFile(File file, String data) throws IOException
	{
		return writFile(file, data, "UTF-8");
	}
	
	/**
	 * 写文件(带缓存区的写文件方式)
	 * @param filePath	
	 * 			文件路径，
	 * @param data
	 * 			源数据，字符串
	 * @return
	 */
	public static File writFile(String filePath, String data) throws IOException
	{
		File file = new File(filePath);
		return writFile(file, data);
	}
	
	/**
	 * 根据filePath创建相应的文件
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static File makeFile(String filePath) throws IOException{
		File file = new File(filePath);
		return makeFile(file);
	}
	
	/**
	 * 根据file创建相应的文件
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static File makeFile(File file) throws IOException{
		if(!file.exists())
		{
			file.createNewFile();
		}
		return file;
	}
	
	/**
	 * 根据filePath创建相应的目录和文件(带递归)
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static File makeDirFile(File file) throws IOException{
		return makeDirFile(file, null);
	}
	
	/**
	 * 根据filePath创建相应的目录和文件(带递归)
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static File makeDirFile(String filePath) throws IOException{
		File file = new File(filePath);
		return makeDirFile(file, null);
	}
	
	/**
	 * 根据filePath创建相应的目录和文件(带递归)
	 * @param filePath
	 * @param isFile 
	 * 			如果要强制创建成文件，有些文件没有后缀名，但他是文件格式，而不是文件夹
	 * @return
	 * @throws IOException
	 */
	public static File makeDirFile(File file, String isFile) throws IOException{
		//首先递归创建目录
		if( file!=null ) { 
		   if( !file.exists() ) {
			   if( file.getParent()!=null ) {
			      File parentDir = new File(file.getParent());
			      if( !parentDir.exists() ) {
			    	  makeDirFile(parentDir, isFile);//递归
			      }
		    	}
		      if(file.getPath().indexOf(".") != -1 && !(file.getPath().endsWith("/") || file.getPath().endsWith("\\")))
		      {//创建文件如果有.且最后一个字符不是路径分隔符的,统一视为文件
		    	  file.createNewFile();
		      }else
		      {//默认创建目录
		    	  if(null != isFile && "true".equals(isFile.toLowerCase()))
		    	  {//如果要强制创建成文件，有些文件没有后缀名，但他是文件格式，而不是文件夹
		    		  file.createNewFile();
		    	  }else
		    	  {
		    		  file.mkdir();
		    	  }
		      }
		   }
		}
		return file;
	}
	
	/**
	 * 只创建目录(带递归)
	 * @param dir
	 */
	public static void makeDir(File dir) {
	  if( dir!=null ) { 
	    if( !dir.exists() ) {
	    	if( dir.getParent()!=null ) {
		      File parentDir = new File(dir.getParent());
		      if( !parentDir.exists() ) {
		      	makeDir(parentDir);
		      }
	    	}
	      dir.mkdir();
	    }
	  }
	}

	/**
	 * 删除文件夹 param folderPath 文件夹完整绝对路径
	 */
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 删除空文件夹
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除指定文件夹下所有文件 param path 文件夹完整绝对路径
	 * 
	 * @param path
	 * @return
	 */
	public static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
				flag = true;
			}
		}
		return flag;
	}
	
	/**
	 * 获取指定目录下的所有文件列表(无递归)
	  * @param path
	 *            文件或者文件夹路径
	 * @return
	 */
	public final static List<File> getFiles(String path) {
		File file = new File(path);
		List<File> list = new ArrayList<File>();
		return getFiles(file, list, false);
	}
	
	/**
	 * 获取指定目录下的所有文件列表(可带递归)
	 * @param path
	 *            文件或者文件夹路径
	 * @param isRecursion
	 *            是否需要递归，true表示要进行递归查找            
	 * @return
	 */
	public final static List<File> getFiles(String path, boolean isRecursion) {
		File file = new File(path);
		List<File> list = new ArrayList<File>();
		return getFiles(file, list, true);
	}
	
	/**
	 * 递归获取指定目录下的所有文件列表
	  * @param file
	 *            文件或者文件夹
	 * @param isRecursion
	 *            是否需要递归，true表示要进行递归查找                 
	 * @return
	 */
	private final static List<File> getFiles(File file, List<File> list, boolean isRecursion) {
		if(!file.exists())
			return list;
		if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			if(null != childFiles)
				for (File childFile : childFiles) {
					if(!isRecursion)
					{//不递归查找
						list.add(childFile);
					}else
					{//递归查找
						if (childFile.isFile()) {
							list.add(childFile);
						} else {
							getFiles(childFile, list, isRecursion);//递归调用
						}
					}
				}
		} else
			list.add(file);
		return list;
	}
	
	/**
	 * 获取文件扩展名
	 * @param file
	 */
    public static String getExtName(File file) { 
    	return getExtName(file.getAbsolutePath());
    }
    
    /**
	 * 获取文件名（不带扩展名）
	 * @param file
	 */
    public static String getFileName(String path) { 
    	File file = new File(path);
    	return file.getName();
    }
	
	/**
	 * 获取文件扩展名
	 * @param fileName
	 */
    public static String getExtName(String fileName) {
    	log.info("fileName="+fileName);
    	if(isDir(fileName))
    		return "dir";
        if ((fileName != null) && (fileName.length() > 0)) { 
            int dot = fileName.lastIndexOf('.'); 
            if ((dot >-1) && (dot < (fileName.length() - 1))) {
            	log.info("后缀名称:" + fileName.substring(dot + 1));
                return fileName.substring(dot + 1); 
            } 
        } 
        return fileName; 
    } 
    
    /**
     * 判断一个路径字符串是否是目录
     * @param dir
     * @return
     */
    public static boolean isDir(String path) {
		if(path == null || path.length() == 0)
			return false;
		File f = new File(path);
		if(f.isDirectory())
			return true;
		return false;
	}
    
    /**
     * 判断一个路径字符串是否是文件
     * @param dir
     * @return
     */
    public static boolean isFile(String path) {
		if(path == null || path.length() == 0)
			return false;
		File f = new File(path);
		if(f.isFile())
			return true;
		return false;
	}
    
    /**
     * 判断指定路径的文件是否存在
     * @param path
     * @return
     */
    public static boolean fileExists(String path) {
		if(path == null || path.length() == 0)
			return false;
		File f = new File(path);
		if(f.exists())
			return true;
		return false;
	}
    
    /**
     * 获取文件编码
     * @param file
     * @return "UTF-8" OR "GBK"
     */
    public static String getFileCoding(File file) {
    	byte[] data = new byte[BUFFER_SIZE];
    	FileInputStream input = null;
    	try{
	    	input = new FileInputStream(file);
			int len = 0;
			while ((len = input.read(data)) != -1) {
				if(len > 1000)
					break;//验证编码不用读取全部文件内容
			}
			input.close();
    	}catch (Exception e) {
    		close(input, null);
		}finally
		{
			close(input, null);
		}
    	
        return getByteCoding(data);
    }

    /**
     * 获取一个字节流的编码
     * @param data
     * @return
     */
	public static String getByteCoding(byte[] data) {
		int count_good_utf = 0;
        int count_bad_utf = 0;
        byte current_byte = 0x00;
        byte previous_byte = 0x00;
        for (int i = 1; i < data.length; i++) {
            current_byte = data[i];
            previous_byte = data[i - 1];
            if ((current_byte & 0xC0) == 0x80) {
                if ((previous_byte & 0xC0) == 0xC0) {
                    count_good_utf++;
                } else if ((previous_byte & 0x80) == 0x00) {
                    count_bad_utf++;
                }
            } else if ((previous_byte & 0xC0) == 0xC0) {
                count_bad_utf++;
            }
        }
        return (count_good_utf > count_bad_utf) ? "UTF-8" : "GBK";
	}
	
	/**
     * 修改PDF文件权限
     * @param file
     * @param oldPassword PDF文件原密码
     * @return
     */
    public static File updatePDFFilePopedom(File file, String oldPassword){
    	File tagetFile = file;//目标PDF文件
    	if(!file.getAbsolutePath().toLowerCase().trim().endsWith(".pdf"+FileUtil.DEFAULT_FILE_SUFFIX))
    	{
    		tagetFile = new File(file.getAbsolutePath()+FileUtil.DEFAULT_FILE_SUFFIX);//另存为目标PDF文件
    	}
    	
    	if(FileUtil.fileExists(tagetFile.getAbsolutePath()) && tagetFile.length() > 0)
    	{//如果文件存在且不是空文件
    		return tagetFile;
    	}
		OutputStream os = null;
		PdfReader reader = null;
		try {
			if(null == oldPassword || "".equals(oldPassword))
			{
				reader = new PdfReader(file.getAbsolutePath());
			}else{
				reader = new PdfReader(file.getAbsolutePath(), oldPassword.getBytes());
			}
			//更改PDF权限
			PdfReader.unethicalreading = true;//ITEXT专门留下的更改PDF文档权限的后门。必须为true时权限更改才能成功
			os = new FileOutputStream(tagetFile);//另存为目标PDF文件流
			PdfEncryptor.encrypt(reader, os, true, "HK@FB", Config.PDF_PASSWORD, PdfWriter.ALLOW_COPY | PdfWriter.ALLOW_PRINTING);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(null != reader)
					reader.close();
				if(null != os)
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return tagetFile;
	}
    
    /**
     * 获取指定文件的总页数
     * @param file
     * @return
     */
    public static int getPDFFileTotalPage(File file){
		int page = 0;
		PdfReader reader = null;
		try {
			reader = new PdfReader(file.getAbsolutePath(), Config.PDF_PASSWORD.getBytes());
			page = reader.getNumberOfPages();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(null != reader)
				reader.close();
		}
		return page;
	}
    
    /**
     * 拷贝文件从原始路径到目标路径
     * 使用NIO中的管道到管道传输
     * 实现方法很简单,分别对2个文件构建输入输出流,并且使用一个字节数组作为我们内存的缓存器, 
     * 然后使用流从f1 中读出数据到缓存里,在将缓存数据写到f2里面去.这里的缓存是2MB的字节数组
     * @param fromPath 源地址
     * @param toPath 现地址
     * @param oldFileName 老文件名
     * @param newFileName 新文件名
     * @return 返回拷贝后的文件大小
     */
    public synchronized static long copeFile(String fromPath,
	    String toPath, String oldFileName, String newFileName) {
    	int bufferSize=2097152;//这里的缓存是2MB的字节数组
    	File file = null;
		FileInputStream in = null;
		FileOutputStream out = null;
		try {
		    File filePath = new File(toPath);
		    //if (!filePath.exists()) { // 目标文件夹不存在,则创建文件夹
		    //	filePath.mkdirs();
		    //}
		    if(!filePath.exists())
		    {
		    	return 0L;//目标文件不存在就结束
		    }
		    //源文件
		    in = new FileInputStream(new StringBuffer(fromPath).append(File.separator).append(oldFileName).toString());
		    //目标文件
		    out = new FileOutputStream(new StringBuffer(toPath).append(File.separator).append(newFileName).toString());
			
		    FileChannel inC=in.getChannel();
	        FileChannel outC=out.getChannel();
	        int i=0;
	        while(true){
	            if(inC.position()==inC.size()){
	                inC.close();
	                outC.close();
	                break;
	            }
	            if((inC.size()-inC.position())<20971520)
	            	bufferSize=(int)(inC.size()-inC.position());
	            else
	            	bufferSize=20971520;
	            inC.transferTo(inC.position(),bufferSize,outC);
	            inC.position(inC.position()+bufferSize);
	            i++;
	        }
		    // 判断文件是否存在
		    file = new File(new StringBuffer(toPath).append(File.separator).append(newFileName).toString());
		    if (file.isFile()) {
		    	return file.length();
		    }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		    System.out.println(e.getMessage());
		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
		    close(in, out);
		}
		return 0L;
    }
    
    /**
     * 调整图片文件的尺寸
     * @param srcImgPath 原图路径
     * @param newImgPath 新图路径
     * @param width 指定的宽
     * @param height 指定的高
     * @throws IOException
     */
    public static void resizeImage(String srcImgPath, String newImgPath,
    	    int width, int height) throws IOException {
    	File srcFile = new File(srcImgPath);
    	Image srcImg = ImageIO.read(srcFile);
    	BufferedImage buffImg = null;
    	buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    	buffImg.getGraphics().drawImage(
    		srcImg.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0,
    		0, null);

    	ImageIO.write(buffImg, "gif", new File(newImgPath));
    }

    /**
     * 关闭文件输入输出流
     * @param in
     * @param out
     */
	public static void close(InputStream in, OutputStream out) {
		if (out != null) {
		try {
		    out.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
		}
		if (in != null) {
		try {
		    in.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
		}
	}
    
    /**
     * 搜索指定目录下，与通配符匹配的文件
     * @param dir
     * @param pattern \n通配符可以为：" "或者"."或者"*"
     * @return
     */
    public static List<String> searchFiles(String dir, String pattern){
		List<String> result = new ArrayList<String>();
		File d = new File(dir);
		if(!d.isDirectory() || null == pattern)
			return result;
		String[] files = d.list();
		pattern = pattern.replace(" ", "*");
		pattern = pattern.replace(".", "[.]");
		pattern = pattern.replace("*", "(.*)");
		Pattern r = Pattern.compile(pattern);
		for(int i = 0; i < files.length; i++){
			Matcher m = r.matcher(files[i]);
			if (m.matches( )) {
				result.add(dir + files[i]);
			}
		}
		return result;
	}

//	public static void main(String[] args) {
//		StringBuffer data = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>");//XML文件内容
//		data.append("\n").append("<a>12</a><b>12</b><c>12</c>");
//		try {
//			FileUtil.writFile("D:\\XmlContext.xml", String.valueOf(data));//创建目录和文件（带递归）并写文件
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		for(com.netpower.library.util.file.File file : getAllPath("D:\\Tomcat6.80\\webapps\\JLibrary\\sp\\中文.zip.seeyon\\中文\\中文文件夹\\新建文件夹\\新建文本文档.txt")) {
//			System.out.println(file.getFileName());
//		}
//	}
	
	/**
	 * 遍历文件全路径
	 * @param filePath 文件路径
	 * @return
	 */
	public static List<com.netpower.library.util.file.File> getAllPath(String filePath) {
		List<com.netpower.library.util.file.File> files = new ArrayList<com.netpower.library.util.file.File>();
		File parentFile = new File(filePath).getParentFile();
		// 文件属于压缩文件
		if(filePath.indexOf(FileUtil.DEFAULT_FILE_SUFFIX) != -1) {
			List<File> tmpFiles = new ArrayList<File>();
			// 循环所有级文件名称
			while(true) {
				tmpFiles.add(0, parentFile);
				parentFile = parentFile.getParentFile();
				if(parentFile.getParentFile() == null) {
					break;
				}
			} 
			// 查询出解压缩物理路径层级
			int index = 0;
			for(File f : tmpFiles) {
				if(f.getName().indexOf(FileUtil.DEFAULT_FILE_SUFFIX) != -1) {
					break;
				}
				index ++;
			}
			// 从解压缩文件层级开始,选择解压缩文件的文件路径层级顺序
			for (; index < tmpFiles.size(); index++) {
				files.add(new com.netpower.library.util.file.File(tmpFiles.get(index).getName().replaceAll(FileUtil.DEFAULT_FILE_SUFFIX, "")));
			}
		} else {
			files.add(0, new com.netpower.library.util.file.File(parentFile.getName()));
		}
		return files;
	}	
	
	/**
	 * 慎用哈！网上有人说该方法在window下是正常的，在linux下面是不正常的。这个很难说通，SUN不可能搞出这种平台不一致的代码出来啊。 
	 * @param srcFilePath
	 * @param toFilePath
	 * @return 是否重命名成功
	 * 注意：注意看结果，从C盘到E盘失败了，从C盘到D盘成功了。因为我的电脑C、D两个盘是NTFS格式的，而E盘是FAT32格式的。
	 * 所以从C到E就是上面文章所说的"file systems"不一样。从C到D由于同是NTFS分区，所以不存在这个问题，当然就成功了。 
	 * 果然是不能把File#renameTo(File)当作move方法使用。 
	 * 可以考虑使用apache组织的commons-io包里面的FileUtils#copyFile(File,File)
	 * 和FileUtils#copyFileToDirectory(File,File)方法实现copy的效果。
	 * 至于删除嘛，我想如果要求不是那么精确，可以调用File#deleteOnExit()方法，在虚拟机终止的时候，删除掉这个目录或文件。 
	 */
	public static boolean renameFile(String srcFilePath, String toFilePath) {
        File toBeRenamed = new File(srcFilePath);
        //检查要重命名的文件是否存在，是否是文件
        if (!toBeRenamed.exists() || toBeRenamed.isDirectory()) {
//            System.out.println("File does not exist: " + srcFilePath);
            return false;
        }

        File newFile = new File(toFilePath);
        boolean flag = false;
        if (newFile.exists() && !newFile.isDirectory()) {
        	flag = newFile.delete();
        }
        //修改文件名
        if (flag && toBeRenamed.renameTo(newFile)) {
//            System.out.println("File has been renamed.");
        	return true;
        } else {
//            System.out.println("Error renmaing file");
        	return false;
        }
    }
}