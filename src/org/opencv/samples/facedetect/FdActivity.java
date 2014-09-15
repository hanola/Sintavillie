package org.opencv.samples.facedetect;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

import no.olav.samples.facedetect.R;
import no.olav.samples.facedetect.WinFragment;
import no.olav.samples.facedetect.MainActivity;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.objdetect.CascadeClassifier;

import com.google.example.games.basegameutils.BaseGameActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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
    private Button 							learnbutton;

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
                        // load cascade file from application resources
                       /* InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                        mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
                        FileOutputStream os = new FileOutputStream(mCascadeFile);

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                        is.close();
                        os.close();
*/
                     // ------------------------------------------------------------------------------------------------------
                        
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

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.fd_activity_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);
        //learnbutton = new Button(getApplicationContext());
        //ArrayList<View> views = new ArrayList<View>();
        //views.add(findViewById(R.id.digit_button_0));
        //learnbutton=(Button)findViewById(R.id.digit_button_0); 
        /*mOpenCvCameraView.addTouchables(views);
        mOpenCvCameraView.enableFpsMeter();
        final Button button = (Button) findViewById(R.id.digit_button_0);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            }
        });*/
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
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
    }

    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
    	
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
        
        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++)
            //Core.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);
       
        if(facesArray.length>0)
        {
        	
        	//Rect roi = new Rect((int)facesArray[0].tl().x,(int)facesArray[0].tl().y,facesArray[0].width,facesArray[0].height);
        	//Rect roi = new Rect((int)facesArray[0].tl().x,(int)(facesArray[0].tl().y+facesArray[0].height/5),facesArray[0].width,(int)(facesArray[0].height/3));//imran
        	
        	
        	//making roi from face image
        	Rect roi = new Rect((int)facesArray[0].tl().x,(int)(facesArray[0].tl().y),facesArray[0].width,(int)(facesArray[0].height));//imran
        	 //inserts the rectangle ROI for mouth 
        	
        	//testing smaller ROI
            Rect mouth2 = new Rect(facesArray[0].x + facesArray[0].width/12, (int)(facesArray[0].y+(facesArray[0].height/1.6)),facesArray[0].width - 2*facesArray[0].width/12,(int)(facesArray[0].height/3));
            //Core.rectangle(mRgba, mouth2.tl(), mouth2.br(), new Scalar(255,0, 150, 150), 2);
            //--------------------------------------------
            
        	//taking inputs from nustrat opencv example
        	//imran check above, using tl of x and tl of y.other wise it will give runtime errors
        	Mat cropped = new Mat();
        	//cropped = mGray.submat(facesArray[0]);//imran yuppie!, this did the trick!...everything else was failing
        	//refer to opencv 2.4 tut pdf
        	//cropped = mGray.submat(roi);
        	cropped = mGray.submat(mouth2);
        	//cropped.copyTo(mGray.submat(roi));
        	
       /* 	
        	if (mEyeDetector != null)
        	mEyeDetector.detectMultiScale(cropped, eyes, 1.1,2,2,new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
        	else
        	Log.i("Fdvuew","mEyeDetector is NULL");
        	
        Rect[] eyesArray;
        eyesArray = eyes.toArray();
        Log.i("Fdvuew","Eyes Count"+eyesArray.length);
        Point x1=new Point();
        //using opencv tutorials for circle, its working fine now.
        
        for (int i = 0; i < eyesArray.length; i++)
        {
           
        	x1.x=facesArray[0].x + eyesArray[i].x + eyesArray[i].width*0.5;
        	x1.y=facesArray[0].y + eyesArray[i].y + eyesArray[i].height*0.5;
        	int Radius=(int)((eyesArray[i].width + eyesArray[i].height)*0.25 );
        	Core.circle(mRgba, x1, Radius, EYE_RECT_COLOR);
        	//x1.y=faces[i].y + eyes[j].y + eyes[j].height*0.5;
        	
        	// Core.rectangle(mRgba,eyesArray[i].tl(), eyesArray[i].br(), EYE_RECT_COLOR, 3);
        	//x1.x=eyesArray[i].tl().x + facesArray[0].width;
        	//x1.y=eyesArray[i].tl().y + facesArray[0].width;
        	//Core.rectangle(mRgba,x1, eyesArray[i].br(), EYE_RECT_COLOR, 3);
        }
        */
        	
        	
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
    	//Core.circle(mRgba, x2, Radius, EYE_RECT_COLOR);
    	//Core.putText(mRgba, "Smiling", x2, 2, 4 , EYE_RECT_COLOR);
    	//x1.y=faces[i].y + eyes[j].y + eyes[j].height*0.5;
    	
    	// Core.rectangle(mRgba,eyesArray[i].tl(), eyesArray[i].br(), EYE_RECT_COLOR, 3); TODO: check i or j variable in for 
    	//x1.x=eyesArray[i].tl().x + facesArray[0].width;
    	//x1.y=eyesArray[i].tl().y + facesArray[0].width;
    	//Core.rectangle(mRgba,x1, eyesArray[i].br(), EYE_RECT_COLOR, 3);
    	
    	gameScore = gameScore+1;
    	String Score = Integer.toString(gameScore);
    	Log.i("Score" , "String Score    "+Score);
    	Log.i("Score","Game Score  "+gameScore);
    	Core.putText(mRgba, "Score  " +Score, x2, 2, 4 , EYE_RECT_COLOR);
    	
    	
    	if (gameScore == 1){
    		startTime = System.currentTimeMillis();
        	Log.i("timeScore" , "StartTime    "+startTime);
    	}
    	
    	
    	//running in new thread to work
    	if (gameScore == 20){
    		runOnUiThread(new Runnable() {

    			public void run() {

    			  Toast.makeText(getApplicationContext(), "N� er vi igang!", Toast.LENGTH_LONG).show();

    			   }
    			});
    	}
    	
    	//running in new thread to work
    	if (gameScore == 50){
    		stopTime = System.currentTimeMillis();
        	Log.i("timeScore" , "StopTime    "+stopTime);
        	long sumTime = stopTime - startTime;
        	long secTime = sumTime /1000 % 60;
        	Log.i("timeScore" , "Time used    "+sumTime);
        	Log.i("timeScore" , "Time used sec    "+secTime);
        
    		runOnUiThread(new Runnable() {

    			public void run() {

    			  Toast.makeText(getApplicationContext(), "50 smil begynner � ligne p� noe!!!", Toast.LENGTH_LONG).show();

    			   }
    			});
    	}
    	
    	if (gameScore == 100){
    		runOnUiThread(new Runnable() {

    			public void run() {

    			  Toast.makeText(getApplicationContext(), "Bare 50 smil til:)", Toast.LENGTH_LONG).show();

    			   }
    			});
    	}
    	
    	if (gameScore == 15 ){
    		//WinnerFragment      =new WinFragment();
    		//getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, WinnerFragment ).commit();
    		 Log.i("Score" , "Put intent FdActivity");
    		
    		Intent l1 = new Intent(getApplicationContext(), no.olav.samples.facedetect.WinnerActivity.class);
    		l1.putExtra("EXTRA_ID", Score);
    		l1.putExtra("Game_Score", gameScore);
    	  	  startActivity(l1);
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

	
}

/*
//Smile Detector classifier load TODO:make ROI so the detector is more efficient
if (mDetectorType == JAVA_DETECTOR) {
    if (mSmileDetector!= null)
    	mSmileDetector.detectMultiScale(mGray, faces, 1.1, 2, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
                new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
    
    //inserted from fdview ----------------------------------------------------------
    
    Rect[] facesArray = faces.toArray();
    for (int i = 0; i < facesArray.length; i++){
     	Rect r = facesArray[i];
         Core.rectangle(mGray, r.tl(), r.br(), new Scalar(0, 255, 0, 255), 3);
         Core.rectangle(mRgba, r.tl(), r.br(), new Scalar(0, 255, 0, 255), 3);
    
    //inserts the rectangle ROI for mouth 
    mouth = new Rect(r.x + r.width/12, (int)(r.y+(r.height/1.6)),r.width - 2*r.width/12,(int)(r.height/3));
    Core.rectangle(mRgba, mouth.tl(), mouth.br(), new Scalar(255,0, 150, 150), 2);
    //--------------------------------------------


*/