package com.xhm.q3.view;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.photoalbum.R;
import com.xhm.q3.GetVideo_info.q3_GetVideo_Info;

public class q3_youcai_haoyou extends Activity implements OnClickListener {
	private ImageView mBack, mAdd;
	private ListView mListView;
	private ArrayList<q3_Haoyou_Info> mHaoyou_Infos;
	private HaoYou_Adapter mAdapter;

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

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		System.out.println("new__intent");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mAdapter.DataChange(q3_GetVideo_Info.GetfriendsInfo(null, null,
				getIntent().getStringExtra("name")));
	}

	private void initView() {
		mBack = (ImageView) findViewById(R.id.q3_youcaifeixiang_back);
		mBack.setOnClickListener(this);
		mAdd = (ImageView) findViewById(R.id.q3_youcai_haoyou_add);
		mAdd.setOnClickListener(this);
		mListView = (ListView) findViewById(R.id.q3_youcai_haoyou_list);
		System.out.println("mHaoyou_Infos" + mHaoyou_Infos.size());
		mAdapter = new HaoYou_Adapter(this, mHaoyou_Infos);
		mListView.setAdapter(mAdapter);
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

	public void DataChange(ArrayList<q3_Haoyou_Info> mHaoyou) {
		if (mHaoyou == null) {
			mHaoyou_Infos = new ArrayList<q3_Haoyou_Info>();
		} else {
			this.mHaoyou_Infos = mHaoyou;
		}
		notifyDataSetChanged();
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
		final ViewHolder holder = getViewHolder(v);
		switch (v.getId()) {
		case R.id.q3_youcai_haoyou_delete:
			new AlertDialog.Builder(mContext)
					.setIcon(R.drawable.ic_launcher)
					.setTitle("提示：")
					.setMessage("是否删除好友？")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									String str = q3_GetVideo_Info
											.Delfriend(mHaoyou_Infos.get(
													holder.position).getmID());
									if (str.contains("成功")) {
										Toast.makeText(mContext, "删除成功！",
												Toast.LENGTH_LONG).show();
										mHaoyou_Infos.remove(holder.position);
										notifyDataSetChanged();
									}
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									/* User clicked Cancel so do some stuff */
								}
							}).create().show();

			break;
		case R.id.q3_youcai_haoyou_xiugai:
			Intent intent = new Intent(mContext,
					q3_youcai_genggaihaoyouxinxi.class);
			intent.putExtra("id", mHaoyou_Infos.get(holder.position).getmID());
			intent.putExtra("name", mHaoyou_Infos.get(holder.position)
					.getmName());
			intent.putExtra("phone", mHaoyou_Infos.get(holder.position)
					.getmPhone_num());
			mContext.startActivity(intent);
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