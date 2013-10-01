package pl.tlasica.habitoo;

public class GoalListItem {

	
	enum Progress {
		STATUS_VERY_GOOD,
		STATUS_GOOD,
		STATUS_BAD,
		STATUS_VERY_BAD
	}

	public int			id;
	public int			goalId;
	public String 		name;
	public Boolean		done;
	public String 		text;
	public int			daysSucc;
	public int			daysTracked;
	public Progress		progress;
	
	public GoalListItem(String _name) {
		name = _name;
	}
	
	public GoalListItem setStats(int _dSucc, int _dTrack, Progress _progress) {
		daysSucc = _dSucc;
		daysTracked = _dTrack;
		progress = _progress;
		return this;
	}

	public String streak() {
		if (daysTracked > 0)
			return String.format("%d/%d", daysSucc, daysTracked);
		else
			return STREAK_UNKNOWN;
	}
	
	
	private static final String STREAK_UNKNOWN = "?";	
}
