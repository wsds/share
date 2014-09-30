package com.open.hot.view;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.animation.Animation;

import com.facebook.rebound.BaseSpringSystem;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.open.hot.R;
import com.open.hot.controller.HotController;
import com.open.hot.model.Data;
import com.open.lib.TouchImageView;
import com.open.lib.TouchView;

public class HotView {
	public Data data = Data.getInstance();
	public String tag = "LoginView";

	public Context context;
	public HotView thisView;
	public HotController thisController;
	public Activity thisActivity;

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

	public DisplayMetrics displayMetrics;
	public LayoutInflater mInflater;
	public TouchView main_container;

	public void initView() {
		displayMetrics = new DisplayMetrics();

		thisActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		mInflater = thisActivity.getLayoutInflater();

		thisActivity.setContentView(R.layout.activity_hot);
		// thisActivity.setContentView(R.layout.view_card);
		main_container = (TouchView) thisActivity.findViewById(R.id.main_container);

		int cardWidth = (int) (displayMetrics.widthPixels * 4 / 9);
		int cardHeight = (int) (cardWidth * 1.78f);
		TouchView.LayoutParams layoutParams = new TouchView.LayoutParams(cardWidth, cardHeight);

		TouchView cardView1 = drawCardView();
		main_container.addView(cardView1, layoutParams);
		cardView1.setX(0);

		TouchView cardView2 = drawCardView();
		main_container.addView(cardView2, layoutParams);
		cardView2.setX(cardWidth + 2 * displayMetrics.density);
		
		TouchView cardView3 = drawCardView();
		main_container.addView(cardView3, layoutParams);
		cardView3.setX((cardWidth + 2 * displayMetrics.density)*2);

	}

	public TouchView drawCardView() {

		TouchView cardView;

		cardView = (TouchView) mInflater.inflate(R.layout.view_card, null);

		int cardWidth = (int) (displayMetrics.widthPixels * 4 / 9);
		int cardHeight = (int) (cardWidth * 1.78f);

		int imageHeight = (int) (cardWidth - displayMetrics.density * 20);
		cardView.setY(displayMetrics.heightPixels - 38 - cardHeight);

		TouchImageView cardImage = (TouchImageView) cardView.findViewById(R.id.content_image);

		TouchView.LayoutParams imageLayoutParams = new TouchView.LayoutParams(imageHeight, imageHeight);
		cardImage.setLayoutParams(imageLayoutParams);
		cardImage.setY(cardHeight - imageHeight - displayMetrics.density * 20);

		return cardView;
	}
}