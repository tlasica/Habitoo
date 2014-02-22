package pl.tlasica.habitoo;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Model for a view of one day with several goals and it's statuses for this dat.
 * It can be today, day in the past or in the future.
 */
public class Day {

	private	 Context							context;
	private Calendar 							now = Calendar.getInstance();
	private List<GoalListItem>					goals;
	
	private Day(List<GoalListItem> _goals) {
		goals = _goals;
	}

	public List<GoalListItem> goals() {
		return goals;
	}

	public void markDone(int position, Boolean done) {
        // update model
		GoalListItem item = goals.get(position); 
		item.done = done;
        // persist change
		DatabaseHelper db = DatabaseHelper.getInstance(context);
		db.saveDone(item.goalId, now, done);
        // update content
        reload();
        //TODO: optimize and update only this one item

	}
	
	public void newGoal(String name, String desc) {
        // persist change
		DatabaseHelper db = DatabaseHelper.getInstance(context);
		db.newGoal(name, desc, now);
        // update model
        reload();
        // TODO: smart update - 1 day only
        /*
		GoalListItem item = new GoalListItem(name);
		item.daysTracked = 1;
		item.text = desc;
		goals.add( item );
        */
	}

    public boolean removeGoal(int position) {
        GoalListItem item = goals.get(position);
        // persist change
        DatabaseHelper db = DatabaseHelper.getInstance(context);
        boolean removed = db.removeGoal(item.goalId, now);
        if (removed) {
            // update model
            GoalListItem remove = goals.remove(position);
            Log.d("DAY", "removed item:" + remove.name);
            return true;
        }
        else return false;

    }

    public void setDateAndUpdate(Calendar aDate) {
		now = aDate;
		List<GoalListItem> loaded = load();
		goals.clear();
		goals.addAll(loaded);
	}

    private void reload() {
        setDateAndUpdate(now);
    }

	private List<GoalListItem> load() {
		DatabaseHelper db = DatabaseHelper.getInstance(context);
		List<Goal> gList =  db.getOpenGoals(now);
		//List<GoalDay> gdList = db.getDailyGoalsStatus(now);

		List<GoalListItem> res = new ArrayList<GoalListItem>();
		for(Goal g: gList ) {
			GoalListItem item = new GoalListItem(g.name);
			item.goalId = g.id;
            updateGoalDetailsFromDatabase(item, g);
			res.add(item);
		}
		
		return res;
	}




    private void updateGoalDetailsFromDatabase(GoalListItem item, Goal goal) {
        // load history from the database
        DatabaseHelper db = DatabaseHelper.getInstance(context);
        List<GoalDay> history = db.getGoalHistory(item.goalId, now);
        // store createOn
        item.since = goal.createdOn;
        // calculate num of successes / tracked
        item.daysTracked = goal.numTrackingDays(now);
        Calendar from = goal.startTrackingFrom(now);
        item.daysSucc = GoalDay.calculateSuccessCount(history, from, now);
        item.text = goal.desc;
        // update "now" status
        GoalDay nowData = GoalDay.findDayInHistory(history, now);
        if (nowData != null) {
            item.done = nowData.status;
            if (nowData.notes != null) item.text =nowData.notes;
        }
        else {
            item.done = null;
            item.text = goal.desc;
            Log.e("DAY", "no data for goal:" + item.goalId + " for now:" + Handy.toString(now));
        }
    }
/*
    private GoalDay findGoalDayForGoal(List<GoalDay> list, Goal goal) {
        for(GoalDay d : list) {
            if (d.goalId == goal.id) return d;
        }
        return null;
    }

	private void clearStatus() {
		for(GoalListItem g: goals) {
			g.done = null;
			g.text = null;
		}
	}
*/
	public static Day create(Context ctx) {
		Day d = new Day( new ArrayList<GoalListItem>() );
		d.context = ctx;
		return d;		
	}
	
}
