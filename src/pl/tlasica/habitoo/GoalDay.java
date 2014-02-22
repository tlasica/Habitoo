package pl.tlasica.habitoo;

import java.util.Calendar;
import java.util.List;

public class GoalDay {

	int 		id;
	int			goalId;
	Calendar	tstamp;
	Boolean		status;
	String		notes;

    public static int calculateSuccessCount(List<GoalDay> history, Calendar from, Calendar now) {
        int res = 0;
        for(GoalDay day: history) {
            if (day.tstamp.before(now) && day.tstamp.after(from)) {
                if (day.status == true ) res++;
            }
        }
        return res;
    }

    // optimization is not required because the list is returned in tstamp order desc
    public static GoalDay findDayInHistory(List<GoalDay> history, Calendar now) {
        int numNow = Handy.calToNum(now);
        for(GoalDay day: history) {
            final int numDay = Handy.calToNum(day.tstamp);
            if (numDay == numNow) return day;
        }
        return null;
    }

}
