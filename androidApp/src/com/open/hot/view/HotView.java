package com.open.hot.view;

import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.rebound.BaseSpringSystem;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.open.hot.R;
import com.open.hot.controller.HotController;
import com.open.hot.model.Data;
import com.open.hot.model.Data.Hot;
import com.open.lib.TouchImageView;
import com.open.lib.TouchView;
import com.open.lib.viewbody.BodyCallback;
import com.open.lib.viewbody.ListBody2;
import com.open.lib.viewbody.ListBody2.MyListItemBody;
import com.open.lib.viewbody.PagerBody;

public class HotView {
	public Data data = Data.getInstance();
	public String tag = "LoginView";

	public Context context;
	public HotView thisView;
	public HotController thisController;
	public Activity thisActivity;

	ViewManage viewManage = ViewManage.getInstance();

	public Animation animationBackIn;

	public enum Status {
		welcome, start, loginOrRegister, loginUsePassword, verifyPhoneForRegister, verifyPhoneForResetPassword, verifyPhoneForLogin, setPassword, resetPassword
	}

	String 状态机;

	public Status status = Status.welcome;

	public SpringConfig ORIGAMI_SPRING_CONFIG = SpringConfig.fromOrigamiTensionAndFriction(240, 12);

	public BaseSpringSystem mSpringSystem = SpringSystem.create();
	public Spring mScaleCardSpring = mSpringSystem.createSpring().setSpringConfig(ORIGAMI_SPRING_CONFIG);
	public Spring mFoldCardSpring = mSpringSystem.createSpring().setSpringConfig(ORIGAMI_SPRING_CONFIG);

	public HotView(Activity activity) {
		this.context = activity;
		this.thisActivity = activity;
	}

	public DisplayMetrics displayMetrics;
	public LayoutInflater mInflater;
	public ImageLoader imageLoader = ImageLoader.getInstance();

	public TouchView main_container;
	public TouchView bigCardView;
	public TouchView big_card_container;

	public PagerBody mainPagerBody;

	public ListBody2 cardListBody;

	public TextView logo;
	public ImageView more;

	public ImageView album;

	public int cardWidth = 0;
	public int cardHeight = 0;
	public TouchView cardView2Clicked = null;
	public TouchView cardViewClickedLeft = null;
	public TouchView cardViewClickedRight = null;

	public TouchImageView background_image1 = null;
	public TouchImageView background_image2 = null;
	public TouchImageView background_image3 = null;

	public void initView() {
		viewManage.initialize(thisActivity);
		mInflater = viewManage.mInflater;
		displayMetrics = viewManage.displayMetrics;

		thisActivity.setContentView(R.layout.activity_hot);
		// thisActivity.setContentView(R.layout.view_card);
		main_container = (TouchView) thisActivity.findViewById(R.id.main_container);

		logo = (TextView) main_container.findViewById(R.id.logo);
		more = (ImageView) main_container.findViewById(R.id.more);

		album = (ImageView) main_container.findViewById(R.id.album);

		BodyCallback myBodyCallback = new BodyCallback();

		mainPagerBody = new PagerBody();
		mainPagerBody.tag = "mainPagerBody";
		mainPagerBody.initialize(displayMetrics, myBodyCallback, null);
		viewManage.postViewPool.container = main_container;
		Map<String, Hot> hotMap = data.hotMap;

		cardListBody = new ListBody2();
		cardListBody.initialize(displayMetrics, big_card_container);

		String key1 = "card1";
		CardItem cardItem1 = new CardItem(this.cardListBody);
		TouchView cardView1 = cardItem1.initialize3("2003");
		this.cardListBody.height = cardWidth + 2 * displayMetrics.density;
		this.cardListBody.listItemBodiesMap.put(key1, cardItem1);
		this.cardListBody.listItemsSequence.add(key1);
		// big_card_container.addView(cardView1, layoutParams);
		cardView1.setX(0);
		cardItem1.y = 0;
		this.cardListBody.height = (cardWidth + 2 * displayMetrics.density) * 1;
		background_image1 = (TouchImageView) cardView1.findViewById(R.id.content_image);

		cardViewClickedLeft = cardView1;

		String key2 = "card2";
		CardItem cardItem2 = new CardItem(this.cardListBody);
		TouchView cardView2 = cardItem2.initialize3("2001");
		this.cardListBody.listItemBodiesMap.put(key2, cardItem2);
		this.cardListBody.listItemsSequence.add(key2);
		cardView2.setX(cardWidth + 2 * displayMetrics.density);
		cardItem2.y = this.cardListBody.height;
		this.cardListBody.height = (cardWidth + 2 * displayMetrics.density) * 2;

		background_image3 = (TouchImageView) cardView2.findViewById(R.id.content_image);

		imageLoader.displayImage("drawable://" + R.drawable.test_abc_121212, background_image3, viewManage.options);

		PostBody post1 = new PostBody();
		View bigCardView1 = post1.initialize(hotMap.get("2001"));
		viewManage.postViewPool.putView("2001", post1.postView);
		mainPagerBody.addChildView(post1.postView);
		mainPagerBody.setTitleView(post1.titleView, 0);

		PostBody post2 = new PostBody();
		post2.initialize(hotMap.get("2003"));
		viewManage.postViewPool.putView("2003", post2.postView);
		mainPagerBody.addChildView(post2.postView);
		mainPagerBody.setTitleView(post2.titleView, 1);

		PostBody post3 = new PostBody();
		post3.initialize(hotMap.get("2004"));
		viewManage.postViewPool.putView("2004", post3.postView);
		mainPagerBody.addChildView(post3.postView);
		mainPagerBody.setTitleView(post3.titleView, 2);

		big_card_container = (TouchView) bigCardView1.findViewById(R.id.view_container);

		cardWidth = (int) (displayMetrics.widthPixels * 4 / 9);
		cardHeight = (int) (cardWidth * 1.78f);
		TouchView.LayoutParams layoutParams = new TouchView.LayoutParams(cardWidth, cardHeight);

		cardView2Clicked = cardView2;

		String key3 = "card3";
		CardItem cardItem3 = new CardItem(this.cardListBody);
		TouchView cardView3 = cardItem3.initialize2();
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
		mScaleCardSpring.setEndValue(1);

		FoldCardSpringListener mFoldCardSpringListener = new FoldCardSpringListener();
		mFoldCardSpring.addListener(mFoldCardSpringListener);
		mFoldCardSpring.setEndValue(1);
	}

	public class CardItem extends MyListItemBody {
		CardItem(ListBody2 listBody) {
			listBody.super();
		}

		public TouchView cardView;

		public TouchView initialize() {
			cardView = (TouchView) mInflater.inflate(R.layout.post_paper, null);

			int imageHeight = (int) (cardWidth - displayMetrics.density * 20);
			cardView.setY(displayMetrics.heightPixels - 38 - cardHeight);

			TouchImageView cardImage = (TouchImageView) cardView.findViewById(R.id.content_image);

			TouchView.LayoutParams imageLayoutParams = new TouchView.LayoutParams(imageHeight, imageHeight);
			cardImage.setLayoutParams(imageLayoutParams);
			cardImage.setY(cardHeight - imageHeight - displayMetrics.density * 10);
			cardImage.setX(displayMetrics.density * 10);

			this.itemHeight = cardWidth;
			super.initialize(cardView);
			return cardView;
		}

		public TouchView initialize2() {

			TouchView cardView;

			cardView = (TouchView) mInflater.inflate(R.layout.view_card_big, null);

			cardView.setY(displayMetrics.heightPixels - 38 - cardHeight);

			TouchImageView background_image = (TouchImageView) cardView.findViewById(R.id.background_image);

			imageLoader.displayImage("drawable://" + R.drawable.test1, background_image, viewManage.options);

			this.itemHeight = cardWidth;
			super.initialize(cardView);
			return cardView;
		}

		public TouchView initialize3(String key) {

			Map<String, Hot> hotMap = data.hotMap;

			PostBody post2 = new PostBody();
			post2.initialize(hotMap.get(key));
			viewManage.postViewPool.putView("2004", post2.postView);
			cardView = (TouchView) post2.postView;

			this.itemHeight = cardWidth;
			super.initialize(cardView);
			return cardView;
		}

		public void setContent() {
		}

		public void setViewLayout() {
		}
	}

	public class SpringListener extends SimpleSpringListener {
		@Override
		public void onSpringUpdate(Spring spring) {
			render();
		}
	}

	public class FoldCardSpringListener extends SimpleSpringListener {
		@Override
		public void onSpringUpdate(Spring spring) {
			renderFoldCard();
		}
	}

	TouchView.LayoutParams renderParams = new TouchView.LayoutParams(cardWidth, cardHeight);

	TouchView.LayoutParams imageParams = new TouchView.LayoutParams(100, 100);

	@SuppressLint("NewApi")
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

		imageParams.width = (int) (cardWidth - displayMetrics.density * 20 + (displayMetrics.widthPixels - cardWidth) * (1 - value));
		imageParams.height = (int) (cardWidth - displayMetrics.density * 20 + (displayMetrics.widthPixels - cardWidth) * (1 - value));
		background_image3.setLayoutParams(imageParams);
		background_image1.setLayoutParams(imageParams);

		if (value < 0.1) {
			logo.setTextColor(0xff0099cd);
			more.setColorFilter(0xff0099cd);
			cardView2Clicked.setBackground(viewManage.card_background_ff);
		} else {
			logo.setTextColor(0xeeffffff);
			more.setColorFilter(0xeeffffff);
			cardView2Clicked.setBackground(viewManage.card_background);
		}

	}

	public void renderFoldCard() {
		double value = mFoldCardSpring.getCurrentValue();

		cardView2Clicked.setY((float) ((displayMetrics.heightPixels - 38 - cardHeight) + cardHeight * (1 - value)));

		cardViewClickedLeft.setY((float) ((displayMetrics.heightPixels - 38 - cardHeight) + cardHeight * (1 - value)));

		cardViewClickedRight.setY((float) ((displayMetrics.heightPixels - 38 - cardHeight) + cardHeight * (1 - value)));
		cardView2Clicked.setAlpha((float) (value));
		cardViewClickedLeft.setAlpha((float) (value));
		cardViewClickedRight.setAlpha((float) (value));

		if (value < 0.2) {
			album.setVisibility(View.VISIBLE);
			album.setScaleX((float) (1 + 2 * value));
			album.setScaleY((float) (1 + 2 * value));

			cardView2Clicked.setVisibility(View.INVISIBLE);
			cardViewClickedLeft.setVisibility(View.INVISIBLE);
			cardViewClickedRight.setVisibility(View.INVISIBLE);
		} else {
			album.setVisibility(View.INVISIBLE);

			cardView2Clicked.setVisibility(View.VISIBLE);
			cardViewClickedLeft.setVisibility(View.VISIBLE);
			cardViewClickedRight.setVisibility(View.VISIBLE);

		}

	}
}