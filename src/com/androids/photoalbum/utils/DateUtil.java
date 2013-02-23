package com.androids.photoalbum.utils;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	public static String getCreateAt(Date date){
		Calendar c = Calendar.getInstance();
		if(c.get(Calendar.YEAR)-(date.getYear()+1900)>0){
			int i = c.get(Calendar.YEAR)-date.getYear();
			return i+"��ǰ";
		}else if(c.get(Calendar.MONTH)-date.getMonth()>0){
			int i = c.get(Calendar.MONTH)-date.getMonth();
			return i+"��ǰ";
		}else if(c.get(Calendar.DAY_OF_MONTH)-date.getDate()>0){
			int i = c.get(Calendar.DAY_OF_MONTH)-date.getDate();
			return i+"��ǰ"; 
		}else if(c.get(Calendar.HOUR_OF_DAY)-date.getHours()>0){
			int i = c.get(Calendar.HOUR_OF_DAY)-date.getHours();
			return i+"Сʱǰ"; 
		}else if(c.get(Calendar.MINUTE)-date.getMinutes()>0){
			int i = c.get(Calendar.MINUTE)-date.getMinutes();
			return i+"����ǰ";
		}else {
			return "�ո�";
		}
	}
}
