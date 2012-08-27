package com.oot.twitter.info;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class User {
	protected String user_id, username, full_name, avatar, status;

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public Bitmap getAvatarBitmap() {
		Bitmap bitmap = null;
    	try {
    		bitmap = BitmapFactory.decodeStream((InputStream)new URL(getAvatar()).getContent());
		} catch (MalformedURLException e) {
			System.out.println("Error in decode avatar bitmap");
		} catch (IOException e) {
			System.out.println("Error in io avatar bitmap");
		}
    	return bitmap;
	}
	
	public User(){
		
	}
	
	public User(JSONObject jsonObject) {
		try {
			user_id   = jsonObject.getString("user_id");
			username  = jsonObject.getString("username");
			full_name = jsonObject.getString("full_name");
			avatar    = jsonObject.getString("avatar");
			try{
                status    = jsonObject.getString("status");
            }catch(Exception e){

            }
		} catch (JSONException e) {
			System.out.println("Error in parse User json");
		}
	}
}
