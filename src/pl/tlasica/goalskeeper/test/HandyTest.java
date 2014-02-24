package pl.tlasica.goalskeeper.test;

import org.junit.Test;
import pl.tlasica.goalskeeper.Handy;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;

public class HandyTest {

	@Test
	public void testCalToNum() {
		Calendar c = Calendar.getInstance();
		c.set(2013, 9, 30, 12, 53);		
		assertEquals( 20130930, Handy.calToNum(c));
	}

}
