package pl.tlasica.goalskeeper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;


public class NewGoalActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newgoal);
	}	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}
	
	public void onSave(View view) {
		EditText eName = (EditText) findViewById(R.id.newgoal_name);
		EditText eDesc = (EditText) findViewById(R.id.newgoal_desc);
		String name = eName.getText().toString();
		String desc = eDesc.getText().toString();
		if (name==null || name.equals("")) {
			eName.setError(getString(R.string.newgoal_err_namereq));
		}
		else {
			Intent data = new Intent();
			data.putExtra("name", name);
			data.putExtra("desc", desc);
			setResult(RESULT_OK, data);
			this.finish();
		}
	}
	
	public void onCancel(View view) {
		setResult(RESULT_CANCELED, null);		
		this.finish();
	}

}
