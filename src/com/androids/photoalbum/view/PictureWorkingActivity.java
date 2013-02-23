package com.androids.photoalbum.view;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.android.photoalbum.R;
import com.androids.photoalbum.netinfo.WebsiteInfo;
import com.androids.photoalbum.parser.EffectWebsiteParser;
import com.androids.photoalbum.parser.FrameWebsiteParser;
import com.androids.photoalbum.parser.TemplateWebsiteParser;
import com.androids.photoalbum.parser.WorkingPhotoWebsiteParser;
import com.androids.photoalbum.tab.ui.BaseActivity;
import com.androids.photoalbum.tab.ui.MessageActivity;
import com.androids.photoalbum.userinfo.StaticInfo;
import com.androids.photoalbum.utils.BASE64Encoder;
import com.androids.photoalbum.utils.GridViewAdapter;
import com.androids.photoalbum.utils.GridViewWebsiteParserTask;
import com.androids.photoalbum.utils.NetWorkUtils;
import com.androids.photoalbum.utils.Utils;
import com.ant.liao.GifView;
import com.ant.liao.GifView.GifImageType;
import com.ant.liao.TouchGifView;
import com.ant.liao.TouchTextView;

public class PictureWorkingActivity extends BaseActivity implements
		CompoundButton.OnCheckedChangeListener, AnimationListener,
		OnItemClickListener, TextWatcher {
	private Button[] mButtons;
	private Gallery mPicutureList;
	private PictureGalleryAdapter mDeleteGalleryAdapter;
	private Intent mPictureIntent;
	private Intent mCameraIntent;
	private GridView mGallery;
	private boolean mNeedConfirm = false;
	private GridViewAdapter mAdapter;
	public final static int GALLERY_FRAME_TYPE = 0;
	public final static int GALLERY_TEMPLATE_TYPE = 1;
	public final static int GALLERY_EFFECT_TYPE = 2;
	public final static int GALLERY_WORDS_TYPE = 3;
	private Intent mHomeIntent;
	private GifView mWorkingPhoto;
	private LinearLayout mLeftPanel;
	private LinearLayout mBottomPanel;

	private LinearLayout mBottomFunctionBar;

	private ImageButton mLeftButton;
	private ImageButton mRightButton;
	private ImageView mGalleryBar;
	private WebView mPhotoWebView;
	public static FrameContainer mFrameWebView;
	private FrameLayout mFrameLayout;
	private ImageButton mQuitButton;
	private ImageButton mBackButton;
	private TouchTextView mWords;
	int mPhotoCount = 2;
	private int mWordSize = 20;
	private String web_path = "file:///android_asset/";
	private static final int SELECT_PICTURE = 1;
	public final static String ACTION_GALLERY_NOTIFY_DATA_SET_CHANGED = "action_gallery_notify_data_set_changed";
	private AutoCompleteTextView mWordsContent;
	private boolean wordsAdded = false;

	private GridView mGridView;
	private HorizontalScrollView mScrollView;
	public static int cWidth = 120;
	public static int cHeight = 120;
	private int hSpacing = 10;

	private void findView() {
		mGridView = (GridView) findViewById(R.id.mGridView);
		mScrollView = (HorizontalScrollView) findViewById(R.id.mScrollView);
		mScrollView.setHorizontalScrollBarEnabled(false);// ���ع���
		// cWidth = mGridView.getHeight();
		cWidth = (int) getResources().getDimension(R.dimen.delete_with);
		cHeight = (int) getResources().getDimension(R.dimen.delete_height);
	}

	static class ViewHolder {
		ImageView mImg;
		ImageView mTxt;
	}

	GridAdapter mGridAdapter;

	private void setValue() {
		mGridAdapter = new GridAdapter(this, mPhotoArrayList);
		mGridView.setAdapter(mGridAdapter);
		LayoutParams params = new LayoutParams(mGridView.getCount()
				* (cWidth + hSpacing), LayoutParams.WRAP_CONTENT);
		mGridView.setLayoutParams(params);
		mGridView.setColumnWidth(cWidth);
		mGridView.setHorizontalSpacing(hSpacing);
		mGridView.setStretchMode(GridView.NO_STRETCH);
		mGridView.setNumColumns(mGridAdapter.getCount());
	}

	int pos = 0;

	private void setListener() {
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mPhotoArrayList.remove(position);
				mBase64Pictures.remove(position);
				mFrameWebView.removeViewAt(position);
				mGridAdapter.notifyDataSetChanged();
				pos = position;
			}
		});
	}

	class GridAdapter extends BaseAdapter {
		Context mContext;
		LayoutInflater mInflater;
		private ArrayList mPhotoArrayList;

		public GridAdapter(Context c, ArrayList arrayList) {
			mContext = c;
			mInflater = LayoutInflater.from(mContext);
			mPhotoArrayList = arrayList;
		}

		public int getCount() {
			Log.d("zheng", "getCount:" + mPhotoArrayList.size());
			// int size = mPhotoArrayList.size();
			// if (mPhotoArrayList.size() > 0 &&
			// ((View)mPhotoArrayList.get(0)).getVisibility() == View.GONE) {
			// size--;
			// }
			//
			// if (mPhotoArrayList.size() > 1 &&
			// ((View)mPhotoArrayList.get(1)).getVisibility() == View.GONE) {
			// size--;
			// }
			return mPhotoArrayList.size();
			// return size;
		}

		public Object getItem(int position) {
			return mPhotoArrayList.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View contentView, ViewGroup arg2) {
			// if (((View)mPhotoArrayList.get(0)).getVisibility() == View.GONE)
			// {
			// position++;
			// }
			//
			// if (((View)mPhotoArrayList.get(1)).getVisibility() == View.GONE)
			// {
			// position++;
			// }

			return (View) mPhotoArrayList.get(position);
		}
	}

	public static int windowHeight;
	public static int windowWidth;

	protected int screenWidth;
	protected int screenHeigth;
	protected float mScale = 0;

	@Override
	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setView(R.layout.picture_working_layout);

		setTitleBar(com.androids.photoalbum.view.BaseLayout.TYPE_GONE, null,
				null, null);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		screenHeigth = dm.heightPixels;
		mScale = screenWidth / screenHeigth;

		// mHost = getTabHost();
		// mWorkingPhoto = (GifView)findViewById(R.id.working_photo);
		mHomeIntent = new Intent(Intent.ACTION_GET_CONTENT);
		mGallery = (GridView) findViewById(R.id.gallery);
		LayoutParams params = new LayoutParams(mGallery.getCount()
				* (cWidth + 5), cWidth);
		mGallery.setLayoutParams(params);
		mGallery.setColumnWidth((int) getResources().getDimension(
				R.dimen.gallery_height));
		mGallery.setHorizontalSpacing(5);
		mGallery.setStretchMode(GridView.NO_STRETCH);

		// mGallery.getBackground().setAlpha(155);
		mGallery.setOnItemClickListener(this);

		mLeftPanel = (LinearLayout) findViewById(R.id.left_organize);
		mLeftButton = (ImageButton) findViewById(R.id.left_btn);
		mLeftButton.setOnTouchListener(this);
		mLeftButton.setOnClickListener(this);

		mRightButton = (ImageButton) findViewById(R.id.right_btn);
		mRightButton.setOnTouchListener(this);
		mRightButton.setOnClickListener(this);

		mBottomPanel = (LinearLayout) findViewById(R.id.bottom_gallery);
		mBottomPanel.getBackground().setAlpha(120);
		mGalleryBar = (ImageView) findViewById(R.id.keyboard_bar);
		mGalleryBar.setOnClickListener(this);
		mGalleryBar.setAlpha(150);

		mBottomFunctionBar = (LinearLayout) findViewById(R.id.bottom_function_bar);

		mImageWidth = (int) mResources.getDimension(R.dimen.working_image_with);
		mImageHeigth = (int) mResources
				.getDimension(R.dimen.working_image_height);

		initButtons();
		setupIntent();

		IntentFilter filter = new IntentFilter(
				ACTION_GALLERY_NOTIFY_DATA_SET_CHANGED);
		registerReceiver(mReceiver, filter);
		mFrameLayout = (FrameLayout) findViewById(R.id.frame_layout);
		// mPhotoWebView = (WebView) findViewById(R.id.webview_photo);

		mQuitButton = (ImageButton) findViewById(R.id.quit);
		mQuitButton.setOnClickListener(this);
		mQuitButton.setOnTouchListener(this);
		mBackButton = (ImageButton) findViewById(R.id.back);
		mBackButton.setOnClickListener(this);
		mBackButton.setOnTouchListener(this);
		windowHeight = getWindowManager().getDefaultDisplay().getHeight();
		windowWidth = getWindowManager().getDefaultDisplay().getWidth();
		Log.d("zheng", "System height: " + windowHeight + " System width: "
				+ windowWidth);

		mFrameWebView = (FrameContainer) findViewById(R.id.webview_frame);

		mImageWidth = (int) getResources().getDimension(
				R.dimen.working_image_with);
		mImageHeigth = (int) getResources().getDimension(
				R.dimen.working_image_height);

		LinearLayout colortable = (LinearLayout) findViewById(R.id.color_table);
		int count = colortable.getChildCount();
		for (int i = 0; i < count; i++) {
			View view = colortable.getChildAt(i);
			view.setOnClickListener(this);
		}

		// mWords = (TextView)findViewById(R.id.words);
		mWords = (TouchTextView) mInflater.inflate(R.layout.words, null);

		SeekBar seek = (SeekBar) findViewById(R.id.word_size);
		seek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			public void onStopTrackingTouch(SeekBar seekBar) {
				// myTextView2.setText("停止调节");
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				// myTextView2.setText("开始调节");
			}

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				mWordSize = progress;
				mWords.setTextSize(mWordSize + 8);
			}
		});

		mWordsContent = (AutoCompleteTextView) findViewById(R.id.words_content);
		mWordsContent.addTextChangedListener(this);

		mPicutureList = (Gallery) findViewById(R.id.record_list);
		mPicutureList.setOnItemClickListener(this);
		mDeleteGalleryAdapter = new PictureGalleryAdapter();
		mPicutureList.setAdapter(mDeleteGalleryAdapter);

		ImageView view = new ImageView(this);
		view.setVisibility(View.GONE);
		// mPhotoArrayList.add(0, view);
		// mPhotoArrayList.add(1, view);
		//
		// mBase64Pictures.add(0, "null");
		// mBase64Pictures.add(1, "null");

		// alignGalleryToLeft(mBottomPanel, mGallery);

		alignGalleryToLeft(mBottomFunctionBar, mPicutureList);

		findView();
		setValue();
		setListener();
	}

	private Bitmap getFillImage(Bitmap bitmap) {
		int scale = bitmap.getWidth() / bitmap.getHeight();
		if (mScale > scale) {
			bitmap = scaleImage(bitmap, bitmap.getWidth() * screenHeigth
					/ bitmap.getHeight(), screenHeigth);
			// mBlogContentPic.setMinimumWidth(bitmap.getWidth()*screenHeigth/bitmap.getHeight());
			// mBlogContentPic.setMinimumHeight(screenHeigth);
		} else {
			bitmap = scaleImage(bitmap, screenWidth, bitmap.getHeight()
					* screenWidth / bitmap.getWidth());
			// mBlogContentPic.setMinimumWidth(screenWidth);
			// mBlogContentPic.setMinimumHeight(bitmap.getHeight()*screenWidth/bitmap.getWidth());
		}

		return bitmap;
	}

	protected Bitmap scaleImage(Bitmap bitmap, int sWidth, int sHeight) {
		int bmpWidth = bitmap.getWidth();

		int bmpHeight = bitmap.getHeight();

		// 缩放图片的尺寸

		float scaleWidth = (float) sWidth / bmpWidth; // 按固定大小缩放 sWidth 写多大就多大

		float scaleHeight = (float) sHeight / bmpHeight; //

		Matrix matrix = new Matrix();

		matrix.postScale(scaleWidth, scaleHeight);// 产生缩放后的Bitmap对象

		Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bmpWidth,
				bmpHeight, matrix, false);

		// bitmap.recycle();
		return resizeBitmap;

		// Bitmap resizeBitmap = bitmap;
		//
		// //Bitmap to byte[]
		//
		// byte[] photoData = bitmap2Bytes(resizeBitmap);
		//
		// //save file
		//
		// String fileName = "/sdcard/test.jpg";
		//
		// FileUtil.writeToFile(fileName, photoData);

	}

	private void initButtons() {
		mLeftPanel = (LinearLayout) findViewById(R.id.left_organize);
		mButtons = new Button[6];
		for (int i = 0; i < 6; i++) {
			String str = "radio_button" + i;
			mButtons[i] = (Button) mLeftPanel.findViewWithTag(str);
			mButtons[i].setOnClickListener(this);
			mButtons[i].getBackground().setAlpha(0);
		}

	}

	@Override
	protected void onResume() {
		super.onResume();

		mLeftPanel.setVisibility(View.VISIBLE);
		Animation animation = AnimationUtils.loadAnimation(this,
				R.anim.left_appear);
		animation.setAnimationListener(this);
		mLeftPanel.startAnimation(animation);

	}

	private void setupIntent() {
		mPictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
		mPictureIntent.setType("image/*");

		mCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File out = new File(Environment.getExternalStorageDirectory(),
				"camera.png");
		Uri uri = Uri.fromFile(out);
		mCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
	}

	private void templateSelect() {
		LinearLayout bottom = (LinearLayout) findViewById(R.id.bottom_words);
		bottom.setVisibility(View.GONE);
		mGallery.setVisibility(View.VISIBLE);
		new GridViewWebsiteParserTask(this, new TemplateWebsiteParser(),
				mGallery).execute(NetWorkUtils.GET_STENCIL);
	}

	private void frameSelect() {
		LinearLayout bottom = (LinearLayout) findViewById(R.id.bottom_words);
		bottom.setVisibility(View.GONE);
		mGallery.setVisibility(View.VISIBLE);
		new GridViewWebsiteParserTask(this, new FrameWebsiteParser(), mGallery)
				.execute(NetWorkUtils.GET_FRAME);
	}

	private void effectSelect() {
		LinearLayout bottom = (LinearLayout) findViewById(R.id.bottom_words);
		bottom.setVisibility(View.GONE);
		mGallery.setVisibility(View.VISIBLE);
		new GridViewWebsiteParserTask(this, new EffectWebsiteParser(), mGallery)
				.execute(NetWorkUtils.GET_EFFECTS);
	}

	private void addWordsSelected() {
		LinearLayout bottom = (LinearLayout) findViewById(R.id.bottom_words);
		bottom.setVisibility(View.VISIBLE);
		mGallery.setVisibility(View.GONE);

	}

	protected void backWorkingPicturePage() {
		Gallery recordlist = (Gallery) findViewById(R.id.record_list);

		mPhotoArrayList.clear();
		// ImageView view = new ImageView(this);
		// view.setVisibility(View.GONE);
		// mPhotoArrayList.add(view);
		// mPhotoArrayList.add(view);
		findView();
		setValue();
		setListener();

		mDeleteGalleryAdapter.notifyDataSetChanged();

		mBase64Pictures.clear();
		// mBase64Pictures.add("null");
		// mBase64Pictures.add("null");

		mFrameWebView.removeAllViews();
		// GifView v1 = new GifView(this);
		// v1.setVisibility(View.GONE);
		// GifView v2 = new GifView(this);
		// v2.setVisibility(View.GONE);
		// mFrameWebView.addView(v1, 0);
		// mFrameWebView.addView(v2, 1);
		mWords.setText("");
		wordsAdded = false;
	}

	private InputMethodManager mInputManager;

	private void restorePostion() {
		int count = mFrameWebView.getChildCount();
		for (int index = 0; index < count; index++) {
			TouchGifView touchView = (TouchGifView) mFrameWebView
					.getChildAt(index);
			touchView.layout(touchView.left, touchView.top, touchView.right,
					touchView.bottm);
		}
	}

	@Override
	public void onClick(View view) {
		// mFrameWebView.needSelfLayout = true;
		switch (view.getId()) {
		case R.id.left_btn:
			boolean show = false;
			show = (mLeftPanel.getVisibility() == View.GONE);
			if (show) {
				mLeftButton.setBackgroundResource(R.drawable.left_arrow_back);
			} else {
				mLeftButton
						.setBackgroundResource(R.drawable.right_arrow_farward);
			}
			handleLeftPanel(show);
			handleBottomPanel(false);
			// restorePostion();
			return;
		case R.id.keyboard_bar:
			if (mInputManager == null) {
				mInputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			}

			if (mInputManager != null && getCurrentFocus() != null) {
				IBinder binder = getCurrentFocus().getWindowToken();
				if (binder != null) {
					mInputManager.hideSoftInputFromWindow(binder,
							InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}
			handleBottomPanel(false);
			// restorePostion();
			return;
		case R.id.right_btn:
			if (needConfirm()) {
				showDialog(DIALOG_QUIT);
			} else {
				finish();
			}
			return;
		case R.id.back:
			if (needConfirm()) {
				showDialog(DIALOG_BACK);
			}
			return;
		case R.id.quit:
			if (mPhotoArrayList.size() == 0
					|| (!"template".equals(((View) mPhotoArrayList.get(0))
							.getTag()) && !"photo"
							.equals(((View) mPhotoArrayList.get(0)).getTag()))) {
				showDialog(DIALOG_CONFIRM_ADD_PHOTO);
				return;
			}
			complete();
			return;
		default:
			isButtonClicked(view);
			break;
		}

		super.onClick(view);
	}

	private boolean needConfirm() {
		return mPhotoArrayList.size() > 0
				|| (wordsAdded && mWords.getText().toString() != null && mWords
						.getText().toString().length() != 0);
	}

	private void complete() {
		Runnable run = new Runnable() {

			public void run() {
				showProgessBarDialog("Loading...");
				postGetCombination();
				pd.dismiss();
			}
		};

		new Thread(run).start();
	}

	private void handleLeftPanel(boolean show) {
		if (show && mLeftPanel.getVisibility() != View.VISIBLE) {
			mLeftPanel.setVisibility(View.VISIBLE);
			Animation animation = AnimationUtils.loadAnimation(this,
					R.anim.left_appear);
			animation.setAnimationListener(this);
			mLeftPanel.startAnimation(animation);
		} else if (!show && mLeftPanel.getVisibility() != View.GONE) {
			mLeftPanel.setVisibility(View.GONE);
			mLeftPanel.startAnimation(AnimationUtils.loadAnimation(this,
					R.anim.left_disappear));
		}
	}

	private void handleBottomPanel(boolean show) {
		// mFrameWebView.needSelfLayout = true;
		if (show && mBottomPanel.getVisibility() != View.VISIBLE) {
			mBottomPanel.setVisibility(View.VISIBLE);
			Animation animation = AnimationUtils.loadAnimation(this,
					R.anim.footer_appear);
			animation.setAnimationListener(this);
			mBottomPanel.startAnimation(animation);
			if (show) {
				mLeftButton
						.setBackgroundResource(R.drawable.right_arrow_farward);
			} else {
				mLeftButton.setBackgroundResource(R.drawable.left_arrow_back);
			}
		} else if (!show && mBottomPanel.getVisibility() != View.GONE) {
			mBottomPanel.setVisibility(View.GONE);
			mBottomPanel.startAnimation(AnimationUtils.loadAnimation(this,
					R.anim.footer_disappear));
		}
	}

	// private void wordsAdd() {
	// new GalleryWebsiteParserTask(this, new WordsAddWebsiteParser(),
	// mGallery).execute(NetWorkUtils.GET_NAME + "?Name=李思雨");
	// if(Utils.logging)Log.d("zheng", "url: " + NetWorkUtils.GET_NAME +
	// "?Name=李思雨");
	// }
	public void isButtonClicked(View view) {

		if (view == mButtons[0]) {
			// startActivityForResult(mPictureIntent, 0);
			getGalleryPicture(this);
		} else if (view == mButtons[1]) {
			// startActivityForResult(mCameraIntent, 3);
			getCameraPicture(this);
		} else if (view == mButtons[2]) {
			templateSelect();
			handleBottomPanel(true);
			// mHost.setCurrentTabByTag(TEMPLATE_TAB);
		} else if (view == mButtons[3]) {
			frameSelect();
			handleBottomPanel(true);
			// mHost.setCurrentTabByTag(FRAME_TAB);
		} else if (view == mButtons[4]) {
			effectSelect();
			handleBottomPanel(true);
			// mHost.setCurrentTabByTag(TEXIAO_TAB);
		} else if (view == mButtons[5]) {
			addWordsSelected();
			handleBottomPanel(true);
			// wordsAdd();
			// mHost.setCurrentTabByTag(CHARACTOR_TAB);
		} else if (view instanceof Button) {
			ImageView imageView = (ImageView) findViewById(R.id.selected_color);
			Drawable drawable = ((Button) view).getBackground();
			imageView.setImageDrawable(drawable);

			mWords.setTextColor(((Button) view).getTextColors());
			mWords.setTag(view.getTag());
			if (!wordsAdded) {
				FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
						FrameLayout.LayoutParams.WRAP_CONTENT,
						FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
				params.setMargins(0, 0, 0, 0);
				mWords.setLayoutParams(params);
				mFrameWebView.addView(mWords);
				wordsAdded = true;
			}
			return;
		}

		handleLeftPanel(false);
	}

	protected void setPhoto(Bitmap photo) {
		if (mPhotoArrayList.size() > 0
				&& ("template".equals(((View) mPhotoArrayList.get(0)).getTag()) || "photo"
						.equals(((View) mPhotoArrayList.get(0)).getTag()))) {
			mPhotoArrayList.remove(0);
			mFrameWebView.removeViewAt(0);
			mBase64Pictures.remove(0);
		}

		isTemplate = false;
		LinearLayout workingPhoto = null;

		ImageView image = (ImageView) mInflater.inflate(R.layout.photo_layout,
				mFrameWebView, false);
		// photo = Utils.getResizedBitmap(photo, mScreenWith, mScreenHeihgt);
		photo = getFillImage(photo);
		BitmapDrawable drawable = new BitmapDrawable(photo);
		image.setBackgroundDrawable(drawable);
		// workingPhoto.setShowDimension(mImageWidth, mImageHeigth);
		// workingPhoto.setGifImageType(GifImageType.SYNC_DECODER);
		// workingPhoto.setGifImage(bytes);
		mFrameWebView.addView(image, 0);

		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		photo.compress(Bitmap.CompressFormat.JPEG, 100, os);
		byte[] imgbytes = os.toByteArray();
		String content = new BASE64Encoder().encode(imgbytes);
		mBase64Pictures.add(0, content);

		FrameLayout deletephoto = (FrameLayout) mInflater.inflate(
				R.layout.gridview_item, null);
		image = (ImageView) deletephoto.findViewById(R.id.image);
		deletephoto.findViewById(R.id.mText).setVisibility(View.VISIBLE);
		photo = Utils.getResizedBitmap(photo, cWidth, cHeight);
		// BitmapDrawable d = new BitmapDrawable(photo);
		image.setImageBitmap(photo);

		deletephoto.setTag("photo");
		mPhotoArrayList.add(0, deletephoto);
		mDeleteGalleryAdapter.notifyDataSetChanged();

		findView();
		setValue();
		setListener();
	}

	// @Override
	protected void onActivityResult_abandon(int requestCode, int resultCode,
			Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}

		// int width = mWorkingPhoto.getWidth();
		// int height = mWorkingPhoto.getHeight();
		int width = getWindowManager().getDefaultDisplay().getWidth();
		int height = getWindowManager().getDefaultDisplay().getHeight();
		if (requestCode == 0) {
			Intent cj = new Intent("com.android.camera.action.CROP");
			cj.setData(data.getData());
			cj.putExtra("crop", "true");
			cj.putExtra("aspectX", 2);
			cj.putExtra("aspectY", 2);
			cj.putExtra("outputX", width);
			cj.putExtra("outputY", height);
			cj.putExtra("noFaceDetection", true);
			cj.putExtra("return-data", true);
			startActivityForResult(Intent.createChooser(cj, "裁剪"), 2);
		} else if (requestCode == 3) {
			Intent cj1 = new Intent("com.android.camera.action.CROP");
			try {
				cj1.setData(Uri.parse(android.provider.MediaStore.Images.Media
						.insertImage(getContentResolver(),
								"/mnt/sdcard/camera.png", null, null)));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			cj1.putExtra("crop", "true");
			cj1.putExtra("aspectX", 2);
			cj1.putExtra("aspectY", 2);
			cj1.putExtra("outputX", width);
			cj1.putExtra("outputY", height);
			cj1.putExtra("return-data", true);
			startActivityForResult(cj1, 2);

			File camera = new File("/mnt/sdcard/camera.png");
			if (camera.exists()) {
				camera.delete();
			}
		} else if (requestCode == 2) {
			Intent intent = new Intent();
			Bundle extras = data.getExtras();
			if (extras != null) {
				Bitmap photo = extras.getParcelable("data");
				photo = Utils.getResizedBitmap(photo, width, height);
				setPhoto(photo);
				// mWorkingPhoto.setBackgroundDrawable(new
				// BitmapDrawable(photo));

				// try {
				// FileOutputStream outStreamz = new FileOutputStream(sdCard);
				// photo.compress(Bitmap.CompressFormat.JPEG, 30, outStreamz);
				// outStreamz.close();
				// } catch (FileNotFoundException e) {
				// e.printStackTrace();
				// } catch (IOException e) {
				// e.printStackTrace();
				// }

				// Drawable d = new BitmapDrawable(photo);
				// mPicView.setBackgroundDrawable(d);
				// mPicView.setVisibility(View.VISIBLE);

				final ByteArrayOutputStream os = new ByteArrayOutputStream();

				// 构造SQLite的Content对象，这里也可以使用raw
				// ContentValues values = new ContentValues();
				// mNewHeadPic = os.toByteArray();
				// if(Utils.logging)Log.d("zheng", "StaticInfo.mPicQuality:" +
				// StaticInfo.mPicQuality + " pic size:" + mNewHeadPic.length);

				// mHeadPicView.setImageURI(data.getData());
				// try {
				//
				// FileOutputStream outStreamz = new FileOutputStream(sdCard);
				//
				// photo.compress(Bitmap.CompressFormat.PNG, 50, outStreamz);
				//
				// outStreamz.close();
				//
				// } catch (FileNotFoundException e) {
				//
				// e.printStackTrace();
				//
				// } catch (IOException e) {
				//
				// e.printStackTrace();
				//
				// }

				// intent.setClass(AddCardActivity.this,
				// ChooseABActivity.class);
				//
				// startActivity(intent);
				//
				// finish();

			}
		}
	}

	private void initTemplateUI(WebsiteInfo result) {
		mAdapter = new GridViewAdapter(this, result);
		mGallery.setAdapter(mAdapter);
	}

	BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (ACTION_GALLERY_NOTIFY_DATA_SET_CHANGED.equals(intent
					.getAction())) {
				mAdapter = (GridViewAdapter) mGallery.getAdapter();
				if (mAdapter != null) {
					mAdapter.notifyDataSetChanged();
				}
			}

			if (Utils.logging)
				Log.d("zheng", "BroadcastReceiver notifyDataSetChanged");
		}
	};

	protected void onDestroy() {
		super.onDestroy();
		if (mReceiver != null) {
			unregisterReceiver(mReceiver);
			mReceiver = null;
		}
	}

	@Override
	protected void handleTitleBarEvent(int paramInt) {
		MainTabActivity.getCurrentActivityGroup().setPreviousActivity();
	}

	public void onAnimationEnd(Animation animation) {

	}

	public void onAnimationRepeat(Animation animation) {

	}

	public void onAnimationStart(Animation animation) {
	}

	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {

	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// mFrameWebView.needSelfLayout = true;
		// if (mPicutureList == parent) {
		// mPhotoArrayList.remove(position);
		// mFrameWebView.removeViewAt(position);
		// mBase64Pictures.remove(position);
		//
		// ImageView imageview = new ImageView(this);
		// imageview.setVisibility(View.GONE);
		//
		// ImageView defaultview = new ImageView(this);
		// defaultview.setMaxHeight(view.getHeight());
		// defaultview.setMaxWidth(view.getWidth());
		// if (position == 0 || position == 1) {
		// mPhotoArrayList.add(position, imageview);
		// mBase64Pictures.add(position, "null");
		// GifView gifview = new GifView(this);
		// gifview.setVisibility(View.GONE);
		// mFrameWebView.addView(gifview, position);
		// }
		// mDeleteGalleryAdapter.notifyDataSetChanged();
		// return;
		// }

		WebsiteInfo websiteinfo = (WebsiteInfo) parent
				.getItemAtPosition(position);
		final String photoaddress = websiteinfo.getPictureAddress();
		if (websiteinfo.getPictureAddress() != null) {
			GridViewAdapter adapter = (GridViewAdapter) parent.getAdapter();
			final int gallerytype = adapter.getGalleryType();
			showProgessBarDialog("Loading...");
			Runnable run = new Runnable() {
				public void run() {
					final byte[] bytes = Utils.getInputStreamBytesFromDatabase(
							PictureWorkingActivity.this, photoaddress);
					Runnable action = new Runnable() {

						public void run() {
							setPicture(gallerytype, bytes, photoaddress);
							if (pd != null) {
								pd.dismiss();
							}
						}
					};
					runOnUiThread(action);
				}
			};
			new Thread(run).start();
		}
	}

	private boolean isTemplate = false;
	private byte[] templateBytes;
	private ArrayList mBase64Pictures = new ArrayList();

	private void setPicture(int gallerytype, byte[] bytes, String photoaddress) {
		Log.d("zheng", "photoaddress=== " + photoaddress);
		GifView workingPhoto = null;
		FrameLayout deletephoto = null;
		String content = null;
		GifView gifview = null;
		ImageView image = null;

		deletephoto = (FrameLayout) mInflater.inflate(R.layout.gridview_item,
				null);
		image = (ImageView) deletephoto.findViewById(R.id.image);
		deletephoto.findViewById(R.id.mText).setVisibility(View.VISIBLE);
		Bitmap bitmap = Utils.getPhotoFromDatabase(this, photoaddress);
		// if (bitmap == null)　{
		// bitmap = Utils.getBitMapFromNetwork(photoaddress, context);
		// }
		if (bitmap != null) {
			bitmap = Utils.getResizedBitmap(bitmap, cWidth, cHeight);
			image.setImageBitmap(bitmap);
		}

		int size = 0;
		switch (gallerytype) {
		case GALLERY_FRAME_TYPE:
			workingPhoto = (GifView) mInflater.inflate(R.layout.gif_layout,
					mFrameWebView, false);
			workingPhoto.setShowDimension(mImageWidth, mImageHeigth);
			workingPhoto.setGifImageType(GifImageType.SYNC_DECODER);
			workingPhoto.setGifImage(bytes);
			size = mPhotoArrayList.size();
			content = new BASE64Encoder().encode(bytes);
			int pos = 0;
			if (mPhotoArrayList.size() > 1
					&& "frame".equals(((View) mPhotoArrayList.get(1)).getTag())) {
				pos = 1;
				mPhotoArrayList.remove(1);
				mFrameWebView.removeViewAt(1);
				mBase64Pictures.remove(1);
			} else if (mPhotoArrayList.size() > 0
					&& "frame".equals(((View) mPhotoArrayList.get(0)).getTag())) {
				pos = 0;
				mPhotoArrayList.remove(0);
				mFrameWebView.removeViewAt(0);
				mBase64Pictures.remove(0);
			} else if (mPhotoArrayList.size() > 0
					&& ("template".equals(((View) mPhotoArrayList.get(0))
							.getTag()) || "photo"
							.equals(((View) mPhotoArrayList.get(0)).getTag()))) {
				pos = 1;
			}

			mFrameWebView.addView(workingPhoto, pos);
			mBase64Pictures.add(pos, photoaddress);
			mPhotoArrayList.add(pos, deletephoto);

			deletephoto.setTag("frame");
			mDeleteGalleryAdapter.notifyDataSetChanged();
			break;
		case GALLERY_TEMPLATE_TYPE:
			if (mPhotoArrayList.size() > 0
					&& ("template".equals(((View) mPhotoArrayList.get(0))
							.getTag()) || "photo"
							.equals(((View) mPhotoArrayList.get(0)).getTag()))) {
				mPhotoArrayList.remove(0);
				mFrameWebView.removeViewAt(0);
				mBase64Pictures.remove(0);
			}
			isTemplate = true;
			workingPhoto = (GifView) mInflater.inflate(R.layout.gif_layout,
					mFrameWebView, false);
			workingPhoto.setShowDimension(mImageWidth, mImageHeigth);
			workingPhoto.setGifImageType(GifImageType.SYNC_DECODER);
			workingPhoto.setGifImage(bytes);
			mFrameWebView.addView(workingPhoto, 0);

			content = new BASE64Encoder().encode(bytes);
			mBase64Pictures.add(0, photoaddress);

			size = mPhotoArrayList.size();

			deletephoto.setTag("template");
			mPhotoArrayList.add(0, deletephoto);
			mDeleteGalleryAdapter.notifyDataSetChanged();
			break;
		case GALLERY_EFFECT_TYPE:
			// WebView webview = new WebView(this);
			// webview.loadUrl(photoaddress);
			// WebSettings framewebSettings = webview.getSettings();
			// framewebSettings.setJavaScriptEnabled(true);
			// framewebSettings.setSupportZoom(false);
			// webview.setOnTouchListener(new MulitPointTouchListener());
			// mFrameWebView.addView(webview, mFrameWebView.getChildCount());

			workingPhoto = (TouchGifView) mInflater.inflate(
					R.layout.touchgifview_layout, mFrameWebView, false);
			workingPhoto.setGifImageType(GifImageType.SYNC_DECODER);
			// LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
			// LayoutParams.WRAP_CONTENT);
			// mGallery.setLayoutParams(params);
			// Bitmap bm = workingPhoto.getFirstImage();
			// workingPhoto.setMaxHeight(mImageHeigth);
			// workingPhoto.setMaxWidth(mImageWidth);
			workingPhoto.setGifImage(bytes);
			// ImageWidth, mImageHeigth
			// workingPhoto.setOnTouchListener(new MulitPointTouchListener());
			mFrameWebView.addView(workingPhoto, mFrameWebView.getChildCount());
			Log.d("zheng", "view count:" + mFrameWebView.getChildCount());

			content = new BASE64Encoder().encode(bytes);
			mBase64Pictures.add(mBase64Pictures.size(), photoaddress);

			// gifview.setShowDimension(mPicutureList.getHeight(),
			// mPicutureList.getHeight());
			// gifview.setGifImageType(GifImageType.SYNC_DECODER);
			// gifview.setGifImage(bytes);
			mPhotoArrayList.add(deletephoto);
			mDeleteGalleryAdapter.notifyDataSetChanged();
			break;
		default:
			break;
		}

		mGridAdapter.notifyDataSetChanged();

		findView();
		setValue();
		setListener();
	}

	private byte[] bytes;

	/**
	 * Web view client
	 */
	final class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.contains("head_select.html")) {
				// page_current = PAGE_HEAD_SELECT;
				return false;
			} else if (url.contains("head_editor.html")) {
				// in onCreate or any event where your want the user to
				// select a file
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(
						Intent.createChooser(intent, "Select Picture"),
						SELECT_PICTURE);
				return true;
			} else if (url.contains("head_editor.html")) {
				return true;
			} else if (url.contains("main_page")) {
				return false; // don't redirect the page
			} else {
				return false;
			}
		}
	}

	final class MyWebChromeClient extends WebChromeClient {
		public boolean onJsAlert(WebView view, String url, String message,
				JsResult result) {
			result.confirm();
			return true;
		}
	}

	/**
	 * Align the first gallery item to the left.
	 * 
	 * @param parentView
	 *            The view containing the gallery widget (we assume the gallery
	 *            width is set to match_parent)
	 * @param gallery
	 *            The gallery we have to change
	 */
	private void alignGalleryToLeft(View parentView, Gallery gallery) {
		int galleryWidth = parentView.getWidth();

		// We are taking the item widths and spacing from a dimension resource
		// because:
		// 1. No way to get spacing at runtime (no accessor in the Gallery
		// class)
		// 2. There might not yet be any item view created when we are calling
		// this
		// function

		// int itemWidth = getResources().getDimensionPixelSize(
		// R.dimen.gallery_item_width);
		// int spacing = context.getResources().getDimensionPixelSize(
		// R.dimen.gallery_spacing);

		int spacing = 2;
		int itemWidth = mImageWidth;

		// The offset is how much we will pull the gallery to the left in order
		// to simulate
		// left alignment of the first item
		int offset;
		if (galleryWidth <= itemWidth) {
			offset = galleryWidth / 2 - itemWidth / 2 - spacing;
		} else {
			offset = galleryWidth - itemWidth - 2 * spacing;
		}
		offset = 0;

		// Now update the layout parameters of the gallery in order to set the
		// left margin
		MarginLayoutParams mlp = (MarginLayoutParams) gallery.getLayoutParams();
		mlp.setMargins(-offset, mlp.topMargin, mlp.rightMargin,
				mlp.bottomMargin);
	}

	public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub

	}

	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub

	}

	public void onTextChanged(CharSequence words, int arg1, int arg2, int arg3) {
		mWords.setText(words.toString());
		if (!wordsAdded) {
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.WRAP_CONTENT,
					FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
			params.setMargins(0, 0, 0, 0);
			mWords.setLayoutParams(params);
			mFrameWebView.addView(mWords);
			wordsAdded = true;
		}
	}

	private ArrayList mPhotoArrayList = new ArrayList();

	private class PictureGalleryAdapter extends BaseAdapter {

		public int getCount() {
			int size = mPhotoArrayList.size();
			return size > 8 ? size : 8;
		}

		public Object getItem(int position) {
			return mPhotoArrayList.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (mPhotoArrayList.size() <= position) {
				FrameLayout deletephoto = (FrameLayout) mInflater.inflate(
						R.layout.delete_frame_layout, null);
				ImageView image = (ImageView) deletephoto
						.findViewById(R.id.image);
				image.setMaxHeight(mPicutureList.getHeight());
				image.setMaxWidth(mPicutureList.getHeight() - 8);
				deletephoto.setBackgroundColor(android.R.color.background_dark);
				ImageView deleteIcon = (ImageView) deletephoto
						.findViewById(R.id.delete_icon);
				deleteIcon.setVisibility(View.GONE);
				return deletephoto;
			} else {
				return (View) mPhotoArrayList.get(position);
			}
		}
	}

	private Bitmap mCombinedBitmap = null;
	private String mCombinedAddress = null;

	private void postGetCombination() {
		HttpClient httpclient = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 5000);
		HttpPost httppost = new HttpPost(
				"http://www.uuunm.com/getCombination.jsp");

		try {
			Log.d("zheng", getProductDownAPIXMLString());
			System.out.println("getProductDownAPIXMLString==="
					+ getProductDownAPIXMLString());
			StringEntity se = new StringEntity(getProductDownAPIXMLString(),
					HTTP.UTF_8);
			se.setContentType("text/xml");
			httppost.setEntity(se);

			HttpResponse httpresponse = httpclient.execute(httppost);
			HttpEntity resEntity = httpresponse.getEntity();
			InputStream is = resEntity.getContent();
			System.out.println("is===" + is);
			String address = new WorkingPhotoWebsiteParser()
					.parseWebsiteInfo(is);
			if (address == null) {
				Utils.notifyToUser("合成图片超过90K了, 无法下载",
						PictureWorkingActivity.this);
				mCombinedBitmap = null;
				mCombinedAddress = null;
			} else {
				mCombinedBitmap = Utils.getBitMapFromNetwork(address, this);
				mCombinedAddress = address;

				Runnable run = new Runnable() {

					public void run() {
						showDialog(DIALOG_COMBINE_SUCESS);
					}
				};

				PictureWorkingActivity.this.runOnUiThread(run);
			}
			Log.d("zheng", "the final working address is:" + address);

			// Utils.notifyToUser(mCombinedAddress,
			// MainTabActivity.getCurrentActivityGroup());
			Log.d("zheng", "Status OK: \n" + mCombinedAddress);

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			notifyToUser(e.getMessage());
			// tv.setText("Status NOT OK: \n" + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			notifyToUser(e.getMessage());
			// tv.setText("Status NOT OK: \n" + e.getMessage());
		}
	}

	public String getProductDownAPIXMLString() {
		Text content;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = dbf.newDocumentBuilder();
		} catch (Exception e) {
		}
		Document doc = builder.newDocument();

		Element root = doc.createElement("MMessage");
		doc.appendChild(root); // 将根元素添加到文档上

		Element consignid = doc.createElement("consignid");
		consignid.setAttribute("attr", StaticInfo.mUsername);
		root.appendChild(consignid);// 添加属性

		Element password = doc.createElement("password");
		password.setAttribute("attr", "");
		root.appendChild(password);// 添加属性

		long timestamp = System.currentTimeMillis();
		Element linkid = doc.createElement("linkid");
		linkid.setAttribute("attr", String.valueOf(timestamp));
		root.appendChild(linkid);// 添加属性

		Element productInfo = doc.createElement("productInfo");
		root.appendChild(productInfo);// 添加属性

		// 底图信息
		Element PicInfo = doc.createElement("PicInfo");
		productInfo.appendChild(PicInfo);// 添加属性

		Element isModel = doc.createElement("isModel");
		isModel.setAttribute("attr", isTemplate == true ? "0" : "1");
		PicInfo.appendChild(isModel);// 添加属性

		Element photo;
		Element MMSPath;
		if (isTemplate) {
			MMSPath = doc.createElement("MMSPath");
			PicInfo.appendChild(MMSPath);

			content = doc.createTextNode((String) mBase64Pictures.get(0));
			photo = doc.createElement("photo");
			photo.appendChild(content);
			MMSPath.appendChild(photo);
		} else {
			Element textroot = doc.createElement("MMSContent");
			PicInfo.appendChild(textroot); // 将根元素添加到文档上

			Element imagecontent = doc.createElement("content");
			content = doc.createTextNode((String) mBase64Pictures.get(0));
			imagecontent.appendChild(content);
			textroot.appendChild(imagecontent);
		}

		Element posInfo = doc.createElement("posInfo");
		PicInfo.appendChild(posInfo);

		Element left = doc.createElement("left");
		left.setAttribute("attr", "0");
		posInfo.appendChild(left);

		Element top = doc.createElement("top");
		// top.setAttribute("attr", "0");
		int toppadding = mFrameWebView.getChildAt(0).getTop();
		if (toppadding != 0) {
			toppadding = toppadding / 2;
		}
		Log.d("peter", "padding:" + toppadding);
		top.setAttribute("attr", String.valueOf(toppadding));
		posInfo.appendChild(top);

		int with;
		int hight;
		if (mFrameWebView.getChildAt(0) instanceof TouchGifView) {
			with = (int) mResources.getDimension(R.dimen.gif_with);
			hight = (int) mResources.getDimension(R.dimen.gif_height);

			// ((TouchGifView)mFrameWebView.getChildAt(0)).setShowDimension(with,
			// hight);
		} else {
			with = mFrameWebView.getChildAt(0).getWidth();
			hight = mFrameWebView.getChildAt(0).getHeight();
		}
		Element width = doc.createElement("width");
		width.setAttribute("attr", String.valueOf(with / 2 + 19));
		posInfo.appendChild(width);

		Element height = doc.createElement("height");
		height.setAttribute("attr", String.valueOf(hight / 2 + 13));
		posInfo.appendChild(height);

		// 添加相框信息
		if (mPhotoArrayList.size() > 1) {
			View view = (View) mPhotoArrayList.get(1);
			if (view instanceof FrameLayout) {
				Element FrameInfo = doc.createElement("FrameInfo");
				productInfo.appendChild(FrameInfo);

				MMSPath = doc.createElement("MMSPath");
				content = doc.createTextNode((String) mBase64Pictures.get(1));
				photo = doc.createElement("photo");
				photo.appendChild(content);
				MMSPath.appendChild(photo);
				FrameInfo.appendChild(MMSPath);
			}
		}

		// 添加特效信息
		if (mBase64Pictures.size() > 2) {
			for (int i = 2; i < mBase64Pictures.size(); i++) {
				Element EffectInfo = doc.createElement("EffectInfo");
				productInfo.appendChild(EffectInfo);

				MMSPath = doc.createElement("MMSPath");
				EffectInfo.appendChild(MMSPath);

				content = doc.createTextNode((String) mBase64Pictures.get(i));
				photo = doc.createElement("photo");
				photo.appendChild(content);
				MMSPath.appendChild(photo);

				posInfo = doc.createElement("posInfo");
				EffectInfo.appendChild(posInfo);

				left = doc.createElement("left");
				left.setAttribute("attr", String.valueOf(mFrameWebView
						.getChildAt(i).getLeft() / 2));
				posInfo.appendChild(left);

				top = doc.createElement("top");
				top.setAttribute("attr", String.valueOf(mFrameWebView
						.getChildAt(i).getTop() / 2 + 20));
				posInfo.appendChild(top);

				Log.d("zheng", "i: " + i);
				View gifview = mFrameWebView.getChildAt(i);
				width = doc.createElement("width");
				width.setAttribute("attr",
						String.valueOf(gifview.getWidth() / 2));
				posInfo.appendChild(width);

				height = doc.createElement("height");
				height.setAttribute("attr",
						String.valueOf(gifview.getHeight() / 2));
				posInfo.appendChild(height);
			}
		}

		if (wordsAdded && mWords.getText().toString() != null
				&& mWords.getText().toString().length() != 0) {
			Element TextInfo = doc.createElement("TextInfo");
			productInfo.appendChild(TextInfo);

			Element textcontent = doc.createElement("content");
			TextInfo.appendChild(textcontent);

			content = doc.createTextNode(mWords.getText().toString());
			textcontent.appendChild(content);

			posInfo = doc.createElement("posInfo");
			TextInfo.appendChild(posInfo);

			left = doc.createElement("left");
			left.setAttribute("attr",
					String.valueOf(mWords.getLeft() * 130 / 312));
			posInfo.appendChild(left);

			top = doc.createElement("top");
			top.setAttribute("attr",
					String.valueOf(mWords.getTop() * 170 / 350));
			posInfo.appendChild(top);

			Element TextType = doc.createElement("TextType");
			TextType.setAttribute("attr", "宋体");
			posInfo.appendChild(TextType);

			Element size = doc.createElement("size");
			size.setAttribute("attr",
					String.valueOf((int) mWords.getTextSize()));
			posInfo.appendChild(size);

			Element color = doc.createElement("color");
			color.setAttribute("attr", (String) mWords.getTag());
			posInfo.appendChild(color);
		}

		return toStringFromDoc(doc);
	}

	/*
	 * 把dom文件转换为xml字符串
	 */
	public static String toStringFromDoc(Document document) {
		String result = null;

		if (document != null) {
			StringWriter strWtr = new StringWriter();
			StreamResult strResult = new StreamResult(strWtr);
			TransformerFactory tfac = TransformerFactory.newInstance();
			try {
				javax.xml.transform.Transformer t = tfac.newTransformer();
				t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
				t.setOutputProperty(OutputKeys.INDENT, "yes");
				t.setOutputProperty(OutputKeys.METHOD, "xml"); // xml, html,
																// text
				t.setOutputProperty(
						"{http://xml.apache.org/xslt}indent-amount", "4");
				t.transform(new DOMSource(document.getDocumentElement()),
						strResult);
			} catch (Exception e) {
				System.err.println("XML.toString(Document): " + e);
			}
			result = strResult.getWriter().toString();
			try {
				strWtr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	private void downloadMessage() {
		Intent intent = new Intent(this, MessageActivity.class);
		intent.setAction(BaseActivity.ACTION_MESSAGE_COMPOSE);
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		if (mCombinedBitmap == null) {
			return;
		}
		mCombinedBitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
		byte[] imgByte = os.toByteArray();
		intent.putExtra("photo_bitmap", imgByte);
		intent.putExtra("in_tab_group", false);
		intent.putExtra("is_combined_image", true);
		intent.putExtra("photoaddress", mCombinedAddress);
		intent.putExtra("from_activity", "PictureWorkingActivity");
		startActivity(intent);

		// MainTabActivity.getCurrentActivityGroup().setNextActivity(intent,
		// "MessageActivity");
	}

	protected Dialog createCombineSucessDialog() {
		return new AlertDialog.Builder(this)
				.setMessage("成功合成一条新彩信.")
				.setPositiveButton("下载", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dismissDialog(DIALOG_COMBINE_SUCESS);

						downloadMessage();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dismissDialog(DIALOG_COMBINE_SUCESS);
					}
				}).create();
	}

	public void showProgessBarDialog(final String msg) {
		Runnable action = new Runnable() {

			public void run() {
				if (pd == null) {
					pd = new ProgressDialog(PictureWorkingActivity.this);
					// pd.setTitle("加载图片");
				}
				pd.setMessage(" 加载中... ");
				pd.show();
			}
		};

		runOnUiThread(action);
	}
}

class MulitPointTouchListener implements OnTouchListener {
	private static final String TAG = "Touch";
	// These matrices will be used to move and zoom image
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();

	// We can be in one of these 3 states
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;

	// Remember some things for zooming
	PointF start = new PointF();
	PointF mid = new PointF();
	float oldDist = 1f;

	public boolean onTouch(View v, MotionEvent event) {
		Log.d("zheng", "Working onTouch");
		ImageView view = (ImageView) v;
		// Log.e("view_width",
		// view.getImageMatrix()..toString()+"*"+v.getWidth());
		// Dump touch event to log
		dumpEvent(event);

		// Handle touch events here...
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:

			matrix.set(view.getImageMatrix());
			savedMatrix.set(matrix);
			start.set(event.getX(), event.getY());
			Log.d(TAG, "mode=DRAG");
			mode = DRAG;

			Log.d(TAG, "mode=NONE");
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			oldDist = spacing(event);
			Log.d(TAG, "oldDist=" + oldDist);
			if (oldDist > 10f) {
				savedMatrix.set(matrix);
				midPoint(mid, event);
				mode = ZOOM;
				Log.d(TAG, "mode=ZOOM");
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			Log.e("view.getWidth", view.getWidth() + "");
			Log.e("view.getHeight", view.getHeight() + "");

			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				// ...
				matrix.set(savedMatrix);
				matrix.postTranslate(event.getX() - start.x, event.getY()
						- start.y);
			} else if (mode == ZOOM) {
				float newDist = spacing(event);
				Log.d(TAG, "newDist=" + newDist);
				if (newDist > 10f) {
					matrix.set(savedMatrix);
					float scale = newDist / oldDist;
					matrix.postScale(scale, scale, mid.x, mid.y);
				}
			}
			break;
		}

		view.setImageMatrix(matrix);
		return true; // indicate event was handled
	}

	private void dumpEvent(MotionEvent event) {
		String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
				"POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
		StringBuilder sb = new StringBuilder();
		int action = event.getAction();
		int actionCode = action & MotionEvent.ACTION_MASK;
		sb.append("event ACTION_").append(names[actionCode]);
		if (actionCode == MotionEvent.ACTION_POINTER_DOWN
				|| actionCode == MotionEvent.ACTION_POINTER_UP) {
			sb.append("(pid ").append(
					action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
			sb.append(")");
		}
		sb.append("[");
		for (int i = 0; i < event.getPointerCount(); i++) {
			sb.append("#").append(i);
			sb.append("(pid ").append(event.getPointerId(i));
			sb.append(")=").append((int) event.getX(i));
			sb.append(",").append((int) event.getY(i));
			if (i + 1 < event.getPointerCount())
				sb.append(";");
		}
		sb.append("]");
		Log.d(TAG, sb.toString());
	}

	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}
}