package com.xhm.q3.view;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.photoalbum.R;
import com.xhm.q3.GetVideo_info.q3_GetVideo_Info;
import com.xhm.q3.view.TuiJianAdapter.ViewHolder;

public class q3_youcai_haoyou extends Activity implements OnClickListener {
	private ImageView mBack, mAdd;
	private ListView mListView;
	private ArrayList<q3_Haoyou_Info> mHaoyou_Infos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.q3_youcai_haoyou);
		mHaoyou_Infos = new ArrayList<q3_Haoyou_Info>();
		mHaoyou_Infos = q3_GetVideo_Info.GetfriendsInfo(null, null, getIntent()
				.getStringExtra("name"));
		initView();
	}

	private void initView() {
		mBack = (ImageView) findViewById(R.id.q3_youcaifeixiang_back);
		mBack.setOnClickListener(this);
		mAdd = (ImageView) findViewById(R.id.q3_youcai_haoyou_add);
		mAdd.setOnClickListener(this);
		mListView = (ListView) findViewById(R.id.q3_youcai_haoyou_list);
		System.out.println("mHaoyou_Infos" + mHaoyou_Infos.size());
		HaoYou_Adapter adapter = new HaoYou_Adapter(this, mHaoyou_Infos);
		mListView.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.q3_youcaifeixiang_back:
			finish();
			break;
		case R.id.q3_youcai_haoyou_add:
			Intent intent = new Intent(this, q3_youcai_haoyou_add.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}
}

class HaoYou_Adapter extends BaseAdapter implements OnClickListener {
	private Context mContext;
	private ArrayList<q3_Haoyou_Info> mHaoyou_Infos;

	public HaoYou_Adapter(Context context, ArrayList<q3_Haoyou_Info> a) {
		this.mContext = context;
		if (a == null) {
			mHaoyou_Infos = new ArrayList<q3_Haoyou_Info>();
		} else {
			this.mHaoyou_Infos = a;
		}

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mHaoyou_Infos.size();
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
		ViewHolder getView = null;
		if (convertView == null) {
			getView = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.q3_youcai_haoyou_item, null);
			getView.mName = (TextView) convertView
					.findViewById(R.id.q3_youcai_haoyou_item_name);
			getView.mId = (TextView) convertView
					.findViewById(R.id.q3_youcai_haoyou_item_id);
			getView.mButton_delete = (ImageView) convertView
					.findViewById(R.id.q3_youcai_haoyou_delete);
			getView.mButton_delete.setOnClickListener(this);
			getView.mButton_xiugai = (ImageView) convertView
					.findViewById(R.id.q3_youcai_haoyou_xiugai);
			getView.mButton_xiugai.setOnClickListener(this);
			convertView.setTag(getView);
		} else {
			getView = (ViewHolder) convertView.getTag();
		}
		getView.position = position;
		getView.mName.setText(mHaoyou_Infos.get(position).getmName());
		getView.mId.setText(mHaoyou_Infos.get(position).getmPhone_num());
		return convertView;
	}

	class ViewHolder {
		int position;
		TextView mName, mId;
		ImageView mButton_delete, mButton_xiugai;
	}

	@Override
	public void onClick(View v) {
		ViewHolder holder = getViewHolder(v);
		switch (v.getId()) {
		case R.id.q3_youcai_haoyou_delete:
			System.out.println("positon===" + holder.position);

			String str = q3_GetVideo_Info.Delfriend(mHaoyou_Infos.get(
					holder.position).getmID());
			// mHaoyou_Infos = q3_GetVideo_Info.GetfriendsInfo(null, null,
			// getIntent()
			// .getStringExtra("name"));
			notifyDataSetChanged();
			break;
		case R.id.q3_youcai_haoyou_xiugai:
			System.out.println("positon===" + holder.position);
			break;
		default:
			break;
		}
	}

	public ViewHolder getViewHolder(View v) {
		if (v.getTag() == null) {
			return getViewHolder((View) v.getParent());
		}
		return (ViewHolder) v.getTag();
	}
}