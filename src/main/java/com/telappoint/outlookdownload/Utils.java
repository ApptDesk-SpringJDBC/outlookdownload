package com.telappoint.outlookdownload;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.apache.log4j.Logger;

public class Utils {
	private static Logger logger = Logger.getLogger(SyncOutlook.class);

	public static String formatPhoneNumber(String phone) {
		StringBuilder buf = new StringBuilder("");
		if (phone != null && phone.length() == 10) {
			buf.append(phone.substring(0, 3));
			buf.append("-");
			buf.append(phone.substring(3, 6));
			buf.append("-");
			buf.append(phone.substring(6, 10));
		} else {
			buf.append("");
		}
		return buf.toString();
	}

	public static GregorianCalendar formatSqlStringToGC(String datestr) {
		int year = Integer.parseInt(datestr.substring(0, 4));
		int month = Integer.parseInt(datestr.substring(5, 7));
		int date = Integer.parseInt(datestr.substring(8, 10));
		int hour = Integer.parseInt(datestr.substring(11, 13));
		int min = Integer.parseInt(datestr.substring(14, 16));
		int sec = Integer.parseInt(datestr.substring(17, 19));
		GregorianCalendar cal = new GregorianCalendar();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DATE, date);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, min);
		cal.set(Calendar.SECOND, sec);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}

	public static String formatGCDateToMMDDYYYY_HHMMAMPM(GregorianCalendar dateGC) {
		String dateString = "";
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
		formatter.setCalendar(dateGC);
		try {
			dateString = formatter.format(dateGC.getTime());
		} catch (Exception e) {
			logger.error("Error: " + e, e);
		}
		return dateString;
	}
}
