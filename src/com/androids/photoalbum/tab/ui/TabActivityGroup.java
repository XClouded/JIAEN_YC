package com.androids.photoalbum.tab.ui;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.photoalbum.R;
import com.androids.photoalbum.parser.DelCollectionWebsiteParser;
import com.androids.photoalbum.utils.CommonWebsiteTask;
import com.androids.photoalbum.utils.NetWorkUtils;
import com.androids.photoalbum.utils.Stack;
import com.androids.photoalbum.view.ImageLayout;
import com.androids.photoalbum.view.MainTabActivity;
import com.androids.photoalbum.view.PictureReviewActivity;

public class TabActivityGroup extends ActivityGroup implements
		OnItemSelectedListener {
	private Intent mCurrentIntent = null;
	private Stack mStack = new Stack();
	public ProgressDialog pd = null;
	private ContentResolver mResolver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setMainTabActivityGroup();
		initVar();
	}

	private void initVar() {
		mResolver = this.getContentResolver();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case BaseActivity.DIALOG_SAVE_IMAGE:
			break;
		case BaseActivity.DIALOG_CANCEL_FAVORITE:
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
								dismissDialog(BaseActivity.DIALOG_CANCEL_FAVORITE);
								new CommonWebsiteTask(
										new DelCollectionWebsiteParser(),
										TabActivityGroup.this)
										.execute(NetWorkUtils.DELLCOLLECTION
												+ "?id="
												+ ImageLayout.productId);
							}
						})
				.setNegativeButton(R.string.cancel_action,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dismissDialog(BaseActivity.DIALOG_CANCEL_FAVORITE);
							}
						}).create();
	}

	@Override
	public void onBackPressed() {
		finish();
	}

	public boolean showProgessBarDialog(final String msg) {
		if (pd != null) {
			return false;
		}
		Runnable action = new Runnable() {

			public void run() {
				pd = new ProgressDialog(TabActivityGroup.this);
				pd.setMessage(msg);
				pd.show();
			}
		};

		MainTabActivity.getCurrentActivityGroup().runOnUiThread(action);

		return true;
	}

	public void dismissProgressBarDialog() {
		Runnable action = new Runnable() {

			public void run() {
				if (pd != null) {
					pd.dismiss();
					pd = null;
				}
			}
		};

		MainTabActivity.getCurrentActivityGroup().runOnUiThread(action);
	}

	public void saveImage(Bitmap bitmap, String photoaddress) {
		if (photoaddress == null) {
			return;
		}
		// Bitmap bitmap = image.getImage();
		// HashMap<String, Bitmap> blogPicList =
		// mPicDownloadedReceiver.getBlogPicList();
		// Bitmap bitmap = blogPicList.get(imgbig);
		// if (bitmap == null) {
		// return;
		// }

		// if (d == null) {
		// return;
		// }
		//
		// BitmapDrawable bd = (BitmapDrawable) d;
		// Bitmap bitmap = bd.getBitmap();

		try {
			File sdPhoto = new File("/sdcard/优彩/");
			if (!sdPhoto.exists()) {
				sdPhoto.mkdirs();
			}

			String filename = "/sdcard/优彩/";
			int index = photoaddress.lastIndexOf("/");
			filename += photoaddress.substring(index + 1);
			File sdCard = new File(filename);
			FileOutputStream outStreamz = new FileOutputStream(sdCard);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStreamz);
			outStreamz.flush();
			outStreamz.close();
			notifyToUser("图片保存成功, 已加到你的相册");
		} catch (FileNotFoundException e) {
			notifyToUser("图片保存失败, 请确保sd卡可用");
			e.printStackTrace();
		} catch (IOException e) {
			notifyToUser("图片保存失败, 请确保sd卡可用");
			e.printStackTrace();
		}

		// pd.dismiss();
	}

	public void notifyToUser(final String result) {
		Runnable run = new Runnable() {

			// @Override
			public void run() {
				Toast.makeText(TabActivityGroup.this, result,
						Toast.LENGTH_SHORT).show();
			}
		};

		runOnUiThread(run);
	}

	public Dialog createSaveImageDialog() {
		return new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("下载艺术签名")
				// .setMessage("下载")
				.setPositiveButton("下载", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dismissDialog(BaseActivity.DIALOG_SAVE_IMAGE);
						// saveImage(SignNameActivity.mBitmap,
						// SignNameActivity.mPhotoaddress);
						ImageLayout.intent.putExtra("from_activity",
								"SignNameActivity");
						startActivity(ImageLayout.intent);
					}
				})
				.setNegativeButton(R.string.cancel_action,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dismissDialog(BaseActivity.DIALOG_SAVE_IMAGE);
							}
						}).create();
	}

	private void setMainTabActivityGroup() {
		switch (MainTabActivity.mCurrentPage) {
		case 0:
			MainTabActivity.mHomeActivityGroup = this;
			break;
		case 1:
			MainTabActivity.mWorkingActivityGroup = this;
			break;
		case 2:
			MainTabActivity.mMultiMessageActivityGroup = this;
			break;
		case 3:
			MainTabActivity.mSearchActivityGroup = this;
			break;
		case 4:
			MainTabActivity.mFavoriteActivityGroup = this;
			break;
		default:
			break;
		}
	}

	public boolean isActivityGroupNull() {
		return mStack.size() <= 0;
	}

	public void setJumpActivity(
			Context context,
			String id,
			Class<? extends com.androids.photoalbum.tab.ui.BaseActivity> tabActivity) {
		// 要跳转的界面
		mCurrentIntent = new Intent(context, tabActivity);
		// .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// 把一个Activity转换成一个View
		Window w = getLocalActivityManager().startActivity(id, mCurrentIntent);
		View view = w.getDecorView();
		// 把View添加大ActivityGroup中
		setContentView(view);

	}

	@Override
	protected void onDestroy() {
		mCurrentIntent = null;
		mStack.clear();
		super.onDestroy();
	}

	public Spinner mSpinner;

	public void setNextActivity(Intent intent, String id) {
		if (mCurrentIntent != null) {
			mStack.push(mCurrentIntent);
		}
		mCurrentIntent = intent;
		mCurrentIntent.putExtra("id", id);
		convertActivity();
	}

	public void setCurrentActivity() {
		if (mCurrentIntent != null) {
			convertActivity();
		}
	}

	private void convertActivity() {
		// mCurrentIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		String id = mCurrentIntent.getStringExtra("id");
		// 把一个Activity转换成一个View
		Window w = getLocalActivityManager().startActivity(id, mCurrentIntent);
		View view = w.getDecorView();
		// 把View添加大ActivityGroup中
		setContentView(view);
	}

	public void setPreviousActivity() {
		mCurrentIntent = mStack.pop();
		if (mCurrentIntent != null) {
			convertActivity();
		}
	}

	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		MessageActivity.mActivity.onItemSelected(parent, view, position, id);
	}

	public void onNothingSelected(AdapterView<?> arg0) {
	}

	// @Override
	protected void onActivityResult_abandon(int requestCode, int resultCode,
			Intent data) {
		Log.d("zheng", "TabActivityGroup onActivityResult");
		if (resultCode != RESULT_OK) {
			return;
		}

		if (requestCode == 10) {
			if (MainTabActivity.getCurrentActivityGroup() == MainTabActivity.mMultiMessageActivityGroup) {
				MultiMediaMessageActivity.contactActivityResult(requestCode,
						resultCode, data, MultiMediaMessageActivity.mReceiver,
						mResolver);
			} else {
				MultiMediaMessageActivity.contactActivityResult(requestCode,
						resultCode, data, MessageActivity.mReceiver, mResolver);
			}
			return;
		}

		int with = getWindowManager().getDefaultDisplay().getWidth();
		int heigth = getWindowManager().getDefaultDisplay().getHeight();
		if (requestCode == 0) {
			Intent cj = new Intent("com.android.camera.action.CROP");
			cj.setData(data.getData());
			cj.putExtra("crop", "true");
			cj.putExtra("aspectX", 2);
			cj.putExtra("aspectY", 2);
			cj.putExtra("outputX", with);
			cj.putExtra("outputY", heigth);
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
			cj1.putExtra("outputX", with);
			cj1.putExtra("outputY", heigth);
			cj1.putExtra("return-data", true);
			startActivityForResult(cj1, 2);

			File camera = new File("/mnt/sdcard/camera.png");
			if (camera.exists()) {
				camera.delete();
			}
		} else if (requestCode == 2) {
			// Intent intent = new Intent();
			if (true) {
				// setResult(MultiMediaMessageActivity.RESULT_CODE_OK, data);
				data.setAction(MultiMediaMessageActivity.ACTION_PICTURE_SELECTED);
				sendBroadcast(data);
				// finish();
				MainTabActivity.getCurrentActivityGroup().setPreviousActivity();
				return;
			}
			Bundle extras = data.getExtras();
			if (extras != null) {
				Bitmap photo = extras.getParcelable("data");

				// photo = Utils.getResizedBitmap(photo, width, height);
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

	protected Dialog createCombineSucessDialog() {
		return new AlertDialog.Builder(this)
				.setMessage("成功合成一条新彩信.")
				.setPositiveButton("查看", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dismissDialog(BaseActivity.DIALOG_COMBINE_SUCESS);
						Intent intent = new Intent(TabActivityGroup.this,
								PictureReviewActivity.class);
						MainTabActivity.getCurrentActivityGroup()
								.setNextActivity(intent,
										"PictureReviewActivity");
						// downloadMessage();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dismissDialog(BaseActivity.DIALOG_COMBINE_SUCESS);
					}
				}).create();
	}

	protected Dialog createNeedAddPhotoDialog() {
		return new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setMessage("请选择图片")
				.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dismissDialog(BaseActivity.DIALOG_CONFIRM_ADD_PHOTO);
					}
				}).create();
	}

	public static Bitmap bmp;

	protected void getGalleryPicture() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("image/*");
		startActivityForResult(Intent.createChooser(intent, "选择图片"), 0);
	}

	protected void getCameraPicture() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, 3);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			// 选择图片
			Uri uri = data.getData();
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

			Intent intent = new Intent(
					MultiMediaMessageActivity.ACTION_PICTURE_SELECTED);
			// data.setAction(MultiMediaMessageActivity.ACTION_PICTURE_SELECTED);
			sendBroadcast(intent);
			MainTabActivity.getCurrentActivityGroup().setPreviousActivity();
		} else {
			Toast.makeText(this, "请重新选择图片", Toast.LENGTH_SHORT).show();
		}
	}

	protected void setPhoto(Bitmap photo) {

	}
}