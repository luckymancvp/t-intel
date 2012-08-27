package com.oot.twitter.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import com.oot.twitter.ConnectionTwitter;
import com.oot.twitter.ImageHelper;
import com.oot.twitter.R;
import com.oot.twitter.info.User;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class UsersListAdapter extends ArrayAdapter<User>{
	public Context context;
	public ArrayList<User> users;
	public int layoutId;
	

	public UsersListAdapter(Context context, int textViewResourceId, ArrayList<User> objects) {
		super(context, textViewResourceId, objects);
		this.context  = context;
		this.users    = objects;
		this.layoutId = textViewResourceId;
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		if (v == null){
			LayoutInflater vi = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(layoutId, null);
		}
		
		final User user = users.get(position);
		if (user!=null) {
			TextView username  = (TextView)v.findViewById(R.id.item_username);
			TextView full_name  = (TextView)v.findViewById(R.id.item_full_name);
			ImageView avatar   = (ImageView)v.findViewById(R.id.item_avarta);
			final ImageButton button = (ImageButton)v.findViewById(R.id.item_unfollow);

			full_name.setText(user.getFull_name());
			username.setText("@" + user.getUsername());
			avatar.setImageBitmap(user.getAvatarBitmap());

			if (button == null )
				return v;
			if (user.getStatus() != "1"){
				button.setImageResource(R.drawable.ic_follow_default);
				button.setBackgroundResource(R.drawable.btn_default);
			}
			// Button Follow and UnFollow
			button.setFocusable(false);
			button.setFocusableInTouchMode(false);
			button.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					// param
					String requestString = null;
					if (user.getStatus() == "1") 
						requestString = ConnectionTwitter.UNFOLLOW_URL;
					else
						requestString = ConnectionTwitter.FOLLOW_URL;
					
			    	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			    	nameValuePairs.add(new BasicNameValuePair("follow_id", user.getUser_id()));
			    	String result = ConnectionTwitter.sendRequest(requestString, nameValuePairs);
			    	System.out.println(result);
			    	// Base on result change current interface
			    	if (result.equals("{\"result\":1}")){
			    		if (user.getStatus() == "1") {
			    			user.setStatus("0");
			    			button.setImageResource(R.drawable.ic_follow_default);
							button.setBackgroundResource(R.drawable.btn_default);
			    		}
			    		else{
			    			user.setStatus("1");
			    			button.setImageResource(R.drawable.ic_follow_checked);
							button.setBackgroundResource(R.drawable.btn_active_default);
			    		}
			    	}
			    	
				}
			});
			
		}
		
		return v;
	}

	
}
