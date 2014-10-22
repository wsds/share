package com.open.hot;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.open.hot.controller.HotController;
import com.open.hot.model.Data;
import com.open.hot.view.HotView;
import com.open.hot.view.ViewManage;
import com.open.hot.view.HotView.Status;

public class HotActivity extends Activity {
	public Data data = Data.getInstance();
	public String tag = "LoginActivity";

	public Context context;
	public HotView thisView;
	public HotController thisController;
	public Activity thisActivity;

	public ViewManage viewManager = ViewManage.getInstance();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		linkViewController();

		if (thisView.status == Status.welcome) {
			thisView.status = Status.welcome;
		} else {
			thisView.status = Status.start;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		thisController.onResume();
		// data.localStatus.thisActivityName = "LoginActivity";
	}

	public void linkViewController() {
		this.thisActivity = this;
		this.context = this;
		this.thisView = new HotView(thisActivity);
		this.thisController = new HotController(thisActivity);
		this.thisView.thisController = this.thisController;
		this.thisController.thisView = this.thisView;

		viewManager.loginView = this.thisView;

		thisController.onCreate();
		thisController.initializeListeners();
		thisView.initView();
		thisController.bindEvent();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// MenuInflater inflater = getMenuInflater();
		// inflater.inflate(R.menu.menu_debug_1, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		thisController.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		thisController.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return thisController.onKeyDown(keyCode, event);
	}
	

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		return thisController.onTouchEvent(event);
	}

}
