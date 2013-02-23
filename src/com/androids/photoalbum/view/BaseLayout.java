package com.androids.photoalbum.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.photoalbum.R;
import com.androids.photoalbum.utils.NetWorkUtils;
import com.androids.photoalbum.utils.Utils;

public class BaseLayout extends LinearLayout implements View.OnTouchListener {
	// public static final int TYPE_BUTTON_GROUP = 0;
	public static final int TYPE_GONE = 0;

	public static final int TYPE_NORMAL = 1;

	public static final int TYPE_HOME = 2;

	public TextView registButton;

	public LinearLayout lyButtonGroup;

	public Button mButton1;

	public Button mButton2;

	public Button mButton3;

	public Button leftButton;

	public Button rightButton;

	public Button midButton;

	public RelativeLayout progressButton;

	public TextView loginButton;

	public TextView tvInfo;

	private void loadLogo() {
		AsyncTask<Void, Void, Void> logoTask = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				try {
					Bitmap bm = Utils.getBitMapFromNetwork(
							NetWorkUtils.LOGO_URI, mContext);
					if (bm != null) {
						YC.mLogo = new BitmapDrawable(bm);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				if (YC.mLogo != null) {
					setBackgroundDrawable(YC.mLogo);
				}
			}

		};

		logoTask.execute();
	}

	public BaseLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		Log.d("zheng", "BaseLayout Context context, AttributeSet attrs");
		leftButton = (Button) findViewById(R.id.left_button);
		rightButton = (Button) findViewById(R.id.right_button);
		midButton = (Button) findViewById(R.id.mid_button);
		if (YC.mLogo != null) {
			setBackgroundDrawable(YC.mLogo);
		} else {
			loadLogo();
		}
	}

	public boolean onTouch(View v, MotionEvent event) {
		if (v == null) {
			return false;
		}
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			v.getBackground().setAlpha(155);// 璁剧疆鍥剧墖閫忔槑搴?~255锛?瀹屽叏閫忔槑锛?55涓嶉€忔槑
			v.invalidate();
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			v.getBackground().setAlpha(255);// 杩樺師鍥剧墖
			v.invalidate();
		}

		return false;
	}

	private Context mContext;

	public BaseLayout(Context paramContext, int resId) {
		super(paramContext);
		mContext = paramContext;
		Log.d("zheng", "BaseLayout Context paramContext, int resId");
	}

	public void setButtonTypeAndInfo(int titleType, String leftText,
			String title, String rightText) {
		// this.lyButtonGroup.setVisibility(GONE);
		Resources resource = getResources();
		switch (titleType) {
		case TYPE_NORMAL:
			if (TextUtils.isEmpty(leftText)) {
				this.leftButton.setVisibility(GONE);
			} else if (leftText.equalsIgnoreCase("返回")) {
				leftButton.setBackgroundDrawable(resource
						.getDrawable(R.drawable.title_button));
				this.leftButton.setText(leftText);
			}

			if (!TextUtils.isEmpty(title)) {
				setTitle(title);
			}

			if (TextUtils.isEmpty(rightText)) {
				rightButton.setVisibility(GONE);
			} else if (rightText.equalsIgnoreCase("刷新")) {
				rightButton.setVisibility(VISIBLE);
				rightButton.setText(rightText);
			} else if (rightText.equalsIgnoreCase("返回")) {
				rightButton.setVisibility(VISIBLE);
				rightButton.setText(rightText);
			}

			if (title != null && "推荐给好友".equals(title)) {
				midButton.setVisibility(View.VISIBLE);
			}
			break;
		case TYPE_GONE:

			break;
		case TYPE_HOME:

		default:
		}
	}

	public void setTitle(String paramString) {
		this.tvInfo.setText(paramString);
	}
}
