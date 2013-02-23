package com.xhm.q3.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.photoalbum.R;

public class q3_info_model_activity extends Activity implements
		OnItemClickListener {
	private ImageView mImageView_back;
	private GridView mGridView_info;
	// 样板的图片
	private int model_image[] = { R.drawable.q3_info_no_logo,
			R.drawable.q3_info_left_logo, R.drawable.q3_info_right_logo,
			R.drawable.q3_info_more_page };
	// 样板的文字描述
	private String[] model_text = { "无LOGO名片", "左LOGO名片", "右LOGO名片", "多帧名片", };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.q3_info_modle_layout);
		initView();
		mGridView_info.setAdapter(new InfoAdapter(this));
		mGridView_info.setOnItemClickListener(this);
	}

	// 初始化控件
	private void initView() {
		mImageView_back = (ImageView) findViewById(R.id.q3_fanhui);
		mGridView_info = (GridView) findViewById(R.id.q3_gridview_info);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.q3_fanhui:
			finish();
			break;

		default:
			break;
		}
	}

	class InfoAdapter extends BaseAdapter {
		private Context mcontext;

		public InfoAdapter(Context c) {
			this.mcontext = c;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return model_image.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(mcontext).inflate(
						R.layout.q3_info_modle_item_layout, null);
				holder.imageView = (ImageView) convertView
						.findViewById(R.id.q3_gridview_info_item_image);
				holder.textView = (TextView) convertView
						.findViewById(R.id.q3_gridview_info_item_text);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.imageView.setImageResource(model_image[position]);
			holder.textView.setText(model_text[position]);
			return convertView;
		}

		class ViewHolder {
			ImageView imageView;
			TextView textView;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = null;
		switch (position) {
		case 0:
			intent = new Intent(this, q3_info_model_nologo_activity.class);
			startActivity(intent);
			break;
		case 1:
			intent = new Intent(this, q3_info_model_leftlogo_activity.class);
			startActivity(intent);
			break;
		case 2:
			intent = new Intent(this, q3_info_model_rightlogo_activity.class);
			startActivity(intent);
			break;
		case 3:
			intent = new Intent(this, q3_info_model_morepage_activity.class);
			startActivity(intent);
			break;

		default:
			break;
		}

	}
}
