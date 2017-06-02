package com.netpower.library.util.date;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * @author wzk
 * @date 2009-9-8
 * @package com.netpower.util.date
 * @desc:日期工具类
 */
public class DateUtil {

	private static Calendar calendar = Calendar.getInstance();

	// 每天的毫秒数: 86400000
	private final static int dayMilliseconds = 86400000;
	
	/**
	 * 袁兵 2013.5.21 添加时间的"时","分","秒"
	 * @param date
	 * @return
	 */
	static public Date AddBackByDate(Date date){
		if(date == null){
			return null;
		}
		String nowDate=formatDateTime(date, "yyyy-MM-dd")+" ";
		nowDate+=formatDateTime(new Date(), "HH:mm:ss");
		return getDateTimeFromString(nowDate);
	}

	/**
	 * @param:
	 * @user wzk
	 * @date 2009-9-4
	 * @return Long
	 * @desc:得到日期的年
	 */
	static public Long getYearFromDate(Date date) {
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR);
		return new Long(year);
	}

	/**
	 * @param:
	 * @user wzk
	 * @date 2009-9-4
	 * @return Long
	 * @desc:得到日期的月
	 */
	static public Long getMonthFromDate(Date date) {
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH) + 1;
		return new Long(month);
	}

	/**
	 * @param:
	 * @user wzk
	 * @date 2009-9-4
	 * @return Long
	 * @desc:得到日期的日
	 */
	static public Long getDayFromDate(Date date) {
		calendar.setTime(date);
		int day = calendar.get(Calendar.DATE);
		return new Long(day);
	}

	/**
	 * @param:
	 * @user wzk
	 * @date 2009-9-4
	 * @return Date
	 * @desc:从年和月得到日期
	 */
	static public Date getDateFromYearMonth(Long year, Long month) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = month.toString();
		if (month.longValue() < 10)
			dateStr = "0" + dateStr;
		dateStr = year.toString() + "-" + dateStr + "-01";
		try {
			Date date = dateFormat.parse(dateStr);
			return date;
		} catch (ParseException e) {
			throw new IllegalArgumentException("Could not parse date: "
					+ e.getMessage());
		}
	}

	/**
	 * @param:
	 * @user wzk
	 * @date 2009-9-4
	 * @return Date
	 * @desc:从年月日得到日期
	 */
	static public Date getDateFromYearMonthDay(Long year, Long month, Long day) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = month.toString();
		String dayStr = day.toString();
		if (month.longValue() < 10)
			dateStr = "0" + dateStr;
		if (day.longValue() < 10)
			dayStr = "0" + dayStr;
		dateStr = year.toString() + "-" + dateStr + "-" + dayStr;
		try {
			Date date = dateFormat.parse(dateStr);
			return date;
		} catch (ParseException e) {
			throw new IllegalArgumentException("Could not parse date: "
					+ e.getMessage());
		}
	}

	/**
	 * @param:
	 * @user wzk
	 * @date 2009-9-4
	 * @return int
	 * @desc: 得到两个日期间的天数
	 */
	static public int getDaysOfDates(Date firstDate, Date lastDate) {
		return (int) ((lastDate.getTime() - firstDate.getTime()) / dayMilliseconds);
	}

	/**
	 * @param:
	 * @user wzk
	 * @date 2009-9-4
	 * @return Date
	 * @desc:日期加指定月数
	 */
	static public Date addMonths(Date date, int months) {
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, months);
		return calendar.getTime();
	}

	/**
	 * @param:
	 * @user wzk
	 * @date 2009-9-4
	 * @return Date
	 * @desc:日期加指定天数
	 */
	static public Date addDays(Date date, int days) {
		calendar.setTime(date);
		calendar.add(Calendar.DATE, days);
		return calendar.getTime();
	}
	
	static public Date addYear(Date date, int year) {
		calendar.setTime(date);
		calendar.add(Calendar.YEAR, year);
		return calendar.getTime();
	}

	/**
	 * @param:
	 * @user wzk
	 * @date 2009-9-4
	 * @return String
	 * @desc:得到时间间的花费时间
	 */
	static public String calculateTimesStringOfDatesBetween(Date startDate,
			Date endDate) {

		long costTime = (endDate.getTime() - startDate.getTime()) / 1000;
		String costTimeStr = null;

		if (costTime < 60)
			costTimeStr = costTime + "秒";
		else if (costTime < 3600)
			costTimeStr = (long) (costTime / 60) + "分" + costTime % 60 + "秒";
		else
			costTimeStr = (long) (costTime / 3600)
					+ "小时"
					+ (long) ((costTime - ((long) (costTime / 3600)) * 3600) / 60)
					+ "分" + costTime % 60 + "秒";
		return costTimeStr;
	}

	/**
	 * @param:
	 * @user ddn
	 * @date 2009-9-4
	 * @return Long
	 * @desc:计算两个时间之间的时间差
	 */
	static public Long calculateTimesOfDatesBetween(Date startDate, Date endDate) {
		long costTime = (endDate.getTime() - startDate.getTime()) / 1000;

		return costTime;
	}

	/**
	 * @param:
	 * @user wzk
	 * @date 2009-9-4
	 * @return String
	 * @desc:把日期格式转换成字符串的年月日格式
	 */
	static public String formatDatetoString(Date date) {
		if (date == null)
			return "";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(date);
	}

	/**
	 * @param:
	 * @user wzk
	 * @date 2009-9-4
	 * @return String
	 * @desc:把日期格式转换成字符串的年月日格式
	 */
	static public Date formatDate(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return dateFormat.parse(dateFormat.format(date));
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @param:
	 * @user wzk
	 * @date 2009-9-4
	 * @return String
	 * @desc:按日期格式定义格式化日期
	 */
	static public String formatDateTime(Date date, String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		if (date == null)
			return "";
		String dateString = dateFormat.format(date);
		if (dateString.equals("9999-12-31"))
			return "";
		return dateFormat.format(date);
	}

	/**
	 * @param:
	 * @user wzk
	 * @date 2009-9-4
	 * @return Date
	 * @desc:把字符串日期转换成年月日时分秒格式
	 */
	static public Date getDateTimeFromString(String dateStr) {
		dateStr = dateStr.replaceAll("T", " ");//如果发现传入的字符串有T的话替换掉
		return getDateFromString(dateStr, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * @param:
	 * @user wzk
	 * @date 2009-9-4
	 * @return Date
	 * @desc:把字符串日期转换成年月日格式
	 */
	static public Date getDateFromString(String dateStr) {
		return getDateFromString(dateStr, "yyyy-MM-dd");
	}

	/**
	 * @param:
	 * @user wzk
	 * @date 2009-9-4
	 * @return Date
	 * @desc:把字符串日期转换为指定格式日期
	 */
	static public Date getDateFromString(String dateStr, String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		try {
			if (dateStr == null || "".equals(dateStr.trim()) || "null".equals(dateStr))
				return null;
			return dateFormat.parse(dateStr);
		} catch (ParseException e) {
			System.out.println(dateStr);
			throw new IllegalArgumentException("不能转换: " + e.getMessage());
		}
	}

	/**
	 * @param:
	 * @user wzk
	 * @date 2009-9-4
	 * @return String
	 * @desc:生成用于文件名称的日期字符串
	 */
	static public String formatDateTimeForFileName(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(date);
	}

	static public String formateDate(Date date, String formate) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(formate);
		return dateFormat.format(date);
	}

	/**
	 * @param:
	 * @user gzd
	 * @date 2009-11-10
	 * @return String
	 * @desc:生成时间格式为yyyy-MM-dd HH:mm:ss
	 */
	public static String formatDateYYMMDDHHmmSS(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(date);
	}

	public static String formatDateYYMMDDHHmm(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return dateFormat.format(date);
	}

	/**
	 * 
	 * @desc: 日期+随机数
	 * @return
	 * @String
	 */
	public static String getDateRandom() {
		String[] s = DateUtil.formatDateYYMMDDHHmmSS(new Date()).split("-| |:");
		String str = "";
		for (String string : s) {
			str += string.trim();
		}

		Random random = new Random();
		int sigle = random.nextInt(10000);
		while (sigle <= 999) {
			sigle = random.nextInt(10000);
			if (sigle > 999)
				break;
		}

		return str + sigle;
	}

	/**
	 * 取得日期的星期
	 * 
	 * @param date
	 * @return
	 */
	public static Long getDayOfWeek(Date date) {
		calendar.setTime(date);
		return Long.parseLong(String
				.valueOf(calendar.get(Calendar.DAY_OF_WEEK)));
	}

	public static int getWeek(Date date) {

		Calendar c = Calendar.getInstance();

		if (date != null) {
			c.setTime(date);
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 0) {
			return 7;
		} else {
			return c.get(Calendar.DAY_OF_WEEK) - 1;
		}
	}

	public static String getZhCnWeek(Date date) {
		Calendar c = Calendar.getInstance();
		c.setFirstDayOfWeek(Calendar.MONDAY);
		int t = c.get(Calendar.DAY_OF_WEEK);
		switch (t) {
		case Calendar.MONDAY:
			return "星期一";
		case Calendar.TUESDAY:
			return "星期二";
		case Calendar.WEDNESDAY:
			return "星期三";
		case Calendar.THURSDAY:
			return "星期四";
		case Calendar.FRIDAY:
			return "星期五";
		case Calendar.SATURDAY:
			return "星期六";
		case Calendar.SUNDAY:
			return "星期天";

		default:
			return null;
		}
	}

	public static int getYear() {
		Calendar calendar = Calendar.getInstance();
		return calendar.get(DateFormat.YEAR_FIELD);
	}
	
	public static int getWeek(String date) {
		Date s = DateUtil.getDateFromString(date);
		Calendar c = Calendar.getInstance();
		c.setTime(s);
		return c.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * 获取前后日期 -1 表示昨天；0 标示今天
	 * 
	 * @param i
	 * @return
	 */
	public static Date getSomeDate(int i) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, i);
		return c.getTime();
	}
	
	/**
	 * 处理特殊日期
	 * 
	 * @param date
	 * @description 判断日期为'1900-01-01'时,则不显示
	 * @return
	 */
	public static Date procSpecialDate(Date date) {
		if (date == null) {
			return null;
		}
		if (formatDate(date).compareTo(
				getDateFromString("1900-01-01", "yyyy-MM-dd")) == 0) {
			return null;
		}
		return date;
	}
	
	/**
	 * 特殊日期处理
	 * 
	 * @param date
	 *            日期
	 * @param format
	 *            日期格式
	 * @return
	 */
	public static String procSpecialDateToString(Date date, String format) {
		if (date == null) {
			return "";
		}
		if(format == null || "".equals(format)) {
			format = "yyyy-MM-dd";
		}
		String strDate = DateUtil.formatDateTime(date, format);
		if (strDate.indexOf("1900-01-01") >= 0 || strDate.indexOf("9999-12-31") >= 0) {
			return "";
		}
		return strDate;
	}
}