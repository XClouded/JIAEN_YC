package qq.gdky005;

import java.io.File;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.photoalbum.R;
import com.androids.photoalbum.tab.ui.MessageActivity;
import com.tencent.weibo.api.TAPI;
import com.tencent.weibo.api.UserAPI;
import com.tencent.weibo.constants.OAuthConstants;
import com.tencent.weibo.oauthv2.OAuthV2;
import com.tencent.weibo.webview.OAuthV2AuthorizeWebView;
import com.xhm.q3.view.q3_youcai_tuijian;

public class WebViewActivity extends Activity implements OnClickListener {
	// 文字内容
	EditText qqContent;
	// 关闭
	Button close;
	// 发送分享
	Button send;
	LinearLayout ll_text_limit_unit;
	TextView tv_text_limit;
	FrameLayout mPiclayout;
	private TAPI mTAPI;
	private OAuthV2 mOAuth;
	private static final String REDIRECTURI = "http://t.qq.com/daizhexiangjilv";
	private static final String APPKEY = "801216434";
	private static final String APPSECRET = "6709d39e313d41dbd4073e9594662814";
	private static final int REQUESTCODE = 0001;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editcontent);
		initObject();
		findViews();
		qqContent.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				// 这些是对文本进行监听，如果有变化就对把文本上的数字改动，让用户知道输入多少数字了，如果过多或者没有输入就把发送按钮变成不可点击的形式！
				String textContent = qqContent.getText().toString();
				double len = 0;
				for (int i = 0; i < textContent.length(); i++) {
					int temp = (int) textContent.charAt(i);
					if (temp > 0 && temp < 127) {
						len += 0.5;
					} else {
						len++;
					}
				}
				if (len > 0 && len <= 140) {

					len = 140 - len;
					tv_text_limit.setTextColor(Color.GRAY);
					if (!send.isEnabled()) {
						send.setEnabled(true);
					}
					tv_text_limit.setText(Math.round(len) + "/140 字");
				} else {
					if (len != 0) {
						len = len - 140;
					}

					tv_text_limit.setTextColor(Color.RED);
					if (send.isEnabled()) {
						send.setEnabled(false);
					}
					tv_text_limit.setText(Math.round(len) + "/140 字");
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void initObject() {
		mOAuth = new OAuthV2(REDIRECTURI);
		mOAuth.setClientId(APPKEY);
		mOAuth.setClientSecret(APPSECRET);
		Intent intent = new Intent(this, OAuthV2AuthorizeWebView.class);
		intent.putExtra("oauth", mOAuth);
		startActivityForResult(intent, REQUESTCODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUESTCODE) {
			if (resultCode == OAuthV2AuthorizeWebView.RESULT_CODE) {
				mOAuth = (OAuthV2) data.getExtras().getSerializable("oauth");
				try {
					if (mOAuth.getStatus() == 0) {
						Toast.makeText(this, "登陆成功", 3000).show();

					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private void findViews() {
		close = (Button) findViewById(R.id.btnClose);
		close.setOnClickListener(this);
		send = (Button) findViewById(R.id.sendweibo);
		send.setOnClickListener(this);
		qqContent = (EditText) findViewById(R.id.qqcontent);
		qqContent.setText(q3_youcai_tuijian.mDeclear);
		ll_text_limit_unit = (LinearLayout) findViewById(R.id.ll_text_limit_unit);
		tv_text_limit = (TextView) findViewById(R.id.tv_text_limit);
		mPiclayout = (FrameLayout) WebViewActivity.this
				.findViewById(R.id.flPic);
		if (TextUtils.isEmpty(q3_youcai_tuijian.mFlie.getAbsolutePath())) {
			mPiclayout.setVisibility(View.GONE);
		} else {
			mPiclayout.setVisibility(View.VISIBLE);
			File file = q3_youcai_tuijian.mFlie;
			if (file.exists()) {
				Bitmap pic = BitmapFactory.decodeFile(q3_youcai_tuijian.mFlie
						.getAbsolutePath());
				ImageView image = (ImageView) this.findViewById(R.id.ivImage);
				image.setImageBitmap(pic);
			} else {
				mPiclayout.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnClose:
			finish();
			break;
		case R.id.sendweibo:
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setMessage("发送中...");
			dialog.show();
			System.out.println("fasong");
			mTAPI = new TAPI(OAuthConstants.OAUTH_VERSION_2_A);
			String picPath = q3_youcai_tuijian.mFlie.getAbsolutePath();
			try {
				mTAPI.addPic(mOAuth, "json", qqContent.getText().toString(),
						"127.0.0.1", picPath);
				dialog.dismiss();
				Toast.makeText(this, "发送微博成功！", 1).show();
				finish();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mTAPI.shutdownConnection();
			break;
		default:
			break;
		}
	}
}
