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
import com.open.lib.TouchTextView;
import com.open.lib.TouchView;
import com.open.lib.viewbody.BodyCallback;
import com.open.lib.viewbody.PagerBody;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class HotView {
	public Data data = Data.getInstance();
	public String tag = "LoginView";

	public Context context;
	public HotView thisView;
	public HotController thisController;
	public Activity thisActivity;

	public ImageLoader imageLoader = ImageLoader.getInstance();
	public DisplayImageOptions options;

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
	public TouchView bigCardView;
	public TouchView big_card_container;

	public PagerBody mainPagerBody;

	public void initView() {
		displayMetrics = new DisplayMetrics();

		thisActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		mInflater = thisActivity.getLayoutInflater();

		imageLoader.init(ImageLoaderConfiguration.createDefault(this.thisActivity));
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).displayer(new RoundedBitmapDisplayer((int) (5 * displayMetrics.density))).build();

		thisActivity.setContentView(R.layout.activity_hot);
		// thisActivity.setContentView(R.layout.view_card);
		main_container = (TouchView) thisActivity.findViewById(R.id.main_container);

		BodyCallback myBodyCallback = new BodyCallback();

		mainPagerBody = new PagerBody();
		mainPagerBody.tag = "mainPagerBody";
		mainPagerBody.pager_indicator = null;
		mainPagerBody.pager_indicator_trip = 10;
		mainPagerBody.initialize(displayMetrics, myBodyCallback);

		TouchView bigCardView1 = (TouchView) mInflater.inflate(R.layout.view_card_big, null);
		main_container.addView(bigCardView1, 0);
		mainPagerBody.addChildView(bigCardView1);
		TouchTextView title1 = (TouchTextView) bigCardView1.findViewById(R.id.title);
		mainPagerBody.setTitleView(title1, 0);
		TouchImageView background_image1 = (TouchImageView) bigCardView1.findViewById(R.id.background_image);
		imageLoader.displayImage("drawable://" + R.drawable.login_background_2, background_image1, options);

		bigCardView = (TouchView) mInflater.inflate(R.layout.view_card_big, null);
		main_container.addView(bigCardView, 0);
		mainPagerBody.addChildView(bigCardView);
		bigCardView.setX(-0);
		big_card_container = (TouchView) bigCardView.findViewById(R.id.big_card_container);
		TouchTextView title = (TouchTextView) bigCardView.findViewById(R.id.title);
		mainPagerBody.setTitleView(title, 1);
		TouchImageView background_image = (TouchImageView) big_card_container.findViewById(R.id.background_image);
		imageLoader.displayImage("drawable://" + R.drawable.login_background_1, background_image, options);

		TouchView bigCardView2 = (TouchView) mInflater.inflate(R.layout.view_card, null);
		main_container.addView(bigCardView2, 0);
		mainPagerBody.addChildView(bigCardView2);
		TouchTextView title2 = (TouchTextView) bigCardView2.findViewById(R.id.title);
		mainPagerBody.setTitleView(title2, 2);
		TouchImageView background_image2 = (TouchImageView) bigCardView2.findViewById(R.id.content_image);
		imageLoader.displayImage("drawable://" + R.drawable.login_background_1, background_image2, options);

		int cardWidth = (int) (displayMetrics.widthPixels * 4 / 9);
		int cardHeight = (int) (cardWidth * 1.78f);
		TouchView.LayoutParams layoutParams = new TouchView.LayoutParams(cardWidth, cardHeight);

		TouchView cardView1 = drawCardView();
		big_card_container.addView(cardView1, layoutParams);
		cardView1.setX(0);

		TouchView cardView2 = drawCardView1();
		big_card_container.addView(cardView2, layoutParams);
		cardView2.setX(cardWidth + 2 * displayMetrics.density);

		TouchView cardView3 = drawCardView();
		big_card_container.addView(cardView3, layoutParams);
		cardView3.setX((cardWidth + 2 * displayMetrics.density) * 2);

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

	public TouchView drawCardView1() {

		TouchView cardView;

		cardView = (TouchView) mInflater.inflate(R.layout.view_card_big, null);

		int cardWidth = (int) (displayMetrics.widthPixels * 4 / 9);
		int cardHeight = (int) (cardWidth * 1.78f);

		cardView.setY(displayMetrics.heightPixels - 38 - cardHeight);

		TouchImageView background_image = (TouchImageView) cardView.findViewById(R.id.background_image);

		imageLoader.displayImage("drawable://" + R.drawable.login_background_1, background_image, options);

		return cardView;
	}
}