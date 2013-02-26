package com.xhm.q3.view;

import com.android.photoalbum.R;
import com.xhm.q3.GetVideo_info.q3_GetVideo_Info;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;

public class q3_youcai_genggaihaoyouxinxi extends q3_haoyou_add_haoyou {
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		Intent intent = null;
		switch (v.getId()) {
		case R.id.q3_haoyou_fanhuitohaoyou:
			finish();
			break;
		case R.id.q3_haoyou_wancheng:
			if ((mName.getText().toString() == null || mName.getText()
					.toString().length() == 0)
					&& ((mPhone.getText().toString() == null) || (mPhone
							.getText().toString().length() == 0))) {
				Toast.makeText(this, "请输入用户名和号码！", Toast.LENGTH_LONG).show();

			} else if (mName.getText().toString() == null
					|| mName.getText().toString().length() == 0) {
				Toast.makeText(this, "请输入用户名！", Toast.LENGTH_LONG).show();

			} else if (mPhone.getText().toString() == null
					|| mPhone.getText().toString().length() == 0) {
				Toast.makeText(this, "请输入号码！", Toast.LENGTH_LONG).show();

			} else if (mPhone.getText().toString().length() == 11
					&& mPhone.getText().toString().substring(0, 1).equals("1")) {
				String result = q3_GetVideo_Info.alterfriendsinfo(mID, mName
						.getText().toString(), mPhone.getText().toString());
				if (result.contains("成功")) {
					Toast.makeText(this, "修改成功！", Toast.LENGTH_LONG).show();
					finish();
				} else {
					Toast.makeText(this, "修改失败！", Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(this, "请输入的号码有误，请重新输入！", Toast.LENGTH_LONG)
						.show();
				break;
			}
		default:
			break;
		}
	}
}
