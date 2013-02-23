package com.androids.photoalbum.tab.ui;

import com.androids.photoalbum.view.MainTabActivity;

import android.app.ActivityGroup;
import android.os.Bundle;

public class HomeActivityGroup extends TabActivityGroup {
	public static ActivityGroup group;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		group = this;
		MainTabActivity.mHomeActivityGroup = this;
	}
}