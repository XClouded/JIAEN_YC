package com.itcast.db;

import com.itcast.net.http.AccessToken;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class TokenUtil {
//����AccessToken
public static void saveAccessToken(Context con,AccessToken at){
    if (con == null) {
        Log.d("zheng", "con is null");
    }
	SharedPreferences sp=con.getSharedPreferences("token", Context.MODE_WORLD_WRITEABLE);
	sp.edit().putString("tk", at.getToken())
	    .putString("tks", at.getTokenSecret())
	    .commit();
}
//��ȡAccessToken
public static AccessToken readAccessToken(Context con){
	SharedPreferences sp=con.getSharedPreferences("token", Context.MODE_WORLD_READABLE);
	String tk=sp.getString("tk", null);
	String tks=sp.getString("tks", null);
    
	if(tk!=null)
    {AccessToken at=new AccessToken(tk,tks);
     return at;	
    }
	return null;
 }
}
