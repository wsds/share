package com.open.hot.view;

import java.io.File;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.open.hot.R;
import com.open.hot.model.Data.Hot;
import com.open.hot.model.Data.Hot.Information;
import com.open.lib.TouchImageView;
import com.open.lib.TouchTextView;
import com.open.lib.TouchView;
import com.open.hot.model.FileHandlers;

public class PostBody {
	public String tag = "PostBody";
	
	public ViewManage viewManage = ViewManage.getInstance();
	public ImageLoader imageLoader = ImageLoader.getInstance();
	public DisplayMetrics displayMetrics;
	public LayoutInflater mInflater;

	public FileHandlers fileHandlers = FileHandlers.getInstance();
	public File mImageFile;

	public class Status {
		public int NORMAL = 0, SCALED = 1, FOLD = 2;
		public int state = NORMAL;
	}

	public Hot hot;
	public Information information;

	public Status status = new Status();

	public String hotType;// "container" | "paper" | "photo"

	public View postView;

	public TouchView titleView;


	public String determineHotType(Hot hot) {
		if (!hot.type.equals("hot")) {
			hotType = "container";
		} else if (hot.content == null || hot.content.size() == 0) {
			hotType = "container";
		} else if (hot.content.size() == 1) {
			hotType = "photo";
		} else {
			Log.d(tag, ""+hot.content.size());
			hotType = "paper";
		}

		return hotType;
	}

	public View initialize(Hot hot) {
		mInflater = viewManage.mInflater;
		displayMetrics = viewManage.displayMetrics;

		determineHotType(hot);
		this.hot = hot;
		information = hot.information;
		// hotType = "pape22r";
		if (hotType.equals("container")) {
			postView = mInflater.inflate(R.layout.post_container, null);

			titleView = (TouchView) postView.findViewById(R.id.title);

			TouchTextView title_text_view = (TouchTextView) titleView.findViewById(R.id.title_text);
			title_text_view.setText(information.title);

			TouchTextView sub_title_view = (TouchTextView) postView.findViewById(R.id.sub_title);
			sub_title_view.setText(information.abstractStr);

			TouchImageView background_image1 = (TouchImageView) postView.findViewById(R.id.background_image);

			mImageFile = fileHandlers.sdcardImageFolder;
			File currentImageFile = new File(mImageFile, information.background);
			String filepath = "file://" + currentImageFile.getAbsolutePath();
			imageLoader.displayImage(filepath, background_image1, viewManage.options);

		} else if (hotType.equals("paper")) {
			postView = mInflater.inflate(R.layout.post_paper, null);

			titleView = (TouchView) postView.findViewById(R.id.title);

			TouchTextView title_text_view = (TouchTextView) titleView.findViewById(R.id.title_text);
			title_text_view.setText(information.title);

			TouchTextView content_view = (TouchTextView) postView.findViewById(R.id.content);
			content_view.setText(information.abstractStr);

			TouchImageView content_image = (TouchImageView) postView.findViewById(R.id.content_image);
			imageLoader.displayImage("drawable://" + R.drawable.test_abc_121212, content_image, viewManage.options);

		} else if (hotType.equals("photo")) {
			
			Log.d(tag, ""+hot.content.size());
			postView = mInflater.inflate(R.layout.post_photo, null);

			titleView = (TouchView) postView.findViewById(R.id.title);

			TouchTextView title_text_view = (TouchTextView) titleView.findViewById(R.id.title_text);
			title_text_view.setText(information.title);

			TouchTextView sub_title_view = (TouchTextView) postView.findViewById(R.id.sub_title);
			sub_title_view.setText(information.abstractStr);

			TouchImageView background_image1 = (TouchImageView) postView.findViewById(R.id.background_image);
			imageLoader.displayImage("drawable://" + R.drawable.test_abc_121212, background_image1, viewManage.options);
		}
		return postView;
	}

}
