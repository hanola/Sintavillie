/*
 * Copyright (C) 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.olav.samples.facedetect;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import no.olav.samples.facedetect.IntroActivity.DoSetPOST;

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
import org.opencv.samples.facedetect.FdActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.achievement.Achievement;
import com.google.android.gms.games.achievement.AchievementBuffer;
import com.google.android.gms.games.achievement.Achievements;
import com.google.android.gms.games.achievement.Achievements.LoadAchievementsResult;
import com.google.example.games.basegameutils.BaseGameActivity;
import com.google.example.games.basegameutils.GameHelper;

/**
 * Our main activity for the game.
 *
 * IMPORTANT: Before attempting to run this sample, please change
 * the package name to your own package name (not com.android.*) and
 * replace the IDs on res/values/ids.xml by your own IDs (you must
 * create a game in the developer console to get those IDs).
 *
 * This is a very simple game where the user selects "easy mode" or
 * "hard mode" and then the "gameplay" consists of inputting the
 * desired score (0 to 9999). In easy mode, you get the score you
 * request; in hard mode, you get half.
 *
 * @author Bruno Oliveira
 */
public class MainActivity extends BaseGameActivity
        implements MainMenuFragment.Listener,
        GameplayFragment.Listener, WinFragment.Listener {

    // Fragments
    MainMenuFragment mMainMenuFragment;
    GameplayFragment mGameplayFragment;
    WinFragment mWinFragment;
    private CommentsDataSource datasource;
    // request codes we use when invoking an external activity
    final int RC_RESOLVE = 5000, RC_UNUSED = 5001;

    // tag for debug logging
    final boolean ENABLE_DEBUG = true;
    final String TAG = "TanC";

    // playing on hard mode?
    boolean mHardMode = false;
    boolean mFrenzyMode = false;
    int timeExpired;
    
    //testing Restfull database
    String firstName = "OlavErBest";
	String lastName = "BrennaHansen";
	String age = "43";
	String points = "4";
	
    private MenuItem               viewDb;
    private MenuItem               transfearDb;
    private MenuItem               mItemFace30;
    private MenuItem               mItemFace20;
    private MenuItem               mItemType;
    
    //playing in easy, hard or frenzy
    String mode = "null";

    // achievements and scores we're pending to push to the cloud
    // (waiting for the user to sign in, for instance)
    AccomplishmentsOutbox mOutbox = new AccomplishmentsOutbox();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        enableDebugLog(ENABLE_DEBUG, TAG);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initialize database for saving events and timestamp
        datasource = new CommentsDataSource(this);
	    datasource.open();
        
        Bundle extras = getIntent().getExtras();
		  if (extras != null) {
			   String datas= extras.getString("EXTRA_ID");
			   int mScore2 = extras.getInt("Game_Score");
			   int mScore = extras.getInt("Tot_Game_Score");
			   mode = extras.getString("game_mode");
			   timeExpired = extras.getInt("time_expired");
			   
			   Log.i("Score" , "Put String MainAct time long  "+datas);
			   Log.i("Score" , "Put int MainAct score   "+mScore);
			   Log.i("Score" , "TotGameScore MainAct   "+mScore);
			   Log.i("Score" , " gameMode MainAct   "+mode);
			   Log.i("Score" , "time expired   "+timeExpired);
			   
			   int requestedScore = 1;
			   if (mode!= null) {
				   Log.i("Score" , "AfterPut String MainAct time long  "+datas);
				   Log.i("Score" , "afterPut int MainAct score   "+mScore);
				   Log.i("Score" , "afterTotGameScore MainAct   "+mScore);
				   Log.i("Score" , "after gameMode MainAct   "+mode);
				   Log.i("Score" , "aftertime expired   "+timeExpired);
				   
				   //TODO send in putExtra form easy or hard
				   mHardMode = false;
				   
				
				   // check for achievements
			        checkForAchievements(requestedScore, mScore);
                   
			        //update leaderboards
			        updateLeaderboards(mScore, timeExpired );

			        // push those accomplishments to the cloud, if signed in
			        pushAccomplishments();
			   }
		  }
		  
        
        // create fragments
        mMainMenuFragment = new MainMenuFragment();
        mGameplayFragment = new GameplayFragment();
        mWinFragment = new WinFragment();

        // listen to fragment events
        mMainMenuFragment.setListener(this);
        mGameplayFragment.setListener(this);
        mWinFragment.setListener(this);

        // add initial fragment (welcome fragment)
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                mMainMenuFragment).commit();

        // IMPORTANT: if this Activity supported rotation, we'd have to be
        // more careful about adding the fragment, since the fragment would
        // already be there after rotation and trying to add it again would
        // result in overlapping fragments. But since we don't support rotation,
        // we don't deal with that for code simplicity.

        // load outbox from file
        mOutbox.loadLocal(this);
    }

    // Switch UI to the given fragment
    void switchToFragment(Fragment newFrag) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newFrag)
                .commit();
    }

    @Override
    public void onStartGameRequested(boolean hardMode) {
        startGame(hardMode);
    }

    @Override
    public void onShowAchievementsRequested() {
    	Comment comment3 = null;
		 String event = "Pressing Achivement button:";
	      String timeStamp = new SimpleDateFormat("ddMM_yyyy_HHmm_ss").format(Calendar.getInstance().getTime());
	      
	      //insert do web server
	      String age = "0";
	      String points = "0";
	      DoSetPOST mDoSetPOST = new DoSetPOST(MainActivity.this, event, timeStamp, age, points);
			mDoSetPOST.execute("");
	      
		comment3 = datasource.createComment(event + timeStamp);
		Log.i("TotScore" , "Pressing Achivement button:   "+timeStamp);
        if (isSignedIn()) {
            startActivityForResult(Games.Achievements.getAchievementsIntent(getApiClient()),
                    RC_UNUSED);
        } else {
            showAlert(getString(R.string.achievements_not_available));
        }
    }

    @Override
    public void onShowLeaderboardsRequested() {
    	Comment comment3 = null;
		 String event = "Pressing Leaderboard button:";
	      String timeStamp = new SimpleDateFormat("ddMM_yyyy_HHmm_ss").format(Calendar.getInstance().getTime());
	      
	      String age = "0";
	      String points = "0";
	      DoSetPOST mDoSetPOST = new DoSetPOST(MainActivity.this, event, timeStamp, age, points);
			mDoSetPOST.execute("");
	      
		comment3 = datasource.createComment(event + timeStamp);
		Log.i("TotScore" , "Pressing Leaderboard button:   "+timeStamp);
        if (isSignedIn()) {
            startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(getApiClient()),
                    RC_UNUSED);
        } else {
            showAlert(getString(R.string.leaderboards_not_available));
        }
    }

    /**
     * Start gameplay. This means updating some status variables and switching
     * to the "gameplay" screen (the screen where the user types the score they want).
     *
     * @param hardMode whether to start gameplay in "hard mode".
     */
    void startGame(boolean hardMode) {
    	Comment comment3 = null;
		 String event = "Pressing hardmode button:";
	      String timeStamp = new SimpleDateFormat("ddMM_yyyy_HHmm_ss").format(Calendar.getInstance().getTime());
	      
	      String age = "0";
	      String points = "0";
	      DoSetPOST mDoSetPOST = new DoSetPOST(MainActivity.this, event, timeStamp, age, points);
			mDoSetPOST.execute("");
	      
		comment3 = datasource.createComment(event + timeStamp);
		Log.i("TotScore" , "Pressing hardmode button:   "+timeStamp);
        mHardMode = hardMode;
        Intent l1 = new Intent(getApplicationContext(), org.opencv.samples.facedetect.FdActivity.class);
  	  startActivity(l1);
        //switchToFragment(mGameplayFragment);
    }

   
    /**
     * Checks that the developer (that's you!) read the instructions.
     *
     * IMPORTANT: a method like this SHOULD NOT EXIST in your production app!
     * It merely exists here to check that anyone running THIS PARTICULAR SAMPLE
     * did what they were supposed to in order for the sample to work.
     */
    boolean verifyPlaceholderIdsReplaced() {
        final boolean CHECK_PKGNAME = true; // set to false to disable check
                                            // (not recommended!)

        // Did the developer forget to change the package name?
        if (CHECK_PKGNAME && getPackageName().startsWith("com.google.example.")) {
            Log.e(TAG, "*** Sample setup problem: " +
                "package name cannot be com.google.example.*. Use your own " +
                "package name.");
            return false;
        }

        // Did the developer forget to replace a placeholder ID?
        int res_ids[] = new int[] {
                R.string.app_id, R.string.achievement_stjerneskudd,
                R.string.achievement_bored, R.string.achievement_entusiast,
                R.string.achievement_entusiast, R.string.achievement_komet,
                R.string.leaderboard_easy, R.string.leaderboard_hard
        };
        for (int i : res_ids) {
            if (getString(i).equalsIgnoreCase("ReplaceMe")) {
                Log.e(TAG, "*** Sample setup problem: You must replace all " +
                    "placeholder IDs in the ids.xml file by your project's IDs.");
                return false;
            }
        }
        return true;
    }

    
    
    @Override
    public void onEnteredScore(int requestedScore) {
        // Compute final score (in easy mode, it's the requested score; in hard mode, it's half)
        int finalScore = mHardMode ? requestedScore / 2 : requestedScore;

        mWinFragment.setFinalScore(finalScore);
        mWinFragment.setExplanation(mHardMode ? getString(R.string.hard_mode_explanation) :
                getString(R.string.easy_mode_explanation));

        // check for achievements
        checkForAchievements(requestedScore, finalScore);

        // update leaderboards
        //updateLeaderboards(finalScore);

        // push those accomplishments to the cloud, if signed in
        pushAccomplishments();

        // switch to the exciting "you won" screen
        //switchToFragment(mWinFragment);
    }

    // Checks if n is prime. We don't consider 0 and 1 to be prime.
    // This is not an implementation we are mathematically proud of, but it gets the job done.
    boolean isPrime(int n) {
        int i;
        if (n == 0 || n == 1) return false;
        for (i = 2; i <= n / 2; i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check for achievements and unlock the appropriate ones.
     *
     * @param requestedScore the score the user requested.
     * @param finalScore the score the user got.
     */
    void checkForAchievements(int requestedScore, int finalScore) {
        // Check if each condition is met; if so, unlock the corresponding
        // achievement.
        if (mode.contentEquals("hard") && timeExpired < 60) {
            mOutbox.mStjerneskuddAchivement = true;
            achievementToast(getString(R.string.achievement_stjerneskudd_toast_text));
        }
        if (mode.contentEquals("frenzy") && finalScore > 300) {
            mOutbox.mSprerGledeAchivement = true;
            achievementToast(getString(R.string.achievement_sprerglede_toast_text));
        }
        if (mode.contentEquals("easy") && finalScore >= 20) {
            mOutbox.mKometAchivement = true;
            achievementToast(getString(R.string.achievement_komet_toast_text));
        }
        if (mode.contentEquals("easy") && finalScore >= 15 ) {
            mOutbox.mPoengsamlerenAchivement = true;
            achievementToast(getString(R.string.achievement_poengsamleren_text));
        }
        mOutbox.mBoredSteps++;
        Log.i("Score", "BoredSteps   " +mOutbox.mBoredSteps);
    }

    void unlockAchievement(int achievementId, String fallbackString) {
        if (isSignedIn()) {
            Games.Achievements.unlock(getApiClient(), getString(achievementId));
        } else {
            Toast.makeText(this, getString(R.string.achievement) + ": " + fallbackString,
                    Toast.LENGTH_LONG).show();
        }
    }

    void achievementToast(String achievement) {
        // Only show toast if not signed in. If signed in, the standard Google Play
        // toasts will appear, so we don't need to show our own.
        if (!isSignedIn()) {
            Toast.makeText(this, getString(R.string.achievement) + ": " + achievement,
                    Toast.LENGTH_LONG).show();
        }
    }

    void pushAccomplishments() {
        if (!isSignedIn()) {
            // can't push to the cloud, so save locally
            mOutbox.saveLocal(this);
            return;
        }
        if (mOutbox.mStjerneskuddAchivement) {
            Games.Achievements.unlock(getApiClient(), getString(R.string.achievement_stjerneskudd));
            mOutbox.mPrimeAchievement = false;
        }
        if (mOutbox.mSprerGledeAchivement) {
            Games.Achievements.unlock(getApiClient(), getString(R.string.achievement_sprer_glede));
            mOutbox.mArrogantAchievement = false;
        }
        if (mOutbox.mKometAchivement) {
            Games.Achievements.unlock(getApiClient(), getString(R.string.achievement_komet));
            mOutbox.mHumbleAchievement = false;
        }
        if (mOutbox.mPoengsamlerenAchivement) {
            Games.Achievements.unlock(getApiClient(), getString(R.string.achievement_poengsamleren));
            
            mOutbox.mPoengsamlerenAchivement = false;
            
        }
        if (mOutbox.mBoredSteps > 0) {
            Games.Achievements.increment(getApiClient(), getString(R.string.achievement_entusiast), mOutbox.mBoredSteps);
            
            Games.Achievements.increment(getApiClient(), getString(R.string.achievement_bored),
                    mOutbox.mBoredSteps);
            
            
        }
        if (mOutbox.mEasyModeScore >= 0) {
            Games.Leaderboards.submitScore(getApiClient(), getString(R.string.leaderboard_easy),
                    mOutbox.mEasyModeScore);
            mOutbox.mEasyModeScore = -1;
            
        }
        if (mOutbox.mHardModeScore >= 0) {
            Games.Leaderboards.submitScore(getApiClient(), getString(R.string.leaderboard_hard),
                    mOutbox.mHardModeScore);
            mOutbox.mHardModeScore = -1;
        }
        
        if (mOutbox.mFrenzyScore >= 0) {
            Games.Leaderboards.submitScore(getApiClient(), getString(R.string.leaderboard_super),
                    mOutbox.mFrenzyScore);
            mOutbox.mFrenzyScore = -1;
        }
        
        mOutbox.saveLocal(this);
    }

    /**
     * Update leaderboards with the user's score.
     *
     * @param finalScore The score the user got.
     */
    void updateLeaderboards(int finalScore, int time) {
    	
    	Log.i("Score", "in update leaderboard" +finalScore);
    	
    	if (mode.contentEquals("frenzy") && mOutbox.mFrenzyScore < finalScore) {
    			mOutbox.mFrenzyScore = finalScore;
    			Log.i("Score", "in update frenzy" +finalScore);
    	}
    	
        if (mode.contentEquals("hard") && mOutbox.mHardModeScore < time) {
            mOutbox.mHardModeScore = time;
            Log.i("Score", "in update hard" +finalScore);
        } 
        
        if (mode.contentEquals("easy") && mOutbox.mEasyModeScore < finalScore) {
            mOutbox.mEasyModeScore = finalScore;
            Log.i("Score", "in update easy" +finalScore);
        }
        
        
        
        
        
        
     /*   if (mode == "hard" & mOutbox.mHardModeScore < finalScore) {
            mOutbox.mHardModeScore = finalScore;
        }
        if (mode == "easy" & mOutbox.mEasyModeScore < finalScore) {
            mOutbox.mEasyModeScore = finalScore;
        }
        if (mode == "frenzy" & mOutbox.mFrenzyScore < finalScore) {
            mOutbox.mFrenzyScore = finalScore;
        }*/
    }

    @Override
    public void onWinScreenDismissed() {
        switchToFragment(mMainMenuFragment);
    }

    @Override
    public void onSignInFailed() {
        // Sign-in failed, so show sign-in button on main menu
        mMainMenuFragment.setGreeting(getString(R.string.signed_out_greeting));
        mMainMenuFragment.setShowSignInButton(true);
        mWinFragment.setShowSignInButton(true);
    }

    @Override
    public void onSignInSucceeded() {
        // Show sign-out button on main menu
        mMainMenuFragment.setShowSignInButton(false);

        // Show "you are signed in" message on win screen, with no sign in button.
        mWinFragment.setShowSignInButton(false);

        // Set the greeting appropriately on main menu
        Player p = Games.Players.getCurrentPlayer(getApiClient());
        String displayName;
        if (p == null) {
            Log.w(TAG, "mGamesClient.getCurrentPlayer() is NULL!");
            displayName = "???";
        } else {
            displayName = p.getDisplayName();
        }
        mMainMenuFragment.setGreeting("Hello, " + displayName);


        // if we have accomplishments to push, push them
        if (!mOutbox.isEmpty()) {
            pushAccomplishments();
            Toast.makeText(this, getString(R.string.your_progress_will_be_uploaded),
                    Toast.LENGTH_LONG).show();
        }
       
        /*runOnUiThread(new Runnable() {

			public void run() {

			 // Toast.makeText(getApplicationContext(), "running loadachivemtns", Toast.LENGTH_SHORT).show();
			  loadAchievements();
			   }
			});*/
    }

    @Override
    public void onSignInButtonClicked() {
        // check if developer read the documentation!
        // (Note: in a production application, this code should NOT exist)
        if (!verifyPlaceholderIdsReplaced()) {
            showAlert("Sample not set up correctly. See README.");
            return;
        }

        // start the sign-in flow
        beginUserInitiatedSignIn();
    }

    @Override
    public void onSignOutButtonClicked() {
        signOut();
        mMainMenuFragment.setGreeting(getString(R.string.signed_out_greeting));
        mMainMenuFragment.setShowSignInButton(true);
        mWinFragment.setShowSignInButton(true);
    }

    class AccomplishmentsOutbox {
        boolean mPrimeAchievement = false;
        boolean mHumbleAchievement = false;
        boolean mLeetAchievement = false;
        boolean mArrogantAchievement = false;
        boolean mPoengsamlerenAchivement = false;
        boolean mStjerneskuddAchivement = false;
        boolean mSprerGledeAchivement = false;
        boolean mKometAchivement = false;
        
        
        int mBoredSteps = 0;
        int mEasyModeScore = -1;
        int mHardModeScore = -1;
        int mFrenzyScore   = -1;

        boolean isEmpty() {
            return !mPrimeAchievement && !mHumbleAchievement & !mStjerneskuddAchivement && !mPoengsamlerenAchivement &&
                    !mSprerGledeAchivement && !mKometAchivement && !mArrogantAchievement && mBoredSteps == 0 && mEasyModeScore < 0 &&
                    mHardModeScore < 0 && mFrenzyScore <0;
        }

        public void saveLocal(Context ctx) {
            /* TODO: This is left as an exercise. To make it more difficult to cheat,
             * this data should be stored in an encrypted file! And remember not to
             * expose your encryption key (obfuscate it by building it from bits and
             * pieces and/or XORing with another string, for instance). */
        }

        public void loadLocal(Context ctx) {
            /* TODO: This is left as an exercise. Write code here that loads data
             * from the file you wrote in saveLocal(). */
        }
    }

    @Override
    public void onWinScreenSignInClicked() {
        beginUserInitiatedSignIn();
    }
    
    public void startFaceDetection(){
    	Comment comment3 = null;
		 String event = "Pressing easy mode button:";
	      String timeStamp = new SimpleDateFormat("ddMM_yyyy_HHmm_ss").format(Calendar.getInstance().getTime());
	      
	      String age = "0";
	      String points = "0";
	      DoSetPOST mDoSetPOST = new DoSetPOST(MainActivity.this, event, timeStamp, age, points);
			mDoSetPOST.execute("");
	      
		comment3 = datasource.createComment(event + timeStamp);
		Log.i("TotScore" , "Pressing easy mode button:   "+timeStamp);
    	Intent l1 = new Intent(getApplicationContext(), org.opencv.samples.facedetect.EasyOneCamera.class);
    	  startActivity(l1);
   }
    
    public void startTimeDetection(){
    	Comment comment3 = null;
		 String event = "Pressing hard mode button:";
	      String timeStamp = new SimpleDateFormat("ddMM_yyyy_HHmm_ss").format(Calendar.getInstance().getTime());
	      
	      String age = "0";
	      String points = "0";
	      DoSetPOST mDoSetPOST = new DoSetPOST(MainActivity.this, event, timeStamp, age, points);
			mDoSetPOST.execute("");
	      
		comment3 = datasource.createComment(event + timeStamp);
		Log.i("TotScore" , "Pressing hard mode button:   "+timeStamp);
      	 Intent l1 = new Intent(getApplicationContext(), org.opencv.samples.facedetect.FdActivity.class);
       	  startActivity(l1);
      }
    
    public void startTutorial(){
    	Comment comment3 = null;
		 String event = "Pressing Super mode button:";
	      String timeStamp = new SimpleDateFormat("ddMM_yyyy_HHmm_ss").format(Calendar.getInstance().getTime());
	      
	      String age = "0";
	      String points = "0";
	      DoSetPOST mDoSetPOST = new DoSetPOST(MainActivity.this, event, timeStamp, age, points);
			mDoSetPOST.execute("");
	      
		comment3 = datasource.createComment(event + timeStamp);
		Log.i("TotScore" , "Pressing Super mode button:   "+timeStamp);
     	
		
		
		Intent l1 = new Intent(getApplicationContext(), org.opencv.samples.facedetect.FrenzyActivity.class);
      	  startActivity(l1);
				
      	 
     }
    
    public void startDb()  {
    	
    	Intent intentLevels = new Intent(this, no.olav.samples.facedetect.GetDb.class);
		startActivity(intentLevels);
    }
    	
    	
    public void loadAchievements()  {
    	   GameHelper mHelper = null;        // GameHelper should be accessible for this to work

    	   boolean fullLoad = false;  // set to 'true' to reload all achievements (ignoring cache)
    	   long waitTime = 60;    // seconds to wait for achievements to load before timing out

    	
    	}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "called onCreateOptionsMenu");
        viewDb = menu.add("View DB");
        transfearDb = menu.add("Tranfear DB");
      
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);
        if (item == viewDb)
        	startDb();
            
        else if (item == transfearDb)
            startDb();
      
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
			    MainActivity.this.runOnUiThread(new Runnable() {
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
