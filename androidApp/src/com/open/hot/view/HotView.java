package com.open.hot.view;

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
	public SpringConfig mid_config = SpringConfig.fromOrigamiTensionAndFriction(120, 12);
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
	public ImageView more_image;

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
		more_image = (ImageView) more.findViewById(R.id.more_image);

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
		setPost(data.me, null, 0);
		setCardList(data.me);
	}

	Map<String, Hot> hotMap;

	public void setPost(String key, String parentKey) {
		Hot hot = hotMap.get(key);
		Hot parentHot = hotMap.get(parentKey);
		int index = parentHot.children.indexOf(key);
		if (hot != null && parentHot != null) {
			setPost(hot, parentHot, index);
			setCardList(hot);
		}
	}

	public void setPost(Hot hot, Hot parentHot, int index) {
		viewManage.clearPostContainer();

		thisController.currentHot = hot;
		setCurrentPost(hot, parentHot, index);
		if (thisController.currentPost.parent != null) {
			setBackGroundPost(thisController.currentPost.parent);
		}
	}

	public void setCurrentPost(String key, String parentKey) {
		Hot hot = hotMap.get(key);
		Hot parentHot = hotMap.get(parentKey);
		int index = parentHot.children.indexOf(key);
		if (hot != null && parentHot != null) {
			setCurrentPost(hot, parentHot, index);
		}
	}

	public void setCurrentPost(Hot hot, Hot parentHot, int index) {

		if (hot == null) {
			return;
		}

		thisController.currentPost = setFullScreenPost(hot, parentHot, index);
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

		PostBody post = getAvailablePost(hot, null, 0);

		post.renderThis(0);
		post.setVisibilityAtBottom(View.VISIBLE);

		return post;
	}

	public PostBody setFullScreenPost(Hot hot, Hot parentHot, int index) {

		if (hot == null) {
			return null;
		}

		PostBody post = getAvailablePost(hot, parentHot, index);

		post.renderThis(0);
		post.setVisibility(View.VISIBLE);

		return post;
	}

	public long delayMillis = 300;

	public void setCardList(Hot hot) {
		if (hot == null || hot.children == null) {
			return;
		}
		Log.d(tag, "Show children List: " + hot.children.toString());
		this.cardListBody.clear();

		float listWidth = 0;

		int listIndex = 0;
		for (String key : hot.children) {
			if (key.equals(hot.id)) {
				viewManage.reportError(tag, 258);
				continue;
			}
			Hot childrenHot = hotMap.get(key);
			if (childrenHot != null) {
				PostBody post = getAvailablePost(childrenHot, hot, listIndex);
				post.record_x = listWidth;
				post.renderThis(1);
				post.setVisibility(View.INVISIBLE);
				post.setVisibility(View.VISIBLE);

				listWidth = listWidth + cardWidth + 2 * displayMetrics.density;
				listIndex++;
			}
		}
		if (thisController.eventStatus.state == thisController.eventStatus.OpenPost || thisController.eventStatus.state == thisController.eventStatus.Done) {

			this.mFoldCardSpring.setCurrentValue(0.2);
			this.mFoldCardSpring.setEndValue(0.2);

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
		} else {
			Log.d(tag, "error occurs in " + "setCardList");
			thisController.logEventStatus();
		}
	}

	public PostBody getAvailablePost(Hot hot, Hot parentHot, int index) {
		PostBody post = viewManage.postPool.getPost(hot.id);
		if (post == null) {
			post = new PostBody();
			post.initialize(hot, 0, parentHot, index);
			// post.pushRelation();

			viewManage.postPool.putPost(hot.id, post);
		} else {
			if (parentHot != null) {
				if (post.status.state == post.status.HIDE && thisController.eventStatus.state == thisController.eventStatus.OpenPost) {
					post.pushRelation();
				}
				if (post.status.state == post.status.SHOW && !post.key.equals(thisController.currentHot.id) && thisController.eventStatus.state == thisController.eventStatus.OpenPost) {
					post.setVisibility(View.INVISIBLE);
					post.pushRelation();
				}
				post.parent = parentHot.id;
				post.brothers = parentHot.children;
				post.index = index;
			} else {
				viewManage.reportError(tag, 219);
			}
		}
		return post;
	}

	void resolvePostsWhenOpen() {
		PostBody parentPost = viewManage.postPool.getPost(thisController.currentPost.parent);
		if (parentPost == null || parentPost.brothers == null) {
			return;
		}
		int brotherCount = parentPost.brothers.size();
		for (int index = 0; index < brotherCount; index++) {
			String key = parentPost.brothers.get(index);
			if (key.equals(thisController.currentPost.key) || key.equals(thisController.currentPost.parent)) {
				continue;
			}
			PostBody postBody = viewManage.postPool.getPost(key);

			postBody.status.state = postBody.status.HIDE;
		}
	}

	void resolvePostsBeforeClose() {
		if (thisController.currentPost.children == null) {
			return;
		}
		int childCount = thisController.currentPost.children.size();
		for (int index = 0; index < childCount; index++) {

			String key = thisController.currentPost.children.get(index);
			PostBody postBody = viewManage.postPool.getPost(key);

			boolean hasRelation = postBody.popRelation();
			if (hasRelation) {
				postBody.status.state = postBody.status.HIDE;
			} else {
				postBody.status.state = postBody.status.FREED;
			}
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
				if (thisController.clickPost != null) {

					Hot hot = thisController.clickPost.hot;
					Hot parentHot = thisController.currentPost.hot;
					int index = parentHot.children.indexOf(hot.id);

					setPost(hot, parentHot, index);
					resolvePostsWhenOpen();
					setCardList(hot);

					thisController.clickPost = null;

					mClosePostSpring.setCurrentValue(0);
					mClosePostSpring.setEndValue(0);

					mOpenPostSpring.setCurrentValue(1);
					mOpenPostSpring.setEndValue(1);

					mFoldCardSpring.setCurrentValue(0.2);
					mFoldCardSpring.setEndValue(0.2);
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

			if (value == 0) {

			} else if (value == 1) {
				if (thisController.currentPost != null && thisController.currentPost.parent != null) {
					resolvePostsBeforeClose();
					PostBody post = viewManage.postPool.getPost(thisController.currentPost.parent);
					Hot hot = post.hot;

					PostBody parentPost = viewManage.postPool.getPost(post.parent);
					// or Hot parentHot = hotMap.get(post.parent);
					Hot parentHot = null;
					int index = 0;
					if (parentPost != null) {
						parentHot = parentPost.hot;
						index = parentHot.children.indexOf(hot.id);
					}

					setPost(hot, parentHot, index);

					setCardList(hot);

					thisController.clickPost = null;

					mClosePostSpring.setCurrentValue(0);
					mClosePostSpring.setEndValue(0);

					mOpenPostSpring.setCurrentValue(1);
					mOpenPostSpring.setEndValue(1);

					mFoldCardSpring.setCurrentValue(1);
					mFoldCardSpring.setEndValue(1);
				}
			}
			mClosePostSpring.setSpringConfig(fast_config);
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
		}
	}

	@SuppressLint("NewApi")
	public void renderOpenPost() {
		double value = mOpenPostSpring.getCurrentValue();
		if (thisController.clickPost != null) {
			thisController.clickPost.render(value);
		}

		if (value < 0.1) {
			if (thisController.clickPost != null && thisController.clickPost.hotType.type == thisController.clickPost.hotType.PAPER) {
				logo.setTextColor(0xff0099cd);
				more_image.setColorFilter(0xff0099cd);
			} else {
				logo.setTextColor(0xeeffffff);
				more_image.setColorFilter(0xeeffffff);
			}
		} else {
			if (thisController.currentPost != null && thisController.currentPost.hotType.type == thisController.currentPost.hotType.PAPER) {
				logo.setTextColor(0xff0099cd);
				more_image.setColorFilter(0xff0099cd);
			} else {
				logo.setTextColor(0xeeffffff);
				more_image.setColorFilter(0xeeffffff);
			}
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

		if (value < 0.1) {
			if (thisController.currentPost != null && thisController.currentPost.hotType.type == thisController.currentPost.hotType.PAPER) {
				logo.setTextColor(0xff0099cd);
				more_image.setColorFilter(0xff0099cd);
			}
		} else {
			logo.setTextColor(0xeeffffff);
			more_image.setColorFilter(0xeeffffff);
		}
	}

	public TouchView.LayoutParams renderParams;

	public void renderFoldCard() {
		double value = mFoldCardSpring.getCurrentValue();

		if (thisController.currentPost == null) {
			viewManage.reportError(tag, 434);
			return;
		} else if (thisController.currentPost.children == null) {
			return;
		}

		int listSize = thisController.currentPost.children.size();
		float x = 0;
		float y = (float) ((displayMetrics.heightPixels - 38 - cardHeight) + cardHeight * (1 - value));
		for (int i = 0; i < listSize; i++) {
			String key = thisController.currentPost.children.get(i);
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
			if (value > 0.8) {
				post.endValue = 1;
			}
		}

		if (value < 0.1) {
			album.setVisibility(View.VISIBLE);
			album.setScaleX((float) (1 + 2 * value));
			album.setScaleY((float) (1 + 2 * value));
		} else {
			album.setVisibility(View.INVISIBLE);
		}

		if (value < 0.2) {
			thisController.subCardStatus.state = thisController.subCardStatus.FOLD;
		} else if (value > 0.8) {
			thisController.subCardStatus.state = thisController.subCardStatus.UNFOLD;
		} else {
			thisController.subCardStatus.state = thisController.subCardStatus.MOVING;
		}

	}
}