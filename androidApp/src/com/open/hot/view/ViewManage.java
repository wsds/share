package com.open.hot.view;

import android.app.Activity;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.open.hot.R;

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

	public int cardWidth;
	public int cardHeight;
	public int position_A;
	public int position_B;
	public int position_C;

	public Activity thisActivity;

	void initialize(Activity thisActivity) {
		this.thisActivity = thisActivity;
		displayMetrics = new DisplayMetrics();

		thisActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		mInflater = thisActivity.getLayoutInflater();

		imageLoader.init(ImageLoaderConfiguration.createDefault(thisActivity));
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_stub).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).build();
		// roundOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_stub).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).displayer(new RoundedBitmapDisplayer((int) (5 * displayMetrics.density))).build();
		roundOptions = options;
		cardWidth = (int) (displayMetrics.widthPixels * 4 / 9);
		cardHeight = (int) (cardWidth * 1.78f);

		position_A = 38;
		position_B = displayMetrics.heightPixels - cardHeight;
		position_C = (int) (displayMetrics.heightPixels - 60 * displayMetrics.density);
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