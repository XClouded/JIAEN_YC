package com.androids.photoalbum.utils;

import java.util.ArrayList;

import android.content.Intent;

public class Stack {
	ArrayList<Intent> mStack = new ArrayList<Intent>();
	
	public Intent pop() {
		int size = mStack.size();
		if (size == 0) {
			return null;
		}
		
		Intent intent = mStack.get(size - 1);
		mStack.remove(size - 1);
		return intent;
	}
	
	public void push(Intent intent) {
		mStack.add(intent);
	}
	
	public int size() {
		return mStack.size();
	}
	
	public void clear() {
		mStack.clear();
	}
}