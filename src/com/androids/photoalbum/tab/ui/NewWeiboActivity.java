
package com.androids.photoalbum.tab.ui;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import weibo4android.examples.Upload;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.photoalbum.R;
import com.androids.photoalbum.utils.Utils;
import com.itcast.logic.MainService;
import com.itcast.logic.Task;
import com.itcast.logic.TaskType;
import com.itcast.logic.WeiboActivity;

public class NewWeiboActivity extends WeiboActivity {
    private String imagePath;

    private EditText etBlog;

    private ImageButton btBack;

    private Button btSend;

    // private Button btPic;
    private Button btGPS;

    private ImageView ivpic;

    private byte picdat[];// ͼƬ�ֽ���

    private static final int BT_TEXT = 1;// ����΢��

    private static final int BT_PIC = 2;// ͼƬ΢��

    private static final int BT_GPS = 3;// GPS΢��

    private static final int BT_PIC_GPS = 4;// ͼƬ��GPS΢��

    private double gpspoint[];

    private Bitmap mBitmap;

    private int blogType = BT_TEXT;// ΢������

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.sina_edit_layout);
        Runnable run = new Runnable() {

            @Override
            public void run() {
                try {
                    picdat = Upload.readFileImage(MessageActivity.imagePath);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(picdat, 0, picdat.length);

                    if (mBitmap != null && !mBitmap.isRecycled()) {
                        mBitmap.recycle();
                    }
                    mBitmap = Utils.getResizedBitmap(bitmap,
                            getResources().getDimensionPixelSize(R.dimen.blog_width),
                            getResources().getDimensionPixelSize(R.dimen.blog_height));

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    picdat = bos.toByteArray();

                    if (bitmap != null && !bitmap.isRecycled()) {
                        bitmap.recycle();
                    }

                    Runnable action = new Runnable() {

                        @Override
                        public void run() {
                            ivpic.setImageBitmap(mBitmap);
                            ivpic.setVisibility(View.VISIBLE);
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

        imagePath = getIntent().getStringExtra("imagePath");
        // ivpic=(ImageView)this.findViewById(R.id.ivCameraPic);
        ivpic = (ImageView)this.findViewById(R.id.btGallery);
        ivpic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity.getGalleryPicture(NewWeiboActivity.this);
                // TODO Auto-generated method stub
                // Intent it=new Intent("android.media.action.IMAGE_CAPTURE");
                // NewWeiboActivity.this.startActivityForResult(it, 0);
                // Intent it=new
                // Intent(NewWeiboActivity.this,CamerActivity.class);
                // NewWeiboActivity.this.startActivityForResult(it,22);
            }
        });
        // btGPS=(Button)this.findViewById(R.id.btGPS);
        // btGPS.setOnClickListener(new OnClickListener()
        // {
        //
        // @Override
        // public void onClick(View v) {
        // gpspoint=GPSPoint.getGPSPoint(NewWeiboActivity.this);
        // Toast.makeText(NewWeiboActivity.this, "��ȡ��ǰλ��\n����"
        // +gpspoint[0]+"\nγ��"+gpspoint[1],500).show();
        // blogType=BT_GPS;
        // }
        //
        // }
        // );

        View title = this.findViewById(R.id.title);
        btBack = (ImageButton)findViewById(R.id.title_bt_left);
        // btBack.setBackgroundResource(R.drawable.title_back);
        // btBack.setText("����");
        btBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        etBlog = (EditText)this.findViewById(R.id.etBlog);
        etBlog.setText("#彩信Android版#");
        btSend = (Button)findViewById(R.id.title_bt_right);
        // btSend.setBackgroundResource(R.drawable.title_new);
        btSend.setText("发送");
        btSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etBlog.length() > 140) {
                    Toast.makeText(NewWeiboActivity.this, "uploading..", 400).show();
                    return;
                }
                HashMap hm = new HashMap();
                hm.put("msg", etBlog.getText().toString());
                int tsType = 0;
                switch (blogType) {
                    case BT_TEXT:
                        tsType = TaskType.TS_NEW_WEIBO;
                        break;
                    case BT_PIC:
                        tsType = TaskType.TS_NEW_WEIBO_PIC;
                        hm.put("picdata", picdat);
                        break;
                    case BT_GPS:
                        tsType = TaskType.TS_NEW_WEIBO_GPS;
                        hm.put("lat", gpspoint[0]);
                        hm.put("lon", gpspoint[1]);
                        break;
                    case BT_PIC_GPS:
                        tsType = TaskType.TS_NEW_WEIBO_PIC_GPS;
                        hm.put("picdata", picdat);
                        hm.put("lat", 0);
                        hm.put("lon", 0);
                        break;
                }
                Task ts = new Task(tsType, hm);
                MainService.newTask(ts);// ��ӷ���΢��������
            }

        });
        // ��������
        final TextView tvlabel = (TextView)this.findViewById(R.id.tvLabel);
        tvlabel.setText((140 - etBlog.length()) + "/140");
        //
        etBlog.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvlabel.setText(140 - etBlog.length() + "/140");
            }
        });
    }

    @Override
    public void init() {
        // TODO Auto-generated method stub

    }

    @Override
    public void refresh(Object... param) {
        // TODO Auto-generated method stub
        int type = (Integer)param[0];
        int result = (Integer)param[1];
        if (result == 1) {
            Toast.makeText(this, "΢������ɹ�", 500).show();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        // Bitmap bmp=(Bitmap)data.getExtras().get("data");

        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            ContentResolver cr = this.getContentResolver();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                if (mBitmap != null && !mBitmap.isRecycled()) {
                    mBitmap.recycle();
                }
                mBitmap = Utils.getResizedBitmap(bitmap,
                        getResources().getDimensionPixelSize(R.dimen.blog_width), getResources()
                                .getDimensionPixelSize(R.dimen.blog_height));
                if (bitmap != null) {
                    bitmap.recycle();
                }
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
//            byte dat[] = data.getByteArrayExtra("pic");
//            Log.d("ok", "----------------ok1" + dat.length);
            // Bitmap bm=BitmapFactory.decodeByteArray(dat, 0,dat.length);
            ivpic.setImageBitmap(mBitmap);
            ivpic.setVisibility(View.VISIBLE);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            this.picdat = bos.toByteArray();// ��ͼƬת��Ϊ�ֽ�����
            this.blogType = BT_PIC;
        }

    }

}
