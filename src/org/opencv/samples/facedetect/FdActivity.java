package org.opencv.samples.facedetect;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import no.olav.samples.facedetect.Comment;
import no.olav.samples.facedetect.CommentsDataSource;
import no.olav.samples.facedetect.R;
import no.olav.samples.facedetect.WinFragment;
import no.olav.samples.facedetect.WinnerActivity;

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
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class FdActivity extends FragmentActivity implements CvCameraViewListener2 {

    private static final String    TAG                 = "OCVSample::Activity";
    private static final Scalar    FACE_RECT_COLOR     = new Scalar(0, 255, 0, 255);
    private static final Scalar   EYE_RECT_COLOR       = new Scalar(255,0, 0, 255);
    public static final int        JAVA_DETECTOR       = 0;
    public static final int        NATIVE_DETECTOR     = 1;
    WinFragment                    WinnerFragment;     
    
    private MenuItem               mItemFace50;
    private MenuItem               mItemFace40;
    private MenuItem               mItemFace30;
    private MenuItem               mItemFace20;
    private MenuItem               mItemType;
    private Mat					   ROI;
    private Mat                    mRgba;
    private Mat                    mGray;
    private File                   mCascadeFile, mEyeFile, mSmileFile;
    private CascadeClassifier      mJavaDetector;
    private CascadeClassifier 	   mSmileDetector;
    private CascadeClassifier 	   mEyeDetector;
    private DetectionBasedTracker  mNativeDetector;

    private int                    mDetectorType       = JAVA_DETECTOR;
    private String[]               mDetectorName;
    private long 				   startTime		   =System.currentTimeMillis();;
    private long 				   stopTime		   =System.currentTimeMillis();;
    
    private float                  mRelativeFaceSize   = 0.2f;
    private int                    mAbsoluteFaceSize   = 0;
    private Rect 				   mouth = new Rect();
    private CameraBridgeViewBase   mOpenCvCameraView;
    private int                    gameScore           =0;
    private int                    TotGameScore           =0;
    String mode                                     = "hard";
    int countDown;
    int countDownTimer;
    private Button 							learnbutton;
    long FstartTime = System.currentTimeMillis();
	long estimatedTime;
	private CommentsDataSource datasource;

    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");

                    // Load native library after(!) OpenCV initialization
                    System.loadLibrary("detection_based_tracker");

                    try {
                       
                        
                    	InputStream is = getResources().openRawResource(R.raw.haarcascade_frontalface_alt);
                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                        mCascadeFile = new File(cascadeDir, "haarcascade_frontalface_alt.xml");            
                        FileOutputStream os = new FileOutputStream(mCascadeFile);
                        
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                        is.close();
                        os.close();
                       
                        mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                        if (mJavaDetector.empty()) {
                            Log.e(TAG, "Failed to load face cascade classifier");
                            mJavaDetector = null;
                        } else
                        {
                            Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());
                        }

                    	
                    	
                        // --------------------------------- load smile classificator ------------------------------------
                           InputStream smile = getResources().openRawResource(R.raw.haarcascade_smile);
                           File smileDir = getDir("cascadesmile", Context.MODE_PRIVATE);
                           mSmileFile = new File(smileDir, "haarcascade_smile.xml");
                           FileOutputStream oses = new FileOutputStream(mSmileFile);

                           byte[] bufferES = new byte[4096];
                           int bytesReadES;
                           while ((bytesReadES = smile.read(bufferES)) != -1) {
                               oses.write(bufferES, 0, bytesReadES);
                           }
                           smile.close();
                           oses.close();
                           
                           mSmileDetector = new CascadeClassifier(mSmileFile.getAbsolutePath());
                           
                           if (mSmileDetector.empty()) {
                               Log.e(TAG, "Failed to load smile cascade classifier");
                               mSmileDetector = null;
                           } else
                           {
                               Log.i(TAG, "Loaded smile cascade classifier from " + mSmileFile.getAbsolutePath());
                           }
                           // ------------------------------------------------------------------------------------------------------
                        
                           
                           // ------------------------------------------------------------------------------------------------------
                           
                           // --------------------------------- load eye classificator ------------------------------------
                           InputStream eye=getResources().openRawResource(R.raw.haarcascade_eye_tree_eyeglasses);
                           File eyeDir=getDir("cascadeeye", Context.MODE_PRIVATE);
                           mEyeFile=new File(eyeDir, "haarcascade_eye_tree_eyeglasses.xml");
                           FileOutputStream os1 = new FileOutputStream(mEyeFile);
                          
                           byte[] buffer1 = new byte[4096];
                           int bytesRead1;
                           while ((bytesRead1 = eye.read(buffer1)) != -1) {
                               os1.write(buffer1, 0, bytesRead1);
                           }
                           eye.close();
                           os1.close();
                           
                           mEyeDetector = new CascadeClassifier(mEyeFile.getAbsolutePath());
                           if (mEyeDetector.empty()) {
                               Log.e(TAG, "Failed to load eye cascade classifier");
                               mEyeDetector = null;
                           } else
                           {
                               Log.i(TAG, "Loaded eye cascade classifier from " + mEyeFile.getAbsolutePath());
                           }
                              // ------------------------------------------------------------------------------------------------------
                           
                       
                        
                        
                        
                     

                        mNativeDetector = new DetectionBasedTracker(mCascadeFile.getAbsolutePath(), 0);

                        cascadeDir.delete();
                        smileDir.delete();
                        eyeDir.delete();

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
                       
                    }

                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public FdActivity() {
        mDetectorName = new String[2];
        mDetectorName[JAVA_DETECTOR] = "Java";
        mDetectorName[NATIVE_DETECTOR] = "Native (tracking)";

        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.face_detect_surface_view);

        datasource = new CommentsDataSource(this);
	    datasource.open();
        
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.fd_activity_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);
       
       
    }

    @Override
    public void onPause()
    {
    	datasource.close();
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
    	datasource.open();
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }

    public void onDestroy() {
        super.onDestroy();
        mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();
        
        runOnUiThread(new Runnable() {

			public void run() {
                    openAlertStart();  
			   }
			});
        
        
    }

    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
    	
    	
    	
    	long timeMouth = System.currentTimeMillis();
    	estimatedTime = (timeMouth - FstartTime)/1000;
  
    	Log.i("TotTime", "time est  " +estimatedTime);
    	
    		
    	
    	
    	int timeMode = 2;
    	
    	if(estimatedTime == 30 & timeMode == 2){
    		Comment comment3 = null;
			 String event = "Time of 30 sek toast in hard mode  ";
		      String timeStamp = new SimpleDateFormat("ddMM_yyyy_HHmm_ss").format(Calendar.getInstance().getTime());
		      
		      String age="0";
		      String points="0";
	   		   DoSetPOST mDoSetPOST = new DoSetPOST(FdActivity.this, event, timeStamp, age, points);
				mDoSetPOST.execute("");
		      
			comment3 = datasource.createComment(event + timeStamp);
			Log.i("TotScore" , "Time of 30 sek toast in hard mode   "+timeStamp);
			
    		final long ToastTime = estimatedTime;
    		runOnUiThread(new Runnable() {
    			
    			public void run() {

    				String message = "Det har g�tt "; 
    				String message2 = " sekunder siden start! ";
    			    String timeMessage = message + Long.toString(ToastTime) + message2;
    			  Toast toast = Toast.makeText(getApplicationContext(),timeMessage, Toast.LENGTH_SHORT);
    		        toast.setGravity(Gravity.TOP, toast.getXOffset() / 2, toast.getYOffset() / 2);
    		        
    		        TextView textView = new TextView(getApplicationContext());
    		        textView.setBackgroundColor(Color.BLUE);
    		        textView.setTextColor(Color.RED);
    		        textView.setTextSize(20);
    		        Typeface typeface = Typeface.create("serif", Typeface.BOLD);
    		        textView.setTypeface(typeface);
    		        textView.setPadding(10, 10, 10, 10);
    		        textView.setText(timeMessage);

    		        toast.setView(textView);
    		        toast.show();
    			   }
    			});
    		
    	}
    			
    		if(estimatedTime == 120 & timeMode == 2){
        		
    			Comment comment3 = null;
   			 String event = "Time of 120 sek toast in hard mode  ";
   		      String timeStamp = new SimpleDateFormat("ddMM_yyyy_HHmm_ss").format(Calendar.getInstance().getTime());
   		      
   		   String age="0";
		      String points="0";
	   		   DoSetPOST mDoSetPOST = new DoSetPOST(FdActivity.this, event, timeStamp, age, points);
				mDoSetPOST.execute("");
   		      
   			comment3 = datasource.createComment(event + timeStamp);
   			Log.i("TotScore" , "Time of 120 sek toast in hard mode   "+timeStamp);
    			
        		final long ToastTime2 = estimatedTime;
        		runOnUiThread(new Runnable() {
        			
        			public void run() {
        				

        				String message = "2 min siden start!! "; 
        				
        				String message2 = " sekunder siden start! ";
        			    String timeMessage = message + Long.toString(ToastTime2) + message2;
        			    
        			    
        			  Toast toast = Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT);
        		        toast.setGravity(Gravity.TOP, toast.getXOffset() / 2, toast.getYOffset() / 2);
        		        
        		        TextView textView = new TextView(getApplicationContext());
        		        textView.setBackgroundColor(Color.BLUE);
        		        textView.setTextColor(Color.RED);
        		        textView.setTextSize(15);
        		        Typeface typeface = Typeface.create("serif", Typeface.BOLD);
        		        textView.setTypeface(typeface);
        		        textView.setPadding(10, 10, 10, 10);
        		        textView.setText(message);

        		        toast.setView(textView);
        		        toast.show();
        			   }
        			});
    		}
        		if(estimatedTime == 160 & timeMode == 2){
        			Comment comment3 = null;
          			 String event = "Time of 160 sek toast in hard mode  ";
          		      String timeStamp = new SimpleDateFormat("ddMM_yyyy_HHmm_ss").format(Calendar.getInstance().getTime());
          		      
          		    String age="0";
      		      String points="0";
      	   		   DoSetPOST mDoSetPOST = new DoSetPOST(FdActivity.this, event, timeStamp, age, points);
      				mDoSetPOST.execute("");
          		      
          			comment3 = datasource.createComment(event + timeStamp);
          			Log.i("TotScore" , "Time of 160 sek toast in hard mode   "+timeStamp);
            		
            		runOnUiThread(new Runnable() {
            			
            			public void run() {
            				

            				String message = "20 sekunder igjen!! "; 
            				
            				
            			  Toast toast = Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT);
            		        toast.setGravity(Gravity.TOP, toast.getXOffset() / 2, toast.getYOffset() / 2);
            		        
            		        TextView textView = new TextView(getApplicationContext());
            		        textView.setBackgroundColor(Color.BLUE);
            		        textView.setTextColor(Color.RED);
            		        textView.setTextSize(15);
            		        Typeface typeface = Typeface.create("serif", Typeface.BOLD);
            		        textView.setTypeface(typeface);
            		        textView.setPadding(10, 10, 10, 10);
            		        textView.setText(message);

            		        toast.setView(textView);
            		        toast.show();
            			   }
            			});
        		}
        		
        		    if(estimatedTime == 180 || TotGameScore == 50){
        		    	
        		    	Comment comment3 = null;
        	   			 String event = "Time of 180 sek toast in hard mode  ";
        	   		      String timeStamp = new SimpleDateFormat("ddMM_yyyy_HHmm_ss").format(Calendar.getInstance().getTime());
        	   		      
        	   		      
        	   		      
        	   			comment3 = datasource.createComment(event + timeStamp);
        	   			Log.i("TotScore" , "Time of 180 sek toast in hard mode   "+timeStamp);
                 	   
                 	   
                        // exit the app and go to the HOME
                 	   if (TotGameScore <50)
                 	   {
                 		   
                 		  Comment comment2 = null;
                			 String event2 = "Time out in hard mode LOOSE  ";
                		      String timeStamp2 = new SimpleDateFormat("ddMM_yyyy_HHmm_ss").format(Calendar.getInstance().getTime());
                		      
                		      String age="0";
                   		      String points="0";
                   	   		   DoSetPOST mDoSetPOST = new DoSetPOST(FdActivity.this, event2, timeStamp2, String.valueOf(estimatedTime), String.valueOf(TotGameScore/10));
                   				mDoSetPOST.execute("");
                		      
                		      
                			comment2 = datasource.createComment(event2 + timeStamp2 + TotGameScore/10);
                			Log.i("TotScore" , "Time out in hard mode LOOSE  "+timeStamp2 + TotGameScore/10);
                			
                 		  Intent looseAct = new Intent(getApplicationContext(), no.olav.samples.facedetect.LooserActivity.class);
                   	   
                   	   
                   	   
                   	looseAct.putExtra("EXTRA_ID", estimatedTime);
                   	Log.i("TotScore" , "estimated time    "+estimatedTime);
                   	looseAct.putExtra("Tot_Game_Score", TotGameScore/10);
                   	Log.i("TotScore" , "TotScore in nei button    "+TotGameScore);
                          startActivity(looseAct);
                 	   }
                 	   
                 	   else {
                 		   
                 		  Comment comment2 = null;
             			 String event2 = "Collected 5 faces hard mode WIN  ";
             		      String timeStamp2 = new SimpleDateFormat("ddMM_yyyy_HHmm_ss").format(Calendar.getInstance().getTime());
             		      
             		     String age="0";
           		      String points="0";
           	   		   DoSetPOST mDoSetPOST = new DoSetPOST(FdActivity.this, event, timeStamp, String.valueOf(estimatedTime), String.valueOf(TotGameScore/10));
           				mDoSetPOST.execute("");
             		      
             			comment2 = datasource.createComment(event2 + timeStamp2 + estimatedTime);
             			Log.i("TotScore" , "Collected 5 faces hard mode WIN  "+timeStamp2);
                 	   
                 	   Intent winAct = new Intent(getApplicationContext(),

                                WinnerActivity.class);
                 	   Log.i("TotScore" , "TotScore in nei button    "+TotGameScore);
                 	   Log.i("TotScore" , "estimated time    "+estimatedTime);
                 	   TotGameScore = 0;
                 	   
                 	  winAct.putExtra("EXTRA_ID", estimatedTime);
                 	  winAct.putExtra("Game_Score", gameScore);
                		winAct.putExtra("Tot_Game_Score", 5);
                		winAct.putExtra("game_mode", mode);
                		Log.i("TotScore" , "estimated time    "+estimatedTime);
                        startActivity(winAct);
        	
                 	   }
            		
        		
    			
    	}
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
            mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);
        }

        MatOfRect faces = new MatOfRect();
        MatOfRect eyes = new MatOfRect();
        MatOfRect mouth = new MatOfRect();

        if (mDetectorType == JAVA_DETECTOR)
        {
        	if (mJavaDetector != null)
                mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 2 // TODO: objdetect.CV_HAAR_SCALE_IMAGE
                        , new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
        }
        else if (mDetectorType == NATIVE_DETECTOR)
        {
        	if (mNativeDetector != null)
        		mNativeDetector.detect(mGray, faces);
        }
        else
        {
        	Log.e(TAG, "Detection method is not selected!");
        }
        
        Point point = new Point(20, 100);
        Point point2 = new Point(20, 200);
        Point point3 = new Point(20, 300);
        Point point4 = new Point(20, 400);
        countDown = 5 - (TotGameScore/10);
        countDownTimer = (int) (180 - estimatedTime);
        Core.putText(mRgba, "Tid  " +estimatedTime, point, 2, 3 , EYE_RECT_COLOR);
        Core.putText(mRgba, "Poeng  " +TotGameScore/10, point2, 2, 2 , EYE_RECT_COLOR);
        Core.putText(mRgba, "Smil igjen  " +countDown, point3, 2, 1 , EYE_RECT_COLOR);
        Core.putText(mRgba, "Tid igjen  " +countDownTimer, point4, 2, 1 , EYE_RECT_COLOR);
        
       
        
        
        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++)
            Core.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);
       
        if(facesArray.length>0)
        {
        	
        	
        	Rect roi = new Rect((int)facesArray[0].tl().x,(int)(facesArray[0].tl().y),facesArray[0].width,(int)(facesArray[0].height));
        	//testing smaller ROI
            Rect mouth2 = new Rect(facesArray[0].x + facesArray[0].width/12, (int)(facesArray[0].y+(facesArray[0].height/1.6)),facesArray[0].width - 2*facesArray[0].width/12,(int)(facesArray[0].height/3));
            
        	Mat cropped = new Mat();
        	
        	cropped = mGray.submat(mouth2);
        	
        	
        	
    	if (mSmileDetector != null)
    		mSmileDetector.detectMultiScale(cropped, mouth, 1.1,2,2,new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
    	else
    	Log.i("Fdvuew","mSmileDetector is NULL");
    	
    Rect[] mouthArray;
    mouthArray = mouth.toArray();
    Log.i("Fdvuew","Smile Count"+mouthArray.length);
    
    Point x2=new Point();
    //using opencv tutorials for circle, its working fine now.
    
    
    
    for (int j = 0; j < mouthArray.length; j++)
    {
    	
    	
      
    	x2.x=facesArray[0].x + mouthArray[j].x + mouthArray[j].width*0.5;
    	x2.y=facesArray[0].y + mouthArray[j].y + mouthArray[j].height*0.5;
    	int Radius=(int)((mouthArray[j].width + mouthArray[j].height)*0.25 );
    	
    	
    	gameScore = gameScore+1;
    	String Score = Integer.toString(gameScore);
    	Log.i("Score" , "String Score    "+Score);
    	Log.i("Score","Game Score  "+gameScore);
    	Core.putText(mRgba, "SMILE", x2, 2, 4 , FACE_RECT_COLOR);
    	
    	Log.i("TotScore" , "estimated time on smile  "+estimatedTime);
    	
    	if (gameScore == 1){
    		startTime = System.currentTimeMillis();
        	Log.i("timeScore" , "StartTime    "+startTime);
    	}
    	
    	
    	
    	
    	if (gameScore == 10){
    		
    		
    		
    		Log.i("TotScore" , "TotScore i if 15 before  "+TotGameScore);
    		Log.i("TotScore" , "Score i if 15   "+gameScore);
    		
    		
    		
    		TotGameScore = TotGameScore + gameScore;
    		Log.i("TotScore" , "TotScore i if 15 after   "+TotGameScore);
    		
    		Comment comment2 = null;
			 String event2 = "Collected a face hard mode  ";
		      String timeStamp2 = new SimpleDateFormat("ddMM_yyyy_HHmm_ss").format(Calendar.getInstance().getTime());
		      
		      String age="0";
   		      String points="0";
   	   		   DoSetPOST mDoSetPOST = new DoSetPOST(FdActivity.this, event2, timeStamp2, String.valueOf(estimatedTime), String.valueOf(TotGameScore/10));
   				mDoSetPOST.execute("");
		      
		      
			comment2 = datasource.createComment(event2 + timeStamp2 + TotGameScore/10);
			Log.i("TotScore" , "Collected 5 faces hard mode WIN  "+timeStamp2 + TotGameScore/10);
    		 
    		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    	       
    		 SharedPreferences.Editor editor = preferences.edit();
 	        editor.putString("Name", "Olav");
 	        editor.putInt("ScoreSmile", gameScore/10);
 	        editor.putInt("TotGameScore", TotGameScore/10);
 	        editor.apply();
    	        
    		gameScore = 0;
    		
    		runOnUiThread(new Runnable() {

    			public void run() {
                        openAlert();  
    			   }
    			});
    			
    			}
    	
    	}
        
        
   }
            
       
       
       
     
        
        return mRgba;
       
}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "called onCreateOptionsMenu");
        mItemFace50 = menu.add("Face size 50%");
        mItemFace40 = menu.add("Face size 40%");
        mItemFace30 = menu.add("Face size 30%");
        mItemFace20 = menu.add("Face size 20%");
        mItemType   = menu.add(mDetectorName[mDetectorType]);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);
        if (item == mItemFace50)
            setMinFaceSize(0.5f);
        else if (item == mItemFace40)
            setMinFaceSize(0.4f);
        else if (item == mItemFace30)
            setMinFaceSize(0.3f);
        else if (item == mItemFace20)
            setMinFaceSize(0.2f);
        else if (item == mItemType) {
            mDetectorType = (mDetectorType + 1) % mDetectorName.length;
            item.setTitle(mDetectorName[mDetectorType]);
            setDetectorType(mDetectorType);
        }
        return true;
    }

    private void setMinFaceSize(float faceSize) {
        mRelativeFaceSize = faceSize;
        mAbsoluteFaceSize = 0;
    }

    private void setDetectorType(int type) {
        if (mDetectorType != type) {
            mDetectorType = type;

            if (type == NATIVE_DETECTOR) {
                Log.i(TAG, "Detection Based Tracker enabled");
                mNativeDetector.start();
            } else {
                Log.i(TAG, "Cascade detector enabled");
                mNativeDetector.stop();
            }
        }
    }
    private void openAlert() {
    	
    	String event2 = "Dialig box collected smile hard mode OK  ";
	      String timeStamp2 = new SimpleDateFormat("ddMM_yyyy_HHmm_ss").format(Calendar.getInstance().getTime());
	      
	      DoSetPOST mDoSetPOST = new DoSetPOST(FdActivity.this, event2, timeStamp2, String.valueOf(estimatedTime), String.valueOf(TotGameScore/10));
				mDoSetPOST.execute("");
	      
	      
		Log.i("TotScore" , "Dialig box collected smile hard mode OK  "+timeStamp2 + TotGameScore/10);
		 
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FdActivity.this);

         

        alertDialogBuilder.setTitle(this.getTitle()+ "    Dine poeng  " +TotGameScore/10);

        alertDialogBuilder.setMessage("Bra jobbet");

        // set positive button: Yes message

        alertDialogBuilder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {

               public void onClick(DialogInterface dialog,int id) {

            	   //TotGameScore = TotGameScore + gameScore;
            	   Log.i("TotScore", "TotalGAmeScore in ja button   " +TotGameScore/10);
                   // go to a new activity of the app
            	   gameScore = 0;
            	  	dialog.dismiss();
            	   
            	   
            	 
               }

             });

      
        // set neutral button: Exit the app message

        alertDialogBuilder.setNeutralButton("Exit",new DialogInterface.OnClickListener() {

               public void onClick(DialogInterface dialog,int id) {
            	   
            	   String event2 = "Dialig box collected smile hard mode EXIT  ";
          	      String timeStamp2 = new SimpleDateFormat("ddMM_yyyy_HHmm_ss").format(Calendar.getInstance().getTime());
          	      
          	    DoSetPOST mDoSetPOST = new DoSetPOST(FdActivity.this, event2, timeStamp2, String.valueOf(estimatedTime), String.valueOf(TotGameScore/10));
     				mDoSetPOST.execute("");
  		      
          	      
          		Log.i("TotScore" , "Dialig box collected smile hard mode EXIT   "+timeStamp2 + TotGameScore/10);
            	   
            	   String Score = Integer.toString(gameScore);
            	   gameScore = 0;
            	 
                   // exit the app and go to the HOME
            	   Intent hovedmeny = new Intent(getApplicationContext(),

                           no.olav.samples.facedetect.MainActivity.class);
            	   Log.i("TotScore" , "TotScore in nei button    "+TotGameScore);
            	   
            	   TotGameScore = 0;
            	   
            	   hovedmeny.putExtra("EXTRA_ID", Score);
           		hovedmeny.putExtra("Game_Score", gameScore);
           		hovedmeny.putExtra("Tot_Game_Score", TotGameScore/10);
           		hovedmeny.putExtra("game_mode", mode);
                   startActivity(hovedmeny);
                  
                   
                   

               }

           });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();

   }

    private void openAlertStart() {
    	
    	 String event2 = "Start Dialig box hard mode  ";
  	      String timeStamp2 = new SimpleDateFormat("ddMM_yyyy_HHmm_ss").format(Calendar.getInstance().getTime());
  	      
  	    DoSetPOST mDoSetPOST = new DoSetPOST(FdActivity.this, event2, timeStamp2, String.valueOf(estimatedTime), String.valueOf(TotGameScore/10));
				mDoSetPOST.execute("");
	      
  	      
  		Log.i("TotScore" , "Start Dialig box hard mode   "+timeStamp2);
		 
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FdActivity.this);
        alertDialogBuilder.setTitle(this.getTitle()+ " valg");
        alertDialogBuilder.setMessage("Klar 5 smil p� 3 min...");
        
        
        
        // set neutral button: verify message
        alertDialogBuilder.setNeutralButton("Ok",new DialogInterface.OnClickListener() {
               
        	
        	
        	
        	public void onClick(DialogInterface dialog,int id) {
                   return;
                }
           });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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
			    FdActivity.this.runOnUiThread(new Runnable() {
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

