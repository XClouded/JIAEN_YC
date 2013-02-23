package com.itcast.logic;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

public abstract class WeiboActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		MainService.addActivity(this);
	}
    public abstract void init();
    public abstract void refresh(Object ... param);
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		MainService.removeActivity(this);
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		init();
	}
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		// TODO Auto-generated method stub
//		if(keyCode==KeyEvent.KEYCODE_BACK)
//		{
//			AlertDialog.Builder  ab=new AlertDialog.Builder(this);
//			ab.setTitle("�˳���ʾ");
//			ab.setMessage("�����Ҫ�˳���?");
//			ab.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					// TODO Auto-generated method stub
//			       Intent it=new Intent(WeiboActivity.this,MainService.class);
//			       WeiboActivity.this.stopService(it);
//				   finish();
//			       android.os.Process.killProcess(android.os.Process.myPid());
//				}
//			});
//			ab.setNegativeButton("ȡ��",null);
//			ab.create().show();
//			
//			return true;
//		}
//		return super.onKeyDown(keyCode, event);
//	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
//		// TODO Auto-generated method stub
//		menu.add(1, 1, 1, "����").setIcon(R.drawable.setting);
//		menu.add(1, 2, 2, "�˺�");
//		menu.add(1, 3, 3, "�ٷ�΢��");
//		menu.add(2, 4, 4, "���!").setIcon(R.drawable.menu_contact);
//		menu.add(2, 5, 5, "����").setIcon(R.drawable.aboutweibo);
//		menu.add(2, 6, 6, "�˳�").setIcon(R.drawable.menu_exit);
//		
//		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "��ѡ����"+item.getItemId(), 500).show();
		switch(item.getItemId())
		{case 1://����
		 case 2://�˺�
		 case 3://�ٷ�΢��
		 case 4://���!
		 case 5://����
		 case 6://�˳� 
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	

}
