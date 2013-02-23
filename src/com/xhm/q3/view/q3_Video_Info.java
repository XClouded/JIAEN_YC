package com.xhm.q3.view;

import java.io.Serializable;

public class q3_Video_Info implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mId, mClassid, mPath, mPic_Path, mName, mDescrib, mShare,
			mKeep, mShow, mTime, mSize;

	public String getmId() {
		return mId;
	}

	public void setmId(String mId) {
		this.mId = mId;
	}

	public String getmClassid() {
		return mClassid;
	}

	public void setmClassid(String mClassid) {
		this.mClassid = mClassid;
	}

	public String getmPath() {
		return mPath;
	}

	public void setmPath(String mPath) {
		this.mPath = mPath;
	}

	public String getmPic_Path() {
		return mPic_Path;
	}

	public void setmPic_Path(String mPic_Path) {
		this.mPic_Path = mPic_Path;
	}

	public String getmDescrib() {
		return mDescrib;
	}

	public void setmDescrib(String mDescrib) {
		this.mDescrib = mDescrib;
	}

	public String getmShare() {
		return mShare;
	}

	public void setmShare(String mShare) {
		this.mShare = mShare;
	}

	public String getmName() {
		return mName;
	}

	public void setmName(String mName) {
		this.mName = mName;
	}

	public String getmKeep() {
		return mKeep;
	}

	public void setmKeep(String mKeep) {
		this.mKeep = mKeep;
	}

	public String getmShow() {
		return mShow;
	}

	public void setmShow(String mShow) {
		this.mShow = mShow;
	}

	public String getmTime() {
		return mTime;
	}

	public void setmTime(String mTime) {
		this.mTime = mTime;
	}

	public String getmSize() {
		return mSize;
	}

	public void setmSize(String mSize) {
		this.mSize = mSize;
	}

}
