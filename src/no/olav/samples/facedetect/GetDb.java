package no.olav.samples.facedetect;

import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

public class GetDb extends ListActivity {
	
	  private CommentsDataSource datasource;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_db);
		
		datasource = new CommentsDataSource(this);
	    datasource.open();

	    List<Comment> values = datasource.getAllComments();

	    // Use the SimpleCursorAdapter to show the
	    // elements in a ListView
	    ArrayAdapter<Comment> adapter = new ArrayAdapter<Comment>(this,
	        android.R.layout.simple_list_item_1, values);
	    setListAdapter(adapter);
	}
	
	    public void onClick(View view) {
	        @SuppressWarnings("unchecked")
	        ArrayAdapter<Comment> adapter = (ArrayAdapter<Comment>) getListAdapter();
	        //initializing comment
	        Comment comment = null;
	        switch (view.getId()) {
	        
	case R.id.delete:
    if (getListAdapter().getCount() > 0) {
      comment = (Comment) getListAdapter().getItem(0);
      datasource.deleteComment(comment);
      adapter.remove(comment);
    }
    break;
  }
  adapter.notifyDataSetChanged();
  	
}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.get_db, menu);
		return true;
	}

}
