package com.open.hot.view;

import java.util.HashMap;
import java.util.Map;

import com.open.lib.TouchView;

import android.view.View;
import android.view.ViewGroup;

public class ViewPool {

	public String tag = "ViewPool";

	public Map<String, ViewEntity> pool = new HashMap<String, ViewEntity>();
	public ViewGroup container;

	public class ViewEntityStatus {
		public int SHOW = 0, HIDE = 1, FREED = 2;
		public int state = SHOW;
	}

	public class ViewEntity {
		public String key;
		public View view;
		public ViewEntityStatus status = new ViewEntityStatus();

	}

	public void putView(String key, View view) {

		ViewEntity viewEntity = new ViewEntity();
		viewEntity.key = key;
		viewEntity.view = view;
		viewEntity.status.state = viewEntity.status.SHOW;

		pool.put(key, viewEntity);
		if (container != null) {
			container.addView(view, 0);
		}
	}

	public void putView(String key, View view, TouchView.LayoutParams layoutParams) {

		ViewEntity viewEntity = new ViewEntity();
		viewEntity.key = key;
		viewEntity.view = view;
		viewEntity.status.state = viewEntity.status.SHOW;

		pool.put(key, viewEntity);
		if (container != null) {
			container.addView(view, 0, layoutParams);
		}
	}
	
	public View getView(String key) {

		ViewEntity viewEntity = pool.get(key);
		if (viewEntity != null && viewEntity.status.state != viewEntity.status.FREED) {

			viewEntity.status.state = viewEntity.status.SHOW;
			viewEntity.view.setVisibility(View.VISIBLE);
			return viewEntity.view;
		} else {
			return null;
		}

	}

}