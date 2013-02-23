package com.androids.photoalbum.tab.ui;

import java.util.HashMap;

import weibo4android.androidexamples.OAuthActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.photoalbum.R;
import com.androids.photoalbum.view.MainTabActivity;
import com.itcast.db.TokenUtil;
import com.itcast.logic.MainService;
import com.itcast.logic.Task;
import com.itcast.logic.TaskType;
import com.itcast.logic.WeiboActivity;
import com.itcast.net.http.AccessToken;
import com.itcast.util.NetUtil;
import com.itcast.weibo.sina.User;

public class SinaLoginActivity extends WeiboActivity{
    public ProgressDialog pd;
    public AutoCompleteTextView  etUser;
    public EditText etPass;
    public Button btLogin; 
     
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.sina_login_layout);
		ImageButton exit = (ImageButton)findViewById(R.id.exit);
		exit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
	    etUser=(AutoCompleteTextView)this.findViewById(R.id.user);  
	    etPass=(EditText)this.findViewById(R.id.password);
	    btLogin=(Button)this.findViewById(R.id.loginButton);
	    btLogin.setOnClickListener(new OnClickListener()
	    {
		   @Override
			public void onClick(View v) {
		       HashMap hm=new HashMap();
		       hm.put("us", etUser.getText().toString());
		       hm.put("ps", etPass.getText().toString());
		       
			   Task ts=new Task(TaskType.TS_USER_LOGIN,hm);
			   MainService.newTask(ts);
			   if(pd==null)
			   {
				   pd=new ProgressDialog(SinaLoginActivity.this);
				   pd.setTitle("登录中");
			   }
			   pd.setMessage("请稍候....");
			   pd.show();
			}
	    }
	    );
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void init() {
	    if(!NetUtil.checkNet(this))//û������
	    {
	    	MainService.alertNetError(this);
	    	
	    }else
	    {//��ϵͳ����
	    if(!MainService.isrun)
		{
//			Intent it=new Intent(this,MainService.class);
//			this.startService(it);
	    	
	    	Runnable run = new MainService();
	    	new Thread(run).start();
		}
	    // �Ƿ��¼��
		AccessToken at=TokenUtil.readAccessToken(this);
		if(at!=null)
		{
			MainService.weibo.setToken(at.getToken(), at.getTokenSecret());
			//��ӵ�¼����
			Task ts=new Task(TaskType.TS_USER_LOGIN,null);
		    MainService.newTask(ts);
		    //��ʾ�Զ���¼�����
		    if(pd==null)
		    {
		    	pd=new ProgressDialog(this);
		    	pd.setTitle("��¼");
		    }
		    pd.setMessage("�����Զ���¼,���Ժ�...");
		    pd.show();
		}else{
			
		}
	    }
	}

	@Override
	public void refresh(final Object... param) {
		
		pd.cancel();
		saveLoginInfo(etUser.getText().toString(), etPass.getText().toString());
		Log.d("peter", "login success!");
		Intent it=new Intent(SinaLoginActivity.this, OAuthActivity.class);
		SinaLoginActivity.this.startActivity(it);
		finish();
		if (true) {
			return;
		}
		// TODO Auto-generated method stub
        
		Runnable run = new Runnable() {
			public void run() {
				User u=(User)param[1];
				if(u!=null)
				{   pd.cancel();
					saveLoginInfo(etUser.getText().toString(), etPass.getText().toString());
					Log.d("peter", "login success!");
					Toast.makeText(SinaLoginActivity.this, u.getName() + " 登录成功!", 1000).show();
					Intent it=new Intent(SinaLoginActivity.this,NewWeiboActivity.class);
					SinaLoginActivity.this.startActivity(it);
					finish();
				} else {
					pd.cancel();
					Toast.makeText(SinaLoginActivity.this, "登录失败! 请检查用户名和密码.", 1000).show();
				}
			}
		};
		
		runOnUiThread(run);
	}

	private void saveLoginInfo(final String name, final String password) {
		Runnable run = new Runnable() {
			
			@Override
			public void run() {
			    Log.d("zheng", "saveLoginInfo");
		        SharedPreferences.Editor editor = getSharedPreferences(MainTabActivity.USER_INFO, MODE_WORLD_READABLE).edit();
		        User.mUserName = name;
		        User.mPassWord = password;
		        editor.putString("sinausername", User.mUserName);
		        editor.putString("sinapassword", User.mPassWord);
		        editor.commit();
			}
		};

		new Thread(run).start();
	}
}
