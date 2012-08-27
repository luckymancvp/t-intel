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

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SignUp extends Activity implements OnClickListener {
	Button btnSignUp;
	EditText username,password,email,fullname;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.twitter_signup_layout);
        
        TextView tv = (TextView)findViewById(R.id.term);
        String txt = "By tapping \"Sign Up\" above, you are agreeing to the <a href='https://twitter.com/tos/'>Terms of Services</a> and <a href='https://twitter.com/privacy'>Privacy Policy</a>";
        tv.setText(Html.fromHtml(txt));
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        
        btnSignUp = (Button) findViewById(R.id.btnSignUpSubmit);
        btnSignUp.setOnClickListener(this);
        
    }

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
			case R.id.btnSignUpSubmit:
				//System.out.println("START SUBMIT TTTTTTTTa");
				
				username = (EditText)findViewById(R.id.signup_username);
				password = (EditText)findViewById(R.id.signup_password);
				email = (EditText)findViewById(R.id.signup_email);
				fullname = (EditText)findViewById(R.id.signup_fullname);
				
				String user_name = this.username.getText().toString();
				String passwd = this.password.getText().toString();
				String mail = this.email.getText().toString();
				String full_name = this.fullname.getText().toString();
				StringBuilder builder = new StringBuilder();
				HttpClient client = new DefaultHttpClient();
				
				System.out.println(user_name + "|"+passwd+"|"+mail+"|"+full_name);
				
				HttpPost httpPost = new HttpPost("http://192.168.1.10/twitter/api/site/signup?");
				
				
		    	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4); 
		    	nameValuePairs.add(new BasicNameValuePair("username", user_name)); 
		    	nameValuePairs.add(new BasicNameValuePair("password", passwd)); 
		    	nameValuePairs.add(new BasicNameValuePair("email", mail)); 
		    	nameValuePairs.add(new BasicNameValuePair("full_name", full_name)); 
		    	try {
					httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}		
				   	
		    	try{
		    		HttpResponse response = client.execute(httpPost);
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
				break;
		
		}
	}
}
