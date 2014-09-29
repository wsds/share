package com.open.welinks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.FileHandlers;
import com.open.welinks.utils.CharacterParser;
import com.open.welinks.utils.ClearEditText;
import com.open.welinks.utils.SideBar;
import com.open.welinks.utils.SideBar.OnTouchingLetterChangedListener;

public class FriendsSortListActivity extends Activity {

	public Data data = Data.getInstance();
	public String tag = "FriendsSortListActivity";

	public static int INVITA_FRIEND_GROUP = 0x01;// Invite friends to add to the group
	public static int RECOMMEND_FRIEND_GROUP = 0x02;// Recommend group to friends
	public static int FORWARD_MESSAGE_FRIEND = 0x03;// Forwarding messages to friends

	private ListView sortListView;
	private SideBar sideBar;
	private TextView dialog;
	private SortAdapter adapter;
	private ClearEditText mClearEditText;

	private CharacterParser characterParser;
	private List<SortModel> SourceDateList;

	private PinyinComparator pinyinComparator;
	public int currentOperationType = 0;
	public Group currentGroup;

	public FileHandlers fileHandlers = FileHandlers.getInstance();

	public DisplayImageOptions options;

	public List<String> friends = new ArrayList<String>();

	public ArrayList<String> invitaFriends = new ArrayList<String>();
	public OnClickListener mOnClickListener;
	public RelativeLayout backView;
	public TextView mConfirm;

	public RelativeLayout rightContainerView;

	public TextView backTitleView;
	public Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int type = getIntent().getIntExtra("type", -1);
		if (type == -1) {
			return;
		} else {
			currentOperationType = type;
		}
		String gid = this.getIntent().getStringExtra("gid");
		if (gid != null && !"".equals(gid)) {
			currentGroup = data.relationship.groupsMap.get(gid);
			if (currentGroup == null) {
				finish();
			} else {
				// thisView.showCurrentGroupMembers();
			}
		} else {
			finish();
		}
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();
		initData();
		initViews();
		initializeListeners();
		bindEvent();
	}

	public void initializeListeners() {
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(backView)) {
					setResult(Activity.RESULT_CANCELED);
					finish();
				} else if (view.equals(mConfirm)) {
					if (currentOperationType == INVITA_FRIEND_GROUP) {
						Intent intent = new Intent();
						intent.putStringArrayListExtra("invitaFriends", invitaFriends);
						setResult(Activity.RESULT_OK, intent);
					}
					finish();
				} else if (view.getTag(R.id.tag_class) != null) {
					String tag_class = (String) view.getTag(R.id.tag_class);
					if ("already_friend".equals(tag_class)) {
						String phone = (String) view.getTag(R.id.tag_first);
						invitaFriends.remove(phone);
						alreadyListContainer.removeView(view);
						adapter.notifyDataSetChanged();
						// if (invitaFriends.size() == 0) {
						// FrameLayout.LayoutParams layoutParams = (android.widget.FrameLayout.LayoutParams) mainContainer.getLayoutParams();
						// layoutParams.bottomMargin = 0;
						// }
					}
				}
			}
		};
	}

	public void bindEvent() {
		this.backView.setOnClickListener(mOnClickListener);
		this.mConfirm.setOnClickListener(mOnClickListener);
	}

	private void initData() {

		List<String> list = new ArrayList<String>();
		for (int i = 0; i < data.relationship.circles.size(); i++) {
			list.addAll(data.relationship.circlesMap.get(data.relationship.circles.get(i)).friends);
		}

		Set<String> set = new LinkedHashSet<String>();
		set.addAll(list);
		set.removeAll(currentGroup.members);
		list.clear();
		list.addAll(set);

		SourceDateList = filledData(list);
	}

	DisplayMetrics displayMetrics;

	public void showAlreayList() {
		int width = (int) (40 * displayMetrics.density);
		int spacing = (int) (5 * displayMetrics.density);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, width);
		layoutParams.setMargins(spacing, spacing, spacing, spacing);
		alreadyListContainer.removeAllViews();
		for (int i = 0; i < invitaFriends.size(); i++) {
			String key = invitaFriends.get(i);
			Friend friend = data.relationship.friendsMap.get(key);
			ImageView imageView = new ImageView(this);
			imageView.setTag(R.id.tag_class, "already_friend");
			imageView.setTag(R.id.tag_first, friend.phone);
			imageView.setOnClickListener(mOnClickListener);
			alreadyListContainer.addView(imageView, layoutParams);
			fileHandlers.getHeadImage(friend.head, imageView, options);
		}
	}

	public LinearLayout alreadyListContainer;
	public FrameLayout mainContainer;

	private void initViews() {
		displayMetrics = new DisplayMetrics();

		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		setContentView(R.layout.activity_sortlist);
		mainContainer = (FrameLayout) findViewById(R.id.mainContainer);
		alreadyListContainer = (LinearLayout) findViewById(R.id.alreadyListContainer);
		backView = (RelativeLayout) findViewById(R.id.backView);
		backTitleView = (TextView) findViewById(R.id.backTitleView);
		backTitleView.setText("选择好友");
		rightContainerView = (RelativeLayout) findViewById(R.id.rightContainer);
		sideBar = (SideBar) findViewById(R.id.sidrbar);
		dialog = (TextView) findViewById(R.id.dialog);
		sideBar.setTextView(dialog);
		sortListView = (ListView) findViewById(R.id.country_lvcountry);

		int dp_5 = (int) (5 * displayMetrics.density);
		mConfirm = new TextView(this);
		mConfirm.setGravity(Gravity.CENTER);
		mConfirm.setPadding(dp_5 * 2, dp_5, dp_5 * 2, dp_5);
		mConfirm.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		mConfirm.setText("完成");
		mConfirm.setTextColor(Color.WHITE);
		mConfirm.setBackgroundResource(R.drawable.textview_bg);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(0, dp_5, (int) 0, dp_5);
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		rightContainerView.addView(mConfirm, layoutParams);

		options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).showImageOnLoading(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).considerExifParams(true).displayer(new RoundedBitmapDisplayer(40)).build();

		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					sortListView.setSelection(position);
				}

			}
		});

		// sortListView.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// Toast.makeText(getApplication(), ((SortModel) adapter.getItem(position)).getName(), Toast.LENGTH_SHORT).show();
		// }
		// });

		// SourceDateList = filledData(getResources().getStringArray(R.array.date));

		Collections.sort(SourceDateList, pinyinComparator);
		adapter = new SortAdapter(this, SourceDateList);
		sortListView.setAdapter(adapter);

		mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);

		mClearEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				filterData(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	private List<SortModel> filledData(List<String> date) {
		List<SortModel> mSortList = new ArrayList<SortModel>();

		for (int i = 0; i < date.size(); i++) {
			SortModel sortModel = new SortModel();
			String key = date.get(i);
			key = data.relationship.friendsMap.get(key).nickName + "#" + key;
			sortModel.setName(key);
			String pinyin = characterParser.getSelling(key);
			String sortString = pinyin.substring(0, 1).toUpperCase();

			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}

			mSortList.add(sortModel);
		}
		return mSortList;

	}

	private void filterData(String filterStr) {
		List<SortModel> filterDateList = new ArrayList<SortModel>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = SourceDateList;
		} else {
			filterDateList.clear();
			for (SortModel sortModel : SourceDateList) {
				String name = sortModel.getName();
				name = name.substring(0, name.lastIndexOf("#"));
				if (name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())) {
					filterDateList.add(sortModel);
				}
			}
		}

		Collections.sort(filterDateList, pinyinComparator);
		adapter.updateListView(filterDateList);
	}

	class SortAdapter extends BaseAdapter implements SectionIndexer {
		private List<SortModel> list = null;
		private Context mContext;

		public SortAdapter(Context mContext, List<SortModel> list) {
			this.mContext = mContext;
			this.list = list;
		}

		public void updateListView(List<SortModel> list) {
			this.list = list;
			notifyDataSetChanged();
		}

		public int getCount() {
			return this.list.size();
		}

		public Object getItem(int position) {
			return list.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View view, ViewGroup arg2) {
			ViewHolder viewHolder = null;
			final SortModel mContent = list.get(position);
			if (view == null) {
				viewHolder = new ViewHolder();
				view = LayoutInflater.from(mContext).inflate(R.layout.item, null);
				viewHolder.tvTitle = (TextView) view.findViewById(R.id.tv_friendNickName);
				viewHolder.tvLetter = (TextView) view.findViewById(R.id.tv_nickNameFirst);
				viewHolder.tvLetterParent = (LinearLayout) view.findViewById(R.id.ll_nickNameType);
				viewHolder.headView = (ImageView) view.findViewById(R.id.iv_friendHead);
				viewHolder.friendInfoLLView = (LinearLayout) view.findViewById(R.id.ll_friendinfo);
				viewHolder.friendHeadStatus = (ImageView) view.findViewById(R.id.iv_friendHeadStatus);
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}
			String name = this.list.get(position).getName();
			int index = name.lastIndexOf("#");
			String name2 = name.substring(0, index);
			String key = name.substring(index + 1);
			final Friend friend = data.relationship.friendsMap.get(key);
			if (!"".equals(friend.alias)) {
				name = friend.alias;
			}
			fileHandlers.getHeadImage(friend.head, viewHolder.headView, options);
			int section = getSectionForPosition(position);

			if (position == getPositionForSection(section)) {
				viewHolder.tvLetterParent.setVisibility(View.VISIBLE);
				viewHolder.tvLetter.setText(mContent.getSortLetters());
			} else {
				viewHolder.tvLetterParent.setVisibility(View.GONE);
			}

			viewHolder.tvTitle.setText(name2);

			if (invitaFriends.contains(friend.phone)) {
				viewHolder.friendHeadStatus.setVisibility(View.VISIBLE);
			} else {
				viewHolder.friendHeadStatus.setVisibility(View.GONE);
			}

			final ViewHolder viewHolder0 = viewHolder;
			viewHolder.friendInfoLLView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (viewHolder0.friendHeadStatus.getVisibility() == View.GONE) {
						viewHolder0.friendHeadStatus.setVisibility(View.VISIBLE);
						invitaFriends.add(friend.phone);
						handler.post(new Runnable() {

							@Override
							public void run() {
//								if (invitaFriends.size() == 1) {
//									FrameLayout.LayoutParams layoutParams = (android.widget.FrameLayout.LayoutParams) mainContainer.getLayoutParams();
//									layoutParams.bottomMargin = (int) (50 * displayMetrics.density);
//								}
								showAlreayList();
							}
						});

					} else {
						viewHolder0.friendHeadStatus.setVisibility(View.GONE);
						invitaFriends.remove(friend.phone);
						handler.post(new Runnable() {

							@Override
							public void run() {
//								if (invitaFriends.size() == 0) {
//									FrameLayout.LayoutParams layoutParams = (android.widget.FrameLayout.LayoutParams) mainContainer.getLayoutParams();
//									layoutParams.bottomMargin = 0;
//								}
								showAlreayList();
							}
						});
					}
				}
			});
			return view;
		}

		class ViewHolder {
			TextView tvLetter;
			TextView tvTitle;
			LinearLayout tvLetterParent;
			ImageView headView;
			ImageView friendHeadStatus;
			LinearLayout friendInfoLLView;
		}

		public int getSectionForPosition(int position) {
			return list.get(position).getSortLetters().charAt(0);
		}

		public int getPositionForSection(int section) {
			for (int i = 0; i < getCount(); i++) {
				String sortStr = list.get(i).getSortLetters();
				char firstChar = sortStr.toUpperCase().charAt(0);
				if (firstChar == section) {
					return i;
				}
			}
			return -1;
		}

		private String getAlpha(String str) {
			String sortStr = str.trim().substring(0, 1).toUpperCase();
			if (sortStr.matches("[A-Z]")) {
				return sortStr;
			} else {
				return "#";
			}
		}

		@Override
		public Object[] getSections() {
			return null;
		}
	}

	class PinyinComparator implements Comparator<SortModel> {

		public int compare(SortModel o1, SortModel o2) {
			if (o1.getSortLetters().equals("@") || o2.getSortLetters().equals("#")) {
				return -1;
			} else if (o1.getSortLetters().equals("#") || o2.getSortLetters().equals("@")) {
				return 1;
			} else {
				return o1.getSortLetters().compareTo(o2.getSortLetters());
			}
		}
	}

	class SortModel {

		private String name;
		private String sortLetters;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getSortLetters() {
			return sortLetters;
		}

		public void setSortLetters(String sortLetters) {
			this.sortLetters = sortLetters;
		}
	}

}
