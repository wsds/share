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
	public Spring mOpenPostSpring = mSpringSystem.createSpring().setSpringConfig(fast_config);
	public Spring mClosePostSpring = mSpringSystem.createSpring().setSpringConfig(fast_config);
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
	public View more;

	public ImageView album;
	public ImageView clicker;

	public int cardWidth = 0;
	public int cardHeight = 0;
	public TouchView cardView2Clicked = null;
	public TouchView cardViewClickedLeft = null;
	public TouchView cardViewClickedRight = null;

	public void initView() {
		viewManage.initialize(thisActivity);
		mInflater = viewManage.mInflater;
		displayMetrics = viewManage.displayMetrics;

		cardWidth = (int) (displayMetrics.widthPixels * 4 / 9);
		cardHeight = (int) (cardWidth * 1.78f);
		renderParams = new TouchView.LayoutParams(cardWidth, cardHeight);

		thisActivity.setContentView(R.layout.activity_hot);
		// thisActivity.setContentView(R.layout.view_card);
		main_container = (TouchView) thisActivity.findViewById(R.id.main_container);
		viewManage.postContainer = main_container;
		logo = (TextView) thisActivity.findViewById(R.id.logo);
		more = (View) thisActivity.findViewById(R.id.more);

		album = (ImageView) thisActivity.findViewById(R.id.album);
		clicker = (ImageView) thisActivity.findViewById(R.id.clicker);

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

		OpenPostSpringListener mOpenPostSpringListener = new OpenPostSpringListener();
		mOpenPostSpring.addListener(mOpenPostSpringListener);
		mOpenPostSpring.setCurrentValue(1);
		mOpenPostSpring.setEndValue(1);

		ClosePostSpringListener mClosePostSpringListener = new ClosePostSpringListener();
		mClosePostSpring.addListener(mClosePostSpringListener);
		mClosePostSpring.setCurrentValue(0);
		mClosePostSpring.setEndValue(0);

		FoldCardSpringListener mFoldCardSpringListener = new FoldCardSpringListener();
		mFoldCardSpring.addListener(mFoldCardSpringListener);
		mFoldCardSpring.setCurrentValue(1);
		mFoldCardSpring.setEndValue(1);

		data.hotMap.put(data.me.id, data.me);
		setPost(data.me);
	}

	Map<String, Hot> hotMap;
	Hot parentHot = null;
	int index = 0;

	public void setPost(String key) {
		Hot hot = hotMap.get(key);
		if (hot != null) {
			setPost(hot);
		}
	}

	public void setPost(Hot hot) {
		viewManage.clearPostContainer();

		setCurrentPost(hot);
		setCardList(hot, hot.children);
		if (thisController.currentPost.parent != null) {
			setBackGroundPost(thisController.currentPost.parent);
		}
	}

	public void setCurrentPost(String key) {
		Hot hot = hotMap.get(key);
		if (hot != null) {
			setCurrentPost(hot);
		}
	}

	public void setCurrentPost(Hot hot) {

		if (hot == null) {
			return;
		}

		thisController.currentPost = setFullScreenPost(hot);
		Log.w(tag, "currentPost: " + thisController.currentPost.key);
	}

	public void setBackGroundPost(String key) {
		Hot hot = hotMap.get(key);
		if (hot != null) {
			setBackGroundPost(hot);
		}
	}

	public PostBody setBackGroundPost(Hot hot) {

		if (hot == null) {
			return null;
		}

		PostBody post = viewManage.postPool.getPost(hot.id);
		if (post == null || post.visible == View.VISIBLE) {// if the post is used in the children list or brother list;
			post = new PostBody();
			post.initialize(hot, 0, null, 0);
		} else {
			post.endValue = 0;
			post.renderThis(0);
			post.peekRelation();
		}
		post.postContainer.addView(post.postView, 0);
		// post.setVisibility(View.VISIBLE);
		post.setXY(0, 0);
		post.setAlpha(1);
		Log.w(tag, "setBackGroundPost: " + post.key);

		return post;
	}

	public PostBody setFullScreenPost(Hot hot) {

		if (hot == null) {
			return null;
		}

		PostBody post = viewManage.postPool.getPost(hot.id);
		if (post == null) {
			post = new PostBody();
			post.initialize(hot, 0, parentHot, index);
		} else {
			post.endValue = 0;
			post.renderThis(0);
			post.peekRelation();
		}

		post.setVisibility(View.VISIBLE);
		post.setXY(0, 0);
		post.setAlpha(1);
		Log.w(tag, "setFullScreenPost: " + post.key);

		return post;
	}

	public long delayMillis = 300;

	public void setCardList(Hot parentHot, ArrayList<String> children) {
		if (children == null) {
			return;
		}
		Log.d(tag, "Show children List: " + children.toString());
		this.cardListBody.clear();
		int index = 0;
		for (String key : children) {
			Hot hot = hotMap.get(key);
			if (hot != null) {
				addToCardList(hot, parentHot, index);
			}
		}
		if (thisController.eventStatus.state == thisController.eventStatus.OpenPost || thisController.eventStatus.state == thisController.eventStatus.Done) {

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
		} else if (thisController.eventStatus.state == thisController.eventStatus.ClosePost) {
			this.mFoldCardSpring.setCurrentValue(1);
			this.mFoldCardSpring.setEndValue(1);
			renderFoldCard();
		} else {
			Log.d(tag, "error occurs in " + "setCardList");
			thisController.logEventStatus();
		}
	}

	public void addToCardList(Hot hot, Hot parentHot, int index) {

		PostBody post = viewManage.postPool.getPost(hot.id);
		if (post == null) {
			post = new PostBody();
			post.initialize(hot, 1, parentHot, index);
		} else {
			post.endValue = 1;
			post.setVisibility(View.VISIBLE);
			post.setAlpha(1);
			// Log.d(tag, "Take to front: " + hot.id + "   key:   " + post.key);
			post.render(1);
		}

		post.setVisibility(View.VISIBLE);

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

			postBody.isRecordX = false;
			postBody.recordX();

			super.initialize(cardView);
			return cardView;
		}
	}

	public class OpenPostSpringListener extends SimpleSpringListener {
		@Override
		public void onSpringUpdate(Spring spring) {
			renderOpenPost();
		}

		@Override
		public void onSpringAtRest(Spring spring) {
			if (thisController.eventStatus.state != thisController.eventStatus.OpenPost) {
				return;
			}
			double value = mOpenPostSpring.getCurrentValue();
			if (value == 0) {
				if (thisController.postClick != null) {
					// thisController.postClick.parent = thisController.currentPost.key;
					thisController.postClick.pushRelation();

					// main_container.removeAllViews();
					// setCurrentPost(thisController.postClick.parent.hot);
					setPost(thisController.postClick.hot);
					thisController.currentPost.setVisibility(View.VISIBLE);

					thisController.currentPost.endValue = 0;
					thisController.postClick = null;

					mClosePostSpring.setCurrentValue(0);
					mClosePostSpring.setEndValue(0);

					mOpenPostSpring.setCurrentValue(1);
					mOpenPostSpring.setEndValue(1);
				}

			} else if (value == 1) {
			}

			thisController.eventStatus.state = thisController.eventStatus.Done;
		}
	}

	public class ClosePostSpringListener extends SimpleSpringListener {
		@Override
		public void onSpringUpdate(Spring spring) {
			renderClosePost();
		}

		@Override
		public void onSpringAtRest(Spring spring) {
			if (thisController.eventStatus.state != thisController.eventStatus.ClosePost) {
				return;
			}
			double value = mClosePostSpring.getCurrentValue();

			Log.d(tag, "ClosePostSpringListener onSpringAtRest: " + value);
			if (value == 0) {
				thisController.currentPost.unPeekRelation();
			} else if (value == 1) {
				if (thisController.currentPost != null && thisController.currentPost.parent != null) {
					// setPost(postClick.parent.hot);

					thisController.currentPost.popRelation();

					// setCurrentPost(thisController.currentPost.parent.hot);
					setPost(thisController.currentPost.parent);

					thisController.currentPost.endValue = 0;

					thisController.postClick = null;

					mClosePostSpring.setCurrentValue(0);
					mClosePostSpring.setEndValue(0);

					mOpenPostSpring.setCurrentValue(1);
					mOpenPostSpring.setEndValue(1);
					// renderScaleCard();
				}
			}

			thisController.eventStatus.state = thisController.eventStatus.Done;
		}
	}

	public class FoldCardSpringListener extends SimpleSpringListener {
		@Override
		public void onSpringUpdate(Spring spring) {
			renderFoldCard();
		}

		@Override
		public void onSpringAtRest(Spring spring) {
			double value = mFoldCardSpring.getCurrentValue();
			if (thisController.subCardStatus.state == thisController.subCardStatus.MOVING) {
				if (value == 0) {
					thisController.subCardStatus.state = thisController.subCardStatus.FOLD;
				} else if (value == 1) {
					thisController.subCardStatus.state = thisController.subCardStatus.UNFOLD;
				}
			}
			if (thisController.eventStatus.state == thisController.eventStatus.Fold) {
				thisController.eventStatus.state = thisController.eventStatus.Done;
			}
			Log.e(tag, "onSpringAtRest: " + value);
			thisController.logEventStatus();
			thisController.logSubCardStatus();
		}
	}

	@SuppressLint("NewApi")
	public void renderOpenPost() {
		double value = mOpenPostSpring.getCurrentValue();
		if (thisController.postClick != null) {
			thisController.postClick.render(value);
		}

		if (value < 0.1) {
			if (thisController.postClick != null && thisController.postClick.hotType.type == thisController.postClick.hotType.PAPER) {
				logo.setTextColor(0xff0099cd);
				// more.setColorFilter(0xff0099cd);
			}
		} else {
			logo.setTextColor(0xeeffffff);
			// more.setColorFilter(0xeeffffff);
		}
	}

	@SuppressLint("NewApi")
	public void renderClosePost() {
		double value = mClosePostSpring.getCurrentValue();
		if (thisController.currentPost != null) {
			thisController.currentPost.render(value);
		}

		if (value < 0.1) {
			album.setVisibility(View.VISIBLE);
		} else {
			album.setVisibility(View.INVISIBLE);
		}
	}

	public TouchView.LayoutParams renderParams;

	public void renderFoldCard() {
		double value = mFoldCardSpring.getCurrentValue();

		int listSize = this.cardListBody.listItemsSequence.size();
		float x = 0;
		float y = (float) ((displayMetrics.heightPixels - 38 - cardHeight) + cardHeight * (1 - value));
		for (int i = 0; i < listSize; i++) {
			String key = this.cardListBody.listItemsSequence.get(i);
			PostBody post = viewManage.postPool.getPost(key);

			x = post.x;
			if (value < 0.2) {
				if (post.visible == View.VISIBLE) {
					post.setVisibility(View.INVISIBLE);
					if (thisController.eventStatus.state == thisController.eventStatus.Fold) {
						post.recordX();
					}
				}
			} else {
				post.isRecordX = false;

				post.setVisibility(View.VISIBLE);
				x = post.record_x;
				post.setSize(cardWidth, cardHeight);
			}
			post.setXY(x, y);
			post.setAlpha((float) (value));
		}

		if (value < 0.1) {
			album.setVisibility(View.VISIBLE);
			album.setScaleX((float) (1 + 2 * value));
			album.setScaleY((float) (1 + 2 * value));
		} else {
			album.setVisibility(View.INVISIBLE);
		}

	}
}