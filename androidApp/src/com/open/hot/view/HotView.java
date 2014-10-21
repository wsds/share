package com.open.hot.view;

import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
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

	public PostBody postBody;
	public PostBody postBodyClick;

	public void initView() {
		viewManage.initialize(thisActivity);
		mInflater = viewManage.mInflater;
		displayMetrics = viewManage.displayMetrics;

		cardWidth = (int) (displayMetrics.widthPixels * 4 / 9);
		cardHeight = (int) (cardWidth * 1.78f);

		thisActivity.setContentView(R.layout.activity_hot);
		// thisActivity.setContentView(R.layout.view_card);
		main_container = (TouchView) thisActivity.findViewById(R.id.main_container);

		logo = (TextView) main_container.findViewById(R.id.logo);
		more = (ImageView) main_container.findViewById(R.id.more);

		album = (ImageView) main_container.findViewById(R.id.album);

		BodyCallback myBodyCallback = new BodyCallback();

		viewManage.postPool.container = main_container;
		Map<String, Hot> hotMap = data.hotMap;

		mainPagerBody = new PagerBody();
		mainPagerBody.tag = "mainPagerBody";
		mainPagerBody.initialize(displayMetrics, myBodyCallback, null);

		cardListBody = new ListBody2();
		cardListBody.initialize(displayMetrics, big_card_container);
		CardItem cardItem = null;

		postBody = new PostBody();
		postBody.initialize(hotMap.get("2003"), 1);
		cardItem = new CardItem(this.cardListBody);
		cardItem.initialize3(postBody);

		postBody = new PostBody();
		postBody.initialize(hotMap.get("2001"), 1);
		cardItem = new CardItem(this.cardListBody);
		cardItem.initialize3(postBody);
		postBodyClick = postBody;

		postBody = new PostBody();
		postBody.initialize(hotMap.get("2002"), 1);
		cardItem = new CardItem(this.cardListBody);
		cardItem.initialize3(postBody);

		postBody = new PostBody();
		postBody.initialize(data.me, 0);

		mainPagerBody.addChildView(postBody.postView);
		mainPagerBody.setTitleView(postBody.titleView, 0);

		SpringListener mSpringListener = new SpringListener();
		mScaleCardSpring.addListener(mSpringListener);
		mScaleCardSpring.setCurrentValue(1);
		mScaleCardSpring.setEndValue(1);

		FoldCardSpringListener mFoldCardSpringListener = new FoldCardSpringListener();
		mFoldCardSpring.addListener(mFoldCardSpringListener);
		mFoldCardSpring.setCurrentValue(1);
		mFoldCardSpring.setEndValue(1);
	}

	public class CardItem extends MyListItemBody {
		CardItem(ListBody2 listBody) {
			listBody.super();
			this.listBody = listBody;
		}

		public ListBody2 listBody;
		public TouchView cardView;
		public PostBody postBody;

		public TouchView initialize3(PostBody postBody) {

			this.postBody = postBody;
			if (listBody.lastAddItem != null) {
				postBody.left = ((CardItem) listBody.lastAddItem).postBody;
				postBody.left.right = postBody;
			}
			cardView = (TouchView) postBody.postView;
			cardView.setX(listBody.height);

			this.itemHeight = cardWidth;

			listBody.height = listBody.height + cardWidth + 2 * displayMetrics.density;
			listBody.listItemBodiesMap.put(postBody.key, this);
			listBody.listItemsSequence.add(postBody.key);
			listBody.lastAddItem = this;
			super.initialize(cardView);
			return cardView;
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

	@SuppressLint("NewApi")
	public void render() {
		double value = mScaleCardSpring.getCurrentValue();
		postBodyClick.render(value);

		if (value < 0.1) {
			logo.setTextColor(0xff0099cd);
			more.setColorFilter(0xff0099cd);
		} else {
			logo.setTextColor(0xeeffffff);
			more.setColorFilter(0xeeffffff);
		}
	}

	public void renderFoldCard() {
		double value = mFoldCardSpring.getCurrentValue();

		int listSize = this.cardListBody.listItemsSequence.size();
		for (int i = 0; i < listSize; i++) {
			String key = this.cardListBody.listItemsSequence.get(i);
			CardItem cardItem = (CardItem) this.cardListBody.listItemBodiesMap.get(key);
			cardItem.cardView.setY((float) ((displayMetrics.heightPixels - 38 - cardHeight) + cardHeight * (1 - value)));
			cardItem.cardView.setAlpha((float) (value));
			if (value < 0.2) {
				cardItem.cardView.setVisibility(View.INVISIBLE);
			} else {
				cardItem.cardView.setVisibility(View.VISIBLE);
			}
		}

		if (value < 0.2) {
			album.setVisibility(View.VISIBLE);
			album.setScaleX((float) (1 + 2 * value));
			album.setScaleY((float) (1 + 2 * value));
		} else {
			album.setVisibility(View.INVISIBLE);
		}

	}
}