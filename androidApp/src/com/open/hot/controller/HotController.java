package com.open.hot.controller;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringUtil;
import com.google.gson.Gson;
import com.open.hot.model.Data;
import com.open.hot.view.HotView;
import com.open.hot.view.HotView.Status;

public class HotController {
	public Data data = Data.getInstance();
	public String tag = "LoginController";

	public Context context;
	public HotView thisView;
	public HotController thisController;
	public Activity thisActivity;

	public OnFocusChangeListener mOnFocusChangeListener;
	public OnClickListener mOnClickListener;
	public OnTouchListener onTouchListener;

	public Gson gson = new Gson();

	public MySpringListener mSpringListener = new MySpringListener();

	public InputMethodManager mInputMethodManager;
	public GestureDetector mGesture;

	public HotController(Activity activity) {
		this.context = activity;
		this.thisActivity = activity;
	}

	public void initializeListeners() {
		onTouchListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				if (action == MotionEvent.ACTION_DOWN) {
					thisView.mScaleSpring.setEndValue(1);
				} else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
					thisView.mScaleSpring.setEndValue(0);
				}
				return true;
			}
		};
		mOnFocusChangeListener = new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View view, boolean hasFocus) {
			}
		};

		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(thisView.logo)) {
					Log.d(tag, "logo");
					if (thisView.mScaleCardSpring.getEndValue() == 0) {
						thisView.mScaleCardSpring.setEndValue(1);
					} else {
						thisView.mScaleCardSpring.setEndValue(0);
					}
				}
			}
		};

	}

	public void bindEvent() {
		thisView.logo.setOnClickListener(mOnClickListener);
	}

	public void onCreate() {
		thisView.status = Status.loginOrRegister;

		mGesture = new GestureDetector(thisActivity, new GestureListener());
	}

	public void onResume() {
		thisView.mScaleSpring.addListener(mSpringListener);
	}

	public void onPause() {
		thisView.mScaleSpring.removeListener(mSpringListener);
	}

	public void onDestroy() {

	}

	private class MySpringListener extends SimpleSpringListener {
		@Override
		public void onSpringUpdate(Spring spring) {
			float mappedValue = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 1, 0.5);
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean flag = false;
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (thisView.status == Status.loginUsePassword) {
			} else if (thisView.status == Status.verifyPhoneForLogin) {

			} else if (thisView.status == Status.verifyPhoneForRegister) {

			} else if (thisView.status == Status.verifyPhoneForResetPassword) {
			} else {
				thisActivity.finish();
			}
		} else {
		}
		return flag;
	}

	public boolean onTouchEvent(MotionEvent event) {
		int motionEvent = event.getAction();
		float y = event.getY();
		if (motionEvent == MotionEvent.ACTION_DOWN) {
			if (y < thisView.displayMetrics.heightPixels - thisView.cardHeight) {
				thisView.mainPagerBody.onTouchDown(event);
			} else {
				thisView.cardListBody.onTouchDown(event);
			}

		} else if (motionEvent == MotionEvent.ACTION_MOVE) {
			if (y < thisView.displayMetrics.heightPixels - thisView.cardHeight) {
				thisView.mainPagerBody.onTouchMove(event);
			} else {
				thisView.cardListBody.onTouchMove(event);
			}
		} else if (motionEvent == MotionEvent.ACTION_UP) {
			if (y < thisView.displayMetrics.heightPixels - thisView.cardHeight) {
				thisView.mainPagerBody.onTouchUp(event);
			} else {
				thisView.cardListBody.onTouchUp(event);
			}
		}
		mGesture.onTouchEvent(event);
		return true;
	}

	class GestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if (thisView.mainPagerBody.bodyStatus.state == thisView.mainPagerBody.bodyStatus.HOMING) {
				thisView.mainPagerBody.onFling(velocityX, velocityY);
			}
			thisView.cardListBody.onFling(velocityX, velocityY);
			return true;
		}
	}

}
