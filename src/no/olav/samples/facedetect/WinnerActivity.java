package no.olav.samples.facedetect;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameActivity;

import no.olav.samples.facedetect.MainActivity.*;
import no.olav.samples.facedetect.WinFragment.Listener;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
		  Log.i("Score", "SharedPref   "+name);
		  
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
    	
        TextView scoreTv = (TextView)findViewById(R.id.score_display);
        TextView explainTv = (TextView)findViewById(R.id.scoreblurb);

        if (scoreTv != null) scoreTv.setText(String.valueOf(mScore));
        if (explainTv != null) explainTv.setText(mExplanation);
        //SignInButton SignIn = (SignInButton)findViewById(R.id.win_screen_sign_in_bar);
       
        //getActivity().findViewById(R.id.win_screen_sign_in_bar).setVisibility(
          //      mShowSignIn ? View.VISIBLE : View.GONE);
        //getActivity().findViewById(R.id.win_screen_signed_in_bar).setVisibility(
          //      mShowSignIn ? View.GONE : View.VISIBLE);
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
	 
	 
	 
	 
}