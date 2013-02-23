package com.renren.android.example;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import android.app.Activity;
import android.content.Context;

import com.androids.photoalbum.tab.ui.MessageActivity;
import com.renren.api.connect.android.Renren;
import com.xhm.q3.view.q3_youcai_tuijian;

/**
 * 封装renren_android_sdk中对照片、相册API的调用
 * 
 * @author Sun Ting (ting.sun@renren-inc.com)
 */
public class PhotoDemo {

	/**
	 * 调用系统界面上传照片<br>
	 * albumResponse 注意：用户需自己准备上传的文件(File对象)
	 * 
	 * @param activity
	 * @param renren
	 */
	public static void uploadPhotoWithActivity(final Activity activity,
			final Renren renren) {
		// 读取assets文件夹下的图片，保存在手机中
		String fileName = "renren.png";
		// 获取文件后缀，构造本地文件名
		int index = fileName.lastIndexOf('.');
		// 文件保存在/sdcard目录下，以renren_前缀加系统毫秒数构造文件名
		final String realName = "renren_" + System.currentTimeMillis()
				+ fileName.substring(index, fileName.length());
		try {
			InputStream is = activity.getResources().getAssets().open(fileName);
			BufferedOutputStream bos = new BufferedOutputStream(
					activity.openFileOutput(realName, Context.MODE_PRIVATE));
			int length = 0;
			byte[] buffer = new byte[1024];
			while ((length = is.read(buffer)) != -1) {
				bos.write(buffer, 0, length);
			}
			is.close();
			bos.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String filePath = activity.getFilesDir().getAbsolutePath() + "/"
				+ realName;
		// 下面调用SDK接口
		renren.publishPhoto(activity, q3_youcai_tuijian.mFlie,
				q3_youcai_tuijian.mDeclear);
	}

}
