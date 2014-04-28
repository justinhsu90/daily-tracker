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
import android.widget.TextView;

public class Login_Lock extends Activity {
	private SharedPreferences login_data;
	private SharedPreferences.Editor login_data_editor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		login_data = getSharedPreferences("loginData", 0);
		login_data_editor = login_data.edit();
		Bundle extras = getIntent().getExtras();
		if (extras.getString("option").equals("Set")) {
			setContentView(R.layout.login_setpin);
			setLock();
		} else {
			setContentView(R.layout.login_password_forget);
			System.out.println("###inininininininin");
			Button GobackButton = (Button)findViewById(R.id.login_forget_goback_button);
			GobackButton.setOnClickListener(GoBackButtonListener);
			findLock();
		}
	}

	public void findLock() {
		final TextView login_question = (TextView) findViewById(R.id.login_question_display);
		login_question.setText(login_data.getString("question", "No Question is been set!!"));
		final EditText AnswerInput = (EditText) findViewById(R.id.login_answer_input);
		Button GetPasslockButton = (Button) findViewById(R.id.login_check_answer_button);
		GetPasslockButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (AnswerInput.getText().toString().equals(login_data.getString("answer", "NULL"))) {
					final TextView passlock_retrieve_display = (TextView) findViewById(R.id.login_password_display);
					passlock_retrieve_display.setText(login_data.getString("passlock", "NULL"));
				} else {
					AlertDialog.Builder alert = new AlertDialog.Builder(Login_Lock.this);
					alert.setTitle("WARNING!!");
					alert.setMessage("Answer is incorrect,please try again!!");
					alert.setPositiveButton("OK", null);
					alert.show();
				}
			}
		});
	}

	OnClickListener GoBackButtonListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent Intent = new Intent().setClass(Login_Lock.this, Login_Main.class);
			startActivity(Intent);
			Login_Lock.this.finish();
		}
	};

	public void setLock() {
		final EditText setPasswordInputOne = (EditText) findViewById(R.id.login_set_password_once);
		final EditText setPasswordInputTwo = (EditText) findViewById(R.id.login_set_password_twice);
		final EditText setQuestion = (EditText) findViewById(R.id.login_question_input);
		final EditText setAnswer = (EditText) findViewById(R.id.login_answer_input);
		Button SetButton = (Button) findViewById(R.id.login_setPasslock_button);
		SetButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (setPasswordInputOne.getText().toString().equals(setPasswordInputTwo.getText().toString())) {
					login_data_editor.putString("passlock", setPasswordInputOne.getText().toString());
					login_data_editor.putString("question", setQuestion.getText().toString());
					login_data_editor.putString("answer", setAnswer.getText().toString());
					login_data_editor.commit();
					AlertDialog.Builder alert = new AlertDialog.Builder(Login_Lock.this);
					alert.setTitle("Notice!!");
					alert.setMessage("Passlock has been set, please click OK to login");
					alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialoginterface, int i) {
							Intent Intent = new Intent().setClass(Login_Lock.this, Login_Main.class);
							startActivity(Intent);
							Login_Lock.this.finish();
						}
					});
					alert.show();
				} else {
					AlertDialog.Builder alert = new AlertDialog.Builder(Login_Lock.this);
					alert.setTitle("WARNING!!");
					alert.setMessage("Password is not match, please re-enter!");
					alert.setPositiveButton("OK", null);
					alert.show();
				}
			}
		});
	}
}
