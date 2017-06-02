package com.netpower.library.util.file;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 文件类型
 * 
 * @author 黄奎
 * 
 */
public class FileType {
	public static final List<String> FILETYPES = new ArrayList<String>(Arrays
			.asList(new String[] { "7z", "ai", "ain", "asp", "avi", "bin",
					"bmp", "cab", "cad", "cat", "cdr", "chm", "com", "css",
					"csv", "cur", "dat", "db", "dll", "doc", "docx", "dot",
					"dps", "dpt", "dwg", "dxf", "emf", "eml", "eps", "esp",
					"et", "ett", "exe", "fla", "flash", "gif", "hdd", "help",
					"html", "htm", "icl", "ico", "inf", "ini", "iso", "jpg", "js",
					"m3u", "max", "mdb", "mde", "mht", "mhtml", "mid", "mov", "mp3",
					"msi", "pdf", "php", "pl", "png", "pot", "ppt", "pptx",
					"psd", "pub", "flv", "rmvb", "ram", "rar", "reg", "rtf", "tif", "tiff",
					"torrent", "txt", "vbs", "vsd", "vss", "vst", "wav", "wmf",
					"wmv", "wps", "wpt", "xls", "xlsx", "xlt", "xlsm", "xml", "zip", "pak",
					"dotx", "docm", "dotm", "odt", "dbf", "prn", "dif", "xltm", "xlsb",
					"xltx", "slk", "xlam", "xla", "ods", "pps", "pptm", "potx", "potm",
					"ppsx", "ppsm", "odp"}));
	
	public static final List<String> DOCTYPES = new ArrayList<String>(Arrays
			.asList(new String[] {"doc", "docx", "pdf", "xls", "xlsx", "ppt",
					"pptx", "txt",  "html", "htm", "xml", "mht", "mhtml", "odt", 
					"ods", "odp", "odg", "odf", "wpd", "wps", "wpt",
					"xlt", "xlsm", "sxc", "sxi", "sxw", "sxd", "rtf", "wiki",
					"csv", "tsv", "svg", "stw", "dot", "dotx", "docm", "dotm",
					 "et", "ett", "dbf", "prn", "dif", "xltm", "xlsb", "xltx", "slk",
					"xlam", "xla", "dps", "dpt", "pot", "pps", "pptm", "potx", "potm",
					"ppsx", "ppsm"}));
	
	//WPS Word
	public static final List<String> WPSWORDTYPES = new ArrayList<String>(Arrays
			.asList(new String[] {"doc", "docx", "txt", "pdf", "dotx", "docm", "dotm",
					"dot", "wps", "wpt", "rtf"}));
	
	//MS Word
	public static final List<String> MSWORDTYPES = new ArrayList<String>(Arrays
			.asList(new String[] {"doc", "docx", "txt", "pdf", "dotx", "docm", "dotm",
					"dot", "rtf", "odt", "xps"}));
	
	//WPS Excel
	public static final List<String> WPSEXCELTYPES = new ArrayList<String>(Arrays
			.asList(new String[] {"xls", "xlsx", "et", "ett", "xlt", "xltm", "xlsm", "dbf",
					"csv", "prn", "dif"}));
	
	//MS Excel
	public static final List<String> MSEXCELTYPES = new ArrayList<String>(Arrays
			.asList(new String[] {"xls", "xlsx", "xlsb", "xltx", "xlt", "xltm", "xlsm", "slk",
					"xlam", "xla", "ods"}));
	
	//WPS PPT
	public static final List<String> WPSPPTTYPES = new ArrayList<String>(Arrays
			.asList(new String[] {"ppt", "pptx", "dps", "dpt", "pot", "pps", "pptm",
					"potx", "potm", "ppsx", "ppsm"}));
	
	//MS PPT
	public static final List<String> MSPPTTYPES = new ArrayList<String>(Arrays
			.asList(new String[] {"ppt", "pptx", "dps", "dpt", "pot", "pps", "pptm",
					"potx", "potm", "ppsx", "ppsm", "odp"}));
	
	public static final List<String> PICTYPES = new ArrayList<String>(Arrays
			.asList(new String[] {"bmp", "gif", "ico", "jpg", "png" ,"jpeg"}));
	
	public static final List<String> VIDEOTYPES = new ArrayList<String>(Arrays
			.asList(new String[] {"avi", "mp4", "mpg", "3gp", "flv", "rm", 
					"rmvb", "wmv", "mov" }));

	public static final List<String> RADIOTYPES = new ArrayList<String>(Arrays
			.asList(new String[] {"mp3", "wav"}));
	
	public static final List<String> FLASHTYPES = new ArrayList<String>(Arrays
			.asList(new String[] {"swf"}));
	
	public static final List<String> CODETYPES = new ArrayList<String>(Arrays
			.asList(new String[] { "js", "jsp", "java", "cs", "aspx", "xml",
					"mht", "mhtml", "htm", "html", "xhtml", "css", "cpp", "sql", "ini",
					"vbs", "log", "config", "dat" }));
	
	public static final List<String> ZIPTYPES = new ArrayList<String>(Arrays
			.asList(new String[] {"zip", "rar", "tar"}));
	
}