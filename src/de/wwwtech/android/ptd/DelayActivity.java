package de.wwwtech.android.ptd;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class DelayActivity extends Activity {
	
	public static final int LINE_SELECT_DIALOG = 1;
	public static final int STATION_SELECT_DIALOG = 2;
    
    PublicTransportDelayDbAdapter mDbHelper;
    
    @Override
    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
        	setContentView(R.layout.delayactivity);
        	mDbHelper = new PublicTransportDelayDbAdapter(this);
        	mDbHelper.open();
        
        	//registerForContextMenu(getListView());
        }
        catch(Exception e)
        {
        	Log.e("DelayActivityCreate", e.getStackTrace().toString());
        }
        
        registerListener();
    }

	private void registerListener() {
		Button confirmButton = (Button) findViewById(R.id.confirm);
		
		confirmButton.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View view) {
		    	Bundle bundle = new Bundle();
		    	
		    	Intent mIntent = new Intent();
		    	mIntent.putExtras(bundle);
		    	setResult(RESULT_OK, mIntent);
		    	finish();
		    }   
		});
		
		Button lineSelectButton = (Button) findViewById(R.id.lineSelect);
		
		confirmButton.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View view) {
		    	showDialog(LINE_SELECT_DIALOG);
		    }   
		});
		
		Button stationSelectButton = (Button) findViewById(R.id.stationSelect);
		
		confirmButton.setOnClickListener(new View.OnClickListener() {

		    public void onClick(View view) {
		    	showDialog(STATION_SELECT_DIALOG);
		    }   
		});
	}
	
	protected Dialog onCreateDialog(int id) {
	    Dialog dialog = null;
	    switch(id) {
	    case LINE_SELECT_DIALOG:
	        // do the work to define the pause Dialog
	        break;
	    case STATION_SELECT_DIALOG:
	        // do the work to define the game over Dialog
	        break;
	    default:
	        dialog = null;
	    }
	    return dialog;
	}
}
