package com.open.welinks.controller;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.text.TextWatcher;
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
import com.open.welinks.model.Data;
import com.open.welinks.view.HotView;
import com.open.welinks.view.HotView.Status;

public class HotController {
	public Data data = Data.getInstance();
	public String tag = "LoginController";

	public Runnable animationRunnable;
	public Runnable showSoftInputRunnable;
	public Runnable remainRegisterRunnable;
	public Runnable remainResetPasswordRunnable;
	public Runnable remainLoginRunnable;
	int remainRegister;
	int remainResetPassword;
	int remainLogin;
	String registerPhone = "";
	String resetPasswordPhone = "";
	String loginPhone = "";

	public Context context;
	public HotView thisView;
	public HotController thisController;
	public Activity thisActivity;

	public OnFocusChangeListener mOnFocusChangeListener;
	public OnClickListener mOnClickListener;
	public TextWatcher mTextWatcher1;
	public TextWatcher mTextWatcher2;
	public OnTouchListener onTouchListener;

	public Handler handler = new Handler();
	public String url_userauth = "http://www.we-links.com/api2/account/auth";

	public Gson gson = new Gson();

	public MySpringListener mSpringListener = new MySpringListener();

	public InputMethodManager mInputMethodManager;

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
				if (view.equals(thisView.input1)) {
					if (hasFocus && !thisView.input1.getText().toString().equals("")) {
						thisView.clearInput1.setVisibility(View.VISIBLE);
					} else {
						thisView.clearInput1.setVisibility(View.INVISIBLE);
					}
				} else if (view.equals(thisView.input2)) {
					if (hasFocus && !thisView.input2.getText().toString().equals("")) {
						thisView.clearInput2.setVisibility(View.VISIBLE);
					} else {
						thisView.clearInput2.setVisibility(View.INVISIBLE);
					}
				}
			}
		};

		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(thisView.loginButton)) {
				} else if (view.equals(thisView.registerButton)) {
					}
			}
		};

	}

	public void bindEvent() {
		thisView.input1.addTextChangedListener(mTextWatcher1);
		thisView.input2.addTextChangedListener(mTextWatcher2);

		thisView.input1.setOnFocusChangeListener(mOnFocusChangeListener);
		thisView.input2.setOnFocusChangeListener(mOnFocusChangeListener);

		thisView.input1.setOnClickListener(mOnClickListener);
		thisView.input2.setOnClickListener(mOnClickListener);
		thisView.loginButton.setOnClickListener(mOnClickListener);
		thisView.registerButton.setOnClickListener(mOnClickListener);
		thisView.rightTopTextButton.setOnClickListener(mOnClickListener);
		thisView.leftTopText.setOnClickListener(mOnClickListener);
		thisView.clearInput1.setOnClickListener(mOnClickListener);
		thisView.clearInput2.setOnClickListener(mOnClickListener);
		thisView.mainButton.setOnClickListener(mOnClickListener);
		thisView.leftBottomTextButton.setOnClickListener(mOnClickListener);
		thisView.rightBottomTextButton.setOnClickListener(mOnClickListener);
		thisView.mRootView.setOnTouchListener(onTouchListener);
	}

	public void onCreate() {
		thisView.status = Status.loginOrRegister;
		thisView.progressBar.setProgress(0);
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
			}
		}, 500);
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
			thisView.appIconToName.setScaleX(mappedValue);
			thisView.appIconToName.setScaleY(mappedValue);
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


}
