package com.open.welinks.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.open.welinks.BusinessCardActivity;
import com.open.welinks.R;
import com.open.welinks.controller.BusinessCardController;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.model.FileHandlers;
import com.open.welinks.model.LBSHandlers;
import com.open.welinks.utils.MCImageUtils;

public class BusinessCardView {

	public Data data = Data.getInstance();

	public BusinessCardController thisController;
	public BusinessCardView thisView;
	public BusinessCardActivity thisActivity;

	public LayoutInflater mInflater;
	public RelativeLayout backview;
	public LinearLayout content, infomation_layout, sex_layout;
	public TextView spacing_one, spacing_two, spacing_three, title, business_title, lable_title, creattime_title, nickname, id, business, lable, creattime, sex, distance;
	public ImageView head, tdcode;
	public Button button_one, button_two, button_three;
	public RelativeLayout rightContainer;
	public BusinessCard businessCard;

	public Status status = Status.SELF;

	public FileHandlers fileHandlers = FileHandlers.getInstance();
	public DisplayImageOptions options;

	public enum Status {
		SELF, FRIEND, TEMPFRIEND, JOINEDGROUP, NOTJOINGROUP, SQUARE
	}

	public BusinessCardView(BusinessCardActivity activity) {
		thisActivity = activity;
		thisView = this;
	}

	public TextView rightTopButton;

	public void initView() {
		mInflater = thisActivity.getLayoutInflater();
		thisActivity.setContentView(R.layout.activity_businesscard);

		DisplayMetrics displayMetrics = new DisplayMetrics();
		thisActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		backview = (RelativeLayout) thisActivity.findViewById(R.id.backView);
		title = (TextView) thisActivity.findViewById(R.id.backTitleView);
		rightContainer = (RelativeLayout) thisActivity.findViewById(R.id.rightContainer);
		content = (LinearLayout) thisActivity.findViewById(R.id.content);
		infomation_layout = (LinearLayout) thisActivity.findViewById(R.id.infomation_layout);
		sex_layout = (LinearLayout) thisActivity.findViewById(R.id.sex_layout);
		spacing_one = (TextView) thisActivity.findViewById(R.id.spacing_one);
		spacing_two = (TextView) thisActivity.findViewById(R.id.spacing_two);
		spacing_three = (TextView) thisActivity.findViewById(R.id.spacing_three);
		business_title = (TextView) thisActivity.findViewById(R.id.business_title);
		lable_title = (TextView) thisActivity.findViewById(R.id.lable_title);
		creattime_title = (TextView) thisActivity.findViewById(R.id.creattime_title);
		id = (TextView) thisActivity.findViewById(R.id.id);
		nickname = (TextView) thisActivity.findViewById(R.id.nickname);
		business = (TextView) thisActivity.findViewById(R.id.business);
		lable = (TextView) thisActivity.findViewById(R.id.lable);
		creattime = (TextView) thisActivity.findViewById(R.id.creattime);
		sex = (TextView) thisActivity.findViewById(R.id.sex);
		head = (ImageView) thisActivity.findViewById(R.id.head);
		distance = (TextView) thisActivity.findViewById(R.id.distance);

		tdcode = (ImageView) thisActivity.findViewById(R.id.tdcode);
		button_one = (Button) thisActivity.findViewById(R.id.button_one);
		button_two = (Button) thisActivity.findViewById(R.id.button_two);
		button_three = (Button) thisActivity.findViewById(R.id.button_three);
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).displayer(new RoundedBitmapDisplayer(45)).build();

		rightTopButton = new TextView(thisActivity);
		int dp_5 = (int) (5 * displayMetrics.density);
		rightTopButton.setGravity(Gravity.CENTER);
		rightTopButton.setTextColor(Color.WHITE);
		rightTopButton.setPadding(dp_5 * 2, dp_5, dp_5 * 2, dp_5);
		rightTopButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		rightTopButton.setText("修改资料");
		rightTopButton.setBackgroundResource(R.drawable.textview_bg);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(0, dp_5, (int) 0, dp_5);
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		this.rightContainer.addView(rightTopButton, layoutParams);
	}

	String GROUPCARDTYPE = "groupcard";
	String USERCARDTYPE = "usercard";
	LBSHandlers lbsHandlers = LBSHandlers.getInstance();

	public void fillData() {
		businessCard = new BusinessCard();
		if (status.equals(Status.SELF)) {
			rightTopButton.setText("修改资料");
			User user = thisController.data.userInformation.currentUser;
			businessCard.id = user.id;
			businessCard.icon = user.head;
			businessCard.sex = user.sex;
			businessCard.distance = "0";
			businessCard.nickname = user.nickName;
			businessCard.mainBusiness = user.mainBusiness;
			businessCard.lable = "暂无标签";
			businessCard.creattime = "2014年 9月 1日";
			businessCard.button_one = "修改我的名片";
			businessCard.button_two = "";
			businessCard.button_three = "";
			sex_layout.setVisibility(View.VISIBLE);
			button_two.setVisibility(View.GONE);
			button_three.setVisibility(View.GONE);
			tdcode.setImageBitmap(MCImageUtils.createQEcodeImage(USERCARDTYPE, user.phone));
		} else if (status.equals(Status.FRIEND)) {
			rightTopButton.setText("发起聊天");
			Friend friend = thisController.data.relationship.friendsMap.get(thisController.key);
			businessCard.id = friend.id;
			businessCard.icon = friend.head;
			String nickName = "";
			if (friend.alias.equals("")) {
				nickName = friend.nickName;
			} else {
				nickName = friend.alias + "(" + friend.nickName + ")";
			}
			User user = thisController.data.userInformation.currentUser;
			businessCard.distance = lbsHandlers.pointDistance(user.longitude, user.latitude, friend.longitude, friend.latitude);
			businessCard.nickname = nickName;
			businessCard.mainBusiness = friend.mainBusiness;
			businessCard.lable = "暂无标签";
			businessCard.creattime = "2014年 9月 1日";
			businessCard.button_one = "发起聊天";
			businessCard.button_two = "修改备注";
			businessCard.button_three = "解除好友关系";
			tdcode.setImageBitmap(MCImageUtils.createQEcodeImage(USERCARDTYPE, friend.phone));
		} else if (status.equals(Status.JOINEDGROUP)) {
			rightTopButton.setText("发起群聊");
			Group group = thisController.data.relationship.groupsMap.get(thisController.key);
			businessCard.id = group.gid;
			businessCard.icon = group.icon;
			businessCard.nickname = group.name;
			String description = "";
			if (group.description == null || group.description.equals("") || group.description.equals("请输入群组描述信息")) {
				description = "此群组暂无业务";
			} else {
				description = group.description;
			}
			User user = thisController.data.userInformation.currentUser;
			businessCard.distance = lbsHandlers.pointDistance(user.longitude, user.latitude, group.longitude, group.latitude);
			businessCard.mainBusiness = description;
			businessCard.lable = "暂无标签";
			businessCard.creattime = "2014年 9月 1日";
			businessCard.button_one = "发起聊天";
			businessCard.button_two = "修改群名片";
			businessCard.button_three = "";
			button_three.setVisibility(View.GONE);
			tdcode.setImageBitmap(MCImageUtils.createQEcodeImage(GROUPCARDTYPE, group.gid + ""));
		} else if (status.equals(Status.TEMPFRIEND)) {
			rightTopButton.setText("加为好友");
			Friend friend = data.tempData.tempFriend;
			businessCard.id = friend.id;
			businessCard.icon = friend.head;
			User user = thisController.data.userInformation.currentUser;
			businessCard.distance = lbsHandlers.pointDistance(user.longitude, user.latitude, friend.longitude, friend.latitude);
			businessCard.nickname = friend.nickName;
			businessCard.mainBusiness = friend.mainBusiness;
			businessCard.lable = "暂无标签";
			businessCard.creattime = "2014年 9月 1日";
			businessCard.button_one = "加为好友";
			businessCard.button_two = "";
			businessCard.button_three = "";
			button_two.setVisibility(View.GONE);
			button_three.setVisibility(View.GONE);
			tdcode.setImageBitmap(MCImageUtils.createQEcodeImage(USERCARDTYPE, data.tempData.tempFriend.phone));
		} else if (status.equals(Status.NOTJOINGROUP)) {
			rightTopButton.setText("加入群组");
			businessCard.id = data.tempData.tempGroup.gid;
			businessCard.icon = data.tempData.tempGroup.icon;
			businessCard.nickname = data.tempData.tempGroup.name;
			businessCard.mainBusiness = data.tempData.tempGroup.description;
			User user = thisController.data.userInformation.currentUser;
			businessCard.distance = lbsHandlers.pointDistance(user.longitude, user.latitude, data.tempData.tempGroup.longitude, data.tempData.tempGroup.latitude);
			businessCard.lable = "暂无标签";
			businessCard.creattime = "2014年 9月 1日";
			businessCard.button_one = "加入群组";
			businessCard.button_two = "";
			businessCard.button_three = "";
			button_two.setVisibility(View.GONE);
			button_three.setVisibility(View.GONE);
			tdcode.setImageBitmap(MCImageUtils.createQEcodeImage(USERCARDTYPE, data.tempData.tempGroup.gid + ""));
		} else if (status.equals(Status.SQUARE)) {
			// rightTopButton.setText("修改资料");
			businessCard.id = Integer.valueOf(thisController.key);
			businessCard.icon = "";
			businessCard.distance = "0";
			businessCard.nickname = "";
			businessCard.mainBusiness = "暂无描述";
			businessCard.lable = "暂无标签";
			businessCard.creattime = "2014年 9月 1日";
			businessCard.button_one = "";
			businessCard.button_two = "";
			businessCard.button_three = "";
			button_one.setVisibility(View.GONE);
			button_two.setVisibility(View.GONE);
			button_three.setVisibility(View.GONE);

		}
		if (businessCard.icon.equals("Head") || "".equals(businessCard.icon)) {
			Bitmap bitmap = MCImageUtils.getCircleBitmap(BitmapFactory.decodeResource(thisActivity.getResources(), R.drawable.face_man), true, 5, Color.WHITE);
			thisView.head.setImageBitmap(bitmap);
		} else {
			fileHandlers.getHeadImage(businessCard.icon, this.head, options);
			// thisController.setHeadImage(businessCard.icon, thisView.head);
		}
		tdcode.setScaleType(ScaleType.FIT_CENTER);
		setData(businessCard);
	}

	public void setData(BusinessCard businessCard) {
		if (status.equals(Status.SELF)) {
			title.setText("我的详情");
			business_title.setText("个人宣言：");
			lable_title.setText("爱好：");
			creattime_title.setText("注册时间：");
		} else if (status.equals(Status.FRIEND) || status.equals(Status.TEMPFRIEND)) {
			title.setText("个人详情");
			business_title.setText("个人宣言：");
			lable_title.setText("爱好：");
			creattime_title.setText("注册时间：");
		} else if (status.equals(Status.JOINEDGROUP) || status.equals(Status.NOTJOINGROUP)) {
			title.setText("群组详情");
			business_title.setText("主要业务：");
			lable_title.setText("标签：");
			creattime_title.setText("创建时间：");
		} else if (status.equals(Status.SQUARE)) {
			title.setText("广场详情");
			business_title.setText("主要业务：");
			lable_title.setText("标签：");
			creattime_title.setText("创建时间：");
		}
		if (!"".equals(businessCard.sex) && ("male".equals(businessCard.sex) || "男".equals(businessCard.sex))) {
			sex.setText("男");
		} else {
			sex.setText("女");
		}
		distance.setText(businessCard.distance + "km");
		nickname.setText(businessCard.nickname);
		id.setText(String.valueOf(businessCard.id));
		business.setText(businessCard.mainBusiness);
		lable.setText(businessCard.lable);
		creattime.setText(businessCard.creattime);
		button_one.setText(businessCard.button_one);
		button_two.setText(businessCard.button_two);
		button_three.setText(businessCard.button_three);
	}

	public class BusinessCard {
		public int id = 0;
		public String icon = "";
		public String nickname = "";
		public String mainBusiness = "";
		public String sex = "";
		public String distance;
		public String lable = "";
		public String creattime = "";
		public String button_one = "";
		public String button_two = "";
		public String button_three = "";
	}
}
