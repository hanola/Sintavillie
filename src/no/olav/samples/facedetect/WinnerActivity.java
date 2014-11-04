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


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.example.games.basegameutils.BaseGameActivity;

public class WinnerActivity extends BaseGameActivity implements OnClickListener{
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
    
 // request codes we use when invoking an external activity
    final int RC_RESOLVE = 5000, RC_UNUSED = 5001;

    // tag for debug logging
    final boolean ENABLE_DEBUG = true;
    final String TAG = "TanC";

    // playing on hard mode?
    boolean mHardMode = false;

    // achievements and scores we're pending to push to the cloud
    // (waiting for the user to sign in, for instance)
    
   
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_winner);
		
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
		  
		  
		  
        updateUi();
        
	}

	

    void updateUi() {
    	
       
        TextView explainTv = (TextView)findViewById(R.id.scoreblurb);
        TextView scoreTv = (TextView)findViewById(R.id.score_display);
       scoreTv.setText(String.valueOf(totScore));
       explainTv.setText(String.valueOf(intLong));
     
        //openAlert();
        
    }

   

	@Override
	public void onSignInFailed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSignInSucceeded() {
		// TODO Auto-generated method stub
		
	}
	
	 public void ToMain(View view) {
		 
		 Comment comment2 = null;
  		 String event2 = "Win activity Save and exit pressed  ";
  	      String timeStamp2 = new SimpleDateFormat("ddMM_yyyy_HHmm_ss").format(Calendar.getInstance().getTime());
  	      
  	    String age="0";
	      String points="0";
		   DoSetPOST mDoSetPOST = new DoSetPOST(WinnerActivity.this, event2, timeStamp2, age, points);
			mDoSetPOST.execute("");
  	      
  		comment2 = datasource.createComment(event2 + timeStamp2);
  		Log.i("TotScore" , "Win activity Save and exit pressed   "+timeStamp2);
		 
		 Intent l1 = new Intent(getApplicationContext(), no.olav.samples.facedetect.MainActivity.class);
		 l1.putExtra("Game_Score", mScore);
		 l1.putExtra("EXTRA_ID", datas);
		 l1.putExtra("Tot_Game_Score" , totScore);
		 l1.putExtra("game_mode" , WinMode);
		 l1.putExtra("time_expired" , intLong);
		 startActivity(l1);
		 
	 
		 
		 
	 
	 }
	 
	 public void TryAgain(View view) {
		 
		 Comment comment2 = null;
  		 String event2 = "Win activity try again pressed  ";
  	      String timeStamp2 = new SimpleDateFormat("ddMM_yyyy_HHmm_ss").format(Calendar.getInstance().getTime());
  	    
    	    String age="0";
  	      String points="0";
  		   DoSetPOST mDoSetPOST = new DoSetPOST(WinnerActivity.this, event2, timeStamp2, age, points);
  			mDoSetPOST.execute("");
  			
  		comment2 = datasource.createComment(event2 + timeStamp2);
  		Log.i("TotScore" , "Win activity try again pressed   "+timeStamp2);
		
	//	 Intent l1 = new Intent(getApplicationContext(), org.opencv.samples.facedetect.EasyOneCamera.class);
	  //	  startActivity(l1);
		 
		 
		 if (WinMode.contentEquals("easy")){
			 Intent l2 = new Intent(getApplicationContext(), org.opencv.samples.facedetect.EasyOneCamera.class);
	  	  startActivity(l2);
		 }
		 
		 if (WinMode.contentEquals("hard")){
			 Intent l3 = new Intent(getApplicationContext(), org.opencv.samples.facedetect.FdActivity.class);
	  	  startActivity(l3);
		 }
		 
		 if (WinMode.contentEquals("frenzy")){
			 Intent l4 = new Intent(getApplicationContext(), org.opencv.samples.facedetect.FrenzyActivity.class);
	  	  startActivity(l4);
		 }
		 
		 
			 
		 
		 
	 
	 }



	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
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
				    WinnerActivity.this.runOnUiThread(new Runnable() {
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

	 
	 
