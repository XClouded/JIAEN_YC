package com.xhm.q3.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.photoalbum.R;
import com.androids.photoalbum.netinfo.PassWordVerifyLocalSiteInfo;
import com.androids.photoalbum.netinfo.PassWordVerifyWebsiteInfo;
import com.androids.photoalbum.netinfo.ServerInfo;
import com.androids.photoalbum.parser.PassWordVerifyWebsiteParser;
import com.androids.photoalbum.tab.ui.BaseActivity;
import com.androids.photoalbum.userinfo.StaticInfo;
import com.androids.photoalbum.utils.NetWorkUtils;
import com.androids.photoalbum.view.MainTabActivity;

public class q3_login_activity extends BaseActivity implements OnTouchListener,
		OnCheckedChangeListener {
	private EditText mEditText_name, mEditText_pwd;
	private CheckBox mCheckBox_auto;
	private ImageView mButton_login;
	private Context mContext;

	@Override
	protected void onCreate(Bundle paramBundle) {
		// TODO Auto-generated method stub
		super.onCreate(paramBundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.q3_login_layout);
		initView();
	}

	// 初始化控件
	private void initView() {
		mEditText_name = (EditText) findViewById(R.id.q3_edit_name);
		mEditText_pwd = (EditText) findViewById(R.id.q3_edit_pwd);

		mCheckBox_auto = (CheckBox) findViewById(R.id.q3_check_login);
		mCheckBox_auto.setButtonDrawable(R.drawable.check_box_not_selected);
		mCheckBox_auto.setOnCheckedChangeListener(this);

		mButton_login = (ImageView) findViewById(R.id.q3_button_login_1);
		mButton_login.setOnTouchListener(this);
		// 查看是否有用户名
		if (StaticInfo.mUsername.length() != 0) {
			mEditText_name.setText(StaticInfo.mUsername);
			mEditText_pwd.requestFocus();
		}
		mContext = this;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mButton_login.setAlpha(100);
			break;
		case MotionEvent.ACTION_UP:
			mButton_login.setAlpha(255);
			if ((mEditText_name.getText().toString() == null || mEditText_name
					.getText().toString().length() == 0)
					&& ((mEditText_pwd.getText().toString() == null) || (mEditText_pwd
							.getText().toString().length() == 0))) {
				notifyToUser("请输入用户名和密码！");

			} else if (mEditText_name.getText().toString() == null
					|| mEditText_name.getText().toString().length() == 0) {
				notifyToUser("请输入用户名！");

			} else if (mEditText_pwd.getText().toString() == null
					|| mEditText_pwd.getText().toString().length() == 0) {
				notifyToUser("请输入密码！");

			} else {
				new LoginTask().execute(mEditText_name.getText().toString(),
						mEditText_pwd.getText().toString());
			}
			break;

		default:
			break;
		}
		return true;
	}

	// 用于独立线程登录的
	public class LoginTask extends AsyncTask<String, Void, Void> {
		private String mUsername;
		private String mPassword;

		public void parseLoginWebsiteInfo(String username, String pwd)
				throws RemoteException {
			PassWordVerifyLocalSiteInfo localInfo = new PassWordVerifyLocalSiteInfo();
			localInfo.username = username;
			localInfo.pwd = pwd;
			mWebsiteInfo = new PassWordVerifyWebsiteParser().parseWebsiteInfo(
					NetWorkUtils.IS_EXIST_USER, localInfo);
			Log.d("zheng", "the website result:" + mWebsiteInfo.result);
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				showProgessBarDialog("Loading...");
				mUsername = params[0];
				mPassword = params[1];
				
				parseLoginWebsiteInfo(mUsername, mPassword);
				
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (pd != null) {
				pd.dismiss();
			}
			if (ServerInfo.SUCCESS.equals(mWebsiteInfo.result)) {
				notifyToUser("登录成功!");
				// 设置登录成功的图片
				q3_function_activity.mImageView_login_value
						.setImageResource(R.drawable.q3_function_page_login_ok);
				StaticInfo.mUsername = mUsername;
				StaticInfo.mPassword = mPassword;
				StaticInfo.userid = ((PassWordVerifyWebsiteInfo) mWebsiteInfo).userid;
				StaticInfo.username = ((PassWordVerifyWebsiteInfo) mWebsiteInfo).username;
				if (mIsAutoLogin) {
					SharedPreferences.Editor editor = getSharedPreferences(
							MainTabActivity.USER_INFO, MODE_WORLD_READABLE)
							.edit();
					editor.putString("username", StaticInfo.mUsername);
					editor.putString("password", StaticInfo.mPassword);
					editor.putString("server_userid",
							((PassWordVerifyWebsiteInfo) mWebsiteInfo).userid);
					editor.putString("server_username",
							((PassWordVerifyWebsiteInfo) mWebsiteInfo).username);
					editor.commit();
				} else {
					SharedPreferences.Editor editor = getSharedPreferences(
							MainTabActivity.USER_INFO, MODE_WORLD_READABLE)
							.edit();
					editor.putString("username", StaticInfo.mUsername);
					editor.putString("password", "");
					editor.putString("server_userid", "");
					editor.putString("server_username", "");
					editor.commit();
				}
				finish();
			} else if (ServerInfo.FAIL.equals(mWebsiteInfo.result)) {
				notifyToUser(mWebsiteInfo.resultdesc);
			}
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	public void showProgessBarDialog(final String msg) {
		Runnable action = new Runnable() {

			public void run() {
				if (pd == null) {
					System.out.println("comtext=" + mContext);
					pd = new ProgressDialog(mContext);
				}
				pd.setMessage(" 登录中... ");
				pd.show();
			}
		};

		runOnUiThread(action);
	}

	@Override
	protected void handleTitleBarEvent(int paramInt) {

	}

	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			mCheckBox_auto.setButtonDrawable(R.drawable.check_box_selected);
			mIsAutoLogin = true;
		} else {
			mCheckBox_auto.setButtonDrawable(R.drawable.check_box_not_selected);
			mIsAutoLogin = false;
		}
	}
}
