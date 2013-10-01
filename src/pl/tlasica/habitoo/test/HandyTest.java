package pl.tlasica.habitoo.test;
import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.Test;

import pl.tlasica.habitoo.Handy;

public class HandyTest {

	@Test
	public void testCalToNum() {
		Calendar c = Calendar.getInstance();
		c.set(2013, 9, 30, 12, 53);		
		assertEquals( 20130930, Handy.calToNum(c));
	}

}
