package com.xhm.q3.view;

import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.android.photoalbum.R;
import com.androids.photoalbum.view.MainTabActivity;
import com.xhm.q3.GetVideo_info.q3_GetVideo_Info;
import com.xhm.q3.view.TuiJianAdapter.ViewHolder;

public class q3_youcai_fenxiang extends Activity implements OnClickListener {
	private ImageView mBack;
	private ListView mListView;
	private ArrayList<q3_Video_Info> mQ3_Video_Infos;
	private Button mButton_wdfx, mButton_hyfx;
	private String mName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.q3_youcai_fenxiang);
		SharedPreferences editor = getSharedPreferences(
				MainTabActivity.USER_INFO, MODE_WORLD_READABLE);
		mName = editor.getString("username", "-1");
		mQ3_Video_Infos = q3_GetVideo_Info.getShareVideoInfo(
				"http://www.uuunm.com/getshareVideo.jsp", null, null, mName);
		initView();
	}

	private void initView() {
		mBack = (ImageView) findViewById(R.id.q3_youcaifeixiang_back);
		mBack.setOnClickListener(this);
		mButton_wdfx = (Button) findViewById(R.id.q3_youcai_fenxiang_wdfx);
		mButton_wdfx.setOnClickListener(this);
		mButton_hyfx = (Button) findViewById(R.id.q3_youcai_fenxiang_hyfx);
		mButton_hyfx.setOnClickListener(this);
		mListView = (ListView) findViewById(R.id.q3_youcai_fenxiang_list);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.q3_youcaifeixiang_back:
			finish();
			break;
		case R.id.q3_youcai_fenxiang_wdfx:
			mButton_wdfx
					.setBackgroundResource(R.drawable.q3_youcai_fenxiang_wdfx_focus);
			mButton_hyfx
					.setBackgroundResource(R.drawable.q3_youcai_fenxiang_hyfx);
			mQ3_Video_Infos = q3_GetVideo_Info
					.getShareVideoInfo(
							"http://www.uuunm.com/getshareVideo.jsp", null,
							null, mName);
			break;
		case R.id.q3_youcai_fenxiang_hyfx:
			mButton_wdfx
					.setBackgroundResource(R.drawable.q3_youcai_fenxiang_wdfx);
			mButton_hyfx
					.setBackgroundResource(R.drawable.q3_youcai_fenxiang_hyfx_focus);
			mQ3_Video_Infos = q3_GetVideo_Info.getShareVideoInfo(
					"http://www.uuunm.com/getfriendsshareVideo.jsp", null,
					null, mName);
			break;
		default:
			break;
		}
	}

	class FenXiangAdapter extends BaseAdapter implements
			OnCheckedChangeListener {
		ViewHolder holder = null;

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mQ3_Video_Infos.size();
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
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(q3_youcai_fenxiang.this)
						.inflate(R.layout.q3_youcai_fenxiang_item, null);
				holder.mPic = (ImageView) convertView
						.findViewById(R.id.q3_youcai_fenxiang_item_pic);
				holder.mName = (TextView) convertView
						.findViewById(R.id.q3_youcai_fenxiang_item_name);
				holder.mDetail = (Button) convertView
						.findViewById(R.id.q3_youcai_fenxiang_item_detail);
				holder.mDetail
						.setOnClickListener((OnClickListener) FenXiangAdapter.this);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.mPosition = position;
			return convertView;
		}

		class ViewHolder {
			int mPosition;
			ImageView mPic;
			TextView mName;
			Button mDetail;
		}

		public ViewHolder getViewHolder(View v) {
			if (v.getTag() == null) {
				return getViewHolder((View) v.getParent());
			}
			return (ViewHolder) v.getTag();
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			// TODO Auto-generated method stub

		}
	}
}
