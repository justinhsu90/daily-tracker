package com.app;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.FB_SessionEvents.AuthListener;
import com.app.FB_SessionEvents.LogoutListener;

public class FB_FB extends Activity implements OnClickListener {

	public static final String APP_ID = "182385801840984";

	private FB_LoginButton mLoginButton;
	private TextView mText;
	private ImageView mUserPic;
	private Handler mHandler;
	ProgressDialog dialog;

	final static int AUTHORIZE_ACTIVITY_RESULT_CODE = 0;
	String[] permissions = { "publish_checkins" };

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (APP_ID == null) {
			FB_Util.showAlert(this, "Warning", "Facebook Applicaton ID must be " + "specified before running this example: see FbAPIs.java");
			return;
		}
		setContentView(R.layout.setting_fb_login);
		setTitle("Facebook Login/Logout");
		mHandler = new Handler();
		mText = (TextView) FB_FB.this.findViewById(R.id.txt);
		mUserPic = (ImageView) FB_FB.this.findViewById(R.id.user_pic);
		/** Create the Facebook Object using the app id. **/
		FB_Utility.mFacebook = new FB_Facebook(APP_ID);
		/** Instantiate the asynrunner object for asynchronous api calls. **/
		FB_Utility.mAsyncRunner = new FB_AsyncFacebookRunner(FB_Utility.mFacebook);
		mLoginButton = (FB_LoginButton) findViewById(R.id.login);
		/** restore session if one exists **/
		FB_SessionStore.restore(FB_Utility.mFacebook, this);
		FB_SessionEvents.addAuthListener(new FbAPIsAuthListener());
		FB_SessionEvents.addLogoutListener(new FbAPIsLogoutListener());
		mLoginButton.init(this, AUTHORIZE_ACTIVITY_RESULT_CODE, FB_Utility.mFacebook, permissions);
		if (FB_Utility.mFacebook.isSessionValid()) {
			requestUserData();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (FB_Utility.mFacebook != null) {
			if (!FB_Utility.mFacebook.isSessionValid()) {
				mText.setText("You are logged out! ");
				mUserPic.setImageBitmap(null);
			} else {
				FB_Utility.mFacebook.extendAccessTokenIfNeeded(this, null);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		/*
		 * if this is the activity result from authorization flow, do a call
		 * back to authorizeCallback Source Tag: login_tag
		 */
		case AUTHORIZE_ACTIVITY_RESULT_CODE: {
			FB_Utility.mFacebook.authorizeCallback(requestCode, resultCode, data);
			break;
		}
		}
	}

	/*
	 * Callback for fetching current user's name, picture, uid.
	 */
	public class UserRequestListener extends FB_BaseRequestListener {
		@Override
		public void onComplete(final String response, final Object state) {
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(response);
				final String picURL = jsonObject.getString("picture");
				final String name = jsonObject.getString("name");
				FB_Utility.userUID = jsonObject.getString("id");
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						mText.setText("Welcome " + name + "!");
						mUserPic.setImageBitmap(FB_Utility.getBitmap(picURL));
					}
				});
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/*
	 * The Callback for notifying the application when authorization succeeds or
	 * fails.
	 */
	public class FbAPIsAuthListener implements AuthListener {
		@Override
		public void onAuthSucceed() {
			requestUserData();
		}

		@Override
		public void onAuthFail(String error) {
			mText.setText("Login Failed: " + error);
		}
	}

	/*
	 * The Callback for notifying the application when log out starts and
	 * finishes.
	 */
	public class FbAPIsLogoutListener implements LogoutListener {
		@Override
		public void onLogoutBegin() {
			mText.setText("Logging out...");
		}

		@Override
		public void onLogoutFinish() {
			mText.setText("You have logged out! ");
			mUserPic.setImageBitmap(null);
		}
	}

	/*
	 * Request user name, and picture to show on the main screen.
	 */
	public void requestUserData() {
		mText.setText("Fetching user name, profile pic...");
		Bundle params = new Bundle();
		params.putString("fields", "name, picture");
		FB_Utility.mAsyncRunner.request("me", params, new UserRequestListener());
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
	}
}
