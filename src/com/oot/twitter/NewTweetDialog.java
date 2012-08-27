package com.oot.twitter;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.widget.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;

import com.oot.twitter.adapter.UsersListAdapter;
import com.oot.twitter.base.SimpleURI;
import com.oot.twitter.info.User;

public class NewTweetDialog extends Activity implements OnClickListener, AbsListView.OnScrollListener, AdapterView.OnItemClickListener {
	AutoCompleteTextView content;
	Button btnCancel,btnTweet;
	ImageButton btnAddImageFromCamera, btnAddImageFromPhone,btnAddMention;
	TextView character_left;
	ImageView avatar;
	int character = 140;
	
	Drawable img;
	Bitmap photo;
	
	int start = 0;
	boolean is_mention = false;
	String sug;
	
	public ArrayList<SuggestPerson> m_suggest_item =null;
	public SuggestListAdapter m_adapter;	
	
	int count = 0;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.new_tweet_dialog);
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay(); 
		
		/** 
		 * TODO HERE
		 * 1. Prepare list view for display suggested user
		 * 2. Hide this view. Because user haven't inputed anything
		 */
		createSuggestedList();
		
		int w = display.getWidth(); 
		int h = display.getHeight();
		w = (int) (w*0.95);
		h = (int) (h*0.9);
        TextView tv = (TextView)findViewById(R.id.new_tweet_username);
        tv.setText(GlobalData.username);
        
		getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		getWindow().setBackgroundDrawable(new ColorDrawable(0));
		
		content = (AutoCompleteTextView)findViewById(R.id.tweet_content);
		btnCancel = (Button)findViewById(R.id.btnCancelNewTweet);
		btnTweet = (Button)findViewById(R.id.btnTweet);
		btnAddImageFromCamera = (ImageButton)findViewById(R.id.btnAddImageFromCamera);
		btnAddImageFromPhone = (ImageButton)findViewById(R.id.btnAddImageFromPhone);
		btnAddMention = (ImageButton)findViewById(R.id.btnAddMention);
		character_left = (TextView)findViewById(R.id.character_left);
		
		btnCancel.setOnClickListener(this);
		btnTweet.setOnClickListener(this);
		btnAddImageFromCamera.setOnClickListener(this);
		btnAddImageFromPhone.setOnClickListener(this);
		btnAddMention.setOnClickListener(this);
		
		avatar = (ImageView)findViewById(R.id.new_tweet_avarta);
		if(GlobalData.avarta != null)
			avatar.setImageBitmap(ImageHelper.getRoundedCornerBitmap(GlobalData.avarta, 5));
		
		content.setThreshold(1);
		content.addTextChangedListener(new TextWatcher() {		
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s.length()>0)
				{
					btnTweet.setBackgroundResource(R.drawable.btn_active_default);
					btnTweet.setTextColor(Color.WHITE);
					
					character = 140 - s.length();
					String c = s.toString();
					if(!c.contains("@"))
					{
						is_mention = false;
					}
					
					/**
					 * TODO HERE
					 * 1. Detect user is typing a user name
					 * 2. IF user is typing a user name then show suggested list 
					 */
					String mentioning = getMentioning(s, start, before, count);
                    showSuggestedList(mentioning);
				}
				if(s.length() == 0)
				{
					btnTweet.setBackgroundResource(R.drawable.button_bg_disabled);
					btnTweet.setTextColor(Color.parseColor("#ACAAAC"));
				}	
				//character_left.setText(character);
			}	
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
				// TODO Auto-generated method stub
				
			}
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				character_left.setText(" "+character);

			}
		});
		

    }
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.btnCancelNewTweet:
				finish();
				break;
			case R.id.btnTweet:
				StringBuilder builder = new StringBuilder();
				
				//Add images to Send
				String ba1="";
				if(photo!=null)
				{
					ByteArrayOutputStream bao = new ByteArrayOutputStream();
					photo.compress(Bitmap.CompressFormat.JPEG, 90, bao);
					byte [] ba = bao.toByteArray();
				    ba1=Base64.encodeBytes(ba);
				}

				
				HttpPost httpPost = new HttpPost("http://192.168.1.10/twitter/api/tweet/post?");
		    	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2); 
		    	nameValuePairs.add(new BasicNameValuePair("content", content.getText().toString())); 
		    	nameValuePairs.add(new BasicNameValuePair("image",ba1));
		    	
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
		    	
		    	
				finish();
				break;
				
				
			case R.id.btnAddImageFromCamera:
				dispatchTakePictureIntent(0);
				break;
				
			case R.id.btnAddImageFromPhone:
				dispatchPictureSDCard(1);
				
				break;
			case R.id.btnAddMention:
			    start = content.getSelectionStart();
				content.getText().insert(start, "@");
				is_mention = true;
				break;
			default:
				break;
		}
	}


    private class GetSuggest extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() 
        {
        	m_suggest_item = new ArrayList<SuggestPerson>();
        	     	
        }     	
		@Override
		protected String doInBackground(String... arg0) 
		{
			// TODO Auto-generated method stub
	    	StringBuilder builder = new StringBuilder();
	    	
	    	HttpPost httpPost = new HttpPost("http://192.168.1.10/twitter/api/user/suggestion?count=10&slug="+sug);   
	    	System.out.println("http://192.168.1.10/twitter/api/user/suggestion?count=10&slug="+sug);
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
	    				Log.e(EditProfile.class.toString(), "Failed to download JSON string");
	    			}			
	    		
	    	}catch(Exception e)
	    	{
	    		e.printStackTrace();
	    	}	
	    	
	    	try{
	    		JSONArray jsonArray = new JSONArray(builder.toString());
	    		for(int i = 0;i < jsonArray.length(); i++)
	    		{
	    			JSONObject jsonObject = jsonArray.getJSONObject(i);
	    			Bitmap bitmap = null;
	   			 	try {
	   			 		bitmap = BitmapFactory.decodeStream((InputStream)new URL(jsonObject.getString("avatar")).getContent());
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
	   			 	
	   			 	SuggestPerson sp = new SuggestPerson(jsonObject.getString("user_id"), jsonObject.getString("full_name"), jsonObject.getString("full_name"), bitmap);
	   			 	m_suggest_item.add(sp);
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
	    	System.out.println("SIZE="+m_suggest_item.size());
        	m_adapter = new SuggestListAdapter(NewTweetDialog.this, R.layout.new_tweet_dialog_suggest, m_suggest_item);
        	m_adapter.setNotifyOnChange(true);
		    content.setAdapter(m_adapter);			
        }       	
    	
    }
	
	public class SuggestPerson{
		public String id;
		public String username;
		public String fullname;
		public Bitmap avatar;
		public SuggestPerson(String id,String username,String fullname,Bitmap avatar)
		{
			this.id = id;
			this.username = username;
			this.fullname = fullname;
			this.avatar = avatar;
		}
		public String getID()
		{
			return this.id;
		}
		public String getUsername()
		{
			return this.username;
		}
		public String getFullname()
		{
			return this.fullname;
		}
		public Bitmap getAvatar()
		{
			return this.avatar;
		}	
	}
	
	private class SuggestListAdapter extends ArrayAdapter<SuggestPerson>
	{    
	    private ArrayList<SuggestPerson> items;

	    public SuggestListAdapter(Context context, int textViewResourceId, ArrayList<SuggestPerson> items)
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
    			v = vi.inflate(R.layout.new_tweet_dialog_suggest,null);
    		}
    		final SuggestPerson suggest_item = items.get(position);
    		
    		ImageView img = (ImageView)v.findViewById(R.id.autocomplete_avatar);
    		TextView username = (TextView)v.findViewById(R.id.autocomplete_username);
    		TextView fullname = (TextView)v.findViewById(R.id.autocomplete_fullname);
    		
    		img.setImageBitmap(ImageHelper.getRoundedCornerBitmap(suggest_item.getAvatar(), 5));
    		username.setText(suggest_item.getUsername());
    		fullname.setText(suggest_item.getFullname());
    		
			return v;
    	}
    
	}
	
	
	//Take picture from camera
	private void dispatchTakePictureIntent(int actionCode) {
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    startActivityForResult(takePictureIntent, actionCode);
	}    
	
	//Choose picture from sd card
	private void dispatchPictureSDCard(int actionCode){
		Intent dispatchPictureIntent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(dispatchPictureIntent, actionCode);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
	    super.onActivityResult(requestCode, resultCode, data);
	    if(requestCode == 0)
	    {
	        if(data != null)
	        {
	            photo = (Bitmap) data.getExtras().get("data");
	            photo = photo.createScaledBitmap(photo, 160, 160, false);
	            Drawable img = new BitmapDrawable(photo);
	            content.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);

	        }
	        else{
	        }
	    }
	    
	    if(requestCode == 1)
	    {
	    	System.out.println("CHOOSE IMAGES !!!" + data);
	    	if(data != null)
	    	{
	    		Uri selectedImage = data.getData();
	    		String[] filePathColumn = {MediaStore.Images.Media.DATA};

	    		Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
	    		
	    		if(cursor.moveToFirst())
	    		{

	    			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	    			String filePath = cursor.getString(columnIndex);
	    			cursor.close();

	    			photo = BitmapFactory.decodeFile(filePath);
	    			photo = Bitmap.createScaledBitmap(photo, 160, 240, false);
	    			content.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
	    		}
	    	}
	    }
	    
	}	
	
	

	// List view for display suggested user
	private LinearLayout tweetScreen;
	private LinearLayout suggestedList;
    private ListView list;
    private UsersListAdapter ula;
    private ArrayList<User> users;
    private AutoCompleteTextView tweetContent;
	
	/**
	 * Find suggested list in layout file and hide this for the first time.
	 * Done but doesn't optimize
	 */
	private void createSuggestedList() {
        // Get object with corresponded id
		suggestedList = (LinearLayout)findViewById(R.id.layout_sussgest);
		tweetScreen   = (LinearLayout)findViewById(R.id.tweet_screen);
        tweetContent  = (AutoCompleteTextView) findViewById(R.id.tweet_content);

        list = (ListView)findViewById(R.id.list_sussgest);
        list.setOnScrollListener(this);
        list.setOnItemClickListener(this);
	}
	
	
	/**
	 * Detect whenever user is mention another people or not
	 * @param s inputed sring
	 * @param start
	 * @param before
	 * @param count
	 * @return mentioning name
	 */
	private String getMentioning(CharSequence s, int start, int before, int count) {
		String lastestWord = getLastestWord(s, start - before);
		if (lastestWord.length() < 2)
			return null;
		if (lastestWord.charAt(0) != '@') return null;
		return lastestWord.substring(1);
	}
	
	private String getLastestWord(CharSequence s, int start){
		int n       = s.length();
        int i       = start + 1;
		String word = "";

        if ((start < 0) || (start > n)) {
            System.out.println("Current cursor is exceed bound of word");
            return "";
        }
        // get character forward
        while (i < n){
            if (isSpaceCharacter(s.charAt(i)))
                i = n;
            else
                word = word + s.charAt(i++);
        }

        // Get character backward
        i = start;
		while (i >= 0){
            if (isSpaceCharacter(s.charAt(i)))
					return word;
			word = s.charAt(i) + word;
			i--;
		}
		return word;
	}

    /**
     *
     * @param c character
     * @return  whenever it is space character or not
     */
    private boolean isSpaceCharacter(char c){
        return ((c == ' ') || (c == '\t') || (c == '\n'));
    }
	
	/**
	 * Show suggest user list for user choose
	 */
	private void showSuggestedList(String mentioning){
        // initialize data
        users = new ArrayList<User>();
		if ((mentioning != "")&&(mentioning != null))
            getDataFromServer(mentioning);
        // Tranfer data to list view
    	ula = new UsersListAdapter(this, R.layout.user_item_suggest, users);
        list.setAdapter(ula);

        // Does nothing if nothing returned
        if (users.size() == 0){
            tweetScreen.bringToFront();
            return;
        }

        // Show list view to front
        suggestedList.bringToFront();
	}
	
	
	/**
	 * get suggested user from server
	 * @param slug
	 */
	private void getDataFromServer(String slug) {
    	// Prepare parameter
		List<NameValuePair> param = new ArrayList<NameValuePair>(2);
		param.add(new BasicNameValuePair("slug", slug));
		System.out.println("slug:"+slug);
		param.add(new BasicNameValuePair("count", "10"));
    	SimpleURI uri = new SimpleURI(ConnectionTwitter.SUGGESTION, param);
    	
    	// Get result from server
    	JSONArray jsonArray = ConnectionTwitter.sendRequestJSonArray(uri.getUrl(), uri.getParam());
    	
    	try{
	    	for (int i=0; i< jsonArray.length(); i++){
	        	users.add(new User((JSONObject)jsonArray.get(i)));
	    	}
    	}catch(Exception e){
    		System.out.println("Error in loop parse users list");
    		return ;
    	}
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        if (i == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
            // Show full size of list suggested
            LayoutParams layoutParams = (LayoutParams) list.getLayoutParams();
            System.out.println(layoutParams.height);
            layoutParams.height = 450;
            list.setLayoutParams(layoutParams);
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {

    }


    @Override
    public void onItemClick(AdapterView<?> av, View arg1, int position, long arg3) {
        /**
         * TODO
         * Replace mentioning text with clicked username
         * 1. STEP 1 : Find the start and end point of currently editing text
         * 2. STEP 2 : replace word with username
         * 3. STEP 3 : Change cursor to the end of username
         */
        User user = ula.getItem(position);
        int cursor = tweetContent.getSelectionStart()-1;
        int start = 0,end = 0;
        String s = tweetContent.getText().toString();
        int n    = s.length();

        // Implement step1
        // get start point of letter
        int i = cursor;
        char c;
        while (i >=0) {
            c = s.charAt(i);
            if (isSpaceCharacter(c)||(c == '@')){
                start = i+1;
                i=0;
            }
            i--;
        }

        // get end point of letter
        i= cursor+1;
        while  (i < n)  {
            c = s.charAt(i);
            if (isSpaceCharacter(c)||(c == '@')){
                end = i-1;
                i=n;
            }
            i++;
        }
        if (end == 0)
            end = n-1;

        // Implement Step2 :Replace mention text with new username
        String newS = "";
        for ( i = 0; i < start; i++) {
            newS += s.charAt(i);
        }
        newS += user.getUsername();
        for ( i= end+ 1; i< n; i++)
            newS += s.charAt(i);
        tweetContent.setText(newS);
    }
}
