package com.oot.twitter;
import com.markupartist.android.widget.*;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.support.v4.app.NavUtils;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class Twitter extends Activity implements OnClickListener {

	ViewGroup parent;
	LinearLayout twitter_home,twitter_connect,twitter_discover,twitter_me;
	LinearLayout twitter_search,twitter_new_tweet;
	ImageView image_tab_home,image_tab_connect,image_tab_discover,image_tab_me;
	TextView text_home,text_connect,text_discover,text_me;
	
	public ArrayList<TweetItem> m_tweet_item =null;
	public TimeLineListAdapter m_adapter;
	
	PullToRefreshListView list;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_twitter);
        
        twitter_home = (LinearLayout)findViewById(R.id.twitter_home);
        twitter_connect = (LinearLayout)findViewById(R.id.twitter_connect);
        twitter_discover = (LinearLayout)findViewById(R.id.twitter_discover);
        twitter_me = (LinearLayout)findViewById(R.id.twitter_me);
        twitter_search = (LinearLayout)findViewById(R.id.twitter_search);
        twitter_new_tweet = (LinearLayout)findViewById(R.id.twitter_new_tweet);
        
        
        
        twitter_home.setOnClickListener(this);
        twitter_connect.setOnClickListener(this);
        twitter_discover.setOnClickListener(this);
        twitter_me.setOnClickListener(this);
        twitter_search.setOnClickListener(this);
        twitter_new_tweet.setOnClickListener(this);
        
        image_tab_home = (ImageView)findViewById(R.id.image_tab_home);
        image_tab_connect = (ImageView)findViewById(R.id.image_tab_connect);
        image_tab_discover = (ImageView)findViewById(R.id.image_tab_discover);
        image_tab_me = (ImageView)findViewById(R.id.image_tab_me);
        
        text_home = (TextView)findViewById(R.id.text_tab_home);
        text_connect = (TextView)findViewById(R.id.text_tab_connect);
        text_discover = (TextView)findViewById(R.id.text_tab_discover);
        text_me = (TextView)findViewById(R.id.text_tab_me);
        
        
        initializeUI();
        getUserID();
    }
    
    public void initializeUI() //Initialize UI and Data
    {
    	parent = (ViewGroup)findViewById(R.id.tabbar);
    	parent.removeAllViews();
    	View v = LayoutInflater.from(getBaseContext()).inflate(R.layout.twitter_home_layout, parent, true);
    	
    	
    	
    	//Get data from Server
    	StringBuilder builder = new StringBuilder();
    	
    	HttpGet httpPost = new HttpGet("http://192.168.1.10/twitter/api/tweet/home_timeline");
    	try{
    			HttpResponse response = GlobalData.client.execute(httpPost);
    			StatusLine statusLine = response.getStatusLine();
    		
    			int statusCode = statusLine.getStatusCode();
    			if (statusCode == 200) {
    				HttpEntity entity = response.getEntity();
    				InputStream content = entity.getContent();
    				BufferedReader reader = new BufferedReader(
    						new InputStreamReader(content));
    				String line;
    				while ((line = reader.readLine()) != null) {
    					builder.append(line);
    				}
    			} else {
    				Log.e(Twitter.class.toString(), "Failed to download JSON string");
    			}			
    		
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}					
		
    	//System.out.println(builder.toString());    	
    	
    	
    	m_tweet_item = new ArrayList<TweetItem>();
    	
    	try{
    		JSONArray jsonArray = new JSONArray(builder.toString());
    		//System.out.println("Length = "+jsonArray.length());
    		for(int i = 0;i < jsonArray.length(); i++)
    		{
    			JSONObject jsonObject = jsonArray.getJSONObject(i);
    			Bitmap bitmap = null;
    			//System.out.println(jsonObject.getString("tweet_id")+"|"+jsonObject.getString("content")+"|"+jsonObject.getString("created_time")+"|"+jsonObject.getString("username")+"|"+jsonObject.getString("full_name")+jsonObject.getString("avatar"));
   			 	try {
   			 		bitmap = BitmapFactory.decodeStream((InputStream)new URL(jsonObject.getString("avatar")).getContent());
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
   			 	if(i == 0)
   			 		GlobalData.timeline_maxid = jsonObject.getInt("tweet_id");
   			 	if(i == jsonArray.length() - 1)
   			 		GlobalData.timeline_sinceid = jsonObject.getInt("tweet_id");
   			 	
   			 	
    			TweetItem tweet_item = new TweetItem(jsonObject.getString("tweet_id"), jsonObject.getString("content"), jsonObject.getString("created_time"), jsonObject.getString("username"), jsonObject.getString("full_name"),bitmap ,jsonObject.getString("refav"));
    			m_tweet_item.add(tweet_item);
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	
    	System.out.println("MIN ID = "+ GlobalData.timeline_sinceid);
    	
    	this.m_adapter = new TimeLineListAdapter(this, R.layout.tweet_item, m_tweet_item);
        list = (PullToRefreshListView)v.findViewById(R.id.timeline_list);
        list.setOnRefreshListener(new OnRefreshListener() {
            public void onRefresh() {
                // Do work to refresh the list here.
            	new GetNewData().execute(new String[]{"ABCD"});
            }
        });
        
    	list.setAdapter(m_adapter);
    	
    	
    	
    	
    	
    	
    	list.setOnScrollListener(new OnScrollListener() {	//Load More item when endless scroll	
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				
			}
			
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				boolean loadmore = (firstVisibleItem + visibleItemCount >= totalItemCount);
				

				//System.out.println("SINCE ID = "+GlobalData.timeline_sinceid);
				//System.out.println("TOTAL ITEM = "+ totalItemCount + loadmore);
				if(loadmore && GlobalData.isGetMore)
				{
					System.out.println("MORE ITEM");
					// TODO Auto-generated method stub
					getMoreData();
					m_adapter.notifyDataSetChanged();
					list.onRefreshComplete();
				}				
			}
		});
    	
    	
    	
    	
    }
    
    public void getMoreData()
    {
    	StringBuilder builder = new StringBuilder();
    	
    	HttpGet httpPost = new HttpGet("http://192.168.1.10/twitter/api/tweet/home_timeline?since_id="+GlobalData.timeline_sinceid);
    	try{
    			HttpResponse response = GlobalData.client.execute(httpPost);
    			StatusLine statusLine = response.getStatusLine();
    		
    			int statusCode = statusLine.getStatusCode();
    			if (statusCode == 200) {
    				HttpEntity entity = response.getEntity();
    				InputStream content = entity.getContent();
    				BufferedReader reader = new BufferedReader(
    						new InputStreamReader(content));
    				String line;
    				while ((line = reader.readLine()) != null) {
    					builder.append(line);
    				}
    			} else {
    				Log.e(Twitter.class.toString(), "Failed to download JSON string");
    			}			
    		
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}		
    	//System.out.println(builder.toString());

    	if(builder.toString().length()>3)
    	{
    		try{
    			JSONArray jsonArray = new JSONArray(builder.toString());
    			System.out.println("Length More = "+jsonArray.length());
    			for(int i = 0;i < jsonArray.length(); i++)
    			{
    				JSONObject jsonObject = jsonArray.getJSONObject(i);
    				Bitmap bitmap = null;
    				//System.out.println(jsonObject.getString("tweet_id")+"|"+jsonObject.getString("content")+"|"+jsonObject.getString("created_time")+"|"+jsonObject.getString("username")+"|"+jsonObject.getString("full_name")+jsonObject.getString("avatar"));
   			 		try {
   			 			bitmap = BitmapFactory.decodeStream((InputStream)new URL(jsonObject.getString("avatar")).getContent());
   			 		} catch (MalformedURLException e) {
   			 			// TODO Auto-generated catch block
   			 			e.printStackTrace();
   			 		} catch (IOException e) {
   			 			// TODO Auto-generated catch block
   			 			e.printStackTrace();
   			 		}
   			 		if( i == jsonArray.length() - 1)
   			 			GlobalData.timeline_sinceid = jsonObject.getInt("tweet_id");
   			 	
   			 		//System.out.println("MIN ID = "+GlobalData.timeline_sinceid);
   			 	
   			 		TweetItem tweet_item = new TweetItem(jsonObject.getString("tweet_id"), jsonObject.getString("content"), jsonObject.getString("created_time"), jsonObject.getString("username"), jsonObject.getString("full_name"),bitmap ,jsonObject.getString("refav"));
   			 		m_tweet_item.add(tweet_item);
    			

    			}
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    	else
    	{
    		GlobalData.isGetMore = false;
    		//System.out.println("END LIST");
    	}
    	
    }
  
    public void getUserID()
    {
		StringBuilder builder = new StringBuilder();
		HttpPost httpPost = new HttpPost("http://192.168.1.10/twitter/api/site/getCurrentUserID");
		
    	try{
    		HttpResponse response = GlobalData.client.execute(httpPost);
    		StatusLine statusLine = response.getStatusLine();
    		
    		int statusCode = statusLine.getStatusCode();
    		

    			HttpEntity entity = response.getEntity();
    			InputStream content = entity.getContent();
    			BufferedReader reader = new BufferedReader(new InputStreamReader(content));
    			String line;
    			while((line= reader.readLine())!= null)
    			{
    				builder.append(line);			
    			}				
    		
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}					
		
    	GlobalData.user_id = builder.toString().trim();
    	System.out.println(builder.toString());		    	
    	
    }
    
    //On Mouse Click Event Handler
    public void onClick(View v) {
		switch(v.getId())
		{
			case R.id.twitter_home:		
				image_tab_home.setImageResource(R.drawable.ic_tab_home_selected);
				image_tab_connect.setImageResource(R.drawable.ic_tab_connect_default);
				image_tab_discover.setImageResource(R.drawable.ic_tab_discover_default);
				image_tab_me.setImageResource(R.drawable.ic_tab_me_default);
				
		    	parent = (ViewGroup)findViewById(R.id.tabbar);
		    	parent.removeAllViews();
		    	View v1 = LayoutInflater.from(getBaseContext()).inflate(R.layout.twitter_home_layout, parent, true);	
		    	
		    	list = (PullToRefreshListView)v1.findViewById(R.id.timeline_list);
		    	list.setAdapter(m_adapter);
		    	
				break;
			case R.id.twitter_connect:
				image_tab_home.setImageResource(R.drawable.ic_tab_home_default);
				image_tab_connect.setImageResource(R.drawable.ic_tab_connect_selected);
				image_tab_discover.setImageResource(R.drawable.ic_tab_discover_default);
				image_tab_me.setImageResource(R.drawable.ic_tab_me_default);	
				
		    	parent = (ViewGroup)findViewById(R.id.tabbar);
		    	parent.removeAllViews();				
				View v2 = LayoutInflater.from(getBaseContext()).inflate(R.layout.twitter_connect_layout, parent, true);	
				
				final Button btnInteractions = (Button)v2.findViewById(R.id.btnTabInteractions);
				final Button btnMentions = (Button)v2.findViewById(R.id.btnTabMentions);
				
				btnInteractions.setOnClickListener(new OnClickListener() {				
					public void onClick(View v) {
						// TODO Auto-generated method stub
						btnInteractions.setBackgroundResource(R.drawable.btn_segmented_selected);
						btnMentions.setBackgroundResource(R.drawable.btn_segmented_default);
					}
				});
				
				btnMentions.setOnClickListener(new OnClickListener() {					
					public void onClick(View v) {
						// TODO Auto-generated method stub
						btnInteractions.setBackgroundResource(R.drawable.btn_segmented_default);
						btnMentions.setBackgroundResource(R.drawable.btn_segmented_selected);						
					}
				});
				
				break;
				
			case R.id.twitter_discover:
				image_tab_home.setImageResource(R.drawable.ic_tab_home_default);
				image_tab_connect.setImageResource(R.drawable.ic_tab_connect_default);
				image_tab_discover.setImageResource(R.drawable.ic_tab_discover_selected);
				image_tab_me.setImageResource(R.drawable.ic_tab_me_default);				
				break;
				
			case R.id.twitter_me:
				image_tab_home.setImageResource(R.drawable.ic_tab_home_default);
				image_tab_connect.setImageResource(R.drawable.ic_tab_connect_default);
				image_tab_discover.setImageResource(R.drawable.ic_tab_discover_default);
				image_tab_me.setImageResource(R.drawable.ic_tab_me_selected);	
				
		    	parent = (ViewGroup)findViewById(R.id.tabbar);
		    	parent.removeAllViews();
		    	View v4 = LayoutInflater.from(getBaseContext()).inflate(R.layout.twitter_me_layout, parent, true);	
		    	
		    	//Get data from API service
		    	StringBuilder return_value = new StringBuilder();
		    	HttpGet httpGet = new HttpGet("http://192.168.1.10/twitter/api/user/show");
		    	try{
		    		HttpResponse response = GlobalData.client.execute(httpGet);
		    		StatusLine statusLine = response.getStatusLine();
	    			HttpEntity entity = response.getEntity();
	    			InputStream content = entity.getContent();
	    			BufferedReader reader = new BufferedReader(new InputStreamReader(content));
	    			String line;
	    			while((line= reader.readLine())!= null)
	    			{
	    				return_value.append(line);			
	    			}			    		
		    		
		    	}
		    	catch(Exception e)
		    	{
		    		e.printStackTrace();
		    	}
		    	String jsonvalue = return_value.toString();
		    	jsonvalue = "["+return_value+"]";
		    	System.out.println(jsonvalue);
		    	
		    	try{
		    		JSONArray json = new JSONArray(jsonvalue);
		    		JSONObject obj = json.getJSONObject(0);
		    		GlobalData.user_id = obj.getString("user_id");
		    		GlobalData.username = obj.getString("username");
		    		GlobalData.fullname = obj.getString("full_name");
		    		GlobalData.avarta = BitmapFactory.decodeStream((InputStream)new URL(obj.getString("avatar")).getContent());
		    		GlobalData.follower = obj.getString("followers_count");
		    		GlobalData.following = obj.getString("following_count");
		    		GlobalData.tweets = obj.getString("tweets_count");
		    		GlobalData.bio = obj.getString("bio");
		    		GlobalData.website = obj.getString("website");
		    		GlobalData.location = obj.getString("location");
		    		//System.out.println("CHANCHANCHANCHAN|"+obj.get("avatar"));
		    	}
		    	catch (Exception e) {
					// TODO: handle exception
		    		e.printStackTrace();
				}
		    	
		    	TextView me_tweets = (TextView)v4.findViewById(R.id.me_tweets);
		    	TextView me_following = (TextView)v4.findViewById(R.id.me_following);
		    	TextView me_follower = (TextView)v4.findViewById(R.id.me_follower);
		    	ImageView me_avarta = (ImageView)v4.findViewById(R.id.me_avarta);
		    	
		    	me_tweets.setText(GlobalData.tweets);
		    	me_following.setText(GlobalData.following);
		    	me_follower.setText(GlobalData.follower);
		    	me_avarta.setImageBitmap(ImageHelper.getRoundedCornerBitmap(GlobalData.avarta, 5));
		    	
		    	LinearLayout tweets_layout = (LinearLayout)v4.findViewById(R.id.me_tweets_layout);
		    	LinearLayout following_layout = (LinearLayout)v4.findViewById(R.id.me_following_layout);
		    	LinearLayout follower_layout = (LinearLayout)v4.findViewById(R.id.me_follower_layout);
		    	
		    	tweets_layout.setOnClickListener(this);
		    	following_layout.setOnClickListener(this);
		    	follower_layout.setOnClickListener(this);
		    	
		    	LinearLayout twitter_me_detail = (LinearLayout)v4.findViewById(R.id.twitter_me_detail);
		    	twitter_me_detail.setOnClickListener(this);
		    	
				break;
				
			case R.id.twitter_search:		
				
				break;
				
			case R.id.twitter_new_tweet:
				Intent i = new Intent(Twitter.this,NewTweetDialog.class);
				startActivity(i);
								
				break;
				
			case R.id.me_tweets_layout:
				Intent tweet_activity = new Intent(Twitter.this,Tweets.class);
				startActivity(tweet_activity);
				break;
			
			case R.id.me_following_layout:
				Intent following_activity = new Intent(Twitter.this,Following.class);
				startActivity(following_activity);
				break;
				
			case R.id.me_follower_layout:
				
				break;
			case R.id.twitter_me_detail :
				Intent me_detail = new Intent(Twitter.this,OtherPersonProfile.class);
				Bundle s = new Bundle();
				s.putString("id", GlobalData.user_id);
				s.putString("username", GlobalData.username);
				s.putString("fullname", GlobalData.fullname);
				s.putString("location", GlobalData.location);
				s.putString("website", GlobalData.website);
				s.putString("bio", GlobalData.bio);			
				s.putString("tweets_count", GlobalData.tweets);
				s.putString("following_count", GlobalData.following);
				s.putString("follower_count", GlobalData.follower);
				me_detail.putExtras(s);
				me_detail.putExtra("avarta", GlobalData.avarta);
				startActivity(me_detail);				
				break;
		}
    	
	}
    
    private class GetNewData extends AsyncTask<String,Void,String> {//Get New Data When Pull Scroll
        @Override
        protected void onPreExecute() 
        {
        	
        }  
		@Override
		protected String doInBackground(String... params) 
		{
			// TODO Auto-generated method stub
	    	StringBuilder builder = new StringBuilder();
	    	
	    	HttpGet httpPost = new HttpGet("http://192.168.1.10/twitter/api/tweet/home_timeline?max_id="+GlobalData.timeline_maxid);
	    	try{
	    			HttpResponse response = GlobalData.client.execute(httpPost);
	    			StatusLine statusLine = response.getStatusLine();
	    		
	    			int statusCode = statusLine.getStatusCode();
	    			if (statusCode == 200) {
	    				HttpEntity entity = response.getEntity();
	    				InputStream content = entity.getContent();
	    				BufferedReader reader = new BufferedReader(
	    						new InputStreamReader(content));
	    				String line;
	    				while ((line = reader.readLine()) != null) {
	    					builder.append(line);
	    				}
	    			} else {
	    				Log.e(Twitter.class.toString(), "Failed to download JSON string");
	    			}			
	    		
	    	}catch(Exception e)
	    	{
	    		e.printStackTrace();
	    	}					
	    	
	    	try{
	    		JSONArray jsonArray = new JSONArray(builder.toString());
	    		System.out.println("Length refresh = "+jsonArray.length());
	    		for(int i = 0;i < jsonArray.length(); i++)
	    		{
	    			JSONObject jsonObject = jsonArray.getJSONObject(i);
	    			Bitmap bitmap = null;
	    			//System.out.println(jsonObject.getString("tweet_id")+"|"+jsonObject.getString("content")+"|"+jsonObject.getString("created_time")+"|"+jsonObject.getString("username")+"|"+jsonObject.getString("full_name")+jsonObject.getString("avatar"));
	   			 	try {
	   			 		bitmap = BitmapFactory.decodeStream((InputStream)new URL(jsonObject.getString("avatar")).getContent());
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	   			 	if( i == 0)
	   			 		GlobalData.timeline_maxid = jsonObject.getInt("tweet_id");
	   			 		
	    			TweetItem tweet_item = new TweetItem(jsonObject.getString("tweet_id"), jsonObject.getString("content"), jsonObject.getString("created_time"), jsonObject.getString("username"), jsonObject.getString("full_name"),bitmap ,jsonObject.getString("refav"));
	    			m_tweet_item.add(0,tweet_item);

	    		}
	    	}
	    	catch(Exception e)
	    	{
	    		e.printStackTrace();
	    	}			

			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			//System.out.println("Refrested");
	    	//m_adapter = new TimeLineListAdapter(Twitter.this, R.layout.tweet_item, m_tweet_item);	
			m_adapter.notifyDataSetChanged();
	    	//list.setAdapter(m_adapter);			
            list.onRefreshComplete();
            super.onPostExecute("a");
        }    	 
    }    


    
    public class TweetItem{ //Descriptions Tweets item in ListView
    	public String tweet_id;
    	public String content;
    	public String created_time;
    	public String username;
    	public String fullname;
    	public Bitmap avarta_url;
    	public String refav;
    	public TweetItem(String tweet_id,String content,String created_time,String username,String fullname,Bitmap img,String refav)
    	{
    		this.tweet_id = tweet_id;
    		this.content = content;
    		this.created_time = created_time;
    		this.username = username;
    		this.fullname = fullname;
    		this.avarta_url = img;
    		this.refav = refav;
    	}
    	public String getID()
    	{
    		return this.tweet_id;
    	}
    	public String getContent()
    	{
    		return this.content;
    	}
    	public String getCreatedTime()
    	{
    		return this.created_time;
    	}
    	public String getUsername()
    	{
    		return this.username;
    	}
    	public String getFullname()
    	{
    		return this.fullname;
    	}
    	public Bitmap getAvarta()
    	{
    		return this.avarta_url;
    	}
    	public String getRefav()
    	{
    		return this.refav;
    	}
    }
    
    private class TimeLineListAdapter extends ArrayAdapter<TweetItem>{ //Timeline Home List Adapter
    	private ArrayList<TweetItem> items;
    	public TimeLineListAdapter(Context context, int textViewResourceId, ArrayList<TweetItem> items)
    	{
    		super(context, textViewResourceId,items);
    		this.items = items;
    	}
    	@Override
    	public int getViewTypeCount() {
    	    return 12;
    	  }
        @Override
        public int getItemViewType(int position) {
        	String type = this.items.get(position).getRefav();
        	if(type.equals("0")) //Normal Tweets
        		return 0;
        	else if(type.equals("1")) //Retweet
        		return 1;
        	else if(type.equals("10")) //Favorites
        		return 10;
        	else   						//Retweet and Favorites
        		return 11;

        }      
        
    	@Override
    	public View getView(int position, View v, ViewGroup parent) 
    	{    

    		if(v == null)
    		{
    			LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    			v = vi.inflate(R.layout.tweet_item,null);
    		}
    		
    		TweetItem tweet_item = items.get(position);
    		
    		if(tweet_item!= null)
    		{
    			ImageView imgview = (ImageView)v.findViewById(R.id.timeline_avarta);
    			TextView fullname = (TextView)v.findViewById(R.id.timeline_fullname);
    			TextView username = (TextView)v.findViewById(R.id.timeline_username);
    			TextView content = (TextView)v.findViewById(R.id.timeline_content);
    			
    			ImageView refav = (ImageView)v.findViewById(R.id.timeline_refav);
    			
    			switch(getItemViewType(position))
    			{
    				case 0 : 
    					break;
    				case 1 :
    					refav.setImageResource(R.drawable.ic_dogear_rt);
    					break;
    				case 10 : 
    					refav.setImageResource(R.drawable.ic_dogear_fave);
    					break;
    				case 11:
    					refav.setImageResource(R.drawable.ic_dogear_both);
    					break;
    			}
	
    			fullname.setText(tweet_item.getFullname());
    			username.setText("@"+tweet_item.getUsername());
    			content.setText(tweet_item.getContent());
    			//imgview.setImageBitmap(tweet_item.getAvarta()); 
    			imgview.setImageBitmap(ImageHelper.getRoundedCornerBitmap(tweet_item.getAvarta(), 5));
    		}
    		
    		return v;
    	}
    	
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_twitter, menu);
        return true;
    }


    
    
}
