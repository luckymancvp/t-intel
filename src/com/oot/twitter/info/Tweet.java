/**
 * 
 */
package com.oot.twitter.info;

import org.json.JSONObject;

/**
 * @author luckymancvp
 *
 */
public class Tweet {
	private String tweet_id, content, created_time, parent_id, user_id, username, full_name, avatar, refav, retweeted_by;
	private boolean isFavorited = false, isRetweeted = false;

	public String getTweet_id() {
		return tweet_id;
	}

	public void setTweet_id(String tweet_id) {
		this.tweet_id = tweet_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getRefav() {
		return refav;
	}

	public void setRefav(String refav) {
		this.refav = refav;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCreated_time() {
		return created_time;
	}

	public void setCreated_time(String created_time) {
		this.created_time = created_time;
	}

	public String getParent_id() {
		return parent_id;
	}

	public void setParent_id(String parent_id) {
		this.parent_id = parent_id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFull_name() {
		return full_name;
	}

	public void setFull_name(String full_name) {
		this.full_name = full_name;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public boolean isFavorited() {
		return isFavorited;
	}

	public void setFavorited(boolean isFavorited) {
		this.isFavorited = isFavorited;
	}

	public boolean isRetweeted() {
		return isRetweeted;
	}

	public void setRetweeted(boolean isRetweeted) {
		this.isRetweeted = isRetweeted;
	}
	
	public Tweet(String jsonString){
	}
	
	public Tweet(JSONObject jsonObject){
		try{
			tweet_id     = jsonObject.getString("tweet_id");
			content      = jsonObject.getString("content");
			parent_id    = jsonObject.getString("parent_id");
			created_time = jsonObject.getString("created_time");
			user_id      = jsonObject.getString("user_id");
			full_name    = jsonObject.getString("full_name");
			username     = jsonObject.getString("username");
			avatar       = jsonObject.getString("avatar");
			retweeted_by = jsonObject.getString("retweeted_by");
			refav        = jsonObject.getString("refav");
			
			// Calc favorite and retweet value
			int refavVal = Integer.parseInt(refav);
			if (refavVal % 10 == 1) isRetweeted = true;
			if (refavVal / 10 == 1) isFavorited = true;
		}catch (Exception e){
			System.out.println("Error in construct Tweet with json object");
		}
	}
}
