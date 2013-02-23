package com.androids.photoalbum.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.android.photoalbum.R;
import com.androids.photoalbum.parser.SearchWebsiteParser;
import com.androids.photoalbum.parser.WebsiteParser;
import com.androids.photoalbum.tab.ui.BaseActivity;
import com.androids.photoalbum.utils.ListViewWebsiteParserTask;
import com.androids.photoalbum.utils.NetWorkUtils;

public class HomeDownloadTopActivity extends BaseActivity {
	private String classid = null;
	private String optype = null;
//	private ListView mListView;
	
	@Override
	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		
		setView(R.layout.home_download_top_layout);
		setTitleBar(com.androids.photoalbum.view.BaseLayout.TYPE_NORMAL, "", "", "返回");
		mListView = (ListView)findViewById(R.id.list_view);
	    Intent intent = getIntent();
	    if (intent != null) {
	    	classid = intent.getStringExtra("classid");
	    	Log.d("zheng", "mConclassId: " + classid);
	    	optype = intent.getStringExtra("optype");
//	    	String parentid = intent.getStringExtra("parentid");
//	    	if (classid != null && optype != null && parentid != null) {
//	    		new ConclassParserTask(this).execute(WebsiteParser.PARENTCLASS, parentid, null);
//	    	}
	    	String urlParam = null;
	    	int pageindex = (Integer)mFooterView.getTag();
	    	boolean isupdate = intent.getBooleanExtra("is_latest_update", false);
	    	if (isupdate) {
	    		urlParam = "?productid=&parentclass=&sonclass=&addtime=&pageindex=" + pageindex + "&pagecount=30&ordertype=Hits";
	    		new ListViewWebsiteParserTask(this, new SearchWebsiteParser(), mListView, 4, mImageWidth, mImageHeigth, true).execute(NetWorkUtils.GET_PRODUCTS + urlParam);
	    		return;
	    	}
	    	if (WebsiteParser.ALL.equals(optype)) {
	    		urlParam = "?productid=&parentclass=&sonclass=&addtime=&pageindex=" + pageindex + "&pagecount=30&ordertype=DownHits";
	    		new ListViewWebsiteParserTask(this, new SearchWebsiteParser(), mListView, 4, mImageWidth, mImageHeigth, true).execute(NetWorkUtils.GET_PRODUCTS + urlParam);
	    	} else if (WebsiteParser.PARENTCLASS.equals(optype)) {
	    		urlParam = "?productid=&parentclass=" + classid + "&sonclass=&addtime=&pageindex=" + pageindex + "&pagecount=30&ordertype=DownHits";
	    		new ListViewWebsiteParserTask(this, new SearchWebsiteParser(), mListView, 4, mImageWidth, mImageHeigth, true).execute(NetWorkUtils.GET_PRODUCTS + urlParam);
	    	} else if (WebsiteParser.SINGLE.equals(optype)) {
	    		urlParam = "?productid=&parentclass=&sonclass=" + classid + "&addtime=&pageindex=" + pageindex + "&pagecount=30&ordertype=DownHits";
	    		new ListViewWebsiteParserTask(this, new SearchWebsiteParser(), mListView, 4, mImageWidth, mImageHeigth, true).execute(NetWorkUtils.GET_PRODUCTS + urlParam);
	    	}
	    }
	}

	@Override
	protected void handleTitleBarEvent(int paramInt) {
		switch (paramInt) {
			case BaseActivity.RIGHT_BUTTON:
				MainTabActivity.mHomeActivityGroup.setPreviousActivity();
				break;
			default:
				break;
		}
	}
}