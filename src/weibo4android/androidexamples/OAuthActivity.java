package weibo4android.androidexamples;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import weibo4android.Paging;
import weibo4android.Status;
import weibo4android.Weibo;
import weibo4android.WeiboException;
import weibo4android.examples.Upload;
import weibo4android.http.AccessToken;
import weibo4android.http.RequestToken;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.photoalbum.R;
import com.androids.photoalbum.tab.ui.BaseActivity;
import com.androids.photoalbum.tab.ui.MessageActivity;
import com.androids.photoalbum.utils.Utils;

public class OAuthActivity extends BaseActivity implements OnClickListener {
	private String imagePath;
	private TextView mNotify;
	
    private EditText mBlogEdit;
    private ImageButton  btBack;
    private Button  btSend;
    
    private Bitmap mBitmap;
    private byte picdat[];//ͼƬ�ֽ���
    private ImageView ivpic;
    
    private void putNameText() {
        int i = mBlogEdit.getSelectionStart();
        int j = mBlogEdit.getSelectionEnd();
        String str1 = mBlogEdit.getText().toString();
        if ((i == -1) || (j == -1) || (i > j))
        {
            mBlogEdit.append("@请输入用户名 ");
          int k = mBlogEdit.getText().toString().length();
          int m = "@请输入用户名 ".length();
          int n = k - m + 1;
          int i1 = k - 1;
          mBlogEdit.setSelection(n, i1);
        }
          StringBuilder strBuilder = new StringBuilder();
          String str2 = str1.substring(0, i);
          strBuilder.append(str2);
          strBuilder.append("@请输入用户名 ");
          String str3 = str1.substring(j);
          strBuilder.append(str3);
          String str4 = strBuilder.toString();
          mBlogEdit.setText(str4);
          Editable localEditable = mBlogEdit.getText();
          int i2 = i + 1;
          int i3 = "@请输入用户名 ".length() + i - 1;
          Selection.setSelection(localEditable, i2, i3);
    }
    
    private void putTopicText() {

        int i = mBlogEdit.getSelectionStart();
        int j = mBlogEdit.getSelectionEnd();
        String str1 = mBlogEdit.getText().toString();
        if ((i == -1) || (j == -1) || (i > j))
        {
            mBlogEdit.append("#请插入话题名称#");
          int k = mBlogEdit.getText().toString().length();
          int m = "#请插入话题名称#".length();
          int n = k - m + 1;
          int i1 = k - 1;
          mBlogEdit.setSelection(n, i1);
        }

        StringBuilder strBuilder = new StringBuilder();
        String str2 = str1.substring(0, i);
        strBuilder.append(str2);
        strBuilder.append("#请插入话题名称#");
        String str3 = str1.substring(j);
        strBuilder.append(str3);
        String str4 = strBuilder.toString();
        mBlogEdit.setText(str4);
        Editable localEditable = mBlogEdit.getText();
        int i2 = i + 1;
        int i3 = "#请插入话题名称#".length() + i - 1;
        Selection.setSelection(localEditable, i2, i3);
    }
    
    private void getPicture() {
        Log.d("zheng", "getPicture");
		Runnable run = new Runnable() {
			
			@Override
			public void run() {
				try {
				    Log.d("zheng", "in run");
					picdat = Upload.readFileImage(MessageActivity.imagePath);
					Bitmap bitmap = BitmapFactory.decodeByteArray(picdat, 0, picdat.length);
					
					if (mBitmap != null && !mBitmap.isRecycled()) {
						mBitmap.recycle();
					}
					mBitmap = Utils.getResizedBitmap(bitmap, getResources().getDimensionPixelSize(R.dimen.blog_width),
							getResources().getDimensionPixelSize(R.dimen.blog_height));
					
					ByteArrayOutputStream bos=new ByteArrayOutputStream();
					mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
					picdat=bos.toByteArray();
					
					if (bitmap != null && !bitmap.isRecycled()) {
						bitmap.recycle();
					}
					Log.d("zheng", "begin action");
					Runnable action = new Runnable() {
						
						@Override
						public void run() {
						    Log.d("zheng", "in action");
							ivpic.setImageBitmap(mBitmap);
							ivpic.setVisibility(View.VISIBLE);
							
						   btSend.setOnClickListener(new OnClickListener() {
						     	@Override
								public void onClick(View v) {
						    		showProgessBarDialog("Loading");
						    		shareToWeibo();
						     	}
						   
					   		}
						   );
						}
					};
					
					runOnUiThread(action);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		
		new Thread(run).start();
    }
    
	private void init() {
		ImageButton mAt = (ImageButton)findViewById(R.id.at);
		mAt.setOnClickListener(this);
		mAt.setOnClickListener(this);
		
		ImageButton mTopic = (ImageButton)findViewById(R.id.topic);
		mTopic.setOnClickListener(this);
		mTopic.setOnTouchListener(this);
		
//		imagePath = getIntent().getStringExtra("imagePath");
		ivpic=(ImageView)this.findViewById(R.id.btGallery);
		ivpic.setOnClickListener(new OnClickListener()
		{
         	@Override
			public void onClick(View v) {
//         		BaseActivity.getGalleryPicture(OAuthActivity.this);
			}
		}
		);
		
		View title=this.findViewById(R.id.title);
		btBack=(ImageButton)findViewById(R.id.title_bt_left);
//	   btBack.setBackgroundResource(R.drawable.title_back);
//	   btBack.setText("����");
	   btBack.setOnClickListener(new OnClickListener() {
		       	@Override
				public void onClick(View v) {
					finish();
				}
	   		}
	   );
	   mBlogEdit=(EditText)this.findViewById(R.id.etBlog);
	   String subject = MessageActivity.getWeiboSubject();
	   if (subject == null || subject.length() == 0) {
		   mBlogEdit.setText("#彩信Android版# " + MessageActivity.getWeiboBody());
	   } else {
		   mBlogEdit.setText(MessageActivity.getWeiboBody());
	   }
	   btSend=(Button)findViewById(R.id.title_bt_right);
//	   btSend.setBackgroundResource(R.drawable.title_new);
//	   btSend.setText("����");
	   //��������
	  final TextView tvlabel=(TextView)this.findViewById(R.id.tvLabel);
	   tvlabel.setText(mBlogEdit.length() + "/140");
	   //
	   mBlogEdit.addTextChangedListener(new TextWatcher()
	   {
     	@Override
		public void afterTextChanged(Editable s) {
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
            tvlabel.setText(mBlogEdit.length() + "/140");
		}
	   }
	   );
	   
	   getPicture();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ivpic.setImageBitmap(bmp);
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setView(R.layout.timeline);
		setContentView(R.layout.sina_edit_layout);
		setTitleBar(com.androids.photoalbum.view.BaseLayout.TYPE_NORMAL, "", "", "返回");
		Intent intent = getIntent();
		if (intent != null) {
		    imagePath = intent.getStringExtra("imagePath");
		    Log.d("zheng", "imagePath: "+imagePath);
		}
		
		init();
//		mNotify = (TextView)findViewById(R.id.notify);

		if (true) {
		    return;
		}
		
		Uri uri=this.getIntent().getData();
		try {
			RequestToken requestToken= OAuthConstant.getInstance().getRequestToken();
			AccessToken accessToken=requestToken.getAccessToken(uri.getQueryParameter("oauth_verifier"));
//			OAuthConstant.getInstance().setAccessToken(accessToken);
			TextView textView = (TextView) findViewById(R.id.TextView01);
			textView.setText("得到AccessToken的key和Secret,可以使用这两个参数进行授权登录了.\n Access token:\n"+accessToken.getToken()+"\n Access token secret:\n"+accessToken.getTokenSecret());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
		Button button=  (Button) findViewById(R.id.Button01);
		button.setText("某一话题下的微博");
		button.setOnClickListener(new Button.OnClickListener()
        {

            public void onClick( View v )
            {
    				Weibo weibo=OAuthConstant.getInstance().getWeibo();
    				weibo.setToken(OAuthConstant.getInstance().getToken(), OAuthConstant.getInstance().getTokenSecret());
    				List<Status> friendsTimeline;
    					try {
							friendsTimeline = weibo.getTrendStatus("seaeast", new Paging(1,20));
							StringBuilder stringBuilder = new StringBuilder("1");
	    					for (Status status : friendsTimeline) {
	    						stringBuilder.append(status.getUser().getScreenName() + "说:"
	    								+ status.getText() + "-------------------------\n");
	    					}
	    					TextView textView = (TextView) findViewById(R.id.TextView01);
	    					textView.setText(stringBuilder.toString());
						} catch (WeiboException e) {
							e.printStackTrace();
						}
    					
            }
        } );
//		
	}
	private void shareToWeibo() {
	    Runnable run = new Runnable() {
            
            public void run() {
                Uri uri = OAuthActivity.this.getIntent().getData();
                try {
//                    RequestToken requestToken= OAuthConstant.getInstance().getRequestToken();
//                    AccessToken accessToken=requestToken.getAccessToken(uri.getQueryParameter("oauth_verifier"));
                    
                    
//                    OAuthConstant.getInstance().setAccessToken(MainService.mAccessToken);
                    // upload weibo here
                    OAuthConstant constant = OAuthConstant.getInstance();
                    String weiboBody = mBlogEdit.getEditableText().toString();

                    String[] params = new String[]{constant.getToken(), constant.getTokenSecret(), MessageActivity.imagePath, weiboBody == null ? "" : weiboBody/*MessageActivity.getWeiboBody()*/};
                    Upload upload = new Upload();
                    final boolean success = upload.UploadWeiBo(OAuthActivity.this, params);
                    Runnable action = new Runnable() {
                    	
	                    public void run() {
		                    if (success) {
	                    		Toast.makeText(OAuthActivity.this, "成功上传微博", Toast.LENGTH_LONG).show();
//	                    		mNotify.setText("上传成功, 您可以登录微博查看.");
		                    } else {
                    			Toast.makeText(OAuthActivity.this, "发生网络异常，上传微博失败", Toast.LENGTH_LONG).show();
//                    			mNotify.setText("sorry, 上传失败.");
		                    }
		                    
		                    if (pd != null) {
		                    	pd.dismiss();
		                    }
	                    }
                	};
                    runOnUiThread(action);
                    
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        
        new Thread(run).start();
	}

	@Override
	protected void handleTitleBarEvent(int paramInt) {
		finish();
	}
	
	public void showProgessBarDialog(final String msg) {
		Runnable action = new Runnable() {
			
			public void run() {
			    if(pd==null) {
			    	pd=new ProgressDialog(OAuthActivity.this);
//			    	pd=new ProgressDialog(getApplicationContext());
//			    	pd.setTitle("加载图片");
			    }
			    pd.setMessage(" 微博上传中... ");
			    pd.show();	
			}
		};

	    
	    runOnUiThread(action);
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.at:
				putNameText();
				break;
			case R.id.topic:
				putTopicText();
				break;
			default:
				super.onClick(view);
				break;
		}
	}
}
