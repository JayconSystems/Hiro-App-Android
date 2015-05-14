/**
 * Created by Jaycon Systems on 25/12/14.
 * Copyright @ 2014 Jaycon Systems. All rights reserved.
 */
package com.hiroapp.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.Window;

public class SplashScreen extends Activity implements Callback, Runnable {

	private Handler handler;
	private int splash_interval = 3000;
	private String TAG = this.getClass().getSimpleName();
	private SharedPreferences preferences;
	private Thread thread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splashscreenlayout);

		preferences = this.getSharedPreferences(getString(R.string.app_name),
				Activity.MODE_WORLD_WRITEABLE);

		getPreferenceValue();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		handler = new Handler(this);
		thread = new Thread(this);
		thread.start();

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			Thread.sleep(splash_interval);
			handler.sendEmptyMessage(0);
		} catch (InterruptedException ex) {
		}
	}

	
	/**
	 *  Start the MainActivity when Splash interval is complete.
	 */
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		if (msg.what == 0) {
			// load pick band activity
			Intent intent = null;
			intent = new Intent(SplashScreen.this, MainActivity.class);
			startActivity(intent);
			finish(); // finish the splash screen
		}
		return false;
	}

	
	/**
	 *  Get the Preference value
	 */
	public void getPreferenceValue() {

//		boolean isBattery = preferences.getBoolean("isbattery", true);
//		boolean isnotification = preferences.getBoolean("isnotification", true);
//		boolean isSoundAlert = preferences.getBoolean("isSoundAlert", true);
//		boolean isHeroBeepAlert = preferences.getBoolean("isHeroBeepAlert",
//				true);
//		boolean isbeepHigh = preferences.getBoolean("isbeepHigh", true);
//		boolean isbeepVolume = preferences.getBoolean("isbeepVolumeHigh", true);
//
//		String disRing = preferences.getString("DisconnectRing", "");
		String phoneRing = preferences.getString("PhoneRing", "");

		SharedPreferences.Editor editor1 = preferences.edit();
//		editor1.putBoolean("isbattery", isBattery);
//		editor1.putBoolean("isbeepHigh", isbeepHigh);
//		editor1.putBoolean("isbeepVolumeHigh", isbeepVolume);
//		editor1.putBoolean("isnotification", isnotification);
//		editor1.putBoolean("isSoundAlert", isSoundAlert);
//		editor1.putBoolean("isHeroBeepAlert", isHeroBeepAlert);
//		editor1.putString("DisconnectRing", disRing);
		editor1.putString("PhoneRing", phoneRing);
		editor1.commit();

	}
	
	
	/**
	 *  called when Activity is destroy and remove the handler.
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		if(thread != null){
			thread.interrupt();
			thread = null;
			
			if(handler != null && handler.hasMessages(0))
				handler.removeMessages(0);
			
		}
			
	}

}
