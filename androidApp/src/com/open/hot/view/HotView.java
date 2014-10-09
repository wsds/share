package com.open.hot.view;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.a.a.a.a.d;
import com.facebook.rebound.BaseSpringSystem;
import com.facebook.rebound.SimpleSpringListener;
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
import com.open.lib.viewbody.ListBody1;
import com.open.lib.viewbody.ListBody2;
import com.open.lib.viewbody.ListBody2.MyListItemBody;
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

	public SpringConfig ORIGAMI_SPRING_CONFIG = SpringConfig.fromOrigamiTensionAndFriction(80, 12);

	public BaseSpringSystem mSpringSystem = SpringSystem.create();
	public Spring mScaleSpring = mSpringSystem.createSpring().setSpringConfig(ORIGAMI_SPRING_CONFIG);

	public Spring mScaleCardSpring = mSpringSystem.createSpring().setSpringConfig(ORIGAMI_SPRING_CONFIG);

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

	public ListBody2 cardListBody;

	public TextView logo;
	public ImageView more;

	public int cardWidth = 0;
	public int cardHeight = 0;
	public TouchView cardView2Clicked = null;
	public TouchView cardViewClickedLeft = null;
	public TouchView cardViewClickedRight = null;

	public TouchImageView background_image3 = null;

	public void initView() {
		displayMetrics = new DisplayMetrics();

		thisActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		mInflater = thisActivity.getLayoutInflater();

		imageLoader.init(ImageLoaderConfiguration.createDefault(this.thisActivity));
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).displayer(new RoundedBitmapDisplayer((int) (5 * displayMetrics.density))).build();

		thisActivity.setContentView(R.layout.activity_hot);
		// thisActivity.setContentView(R.layout.view_card);
		main_container = (TouchView) thisActivity.findViewById(R.id.main_container);

		logo = (TextView) main_container.findViewById(R.id.logo);
		more = (ImageView) main_container.findViewById(R.id.more);

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
		imageLoader.displayImage("drawable://" + R.drawable.login_background_1, background_image1, options);

		bigCardView = (TouchView) mInflater.inflate(R.layout.view_card_big, null);
		main_container.addView(bigCardView, 0);
		mainPagerBody.addChildView(bigCardView);
		bigCardView.setX(-0);

		TouchTextView title = (TouchTextView) bigCardView.findViewById(R.id.title);
		mainPagerBody.setTitleView(title, 1);
		TouchImageView background_image = (TouchImageView) bigCardView.findViewById(R.id.background_image);
		imageLoader.displayImage("drawable://" + R.drawable.login_background_2, background_image, options);

		TouchView bigCardView2 = (TouchView) mInflater.inflate(R.layout.view_card, null);
		main_container.addView(bigCardView2, 0);
		mainPagerBody.addChildView(bigCardView2);
		TouchTextView title2 = (TouchTextView) bigCardView2.findViewById(R.id.title);
		mainPagerBody.setTitleView(title2, 2);
		TouchImageView background_image2 = (TouchImageView) bigCardView2.findViewById(R.id.content_image);
		imageLoader.displayImage("drawable://" + R.drawable.login_background_1, background_image2, options);

		big_card_container = (TouchView) bigCardView1.findViewById(R.id.big_card_container);

		cardListBody = new ListBody2();
		cardListBody.initialize(displayMetrics, big_card_container);

		cardWidth = (int) (displayMetrics.widthPixels * 4 / 9);
		cardHeight = (int) (cardWidth * 1.78f);
		TouchView.LayoutParams layoutParams = new TouchView.LayoutParams(cardWidth, cardHeight);

		String key1 = "card1";
		CardItem cardItem1 = new CardItem(this.cardListBody);
		TouchView cardView1 = cardItem1.initialize();
		this.cardListBody.height = cardWidth + 2 * displayMetrics.density;
		this.cardListBody.listItemBodiesMap.put(key1, cardItem1);
		this.cardListBody.listItemsSequence.add(key1);
		big_card_container.addView(cardView1, layoutParams);
		cardView1.setX(0);
		cardItem1.y = 0;
		this.cardListBody.height = (cardWidth + 2 * displayMetrics.density) * 1;

		cardViewClickedLeft = cardView1;

		String key2 = "card2";
		CardItem cardItem2 = new CardItem(this.cardListBody);
		TouchView cardView2 = cardItem2.initialize();
		big_card_container.addView(cardView2, layoutParams);
		this.cardListBody.listItemBodiesMap.put(key2, cardItem2);
		this.cardListBody.listItemsSequence.add(key2);
		cardView2.setX(cardWidth + 2 * displayMetrics.density);
		cardItem2.y = this.cardListBody.height;
		this.cardListBody.height = (cardWidth + 2 * displayMetrics.density) * 2;

		background_image3 = (TouchImageView) cardView2.findViewById(R.id.content_image);
		imageLoader.displayImage("drawable://" + R.drawable.login_background_1, background_image3, options);

		cardView2Clicked = cardView2;

		String key3 = "card3";
		CardItem cardItem3 = new CardItem(this.cardListBody);
		TouchView cardView3 = cardItem3.initialize();
		big_card_container.addView(cardView3, layoutParams);
		this.cardListBody.listItemBodiesMap.put(key3, cardItem3);
		this.cardListBody.listItemsSequence.add(key3);
		cardView3.setX((cardWidth + 2 * displayMetrics.density) * 2);
		cardItem3.y = this.cardListBody.height;
		this.cardListBody.height = (cardWidth + 2 * displayMetrics.density) * 3;
		this.cardListBody.containerHeight = displayMetrics.widthPixels;

		cardViewClickedRight = cardView3;

		SpringListener mSpringListener = new SpringListener();
		mScaleCardSpring.addListener(mSpringListener);
		mScaleCardSpring.setCurrentValue(0);
	}

	public class CardItem extends MyListItemBody {
		CardItem(ListBody2 listBody) {
			listBody.super();
		}

		public TouchView cardView;

		public TouchView initialize() {
			cardView = (TouchView) mInflater.inflate(R.layout.view_card, null);

			int cardWidth = (int) (displayMetrics.widthPixels * 4 / 9);
			int cardHeight = (int) (cardWidth * 1.78f);

			int imageHeight = (int) (cardWidth - displayMetrics.density * 20);
			cardView.setY(displayMetrics.heightPixels - 38 - cardHeight);

			TouchImageView cardImage = (TouchImageView) cardView.findViewById(R.id.content_image);

			TouchView.LayoutParams imageLayoutParams = new TouchView.LayoutParams(imageHeight, imageHeight);
			cardImage.setLayoutParams(imageLayoutParams);
			cardImage.setY(cardHeight - imageHeight - displayMetrics.density * 20);

			this.itemHeight = cardWidth;
			super.initialize(cardView);
			return cardView;
		}

		public void setContent() {
		}

		public void setViewLayout() {
		}
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

	public class SpringListener extends SimpleSpringListener {
		@Override
		public void onSpringUpdate(Spring spring) {
			render();
		}
	}

	TouchView.LayoutParams renderParams = new TouchView.LayoutParams(cardWidth, cardHeight);

	TouchView.LayoutParams imageParams = new TouchView.LayoutParams(100, 100);

	public void render() {
		double value = mScaleCardSpring.getCurrentValue();
		cardView2Clicked.setX((float) ((cardWidth + 2 * displayMetrics.density) * value));
		cardView2Clicked.setY((float) ((displayMetrics.heightPixels - 38 - cardHeight) * value));
		cardViewClickedLeft.setY((float) ((displayMetrics.heightPixels - 38 - cardHeight) * value));
		cardViewClickedRight.setY((float) ((displayMetrics.heightPixels - 38 - cardHeight) * value));

		cardViewClickedLeft.setX((float) ((-displayMetrics.widthPixels) * (1 - value)));
		cardViewClickedRight.setX((float) (displayMetrics.widthPixels - value * (displayMetrics.widthPixels - (cardWidth + 2 * displayMetrics.density) * 2)));

		renderParams.width = (int) ((cardWidth - displayMetrics.widthPixels) * value + displayMetrics.widthPixels);
		renderParams.height = (int) ((cardHeight - displayMetrics.heightPixels + 38) * value + displayMetrics.heightPixels - 38);
		cardView2Clicked.setLayoutParams(renderParams);
		cardViewClickedLeft.setLayoutParams(renderParams);
		cardViewClickedRight.setLayoutParams(renderParams);

		imageParams.width = (int) (cardWidth - displayMetrics.density * 20 + 100 * (1 - value));
		imageParams.height = (int) (cardWidth - displayMetrics.density * 20 + 100 * (1 - value));
		background_image3.setLayoutParams(imageParams);

		if (value < 0.1) {
			logo.setTextColor(0xff0099cd);
			more.setColorFilter(0xff0099cd);
		} else {
			logo.setTextColor(0xeeffffff);
			more.setColorFilter(0xeeffffff);
		}

	}
}