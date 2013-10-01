package pl.tlasica.goalero;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import android.content.Context;

public class Day {

	private	 Context							context;
	private Calendar 							date = Calendar.getInstance();
	private List<GoalListItem>					goals;
	
	private Day(List<GoalListItem> _goals) {
		goals = _goals;
	}

	public List<GoalListItem> goals() {
		return goals;
	}

	public void markDone(int position, Boolean done) {
		GoalListItem item = goals.get(position); 
		item.done = done;		
		DatabaseHelper db = DatabaseHelper.getInstance(context);
		db.saveDone(item.goalId, date, done);
	}
	
	public void newGoal(String name, String desc) {
		DatabaseHelper db = DatabaseHelper.getInstance(context);
		db.newGoal(name, desc, date);
		GoalListItem item = new GoalListItem(name);
		item.daysTracked = 1;
		item.text = desc;
		goals.add( item );		
	}
	
	public void switchDate(Calendar _date) {
		date = _date;
		List<GoalListItem> loaded = load(_date);
		goals.clear();
		goals.addAll(loaded);
	}
	
	private List<GoalListItem> load(Calendar _date) {
		DatabaseHelper db = DatabaseHelper.getInstance(context);
		List<Goal> gList =  db.getOpenGoals(_date);
		List<GoalDay> gdList = db.getDailyGoalsStatus(_date);

		List<GoalListItem> res = new ArrayList<GoalListItem>();
		for(Goal g: gList ) {
			GoalListItem item = new GoalListItem(g.name);
			item.goalId = g.id;
			item.daysTracked = 10;	//TODO in Handy
			
			//TODO: extract method
			for(GoalDay d : gdList) {
				if (d.goalId == g.id) {
					item.done = d.status;
					item.text = d.notes;
					break;
				}
			}
			res.add(item);
		}
		
		return res;
	}
	
	private void clearStatus() {
		for(GoalListItem g: goals) {
			g.done = null;
			g.text = null;
		}
	}
	
	public static final Day create(Context ctx) {
		Day d = new Day( new ArrayList<GoalListItem>() );
		d.context = ctx;
		return d;		
	}
	
	public static final Day createForTesting(Context ctx) {
		Day d = create(ctx);
		TestData.feed(d.goals);
		return d;
	}
}
