package com.xhm.q3.view;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.photoalbum.R;
import com.androids.photoalbum.netinfo.PassWordVerifyLocalSiteInfo;
import com.androids.photoalbum.netinfo.ServerInfo;
import com.androids.photoalbum.netinfo.WebsiteInfo;
import com.androids.photoalbum.parser.PassWordVerifyWebsiteParser;
import com.androids.photoalbum.utils.NetWorkUtils;
import com.androids.photoalbum.view.MainTabActivity;
import com.androids.photoalbum.view.PictureWorkingActivity;
import com.androids.photoalbum.view.SignNameActivity;

public class q3_function_activity extends Activity implements OnTouchListener {
	private ImageView mImageView_sms, mImageView_info, mImageView_youcai,
			mImageView, mImageView_word, mImageView_erweima,
			mImageView_progress, mImageView_login, mImageView_exit;
	public static ImageView mImageView_login_value;
	private Camera mCamera;
	private Dialog mDialog_login, mDialog_login_regist;
	public static WebsiteInfo mWebsiteInfo = null;
	private ProgressDialog mDialog;
	private Context mContext;
	PassWordVerifyLocalSiteInfo localInfo;
	private Handler mHandler;
	// 验证登录的线程
	Thread thread = new Thread(new Runnable() {
		@Override
		public void run() {
			mWebsiteInfo = new PassWordVerifyWebsiteParser().parseWebsiteInfo(
					NetWorkUtils.IS_EXIST_USER, localInfo);
			if (ServerInfo.SUCCESS.equals(mWebsiteInfo.result)) {
				mDialog.dismiss();
				mDialog_login.dismiss();
				mHandler.sendEmptyMessage(0);
				notifyToUser("登录成功!");
				return;
			} else {
				mDialog.dismiss();
				mDialog_login.dismiss();
				notifyToUser("登录失败!");
				return;
			}

		}
	});

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.q_3_function_page);
		createDialog();
		initVar();
		initView();
		SharedPreferences editor = getSharedPreferences(
				MainTabActivity.USER_INFO, MODE_WORLD_READABLE);
		String name = editor.getString("username", "-1");
		if (("-1" != (name)) && (name.length() != 0)) {
			mImageView_login_value
					.setImageResource(R.drawable.q3_function_page_login_ok);
		}
	}

	// 初始化成员变量
	private void initVar() {
		mContext = this;
		mCamera = new Camera();
		localInfo = new PassWordVerifyLocalSiteInfo();
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				mImageView_login_value
						.setImageResource(R.drawable.q3_function_page_login_ok);
			}
		};

	}

	// 控件初始化
	private void initView() {
		mImageView_login_value = (ImageView) findViewById(R.id.q3_login);

		mImageView_sms = (ImageView) findViewById(R.id.q3_msm);
		mImageView_sms.setOnTouchListener(this);

		mImageView_info = (ImageView) findViewById(R.id.q3_info);
		mImageView_info.setOnTouchListener(this);

		mImageView_youcai = (ImageView) findViewById(R.id.q3_youcai);
		mImageView_youcai.setOnTouchListener(this);

		mImageView = (ImageView) findViewById(R.id.q3_page);
		mImageView.setOnTouchListener(this);

		mImageView_word = (ImageView) findViewById(R.id.q3_word);
		mImageView_word.setOnTouchListener(this);

		mImageView_erweima = (ImageView) findViewById(R.id.q3_erweima);
		mImageView_erweima.setOnTouchListener(this);

		mImageView_progress = (ImageView) findViewById(R.id.q3_process);
		mImageView_progress.setOnTouchListener(this);

		mImageView_login.setOnTouchListener(login_listener);
		mImageView_exit.setOnTouchListener(login_listener);
	}

	// 控件的Touch事件,抬起的时候进行功能跳转
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			System.out.println("down");
			camera_info(0.02f, getImageView_id(v.getId()), (ImageView) v);
			break;
		case MotionEvent.ACTION_UP:
			camera_info(0f, getImageView_id(v.getId()), (ImageView) v);
			Intent intent = null;
			switch (v.getId()) {
			case R.id.q3_msm:
				// intent = new Intent(this, q3_msm_activity.class);
				intent = new Intent(this, MainTabActivity.class);
				startActivity(intent);
				break;
			case R.id.q3_info:
				intent = new Intent(this, q3_info_model_activity.class);
				startActivity(intent);
				break;
			case R.id.q3_youcai:
				intent = new Intent(this, q3_youcai_activity.class);
				startActivity(intent);
				break;
			case R.id.q3_page:

				break;
			case R.id.q3_word:
				intent = new Intent(this, SignNameActivity.class);
				startActivity(intent);
				break;
			case R.id.q3_erweima:
				intent = new Intent(this, q3_erweima_activity.class);
				startActivity(intent);
				break;
			case R.id.q3_process:
				intent = new Intent(this, PictureWorkingActivity.class);
				startActivity(intent);
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}
		return true;
	}

	// 控件的点击事件，跳转到登录页面
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.q3_login:
			mDialog_login.show();
			break;
		default:
			break;
		}
	}

	// 创建登录的全屏Dialog界面
	private void createDialog() {
		mDialog_login = new Dialog(this, android.R.style.Theme_NoTitleBar);
		mDialog_login.setContentView(R.layout.q3_login_page);
		mImageView_login = (ImageView) mDialog_login
				.findViewById(R.id.q3_button_login);
		mImageView_exit = (ImageView) mDialog_login
				.findViewById(R.id.q3_button_exit);
	}

	// 登录页面的登录和注册按钮的点击监听
	private OnTouchListener login_listener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			Intent intent = null;
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				System.out.println("down");
				if (R.id.q3_button_login == v.getId()) {
					System.out.println("login");
					mImageView_login.setAlpha(100);
				} else if (R.id.q3_button_exit == v.getId()) {
					System.out.println("regist");
					mImageView_exit.setAlpha(100);
				}
				break;
			case MotionEvent.ACTION_UP:
				System.out.println("up");
				// 登录事件
				if (R.id.q3_button_login == v.getId()) {
					mImageView_login.setAlpha(255);
					// 获得用户名和密码
					SharedPreferences editor = getSharedPreferences(
							MainTabActivity.USER_INFO, MODE_WORLD_READABLE);
					String auto_login = editor.getString("auto_login", "-2");
					String name = editor.getString("username", "-1");
					String pwd = editor.getString("password", "-1");
					System.out.println("name" + name + "     " + "pwd" + pwd);
					System.out.println("auto_login===" + auto_login);
					if (auto_login.equals("1")) {
						System.out.println("show");
						// 显示dialog
						showProgessBarDialog("Loading...");
						localInfo.username = name;
						localInfo.pwd = pwd;
						thread.start();
						System.out.println("info_qian");

					} else {
						mDialog_login.dismiss();
						intent = new Intent(q3_function_activity.this,
								q3_login_activity.class);
						startActivity(intent);
					}

				}// 注册事件
				else if (R.id.q3_button_exit == v.getId()) {
					mImageView_exit.setAlpha(255);
					mDialog_login_regist = new Dialog(
							q3_function_activity.this,
							android.R.style.Theme_NoTitleBar);
					ImageView _imageView = new ImageView(
							q3_function_activity.this);
					_imageView.setImageResource(R.drawable.regist);
					mDialog_login_regist.setContentView(_imageView);
					mDialog_login.dismiss();
					mDialog_login_regist.show();
				}
				break;
			default:
				break;
			}
			return true;
		}
	};

	// 图片变形的方法
	private void camera_info(float a, int imagepaht, ImageView view) {
		mCamera.save();
		Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(imagepaht))
				.getBitmap();
		Matrix matrix = new Matrix();
		mCamera.getMatrix(matrix);
		// 恢复到初始化
		mCamera.restore();
		matrix.preSkew(0f, a);
		try {
			Bitmap newbitmap = Bitmap.createBitmap(bitmap, 0, 0,
					bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			view.setImageBitmap(newbitmap);
		} catch (Exception e) {
			System.out.println("出问题的Y的");
		}
	}

	// 根据点击图片的ID获得imageview图片
	private int getImageView_id(int ImageView_id) {
		switch (ImageView_id) {
		case R.id.q3_msm:
			return R.drawable.q3_function_page_msm;
		case R.id.q3_info:
			return R.drawable.q3_function_page_info;
		case R.id.q3_erweima:
			return R.drawable.q3_function_page_erweima;
		case R.id.q3_youcai:
			return R.drawable.q3_function_page_video;
		case R.id.q3_word:
			return R.drawable.q3_function_page_word;
		case R.id.q3_process:
			return R.drawable.q3_function_page_process;
		case R.id.q3_page:
			return R.drawable.q3_function_page;
		default:

			break;
		}
		return 0;
	}

	// 通知用户
	protected void notifyToUser(final String result) {
		Runnable run = new Runnable() {

			// @Override
			public void run() {
				Toast.makeText(q3_function_activity.this, result,
						Toast.LENGTH_SHORT).show();
			}
		};

		runOnUiThread(run);
	}

	// 显示等待Dialog
	public void showProgessBarDialog(final String msg) {
		Runnable action = new Runnable() {

			public void run() {
				if (mDialog == null) {
					mDialog = new ProgressDialog(mContext);
				}
				mDialog.setMessage("登录中... ");
				mDialog.show();
			}
		};

		runOnUiThread(action);
	}
}
