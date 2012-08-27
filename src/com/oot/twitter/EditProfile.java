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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.oot.twitter.Twitter.TweetItem;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.style.BulletSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class EditProfile extends Activity implements OnClickListener {
	public String id,fullname,bio,website,location;
	Bitmap avarta;
	Bitmap avatar_temp;
	ImageView edit_avarta;
	EditText edit_name,edit_bio,edit_web,edit_location;
	
	Button submit,cancel;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.edit_profile);
        
        Bundle receiveBundle = this.getIntent().getExtras();
        this.id = receiveBundle.getString("id");
        this.fullname = receiveBundle.getString("fullname");
        if(!receiveBundle.getString("bio").equals("null"))
        	this.bio = receiveBundle.getString("bio");
        else
        	this.bio = "";
        if(!receiveBundle.getString("website").equals("null"))
        	this.website = receiveBundle.getString("website");
        else
        	this.website = "";
        if(!receiveBundle.getString("location").equals("null"))
        	this.location = receiveBundle.getString("location");
        else
        	this.location = "";
        this.avarta = (Bitmap)getIntent().getParcelableExtra("avarta");
        
        
        initializeData();
    }
    public void initializeData()
    {
    	LinearLayout change_avatar = (LinearLayout)findViewById(R.id.edit_profile_change_avatar);
    	
        edit_avarta = (ImageView)findViewById(R.id.edit_profile_avarta);
        edit_name = (EditText)findViewById(R.id.edit_profile_name);
    	edit_bio = (EditText)findViewById(R.id.edit_profile_bio);
    	edit_web = (EditText)findViewById(R.id.edit_profile_website);
    	edit_location = (EditText)findViewById(R.id.edit_profile_location);
    	
    	edit_avarta.setImageBitmap(ImageHelper.getRoundedCornerBitmap(this.avarta, 5));
    	edit_name.setText(this.fullname);
    	edit_bio.setText(this.bio);
    	edit_web.setText(this.website);
    	edit_location.setText(this.location);
    	
    	submit = (Button)findViewById(R.id.edit_profile_submit);
    	cancel = (Button)findViewById(R.id.edit_profile_cancel);
    	
    	submit.setOnClickListener(this);
    	cancel.setOnClickListener(this);
    	change_avatar.setOnClickListener(this);
    }
    
    private class SubmitProfile extends AsyncTask<String,Void,String> {
    	ProgressDialog dialog;
        @Override
        protected void onPreExecute() 
        {
        	dialog = ProgressDialog.show(EditProfile.this, "", "Updating Profile...",true);
        }  
		@Override
		protected String doInBackground(String... params) 
		{
			// TODO Auto-generated method stub
	    	StringBuilder builder = new StringBuilder();
	    	
	    	HttpPost httpPost = new HttpPost("http://192.168.1.10/twitter/api/user/updateprofile?");
	    	
	    	System.out.println(EditProfile.this.fullname+"|"+EditProfile.this.bio+"|"+EditProfile.this.website+"|"+EditProfile.this.location);
	    	
	    	
	    	
	    	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4); 
	    	nameValuePairs.add(new BasicNameValuePair("full_name", EditProfile.this.fullname )); 
	    	nameValuePairs.add(new BasicNameValuePair("bio", EditProfile.this.bio)); 
	    	nameValuePairs.add(new BasicNameValuePair("website", EditProfile.this.website)); 
	    	nameValuePairs.add(new BasicNameValuePair("location", EditProfile.this.location)); 
	    	
	    	
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
	    	
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			dialog.dismiss();
        }    	 
    }  

    private class SubmitAvatar extends AsyncTask<String,Void,String> {
    	ProgressDialog dialog;
        @Override
        protected void onPreExecute() 
        {
        	dialog = ProgressDialog.show(EditProfile.this, "", "Updating Avatar...",true);
        }  
		@Override
		protected String doInBackground(String... params) 
		{
			// TODO Auto-generated method stub
	    	StringBuilder builder = new StringBuilder();
	    	
			String ba1="";
			if(EditProfile.this.avatar_temp!=null)
			{
				ByteArrayOutputStream bao = new ByteArrayOutputStream();
				EditProfile.this.avatar_temp.compress(Bitmap.CompressFormat.JPEG, 90, bao);
				byte [] ba = bao.toByteArray();
			    ba1=Base64.encodeBytes(ba);
			}
	    	
	    	HttpPost httpPost = new HttpPost("http://192.168.1.10/twitter/api/user/updateavatar?");	    	
	    	
	    	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1); 
	    	nameValuePairs.add(new BasicNameValuePair("avatar", ba1 )); 	    	
	    	
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
	    	
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			dialog.dismiss();
			edit_avarta.setImageBitmap(ImageHelper.getRoundedCornerBitmap(avatar_temp, 10));
			avarta = avatar_temp;			
        }    	 
    }       
    
	public void onClick(View v) 
	{
		// TODO Auto-generated method stub
		switch(v.getId())
		{
			case R.id.edit_profile_submit:
				this.bio = edit_bio.getText().toString();
				this.location = edit_location.getText().toString();
				this.website = edit_web.getText().toString();
				this.fullname = edit_name.getText().toString();
				new SubmitProfile().execute(new String[]{"ABCD"});
				
				
				Intent return_intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("fullname", this.fullname);
				bundle.putString("location", this.location);
				bundle.putString("website", this.website);
				bundle.putString("bio", this.bio);
				
				System.out.println("EDIT|"+this.fullname+"|"+this.website+"|"+this.bio+"|"+this.location);
				
				return_intent.putExtras(bundle);
				return_intent.putExtra("avatar", this.avarta);
				
				setResult(0, return_intent);
				
				finish();				
				break;
			case R.id.edit_profile_cancel:
				
				setResult(0);
				finish();
				break;
			case R.id.edit_profile_change_avatar:
				dispatchPictureSDCard(1);
				break;
		
		}
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
	    if(requestCode == 1)
	    {
	    	//System.out.println("CHOOSE IMAGES !!!" + data);
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

	    			avatar_temp = BitmapFactory.decodeFile(filePath);
	    			avatar_temp = Bitmap.createScaledBitmap(avatar_temp, 160, 160, false);
	    			
	    			if(avatar_temp != null)
	    			{
	    				//System.out.println("BBB|"+avatar_temp);
	    				new SubmitAvatar().execute(new String[]{"ABCD"});	    				
	    			}
	    		}	
	    	}
	    }
	}
}
