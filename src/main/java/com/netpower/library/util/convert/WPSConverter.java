package com.netpower.library.util.convert;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import com.netpower.library.util.Common;
import com.netpower.library.util.file.FileType;
import com.netpower.library.util.file.FileUtil;

public class WPSConverter extends Common implements PDFConverter{
	private static final Log log = LogFactory.getLog(WPSConverter.class);
	public final static String WORDSERVER_STRING="KWPS.Application"; //WPS2015及以上版本 Word应用程序PID
    public final static String EXECLSERVER_STRING="KET.Application"; //WPS2015及以上版本 Excle应用程序PID 
    public final static String PPTSERVER_STRING="KWPP.Application"; //WPS2015及以上版本 PPT应用程序PID
    private static final int wdFormatPDF = 17; //Word转PDF参数值
    private static final int xlTypePDF = 0; //Excle转PDF参数值
    private static final int ppSaveAsPDF = 32; //PPT转PDF参数值
	
	public synchronized boolean doc2pdf(File docFile, File pdfFile) {
		String suffix =  FileUtil.getExtName(docFile);
		if(null != suffix)
		{
			suffix = suffix.toLowerCase();//文件扩展名转化为小写
			if(FileType.WPSWORDTYPES.contains(suffix) || FileType.CODETYPES.contains(suffix)){
				return word2pdf(docFile.getAbsolutePath(), pdfFile.getAbsolutePath());
			}else if(FileType.WPSEXCELTYPES.contains(suffix)){

				//MS Excle软件当转换pdf时总是要弹出安装打印机的窗口，暂时放弃这个转换方式，改用其他
				//return false;
				String toPdfName=pdfFile.getName();
				String[] toPdfNameArray=toPdfName.split("\\.");
				String htmlName=toPdfNameArray[0] + ".html";
				return excel2html(docFile.getAbsolutePath(),new File(pdfFile.getParentFile(),htmlName).getAbsolutePath());
			
			}else if(FileType.WPSPPTTYPES.contains(suffix)){
				return ppt2pdf(docFile.getAbsolutePath(), pdfFile.getAbsolutePath());
			}else{
				log.info("WPS 不支持的文件格式，切换其他的转换工具尝试转换!");
				return false;
			}
		}else
		{
			return false;
		}
	}
	
	private boolean word2pdf(String docFilePath, String pdfFilePath){
		boolean flag = false;
		ActiveXComponent word = null; //WPS Word运行程序对象  
		ActiveXComponent documents = null;//所有文档 
		ActiveXComponent doc = null;//单个文档  
        try {  
//        	ComThread.InitSTA();//初始化COM线程
        	try{
        		//WPS2015 Word版及以上版本
        		word = new ActiveXComponent(WORDSERVER_STRING);//初始化 WPS2015 Word版及以上版本
        	}catch(Exception ex)
        	{
        		log.error("****加载WPS2015 Word版及高版本COM组件失败！错误原因："+ex.fillInStackTrace()+"\n 只能放弃WPS转换Word方案，返回false,采用其他转换方式...");
        		return false;//直接返回了
        	}
        	word.setProperty("Visible", new Variant(false));//标识在WPS打开应用的时候是否是可见的
        	documents = word.invokeGetComponent("Documents");//所有文档  
        	
        	//打开参数
        	Variant[] openParams=new Variant[]{  
                    new Variant(docFilePath),//filePath  
                    new Variant(true),  
                    new Variant(true)//readOnley只读模式  
             };
        	//打开要转换的文件
        	doc = documents.invokeGetComponent("Open", new Variant(docFilePath),new Variant(true),new Variant(true));
            //调用 WPS的 转换 PDF服务应用
        	doc.invoke("SaveAs",new Variant(pdfFilePath),new Variant(wdFormatPDF)); 
        	flag = true;
        } catch (Exception ex) {  
        	log.error("****采用WPS Word服务进行pdf转换异常，源doc文件：" + docFilePath+ "****");  
        	log.error(ex.fillInStackTrace());
            return false;  
        } catch (Error ex) {  
        	log.error("****采用WPS Word服务进行pdf转换错误，源doc文件：" + docFilePath+ "****"); 
        	log.error(ex.fillInStackTrace());  
            return false;  
        } finally {  
        	if(doc != null)
        	{
        		try{
        			//关闭文档
        			Dispatch.call(doc, "Close");
        			doc.safeRelease();  
        		}catch(Exception exception){
        			//捕获一下文档关闭异常，防止最后不能关闭WPS
        		}
        	}
            //  应用的最后 是要退出 WPS 、否则 程序 会有可能被锁。
            if (word != null) {
            	try{
            		word.invoke("Quit");
            		word.safeRelease();
            	}catch(Exception exception){
        			//捕获一下Word关闭异常，防止最后不能释放Com线程
        		}
            } 
          ComThread.Release();//释放Com线程 [这句话会导致非常耗时所以先去掉，目前观察进程也会释放]
        }  
        return flag;
	}
	private boolean excel2html(String docFilePath,String htmlFilePath)
	{

		boolean flag=false;
		ActiveXComponent app=null;
		Dispatch excel=null;
		 try  {  
		     app = new ActiveXComponent(EXECLSERVER_STRING); // 启动word   
             app.setProperty("Visible", new Variant(false));   
             Dispatch excels = app.getProperty("Workbooks").toDispatch();   
             excel = Dispatch.invoke(   
                     excels,   
                    "Open",   
                     Dispatch.Method,   
                    new Object[] { docFilePath, new Variant(false),   
                            new Variant(true),new Variant(1),"一定是错的密码"}, new int[1]).toDispatch();
             //在转换之前删除已经存在的文件
             File htmlFile=new File(htmlFilePath);
             if(htmlFile.exists())
             {
            	 boolean result=htmlFile.delete();
            	 log.info("删除重复文件"+ htmlFilePath+"---->"+result);
             }
             Dispatch.invoke(excel, "SaveAs", Dispatch.Method, new Object[] {   
            		 htmlFilePath, new Variant(44) }, new int[1]);   
             
             flag=true;
         }   catch (Exception e)   {   
             log.error(e.fillInStackTrace());   
         }   finally   {
        	 if(excel!=null)
        	 {
        		 Dispatch.call(excel, "Close",new Variant(false));
        		 excel.safeRelease();
        	 }
        	 
        	 if(app!=null)
        	 {
        		 app.invoke("Quit", new Variant[] {});
        		 app.safeRelease();
        	 }
        	 ComThread.Release();//释放Com线程 [这句话会导致非常耗时所以先去掉，目前观察进程也会释放]
         }
        return flag;
	
	}
	private boolean excel2pdf(String docFilePath, String pdfFilePath){
		boolean flag = false;
		ActiveXComponent excel = null; //WPS Excel运行程序对象  
		Dispatch documents = null;//所有文档 
	    Dispatch doc = null;//单个文档  
        try {  
//        	ComThread.InitSTA();//初始化COM线程
        	try{
        		//WPS2015 Excel版及以上版本
        		excel = new ActiveXComponent(EXECLSERVER_STRING);//初始化 WPS2015 Excel版及以上版本
        	}catch(Exception ex)
        	{
        		System.out.println("****加载WPS2015 Excel版及高版本COM组件失败！错误原因："+ex.getMessage()+"\n 只能放弃WPS转换Excel方案，返回false,采用其他转换方式...");
        		return false;//直接返回了
        	}
        	excel.setProperty("Visible", new Variant(false));//标识在WPS打开应用的时候是否是可见的
        	documents = excel.getProperty("Workbooks").toDispatch();//所有文档  
        	
        	//打开要转换的文件
        	//这一句也可以的
//          doc = Dispatch.call(documents, "Open", docFilePath).toDispatch();
        	//这一句也可以的
//        	doc = Dispatch.call(documents, "Open", docFilePath,
//					true,//ReadOnly只读模式
//					true,//Untitled指定文件是否有标题
//					false//WithWindow指定文件是否可见
//					).toDispatch();
            doc = Dispatch.invoke(documents,"Open",
            		Dispatch.Method,
            		new Object[]{docFilePath,0,true},
            		new int[1]
            	    ).toDispatch();     
            
            //调用 WPS的 转换 PDF服务应用
            //Dispatch.invoke(doc,"SaveAs",Dispatch.Method,new Object[]{pdfFilePath,xlTypePDF},new int[1]);  
            Dispatch.call(doc,"ExportAsFixedFormat",new Object[]{xlTypePDF,pdfFilePath});  
            //由于WPS是采用SaveAs或ExportAsFixedFormat所以在磁盘上会另外创建一个.pdf后缀名的文件
            flag = FileUtil.renameFile(pdfFilePath+".pdf", pdfFilePath);//替换一下空文件
        } catch (Exception ex) {  
        	System.out.println("****采用WPS Excel服务进行pdf转换异常，源doc文件：" + docFilePath+ "****");  
        	ex.printStackTrace();
            return false;  
        } catch (Error ex) {  
        	System.out.println("****采用WPS Excel服务进行pdf转换错误，源doc文件：" + docFilePath+ "****"); 
        	ex.printStackTrace();  
            return false;  
        } finally {  
        	if(doc != null)
        	{
        		try{
        			//关闭文档
        			Dispatch.call(doc, "Close");
        			doc.safeRelease();  
        		}catch(Exception exception){
        			//捕获一下文档关闭异常，防止最后不能关闭WPS
        		}
        	}
            //  应用的最后 是要退出 WPS 、否则 程序 会有可能被锁。
            if (excel != null) {  
            	try{
            		excel.invoke("Quit");
            		excel.safeRelease();
            	}catch(Exception exception){
        			//捕获一下Excel关闭异常，防止最后不能释放Com线程
        		}
            } 
            ComThread.Release();//释放Com线程 [这句话会导致非常耗时所以先去掉，目前观察进程也会释放]
        }  
        return flag;
	}
	
	private boolean ppt2pdf(String docFilePath, String pdfFilePath){
		boolean flag = false;
		ActiveXComponent ppt = null; //WPS PPT运行程序对象  
		ActiveXComponent documents = null;//所有文档 
		ActiveXComponent doc = null;//单个文档  
        try {  
//        	ComThread.InitSTA();//初始化COM线程
        	try{
        		//WPS2015 PPT版及以上版本
        		ppt = new ActiveXComponent(PPTSERVER_STRING);//初始化 WPS2015 PPT版及以上版本
        	}catch(Exception ex)
        	{
        		log.info("****加载WPS2015 PPT版及高版本COM组件失败！错误原因："+ex.getMessage()+"\n 只能放弃WPS转换PPT方案，返回false,采用其他转换方式...");
        		return false;//直接返回了
        	}
        	documents = ppt.invokeGetComponent("Presentations");//所有文档  
        	//打开要转换的文件
        	doc = documents.invokeGetComponent("Open",
        			new Variant(docFilePath),
        			new Variant(true)//ReadOnly只读模式
        	);
            //调用 WPS的 转换 PDF服务应用
        	doc.invoke("SaveAs",new Variant(pdfFilePath),new Variant(ppSaveAsPDF));
        	flag = true;
        } catch (Exception ex) {  
        	log.error("****采用WPS PPT服务进行pdf转换异常，源doc文件：" + docFilePath+ "****");  
        	log.error(ex.fillInStackTrace());
            return false;  
        } catch (Error ex) {  
        	log.error("****采用WPS PPT服务进行pdf转换错误，源doc文件：" + docFilePath+ "****"); 
        	log.error(ex.fillInStackTrace());  
            return false;  
        } finally {  
        	if(doc != null)
        	{
        		try{
        			//关闭文档
        			Dispatch.call(doc, "Close");
        			doc.safeRelease();  
        		}catch(Exception exception){
        			//捕获一下文档关闭异常，防止最后不能关闭WPS
        		}
        	}
            //  应用的最后 是要退出 WPS 、否则 程序 会有可能被锁。
            if (ppt != null) {
            	try{
	            	ppt.invoke("Quit");
	            	ppt.safeRelease();
            	}catch(Exception exception){
        			//捕获一下PPT关闭异常，防止最后不能释放Com线程
        		}
            } 
          ComThread.Release();//释放Com线程 [这句话会导致非常耗时所以先去掉，目前观察进程也会释放]
        }  
        return flag;
	}
}
