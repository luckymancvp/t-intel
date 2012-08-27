package com.oot.twitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Following extends Activity{
	public ArrayList<FollowingItem> m_following_item =null;
	public FollowingListAdapter m_adapter;	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.following);
        
        initializeData();
    }
    
    public void initializeData()
    {
    	//Get data from Server
    	StringBuilder builder = new StringBuilder();
    	
    	HttpGet httpPost = new HttpGet("http://192.168.1.10/twitter/api/user/following");
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
    	
    	m_following_item = new ArrayList<FollowingItem>();
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
    			FollowingItem following_item = new FollowingItem(jsonObject.getString("user_id"), jsonObject.getString("username"), jsonObject.getString("full_name"),bitmap,jsonObject.getString("bio"),jsonObject.getString("location"),jsonObject.getString("website") );
    			m_following_item.add(following_item);
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}    	
   
    	this.m_adapter = new FollowingListAdapter(this, R.layout.following_item, m_following_item);
    	ListView list = (ListView)findViewById(R.id.following_list);
    	list.setAdapter(m_adapter);    
    	list.setItemsCanFocus(false);
    	list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
				// TODO Auto-generated method stub
				//System.out.println("ONE OF THEMMMMMMMMMMMMMMMMMM");
				String following_id = m_following_item.get(position).getID();
				String fullname = m_following_item.get(position).getFullname();
				String username = m_following_item.get(position).getUsername();
				Bitmap avarta = m_following_item.get(position).getAvarta();
				String location = m_following_item.get(position).getLocation();
				String website = m_following_item.get(position).getWebsite();
				String bio = m_following_item.get(position).getBio();
				
				Bundle sendBundle = new Bundle();
				sendBundle.putString("id", following_id);
				sendBundle.putString("username", username);
				sendBundle.putString("fullname", fullname);
				sendBundle.putString("location", location);
				sendBundle.putString("website", website);
				sendBundle.putString("bio", bio);
				Intent i = new Intent(Following.this,OtherPersonProfile.class);
				i.putExtras(sendBundle);
				i.putExtra("avarta", avarta);
				startActivity(i);
				
			}
		});
    	
    }
    
    
    public class FollowingItem{ //Descriptions following item
    	public String user_id;
    	public String username;
    	public String fullname;
    	public String bio;
    	public String location;
    	public String website;
    	public Bitmap avarta_url;
    	public FollowingItem(String user_id,String username,String fullname,Bitmap img,String bio,String location,String website)
    	{
    		this.user_id = user_id;
    		this.username = username;
    		this.fullname = fullname;
    		this.avarta_url = img;
    		this.bio = bio;
    		this.location = location;
    		this.website = website;
    	}
    	public String getID()
    	{
    		return this.user_id;
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
    	public String getBio()
    	{
    		return this.bio;
    	}
    	public String getLocation()
    	{
    		return this.location;
    	}
    	public String getWebsite()
    	{
    		return this.website;
    	}
    }
    private class FollowingListAdapter extends ArrayAdapter<FollowingItem>{    
    	private ArrayList<FollowingItem> items;
    	int flag = 0;
    	public FollowingListAdapter(Context context, int textViewResourceId, ArrayList<FollowingItem> items)
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
    			v = vi.inflate(R.layout.following_item,null);
    		}
    		
    		final FollowingItem following_item = items.get(position);
    		
    		if(following_item!= null)
    		{
    			ImageView avarta = (ImageView)v.findViewById(R.id.following_avarta);
    			TextView fullname = (TextView)v.findViewById(R.id.following_fullname);
    			TextView username = (TextView)v.findViewById(R.id.following_username);
    			
    			
    			final ImageButton following_unfollowing = (ImageButton)v.findViewById(R.id.following_unfollow);
    			following_unfollowing.setFocusable(false);
    			following_unfollowing.setFocusableInTouchMode(false);
    			following_unfollowing.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(flag == 0)
						{
							following_unfollowing.setBackgroundResource(R.drawable.btn_default);
							following_unfollowing.setImageResource(R.drawable.ic_follow_default);
							flag = 1;
							
					    	StringBuilder builder = new StringBuilder();
					    	
					    	HttpPost httpPost = new HttpPost("http://192.168.1.10/twitter/api/user/unfollow");
					    	
					    	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1); 
					    	nameValuePairs.add(new BasicNameValuePair("follow_id", following_item.getID())); 
   	
					    	try {
								httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
							} catch (UnsupportedEncodingException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}			   	
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
						}
						else
						{
							following_unfollowing.setBackgroundResource(R.drawable.btn_active_default);
							following_unfollowing.setImageResource(R.drawable.ic_follow_checked);
							flag = 0;
							
					    	StringBuilder builder = new StringBuilder();
					    	
					    	HttpPost httpPost = new HttpPost("http://192.168.1.10/twitter/api/user/follow");
					    	
					    	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1); 
					    	nameValuePairs.add(new BasicNameValuePair("follow_id", following_item.getID())); 
   	
					    	try {
								httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
							} catch (UnsupportedEncodingException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}			   	
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
							
						}
					}
				});
    			
    			avarta.setImageBitmap(ImageHelper.getRoundedCornerBitmap(following_item.getAvarta(), 5)); 
    			fullname.setText(following_item.getFullname());
    			username.setText("@"+following_item.getUsername());
    		}
    		
    		return v;
    	}    	
    	
    }
    
}
