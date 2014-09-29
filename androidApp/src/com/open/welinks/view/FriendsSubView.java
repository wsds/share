package com.open.welinks.view;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.rebound.BaseSpringSystem;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.open.lib.TouchView;
import com.open.lib.viewbody.ListBody1;
import com.open.lib.viewbody.ListBody1.MyListItemBody;
import com.open.welinks.R;
import com.open.welinks.controller.FriendsSubController;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Circle;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.model.FileHandlers;
import com.open.welinks.model.LBSHandlers;
import com.open.welinks.model.Parser;
import com.open.welinks.utils.MCImageUtils;

public class FriendsSubView {

	public Data data = Data.getInstance();

	public String tag = "FriendsSubView";

	public DisplayMetrics displayMetrics;

	public TouchView friendsView;

	public ListBody1 friendListBody;

	public List<String> circles;
	public Map<String, Circle> circlesMap;
	public Map<String, Friend> friendsMap;

	public MainView mainView;

	public Parser parser = Parser.getInstance();

	public FileHandlers fileHandlers = FileHandlers.getInstance();

	public DisplayImageOptions options;

	public FriendsSubView(MainView mainView) {
		this.mainView = mainView;
	}

	public void initData() {
	}

	public void initViews() {

		this.friendsView = mainView.friendsView;
		this.displayMetrics = mainView.displayMetrics;
		this.mInflater = mainView.thisActivity.getLayoutInflater();

		friendsView = (TouchView) mainView.friendsView.findViewById(R.id.friendsContainer);
		friendListBody = new ListBody1();
		friendListBody.initialize(displayMetrics, friendsView);
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).displayer(new RoundedBitmapDisplayer(52)).build();

		initSmallBusinessCardDialog();
	}

	public void showCircles() {
		data = parser.check();
		circles = data.relationship.circles;
		circlesMap = data.relationship.circlesMap;
		friendsMap = data.relationship.friendsMap;

		this.friendListBody.containerView.removeAllViews();
		this.friendListBody.height = 0;
		this.friendListBody.y = 0;

		this.friendListBody.listItemsSequence.clear();

		for (int i = 0; i < circles.size(); i++) {
			Circle circle = circlesMap.get(circles.get(i));
			// TODO why circle is null from user infomation update event
			if (circle == null) {
				continue;
			}

			CircleBody circleBody = null;
			circleBody = new CircleBody(this.friendListBody);
			circleBody.initialize();
			circleBody.setContent(circle);

			this.friendListBody.listItemsSequence.add("circle#" + circle.rid);
			this.friendListBody.listItemBodiesMap.put("circle#" + circle.rid, circleBody);

			TouchView.LayoutParams layoutParams = new TouchView.LayoutParams((int) (displayMetrics.widthPixels - displayMetrics.density * 20), (int) (circleBody.itemHeight - 10 * displayMetrics.density));
			circleBody.y = this.friendListBody.height;
			circleBody.cardView.setY(circleBody.y);
			circleBody.cardView.setX(0);

			this.friendListBody.containerView.addView(circleBody.cardView, layoutParams);
			this.friendListBody.height = this.friendListBody.height + circleBody.itemHeight;
			// Log.d(tag, "addView");
			Log.v(tag, "this.friendListBody.height: " + this.friendListBody.height + "    circleBody.y:  " + circleBody.y);
		}

		this.friendListBody.containerHeight = (int) (this.displayMetrics.heightPixels - 38 - displayMetrics.density * 88);
	}

	Bitmap bitmap = null;

	public class CircleBody extends MyListItemBody {

		CircleBody(ListBody1 listBody) {
			listBody.super();
		}

		public List<String> friendsSequence = new ArrayList<String>();
		public Map<String, FriendBody> friendBodiesMap = new HashMap<String, FriendBody>();

		public TouchView cardView = null;
		public TextView leftTopText = null;
		public TouchView leftTopTextButton = null;
		public TouchView gripView = null;
		public ImageView gripCardBackground = null;

		int lineCount = 0;

		public View initialize() {

			Resources resources = mainView.thisActivity.getResources();
			bitmap = BitmapFactory.decodeResource(resources, R.drawable.face_man);
			bitmap = MCImageUtils.getCircleBitmap(bitmap, true, 5, Color.WHITE);

			this.cardView = (TouchView) mainView.mInflater.inflate(R.layout.view_control_circle_card, null);
			this.leftTopText = (TextView) this.cardView.findViewById(R.id.leftTopText);
			this.gripView = (TouchView) this.cardView.findViewById(R.id.grip);
			this.leftTopTextButton = (TouchView) this.cardView.findViewById(R.id.leftTopTextButton);

			this.gripCardBackground = (ImageView) this.cardView.findViewById(R.id.grip_card_background);

			this.leftTopTextButton.setOnTouchListener(thisController.onTouchListener);
			// this.leftTopText.setOnLongClickListener(mainView.thisController.onLongClickListener);

			this.gripView.setOnTouchListener(thisController.onTouchListener);

			itemWidth = mainView.displayMetrics.widthPixels - 20 * mainView.displayMetrics.density;
			itemHeight = 260 * displayMetrics.density;

			super.initialize(cardView);
			return cardView;
		}

		public void setContent(Circle circle) {
			this.leftTopText.setText(circle.name);

			this.leftTopTextButton.setTag(R.id.tag_first, circle);
			this.leftTopTextButton.setTag(R.id.tag_class, "card_title");

			this.gripView.setTag(R.id.tag_first, circle);
			this.gripView.setTag(R.id.tag_class, "card_grip");

			int lineCount = circle.friends.size() / 4;
			if (lineCount == 0) {
				lineCount = 1;
			}
			int membrane = circle.friends.size() % 4;
			if (membrane != 0) {
				lineCount++;
			}
			itemHeight = (78 + lineCount * 96) * displayMetrics.density;// 174 to 78

			//
			int containerWidth = (int) (displayMetrics.widthPixels - 20 * displayMetrics.density);
			int spacing = (int) (20 * displayMetrics.density);
			int singleWidth = (containerWidth - spacing * 5) / 4;
			//

			TouchView.LayoutParams layoutParams = new TouchView.LayoutParams(singleWidth, (int) (78 * displayMetrics.density));
			this.friendsSequence.clear();
			// Log.e(tag, circle.friends.size() + "---size");
			for (int i = 0; i < circle.friends.size(); i++) {
				String phone = circle.friends.get(i);
				Friend friend = friendsMap.get(phone);

				FriendBody friendBody = new FriendBody();
				friendBody.Initialize();
				friendBody.setData(friend);

				this.cardView.addView(friendBody.friendView, layoutParams);
				int x = (i % 4 + 1) * spacing + (i % 4) * singleWidth;
				int y = (int) ((i / 4) * (95 * displayMetrics.density) + 64 * displayMetrics.density);
				// int x = 120 * (int) displayMetrics.density * (i % 4) + (int) itemWidth / 16;
				// int y = 140 * (int) displayMetrics.density * (i / 4) + 96 * (int) displayMetrics.density;

				// int rows = i / 4;
				// int membrane = i % 4;
				// if (membrane != 0) {
				// rows = rows + 1;
				// }
				// int x = 120 * (int) displayMetrics.density * membrane + (int) itemWidth / 16;
				// int y = 140 * (int) displayMetrics.density * rows + 96 * (int) displayMetrics.density;

				friendBody.friendView.setX(x);
				friendBody.friendView.setY(y);

				if (this.friendBodiesMap.get(phone) == null) {
					// optimize friendBodiesMap pool
				}
			}
		}
	}

	public class FriendBody {
		public View friendView = null;

		public ImageView headImageView;
		public TextView nickNameView;

		public View Initialize() {
			this.friendView = mainView.mInflater.inflate(R.layout.circles_gridpage_item, null);
			this.headImageView = (ImageView) this.friendView.findViewById(R.id.head_image);
			this.nickNameView = (TextView) this.friendView.findViewById(R.id.nickname);
			return friendView;
		}

		public void setData(Friend friend) {

			fileHandlers.getHeadImage(friend.head, this.headImageView, options);
			// this.headImageView.setImageBitmap(bitmap);

			this.nickNameView.setText(friend.nickName);
			this.friendView.setTag(R.id.friendsContainer, friend);
			this.friendView.setTag(R.id.tag_class, "friend_view");
			this.friendView.setOnClickListener(thisController.mOnClickListener);

			this.friendView.setOnTouchListener(thisController.onTouchListener);

		}
	}

	public PopupWindow inputPopWindow;
	public View inputDialogView;

	public void showInputDialog() {
		mInflater = mainView.thisActivity.getLayoutInflater();
		inputDialogView = mInflater.inflate(R.layout.widget_alert_input_dialog, null);
		inputPopWindow = new PopupWindow(inputDialogView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		inputPopWindow.setBackgroundDrawable(new BitmapDrawable());
		inputPopWindow.showAtLocation(mainView.main_container, Gravity.CENTER, 0, 0);
	}

	public BaseSpringSystem mSpringSystem = SpringSystem.create();
	public SpringConfig IMAGE_SPRING_CONFIG = SpringConfig.fromOrigamiTensionAndFriction(40, 9);
	public SpringConfig IMAGE_SPRING_CONFIG_TO = SpringConfig.fromOrigamiTensionAndFriction(40, 15);
	public Spring dialogSpring = mSpringSystem.createSpring().setSpringConfig(IMAGE_SPRING_CONFIG);
	public Spring dialogOutSpring = mSpringSystem.createSpring().setSpringConfig(IMAGE_SPRING_CONFIG_TO);
	public Spring dialogInSpring = mSpringSystem.createSpring().setSpringConfig(IMAGE_SPRING_CONFIG_TO);
	public View dialogRootView;
	public DialogShowSpringListener dialogSpringListener = new DialogShowSpringListener();

	public RelativeLayout cirlcesDialogContent;
	public PopupWindow circlePopWindow;
	public View circleDialogView;

	public RelativeLayout dialogContentView;
	public View inputDialigView;

	public LayoutInflater mInflater;

	public int SHOW_DIALOG = 0x01;
	public int DIALOG_SWITCH = 0x02;
	public int currentStatus = SHOW_DIALOG;

	public TextView modifyCircleNameView, deleteCircleView, createCircleView;
	public TextView cancleButton;
	public TextView confirmButton;
	public EditText inputEditView;
	public TextView circleName;

	public void showCircleSettingDialog(Circle circle) {
		currentStatus = SHOW_DIALOG;
		dialogSpring.addListener(dialogSpringListener);
		// final DisplayMetrics displayMetrics = new DisplayMetrics();
		circleDialogView = mInflater.inflate(R.layout.circle_longclick_dialog, null);
		dialogContentView = (RelativeLayout) circleDialogView.findViewById(R.id.dialogContent);
		inputDialigView = circleDialogView.findViewById(R.id.inputDialogContent);
		height = displayMetrics.density * 140 + 0.5f;
		// y = inputDialigView.getTranslationY();
		y = ((displayMetrics.heightPixels - height) / 2) + displayMetrics.heightPixels;
		inputDialigView.setTranslationY(y);
		dialogRootView = dialogContentView;
		y0 = dialogRootView.getTranslationY();
		dialogSpring.setCurrentValue(0);
		dialogSpring.setEndValue(1);

		circlePopWindow = new PopupWindow(circleDialogView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		circlePopWindow.setBackgroundDrawable(new BitmapDrawable());
		circlePopWindow.showAtLocation(mainView.main_container, Gravity.CENTER, 0, 0);

		modifyCircleNameView = (TextView) circleDialogView.findViewById(R.id.modifyCircleName);
		modifyCircleNameView.setOnClickListener(mainView.thisController.mOnClickListener);

		deleteCircleView = (TextView) circleDialogView.findViewById(R.id.deleteCircle);
		deleteCircleView.setOnClickListener(mainView.thisController.mOnClickListener);
		deleteCircleView.setTag(R.id.tag_first, circle.rid);

		createCircleView = (TextView) circleDialogView.findViewById(R.id.createCircle);
		createCircleView.setOnClickListener(mainView.thisController.mOnClickListener);

		circleName = (TextView) circleDialogView.findViewById(R.id.circleName);

		cancleButton = (TextView) circleDialogView.findViewById(R.id.cancel);
		confirmButton = (TextView) circleDialogView.findViewById(R.id.confirm);
		cancleButton.setOnClickListener(mainView.thisController.mOnClickListener);
		confirmButton.setOnClickListener(mainView.thisController.mOnClickListener);
		inputEditView = (EditText) circleDialogView.findViewById(R.id.input);
		circleName.setText(circle.name);
		inputEditView.setText(circle.name);

		confirmButton.setTag(R.id.tag_first, inputEditView);
		confirmButton.setTag(R.id.tag_second, circle);

		confirmButton.setTag(R.id.tag_class, "CircleSettingConfirmButton");
	}

	public void dismissCircleSettingDialog() {
		circlePopWindow.dismiss();
	}

	float y;
	float height;
	float y0;

	public FriendsSubController thisController;

	private class DialogShowSpringListener extends SimpleSpringListener {
		@Override
		public void onSpringUpdate(Spring spring) {
			float mappedValue = (float) spring.getCurrentValue();
			if (spring.equals(dialogSpring)) {
				dialogRootView.setScaleX(mappedValue);
				dialogRootView.setScaleY(mappedValue);
			} else if (spring.equals(dialogOutSpring)) {
				dialogRootView.setTranslationY(y0 - displayMetrics.heightPixels * (1.0f - mappedValue));
				// Log.e(tag, mappedValue + "---------------");
				if (mappedValue <= 0.8f) {

				}
			} else if (spring.equals(dialogInSpring)) {
				float y0 = (mappedValue / 1f) * y;
				if (((displayMetrics.heightPixels - height) / 2) < y0)
					inputDialigView.setTranslationY(y0);
			}
		}
	}

	// small businesscard
	public DisplayImageOptions smallBusinessCardOptions;
	public View userCardMainView;
	public PopupWindow userCardPopWindow;
	public RelativeLayout userBusinessContainer;
	public TextView goInfomationView;
	public TextView goChatView;
	public ImageView userHeadView;
	public TextView userNickNameView;
	public TextView userAgeView;
	public TextView distanceView;
	public TextView lastLoginTimeView;
	public TextView singleButtonView;

	public void initSmallBusinessCardDialog() {
		userCardMainView = mInflater.inflate(R.layout.account_info_pop, null);
		userNickNameView = (TextView) userCardMainView.findViewById(R.id.userNickName);
		userAgeView = (TextView) userCardMainView.findViewById(R.id.userAge);
		distanceView = (TextView) userCardMainView.findViewById(R.id.userDistance);
		lastLoginTimeView = (TextView) userCardMainView.findViewById(R.id.lastLoginTime);
		userBusinessContainer = (RelativeLayout) userCardMainView.findViewById(R.id.userBusinessView);
		int height = (int) (displayMetrics.heightPixels * 0.5f - 50 * displayMetrics.density) + getStatusBarHeight(mainView.thisActivity);
		userBusinessContainer.getLayoutParams().height = height;
		goInfomationView = (TextView) userCardMainView.findViewById(R.id.goInfomation);
		goChatView = (TextView) userCardMainView.findViewById(R.id.goChat);
		singleButtonView = (TextView) userCardMainView.findViewById(R.id.singleButton);
		singleButtonView.setVisibility(View.GONE);
		userHeadView = (ImageView) userCardMainView.findViewById(R.id.userHead);
		userHeadView.getLayoutParams().height = height;
		userCardPopWindow = new PopupWindow(userCardMainView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		userCardPopWindow.setBackgroundDrawable(new BitmapDrawable());
		smallBusinessCardOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).displayer(new RoundedBitmapDisplayer(5)).build();
	}

	LBSHandlers lbsHandlers = LBSHandlers.getInstance();

	public void setSmallBusinessCardContent(Friend friend) {
		User user = data.userInformation.currentUser;
		goInfomationView.setTag(R.id.tag_first, friend.phone);
		goChatView.setTag(R.id.tag_first, friend.phone);
		fileHandlers.getHeadImage(friend.head, userHeadView, smallBusinessCardOptions);
		userNickNameView.setText(friend.nickName);
		userAgeView.setText(friend.age + "");
		distanceView.setText(lbsHandlers.pointDistance(user.longitude, user.latitude, friend.longitude, friend.latitude) + "km");
		lastLoginTimeView.setText("0小时前");
	}

	public void showUserCardDialogView() {
		if (userCardPopWindow != null && !userCardPopWindow.isShowing()) {
			userCardPopWindow.showAtLocation(mainView.main_container, Gravity.CENTER, 0, 0);
		}
	}

	public void dismissUserCardDialogView() {
		if (userCardPopWindow != null && userCardPopWindow.isShowing()) {
			userCardPopWindow.dismiss();
		}
	}

	public static int getStatusBarHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return statusBarHeight;
	}
}
