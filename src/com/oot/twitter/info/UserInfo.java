package com.oot.twitter.info;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class UserInfo extends User{
	private String location, website, bio;

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}
	
	public UserInfo(JSONObject jsonObject) {
		try {
			user_id   = jsonObject.getString("user_id");
			username  = jsonObject.getString("username");
			full_name = jsonObject.getString("full_name");
			avatar    = jsonObject.getString("avatar");
			location  = jsonObject.getString("location");
			website   = jsonObject.getString("website");
			bio       = jsonObject.getString("bio");
			status    = jsonObject.getString("status");
		} catch (JSONException e) {
			System.out.println("Error in parse User json");
		}
	}
	
}
