package com.qinglan.sdk.server.utils.channel.lewan.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimeHelper {
	/**
	 * 得到指定格式的当前时间字符串
	 * @param format
	 * @return
	 */
	public static String getTime(String format){
		String time="";
		try{
			SimpleDateFormat sf=new SimpleDateFormat(format);
			Date date=new Date();
			time=sf.format(date);
		}catch(IllegalArgumentException e){
			time=format;
		}catch(Exception e){
			time="1900-01-01";
		}
		return time;
	}
	/**
	 * 比较时间
	 * @param startIime
	 * @param endTime
	 * @param format
	 * @return
	 */
	public static int compTime(String startIime,String endTime,String format){
		SimpleDateFormat  df=new SimpleDateFormat(format); 
		Date sdate=null,edate=null;
		boolean aflag=false,bflag=false;
		int code=0;
		try{
			sdate=df.parse(startIime);
			edate=df.parse(endTime);
			aflag=sdate.after(edate);
			bflag=sdate.before(edate);
			if(!aflag&&!bflag){
				code=0;
			}else if(aflag){
				code=-1;
			}else if(bflag){
				code=1;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return code;
	}
	/**
	 * 将字符串转换为指定格式的时间
	 * @param format
	 * @return
	 */
	public static Date toDate(String time,String format){
		SimpleDateFormat  df=new SimpleDateFormat(format); 
		Date date=null;
		try {
			date = df.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	/**
	 * 得到昨天的时间
	 * @param format
	 * @return
	 */
	public static String yesterday(String format){
		Date yesterday_date=dateAdd(new Date(), Calendar.DAY_OF_WEEK, -1);
		return getTime(format,yesterday_date);
	}
	/**
	 * 给传入的时间添加指定的时间
	 * @param time
	 * @param format
	 * @param addWort
	 * @param addValue
	 * @return
	 */
	public static String timeAdd(String time,String format,int addWort,int addValue){
		return getTime("yyyy-MM-dd",dateAdd(toDate(time, format),addWort, addValue));
	}
	/**
	 * 得到指定格式的当前时间字符串
	 * @param format
	 * @return
	 */
	public static String getTime(String format,Date date){
		String time="";
		try{
			SimpleDateFormat sf=new SimpleDateFormat(format);
			time=sf.format(date);
		}catch(IllegalArgumentException e){
			time=format;
		}catch(Exception e){
			time="1900-01-01";
		}
		return time;
	}

	/**
	 * 给传入的时间添加指定的时间
	 * @param time
	 * @param format
	 * @param addWort
	 * @param addValue
	 * @return
	 */
	public static Date dateAdd(Date date,int addWort,int addValue){
		Calendar c=Calendar.getInstance();
		c.setTime(date);
		c.add(addWort,addValue);
		return c.getTime();
	}
	/**
	 * 得到两个时间的时间差天数
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static int getDaysBetween(String startTime,String endTime){
		int days = 0;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar c_s = Calendar.getInstance();
		Calendar c_e = Calendar.getInstance();
		try{
			c_s.setTime(df.parse(startTime));
			c_e.setTime(df.parse(endTime));
			while(c_s.before(c_e)){
				days++;
				c_s.add(Calendar.DAY_OF_YEAR, 1);
			}
		}catch(ParseException pe){
			System.out.println("日期格式必须为：yyyy-MM-dd；如：2010-04-04");
		}
		return days; 
	}
	/**
	 * 返回指定时间加一天后的字符串
	 * @param time
	 * @return
	 */
	public static String addDate(String time){
		DateFormat  format=new SimpleDateFormat("yyyy-MM-dd"); 
		Date date;
		try {
			date = format.parse(time);
			Calendar  calendar=Calendar.getInstance(); 
			calendar.setTime(date); 
			calendar.add(Calendar.DAY_OF_MONTH,1); 
			time=format.format(calendar.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		} 
		return time;
	}

	/**
	 * 得到一个星期的第一天
	 * @param date
	 * @return
	 */
	public static Date firstDayOfWeek(Date date){
		Calendar c = new GregorianCalendar();   
		c.setFirstDayOfWeek(Calendar.MONDAY);   
		c.setTime(date);   
		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek()); // Monday   
		return c.getTime();   

	}
	/**
	 * 得到一个星期的最后一天
	 * @param date
	 * @return
	 */
	public static Date lastDayOfWeek(Date date){
		Calendar c = new GregorianCalendar();   
		c.setFirstDayOfWeek(Calendar.MONDAY);   
		c.setTime(date);   
		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek()+6); // Sunday   
		return c.getTime();   
	}
	/**
	 * 得到一个星期的第一天
	 * @param date
	 * @return
	 */
	public static String firstDayOfWeek(Date date,String format){
		Calendar c = new GregorianCalendar();   
		c.setFirstDayOfWeek(Calendar.MONDAY);   
		c.setTime(date);   
		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek()); // Monday   
		return getTime(format,c.getTime()); 

	}
	/**
	 * 得到一个星期的最后一天
	 * @param date
	 * @return
	 */
	public static String lastDayOfWeek(Date date,String format){
		Calendar c = new GregorianCalendar();   
		c.setFirstDayOfWeek(Calendar.MONDAY);   
		c.setTime(date);   
		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek()+6); // Sunday   
		return getTime(format,c.getTime()); 

	}
	/**
	 * 得到当前星期的第一天
	 * @param date
	 * @return
	 */
	public static String firstDayOfCurrWeek(String format){
		Calendar c = new GregorianCalendar();   
		c.setFirstDayOfWeek(Calendar.MONDAY);   
		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek()); // Monday   
		return getTime(format,c.getTime());

	}
	/**
	 * 得到当前星期的最后一天
	 * @param date
	 * @return
	 */
	public static String lastDayOfCurrWeek(String format){
		Calendar c = new GregorianCalendar();   
		c.setFirstDayOfWeek(Calendar.MONDAY);   
		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek()+6); // Sunday   
		return getTime(format,c.getTime());
	}
	/**
	 * 得到一个月的第一天
	 * @param date
	 * @return
	 */
	public static Date firstDayOfMonth(Date date) {   
		Calendar c = new GregorianCalendar();   
		c.setTime(date);   
		c.set(Calendar.DAY_OF_MONTH, 1);
		return c.getTime();   
	}   
	/**  
	 * 得到一个月的最后一天
	 * @param date  
	 * @return  
	 */  
	public static Date lastDayOfMonth(Date date) {   
		Calendar c = new GregorianCalendar();   
		c.setTime(date);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.add(Calendar.MONTH, 1);
		c.add(Calendar.DATE, -1);
		return c.getTime();   
	}
	/**
	 * 得到一个月的第一天
	 * @param date
	 * @return
	 */
	public static String firstDayOfMonth(Date date,String format) {   
		Calendar c = new GregorianCalendar();   
		c.setTime(date);   
		c.set(Calendar.DAY_OF_MONTH, 1);
		return getTime(format,c.getTime());
	}   
	/**  
	 * 得到一个月的最后一天
	 * @param date  
	 * @return  
	 */  
	public static String lastDayOfMonth(Date date,String format) {   
		Calendar c = new GregorianCalendar();   
		c.setTime(date);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.add(Calendar.MONTH, 1);
		c.add(Calendar.DATE, -1);
		return getTime(format,c.getTime());
	}
	/**
	 * 得到当前月的第一天
	 * @param date
	 * @return
	 */
	public static String firstDayOfCurrMonth(String format) {   
		Calendar c = new GregorianCalendar();   
		c.set(Calendar.DAY_OF_MONTH, 1);
		return getTime(format,c.getTime());
	}  
	/**
	 * 得到当前月的最后一天
	 * @param date
	 * @return
	 */
	public static String lastDayOfCurrMonth(String format) {   
		Calendar c = new GregorianCalendar();   
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.add(Calendar.MONTH, 1);
		c.add(Calendar.DATE, -1);
		return getTime(format,c.getTime());
	}  
	/**  
	 * 得到一年的第一天
	 * @param date  
	 * @return  
	 */  
	public static Date firstDayOfYear(Date date) {   
		Calendar c = new GregorianCalendar();   
		c.setTime(date);   
		c.set(Calendar.MONTH, 0);
		c.set(Calendar.DAY_OF_MONTH, 1);
		return c.getTime();   
	}   

	/**  
	 * 得到一年的最后一天
	 * @param date  
	 * @return  
	 */ 
	public static Date lastDayOfYear(Date date) {   
		Calendar c = new GregorianCalendar();   
		c.setTime(date);
		c.set(Calendar.MONTH, 11);
		c.set(Calendar.DAY_OF_MONTH, 31);
		return c.getTime();   
	}
	
	public static int getMinuteBetween(String startTime,String endTime){
		int days = 0;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar c_s = Calendar.getInstance();
		Calendar c_e = Calendar.getInstance();
		try{
			c_s.setTime(df.parse(startTime));
			c_e.setTime(df.parse(endTime));
			while(c_s.before(c_e)){
				days++;
				c_s.add(Calendar.MINUTE, 1);
			}
		}catch(ParseException pe){
			System.out.println("日期格式必须为：yyyy-MM-dd；如：2010-04-04");
		}
		return days; 
	}
	
	public static int getHourBetween(String startTime,String endTime){
		int days = 0;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar c_s = Calendar.getInstance();
		Calendar c_e = Calendar.getInstance();
		try{
			c_s.setTime(df.parse(startTime));
			c_e.setTime(df.parse(endTime));
			while(c_s.before(c_e)){
				days++;
				c_s.add(Calendar.HOUR, 1);
			}
		}catch(ParseException pe){
			System.out.println("日期格式必须为：yyyy-MM-dd；如：2010-04-04");
		}
		return days; 
	}

	public static void main(String[] args) {
		//		Date date1=firstDayOfWeek(new Date());
		//		System.out.println(date1.toLocaleString());
		//		Date date2=lastDayOfWeek(new Date());
		//		System.out.println(date2.toLocaleString());
		//		System.out.println(new SimpleDateFormat("yyyy-MM-dd").format(TimeHelper.firstDayOfMonth(new Date())));
//		System.out.println(timeAdd(TimeHelper.getTime("yyyy-MM-dd"),"yyyy-MM-dd",Calendar.DAY_OF_WEEK,-1));
		//System.out.println(TimeHelper.getDaysBetween("2012-02-28", "2012-2-22"));
		System.out.println(TimeHelper.compTime("2012-02-28", "2012-2-28","yyyy-MM-dd"));
		
		System.out.println(TimeHelper.getMinuteBetween("2012-02-28 13:01:01", "2012-02-28 14:00:00"));
	}
}
