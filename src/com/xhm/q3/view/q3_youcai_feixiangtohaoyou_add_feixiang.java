package com.xhm.q3.view;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
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
import com.androids.photoalbum.view.MainTabActivity;
import com.xhm.q3.GetVideo_info.q3_GetVideo_Info;
import com.xhm.q3.view.TuiJianAdapter.ViewHolder;

public class q3_youcai_feixiangtohaoyou_add_feixiang extends Activity implements
		OnClickListener {
	private ImageView mBack, mShousuo;
	private TextView mFeixiang;
	private ListView mList;
	private Button mSelect, mfanhui;
	private ArrayList<q3_Haoyou_Info> mHaoYou_Infos;
	private String mName;
	private feixiangtohaoyouAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.q3_youcai_feixiangtohaoyou_add_feixiang);
		initView();
		initVar();
	}

	private void initVar() {
		SharedPreferences editor = getSharedPreferences(
				MainTabActivity.USER_INFO, MODE_WORLD_READABLE);
		mName = editor.getString("username", "-1");
		mHaoYou_Infos = q3_GetVideo_Info.GetfriendsInfo(null, null, mName);
		mAdapter = new feixiangtohaoyouAdapter(mHaoYou_Infos, this);
		mList.setAdapter(mAdapter);
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
			String phone = "";
			for (int i = 0; i < mAdapter.mHaoyou_Infos.size(); i++) {
				boolean isSelect = mAdapter.mHaoyou_Infos.get(i).getmIsSelect();
				if (isSelect) {
					if (phone == "" || phone.length() == 0) {
						phone += mAdapter.mHaoyou_Infos.get(i).getmPhone_num();
					} else {
						phone += ","
								+ mAdapter.mHaoyou_Infos.get(i).getmPhone_num();
					}

				}
			}
			Intent intent = new Intent(this, q3_youcai_haoyou_add.class);
			intent.putExtra("phone", phone);
			setResult(20, intent);
			finish();
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
		private ViewHolder holder = null;

		public feixiangtohaoyouAdapter(ArrayList<q3_Haoyou_Info> Infos,
				Context mContext) {
			this.mContext = mContext;
			if (Infos == null) {
				this.mHaoyou_Infos = new ArrayList<q3_Haoyou_Info>();
			} else {
				this.mHaoyou_Infos = Infos;
			}
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mHaoYou_Infos.size();
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

			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.q3_youcai_haoyou_item, null);
			holder.mBox = (CheckBox) convertView
					.findViewById(R.id.q3_check_login);
			holder.mBox.setVisibility(View.VISIBLE);
			if (mHaoYou_Infos.get(position).getmIsSelect()) {
				holder.mBox.setButtonDrawable(R.drawable.check_box_selected);
			}
			holder.mBox.setOnCheckedChangeListener(this);
			holder.mName = (TextView) convertView
					.findViewById(R.id.q3_youcai_haoyou_item_name);
			holder.mId = (TextView) convertView
					.findViewById(R.id.q3_youcai_haoyou_item_id);
			holder.mButton_delete = (ImageView) convertView
					.findViewById(R.id.q3_youcai_haoyou_delete);
			holder.mButton_delete.setVisibility(View.INVISIBLE);
			holder.mButton_xiugai = (ImageView) convertView
					.findViewById(R.id.q3_youcai_haoyou_xiugai);
			holder.mButton_xiugai.setVisibility(View.GONE);
			convertView.setTag(holder);

			holder.position = position;
			holder.mName.setText(mHaoyou_Infos.get(position).getmName());
			holder.mId.setText(mHaoyou_Infos.get(position).getmPhone_num());
			return convertView;
		}

		class ViewHolder {
			private int position;
			private CheckBox mBox;
			private TextView mName, mId;
			private ImageView mButton_delete, mButton_xiugai;
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
			holder = getViewHolder(buttonView);
			if (isChecked) {
				holder.mBox.setButtonDrawable(R.drawable.check_box_selected);
				mHaoyou_Infos.get(holder.position).setmIsSelect(true);
			} else {
				mHaoyou_Infos.get(holder.position).setmIsSelect(false);
				holder.mBox
						.setButtonDrawable(R.drawable.check_box_not_selected);
			}
		}
	}
}
