package com.oot.twitter;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import com.oot.twitter.info.User;

import android.R.bool;
import android.graphics.Bitmap;

public class GlobalData {
	public static User currentUser;
	public static String user_id;
	public static String username;
	public static String fullname;
	public static String email;
	public static String tweets;
	public static String following;
	public static String follower;
	public static String bio;
	public static String website;
	public static String location;
	public static Bitmap avarta=null;
	public static HttpClient client = new DefaultHttpClient();
	
	public static int timeline_maxid = 0;
	public static int timeline_sinceid = 0;
	public static boolean isGetMore = true;
	
	public static int tweets_maxid = 0;
	public static int tweets_sinceid = 0;
	
	// For pass object param to another activity
	public static Object paramIntent;
}

/*
private class GetMoreData extends AsyncTask<String,Void,String> {//Get Old Data when Scroll Endlist
    @Override
    protected void onPreExecute() 
    {
    	
    }  
	@Override
	protected String doInBackground(String... params) 
	{
		// TODO Auto-generated method stub
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
   			 	
    			TweetItem tweet_item = new TweetItem(jsonObject.getString("tweet_id"), jsonObject.getString("content"), jsonObject.getString("created_time"), jsonObject.getString("username"), jsonObject.getString("full_name"),bitmap );
    			m_tweet_item.add(tweet_item);
    			

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

		m_adapter.notifyDataSetChanged();	    	
		list.onRefreshComplete();
        super.onPostExecute("a");
    }    	 
}    

*/