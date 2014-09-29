package com.open.welinks.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.facebook.rebound.BaseSpringSystem;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.open.welinks.R;
import com.open.welinks.controller.HotController;
import com.open.welinks.model.Data;

public class HotView {
	public Data data = Data.getInstance();
	public String tag = "LoginView";

	public Context context;
	public HotView thisView;
	public HotController thisController;
	public Activity thisActivity;

	public View loginOrRegister;
	public View loginOrRegisterButton;
	public View loginButton;
	public View registerButton;

	public View card;
	public TextView leftTopText;
	public TextView rightTopTextButton;
	public TextView error_message;
	public EditText input1;
	public EditText input2;
	public View clearInput1;
	public View clearInput2;
	public TextView mainButton;
	public TextView leftBottomTextButton;
	public TextView rightBottomTextButton;
	public ImageView appIconToName;
	public ProgressBar progressBar;
	public ImageView cardTopLine;

	public View mRootView;

	public Animation animationNextOut;
	public Animation animationNextIn;
	public Animation animationBackOut;
	public Animation animationBackIn;

	public enum Status {
		welcome, start, loginOrRegister, loginUsePassword, verifyPhoneForRegister, verifyPhoneForResetPassword, verifyPhoneForLogin, setPassword, resetPassword
	}

	String 状态机;

	public Status status = Status.welcome;

	public SpringConfig ORIGAMI_SPRING_CONFIG = SpringConfig.fromOrigamiTensionAndFriction(10, 2);

	public BaseSpringSystem mSpringSystem = SpringSystem.create();
	public Spring mScaleSpring = mSpringSystem.createSpring().setSpringConfig(ORIGAMI_SPRING_CONFIG);

	public HotView(Activity activity) {
		this.context = activity;
		this.thisActivity = activity;
	}

	public void initView() {

		thisActivity.setContentView(R.layout.activity_login);

		animationNextOut = AnimationUtils.loadAnimation(context, R.anim.animation_next_out);
		animationNextIn = AnimationUtils.loadAnimation(context, R.anim.animation_next_in);
		animationBackOut = AnimationUtils.loadAnimation(context, R.anim.animation_back_out);
		animationBackIn = AnimationUtils.loadAnimation(context, R.anim.animation_back_in);

		loginOrRegister = thisActivity.findViewById(R.id.loginOrRegister);
		loginOrRegisterButton = thisActivity.findViewById(R.id.loginOrRegisterButton);
		loginButton = thisActivity.findViewById(R.id.loginButton);
		registerButton = thisActivity.findViewById(R.id.registerButton);

		card = thisActivity.findViewById(R.id.card);
		leftTopText = (TextView) thisActivity.findViewById(R.id.leftTopText);
		rightTopTextButton = (TextView) thisActivity.findViewById(R.id.rightTopTextButton);
		input1 = (EditText) thisActivity.findViewById(R.id.input1);
		input2 = (EditText) thisActivity.findViewById(R.id.input2);
		clearInput1 = thisActivity.findViewById(R.id.clearInput1);
		clearInput2 = thisActivity.findViewById(R.id.clearInput2);
		mainButton = (TextView) thisActivity.findViewById(R.id.mainButton);
		leftBottomTextButton = (TextView) thisActivity.findViewById(R.id.leftBottomTextButton);
		rightBottomTextButton = (TextView) thisActivity.findViewById(R.id.rightBottomTextButton);
		appIconToName = (ImageView) thisActivity.findViewById(R.id.appIconToName);
		error_message = (TextView) thisActivity.findViewById(R.id.err_message);
		progressBar = (ProgressBar) thisActivity.findViewById(R.id.progressBar);
		cardTopLine = (ImageView) thisActivity.findViewById(R.id.cardTopLine);

		cardTopLine.setVisibility(View.GONE);
		progressBar.setVisibility(View.VISIBLE);
		mRootView = appIconToName;
	}

	int remainRegister = 0;
	int remainLogin = 0;
	int remainResetPassword = 0;

}