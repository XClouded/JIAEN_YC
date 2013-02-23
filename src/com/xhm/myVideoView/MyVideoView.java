package com.xhm.myVideoView;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.photoalbum.R;

/**
 * 视频播放
 * 
 * @author Administrator
 * 
 */
public class MyVideoView extends Activity {
	private VideoView mView;
	private MediaController mController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				// 获得地址
				mView.start();
			}
		};
		final Thread thread = new Thread(runnable);
		// 全屏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.myvideoview);
		Intent videoIntent = getIntent();
		String uri = videoIntent.getStringExtra("video_patch");
		mView = (VideoView) findViewById(R.id.myVideo);
		mController = new MediaController(this);
		mView.setMediaController(mController);
		mView.setVideoURI(Uri.parse(uri));
		mView.setFocusable(true);
		mView.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				finish();
			}
		});
		mView.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				mView.start();
			}
		});
		mView.setOnErrorListener(new OnErrorListener() {

			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				Toast.makeText(MyVideoView.this, "播放出错，请稍后播放",
						Toast.LENGTH_LONG).show();
				finish();
				return false;
			}
		});

	}

}
