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
import com.open.hot.model.Parser;
import com.open.hot.view.HotView;
import com.open.hot.view.HotView.Status;
import com.open.hot.view.PostBody;
import com.open.hot.view.ViewManage;

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

	public PostBody postClick;
	public PostBody postClick_debug;

	public ViewManage viewManage = ViewManage.getInstance();

	public void initializeListeners() {
		onTouchListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				int action = event.getAction();
				if (action == MotionEvent.ACTION_DOWN) {
					String view_class = (String) view.getTag(R.id.tag_class);
					if (view_class.equals("CardView")) {
						String key = (String) view.getTag(R.id.tag_key);
						PostBody post = thisView.viewManage.postPool.pool.get(key);
						if (post != null && post.endValue == 1 && thisView.mScaleCardSpring.getCurrentValue() == 1) {
							Log.d(tag, "Touch: " + post.key);
							postClick = post;
							postClick.recordX();
						}
					}
				} else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
				}
				return false;
			}
		};
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
					// if (thisView.mScaleCardSpring.getEndValue() == 0) {
					// thisView.mScaleCardSpring.setEndValue(1);
					// } else {
					// thisView.mScaleCardSpring.setEndValue(0);
					// }

					if (thisView.mFoldCardSpring.getEndValue() == 0) {
						thisView.mFoldCardSpring.setEndValue(1);
					} else {
						thisView.mFoldCardSpring.setEndValue(0);
					}

					Parser parser = Parser.getInstance();
					parser.parse();
				} else if (view.equals(thisView.album)) {
					if (thisView.mFoldCardSpring.getEndValue() == 0) {
						thisView.mFoldCardSpring.setEndValue(1);
					} else {
						thisView.mFoldCardSpring.setEndValue(0);
					}
				}
			}
		};

	}

	public void bindEvent() {
		thisView.logo.setOnClickListener(mOnClickListener);
		thisView.album.setOnClickListener(mOnClickListener);
		thisView.clicker.setOnClickListener(mOnClickListener);
	}

	public void onCreate() {
		thisView.status = Status.loginOrRegister;

		mGesture = new GestureDetector(thisActivity, new GestureListener());
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

	public SubCardStatus subCardStatus = new SubCardStatus();

	public boolean AtPostTop = true;
	public boolean AtPostBottom = true;

	public class EventStatus {
		public int Done = 0, Fold = 1, UnFold = 2, ScrollPost = 3, ClosePost = 4, FlipPage = 5, OpenPost = 6, ScrollList = 7, ScrollPost_Horizontal = 8;
		public int state = Done;
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
			Log.d(tag, "ACTION_DOWN");
			touch_pre_x = x;
			touch_pre_y = y;
			double value1 = thisView.mFoldCardSpring.getCurrentValue();
			if (value1 < 0.5) {
				subCardStatus.state = subCardStatus.FOLD;
			} else {
				subCardStatus.state = subCardStatus.UNFOLD;
			}

			if (y < viewManage.position_B) {
				touchDownArea.area = touchDownArea.A;
				// foldCard();
			} else if (y > viewManage.position_C) {
				touchDownArea.area = touchDownArea.C;
			} else {
				touchDownArea.area = touchDownArea.B;
			}

			if (eventStatus.state != eventStatus.Done) {
				return true;
			}
			touchStatus.state = touchStatus.Down;
		} else if (motionEvent == MotionEvent.ACTION_MOVE) {
			// Log.d(tag, " ACTION_MOVE");
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

			} else if (touchStatus.state == touchStatus.Vertical) {
				if (touchDownArea.area == touchDownArea.B) {
					if (Δy < 0) {
						if (subCardStatus.state == subCardStatus.UNFOLD) {
							openPost(Δy);
						}
					} else {
						if (subCardStatus.state != subCardStatus.FOLD) {
							foldCard(Δy);
						}
					}
				}
				if (touchDownArea.area == touchDownArea.C) {
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
			} else if (touchDownArea.area == touchDownArea.C || touchDownArea.area == touchDownArea.B) {
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
					double value = thisView.mScaleCardSpring.getCurrentValue();
					if (value > 0.5) {
						thisView.mScaleCardSpring.setEndValue(1);
					} else {
						thisView.mScaleCardSpring.setEndValue(0);
					}
				}
			}
		}
		mGesture.onTouchEvent(event);
		return true;
	}

	public void onClick() {
		if (touchDownArea.area == touchDownArea.C || touchDownArea.area == touchDownArea.B) {
			thisView.clicker.performClick();
			openPost();
		}
	}

	public void foldCard(boolean isFord) {
		if (isFord == true) {

			if (subCardStatus.state != subCardStatus.FOLD) {
				postClick = null;
				thisView.mFoldCardSpring.setEndValue(0);
				eventStatus.state = eventStatus.Fold;
				subCardStatus.state = subCardStatus.MOVING;
			}
		} else {
			if (subCardStatus.state != subCardStatus.UNFOLD) {
				postClick = null;
				thisView.mFoldCardSpring.setEndValue(1);
				eventStatus.state = eventStatus.Fold;
				subCardStatus.state = subCardStatus.MOVING;
			}
		}
	}

	public void foldCard(float Δy) {
		if (eventStatus.state != eventStatus.Done && eventStatus.state != eventStatus.Fold && eventStatus.state != eventStatus.OpenPost) {
			return;
		}
		eventStatus.state = eventStatus.Fold;
		// float ratio = -Δy / (thisView.displayMetrics.heightPixels - 38 - thisView.cardHeight);
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

			thisView.mScaleCardSpring.setCurrentValue(1);
			thisView.mScaleCardSpring.setEndValue(1);
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

	public void openPost() {
		Log.d(tag, " openPost");

		if (postClick != null) {
			Log.d(tag, " setEndValue 0" + "postClick key:" + postClick.key);

			thisView.mScaleCardSpring.setEndValue(0);
		}
	}

	public void openPost(float Δy) {
		if (eventStatus.state != eventStatus.Done && eventStatus.state != eventStatus.Fold && eventStatus.state != eventStatus.OpenPost) {
			return;
		}
		eventStatus.state = eventStatus.OpenPost;

		float ratio = -Δy / (thisView.displayMetrics.heightPixels - 38 - thisView.cardHeight);

		if (ratio >= 1) {
			ratio = 0.994f;
		}
		if (ratio < 0) {
			ratio = 0;
		}

		thisView.mScaleCardSpring.setCurrentValue(1 - ratio);
		thisView.mScaleCardSpring.setEndValue(1 - ratio);

		thisView.mFoldCardSpring.setCurrentValue(1);
		thisView.mFoldCardSpring.setEndValue(1);

		thisView.renderScaleCard();
	}

	class GestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if (velocityX * velocityX + velocityY * velocityY > 250000) {
				if (velocityX * velocityX > velocityY * velocityY) {

				} else {

					if (velocityY > 0) {
						if (touchDownArea.area == touchDownArea.B) {
							foldCard(true);
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
				foldCard(true);
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
