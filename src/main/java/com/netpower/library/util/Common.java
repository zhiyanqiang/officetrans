package com.netpower.library.util;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.netpower.library.util.config.Config;
import com.netpower.library.util.file.FileUtil;
import com.netpower.library.util.os.ServerUitl;

public class Common {

	public void setCacheHeaders(HttpServletResponse response){
		response.setHeader("Cache-Control", "private, max-age=10800, pre-check=10800");
		response.setHeader("Pragma", "private");
		SimpleDateFormat RFC822DATEFORMAT = new SimpleDateFormat("EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'Z", Locale.US);
		response.setHeader("Expires", RFC822DATEFORMAT.format(new Date()));
	}
	
	public void clearCacheHeaders(HttpServletResponse response){
		response.setHeader("Cache-Control", "private, max-age=0, pre-check=0");
		response.setHeader("Pragma", "No-Cache");
		response.setHeader("Expires", "0");
	}

	public boolean endOrRespond(HttpServletRequest request, HttpServletResponse response){
		String mod = request.getHeader("If-Modified-Since");
		if(mod == null){
			return true;
		}
		response.setHeader("Last-Modified", mod);
		return false;
	}

	public String getForkCommandStart(){
		if(Config.isWin())
			return "START ";
		return "";
	}

	public String getForkCommandEnd(){
		if(	!Config.isWin() )
			return " >/dev/null 2>&1 &";
		return "";
	}

	public int getStringHashCode(String string){
		string = string.trim();
		if(string == null || string.length() == 0)
			return 0;
		int hash = 0;
		for(int i = 0; i < string.length(); i++){
			hash = 31 * hash + (int)string.charAt(i);
		}
		return hash;
    }

	public BufferedImage createResizedCopy(Image originalImage, 
            int scaledWidth, int scaledHeight, 
            boolean preserveAlpha) {
		int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
		BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, imageType);
		Graphics2D g = scaledBI.createGraphics();
	    if (preserveAlpha) {
	    	g.setComposite(AlphaComposite.Src);
	    }
	    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null); 
	    g.dispose();
	    return scaledBI;
	}


	public void scale(String src, int width, int height, String dest) throws IOException {
		BufferedImage bsrc = ImageIO.read(new File(src));
		BufferedImage bdest = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		bdest = createResizedCopy(bsrc, width, height, false);
		ImageIO.write(bdest,"png",new File(dest));
	}

	public String generateImage(String source_file, String cache_file, String resolution, String outformat) {
		String extension = source_file.substring(source_file.lastIndexOf(".") + 1).toLowerCase();
		try {
			BufferedImage bimg = ImageIO.read(new File(source_file));
			int width          = bimg.getWidth();
			int height         = bimg.getHeight();
			int resol = 1;
			
			if(resolution!=null){
				try{
					resol = Integer.parseInt(resolution.trim());
				}catch(Exception e){
					resol = width;
				}
				if (width <= resol) {
					return source_file;
				}
			}else{
				resol = width;
			}
			
			double ratio      = (double)height / (double)width;
			int new_width  = resol;
			int new_height = (int)Math.ceil((double)(new_width * ratio));
			BufferedImage bdest = new BufferedImage(new_width, new_height, BufferedImage.TYPE_INT_ARGB);
			bdest = createResizedCopy(bimg, new_width, new_height, false);
			ImageIO.write(bdest, extension, new File(cache_file));
		}catch(Exception e){
			cache_file = source_file;
			e.printStackTrace();
		}
		return cache_file;
	}
	
	public static boolean doc2pdfEnabled(String path_to_soffice){
		if ("/".equals(Config.DIRECTORY_SEPARATOR) || ("\\".equals(Config.DIRECTORY_SEPARATOR) && !"soffice".equals(path_to_soffice))) {
			if(!FileUtil.fileExists(path_to_soffice))
				return false;
			else
				return true;
		}
		return false;
	}
	
	/**
	 * 启动OpenOffice Server服务
	 * @param path_to_soffice
	 * @return
	 */
	public static String startOpenOfficeServer(String path_to_soffice) throws Exception{
		String result = "";
		if ("/".equals(Config.DIRECTORY_SEPARATOR) || ("\\".equals(Config.DIRECTORY_SEPARATOR) && !"soffice".equals(path_to_soffice))) {
			if(!FileUtil.fileExists(path_to_soffice))
				return "当前系统中找不到，OpenOffice Server服务，请确认是否安装，OpenOffice软件";
			try{
				if(Config.isWin()){
					ServerUitl.execmd('"' + path_to_soffice + '"' + " -headless -accept=" + '"' + "socket,host="+Config.OpenOffice_SERVER_IP+",port="+Config.OpenOffice_SERVER_PORT+";urp;" + '"');
				}else{	
					ServerUitl.execmd(path_to_soffice + " --headless --nofirststartwizard --accept=" + '"' + "socket,host="+Config.OpenOffice_SERVER_IP+",port="+Config.OpenOffice_SERVER_PORT+";urp;" + '"' + " &");
				}
			}catch (Exception e) {
				result = "启动OpenOffice Server服务错误！原因："+e.getMessage();
				System.out.println(result);
				throw new Exception(result);
			}
		}
		return result;
	}
	
	public static boolean pdf2jsonEnabled(String path_to_pdf2json){
		if ("/".equals(Config.DIRECTORY_SEPARATOR) || ("\\".equals(Config.DIRECTORY_SEPARATOR) && !"pdf2json".equals(path_to_pdf2json))) {
			boolean result = false;
			if(!FileUtil.fileExists(path_to_pdf2json))
				return result;
			try
			{
				if(Config.isWin()){
					result = ServerUitl.exec('"' + path_to_pdf2json + '"' + " -help 2>&1");
				}else{
					result = ServerUitl.exec(path_to_pdf2json + " -help 2>&1");
				}
				if (result) {
						return true;
				}
			}catch (Exception e) {
				return false;
			}
		}
		return false;
	}
	
	public static boolean pdf2swfEnabled(String path_to_pdf2swf){
		if ("/".equals(Config.DIRECTORY_SEPARATOR) || ("\\".equals(Config.DIRECTORY_SEPARATOR) && !"pdf2swf".equals(path_to_pdf2swf))) {
			boolean out = false;
			if(!FileUtil.fileExists(path_to_pdf2swf))
				return out;
			try{
				if(Config.isWin()){
					out = ServerUitl.exec('"' + path_to_pdf2swf + '"' + " --version 2>&1");
				}else{	
					out = ServerUitl.exec(path_to_pdf2swf + " --version 2>&1");
				}
				if (out) {
					return true;
				}
			}catch (Exception e) {
				return false;
			}
		}
		return false;
	}
	
	/**
	 * 获取pdf转换为swf或json的命令
	 * @return
	 */
	public JArray getConverCommands()
	{
		String exe = Config.getExeSuffix();
		
		JArray ht = new JArray();
		ht.set("pdf2swf", String.valueOf(pdf2swfEnabled("\""+Config.PDF2SWF_TOOL_PATH + "pdf2swf" + exe+"\"")));
		ht.set("renderingorder.primary", String.valueOf(Config.RenderType.flash));
		ht.set("renderingorder.secondary", String.valueOf(Config.RenderType.html));
		ht.set("allowcache", "true");
        File chineseXpdf=new File(Config.xpdfPath,"chinese-simplified");
		//pdf2swf 1.pdf -o 1.swf -f -T 9 -G 
		String temp = "pdf2swf"+exe+" \"{path.pdf}\" -o \"{path.swf}.swf"+FileUtil.DEFAULT_FILE_SUFFIX+"\" -P "+Config.PDF_PASSWORD+" -f -T 9 -t -s storeallcharacters -s languagedir="+chineseXpdf.getAbsolutePath()+" -G -s linknameurl";
		if(Config.isWin())
		{
			ht.set("cmd.conversion.singledoc", temp.replace("pdf2swf"+exe+"", "\""+Config.PDF2SWF_TOOL_PATH + "pdf2swf"+exe+"\""));
		}
		else
		{
			ht.set("cmd.conversion.singledoc", temp.replace("pdf2swf"+exe+"", Config.PDF2SWF_TOOL_PATH + "pdf2swf"+exe));
		}
		
		temp = "pdf2swf"+exe+" \"{path.pdf}\" -o \"{path.swf}_%.swf"+FileUtil.DEFAULT_FILE_SUFFIX+"\" -P "+Config.PDF_PASSWORD+" -f -T 9 -t -s storeallcharacters -s languagedir="+chineseXpdf.getAbsolutePath()+" -G -s linknameurl";
		if(Config.isWin())
		{
			ht.set("cmd.conversion.splitpages", temp.replace("pdf2swf"+exe+"",  "\""+Config.PDF2SWF_TOOL_PATH + "pdf2swf"+exe+"\""));
		}
		else
		{
			ht.set("cmd.conversion.splitpages", temp.replace("pdf2swf"+exe+"",Config.PDF2SWF_TOOL_PATH + "pdf2swf"+exe));
		}
		
		temp = "swfrender"+exe+" \"{path.swf}\" -p {page} -o \"{path.swf}.png"+FileUtil.DEFAULT_FILE_SUFFIX+"\" -X 1024 -s keepaspectratio";
		ht.set("cmd.conversion.renderpage", temp.replace("swfrender"+exe+"",  "\""+Config.PDF2SWF_TOOL_PATH + "swfrender"+exe+"\""));
		
		temp = "swfrender"+exe+" \"{path.swf}\" -o \"{path.swf}.png"+FileUtil.DEFAULT_FILE_SUFFIX+"\" -X 1024 -s keepaspectratio";
		ht.set("cmd.conversion.rendersplitpage", temp.replace("swfrender"+exe+"", "\""+Config.PDF2SWF_TOOL_PATH + "swfrender"+exe+"\""));
		
		temp = "pdf2json"+exe+" \"{path.pdf}\" -enc UTF-8 -compress \"{path.swf}.js"+FileUtil.DEFAULT_FILE_SUFFIX+"\"";	
		ht.set("cmd.conversion.jsonfile", temp.replace("pdf2json"+exe+"", "\""+Config.PDF2JSON_TOOL_PATH + "pdf2json"+exe+"\""));
		
		temp = "swfstrings"+exe+" {swffile}";
		ht.set("cmd.searching.extracttext", temp.replace("swfstrings"+exe+"", "\""+Config.PDF2SWF_TOOL_PATH + "swfstrings"+exe+"\""));
		
		temp = "pdf2json"+exe+" \"{path.pdf}\" -enc UTF-8 -compress -split 10 \"{path.swf}_%.js"+FileUtil.DEFAULT_FILE_SUFFIX+"\"";
		ht.set("cmd.conversion.splitjsonfile", temp.replace("pdf2json"+exe+"", "\""+Config.PDF2JSON_TOOL_PATH + "pdf2json"+exe+"\""));
		
		ht.set("cmd.query.swfwidth","swfdump"+exe+" \"{path.swf}\" -X");
		ht.set("cmd.query.swfheight","swfdump"+exe+" \"{path.swf}\" -Y");
		return ht;
	}
}