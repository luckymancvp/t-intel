package com.oot.twitter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.oot.twitter.info.User;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignIn extends Activity implements OnClickListener {
	Button btnSignIn,btnBackToSignUp;
	EditText username,password;
	
    ProgressDialog dialog;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.twitter_signin_layout);
        
		this.username = (EditText)findViewById(R.id.signin_username);
		this.password = (EditText)findViewById(R.id.signin_password);
        this.btnSignIn = (Button)findViewById(R.id.btnSignInSubmit);
        this.btnBackToSignUp = (Button)findViewById(R.id.btnBackToSignUp);
        
        password.addTextChangedListener(new TextWatcher() {		
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if(s.length()>0 && username.length()>0)
				{
					btnSignIn.setTextColor(Color.BLACK);
					btnSignIn.setBackgroundResource(R.drawable.btn_submit_default);
				}
				if(s.length() <=0 || username.length() <=0)
				{
					btnSignIn.setTextColor(Color.parseColor("#ACAAAC"));
					btnSignIn.setBackgroundResource(R.drawable.btn_submit_disabled);
				}
			}		
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub		
			}		
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub				
			}
		});
        username.addTextChangedListener(new TextWatcher() {		
			public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				if(s.length() >0 && password.length() >0)
				{
					btnSignIn.setTextColor(Color.BLACK);
					btnSignIn.setBackgroundResource(R.drawable.btn_submit_default);					
				}
				if(s.length()<=0 || password.length()<=0)
				{
					btnSignIn.setTextColor(Color.parseColor("#ACAAAC"));
					btnSignIn.setBackgroundResource(R.drawable.btn_submit_disabled);					
				}
			}	
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub			
			}			
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub			
			}
		});
        
        
        this.btnSignIn.setOnClickListener(this);
        this.btnBackToSignUp.setOnClickListener(this);
    }

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
			case R.id.btnSignInSubmit:
				
				
				username = (EditText)findViewById(R.id.signin_username);
				password = (EditText)findViewById(R.id.signin_password);

				
				String user_name = this.username.getText().toString();
				String passwd = this.password.getText().toString();

				StringBuilder builder = new StringBuilder();
				GlobalData.client = new DefaultHttpClient();
				
				//System.out.println(user_name + "|"+passwd);
				
				HttpPost httpPost = new HttpPost(ConnectionTwitter.SERVER_URL+"api/site/signin?");
				//System.out.println(ConnectionTwitter.SERVER_URL+"api/site/signin?");
				
				
		    	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2); 
		    	nameValuePairs.add(new BasicNameValuePair("username", user_name)); 
		    	nameValuePairs.add(new BasicNameValuePair("password", passwd)); 

		    	
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
				
		    	//System.out.println(builder.toString());
		    	
		    	TextView tv = (TextView)findViewById(R.id.login_result);
		    	tv.setText(builder.toString());
		    	
		    	String login_resutl = builder.toString();
		    	login_resutl = "["+login_resutl+"]";
		    	try {
		    		JSONArray json = new JSONArray(login_resutl);
		    		//System.out.println("Length = " + json.length());
		    		JSONObject jsonObject = json.getJSONObject(0);
		    		String return_value = jsonObject.getString("result");
		    		int res = Integer.parseInt(return_value);
		    		if(res == 1)
		    		{
		    			/** TODO
		    			 * Get info of current login user by this object
		    			 */
		    			GlobalData.currentUser = new User(jsonObject.getJSONObject("user"));
		    			GlobalData.username = user_name;
		    			
				    	Intent home = new Intent(SignIn.this,Twitter.class);
				    	startActivity(home);

				    	onStop();
				    	onDestroy();
				    	
				    	finish();  

		    		}
		    		else
		    		{
		    			Toast.makeText(this, "Username or Password is incorrect", (int) 1.0);
		    			//tv.setText("Username or Password is incorrect");
		    		}
		    		
		    	} catch (JSONException e){ 
		    		// TODO Auto-generated catch block
		    		tv.setText("Return Value = "+builder.toString());
		    		e.printStackTrace();
		    	}
		    	
		    	
				break;
				
			case R.id.btnBackToSignUp:
				Intent i = new Intent(SignIn.this,SignUp.class);
				startActivity(i);
				finish();	
				break;
		
		}
	}
	
	@Override
	protected void onStop() {
	    setResult(2);
	    super.onStop();
	}
	@Override
	protected void onDestroy() {
	    setResult(2);
	    super.onDestroy();
	}	
}
