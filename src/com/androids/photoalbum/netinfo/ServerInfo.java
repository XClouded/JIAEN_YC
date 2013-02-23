package com.androids.photoalbum.netinfo;

import java.io.InputStream;

// use to cache the info get from server
public class ServerInfo {
    public int mStatusCode = -1;
    public InputStream mServerInputStream;
    
	public final static String SUCCESS = "succ";
	public final static String FAIL = "fail";
	public final static String CONNECT_FAIL = "与服务器连接失败";
	public final static String UNKNOWN_FAIL = "发生未知异常";
}
