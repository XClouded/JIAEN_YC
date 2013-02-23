package com.androids.photoalbum.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.android.photoalbum.R;
import com.androids.photoalbum.netinfo.PassWordVerifyLocalSiteInfo;
import com.androids.photoalbum.netinfo.PassWordVerifyWebsiteInfo;
import com.androids.photoalbum.netinfo.ServerInfo;
import com.androids.photoalbum.parser.PassWordVerifyWebsiteParser;
import com.androids.photoalbum.tab.ui.BaseActivity;
import com.androids.photoalbum.tab.ui.FavoriteActivity;
import com.androids.photoalbum.userinfo.StaticInfo;
import com.androids.photoalbum.utils.NetWorkUtils;

public class LoginActivity extends BaseActivity implements
		OnCheckedChangeListener {
	private TextView mRegister;
	private TextView mForgetPwd;
	public final static int REQUEST_LOGIN_CODE = 0;
	private ImageView mLoginBtn;
	private EditText mUsername;
	private EditText mPassword;
	private CheckBox mAutoLogin;
	private Context mContext;

	@Override
	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setView(R.layout.login_layout_q3);
		setTitleBar(com.androids.photoalbum.view.BaseLayout.TYPE_NORMAL, "返回",
				null, null);
		// mFestival = (TextView)findViewById(R.id.festival);
		// mFestival.setOnClickListener(this);
		mLoginBtn = (ImageView) findViewById(R.id.btn_login);
		mLoginBtn.setOnClickListener(this);
		mLoginBtn.setOnTouchListener(this);
		// mRegister = (TextView) findViewById(R.id.register);
		// mRegister.setOnClickListener(this);
		// mForgetPwd = (TextView) findViewById(R.id.forget_pwd);
		// mForgetPwd.setOnClickListener(this);
		// mWebsiteParser = new PassWordVerifyWebsiteParser();
		// loadServerInfo(WebsiteParser.ALL, null, null);
		mUsername = (EditText) findViewById(R.id.user_name);
		mPassword = (EditText) findViewById(R.id.pwd);
		if (StaticInfo.mUsername != null) {
			mUsername.setText(StaticInfo.mUsername);
			mPassword.requestFocus();
		}
		mAutoLogin = (CheckBox) findViewById(R.id.auto_login);
		mAutoLogin.setOnCheckedChangeListener(this);
		// StaticInfo.mUsername = "15647424490";
		// StaticInfo.mPassword = "111111";

		Intent intent = getIntent();
		if (intent != null) {
			isActivity = intent.getBooleanExtra("isactivity", false);
		}

		if (isActivity) {
			mContext = this;
		} else {
			mContext = MainTabActivity.getCurrentActivityGroup();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (StaticInfo.mPassword != null && StaticInfo.mPassword.length() != 0) {
			MainTabActivity.getCurrentActivityGroup().setPreviousActivity();
		}
	}

	public class LoginTask extends AsyncTask<String, Void, Void> {
		private String mUsername;
		private String mPassword;

		// 登录程序
		public void parseLoginWebsiteInfo(String username, String pwd)
				throws RemoteException {
			Log.d("zheng", "calling parseClassificationWebsiteInfo");
			PassWordVerifyLocalSiteInfo localInfo = new PassWordVerifyLocalSiteInfo();
			localInfo.username = username;
			localInfo.pwd = pwd;
			mWebsiteInfo = new PassWordVerifyWebsiteParser().parseWebsiteInfo(
					NetWorkUtils.IS_EXIST_USER, localInfo);
			Log.d("zheng", "the website result:" + mWebsiteInfo.result);
		}

		// 获得输入的用户名和密码
		@Override
		protected Void doInBackground(String... params) {
			Log.d("zheng", "calling LoginTask");
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
				StaticInfo.mUsername = mUsername;
				StaticInfo.mPassword = mPassword;
				StaticInfo.userid = ((PassWordVerifyWebsiteInfo) mWebsiteInfo).userid;
				StaticInfo.username = ((PassWordVerifyWebsiteInfo) mWebsiteInfo).username;
				Log.d("zheng", "username:" + StaticInfo.mUsername
						+ " password:" + StaticInfo.mPassword);
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

				if (isActivity) {
					finish();
				} else {
					Intent intent = new Intent(LoginActivity.this,
							FavoriteActivity.class);
					MainTabActivity.getCurrentActivityGroup().setNextActivity(
							intent, "FavoriteActivity");
				}

				// FavoriteActivity.notLoaded = true;

				// setResult(Activity.RESULT_OK);
				// BaseActivity.this.finish();
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
					pd = new ProgressDialog(mContext);
					// pd.setTitle("加载图片");
				}
				pd.setMessage(" 加载中... ");
				pd.show();
			}
		};

		runOnUiThread(action);
	}

	@Override
	protected void handleTitleBarEvent(int paramInt) {
		if (!isActivity) {
			MainTabActivity.getCurrentActivityGroup().setPreviousActivity();
		} else {
			finish();
		}
	}

	private void howToRegist() {
		Intent intent = new Intent(this, RegisterActivity.class);
		startActivity(intent);
	}

	private void forgetPwd() {
		Intent intent = new Intent(this, ForgetPwdActivity.class);
		startActivity(intent);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.register:
			howToRegist();
			break;
		case R.id.forget_pwd:
			forgetPwd();
			break;
		case R.id.btn_login:
			if ((mUsername.getText().toString() == null || mUsername.getText()
					.toString().length() == 0)
					&& ((mPassword.getText().toString() == null) || (mPassword
							.getText().toString().length() == 0))) {
				notifyToUser("请输入用户名和密码！");
				return;
			} else if (mUsername.getText().toString() == null
					|| mUsername.getText().toString().length() == 0) {
				notifyToUser("请输入用户名！");
				return;
			} else if (mPassword.getText().toString() == null
					|| mPassword.getText().toString().length() == 0) {
				notifyToUser("请输入密码！");
				return;
			}

			new LoginTask().execute(mUsername.getText().toString(), mPassword
					.getText().toString());
			return;
		default:
			break;
		}

		super.onClick(view);
	}

	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			mAutoLogin.setButtonDrawable(R.drawable.check_box_selected);
			mIsAutoLogin = true;
		} else {
			mAutoLogin.setButtonDrawable(R.drawable.check_box_not_selected);
			mIsAutoLogin = false;
		}
	}
}