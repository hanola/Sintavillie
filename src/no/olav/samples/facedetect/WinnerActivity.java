package no.olav.samples.facedetect;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

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
		 Intent l1 = new Intent(getApplicationContext(), no.olav.samples.facedetect.MainActivity.class);
		 l1.putExtra("Game_Score", mScore);
		 l1.putExtra("EXTRA_ID", datas);
		 l1.putExtra("Tot_Game_Score" , totScore);
		 l1.putExtra("game_mode" , WinMode);
		 l1.putExtra("time_expired" , intLong);
		 startActivity(l1);
		 
	 
		 
		 
	 
	 }
	 
	 public void TryAgain(View view) {
		
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
	 
}

	 
	 
