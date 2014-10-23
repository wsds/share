package com.open.hot.view;

import java.util.ArrayList;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
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

	public ViewManage viewManage = ViewManage.getInstance();

	public Animation animationBackIn;

	public enum Status {
		welcome, start, loginOrRegister, loginUsePassword, verifyPhoneForRegister, verifyPhoneForResetPassword, verifyPhoneForLogin, setPassword, resetPassword
	}

	String 状态机;

	public Status status = Status.welcome;

	public SpringConfig fast_config = SpringConfig.fromOrigamiTensionAndFriction(240, 12);
	public SpringConfig slow_config = SpringConfig.fromOrigamiTensionAndFriction(60, 12);

	public BaseSpringSystem mSpringSystem = SpringSystem.create();
	public Spring mScaleCardSpring = mSpringSystem.createSpring().setSpringConfig(fast_config);
	public Spring mFoldCardSpring = mSpringSystem.createSpring().setSpringConfig(fast_config);

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

	public PostBody currentPost;
	public PostBody postClick;

	public void initView() {
		viewManage.initialize(thisActivity);
		mInflater = viewManage.mInflater;
		displayMetrics = viewManage.displayMetrics;

		cardWidth = (int) (displayMetrics.widthPixels * 4 / 9);
		cardHeight = (int) (cardWidth * 1.78f);

		thisActivity.setContentView(R.layout.activity_hot);
		// thisActivity.setContentView(R.layout.view_card);
		main_container = (TouchView) thisActivity.findViewById(R.id.main_container);

		logo = (TextView) thisActivity.findViewById(R.id.logo);
		more = (ImageView) thisActivity.findViewById(R.id.more);

		album = (ImageView) thisActivity.findViewById(R.id.album);

		BodyCallback myBodyCallback = new BodyCallback();

		viewManage.postPool.container = main_container;
		viewManage.postPool.pool.clear();
		hotMap = data.hotMap;

		mainPagerBody = new PagerBody();
		mainPagerBody.tag = "mainPagerBody";
		mainPagerBody.initialize(displayMetrics, myBodyCallback, null);

		cardListBody = new ListBody2();
		cardListBody.initialize(displayMetrics, big_card_container);

		// mainPagerBody.addChildView(postBody.postView);
		// mainPagerBody.setTitleView(postBody.titleView, 0);

		ScaleCardSpringListener mSpringListener = new ScaleCardSpringListener();
		mScaleCardSpring.addListener(mSpringListener);
		mScaleCardSpring.setCurrentValue(1);
		mScaleCardSpring.setEndValue(1);

		FoldCardSpringListener mFoldCardSpringListener = new FoldCardSpringListener();
		mFoldCardSpring.addListener(mFoldCardSpringListener);
		mFoldCardSpring.setCurrentValue(1);
		mFoldCardSpring.setEndValue(1);

		setPost(data.me);
	}

	Map<String, Hot> hotMap;

	public void setPost(Hot hot) {
		setCurrentPost(hot);
		setCardList(hot.children);
	}

	public void setCurrentPost(String key) {
		Hot hot = hotMap.get(key);
		if (hot != null) {
			setCurrentPost(hot);
		}
	}

	public void setCurrentPost(Hot hot) {
		currentPost = viewManage.postPool.getPost(hot.id);
		if (currentPost == null) {
			currentPost = new PostBody();
			currentPost.initialize(hot, 0);
		} else {
			currentPost.endValue = 0;
			currentPost.render(0);
		}
	}

	public long delayMillis = 300;

	public void setCardList(ArrayList<String> children) {
		if (children == null) {
			return;
		}
		Log.d(tag, "Show children List: " + children.toString());
		this.cardListBody.clear();
		for (String key : children) {
			Hot hot = hotMap.get(key);
			if (hot != null) {
				addToCardList(hot);
			}
		}
		this.mFoldCardSpring.setCurrentValue(0.2);
		this.mFoldCardSpring.setEndValue(0.2);

		this.renderFoldCard();
		new Handler().postDelayed(new Runnable() {
			public void run() {
				mFoldCardSpring.setSpringConfig(slow_config);
				mFoldCardSpring.setEndValue(1);
				delayMillis = 20;
			}
		}, delayMillis);
		new Handler().postDelayed(new Runnable() {
			public void run() {
				mFoldCardSpring.setSpringConfig(fast_config);
			}
		}, 1000);

	}

	public void addToCardList(Hot hot) {

		PostBody post = viewManage.postPool.getPost(hot.id);
		if (post == null) {
			post = new PostBody();
			post.initialize(hot, 1);
		} else {
			post.endValue = 1;
			post.postView.setVisibility(View.VISIBLE);
			main_container.removeView(post.postView);
			main_container.addView(post.postView);
			Log.d(tag, "Take to front: " + hot.id + "   key:   " + post.key);
			post.left = null;
			post.right = null;
			post.render(1);
		}

		CardItem cardItem = new CardItem(this.cardListBody);
		cardItem.initialize3(post);
	}

	public class CardItem extends MyListItemBody {
		CardItem(ListBody2 listBody) {
			listBody.super();
			this.listBody = listBody;
		}

		public ListBody2 listBody;
		public TouchView cardView;
		public PostBody postBody;

		public TouchView.LayoutParams renderParams = new TouchView.LayoutParams(cardWidth, cardHeight);

		public TouchView initialize3(PostBody postBody) {

			this.postBody = postBody;

			if (listBody.lastAddItem != null) {
				postBody.left = ((CardItem) listBody.lastAddItem).postBody;
				postBody.left.right = postBody;
			}
			cardView = (TouchView) postBody.postView;
			cardView.setVisibility(View.VISIBLE);

			cardView.setX(listBody.height);
			cardView.setY((float) (displayMetrics.heightPixels - 38 - cardHeight));
			cardView.setLayoutParams(renderParams);

			cardView.setTag(R.id.tag_class, "CardView");
			cardView.setTag(R.id.tag_key, postBody.key);

			cardView.setOnTouchListener(thisController.onTouchListener);

			this.itemHeight = cardWidth;

			listBody.height = listBody.height + cardWidth + 2 * displayMetrics.density;
			listBody.listItemBodiesMap.put(postBody.key, this);
			listBody.listItemsSequence.add(postBody.key);
			listBody.lastAddItem = this;
			super.initialize(cardView);
			return cardView;
		}
	}

	public class ScaleCardSpringListener extends SimpleSpringListener {
		@Override
		public void onSpringUpdate(Spring spring) {
			renderScaleCard();
		}

		@Override
		public void onSpringAtRest(Spring spring) {
			double value = mScaleCardSpring.getCurrentValue();
			Log.d(tag, "onSpringAtRest 2: " + value);
			if (value == 0) {
				if (postClick != null) {
					postClick.parent = currentPost;
					setPost(postClick.hot);
					postClick.postView.setVisibility(View.VISIBLE);

					postClick = null;

					mScaleCardSpring.setCurrentValue(1);
					mScaleCardSpring.setEndValue(1);
				}

			} else if (value == 1) {
				if (postClick != null && postClick.parent != null) {
					// setPost(postClick.parent.hot);

					mScaleCardSpring.setCurrentValue(1);
					mScaleCardSpring.setEndValue(1);
					// renderScaleCard();
				}
			}
		}
	}

	public class FoldCardSpringListener extends SimpleSpringListener {
		@Override
		public void onSpringUpdate(Spring spring) {
			renderFoldCard();
		}
	}

	@SuppressLint("NewApi")
	public void renderScaleCard() {
		double value = mScaleCardSpring.getCurrentValue();
		if (postClick != null) {
			postClick.render(value);
		}

		if (value < 0.1) {
			if (postClick != null && postClick.hotType.type == postClick.hotType.PAPER) {
				logo.setTextColor(0xff0099cd);
				more.setColorFilter(0xff0099cd);
			}
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