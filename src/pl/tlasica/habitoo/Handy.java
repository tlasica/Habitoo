package pl.tlasica.habitoo;

import java.util.Calendar;

public class Handy {

	public static final int calToNum(Calendar cal) {
		int acc = 0;
		acc += cal.get(Calendar.YEAR) * 10000;
		acc += (cal.get(Calendar.MONTH)+1) * 100;
		acc += cal.get(Calendar.DAY_OF_MONTH);
		return acc;
	}
	
	public static final Calendar numToCal(int num) {
		Calendar c = Calendar.getInstance();
		c.set(num/10000, (num%10000)/100, num%100);
		return c;
	}
	
	public static final int diffDays(Calendar a, Calendar b) {
		long diff = Math.abs( a.getTimeInMillis() - b.getTimeInMillis());
		return (int)(diff / (1000 * 60 * 60 * 24));
	}

}
