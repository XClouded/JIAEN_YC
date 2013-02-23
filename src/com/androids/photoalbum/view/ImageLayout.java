package com.androids.photoalbum.view;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.photoalbum.R;
import com.androids.photoalbum.netinfo.ClassificationWebSiteInfo.ProductInfo;
import com.androids.photoalbum.parser.AddCollectionWebsiteParser;
import com.androids.photoalbum.tab.ui.BaseActivity;
import com.androids.photoalbum.tab.ui.MessageActivity;
import com.androids.photoalbum.tab.ui.SignNameMessageActivity;
import com.androids.photoalbum.userinfo.StaticInfo;
import com.androids.photoalbum.utils.CommonWebsiteTask;
import com.androids.photoalbum.utils.NetWorkUtils;

public class ImageLayout extends LinearLayout implements View.OnClickListener,
		View.OnLongClickListener {
	private ImageView mImageView;
	public ProgressBar mProgressBar;
	private ProductInfo mProductInfo;
	private Context mContext;
	private boolean mShowButton;
	private Button mDelButton;
	private Button mSendButton;
	private int mPhotoType = PHOTO_TYPE_DEFAULT;
	public final static int PHOTO_TYPE_HOME = 0;
	public final static int PHOTO_TYPE_SIGN_NAME = 1;
	public final static int PHOTO_TYPE_SEARCH = 2;
	public final static int PHOTO_TYPE_DEFAULT = PHOTO_TYPE_HOME;
	public static String productId;
	public static Intent intent = null;

	public void setPhotoType(int photoType) {
		mPhotoType = photoType;
	}

	public String getPhotoAddress() {
		return mProductInfo.photoaddress;
	}

	public Bitmap getImage() {
		Drawable d = mImageView.getDrawable();
		BitmapDrawable bd = (BitmapDrawable) d;
		return bd.getBitmap();
	}

	public ImageLayout(Context context, AttributeSet attrs,
			ProductInfo productInfo, boolean showbutton) {
		super(context, attrs);
		mProductInfo = productInfo;
		LinearLayout imgFrm = (LinearLayout) inflate(context,
				R.layout.image_layout, null);
		this.addView(imgFrm);

		mContext = context;
		mImageView = (ImageView) findViewById(R.id.image);
		mImageView.setOnClickListener(this);
		((Activity) mContext).registerForContextMenu(mImageView);
		mImageView.setOnLongClickListener(this);
		mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
		mShowButton = showbutton;
		if (mShowButton) {
			mDelButton = (Button) findViewById(R.id.delete);
			mSendButton = (Button) findViewById(R.id.send);
			mDelButton.setVisibility(View.VISIBLE);
			mSendButton.setVisibility(View.VISIBLE);
			mDelButton.setOnClickListener(this);
			mSendButton.setOnClickListener(this);
		}
		setPadding(2, 0, 2, 0);
	}

	@Override
	protected void onCreateContextMenu(ContextMenu menu) {
		super.onCreateContextMenu(menu);
		if (mProductInfo.photobitmap == null) {
			return;
		}
		menu.setHeaderTitle("收藏此图片？");
		((Activity) mContext).getMenuInflater().inflate(R.menu.favorite_img,
				menu);
		menu.findItem(R.id.favorite_image).setTitle(mProductInfo.proname)
				.setIcon(new BitmapDrawable(mProductInfo.photobitmap));
	}

	public void onClick(View v) {
		if (mProductInfo == null || mProductInfo.photobitmap == null) {
			return;
		}

		if (MainTabActivity.mCurrentPage == 1) {
			intent = new Intent(mContext, SignNameMessageActivity.class);
		} else {
			intent = new Intent(mContext, MessageActivity.class);
		}
		intent.setAction(BaseActivity.ACTION_MESSAGE_COMPOSE);
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		mProductInfo.photobitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
		byte[] imgByte = os.toByteArray();
		intent.putExtra("photo_bitmap", imgByte);
		intent.putExtra("productid", mProductInfo.productid);
		intent.putExtra("id", mProductInfo.id);
		intent.putExtra("photoaddress", mProductInfo.photoaddress);

		switch (v.getId()) {
		case R.id.image:
			System.out.println("image");
			if (mProductInfo.photobitmap != null) {
				switch (MainTabActivity.mCurrentPage) {
				case 0:
					// mContext.startActivity(intent);
					// q3_选择收藏还是发送彩信
					AlertDialog.Builder builder = new AlertDialog.Builder(
							MainTabActivity.mContext);
					Drawable drawable = new BitmapDrawable(
							mProductInfo.photobitmap);
					builder.setTitle(mProductInfo.proname);
					builder.setIcon(drawable);
					builder.setPositiveButton("发送彩信",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									mContext.startActivity(intent);
								}
							});
					builder.setNegativeButton("收藏图片",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									if (StaticInfo.mPassword == null
											|| StaticInfo.mPassword.length() == 0) {
										Intent intent = new Intent(mContext,
												LoginActivity.class);
										intent.putExtra("isactivity", true);
										mContext.startActivity(intent);
										return;
									}
									new CommonWebsiteTask(
											new AddCollectionWebsiteParser(),
											mContext)
											.execute(NetWorkUtils.ADD_COLLECTION
													+ "?Proid="
													+ mProductInfo.productid
													+ "&userid="
													+ StaticInfo.userid);
								}
							});
					builder.show();
					break;
				default:
					break;
				}
			} else {
				mProgressBar.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.delete:
			productId = mProductInfo.id;
			MainTabActivity.getCurrentActivityGroup().showDialog(
					BaseActivity.DIALOG_CANCEL_FAVORITE);
			// ((FavoriteActivity)mContext).reloadFavoriteList();
			break;
		case R.id.send:
			mContext.startActivity(intent);
			break;
		default:
			break;
		}

	}

	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		System.out.println("long");
		return false;
	}
}