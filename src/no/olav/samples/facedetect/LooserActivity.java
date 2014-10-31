package no.olav.samples.facedetect;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
		 
		 Comment comment2 = null;
  		 String event2 = "Loose activity EXIT pressed  ";
  	      String timeStamp2 = new SimpleDateFormat("ddMM_yyyy_HHmm_ss").format(Calendar.getInstance().getTime());
  		comment2 = datasource.createComment(event2 + timeStamp2);
  		Log.i("TotScore" , "Loose activity EXIT pressed   "+timeStamp2);
		 
		 Intent l1 = new Intent(getApplicationContext(), no.olav.samples.facedetect.MainActivity.class);
		 l1.putExtra("Game_Score", mScore);
		 l1.putExtra("EXTRA_ID", datas);
		 l1.putExtra("Tot_Game_Score" , totScore);
		 l1.putExtra("game_mode" , WinMode);
		 startActivity(l1);
		 
	 
	 
	 }
	 
	 public void TryAgain(View view) {
		 
		 Comment comment2 = null;
  		 String event2 = "Loose activity TRY AGAIN pressed  ";
  	      String timeStamp2 = new SimpleDateFormat("ddMM_yyyy_HHmm_ss").format(Calendar.getInstance().getTime());
  		comment2 = datasource.createComment(event2 + timeStamp2);
  		Log.i("TotScore" , "Loose activity TRY AGAIN pressed   "+timeStamp2);
		 
//		 Intent l1 = new Intent(getApplicationContext(), org.opencv.samples.facedetect.EasyOneCamera.class);
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
				 Intent l4 = new Intent(getApplicationContext(), org.opencv.samples.facedetect.FdActivity.class);
		  	  startActivity(l4);
			 }
			 
			 
			 
		 
		 
	 
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
	 
}
