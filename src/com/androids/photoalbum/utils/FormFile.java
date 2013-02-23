package com.androids.photoalbum.utils;

import java.io.InputStream;

public class FormFile {
	private String formname;
	private InputStream inStream;
	private String contentType;
	private byte[] data;
	private String filname;
	private long fileLength;

	public String getFormname() {
		return formname;
	}

	public void setFormname(String formname) {
		this.formname = formname;
	}

	public InputStream getInStream() {
		return inStream;
	}

	public void setInStream(InputStream inStream) {
		this.inStream = inStream;
	}
	
	public void setFileLength(long fileLength) {
		this.fileLength = fileLength;
	}
	
	public long getFileLength() {
		return fileLength;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getFilname() {
		return filname;
	}

	public void setFilname(String filname) {
		this.filname = filname;
	}

	public byte[] getData() { 
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

}