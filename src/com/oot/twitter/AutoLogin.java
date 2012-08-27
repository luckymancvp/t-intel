package com.oot.twitter;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.oot.twitter.info.User;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

public class AutoLogin extends Activity {

	private String username = "ngoc";
	private String password = "oot";
	private String user_id  = "2";
	
	private Class targetClass = NewTweetDialog.class;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        login();
    }

    private void login() {
    	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
    	nameValuePairs.add(new BasicNameValuePair("username", username));
    	nameValuePairs.add(new BasicNameValuePair("password", password));
    	
    	JSONObject json = ConnectionTwitter.sendRequestJSon(ConnectionTwitter.LOGIN_URL, nameValuePairs);
    	try{
	    	if (!json.getString("result").equals("1")){
	    		System.out.println("Login fail");
	    		return;
	    	}
	    	
	    	GlobalData.currentUser = new User(json.getJSONObject("user"));
    	}catch(Exception e){
    		System.out.println("Error parsing json when login");
    		return;
    	}
    	
    	Intent target = new Intent(AutoLogin.this, targetClass);
    	startActivity(target);
    }
    
}
