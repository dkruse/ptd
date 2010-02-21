package de.wwwtech.android.ptd;


import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class DelayList extends ListActivity {
    
    PublicTransportDelayDbAdapter mDbHelper;
    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
    
    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
    
    @Override
    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
        	setContentView(R.layout.delaylist);
        	mDbHelper = new PublicTransportDelayDbAdapter(this);
        	mDbHelper.open();
        
        	//registerForContextMenu(getListView());
        }
        catch(Exception e)
        {
        	Log.e("DelayListCreate", e.getStackTrace().toString());
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.menu_insert_delay);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        case INSERT_ID:
            createDelay();
            return true;
        }
        
        return super.onMenuItemSelected(featureId, item);
    }
    
    private void createDelay() {
    	Intent i = new Intent(this, DelayActivity.class);
    	startActivityForResult(i, ACTIVITY_CREATE);
    }
}
