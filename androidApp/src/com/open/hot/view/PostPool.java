package com.open.hot.view;

import java.util.HashMap;
import java.util.Map;

import android.view.ViewGroup;

import com.open.lib.TouchView;

public class PostPool {

	public String tag = "PostPool";

	public Map<String, PostBody> pool = new HashMap<String, PostBody>();
	public ViewGroup container;

	public void putView(String key, PostBody postBody) {
		pool.put(key, postBody);
		if (container != null) {
			container.addView(postBody.postView, 0);
		}
	}

	public void putView(String key, PostBody postBody, TouchView.LayoutParams layoutParams) {
		pool.put(key, postBody);
		if (container != null) {
			container.addView(postBody.postView, 0, layoutParams);
		}
	}
	
	public PostBody getView(String key) {
		PostBody postBody = pool.get(key);
		if (postBody != null && postBody.status.state != postBody.status.FREED) {
			return postBody;
		} else {
			return null;
		}
	}

}