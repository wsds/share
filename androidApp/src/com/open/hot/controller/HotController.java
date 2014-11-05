package com.open.hot.controller;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.open.hot.R;
import com.open.hot.model.Data;
import com.open.hot.model.Data.Hot;
import com.open.hot.model.Parser;
import com.open.hot.view.HotView;
import com.open.hot.view.HotView.Status;
import com.open.hot.view.PostBody;
import com.open.hot.view.ViewManage;
import com.open.lib.OpenLooper;
import com.open.lib.OpenLooper.LoopCallback;

public class HotController {
	public Data data = Data.getInstance();
	public String tag = "LoginController";

	public Context context;
	public HotView thisView;
	public HotController thisController;
	public Activity thisActivity;

	public OnFocusChangeListener mOnFocusChangeListener;
	public OnClickListener mOnClickListener;
	public OnTouchListener onTouchListener;

	public Gson gson = new Gson();

	public InputMethodManager mInputMethodManager;
	public GestureDetector mGesture;

	public HotController(Activity activity) {
		this.context = activity;
		this.thisActivity = activity;
	}

	public PostBody clickPost;
	public Hot currentHot;
	public PostBody currentPost;
	public PostBody postClick_debug;

	public ViewManage viewManage = ViewManage.getInstance();

	public void initializeListeners() {
		onTouchListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				int action = event.getAction();
				if (action == MotionEvent.ACTION_DOWN) {
					String view_class = (String) view.getTag(R.id.tag_class);
					if (view_class.equals("PostView")) {
						String key = (String) view.getTag(R.id.tag_key);
						PostBody post = thisView.viewManage.postPool.getPost(key);
						if (post != null && post.endValue == 1 && thisView.mOpenPostSpring.getCurrentValue() == 1) {
							Log.d(tag, "Touch: " + post.key);
							clickPost = post;

						}
					}
				} else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
				}
				return false;
			}
		};
		viewManage.onTouchListener = onTouchListener;
		mOnFocusChangeListener = new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View view, boolean hasFocus) {
			}
		};

		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(thisView.logo)) {
					Log.d(tag, "logo");

					if (thisView.mFoldCardSpring.getEndValue() == 0) {
						thisView.mFoldCardSpring.setEndValue(1);
					} else {
						thisView.mFoldCardSpring.setEndValue(0);
					}

					Parser parser = Parser.getInstance();
					parser.parse();
				} else if (view.equals(thisView.album)) {
					foldCard(false);
				} else if (view.equals(thisView.more)) {
					Log.e(tag, "more");
					logEventStatus();
					logSubCardStatus();
					PostBody post = viewManage.postPool.getPost("2009");
					post.logPost();
					Log.e(tag, "currentPost:  " + currentPost.key);
					currentPost.logPost();
				}
			}
		};

	}

	public void bindEvent() {
		thisView.logo.setOnClickListener(mOnClickListener);
		thisView.album.setOnClickListener(mOnClickListener);
		thisView.more.setOnClickListener(mOnClickListener);
		thisView.clicker.setOnClickListener(mOnClickListener);
	}

	public void onCreate() {
		thisView.status = Status.loginOrRegister;

		mGesture = new GestureDetector(thisActivity, new GestureListener());
		openLooper = new OpenLooper();
		openLooper.createOpenLooper();
		loopCallback = new ListLoopCallback(openLooper);
		openLooper.loopCallback = loopCallback;
	}

	public void onResume() {
		Log.d(tag, "onResume");
		// thisView.setPost(data.me);
	}

	public void onPause() {
	}

	public void onDestroy() {

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean flag = false;
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (thisView.status == Status.loginUsePassword) {
			} else if (thisView.status == Status.verifyPhoneForLogin) {

			} else if (thisView.status == Status.verifyPhoneForRegister) {

			} else if (thisView.status == Status.verifyPhoneForResetPassword) {
			} else {
			}
		} else {
		}
		return flag;
	}

	float touch_pre_x = 0;
	float touch_pre_y = 0;

	boolean scale = true;

	boolean fold = true;

	public class SubCardStatus {
		public int UNFOLD = 0, MOVING = 1, FOLD = 2;
		public int state = UNFOLD;
	}

	public void logSubCardStatus() {
		if (subCardStatus.state == subCardStatus.UNFOLD) {
			Log.w(tag, "subCardStatus:   " + "UNFOLD");
		} else if (subCardStatus.state == subCardStatus.MOVING) {
			Log.w(tag, "subCardStatus:   " + "MOVING");
		} else if (subCardStatus.state == subCardStatus.FOLD) {
			Log.w(tag, "subCardStatus:   " + "FOLD");
		}
	}

	public SubCardStatus subCardStatus = new SubCardStatus();

	public boolean atPostTop = true;
	public boolean atPostBottom = true;

	public class EventStatus {
		public int Done = 0, Fold = 1, UnFold = 2, ScrollPost = 3, ClosePost = 4, FlipPage = 5, OpenPost = 6, ScrollList = 7, ScrollPost_Horizontal = 8;
		public int state = Done;
	}

	public void logEventStatus() {
		if (eventStatus.state == eventStatus.Done) {
			Log.w(tag, "eventStatus:   " + "Done");
		} else if (eventStatus.state == eventStatus.Fold) {
			Log.w(tag, "eventStatus:   " + "Fold");
		} else if (eventStatus.state == eventStatus.UnFold) {
			Log.w(tag, "eventStatus:   " + "UnFold");
		} else if (eventStatus.state == eventStatus.ScrollPost) {
			Log.w(tag, "eventStatus:   " + "ScrollPost");
		} else if (eventStatus.state == eventStatus.ClosePost) {
			Log.w(tag, "eventStatus:   " + "ClosePost");
		} else if (eventStatus.state == eventStatus.FlipPage) {
			Log.w(tag, "eventStatus:   " + "FlipPage");
		} else if (eventStatus.state == eventStatus.OpenPost) {
			Log.w(tag, "eventStatus:   " + "OpenPost");
		} else if (eventStatus.state == eventStatus.ScrollList) {
			Log.w(tag, "eventStatus:   " + "ScrollList");
		} else if (eventStatus.state == eventStatus.ScrollPost_Horizontal) {
			Log.w(tag, "eventStatus:   " + "ScrollPost_Horizontal");
		}
	}

	public EventStatus eventStatus = new EventStatus();

	public class TouchDownArea {
		public int A = 0, B = 1, C = 2;
		public int area = A;
	}

	public TouchDownArea touchDownArea = new TouchDownArea();

	public class TouchStatus {
		public int None = 4, Down = 1, Horizontal = 2, Vertical = 3, Up = 4, LongPress = 5;
		public int state = None;
	}

	public TouchStatus touchStatus = new TouchStatus();

	public boolean onTouchEvent(MotionEvent event) {
		int motionEvent = event.getAction();
		float x = event.getX();
		float y = event.getY();
		if (motionEvent == MotionEvent.ACTION_DOWN) {
			touch_pre_x = x;
			touch_pre_y = y;
			last_Δy = 0;

			if (y < viewManage.position_B) {
				touchDownArea.area = touchDownArea.A;
				// foldCard();
			} else if (y > viewManage.position_C) {
				touchDownArea.area = touchDownArea.C;
			} else {
				touchDownArea.area = touchDownArea.B;
				list_x_down = currentPost.childList_x;
				dxSpeed = 0;
			}

			if (eventStatus.state != eventStatus.Done) {
				return true;
			}
			touchStatus.state = touchStatus.Down;
		} else if (motionEvent == MotionEvent.ACTION_MOVE) {
			float Δy = (y - touch_pre_y);
			float Δx = (x - touch_pre_x);
			if (touchStatus.state == touchStatus.Down) {
				if (Δx * Δx + Δy * Δy > 400) {
					if (Δx * Δx > Δy * Δy) {
						touchStatus.state = touchStatus.Horizontal;
						Log.d(tag, " ACTION_MOVE Horizontal");
					} else {
						touchStatus.state = touchStatus.Vertical;
						Log.d(tag, " ACTION_MOVE Vertical");

					}
					touch_pre_x = x;
					touch_pre_y = y;

					if (touchDownArea.area == touchDownArea.A) {
						foldCard(true);
					}
				}
			} else if (touchStatus.state == touchStatus.Horizontal) {
				if (touchDownArea.area == touchDownArea.B || touchDownArea.area == touchDownArea.C) {
					if (subCardStatus.state == subCardStatus.UNFOLD) {
						scrollList(Δx);
					}
				} else if (touchDownArea.area == touchDownArea.A) {
					flipPage(Δx);
				}
			} else if (touchStatus.state == touchStatus.Vertical) {
				if (touchDownArea.area == touchDownArea.A) {
					if (Δy < 0) {

					} else {
						if (atPostTop == true) {
							closePost(Δy);
						}
					}
				} else if (touchDownArea.area == touchDownArea.B) {
					if (Δy < 0) {
						if (subCardStatus.state == subCardStatus.UNFOLD) {
							openPost(Δy);
						}
					} else {
						if (subCardStatus.state != subCardStatus.FOLD) {
							foldCard(Δy);
						} else {
							if (atPostTop == true) {
								closePost(Δy);
							}
						}
					}
				} else if (touchDownArea.area == touchDownArea.C) {
					if (Δy < 0) {
						if (subCardStatus.state != subCardStatus.UNFOLD) {
							foldCard(Δy);
						} else {
							openPost(Δy);
						}
					} else {
					}
				}

			}
		} else if (motionEvent == MotionEvent.ACTION_UP) {

			if (touchStatus.state == touchStatus.Down) {
				onClick();
			} else {
				logEventStatus();
				if (eventStatus.state == eventStatus.Fold) {
					double value = thisView.mFoldCardSpring.getCurrentValue();
					if (value > 0.5) {
						if (value != 1) {
							subCardStatus.state = subCardStatus.MOVING;
							thisView.mFoldCardSpring.setEndValue(1);
						}
					} else {
						if (value != 0) {
							subCardStatus.state = subCardStatus.MOVING;
							thisView.mFoldCardSpring.setEndValue(0);
						}
					}
				} else if (eventStatus.state == eventStatus.OpenPost) {
					double value = thisView.mOpenPostSpring.getCurrentValue();
					if (value > 0.5) {
						thisView.mOpenPostSpring.setEndValue(1);
					} else {
						thisView.mOpenPostSpring.setEndValue(0);
					}
				} else if (eventStatus.state == eventStatus.ClosePost) {
					double value = thisView.mClosePostSpring.getCurrentValue();
					if (value > 0.5) {
						thisView.mClosePostSpring.setEndValue(1);
					} else {
						thisView.mClosePostSpring.setEndValue(0);
					}
				} else if (eventStatus.state == eventStatus.FlipPage) {
					double value = thisView.mPagerSpring.getCurrentValue();
					thisView.mPagerSpring.setEndValue(Math.round(value));
				}
			}
		}
		mGesture.onTouchEvent(event);
		return true;
	}

	public void onClick() {
		if (touchDownArea.area == touchDownArea.A) {
			thisView.clicker.performClick();
			foldCard(true);
		} else if (touchDownArea.area == touchDownArea.C || touchDownArea.area == touchDownArea.B) {
			thisView.clicker.performClick();
			openPost();
		}
	}

	public void foldCard(boolean isFord) {
		if (isFord == true) {
			if (eventStatus.state == eventStatus.ClosePost) {
			}
			if (subCardStatus.state != subCardStatus.FOLD) {
				clickPost = null;
				thisView.mFoldCardSpring.setEndValue(0);
				// eventStatus.state = eventStatus.Fold;
				subCardStatus.state = subCardStatus.MOVING;

				thisView.mOpenPostSpring.setCurrentValue(1);
				thisView.mOpenPostSpring.setEndValue(1);
			}
		} else {
			if (subCardStatus.state != subCardStatus.UNFOLD) {
				clickPost = null;
				thisView.mFoldCardSpring.setEndValue(1);
				eventStatus.state = eventStatus.Fold;
				subCardStatus.state = subCardStatus.MOVING;

				thisView.mOpenPostSpring.setCurrentValue(1);
				thisView.mOpenPostSpring.setEndValue(1);
			}
		}
	}

	public void foldCard(float Δy) {
		if (eventStatus.state != eventStatus.Done && eventStatus.state != eventStatus.Fold && eventStatus.state != eventStatus.OpenPost) {
			return;
		}
		eventStatus.state = eventStatus.Fold;
		float ratio1 = Δy / (thisView.cardHeight);

		if (Δy > 0) {
			if (ratio1 > 1) {
				ratio1 = 1;
			}
			if (ratio1 < 0) {
				ratio1 = 0;
			}
			thisView.mFoldCardSpring.setCurrentValue(1 - ratio1);
			thisView.mFoldCardSpring.setEndValue(1 - ratio1);

			thisView.mOpenPostSpring.setCurrentValue(1);
			thisView.mOpenPostSpring.setEndValue(1);
		} else {
			if (ratio1 < -1) {
				ratio1 = -1;
			}
			if (ratio1 > 0) {
				ratio1 = 0;
			}
			thisView.mFoldCardSpring.setCurrentValue(-ratio1);
			thisView.mFoldCardSpring.setEndValue(-ratio1);
		}

		thisView.renderFoldCard();
	}

	public boolean closePost() {
		if (eventStatus.state != eventStatus.Done && eventStatus.state != eventStatus.ClosePost) {
			return false;
		}
		if (currentPost.parent == null) {
			return false;
		}
		eventStatus.state = eventStatus.ClosePost;

		thisView.mClosePostSpring.setEndValue(1);
		return true;
	}

	public float last_Δy = 0;

	public void closePost(float Δy) {

		if (eventStatus.state == eventStatus.Done) {
			if (currentPost.parent == null) {
				return;
			}
			eventStatus.state = eventStatus.ClosePost;
		} else if (eventStatus.state == eventStatus.ClosePost) {
		} else {
			last_Δy = Δy;
			return;
		}
		float ratio = -(Δy - 0) / (thisView.displayMetrics.heightPixels - 38 - thisView.cardHeight);

		if (ratio <= -1) {
			ratio = -0.994f;
		}
		if (ratio > 0) {
			ratio = 0;
		}
		thisView.mClosePostSpring.setCurrentValue(-ratio);
		thisView.mClosePostSpring.setEndValue(-ratio);

		thisView.renderClosePost();
	}

	public void openPost() {
		if (eventStatus.state != eventStatus.Done && eventStatus.state != eventStatus.Fold && eventStatus.state != eventStatus.OpenPost) {
			return;
		}

		if (clickPost == null) {
			return;
		}

		eventStatus.state = eventStatus.OpenPost;
		clickPost.isRecordX = false;
		clickPost.recordX();

		if (clickPost != null) {
			thisView.mOpenPostSpring.setEndValue(0);
		}
	}

	public void openPost(float Δy) {
		if (eventStatus.state != eventStatus.Done && eventStatus.state != eventStatus.Fold && eventStatus.state != eventStatus.OpenPost) {
			return;
		}
		if (clickPost == null) {
			return;
		}
		if (eventStatus.state == eventStatus.Done || eventStatus.state == eventStatus.Fold) {
			eventStatus.state = eventStatus.OpenPost;
			clickPost.isRecordX = false;
			clickPost.recordX();
		}

		float ratio = -Δy / (thisView.displayMetrics.heightPixels - 38 - thisView.cardHeight);

		if (ratio >= 1) {
			ratio = 0.994f;
		}
		if (ratio < 0) {
			ratio = 0;
		}

		thisView.mOpenPostSpring.setCurrentValue(1 - ratio);
		thisView.mOpenPostSpring.setEndValue(1 - ratio);

		thisView.mFoldCardSpring.setCurrentValue(1);
		thisView.mFoldCardSpring.setEndValue(1);

		thisView.renderOpenPost();
	}

	public float list_x_down = 0;

	public void slidingList(float dxSpeed) {
		this.dxSpeed = dxSpeed;
		this.lastMillis = System.currentTimeMillis();
		this.openLooper.start();
	}

	public void scrollList(float Δx) {
		currentPost.childList_x = list_x_down + Δx;
		if (currentPost.childList_x >= 0) {
			currentPost.childList_x = 0;
			this.dxSpeed = 0;
		} else if (currentPost.childList_x < -viewManage.listWidth + viewManage.screenWidth + 2 * viewManage.displayMetrics.density) {
			currentPost.childList_x = -viewManage.listWidth + viewManage.screenWidth + 2 * viewManage.displayMetrics.density;
			this.dxSpeed = 0;
		}

		updateListX();
	}

	public void moveList(float Δx) {
		currentPost.childList_x = currentPost.childList_x + Δx;
		if (currentPost.childList_x >= 0) {
			currentPost.childList_x = 0;
			this.dxSpeed = 0;
		} else if (currentPost.childList_x < -viewManage.listWidth + viewManage.screenWidth + 2 * viewManage.displayMetrics.density) {
			currentPost.childList_x = -viewManage.listWidth + viewManage.screenWidth + 2 * viewManage.displayMetrics.density;
			this.dxSpeed = 0;
		}

		updateListX();
	}

	public void updateListX() {
		for (int i = 0; i < currentPost.children.size(); i++) {
			String key = currentPost.children.get(i);
			PostBody post = viewManage.postPool.getPost(key);
			post.updateX();
		}
	}

	public long lastMillis = 0;
	OpenLooper openLooper = null;
	LoopCallback loopCallback = null;

	public class ListLoopCallback extends LoopCallback {
		public ListLoopCallback(OpenLooper openLooper) {
			openLooper.super();
		}

		@Override
		public void loop(double ellapsedMillis) {

			flingHoming((float) ellapsedMillis);
		}
	}

	public void flingHoming(float delta1) {
		long currentMillis = System.currentTimeMillis();

		if (lastMillis != 0) {
			long delta = currentMillis - lastMillis;
			dampenSpeed(delta);
			moveList(this.ratio * delta * this.dxSpeed);
		}

		lastMillis = currentMillis;

		if (this.dxSpeed == 0) {
			this.openLooper.stop();
		}
	}

	float dxSpeed = 0;
	float ratio = 0.0008f;

	public void dampenSpeed(long deltaMillis) {

		if (dxSpeed != 0.0f) {
			dxSpeed *= (1.0f - 0.002f * deltaMillis);
			if (Math.abs(dxSpeed) < 50f)
				dxSpeed = 0.0f;
		}
	}

	public void flipPage(String direction) {
		double value = thisView.mPagerSpring.getCurrentValue();
		double floorValue = Math.floor(value);
		if (direction.equals("left") && floorValue >= 0) {
			thisView.mPagerSpring.setEndValue(floorValue);
		} else if (direction.equals("right") && floorValue + 1 < currentPost.brothers.size()) {
			thisView.mPagerSpring.setEndValue(floorValue + 1);
		}

	}

	public void flipPage(float Δx) {
		if (eventStatus.state != eventStatus.Done && eventStatus.state != eventStatus.FlipPage) {
			return;
		}
		eventStatus.state = eventStatus.FlipPage;

		if (currentPost == null || currentPost.brothers == null) {
			return;
		}

		float ratio = currentPost.index - Δx / thisView.displayMetrics.widthPixels;

		if (ratio < 0) {
			ratio = 0;
		}
		if (ratio > currentPost.brothers.size() - 1) {
			ratio = currentPost.brothers.size() - 1;
		}
		thisView.mPagerSpring.setCurrentValue(ratio);
		thisView.mPagerSpring.setEndValue(ratio);
	}

	class GestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if (velocityX * velocityX + velocityY * velocityY > 250000) {
				if (velocityX * velocityX > velocityY * velocityY) {

					if (touchDownArea.area == touchDownArea.B) {
						slidingList(velocityX);
					} else if (touchDownArea.area == touchDownArea.A) {
						if (velocityX > 0) {
							flipPage("left");
						} else {
							flipPage("right");
						}
					}

				} else {

					if (velocityY > 0) {
						if (touchDownArea.area == touchDownArea.B) {
							if (subCardStatus.state != subCardStatus.FOLD) {
								foldCard(true);
							} else {
								if (atPostTop == true) {
									closePost();
								}
							}
						} else if (touchDownArea.area == touchDownArea.A) {
							closePost();
						}

					} else {
						if (touchDownArea.area == touchDownArea.B) {
							openPost();
						} else if (touchDownArea.area == touchDownArea.C) {
							foldCard(false);
							openPost();
						}
					}

				}
			}

			return true;
		}

		public void onLongPress(MotionEvent event) {

			if (touchDownArea.area == touchDownArea.A) {
				foldCard(true);
			}
		}

		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

			if (touchDownArea.area == touchDownArea.A) {
				// foldCard(true);
			}
			return false;
		}

		public boolean onDoubleTapEvent(MotionEvent event) {
			if (touchDownArea.area == touchDownArea.A) {
				foldCard(true);
			}
			return false;
		}
	}

	public boolean isExit = false;
	boolean isRoot = true;

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			foldCard(true);
			thisView.mClosePostSpring.setSpringConfig(thisView.mid_config);
			boolean isClosingPost = closePost();
			if (isClosingPost == true) {
				return true;
			}
			if (isExit) {
				thisActivity.finish();
			} else {
				if (isRoot) {
					Toast.makeText(thisActivity, "再按一次退出程序...", Toast.LENGTH_SHORT).show();
					isExit = true;
					new Thread() {
						@Override
						public void run() {
							try {
								sleep(2000);
								isExit = false;
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							super.run();
						}
					}.start();
				}
			}
		}
		return true;
	}

}
