package com.oot.twitter;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.oot.twitter.adapter.UsersListAdapter;
import com.oot.twitter.base.BaseActivity;
import com.oot.twitter.base.SimpleURI;
import com.oot.twitter.info.User;

public class UsersList extends BaseActivity implements OnItemClickListener {
	private ArrayList<User> users = new ArrayList<User>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.userslist);
        getDataFromServer();
    }

    private void getDataFromServer() {
    	// Get Param From other intent 
    	SimpleURI uri = (SimpleURI) GlobalData.paramIntent;
    	
    	JSONArray jsonArray = ConnectionTwitter.sendRequestJSonArray(uri.getUrl(), uri.getParam());
    	
    	try{
	    	for (int i=0; i< jsonArray.length(); i++){
	        	users.add(new User((JSONObject)jsonArray.get(i)));
	    	}
    	}catch(Exception e){
    		System.out.println("Error in loop parse users list");
    		return ;
    	}
    	
    	UsersListAdapter ula = new UsersListAdapter(this, R.layout.user_item, users);
    	ListView list = (ListView)findViewById(R.id.users_list);
    	list.setAdapter(ula);
    	list.setOnItemClickListener(this);
    }

	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		User user = users.get(position);
		
		Intent showInfo = new Intent(UsersList.this, OtherPersonProfile.class);
		Bundle param    = new Bundle();
		/**
		 * TODO
		 * Tranfer param to Ngoc's Intent
		 */
		param.putString("id", user.getUser_id());
		param.putString("username", user.getUsername());
		param.putString("fullname", user.getFull_name());
		
		
		showInfo.putExtras(param);
		showInfo.putExtra("avarta", user.getAvatarBitmap());
		startActivity(showInfo);
		
		
		System.out.println(user.getUser_id());
	}
}
