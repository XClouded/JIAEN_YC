package com.androids.photoalbum.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;

import com.android.photoalbum.R;
import com.androids.photoalbum.tab.ui.BaseActivity;
import com.androids.photoalbum.tab.ui.FavoriteActivity;
import com.androids.photoalbum.tab.ui.HomeActivity;
import com.androids.photoalbum.tab.ui.MultiMediaMessageActivity;
import com.androids.photoalbum.tab.ui.SearchActivity;
import com.androids.photoalbum.tab.ui.TabActivityGroup;
import com.androids.photoalbum.tab.ui.WorkingActivity;
import com.androids.photoalbum.userinfo.StaticInfo;
import com.itcast.db.TokenUtil;
import com.itcast.logic.MainService;
import com.itcast.weibo.sina.User;
import com.xhm.q3.view.q3_login_activity;

public class MainTabActivity extends TabActivity implements
		CompoundButton.OnCheckedChangeListener {
	public final static String USER_INFO = "user_info";
	public static final String KEY_NICKNAME = "nickname";
	public static final int MODE_AT = 5;
	public static final int MODE_CELEBRITY = 12;
	public static final int MODE_COMMENT = 4;
	public static final int MODE_DEFAULT = 255;
	public static final int MODE_FAMOUS = 8;
	public static final int MODE_FAVORITE = 5;
	public static final int MODE_GRASSROOT = 7;
	public static final int MODE_GUESS = 16;
	public static final int MODE_HOME = 0;
	public static final int MODE_HOTCOMMENT = 14;
	public static final int MODE_HOTFORWARD = 11;
	public static final int MODE_HOTTOPIC = 13;
	public static final String MODE_KEY = "MODE_KEY";
	public static final int MODE_LOOKAROUND = 9;
	public static final int MODE_MBLOG = 0;
	public static final int MODE_MESSAGE = 6;
	public static final int MODE_MORE = 3;
	public static final int MODE_MYBLOG = 4;
	public static final int MODE_POPULACE = 15;
	public static final int MODE_SEARCH = 2;
	public static final int MODE_TOPIC = 11;
	public static final int MODE_TOPUSER = 10;
	public static final int MODE_USER = 10;
	public static final int MODE_USERINFO = 1;
	private static final int OEM_FACTORY = 0;
	private static final int OEM_SONY_ERICSSON = 1;
	public static final int SWITCHUSER = 0;
	public static final int TOTAL_AT = 3;
	public static final int TOTAL_COMMENT = 1;
	public static final int TOTAL_MBLOG = 0;
	public static final int TOTAL_MESSAGE = 2;
	public static boolean beSwitchuser;
	private static int height;
	public static boolean mAtReload;
	public static boolean mCommentReload;
	public static boolean mFansReload;
	public static boolean mHomeReload;
	public static boolean mIsFinished;
	public static boolean mMessageReload;
	public static int mMode;
	public static boolean mPrefAutoCheck = false;
	public static boolean mPrefAutoloadMore;
	public static int mPrefInterval;
	public static boolean mPrefNotifyAt;
	public static boolean mPrefNotifyComment;
	public static boolean mPrefNotifyFans;
	public static boolean mPrefNotifyMessage;
	public static boolean mPrefNotifyNewblog;
	public static int mPrefPictureSize;
	public static boolean mPrefShowPicture;
	public static boolean mPrefVibrator;
	public static boolean mScreenOrientation;
	public static boolean mSetLookTop;
	public static boolean mSetSwitchTop;
	public static boolean mSwitchUserOnTop;
	public static boolean mTabSetup;
	public static boolean mWeiboFinish;
	private static int width;
	private String blogId = null;
	private TabHost mHost;
	private Intent mManageIntent;
	private Intent mHomeIntent;
	private Intent mWorkingIntent;
	private Intent mMessageIntent;
	private RadioButton[] mRadioButtons;
	private Intent mSearchIntent;
	private BroadcastReceiver mSwitchModeReceiver;
	private Intent mFavoriteIntent;
	public Vibrator mVibrator;
	private boolean mBackHomePage = true;
	public ViewGroup vg;
	public static Context mContext;

	private final static String HOME_TAB = "home_tab";
	private final static String SEARCH_TAB = "search_tab";
	private final static String FAVORITE_TAB = "favorite_tab";
	private final static String WORKING_TAB = "working_tab";
	private final static String MESSAGE_TAB = "message_tab";

	private final static int REQUEST_LOGIN = -500;
	public final static int CANCEL_LOGIN = 1;

	static {
		beSwitchuser = false;
		mMode = 0;
		mTabSetup = false;
		mSwitchUserOnTop = false;
		mHomeReload = false;
		mCommentReload = false;
		mAtReload = false;
		mMessageReload = false;
		mFansReload = false;
		mSetSwitchTop = false;
		mSetLookTop = false;
		mWeiboFinish = false;
		mIsFinished = false;
		width = 0;
		height = 0;
	}

	public MainTabActivity() {
	}

	public static void ReverseReloadVariant() {
		mHomeReload = false;
		mCommentReload = false;
		mAtReload = false;
		mMessageReload = false;
		mFansReload = false;
	}

	public static int getWindowHeight() {
		return height;
	}

	public static int getWindowWidth() {
		return width;
	}

	// 初始化tab按钮
	private void initRadios() {
		RadioGroup localRadioGroup = (RadioGroup) findViewById(R.id.radio_group);
		mRadioButtons = new RadioButton[5];
		for (int i = 0; i < 5; i++) {
			if (1 == i || 2 == i) {
				System.out.println("iii===" + i);
			} else {
				String str = "radio_button" + i;
				mRadioButtons[i] = (RadioButton) localRadioGroup
						.findViewWithTag(str);
				mRadioButtons[i].setOnCheckedChangeListener(this);
			}
		}
	}

	private void loadSettings() {
		SharedPreferences userLoginPreference = getSharedPreferences(USER_INFO,
				0);
		StaticInfo.mUsername = userLoginPreference.getString("username", "");
		StaticInfo.mPassword = userLoginPreference.getString("password", "");
		StaticInfo.userid = userLoginPreference.getString("server_userid", "");
		StaticInfo.username = userLoginPreference.getString("server_username",
				"");
		Log.d("zheng", "username:" + StaticInfo.mUsername + " password:"
				+ StaticInfo.mPassword);
	}

	private void setDisplay() {
		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		Display localDisplay = getWindowManager().getDefaultDisplay();
		localDisplay.getMetrics(localDisplayMetrics);
		int i = localDisplay.getWidth();
		int j = localDisplay.getHeight();
		// int k = localDisplayMetrics.densityDpi;
	}

	/**
	 * 为Tab添加标签图片和对应的Fragment中的内容
	 */
	private void setupIntent() {
		mHost.clearAllTabs();

		TabHost.TabSpec localTabSpec1 = mHost.newTabSpec(HOME_TAB);
		String str2 = "首页";// getString(2131427368);
		Drawable localDrawable1 = getResources().getDrawable(
				R.drawable.home_not_selected_bg);
		TabHost.TabSpec localTabSpec2 = localTabSpec1.setIndicator(str2,
				localDrawable1);
		TabHost.TabSpec localTabSpec3 = localTabSpec2.setContent(mHomeIntent);
		mHost.addTab(localTabSpec3);

		TabHost.TabSpec localTabSpec4 = mHost.newTabSpec(WORKING_TAB);
		String str3 = "浏览";// getString(2131427372);
		Drawable localDrawable2 = getResources().getDrawable(
				R.drawable.modify_not_selected_bg);
		TabHost.TabSpec localTabSpec5 = localTabSpec4.setIndicator(str3,
				localDrawable2);
		TabHost.TabSpec localTabSpec6 = localTabSpec5.setContent(mHomeIntent);
		mHost.addTab(localTabSpec6);

		TabHost.TabSpec localTabSpec7 = mHost.newTabSpec(MESSAGE_TAB);
		String str4 = "彩信";// getString(2131427449);
		Drawable localDrawable3 = getResources().getDrawable(
				R.drawable.message_not_selected_bg);
		TabHost.TabSpec localTabSpec8 = localTabSpec7.setIndicator(str4,
				localDrawable3);
		TabHost.TabSpec localTabSpec9 = localTabSpec8
				.setContent(mMessageIntent);
		mHost.addTab(localTabSpec9);

		TabHost.TabSpec localTabSpec13 = mHost.newTabSpec(SEARCH_TAB);
		Drawable localDrawable5 = getResources().getDrawable(
				R.drawable.search_bg);
		TabHost.TabSpec localTabSpec14 = localTabSpec13.setIndicator("搜索",
				localDrawable5);
		TabHost.TabSpec localTabSpec15 = localTabSpec14
				.setContent(mSearchIntent);
		mHost.addTab(localTabSpec15);

		TabHost.TabSpec localTabSpec10 = mHost.newTabSpec(FAVORITE_TAB);
		String str5 = "收藏";// getString(2131427358);
		Drawable localDrawable4 = getResources().getDrawable(
				R.drawable.store_not_selected_bg);
		TabHost.TabSpec localTabSpec11 = localTabSpec10.setIndicator(str5,
				localDrawable4);
		TabHost.TabSpec localTabSpec12 = localTabSpec11
				.setContent(mFavoriteIntent);
		mHost.addTab(localTabSpec12);

		mTabSetup = true;
	}

	protected Dialog createConfirmQuitDialog() {
		return new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setMessage("确认退出微博?")
				.setPositiveButton(R.string.okay_action,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dismissDialog(BaseActivity.DIALOG_CONFIRM_QUIT_YC);
								android.os.Process
										.killProcess(android.os.Process.myPid());
							}
						})
				.setNegativeButton(R.string.cancel_action,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dismissDialog(BaseActivity.DIALOG_CONFIRM_QUIT_YC);
							}
						}).create();
	}

	private void changeForActivityGroup() {
		TabActivityGroup activityGroup = null;
		Class<?> cls = null;
		String id = null;
		switch (mCurrentPage) {
		case 0:
			activityGroup = mHomeActivityGroup;
			cls = HomeActivity.class;
			id = "HomeActivity";
			System.out.println("change_0");
			break;
		case 1:
			activityGroup = mWorkingActivityGroup;
			cls = WorkingActivity.class;
			id = "WorkingActivity";
			System.out.println("change_1");
			break;
		case 2:
			activityGroup = mMultiMessageActivityGroup;
			cls = MultiMediaMessageActivity.class;
			id = "MultiMediaMessageActivity";
			break;
		case 3:
			activityGroup = mSearchActivityGroup;
			cls = SearchActivity.class;
			id = "SearchActivity";
			break;
		case 4:
			activityGroup = mFavoriteActivityGroup;
			System.out.println("StaticInfo.mPassword=" + StaticInfo.mPassword
					+ "----------" + "StaticInfo.mPassword="
					+ StaticInfo.mPassword);
			if (StaticInfo.mPassword == null
					|| StaticInfo.mPassword.length() == 0) {
				cls = LoginActivity.class;
				id = "LoginActivity";
			} else {
				cls = FavoriteActivity.class;
				id = "FavoriteActivity";
			}
			break;
		default:
			return;
		}
		System.out.println("activityGroup==" + activityGroup);
		System.out.println("activityGroup.isActivityGroupNull()=="
				+ activityGroup.isActivityGroupNull());
		if (activityGroup != null && activityGroup.isActivityGroupNull()) {
			System.out.println("shang");
			Intent intent = new Intent(this, cls);
			activityGroup.setNextActivity(intent, id);
		} else if (activityGroup != null) {
			System.out.println("xia");
			activityGroup.setCurrentActivity();
		}
	}

	public static TabActivityGroup getCurrentActivityGroup() {
		System.out.println("mCurrentPage" + mCurrentPage);
		switch (mCurrentPage) {
		case 0:
			return mHomeActivityGroup;
		case 1:
			return mWorkingActivityGroup;
		case 2:
			return mMultiMessageActivityGroup;
		case 3:
			return mSearchActivityGroup;
		case 4:
			return mFavoriteActivityGroup;
		default:
			return mHomeActivityGroup;
		}
	}

	int homemode = 0;
	public static int mCurrentPage = 0;
	public static TabActivityGroup mHomeActivityGroup = null;
	public static TabActivityGroup mSearchActivityGroup = null;
	public static TabActivityGroup mFavoriteActivityGroup = null;
	public static TabActivityGroup mMultiMessageActivityGroup = null;
	public static TabActivityGroup mWorkingActivityGroup = null;

	public void onCheckedChanged(CompoundButton paramCompoundButton,
			boolean paramBoolean) {
		if ((paramBoolean) && (this.mHost != null)) {
			if (paramCompoundButton == mRadioButtons[0]) {
				mCurrentPage = 0;
				System.out.println("tab_0");
				// if (mHomeActivityGroup.isActivityGroupNull()) {
				// Intent intent = new Intent(this, HomeActivity.class);
				// mHomeActivityGroup.setNextActivity(intent, "HomeActivity");
				// } else {
				// mHomeActivityGroup.setCurrentActivity();
				// }

				mHost.setCurrentTabByTag(HOME_TAB);
				changeForActivityGroup();
			} else if (paramCompoundButton == mRadioButtons[1]) {
				mCurrentPage = 1;
				System.out.println("tab_1");
				// if (mSearchActivityGroup.isActivityGroupNull()) {
				// Intent intent = new Intent(this, SearchActivity.class);
				// mSearchActivityGroup.setNextActivity(intent,
				// "SearchActivity");
				// } else {
				// mSearchActivityGroup.setCurrentActivity();
				// }
				mHost.setCurrentTabByTag(WORKING_TAB);
				changeForActivityGroup();
			} else if (paramCompoundButton == mRadioButtons[2]) {
				mCurrentPage = 2;
				// if (mFavoriteActivityGroup.isActivityGroupNull()) {
				// Intent intent = new Intent(this, FavoriteActivity.class);
				// mFavoriteActivityGroup.setNextActivity(intent,
				// "FavoriteActivity");
				// } else {
				// mFavoriteActivityGroup.setCurrentActivity();
				// }
				mHost.setCurrentTabByTag(MESSAGE_TAB);
				changeForActivityGroup();
			} else if (paramCompoundButton == mRadioButtons[3]) {
				mCurrentPage = 3;
				mHost.setCurrentTabByTag(SEARCH_TAB);
				changeForActivityGroup();
			} else if (paramCompoundButton == mRadioButtons[4]) {
				mCurrentPage = 4;
				mHost.setCurrentTabByTag(FAVORITE_TAB);
				changeForActivityGroup();
			} else if (paramCompoundButton == mRadioButtons[5]) {
				mHost.setCurrentTabByTag("search_tab");
			} else if (paramCompoundButton == mRadioButtons[6]) {
				mHost.setCurrentTabByTag("search_tab");
			} else if (paramCompoundButton == mRadioButtons[7]) {
				mHost.setCurrentTabByTag("message_tab");
			} else {
				mMode = 3;
				mHost.setCurrentTabByTag("more_tab");
			}
		}
	}

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		restoreLoginInfo();
		mContext = this;
		mCurrentPage = 0;
		// 获得显示器的大小和dip
		setDisplay();
		// 注册广播接受者
		IntentFilter localIntentFilter = new IntentFilter();
		localIntentFilter.addAction("com.sina.weibo.action.SWITCHMODE");
		localIntentFilter.addAction("com.sina.weibo.action.SWITCHUSER");
		localIntentFilter.addAction("sina.weibo.action.SWITCHUSERDONE");
		BroadcastReceiver localBroadcastReceiver = this.mSwitchModeReceiver;
		registerReceiver(localBroadcastReceiver, localIntentFilter);

		mTabSetup = false;
		mSetSwitchTop = false;
		mIsFinished = false;
		// setContentView(R.layout.maintabs);

		// Q3——新的界面
		setContentView(R.layout.q3_msm_layout);
		mHost = getTabHost();

		// mHomeIntent = new Intent(this, HomeActivity.class);
		mHomeIntent = new Intent(this, TabActivityGroup.class);
		mSearchIntent = new Intent(this, TabActivityGroup.class);
		mManageIntent = new Intent(this, TabActivityGroup.class);
		mFavoriteIntent = new Intent(this, TabActivityGroup.class);
		// mFavoriteIntent.putExtra("my_tab_info", true);
		mWorkingIntent = new Intent(this, TabActivityGroup.class);
		mMessageIntent = new Intent(this, TabActivityGroup.class);
		initRadios();
		setupIntent(); // thisline should be deleted
		loadSettings();

		mBackHomePage = false;
		mHost.setCurrentTabByTag(HOME_TAB);
		updateRadioButtonStatus(HOME_TAB);
		changeForActivityGroup();
	}

	protected void onDestroy() {
		// SharedPreferences.Editor editor = getSharedPreferences(USER_INFO,
		// MODE_WORLD_READABLE).edit();
		// editor.putString("current_tab", HOME_TAB);
		// editor.commit();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// SharedPreferences.Editor editor = getSharedPreferences(USER_INFO,
		// MODE_WORLD_READABLE).edit();
		// editor.putString("current_tab", mHost.getCurrentTabTag());
		// editor.commit();
	}

	protected void onNewIntent(Intent paramIntent) {
		if (paramIntent.getAction() != null) {
			if ("com.android.microblog.action.go_back_to_homepage"
					.equals(paramIntent.getAction())) {
				mBackHomePage = true;
			} else if (BaseActivity.ACTION_MESSAGE_COMPOSE.equals(paramIntent
					.getAction())) {
				String productid = paramIntent.getStringExtra("productid");
				mMessageIntent.putExtra("productid", productid);

				mRadioButtons[0].setChecked(false);
				mHost.setCurrentTabByTag(MESSAGE_TAB);
				updateRadioButtonStatus(MESSAGE_TAB);

				// mHost.setCurrentTabByTag(MESSAGE_TAB);
				// mRadioButtons[4].setChecked(true);
				// mRadioButtons[4].toggle();
				// updateRadioButtonStatus(MESSAGE_TAB);
			}
		}
	}

	/*
	 * protected void onPause() { super.onPause(); this.mBeBackground = true; if
	 * (StaticInfo.mUser != null) { int i = mMode; String str = this.mCacheDir;
	 * User localUser = StaticInfo.mUser; Utils.saveModeFile(i, str, localUser);
	 * } if (isFinishing()) { mPrefAutoCheck = 0; mIsFinished = 1; Intent
	 * localIntent = new Intent(this, WeiboEventService.class); boolean bool =
	 * stopService(localIntent); } }
	 */
	void updateRadioButtonStatus(String curTab) {
		if (HOME_TAB.equals(curTab)) {
			if (!mRadioButtons[0].isChecked()) {
				mRadioButtons[0].toggle();
			}
		} else if (WORKING_TAB.equals(curTab)) {
			if (!mRadioButtons[1].isChecked()) {
				mRadioButtons[1].toggle();
			}
		} else if (MESSAGE_TAB.equals(curTab)) {
			if (!mRadioButtons[2].isChecked()) {
				mRadioButtons[2].toggle();
			}
		} else if (SEARCH_TAB.equals(curTab)) {
			if (!mRadioButtons[3].isChecked()) {
				mRadioButtons[3].toggle();
			}
		} else if (FAVORITE_TAB.equals(curTab)) {
			if (!mRadioButtons[4].isChecked()) {
				mRadioButtons[4].toggle();
			}
		}
	}

	boolean isNoUserStatus() {
		if (StaticInfo.mUsername == null || StaticInfo.mPassword == null) {
			return true;
		}

		if (StaticInfo.mUsername.length() == 0
				|| StaticInfo.mPassword.length() == 0) {
			return true;
		}

		return false;
	}

	@Override
	public void onBackPressed() {
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	protected void onResume() {
		Log.d("zheng", "MainTabActiviy onResume mWeiboFinish: " + mWeiboFinish);
		if (mWeiboFinish) {
			Log.d("zheng", "finish Maintab acitivity");
			super.onResume();
			mWeiboFinish = false;
			finish();
			return;
		}

		Log.d("zheng", "MainTabActivity onResume");
		// if (isNoUserStatus()) {
		// Log.d("zheng", "MainTabActivity onResume no user, need login");
		// Intent loginIntent = new Intent(this, HomeActivity.class);
		// startActivityForResult(loginIntent, REQUEST_LOGIN);
		// super.onResume();
		// return;
		// }
		// LoginActivity.login_test();
		if (mHomeReload) {
			Intent intent = new Intent(
					BaseActivity.ACTION_RELOAD_AFTER_SWITCH_USER);
			sendBroadcast(intent);
			mHomeReload = false;
		}

		// String currentTab = HOME_TAB;
		// if (mBackHomePage) {
		// currentTab = HOME_TAB;
		// mBackHomePage = false;
		// mCurrentPage = 0;
		// } else {
		// SharedPreferences sharedPre = getSharedPreferences(USER_INFO,
		// MODE_WORLD_READABLE);
		// currentTab = sharedPre.getString("current_tab", HOME_TAB);
		// if (currentTab.equals(HOME_TAB)) {
		// mCurrentPage = 0;
		// } else if (currentTab.equals(SEARCH_TAB)) {
		// mCurrentPage = 1;
		// } else if (currentTab.equals(FAVORITE_TAB)) {
		// mCurrentPage = 2;
		// } else if (currentTab.equals(MESSAGE_TAB)) {
		// mCurrentPage = 3;
		// } else if (currentTab.equals(WORKING_TAB)) {
		// mCurrentPage = 4;
		// }
		// }
		//
		// mHost.setCurrentTabByTag(currentTab);
		// updateRadioButtonStatus(currentTab);
		super.onResume();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (REQUEST_LOGIN == requestCode) {
			if (resultCode == CANCEL_LOGIN) {
				Log.d("zheng", "MainTabActivity CANCEL_LOGIN");
				mWeiboFinish = false;
				finish();
			} else if (resultCode == RESULT_OK) {
				String name = data.getStringExtra("name");
				String password = data.getStringExtra("pwd");
				String nickName = data.getStringExtra("nick_name");
				SharedPreferences.Editor editor = getSharedPreferences(
						USER_INFO, MODE_WORLD_READABLE).edit();
				StaticInfo.mUsername = name;
				StaticInfo.mPassword = password;
				Log.d("zheng", "name:" + name + " pwd:" + password
						+ " nickname:" + nickName);
				editor.putString("username", StaticInfo.mUsername);
				editor.putString("password", StaticInfo.mPassword);
				editor.putString("nickname", StaticInfo.mNickName);
				editor.commit();
			}
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		return createConfirmQuitDialog();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == event.KEYCODE_BACK) {
			showDialog(BaseActivity.DIALOG_CONFIRM_QUIT_YC);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private Dialog createExitLoginDialog() {
		return new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setMessage("确认退出登录?")
				.setPositiveButton(R.string.okay_action,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dismissDialog(BaseActivity.DIALOG_LOGOUT_ACCOUNT);
								finish();
							}
						})
				.setNegativeButton(R.string.cancel_action,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dismissDialog(BaseActivity.DIALOG_LOGOUT_ACCOUNT);
							}
						}).create();
	}

	public void restoreLoginInfo() {
		Runnable run = new Runnable() {

			@Override
			public void run() {
				SharedPreferences userLoginPreference = getSharedPreferences(
						MainTabActivity.USER_INFO, MODE_WORLD_READABLE);
				User.mUserName = userLoginPreference.getString("sinausername",
						"");
				User.mPassWord = userLoginPreference.getString("sinapassword",
						"");

				MainService.mAccessToken = TokenUtil
						.readAccessToken(MainTabActivity.this);
				// OAuthConstant constant = OAuthConstant.getInstance();
				// constant.setAccessToken(MainService.mAccessToken);
			}
		};

		new Thread(run).start();
	}
}
