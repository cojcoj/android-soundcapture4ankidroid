package com.learnerstechlab.soundcapture4ankidroid;


import android.net.http.SslError;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.Menu;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class WebDictActivity extends Activity {
	private static final String TAG ="WebDictActivity";
//	MyWebChromeClient mChrome;
	WebView mWebView;
	private PlaySound playsound;
	Context mContext;
	String searchword;
	String soundsaved = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        setContentView(R.layout.activity_web_dict);
//        mChrome = new MyWebChromeClient();
        mWebView = (WebView) findViewById(R.id.webview);
//        mWebView.setWebChromeClient(mChrome);
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
//			searchword = "";
			searchword = "configuration";
		}
        
        mWebView.loadUrl("http://www.thefreedictionary.com/" + searchword.replace(" ", "+"));
        
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_web_dict, menu);
//        return true;
//    }
    
//    private class MyWebChromeClient extends WebChromeClient {
////       	TextView mProgress;
////       	String mText;
//       	public void setProgressView (TextView progressview) {
////       		mProgress = progressview;
////       		mText = mProgress.getText().toString();
//       	}
//       	@Override
//       	public void onProgressChanged (WebView view, int progress) {
////       		if (progress != 100) {
////       			mProgress.setText(Integer.toString(progress) + "%");
////       		} else {
////       			mProgress.setText(mText);
////       		}
//       	}
//       	
//       }
    
    private class MyWebViewClient extends WebViewClient {
//    	private static String staticUrl;
   	 @Override
   	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
   		if (url.endsWith(".ogg")||url.endsWith(".mp3")||url.endsWith(".wav")
   				||url.endsWith(".mp4")||url.endsWith(".m4a")||url.endsWith(".3gp")){
   			
   			if (playsound == null) playsound = new PlaySound(mContext);
   			playsound.setSoundFile(url);
   			soundsaved = playsound.setFilename(searchword.replace(" ", "_"));
   			playsound.play();
            
            return true;
        }else{
            return super.shouldOverrideUrlLoading(view, url);
        }
   	    }
   	@Override
	 	public void onPageFinished(WebView view, String url) {
//		view.loadUrl("javascript:console.log('MAGIC'+document.getElementsByTagName('html')[0].innerHTML);");
		Log.i(TAG, "F WV:"+ view + " Scale:" + view.getScale() + " Url:" + view.getUrl());
//		Log.i(TAG, "F WV:" + view + " mhight:" + view.getMeasuredHeight() + " mwidth:" + view.getMeasuredWidth());
//		Log.i(TAG, "F WV:" + view + " hight:" + view.getHeight() + " width:" + view.getWidth());
//		Log.i(TAG, "F WV:" + view + " Scale:" + view.getScale() + " Quality:" + view.getDrawingCacheQuality());
		do {} while (view.zoomOut());
   		}
   	@Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
   		super.onReceivedError(view, errorCode, description, failingUrl);
   		Log.e(TAG, "ErrCode:" + errorCode + " Description:" + description);
   	}
   	@Override
   	public void onReceivedSslError (WebView view, SslErrorHandler handler, SslError error) {
   		handler.proceed();
   	}
   	
    }
}
