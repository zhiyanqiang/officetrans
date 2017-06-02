package com.seeyon.ctp.officetransservice.timer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.netpower.library.util.config.Config;

/**
 * 将officeTrans产生的转换后文件删除
 * 防止由于产生的文件太多，造成磁盘慢影响系统的使用
 * 功能描述：   
 * 创建人：zhiyanqiang  
 * 创建时间：2017年5月19日
 */
public class DeleteOfficeTransTask implements Runnable{
	private static final Log log = LogFactory.getLog(DeleteOfficeTransTask.class); 
	private SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");
	@Override
	public void run() {
		String retainDateStr=Config.File_Retains_Day;
		String officeTransRootDir=Config.OfficeTrans_Root_Dir;
		int retainDate=30;
		if((retainDateStr!=null)&&(retainDateStr.trim().length()>0))
		{
			try{
				retainDate=Integer.valueOf(retainDateStr);
			}
			catch(Exception e)
			{
				log.error(e.fillInStackTrace());
			}
		}
		File officeTransRootDirFile=new File(officeTransRootDir);
		Calendar calendar = Calendar.getInstance();  
	    calendar.setTime(new Date());
	    calendar.add(Calendar.DATE,-retainDate);//当前时间的前30天
		if(officeTransRootDirFile.exists())
		{
			File[] filesArray=officeTransRootDirFile.listFiles();
			for(File file:filesArray)
			{
				if(file.isDirectory())
				{
					String dateStr=file.getName();
					try{
						Date dirDate=df.parse(dateStr);
						Calendar fileCalendar=Calendar.getInstance();
						fileCalendar.setTime(dirDate);
						if(fileCalendar.before(calendar))
						{
							//文件产生的时间是在保留时间之前的话，就要删除了
							FileUtils.deleteDirectory(file);
							log.info("清理officeTrans转换目录-->"+file.getAbsolutePath());
						}
					}
					catch(Exception e)
					{
						log.error(e.fillInStackTrace());
					}
				}
			}
		}
	}

}
