package com.netpower.library.util;

import com.netpower.library.util.file.FileUtil;

/**
 * SWF文件摘要提取
 * @author 黄奎
 *
 */
public class swfextract extends Common {

	/**
	 * TODO 此功能后边有时间在进一步实现
	 * 查找SWF文件中指定内容
	 * @param doc
	 * @param page
	 * @param searchterm 要查找的内容
	 * @param numPages
	 * @return
	 */
	public String findText(String doc, int page, String searchterm, int numPages) {
		return null;
//		if(searchterm.length()==0){return "[{\"page\":-1, \"position\":-1}]";}
//
//		try {
//			int pagecount = numPages;
//			if(numPages == -1){
//				pagecount = FileUtil.searchFiles(getConfig("path.swf", ""),doc + "*").size();
//			}
//
//			JArray commands = getConverCommands();
//			String command = commands.get("cmd.searching.extracttext");
//			
//			command = command.replace("{swffile}",  doc + "_" + page + ".swf");
//
//			String output = execs(command);
//			int pos = -1;
//			if(output != null)
//				pos = output.toLowerCase().indexOf(searchterm.toLowerCase());
//            if(pos > 0){
//                return "[{\"page\":" + page + ", \"position\":" + pos + "}]";
//            }else{
//                if(page < pagecount){
//                    page++;
//                    return findText(doc, page, searchterm, pagecount);
//                }else{
//                    return "[{\"page\":-1, \"position\":-1}]";
//                }
//            }
//		} catch (Exception ex) {
//			return ex.toString();
//		}
	}
}