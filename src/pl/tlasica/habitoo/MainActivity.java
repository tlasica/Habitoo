package pl.tlasica.habitoo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import pl.tlasica.goalero.R;

import java.util.Calendar;

public class MainActivity extends Activity {

	private static final int REQUEST_CODE_NEWGOAL = 1813;
	
	private Calendar 				currDay = Calendar.getInstance();
	private TextView 				mCurrDayText;
	private GestureDetectorCompat 	mDetector;
    private GoalListAdapter			mListAdapter;
	//private List<GoalListItem>	goals;
	private Day						mDay;
	
	public int selectedItem = -1;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        // init controls
		mCurrDayText = (TextView) findViewById(R.id.textview_date);
		// init list view
		//goals = new ArrayList<GoalListItem>();
		mDay = Day.create( getApplicationContext() );
        ListView mListView = (ListView) findViewById(R.id.listview);
		mListAdapter = new GoalListAdapter(this, R.layout.listview, mDay.goals());
		mListView.setAdapter(mListAdapter);
		mListView.setOnItemClickListener(onItemClickListener());
		this.registerForContextMenu(mListView);
		// gesture detector to swipe => change date
		mDetector = new GestureDetectorCompat(this, new MyGestureListener() );
		// update content
		updateCurrDay();
		updateContent();
    }

    private OnItemClickListener onItemClickListener() {
    	
    	return new OnItemClickListener() {
    	      @Override
    	      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectedItem = position;
            view.showContextMenu();
    	      }
    	};
	}

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
      super.onCreateContextMenu(menu, v, menuInfo);
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.listview_actions, menu);
    }    

    public void onMenuGoalSuccess(MenuItem item) {
    	if (selectedItem >= 0) {
	    	String name = mDay.goals().get(selectedItem).name;
	    	mDay.markDone(selectedItem, true);
	    	selectedItem = -1;
	    	updateContent();
	        Toast.makeText(MainActivity.this, name + "\n" + getString(R.string.marked_success), Toast.LENGTH_SHORT).show();
    	}
    }

    public void onMenuGoalFail(MenuItem item) {
    	if (selectedItem >= 0) {
            String name = mDay.goals().get(selectedItem).name;
	    	mDay.markDone(selectedItem, false);
	    	selectedItem = -1;
	    	updateContent();
            Toast.makeText(MainActivity.this, name + "\n" + getString(R.string.marked_fail), Toast.LENGTH_SHORT).show();
    	}
    }

    public void onMenuGoalStats(MenuItem item) {
    	if (selectedItem >= 0) {
            GoalListItem goalListItem = mDay.goals().get(selectedItem);
            String name = goalListItem.name;
            selectedItem = -1;
            // open activity
            Intent intent = new Intent(this, HistoryViewActivity.class);
            intent.putExtra("GOAL_ID", goalListItem.goalId);
            intent.putExtra("GOAL_NAME", goalListItem.name);
            intent.putExtra("GOAL_SINCE", goalListItem.since.getTimeInMillis());
            intent.putExtra("NOW", currDay.getTimeInMillis());
            startActivity(intent);
    	}
    }
    
    public void onMenuGoalStop(MenuItem item) {
    	if (selectedItem >= 0) {
            GoalListItem goal = mDay.goals().get(selectedItem);
            mDay.removeGoal(selectedItem);
	    	selectedItem = -1;
            updateContent();
            Toast.makeText(MainActivity.this, goal.name + "\n" + getString(R.string.toast_goal_removed), Toast.LENGTH_SHORT).show();
    	}
    }

    public void onMenuNewGoal(MenuItem item) {
		Intent intent = new Intent(this, NewGoalActivity.class);
		startActivityForResult(intent, REQUEST_CODE_NEWGOAL);
    }

    public void onMenuHelp(MenuItem item) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.menu_help))
                .setMessage(getString(R.string.text_help))
                .setPositiveButton("Ok", null)
                .show();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode==REQUEST_CODE_NEWGOAL && resultCode==RESULT_OK) {
    		String name = data.getStringExtra("name");
    		String desc = data.getStringExtra("desc");
        	mDay.newGoal(name, desc);
            Toast.makeText(MainActivity.this, name + " created!", Toast.LENGTH_SHORT).show();
            this.updateContent();
    		
    	}
    }
    
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override 
    public boolean onTouchEvent(MotionEvent event){ 
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
    
 
    public void previousDay() {
    	currDay.add(Calendar.DAY_OF_YEAR, -1);
    	updateCurrDay();
    	updateContent();    	    	
    }
    
    public void nextDay() {
    	currDay.add(Calendar.DAY_OF_YEAR, +1);
    	updateCurrDay();
    	updateContent();
    }
    
    public void onDateClick(View view) {
    	currDay = Calendar.getInstance();
    	updateCurrDay();
    	updateContent();
    }
    
    private void updateContent() {
    	mListAdapter.notifyDataSetChanged();  	
    }

	private void updateCurrDay() {
		String str = DateFormat.getDateFormat(getApplicationContext()).format( currDay.getTime() );
		mCurrDayText.setText( str );
    	mDay.setDateAndUpdate(currDay);
	}

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
       
        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, 
                float velocityX, float velocityY) {
        	float x1 = event1.getX();
        	float x2 = event2.getX();
            float fling = x2-x1;

            Log.i("FLING", "x2-x1: " + fling);

        	if (fling > 150.0) {
        		previousDay();
        	}
        	if (fling < -150.0) {
        		nextDay();
        	}
            return true;
        }
    }
	
}
