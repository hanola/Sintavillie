package no.olav.samples.facedetect;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;


import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class GetDb extends ListActivity {
	
	  private CommentsDataSource datasource;
	  String firstName = "OlavErBest";
		String lastName = "TestData";
		String age = "999";
		String points = "999";
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
    
	case R.id.add:
		String[] getArrayComments = new String[3000];
    	getArrayComments = datasource.getAllCommentsArray();
    	
    	int i = 0;
    	
    	
    	for(String s : getArrayComments){
    	//Log.i("test", "String get commentArray mainTest  " +getArrayComments[i]);
    	DoSetPOST mDoSetPOST = new DoSetPOST(GetDb.this, s, lastName, age, points);
		mDoSetPOST.execute("");
		i++;
    	}
  }
  adapter.notifyDataSetChanged();
  	
}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.get_db, menu);
		return true;
	}
	
	public class DoSetPOST extends AsyncTask<String, Void, Boolean>{

		Context mContext = null;
		String strFirstName = "";
		String strLastName = "";
		String strAge = "";
		String strPoints = "";
		
		Exception exception = null;
		
		DoSetPOST(Context context, String firstName, String lastName, String age, String points){
			mContext = context;
			strFirstName = firstName;
			strLastName = lastName;
			strAge = age;
			strPoints = points;			
		}

		@Override
		protected Boolean doInBackground(String... arg0) {

			try{

				//Setup the parameters
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				//for search 
				//nameValuePairs.add(new BasicNameValuePair("FirstNameToSearch", strNameToSearch));
				nameValuePairs.add(new BasicNameValuePair("firstname", strFirstName));
				nameValuePairs.add(new BasicNameValuePair("lastname", strLastName));
				nameValuePairs.add(new BasicNameValuePair("age", strAge));
				nameValuePairs.add(new BasicNameValuePair("points", strPoints));
				//Add more parameters as necessary

				//Create the HTTP request
				HttpParams httpParameters = new BasicHttpParams();

				//Setup timeouts
				HttpConnectionParams.setConnectionTimeout(httpParameters, 15000);
				HttpConnectionParams.setSoTimeout(httpParameters, 15000);			

				HttpClient httpclient = new DefaultHttpClient(httpParameters);
				HttpPost httppost = new HttpPost("http://www.vasetskiheiser.no/clientservertest/insert.php");
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));        
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();

				final String result = EntityUtils.toString(entity);
				
				//new thread for toast
				GetDb.this.runOnUiThread(new Runnable() {
				    public void run() {
				    	Toast.makeText(getBaseContext(), result,
								Toast.LENGTH_SHORT).show();
				    }
				});
				
			
	            
				
			}catch (Exception e){
				Log.e("ClientServerDemo", "Error:", e);
				exception = e;
			}

			return true;
		}
		
		


		@Override
		protected void onPostExecute(Boolean valid){
			
			//buttonSetData.setEnabled(true);
			
			if(exception != null){
				Toast.makeText(mContext, exception.getMessage(), Toast.LENGTH_LONG).show();
			}
		}

	}

}
