package com.androids.photoalbum.tab.ui;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import com.androids.photoalbum.utils.GifDecoder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class GIFEditorView extends ImageView implements Runnable{
	private Context mContext;
	public GIFEditorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		new Thread(this).start();
	}

	String mGifPath = "/sdcard/album/gif008.gif";
	static final String TAG = "GIFFramePreviewView";
	Vector<Bitmap> gifBitmaps = new Vector<Bitmap>(4);
	int mCurDrawFrame = 0;
	int mTotalFrames = 0;
	String mPhotoAddress = "http://www.uuunm.com/Product/MMS/0030070428.gif";
	
//	public GIFEditorView(Context context) {
//		super(context);
//		new Thread(this).start();
//	}

	public void drawImages(Canvas canvas){
		//
		if(gifBitmaps.size() > 0) {
			if(mCurDrawFrame == mTotalFrames) {
				mCurDrawFrame = 0;
			}
			
			final Bitmap btm = gifBitmaps.get(mCurDrawFrame);
			Log.d("previewview", "draw i frame " + mCurDrawFrame + "," +btm);
//			setImageBitmap(btm);
			
			canvas.drawBitmap(btm, 0, 0, null);
			mCurDrawFrame++;
		}
	}
	
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
		drawImages(canvas);
	}
	
	public void run() {
        Log.i("returnBitMap", "url=" + mPhotoAddress);
        URL myFileUrl = null;
        try {
            myFileUrl = new URL(mPhotoAddress);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        InputStream is = null;
        try {
            HttpURLConnection conn = (HttpURLConnection)myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            is = conn.getInputStream();
        } catch (Exception e) {
        	e.printStackTrace();
        }
		// TODO Auto-generated method stub
		GifDecoder decoder = new GifDecoder();
        decoder.read(is);
        mTotalFrames = decoder.getFrameCount(); //得到frame的个数
        Log.d(TAG, "get frames after decoded " + mTotalFrames);
        for (int i = 0; i < mTotalFrames; i++) {
        	Bitmap btm = decoder.getFrame(i);
        	gifBitmaps.add(btm); //得到帧
        	Log.d("previewview", "Get i frame " + btm);
        }
        
		while (!Thread.interrupted()) {
			try {
				Thread.sleep(2000);
//				drawImages();
				this.postInvalidate();
			} catch (Exception ex) {
				ex.printStackTrace();
				Thread.currentThread().interrupt();
			}
		}
		
	}

}
