package com.androids.photoalbum.view;

import com.ant.liao.TouchGifView;
import com.ant.liao.TouchTextView;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class FrameContainer extends FrameLayout {
	public FrameContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			if (child instanceof TouchGifView) {
				TouchGifView touchGif = (TouchGifView) child;
				touchGif.layout(touchGif.left, touchGif.top, touchGif.left
						+ touchGif.getWidth(),
						touchGif.top + touchGif.getHeight());
			} else if (child instanceof TouchTextView) {
				TouchTextView touchGif = (TouchTextView) child;
				touchGif.layout(touchGif.left, touchGif.top, touchGif.left
						+ touchGif.getWidth(),
						touchGif.top + touchGif.getHeight());
			}
		}
	}
}