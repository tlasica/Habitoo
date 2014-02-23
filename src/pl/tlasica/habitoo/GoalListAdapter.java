package pl.tlasica.habitoo;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import pl.tlasica.goalskeeper.R;

import java.util.List;

public class GoalListAdapter extends ArrayAdapter<GoalListItem> {

	public GoalListAdapter(Context context, int resource, List<GoalListItem> objects) {
		super(context, resource, R.id.goalname, objects);
		// TODO Auto-generated constructor stub
	}

    /*
    @Override
    public boolean isEnabled(int position) {
        // to disable selectability on list items
        return false;
    }
    */

    @Override
	public View getView(int position, View convertView, ViewGroup parent) {  	
        //get reference to the row
	    final View row = super.getView(position, convertView, parent);
	    // get data
	    GoalListItem item = this.getItem(position);
        if (item != null) {
	        // set name
	        TextView tvName = (TextView) row.findViewById(R.id.goalname);
	        tvName.setText(item.name);
	        int rgb = Handy.doneColor(item.done);
	        tvName.setBackgroundColor(rgb);
	        // set status icon
	        ImageView ivProgress = (ImageView) row.findViewById(R.id.goalprogress);
	        int iconProgressId = iconProgress(item);
	        if (iconProgressId>0)
		        ivProgress.setImageResource(iconProgressId);
	        else
		        ivProgress.setImageDrawable(null);
	        // set since
            if (item.since != null) {
                String textSince = DateFormat.getLongDateFormat(this.getContext()).format( item.since.getTime() );
                TextView tvSince = (TextView) row.findViewById(R.id.goal_since);
                tvSince.setText(this.getContext().getString(R.string.tracked_since) + " " + textSince);
            }
	        // set additional test
	        TextView tvText = (TextView) row.findViewById(R.id.goaltext);
	        tvText.setText(item.text);
	        // set streak
	        TextView tvStreak = (TextView) row.findViewById(R.id.goalstreak);
	        tvStreak.setText(item.streak());
        }
        else Log.w("ADAPTER", "Item not found for position:" + position);
	    return row;
	  }
	
	
	private int iconProgress(GoalListItem i) {

        switch (i.progress()) {
            case STATUS_VERY_GOOD:
                return R.drawable.weather_clear;
            case STATUS_GOOD:
                return R.drawable.weather_few_clouds;
            case STATUS_BAD:
                return R.drawable.weather_overcast;
            case STATUS_VERY_BAD:
                return R.drawable.weather_storm;
            case STATUS_TOO_SHORT:
                return R.drawable.weather_too_short;
            default:
                return 0;
        }
	}

}
