package com.creamobile.kaskomobil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.creamobile.kaskomobil.R;
import com.foregroundcameraplugin.ForegroundCameraPreview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
* Camera Activity Class. Configures Android camera to take picture and show it.
*/
@SuppressLint("NewApi")
public class CameraActivity extends Activity  implements SensorEventListener {

private static final String TAG = "CameraActivity";

private Camera mCamera;
private ForegroundCameraPreview mPreview;
private boolean pressed = false;
private SensorManager sensorManager;
private long lastUpdate;
int sensorOktimer;

@Override
public void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
requestWindowFeature(Window.FEATURE_NO_TITLE);
getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
WindowManager.LayoutParams.FLAG_FULLSCREEN);
getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

setContentView(R.layout.foregroundcameraplugin);

// Create an instance of Camera
mCamera = getCameraInstance();

// Create a Preview and set it as the content of activity.
mPreview = new ForegroundCameraPreview(this, mCamera);
FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
preview.addView(mPreview);

 sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    lastUpdate = System.currentTimeMillis();

    sensorManager.registerListener(this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL);
    sensorOktimer=0;

}



private void getAccelerometer(SensorEvent event) {
float[] values = event.values;
// Movement
float x = values[0];
float y = values[1];
float z = values[2];

long actualTime = System.currentTimeMillis();
  if (actualTime - lastUpdate < 1000) {
    return;
  }
  lastUpdate = actualTime;


  TextView say = (TextView) findViewById(R.id.textView1);
  ProgressBar pb2 = (ProgressBar) findViewById(R.id.progressBar2);
  if (x>0 && x<=2.5) {
	  pb2.setProgress(10);
  } else if (x>2.5 && x<=5) {
	  pb2.setProgress(20);
  } else if (x>5 && x<=6) {
	  pb2.setProgress(30);
  } else if (x>6 && x<=9) {
	  pb2.setProgress(45);
  } else if (x>9 && x<=11) {
	  pb2.setProgress(50);
  } 
  
  // (z>-0.9 && z<0.9)
if (x>9 & x<=11) {
	say.setVisibility(View.VISIBLE);
	pb2.setVisibility(View.INVISIBLE);
	say.setText("" + (3-sensorOktimer));
	
	if (sensorOktimer>=3) {
		if (!pressed) {

			pressed = true;
			mCamera.takePicture(null, null, mPicture);
		}
	} else if (sensorOktimer>=2) {
		FrameLayout camerascreen = (FrameLayout) findViewById(R.id.camera_preview);
		camerascreen.setVisibility(View.INVISIBLE);
		
		FrameLayout ly_bilgiAlan1 = (FrameLayout) findViewById(R.id.bilgiAlan1);
		ly_bilgiAlan1.setVisibility(View.INVISIBLE);
		
		FrameLayout ly_bilgiAlan2 = (FrameLayout) findViewById(R.id.bilgiAlan2);
		ly_bilgiAlan2.setVisibility(View.INVISIBLE);
		
		LinearLayout frameLayoutBalance = (LinearLayout) findViewById(R.id.ab1);
		frameLayoutBalance.setBackgroundColor(Color.rgb(0, 255, 0));
		//frameLayoutBalance.setBackgroundColor(Color.rgb(255, 255, 255));
		
	}
	
	sensorOktimer ++;
	
} else {
	sensorOktimer=0;
	pb2.setVisibility(View.VISIBLE);
	say.setVisibility(View.INVISIBLE);
	FrameLayout camerascreen = (FrameLayout) findViewById(R.id.camera_preview);
	camerascreen.setVisibility(View.VISIBLE);
	
	FrameLayout ly_bilgiAlan1 = (FrameLayout) findViewById(R.id.bilgiAlan1);
	ly_bilgiAlan1.setVisibility(View.VISIBLE);
	
	FrameLayout ly_bilgiAlan2 = (FrameLayout) findViewById(R.id.bilgiAlan2);
	ly_bilgiAlan2.setVisibility(View.VISIBLE);
	
	LinearLayout frameLayoutBalance = (LinearLayout) findViewById(R.id.ab1);
	frameLayoutBalance.setBackgroundColor(Color.BLACK);
}

}

@Override
protected void onPause() {
if (mCamera != null) {
	mCamera.release(); // release the camera for other applications
	mCamera = null;
}
sensorManager.unregisterListener(this);
super.onPause();
}

/** A safe way to get an instance of the Camera object. */
@SuppressLint("InlinedApi")
public static Camera getCameraInstance() {
Camera c = null;
try {
	c = Camera.open(CameraInfo.CAMERA_FACING_FRONT); // attempt to get a Camera instance
} catch (Exception e) {
	// Camera is not available (in use or does not exist)
}
return c; // returns null if camera is unavailable
}



private PictureCallback mPicture = new PictureCallback() {

public void onPictureTaken(byte[] data, Camera camera) {

	
	Uri fileUri = (Uri) getIntent().getExtras().get(MediaStore.EXTRA_OUTPUT);
	File pictureFile = new File(fileUri.getPath());
	
     try {
    	 
			FileOutputStream fos = new FileOutputStream(pictureFile);
			fos.write(data);
			fos.close();

			
			setResult(RESULT_OK);
    		finish();

     
     } catch (Exception e) {

			setResult(RESULT_CANCELED);
    		finish();
     }

     
     
     
}
};


@Override
public void onAccuracyChanged(Sensor arg0, int arg1) {
// TODO Auto-generated method stub

}

@Override
public void onSensorChanged(SensorEvent event) {
// TODO Auto-generated method stub
 if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
      getAccelerometer(event);
    }

}
}