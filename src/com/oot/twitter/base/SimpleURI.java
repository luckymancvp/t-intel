package com.oot.twitter.base;

import java.util.List;

import org.apache.http.NameValuePair;

public class SimpleURI {
	private String header;
	private String url;
	private List<NameValuePair> param;
	
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List<NameValuePair> getParam() {
		return param;
	}
	public void setParam(List<NameValuePair> param) {
		this.param = param;
	}
	
	/**
	 * @param url Address of api
	 * @param param Param pass to api
	 */
	public SimpleURI( String url, List<NameValuePair> param) {
		this.url = url;
		this.param         = param;
	}
	
	/**
	 * @param url Address of api
	 * @param param Param pass to api
	 */
	public SimpleURI( String header, String url, List<NameValuePair> param) {
		this.header = header;
		this.url    = url;
		this.param  = param;
	}
}
