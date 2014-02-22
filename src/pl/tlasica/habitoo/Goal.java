package pl.tlasica.habitoo;

import java.util.Calendar;

public class Goal {

    public static final int TRACKING_WINDOW_DAYS = 14;

	int 		id;
	String 		name;
	String		desc;
	Calendar	createdOn;
	Calendar	removedOn;

    public int numTrackingDays(Calendar now) {
        if (now.after(createdOn)) {
            int days = Handy.diffDays(createdOn, now)+1;
            if (days > TRACKING_WINDOW_DAYS) {
                return TRACKING_WINDOW_DAYS;
            } else return days;
        }
        else return 0;
    }

    public Calendar startTrackingFrom(Calendar now) {
        int numDays = numTrackingDays(now);
        if (numDays > TRACKING_WINDOW_DAYS) {
            Calendar from = (Calendar)now.clone();
            from.add(Calendar.DATE, -numDays);
            return from;
        }
        else return createdOn;
    }

}
