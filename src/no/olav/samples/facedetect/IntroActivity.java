package no.olav.samples.facedetect;



import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

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
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class IntroActivity extends Activity {
	private CommentsDataSource datasource;
	
	ImageButton nextbutton;
	Button startbutton;
	ImageView infoImage;
	int i = 0; 
	
	int infoImages[] = {R.drawable.sintavillie, R.drawable.inverted, R.drawable.end, R.drawable.tutorial1, R.drawable.tutorial2};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intro);
		
		datasource = new CommentsDataSource(this);
	    datasource.open();
		
		addButtonListener();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.intro, menu);
		return true;
	}
	
	protected void chooseLevel(View view){
		Comment comment3 = null;
		 String event = "Pressing play in intro:";
	      String timeStamp = new SimpleDateFormat("ddMM_yyyy_HHmm_ss").format(Calendar.getInstance().getTime());
		
	      //insert do web server
	      String age = "0";
	      String points = "0";
	      DoSetPOST mDoSetPOST = new DoSetPOST(IntroActivity.this, event, timeStamp, age, points);
			mDoSetPOST.execute("");
	      
	      
	      comment3 = datasource.createComment(event + timeStamp);
		Log.i("TotScore" , "Pressing play in intro:   "+timeStamp);
		
		//Intent intentLevels = new Intent(this, no.olav.samples.facedetect.GetDb.class);
		//startActivity(intentLevels);
		
		Intent intentLevels = new Intent(this, no.olav.samples.facedetect.MainActivity.class);
		startActivity(intentLevels);
	}
	
	public void addButtonListener(){
		 
		infoImage = (ImageView)findViewById(R.id.imageView1);
		
		nextbutton = (ImageButton)findViewById(R.id.next);
		nextbutton.setOnClickListener(new View.OnClickListener(){
			
			public void onClick(View v){
				Comment comment3 = null;
				 String event = "Intro next button";
			      String timeStamp = new SimpleDateFormat("ddMM_yyyy_HHmm_ss").format(Calendar.getInstance().getTime());
			      String age = "0";
			      String points = "0";
			      
			      DoSetPOST mDoSetPOST = new DoSetPOST(IntroActivity.this, event, timeStamp, age, points);
					mDoSetPOST.execute("");
			     // into Sqlite database
				comment3 = datasource.createComment(event + timeStamp);
				Log.i("TotScore" , "Time for pressing button next   "+timeStamp);
				
				SnapShot sn = new SnapShot(v);
				Bitmap b = sn.snap();
				Log.d("Snap", "result: "+b);
				infoImage.setImageResource(infoImages[i++]);
				if (i==5){
					i=0;
					nextbutton.setVisibility(View.INVISIBLE);
					startbutton.setVisibility(View.VISIBLE);	
				}
			}
		});
		
		startbutton = (Button)findViewById(R.id.startGame);
		startbutton.setVisibility(View.INVISIBLE);	
		
		//startbutton
		startbutton.setOnClickListener(new View.OnClickListener(){
			
			public void onClick(View v){
				
				chooseLevel(v);
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	 @Override
	  protected void onResume() {
	    datasource.open();
	    super.onResume();
	  }

	  @Override
	  protected void onPause() {
	    datasource.close();
	    super.onPause();
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
					IntroActivity.this.runOnUiThread(new Runnable() {
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
			
	  }
}
