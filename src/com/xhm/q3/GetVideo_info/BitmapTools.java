package com.xhm.q3.GetVideo_info;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.util.Log;

public class BitmapTools {
	/**
	 * �����������ȡλͼ����
	 * 
	 * @param is
	 * @return
	 */
	public static Bitmap getBitmap(InputStream is) {
		return BitmapFactory.decodeStream(is);
	}

	/**
	 * ����������� ��С���� ��ȡλͼ����
	 * 
	 * @param is
	 * @param scale
	 * @return
	 */
	public static Bitmap getBitmap(InputStream is, int scale) {
		Bitmap bitmap = null;
		Options opts = new Options();
		opts.inSampleSize = scale;
		bitmap = BitmapFactory.decodeStream(is, null, opts);
		return bitmap;
	}

	/**
	 * ���ָ���Ŀ�� �����ݺ�� ��С��ȡָ��ͼƬ
	 * 
	 * @param bytes
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap getBitmap(byte[] bytes, int width, int height) {
		Bitmap bitmap = null;
		Options opts = new Options();
		opts.inJustDecodeBounds = true;
		bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
		opts.inJustDecodeBounds = false;
		int scaleX = opts.outWidth / width;
		int scaleY = opts.outHeight / height;
		int scale = scaleX > scaleY ? scaleX : scaleY;
		opts.inSampleSize = scale;
		Log.i("info", "scale : " + scale);
		bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
		return bitmap;
	}

	/**
	 * ���·�� ���ļ��ж�ȡλͼ����
	 * 
	 * @param path
	 * @return
	 */
	public static Bitmap getbiBitmap(String path) {
		Bitmap bitmap = null;
		bitmap = BitmapFactory.decodeFile(path);
		return bitmap;
	}

	/**
	 * ����λͼ����ָ��λ��
	 * 
	 * @param path
	 * @param bitmap
	 * @throws IOException
	 */
	public static void saveBitmap(String path, Bitmap bitmap)
			throws IOException {
		if (path != null && bitmap != null) {
			File file = new File(path);
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			OutputStream stream = new FileOutputStream(file);
			String name = file.getName();
			String end = name.substring(name.lastIndexOf(".") + 1);
			if ("png".equals(end)) {
				System.out.println("png");
				bitmap.compress(CompressFormat.PNG, 100, stream);
			} else {
				System.out.println("jpeg");
				bitmap.compress(CompressFormat.JPEG, 100, stream);
			}
		}
	}
}
