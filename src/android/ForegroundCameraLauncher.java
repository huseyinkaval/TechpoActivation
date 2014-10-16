package com.foregroundcameraplugin;

import java.io.File;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;

import com.creamobile.kaskomobil.CameraActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.provider.MediaStore;
import android.net.Uri;

public class ForegroundCameraLauncher extends CordovaPlugin {

	public CallbackContext callbackContext;
	
	  @Override
	    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
		  
		  this.callbackContext = callbackContext;


	        if (action.equals("takePicture")) {
	        	try {
	        		File photo = new File(getTempDirectoryPath(this.cordova.getActivity().getApplicationContext()), "kmactivation.jpg");
	        		Uri imageuri=Uri.fromFile(photo);

	        		Intent intent = new Intent(this.cordova.getActivity().getApplicationContext(), CameraActivity.class);
	        		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageuri);

	        		this.cordova.startActivityForResult(this, intent, 1);
	        	
	        	} catch (Exception e1){
	        		callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, ""));
	        	}
	        } else {
	        	callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.INVALID_ACTION, ""));
	        }
	        
	        return true;
	    }
	  
	  public void onActivityResult(int requestCode, int resultCode, Intent intent) {
  			File photo = new File(getTempDirectoryPath(this.cordova.getActivity().getApplicationContext()), "kmactivation.jpg");
	        String result = Uri.fromFile(photo).getPath();
	        
	        if (resultCode == Activity.RESULT_OK) {
	        	callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, result));
			}

			// If cancelled
			else if (resultCode == Activity.RESULT_CANCELED) {
				callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "Iptal Edildi"));
			}

			// If something else
			else {
				callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "Ön Kameranýz Açýlamýyor. Lütfen Tekrar Deneyiniz."));
			}
	        
	        
		}
	  
		private String getTempDirectoryPath(Context ctx) {
			File cache = null;

			// SD Card Mounted
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				cache = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath()
						+ "/Android/data/"
						+ ctx.getPackageName() + "/cache/");
			}
			// Use internal storage
			else {
				cache = ctx.getCacheDir();
			}

			// Create the cache directory if it doesn't exist
			if (!cache.exists()) {
				cache.mkdirs();
			}

			return cache.getAbsolutePath();
		}
	  
}
