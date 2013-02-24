package com.xhm.q3.view;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.sax.StartElementListener;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.photoalbum.R;
import com.androids.photoalbum.tab.ui.MessageActivity;
import com.androids.photoalbum.view.MainTabActivity;
import com.xhm.myVideoView.MyVideoView;
import com.xhm.q3.GetVideo_info.AsynImageLoader;
import com.xhm.q3.GetVideo_info.q3_GetVideo_Info;

public class q3_youcai_tuijian extends Activity implements OnClickListener {
	private ImageView mBack;
	private ListView mListView;
	private ArrayList<q3_Video_Info> mQ3_Video_Infos;
	private Button mButton_jinri, mButton_haoyou;
	public static File mFlie;
	public static String mDeclear;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.q3_youcai_tuijian);
		initVar();
		initView();
	}

	private void initView() {
		mBack = (ImageView) findViewById(R.id.q3_youcaifeixiang_back);
		mBack.setOnClickListener(this);
		mButton_jinri = (Button) findViewById(R.id.q3_youcai_tuijian_jinrituijian);
		mButton_jinri.setOnClickListener(this);
		mButton_haoyou = (Button) findViewById(R.id.q3_youcai_tuijian_haoyoutuijian);
		mButton_haoyou.setOnClickListener(this);
		mListView = (ListView) findViewById(R.id.q3_youcai_tuijian_list);
		TuiJianAdapter adapter = new TuiJianAdapter(this, mQ3_Video_Infos);
		mListView.setAdapter(adapter);
	}

	private void initVar() {
		mQ3_Video_Infos = q3_GetVideo_Info.getVideoInfo(null, "15", null,
				"NewId()", null, null);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.q3_youcaifeixiang_back:
			finish();
			break;
		case R.id.q3_youcai_tuijian_jinrituijian:
			mButton_jinri
					.setBackgroundResource(R.drawable.q3_youcai_tuijian_jinri_focus);
			mButton_haoyou
					.setBackgroundResource(R.drawable.q3_youcai_tuijian_haoyou);
			break;
		case R.id.q3_youcai_tuijian_haoyoutuijian:
			mButton_jinri
					.setBackgroundResource(R.drawable.q3_youcai_tuijian_jinri);
			mButton_haoyou
					.setBackgroundResource(R.drawable.q3_youcai_tuijian_haoyou_focus);
			break;
		default:
			break;
		}
	}
}

class TuiJianAdapter extends BaseAdapter {
	class ViewHolder {
		int mItem_positon;
		ImageView mImageView;
		TextView mName, mDianji, mKeep, mShow, mTime, mSize;
		Button mButton_Show, mButton_To_Haoyou, mButton_Keep, mButton_To_Weibo;
	}

	public ViewHolder getViewHolder(View v) {
		if (v.getTag() == null) {
			return getViewHolder((View) v.getParent());
		}
		return (ViewHolder) v.getTag();
	}

	class lvButtonListener implements OnClickListener {
		private int position;

		lvButtonListener(int pos) {
			position = pos;
		}

		@Override
		public void onClick(View v) {

			ViewHolder holder = getViewHolder(v);
			position = holder.mItem_positon;// 此处获得所点击button在list中的位置即：position
			Intent intent = null;
			// 获得用户的信息
			SharedPreferences editor = mContext.getSharedPreferences(
					MainTabActivity.USER_INFO, mContext.MODE_WORLD_READABLE);
			String name = editor.getString("username", "-1");
			switch (v.getId()) {
			case R.id.q3_youcai_tuijian_button_show:
				// 增加视频播放次数
				q3_GetVideo_Info.Videoplaynum(mQ3_Video_Infos.get(position)
						.getmId(), null);
				intent = new Intent(mContext, MyVideoView.class);
				intent.putExtra("video_patch", mQ3_Video_Infos.get(position)
						.getmPath());
				mContext.startActivity(intent);
				break;
			case R.id.q3_youcai_tuijian_button_keep:
				System.out.println("keep");
				if (("-1" != (name)) && (name.length() != 0)) {
					System.out.println("name===" + name + "____" + "id==="
							+ mQ3_Video_Infos.get(position).getmId());
					String resuet = q3_GetVideo_Info.Videocollectnum(
							mQ3_Video_Infos.get(position).getmId(), name);
					if (resuet.contains("收藏次数增加成功")) {
						Toast.makeText(mContext, "收藏成功！", Toast.LENGTH_LONG)
								.show();
					} else {
						Toast.makeText(mContext, "收藏失败！", Toast.LENGTH_LONG)
								.show();
					}

				} else {
					// 未登录请先登录
					intent = new Intent(mContext, q3_login_activity.class);
					mContext.startActivity(intent);
				}
				break;
			case R.id.q3_youcai_tuijian_button_to_haoyou:
				System.out.println("haoyou");
				q3_GetVideo_Info.VideoShare(mQ3_Video_Infos.get(position)
						.getmId(), name, null);
				intent = new Intent(mContext,
						q3_youcai_item_feixiangtohaoyou.class);
				Bundle mBundle = new Bundle();
				mBundle.putSerializable("video_info",
						mQ3_Video_Infos.get(position));
				intent.putExtras(mBundle);
				mContext.startActivity(intent);
				break;
			case R.id.q3_youcai_tuijian_button_to_weibo:
				// 获得视频的地址，和图片地址
				q3_youcai_tuijian.mFlie = new File(
						mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
						mQ3_Video_Infos.get(position).getmPic_Path()
								.replace("http://www.uuunm.com/video/", ""));
				q3_youcai_tuijian.mDeclear = mQ3_Video_Infos.get(position)
						.getmPath();

				// 增加分享的次数
				if (("-1" != (name)) && (name.length() != 0)) {
					System.out.println("fenxiang ==="+name);
					q3_GetVideo_Info.VideoShare(mQ3_Video_Infos.get(position)
							.getmId(), name, null);
					intent = new Intent(Intent.ACTION_SEND);
					intent.putExtra(Intent.EXTRA_STREAM,
							mQ3_Video_Infos.get(position).getmPath());
					mContext.startActivity(Intent.createChooser(intent,
							"选择分享方式"));
				} else {
					// 未登录请先登录
					intent = new Intent(mContext, q3_login_activity.class);
					mContext.startActivity(intent);
				}
				break;
			default:
				break;
			}
		}
	}

	private Context mContext;
	private ArrayList<q3_Video_Info> mQ3_Video_Infos;
	private ViewHolder holder = null;
	private q3_Video_Info info = null;

	public TuiJianAdapter(Context c, ArrayList<q3_Video_Info> q3_Video_Infos) {
		this.mContext = c;
		if (q3_Video_Infos == null) {
			mQ3_Video_Infos = new ArrayList<q3_Video_Info>();
		} else {
			this.mQ3_Video_Infos = q3_Video_Infos;
		}
	}

	public void notifyDataChanged(ArrayList<q3_Video_Info> q3_Video_Infos) {
		if (q3_Video_Infos == null) {
			mQ3_Video_Infos = new ArrayList<q3_Video_Info>();
		} else {
			this.mQ3_Video_Infos = q3_Video_Infos;
		}
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mQ3_Video_Infos.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mQ3_Video_Infos.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.q3_youcai_tuijian_item, null);
			holder.mImageView = (ImageView) convertView
					.findViewById(R.id.q3_youcai_tuijian_item_pic);
			holder.mName = (TextView) convertView
					.findViewById(R.id.q3_youcai_tuijian_item_name);
			holder.mDianji = (TextView) convertView
					.findViewById(R.id.q3_youcai_tuijian_item_dianji);
			holder.mKeep = (TextView) convertView
					.findViewById(R.id.q3_youcai_tuijian_item_keep);
			holder.mShow = (TextView) convertView
					.findViewById(R.id.q3_youcai_tuijian_item_show);
			holder.mTime = (TextView) convertView
					.findViewById(R.id.q3_youcai_tuijian_item_time);
			holder.mSize = (TextView) convertView
					.findViewById(R.id.q3_youcai_tuijian_item_size);
			holder.mButton_Show = (Button) convertView
					.findViewById(R.id.q3_youcai_tuijian_button_show);
			holder.mButton_Show.setOnClickListener(new lvButtonListener(
					position));
			holder.mButton_To_Haoyou = (Button) convertView
					.findViewById(R.id.q3_youcai_tuijian_button_to_haoyou);
			holder.mButton_To_Haoyou.setOnClickListener(new lvButtonListener(
					position));
			holder.mButton_Keep = (Button) convertView
					.findViewById(R.id.q3_youcai_tuijian_button_keep);
			holder.mButton_Keep.setOnClickListener(new lvButtonListener(
					position));
			holder.mButton_To_Weibo = (Button) convertView
					.findViewById(R.id.q3_youcai_tuijian_button_to_weibo);
			holder.mButton_To_Weibo.setOnClickListener(new lvButtonListener(
					position));
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		info = mQ3_Video_Infos.get(position);
		holder.mName.setText(info.getmName());
		holder.mItem_positon = position;
		holder.mDianji.setText(Html.fromHtml("播放：<font color=red>"
				+ info.getmShow() + "</font> 次"));
		holder.mKeep.setText(Html.fromHtml("收藏：<font color=red>"
				+ info.getmKeep() + "</font> 次"));
		holder.mShow.setText(Html.fromHtml("分享：<font color=red>"
				+ info.getmShare() + "</font> 次"));
		holder.mTime.setText(Html.fromHtml("时长：<font color=red>"
				+ info.getmTime() + "</font> 分"));
		holder.mSize.setText(Html.fromHtml("大小：<font color=red>"
				+ info.getmSize() + "</font> M"));
		AsynImageLoader loader = new AsynImageLoader(mContext);
		loader.showImageAsyn(holder.mImageView,
				info.getmPath().replace("mp4", "jpg"), R.drawable.icon);
		return convertView;
	}

}