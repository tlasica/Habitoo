package pl.tlasica.habitoo;

import java.util.List;

public class TestData {

	public static final void feed(List<GoalListItem> goals) {
		GoalListItem i;
		i = new GoalListItem("No sweets").setStats(10, 13, GoalListItem.Progress.STATUS_GOOD);
		i.done = true;
		i.text = "even no chocolate!";
		goals.add( i );
		//
		i = new GoalListItem("Exercise").setStats(14, 15, GoalListItem.Progress.STATUS_VERY_GOOD);
		i.done = false;
		i.text = "illness prevents me from doing exercises, even small ones";
		goals.add( i );
		i = new GoalListItem("No drinks").setStats(1, 9, GoalListItem.Progress.STATUS_VERY_BAD);
		i.done = null;
		goals.add( i );
		// very long texts
		i = new GoalListItem("No sex, do drugs,no drinks, no rock&roll").setStats(1, 5, GoalListItem.Progress.STATUS_BAD);		
		i.done = null;
		i.text = "Unfortunatelly I'm not able to stop all of those things together." +
		"It makes me furious and tired, but I have to. What to do?";
		goals.add( i );		
	}

}
