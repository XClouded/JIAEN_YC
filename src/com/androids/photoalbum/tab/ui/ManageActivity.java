package com.androids.photoalbum.tab.ui;

import com.androids.photoalbum.view.MainTabActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;


public class ManageActivity extends BaseActivity {
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
//        setContentView(R.layout.splash);
	    
        Handler localHandler = new Handler();
        SplashRunnable runnable = new SplashRunnable();
        boolean bool = localHandler.postDelayed(runnable, 2000L);
    }

	@Override
	protected void handleTitleBarEvent(int paramInt) {
		
	}
	
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("zheng", "SplashActivity onResume");
    }
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            return true;
        }

       return super.onKeyDown(keyCode, keyEvent);
    }

    class SplashRunnable implements Runnable {
        public void run() {
            Intent mainTabIntent = new Intent(getApplicationContext(), MainTabActivity.class);
            startActivity(mainTabIntent);
            finish();
        }
    }
	
}