package com.open.welinks.controller;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.open.welinks.model.Data;
import com.open.welinks.view.PictureBrowseView;

public class PictureBrowseController {
	public Data data = Data.getInstance();
	public String tag = "PictureBrowseController";

	public Context context;
	public PictureBrowseView thisView;
	public PictureBrowseController thisController;
	public Activity thisActivity;

	public ArrayList<String> imagesBrowseList;

	public int currentPosition;
	public int currentType;

	public OnClickListener mOnClickListener;
	public OnPageChangeListener mOnPageChangeListener;
	public OnDownloadListener downloadListener;

	public PictureBrowseController(Activity thisActivity) {
		this.context = thisActivity;
		this.thisActivity = thisActivity;
		currentPosition = Integer.valueOf(thisActivity.getIntent().getStringExtra("position"));
		currentType = thisActivity.getIntent().getIntExtra("type", thisView.IMAGEBROWSE_COMMON);
		if (data.tempData.selectedImageList != null)
			imagesBrowseList = data.tempData.selectedImageList;
		else
			imagesBrowseList = new ArrayList<String>();
	}

	public void onCreate(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			currentPosition = savedInstanceState.getInt("position");
		}
	}

	public void onSaveInstanceState(Bundle outState) {
		outState.putInt("position", thisView.imageViewPageContent.getCurrentItem());
	}

	public void initializeListeners() {
		mOnPageChangeListener = new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int state) {
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageSelected(int position) {
				currentPosition = position;
				thisView.imageNumberView.setText(position + 1 + "/" + imagesBrowseList.size());
			}
		};
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(thisView.backView)) {
					thisActivity.setResult(Activity.RESULT_CANCELED);
					thisActivity.finish();
				} else if (view.equals(thisView.deleteButtonView)) {
					imagesBrowseList.remove(currentPosition);
					if (imagesBrowseList.size() == 0) {
						thisActivity.finish();
					} else if (currentPosition > imagesBrowseList.size() - 1) {
						currentPosition = imagesBrowseList.size() - 1;
						// thisView.imageViewPageContent.setCurrentItem(currentPosition);
					}
					thisView.notifyAdapter();
					Toast.makeText(context, "deleteButtonView", Toast.LENGTH_SHORT).show();
				}
			}
		};
		downloadListener = new OnDownloadListener() {

			@Override
			public void onSuccess(DownloadFile instance, int status) {
				thisView.imageLoader.displayImage("file://" + instance.path, (ImageView) instance.view, thisView.options);

			}

			@Override
			public void loading(DownloadFile instance, int precent, int status) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFailure(DownloadFile instance, int status) {
				// TODO Auto-generated method stub

			}
		};
	}

	public void bindEvent() {
		thisView.backView.setOnClickListener(mOnClickListener);
		thisView.deleteButtonView.setOnClickListener(mOnClickListener);
		thisView.imageViewPageContent.setOnPageChangeListener(mOnPageChangeListener);
	}

	public void onBackPressed() {
		thisActivity.setResult(Activity.RESULT_CANCELED);
		thisActivity.finish();
	}
}
