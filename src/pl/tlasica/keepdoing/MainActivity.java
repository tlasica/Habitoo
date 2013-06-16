package pl.tlasica.keepdoing;

import java.util.Calendar;
import java.util.Date;

import pl.tlasica.keepdoing.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

public class MainActivity extends Activity {

	private Calendar currDay = Calendar.getInstance();
	private TextView m_CurrDayText;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
		m_CurrDayText = (TextView) findViewById(R.id.textview_date);
		
		updateContent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
 
    public void previousDay(View view) {
    	currDay.add(Calendar.DAY_OF_YEAR, +1);
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
//    	loadDataFromDatabase();
    	//updateCommitments();
    }

	private void updateCurrDay() {
		String str = DateFormat.getDateFormat(getApplicationContext()).format( currDay.getTime() );
		m_CurrDayText.setText( str );
	}
	
}
