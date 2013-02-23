package com.xhm.q3.view;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.photoalbum.R;

public class q3_youcai_feixiangtohaoyou_add_feixiang extends Activity implements
		OnClickListener {
	private ImageView mBack, mShousuo;
	private TextView mFeixiang;
	private ListView mList;
	private Button mSelect, mfanhui;
	private ArrayList<q3_Haoyou_Info> mHaoYou_Infos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.q3_youcai_feixiangtohaoyou_add_feixiang);
		initView();
	}

	private void initView() {
		mBack = (ImageView) findViewById(R.id.q3_youcaifeixiang_back);
		mBack.setOnClickListener(this);
		mShousuo = (ImageView) findViewById(R.id.q3_youcai_haoyou_add_local_rentou);
		mShousuo.setOnClickListener(this);
		mFeixiang = (TextView) findViewById(R.id.q3_youcai_haoyou_add_local);
		mList = (ListView) findViewById(R.id.q3_youcai_feixiangtohaoyou_add_feixiang_list);
		mSelect = (Button) findViewById(R.id.q3_youcai_feixiangtohaoyou_add_feixiang_selete);
		mSelect.setOnClickListener(this);
		mfanhui = (Button) findViewById(R.id.q3_youcai_feixiangtohaoyou_add_feixiang_back);
		mfanhui.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.q3_youcaifeixiang_back:
			finish();
			break;
		case R.id.q3_youcai_haoyou_add_local_rentou:

			break;
		case R.id.q3_youcai_feixiangtohaoyou_add_feixiang_selete:

			break;
		case R.id.q3_youcai_feixiangtohaoyou_add_feixiang_back:
			finish();
			break;

		default:
			break;
		}
	}

	class feixiangtohaoyouAdapter extends BaseAdapter implements
			OnCheckedChangeListener {
		private ArrayList<q3_Haoyou_Info> mHaoyou_Infos;
		private Context mContext;
		ViewHolder holder = null;

		public feixiangtohaoyouAdapter(ArrayList<q3_Haoyou_Info> Infos,
				Context mContext) {
			this.mContext = mContext;
			if (Infos == null) {
				mHaoYou_Infos = new ArrayList<q3_Haoyou_Info>();
			} else {
				mHaoYou_Infos = Infos;
			}
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 3;
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
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.q3_youcai_haoyou_item, null);
				holder.mBox = (CheckBox) convertView
						.findViewById(R.id.q3_check_login);
				holder.mBox.setVisibility(View.VISIBLE);
				holder.mBox.setOnCheckedChangeListener(this);
				holder.mName = (TextView) convertView
						.findViewById(R.id.q3_youcai_haoyou_item_name);
				holder.mId = (TextView) convertView
						.findViewById(R.id.q3_youcai_haoyou_item_id);
				holder.mButton_delete = (ImageView) convertView
						.findViewById(R.id.q3_youcai_haoyou_delete);
				holder.mButton_delete.setVisibility(View.INVISIBLE);
				holder.mButton_xiugai = (ImageView) findViewById(R.id.q3_youcai_haoyou_xiugai);
				holder.mButton_xiugai.setVisibility(View.INVISIBLE);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.position = position;
			return null;
		}

		class ViewHolder {
			private int position;
			private CheckBox mBox;
			private TextView mName, mId;
			private ImageView mButton_delete, mButton_xiugai;
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (isChecked) {
				holder.mBox.setButtonDrawable(R.drawable.check_box_selected);
				// mIsAutoLogin = true;
			} else {
				holder.mBox
						.setButtonDrawable(R.drawable.check_box_not_selected);
				// mIsAutoLogin = false;
			}
		}
	}
}
