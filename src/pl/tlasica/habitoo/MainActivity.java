package pl.tlasica.habitoo;

import java.util.Calendar;

import pl.tlasica.goalero.R;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final int REQUEST_CODE_NEWGOAL = 1813;
	
	private Calendar 				currDay = Calendar.getInstance();
	private TextView 				mCurrDayText;
	private GestureDetectorCompat 	mDetector;
	private ListView				mListView;
	private GoalListAdapter			mListAdapter;
	//private List<GoalListItem>		goals;
	private Day						mDay;
	
	protected Object mActionMode;
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
		mListView = (ListView) findViewById(R.id.listview);
		mListAdapter = new GoalListAdapter(this, R.layout.listview, mDay.goals());
		mListView.setAdapter( mListAdapter );
		mListView.setOnItemClickListener( onItemClickListener() );
		this.registerForContextMenu( mListView );
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
    	    	  	Log.d("LONGCLICK", String.format("pos=%d id=%d", position, id));
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

    public boolean onMenuGoalSuccess(MenuItem item) {
    	if (selectedItem >= 0) {
	    	String name = mDay.goals().get(selectedItem).name;
	    	mDay.markDone(selectedItem, true);
	    	selectedItem = -1;
	    	updateContent();
	        Toast.makeText(MainActivity.this, name + ": DONE!", Toast.LENGTH_SHORT).show();
    	}
        return true;
    }

    public boolean onMenuGoalFail(MenuItem item) {
    	if (selectedItem >= 0) {
	    	mDay.markDone(selectedItem, false);
	    	selectedItem = -1;
	    	updateContent();
    	}
        return true;
    }

    public boolean onMenuGoalClear(MenuItem item) {
    	if (selectedItem >= 0) {
	    	mDay.markDone(selectedItem, null);
	    	selectedItem = -1;
	    	updateContent();
    	}
        return true;
    }
    
    public boolean onMenuGoalStats(MenuItem item) {
    	if (selectedItem >= 0) {
	    	String name = mDay.goals().get(selectedItem).name;
	        Toast.makeText(MainActivity.this, name + ": Statistics to be implemented", Toast.LENGTH_SHORT).show();
	    	selectedItem = -1;
    	}
        return true;
    }
    
    public boolean onMenuGoalStop(MenuItem item) {
    	if (selectedItem >= 0) {
	    	String name = mDay.goals().get(selectedItem).name;
	        Toast.makeText(MainActivity.this, name + ": STOP to be implemented", Toast.LENGTH_SHORT).show();
	    	selectedItem = -1;
    	}
    	return true;
    }
    
    public boolean onMenuNewGoal(MenuItem item) {
		Intent intent = new Intent(this, NewGoalActivity.class);
		startActivityForResult(intent, REQUEST_CODE_NEWGOAL);
        return true;
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
    
 
    public void previousDay(View view) {
    	currDay.add(Calendar.DAY_OF_YEAR, -1);
    	updateCurrDay();
    	updateContent();    	    	
    }
    
    public void nextDay(View view) {
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
    	mDay.switchDate(currDay);
	}

	class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
       
        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, 
                float velocityX, float velocityY) {
        	float x1 = event1.getX();
        	float x2 = event2.getX();
        	
        	if (x2 - x1 > 200.0) {
        		previousDay( null );
        	}
        	if (x2 - x1 < -200.0) {
        		nextDay( null );
        	}
            return true;
        }
    }
	
}
