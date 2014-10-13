package com.open.hot.view;
import com.open.lib.viewbody.ListBody1;



public class SectionBody extends ListBody1.MyListItemBody{
	
	SectionBody(ListBody1 listBody) {
		listBody.super();
	}
	
	public class  Status {
		public int NORMAL = 0, SCALED = 1, FOLD = 2;
		public int state = NORMAL;
	}

	public Status status = new Status();

	public String type;// "account" | "group" | "hot"
	
	public String contentStr;
}
