package com.androids.photoalbum.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class BitMapHelper {
	
	/**
	 * Load bitmap from file, and covert the size to fit the dest rectangle
	 * @param path      The filename to open
	 * @param maxWidth  The destination rectangle width
	 * @param maxHeight The destination rectangle height 
	 * @return The new bitmap generated, null if meet some error
	 */
	static public Bitmap loadBitmapToFitRect(String path, int maxWidth, int maxHeight){
		Bitmap bitmap = null;
		
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bounds);
        if (bounds.outWidth == -1) { return null; }
        
        int width = bounds.outWidth;
        int height = bounds.outHeight;
        boolean withinBounds = width <= maxWidth && height <= maxHeight;
        if (!withinBounds) {
            float sampleSizeF = Math.max((float)width/(float)maxWidth,
					(float)height/(float)maxHeight);
		
            int sampleSize = Math.round(sampleSizeF);
            BitmapFactory.Options resample = new BitmapFactory.Options();
            resample.inSampleSize = sampleSize;
            resample.inPreferredConfig = Bitmap.Config.RGB_565;
            bitmap = BitmapFactory.decodeFile(path, resample);
        }else{
        	bitmap = BitmapFactory.decodeFile(path, null);
        }
        return bitmap;
	}
	
	/**
	 * rotate the bitmap to some angle
	 * @param oldBitmap The old Bitmap
	 * @param angle     The angle wanted to rotate
	 * @return The new Bitmap
	 */
	static public Bitmap rotateBitmap(Bitmap oldBitmap, int angle){
		Matrix vMatrix = new Matrix();
		vMatrix.setRotate(angle);
		Bitmap newBitmap = Bitmap.createBitmap(oldBitmap, 0, 0, oldBitmap.getWidth(),
				oldBitmap.getHeight(), vMatrix, true);
		return newBitmap;
	}
	
}
