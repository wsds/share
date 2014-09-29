package com.open.welinks.view;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.open.lib.MyLog;
import com.open.lib.TouchImageView;
import com.open.lib.TouchView;
import com.open.lib.viewbody.ListBody1;
import com.open.lib.viewbody.ListBody1.MyListItemBody;
import com.open.welinks.R;
import com.open.welinks.controller.DownloadFile;
import com.open.welinks.controller.DownloadFileList;
import com.open.welinks.controller.SquareSubController;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Data.ShareContent;
import com.open.welinks.model.Data.ShareContent.ShareContentItem;
import com.open.welinks.model.Data.Shares.Share;
import com.open.welinks.model.Data.Shares.Share.Comment;
import com.open.welinks.model.Data.Shares.Share.ShareMessage;
import com.open.welinks.model.FileHandlers;
import com.open.welinks.model.Parser;
import com.open.welinks.utils.DateUtil;

public class SquareSubView {

	public Data data = Data.getInstance();

	public String tag = "SquareSubView";

	public SquareSubController thisController;

	public MyLog log = new MyLog(tag, false);

	public FileHandlers fileHandlers = FileHandlers.getInstance();

	public DisplayMetrics displayMetrics;

	public MainView mainView;

	// share
	public RelativeLayout squareView;

	public ViewGroup squareMessageView;
	public ListBody1 squareMessageListBody;

	public ImageView leftImageButton;
	public RelativeLayout squareTopMenuGroupNameParent;
	public TextView squareTopMenuSquareName;

	// group
	// public PopupWindow groupPopWindow;
	// pop layout
	public TouchView squareDialogView;

	public ViewGroup groupsDialogContent;

	public ListBody1 squaresListBody;

	// share top Bar child view
	// public View groupMembersView;
	// public RelativeLayout groupMembersListContentView;
	// public ImageView releaseShareView;

	public View groupManageView;
	public View groupsManageButtons;
	public View groupListButtonView;
	public View createGroupButtonView;
	public View findMoreGroupButtonView;

	public int shareImageHeight;

	public float imageHeightScale = 0.5686505598114319f;

	public float panelScale = 1.010845986984816f;

	public int panelHeight;
	public int panelWidth;

	public ImageLoader imageLoader = ImageLoader.getInstance();
	public DisplayImageOptions options;
	public DownloadFileList downloadFileList = DownloadFileList.getInstance();
	public Gson gson = new Gson();
	public Parser parser = Parser.getInstance();

	public ViewManage viewManage = ViewManage.getInstance();

	public boolean isShowFirstMessageAnimation = false;

	public LayoutInflater mInflater;

	public int showImageWidth;
	public int showImageHeight;

	public SquareSubView(MainView mainView) {
		this.mainView = mainView;
		viewManage.squareSubView = this;
	}

	public void initViews() {

		this.squareView = mainView.squareView;
		this.displayMetrics = mainView.displayMetrics;

		this.showImageWidth = displayMetrics.widthPixels;
		this.showImageHeight = (int) (115 * displayMetrics.density + 0.5f);

		this.mInflater = mainView.mInflater;

		shareImageHeight = (int) (this.displayMetrics.widthPixels * imageHeightScale);
		panelHeight = (int) (this.displayMetrics.widthPixels * panelScale);

		squareMessageView = (ViewGroup) squareView.findViewById(R.id.squareContainer);

		squareMessageListBody = new ListBody1();
		squareMessageListBody.initialize(displayMetrics, squareMessageView);

		leftImageButton = (ImageView) squareView.findViewById(R.id.leftImageButton);
		squareTopMenuGroupNameParent = (RelativeLayout) squareView.findViewById(R.id.shareTopMenuGroupNameParent);
		squareTopMenuSquareName = (TextView) squareView.findViewById(R.id.shareTopMenuSquareName);

		options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
		headOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).displayer(new RoundedBitmapDisplayer(40)).build();

		mImageFile = fileHandlers.sdcardHeadImageFolder;

		data = parser.check();

		try {
			Group group0 = data.relationship.groupsMap.get(data.localStatus.localData.currentSelectedSquare);
			if (group0 != null) {
				this.squareTopMenuSquareName.setText(group0.name);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		showSquareMessages();
		initReleaseShareDialogView();
		initializationSquaresDialog();
	}

	public void showSquareMessages() {

		this.squareMessageListBody.listItemsSequence.clear();
		this.squareMessageListBody.containerView.removeAllViews();
		this.squareMessageListBody.height = 10 * displayMetrics.density;

		data = parser.check();
		if (data.shares.shareMap == null || data.localStatus.localData == null) {
			return;
		}
		Share share = data.shares.shareMap.get(data.localStatus.localData.currentSelectedSquare);
		if (share == null)
			return;
		List<String> sharesOrder = share.shareMessagesOrder;
		Map<String, ShareMessage> sharesMap = share.shareMessagesMap;
		for (int i = 0; i < sharesOrder.size(); i++) {
			String key = sharesOrder.get(i);
			ShareMessage shareMessage = null;
			shareMessage = sharesMap.get(key);
			if (!shareMessage.type.equals("imagetext")) {
				continue;
			}
			SharesMessageBody sharesMessageBody = null;

			String keyName = "message#" + shareMessage.gsid;
			if (this.squareMessageListBody.listItemBodiesMap.get(keyName) != null) {
				sharesMessageBody = (SharesMessageBody) this.squareMessageListBody.listItemBodiesMap.get(keyName);
			} else {
				sharesMessageBody = new SharesMessageBody(this.squareMessageListBody);
				sharesMessageBody.initialize(i);
				this.squareMessageListBody.listItemBodiesMap.put(keyName, sharesMessageBody);
			}
			// Friend friend =
			// data.relationship.friendsMap.get(shareMessage.phone);
			this.squareMessageListBody.listItemsSequence.add(keyName);

			Friend friend = data.relationship.friendsMap.get(shareMessage.phone);
			String fileName = "";
			if (friend != null) {
				fileName = friend.head;
			}
			sharesMessageBody.setContent(shareMessage, fileName);

			TouchView.LayoutParams layoutParams = new TouchView.LayoutParams((int) (displayMetrics.widthPixels - displayMetrics.density * 20), (int) (210 * displayMetrics.density));
			sharesMessageBody.y = this.squareMessageListBody.height;
			sharesMessageBody.cardView.setY(sharesMessageBody.y);
			sharesMessageBody.cardView.setX(displayMetrics.density * 10);
			sharesMessageBody.itemHeight = 210 * displayMetrics.density;
			this.squareMessageListBody.height = this.squareMessageListBody.height + 210 * displayMetrics.density;
			this.squareMessageListBody.containerView.addView(sharesMessageBody.cardView, layoutParams);

			sharesMessageBody.cardView.setTag(R.id.tag_class, "share_view");
			sharesMessageBody.cardView.setTag("ShareMessageDetail#" + shareMessage.gsid);
			sharesMessageBody.cardView.setOnClickListener(thisController.mOnClickListener);
			sharesMessageBody.cardView.setOnTouchListener(thisController.mOnTouchListener);
		}

		this.squareMessageListBody.containerHeight = (int) (this.displayMetrics.heightPixels - 38 - displayMetrics.density * 48);
		squareMessageListBody.setChildrenPosition();
	}

	public class SharesMessageBody extends MyListItemBody {

		SharesMessageBody(ListBody1 listBody) {
			listBody.super();
		}

		public View cardView;

		public ImageView headView;
		public TextView nickNameView;
		public TextView releaseTimeView;
		public TextView shareTextContentView;
		public ImageView shareImageContentView;
		public TextView sharePraiseNumberView;
		public ImageView sharePraiseIconView;
		public TextView shareCommentNumberView;
		public ImageView shareCommentIconView;

		public DownloadFile downloadFile = null;

		public ShareMessage message;

		public String fileName;

		public int i;
		public String option;

		public View initialize(int i) {
			this.cardView = mInflater.inflate(R.layout.square_message_item_left, null);

			this.headView = (ImageView) this.cardView.findViewById(R.id.share_head);
			this.nickNameView = (TextView) this.cardView.findViewById(R.id.share_nickName);
			this.releaseTimeView = (TextView) this.cardView.findViewById(R.id.share_releaseTime);
			this.shareTextContentView = (TextView) this.cardView.findViewById(R.id.share_textContent);
			this.shareImageContentView = (ImageView) this.cardView.findViewById(R.id.share_imageContent);
			this.sharePraiseNumberView = (TextView) this.cardView.findViewById(R.id.share_praise);
			this.sharePraiseIconView = (ImageView) this.cardView.findViewById(R.id.share_praise_icon);
			this.shareCommentNumberView = (TextView) this.cardView.findViewById(R.id.share_comment);
			this.shareCommentIconView = (ImageView) this.cardView.findViewById(R.id.share_comment_icon);

			this.itemHeight = 350 * displayMetrics.density;
			super.initialize(cardView);
			return cardView;
		}

		public void setContent(ShareMessage shareMessage, String fileName) {
			data = parser.check();

			this.message = shareMessage;
			this.fileName = fileName;
			fileHandlers.getHeadImage(fileName, this.headView, headOptions);
			if (data.relationship.friendsMap.get(shareMessage.phone) == null) {
				this.nickNameView.setText(shareMessage.phone);
			} else {
				this.nickNameView.setText(data.relationship.friendsMap.get(shareMessage.phone).nickName);
			}
			this.releaseTimeView.setText(DateUtil.formatHourMinute(shareMessage.time));
			ShareContent shareContent = gson.fromJson("{shareContentItems:" + shareMessage.content + "}", ShareContent.class);
			String textContent = "";
			String imageContent = "";
			List<ShareContentItem> shareContentItems = shareContent.shareContentItems;
			for (int i = 0; i < shareContentItems.size(); i++) {
				ShareContentItem shareContentItem = shareContentItems.get(i);
				if (shareContentItem.type.equals("image")) {
					imageContent = shareContentItem.detail;
					if (!"".equals(textContent))
						break;
				} else if (shareContentItem.type.equals("text")) {
					textContent = shareContentItem.detail;
					if (!"".equals(imageContent))
						break;
				}
			}

			this.shareTextContentView.setText(textContent);
			File file = new File(fileHandlers.sdcardThumbnailFolder, imageContent);
			final int showImageWidth = (int) (displayMetrics.widthPixels - 20 * displayMetrics.density + 120);
			final int showImageHeight = (int) (displayMetrics.density * 136);

			FrameLayout.LayoutParams shareImageParams = new FrameLayout.LayoutParams(showImageWidth, showImageHeight);
			// int margin = (int) ((int) displayMetrics.density * 1 + 0.5f);
			shareImageContentView.setLayoutParams(shareImageParams);
			if (!imageContent.equals("")) {
				final String url = API.DOMAIN_OSS_THUMBNAIL + "images/" + imageContent + "@" + showImageWidth / 2 + "w_" + showImageHeight / 2 + "h_1c_1e_100q";
				final String path = file.getAbsolutePath();
				if (file.exists()) {
					imageLoader.displayImage("file://" + path, shareImageContentView, options, new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
						}

						@Override
						public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
							downloadFile = new DownloadFile(url, path);
							downloadFile.view = shareImageContentView;
							downloadFile.view.setTag("image");
							downloadFile.setDownloadFileListener(thisController.downloadListener);
							downloadFileList.addDownloadFile(downloadFile);
						}

						@Override
						public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
							int height = showImageHeight;
							FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(showImageWidth, height);
							shareImageContentView.setLayoutParams(params);
						}
					});
				} else {
					File file2 = new File(fileHandlers.sdcardImageFolder, imageContent);
					final String path2 = file2.getAbsolutePath();
					if (file2.exists()) {
						imageLoader.displayImage("file://" + path2, shareImageContentView, options);
					}
					downloadFile = new DownloadFile(url, path);
					downloadFile.view = shareImageContentView;
					downloadFile.view.setTag("image");
					downloadFile.setDownloadFileListener(thisController.downloadListener);
					downloadFileList.addDownloadFile(downloadFile);
				}
			}

			this.sharePraiseNumberView.setText(shareMessage.praiseusers.size() + "");
			this.shareCommentNumberView.setText(shareMessage.comments.size() + "");
			String userPhone = data.userInformation.currentUser.phone;
			if (shareMessage.praiseusers.contains(userPhone)) {
				this.sharePraiseIconView.setImageResource(R.drawable.praised_icon);
			} else {
				this.sharePraiseIconView.setImageResource(R.drawable.praise_icon);
			}
			List<Comment> comments = shareMessage.comments;
			this.shareCommentIconView.setImageResource(R.drawable.comment_icon);
			for (int i = 0; i < comments.size(); i++) {
				Comment comment = comments.get(i);
				if (comment.phone.equals(userPhone)) {
					this.shareCommentIconView.setImageResource(R.drawable.commented_icon);
					break;
				}
			}
		}
	}

	public ViewGroup pop_out_background1;
	public ViewGroup pop_out_background2;

	public void initializationSquaresDialog() {
		squareDialogView = (TouchView) mainView.mInflater.inflate(R.layout.share_group_select_dialog, null, false);

		squareDialogView.setTag(R.id.tag_class, "group_view");

		pop_out_background1 = (ViewGroup) squareDialogView.findViewById(R.id.pop_out_background1);
		pop_out_background2 = (ViewGroup) squareDialogView.findViewById(R.id.pop_out_background2);

		groupManageView = squareDialogView.findViewById(R.id.groups_manage);
		groupManageView.setTag(R.id.tag_class, "group_setting");
		groupListButtonView = squareDialogView.findViewById(R.id.groupListButton);
		groupListButtonView.setVisibility(View.GONE);
		createGroupButtonView = squareDialogView.findViewById(R.id.createGroupButton);
		createGroupButtonView.setVisibility(View.GONE);
		findMoreGroupButtonView = squareDialogView.findViewById(R.id.findMoreButton);
		groupsManageButtons = squareDialogView.findViewById(R.id.groups_manage_buttons);

		TouchView mainContentView = (TouchView) squareDialogView;
		groupsDialogContent = (ViewGroup) squareDialogView.findViewById(R.id.groupsContent);

		panelWidth = (int) (displayMetrics.widthPixels * 0.7578125f);
		panelHeight = (int) (displayMetrics.heightPixels * 0.7578125f);

		TouchView.LayoutParams mainContentParams = new TouchView.LayoutParams(panelWidth, panelHeight);

		mainContentView.setLayoutParams(mainContentParams);
		squaresListBody = new ListBody1();
		squaresListBody.initialize(displayMetrics, groupsDialogContent);
		setSquaresDialogContent();
	}

	public boolean isShowSquareDialog = false;

	public void showSquaresDialog() {
		if (!isShowSquareDialog) {
			if (data.relationship.squares.size() == 0) {
				if (groupsManageButtons.getVisibility() == View.GONE) {
					groupsManageButtons.setVisibility(View.VISIBLE);
				}
			} else {
				if (groupsManageButtons.getVisibility() == View.VISIBLE) {
					groupsManageButtons.setVisibility(View.GONE);
				}
			}
			squaresListBody.active();
			squareMessageListBody.inActive();
			mainView.mainPagerBody.inActive();
			// groupPopWindow.showAtLocation(mainView.main_container,
			// Gravity.CENTER, 0, 0);
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
			mainView.main_container.addView(this.squareDialogView, layoutParams);
			isShowSquareDialog = true;
		}
	}

	public void dismissSquareDialog() {
		if (isShowSquareDialog) {
			squaresListBody.inActive();
			squareMessageListBody.active();
			mainView.mainPagerBody.active();
			// groupPopWindow.dismiss();
			mainView.main_container.removeView(this.squareDialogView);
			isShowSquareDialog = false;
		}
	}

	public void setSquaresDialogContent() {
		data = parser.check();

		Group group0 = data.relationship.groupsMap.get(data.localStatus.localData.currentSelectedSquare);
		if (group0 != null) {
			this.squareTopMenuSquareName.setText(group0.name);
		}

		List<String> squares = data.relationship.squares;
		Map<String, Group> groupsMap = data.relationship.groupsMap;
		this.squaresListBody.containerView.removeAllViews();
		groupsDialogContent.removeAllViews();
		this.squaresListBody.height = 0;
		squaresListBody.listItemsSequence.clear();
		for (int i = 0; i < squares.size(); i++) {
			// boolean a = groups.get(i) == "1765";
			// log.e(a + "--------" + groups.get(i) + "---" +
			// groupsMap.get("1765"));
			Group group = groupsMap.get(squares.get(i));
			String key = "group#" + group.gid + "_" + group.name;
			GroupDialogItem groupDialogItem;
			View view = null;
			if (squaresListBody.listItemBodiesMap.get(key) != null) {
				groupDialogItem = (GroupDialogItem) squaresListBody.listItemBodiesMap.get(key);
				view = groupDialogItem.cardView;
			} else {
				groupDialogItem = new GroupDialogItem(this.squaresListBody);
				view = groupDialogItem.initialize();
				squaresListBody.listItemBodiesMap.put(key, groupDialogItem);
			}
			squaresListBody.listItemsSequence.add(key);
			groupDialogItem.setContent(group);
			// groupDialogItem.setViewLayout();

			TouchView.LayoutParams layoutParams = new TouchView.LayoutParams((int) (displayMetrics.widthPixels - displayMetrics.density * 60), (int) (60 * displayMetrics.density));
			groupDialogItem.y = this.squaresListBody.height;
			groupDialogItem.cardView.setY(groupDialogItem.y);
			groupDialogItem.cardView.setX(0);
			this.squaresListBody.height = this.squaresListBody.height + 60 * displayMetrics.density;
			this.squaresListBody.containerView.addView(view, layoutParams);

			// onclick
			view.setTag("GroupDialogContentItem#" + group.gid);
			view.setTag(R.id.shareTopMenuGroupName, squareTopMenuSquareName);
			// listener
			view.setTag(R.id.tag_class, "group_view");
			view.setTag(R.id.tag_first, group);
			view.setOnClickListener(thisController.mOnClickListener);
			view.setOnTouchListener(thisController.mOnTouchListener);

			Log.v(tag, "this.friendListBody.height: " + this.squaresListBody.height + "    circleBody.y:  " + groupDialogItem.y);
		}
		this.squaresListBody.containerHeight = (int) (displayMetrics.heightPixels * 0.6578125f);

	}

	public void modifyCurrentShowGroup() {
		List<String> listItemsSequence = squaresListBody.listItemsSequence;
		Map<String, MyListItemBody> listItemsSequenceMap = squaresListBody.listItemBodiesMap;
		for (int i = 0; i < listItemsSequence.size(); i++) {
			String key = listItemsSequence.get(i);
			GroupDialogItem body = (GroupDialogItem) listItemsSequenceMap.get(key);
			body.setViewLayout();
		}
	}

	public class GroupDialogItem extends MyListItemBody {
		GroupDialogItem(ListBody1 listBody) {
			listBody.super();
		}

		public View cardView;

		public ImageView groupIconView;
		public TextView groupNameView;
		public ImageView groupSelectedStatusView;

		public ImageView gripCardBackground;

		public Group group;

		public View initialize() {
			this.cardView = mainView.mInflater.inflate(R.layout.share_group_select_dialog_item, null);
			this.groupIconView = (ImageView) this.cardView.findViewById(R.id.groupIcon);
			this.groupNameView = (TextView) this.cardView.findViewById(R.id.groupName);
			this.groupSelectedStatusView = (ImageView) this.cardView.findViewById(R.id.groupSelectedStatus);

			this.gripCardBackground = (ImageView) this.cardView.findViewById(R.id.grip_card_background);

			super.initialize(cardView);
			return cardView;
		}

		public void setContent(Group group) {
			data = parser.check();
			this.group = group;
			fileHandlers.getHeadImage(group.icon, this.groupIconView, headOptions);
			this.groupNameView.setText(group.name);
			if (data.localStatus.localData.currentSelectedGroup.equals(group.gid + "")) {
				this.groupSelectedStatusView.setVisibility(View.VISIBLE);
			} else {
				this.groupSelectedStatusView.setVisibility(View.GONE);
			}
			this.itemHeight = 60 * displayMetrics.density;
		}

		public void setViewLayout() {
			data = parser.check();
			if (data.localStatus.localData.currentSelectedGroup.equals(group.gid + "")) {
				this.groupSelectedStatusView.setVisibility(View.VISIBLE);
				this.groupNameView.setText(group.name);
			} else {
				this.groupSelectedStatusView.setVisibility(View.GONE);
			}
		}
	}

	public int width;
	public File mImageFile;
	public DisplayImageOptions headOptions;

	public class MyScrollImageBody {
		public ArrayList<String> selectedImagesSequence = new ArrayList<String>();
		public HashMap<String, ImageBody> selectedImagesSequenceMap = new HashMap<String, ImageBody>();

		public RelativeLayout contentView;

		public RelativeLayout initialize(RelativeLayout view) {
			this.contentView = view;
			return view;
		}

		public void recordChildrenPosition() {
			for (int i = 0; i < selectedImagesSequence.size(); i++) {
				String key = selectedImagesSequence.get(i);
				ImageBody imageBody = selectedImagesSequenceMap.get(key);
				imageBody.x = imageBody.imageView.getX();
				imageBody.y = imageBody.imageView.getY();
			}
		}

		public void setChildrenPosition(float deltaX, float deltaY) {
			float screenWidth = displayMetrics.widthPixels;
			float totalLength = selectedImagesSequence.size() * (width + 2 * displayMetrics.density) + 2 * displayMetrics.density;
			if (totalLength < screenWidth) {
				return;
			}
			for (int i = 0; i < selectedImagesSequence.size(); i++) {
				String key = selectedImagesSequence.get(i);
				ImageBody imageBody = selectedImagesSequenceMap.get(key);
				if ((imageBody.x + deltaX) < (screenWidth - totalLength))
					break;
				if (i == 0 && (imageBody.x + deltaX) > (5 * displayMetrics.density))
					break;
				imageBody.imageView.setX(imageBody.x + deltaX);
				imageBody.imageView.setY(imageBody.y + deltaY);
			}
		}
	}

	public class ImageBody {
		public int i;

		public float x;
		public float y;
		public TouchImageView imageView;

		public TouchImageView initialize() {
			this.imageView = new TouchImageView(mainView.context);
			return this.imageView;
		}
	}

	public View shareMessageRootView;

	public PopupWindow releaseSharePopWindow;

	public View releaseShareDialogView;
	public HorizontalScrollView dialogMainContentView;

	public TouchView releaseTextButton;
	public TouchView releaseAlbumButton;
	public TouchView releaseImageViewButton;

	@SuppressWarnings("deprecation")
	public void initReleaseShareDialogView() {
		releaseShareDialogView = mainView.mInflater.inflate(R.layout.share_release_type_dialog, null);
		dialogMainContentView = (HorizontalScrollView) releaseShareDialogView.findViewById(R.id.dialogMainContent);
		releaseTextButton = (TouchView) releaseShareDialogView.findViewById(R.id.releaseTextShareButton);
		releaseTextButton.isIntercept = true;
		releaseAlbumButton = (TouchView) releaseShareDialogView.findViewById(R.id.releaseAlbumShareButton);
		releaseAlbumButton.isIntercept = true;
		releaseImageViewButton = (TouchView) releaseShareDialogView.findViewById(R.id.releaseImageTextShareButton);
		releaseImageViewButton.isIntercept = true;

		releaseTextButton.setOnClickListener(thisController.mOnClickListener);
		releaseAlbumButton.setOnClickListener(thisController.mOnClickListener);
		releaseImageViewButton.setOnClickListener(thisController.mOnClickListener);
		dialogMainContentView.setOnClickListener(thisController.mOnClickListener);
		releaseShareDialogView.setOnClickListener(thisController.mOnClickListener);
		// releaseVoiceTextButton.setOnClickListener(thisController.mOnClickListener);
		// releaseVoteButton.setOnClickListener(thisController.mOnClickListener);

		releaseSharePopWindow = new PopupWindow(releaseShareDialogView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		releaseSharePopWindow.setBackgroundDrawable(new BitmapDrawable());
	}

	public void showReleaseShareDialogView() {
		if (releaseSharePopWindow != null && !releaseSharePopWindow.isShowing()) {
			releaseSharePopWindow.showAtLocation(mainView.main_container, Gravity.CENTER, 0, 0);
		}
	}

	public void dismissReleaseShareDialogView() {
		if (releaseSharePopWindow != null && releaseSharePopWindow.isShowing()) {
			releaseSharePopWindow.dismiss();
		}
	}

}
