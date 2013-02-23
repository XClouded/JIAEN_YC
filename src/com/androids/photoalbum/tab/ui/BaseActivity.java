package com.androids.photoalbum.tab.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.photoalbum.R;
import com.androids.photoalbum.netinfo.ClassificationLocalSiteInfo;
import com.androids.photoalbum.netinfo.ClassificationWebSiteInfo;
import com.androids.photoalbum.netinfo.ClassificationWebSiteInfo.ProductInfo;
import com.androids.photoalbum.netinfo.CollectionsWebsiteInfo;
import com.androids.photoalbum.netinfo.MusicLocalSiteInfo;
import com.androids.photoalbum.netinfo.SearchWebsiteInfo;
import com.androids.photoalbum.netinfo.ServerInfo;
import com.androids.photoalbum.netinfo.WebsiteInfo;
import com.androids.photoalbum.parser.ClassificationWebsiteParser;
import com.androids.photoalbum.parser.CollectionsWebsiteParser;
import com.androids.photoalbum.parser.DelCollectionWebsiteParser;
import com.androids.photoalbum.parser.MusicWebsiteParser;
import com.androids.photoalbum.parser.SearchWebsiteParser;
import com.androids.photoalbum.parser.WebsiteParser;
import com.androids.photoalbum.userinfo.StaticInfo;
import com.androids.photoalbum.utils.CommonWebsiteTask;
import com.androids.photoalbum.utils.ListViewAdapter;
import com.androids.photoalbum.utils.LoadImageTask;
import com.androids.photoalbum.utils.MusicListAdapter;
import com.androids.photoalbum.utils.NetWorkUtils;
import com.androids.photoalbum.utils.PhotoListAdapter;
import com.androids.photoalbum.utils.Utils;
import com.androids.photoalbum.view.BaseLayout;
import com.androids.photoalbum.view.ImageLayout;
import com.androids.photoalbum.view.MainTabActivity;

public abstract class BaseActivity extends Activity implements
		View.OnClickListener, View.OnTouchListener {
	public final static String ADAPTER_ID = "adapter_id";
	private TextView mFavoriteText;
	protected boolean isActivity = false;
	public int mImageWidth;
	public int mImageHeigth;
	public int mScreenWith;
	public int mScreenHeihgt;
	public int mLineCount = 4;
	public Spinner mSpinner;
	protected boolean mIsAutoLogin = false;
	protected LinearLayout mProductList;
	protected float mScale = 0;
	// protected LinearLayout mHomeView;
	public ListView mListView;
	public GridView mGridView;
	// protected ArrayList mBlogList;
	protected Resources mResources;
	protected long mNowTimeStamp = -1;
	protected String mRootNode = "";
	protected String mCommand = "";
	protected Handler mHandler = new Handler();
	protected Runnable mBlogPicRun = null;
	protected Runnable mHeadPicRun = null;
	protected static LayoutInflater mInflater;
	protected Map<String, Bitmap> mHeadPicMap = new HashMap<String, Bitmap>();
	protected Map<ImageView, String> mHeadViewMap = new HashMap<ImageView, String>();
	public static final int BG_BUTTON1 = 2;

	public static final int BG_BUTTON2 = 3;

	public static final int BG_BUTTON3 = 4;

	public static final int LEFT_BUTTON = 1;
	public static final int MID_BUTTON = 5;
	public final static long ONE_DAY = 60 * 60 * 24;
	public final static long ONE_HNOUR = 60 * 60;
	public final static long ONE_MINUTE = 60;
	// private static final int MSG_SWITCH_TO_LOGIN;
	public static final int RIGHT_BUTTON = 0;
	public LoadImageTask mLoadImageTask;

	protected BaseLayout ly;
	protected boolean mNeedReloaded = false;
	protected boolean mIsPicReceiverRegisted = false;
	protected Bitmap mHeadPicBitmap = null;

	Handler mBaseHandler;
	protected IntentFilter mPicDownloadFilter = new IntentFilter();

	public final static int TYPE_QUERY_IMAGE_SAVE_PIC_FROM_DATABASE = 2;
	public ImageView mHeadPicView;
	protected String mRetweetBlogid = null;
	protected String mBlogId = null;
	protected ImageView mBlogContentPic = null;
	protected ImageView mRetweetBlogPic = null;
	protected String mBlogContentPicPath = null;
	protected String mBlogRetweetPicPath = null;
	protected Drawable mDefaultHeadpicDrawable = null;

	protected RelativeLayout titleBar;
	public static final int DIALOG_SAVE_IMAGE = 0;
	public static final int DIALOG_LOGOUT_ACCOUNT = 1;
	protected static final int DIALOG_CLEAR_WORDS = 2;
	protected static final int DIALOG_CONFIRM_ADD_PHOTO = 3;
	protected static final int DIALOG_COMBINE_SUCESS = 4;
	public static final int DIALOG_CONFIRM_QUIT_YC = 5;
	public static final int DIALOG_QUIT = 6;
	public static final int DIALOG_BACK = 7;
	public static final int DIALOG_CANCEL_FAVORITE = 8;
	protected ContentResolver mResolver;
	public static final String ACTION_MESSAGE_COMPOSE = "com.android.photoalbum.MESSAGE_COMPOSE";
	public static final String ACTION_RELOAD_AFTER_SWITCH_USER = "com.android.photoalbum.RELOAD_AFTER_SWITCH_USER";
	public static final String ACTION_RELOAD_AFTER_DATA_CHANGE = "com.android.microblog.RELOAD_AFTER_DATA_CHANGE";

	public static WebsiteInfo mWebsiteInfo = null;
	public PhotoListAdapter mPhotoAdapter = null;
	public ListViewAdapter mAlbumAdapter = null;
	public MusicListAdapter mMusicAdapter = null;
	public View mFooterView;
	public static final String ACTION_NOTIFY_DATA_SET_CHANGED = "action_notify_data_set_changed";
	public static final String ACTION_NOTIFY_DATA_SET_INVALIDATED = "action_notify_data_set_invalidated";
	public static final String ACTION_NOTIFY_DATA_SET_INITIALIZED = "action_notify_data_set_initialized";

	public static final String ACTION_NOTIFY_LISTVIEW_DATA_SET_CHANGED = "action_notify_listview_data_set_changed";

	// private final ServiceConnection mAlbumServiceConnection = new
	// AlbumServiceConnection ();

	public BaseActivity() {
		mBaseHandler = new MessageHandler();
	}

	synchronized protected void reloadList(final boolean isMoreLoaded) {
	}

	private void switchToLoginActivity() {
		// Utils.cleanUserFile(getCacheDir().getPath());
		// StaticInfo.mUser = null;
		StaticInfo.mPassword = null;
		StaticInfo.mUsername = null;
		StaticInfo.mTotals = null;
		StaticInfo.mNickName = null;
		SharedPreferences.Editor userInfoPre = getSharedPreferences("weibo", 0)
				.edit();
		userInfoPre.clear();
		userInfoPre.commit();
		// AccountHelper.cleanAccounts(this);
		Intent noUserIntent = new Intent("sina.weibo.action.NOUSER");
		sendBroadcast(noUserIntent);
		MainTabActivity.mMode = 0;
		Intent mainTabIntent = new Intent(this, MainTabActivity.class);
		mainTabIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		mainTabIntent.putExtra("MODE_KEY", 0);
		mainTabIntent
				.setAction("com.sina.weibo.action.switchuser_activity_on_stack_top");
		startActivity(mainTabIntent);
	}

	protected void handleErrorEvent(String event) {
		if ((event != null) && (event.equalsIgnoreCase("-100"))) {
			Message msg = this.mBaseHandler.obtainMessage();
			msg.what = 0;
			boolean bool = this.mBaseHandler.sendMessage(msg);
		}
	}

	protected ResultDisplayer mPendingResult;

	class ResultDisplayer implements OnClickListener {
		String mMsg;
		String mMimeType;

		ResultDisplayer(String msg, String mimeType) {
			mMsg = msg;
			mMimeType = mimeType;
		}

		public void onClick(View v) {
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType(mMimeType);
			mPendingResult = this;
			if (isActivity) {
				startActivityForResult(intent, 10);
			} else {
				MainTabActivity.getCurrentActivityGroup()
						.startActivityForResult(intent, 10);
			}
		}
	}

	protected abstract void handleTitleBarEvent(int paramInt);

	public void onClick(View view) {
		if (view == ly.leftButton) {
			handleTitleBarEvent(LEFT_BUTTON);
		} else if (view == ly.rightButton) {
			handleTitleBarEvent(RIGHT_BUTTON);
		} else if (view == ly.mButton1) {
			handleTitleBarEvent(BG_BUTTON1);
		} else if (view == ly.mButton2) {
			handleTitleBarEvent(BG_BUTTON2);
		} else if (view == ly.mButton3) {
			handleTitleBarEvent(BG_BUTTON3);
		} else if (view == mFavoriteText) {
			String param = NetWorkUtils.COLLECTION + "?userid="
					+ StaticInfo.userid + "&pageindex=" + 0 + "&pagecount="
					+ 30;
			new WebsiteParserTask(new CollectionsWebsiteParser(), this)
					.execute(param);
		} else if (view == ly.midButton) {
			// handleTitleBarEvent(MID_BUTTON);
			sendEmail();
		}
	}

	private void sendEmail() {
		// Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
		// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// String body = "更多精彩，更多快乐尽在优彩, www.baidu.com";
		// Uri data = Uri.parse("sms:?subject=" + "彩信应用推荐" + "&body=" + body);
		// intent.setData(data);
		// startActivity(intent);

		Uri uri = Uri.parse("smsto:");
		Intent it = new Intent(Intent.ACTION_SENDTO, uri);
		it.putExtra(
				"sms_body",
				"彩信应用推荐\n更多精彩，更多快乐尽在优彩, http://mobile.91.com/Soft/Detail.aspx?Platform=Android&f_id=4389766");
		startActivity(it);

		// intent.putExtra(Intent.EXTRA_SUBJECT, "分享微博");
		// if (hasRetweet) {
		// intent.putExtra(Intent.EXTRA_TEXT, mUserName.getText() + ": " +
		// mBlogContent.getText() + "\n\n" + "回复 " + retweetuname + ": " +
		// mRetweetBlogCnt.getText());
		// } else {
		// intent.putExtra(Intent.EXTRA_TEXT, mUserName.getText() + ": " +
		// mBlogContent.getText());
		// }
		// Intent mailer = Intent.createChooser(intent, "请选择邮件发送软件");
		// startActivity(mailer);

	}

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mResolver = this.getContentResolver();
		mInflater = (LayoutInflater) this.getSystemService("layout_inflater");
		mResources = getResources();

		// if (this instanceof HomeActivity) {
		mWebsiteInfo = new ClassificationWebSiteInfo();
		// }

		IntentFilter filter = new IntentFilter(ACTION_NOTIFY_DATA_SET_CHANGED);
		filter.addAction(ACTION_NOTIFY_DATA_SET_INITIALIZED);
		filter.addAction(ACTION_NOTIFY_DATA_SET_INVALIDATED);

		registerReceiver(mReceiver, filter);

		mImageWidth = (int) mResources.getDimension(R.dimen.image_with);
		mImageHeigth = (int) mResources.getDimension(R.dimen.image_height);

		mScreenWith = getWindowManager().getDefaultDisplay().getWidth();
		mScreenHeihgt = getWindowManager().getDefaultDisplay().getHeight();

		mFooterView = getLayoutInflater().inflate(R.layout.list_footer, null);
		mFooterView.setTag(0);
		mFooterView.setOnClickListener(this);
	}

	protected void setAppTitle(String paramString1, String paramString2) {
	}

	protected void setTitleBar(int paramInt, String paramString1,
			String paramString2, String paramString3) {
		if (this.ly != null)
			this.ly.setButtonTypeAndInfo(paramInt, paramString1, paramString2,
					paramString3);
	}

	protected void setView(int resId) {
		setContentView(resId);
		ly = (BaseLayout) this.findViewById(R.id.title);
		if (ly == null) {
			Log.d("zheng", "the ly is null");
		}

		ly.leftButton = (Button) ly.findViewById(R.id.left_button);
		ly.rightButton = (Button) ly.findViewById(R.id.right_button);
		ly.midButton = (Button) ly.findViewById(R.id.mid_button);
		ly.tvInfo = (TextView) ly.findViewById(R.id.center_textview);

		this.ly.leftButton.setOnClickListener(this);
		ly.leftButton.setOnTouchListener(this);
		this.ly.rightButton.setOnClickListener(this);
		ly.rightButton.setOnTouchListener(this);
		ly.midButton.setOnClickListener(this);
		ly.midButton.setOnTouchListener(this);

	}

	protected void showToast(int resId) {
		System.out.println("resId=" + resId);
		Toast toast = Toast.makeText(this, resId, Toast.LENGTH_LONG);
		toast.setGravity(17, 0, 0);
		toast.show();
	}

	class MessageHandler extends Handler {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			default:
			case 0:
			}
		}
	}

	protected Dialog createClearWordsDialog() {
		return null;
	}

	private Dialog createExitLoginDialog() {
		return new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setMessage("确认退出登录?")
				.setPositiveButton(R.string.okay_action,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dismissDialog(DIALOG_LOGOUT_ACCOUNT);
								switchToLoginActivity();
								// StaticInfo.mNickName = null;
								// StaticInfo.mUsername = null;
								// StaticInfo.mPassword = null;
								// // Process.killProcess(Process.myPid());
								// Intent intent = new Intent(getApplication(),
								// MainTabActivity.class);
								// startActivity(intent);
							}
						})
				.setNegativeButton(R.string.cancel_action,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dismissDialog(DIALOG_LOGOUT_ACCOUNT);
							}
						}).create();
	}

	protected Dialog createSaveImageDialog() {
		return null;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case DIALOG_SAVE_IMAGE:
			dialog = createSaveImageDialog();
			break;
		case DIALOG_LOGOUT_ACCOUNT:
			dialog = createExitLoginDialog();
			break;
		case DIALOG_CLEAR_WORDS:
			dialog = createClearWordsDialog();
			break;
		case DIALOG_CONFIRM_ADD_PHOTO:
			dialog = createNeedAddPhotoDialog();
			break;
		case DIALOG_COMBINE_SUCESS:
			dialog = createCombineSucessDialog();
			break;
		case DIALOG_CONFIRM_QUIT_YC:
			dialog = createConfirmQuitDialog();
			break;
		case DIALOG_BACK:
			dialog = createBackWorkingDialog();
			break;
		case DIALOG_QUIT:
			dialog = createQuitWorkingDialog();
			break;
		case DIALOG_CANCEL_FAVORITE:
			dialog = createCancelFavoriteDialog();
		default:
			break;
		}

		return dialog;
	}

	private Dialog createCancelFavoriteDialog() {
		return new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setMessage("确认取消收藏?")
				.setPositiveButton(R.string.okay_action,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dismissDialog(DIALOG_CANCEL_FAVORITE);
								new CommonWebsiteTask(
										new DelCollectionWebsiteParser(),
										BaseActivity.this)
										.execute(NetWorkUtils.DELLCOLLECTION
												+ "?id="
												+ ImageLayout.productId);
							}
						})
				.setNegativeButton(R.string.cancel_action,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dismissDialog(DIALOG_CANCEL_FAVORITE);
							}
						}).create();
	}

	protected void backWorkingPicturePage() {

	}

	private Dialog createQuitWorkingDialog() {
		return new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				// .setTitle("取消发布")
				.setMessage("确定要放弃正在编辑的彩信并退出吗?")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dismissDialog(DIALOG_QUIT);
						finish();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dismissDialog(DIALOG_QUIT);
					}
				}).create();
	}

	private Dialog createBackWorkingDialog() {
		return new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setMessage("确定要放弃正在编辑的彩信吗?")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dismissDialog(DIALOG_BACK);
						backWorkingPicturePage();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dismissDialog(DIALOG_BACK);
					}
				}).create();
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (!NetWorkUtils.checkNet(this)) {
			alertNetError(this);
		}
	}

	public void alertNetError(final Context context) {
		AlertDialog.Builder ab = new AlertDialog.Builder(context);
		ab.setTitle("网络出错");
		ab.setMessage("连接网络失败，请检查网络");
		ab.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			// @Override
			public void onClick(DialogInterface dialog, int which) {
				if (NetWorkUtils.checkNet(context)) {
					dialog.cancel();
				} else {
					context.startActivity(new Intent(
							android.provider.Settings.ACTION_WIRELESS_SETTINGS));
					dialog.cancel();
				}
			}
		});
		ab.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			// @Override
			public void onClick(DialogInterface dialog, int which) {
				// ֹͣ����
				// Intent it=new Intent(context,MainService.class);
				// context.stopService(it);
				if (!NetWorkUtils.checkNet(context)) {
					android.os.Process.killProcess(android.os.Process.myPid());
				}
			}
		});
		// ab.create().show();
	}

	protected Dialog createCombineSucessDialog() {
		return null;
	}

	protected Dialog createNeedAddPhotoDialog() {
		return new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setMessage("请添加一张图片或选择一个模板")
				.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dismissDialog(DIALOG_CONFIRM_ADD_PHOTO);
					}
				}).create();
	}

	protected Dialog createConfirmQuitDialog() {
		return new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setMessage("确认退出微博?")
				.setPositiveButton(R.string.okay_action,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dismissDialog(DIALOG_CONFIRM_QUIT_YC);
								android.os.Process
										.killProcess(android.os.Process.myPid());
							}
						})
				.setNegativeButton(R.string.cancel_action,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dismissDialog(DIALOG_CONFIRM_QUIT_YC);
							}
						}).create();
	}

	protected void setHeadPic(String ulogname, ImageView head_pic) {
		Bitmap headpic = mHeadPicMap.get(ulogname);
		if (headpic != null) {
			Drawable drawable = null;
			drawable = new BitmapDrawable(headpic);
			head_pic.setBackgroundDrawable(drawable);
		}
	}

	public final static int REQUST_CODE_NEED_RELOAD = 1;

	protected void notifyToUser(final String result) {
		Runnable run = new Runnable() {

			// @Override
			public void run() {
				System.out.println("Toast");
				if (result != null) {
					Toast.makeText(BaseActivity.this, result,
							Toast.LENGTH_SHORT).show();
				}
			}
		};

		runOnUiThread(run);
	}

	protected void returnToMainPage() {
		Intent intent = new Intent(this, MainTabActivity.class);
		intent.setAction("com.android.microblog.action.go_back_to_homepage");
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}

	protected void setSentTime(final long sentTime, final String sentDate,
			final TextView textview) {
		long nowTime = mNowTimeStamp;
		int timelength = (int) (nowTime - sentTime);
		String timeStr = null;
		// textview.setTextColor(Color.YELLOW);
		if (timelength < ONE_MINUTE) {
			timeStr = timelength + "秒钟前";
		} else if (timelength < ONE_HNOUR) {
			timeStr = timelength / ONE_MINUTE + "分钟前";
		} else if (timelength < ONE_DAY) {
			timeStr = timelength / ONE_HNOUR + "小时前";
		} else {
			// textview.setTextColor(Color.GRAY);
			timeStr = sentDate;
		}

		final String time = timeStr;
		textview.setText(time);
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

		return resizeBitmap;
	}

	public class ClassficationParserTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			Log.d("zheng", "calling parseClassificationWebsiteInfo");
			ClassificationLocalSiteInfo localInfo = new ClassificationLocalSiteInfo();
			localInfo.optype = params[0];
			localInfo.classid = params[1];
			localInfo.classname = params[2];
			// showProgessBarDialog("Loading...");
			MainTabActivity.getCurrentActivityGroup().showProgessBarDialog(
					"加载中...");
			mWebsiteInfo = new ClassificationWebsiteParser().parseWebsiteInfo(
					NetWorkUtils.GET_CLASS, localInfo);

			String urlParam = null;
			String hitsUrlParam = null;
			if (WebsiteParser.ALL.equals(localInfo.optype)) {
				urlParam = "?productid=&parentclass=&sonclass=&addtime=&pageindex=0&pagecount=4&ordertype=DownHits";
			} else if (WebsiteParser.PARENTCLASS.equals(localInfo.optype)) {
				urlParam = "?productid=&parentclass="
						+ localInfo.classid
						+ "&sonclass=&addtime=&pageindex=0&pagecount=4&ordertype=DownHits";
			} else if (WebsiteParser.SINGLE.equals(localInfo.optype)) {
				urlParam = "?productid=&parentclass=&sonclass="
						+ localInfo.classid
						+ "&addtime=&pageindex=0&pagecount=4&ordertype=DownHits";
			}

			WebsiteInfo webinfo = new SearchWebsiteParser()
					.parseWebsiteInfo(NetWorkUtils.GET_PRODUCTS + urlParam);
			// notifyToUser(webinfo.resultdesc);
			if (ServerInfo.FAIL.equals(webinfo.result)) {
				webinfo = null;
				// return null;
			}

			if (WebsiteParser.ALL.equals(localInfo.optype)) {
				hitsUrlParam = "?productid=&parentclass=&sonclass=&addtime=&pageindex=0&pagecount=4&ordertype=Hits";
				WebsiteInfo hitsWebinfo = new SearchWebsiteParser()
						.parseWebsiteInfo(NetWorkUtils.GET_PRODUCTS
								+ hitsUrlParam);
				notifyToUser(hitsWebinfo.resultdesc);
				if (!ServerInfo.FAIL.equals(hitsWebinfo.result)) {
					SearchWebsiteInfo productWebsiteInfo = (SearchWebsiteInfo) hitsWebinfo;
					ClassificationWebSiteInfo classificationWebsiteInfo = (ClassificationWebSiteInfo) mWebsiteInfo;
					ClassificationWebSiteInfo.ParentClassInfo parentinfo = new ClassificationWebSiteInfo.ParentClassInfo();
					parentinfo.mBaseInfo.classname = "最近更新";
					classificationWebsiteInfo.mParentInfoList
							.add(0, parentinfo);

					ClassificationWebSiteInfo.ConclassInfo conclassinfo = new ClassificationWebSiteInfo.ConclassInfo();
					parentinfo.newEntry(conclassinfo);

					int productCount = productWebsiteInfo.getProductsCount();
					for (int i = 0; i < productCount; i++) {
						SearchWebsiteInfo.ProductInfo productinfo = (SearchWebsiteInfo.ProductInfo) productWebsiteInfo
								.getProductInfo(i);
						conclassinfo.newEntry(productinfo);
					}
				}
			} else if (WebsiteParser.PARENTCLASS.equals(localInfo.optype)) {
				// hitsUrlParam =
				// "?productid=&parentclass=&sonclass=&addtime=&pageindex=0&pagecount=30&ordertype=Hits";
				// WebsiteInfo hitsWebinfo = new
				// SearchWebsiteParser().parseWebsiteInfo(NetWorkUtils.GET_PRODUCTS
				// + hitsUrlParam);
				// notifyToUser(hitsWebinfo.resultdesc);
				// if (!ServerInfo.FAIL.equals(hitsWebinfo.result)) {
				//
				// }
			}

			SearchWebsiteInfo productWebsiteInfo = (SearchWebsiteInfo) webinfo;
			if (productWebsiteInfo == null) {
				return null;
			}
			ClassificationWebSiteInfo.ConclassInfo conclassinfo = null;
			if (WebsiteParser.ALL.equals(localInfo.optype)) {
				ClassificationWebSiteInfo classificationWebsiteInfo = (ClassificationWebSiteInfo) mWebsiteInfo;
				ClassificationWebSiteInfo.ParentClassInfo parentinfo = new ClassificationWebSiteInfo.ParentClassInfo();
				parentinfo.mBaseInfo.classname = "下载排行";
				classificationWebsiteInfo.newEntry(parentinfo);

				conclassinfo = new ClassificationWebSiteInfo.ConclassInfo();
				parentinfo.newEntry(conclassinfo);
			} else if (WebsiteParser.PARENTCLASS.equals(localInfo.optype)) {
				ClassificationWebSiteInfo.ParentClassInfo parentinfo = (ClassificationWebSiteInfo.ParentClassInfo) mWebsiteInfo;
				conclassinfo = new ClassificationWebSiteInfo.ConclassInfo();
				conclassinfo.mBaseInfo.classname = "下载排行";
				parentinfo.newEntry(conclassinfo);
			} else if (WebsiteParser.SINGLE.equals(localInfo.optype)) {
				conclassinfo = (ClassificationWebSiteInfo.ConclassInfo) mWebsiteInfo;
				conclassinfo.mBaseInfo.classname = "栏目下载排行";
			}

			int productCount = productWebsiteInfo.getProductsCount();
			for (int i = 0; i < productCount; i++) {
				SearchWebsiteInfo.ProductInfo productinfo = (SearchWebsiteInfo.ProductInfo) productWebsiteInfo
						.getProductInfo(i);
				conclassinfo.newEntry(productinfo);
			}
			// Intent intent = new Intent(ACTION_NOTIFY_DATA_SET_INITIALIZED);
			// getApplicationContext().sendBroadcast(intent);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			MainTabActivity.getCurrentActivityGroup()
					.dismissProgressBarDialog();
			// pd.dismiss();
			mPhotoAdapter = new PhotoListAdapter(BaseActivity.this,
					mWebsiteInfo);
			if (mListView != null) {
				mListView.setAdapter(mPhotoAdapter);
			}
			if (mGridView != null) {
				mGridView.setAdapter(mPhotoAdapter);
			}
			// super.onPostExecuted(result);
		}
	}

	private void saveUserInfo() {

	}

	public class MusicParserTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			Log.d("zheng", "calling MusicParserTask");
			MusicLocalSiteInfo localInfo = new MusicLocalSiteInfo();
			localInfo.optype = params[0];
			localInfo.id = params[1];
			// showProgessBarDialog("Loading...");
			mWebsiteInfo = new MusicWebsiteParser().parseWebsiteInfo(
					NetWorkUtils.GET_MUSIC, localInfo);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// pd.dismiss();
			mMusicAdapter = new MusicListAdapter(
					MainTabActivity.mHomeActivityGroup.getParent(),
					android.R.layout.simple_spinner_item, mWebsiteInfo);
			mMusicAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			mSpinner.setAdapter(mMusicAdapter);
			mSpinner.setSelected(false);
		}
	}

	public class CollectionsParserTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}
	}

	public class WebsiteParserTask extends AsyncTask<String, Void, Void> {
		private WebsiteParser mWebsiteParser;
		private Context mContext;

		public WebsiteParserTask(WebsiteParser websiteParser, Context context) {
			mWebsiteParser = websiteParser;
			mContext = context;
		}

		@Override
		protected Void doInBackground(String... params) {
			Log.d("zheng", "calling SearchParserTask");
			// showProgessBarDialog("Loading...");
			MainTabActivity.getCurrentActivityGroup().showProgessBarDialog(
					"加载中...");
			mWebsiteInfo = mWebsiteParser.parseWebsiteInfo(params[0]);

			int productCount = mWebsiteInfo.getProductsCount();
			for (int j = 0; j < productCount; j++) {
				ClassificationWebSiteInfo.ProductInfo productinfo = (ClassificationWebSiteInfo.ProductInfo) mWebsiteInfo
						.getProductInfo(j);
				if (productinfo == null) {
					continue;
				}
				if (productinfo.photobitmap == null) {

					// query the photo from database firstly
					productinfo.photobitmap = Utils.getPhotoFromDatabase(
							mContext, productinfo);
					if (productinfo.photobitmap != null) {
						Log.d("zheng", "getPhotoFromDatabase:"
								+ productinfo.photoaddress);
					}

					// if database doesn't store this photo, get it from network
					if (productinfo.photobitmap == null) {
						try {
							productinfo.photobitmap = Utils
									.getBitMapFromNetwork(
											productinfo.photoaddress, mContext);
							if (productinfo.photobitmap != null) {
								Log.d("zheng", "getBitMapFromNetwork:"
										+ productinfo.photoaddress);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

						if (productinfo.photobitmap != null) {
							// restore this photo into database
							// Utils.savePhotoIntoDatabase(mResolver,
							// productinfo);

							productinfo.photobitmap = Utils.getResizedBitmap(
									productinfo.photobitmap, mImageWidth,
									mImageHeigth);
							Log.d("zheng", "the bitmap with:"
									+ productinfo.photobitmap.getWidth());
							Log.d("zheng", "the bitmap height:"
									+ productinfo.photobitmap.getHeight());
						}
					}
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// pd.dismiss();
			MainTabActivity.getCurrentActivityGroup()
					.dismissProgressBarDialog();
			ImageView iv = null;
			if (mWebsiteInfo == null) {
				return;
			}

			notifyToUser(mWebsiteInfo.resultdesc);
			if (ServerInfo.FAIL.equals(mWebsiteInfo.result)) {
				return;
			}
			int productCount = mWebsiteInfo.getProductsCount();
			LinearLayout lineLayout = null;
			ImageLayout imageLayout = null;
			ImageView imageView = null;
			ProgressBar probar = null;
			boolean needDownload = false;
			boolean showButton = false;
			mProductList.removeAllViewsInLayout();
			if (mWebsiteInfo instanceof CollectionsWebsiteInfo) {
				showButton = true;

				mFavoriteText = (TextView) mInflater.inflate(
						R.layout.textview_layout, null);
				mFavoriteText.setOnClickListener(BaseActivity.this);
				mProductList.addView(mFavoriteText);
			}
			Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(),
					R.drawable.default_image);
			bmp = Utils.getResizedBitmap(bmp, mImageWidth, mImageHeigth);
			for (int productIndex = 0; productIndex < productCount; productIndex++) {
				if (productIndex % mLineCount == 0) {
					lineLayout = new LinearLayout(mContext);
					mProductList.addView(lineLayout);
				}

				ProductInfo productinfo = (ProductInfo) mWebsiteInfo
						.getProductInfo(productIndex);
				imageLayout = new ImageLayout(mContext, null, productinfo,
						showButton);

				imageView = (ImageView) imageLayout.findViewById(R.id.image);
				probar = (ProgressBar) imageLayout
						.findViewById(R.id.progressbar);
				if (productinfo.photobitmap != null) {
					probar.setVisibility(View.GONE);
					imageView.setImageBitmap(productinfo.photobitmap);
				} else {
					imageView.setImageBitmap(bmp);
					probar.setVisibility(View.VISIBLE);
					needDownload = true;
				}

				((LinearLayout) lineLayout).addView(imageLayout, productIndex
						% mLineCount);

				// if (productinfo.photobitmap != null) {
				// iv = (ImageView)mInflater.inflate(R.layout.image, null);
				// iv.setImageBitmap(productinfo.photobitmap);
				// mProductList.addView(iv, productIndex);
				// }
			}

			mProductList.invalidate();
			if (needDownload && mLoadImageTask == null) {
				mLoadImageTask = new LoadImageTask(mContext, mWebsiteInfo);
				mLoadImageTask.execute();
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mReceiver != null) {
			unregisterReceiver(mReceiver);
			mReceiver = null;
		}

		if (pd != null) {
			pd.dismiss();
		}
	}

	public BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (ACTION_NOTIFY_DATA_SET_CHANGED.equals(intent.getAction())) {
				if (mPhotoAdapter != null) {
					mPhotoAdapter.notifyDataSetChanged();
				}

				// if (mAlbumAdapter != null) {
				// mAlbumAdapter.notifyDataSetChanged();
				// }

				if (mListView != null) {
					BaseAdapter adapter = (BaseAdapter) mListView.getAdapter();
					if (adapter != null) {
						adapter.notifyDataSetChanged();
					}
				}
			} else if (ACTION_NOTIFY_DATA_SET_INVALIDATED.equals(intent
					.getAction())) {
				mPhotoAdapter.notifyDataSetInvalidated();
			} else if (ACTION_NOTIFY_DATA_SET_INITIALIZED.equals(intent
					.getAction())) {
				if (mPhotoAdapter == null) {
					mPhotoAdapter = new PhotoListAdapter(BaseActivity.this,
							mWebsiteInfo);
					mListView.setAdapter(mPhotoAdapter);
				}
			}

			Log.d("zheng", "BroadcastReceiver notifyDataSetChanged");
		}
	};

	public ProgressDialog pd;

	public void showProgessBarDialog(final String msg) {
		Runnable action = new Runnable() {

			public void run() {
				if (pd == null) {
					pd = new ProgressDialog(BaseActivity.this);
					// pd=new ProgressDialog(getApplicationContext());
					// pd.setTitle("加载图片");
				}
				pd.setMessage(" 加载中... ");
				// pd.show();
			}
		};

		runOnUiThread(action);
	}

	public boolean onTouch(View v, MotionEvent event) {
		if (v == null) {
			return false;
		}
		// if (event.getAction() == MotionEvent.ACTION_DOWN) {
		// v.getBackground().setAlpha(155);//
		// v.invalidate();
		// } else if (event.getAction() == MotionEvent.ACTION_UP) {
		// v.getBackground().setAlpha(255);//
		// v.invalidate();
		// }

		return false;
	}

	// public class WebsiteParserTask extends AsyncTask<String, Void,
	// WebsiteInfo> {
	// public WebsiteParser mWebsiteParser = null;
	//
	// public WebsiteParserTask(WebsiteParser parser) {
	// mWebsiteParser = parser;
	// }
	//
	// @Override
	// protected WebsiteInfo doInBackground(String... params) {
	// WebsiteInfo websiteInfo = mWebsiteParser.parseWebsiteInfo(params[0]);
	// return websiteInfo;
	// }
	//
	// @Override
	// protected void onPostExecute(WebsiteInfo result) {
	// postExecute(result);
	// }
	// }

	// public void postExecute(WebsiteInfo result) {
	//
	// }
	protected Bitmap bmp;

	public static void getGalleryPicture(Activity activity) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("image/*");
		activity.startActivityForResult(Intent.createChooser(intent, "选择图片"), 0);
	}

	protected void getCameraPicture(Activity activity) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		activity.startActivityForResult(intent, 3);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		System.out.println("requestCode==" + requestCode + "----"
				+ "resultCode==" + resultCode + "-----" + "data===" + data);
		if (resultCode == RESULT_OK) {
			// 选择图片
			Uri uri = data.getData();
			if (null == uri) {
				bmp = (Bitmap) (data.getExtras() == null ? null : data
						.getExtras().get("data"));
				setPhoto(bmp);
				return;
			}
			ContentResolver cr = this.getContentResolver();
			try {
				if (bmp != null && !bmp.isRecycled())// 如果不释放的话，不断取图片，将会内存不够
					bmp.recycle();
				bmp = BitmapFactory.decodeStream(cr.openInputStream(uri));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("the bmp toString: " + bmp);
			// mPicView.setImageBitmap(bmp);
			setPhoto(bmp);
		} else {
			Toast.makeText(this, "请重新选择图片", Toast.LENGTH_SHORT).show();
		}
	}

	protected void setPhoto(Bitmap photo) {

	}

	protected String saveImage(Context context, String photoaddress) {
		if (photoaddress == null) {
			return null;
		}
		File sdCard = null;
		try {
			String filename = "/sdcard/";
			int index = photoaddress.lastIndexOf("/");
			filename += photoaddress.substring(index + 1);
			sdCard = new File(filename);
			if (sdCard.exists()) {
				return filename;
			}

			Bitmap bitmap = Utils.getBitMapFromNetwork(photoaddress, context);
			if (bitmap == null) {
				return null;
			}
			FileOutputStream outStreamz = new FileOutputStream(sdCard);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStreamz);
			outStreamz.close();
			return filename;
		} catch (Exception e) {
			e.printStackTrace();

			if (photoaddress == null) {
				return null;
			}

			try {
				String filename = "/mnt/sdcard/";
				int index = photoaddress.lastIndexOf("/");
				filename += photoaddress.substring(index + 1);
				sdCard = new File(filename);
				if (sdCard.exists()) {
					return filename;
				}

				Bitmap bitmap = Utils.getBitMapFromNetwork(photoaddress,
						context);
				if (bitmap == null) {
					notifyToUser("获取图片失败, 请检查网络!");
					return null;
				}
				FileOutputStream outStreamz = new FileOutputStream(sdCard);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStreamz);
				outStreamz.close();
				return filename;
			} catch (Exception e1) {
				notifyToUser("保存失败，请检查SD卡是否可用!");
				e1.printStackTrace();
			}
		}

		return null;
	}
}
