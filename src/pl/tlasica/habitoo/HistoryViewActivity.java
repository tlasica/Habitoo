package pl.tlasica.habitoo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import pl.tlasica.goalero.R;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by tomek on 23.02.14.
 */
public class HistoryViewActivity extends Activity {

    GridView    mGridView;
    Calendar    mUntilDate;
    Calendar    mTrackedSince;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_history);

        mGridView = (GridView) findViewById(R.id.historygrid);

        // set until date
        long ms = getIntent().getLongExtra("NOW", 0);
        mUntilDate = Calendar.getInstance();
        mUntilDate.setTimeInMillis(ms);
        TextView tvSince = (TextView) findViewById(R.id.textview_history_until);
        tvSince.setText(Handy.toString(mUntilDate));

        // get goal name
        String name = getIntent().getStringExtra("GOAL_NAME");
        TextView tvName = (TextView) findViewById(R.id.textview_history_name);
        tvName.setText(name);

        // get goal id
        int id = getIntent().getIntExtra("GOAL_ID", 0);
        if (id == 0) {
            Log.e("HISTORY", "No goal id in intent extras!");
            this.finish();
        }

        ms = getIntent().getLongExtra("GOAL_SINCE", 0);
        mTrackedSince = Calendar.getInstance();
        mTrackedSince.setTimeInMillis(ms);
        mTrackedSince.clear(Calendar.HOUR);
        mTrackedSince.clear(Calendar.MINUTE);
        mTrackedSince.clear(Calendar.SECOND);
        mTrackedSince.clear(Calendar.MILLISECOND);

        List<DataItem> data = this.buildAdapterData(id);

        // create adapter to show data
        BaseAdapter adapter = new HistoryViewAdapter(this, data);
        mGridView.setAdapter(adapter);


    }

    private List<DataItem> listOfPastDays(Calendar now, int howManyDays) {
        List<DataItem> res = new LinkedList<DataItem>();
        for(int i=-howManyDays+1; i<=0; i++) {
            Calendar x = (Calendar) now.clone();
            x.add(Calendar.DATE, i);
            res.add( new DataItem(x, false));
        }
        return res;
    }

    private List<DataItem> buildAdapterData(int goalId) {
        // connect to database
        DatabaseHelper db = DatabaseHelper.getInstance(this.getApplicationContext());
        // get GoalDescription
        final List<GoalDay> history = db.getGoalHistory(goalId, mUntilDate);
        // build initial list of calendar dates
        final List<DataItem> res = listOfPastDays(mUntilDate, 30);
        // update done information
        for(DataItem item: res) {
            int num = Handy.calToNum(item.date);
            for(GoalDay h: history) {
                if ((h.status!=null && h.status) && Handy.calToNum(h.tstamp) == num) item.isDone = true;
            }
            if (item.date.after(mTrackedSince)) item.isTracked = true;
        }

        return res;

        /*
        List<DataItem> res = new LinkedList<DataItem>();
        Calendar start = Calendar.getInstance();
        for(int i=1; i<10; i++) {
            Calendar x = (Calendar) start.clone();
            x.add(Calendar.DATE, i);
            boolean isDone = (x.get(Calendar.DAY_OF_YEAR) % 6 == 2);
            res.add( new DataItem(x, isDone));
        }
        */
    }

}

class DataItem {
    Calendar date;
    boolean isDone;
    boolean isTracked = false;
    public DataItem(Calendar c, boolean b) {date=c; isDone=b;}
}


class HistoryViewAdapter extends BaseAdapter {


    private class ContentItem {
        String  text;
        boolean isDone;
        boolean isTracked;
        ContentItem(String s) {text=s;}
        ContentItem(String s, boolean b) {text=s; isDone=b;}
    }

    private final Context       context;

    private final List<ContentItem>    content = new LinkedList<ContentItem>();

    int                         firstDayOfWeek = Calendar.getInstance().getFirstDayOfWeek();
    int[]                       weekDays = new int[7];
    String[]                    weekdayNames = new String[7];

    public HistoryViewAdapter(Context context, List<DataItem> history) {
        this.context = context;

        // prepare weekday names and positions
        prepareWeekDays();

        // prepare string content / dones based on data
        prepareContent(history);
    }

    @Override
    public int getCount() {
        return content.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        TextView textView = null;
        if (convertView == null) {
            textView = new TextView(context);
            // set one line
            textView.setSingleLine();
            // fill content
            ContentItem item = content.get(i);
            if (item.text != null) {
                // set text
                textView.setText( item.text);
                // set text color
                textView.setTextColor(Color.BLACK);
                // set background if done
                int bgColor = Handy.doneColor(item.isDone);
                textView.setBackgroundColor(bgColor);
                // gravity
                textView.setGravity(Gravity.CENTER);
                // bold for first weekdays
                if (i<7) {
                    textView.setTypeface(null, Typeface.BOLD);
                    textView.setBackgroundColor(Color.LTGRAY);
                }
                else { // untracked
                    if (!item.isTracked) textView.setTextColor(Color.LTGRAY);
                }
            }
        }
        else {
            textView = (TextView) convertView;
        }

        return textView;
    }

    private void prepareWeekDays() {
        // list of week names 0..7, Sunday=1, Monday=2
        String[] names = new DateFormatSymbols().getWeekdays();
        int start = firstDayOfWeek;
        for(int i=0; i<7; ++i) {
            int dayOfWeek = (start + i);
            if (dayOfWeek>7) dayOfWeek = dayOfWeek -7 ;
            weekdayNames[i] = names[dayOfWeek].substring(0, 3);
            weekDays[i] = dayOfWeek;
        }
        Log.d("HISTORY", "weekdays:" + Arrays.toString(weekDays));
    }

    /**
     * Combines weekdays and history into one content
     * @param history
     */
    private void prepareContent(List<DataItem> history) {
        content.clear();

        // adding week names
        for(String w: this.weekdayNames ) {
            content.add( new ContentItem(w));
        }

        // calculate column for 1st date
        int startCol = this.columnOfDate(history.get(0).date);
        // add empty for
        for(int i=0; i<startCol; ++i) {
            content.add(new ContentItem(null));
        }

        // add history
        SimpleDateFormat fmt = new SimpleDateFormat("dd");
        for(DataItem di: history) {
            String s = fmt.format(di.date.getTime());
            ContentItem ci = new ContentItem(s, di.isDone);
            ci.isTracked = di.isTracked;
            content.add(ci);
        }
    }

    private int columnOfDate(Calendar day) {
        int dayOfWeek = day.get(Calendar.DAY_OF_WEEK);
        for(int c=0; c<7; ++c) {
            if (this.weekDays[c] == dayOfWeek) return c;
        }
        return 0;
    }
}