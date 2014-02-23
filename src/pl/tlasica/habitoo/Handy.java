package pl.tlasica.habitoo;

import android.graphics.Color;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Handy {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm");

	public static int calToNum(Calendar cal) {
		int acc = 0;
		acc += cal.get(Calendar.YEAR) * 10000;
		acc += (cal.get(Calendar.MONTH)+1) * 100;
		acc += cal.get(Calendar.DAY_OF_MONTH);
		return acc;
	}

    public static void resetTime(Calendar cal) {
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
    }

	public static Calendar numToCal(int num) {
		Calendar c = Calendar.getInstance();
        resetTime(c);
        int year = num / 10000;
        int month = ((num % 10000) / 100)-1;
        int day = num % 100;
		c.set(year, month, day);
		return c;
	}
	
	public static int diffDays(Calendar a, Calendar b) {
		long diff = Math.abs( a.getTimeInMillis() - b.getTimeInMillis());
		return (int)(diff / (1000 * 60 * 60 * 24));
        //daysBetween(LocalDate.fromCalendarFields(tstamp), LocalDate.fromCalendarFields(now)).getDays()
	}

    public static String toString(Calendar tstamp) {
        return FORMAT.format(tstamp.getTime());
    }

    public static int doneColor(Boolean done) {
        if (done!=null && done) {
            return Color.parseColor("#99CC00");
        }
        else return Color.WHITE;
        //else return Color.parseColor("#FF4444"); (taki czerwony)
    }

}
