package com.androids.photoalbum.view;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.photoalbum.R;
import com.androids.photoalbum.parser.BackPwdWebsiteParser;
import com.androids.photoalbum.tab.ui.BaseActivity;
import com.androids.photoalbum.utils.Utils;

public class ForgetPwdActivity extends BaseActivity {
	private Button mChangePwd;
	private EditText mContent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setView(R.layout.forget_pwd_layout);
		setTitleBar(com.androids.photoalbum.view.BaseLayout.TYPE_NORMAL, "返回", null, null);
		
		mChangePwd = (Button)findViewById(R.id.change_pwd);
		mChangePwd.setOnTouchListener(this);
		mChangePwd.setOnClickListener(this);
		
		mContent = (EditText)findViewById(R.id.content);
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.change_pwd:
				changePwd();
				break;
			default:
				super.onClick(view);	
		}
	}
	
	public void showProgessBarDialog(final String msg) {
		Runnable action = new Runnable() {
			
			public void run() {
			    if(pd==null) {
			    	pd=new ProgressDialog(ForgetPwdActivity.this);
//			    	pd.setTitle("加载图片");
			    }
			    pd.setMessage(" 加载中... ");
			    pd.show();	
			}
		};

	    
	    runOnUiThread(action);
	}
	
	private void changePwd() {
		if (mContent.getEditableText().toString() == null || mContent.getEditableText().toString().length() == 0) {
			notifyToUser("请输入用户名!");
			return;
		} else {
			Runnable run = new Runnable() { 
				 
				public void run() {
					String url = null;
 
					url = "http://www.uuunm.com/Backpwd.jsp?userid=" + mContent.getEditableText().toString();
					Log.d("zheng", "url:  " + url);
					showProgessBarDialog("Loading...");
					mWebsiteInfo = new BackPwdWebsiteParser().parseWebsiteInfo(url);
					pd.dismiss();
					if ("成功".equals(mWebsiteInfo.resultdesc)) {
						Utils.notifyToUser("成功", ForgetPwdActivity.this);
						Log.d("zheng", "成功");
					} else {
						Utils.notifyToUser("用户不存在, 或用户名不正确.", ForgetPwdActivity.this);
					}
				}
			};
			
			new Thread(run).start();
		}
	}

	@Override
	protected void handleTitleBarEvent(int paramInt) {
		finish();
	}
}