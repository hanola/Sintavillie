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

import no.olav.samples.facedetect.WinnerActivity.DoSetPOST;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LooserActivity extends Activity {
	String mExplanation = "Testing ";
    int mScore = 100;
    long datas;
    boolean mShowSignIn = false;
    private static final int REQUEST_CODE = 10;
    static CountDownTimer myTimer =null;    
    private Button mainBtn;
    int totScore = 1;
    String WinMode;
    int intLong;
    private CommentsDataSource datasource;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_looser);
		
		datasource = new CommentsDataSource(this);
	    datasource.open();
		
		Bundle extras = getIntent().getExtras();
		  if (extras != null) {
			   datas= extras.getLong("EXTRA_ID");
			   mScore = extras.getInt("Game_Score");
			   totScore = extras.getInt("Tot_Game_Score");
      		WinMode = extras.getString("game_mode");
      		intLong = (int) datas;
			   if (WinMode != null) {
				   Log.i("Score" , "Put String WinAct estimated time  "+intLong);
				   Log.i("Score" , "Put int WinAct   "+mScore);
				   Log.i("Score" , "Put int WinAct tot score  "+totScore);
				   Log.i("Score" , "Put int WinAct  gamemode "+WinMode);
				   
				   
			   }
		  }
		
		  SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		  String name = preferences.getString("Name","");
		  int scoreSmile = preferences.getInt("ScoreSmile", mScore);
		  Log.i("Score", "SharedPref winner  "+name);
		  Log.i("Score", "SharedPref scoreSmile   "+scoreSmile);
		  
		  
		  
      //updateUi();
      
	}
		
		
	
	void updateUi() {
    	
	       
        TextView explainTv = (TextView)findViewById(R.id.scoreblurb);
        TextView scoreTv = (TextView)findViewById(R.id.score_display);
       scoreTv.setText(String.valueOf(totScore));
       explainTv.setText(String.valueOf(intLong));
       
        //openAlert();
        
    }

   

	
	
	
	 public void ToMain(View view) {
		 
		 String timeStamp2 = new SimpleDateFormat("ddMM_yyyy_HHmm_ss").format(Calendar.getInstance().getTime());
  		Log.i("TotScore" , "Loose activity EXIT pressed   "+timeStamp2);
		 
		 Intent l1 = new Intent(getApplicationContext(), no.olav.samples.facedetect.MainActivity.class);
		 l1.putExtra("Game_Score", mScore);
		 l1.putExtra("EXTRA_ID", datas);
		 l1.putExtra("Tot_Game_Score" , totScore);
		 l1.putExtra("game_mode" , WinMode);
		 startActivity(l1);
		 
	 
	 
	 }
	 
	 public void TryAgain(View view) {
		 
		 String event2 = "Loose activity TRY AGAIN pressed  ";
  	      String timeStamp2 = new SimpleDateFormat("ddMM_yyyy_HHmm_ss").format(Calendar.getInstance().getTime());
  	      
  	    String age="0";
	      String points="0";
		   DoSetPOST mDoSetPOST = new DoSetPOST(LooserActivity.this, event2, timeStamp2, age, points);
			mDoSetPOST.execute("");
  	      
  		Log.i("TotScore" , "Loose activity TRY AGAIN pressed   "+timeStamp2);
		 
		 Intent l1 = new Intent(getApplicationContext(), org.opencv.samples.facedetect.FdActivity.class);
		  	  startActivity(l1);
			
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
				    LooserActivity.this.runOnUiThread(new Runnable() {
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
