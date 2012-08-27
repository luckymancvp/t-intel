package com.oot.twitter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ConnectionTwitter {
	
	public static final String SERVER_URL   = "http://192.168.1.10/twitter/";
	
	// Site API
	public static final String LOGIN_URL  = SERVER_URL+"api/site/signin";
	
	// User API
	public static final String FOLLOWER_URL  = SERVER_URL+"api/user/followers";
	public static final String FOLLOWING_URL = SERVER_URL+"api/user/following";
	public static final String FOLLOW_URL    = SERVER_URL+"api/user/follow";
	public static final String UNFOLLOW_URL  = SERVER_URL+"api/user/unfollow";
	public static final String SUGGESTION    = SERVER_URL+"api/user/search";
	
	// Tweet API
	public static final String FAVORITEDBY  = SERVER_URL+"api/tweet/favoritedBy";
	public static final String count_refav  = SERVER_URL+"api/tweet/countRefav";

	/**
	 * Send request to twitter server
	 * @param String requestString
	 * @return JSONObject objectJson
	 */
	public static JSONObject sendRequest(String requestString){
		StringBuilder builder = new StringBuilder();
		HttpGet httpGet = new HttpGet(requestString);
    	try{
    		HttpResponse response = GlobalData.client.execute(httpGet);
    		StatusLine statusLine = response.getStatusLine();
    		
    		int statusCode = statusLine.getStatusCode();
    		if (statusCode != 200){
    			System.out.println("status code return not equal 200 :"+requestString);
    			return null;
    		}

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
    		System.out.println("Error when send request to "+requestString);
    		return null;
    	}
		try {
			return new JSONObject(builder.toString());
		} catch (JSONException e) {
			System.out.println("Error when parse json");
			return null;
		}
	}
	
	/**
	 * Send request to twitter server and get String return
	 * @param requestString
	 * @param nameValuePairs
	 * @return String
	 */
	public static String sendRequest(String requestString, List<NameValuePair> nameValuePairs){
		StringBuilder builder = new StringBuilder();
		HttpPost httpPost = new HttpPost(requestString);
		if (nameValuePairs != null)
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e1) {
			System.out.println("Error in pass param to Http Post");
		}
		
    	try{
    		HttpResponse response = GlobalData.client.execute(httpPost);
    		StatusLine statusLine = response.getStatusLine();

    		
    		int statusCode = statusLine.getStatusCode();
    		if (statusCode != 200){
    			System.out.println("status code return not equal 200 :"+statusCode + " in " + requestString);
    			return null;
    		}

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
    		System.out.println("Error when send request to "+requestString);
    		return null;
    	}
    	return builder.toString();
	}
	
	/**
	 * Send request to twitter server
	 * @param requestString
	 * @param nameValuePairs
	 * @return jsonObject
	 */
	public static JSONObject sendRequestJSon(String requestString, List<NameValuePair> nameValuePairs){
		String result = sendRequest(requestString, nameValuePairs);
		try {
			return new JSONObject(result);
		} catch (JSONException e) {
			System.out.println("Error in parse Json. String : "+result);
			return null;
		}
	}
	
	/**
	 * Send request to twitter server
	 * @param requestString
	 * @param nameValuePairs
	 * @return jsonObject
	 */
	public static JSONArray sendRequestJSonArray(String requestString, List<NameValuePair> nameValuePairs){
		String result = sendRequest(requestString, nameValuePairs);
		try {
			return new JSONArray(result);
		} catch (JSONException e) {
			System.out.println("Error in parse Json");
			return null;
		}
	}
}
