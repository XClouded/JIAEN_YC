package com.xhm.q3.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.photoalbum.R;
import com.androids.photoalbum.view.MainTabActivity;

public class q3_youcai_activity extends Activity implements OnClickListener,
		OnItemClickListener {
	public static ProgressDialog pd;
	private GridView mGridView;
	private ImageView mBack;
	private int PIC[] = { R.drawable.q3_youcaifeixiang_tuijian,
			R.drawable.q3_youcaifeixiang_remen,
			R.drawable.q3_youcaifeixiang_haoyou,
			R.drawable.q3_youcaifeixiang_paihang,
			R.drawable.q3_youcaifeixiang_fenlie,
			R.drawable.q3_youcaifeixiang_fenxiang, };
	private String DESCRIBE[] = { "推荐", "热门", "好友", "排行", "分类", "分享" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.q3_youcai_layout);
		initView();
	}

	private void initView() {
		mBack = (ImageView) findViewById(R.id.q3_youcaifeixiang_back);
		mBack.setOnClickListener(this);
		mGridView = (GridView) findViewById(R.id.q3_youcaifeixiang_grid);
		YouCai_Adapter adapter = new YouCai_Adapter(this, PIC, DESCRIBE);
		mGridView.setAdapter(adapter);
		mGridView.setOnItemClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.q3_youcaifeixiang_back:
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = null;
		pd = new ProgressDialog(this);
		pd.setMessage("加载中...");

		switch (position) {
		case 0:
			intent = new Intent(this, q3_youcai_tuijian.class);
			startActivity(intent);
			pd.show();
			break;
		case 1:
			intent = new Intent(this, q3_youcai_remen.class);
			startActivity(intent);
			pd.show();
			break;
		case 2:
			intent = new Intent(this, q3_youcai_haoyou.class);
			SharedPreferences editor = getSharedPreferences(
					MainTabActivity.USER_INFO, MODE_WORLD_READABLE);
			String name = editor.getString("username", "-1");
			if (("-1" != (name)) && (name.length() != 0)) {
				intent.putExtra("name", name);
				startActivity(intent);
			} else {
				// 未登录请先登录
				intent = new Intent(this, q3_login_activity.class);
				startActivity(intent);
			}
			break;
		case 3:
			intent = new Intent(this, q3_youcai_paihang.class);
			startActivity(intent);
			pd.show();
			break;
		case 4:
			intent = new Intent(this, q3_youcai_fenlei.class);
			startActivity(intent);
			break;
		case 5:
			intent = new Intent(this, q3_youcai_fenxiang.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

}

class YouCai_Adapter extends BaseAdapter {
	private Context mContext;
	private int mPic[];
	private String mDescribe[];

	public YouCai_Adapter(Context context, int i[], String str[]) {
		this.mContext = context;
		if (i == null || i.length == 0) {
			mPic = new int[] {};
		} else {
			this.mPic = i;
		}
		if (str == null || str.length == 0) {
			mDescribe = new String[] {};
		} else {
			this.mDescribe = str;
		}

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mPic.length;
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
					R.layout.q3_youcai_item, null);
			getView.mImageView = (ImageView) convertView
					.findViewById(R.id.q3_youcai_item_image);
			getView.mTextView = (TextView) convertView
					.findViewById(R.id.q3_youcai_item_text);
			convertView.setTag(getView);
		} else {
			getView = (ViewHolder) convertView.getTag();
		}
		getView.mImageView.setImageResource(mPic[position]);
		if (mDescribe.length == 0) {
			getView.mTextView.setVisibility(View.INVISIBLE);
		} else {
			getView.mTextView.setText(mDescribe[position]);
		}
		return convertView;
	}

	class ViewHolder {
		ImageView mImageView;
		TextView mTextView;
	}
}