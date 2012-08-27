package com.oot.twitter.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oot.twitter.GlobalData;
import com.oot.twitter.NewTweetDialog;
import com.oot.twitter.R;
import com.oot.twitter.Twitter;

public class BaseActivity extends Activity {

	@Override
	public void setContentView(int layoutResID) {
		
		super.setContentView(layoutResID);
		// Add header action
		LinearLayout twitter_search, twitter_new_tweet;
		try{
			twitter_search = (LinearLayout)findViewById(R.id.header_search);
	        twitter_new_tweet = (LinearLayout)findViewById(R.id.header_new_tweet);
		}catch(Exception e){
			System.out.println("Can't find search and new tweet layout element");
			return;
		}
        
		// listener for search button
        twitter_search.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				System.out.println("Search aciton here");
				//Intent i = new Intent(BaseActivity.this,NewTweetDialog.class);
				//startActivity(i);
			};
		});
        
        // listener for new tweet button
        twitter_new_tweet.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent i = new Intent(BaseActivity.this,NewTweetDialog.class);
				startActivity(i);
			};
		});
        
        // Set Header Text
        try{
	        TextView headerText  = (TextView)findViewById(R.id.header_text);
	        headerText.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// Back to home screen
					Intent home = new Intent(BaseActivity.this, Twitter.class);
					startActivity(home);
				}
			});
	        headerText.setText(((SimpleURI)GlobalData.paramIntent).getHeader());
        }catch(Exception e){
        	System.out.println("Can't set header text");
        }
	}
}
