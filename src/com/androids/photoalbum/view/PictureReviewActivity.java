package com.androids.photoalbum.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.android.photoalbum.R;
import com.androids.photoalbum.tab.ui.BaseActivity;
import com.androids.photoalbum.tab.ui.MessageActivity;
import com.androids.photoalbum.utils.Utils;
import com.androids.photoalbum.view.PictureCombineActivity.GridAdapter;

public class PictureReviewActivity extends BaseActivity {
	private Button mDownLoad;
	private Button mReturn;
	private GridView mGridView;
	private GridAdapter mAdapter;
	private ImageView mPic;
	private Bitmap mBitmap;
	private String mAddress;
	@Override
	protected void handleTitleBarEvent(int paramInt) {
		
	}
	
	@Override
	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setView(R.layout.pic_review_layout);
		setTitleBar(com.androids.photoalbum.view.BaseLayout.TYPE_NORMAL, "", "", "");
		mDownLoad = (Button)findViewById(R.id.download);
		mDownLoad.setOnClickListener(this);
		mDownLoad.setOnTouchListener(this);
		
		mReturn = (Button)findViewById(R.id.back);
		mReturn.setOnClickListener(this);
		mReturn.setOnTouchListener(this);
		
		mPic = (ImageView)findViewById(R.id.pic);

		
		mImageWidth = (int)mResources.getDimension(R.dimen.favorite_image_with);
	    mImageHeigth = (int)mResources.getDimension(R.dimen.favorite_image_height);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (mAddress == PictureCombineActivity.mCombinedAddress) {
			return;
		}
		mAddress = PictureCombineActivity.mCombinedAddress;
		mPic.setImageBitmap(null);
		showProgessBarDialog("Loading...");
		Runnable run = new Runnable() {
			
			public void run() {
				if (mBitmap != null && !mBitmap.isRecycled()) {
					mBitmap.recycle();
				}
				mBitmap = Utils.getBitMapFromNetwork(PictureCombineActivity.mCombinedAddress, PictureReviewActivity.this);
				Runnable action = new Runnable() {
					
					public void run() {
						mPic.setImageBitmap(mBitmap);
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
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back:
			MainTabActivity.getCurrentActivityGroup().setPreviousActivity();
			break;
		case R.id.download:
//		    if (mBitmap != null) {
		        downloadMessage();
//		    }
			break;
		default:
			super.onClick(view);
			break;
		}
	}
	
//	public static byte[] imgByte;
	private void downloadMessage() {
		Intent intent = new Intent(this, MessageActivity.class);
		intent.setAction(BaseActivity.ACTION_MESSAGE_COMPOSE);
//        final ByteArrayOutputStream os = new ByteArrayOutputStream();
//        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
//        byte[] imgByte = os.toByteArray();
//		intent.putExtra("photo_bitmap", imgByte);
		intent.putExtra("in_tab_group", false);
		intent.putExtra("from_activity", "PictureReviewActivity");
		intent.putExtra("is_combined_image", true);
		intent.putExtra("photoaddress", PictureCombineActivity.mCombinedAddress);
		Log.d("zheng", "downloadMessage");
		startActivity(intent);
	}
}