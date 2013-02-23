package com.androids.photoalbum.view;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.android.photoalbum.R;
import com.androids.photoalbum.tab.ui.BaseActivity;

public class SelectPictureActivity extends BaseActivity {
	private Button mLocalPicture;
	private Button mTakeCamera;
	
	@Override
	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setView(R.layout.select_picture_layout);
		setTitleBar(com.androids.photoalbum.view.BaseLayout.TYPE_NORMAL, "", "", "返回");
		mLocalPicture = (Button)findViewById(R.id.local_select);
		mLocalPicture.setOnClickListener(this);
		mLocalPicture.setOnTouchListener(this);
		
		mTakeCamera = (Button)findViewById(R.id.take_picture);
		mTakeCamera.setOnClickListener(this);
		mTakeCamera.setOnTouchListener(this);
		
	    mImageWidth = (int)mResources.getDimension(R.dimen.mms_image_with);
	    mImageHeigth = (int)mResources.getDimension(R.dimen.mms_image_height);
	}

	@Override
	protected void handleTitleBarEvent(int paramInt) {
		MainTabActivity.getCurrentActivityGroup().setPreviousActivity();
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.local_select:
                //获取照片
				getGalleryPicture(MainTabActivity.getCurrentActivityGroup());
//                Intent in = new Intent(Intent.ACTION_GET_CONTENT);
//				in.setType("image/*");
//				MainTabActivity.getCurrentActivityGroup().startActivityForResult(in, 0);
				break;
			case R.id.take_picture:
				getCameraPicture(MainTabActivity.getCurrentActivityGroup());
//				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//				File out = new File(Environment.getExternalStorageDirectory(),
//						"camera.png");
//				Uri uri = Uri.fromFile(out);
//				intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//				MainTabActivity.getCurrentActivityGroup().startActivityForResult(intent, 3);
				break;
			default:
				super.onClick(view);
				break;
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
}