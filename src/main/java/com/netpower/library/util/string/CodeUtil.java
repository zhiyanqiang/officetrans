package com.netpower.library.util.string;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import com.netpower.library.util.file.FileUtil;

/**
 * 用于字符编码、解码的工具类
 * 
 * @author song
 * @date 2009年9月18日
 * @version 1.0
 * 
 */
public class CodeUtil {
	
	public static String encode(String param) {
		return encode(param, "UTF-8");
	}
	
	/**
	 * 根据指定加码方式编码
	 * @param param
	 * @param coding
	 * @return
	 */
	public static String encode(String param, String code) {
		if (param == null)
			return null;
		if (param.trim() == "")
			return "";
		String temp = null;
		try {
			temp = URLEncoder.encode(new String(param.getBytes("iso8859-1"), code), code);
		} catch (UnsupportedEncodingException e) {
			temp = param;
			e.printStackTrace();
		}
		return temp.trim();
	}

	public static String decode(String param) {
		return decode(param, "UTF-8");
	}
	
	/**
	 * 根据指定解码方式解码
	 * @param param
	 * @param coding
	 * @return
	 */
	public static String decode(String param, String code) {
		if (param == null)
			return null;
		if (param.trim() == "")
			return "";
		String temp = null;
		try {
			String codeType = FileUtil.getByteCoding(param.getBytes("iso8859-1"));
			temp = new String(param.getBytes("iso8859-1"), codeType);
			temp = URLDecoder.decode(temp, code);
		} catch (UnsupportedEncodingException e) {
			temp = param;
			e.printStackTrace();
		}
		return temp.trim();
	}
	
	public static String decodeAuto(String param) {
		return decodeAuto(param, "UTF-8");
	}
	
	/**
	 * 根据指定解码方式解码
	 * @param param
	 * @param coding
	 * @return
	 */
	public static String decodeAuto(String param, String code) {
		if (param == null)
			return null;
		if (param.trim() == "")
			return param;
		try {
			String codeType = getByteCoding(param.getBytes("iso8859-1"));
			if(!"GBK".equals(codeType))
			{
				param = new String(param.getBytes("iso8859-1"), codeType);
			}
			if(!isConSpeCharacters(param))
			{
				param = new String(param.getBytes("iso8859-1"), codeType);
			}
			param = URLDecoder.decode(param, code);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return param;
	}
	
	/**
	  * 功能：判断一个字符串是否包含特殊字符
	  * @param string 要判断的字符串
	  * @return true  提供的参数string不包含特殊字符
	  * @return false 提供的参数string包含特殊字符
	  */
	 public static boolean isConSpeCharacters(String string) {
		  //[\\u0391-\\uFFE5]匹配双字节字符（汉字+符号） 
		  //[\\u4e00-\\u9fa5]注意只匹配汉字，不匹配双字节字符 
		  if(string.replaceAll("[\u0391-\uFFE5]*[a-z]*[A-Z]*\\d*-*_*\\s*", "").replace(",", "").replace("~", "").replace(".", "").replace("/", "")
				  		.replace("\\", "").replace("(", "").replace(")", "").replace(":", "").length() == 0){
		   //如果不包含特殊字符
		   return  true;
		  }
		  return false;
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
	
	public static String escape(String src) {  
        int i;  
        char j;  
        StringBuffer tmp = new StringBuffer();  
        tmp.ensureCapacity(src.length() * 6);  
        for (i = 0; i < src.length(); i++) {  
            j = src.charAt(i);  
            if (Character.isDigit(j) || Character.isLowerCase(j)  
                    || Character.isUpperCase(j))  
                tmp.append(j);  
            else if (j < 256) {  
                tmp.append("%");  
                if (j < 16)  
                    tmp.append("0");  
                tmp.append(Integer.toString(j, 16));  
            } else {  
                tmp.append("%u");  
                tmp.append(Integer.toString(j, 16));  
            }  
        }  
        return tmp.toString();  
    }  
 
    public static String unescape(String src) {  
        StringBuffer tmp = new StringBuffer();  
        tmp.ensureCapacity(src.length());  
        int lastPos = 0, pos = 0;  
        char ch;  
        while (lastPos < src.length()) {  
            pos = src.indexOf("%", lastPos);  
            if (pos == lastPos) {  
                if (src.charAt(pos + 1) == 'u') {  
                    ch = (char) Integer.parseInt(src  
                            .substring(pos + 2, pos + 6), 16);  
                    tmp.append(ch);  
                    lastPos = pos + 6;  
                } else {  
                    ch = (char) Integer.parseInt(src  
                            .substring(pos + 1, pos + 3), 16);  
                    tmp.append(ch);  
                    lastPos = pos + 3;  
                }  
            } else {  
                if (pos == -1) {  
                    tmp.append(src.substring(lastPos));  
                    lastPos = src.length();  
                } else {  
                    tmp.append(src.substring(lastPos, pos));  
                    lastPos = pos;  
                }  
            }  
        }  
        return tmp.toString();  
    }
}