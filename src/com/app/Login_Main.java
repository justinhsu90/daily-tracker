package com.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Login_Main extends Activity {
	private SharedPreferences login_data;
	private EditText PasswordInput = null;
	private Button LoginButton = null;
	private Button ForgetButton = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_main);
		login_data = getSharedPreferences("loginData", 0);
		PasswordInput = (EditText) findViewById(R.id.login_password_input);
		LoginButton = (Button) findViewById(R.id.login_button);
		ForgetButton = (Button) findViewById(R.id.login_forget_button);
		LoginButton.setOnClickListener(LoginButtonListener);
		ForgetButton.setOnClickListener(ForgetButtonListener);
		run();
	}

	public void run() {
		if (login_data.getString("passlock", "NOTSETYET").equals("NOTSETYET")) {
			AlertDialog.Builder alert = new AlertDialog.Builder(Login_Main.this);
			alert.setTitle("WARNING!!");
			alert.setMessage("Passlock has not been set yet,please click SET to set the passlock");
			alert.setPositiveButton("SET", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialoginterface, int i) {
					Intent Intent = new Intent().setClass(Login_Main.this, Login_Lock.class);
					Intent.putExtra("option", "Set");
					startActivity(Intent);
					Login_Main.this.finish();
				}
			});
			alert.show();
		}
	}

	OnClickListener LoginButtonListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			String input = PasswordInput.getText().toString();
			if (input.equals(login_data.getString("passlock", "NOTFOUND"))) {
				Intent Intent = new Intent().setClass(Login_Main.this, Tab_PageSwitch.class);
				startActivity(Intent);
				Login_Main.this.finish();
			} else {
				AlertDialog.Builder alert = new AlertDialog.Builder(Login_Main.this);
				alert.setTitle("WARNING!!");
				alert.setMessage("Passlock is incorrect, please try again!");
				alert.setPositiveButton("OK", null);
				alert.show();
			}
		}
	};

	OnClickListener ForgetButtonListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent Intent = new Intent().setClass(Login_Main.this, Login_Lock.class);
			Intent.putExtra("option", "Find");
			startActivity(Intent);
			Login_Main.this.finish();
		}
	};

}
