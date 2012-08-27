package com.oot.twitter;

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
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONObject;

import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Tweets extends Activity {
	
	public ArrayList<TweetItem> m_tweet_item =null;
	public UserTweetsListAdapter m_adapter;
	
	PullToRefreshListView list;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.tweets);
        
        initializeData();
    }
    
    public void initializeData()
    {
    	//Get data from Server
    	StringBuilder builder = new StringBuilder();
    	
    	HttpGet httpPost = new HttpGet("http://192.168.1.10/twitter/api/tweet/user_timeline");
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
    		System.out.println("Length = "+jsonArray.length());
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
   			 		GlobalData.tweets_maxid = jsonObject.getInt("tweet_id");   			 	
   			 	
    			TweetItem tweet_item = new TweetItem(jsonObject.getString("tweet_id"), jsonObject.getString("content"), jsonObject.getString("created_time"), jsonObject.getString("username"), jsonObject.getString("full_name"),bitmap );
    			m_tweet_item.add(tweet_item);
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	
    	this.m_adapter = new UserTweetsListAdapter(this, R.layout.tweet_item, m_tweet_item);
        list = (PullToRefreshListView)findViewById(R.id.tweets_list);
        list.setOnRefreshListener(new OnRefreshListener() {
            public void onRefresh() {
                // Do work to refresh the list here.
            	new GetNewData().execute(new String[]{"ABCD"});
            }
        });        
    	list.setAdapter(m_adapter);    	
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
	    	
	    	HttpGet httpPost = new HttpGet("http://192.168.1.10/twitter/api/tweet/user_timeline?max_id="+GlobalData.tweets_maxid);
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
	   			 		GlobalData.tweets_maxid = jsonObject.getInt("tweet_id");
	   			 		
	    			TweetItem tweet_item = new TweetItem(jsonObject.getString("tweet_id"), jsonObject.getString("content"), jsonObject.getString("created_time"), jsonObject.getString("username"), jsonObject.getString("full_name"),bitmap );
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
    
    public class TweetItem{ //Descriptions
    	public String tweet_id;
    	public String content;
    	public String created_time;
    	public String username;
    	public String fullname;
    	public Bitmap avarta_url;
    	public TweetItem(String tweet_id,String content,String created_time,String username,String fullname,Bitmap img)
    	{
    		this.tweet_id = tweet_id;
    		this.content = content;
    		this.created_time = created_time;
    		this.username = username;
    		this.fullname = fullname;
    		this.avarta_url = img;
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
    
    }
    private class UserTweetsListAdapter extends ArrayAdapter<TweetItem>{
    	private ArrayList<TweetItem> items;
    	public UserTweetsListAdapter(Context context, int textViewResourceId, ArrayList<TweetItem> items)
    	{
    		super(context, textViewResourceId,items);
    		this.items = items;
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
    			
    			fullname.setText(tweet_item.getFullname());
    			username.setText("@"+tweet_item.getUsername());
    			content.setText(tweet_item.getContent());
    			imgview.setImageBitmap(tweet_item.getAvarta()); 
    		}
    		
    		return v;
    	}
    	
    }    
}
