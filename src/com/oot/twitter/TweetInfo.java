package com.oot.twitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oot.twitter.base.BaseActivity;
import com.oot.twitter.base.SimpleURI;
import com.oot.twitter.info.Tweet;

/**
 * @author luckymancvp
 *
 */
/**
 * @author luckymancvp
 *
 */
public class TweetInfo extends BaseActivity implements OnClickListener, android.content.DialogInterface.OnClickListener{
	// Declare private property
	private LinearLayout ic_reply, ic_rt, ic_fave, ic_share;
	private Tweet tweet;
	private String count_favorites = "0";
	private String count_retweets  = "0";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);      
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        initializeData();
        if (GlobalData.currentUser.getUsername().equals(tweet.getUsername())){
        	setContentView(R.layout.activity_tweet_info_del);	
        }
        else{
        	setContentView(R.layout.activity_tweet_info);
        }
       displayCountRefav();
       display();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.activity_tweet_info, menu);
        return true;
    }
    
    public void initializeData()
    {
        // Get param
        Bundle bundle   = this.getIntent().getExtras();
        //String tweet_id = bundle.getString("tweet_id");
        String tweet_id = "297";
        
        StringBuilder builder = new StringBuilder();
        HttpGet httpGet = new HttpGet(ConnectionTwitter.SERVER_URL+"api/tweet/tweetInfo?tweet_id="+tweet_id);
        try{
        	HttpResponse response = GlobalData.client.execute(httpGet);
        	StatusLine statusLine = response.getStatusLine();
        	int statusCode = statusLine.getStatusCode();
        	if( statusCode == 200) {
        		HttpEntity entity = response.getEntity();
        		InputStream content = entity.getContent();
        		BufferedReader reader = new BufferedReader(new InputStreamReader(content));
        		
        		String line;
        		while((line = reader.readLine())!=null){
        			builder.append(line);
        		}
        	}
        	else {
        		Log.e(Twitter.class.toString(),"Network connection is error");
        		return;
        	}
        }catch (Exception e){
        	System.out.println("Failed to download JSON string");
        }
        
        try{
        	JSONObject jsonObject = new JSONObject(builder.toString());
        	tweet = new Tweet(new JSONObject(jsonObject.getString("tweet")));
        	
        }catch(Exception e){
        	System.out.println("Error in parse JSON string");
        }
        
    }
    
    /**
     * Display Tweet
     * 
     */
    private void display()
    {
    	// Get graphic object
    	TextView content      = (TextView)findViewById(R.id.content);
    	TextView full_name    = (TextView)findViewById(R.id.full_name);
    	TextView username     = (TextView)findViewById(R.id.username);
    	ImageView avatar      = (ImageView)findViewById(R.id.avatar);
    	TextView created_time = (TextView)findViewById(R.id.created_time);
    	
    	// Add listener
    	ic_reply = (LinearLayout)findViewById(R.id.ic_reply);
    	ic_rt    = (LinearLayout)findViewById(R.id.ic_rt);
    	ic_fave  = (LinearLayout)findViewById(R.id.ic_fave);
    	ic_share = (LinearLayout)findViewById(R.id.ic_share);
    	ic_reply.setOnClickListener(this);
    	ic_rt.setOnClickListener(this);
    	ic_fave.setOnClickListener(this);
    	ic_share.setOnClickListener(this);
    	if (GlobalData.currentUser.getUsername().equals(tweet.getUsername()))
    			((LinearLayout)findViewById(R.id.ic_del)).setOnClickListener(this);
    	
    	
    	// Tranfer value
    	full_name.setText(tweet.getFull_name());
    	username.setText("@"+tweet.getUsername());
    	content.setText(tweet.getContent());
    	displayFaveButton((Integer.parseInt(tweet.getRefav()))/10);
    	displayRtButton((Integer.parseInt(tweet.getRefav()))%10);
    	
    	// Compile content
    	Pattern userMatcher = Pattern.compile("\\B@[^:\\s]+");
    	String userViewURL = "net.cogitas.findtweets.user://";
    	Linkify.addLinks(content, Linkify.WEB_URLS);
    	Linkify.addLinks(content, userMatcher, userViewURL);
    	
    	// Get user'stweet avatar
    	Bitmap bitmap = null;
    	try {
		 		bitmap = BitmapFactory.decodeStream((InputStream)new URL(tweet.getAvatar()).getContent());
		} catch (MalformedURLException e) {
			System.out.println("Error in decode");
		} catch (IOException e) {
			System.out.println("Error in io");
		}
    	avatar.setImageBitmap(bitmap);
    	
    	// Display Date
    	SimpleDateFormat formatterIn  = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    	SimpleDateFormat formatterOut = new SimpleDateFormat("h:mma - dd MMM yy");
    	Date createdDate = null;
		try {
			createdDate = formatterIn.parse(tweet.getCreated_time());
		} catch (ParseException e) {
			System.out.println("Error in convert time date");
		}
    	created_time.setText(formatterOut.format(createdDate));
    	
    }

    
    /**
     * Implement onClick method
     */
	public void onClick(View v) {
		switch(v.getId())
		{
			case R.id.ic_reply:
				replyAction();
				break;
			case R.id.ic_rt:
				rtAction();
				break;
			case R.id.ic_fave:
				faveAction();
				break;
			case R.id.ic_share:
				shareAction();
				break;
			case R.id.ic_del:
				delAction();
				break;
			case R.id.showRetweets:
				System.out.println("");
				break;
			case R.id.showFavorites:
				// prepare parameter for intent
				List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
				nameValuePair.add(new BasicNameValuePair("tweet_id", tweet.getTweet_id()));
				SimpleURI uri = new SimpleURI("Favorited by",ConnectionTwitter.FAVORITEDBY, nameValuePair);
				GlobalData.paramIntent = uri;
				
				// change to new intent
				startActivity(new Intent(TweetInfo.this, UsersList.class));
				break;
		}
	}
	
	/**
	 * Handle when click favorite button
	 */
	private void faveAction()
	{
		String requestString;
		int refav = Integer.parseInt(tweet.getRefav());
		int fav   = refav / 10;
		if (fav != 1) {
			requestString = ConnectionTwitter.SERVER_URL+"api/tweet/favorite?tweet_id="+tweet.getTweet_id();
		}
		else
			requestString = ConnectionTwitter.SERVER_URL+"api/tweet/unFavorite?tweet_id="+tweet.getTweet_id();
		if (sendRequest(requestString)){
			if (fav == 1) {
				tweet.setRefav((new Integer(refav-10)).toString());
				
				// count down				
				TextView favTxt = (TextView)findViewById(R.id.countFavTxt);
				count_favorites = (new Integer( Integer.parseInt(count_favorites) - 1)).toString();
		        favTxt.setText(count_favorites);
		        
				((ImageView)findViewById(R.id.faveImg)).setImageResource(R.drawable.ic_action_fave_off_default);
			}
			else{
				// count down				
				TextView favTxt = (TextView)findViewById(R.id.countFavTxt);
				count_favorites = (new Integer( Integer.parseInt(count_favorites) + 1)).toString();
		        favTxt.setText(count_favorites);
		        
				tweet.setRefav((new Integer(refav+10)).toString());
				((ImageView)findViewById(R.id.faveImg)).setImageResource(R.drawable.ic_action_fave_on_default);
			}
		}
	}
	
	
	/**
	 * Send request to server
	 * @param String requestString
	 * @return boolean send result
	 */
	private boolean sendRequest(String requestString){
		StringBuilder builder = new StringBuilder();
		HttpGet httpGet = new HttpGet(requestString);
		
    	try{
    		HttpResponse response = GlobalData.client.execute(httpGet);
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
			System.out.println(builder.toString());
    		
    	}catch(Exception e)
    	{
    		System.out.println("Error when send request to "+requestString);
    		return false;
    	}
		return true;
	}
	
	/**
	 * Display favorite button
	 * @param int refav Rewteet value 1: Favorite, 0: Unfavorite
	 */
	private void displayFaveButton(int refav) 
	{
		if (refav == 1){
			ImageView faveImg = (ImageView)findViewById(R.id.faveImg);
			faveImg.setImageResource(R.drawable.ic_action_fave_on_default);
		}
		else
			((ImageView)findViewById(R.id.faveImg)).setImageResource(R.drawable.ic_action_fave_off_default);
	}
	
	/**
	 * Display retweet button
	 * @param int refav Rewteet value 1: Retweet, 0: Unretweet
	 */
	private void displayRtButton(int refav)
	{
		if (tweet.getUsername() == GlobalData.username){
			((ImageView)findViewById(R.id.rtImg)).setImageResource(R.drawable.ic_action_rt_disabled);
			return;
		}
		if (refav == 1)
			((ImageView)findViewById(R.id.rtImg)).setImageResource(R.drawable.ic_action_rt_on_default);
		else
			((ImageView)findViewById(R.id.rtImg)).setImageResource(R.drawable.ic_action_rt_off_default);
	}
	
	/**
	 * Handle when click share button
	 */
	private void shareAction() {
		//create the send intent
		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);

		//set the type
		shareIntent.setType("text/plain");

		//add a subject
		shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share");

		//build the body of the message to be shared
		String shareMessage = tweet.getContent();

		//add the message
		shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);

		//start the chooser for sharing
		startActivity(Intent.createChooser(shareIntent,"Share"));
	}
	
	/**
	 * Handle when click reply button
	 */
	private void replyAction(){
		System.out.println("Reply");
	}
	
	/**
	 * Handle when click retweet button
	 */
	private void rtAction()
	{
		if (tweet.getUsername() == GlobalData.currentUser.getUsername())
			return;
		String message, button1;
		// Get retweet value
		int refav = Integer.parseInt(tweet.getRefav()) %10;
		if (refav == 0){
			message = "Retweet this to your followers ?";
			button1 = "Retweet"; 
		}
		else{
			message = "Undo this retweet ?";
			button1 = "Undo";
		}
			
		AlertDialog ab = new AlertDialog.Builder(this).create();
			ab.setTitle("Retweet");
			ab.setMessage(message);
			ab.setButton (button1, this);
			ab.setButton3("Quote", this);
			ab.setButton2("Cancel", this);
		ab.show();
	}
	
	/**
	 * Handle when click del button
	 */
	private void delAction()
	{
		AlertDialog ab = new AlertDialog.Builder(this).create();
			ab.setTitle("Delete");
			ab.setMessage("Do you want to delete this Tweet ?");
			ab.setButton ("Delete", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					System.out.println("Del action here");
				}
			});
			ab.setButton2("Cancel", this);
		ab.show();
	}

	
	/* Handle click in dialog
	 * @param DialogInterface dialog
	 * @param int which
	 */
	public void onClick(DialogInterface dialog, int which) {
		System.out.println(dialog);
		switch (which) 
		{
			case DialogInterface.BUTTON_POSITIVE:
				String requestString;
				int refav = Integer.parseInt(tweet.getRefav()) %10;
				if (refav == 0){
					requestString = ConnectionTwitter.SERVER_URL+"api/tweet/retweet?tweet_id="+tweet.getTweet_id();
				}
				else
					requestString = ConnectionTwitter.SERVER_URL+"api/tweet/unretweet?tweet_id="+tweet.getTweet_id();
				if (sendRequest(requestString)){
					// Change value of retweet and image
					if (refav == 0){
						// count down				
						TextView reTxt = (TextView)findViewById(R.id.countReTxt);
						count_retweets = (new Integer( Integer.parseInt(count_retweets) + 1)).toString();
						reTxt.setText(count_retweets);
				        
						tweet.setRefav((new Integer(refav+1)).toString());
						((ImageView)findViewById(R.id.rtImg)).setImageResource(R.drawable.ic_action_rt_on_default);
					}
					else{
						// count down				
						TextView reTxt = (TextView)findViewById(R.id.countReTxt);
						count_retweets = (new Integer( Integer.parseInt(count_retweets) - 1)).toString();
						reTxt.setText(count_retweets);
						
						tweet.setRefav((new Integer(refav-1)).toString());
						((ImageView)findViewById(R.id.rtImg)).setImageResource(R.drawable.ic_action_rt_off_default);
					}
				}
				break;
			case DialogInterface.BUTTON_NEUTRAL:
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				break;
		}
	}
	
	private void displayCountRefav()
	{
		// prepare for request
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1); 
    	nameValuePairs.add(new BasicNameValuePair("tweet_id", tweet.getTweet_id()));
    	JSONObject result = ConnectionTwitter.sendRequestJSon(ConnectionTwitter.count_refav, nameValuePairs);
    	
    	try{
	    	count_favorites = result.getString("count_favorites");
	    	count_retweets  = result.getString("count_retweets");
    	}catch (Exception e){
    		System.out.println("Error in parse count refav");
    	}
    	
    	// display count to interface
    	
        getLayoutInflater().inflate(R.layout.count_refav, (LinearLayout)findViewById(R.id.tweet_info));
        TextView favTxt = (TextView)findViewById(R.id.countFavTxt);
        TextView reTxt  = (TextView)findViewById(R.id.countReTxt);
        favTxt.setText(count_favorites);
        reTxt.setText(count_retweets);
        
        // Click listener
        findViewById(R.id.showFavorites).setOnClickListener(this);
        findViewById(R.id.showRetweets).setOnClickListener(this);
	}
}
