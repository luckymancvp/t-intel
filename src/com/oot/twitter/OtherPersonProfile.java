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
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

public class OtherPersonProfile extends Activity implements OnClickListener {
	String id,username,fullname,bio=null,website= null,location=null;
	Bitmap avarta;
	String tweets_count,following_count,follower_count;
	
	//public ArrayList<TweetItem> m_tweet_item =null;
	//public TimeLineListAdapter m_adapter;
	
	boolean is_following = true;
	
	boolean is_me = false;
	
	Button following;
	ImageButton block;
	
	LinearLayout following_list,follower_list,favorites_list;
	LinearLayout following_list_top, follower_list_top;
	
	int edit_profile_count = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.other_person_profile);
        
        Bundle receiveBundle = this.getIntent().getExtras();
        this.id = receiveBundle.getString("id");
        this.username = receiveBundle.getString("username");
        this.fullname = receiveBundle.getString("fullname");
        this.location = receiveBundle.getString("location");
        this.website = receiveBundle.getString("website");
        this.bio = receiveBundle.getString("bio");
        if(this.id.equals(GlobalData.user_id))
        {
        	this.is_me = true;
        	this.follower_count = receiveBundle.getString("follower_count");
        	this.following_count = receiveBundle.getString("following_count");
        	this.tweets_count = receiveBundle.getString("tweets_count");
        }
        this.avarta = (Bitmap)getIntent().getParcelableExtra("avarta");
        
        //if(!this.website.equals("null"))
        	//this.website = "<a href='"+this.website+"'>"+this.website+"</a>";
        
        
        initializeProfile();
        
        if(!is_me)
        	initializeData();
        
        initializeTweets();
        

        if(!is_me)
        {
        	block = (ImageButton)findViewById(R.id.otherperson_btnBlock);
        	block.setOnClickListener(this);
        }
        else
        {
        	block = (ImageButton)findViewById(R.id.otherperson_btnBlock);
        	block.setVisibility(View.GONE);
        }
        
        if(!is_me)
        {
        	following = (Button)findViewById(R.id.otherperson_btnfollowing);
        	following.setOnClickListener(this);
        }
        else
        {
        	following = (Button)findViewById(R.id.otherperson_btnfollowing);
        	following.setBackgroundResource(R.drawable.btn_default);
        	following.setText("Edit Profile");
        	following.setTextColor(Color.BLACK);
        	following.setOnClickListener(this);        	
        }
        
        
        following_list_top = (LinearLayout)findViewById(R.id.otherperson_following_list_top);
        follower_list_top = (LinearLayout)findViewById(R.id.otherperson_followers_list_top);
        
        following_list = (LinearLayout)findViewById(R.id.otherperson_following_list);
        follower_list = (LinearLayout)findViewById(R.id.otherperson_follower_list);
        favorites_list = (LinearLayout)findViewById(R.id.otherperson_favorites_list);
        
        following_list.setOnClickListener(this);
        follower_list.setOnClickListener(this);
        favorites_list.setOnClickListener(this);
        following_list_top.setOnClickListener(this);
        follower_list_top.setOnClickListener(this);
        


        

        final ScrollView sc = (ScrollView)findViewById(R.id.scrollview);		
        sc.post(new Runnable() {	
			public void run() {
				// TODO Auto-generated method stub
				LinearLayout test = (LinearLayout)findViewById(R.id.testest);
				LinearLayout t = (LinearLayout)findViewById(R.id.testthu);
				System.out.println("------------------|"+test.getTop()+"|"+test.getScrollY()+"|"+t.getHeight());				
				sc.scrollTo(0,test.getTop());				
			}
		});
        
        
        
    }
    
    
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
			case R.id.otherperson_btnfollowing:
			if (!is_me) 
			{
				if (is_following) {
					AlertDialog.Builder dialog = new AlertDialog.Builder(this);
					dialog.setTitle("Unfollow");
					dialog.setMessage("Stop following this user?");
					dialog.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									following.setText("Follow");
									following.setTextColor(Color.BLACK);
									following
											.setBackgroundResource(R.drawable.btn_default);

									// TODO Auto-generated method stub
									StringBuilder builder = new StringBuilder();

									HttpPost httpPost = new HttpPost(
											"http://192.168.1.10/twitter/api/user/unfollow");

									List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
											1);
									nameValuePairs.add(new BasicNameValuePair(
											"follow_id", id));

									try {
										httpPost.setEntity(new UrlEncodedFormEntity(
												nameValuePairs));
									} catch (UnsupportedEncodingException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
									try {
										HttpResponse response = GlobalData.client
												.execute(httpPost);
										StatusLine statusLine = response
												.getStatusLine();

										int statusCode = statusLine
												.getStatusCode();

										HttpEntity entity = response
												.getEntity();
										InputStream content = entity
												.getContent();
										BufferedReader reader = new BufferedReader(
												new InputStreamReader(content));
										String line;
										while ((line = reader.readLine()) != null) {
											builder.append(line);
										}

									} catch (Exception e) {
										e.printStackTrace();
									}

								}
							});
					dialog.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();
								}
							});
					dialog.show();
					is_following = false;
				} else {
					following.setText("Following");
					following.setTextColor(Color.WHITE);
					following
							.setBackgroundResource(R.drawable.btn_active_default);

					StringBuilder builder = new StringBuilder();
					HttpPost httpPost = new HttpPost(
							"http://192.168.1.10/twitter/api/user/follow");

					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
							1);
					nameValuePairs.add(new BasicNameValuePair("follow_id", id));

					try {
						httpPost.setEntity(new UrlEncodedFormEntity(
								nameValuePairs));
					} catch (UnsupportedEncodingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try {
						HttpResponse response = GlobalData.client
								.execute(httpPost);
						StatusLine statusLine = response.getStatusLine();

						int statusCode = statusLine.getStatusCode();

						HttpEntity entity = response.getEntity();
						InputStream content = entity.getContent();
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(content));
						String line;
						while ((line = reader.readLine()) != null) {
							builder.append(line);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

					is_following = true;
				}
			}
			else
			{
				
				Intent edit = new Intent(OtherPersonProfile.this,EditProfile.class);
				Bundle bun = new Bundle();
				bun.putString("id", this.id);
				bun.putString("fullname", this.fullname);
				bun.putString("bio", this.bio);
				bun.putString("website", this.website);
				bun.putString("location", this.location);			
				
				edit.putExtras(bun);
				edit.putExtra("avarta", this.avarta);
				System.out.println(this.id+"|"+this.fullname+"|"+this.website+"|"+this.bio+"|"+this.location);
				startActivityForResult(edit, 0);

			}
				break;
			case R.id.otherperson_btnBlock:
				
				break;
				
			case R.id.otherperson_following_list:
				Intent i = new Intent(OtherPersonProfile.this,OtherPersonFollowing.class);
				Bundle sendBundle = new Bundle();
				sendBundle.putString("id", this.id);
				i.putExtras(sendBundle);
				startActivity(i);
				
				break;
			case R.id.otherperson_follower_list:
				
				break;
			case R.id.otherperson_favorites_list:
				
				break;
			case R.id.otherperson_followers_list_top:
				Intent i1 = new Intent(OtherPersonProfile.this,OtherPersonFollowing.class);
				Bundle sendBundle1 = new Bundle();
				sendBundle1.putString("id", this.id);
				i1.putExtras(sendBundle1);
				startActivity(i1);				
				break;
		}
	}	   
    
	public void initializeProfile()
	{
		TextView full_name = (TextView)findViewById(R.id.otherperson_fullname);
		TextView user_name = (TextView)findViewById(R.id.other_person_username);
		TextView loca = (TextView)findViewById(R.id.otherperson_location);
		ImageView ava = (ImageView)findViewById(R.id.otherperson_avarta);
		TextView bio = (TextView)findViewById(R.id.otherperson_bio);
		TextView web = (TextView)findViewById(R.id.otherperson_hompage);
		if(is_me)
		{
			TextView tweets_count = (TextView)findViewById(R.id.otherperson_tweets);
			TextView following_count = (TextView)findViewById(R.id.otherperson_following);
			TextView follower_count = (TextView)findViewById(R.id.otherperson_follower);
			
			tweets_count.setText(this.tweets_count);
			follower_count.setText(this.follower_count);
			following_count.setText(this.following_count);
		}
		//System.out.println(this.location + "|"+this.bio+"|"+this.bio.length()+this.location.length());
		full_name.setText(this.fullname);
		user_name.setText("@"+this.username);
		if(!this.location.equals("null"))
			loca.setText(this.location);
		if(!this.bio.equals("null"))
			bio.setText(this.bio);
		if(!this.website.equals("null"))
		{
    		web.setText(this.website);
    		Linkify.addLinks(web, Linkify.WEB_URLS);
		}
		ava.setImageBitmap(ImageHelper.getRoundedCornerBitmap(this.avarta, 5));
		
	}
	
	
	public void initializeData()
	{
		TextView tweets = (TextView)findViewById(R.id.otherperson_tweets);
		TextView following = (TextView)findViewById(R.id.otherperson_following);
		TextView follower = (TextView)findViewById(R.id.otherperson_follower);
		
    	StringBuilder return_value = new StringBuilder();
    	HttpGet httpGet = new HttpGet("http://192.168.1.10/twitter/api/user/show?user_id="+this.id);
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
    	//System.out.println(jsonvalue);
    	try{
    		JSONArray json = new JSONArray(jsonvalue);
    		JSONObject obj = json.getJSONObject(0);
    		this.follower_count =  obj.getString("followers_count");
    		this.following_count = obj.getString("following_count");
    		this.tweets_count = obj.getString("tweets_count");
    	}
    	catch (Exception e) {
			// TODO: handle exception
    		e.printStackTrace();
		}	
    	
    	tweets.setText(this.tweets_count);
    	following.setText(this.following_count);
    	follower.setText(this.follower_count);

	}
	
	public void initializeTweets()
	{
    	StringBuilder return_value = new StringBuilder();
    	HttpGet httpGet = null;
    	if(!is_me)
    		httpGet = new HttpGet("http://192.168.1.10/twitter/api/tweet/user_timeline?count=3&user_id="+this.id);
    	else
    	{
    		httpGet = new HttpGet("http://192.168.1.10/twitter/api/tweet/user_timeline?count=3");
    		//System.out.println("huhu|"+this.id);
    	}
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
    	
		 	LinearLayout layout = (LinearLayout)findViewById(R.id.otherperson_tweets_parent);
		 	LinearLayout layout_1 = (LinearLayout)findViewById(R.id.otherperson_tweets_1);
		 	LinearLayout layout_2 = (LinearLayout)findViewById(R.id.otherperson_tweets_2);
		 	LinearLayout layout_3 = (LinearLayout)findViewById(R.id.otherperson_tweets_3);    	
    	if(return_value.toString().equals("{\"result\":1}"))
    	{
		 	layout_1.setVisibility(View.GONE);
		 	layout_2.setVisibility(View.GONE);
		 	layout_3.setVisibility(View.GONE);   			 		
		 	layout.removeView(layout_1);
		 	layout.removeView(layout_2);
		 	layout.removeView(layout_3);    		   		
    	}
    	
    	try{
    		JSONArray jsonArray = new JSONArray(return_value.toString());
    		System.out.println("LENGTH = "+jsonArray.length());
    		for(int i = 0;i < jsonArray.length(); i++)
    		{
    			
   			 	if(i == 0)
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
   			 		ImageView avatar_1 = (ImageView)findViewById(R.id.otherperson_tweets_avatar_1);
   			 		TextView fn1 = (TextView)findViewById(R.id.otherperson_tweets_fullname_1);
   			 		TextView un1 = (TextView)findViewById(R.id.otherperson_tweets_username_1);
   			 		TextView cnt1 = (TextView)findViewById(R.id.otherperson_content_1);
   			 		ImageView refav1 = (ImageView)findViewById(R.id.otherperson_tweets_refav_1);
   			 		
   			 		avatar_1.setImageBitmap(ImageHelper.getRoundedCornerBitmap(bitmap, 5));
   			 		fn1.setText(jsonObject.getString("full_name"));
   			 		un1.setText(jsonObject.getString("username"));
   			 		cnt1.setText(jsonObject.getString("content"));
   			 		int ref = Integer.parseInt(jsonObject.getString("refav"));
   			 		if(ref == 1)
   			 			refav1.setImageResource(R.drawable.ic_dogear_rt);
   			 		if(ref == 10)
   			 			refav1.setImageResource(R.drawable.ic_dogear_fave);
   			 		if(ref == 11)
   			 			refav1.setImageResource(R.drawable.ic_dogear_both);
   			 	}
   			 	if(i == 1)
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
   			 		ImageView avatar_2 = (ImageView)findViewById(R.id.otherperson_tweets_avatar_2);
   			 		TextView fn2 = (TextView)findViewById(R.id.otherperson_tweets_fullname_2);
   			 		TextView un2 = (TextView)findViewById(R.id.otherperson_tweets_username_2);
   			 		TextView cnt2 = (TextView)findViewById(R.id.otherperson_content_2);
   			 		ImageView refav2 = (ImageView)findViewById(R.id.otherperson_tweets_refav_2);
   			 		
   			 		avatar_2.setImageBitmap(ImageHelper.getRoundedCornerBitmap(bitmap, 5));
   			 		fn2.setText(jsonObject.getString("full_name"));
   			 		un2.setText(jsonObject.getString("username"));
   			 		cnt2.setText(jsonObject.getString("content"));
   			 		int ref = Integer.parseInt(jsonObject.getString("refav"));
   			 		if(ref == 1)
   			 			refav2.setImageResource(R.drawable.ic_dogear_rt);
   			 		if(ref == 10)
   			 			refav2.setImageResource(R.drawable.ic_dogear_fave);
   			 		if(ref == 11)
   			 			refav2.setImageResource(R.drawable.ic_dogear_both);   			 		
   			 	}
   			 	if(i == 2)
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
   			 		ImageView avatar_3 = (ImageView)findViewById(R.id.otherperson_tweets_avatar_3);
   			 		TextView fn3 = (TextView)findViewById(R.id.otherperson_tweets_fullname_3);
   			 		TextView un3 = (TextView)findViewById(R.id.otherperson_tweets_username_3);
   			 		TextView cnt3 = (TextView)findViewById(R.id.otherperson_content_3);
   			 		ImageView refav3 = (ImageView)findViewById(R.id.otherperson_tweets_refav_3);
   			 		
   			 		avatar_3.setImageBitmap(ImageHelper.getRoundedCornerBitmap(bitmap, 5));
   			 		fn3.setText(jsonObject.getString("full_name"));
   			 		un3.setText(jsonObject.getString("username"));
   			 		cnt3.setText(jsonObject.getString("content"));
   			 		int ref = Integer.parseInt(jsonObject.getString("refav"));
   			 		if(ref == 1)
   			 			refav3.setImageResource(R.drawable.ic_dogear_rt);
   			 		if(ref == 10)
   			 			refav3.setImageResource(R.drawable.ic_dogear_fave);
   			 		if(ref == 11)
   			 			refav3.setImageResource(R.drawable.ic_dogear_both);     			 		
   			 	}
   			 	if(jsonArray.length() == 0)
   			 	{	
   			 		layout_1.setVisibility(View.GONE);
   			 		layout_2.setVisibility(View.GONE);
   			 		layout_3.setVisibility(View.GONE);   			 		
   			 		layout.removeView(layout_1);
   			 		layout.removeView(layout_2);
   			 		layout.removeView(layout_3);
   			 	}
   			 	if(jsonArray.length() == 1)
   			 	{
   			 		layout_2.setVisibility(View.GONE);
   			 		layout_3.setVisibility(View.GONE);   			 		
   			 	}
   			 	if(jsonArray.length() == 2)
   			 	{
   			 		layout_3.setVisibility(View.GONE);   
   			 	}
    			//TweetItem tweet_item = new TweetItem(jsonObject.getString("tweet_id"), jsonObject.getString("content"), jsonObject.getString("created_time"), jsonObject.getString("username"), jsonObject.getString("full_name"),bitmap );
    			//m_tweet_item.add(tweet_item);
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}    	
    	
    	//this.m_adapter = new TimeLineListAdapter(this, R.layout.tweet_item, m_tweet_item);
    	//ListView list = (ListView)findViewById(R.id.otherperson_list);
    	//list.setAdapter(m_adapter);
    	//System.out.println("Height of child|"+list.getChildAt(0).getHeight());
    	
	}
	
	/*
    public class TweetItem{ //Descriptions Tweets item in ListView
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
    private class TimeLineListAdapter extends ArrayAdapter<TweetItem>{ //Timeline Home List Adapter
    	private ArrayList<TweetItem> items;
    	public TimeLineListAdapter(Context context, int textViewResourceId, ArrayList<TweetItem> items)
    	{
    		super(context, textViewResourceId,items);
    		this.items = items;
    	}
    	@Override
    	public View getView(int position, View v, ViewGroup parent) 
    	{    
    		System.out.println("POSITION = "+position);
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
    */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
	    super.onActivityResult(requestCode, resultCode, data);
	    if(requestCode == 0)
	    {
	    	if(data != null)
	    	{
	    		avarta = (Bitmap)data.getParcelableExtra("avatar");
	    		Bundle receive = data.getExtras();
	    		this.location = receive.getString("location");
	    		this.website = receive.getString("website");
	    		this.bio = receive.getString("bio");
	    		this.fullname = receive.getString("fullname");
	    		
	    		//System.out.println(this.fullname+"|"+this.website+"|"+this.bio+"|"+this.location);
	    		
	    		TextView full_name = (TextView)findViewById(R.id.otherperson_fullname);
	    		TextView user_name = (TextView)findViewById(R.id.other_person_username);
	    		TextView loca = (TextView)findViewById(R.id.otherperson_location);
	    		ImageView ava = (ImageView)findViewById(R.id.otherperson_avarta);
	    		TextView bio = (TextView)findViewById(R.id.otherperson_bio);
	    		TextView web = (TextView)findViewById(R.id.otherperson_hompage);
	    		
	    		full_name.setText(this.fullname);
	    		user_name.setText("@"+this.username);
	    		loca.setText(this.location);
	    		bio.setText(this.bio);
	    		web.setText(this.website);
	    		Linkify.addLinks(web, Linkify.WEB_URLS);
	    		
	    		ava.setImageBitmap(ImageHelper.getRoundedCornerBitmap(avarta, 10));
	    	}
	    }
	}
	
}
