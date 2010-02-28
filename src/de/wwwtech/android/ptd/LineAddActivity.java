package de.wwwtech.android.ptd;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LineAddActivity extends Activity {
	@Override
    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
        	setContentView(R.layout.line_add_activity);
        
        	//registerForContextMenu(getListView());
        }
        catch(Exception e)
        {
        	Log.e("LineAddActivity", e.getStackTrace().toString());
        }
        
        registerListener();
    }
	
	private void registerListener() {
		Button confirmButton = (Button) findViewById(R.id.confirm);
		
		confirmButton.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View view) {
		    	saveLine();
		    	finish();
		    }   
		});
	}
	
	private void saveLine()
	{
	    PublicTransportDelayDbAdapter dbAdapter = new PublicTransportDelayDbAdapter(this);
	    
	    String name = ((EditText)findViewById(R.id.line_name)).getText().toString();
	    String start = ((EditText)findViewById(R.id.line_start)).getText().toString();
	    String end = ((EditText)findViewById(R.id.line_end)).getText().toString();
	    dbAdapter.open();
	    dbAdapter.lineCreate(name, start, end);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("LineAddActivity", "Returned from Activity");
	}
}
