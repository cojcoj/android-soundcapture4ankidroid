package com.learnerstechlab.soundcapture4ankidroid;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class PlaySound {
	private static final String TAG = "PlaySound";
	private static final String filedir = Environment.getExternalStorageDirectory() +
    "/Ankidroid/collection.media/";
	private static String filename;
	private static String soundurl;
	private static Context mContext;
	
	public PlaySound (Context ctx) {
		mContext = ctx;
	}
	
	
	public void setSoundFile(String su) {
		soundurl = su;
	}
	
	public String setFilename (String candidate) {
		if (soundurl == null) {
			return null;
		}
		String extension = "";
		int i = soundurl.lastIndexOf('.');
		int p = Math.max(soundurl.lastIndexOf('/'), soundurl.lastIndexOf('\\'));

		if (i > p) {
		    extension = soundurl.substring(i);
		}
		
		filename = filedir + candidate +  extension;
		File f = new File(filename);
		if (!f.exists()) {
			createFile(filename);
			return candidate + extension;
		}
				
		int j = 0;
	    filename = filedir + candidate + Integer.toString(j) + extension;
	    f = new File(filename);
	    while (f.exists()) {
	        j++;
	        filename = filedir + candidate + Integer.toString(j) + extension;
	        f = new File(filename);
	    }
	    createFile(filename);
		
		return candidate + Integer.toString(j) + extension;
	}
	
	private void createFile(String file_name) {
		try {
			File f = new File(file_name);
			File parent = f.getParentFile();
			if(!parent.exists() && !parent.mkdirs()){
			    throw new IllegalStateException("Couldn't create dir: " + parent);
			}
	    	f.createNewFile();
	    } catch (Exception e) {
	    	Log.i(TAG,"Error Create New File:"+e);
	    } 
	}
	
	public void play() {
		 new Thread(new Runnable() {
	    public void run() {
//	    	if (soundurl.endsWith(".wav")) {
	   			FileDownload(soundurl);
	        	Uri tempPath = Uri.parse(filename);
	        	MediaPlayer player = MediaPlayer.create(mContext, tempPath);
	        	if (player != null) player.start();
//	        	File soundfile = new File(filename);
//	            if (soundfile.exists()) soundfile.delete();			
//	    	}
//	    	if (soundurl.endsWith(".ogg")||soundurl.endsWith(".mp3")
//	   				||soundurl.endsWith(".mp4")||soundurl.endsWith(".m4a")||soundurl.endsWith(".3gp")){
//	            Uri tempPath = Uri.parse(soundurl);
//	            MediaPlayer player = MediaPlayer.create(mContext, tempPath);
//	            if (player != null) {
//	            	player.start();
//	            } else {
//	            	FileDownload(soundurl);
//	            	tempPath = Uri.parse(filename);
//	            	player = MediaPlayer.create(mContext, tempPath);
//	            	if (player != null) player.start();
//	            }
//	            File soundfile = new File(filename);
//	            if (soundfile.exists()) soundfile.delete();
//	        }
	    }
	  }).start();
	}
	
	private void FileDownload(String url)  
    {  
        int responseCode = 0;  
        int BUFFER_SIZE = 10240;  
      try { 
    	URI uri = URI.create(url);
        HttpClient hClient = new DefaultHttpClient();  
        HttpGet hGet = new HttpGet();  
        HttpResponse hResp = null;  
      
        hClient.getParams().setParameter("http.connection.timeout", new Integer(7000));  
      
        hGet.setURI(uri);  
      
        
        
        hResp = hClient.execute(hGet);  
      
        responseCode = hResp.getStatusLine().getStatusCode();  
      
        if (responseCode == HttpStatus.SC_OK) {  
            File file = new File(filename);  
            InputStream is = hResp.getEntity().getContent();  
            BufferedInputStream in = new BufferedInputStream(is, BUFFER_SIZE);  
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file, false), BUFFER_SIZE);  
      
            byte buf[] = new byte[BUFFER_SIZE];  
            int size = -1;  
            while((size = in.read(buf)) != -1) {  
                out.write(buf, 0, size);  
            }  
            out.flush();  
      
            out.close();  
            in.close();  
        }  
        else if (responseCode == HttpStatus.SC_NOT_FOUND) {  
            /* Dowload file not found */  
        }  
        else if (responseCode == HttpStatus.SC_REQUEST_TIMEOUT) {  
            /* Connection Timed Out */  
        }  
      } catch (Exception e) {
      Log.i(TAG,"Error:" + e);
      }  

    }
}
