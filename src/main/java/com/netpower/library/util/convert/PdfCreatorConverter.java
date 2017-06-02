package com.netpower.library.util.convert;

import java.io.File;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.DispatchEvents;
import com.jacob.com.Variant;
import com.netpower.library.util.Common;

public class PdfCreatorConverter extends Common implements PDFConverter{
	
	public static final int STATUS_IN_PROGRESS = 2;  
    public static final int STATUS_WITH_ERRORS = 1;  
    public static final int STATUS_READY = 0;  
    private ActiveXComponent pdfCreator;  
    private DispatchEvents dispatcher;  
    private volatile int status;  
    private Variant defaultPrinter;  

    private void init() {  
        pdfCreator = new ActiveXComponent("PDFCreator.clsPDFCreator");  
        dispatcher = new DispatchEvents(pdfCreator, this);  
        pdfCreator.setProperty("cVisible", new Variant(false));  
        pdfCreator.invoke("cStart", new Variant[]{new Variant("/NoProcessingAtStartup"), new Variant(true)});  
        setCOption("UseAutosave", 1);  
        setCOption("UseAutosaveDirectory", 1);  
        setCOption("AutosaveFormat", 0);  
        defaultPrinter = pdfCreator.getProperty("cDefaultPrinter");  
        status = STATUS_IN_PROGRESS;  
        pdfCreator.setProperty("cDefaultprinter", "PDFCreator");  
        pdfCreator.invoke("cClearCache");  
        pdfCreator.setProperty("cPrinterStop", false);  
    }  

    private void setCOption(String property, Object value) {  
        Dispatch.invoke(pdfCreator, "cOption", Dispatch.Put, new Object[]{property, value}, new int[2]);  
    }  

    private void close() {  
        if (pdfCreator != null) {  
            pdfCreator.setProperty("cDefaultprinter", defaultPrinter);  
            pdfCreator.invoke("cClearCache");  
            pdfCreator.setProperty("cPrinterStop", true);  
            pdfCreator.invoke("cClose");  
            pdfCreator.safeRelease();  
            pdfCreator = null;  
        }  
        if (dispatcher != null) {  
            dispatcher.safeRelease();  
            dispatcher = null;  
        }  
    }  

    public synchronized boolean doc2pdf(File docFile, File pdfFile) {
        try {  
            init();  
            setCOption("AutosaveDirectory", pdfFile.getParentFile().getAbsolutePath());  
            setCOption("AutosaveFilename", pdfFile.getName());  
            pdfCreator.invoke("cPrintfile", docFile.getAbsolutePath());  
            int seconds = 0;  
            while (isInProcess()) {  
                Thread.sleep(1000);  
                seconds++;  
                if (seconds > 20) { // timeout  
                    break;  
                }  
            }  
            if (seconds > 20 || isWithError()) return false;  
        } catch (InterruptedException ex) {  
        	System.out.println("****采用PdfCreator软件服务进行pdf转换错误，源doc文件：" + docFile.getAbsolutePath()+ "****"); 
        	ex.printStackTrace();
        	return false;  
        } catch (Exception ex) {  
        	System.out.println("****采用PdfCreator软件服务进行pdf转换错误，源doc文件：" + docFile.getAbsolutePath()+ "****"); 
        	ex.printStackTrace();  
            return false;  
        } catch (Error ex) {  
        	System.out.println("****采用PdfCreator软件服务进行pdf转换错误，源doc文件：" + docFile.getAbsolutePath()+ "****"); 
        	ex.printStackTrace(); 
            return false;  
        } finally {  
            close();  
        }  
        return true;  
    }  

    private boolean isInProcess() {  
        return status == STATUS_IN_PROGRESS;  
    }  

    private boolean isWithError() {  
        return status == STATUS_WITH_ERRORS;  
    }  

    // eReady event  
    public void eReady(Variant[] args) {  
        status = STATUS_READY;  
    }  

    // eError event  
    public void eError(Variant[] args) {  
        status = STATUS_WITH_ERRORS;  
    }  
}
