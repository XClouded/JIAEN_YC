/*
 * UserObjectWapper.java created on 2010-7-28 上午08:48:35 by bwl (Liu Daoru)
 */

package com.itcast.weibo.sina;

import java.io.Serializable;
import java.util.List;

/**
 * 对User对象列表进行的包装，以支持cursor相关信息传�?
 * @author liudaoru - daoru at sina.com.cn
 */
public class UserWapper implements Serializable {

	private static final long serialVersionUID = -3119107701303920284L;

	/**
	 * 用户对象列表
	 */
	private List<User> users;

	/**
	 * 向前翻页的cursor
	 */
	private long previousCursor;

	/**
	 * 向后翻页的cursor
	 */
	private long nextCursor;

	public UserWapper(List<User> users, long previousCursor, long nextCursor) {
		this.users = users;
		this.previousCursor = previousCursor;
		this.nextCursor = nextCursor;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public long getPreviousCursor() {
		return previousCursor;
	}

	public void setPreviousCursor(long previousCursor) {
		this.previousCursor = previousCursor;
	}

	public long getNextCursor() {
		return nextCursor;
	}

	public void setNextCursor(long nextCursor) {
		this.nextCursor = nextCursor;
	}

}
