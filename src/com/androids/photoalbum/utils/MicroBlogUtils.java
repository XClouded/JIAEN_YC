package com.androids.photoalbum.utils;

import org.apache.http.HttpEntity;
import org.apache.http.protocol.HTTP;
import android.util.Log;
import java.io.IOException;
import java.io.InputStreamReader; 
import java.io.UnsupportedEncodingException;

public class MicroBlogUtils {
    public final static String BASE_URI = "http://221.122.119.86/xmltest/PhoneApi/ComBlog/";
//	public final static String BASE_URI = "http://www.gloria.cc/yhoa/phoneapi/comblog/";
    public final static String TAG = "zheng";
    public final static boolean LOG_ON = true; 

    /** 
     * 处理httpResponse信息,返回String
     * 
     * @param httpEntity
     * @return String
     */
    public static String retrieveInputStream(HttpEntity httpEntity) {
        httpEntity.getContentLength();
        int length = (int)httpEntity.getContentLength();
        // the number of bytes of the content, or a negative number if unknown.
        // If the content length is known but exceeds Long.MAX_VALUE, a negative
        // number is returned.
        // length==-1，下面这句报错，println needs a message
        if (length < 0)
            length = 100000;
        StringBuffer stringBuffer = new StringBuffer(length);
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(httpEntity.getContent(),
                    HTTP.UTF_8);
            char buffer[] = new char[length];
            int count;
            while ((count = inputStreamReader.read(buffer, 0, length - 1)) > 0) {
                stringBuffer.append(buffer, 0, count);
            }
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.getMessage());
        } catch (IllegalStateException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return stringBuffer.toString();
    }
}