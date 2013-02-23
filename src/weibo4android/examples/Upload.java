package weibo4android.examples;

import com.androids.photoalbum.tab.ui.MessageActivity;
import com.androids.photoalbum.utils.Utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import weibo4android.Status;
import weibo4android.Weibo;
import weibo4android.androidexamples.OAuthActivity;
import weibo4android.http.ImageItem;



public class Upload {
	public boolean UploadWeiBo(Context context, String args[]){
    	try{
    	
    		System.setProperty("weibo4j.oauth.consumerKey", Weibo.CONSUMER_KEY);
        	System.setProperty("weibo4j.oauth.consumerSecret", Weibo.CONSUMER_SECRET);
        	Weibo weibo = new Weibo();
            
            weibo.setToken(args[0], args[1]);
            
            try{
//            	byte[] content= readFileImage(args[2]);
                byte[] content = Utils.getInputStreamBytesFromDatabase(context, MessageActivity.photoaddress);
                if (content == null) {
                    content = Utils.getInputStreamBytesFromNetwork(MessageActivity.photoaddress, context);
                }
                
            	System.out.println("content length:" + content.length);
            	ImageItem pic=new ImageItem("pic",content);
            	
            	if (args[3] == null) {
            	    args[3] = "";
            	}
            	String s=java.net.URLEncoder.encode(args[3],"utf-8");
            	Status status=weibo.uploadStatus(s, pic);
            	Log.d("zheng", "Successfully upload the status to ["
            	        +status.getText()+status.getOriginal_pic()+"].");
            	return true;
            	
            } catch(Exception e1){
                Log.d("zheng", "Upload Exception: " + e1.getMessage());
             	e1.printStackTrace();
            }
        }catch(Exception ioe){
            Log.d("zheng", "Failed to read the system input: " + ioe.getMessage());
        	System.out.println("Failed to read the system input.");
        }
    	
    	return false;
	}
	
public static byte[] readFileImage(String filename)throws IOException{
		BufferedInputStream bufferedInputStream=new BufferedInputStream(
				new FileInputStream(filename));
		int len =bufferedInputStream.available();
		byte[] bytes=new byte[len];
		int r=bufferedInputStream.read(bytes);
		if(len !=r){
			bytes=null;
			throw new IOException("读取文件不正确");
		}
		bufferedInputStream.close();
		return bytes;
	}
}
     