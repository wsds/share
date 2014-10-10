package com.open.hot.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Data {

	public static Data data;

	public static Data getInstance() {
		if (data == null) {
			data = new Data();
		}
		return data;
	}

	public TempData tempData = new TempData();

	public class TempData {
	}

	public LocalStatus localStatus = new LocalStatus();

	public class LocalStatus {
	}

	public class Hot {

		public String id;
		public String type;// "account" | "group" | "hot"

		public Settings settings;

		public class Settings {

		}

		public Information information;

		public class Information {
			public String title;
			public String createTime;
			public String lastModifyTime;
			public String permission;// none|me|group|public
			public String subPost;// none|fold|show
			public String background;
		}

		public List<String> children = new ArrayList<String>();

		public Map<String, String> content = new HashMap<String, String>();

		public Account account;

		public class Account {
			public String nickName = "";
			public String mainBusiness = "";
			public String head = "Head";
			public List<String> groups = new ArrayList<String>();

		}

		public Group group;

		public class Group {

			public List<String> members = new ArrayList<String>();
		}

	}

	public Hot me;
	public Map<String, Hot> hotMap = new HashMap<String, Hot>();

}
