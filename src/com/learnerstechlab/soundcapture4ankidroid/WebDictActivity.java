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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class WebDictActivity extends Activity {
	private static final String TAG = "WebDictActivity";
	private static final String DICTIONARY = "dictionary";
	// MyWebChromeClient mChrome;
	WebView mWebView;
	static MyWebChromeClient mWebChromeClient;
	static ProgressBar mProgressBar;
	private EditText input;
	private PlaySound playsound;
	private static Context mContext;
	private static EditText mEditText;
	private static String[] mDictPrefixes;
	private static String searchword;
	String soundsaved = "";
	int mId = 0;
	static WebDictActivity mActivity;
	private static SharedPreferences mPreferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = this;
		mContext = getApplicationContext();
		mDictPrefixes = mContext.getResources().getStringArray(R.array.urlprefixes);
		mPreferences = getSharedPreferences("sound4ankidroid", MODE_PRIVATE);
		setContentView(R.layout.activity_web_dict);
		// mChrome = new MyWebChromeClient();
		mWebView = (WebView) findViewById(R.id.webview);
		// mWebView.setWebChromeClient(mChrome);
		mWebView.setWebViewClient(new MyWebViewClient());
		mWebChromeClient = new MyWebChromeClient();
		mWebView.setWebChromeClient(mWebChromeClient);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setAllowFileAccess(true);
		mWebView.getSettings().setPluginsEnabled(true);
		mWebView.getSettings().setPluginState(PluginState.ON);
		mWebView.getSettings().setSupportZoom(true);
		mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.setHorizontalScrollBarEnabled(true);
		
		mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
		mProgressBar.setIndeterminate(false);
		mWebChromeClient.setProgressView(mProgressBar);
		

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
				Toast.makeText(mContext, soundsaved + " download START", Toast.LENGTH_SHORT).show();
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
	
	private class MyWebChromeClient extends WebChromeClient {
	   	ProgressBar mProgress;
	   	String mText;
	   	public void setProgressView (ProgressBar progressview) {
	   		mProgress = progressview;
//	   		mText = mProgress.getText().toString();
	   	}
	   	@Override
	   	public void onProgressChanged (WebView view, int progress) {
	   		if (progress != 100) {
	   			mProgress.setVisibility(View.VISIBLE);
	   			mProgress.setProgress(progress);
	   		} else {
	   			mProgress.setVisibility(View.GONE);
	   		}
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
//    	final EditText input = new EditText(this);
//    	input = new EditText(this);
//    	input.setText(mPreferences.getString(DICTIONARY, "http://www.thefreedictionary.com/"));
    	
    	if (item.getItemId() == R.id.dictionary) {
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		View vw = LayoutInflater.from(this).inflate(R.layout.dialog_layout,
    				null);
    		builder
    				.setNegativeButton("Cancel", mCancelListener)
    				.setPositiveButton("OK", mOkListener)
    				.setIcon(R.drawable.ic_launcher).setTitle("Dictionaries").setView(vw);

    		ListView lv = (ListView) vw.findViewById(R.id.listview);
    		mEditText = (EditText) vw.findViewById(R.id.textview);
    		ArrayAdapter<CharSequence> adapter = ArrayAdapter
    				.createFromResource(this, R.array.dict_names,
    						android.R.layout.simple_list_item_1);
    		lv.setOnItemClickListener(mItemSelectedListner);
    		lv.setAdapter(adapter);
    		mEditText.setText(mPreferences.getString(DICTIONARY, "http://www.thefreedictionary.com/"));
    		builder.show();
    	}
    	return true;
    }
    
	AdapterView.OnItemClickListener mItemSelectedListner =
		new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int idx,
					long arg3) {
				mEditText.setText(mDictPrefixes[idx]);
			}
		
	};

	DialogInterface.OnClickListener mOkListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			Editor ed = mPreferences.edit();
        	ed.putString(DICTIONARY, mEditText.getText().toString());
        	ed.commit();
        	mWebView.loadUrl(
    				mPreferences.getString(DICTIONARY, "http://www.thefreedictionary.com/")
    				+ searchword.replace(" ", "+"));
		}

	};

	DialogInterface.OnClickListener mCancelListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub

		}

	};
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    // Check if the key event was the Back button and if there's history
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	    	if (mWebView.canGoBack()) {
		        mWebView.goBack();
		        return true;
	    	} else {
	    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    		builder
	    				.setMessage("Hide? Exit? or Cancel?")
	    				.setNegativeButton("Hide", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								displayNotification();
								ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
								clipboard.setText("[sound:" + soundsaved + "]");
								finish();
							}})
	    				.setNeutralButton("Exit", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
								clipboard.setText("[sound:" + soundsaved + "]");
								finish();
							}})
	    				.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								
							}})
	    				.setIcon(R.drawable.ic_launcher).setTitle("Cannot go back.");
	    		builder.show();
	    		return true;
	    	}
	    }
	    // If it wasn't the Back key or there's no web page history, bubble up to the default
	    // system behavior (probably exit the activity)
	    return super.onKeyDown(keyCode, event);
	}

}
