package pl.tlasica.keepdoing;

import java.util.Calendar;

import pl.tlasica.keepdoing.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.support.v4.view.GestureDetectorCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

	private Calendar 				currDay = Calendar.getInstance();
	private TextView 				mCurrDayText;
	private GestureDetectorCompat 	mDetector;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
		mCurrDayText = (TextView) findViewById(R.id.textview_date);
		mDetector = new GestureDetectorCompat(this, new MyGestureListener() );
		updateContent();
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
    	updateContent();    	    	
    }
    
    public void nextDay(View view) {
    	currDay.add(Calendar.DAY_OF_YEAR, +1);
    	updateContent();
    }
    
    public void today(View view) {
    	currDay = Calendar.getInstance();
    	updateContent();
    }
    
    private void updateContent() {
    	updateCurrDay();
    	//loadDataFromDatabase();
    	//updateCommitments();
    }

	private void updateCurrDay() {
		String str = DateFormat.getDateFormat(getApplicationContext()).format( currDay.getTime() );
		mCurrDayText.setText( str );
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
