package com.xhm.q3.view;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.photoalbum.R;
import com.betterman.util.RGBLuminanceSource;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;

public class q3_erweima_activity extends Activity implements OnClickListener {
	private Button mButton_bendi, mButton_saomiao;
	private ImageView mImageView_pic;
	private TextView mTextView_info;
	private final static int QR_WIDTH = 200, QR_HEIGHT = 200;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.q3_erweima_layout);
		initView();
	}

	private void initView() {
		mButton_bendi = (Button) findViewById(R.id.q3_erweima_bendi);
		mButton_saomiao = (Button) findViewById(R.id.q3_erweima_saomiao);
		mButton_bendi.setOnClickListener(this);
		mButton_saomiao.setOnClickListener(this);
		mImageView_pic = (ImageView) findViewById(R.id.q3_erweima_pic);
		mTextView_info = (TextView) findViewById(R.id.q3_erweima_info);
		mImageView_pic.setOnClickListener(this);
		mTextView_info.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.q3_erweima_bendi:
			intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intent, 1);
			break;
		case R.id.q3_erweima_saomiao:
			intent = new Intent(this, CaptureActivity.class);
			startActivity(intent);
			break;
		case R.id.q3_erweima_info:
			System.out.println("info");
			createImage(mTextView_info, mImageView_pic);
			break;
		case R.id.q3_erweima_pic:
			System.out.println("pic");
			scanningImage(mImageView_pic, mTextView_info);
			break;
		default:
			break;
		}
	}

	// 生成QR图
	private void createImage(TextView qr_text, ImageView qr_image) {
		try {
			QRCodeWriter writer = new QRCodeWriter();

			String text = qr_text.getText().toString();

			if (text == null || "".equals(text) || text.length() < 1) {
				return;
			}

			BitMatrix martix = writer.encode(text, BarcodeFormat.QR_CODE,
					QR_WIDTH, QR_HEIGHT);

			System.out.println("w:" + martix.getWidth() + "h:"
					+ martix.getHeight());

			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			BitMatrix bitMatrix = new QRCodeWriter().encode(text,
					BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
			int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
			for (int y = 0; y < QR_HEIGHT; y++) {
				for (int x = 0; x < QR_WIDTH; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * QR_WIDTH + x] = 0xff000000;
					} else {
						pixels[y * QR_WIDTH + x] = 0xffffffff;
					}

				}
			}

			Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT,
					Bitmap.Config.ARGB_8888);

			bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
			qr_image.setImageBitmap(bitmap);

		} catch (WriterException e) {
			e.printStackTrace();
		}
	}

	// 解析QR图片
	private void scanningImage(ImageView qr_image, TextView qr_result) {
		System.out.println("scann");
		Map<DecodeHintType, String> hints = new HashMap<DecodeHintType, String>();
		hints.put(DecodeHintType.CHARACTER_SET, "utf-8");

		Bitmap bitmap = ((BitmapDrawable) qr_image.getDrawable()).getBitmap();
		RGBLuminanceSource source = new RGBLuminanceSource(bitmap);
		BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
		QRCodeReader reader = new QRCodeReader();
		Result result = null;
		System.out.println("result");
		try {
			result = reader.decode(bitmap1, hints);
			System.out.println("res：》》》》》》》：" + result.getText());
			qr_result.setText(result.getText());
			// 添加联系人
			// String[] info = result.getText().split("：");
			// AddContact(info[1], info[2], info[3]);
			System.out.println("settext");
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (ChecksumException e) {
			e.printStackTrace();
		} catch (FormatException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			Uri uri = data.getData();
			ContentResolver cr = getContentResolver();
			Cursor cursor = cr.query(uri, null, null, null, null);
			try {
				InputStream in = cr.openInputStream(uri);
				Bitmap bitmap = BitmapFactory.decodeStream(in);
				mImageView_pic.setImageBitmap(bitmap);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	/**
	 * @param 联系人对象
	 */
	public void AddContact(String name, String phone, String email) {
		ContentResolver resolver = getContentResolver();
		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		ContentValues values = new ContentValues();
		// 向raw_contacts插入一条除ID外全部为null的数据，ID自动生成
		long id = ContentUris.parseId(resolver.insert(uri, values));
		// 添加联系人姓名
		uri = Uri.parse("content://com.android.contacts/data");
		values.put("raw_contact_id", id);
		values.put("data2", name);
		values.put("mimetype", "vnd.android.cursor.item/name");
		resolver.insert(uri, values);
		// 添加联系人电话
		values.clear(); // 清空上次的数据
		values.put("raw_contact_id", id);
		values.put("data1", phone);
		values.put("data2", "2");
		values.put("mimetype", "vnd.android.cursor.item/phone_v2");
		resolver.insert(uri, values);
		// 添加联系人邮箱
		values.clear();
		values.put("raw_contact_id", id);
		values.put("data1", email);
		values.put("data2", "1");
		values.put("mimetype", "vnd.android.cursor.item/email_v2");
		resolver.insert(uri, values);
		Toast.makeText(this, "联系人添加成功！", Toast.LENGTH_LONG).show();
	}
}
