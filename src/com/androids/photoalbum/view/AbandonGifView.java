package com.androids.photoalbum.view;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.view.View;


public class AbandonGifView extends View {
    private Movie mMovie;
    private long mMovieStart;
    
    private static byte[] streamToBytes(InputStream is) {
        ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
        byte[] buffer = new byte[1024];
        int len;
        try {
            while ((len = is.read(buffer)) >= 0) {
                os.write(buffer, 0, len);
            }
        } catch (java.io.IOException e) {
        }
        return os.toByteArray();
    }
    
    public AbandonGifView(Context context, AttributeSet attrs) {
    	super(context, attrs);
    }
    
    public void setGifView(java.io.InputStream is) {
        setFocusable(true);
        
        if (false) {
            mMovie = Movie.decodeStream(is);
        } else {
            byte[] array = streamToBytes(is);
            mMovie = Movie.decodeByteArray(array, 0, array.length);
        }
    }
    
    @Override protected void onDraw(Canvas canvas) {
        canvas.drawColor(0xFFCCCCCC);            
        
        long now = android.os.SystemClock.uptimeMillis();
        if (mMovieStart == 0) {   // first time
            mMovieStart = now;
        }
        if (mMovie != null) {
            int dur = mMovie.duration();
            if (dur == 0) {
                dur = 1000;
            }
            int relTime = (int)((now - mMovieStart) % dur);
            mMovie.setTime(relTime);
//            canvas.scale( mMovie.width()/getWidth(),  mMovie.height()/getHeight());
            mMovie.draw(canvas, getWidth() - mMovie.width(),
                        getHeight() - mMovie.height());
//            canvas.scale(getWidth() / mMovie.width(), getHeight() / mMovie.height());
            invalidate();
        }
    }
}