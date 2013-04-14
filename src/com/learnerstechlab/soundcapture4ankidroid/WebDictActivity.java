package com.learnerstechlab.soundcapture4ankidroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.ClipboardManager;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

public class WebDictActivity extends Activity {
	private static final String TAG = "WebDictActivity";
	private static final String DICTIONARY = "dictionary";
	// MyWebChromeClient mChrome;
	WebView mWebView;
	private PlaySound playsound;
	Context mContext;
	String searchword;
	String soundsaved = "";
	int mId = 0;
	private static SharedPreferences mPreferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getApplicationContext();
		mPreferences = getSharedPreferences("sound4ankidroid", MODE_PRIVATE);
		setContentView(R.layout.activity_web_dict);
		// mChrome = new MyWebChromeClient();
		mWebView = (WebView) findViewById(R.id.webview);
		// mWebView.setWebChromeClient(mChrome);
		mWebView.setWebViewClient(new MyWebViewClient());
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setAllowFileAccess(true);
		mWebView.getSettings().setPluginsEnabled(true);
		mWebView.getSettings().setSupportZoom(true);
		mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.setHorizontalScrollBarEnabled(true);

		ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		if ((clipboard.hasText())) {
			searchword = clipboard.getText().toString();
		} else {
			 searchword = "";
//			searchword = "configuration";
		}

//		mWebView.loadUrl("http://www.thefreedictionary.com/"
//				+ searchword.replace(" ", "+"));
		mWebView.loadUrl(
				mPreferences.getString(DICTIONARY, "http://www.thefreedictionary.com/")
				+ searchword.replace(" ", "+"));

	}

	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.endsWith(".ogg") || url.endsWith(".mp3")
					|| url.endsWith(".wav") || url.endsWith(".mp4")
					|| url.endsWith(".m4a") || url.endsWith(".3gp")) {

				if (playsound == null)
					playsound = new PlaySound(mContext);
				playsound.setSoundFile(url);
				soundsaved = playsound
						.setFilename(searchword.replace(" ", "_"));
				playsound.play();

				return true;
			} else {
				return super.shouldOverrideUrlLoading(view, url);
			}
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			Log.i(TAG, "onPageFinished:" + view + " Scale:" + view.getScale()
					+ " Url:" + view.getUrl());
			do {
			} while (view.zoomOut());
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			Log.e(TAG, "ErrCode:" + errorCode + " Description:" + description);
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler,
				SslError error) {
			handler.proceed();
		}

	}
	
	@Override
	public void onStop(){
		super.onStop();
		ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		clipboard.setText("[sound:" + soundsaved + "]");
		finish();
	}

	public void hideButton(View view) {
		displayNotification();
		exitButton(view);
	}

	public void exitButton(View view) {
		ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		clipboard.setText("[sound:" + soundsaved + "]");
		finish();
	}
	
//	private void displayNotification_old() {
//		NotificationCompat.Builder mBuilder =
//	        new NotificationCompat.Builder(this)
//	        .setSmallIcon(R.drawable.ic_launcher)
//	        .setContentTitle("Sound Capture for Ankidroid")
//	        .setContentText("Click to show.");
//		// Creates an explicit intent for an Activity in your app
//		
////		Intent resultIntent = new Intent(this, WebDictActivity.class);
//		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
////		stackBuilder.addParentStack(WebDictActivity.class);
////		stackBuilder.addNextIntent(resultIntent);
//		
//		PendingIntent resultPendingIntent =
//		        stackBuilder.getPendingIntent(
//		            0,
//		            PendingIntent.FLAG_UPDATE_CURRENT
//		        );
//		mBuilder.setContentIntent(resultPendingIntent);
//		NotificationManager mNotificationManager =
//		    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//		// mId allows you to update the notification later on.
//		mNotificationManager.notify(mId, mBuilder.build());
//	}
	
	private void displayNotification() {
		// Instantiate a Builder object.
		NotificationCompat.Builder builder =
	        new NotificationCompat.Builder(this)
	        .setSmallIcon(R.drawable.ic_launcher)
	        .setContentTitle("Sound Capture for Ankidroid")
	        .setContentText("Click to show.")
	        .setAutoCancel(true)
	        .setOngoing(true);
		// Creates an Intent for the Activity
		Intent notifyIntent =
		        new Intent(this, WebDictActivity.class);
		// Sets the Activity to start in a new, empty task
		notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		// Creates the PendingIntent
		PendingIntent notifyPendingIntent =
		        PendingIntent.getActivity(
		        this,
		        0,
		        notifyIntent,
		        PendingIntent.FLAG_UPDATE_CURRENT
		);

		// Puts the PendingIntent into the notification builder
		builder.setContentIntent(notifyPendingIntent);
		// Notifications are issued by sending them to the
		// NotificationManager system service.
		NotificationManager mNotificationManager =
		    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// Builds an anonymous Notification object from the builder, and
		// passes it to the NotificationManager
		mNotificationManager.notify(mId, builder.build());
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_web_dict, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	final EditText input = new EditText(this);
    	input.setText(mPreferences.getString(DICTIONARY, "http://www.thefreedictionary.com/"));
    	
    	if (item.getItemId() == R.id.dictionary) {
    		new AlertDialog.Builder(this)
    	    .setTitle("Edit Dictionary")
    	    .setMessage("Type new web dictionary url")
    	    .setView(input)
    	    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    	        public void onClick(DialogInterface dialog, int whichButton) {
    	            String value = input.getText().toString(); 
    	            Editor ed = mPreferences.edit();
    	            ed.putString(DICTIONARY, value);
    	            ed.commit();
    	        }
    	    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    	        public void onClick(DialogInterface dialog, int whichButton) {
    	            // Do nothing.
    	        }
    	    }).show();
    	}
    	return true;
    }
}
