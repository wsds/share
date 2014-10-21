package com.open.hot.view;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.open.hot.R;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;

public class ViewManage {

	public String tag = "ViewManage";

	public HotView loginView = null;

	public Handler handler = new Handler();

	public static ViewManage viewManager;

	public static ViewManage getInstance() {
		if (viewManager == null) {
			viewManager = new ViewManage();
		}
		return viewManager;
	}

	 public PostPool postPool = new PostPool();

	public ImageLoader imageLoader = ImageLoader.getInstance();
	public DisplayImageOptions roundOptions;
	public DisplayImageOptions options;
	public DisplayMetrics displayMetrics;
	public LayoutInflater mInflater;

	public Drawable card_background_ff;

	public Drawable card_background;

	void initialize(Activity thisActivity) {
		displayMetrics = new DisplayMetrics();

		thisActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		mInflater = thisActivity.getLayoutInflater();

		imageLoader.init(ImageLoaderConfiguration.createDefault(thisActivity));
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_stub).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).build();
		roundOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_stub).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).displayer(new RoundedBitmapDisplayer((int) (5 * displayMetrics.density))).build();

		card_background_ff = thisActivity.getResources().getDrawable(R.drawable.card_background_white_ff_radius);
		card_background = thisActivity.getResources().getDrawable(R.drawable.card_background_white_radius);
	}

	public void postNotifyView(final String viewName) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				viewManager.notifyView(viewName);
			}
		});
	}

	public void notifyView(String viewName) {

	}
}