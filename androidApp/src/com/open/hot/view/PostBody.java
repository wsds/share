package com.open.hot.view;

import java.io.File;
import java.util.ArrayList;
import java.util.Stack;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.open.hot.R;
import com.open.hot.model.Data.Hot;
import com.open.hot.model.Data.Hot.Information;
import com.open.hot.model.FileHandlers;
import com.open.lib.TouchImageView;
import com.open.lib.TouchTextView;
import com.open.lib.TouchView;

public class PostBody {
	public String tag = "PostBody";

	public ViewManage viewManage = ViewManage.getInstance();
	public ImageLoader imageLoader = ImageLoader.getInstance();
	public DisplayMetrics displayMetrics;
	public LayoutInflater mInflater;

	public Drawable card_background_ff;
	public Drawable card_background;

	public FileHandlers fileHandlers = FileHandlers.getInstance();
	public File mImageFile;

	public String parent = null;

	public ArrayList<String> children = null;
	public ArrayList<String> brothers = null;
	public int index = -1;

	public PostBody mirror;

	public ViewGroup postContainer;

	public class Relation {
		public String parent = null;
		public ArrayList<String> children = null;
		public ArrayList<String> brothers = null;
		public float record_x;
	}

	public Stack<Relation> relations = new Stack<Relation>();

	public void pushRelation() {
		Relation relation = new Relation();

		if (parent != null) {
			relation.parent = parent;
		}

		if (children != null) {
			relation.children = children;
		}
		if (brothers != null) {
			relation.brothers = brothers;
		}

		// recordX();
		relation.record_x = record_x;
		relations.push(relation);

	}

	public void peekRelation() {
		if (relations.size() == 0) {
			return;
		}
		Relation relation = relations.peek();
		if (relation != null) {
			this.parent = relation.parent;
			this.children = relation.children;
			this.brothers = relation.brothers;
			this.record_x = relation.record_x;

		}
	}

	public void unPeekRelation() {
	}

	public void popRelation() {
		unPeekRelation();
		relations.pop();
	}

	public class Status {
		public int NORMAL = 0, SCALED = 1, FOLD = 2, FREED = 3;
		public int state = NORMAL;
	}

	public double endValue = 1;

	public Hot hot;

	public String key;
	public Information information;

	public Status status = new Status();

	public HotType hotType = new HotType();;// "container" | "paper" | "photo"

	public class HotType {
		public int CONTAINER = 0, PAPER = 1, PHOTO = 2;
		public int type = CONTAINER;
	}

	public TouchView postView;

	public void logPost() {
		Log.d(tag, "Log Post:  " + this.key);
		Log.d(tag, "x=  " + x + "        y=" + y + "        alpha=" + alpha + "        record_x=" + record_x);
		Log.d(tag, "parent:  " + parent);
		if (brothers != null) {
			Log.d(tag, "brother:  " + brothers.toString());
		}
		if (children != null) {
			Log.d(tag, "children:  " + children.toString());
		}
		if (visible == View.GONE) {
			Log.d(tag, "visible:  " + "View.GONE");
		} else if (visible == View.VISIBLE) {
			Log.d(tag, "visible:  " + "View.VISIBLE");
		} else if (visible == View.INVISIBLE) {
			Log.d(tag, "visible:  " + "View.INVISIBLE");
		}
	}

	public TouchView titleView;
	public TouchTextView sub_title_view;
	public TouchView children_list_view;
	public TouchImageView background_image1;
	public TouchImageView content_image;
	public int cardWidth = 0;
	public int cardHeight = 0;

	public HotType determineHotType(Hot hot) {
		if (!hot.type.equals("hot")) {
			hotType.type = hotType.CONTAINER;
		} else if (hot.content == null || hot.content.size() == 0) {
			hotType.type = hotType.CONTAINER;
		} else if (hot.content.size() == 1) {
			hotType.type = hotType.PHOTO;
		} else {
			Log.d(tag, "" + hot.content.size());
			hotType.type = hotType.PAPER;
		}

		return hotType;
	}

	@SuppressLint("NewApi")
	public View initialize(Hot hot, double endValue, Hot parentHot, int index) {
		if (hot == null) {
			return null;
		}
		mInflater = viewManage.mInflater;
		displayMetrics = viewManage.displayMetrics;

		postContainer = viewManage.postContainer;

		cardWidth = (int) (displayMetrics.widthPixels * 4 / 9);
		cardHeight = (int) (cardWidth * 1.78f);

		card_background_ff = viewManage.thisActivity.getResources().getDrawable(R.drawable.card_background_white_ff_radius);
		card_background = viewManage.thisActivity.getResources().getDrawable(R.drawable.card_background_white_radius);

		determineHotType(hot);
		this.hot = hot;
		this.information = hot.information;
		this.key = hot.id;

		this.index = index;
		this.children = hot.children;

		if (parentHot != null) {
			this.brothers = parentHot.children;
			this.parent = parentHot.id;
		}
		// hotType = "pape22r";
		if (hotType.type == hotType.CONTAINER) {
			postView = (TouchView) mInflater.inflate(R.layout.post_container, null);

			titleView = (TouchView) postView.findViewById(R.id.title);

			TouchTextView title_text_view = (TouchTextView) titleView.findViewById(R.id.title_text);
			title_text_view.setText(information.title);

			sub_title_view = (TouchTextView) postView.findViewById(R.id.sub_title);
			sub_title_view.setText(information.abstractStr);

			background_image1 = (TouchImageView) postView.findViewById(R.id.background_image);

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

		} else if (hotType.type == hotType.PAPER) {
			postView = (TouchView) mInflater.inflate(R.layout.post_paper, null);

			titleView = (TouchView) postView.findViewById(R.id.title);

			TouchTextView title_text_view = (TouchTextView) titleView.findViewById(R.id.title_text);
			title_text_view.setText(information.title);

			TouchTextView content_view = (TouchTextView) postView.findViewById(R.id.content);
			content_view.setText(information.abstractStr);

			content_image = (TouchImageView) postView.findViewById(R.id.content_image);

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

		} else if (hotType.type == hotType.PHOTO) {

			Log.d(tag, "" + hot.content.size());
			postView = (TouchView) mInflater.inflate(R.layout.post_photo, null);

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
		}
		this.endValue = endValue;

		postView.setTag(R.id.tag_post_body, this);

		viewManage.postPool.putPost(hot.id, this);
		render(endValue);

		return postView;
	}

	public TouchView.LayoutParams sizeParams = new TouchView.LayoutParams(cardWidth, cardHeight);

	public TouchView.LayoutParams imageParams = new TouchView.LayoutParams(100, 100);

	public float record_x;
	public boolean isRecordX = false;

	public float x = 0;
	public float y = 0;
	public int height = 0;
	public int width = 0;
	public float alpha = 0;

	public int visible = View.GONE;

	public void setXY(float x, float y) {
		if (this.x == x && this.y == y) {
			return;
		}
		this.x = x;
		this.y = y;
		postView.setX(this.x);
		postView.setY(this.y);
	}

	public void setSize(int width, int height) {
		if (this.width == width && this.height == height) {
			return;
		}
		this.width = width;
		this.height = height;
		sizeParams.width = this.width;
		sizeParams.height = this.height;
		postView.setLayoutParams(sizeParams);
	}

	public void setAlpha(float α) {
		if (this.alpha == α) {
			return;
		}
		this.alpha = α;
		postView.setAlpha(this.alpha);
	}

	public void setVisibility(int visible) {
		if (this.visible == visible) {
			return;
		}
		this.visible = visible;
		postView.setVisibility(visible);
		if (visible == View.VISIBLE) {
			if (postView.getParent() == null) {
				postContainer.addView(postView);
			}
		}
	}

	public void recordX() {
		if (isRecordX == false) {
			record_x = postView.getX();
			isRecordX = true;
		}
		if (record_x < -200) {
			logPost();
		}
	}

	@SuppressLint("NewApi")
	public void render(double value) {
		renderThis(value);
		renderRelations(value);
	}

	@SuppressLint("NewApi")
	public void renderThis(double value) {
		float y = (float) ((displayMetrics.heightPixels - 38 - cardHeight) * value);
		float x = (float) (record_x * value);
		setXY(x, y);

		int width = (int) ((cardWidth - displayMetrics.widthPixels) * value + displayMetrics.widthPixels);
		int height = (int) ((cardHeight - displayMetrics.heightPixels + 38) * value + displayMetrics.heightPixels - 38);
		setSize(width, height);

		imageParams.width = (int) (cardWidth - displayMetrics.density * 20 + (displayMetrics.widthPixels - cardWidth) * (1 - value));
		imageParams.height = (int) (cardWidth - displayMetrics.density * 20 + (displayMetrics.widthPixels - cardWidth) * (1 - value));
		if (content_image != null) {
			content_image.setLayoutParams(imageParams);
		}

		if (background_image1 != null) {
			background_image1.setAlpha((float) (1 - value * value));
		}
		if (children_list_view != null) {
			children_list_view.setAlpha((float) (value * value));
		}
		if (value < 0.1) {
			if (sub_title_view != null) {
				sub_title_view.setVisibility(View.VISIBLE);
			}
			postView.setBackground(card_background_ff);
			endValue = 0;
		} else {
			if (value > 0.9) {
				endValue = 1;
			}
			if (sub_title_view != null) {
				sub_title_view.setVisibility(View.GONE);
			}
			postView.setBackground(card_background);

		}
	}

	public void renderRelations(double value) {
		// Post
		// if (left != null) {
		// left.postView.setVisibility(View.VISIBLE);
		// left.postView.setAlpha(1);
		// left.postView.setY((float) ((displayMetrics.heightPixels - 38 - cardHeight) * value));
		// left.postView.setX((float) (-displayMetrics.widthPixels + displayMetrics.widthPixels * value + (record_x - displayMetrics.density * 2 - cardWidth) * value));
		// left.postView.setLayoutParams(renderParams);
		// if (left.content_image != null) {
		// left.content_image.setLayoutParams(imageParams);
		// }
		//
		// if (left.left != null) {
		// left.left.postView.setVisibility(View.VISIBLE);
		// left.left.postView.setAlpha(1);
		//
		// left.left.postView.setY((float) ((displayMetrics.heightPixels - 38 - cardHeight) * value));
		// left.left.postView.setX((float) (-displayMetrics.widthPixels * 2 + displayMetrics.widthPixels * 2 * value + (record_x - displayMetrics.density * 4 - 2 * cardWidth) * value));
		// left.left.postView.setLayoutParams(renderParams);
		// if (left.left.content_image != null) {
		// left.left.content_image.setLayoutParams(imageParams);
		// }
		// }
		// }
		// if (right != null) {
		// right.postView.setVisibility(View.VISIBLE);
		// right.postView.setAlpha(1);
		//
		// right.postView.setY((float) ((displayMetrics.heightPixels - 38 - cardHeight) * value));
		// right.postView.setX((float) (displayMetrics.widthPixels - displayMetrics.widthPixels * value + (record_x + displayMetrics.density * 2 + cardWidth) * value));
		//
		// right.postView.setLayoutParams(renderParams);
		// if (right.content_image != null) {
		// right.content_image.setLayoutParams(imageParams);
		// }
		//
		// if (right.right != null) {
		// right.right.postView.setVisibility(View.VISIBLE);
		// right.right.postView.setAlpha(1);
		//
		// right.right.postView.setY((float) ((displayMetrics.heightPixels - 38 - cardHeight) * value));
		// right.right.postView.setX((float) (displayMetrics.widthPixels * 2 - displayMetrics.widthPixels * 2 * value + (record_x + displayMetrics.density * 4 + 2 * cardWidth) * value));
		//
		// right.right.postView.setLayoutParams(renderParams);
		// if (right.right.content_image != null) {
		// right.right.content_image.setLayoutParams(imageParams);
		// }
		// }
		// }

	}

}
