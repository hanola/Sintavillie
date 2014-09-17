package no.olav.samples.facedetect;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameActivity;

import no.olav.samples.facedetect.MainActivity.*;
import no.olav.samples.facedetect.WinFragment.Listener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class WinnerActivity extends BaseGameActivity implements OnClickListener{
	String mExplanation = "Testing ";
    int mScore = 100;
    String datas;
    boolean mShowSignIn = false;
    private static final int REQUEST_CODE = 10;
    static CountDownTimer myTimer =null;    
    private Button mainBtn;

    
    public interface Listener {
        public void onWinScreenDismissed();
        public void onWinScreenSignInClicked();
    }

    Listener mListener = null;

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
			   datas= extras.getString("EXTRA_ID");
			   mScore = extras.getInt("Game_Score");
			   if (datas!= null) {
				   Log.i("Score" , "Put String WinAct   "+datas);
				   Log.i("Score" , "Put int WinAct   "+mScore);
				   
			   }
		  }
		
		  SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		  String name = preferences.getString("Name","");
		  int scoreSmile = preferences.getInt("ScoreSmile", mScore);
		  Log.i("Score", "SharedPref winner  "+name);
		  Log.i("Score", "SharedPref scoreSmile   "+scoreSmile);
		  
		  
		  
        updateUi();
        
	}

	public void setFinalScore(int i) {
        mScore = i;
    }

    public void setExplanation(String s) {
        mExplanation = s;
    }

    public void setListener(Listener l) {
        mListener = l;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateUi();
    }

    void updateUi() {
    	
       
        TextView explainTv = (TextView)findViewById(R.id.scoreblurb);

       // if (scoreTv != null) scoreTv.setText(String.valueOf(mScore));
        if (explainTv != null) explainTv.setText(mExplanation);
       
        
        new CountDownTimer(30000, 1000) {
        	TextView scoreTv = (TextView)findViewById(R.id.score_display);
		     public void onTick(long millisUntilFinished) {
		    	
		      scoreTv.setText("seconds remaining: " + millisUntilFinished / 1000);
		      
		      
		     }

		     public void onFinish() {
		         scoreTv.setText("done!");
		     }
		  }.start();
		  
		  
		  
		
        
        //SignInButton SignIn = (SignInButton)findViewById(R.id.win_screen_sign_in_bar);
       
        //getActivity().findViewById(R.id.win_screen_sign_in_bar).setVisibility(
          //      mShowSignIn ? View.VISIBLE : View.GONE);
        //getActivity().findViewById(R.id.win_screen_signed_in_bar).setVisibility(
          //      mShowSignIn ? View.GONE : View.VISIBLE);
        
      /*          mainBtn = (Button) findViewById(R.id.button);  
                mainBtn.setOnClickListener(new OnClickListener() {
       @Override
    public void onClick(View v) {
             openAlert(v);
             }
          });*/


        //openAlert();
        countDown();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.win_screen_sign_in_button) {
            mListener.onWinScreenSignInClicked();
        }
        mListener.onWinScreenDismissed();
    }

    public void setShowSignInButton(boolean showSignIn) {
        mShowSignIn = showSignIn;
        updateUi();
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
		 startActivity(l1);
		 
	 
	 
	 }
	 
	 public void SendLeaderboard(View view) {
		 //Intent l1 = new Intent(getApplicationContext(), no.olav.samples.facedetect.MainActivity.class);
	  	 // startActivity(l1);
		 Games.Leaderboards.submitScore(getApiClient(), "CgkIwu3Qp4kGEAIQBw", 1337);
	MainActivity mAct = new MainActivity();
	mAct.updateLeaderboards(100000);
	 
	 }
	 
	 private void openAlert() {
		 
		          AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(WinnerActivity.this);
		 
		           
		 
		          alertDialogBuilder.setTitle(this.getTitle()+ " decision");
		 
		          alertDialogBuilder.setMessage("Are you sure?");
		 
		          // set positive button: Yes message
		 
		          alertDialogBuilder.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
		 
		                 public void onClick(DialogInterface dialog,int id) {
		 
		                     // go to a new activity of the app
		 
		                     Intent positveActivity = new Intent(getApplicationContext(),
		 
		                             no.olav.samples.facedetect.MainActivity.class);
		 
		                     startActivity(positveActivity);
		 
		                 }
		 
		               });
		 
		          // set negative button: No message
		 
		          alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
		 
		                 public void onClick(DialogInterface dialog,int id) {
		
		                     // cancel the alert box and put a Toast to the user
		 
		                     dialog.cancel();
		 
		                    
		 
		                 }
		
		             });
		 
		          // set neutral button: Exit the app message
		
		          alertDialogBuilder.setNeutralButton("Exit the app",new DialogInterface.OnClickListener() {
		
		                 public void onClick(DialogInterface dialog,int id) {
		
		                     // exit the app and go to the HOME
		 
		                     WinnerActivity.this.finish();
		
		                 }
		
		             });
		
		          AlertDialog alertDialog = alertDialogBuilder.create();
		
		          alertDialog.show();
		
		     }
	 public void countDown (){
		 final AlertDialog.Builder alertDialog = new AlertDialog.Builder(WinnerActivity.this);
	 alertDialog.setTitle("Alert 3");  
	 alertDialog.setMessage("00:10");
	 alertDialog.show();   // 

	 new CountDownTimer(10000, 1000) {
	     @Override
	     public void onTick(long millisUntilFinished) {
	        alertDialog.setMessage("00:"+ (millisUntilFinished/1000));
	     }

	     @Override
	     public void onFinish() {
	        
	     }
	 }.start();
	 }
		
		 }

	 
	 
