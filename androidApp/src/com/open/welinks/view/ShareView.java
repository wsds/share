package com.open.welinks.view;

import com.open.welinks.GroupListActivity;
import com.open.welinks.R;
import com.open.welinks.ShareMessageDetailActivity;
import com.open.welinks.model.Data.Messages.Message;
import com.open.welinks.model.Data.Shares.Share;
import com.open.welinks.model.Data.Shares.Share.ShareMessage;
import com.open.welinks.utils.WeChatShareUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class ShareView extends FrameLayout {
	private Context context;
	private Activity activity;
	private LinearLayout layout_one, layout_two, layout_three;
	private View square_share, friend_group, wechat_friend, wechat_circle, sina_weibo, qq_qzone;

	private WeChatShareUtils weChatShareUtils;

	private onWeChatClickListener mWeChatClickListener;
	private Bitmap bitmap;
	private String content, phone, gid, gsid;
	public int RESULT_SHAREVIEW = 0x99;

	private enum Status {
		square_group, friend_group, wechat_friend, wechat_circle, sina_weibo, qq_qzone
	}

	private OnClickListener mOnClickListener;

	public ShareView(Context context) {
		super(context);
		this.context = context;
		this.activity = (Activity) context;
		onCreate();
	}

	public ShareView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		this.activity = (Activity) context;
		onCreate();
	}

	public ShareView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		this.activity = (Activity) context;
		onCreate();
	}

	public class onWeChatClickListener {
		public void onWeChatClick() {
		}
	}

	public void setOnWeChatClickListener(onWeChatClickListener mListener) {
		this.mWeChatClickListener = mListener;
	}

	public void setWeChatContent(Bitmap bitmap, String content, String phone, String gid, String gsid) {
		this.bitmap = bitmap;
		this.content = content;
		this.phone = phone;
		this.gid = gid;
		this.gsid = gsid;
	}

	private void onCreate() {
		LayoutInflater.from(context).inflate(R.layout.layout_share, this);

		weChatShareUtils = WeChatShareUtils.getInstance(context);

		layout_one = (LinearLayout) this.findViewById(R.id.layout_one);
		layout_two = (LinearLayout) this.findViewById(R.id.layout_two);
		layout_three = (LinearLayout) this.findViewById(R.id.layout_three);

		square_share = this.findViewById(R.id.square_share);
		friend_group = this.findViewById(R.id.friend_group);
		wechat_friend = this.findViewById(R.id.wechat_friend);
		wechat_circle = this.findViewById(R.id.wechat_circle);
		sina_weibo = this.findViewById(R.id.sina_weibo);
		qq_qzone = this.findViewById(R.id.qq_qzone);

		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(square_share)) {
					shareToLocal(Status.square_group);
				} else if (view.equals(friend_group)) {
					shareToLocal(Status.friend_group);
				} else if (view.equals(wechat_friend)) {
					mWeChatClickListener.onWeChatClick();
					shareToWechat(Status.wechat_friend);
				} else if (view.equals(wechat_circle)) {
					mWeChatClickListener.onWeChatClick();
					shareToWechat(Status.wechat_circle);
				} else if (view.equals(sina_weibo)) {

				} else if (view.equals(qq_qzone)) {

				}

			}
		};

		mWeChatClickListener = new onWeChatClickListener();

		square_share.setOnClickListener(mOnClickListener);
		friend_group.setOnClickListener(mOnClickListener);
		wechat_friend.setOnClickListener(mOnClickListener);
		wechat_circle.setOnClickListener(mOnClickListener);
		sina_weibo.setOnClickListener(mOnClickListener);
		qq_qzone.setOnClickListener(mOnClickListener);
	}

	private void shareToLocal(Status status) {
		Intent intent = new Intent(activity, GroupListActivity.class);
		if (status == Status.friend_group) {
			intent.putExtra("type", "message");
		} else if (status == Status.square_group) {
			intent.putExtra("type", "share");
		}
		this.activity.startActivityForResult(intent, RESULT_SHAREVIEW);
	}

	private void shareToWechat(Status status) {
		if (status == Status.wechat_friend) {
			weChatShareUtils.shareMessageToWXFriends(bitmap, content, phone, gid, gsid);
		} else if (status == Status.wechat_circle) {
			weChatShareUtils.shareMessageToWXMoments(bitmap, content, phone, gid, gsid);
		}

	}
}
