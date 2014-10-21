package com.open.hot.view;

import java.io.File;
import android.annotation.SuppressLint;
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
import android.widget.ImageView;

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

	double endValue = 1;

	public Hot hot;
	public Information information;

	public Status status = new Status();

	public String hotType;// "container" | "paper" | "photo"

	public View postView;

	public TouchView titleView;

	public TouchView children_list_view;

	public int cardWidth = 0;
	public int cardHeight = 0;

	public String determineHotType(Hot hot) {
		if (!hot.type.equals("hot")) {
			hotType = "container";
		} else if (hot.content == null || hot.content.size() == 0) {
			hotType = "container";
		} else if (hot.content.size() == 1) {
			hotType = "photo";
		} else {
			Log.d(tag, "" + hot.content.size());
			hotType = "paper";
		}

		return hotType;
	}

	@SuppressLint("NewApi")
	public View initialize(Hot hot) {
		mInflater = viewManage.mInflater;
		displayMetrics = viewManage.displayMetrics;

		cardWidth = (int) (displayMetrics.widthPixels * 4 / 9);
		cardHeight = (int) (cardWidth * 1.78f);

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
			imageLoader.displayImage(filepath, background_image1, viewManage.roundOptions);

			background_image1.setAlpha((float) 0);

			int imageHeight = (int) (cardWidth - displayMetrics.density * 24) / 3;
			mImageFile = fileHandlers.sdcardImageFolder;
			TouchView.LayoutParams imageLayoutParams = new TouchView.LayoutParams(imageHeight, imageHeight);
			TouchView.LayoutParams text_container_layoutParams = new TouchView.LayoutParams((int) (cardWidth - imageHeight - displayMetrics.density * 24), imageHeight);

			children_list_view = (TouchView) postView.findViewById(R.id.children_list);
			children_list_view.setVisibility(View.VISIBLE);
			children_list_view.setY(cardHeight - imageHeight * 5 - displayMetrics.density * 18);

			TouchView content_text_5_container = (TouchView) postView.findViewById(R.id.content_text_5_container);
			content_text_5_container.setLayoutParams(text_container_layoutParams);
			content_text_5_container.setX(imageHeight + displayMetrics.density * 14);
			content_text_5_container.setY(displayMetrics.density * 8 + imageHeight * 4);
			TouchTextView content_text_5 = (TouchTextView) postView.findViewById(R.id.content_text_5);
			content_text_5.setText("text5");
			TouchImageView content_image_5 = (TouchImageView) postView.findViewById(R.id.content_image_5);
			content_image_5.setLayoutParams(imageLayoutParams);
			content_image_5.setX(displayMetrics.density * 10);
			content_image_5.setY(displayMetrics.density * 8 + imageHeight * 4);
			currentImageFile = new File(mImageFile, "pp5.jpg");
			filepath = "file://" + currentImageFile.getAbsolutePath();
			imageLoader.displayImage(filepath, content_image_5, viewManage.options);

			TouchView content_text_4_container = (TouchView) postView.findViewById(R.id.content_text_4_container);
			content_text_4_container.setLayoutParams(text_container_layoutParams);
			content_text_4_container.setX(imageHeight + displayMetrics.density * 14);
			content_text_4_container.setY(displayMetrics.density * 6 + imageHeight * 3);
			TouchTextView content_text_4 = (TouchTextView) postView.findViewById(R.id.content_text_4);
			content_text_4.setText("text4");
			TouchImageView content_image_4 = (TouchImageView) postView.findViewById(R.id.content_image_4);
			content_image_4.setLayoutParams(imageLayoutParams);
			content_image_4.setX(displayMetrics.density * 10);
			content_image_4.setY(displayMetrics.density * 6 + imageHeight * 3);
			currentImageFile = new File(mImageFile, "pp4.jpg");
			filepath = "file://" + currentImageFile.getAbsolutePath();
			imageLoader.displayImage(filepath, content_image_4, viewManage.options);

			TouchView content_text_3_container = (TouchView) postView.findViewById(R.id.content_text_3_container);
			content_text_3_container.setLayoutParams(text_container_layoutParams);
			content_text_3_container.setX(imageHeight + displayMetrics.density * 14);
			content_text_3_container.setY(displayMetrics.density * 4 + imageHeight * 2);
			TouchTextView content_text_3 = (TouchTextView) postView.findViewById(R.id.content_text_3);
			content_text_3.setText("text3");
			TouchImageView content_image_3 = (TouchImageView) postView.findViewById(R.id.content_image_3);
			content_image_3.setLayoutParams(imageLayoutParams);
			content_image_3.setX(displayMetrics.density * 10);
			content_image_3.setY(displayMetrics.density * 4 + imageHeight * 2);
			currentImageFile = new File(mImageFile, "pp3.jpg");
			filepath = "file://" + currentImageFile.getAbsolutePath();
			imageLoader.displayImage(filepath, content_image_3, viewManage.options);

			TouchView content_text_2_container = (TouchView) postView.findViewById(R.id.content_text_2_container);
			content_text_2_container.setLayoutParams(text_container_layoutParams);
			content_text_2_container.setX(imageHeight + displayMetrics.density * 14);
			content_text_2_container.setY(displayMetrics.density * 2 + imageHeight);
			TouchTextView content_text_2 = (TouchTextView) postView.findViewById(R.id.content_text_2);
			content_text_2.setText("text2");
			TouchImageView content_image_2 = (TouchImageView) postView.findViewById(R.id.content_image_2);
			content_image_2.setLayoutParams(imageLayoutParams);
			content_image_2.setX(displayMetrics.density * 10);
			content_image_2.setY(displayMetrics.density * 2 + imageHeight);
			currentImageFile = new File(mImageFile, "pp2.jpg");
			filepath = "file://" + currentImageFile.getAbsolutePath();
			imageLoader.displayImage(filepath, content_image_2, viewManage.options);

			TouchView content_text_1_container = (TouchView) postView.findViewById(R.id.content_text_1_container);
			content_text_1_container.setLayoutParams(text_container_layoutParams);
			content_text_1_container.setX(imageHeight + displayMetrics.density * 14);
			content_text_1_container.setY(displayMetrics.density * 0);
			TouchTextView content_text_1 = (TouchTextView) postView.findViewById(R.id.content_text_1);
			content_text_1.setText("text1");
			TouchImageView content_image_1 = (TouchImageView) postView.findViewById(R.id.content_image_1);
			content_image_1.setLayoutParams(imageLayoutParams);
			content_image_1.setX(displayMetrics.density * 10);
			content_image_1.setY(displayMetrics.density * 0);
			currentImageFile = new File(mImageFile, "pp1.jpg");
			filepath = "file://" + currentImageFile.getAbsolutePath();
			imageLoader.displayImage(filepath, content_image_1, viewManage.options);

		} else if (hotType.equals("paper")) {
			postView = mInflater.inflate(R.layout.post_paper, null);

			titleView = (TouchView) postView.findViewById(R.id.title);

			TouchTextView title_text_view = (TouchTextView) titleView.findViewById(R.id.title_text);
			title_text_view.setText(information.title);

			TouchTextView content_view = (TouchTextView) postView.findViewById(R.id.content);
			content_view.setText(information.abstractStr);

			TouchImageView content_image = (TouchImageView) postView.findViewById(R.id.content_image);

			int imageHeight = (int) (cardWidth - displayMetrics.density * 20);
			// TouchView.LayoutParams imageLayoutParams = new TouchView.LayoutParams(imageHeight, imageHeight);
			// content_image.setLayoutParams(imageLayoutParams);
			content_image.setY(cardHeight - imageHeight - displayMetrics.density * 10);
			content_image.setX(displayMetrics.density * 10);

			content_image.setScaleType(ImageView.ScaleType.CENTER_CROP);

			mImageFile = fileHandlers.sdcardImageFolder;
			File currentImageFile = new File(mImageFile, information.background);
			String filepath = "file://" + currentImageFile.getAbsolutePath();
			imageLoader.displayImage(filepath, content_image, viewManage.options);

		} else if (hotType.equals("photo")) {

			Log.d(tag, "" + hot.content.size());
			postView = mInflater.inflate(R.layout.post_photo, null);

			titleView = (TouchView) postView.findViewById(R.id.title);

			TouchTextView title_text_view = (TouchTextView) titleView.findViewById(R.id.title_text);
			title_text_view.setText(information.title);

			TouchTextView sub_title_view = (TouchTextView) postView.findViewById(R.id.sub_title);
			sub_title_view.setText(information.abstractStr);

			TouchImageView background_image1 = (TouchImageView) postView.findViewById(R.id.background_image);
			imageLoader.displayImage("drawable://" + R.drawable.test_abc_121212, background_image1, viewManage.roundOptions);
		}
		return postView;
	}
}
