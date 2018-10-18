package com.qinglan.sdk.server.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
	
	public static final String yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";

	public static final String yyyyMMddHHmmss = "yyyyMMddHHmmss";
	
	public static final String yyyy_MM_dd = "yyyy-MM-dd";
	
	/**
	 * @return String yyyy-MM-dd HH:mm:ss
	 */
	public static final String format(Date date) {
		return format(date, yyyy_MM_dd_HH_mm_ss);
	}
	
	/**
	 * 得到时间 格式为 yyyy-MM-dd HH:mm:ss
	 * 
	 * @return
	 */
	public static final Date toDateTime(String date) {
		return parse(date,yyyy_MM_dd_HH_mm_ss);
	}
	
	public static final String format(Date date, String pattern) {
		SimpleDateFormat f = new SimpleDateFormat(pattern);
		return f.format(date);
	}
	
	public static final Date parse(String date, String pattern){
		SimpleDateFormat ft = new SimpleDateFormat(pattern);
		try {
			return ft.parse(date);
		} catch (ParseException e) {
			throw new IllegalArgumentException("date:" + date + ", pattern:" + pattern, e);
		}
	}
	
	/**
	 * 获取两个时间相差天数
	 * @param smallDate 小的时间
	 * @param bigDate 大的时间
	 * @return
	 */
	public static int getIntervalDays(Date smallDate, Date bigDate) {
       if (null == smallDate || null == bigDate) {
           return -1;
       }
       smallDate = parse(format(smallDate, yyyy_MM_dd), yyyy_MM_dd);
       bigDate = parse(format(bigDate, yyyy_MM_dd), yyyy_MM_dd);
       long intervalMilli = bigDate.getTime() - smallDate.getTime();
       return (int) (intervalMilli / (24 * 60 * 60 * 1000));
    }
	
	/**
	 * 获取两个时间戳相差天数
	 * @param smallDate 小的时间
	 * @param bigDate 大的时间
	 * @return
	 */
	public static int getIntervalDays(Long smallDate, Long bigDate) {
       if (null == smallDate || null == bigDate) {
           return -1;
       }
       return getIntervalDays(new Date(smallDate), new Date(bigDate));
    }
	
	/**
	 * 比较两个时间是否同一个月份
	 * @param oldDate
	 * @param newDate
	 * @return
	 */
	public static boolean sameMonth(Long oldDate, Long newDate) {
		if (null == oldDate || null == newDate) {
			return false;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(oldDate);
		String oldYearMonth = format(calendar.getTime(), "yyyyMM");
		
		calendar.setTimeInMillis(newDate);
		return format(calendar.getTime(), "yyyyMM").equals(oldYearMonth);
	}
	
	/**
	 * 格式化时间yyyyMMddHHmmss
	 * @param date
	 * @return
	 */
	public static final String toStringDate(Date date) {
		return format(date,yyyyMMddHHmmss);
	}
}
