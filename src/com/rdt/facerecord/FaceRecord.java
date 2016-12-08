package com.rdt.facerecord;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import com.rdt.facerecord.R;

import android.app.Activity;
import android.content.ContentValues;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Face;
import android.hardware.Camera.FaceDetectionListener;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class FaceRecord extends Activity implements SurfaceHolder.Callback{

	private InterstitialAd interstitial;
	public void displayInterstitial() {
		if (interstitial.isLoaded()) {
			interstitial.show();
		}
	}
	
 Camera camera;
 SurfaceView surfaceView;
 SurfaceHolder surfaceHolder;
 boolean previewing = false;
 LayoutInflater controlInflater = null;
 
 Button buttonTakePicture;
 TextView prompt, prompt2, prompt3, prompt4, prompt5;
 
 final int RESULT_SAVEIMAGE = 0;
 
 
 
   /** Called when the activity is first created. */
   @Override
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.face);
       
       setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
      
       getWindow().setFormat(PixelFormat.UNKNOWN);
       surfaceView = (SurfaceView)findViewById(R.id.camerapreview);
       surfaceHolder = surfaceView.getHolder();
       surfaceHolder.addCallback(this);
       surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
      
       controlInflater = LayoutInflater.from(getBaseContext());
       View viewControl = controlInflater.inflate(R.layout.control, null);
       LayoutParams layoutParamsControl
        = new LayoutParams(LayoutParams.WRAP_CONTENT,
        LayoutParams.WRAP_CONTENT);
       this.addContentView(viewControl, layoutParamsControl);
       
       interstitial = new InterstitialAd(this);
		interstitial.setAdUnitId("");
		AdRequest adRequest = new AdRequest.Builder().build();
		interstitial.loadAd(adRequest);
		if (!interstitial.isLoaded()) {
			Log.d("#", "Load ad error!");
		} else {
			Log.d("#", "Load ad ok!");
		}
		interstitial.setAdListener(new AdListener() {
			public void onAdLoaded() { displayInterstitial(); }
		});
		interstitial.show();
       
       buttonTakePicture = (Button)findViewById(R.id.takepicture);
       buttonTakePicture.setOnClickListener(new Button.OnClickListener(){

   @Override
   public void onClick(View arg0) {
    // TODO Auto-generated method stub
    camera.takePicture(myShutterCallback,
      myPictureCallback_RAW, myPictureCallback_JPG);
   }});
      
       LinearLayout layoutBackground = (LinearLayout)findViewById(R.id.background);
       layoutBackground.setOnClickListener(new LinearLayout.OnClickListener(){

   @Override
   public void onClick(View arg0) {
    // TODO Auto-generated method stub

    buttonTakePicture.setEnabled(false);
    camera.autoFocus(myAutoFocusCallback);
   }});
      
       prompt = (TextView)findViewById(R.id.prompt);
       prompt2 = (TextView)findViewById(R.id.prompt2);
       prompt3 = (TextView)findViewById(R.id.prompt3);
       prompt4 = (TextView)findViewById(R.id.prompt4);
       prompt5 = (TextView)findViewById(R.id.prompt5);
       
       
   }
  
   FaceDetectionListener faceDetectionListener
   = new FaceDetectionListener(){

  @Override
  public void onFaceDetection(Face[] faces, Camera camera) {
	  
	  prompt2.setText(String.valueOf(
		      "Max Face: " + camera.getParameters().getMaxNumDetectedFaces()));
   
   if (faces.length == 0){
	   
    prompt.setText(" No Face Detected! ");
    prompt3.setText(" none ");
    prompt4.setText(" none ");
    prompt5.setText(" none ");
    
   }else{
    prompt.setText(String.valueOf(faces.length) + " Face Detected");
    
    double leftEye_X = faces[0].leftEye.x;

    double leftEye_Y = faces[0].leftEye.y;

    double rightEye_X = faces[0].rightEye.x;

    double rightEye_Y = faces[0].rightEye.y;

    double distance_eyes_square = Math.pow((leftEye_X - rightEye_X),2) + Math.pow((leftEye_Y - rightEye_Y),2);
    
    double eyesDistance = faces[0].rect.describeContents();
    double eyesDistanceccx = faces[0].rect.exactCenterX();
    double eyesDistanceccy = faces[0].rect.exactCenterY();
    double eyesDistanceh = faces[0].rect.height();
    double eyesDistancew = faces[0].rect.width();
    double eyesDistancecx = faces[0].rect.centerX();
    double eyesDistancecy = faces[0].rect.centerY();
    double eyesDistanceb = faces[0].rect.bottom;
    double eyesDistancet = faces[0].rect.top;
    double eyesDistancel = faces[0].rect.left;
    double eyesDistancer = faces[0].rect.right;
    
    prompt3.setText(String.valueOf(
		      "Values EYE : L-E_X["+ leftEye_X + "]L-E_Y[" + leftEye_Y +"]-R-E_X["+ rightEye_X + "]R-E_Y[" + rightEye_Y +"]"));
    
    prompt4.setText(String.valueOf(
		      "Values ALL : "
		      + "["+ distance_eyes_square + "]"
		      + "["+ eyesDistance + "]"
		      + "["+ eyesDistanceccx + "]"
		      + "["+ eyesDistanceccy + "]"
		      + "["+ eyesDistanceh + "]"
		      + "["+ eyesDistancew + "]"
		      + "["+ eyesDistancecx + "]"
		      + "["+ eyesDistancecy + "]"
		      + "["+ eyesDistanceb + "]"
		      + "["+ eyesDistancet + "]"
		      + "["+ eyesDistancel + "]"
		      + "["+ eyesDistancer + "]"));
    //=======================================================================
    
   }
   
  }};
  
   AutoFocusCallback myAutoFocusCallback = new AutoFocusCallback(){

  @Override
  public void onAutoFocus(boolean arg0, Camera arg1) {
   // TODO Auto-generated method stub
   buttonTakePicture.setEnabled(true);
  }};
  
   ShutterCallback myShutterCallback = new ShutterCallback(){

  @Override
  public void onShutter() {
   // TODO Auto-generated method stub
   
  }};
  
 PictureCallback myPictureCallback_RAW = new PictureCallback(){

  @Override
  public void onPictureTaken(byte[] arg0, Camera arg1) {
   // TODO Auto-generated method stub
   
  }};
  
 PictureCallback myPictureCallback_JPG = new PictureCallback(){

  @Override
  public void onPictureTaken(byte[] arg0, Camera arg1) {
   // TODO Auto-generated method stub
   /*Bitmap bitmapPicture
    = BitmapFactory.decodeByteArray(arg0, 0, arg0.length); */
   
   Uri uriTarget = getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, new ContentValues());

   OutputStream imageFileOS;
   try {
    imageFileOS = getContentResolver().openOutputStream(uriTarget);
    imageFileOS.write(arg0);
    imageFileOS.flush();
    imageFileOS.close();
    
    prompt.setText("Image saved: " + uriTarget.toString());
    
   } catch (FileNotFoundException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
   } catch (IOException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
   }

   camera.setDisplayOrientation(0);
   camera.startPreview();
   camera.startFaceDetection();
   
  }};

 @Override
 public void surfaceChanged(SurfaceHolder holder, int format, int width,
   int height) {
  // TODO Auto-generated method stub
  if(previewing){
   camera.stopFaceDetection();
   camera.stopPreview();
   previewing = false;
  }
  
  if (camera != null){
   try {
    camera.setPreviewDisplay(surfaceHolder);
    camera.startPreview();

    prompt.setText(String.valueOf("Max Face: " + camera.getParameters().getMaxNumDetectedFaces()));
    camera.startFaceDetection();
    previewing = true;
   } catch (IOException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
   }
  }
 }
 
 

 @Override
 public void surfaceCreated(SurfaceHolder holder) {
  // TODO Auto-generated method stub
  camera = Camera.open();
  camera.setFaceDetectionListener(faceDetectionListener);
 }

 @Override
 public void surfaceDestroyed(SurfaceHolder holder) {
  // TODO Auto-generated method stub
  camera.stopFaceDetection();
  camera.stopPreview();
  camera.release();
  camera = null;
  previewing = false;
 }
}