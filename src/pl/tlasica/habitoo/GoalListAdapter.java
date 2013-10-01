package pl.tlasica.habitoo;

import java.util.List;

import pl.tlasica.goalero.R;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GoalListAdapter extends ArrayAdapter<GoalListItem> {

	public GoalListAdapter(Context context, int resource, List<GoalListItem> objects) {
		super(context, resource, R.id.goalname, objects);
		// TODO Auto-generated constructor stub
	}

	@Override 
	public View getView(int position, View convertView, ViewGroup parent) {  	
	   //get reference to the row
	   final View row = super.getView(position, convertView, parent);
	   // get data
	   GoalListItem item = this.getItem(position);
	   // set name
	   TextView tvName = (TextView) row.findViewById(R.id.goalname);
	   tvName.setText(item.name);	  
	   int rgb = doneColor(item);
	   tvName.setBackgroundColor(rgb);
	   //TODO: set color = f(done)
	   // set status icon
	   ImageView ivProgress = (ImageView) row.findViewById(R.id.goalprogress);
	   int iconProgressId = iconProgress(item);
	   if (iconProgressId>0)
		   ivProgress.setImageResource(iconProgressId);
	   else
		   ivProgress.setImageDrawable(null);
	   Log.d("PROGRESS", "Icon progress = " + iconProgressId);
	   // set additional test
	   TextView tvText = (TextView) row.findViewById(R.id.goaltext);
	   tvText.setText(item.text);
	   // set streak
	   TextView tvStreak = (TextView) row.findViewById(R.id.goalstreak);
	   tvStreak.setText(item.streak());
	   
	   return row;  
	  }
	
	
	private int iconProgress(GoalListItem i) {
		if (i.daysTracked > 5) {
			float ratio = (float)i.daysSucc / (float)i.daysTracked;
			Log.d("PROGRESS", "Progress ratio = " + ratio);
			if (ratio>=0.9) return R.drawable.weather_clear;
			else if (ratio>=0.75) return R.drawable.weather_few_clouds;
			else if (ratio>=0.50) return R.drawable.weather_overcast;
			else return R.drawable.weather_storm;
		}
		else {
			return 0;
		}
	}

	private int doneColor(GoalListItem i) {
		if (i.done==null) return Color.LTGRAY;
		else if (i.done==true) return Color.parseColor("#43CF30");
		else return Color.RED;
	}
}
