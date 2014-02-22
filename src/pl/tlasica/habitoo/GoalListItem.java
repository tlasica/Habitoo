package pl.tlasica.habitoo;

import java.util.Calendar;

public class GoalListItem {

	
	enum Progress {
		STATUS_VERY_GOOD,
		STATUS_GOOD,
		STATUS_BAD,
		STATUS_VERY_BAD,
        STATUS_TOO_SHORT
	}

	public int			id;
	public int			goalId;
	public String 		name;
	public Boolean		done;
	public String 		text;
	public int			daysSucc;
	public int			daysTracked;
    public Calendar     since;
	//public Progress		progress;
	
	public GoalListItem(String _name) {
		name = _name;
	}

    public Progress progress() {
        if (daysTracked >= 7) {
            int diff = daysTracked - daysSucc;
            float ratio = (float)daysSucc / (float)daysTracked;
            if (diff == 0) return Progress.STATUS_VERY_GOOD;
            if (ratio>0.90) return Progress.STATUS_VERY_GOOD;
            if (ratio>0.75) return Progress.STATUS_GOOD;
            if (ratio>0.50) return Progress.STATUS_BAD;
            return Progress.STATUS_VERY_BAD;
        }
        else return Progress.STATUS_TOO_SHORT;
    }

	public String streak() {
		if (daysTracked > 0)
			return String.format("%d/%d", daysSucc, daysTracked);
		else
			return STREAK_UNKNOWN;
	}
	
	
	private static final String STREAK_UNKNOWN = "?";	
}
