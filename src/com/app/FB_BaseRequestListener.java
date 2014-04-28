package com.app;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import android.util.Log;

import com.app.FB_AsyncFacebookRunner.RequestListener;

/**
 * Skeleton base class for RequestListeners, providing default error handling.
 * Applications should handle these error conditions.
 */
public abstract class FB_BaseRequestListener implements RequestListener {

	@Override
	public void onFacebookError(FB_FacebookError e, final Object state) {
		Log.e("Facebook", e.getMessage());
		e.printStackTrace();
	}

	@Override
	public void onFileNotFoundException(FileNotFoundException e, final Object state) {
		Log.e("Facebook", e.getMessage());
		e.printStackTrace();
	}

	@Override
	public void onIOException(IOException e, final Object state) {
		Log.e("Facebook", e.getMessage());
		e.printStackTrace();
	}

	@Override
	public void onMalformedURLException(MalformedURLException e, final Object state) {
		Log.e("Facebook", e.getMessage());
		e.printStackTrace();
	}

}
