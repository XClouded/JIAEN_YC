package com.xhm.firstPage;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;

import com.android.photoalbum.R;
import com.androids.photoalbum.view.YC;

public class ViewPagerActivity extends Activity {
	private ViewPager viewPager;
	/** 装分页显示的view的数组 */
	private ArrayList<View> pageViews;
	private ImageView imageView;
	/** 将小圆点的图片用数组表示 */
	private ImageView[] imageViews;
	// 包裹滑动图片的LinearLayout
	private ViewGroup viewPics;
	// 包裹小圆点的LinearLayout
	private ViewGroup viewPoints;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 设置无标题栏
		// 将要分页显示的View装入数组中
		LayoutInflater inflater = getLayoutInflater();
		pageViews = new ArrayList<View>();

		ImageView imageView2 = new ImageView(this);
		// imageView2.setBackgroundDrawable(getResources().getDrawable(
		// R.drawable.load_page_));
		// pageViews.add(imageView2);
		//
		// imageView2 = new ImageView(this);
		imageView2.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.load_page_0));
		pageViews.add(imageView2);

		imageView2 = new ImageView(this);
		imageView2.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.load_page_1));
		pageViews.add(imageView2);

		imageView2 = new ImageView(this);
		imageView2.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.load_page_2));
		pageViews.add(imageView2);

		// 创建imageviews数组，大小是要显示的图片的数量
		imageViews = new ImageView[pageViews.size()];
		// 从指定的XML文件加载视图
		viewPics = (ViewGroup) inflater.inflate(R.layout.view_pics, null);

		// 实例化小圆点的linearLayout和viewpager
		viewPoints = (ViewGroup) viewPics.findViewById(R.id.viewGroup);
		viewPager = (ViewPager) viewPics.findViewById(R.id.guidePages);

		// 添加小圆点的图片
		for (int i = 0; i < pageViews.size(); i++) {
			imageView = new ImageView(ViewPagerActivity.this);
			// 设置小圆点imageview的参数
			imageView.setLayoutParams(new LayoutParams(13, 13));// 创建一个宽高均为20
																// 的布局
			imageView.setPadding(20, 0, 20, 0);
			// 将小圆点layout添加到数组中
			imageViews[i] = imageView;

			// 默认选中的是第一张图片，此时第一个小圆点是选中状态，其他不是
			if (i == 0) {
				imageViews[i]
						.setBackgroundResource(R.drawable.page_indicator_focused);
			} else {
				imageViews[i].setBackgroundResource(R.drawable.page_indicator);
			}
			// 将imageviews添加到小圆点视图组
			viewPoints.addView(imageViews[i]);
		}

		// 显示滑动图片的视图
		setContentView(viewPics);

		// 设置viewpager的适配器和监听事件
		viewPager.setAdapter(new GuidePageAdapter());
		viewPager.setOnPageChangeListener(new GuidePageChangeListener());
		viewPager.setCurrentItem(0);
		pageViews.get(pageViews.size() - 1).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
						Intent mainTabIntent = new Intent(
								ViewPagerActivity.this, YC.class);
						startActivity(mainTabIntent);
					}
				});
	}

	class GuidePageAdapter extends PagerAdapter {

		// 销毁position位置的界面
		@Override
		public void destroyItem(View v, int position, Object arg2) {
			// TODO Auto-generated method stub
			((ViewPager) v).removeView(pageViews.get(position));
			System.out.println("destroyItem");
		}

		@Override
		public void finishUpdate(View arg0) {
			// TODO Auto-generated method stub
			System.out.println("finishUpdate");
		}

		// 获取当前窗体界面数
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			System.out.println("getCount");
			return pageViews.size();
		}

		// 初始化position位置的界面
		@Override
		public Object instantiateItem(View v, int position) {
			// TODO Auto-generated method stub
			System.out.println("instantiateItem");
			((ViewPager) v).addView(pageViews.get(position));
			return pageViews.get(position);
		}

		// 判断是否由对象生成界面
		@Override
		public boolean isViewFromObject(View v, Object arg1) {
			// TODO Auto-generated method stub
			System.out.println("isViewFromObject");
			return v == arg1;
		}

		@Override
		public void startUpdate(View arg0) {
			// TODO Auto-generated method stub
			System.out.println("startUpdate");
		}

		@Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub
			System.out.println("getItemPosition");
			return super.getItemPosition(object);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
			// TODO Auto-generated method stub
			System.out.println("restoreState");
		}

		@Override
		public Parcelable saveState() {
			// TODO Auto-generated method stub
			System.out.println("saveState");
			return null;
		}
	}

	class GuidePageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageSelected(int position) {
			System.out.println("position=" + position);
			for (int i = 0; i < imageViews.length; i++) {
				if (i == position) {
					imageViews[position]
							.setBackgroundResource(R.drawable.page_indicator_focused);
				} else {
					imageViews[i]
							.setBackgroundResource(R.drawable.page_indicator);
				}

			}
			// TODO Auto-generated method stub
			// if (position == 0) {
			// for (int i = 0; i < imageViews.length; i++) {
			// imageViews[position]
			// .setBackgroundResource(R.drawable.page_indicator_focused);
			// if (position != i) {
			// imageViews[i]
			// .setBackgroundResource(R.drawable.page_indicator);
			// }
			// }
			// } else if (position == imageViews.length + 1) {
			// for (int i = 0; i < imageViews.length; i++) {
			// imageViews[position - 2]
			// .setBackgroundResource(R.drawable.page_indicator_focused);
			// if (position - 2 != i) {
			// imageViews[i]
			// .setBackgroundResource(R.drawable.page_indicator);
			// }
			// }
			// } else {
			// for (int i = 0; i < imageViews.length; i++) {
			// imageViews[position - 1]
			// .setBackgroundResource(R.drawable.page_indicator_focused);
			// if (position - 1 != i) {
			// imageViews[i]
			// .setBackgroundResource(R.drawable.page_indicator);
			// }
			// }
			// }
			// 实现回弹效果
			// if (position == 0)
			// viewPager.setCurrentItem(position + 1);
			// else if (position == pageViews.size() - 1)
			// viewPager.setCurrentItem(position - 1);
		}

	}
}