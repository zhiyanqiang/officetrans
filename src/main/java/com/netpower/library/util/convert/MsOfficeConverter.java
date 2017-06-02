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

public class MsOfficeConverter extends Common implements PDFConverter{
	private static final Log log = LogFactory.getLog(MsOfficeConverter.class);
	public final static String WORDSERVER_STRING="Word.Application"; //微软Office2010及以上版本 Word应用程序PID
    public final static String EXECLSERVER_STRING="Excel.Application"; //微软Office2010及以上版本 Excle应用程序PID 
    public final static String PPTSERVER_STRING="PowerPoint.Application"; //微软Office2010及以上版本 PPT应用程序PID
	 private static final int wdFormatPDF = 17;//Word转PDF参数值
	 private static final int xlTypePDF = 0;//Excle转PDF参数值
	 private static final int ppSaveAsPDF = 32;//PPT转PDF参数值
	 private static final int msoTrue = -1;//微软True值
	 private static final int msofalse = 0;//微软false值
	
	public synchronized boolean doc2pdf(File docFile, File pdfFile) {
		String suffix =  FileUtil.getExtName(docFile);
		if(null != suffix)
		{
			suffix = suffix.toLowerCase();//文件扩展名转化为小写
			if(FileType.MSWORDTYPES.contains(suffix) || FileType.CODETYPES.contains(suffix)){
				return word2pdf(docFile.getAbsolutePath(), pdfFile.getAbsolutePath());
			}else if(FileType.MSEXCELTYPES.contains(suffix)){
				//MS Excle软件当转换pdf时总是要弹出安装打印机的窗口，暂时放弃这个转换方式，改用其他
				//return false;
				String toPdfName=pdfFile.getName();
				String[] toPdfNameArray=toPdfName.split("\\.");
				String htmlName=toPdfNameArray[0] + ".html";
				return excel2html(docFile.getAbsolutePath(),new File(pdfFile.getParentFile(),htmlName).getAbsolutePath());
			}else if(FileType.MSPPTTYPES.contains(suffix)){
				return ppt2pdf(docFile.getAbsolutePath(), pdfFile.getAbsolutePath());
			}else{
				log.info("Microsoft Office 不支持的文件格式，切换其他的转换工具尝试转换!");
				return false;
			}
		}else
		{
			return false;
		}
	}
	
	private boolean word2pdf(String docFilePath, String pdfFilePath){
		ActiveXComponent word = null; //word运行程序对象  
	    Dispatch doc = null;//单个文档  
	    Dispatch documents = null;//所有文档  
        try {  
//        	ComThread.InitSTA();//初始化COM线程
        	try{
        		//经过实测，Microsoft Word 2013版转换PDF功能。
        		word = new ActiveXComponent(WORDSERVER_STRING);//Microsoft Word 2013版
        	}catch(Exception ex)
        	{
        		log.error("****加载Microsoft Word COM组件依然失败！错误原因："+ex.fillInStackTrace()+"\n 只能放弃Word转换方案，返回false,采用其他转换方式...");
        		return false;//直接返回了
        	}
        	word.setProperty("Visible", new Variant(false));//标识在WPS打开应用的时候是否是可见的 
//        	word.setProperty("ScreenUpdating", new Variant(false));
        	documents = word.getProperty("Documents").toDispatch();
        	//打开参数
        	Object[] openParams =new Object[] { 
            		docFilePath,     
                    new Variant(false),     
                    new Variant(true),//是否只读    
                    new Variant(false),     
                    new Variant("pwd")//是否有密码，有就传参 
             };
            //打开要转换的文件
            doc = Dispatch.invoke(documents, "Open", Dispatch.Method, openParams, new int[1]).toDispatch();    
            // Dispatch.put(doc, "Compatibility", false);  //Word文档兼容性检查,为特定值false不正确
            //Dispatch.put(doc, "RemovePersonalInformation", false);//是否移除个人信息
            
            //调用Document对象的SaveAs方法，将文档保存为pdf格式(两种方式使用其中一种)
			//Dispatch.call(doc, "SaveAs", pdfFilePath, wdFormatPDF);//word保存为pdf格式宏，值为17
			log.info("开始保存为pdf-->docFilePath="+ docFilePath);
            Dispatch.call(doc, "ExportAsFixedFormat", pdfFilePath, wdFormatPDF);//word保存为pdf格式宏，值为17
        } catch (Exception ex) {  
        	log.error("****采用MS Word服务进行pdf转换异常，源doc文件：" + docFilePath+ "****");  
        	log.error(ex.fillInStackTrace());
            return false;  
        } catch (Error ex) {  
        	log.error("****采用MS Word服务进行pdf转换错误，源doc文件：" + docFilePath+ "****"); 
        	log.error(ex.fillInStackTrace());  
            return false;  
        } finally {  
        	if(doc != null)
        	{
        		try{
        			//关闭文档
        			Dispatch.call(doc, "Close",new Variant(false));
        			doc.safeRelease();  
        		}catch(Exception exception){
        			//捕获一下文档关闭异常，防止最后不能关闭WPS
        		}
        	}
        	//  应用的最后 是要退出 WPS 、否则 程序 会有可能被锁。
            if (word != null) {
            	try{
            		word.invoke("Quit", 0);
            		word.safeRelease();
            	}catch(Exception exception){
        			//捕获一下Word关闭异常，防止最后不能释放Com线程
        		}
            } 
          ComThread.Release();//释放Com线程 [这句话会导致非常耗时所以先去掉，目前观察进程也会释放] 
        }  
        return true; 
	}
	private boolean excel2html(String docFilePath,String htmlFilePath)
	{
		boolean flag=false;
		ActiveXComponent app=null;
		Dispatch excel=null;
		 try  {  
		     app = new ActiveXComponent("Excel.Application"); // 启动word   
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
		ActiveXComponent excel = null; //excel运行程序对象  
	    Dispatch doc = null;//单个文档  
	    Dispatch documents = null;//所有文档  
        try {  
//        	ComThread.InitSTA();//初始化COM线程
        	try{
        		//经过实测，Microsoft Excel 2013版转换PDF功能。
        		excel = new ActiveXComponent(EXECLSERVER_STRING);//Microsoft Excel 2013版
        	}catch(Exception ex)
        	{
        		System.out.println("****加载Microsoft Excel COM组件依然失败！错误原因："+ex.getMessage()+"\n 只能放弃Excel转换方案，返回false,采用其他转换方式...");
        		return false;//直接返回了
        	}
        	excel.setProperty("Visible", new Variant(false));//标识在Excel打开应用的时候是否是可见的 
        	documents = excel.getProperty("Workbooks").toDispatch();
        	//打开要转换的文件
            doc = Dispatch.call(documents,"Open",docFilePath, false,true).toDispatch();
            
            //调用Document对象的方法，将文档保存为pdf格式
            Dispatch.call(doc, "ExportAsFixedFormat", xlTypePDF, pdfFilePath);//excel保存为pdf格式宏，值为0
        } catch (Exception ex) {  
        	System.out.println("****采用Excel服务进行pdf转换异常，源doc文件：" + docFilePath+ "****");  
        	ex.printStackTrace();
            return false;  
        } catch (Error ex) {  
        	System.out.println("****采用Excel服务进行pdf转换错误，源doc文件：" + docFilePath+ "****"); 
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
        			//捕获一下Word关闭异常，防止最后不能释放Com线程
        		}
            } 
//          ComThread.Release();//释放Com线程 [这句话会导致非常耗时所以先去掉，目前观察进程也会释放]
        }  
        return true; 
	}
	
	private boolean ppt2pdf(String docFilePath, String pdfFilePath){
		boolean flag = false;
		ActiveXComponent ppt = null; //PowerPoint运行程序对象  
	    Dispatch doc = null;//单个文档  
	    Dispatch documents = null;//所有文档  
        try {  
//        	ComThread.InitSTA();//初始化COM线程
        	try{
        		//经过实测，Microsoft PowerPoint 2013版转换PDF功能。
        		ppt = new ActiveXComponent(PPTSERVER_STRING);//Microsoft PowerPoint 2013版
        	}catch(Exception ex)
        	{
        		log.error("****加载Microsoft PowerPoint COM组件依然失败！错误原因："+ex.getMessage()+"\n 只能放弃PowerPoint转换方案，返回false,采用其他转换方式...");
        		return false;//直接返回了
        	}
        	documents = ppt.getProperty("Presentations").toDispatch();
            //打开要转换的文件
        	// 因POWER.EXE的发布规则为同步，所以设置为同步发布    
            doc = Dispatch.call(documents, "Open", docFilePath,
					true,//ReadOnly只读模式
					true,//Untitled指定文件是否有标题
					false//WithWindow指定文件是否可见
					).toDispatch();
            
            //调用Document对象的方法，将文档保存为pdf格式
            Dispatch.call(doc, "SaveAs", pdfFilePath, ppSaveAsPDF);//PowerPoint保存为pdf格式宏，值为32
            //由于PowerPoint是采用SaveAs所以在磁盘上会另外创建一个.pdf后缀名的文件
            flag = FileUtil.renameFile(pdfFilePath+".pdf", pdfFilePath);//替换一下空文件
        } catch (Exception ex) {  
        	log.error("****采用PowerPoint服务进行pdf转换异常，源doc文件：" + docFilePath+ "****");  
        	log.error(ex.fillInStackTrace());
            return false;  
        } catch (Error ex) {  
        	log.error("****采用PowerPoint服务进行pdf转换错误，源doc文件：" + docFilePath+ "****"); 
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
        			//捕获一下Word关闭异常，防止最后不能释放Com线程
        		}
            } 
          ComThread.Release();//释放Com线程 [这句话会导致非常耗时所以先去掉，目前观察进程也会释放]
        }  
        return flag; 
	}
	
	/** 
     * 打开文件 
     * @param docPath 要打开的文件，全路径 
     * @return  打开的文件 
     */  
    public Dispatch open(Dispatch documents, String docPath){ 
        return Dispatch.call(documents, "Open", docPath, false, true).toDispatch();  
    }
}
