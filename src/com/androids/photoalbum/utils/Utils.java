package com.androids.photoalbum.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Pattern;
//import sudroid.android.FileUtil;
//import sudroid.android.PackageManagerUtil;
import com.androids.photoalbum.netinfo.ClassificationWebSiteInfo;
import com.androids.photoalbum.netinfo.ClassificationWebSiteInfo.ProductInfo;
import com.androids.photoalbum.provider.AlbumContent.Album;
import com.androids.photoalbum.provider.AlbumContent.AlbumColumns;
import com.androids.photoalbum.tab.ui.BaseActivity;
import com.androids.photoalbum.view.MainTabActivity;
import com.ant.liao.GifView;

public final class Utils {
  public static boolean logging = false;
  public static final String AT_FILE = "_at";
  public static final String BLOG_FILE = "/blog.dat";
  static int CACHE_LIMITATION = 0;
  public static final String CAMERA_FILE_CACHE_PREX = "tmp_bmp_";
  public static final String CM_FILE = "_commentmessage";
  public static final int DELETE_FAV = 1;
  public static final int DELETE_MBLOG = 0;
  private static String DES_KEY;
  public static final String DIR_LARGE_IMAGE = "/large_imgs";
  public static final String FAVHOTWORD_FILE = "favhotword";
  public static final String FAVORITE_FILE = "_favorite";
  public static final String HOME_FILE = "_home";
  public static final String HOTFORWARD_FILE = "/hotforward";
  public static final String LOOKAROUND_FILE = "/lookaround";
  public static final String MODE_FILE = "_mode";
  public static final String MSG_FILE = "_message";
  public static final String MYBLOG_FILE = "_myblog";
  private static final int MaxCacheMapSize = 200;
  public static final String PREFS_ISFIRSTLOGIN = "isfirst";
  public static final String PREFS_NAME = "weibo";
  public static final String PREFS_PASSWORD = "password";
  public static final String PREFS_USERNAME = "username";
  private static final int RRB_DEFAULT_SIZE = 6;
  public static final String TOPUSER_FILE = "/topuser";
  public static final String TOTAL_FILE = "_total";
  public static final String UID_FILE = "_uid.dat";
  public static final String USER_FILE = "/user.dat";
  private static final String VERSION_PATTERN = "(\\d+)\\.(\\d)\\.(\\d)";
  private static Pattern atpattern;
  private static File externalCacheDir;
  private static Pattern filenamepattern;
  private static boolean isLogin = true;
  public static Object lock;
//  private static SinaWeiboDB mDB;
  public static long millisecond8hours;
  private static Paint nbPaint;
  public static Map<String, Bitmap> portraitCacheMap;
  private static Queue<String> portraitQueue;
  private static Path rrbRath;
  private static RectF rrbRectf;
  private static SimpleDateFormat sdf;
  private static Pattern sharppattern;
  private static Pattern webpattern;

  
    static {
        DES_KEY = "sinachina";
        rrbRectf = new RectF();
        rrbRath = new Path();
        nbPaint = new Paint();
        millisecond8hours = 0L;
        portraitCacheMap = new HashMap();
        portraitQueue = new LinkedList();
        lock = new Object();
    }
    
    public static void notifyToUser(final String result, final Context context) {
    	if(result == null || result.length() == 0) {
    		return;
    	}
    	
		Runnable run = new Runnable() {
			public void run() {
				Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
			}
		};
		
		((Activity)context).runOnUiThread(run);
    }
    
    public static void savePhotoIntoDatabase(ContentResolver resolver, ProductInfo productinfo) {
    	ContentValues values = new ContentValues();
    	values.put(AlbumColumns.PHOTO_ADDRESS, productinfo.photoaddress);
    	if (productinfo.photobitmap != null) {
    		final ByteArrayOutputStream os = new ByteArrayOutputStream();
    		productinfo.photobitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
    		byte[] imgByte = os.toByteArray();
    		values.put(AlbumColumns.PHOTO_BITMAP, imgByte);
    	}
        
    	if (productinfo.photobyte != null) {
    		values.put(AlbumColumns.PHOTO_BYTE, productinfo.photobyte);
    	}
    	
//    	values.put(AlbumColumns.PRODUCT_ID, productinfo.productid);
    	Cursor cursor = null;
    	String selection = AlbumColumns.PHOTO_ADDRESS + "=?";
    	try {
    		cursor = resolver.query(Album.ALBUM_CONTENT_URI, new String[]{AlbumColumns.ID}, selection,
    				new String[] { productinfo.photoaddress }, null);
    		
    		if (cursor != null && cursor.moveToFirst()) {
    			resolver.update(Album.ALBUM_CONTENT_URI, values, selection, new String[]{productinfo.photoaddress});
    		} else {
    	    	resolver.insert(Album.ALBUM_CONTENT_URI, values);
    		}
    	} finally {
    		if (cursor != null) {
    			cursor.close();
    		}
    	}
    }
    
    public static byte[] getInputStreamBytesFromDatabase(Context context, String photoaddress) {
    	Cursor cursor = null;
    	String selection = AlbumColumns.PHOTO_ADDRESS + "=?";
    	ContentResolver resolver = context.getContentResolver();
    	try {
    		cursor = resolver.query(Album.ALBUM_CONTENT_URI, Album.CONTENT_PROJECTION, selection,
    				new String[] { photoaddress }, null);
    		
    		if (cursor != null && cursor.moveToFirst()) {
                return cursor.getBlob(AlbumColumns.CONTENT_STREAM_BYTE_COLUMN);
    		}
    	} finally {
    		if (cursor != null) {
    			cursor.close();
    		}
    	}
    	
    	return null;
    }
    
    public static Bitmap getPhotoFromDatabase(Context context, String photoaddress) {
    	Cursor cursor = null;
    	String selection = AlbumColumns.PHOTO_ADDRESS + "=?";
    	ContentResolver resolver = context.getContentResolver();
    	try {
    		cursor = resolver.query(Album.ALBUM_CONTENT_URI, Album.CONTENT_PROJECTION, selection,
    				new String[] { photoaddress }, null);
    		
    		if (cursor != null && cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(AlbumColumns.CONTENT_PHOTO_BITMAP_COLUMN);
                if (data != null && data.length != 0) {
                	Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, null);
                	bitmap = Utils.getResizedBitmap(bitmap, ((BaseActivity)context).mImageWidth, ((BaseActivity)context).mImageHeigth);
                	return bitmap;
                }
    		}
    	} finally {
    		if (cursor != null) {
    			cursor.close();
    		}
    	}
    	
    	return null;
    }

    public static Bitmap getPhotoFromDatabase(Context context, ProductInfo productinfo) {
    	Cursor cursor = null;
    	String selection = AlbumColumns.PHOTO_ADDRESS + "=?";
    	ContentResolver resolver = context.getContentResolver();
    	try {
    		cursor = resolver.query(Album.ALBUM_CONTENT_URI, Album.CONTENT_PROJECTION, selection,
    				new String[] { productinfo.photoaddress }, null);
    		
    		if (cursor != null && cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(AlbumColumns.CONTENT_PHOTO_BITMAP_COLUMN);
                if (data != null && data.length != 0) {
                	Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, null);
                	bitmap = Utils.getResizedBitmap(bitmap, ((BaseActivity)context).mImageWidth, ((BaseActivity)context).mImageHeigth);
                	return bitmap;
                }
    		}
    	} finally {
    		if (cursor != null) {
    			cursor.close();
    		}
    	}
    	
    	return null;
    }
    
//    public static String buildTextColor(String text) {
//        text = "<font color=0xFF0000FF>" + text + "</font>";
//        return text;
//        
//        
//        textview=(TextView)findViewById(R.id.textview);  
//        SpannableStringBuilder style=new SpannableStringBuilder(strs);  
//        style.setSpan(new BackgroundColorSpan(Color.RED),start,end,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  
//        style.setSpan(new ForegroundColorSpan(Color.RED),7,9,Spannable.SPAN_EXCLUSIVE_INCLUSIVE);  
//        textview.setText(style);  
//    }
    
    public static Pattern getAtPattern() {
        if (atpattern == null) {
            Object[] arrayOfObject = new Object[1];
            String str = getPunctuation();
            arrayOfObject[0] = str;
            atpattern = Pattern.compile(String.format("@[[^@\\s%s]0-9]{1,20}", arrayOfObject));
        }
        return atpattern;
    }
    
    private static String getPunctuation()
    {
      return "`~!@#\\$%\\^&*()=+\\[\\]{}\\|/\\?<>,\\.:\\u00D7\\u00B7\\u2014-\\u2026\\u3001-\\u3011\\uFE30-\\uFFE5";
    }
    
    public static Bitmap getResizedBitmap(Bitmap bitmap, int sWidth, int sHeight) {
        int bmpWidth  =bitmap.getWidth();  

        int bmpHeight  =bitmap.getHeight();  

        //缩放图片的尺寸  

        float scaleWidth  = (float) sWidth/ bmpWidth;     //按固定大小缩放  sWidth 写多大就多大

        float scaleHeight = (float) sHeight / bmpHeight;  //

        Matrix matrix = new Matrix();  

        matrix.postScale(scaleWidth, scaleHeight);//产生缩放后的Bitmap对象  

        Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bmpWidth,bmpHeight, matrix, false);  

        return resizeBitmap;
    }
    
    public static byte[] streamToBytes(InputStream is) {
        ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
        byte[] buffer = new byte[1024];
        int len;
        try {
            while ((len = is.read(buffer)) >= 0) {
                os.write(buffer, 0, len);
            }
        } catch (java.io.IOException e) {
        }
        return os.toByteArray();
    }
    
    public static byte[] inputStreamToByte(InputStream is) throws IOException {  
        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();  
        int ch;  
        while ((ch = is.read()) != -1) {  
         bytestream.write(ch);  
        }  
        byte imgdata[] = bytestream.toByteArray();  
        bytestream.close();  
        return imgdata;  
   }  
    
    public static InputStream bytesToInputStream(byte[] data) {
    	InputStream is = new ByteArrayInputStream(data);
    	return is;
    }
    
    public static byte[] getByteArrayFromNetwork(String url, Context context) {
    	byte[] bytes = null;
    	ContentResolver resolver = context.getContentResolver();
        Log.i("returnBitMap", "url=" + url);
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {
            myFileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            HttpURLConnection conn = (HttpURLConnection)myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bytes = inputStreamToByte(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes;
    }
    
    // 显示网络上的图片
    public static Bitmap getBitMapFromNetwork(String url, Context context) {
    	ContentResolver resolver = context.getContentResolver();
        Log.i("returnBitMap", "url=" + url);
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {
            myFileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            HttpURLConnection conn = (HttpURLConnection)myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            byte[] bytes = inputStreamToByte(is);
            
            BitmapFactory.Options opts=new BitmapFactory.Options(); 
            opts.inDither=false;                    
            //Disable Dithering mode 
            opts.inPurgeable=true;                   
            //Tell to gc that whether it needs free memory, the Bitmap can be cleared 
            opts.inInputShareable=true;             
            //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future 
            opts.inTempStorage=new byte[32 * 1024];  
//            GifView view = new GifView(context);
//            view.setGifImage(bytes);
//            bitmap = view.showCover();
            is = bytesToInputStream(bytes);
            bitmap = BitmapFactory.decodeStream(is, null, opts);
            
            // Write photo InputStream into database
//            byte[] bytes = streamToBytes(is);

            ClassificationWebSiteInfo.ProductInfo productinfo = new ClassificationWebSiteInfo.ProductInfo();
            productinfo.photoaddress = url;
            productinfo.photobitmap = bitmap;
            productinfo.photobyte = bytes;
            
            savePhotoIntoDatabase(resolver, productinfo);
//            ContentValues values = new ContentValues();
//            values.put(AlbumColumns.PHOTO_BYTE, bytes);
//            resolver.update(Album.ALBUM_CONTENT_URI, values, AlbumColumns.PHOTO_ADDRESS + "=?", new String[]{url});
            
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }
    
    public static void saveBitmapToSdcard(Bitmap bitmap) {
		try {
			String filename = "/sdcard/优彩/优彩.jpg";
			File sdCard = new File(filename);
			FileOutputStream outStreamz = new FileOutputStream(sdCard);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStreamz);
			outStreamz.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
            bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 8;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

//    public static IBlogService getBlogServiceProxy(Context context) {
//        IBlogService ret = null;
//        //EXCHANGE-REMOVE-SECTION-START
//        ret = new BlogServiceProxy(context, BlogService.class);
//        //EXCHANGE-REMOVE-SECTION-END
////        if (ret == null) {
////            ret = NullEmailService.INSTANCE;
////        }
//        return ret;
//    }
    
/*
  public static int[] Byte2Int(byte[] paramArrayOfByte)
  {
    int i = paramArrayOfByte.length / 3;
    int j = paramArrayOfByte.length % 3;
    int[] arrayOfInt;
    int k;
    int m;
    int n;
    int i1;
    int i2;
    if (j == 0)
    {
      arrayOfInt = new int[i];
      k = 0;
      m = 255;
      n = 8;
      i1 = 0;
      if (i1 < i)
        break label63;
      if (j > 0)
        i2 = 0;
    }
    while (true)
    {
      if (i2 >= j)
      {
        return arrayOfInt;
        arrayOfInt = new int[i + 1];
        break;
        label63: int i3 = 0;
        while (true)
        {
          if (i3 >= 3)
          {
            i1 += 1;
            break;
          }
          int i4 = arrayOfInt[i1];
          int i5 = paramArrayOfByte[k];
          int i6 = n * i3;
          int i7 = i5 << i6;
          int i8 = n * i3;
          int i9 = m << i8;
          int i10 = i7 & i9;
          int i11 = i4 | i10;
          arrayOfInt[i1] = i11;
          k += 1;
          i3 += 1;
        }
      }
      int i12 = arrayOfInt[i];
      int i13 = k + i2;
      int i14 = paramArrayOfByte[i13];
      int i15 = n * i2;
      int i16 = i14 << i15;
      int i17 = n * i2;
      int i18 = m << i17;
      int i19 = i16 & i18;
      int i20 = i12 | i19;
      arrayOfInt[i] = i20;
      i2 += 1;
    }
  }

  public static int[] Encode(String paramString)
  {
    try
    {
      byte[] arrayOfByte1 = DES_KEY.getBytes();
      DESKeySpec localDESKeySpec = new DESKeySpec(arrayOfByte1);
      SecretKey localSecretKey = SecretKeyFactory.getInstance("DES").generateSecret(localDESKeySpec);
      Cipher localCipher = Cipher.getInstance("DES");
      localCipher.init(1, localSecretKey);
      byte[] arrayOfByte2 = paramString.getBytes();
      int[] arrayOfInt1 = Byte2Int(localCipher.doFinal(arrayOfByte2));
      arrayOfInt2 = arrayOfInt1;
      return arrayOfInt2;
    }
    catch (Exception localException)
    {
      while (true)
      {
        loge(localException);
        int[] arrayOfInt2 = null;
      }
    }
  }

  public static boolean SetWeiboDB(SinaWeiboDB paramSinaWeiboDB)
  {
    mDB = paramSinaWeiboDB;
    if (mDB != null);
    for (int i = 1; ; i = 0)
      return i;
  }

  public static boolean checkBlogFile(String paramString)
  {
    if (TextUtils.isEmpty(checkCache(paramString, "/blog.dat")));
    for (int i = 0; ; i = 1)
      return i;
  }

  public static String checkCache(String paramString1, String paramString2)
  {
    String str1 = String.valueOf(paramString1);
    StringBuilder localStringBuilder = new StringBuilder(str1).append("/");
    String str2 = MD5.hexdigest(paramString2);
    String str3 = str2;
    logd("——：" + str3);
    if (new File(str3).exists());
    for (String str4 = str3; ; str4 = null)
      return str4;
  }

  public static void cleanAtListFile(String paramString, User paramUser)
  {
    String str1 = String.valueOf(paramString);
    StringBuilder localStringBuilder = new StringBuilder(str1).append(47);
    String str2 = paramUser.uid;
    String str3 = str2 + "_at";
    boolean bool = new File(str3).delete();
  }

  public static void cleanBlogFile(String paramString)
  {
    String str1 = String.valueOf(paramString);
    String str2 = str1 + "/blog.dat";
    boolean bool = new File(str2).delete();
  }

  public static void cleanCameraBmpCache()
  {
    String str = FileUtil.printSDCardRoot();
    File localFile = new File(str);
    Utils.1 local1 = new Utils.1();
    File[] arrayOfFile = localFile.listFiles(local1);
    int i;
    int j;
    if (arrayOfFile != null)
    {
      i = arrayOfFile.length;
      j = 0;
    }
    while (true)
    {
      if (j >= i)
        return;
      boolean bool = FileUtil.deleteDependon(arrayOfFile[j]);
      j += 1;
    }
  }

  public static void cleanCommentMessageListFile(String paramString, User paramUser)
  {
    String str1 = String.valueOf(paramString);
    StringBuilder localStringBuilder = new StringBuilder(str1).append(47);
    String str2 = paramUser.uid;
    String str3 = str2 + "_commentmessage";
    boolean bool = new File(str3).delete();
  }

  public static void cleanFavoriteListFile(String paramString, User paramUser)
  {
    String str1 = String.valueOf(paramString);
    StringBuilder localStringBuilder = new StringBuilder(str1).append(47);
    String str2 = paramUser.uid;
    String str3 = str2 + "_favorite";
    boolean bool = new File(str3).delete();
  }

  public static void cleanHomeListFile(String paramString, User paramUser)
  {
    boolean bool = mDB.deleteAll(0);
  }

  public static void cleanHotForwardListFile(String paramString)
  {
    String str1 = String.valueOf(paramString);
    String str2 = str1 + "/hotforward";
    boolean bool = new File(str2).delete();
  }

  public static void cleanHotWordListFile(String paramString, User paramUser)
  {
    String str1 = String.valueOf(paramString);
    StringBuilder localStringBuilder = new StringBuilder(str1).append(47);
    String str2 = paramUser.uid;
    String str3 = str2 + "favhotword";
    boolean bool = new File(str3).delete();
  }

  public static void cleanLookAroundListFile(String paramString)
  {
    String str1 = String.valueOf(paramString);
    String str2 = str1 + "/lookaround";
    boolean bool = new File(str2).delete();
  }

  public static void cleanMessageListFile(String paramString, User paramUser)
  {
    String str1 = String.valueOf(paramString);
    StringBuilder localStringBuilder = new StringBuilder(str1).append(47);
    String str2 = paramUser.uid;
    String str3 = str2 + "_message";
    boolean bool = new File(str3).delete();
  }

  public static void cleanMyBlogListFile(String paramString, User paramUser)
  {
    String str1 = String.valueOf(paramString);
    StringBuilder localStringBuilder = new StringBuilder(str1).append(47);
    String str2 = paramUser.uid;
    String str3 = str2 + "_myblog";
    boolean bool = new File(str3).delete();
  }

  public static boolean cleanSearchWordList(String paramString)
  {
    return new File(paramString).delete();
  }

  public static void cleanSubMessageListFile(String paramString1, String paramString2, User paramUser)
  {
    String str1 = String.valueOf(paramString2);
    StringBuilder localStringBuilder = new StringBuilder(str1).append(47);
    String str2 = paramUser.uid;
    String str3 = str2 + 95 + paramString1;
    boolean bool = new File(str3).delete();
  }

  public static void cleanTopUserListFile(String paramString)
  {
    String str1 = String.valueOf(paramString);
    String str2 = str1 + "/topuser";
    boolean bool = new File(str2).delete();
  }

  public static void cleanUserFile(String paramString)
  {
    String str1 = String.valueOf(paramString);
    String str2 = str1 + "/user.dat";
    boolean bool = new File(str2).delete();
  }

  private static boolean cleanUsrnameList(String paramString)
  {
    return new File(paramString).delete();
  }

  public static void clearAllLargeImages(Context paramContext)
  {
    String str = String.valueOf(paramContext.getCacheDir().getAbsolutePath());
    FileUtil.deleteFiles(str + "/large_imgs");
  }

  public static void clearCache(File paramFile)
  {
    if (paramFile == null)
      return;
    File[] arrayOfFile;
    int i;
    int j;
    if (paramFile.exists())
      if (paramFile.isDirectory())
      {
        arrayOfFile = paramFile.listFiles();
        i = arrayOfFile.length;
        j = 0;
      }
    while (true)
    {
      if (j >= i)
      {
        if ((!paramFile.isFile()) || (!paramFile.exists()) || (!paramFile.canWrite()))
          break;
        boolean bool1 = paramFile.delete();
        break;
        break;
      }
      File localFile = arrayOfFile[j];
      if ((localFile.exists()) && (localFile.canWrite()))
        boolean bool2 = localFile.delete();
      j += 1;
    }
  }
*/
  /*
  public static void clearCacheButUser(File paramFile)
  {
    if (paramFile == null)
      return;
    File[] arrayOfFile;
    int i;
    int j;
    if (paramFile.exists())
      if (paramFile.isDirectory())
      {
        arrayOfFile = paramFile.listFiles();
        i = arrayOfFile.length;
        j = 0;
      }
    while (true)
    {
      if (j >= i)
      {
        if (!paramFile.isFile())
          break;
        if ((paramFile.exists()) && (paramFile.canWrite()))
        {
          String str1 = paramFile.getName();
          String str2 = "/user.dat".substring(1);
          if (!str1.equals(str2))
          {
            StringBuilder localStringBuilder1 = new StringBuilder("delete file:");
            String str3 = paramFile.getName();
            String str4 = str3;
            int k = Log.d("weibo", str4);
          }
        }
        boolean bool1 = paramFile.delete();
        break;
        break;
      }
      File localFile = arrayOfFile[j];
      if ((localFile.exists()) && (localFile.canWrite()))
      {
        String str5 = localFile.getName();
        String str6 = "/user.dat".substring(1);
        if (!str5.equals(str6))
        {
          StringBuilder localStringBuilder2 = new StringBuilder("delete file:");
          String str7 = localFile.getName();
          String str8 = str7;
          int m = Log.d("weibo", str8);
          boolean bool2 = localFile.delete();
        }
      }
      j += 1;
    }
  }*/
/*
  public static Intent commentComment(Context paramContext, CommentMessage paramCommentMessage)
  {
    Context localContext = paramContext.getApplicationContext();
    Intent localIntent1 = new Intent(localContext, EditActivity.class);
    EditActivity.LauchMode localLauchMode = EditActivity.LauchMode.COMMENT_COMMENT;
    Intent localIntent2 = localIntent1.putExtra("com.sina.weibo.intent.extra.LAUCH_MODE", localLauchMode);
    String str1 = paramCommentMessage.commentid;
    Intent localIntent3 = localIntent1.putExtra("com.sina.weibo.intent.extra.COMMENT_ID", str1);
    String str2 = paramCommentMessage.commentnick;
    Intent localIntent4 = localIntent1.putExtra("com.sina.weibo.intent.extra.NICKNAME", str2);
    String str3 = paramCommentMessage.commentuid;
    Intent localIntent5 = localIntent1.putExtra("com.sina.weibo.intent.extra.COMMENT_AUTHOR_UID", str3);
    String str4 = paramCommentMessage.mblogid;
    Intent localIntent6 = localIntent1.putExtra("com.sina.weibo.intent.extra.MBLOG_ID", str4);
    String str5 = paramCommentMessage.mbloguid;
    Intent localIntent7 = localIntent1.putExtra("com.sina.weibo.intent.extra.MBLOG_AUTHOR_UID", str5);
    return localIntent1;
  }

  public static Intent commentComment(Context paramContext, String paramString1, String paramString2, Comment paramComment)
  {
    Context localContext = paramContext.getApplicationContext();
    Intent localIntent1 = new Intent(localContext, EditActivity.class);
    EditActivity.LauchMode localLauchMode = EditActivity.LauchMode.COMMENT_COMMENT;
    Intent localIntent2 = localIntent1.putExtra("com.sina.weibo.intent.extra.LAUCH_MODE", localLauchMode);
    String str1 = paramComment.cmtid;
    Intent localIntent3 = localIntent1.putExtra("com.sina.weibo.intent.extra.COMMENT_ID", str1);
    String str2 = paramComment.nick;
    Intent localIntent4 = localIntent1.putExtra("com.sina.weibo.intent.extra.NICKNAME", str2);
    String str3 = paramComment.cmtuid;
    Intent localIntent5 = localIntent1.putExtra("com.sina.weibo.intent.extra.COMMENT_AUTHOR_UID", str3);
    Intent localIntent6 = localIntent1.putExtra("com.sina.weibo.intent.extra.MBLOG_ID", paramString1);
    Intent localIntent7 = localIntent1.putExtra("com.sina.weibo.intent.extra.MBLOG_AUTHOR_UID", paramString2);
    return localIntent1;
  }

  public static boolean compareVersion(Context paramContext, String paramString)
  {
    try
    {
      String str1 = PackageManagerUtil.getVersion(paramContext);
      Matcher localMatcher = Pattern.compile("(\\d+)\\.(\\d)\\.(\\d)").matcher(str1);
      if (localMatcher.matches())
      {
        String str2 = String.valueOf(localMatcher.group(1));
        StringBuilder localStringBuilder1 = new StringBuilder(str2).append("00");
        String str3 = localMatcher.group(2);
        StringBuilder localStringBuilder2 = localStringBuilder1.append(str3);
        String str4 = localMatcher.group(3);
        String str5 = str4;
        logd("localVersion: " + str5 + "\tinternetLatestVersion:" + paramString);
        int i = str5.compareTo(paramString);
        if (i < 0);
        for (j = 1; ; j = 0)
          return j;
      }
    }
    catch (Exception localException)
    {
      while (true)
      {
        loge(localException);
        int j = 0;
      }
    }
  }

  private static int conputingRadius(Paint paramPaint)
  {
    Rect localRect = new Rect();
    int i = "999+".length();
    paramPaint.getTextBounds("999+", 0, i, localRect);
    double d1 = localRect.right;
    double d2 = localRect.bottom;
    return (int)Math.hypot(d1, d2);
  }

  public static CustomToast createProgressCustomToast(int paramInt, Context paramContext)
  {
    return new CustomToast(paramContext, paramInt);
  }

  public static Dialog createProgressDialog(int paramInt1, Activity paramActivity, int paramInt2)
  {
    LinearLayout localLinearLayout = new LinearLayout(paramActivity);
    Dialog localDialog = new Dialog(paramActivity, 2131492867);
    TextView localTextView = setTextView(paramInt1, paramActivity);
    if (paramActivity.getClass() == ImageViewer.class)
      localTextView.setTextColor(-1);
    for (ProgressBar localProgressBar = new ProgressBar(paramActivity, null, 16842873); ; localProgressBar = new ProgressBar(paramActivity, null, 16843400))
    {
      localLinearLayout.addView(localProgressBar);
      localLinearLayout.addView(localTextView);
      localDialog.setContentView(localLinearLayout);
      localDialog.setCancelable(1);
      return localDialog;
    }
  }

  public static Toast createProgressToast(int paramInt, Context paramContext)
  {
    Toast localToast = new Toast(paramContext);
    LinearLayout localLinearLayout = new LinearLayout(paramContext);
    TextView localTextView = setTextView(paramInt, paramContext);
    if (paramContext.getClass() == ImageViewer.class)
      localTextView.setTextColor(-1);
    for (ProgressBar localProgressBar = new ProgressBar(paramContext, null, 16842873); ; localProgressBar = new ProgressBar(paramContext, null, 16843400))
    {
      localLinearLayout.addView(localProgressBar);
      localLinearLayout.addView(localTextView);
      localToast.setView(localLinearLayout);
      localToast.setDuration(0);
      localToast.setGravity(17, 0, 0);
      return localToast;
    }
  }

  public static String dateFormat(long paramLong)
  {
    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("M-dd HH:mm");
    StringBuffer localStringBuffer1 = new StringBuffer();
    long l1 = millisecond8hours;
    long l2 = paramLong + l1;
    Date localDate = new Date(l2);
    FieldPosition localFieldPosition = new FieldPosition(1);
    StringBuffer localStringBuffer2 = localSimpleDateFormat.format(localDate, localStringBuffer1, localFieldPosition);
    return localStringBuffer1.toString();
  }

  public static boolean deleteCacheItem(Object paramObject, int paramInt, String paramString)
  {
    switch (paramInt)
    {
    default:
    case 0:
    case 1:
    }
    label70: label246: 
    while (true)
    {
      int i = 0;
      return i;
      String str = (String)paramObject;
      if (StaticInfo.mUser != null)
      {
        User localUser1 = StaticInfo.mUser;
        localList = loadHomeListFile(paramString, localUser1);
        if (localList != null)
        {
          j = 0;
          k = localList.size();
          if (j < k)
            break label178;
        }
      }
      str = (String)paramObject;
      if (StaticInfo.mUser == null)
        continue;
      User localUser2 = StaticInfo.mUser;
      List localList = loadFavoriteListFile(paramString, localUser2);
      if (localList == null)
        continue;
      int j = 0;
      int k = localList.size();
      while (true)
      {
        if (j >= k)
          break label246;
        if (((MBlog)localList.get(j)).mblogid.equals(str))
        {
          Object localObject1 = localList.remove(j);
          User localUser3 = StaticInfo.mUser;
          saveFavoriteListFile(localList, paramString, localUser3);
          i = 1;
          break;
          if (((MBlog)localList.get(j)).mblogid.equals(str))
          {
            Object localObject2 = localList.remove(j);
            User localUser4 = StaticInfo.mUser;
            saveHomeListFile(localList, paramString, localUser4);
            i = 1;
            break;
          }
          j += 1;
          break label70;
        }
        j += 1;
      }
    }
  }

  public static Intent editAtBlog(Context paramContext, String paramString)
  {
    Context localContext = paramContext.getApplicationContext();
    Intent localIntent1 = new Intent(localContext, EditActivity.class);
    EditActivity.LauchMode localLauchMode = EditActivity.LauchMode.AT;
    Intent localIntent2 = localIntent1.putExtra("com.sina.weibo.intent.extra.LAUCH_MODE", localLauchMode);
    Intent localIntent3 = localIntent1.putExtra("com.sina.weibo.intent.extra.INIT_TEXT", paramString);
    return localIntent1;
  }

  public static Intent editNewBlog(Context paramContext)
  {
    Context localContext = paramContext.getApplicationContext();
    Intent localIntent1 = new Intent(localContext, EditActivity.class);
    EditActivity.LauchMode localLauchMode = EditActivity.LauchMode.NEW_BLOG;
    Intent localIntent2 = localIntent1.putExtra("com.sina.weibo.intent.extra.LAUCH_MODE", localLauchMode);
    return localIntent1;
  }

  public static Intent editNewBlog(Context paramContext, String paramString)
  {
    Context localContext = paramContext.getApplicationContext();
    Intent localIntent1 = new Intent(localContext, EditActivity.class);
    EditActivity.LauchMode localLauchMode = EditActivity.LauchMode.NEW_BLOG;
    Intent localIntent2 = localIntent1.putExtra("com.sina.weibo.intent.extra.LAUCH_MODE", localLauchMode);
    Intent localIntent3 = localIntent1.putExtra("com.sina.weibo.intent.extra.INIT_TEXT", paramString);
    return localIntent1;
  }

  public static Intent editPrivateMessage(Context paramContext, String paramString)
  {
    Context localContext = paramContext.getApplicationContext();
    Intent localIntent1 = new Intent(localContext, EditActivity.class);
    EditActivity.LauchMode localLauchMode = EditActivity.LauchMode.PRIVATE_MESSAGE;
    Intent localIntent2 = localIntent1.putExtra("com.sina.weibo.intent.extra.LAUCH_MODE", localLauchMode);
    Intent localIntent3 = localIntent1.putExtra("com.sina.weibo.intent.extra.NICKNAME", paramString);
    return localIntent1;
  }

  public static Intent editTopicBlog(Context paramContext, String paramString)
  {
    Context localContext = paramContext.getApplicationContext();
    Intent localIntent1 = new Intent(localContext, EditActivity.class);
    EditActivity.LauchMode localLauchMode = EditActivity.LauchMode.TOPIC;
    Intent localIntent2 = localIntent1.putExtra("com.sina.weibo.intent.extra.LAUCH_MODE", localLauchMode);
    Intent localIntent3 = localIntent1.putExtra("com.sina.weibo.intent.extra.INIT_TEXT", paramString);
    return localIntent1;
  }

  public static String formatDate(Context paramContext, Date paramDate)
  {
    long l1 = new Date().getTime();
    long l2 = paramDate.getTime();
    long l3 = l1 - l2;
    String str1;
    if (l3 > 86400000L)
      str1 = getDateFormat().format(paramDate);
    while (true)
    {
      return str1;
      if (l3 > 3600000L)
      {
        Object[] arrayOfObject1 = new Object[2];
        Long localLong1 = Long.valueOf(l3 / 3600000L);
        arrayOfObject1[0] = localLong1;
        String str2 = paramContext.getString(2131427504);
        arrayOfObject1[1] = str2;
        str1 = String.format("%d%s", arrayOfObject1);
        continue;
      }
      if (l3 > 60000L)
      {
        Object[] arrayOfObject2 = new Object[2];
        Long localLong2 = Long.valueOf(l3 / 60000L);
        arrayOfObject2[0] = localLong2;
        String str3 = paramContext.getString(2131427505);
        arrayOfObject2[1] = str3;
        str1 = String.format("%d%s", arrayOfObject2);
        continue;
      }
      Object[] arrayOfObject3 = new Object[1];
      String str4 = paramContext.getString(2131427505);
      arrayOfObject3[0] = str4;
      str1 = String.format("1%s", arrayOfObject3);
    }
  }

  public static Intent forwardMBlog(Context paramContext, MBlog paramMBlog)
  {
    Context localContext = paramContext.getApplicationContext();
    Intent localIntent1 = new Intent(localContext, EditActivity.class);
    EditActivity.LauchMode localLauchMode = EditActivity.LauchMode.FORWARD;
    Intent localIntent2 = localIntent1.putExtra("com.sina.weibo.intent.extra.LAUCH_MODE", localLauchMode);
    String str1 = paramMBlog.mblogid;
    Intent localIntent3 = localIntent1.putExtra("com.sina.weibo.intent.extra.MBLOG_ID", str1);
    String str2 = paramMBlog.uid;
    Intent localIntent4 = localIntent1.putExtra("com.sina.weibo.intent.extra.MBLOG_AUTHOR_UID", str2);
    if (!TextUtils.isEmpty(paramMBlog.rtreason))
    {
      String str3 = paramMBlog.rtreason;
      Intent localIntent5 = localIntent1.putExtra("com.sina.weibo.intent.extra.FORWARD_REASON", str3);
    }
    String str4 = paramMBlog.nick;
    Intent localIntent6 = localIntent1.putExtra("com.sina.weibo.intent.extra.NICKNAME", str4);
    return localIntent1;
  }

  public static Intent forwardSrcMBlog(Context paramContext, MBlog paramMBlog)
  {
    Context localContext = paramContext.getApplicationContext();
    Intent localIntent1 = new Intent(localContext, EditActivity.class);
    EditActivity.LauchMode localLauchMode = EditActivity.LauchMode.FORWARD;
    Intent localIntent2 = localIntent1.putExtra("com.sina.weibo.intent.extra.LAUCH_MODE", localLauchMode);
    String str1 = paramMBlog.mblogid;
    Intent localIntent3 = localIntent1.putExtra("com.sina.weibo.intent.extra.MBLOG_ID", str1);
    String str2 = paramMBlog.uid;
    Intent localIntent4 = localIntent1.putExtra("com.sina.weibo.intent.extra.MBLOG_AUTHOR_UID", str2);
    String str3 = paramMBlog.nick;
    Intent localIntent5 = localIntent1.putExtra("com.sina.weibo.intent.extra.NICKNAME", str3);
    return localIntent1;
  }



  public static Object[] getAttMBlogList(User paramUser, String paramString, int paramInt1, int paramInt2)
  {
    int i = paramInt1 - 1;
    int j = paramInt2 * i;
    Object[] arrayOfObject = new Object[2];
    ArrayList localArrayList = new ArrayList();
    arrayOfObject[1] = localArrayList;
    List localList = loadHomeListFile(paramString, paramUser);
    int k = 0;
    if (localList != null)
      k = 0;
    while (true)
    {
      if (k < paramInt2)
      {
        int m = j + k;
        int n = localList.size();
        if (m < n);
      }
      else
      {
        Integer localInteger = Integer.valueOf(k);
        arrayOfObject[0] = localInteger;
        return arrayOfObject;
      }
      int i1 = j + k;
      MBlog localMBlog = (MBlog)localList.get(i1);
      boolean bool = localArrayList.add(localMBlog);
      k += 1;
    }
  }

  private static SimpleDateFormat getDateFormat()
  {
    if (sdf == null)
      sdf = new SimpleDateFormat("MM-dd HH:mm");
    return sdf;
  }

  public static File getExternalCacheDir()
  {
    if (FileUtil.hasSDCardMounted())
    {
      if (externalCacheDir == null)
      {
        String str1 = String.valueOf(FileUtil.printSDCardRoot());
        String str2 = str1 + "/sina/weibo";
        externalCacheDir = new File(str2);
      }
      if (!externalCacheDir.exists())
        boolean bool = externalCacheDir.mkdirs();
    }
    for (File localFile = externalCacheDir; ; localFile = null)
      return localFile;
  }

  public static Pattern getFilenamePattern()
  {
    if (filenamepattern == null)
      filenamepattern = Pattern.compile("[^/\\\\<>*?:\"| ]+");
    return filenamepattern;
  }

  public static String getLargeImage(Context paramContext, String paramString, boolean paramBoolean)
  {
    String str1 = paramContext.getString(2131427630);
    if (!paramString.contains(str1))
      throw new RuntimeException("Pls make sure url contains \"wap\" chars.");
    try
    {
      String str2 = String.valueOf(paramContext.getCacheDir().getAbsolutePath());
      String str3 = str2 + "/large_imgs";
      if (!FileUtil.doesExisted(str3))
        boolean bool = new File(str3).mkdirs();
      String str4 = SettingsPref.getDownloadImageSize(paramContext);
      String str5 = paramString.replace(str1, str4);
      String str6 = RPCHelper.getInstance(paramContext).getPicture(str5, str3, paramBoolean);
      str7 = str6;
      return str7;
    }
    catch (RPCHelper.HttpException localHttpException)
    {
      while (true)
      {
        loge(localHttpException);
        String str7 = null;
      }
    }
  }

  public static long getLargeImagesTotalSize(Context paramContext)
  {
    String str = String.valueOf(paramContext.getCacheDir().getAbsolutePath());
    return FileUtil.getFileSize(str + "/large_imgs");
  }

  public static boolean getLoginStatus()
  {
    return isLogin;
  }

  public static Bitmap getPortraitBitmap(String paramString1, String paramString2, ListView paramListView, MBlog paramMBlog, boolean paramBoolean)
  {
    while (true)
    {
      Context localContext;
      Object localObject2;
      Bitmap localBitmap1;
      synchronized (lock)
      {
        localContext = paramListView.getContext();
        localObject2 = null;
        if (!isInCacheMap(paramString1))
          continue;
        localBitmap1 = (Bitmap)portraitCacheMap.get(paramString1);
        if ((localBitmap1 != null) && (!localBitmap1.isRecycled()))
          continue;
        Object localObject3 = portraitCacheMap.remove(paramString1);
        String str1 = checkCache(paramString2, paramString1);
        String str2 = "dir:  " + paramString2;
        int i = Log.d("cachedir>>>>>>", str2);
        localBitmap2 = loadFromFile(localContext, str1, paramMBlog, paramString1, paramBoolean);
        return localBitmap2;
        localBitmap2 = localBitmap1;
        continue;
        if ((localObject2 == null) || (!"".equals(localObject2)))
          continue;
        localObject2 = checkCache(paramString2, paramString1);
        if (localObject2 != null)
          break label258;
        String str3 = paramString1;
        ListView localListView1 = paramListView;
        String str4 = paramString2;
        MBlog localMBlog1 = paramMBlog;
        boolean bool1 = paramBoolean;
        localBitmap1 = loadFromNet(localContext, str3, localListView1, str4, localMBlog1, bool1);
        if ((localBitmap1 == null) || (localBitmap1.isRecycled()))
        {
          String str5 = paramString1;
          ListView localListView2 = paramListView;
          String str6 = paramString2;
          MBlog localMBlog2 = paramMBlog;
          boolean bool2 = paramBoolean;
          localBitmap2 = loadFromNet(localContext, str5, localListView2, str6, localMBlog2, bool2);
        }
      }
      monitorexit;
      Bitmap localBitmap2 = localBitmap1;
      continue;
      label258: localBitmap2 = loadFromFile(localContext, (String)localObject2, paramMBlog, paramString1, paramBoolean);
      monitorexit;
    }
  }

  public static Bitmap getPreviewBitmap(String paramString1, String paramString2, ListView paramListView, MBlog paramMBlog, boolean paramBoolean)
  {
    while (true)
    {
      Context localContext;
      Object localObject2;
      Bitmap localBitmap1;
      synchronized (lock)
      {
        localContext = paramListView.getContext();
        localObject2 = null;
        if ((!isInCacheMap(paramString1)) || (paramString2.equals("")))
          continue;
        localBitmap1 = (Bitmap)portraitCacheMap.get(paramString1);
        if ((localBitmap1 != null) && (!localBitmap1.isRecycled()))
          continue;
        Object localObject3 = portraitCacheMap.remove(paramString1);
        String str1 = checkCache(paramString2, paramString1);
        localBitmap2 = loadFromFile(localContext, str1, paramMBlog, paramString1, paramBoolean);
        return localBitmap2;
        localBitmap2 = localBitmap1;
        continue;
        if ((localObject2 == null) || (!"".equals(localObject2)))
          continue;
        localObject2 = checkCache(paramString2, paramString1);
        if (localObject2 != null)
          break label239;
        String str2 = paramString1;
        ListView localListView1 = paramListView;
        String str3 = paramString2;
        MBlog localMBlog1 = paramMBlog;
        boolean bool1 = paramBoolean;
        localBitmap1 = loadFromNet(localContext, str2, localListView1, str3, localMBlog1, bool1);
        if ((localBitmap1 == null) || (localBitmap1.isRecycled()))
        {
          String str4 = paramString1;
          ListView localListView2 = paramListView;
          String str5 = paramString2;
          MBlog localMBlog2 = paramMBlog;
          boolean bool2 = paramBoolean;
          localBitmap2 = loadFromNet(localContext, str4, localListView2, str5, localMBlog2, bool2);
        }
      }
      monitorexit;
      Bitmap localBitmap2 = localBitmap1;
      continue;
      label239: localBitmap2 = loadFromFile(localContext, (String)localObject2, paramMBlog, paramString1, paramBoolean);
      monitorexit;
    }
  }



  public static Throwable getRootCause(Throwable paramThrowable)
  {
    Object localObject;
    if (paramThrowable == null)
    {
      localObject = null;
      return localObject;
    }
    Throwable localThrowable1 = paramThrowable.getCause();
    Throwable localThrowable2 = localThrowable1;
    label15: if (localThrowable1 == null)
      if (localThrowable2 != null)
        break label42;
    label42: for (Throwable localThrowable3 = paramThrowable; ; localThrowable3 = localThrowable2)
    {
      localObject = localThrowable3;
      break;
      localThrowable2 = localThrowable1;
      localThrowable1 = localThrowable1.getCause();
      break label15;
    }
  }

  private static Pattern getSharpPattern()
  {
    if (sharppattern == null)
      sharppattern = Pattern.compile("#[^#]+?#");
    return sharppattern;
  }

  static Pattern getWebPattern()
  {
    if (webpattern == null)
      webpattern = Pattern.compile("http[s]*://[[[^/:]&&[a-zA-Z_0-9]]\\.]+(:\\d+)?(/[a-zA-Z_0-9]+)*(/[a-zA-Z_0-9]*([a-zA-Z_0-9]+\\.[a-zA-Z_0-9]+)*)?(\\?(&?[a-zA-Z_0-9]+=[%[a-zA-Z_0-9]-]*)*)*(#[[a-zA-Z_0-9]|-]+)?");
    return webpattern;
  }

  public static void highlightContent(Context paramContext, Spannable paramSpannable)
  {
    Matcher localMatcher = getAtPattern().matcher(paramSpannable);
    if (!localMatcher.find())
    {
      localMatcher = getWebPattern().matcher(paramSpannable);
      label23: if (localMatcher.find())
        break label138;
      localMatcher = getSharpPattern().matcher(paramSpannable);
      label38: if (localMatcher.find())
        break label179;
      localMatcher = Pattern.compile("\\[(\\S+?)\\]").matcher(paramSpannable);
    }
    while (true)
    {
      if (!localMatcher.find())
      {
        return;
        int i = localMatcher.start();
        int j = localMatcher.end();
        if (j - i == 2)
        {
          int k = i + 1;
          if (paramSpannable.charAt(k) == 25105);
        }
        else
        {
          if (j - i <= 2)
            break;
        }
        ForegroundColorSpan localForegroundColorSpan1 = new ForegroundColorSpan(-15050856);
        paramSpannable.setSpan(localForegroundColorSpan1, i, j, 33);
        break;
        label138: ForegroundColorSpan localForegroundColorSpan2 = new ForegroundColorSpan(-15050856);
        int m = localMatcher.start();
        int n = localMatcher.end();
        paramSpannable.setSpan(localForegroundColorSpan2, m, n, 33);
        break label23;
        label179: ForegroundColorSpan localForegroundColorSpan3 = new ForegroundColorSpan(-15050856);
        int i1 = localMatcher.start();
        int i2 = localMatcher.end();
        paramSpannable.setSpan(localForegroundColorSpan3, i1, i2, 33);
        break label38;
      }
      HashMap localHashMap = EmotionView.emotionsKeyString;
      String str = localMatcher.group(1);
      Integer localInteger = (Integer)localHashMap.get(str);
      if ((localInteger == null) || (localInteger.intValue() <= 0))
        continue;
      int i3 = localMatcher.start();
      int i4 = localMatcher.end();
      int i5 = localInteger.intValue();
      ImageSpan localImageSpan = new ImageSpan(paramContext, i5);
      paramSpannable.setSpan(localImageSpan, i3, i4, 33);
    }
  }

  public static void highlightContentClickable(Context paramContext, Spannable paramSpannable, String paramString)
  {
    Matcher localMatcher = getAtPattern().matcher(paramSpannable);
    if (!localMatcher.find())
    {
      localMatcher = getSharpPattern().matcher(paramSpannable);
      label23: if (localMatcher.find())
        break label192;
      localMatcher = getWebPattern().matcher(paramSpannable);
      label38: if (localMatcher.find())
        break label291;
      localMatcher = Pattern.compile("\\[(\\S+?)\\]").matcher(paramSpannable);
    }
    while (true)
    {
      if (!localMatcher.find())
      {
        return;
        i = localMatcher.start();
        j = localMatcher.end();
        if (j - i == 2)
        {
          int k = i + 1;
          if (paramSpannable.charAt(k) == 25105);
        }
        else
        {
          if (j - i <= 2)
            break;
        }
        ForegroundColorSpan localForegroundColorSpan1 = new ForegroundColorSpan(-15050856);
        paramSpannable.setSpan(localForegroundColorSpan1, i, j, 33);
        int m = i + 1;
        String str1 = paramSpannable.subSequence(m, j).toString();
        AtClicker localAtClicker = new AtClicker(str1);
        paramSpannable.setSpan(localAtClicker, i, j, 33);
        break;
        label192: int n = localMatcher.start();
        int i1 = localMatcher.end();
        ForegroundColorSpan localForegroundColorSpan2 = new ForegroundColorSpan(-15050856);
        paramSpannable.setSpan(localForegroundColorSpan2, n, i1, 33);
        int i2 = n + 1;
        int i3 = i1 - 1;
        String str2 = paramSpannable.subSequence(i2, i3).toString().trim();
        TopicClicker localTopicClicker = new TopicClicker(str2);
        paramSpannable.setSpan(localTopicClicker, n, i1, 33);
        break label23;
        label291: int i4 = localMatcher.start();
        int i5 = localMatcher.end();
        ForegroundColorSpan localForegroundColorSpan3 = new ForegroundColorSpan(-15050856);
        paramSpannable.setSpan(localForegroundColorSpan3, i4, i5, 33);
        String str3 = paramSpannable.subSequence(i4, i5).toString().trim();
        UrlClicker localUrlClicker = new UrlClicker(str3, paramString);
        paramSpannable.setSpan(localUrlClicker, i4, i5, 33);
        break label38;
      }
      int i = localMatcher.start();
      int j = localMatcher.end();
      HashMap localHashMap = EmotionView.emotionsKeyString;
      String str4 = localMatcher.group(1);
      Integer localInteger = (Integer)localHashMap.get(str4);
      if ((localInteger == null) || (localInteger.intValue() <= 0))
        continue;
      int i6 = localInteger.intValue();
      ImageSpan localImageSpan = new ImageSpan(paramContext, i6);
      paramSpannable.setSpan(localImageSpan, i, j, 33);
    }
  }

  // ERROR //
  private static boolean isInCacheMap(String paramString)
  {
    // Byte code:
    //   0: getstatic 165	com/sina/weibo/Utils:lock	Ljava/lang/Object;
    //   3: astore_1
    //   4: aload_1
    //   5: monitorenter
    //   6: aload_0
    //   7: ifnonnull +9 -> 16
    //   10: aload_1
    //   11: monitorexit
    //   12: aconst_null
    //   13: astore_1
    //   14: aload_1
    //   15: ireturn
    //   16: getstatic 157	com/sina/weibo/Utils:portraitCacheMap	Ljava/util/Map;
    //   19: aload_0
    //   20: invokeinterface 793 2 0
    //   25: checkcast 795	android/graphics/Bitmap
    //   28: ifnonnull +10 -> 38
    //   31: aload_1
    //   32: monitorexit
    //   33: aconst_null
    //   34: astore_1
    //   35: goto -21 -> 14
    //   38: aload_1
    //   39: monitorexit
    //   40: aconst_null
    //   41: astore_1
    //   42: goto -28 -> 14
    //   45: astore_2
    //   46: aload_1
    //   47: monitorexit
    //   48: aload_2
    //   49: athrow
    //
    // Exception table:
    //   from	to	target	type
    //   10	45	45	finally
  }
*/
  // ERROR //
  private static Object load(String paramString)
  {
      return new Object(); // this like need be deleted
    // Byte code:
    //   0: aconst_null
    //   1: astore_1
    //   2: new 262	java/io/File
    //   5: dup
    //   6: aload_0
    //   7: invokespecial 263	java/io/File:<init>	(Ljava/lang/String;)V
    //   10: astore_2
    //   11: aload_2
    //   12: invokevirtual 267	java/io/File:exists	()Z
    //   15: ifeq +74 -> 89
    //   18: new 909	java/io/FileInputStream
    //   21: dup
    //   22: aload_2
    //   23: invokespecial 911	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   26: astore_3
    //   27: new 913	java/io/ObjectInputStream
    //   30: dup
    //   31: aload_3
    //   32: invokespecial 916	java/io/ObjectInputStream:<init>	(Ljava/io/InputStream;)V
    //   35: astore 4
    //   37: aload 4
    //   39: invokevirtual 920	java/io/ObjectInputStream:readObject	()Ljava/lang/Object;
    //   42: astore 5
    //   44: aload 5
    //   46: astore_1
    //   47: aload 4
    //   49: invokevirtual 923	java/io/ObjectInputStream:close	()V
    //   52: aload_1
    //   53: astore 6
    //   55: aload 6
    //   57: areturn
    //   58: astore 7
    //   60: aload 7
    //   62: invokestatic 213	com/sina/weibo/Utils:loge	(Ljava/lang/Throwable;)V
    //   65: goto -18 -> 47
    //   68: astore 7
    //   70: aconst_null
    //   71: astore 6
    //   73: aload 7
    //   75: invokestatic 213	com/sina/weibo/Utils:loge	(Ljava/lang/Throwable;)V
    //   78: goto -23 -> 55
    //   81: astore 7
    //   83: aload_1
    //   84: astore 6
    //   86: goto -13 -> 73
    //   89: aconst_null
    //   90: astore 6
    //   92: goto -37 -> 55
    //
    // Exception table:
    //   from	to	target	type
    //   37	44	58	java/lang/ClassNotFoundException
    //   18	37	68	java/io/IOException
    //   37	44	68	java/io/IOException
    //   60	65	68	java/io/IOException
    //   47	52	81	java/io/IOException
  }
/*
  public static List<MBlog> loadAtListFile(String paramString, User paramUser)
  {
    String str1 = String.valueOf(paramString);
    StringBuilder localStringBuilder = new StringBuilder(str1).append(47);
    String str2 = paramUser.uid;
    return (List)load(str2 + "_at");
  }

  public static String loadBlogFile(String paramString)
  {
    String str = String.valueOf(paramString);
    return (String)load(str + "/blog.dat");
  }

  public static List<CommentMessage> loadCommentMessageListFile(String paramString, User paramUser)
  {
    String str1 = String.valueOf(paramString);
    StringBuilder localStringBuilder = new StringBuilder(str1).append(47);
    String str2 = paramUser.uid;
    return (List)load(str2 + "_commentmessage");
  }

  public static List<MBlog> loadFavoriteListFile(String paramString, User paramUser)
  {
    String str1 = String.valueOf(paramString);
    StringBuilder localStringBuilder = new StringBuilder(str1).append(47);
    String str2 = paramUser.uid;
    return (List)load(str2 + "_favorite");
  }

  public static Bitmap loadFromFile(Context paramContext, String paramString1, MBlog paramMBlog, String paramString2, boolean paramBoolean)
  {
    synchronized (lock)
    {
      Bitmap localBitmap1 = BitmapFactory.decodeFile(paramString1);
      if (!paramBoolean);
      Bitmap localBitmap2;
      for (??? = localBitmap1; ; ??? = localBitmap2)
      {
        return ???;
        if ((localBitmap1 == null) || (localBitmap1.isRecycled()))
          break;
        boolean bool1 = paramMBlog.vip;
        localBitmap2 = outputBmp(paramContext, localBitmap1, bool1);
        putCacheMap(paramString2, localBitmap2);
      }
      File localFile = new File(paramString1);
      if (localFile.canWrite())
        boolean bool2 = localFile.delete();
      ??? = null;
    }
  }

  // ERROR //
  public static Bitmap loadFromNet(Context paramContext, String paramString1, ListView paramListView, String paramString2, MBlog paramMBlog, boolean paramBoolean)
  {
    // Byte code:
    //   0: getstatic 165	com/sina/weibo/Utils:lock	Ljava/lang/Object;
    //   3: astore 6
    //   5: aload 6
    //   7: monitorenter
    //   8: aconst_null
    //   9: astore 7
    //   11: aload_0
    //   12: invokestatic 767	com/sina/weibo/RPCHelper:getInstance	(Landroid/content/Context;)Lcom/sina/weibo/RPCHelper;
    //   15: aload_1
    //   16: invokevirtual 949	com/sina/weibo/RPCHelper:getPictureByte	(Ljava/lang/String;)[B
    //   19: astore 8
    //   21: aload 8
    //   23: arraylength
    //   24: istore 9
    //   26: aload 8
    //   28: iconst_0
    //   29: iload 9
    //   31: invokestatic 953	android/graphics/BitmapFactory:decodeByteArray	([BII)Landroid/graphics/Bitmap;
    //   34: astore 10
    //   36: iload 5
    //   38: ifne +31 -> 69
    //   41: aload_3
    //   42: ldc_w 810
    //   45: invokevirtual 360	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   48: ifne +10 -> 58
    //   51: aload 8
    //   53: aload_1
    //   54: aload_3
    //   55: invokestatic 957	com/sina/weibo/Utils:savePictureLocal	([BLjava/lang/String;Ljava/lang/String;)V
    //   58: aload 6
    //   60: monitorexit
    //   61: iconst_0
    //   62: astore 11
    //   64: aload 10
    //   66: astore_1
    //   67: aload_1
    //   68: areturn
    //   69: aload 10
    //   71: ifnull +15 -> 86
    //   74: aload 10
    //   76: invokevirtual 798	android/graphics/Bitmap:isRecycled	()Z
    //   79: istore 12
    //   81: iload 12
    //   83: ifeq +14 -> 97
    //   86: aload 6
    //   88: monitorexit
    //   89: aconst_null
    //   90: astore_1
    //   91: iconst_0
    //   92: astore 13
    //   94: goto -27 -> 67
    //   97: aload_2
    //   98: invokevirtual 960	android/widget/ListView:getFirstVisiblePosition	()I
    //   101: istore 14
    //   103: aload_2
    //   104: invokevirtual 963	android/widget/ListView:getChildCount	()I
    //   107: istore 5
    //   109: iconst_0
    //   110: istore 15
    //   112: iload 15
    //   114: iload 5
    //   116: if_icmplt +33 -> 149
    //   119: aload_3
    //   120: ldc_w 810
    //   123: invokevirtual 360	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   126: ifne +10 -> 136
    //   129: aload 8
    //   131: aload_1
    //   132: aload_3
    //   133: invokestatic 957	com/sina/weibo/Utils:savePictureLocal	([BLjava/lang/String;Ljava/lang/String;)V
    //   136: aload 6
    //   138: monitorexit
    //   139: aload 7
    //   141: astore 16
    //   143: aload 7
    //   145: astore_1
    //   146: goto -79 -> 67
    //   149: aload_2
    //   150: iload 15
    //   152: invokevirtual 967	android/widget/ListView:getChildAt	(I)Landroid/view/View;
    //   155: instanceof 969
    //   158: ifne +12 -> 170
    //   161: iload 15
    //   163: iconst_1
    //   164: iadd
    //   165: istore 15
    //   167: goto -55 -> 112
    //   170: aload_2
    //   171: iload 15
    //   173: invokevirtual 967	android/widget/ListView:getChildAt	(I)Landroid/view/View;
    //   176: checkcast 969	com/sina/weibo/view/MBlogListItemView
    //   179: getfield 973	com/sina/weibo/view/MBlogListItemView:mBlog	Lcom/sina/weibo/models/MBlog;
    //   182: getfield 976	com/sina/weibo/models/MBlog:portrait	Ljava/lang/String;
    //   185: aload_1
    //   186: invokevirtual 360	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   189: ifeq -28 -> 161
    //   192: aload 4
    //   194: getfield 937	com/sina/weibo/models/MBlog:vip	Z
    //   197: istore 17
    //   199: aload_0
    //   200: aload 10
    //   202: iload 17
    //   204: invokestatic 941	com/sina/weibo/Utils:outputBmp	(Landroid/content/Context;Landroid/graphics/Bitmap;Z)Landroid/graphics/Bitmap;
    //   207: astore 7
    //   209: aload_1
    //   210: aload 7
    //   212: invokestatic 945	com/sina/weibo/Utils:putCacheMap	(Ljava/lang/String;Landroid/graphics/Bitmap;)V
    //   215: goto -54 -> 161
    //   218: astore_0
    //   219: aload 7
    //   221: astore_1
    //   222: ldc 69
    //   224: ldc_w 978
    //   227: aload_0
    //   228: invokestatic 982	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   231: istore 18
    //   233: aload 6
    //   235: monitorexit
    //   236: aload_1
    //   237: astore 19
    //   239: aconst_null
    //   240: astore_1
    //   241: aload 19
    //   243: astore 20
    //   245: goto -178 -> 67
    //   248: astore_1
    //   249: aload 7
    //   251: astore 21
    //   253: aload 6
    //   255: monitorexit
    //   256: aload_1
    //   257: athrow
    //   258: astore 22
    //   260: aload_1
    //   261: astore 23
    //   263: aload 22
    //   265: astore_1
    //   266: goto -13 -> 253
    //   269: astore_1
    //   270: goto -17 -> 253
    //
    // Exception table:
    //   from	to	target	type
    //   11	58	218	java/lang/Exception
    //   74	81	218	java/lang/Exception
    //   97	136	218	java/lang/Exception
    //   149	215	218	java/lang/Exception
    //   11	58	248	finally
    //   58	61	248	finally
    //   74	81	248	finally
    //   86	89	248	finally
    //   97	136	248	finally
    //   136	139	248	finally
    //   149	215	248	finally
    //   222	236	258	finally
    //   253	256	269	finally
  }

  public static List<MBlog> loadHomeListFile(String paramString, User paramUser)
  {
    if (mDB == null);
    for (List localList = null; ; localList = (List)mDB.select(0))
      return localList;
  }

  public static List<MBlog> loadHotForwardListFile(String paramString)
  {
    String str = String.valueOf(paramString);
    return (List)load(str + "/hotforward");
  }

  public static List<FavHotWord> loadHotWordListFile(String paramString, User paramUser)
  {
    String str1 = String.valueOf(paramString);
    StringBuilder localStringBuilder = new StringBuilder(str1).append(47);
    String str2 = paramUser.uid;
    return (List)load(str2 + "favhotword");
  }

  public static List<MBlog> loadLookAroundListFile(String paramString)
  {
    String str = String.valueOf(paramString);
    return (List)load(str + "/lookaround");
  }

  public static List<Message> loadMessageListFile(String paramString, User paramUser)
  {
    String str1 = String.valueOf(paramString);
    StringBuilder localStringBuilder = new StringBuilder(str1).append(47);
    String str2 = paramUser.uid;
    return (List)load(str2 + "_message");
  }
*/
//  public static int loadModeFile(String paramString, User paramUser)
//  {
//    int i = 0;
//    if (paramUser != null)
//    {
//      String str1 = String.valueOf(paramString);
//      StringBuilder localStringBuilder = new StringBuilder(str1).append(47);
//      String str2 = paramUser.uid;
//      Integer localInteger = (Integer)load(str2 + "_mode");
//      if (localInteger != null)
//        i = localInteger.intValue();
//      if (i > 8)
//        i = 0;
//    }
//    return i;
//  }
/*
  public static List<MBlog> loadMyBlogListFile(String paramString, User paramUser)
  {
    String str1 = String.valueOf(paramString);
    StringBuilder localStringBuilder = new StringBuilder(str1).append(47);
    String str2 = paramUser.uid;
    return (List)load(str2 + "_myblog");
  }

  public static Set<String> loadSearchWordList(String paramString)
  {
    return (Set)load(paramString);
  }

  public static Object[] loadSubMessageListFile(String paramString1, String paramString2, User paramUser)
  {
    String str1 = String.valueOf(paramString2);
    StringBuilder localStringBuilder = new StringBuilder(str1).append(47);
    String str2 = paramUser.uid;
    Object localObject = load(str2 + 95 + paramString1);
    if ((localObject instanceof Object[]));
    for (Object[] arrayOfObject = (Object[])localObject; ; arrayOfObject = null)
      return arrayOfObject;
  }

  public static List<UserInfo> loadTopUserListFile(String paramString)
  {
    String str = String.valueOf(paramString);
    return (List)load(str + "/topuser");
  }

  public static int[] loadTotalFile(String paramString, User paramUser)
  {
    String str1 = String.valueOf(paramString);
    StringBuilder localStringBuilder = new StringBuilder(str1).append(47);
    String str2 = paramUser.uid;
    return (int[])load(str2 + "_total");
  }
*/
//  public static User loadUserFile(String paramString)
//  {
//    String str = String.valueOf(paramString);
//    return (User)load(str + "/user.dat");
//  }
/*
  public static Set<String> loadUsrnameList(String paramString)
  {
    return (Set)load(paramString);
  }
*/
    public static void logd(CharSequence cs) {
        if (!TextUtils.isEmpty(cs)) {
            String str = cs.toString();
            Log.d("MicroBlog", str);
        }
    }

    public static void loge(CharSequence cs) {
        if (!TextUtils.isEmpty(cs)) {
            String str = cs.toString();
            Log.e("MicroBlog", str);
        }
    }

    public static void loge(Throwable throwable) {
        if (throwable != null)
            Log.e("MicroBlog", "", throwable);
    }
/*
  public static Bitmap outputBmp(Context paramContext, Bitmap paramBitmap, boolean paramBoolean)
  {
    Bitmap localBitmap1;
    Bitmap localBitmap3;
    if (paramBoolean)
    {
      localBitmap1 = BitmapFactory.decodeResource(paramContext.getResources(), 2130838172);
      paramBoolean = null;
      paramContext = 6;
      if ((paramBitmap == null) || (paramContext <= 0))
        break label370;
      int i = paramBitmap.getWidth();
      int j = paramBitmap.getHeight();
      Bitmap.Config localConfig1 = Bitmap.Config.ARGB_4444;
      Bitmap localBitmap2 = Bitmap.createBitmap(i, j, localConfig1);
      Canvas localCanvas1 = new Canvas(localBitmap2);
      rrbRath.reset();
      RectF localRectF1 = rrbRectf;
      float f1 = i;
      float f2 = j;
      localRectF1.set(0.0F, 0.0F, f1, f2);
      Path localPath1 = rrbRath;
      RectF localRectF2 = rrbRectf;
      float f3 = paramContext;
      float f4 = paramContext;
      Path.Direction localDirection = Path.Direction.CW;
      localPath1.addRoundRect(localRectF2, f3, f4, localDirection);
      Path localPath2 = rrbRath;
      boolean bool1 = localCanvas1.clipPath(localPath2);
      Paint localPaint1 = new Paint(1);
      localCanvas1.drawBitmap(paramBitmap, 0.0F, 0.0F, localPaint1);
      if ((localBitmap2 == null) || (localBitmap1 == null))
        break label366;
      int k = localBitmap2.getWidth();
      int m = localBitmap2.getHeight();
      int n = localBitmap1.getWidth();
      int i1 = localBitmap1.getHeight();
      int i2 = n / 2 + k;
      int i3 = i1 / 2 + m;
      Bitmap.Config localConfig2 = Bitmap.Config.ARGB_4444;
      localBitmap3 = Bitmap.createBitmap(i2, i3, localConfig2);
      localBitmap3.eraseColor(0);
      Canvas localCanvas2 = new Canvas(localBitmap3);
      Paint localPaint2 = new Paint(1);
      localCanvas2.drawBitmap(localBitmap2, 0.0F, 0.0F, localPaint2);
      int i4 = n / 2;
      float f5 = k - i4;
      int i5 = i1 / 2;
      float f6 = m - i5;
      Paint localPaint3 = new Paint(1);
      localCanvas2.drawBitmap(localBitmap1, f5, f6, localPaint3);
      localBitmap2.recycle();
      localBitmap1.recycle();
    }
    for (paramBitmap = localBitmap3; ; paramBitmap = paramBoolean)
    {
      return paramBitmap;
      localBitmap1 = BitmapFactory.decodeResource(paramContext.getResources(), 2130838136);
      break;
      label366: paramBitmap.recycle();
      label370: boolean bool2 = paramBoolean;
    }
  }

  public static Bitmap outputCoverBmp(Context paramContext, Bitmap paramBitmap, int paramInt)
  {
    if (paramInt == 1);
    for (Bitmap localBitmap = BitmapFactory.decodeResource(paramContext.getResources(), 2130838172); ; localBitmap = BitmapFactory.decodeResource(paramContext.getResources(), 2130838173))
      return outputCoverBmp(outputRoundRectBitmap(paramBitmap), localBitmap);
  }

  public static Bitmap outputCoverBmp(Bitmap paramBitmap1, Bitmap paramBitmap2)
  {
    Bitmap localBitmap1;
    if ((paramBitmap1 != null) && (paramBitmap2 != null))
    {
      int i = paramBitmap1.getWidth();
      int j = paramBitmap1.getHeight();
      int k = paramBitmap2.getWidth();
      int m = paramBitmap2.getHeight();
      int n = k / 2 + i;
      int i1 = m / 2 + j;
      Bitmap.Config localConfig = Bitmap.Config.ARGB_4444;
      localBitmap1 = Bitmap.createBitmap(n, i1, localConfig);
      localBitmap1.eraseColor(0);
      Canvas localCanvas = new Canvas(localBitmap1);
      Paint localPaint1 = new Paint(1);
      localCanvas.drawBitmap(paramBitmap1, 0.0F, 0.0F, localPaint1);
      int i2 = k / 2;
      float f1 = i - i2;
      int i3 = m / 2;
      float f2 = j - i3;
      Paint localPaint2 = new Paint(1);
      localCanvas.drawBitmap(paramBitmap2, f1, f2, localPaint2);
      paramBitmap1.recycle();
      paramBitmap2.recycle();
    }
    for (Bitmap localBitmap2 = localBitmap1; ; localBitmap2 = null)
      return localBitmap2;
  }

  public static void outputLogger(String paramString)
  {
    if (FileUtil.hasSDCardMounted());
    while (true)
    {
      return;
      File localFile1 = getExternalCacheDir();
      File localFile2 = new File(localFile1, "logger.txt");
      if (!localFile2.getParentFile().exists())
        boolean bool = localFile2.mkdirs();
      try
      {
        byte[] arrayOfByte = paramString.getBytes();
        FileOutputStream localFileOutputStream = new FileOutputStream(localFile2, 1);
        localFileOutputStream.write(arrayOfByte);
        localFileOutputStream.close();
      }
      catch (IOException localIOException)
      {
        localIOException.printStackTrace();
      }
    }
  }

  public static Bitmap outputNumberBitmap(Bitmap paramBitmap, int paramInt)
  {
    Bitmap localBitmap1;
    String str;
    if ((paramBitmap != null) && (paramInt > 0))
    {
      int i = paramBitmap.getHeight();
      int j = paramBitmap.getWidth();
      Bitmap.Config localConfig = Bitmap.Config.ARGB_4444;
      localBitmap1 = Bitmap.createBitmap(j, i, localConfig);
      nbPaint.setTextSize(10.0F);
      int k = (int)nbPaint.descent();
      int m = conputingRadius(nbPaint);
      Canvas localCanvas = new Canvas(localBitmap1);
      Paint localPaint1 = nbPaint;
      localCanvas.drawBitmap(paramBitmap, 0.0F, 0.0F, localPaint1);
      nbPaint.setColor(-65536);
      float f1 = j - m;
      float f2 = i - m;
      float f3 = m;
      Paint localPaint2 = nbPaint;
      localCanvas.drawCircle(f1, f2, f3, localPaint2);
      Paint localPaint3 = nbPaint;
      Paint.Align localAlign = Paint.Align.CENTER;
      localPaint3.setTextAlign(localAlign);
      if (paramInt > 999)
      {
        str = "999+";
        nbPaint.setColor(-1);
        float f4 = j - m;
        float f5 = i - m + k;
        Paint localPaint4 = nbPaint;
        localCanvas.drawText(str, f4, f5, localPaint4);
      }
    }
    for (Bitmap localBitmap2 = localBitmap1; ; localBitmap2 = null)
    {
      return localBitmap2;
      str = String.valueOf(paramInt);
      break;
    }
  }

  public static Bitmap outputRoundRectBitmap(Bitmap paramBitmap)
  {
    return outputRoundRectBitmap(paramBitmap, 6);
  }

  private static Bitmap outputRoundRectBitmap(Bitmap paramBitmap, int paramInt)
  {
    Bitmap localBitmap1;
    if ((paramBitmap != null) && (paramInt > 0))
    {
      int i = paramBitmap.getWidth();
      int j = paramBitmap.getHeight();
      Bitmap.Config localConfig = Bitmap.Config.ARGB_4444;
      localBitmap1 = Bitmap.createBitmap(i, j, localConfig);
      Canvas localCanvas = new Canvas(localBitmap1);
      rrbRath.reset();
      RectF localRectF1 = rrbRectf;
      float f1 = i;
      float f2 = j;
      localRectF1.set(0.0F, 0.0F, f1, f2);
      Path localPath1 = rrbRath;
      RectF localRectF2 = rrbRectf;
      float f3 = paramInt;
      float f4 = paramInt;
      Path.Direction localDirection = Path.Direction.CW;
      localPath1.addRoundRect(localRectF2, f3, f4, localDirection);
      Path localPath2 = rrbRath;
      boolean bool = localCanvas.clipPath(localPath2);
      Paint localPaint = new Paint(1);
      localCanvas.drawBitmap(paramBitmap, 0.0F, 0.0F, localPaint);
      paramBitmap.recycle();
    }
    for (Bitmap localBitmap2 = localBitmap1; ; localBitmap2 = null)
      return localBitmap2;
  }

  private static void putCacheMap(String paramString, Bitmap paramBitmap)
  {
    synchronized (lock)
    {
      if (isInCacheMap(paramString))
        return;
      if (portraitCacheMap.size() <= 200)
        break label108;
      boolean bool1 = portraitQueue.add(paramString);
      if (portraitQueue.size() > 0);
    }
    String str = (String)portraitQueue.remove();
    Bitmap localBitmap = (Bitmap)portraitCacheMap.remove(str);
    Object localObject3 = portraitCacheMap.put(paramString, paramBitmap);
    while (true)
    {
      monitorexit;
      break;
      label108: Object localObject4 = portraitCacheMap.put(paramString, paramBitmap);
      boolean bool2 = portraitQueue.add(paramString);
    }
  }
*/
  private static void save(Object paramObject, String paramString)
  {
    try
    {
      FileOutputStream localFileOutputStream = new FileOutputStream(paramString);
      ObjectOutputStream localObjectOutputStream = new ObjectOutputStream(localFileOutputStream);
      localObjectOutputStream.writeObject(paramObject);
      localObjectOutputStream.flush();
      localObjectOutputStream.close();
      return;
    }
    catch (IOException localIOException)
    {
      while (true)
        loge(localIOException);
    }
  }
/*
  public static void saveAtListFile(List<MBlog> paramList, String paramString, User paramUser)
  {
    int i = paramList.size();
    int j = CACHE_LIMITATION;
    if (i > j)
    {
      ArrayList localArrayList = new ArrayList();
      int k = CACHE_LIMITATION - 1;
      List localList = paramList.subList(0, k);
      boolean bool = localArrayList.addAll(localList);
      String str1 = String.valueOf(paramString);
      StringBuilder localStringBuilder1 = new StringBuilder(str1).append(47);
      String str2 = paramUser.uid;
      String str3 = str2 + "_at";
      save(localArrayList, str3);
    }
    while (true)
    {
      return;
      String str4 = String.valueOf(paramString);
      StringBuilder localStringBuilder2 = new StringBuilder(str4).append(47);
      String str5 = paramUser.uid;
      String str6 = str5 + "_at";
      save(paramList, str6);
    }
  }

  public static void saveBlogFile(String paramString1, String paramString2)
  {
    String str1 = String.valueOf(paramString2);
    String str2 = str1 + "/blog.dat";
    save(paramString1, str2);
  }

  public static void saveCommentMessageListFile(List<CommentMessage> paramList, String paramString, User paramUser)
  {
    int i = paramList.size();
    int j = CACHE_LIMITATION;
    if (i > j)
    {
      ArrayList localArrayList = new ArrayList();
      int k = CACHE_LIMITATION - 1;
      List localList = paramList.subList(0, k);
      boolean bool = localArrayList.addAll(localList);
      String str1 = String.valueOf(paramString);
      StringBuilder localStringBuilder1 = new StringBuilder(str1).append(47);
      String str2 = paramUser.uid;
      String str3 = str2 + "_commentmessage";
      save(localArrayList, str3);
    }
    while (true)
    {
      return;
      String str4 = String.valueOf(paramString);
      StringBuilder localStringBuilder2 = new StringBuilder(str4).append(47);
      String str5 = paramUser.uid;
      String str6 = str5 + "_commentmessage";
      save(paramList, str6);
    }
  }

  public static void saveFavoriteListFile(List<MBlog> paramList, String paramString, User paramUser)
  {
    int i = paramList.size();
    int j = CACHE_LIMITATION;
    if (i > j)
    {
      ArrayList localArrayList = new ArrayList();
      int k = CACHE_LIMITATION - 1;
      List localList = paramList.subList(0, k);
      boolean bool = localArrayList.addAll(localList);
      String str1 = String.valueOf(paramString);
      StringBuilder localStringBuilder1 = new StringBuilder(str1).append(47);
      String str2 = paramUser.uid;
      String str3 = str2 + "_favorite";
      save(localArrayList, str3);
    }
    while (true)
    {
      return;
      String str4 = String.valueOf(paramString);
      StringBuilder localStringBuilder2 = new StringBuilder(str4).append(47);
      String str5 = paramUser.uid;
      String str6 = str5 + "_favorite";
      save(paramList, str6);
    }
  }

  public static void saveHomeListFile(List<MBlog> paramList, String paramString, User paramUser)
  {
    boolean bool1 = mDB.deleteAll(0);
    int i = paramList.size();
    int j = CACHE_LIMITATION;
    if (i > j)
    {
      ArrayList localArrayList = new ArrayList();
      int k = CACHE_LIMITATION - 1;
      List localList = paramList.subList(0, k);
      boolean bool2 = localArrayList.addAll(localList);
      boolean bool3 = mDB.insert(localArrayList, 0);
    }
    while (true)
    {
      return;
      boolean bool4 = mDB.insert(paramList, 0);
    }
  }

  public static void saveHotForwardListFile(List<MBlog> paramList, String paramString)
  {
    String str1 = String.valueOf(paramString);
    String str2 = str1 + "/hotforward";
    save(paramList, str2);
  }

  public static void saveHotWordListFile(List<FavHotWord> paramList, String paramString, User paramUser)
  {
    int i = paramList.size();
    int j = CACHE_LIMITATION;
    if (i > j)
    {
      ArrayList localArrayList = new ArrayList();
      int k = CACHE_LIMITATION - 1;
      List localList = paramList.subList(0, k);
      boolean bool = localArrayList.addAll(localList);
      String str1 = String.valueOf(paramString);
      StringBuilder localStringBuilder1 = new StringBuilder(str1).append(47);
      String str2 = paramUser.uid;
      String str3 = str2 + "favhotword";
      save(localArrayList, str3);
    }
    while (true)
    {
      return;
      String str4 = String.valueOf(paramString);
      StringBuilder localStringBuilder2 = new StringBuilder(str4).append(47);
      String str5 = paramUser.uid;
      String str6 = str5 + "favhotword";
      save(paramList, str6);
    }
  }

  public static void saveLookAroundListFile(List<MBlog> paramList, String paramString)
  {
    String str1 = String.valueOf(paramString);
    String str2 = str1 + "/lookaround";
    save(paramList, str2);
  }

  public static void saveMessageListFile(List<Message> paramList, String paramString, User paramUser)
  {
    int i = paramList.size();
    int j = CACHE_LIMITATION;
    if (i > j)
    {
      ArrayList localArrayList = new ArrayList();
      int k = CACHE_LIMITATION - 1;
      List localList = paramList.subList(0, k);
      boolean bool = localArrayList.addAll(localList);
      String str1 = String.valueOf(paramString);
      StringBuilder localStringBuilder1 = new StringBuilder(str1).append(47);
      String str2 = paramUser.uid;
      String str3 = str2 + "_message";
      save(localArrayList, str3);
    }
    while (true)
    {
      return;
      String str4 = String.valueOf(paramString);
      StringBuilder localStringBuilder2 = new StringBuilder(str4).append(47);
      String str5 = paramUser.uid;
      String str6 = str5 + "_message";
      save(paramList, str6);
    }
  }
*/
//  public static void saveModeFile(int paramInt, String paramString, User paramUser)
//  {
//    Integer localInteger = new Integer(paramInt);
//    String str1 = String.valueOf(paramString);
//    StringBuilder localStringBuilder = new StringBuilder(str1).append(47);
//    String str2 = paramUser.uid;
//    String str3 = str2 + "_mode";
//    save(localInteger, str3);
//  }
/*
  public static void saveMyBlogListFile(List<MBlog> paramList, String paramString, User paramUser)
  {
    int i = paramList.size();
    int j = CACHE_LIMITATION;
    if (i > j)
    {
      ArrayList localArrayList = new ArrayList();
      int k = CACHE_LIMITATION - 1;
      List localList = paramList.subList(0, k);
      boolean bool = localArrayList.addAll(localList);
      String str1 = String.valueOf(paramString);
      StringBuilder localStringBuilder1 = new StringBuilder(str1).append(47);
      String str2 = paramUser.uid;
      String str3 = str2 + "_myblog";
      save(localArrayList, str3);
    }
    while (true)
    {
      return;
      String str4 = String.valueOf(paramString);
      StringBuilder localStringBuilder2 = new StringBuilder(str4).append(47);
      String str5 = paramUser.uid;
      String str6 = str5 + "_myblog";
      save(paramList, str6);
    }
  }

  // ERROR //have zhushi it self
  public static void savePictureLocal(byte[] paramArrayOfByte, String paramString1, String paramString2)
    throws RPCHelper.ApiException, IOException
  {
    // Byte code:
    //   0: getstatic 165	com/sina/weibo/Utils:lock	Ljava/lang/Object;
    //   3: astore_3
    //   4: aload_3
    //   5: monitorenter
    //   6: aload_0
    //   7: ifnull +8 -> 15
    //   10: aload_0
    //   11: arraylength
    //   12: ifgt +6 -> 18
    //   15: aload_3
    //   16: monitorexit
    //   17: return
    //   18: new 262	java/io/File
    //   21: dup
    //   22: aload_2
    //   23: invokespecial 263	java/io/File:<init>	(Ljava/lang/String;)V
    //   26: astore 4
    //   28: aload 4
    //   30: invokevirtual 267	java/io/File:exists	()Z
    //   33: ifne +10 -> 43
    //   36: aload 4
    //   38: invokevirtual 732	java/io/File:mkdirs	()Z
    //   41: istore 5
    //   43: aload_1
    //   44: ifnull +27 -> 71
    //   47: ldc_w 810
    //   50: aload_1
    //   51: invokevirtual 360	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   54: ifne +17 -> 71
    //   57: aload_2
    //   58: ifnull +13 -> 71
    //   61: ldc_w 810
    //   64: aload_2
    //   65: invokevirtual 360	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   68: ifeq +15 -> 83
    //   71: aload_3
    //   72: monitorexit
    //   73: goto -56 -> 17
    //   76: astore 6
    //   78: aload_3
    //   79: monitorexit
    //   80: aload 6
    //   82: athrow
    //   83: aload_1
    //   84: invokestatic 250	com/sina/weibo/MD5:hexdigest	(Ljava/lang/String;)Ljava/lang/String;
    //   87: astore 7
    //   89: aload_2
    //   90: invokestatic 233	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   93: astore 8
    //   95: new 235	java/lang/StringBuilder
    //   98: dup
    //   99: aload 8
    //   101: invokespecial 238	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   104: ldc 240
    //   106: invokevirtual 244	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   109: aload 7
    //   111: invokevirtual 244	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   114: invokevirtual 254	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   117: astore 9
    //   119: new 262	java/io/File
    //   122: dup
    //   123: aload 9
    //   125: invokespecial 263	java/io/File:<init>	(Ljava/lang/String;)V
    //   128: astore 10
    //   130: aconst_null
    //   131: astore 11
    //   133: aload 10
    //   135: invokevirtual 267	java/io/File:exists	()Z
    //   138: ifeq +18 -> 156
    //   141: aload 10
    //   143: invokevirtual 348	java/io/File:canWrite	()Z
    //   146: ifeq +10 -> 156
    //   149: aload 10
    //   151: invokevirtual 281	java/io/File:delete	()Z
    //   154: istore 12
    //   156: new 1098	java/io/FileOutputStream
    //   159: dup
    //   160: aload 10
    //   162: invokespecial 1204	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   165: astore 13
    //   167: aload 13
    //   169: aload_0
    //   170: invokevirtual 1104	java/io/FileOutputStream:write	([B)V
    //   173: aload 13
    //   175: invokevirtual 1205	java/io/FileOutputStream:flush	()V
    //   178: aload 13
    //   180: invokevirtual 1105	java/io/FileOutputStream:close	()V
    //   183: aload_3
    //   184: monitorexit
    //   185: goto -168 -> 17
    //   188: astore 14
    //   190: aload 11
    //   192: ifnull +8 -> 200
    //   195: aload 11
    //   197: invokevirtual 1105	java/io/FileOutputStream:close	()V
    //   200: new 1203	com/sina/weibo/RPCHelper$ApiException
    //   203: dup
    //   204: ldc_w 1207
    //   207: aload 14
    //   209: invokespecial 1210	com/sina/weibo/RPCHelper$ApiException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   212: athrow
    //   213: astore 14
    //   215: aload 13
    //   217: astore 11
    //   219: goto -29 -> 190
    //
    // Exception table:
    //   from	to	target	type
    //   10	76	76	finally
    //   83	130	76	finally
    //   133	167	76	finally
    //   167	183	76	finally
    //   183	213	76	finally
    //   133	167	188	java/io/IOException
    //   167	183	213	java/io/IOException
  }

  public static void saveSearchWordList(String paramString, Set<String> paramSet)
  {
    boolean bool = cleanSearchWordList(paramString);
    save(paramSet, paramString);
  }

  public static void saveSubMessageListFile(List<Message> paramList, int paramInt, String paramString1, String paramString2, User paramUser)
  {
    Object[] arrayOfObject = new Object[2];
    int i = paramList.size();
    int j = CACHE_LIMITATION;
    if (i > j)
    {
      ArrayList localArrayList = new ArrayList();
      int k = CACHE_LIMITATION - 1;
      List localList = paramList.subList(0, k);
      boolean bool = localArrayList.addAll(localList);
      Integer localInteger1 = Integer.valueOf(paramInt);
      arrayOfObject[0] = localInteger1;
      arrayOfObject[1] = localArrayList;
    }
    while (true)
    {
      String str1 = String.valueOf(paramString2);
      StringBuilder localStringBuilder = new StringBuilder(str1).append(47);
      String str2 = paramUser.uid;
      String str3 = str2 + 95 + paramString1;
      save(arrayOfObject, str3);
      return;
      Integer localInteger2 = Integer.valueOf(paramInt);
      arrayOfObject[0] = localInteger2;
      arrayOfObject[1] = paramList;
    }
  }

  public static void saveTopUserListFile(List<UserInfo> paramList, String paramString)
  {
    String str1 = String.valueOf(paramString);
    String str2 = str1 + "/topuser";
    save(paramList, str2);
  }

  public static void saveTotalFile(int[] paramArrayOfInt, String paramString, User paramUser)
  {
    String str1 = String.valueOf(paramString);
    StringBuilder localStringBuilder = new StringBuilder(str1).append(47);
    String str2 = paramUser.uid;
    String str3 = str2 + "_total";
    save(paramArrayOfInt, str3);
  }

  public static void saveUserFile(User paramUser, String paramString, Context paramContext)
  {
    if (paramUser == null);
    while (true)
    {
      return;
      String str1 = String.valueOf(paramString);
      String str2 = str1 + "/user.dat";
      save(paramUser, str2);
    }
  }

  public static void saveUsrnameList(String paramString, Set<String> paramSet)
  {
    boolean bool = cleanUsrnameList(paramString);
    save(paramSet, paramString);
  }

  public static Intent sendFeedback(Context paramContext)
  {
    Context localContext = paramContext.getApplicationContext();
    Intent localIntent1 = new Intent(localContext, EditActivity.class);
    EditActivity.LauchMode localLauchMode = EditActivity.LauchMode.FEEDBACK;
    Intent localIntent2 = localIntent1.putExtra("com.sina.weibo.intent.extra.LAUCH_MODE", localLauchMode);
    return localIntent1;
  }
*/
  public static void setLoginStatus(boolean paramBoolean)
  {
    isLogin = paramBoolean;
  }
/*
  public static TextView setTextView(int paramInt, Context paramContext)
  {
    TextView localTextView = new TextView(paramContext);
    localTextView.setText(paramInt);
    localTextView.setGravity(17);
    localTextView.setTextSize(13.0F);
    localTextView.setPadding(15, 0, 15, 0);
    localTextView.setTextColor(-16777216);
    return localTextView;
  }

  public static Intent simpleComment(Context paramContext, String paramString1, String paramString2)
  {
    Context localContext = paramContext.getApplicationContext();
    Intent localIntent1 = new Intent(localContext, EditActivity.class);
    EditActivity.LauchMode localLauchMode = EditActivity.LauchMode.SIMPLE_COMMENT;
    Intent localIntent2 = localIntent1.putExtra("com.sina.weibo.intent.extra.LAUCH_MODE", localLauchMode);
    Intent localIntent3 = localIntent1.putExtra("com.sina.weibo.intent.extra.MBLOG_ID", paramString1);
    Intent localIntent4 = localIntent1.putExtra("com.sina.weibo.intent.extra.MBLOG_AUTHOR_UID", paramString2);
    return localIntent1;
  }

  public static String translationThrowable(Context paramContext, Throwable paramThrowable)
  {
    paramThrowable = getRootCause(paramThrowable);
    String str3;
    if ((paramThrowable instanceof RPCHelper.ApiException))
    {
      String str1 = paramThrowable.getMessage();
      if (str1.contains(":"))
      {
        int i = str1.indexOf(":");
        String str2 = str1.substring(i);
      }
      str3 = str1;
    }
    while (true)
    {
      return str3;
      if ((paramThrowable instanceof RPCHelper.NoSignalException))
      {
        str3 = paramContext.getString(2131427724);
        continue;
      }
      if ((paramThrowable instanceof NoRouteToHostException))
      {
        str3 = paramContext.getString(2131427718);
        continue;
      }
      if ((paramThrowable instanceof SocketTimeoutException))
      {
        str3 = paramContext.getString(2131427719);
        continue;
      }
      if ((paramThrowable instanceof UnknownHostException))
      {
        str3 = paramContext.getString(2131427720);
        continue;
      }
      if ((paramThrowable instanceof IOException))
      {
        str3 = paramContext.getString(2131427721);
        continue;
      }
      if (paramThrowable.getMessage() == null)
      {
        str3 = paramContext.getString(2131427722);
        continue;
      }
      str3 = paramThrowable.getMessage();
    }
  }

  public static String trim(String paramString)
  {
    StringBuilder localStringBuilder1;
    int i;
    if ((paramString != null) && (paramString.length() != 0))
    {
      localStringBuilder1 = new StringBuilder();
      i = 0;
      int j = paramString.length();
      if (i < j);
    }
    for (String str = String.valueOf(localStringBuilder1.toString()); ; str = paramString)
    {
      return str;
      if (paramString.charAt(i) != 32)
      {
        char c = paramString.charAt(i);
        StringBuilder localStringBuilder2 = localStringBuilder1.append(c);
      }
      i += 1;
      break;
    }
  }

  class AtClicker extends ClickableSpan
  {
    private String nickName;

    public AtClicker(String arg2)
    {
      Object localObject;
      this.nickName = localObject;
    }

    public void onClick(View paramView)
    {
      Context localContext = Utils.this;
      Intent localIntent1 = new Intent(localContext, UserInfoActivity2.class);
      String str1 = this.nickName;
      Intent localIntent2 = localIntent1.putExtra("nick", str1);
      String str2 = StaticInfo.mUser.uid;
      Intent localIntent3 = localIntent1.putExtra("myuid", str2);
      Intent localIntent4 = localIntent1.putExtra("vip", 1);
      Utils.this.startActivity(localIntent1);
    }
  }

  class TopicClicker extends ClickableSpan
  {
    private String topic;

    public TopicClicker(String arg2)
    {
      Object localObject;
      this.topic = localObject;
    }

    public void onClick(View paramView)
    {
      Context localContext = Utils.this;
      Intent localIntent1 = new Intent(localContext, UserTopicAttentionList.class);
      String str = this.topic;
      Intent localIntent2 = localIntent1.putExtra("query", str);
      Utils.this.startActivity(localIntent1);
    }
  }

  class UrlClicker extends ClickableSpan
  {
    private static final String URL_PREX = "http://t.sina.cn/dpool/ttt/sinaurlc.php?vt=3&u=%s&gsid=%s";
    private String gsid;
    private String urlPath;

    public UrlClicker(String paramString1, String arg3)
    {
      this.urlPath = paramString1;
      Object localObject;
      this.gsid = localObject;
    }

    public void onClick(View paramView)
    {
      Intent localIntent1 = new Intent("android.intent.action.VIEW");
      Object[] arrayOfObject = new Object[2];
      String str1 = URLEncoder.encode(this.urlPath);
      arrayOfObject[0] = str1;
      String str2 = this.gsid;
      arrayOfObject[1] = str2;
      Uri localUri = Uri.parse(String.format("http://t.sina.cn/dpool/ttt/sinaurlc.php?vt=3&u=%s&gsid=%s", arrayOfObject));
      Intent localIntent2 = localIntent1.setData(localUri);
      Utils.this.startActivity(localIntent1);
    }
  } */
  
  public static byte[] getImageByteArrayFromDatabase(Context context, String photoaddress) {
  	Cursor cursor = null;
  	String selection = AlbumColumns.PHOTO_ADDRESS + "=?";
  	ContentResolver resolver = context.getContentResolver();
  	try {
  		cursor = resolver.query(Album.ALBUM_CONTENT_URI, Album.CONTENT_PROJECTION, selection,
  				new String[] { photoaddress }, null);
  		
  		if (cursor != null && cursor.moveToFirst()) {
              byte[] data = cursor.getBlob(AlbumColumns.CONTENT_PHOTO_BITMAP_COLUMN);
              if (data != null && data.length != 0) {
            	  return data;
//              	Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, null);
//              	bitmap = Utils.getResizedBitmap(bitmap, ((BaseActivity)context).mImageWidth, ((BaseActivity)context).mImageHeigth);
//              	return bitmap;
              }
  		}
  	} finally {
  		if (cursor != null) {
  			cursor.close();
  		}
  	}
  	
  	return null;
  }
  
  // 显示网络上的图片
  public static byte[] getInputStreamBytesFromNetwork(String url, Context context) {
      ContentResolver resolver = context.getContentResolver();
      Log.i("returnBitMap", "url=" + url);
      URL myFileUrl = null;
      Bitmap bitmap = null;
      byte[] bytes = null;
      try {
          myFileUrl = new URL(url);
      } catch (MalformedURLException e) {
          e.printStackTrace();
      }

      try {
          HttpURLConnection conn = (HttpURLConnection)myFileUrl.openConnection();
          conn.setDoInput(true);
          conn.connect();
          InputStream is = conn.getInputStream();
          bytes = inputStreamToByte(is);
          
          is.close();
      } catch (IOException e) {
          e.printStackTrace();
      }

      return bytes;
  }
}

/* Location:           /home/CORPUSERS/28850420/microblog/microblog/classes.dex.dex2jar.jar
 * Qualified Name:     com.sina.weibo.Utils
 * JD-Core Version:    0.6.0
 */