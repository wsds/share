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

	public Hot me;

	public class Hot {

		public String id;
		public String type;// "account" | "grounp" | "hot"

		public Settings settings;

		public class Settings {

		}

		public Information information;

		class Information {
			String title;
			String createTime;
			String lastModifyTime;
		}

		public List<Hot> children = new ArrayList<Hot>();

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

	public Map<String, Hot> hotMap = new HashMap<String, Hot>();

}
