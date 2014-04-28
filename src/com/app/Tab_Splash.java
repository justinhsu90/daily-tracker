package com.app;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Tab_Splash extends Activity {
	private long splashDelay = 1500;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				finish();
				Intent Intent = new Intent().setClass(Tab_Splash.this, Login_Main.class);
				startActivity(Intent);
			}
		};

		Timer timer = new Timer();
		timer.schedule(task, splashDelay);
	}
}
