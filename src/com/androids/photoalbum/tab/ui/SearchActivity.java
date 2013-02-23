package com.androids.photoalbum.tab.ui;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.photoalbum.R;
import com.androids.photoalbum.parser.SearchWebsiteParser;
import com.androids.photoalbum.utils.ListViewWebsiteParserTask;
import com.androids.photoalbum.utils.NetWorkUtils;

public class SearchActivity extends BaseActivity {
	private AutoCompleteTextView mAutoText;
	private TextView mSearchButton;

	@Override
	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setView(R.layout.search_layout);
		mListView = (ListView) findViewById(R.id.list_view);
		setTitleBar(com.androids.photoalbum.view.BaseLayout.TYPE_GONE, null,
				null, null);
		mProductList = (LinearLayout) findViewById(R.id.product_list);
		mAutoText = (AutoCompleteTextView) findViewById(R.id.search_field);
		mSearchButton = (TextView) findViewById(R.id.search_button);
		mSearchButton.setOnClickListener(this);
	}

	@Override
	protected void handleTitleBarEvent(int paramInt) {

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.search_button:
			String searchTxt = mAutoText.getEditableText().toString();
			if (searchTxt == null || searchTxt.length() == 0) {
				notifyToUser("请输入搜索内容！");
				return;
			}

			startSearching(searchTxt);
			break;

		default:
			break;
		}
		super.onClick(view);
	}

	private void startSearching(String searchTxt) {
		try {
			searchTxt = URLEncoder.encode(searchTxt, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String url = NetWorkUtils.GET_PRODUCTS_SELECT + "?keywords="
				+ searchTxt;
		// new WebsiteParserTask(new SearchWebsiteParser(), this).execute(url);
		new ListViewWebsiteParserTask(this, new SearchWebsiteParser(),
				mListView, mLineCount, mImageWidth, mImageHeigth, true)
				.execute(url);
	}
}