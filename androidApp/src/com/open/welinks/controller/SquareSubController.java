package com.open.welinks.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.open.lib.MyLog;
import com.open.lib.TouchView;
import com.open.lib.viewbody.BodyCallback;
import com.open.welinks.FindMoreActivity;
import com.open.welinks.R;
import com.open.welinks.ShareMessageDetailActivity;
import com.open.welinks.ShareReleaseImageTextActivity;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.FileHandlers;
import com.open.welinks.model.Parser;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.view.Alert;
import com.open.welinks.view.Alert.AlertInputDialog;
import com.open.welinks.view.Alert.AlertInputDialog.OnDialogClickListener;
import com.open.welinks.view.SquareSubView;
import com.open.welinks.view.SquareSubView.GroupDialogItem;
import com.open.welinks.view.SquareSubView.SharesMessageBody;

public class SquareSubController {

	public Data data = Data.getInstance();
	public String tag = "ShareSubController";
	public MyLog log = new MyLog(tag, true);
	public Parser parser = Parser.getInstance();

	public FileHandlers fileHandlers = FileHandlers.getInstance();

	public SquareSubView thisView;
	public Context context;
	public Activity thisActivity;

	public MainController mainController;

	public ResponseHandlers responseHandlers = ResponseHandlers.getInstance();

	public MyOnClickListener mOnClickListener;
	public OnTouchListener mOnTouchListener;
	public OnDownloadListener downloadListener;
	public BodyCallback bodyCallback;

	public View onTouchDownView;
	public View onLongPressView;;

	public boolean isTouchDown = false;

	public Group onTouchDownGroup;

	public int SCAN_MESSAGEDETAIL = 0x01;
	public String currentScanMessageKey;

	public int nowpage = 0;
	public int pagesize = 10;

	public Gson gson = new Gson();

	public BodyCallback shareBodyCallback;

	public SquareSubController(MainController mainController) {
		thisActivity = mainController.thisActivity;

		this.mainController = mainController;
	}

	public void initializeListeners() {
		shareBodyCallback = new BodyCallback() {
			@Override
			public void onRefresh(int direction) {
				super.onRefresh(direction);
				if (direction == 1) {
					nowpage = 0;
					getCurrentSquareShareMessages();
				} else if (direction == -1) {
					nowpage++;
					getCurrentSquareShareMessages();
				}
			}
		};

		downloadListener = new OnDownloadListener() {

			@Override
			public void loading(DownloadFile instance, int precent, int status) {
			}

			@Override
			public void onSuccess(final DownloadFile instance, int status) {
				DisplayImageOptions options = thisView.options;
				final ImageView imageView = (ImageView) instance.view;
				thisView.imageLoader.displayImage("file://" + instance.path, imageView, options, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
					}

					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
						Log.e(tag, "---------------failed");
						TouchView.LayoutParams params = new TouchView.LayoutParams(LayoutParams.MATCH_PARENT, 0);
						imageView.setImageBitmap(null);
						instance.view.setLayoutParams(params);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						int height = thisView.showImageHeight;
						TouchView.LayoutParams params = new TouchView.LayoutParams(thisView.showImageWidth, height);
						imageView.setLayoutParams(params);
					}
				});
			}

			@Override
			public void onFailure(DownloadFile instance, int status) {
				if (instance.view.getTag() != null) {
					if ("image".equals(instance.view.getTag().toString())) {
						String tag = instance.view.getTag().toString();
						Log.e(tag, "---------------failure:" + tag);
						ImageView imageView = ((ImageView) (instance.view));
						if (tag.equals("left")) {
							imageView.setImageResource(R.drawable.square_temp);
						} else {
							imageView.setImageResource(R.drawable.square_temp1);
						}
						// imageView.setImageResource(R.drawable.ic_error);
						// RelativeLayout.LayoutParams params = (LayoutParams)
						// imageView.getLayoutParams();
						// params.height = 10;
						// imageView.setLayoutParams(params);
						// imageView.setBackgroundColor(Color.RED);
					}
				}
			}
		};
		mOnTouchListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				// return onTouchEvent(event);
				int action = event.getAction();
				if (action == MotionEvent.ACTION_DOWN) {
					if (isTouchDown) {
						return false;
					}
					String view_class = (String) view.getTag(R.id.tag_class);
					if (view_class.equals("share_view")) {
						onTouchDownView = view;
						onLongPressView = view;
						isTouchDown = true;
					} else if (view_class.equals("group_view")) {
						// group dialog item onTouch
						onTouchDownView = view;
						// onLongPressView = view;
						isTouchDown = true;
						Object viewTag = view.getTag(R.id.tag_first);
						if (Group.class.isInstance(viewTag) == true) {
							Group group = (Group) viewTag;
							Log.d(tag, "onTouch: gid:" + group.gid + "name" + group.name);

							onTouchDownGroup = group;
						} else {
							Log.d(tag, "onTouch: " + (String) viewTag);
						}
					} else if (view_class.equals("group_setting")) {
						onTouchDownView = view;
						isTouchDown = true;
					}
					if (view.equals(thisView.squareDialogView)) {
						Log.i(tag, "ACTION_DOWN---groupDialogView");
						thisView.squareDialogView.isIntercept = true;
						isTouchDown = true;
					}

					Log.i(tag, "ACTION_DOWN---" + view_class);
					// thisView.mainView.main_container.playSoundEffect(SoundEffectConstants.CLICK);
				}
				return false;
			}
		};
		mOnClickListener = new MyOnClickListener() {

			public void onClickEffective(View view) {
				if (view.equals(thisView.leftImageButton)) {
					Vibrator vibrator = (Vibrator) thisActivity.getSystemService(Service.VIBRATOR_SERVICE);
					long[] pattern = { 30, 100, 30 };
					vibrator.vibrate(pattern, -1);
					thisView.showReleaseShareDialogView();
				} else if (view.equals(thisView.groupManageView)) {

					if (thisView.groupsManageButtons.getVisibility() == View.VISIBLE) {
						thisView.groupsManageButtons.setVisibility(View.GONE);
					} else {
						thisView.groupsManageButtons.setVisibility(View.VISIBLE);
					}
				} else if (view.equals(thisView.pop_out_background1) || view.equals(thisView.pop_out_background2)) {
					thisView.dismissSquareDialog();
				} else if (view.equals(thisView.findMoreGroupButtonView)) {
					Intent intent = new Intent(thisActivity, FindMoreActivity.class);
					intent.putExtra("type", 1);
					thisActivity.startActivity(intent);
					thisView.dismissSquareDialog();
				} else if (view.equals(thisView.squareTopMenuGroupNameParent)) {
					thisView.showSquaresDialog();
				} else if (view.equals(thisView.releaseShareDialogView)) {
					thisView.dismissReleaseShareDialogView();
				} else if (view.equals(thisView.releaseTextButton)) {
					Intent intent = new Intent(mainController.thisActivity, ShareReleaseImageTextActivity.class);
					intent.putExtra("gtype", "square");
					intent.putExtra("type", "text");
					intent.putExtra("gid", data.localStatus.localData.currentSelectedSquare);
					mainController.thisActivity.startActivity(intent);
					thisView.dismissReleaseShareDialogView();
				} else if (view.equals(thisView.releaseAlbumButton)) {
					Intent intent = new Intent(mainController.thisActivity, ShareReleaseImageTextActivity.class);
					intent.putExtra("gtype", "square");
					intent.putExtra("type", "album");
					intent.putExtra("gid", data.localStatus.localData.currentSelectedSquare);
					mainController.thisActivity.startActivity(intent);
					thisView.dismissReleaseShareDialogView();
				} else if (view.equals(thisView.releaseImageViewButton)) {
					Intent intent = new Intent(mainController.thisActivity, ShareReleaseImageTextActivity.class);
					intent.putExtra("gtype", "square");
					intent.putExtra("type", "imagetext");
					intent.putExtra("gid", data.localStatus.localData.currentSelectedSquare);
					mainController.thisActivity.startActivity(intent);
					thisView.dismissReleaseShareDialogView();
				} else if (view.equals(thisView.squareDialogView)) {
					thisView.squareDialogView.isIntercept = false;
					thisView.dismissSquareDialog();
				} else if (view.getTag() != null) {
					String tagContent = (String) view.getTag();
					int index = tagContent.lastIndexOf("#");
					String type = tagContent.substring(0, index);
					String content = tagContent.substring(index + 1);
					if ("GroupDialogContentItem".equals(type)) {
						parser.check();
						// modify data
						thisView.dismissSquareDialog();
						if (!data.localStatus.localData.currentSelectedSquare.equals(content)) {
							try {
								data.localStatus.localData.currentSelectedSquare = content;
								// modify UI
								Group group = data.relationship.groupsMap.get(content);
								TextView shareTopMenuGroupName = (TextView) view.getTag(R.id.shareTopMenuGroupName);
								data.localStatus.localData.currentSelectedSquare = group.gid + "";
								String name = group.name;
								if (name.length() > 8) {
									name = name.substring(0, 8);
								}
								shareTopMenuGroupName.setText(name);
								thisView.modifyCurrentShowGroup();
								// display local data
								nowpage = 0;
								thisView.showSquareMessages();
								getCurrentSquareShareMessages();
								thisView.squareMessageListBody.y = 0;
								thisView.squareMessageListBody.setChildrenPosition();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					} else if ("ShareMessageDetail".equals(type)) {
						Intent intent = new Intent(thisActivity, ShareMessageDetailActivity.class);
						intent.putExtra("gid", data.localStatus.localData.currentSelectedSquare);
						intent.putExtra("gsid", content);
						currentScanMessageKey = content;
						thisActivity.startActivityForResult(intent, SCAN_MESSAGEDETAIL);
						// thisActivity.overridePendingTransition(R.anim.zoomin,
						// R.anim.zoomout);
					}
				}
			}
		};
		bodyCallback = new BodyCallback() {
			@Override
			public void onStopOrdering(List<String> listItemsSequence) {
				super.onStopOrdering(listItemsSequence);
				log.e(tag, listItemsSequence.toString());
				List<String> groups = new ArrayList<String>();
				for (int i = 0; i < listItemsSequence.size(); i++) {
					String key = listItemsSequence.get(i);
					groups.add(key.substring(key.indexOf("#") + 1, key.indexOf("_")));
				}
				// modify local data
				String oldSequece = gson.toJson(data.relationship.squares);
				data.relationship.squares = groups;
				data.relationship.isModified = true;

				String sequenceListString = gson.toJson(data.relationship.squares);

				// modify server data
				if (!sequenceListString.equals(oldSequece)) {
					log.e("广场顺序发生改动");
				} else {
					log.e("广场顺序没有改动");
				}
			}
		};
	}

	public void bindEvent() {
		thisView.squareMessageListBody.bodyCallback = this.shareBodyCallback;
		thisView.squaresListBody.bodyCallback = this.bodyCallback;
		thisView.leftImageButton.setOnClickListener(mOnClickListener);
		thisView.squareTopMenuGroupNameParent.setOnClickListener(mOnClickListener);
		thisView.squareDialogView.setOnClickListener(mOnClickListener);
		thisView.squareDialogView.setOnTouchListener(mOnTouchListener);
		thisView.groupManageView.setOnClickListener(mOnClickListener);
		thisView.groupManageView.setOnTouchListener(mOnTouchListener);

		thisView.findMoreGroupButtonView.setOnClickListener(mOnClickListener);

		thisView.pop_out_background1.setOnClickListener(mOnClickListener);
		thisView.pop_out_background2.setOnClickListener(mOnClickListener);
	}

	public void getCurrentSquareShareMessages() {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		params.addBodyParameter("phone", data.userInformation.currentUser.phone);
		params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
		params.addBodyParameter("gid", data.localStatus.localData.currentSelectedSquare);
		params.addBodyParameter("nowpage", nowpage + "");
		params.addBodyParameter("pagesize", pagesize + "");

		httpUtils.send(HttpMethod.POST, API.SHARE_GETSHARES, params, responseHandlers.share_getSharesCallBack);
	}

	public void setCurrentSquare() {

		String gid = data.relationship.squares.get(0);
		List<String> squares = data.relationship.squares;
		Map<String, Group> groups = data.relationship.groupsMap;
		for (int i = 1; i < squares.size(); i++) {
			if ((groups.get(squares.get(i)).distance) < (groups.get(gid).distance)) {
				gid = squares.get(i);
			}
		}
		final String currentSid = gid;
		if (!data.localStatus.localData.currentSelectedSquare.equals(currentSid) && !data.localStatus.localData.currentSelectedSquare.equals("")) {
			Alert.createDialog(thisActivity).setTitle("您已进入" + groups.get(currentSid).description + "广场，是否切换？").setOnConfirmClickListener(new OnDialogClickListener() {

				@Override
				public void onClick(AlertInputDialog dialog) {
					data.localStatus.localData.currentSelectedSquare = currentSid;
					getCurrentSquareShareMessages();
					thisView.setSquaresDialogContent();
				}
			}).setOnCancelClickListener(new OnDialogClickListener() {

				@Override
				public void onClick(AlertInputDialog dialog) {
					getCurrentSquareShareMessages();
					thisView.setSquaresDialogContent();
				}
			}).show();
		} else {
			data.localStatus.localData.currentSelectedSquare = currentSid;
			getCurrentSquareShareMessages();
			thisView.setSquaresDialogContent();
		}
	}

	public void modifyGroupSequence(String sequenceListString) {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		params.addBodyParameter("phone", data.userInformation.currentUser.phone);
		params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
		params.addBodyParameter("sequence", sequenceListString);

		httpUtils.send(HttpMethod.POST, API.GROUP_MODIFYGROUPSEQUENCE, params, responseHandlers.modifyGroupSequenceCallBack);
	}

	public void onScroll() {
		onTouchDownView = null;
	}

	public void onLongPress(MotionEvent event) {
		if (onTouchDownView != null && onTouchDownGroup != null) {
			String view_class = (String) onTouchDownView.getTag(R.id.tag_class);
			if (view_class.equals("group_view")) {

				Group group = data.relationship.groupsMap.get("" + onTouchDownGroup.gid);
				GroupDialogItem groupDialogItem = (GroupDialogItem) thisView.squaresListBody.listItemBodiesMap.get("group#" + group.gid + "_" + group.name);

				groupDialogItem.gripCardBackground.setVisibility(View.VISIBLE);

				Vibrator vibrator = (Vibrator) this.mainController.thisActivity.getSystemService(Service.VIBRATOR_SERVICE);
				long[] pattern = { 100, 100, 300 };
				vibrator.vibrate(pattern, -1);

				thisView.squaresListBody.startOrdering("group#" + group.gid + "_" + group.name);

				onLongPressView = onTouchDownView;
				onTouchDownView = null;
			}
		}
	}

	public void onSingleTapUp(MotionEvent event) {
		if (onLongPressView != null) {
			if (onTouchDownGroup != null) {
				Group group = data.relationship.groupsMap.get("" + onTouchDownGroup.gid);
				if (group != null) {
					GroupDialogItem groupDialogItem = (GroupDialogItem) thisView.squaresListBody.listItemBodiesMap.get("group#" + group.gid + "_" + group.name);
					groupDialogItem.gripCardBackground.setVisibility(View.INVISIBLE);
				}

				onLongPressView = null;
				onTouchDownGroup = null;
				thisView.squaresListBody.stopOrdering();
			}
		}
		if (onTouchDownView != null) {
			String view_class = (String) onTouchDownView.getTag(R.id.tag_class);
			if (view_class.equals("share_view")) {
				onTouchDownView.performClick();
			} else if (view_class.equals("group_view")) {
				onTouchDownView.performClick();
			} else if (view_class.equals("group_setting")) {
				onTouchDownView.performClick();
			}
			onTouchDownView = null;
		}
		isTouchDown = false;
	}

	public void onActivityResult(int requestCode, int resultCode, Data data2) {
		if (requestCode == SCAN_MESSAGEDETAIL) {
			if (thisView.squareMessageListBody != null) {
				if (thisView.squareMessageListBody.listItemsSequence.size() > 0) {
					if (currentScanMessageKey != null) {
						SharesMessageBody body = (SharesMessageBody) thisView.squareMessageListBody.listItemBodiesMap.get("message#" + currentScanMessageKey);
						if (body != null) {
							body.setContent(body.message, body.fileName);
						}
					}
				}
			}
		}
	}

	public void onBackPressed() {

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (mainController.thisView.activityStatus.state == mainController.thisView.activityStatus.SHARE) {
			if (thisView.isShowSquareDialog) {
				thisView.dismissSquareDialog();
				return false;
			}
		}
		return true;
	}
}
