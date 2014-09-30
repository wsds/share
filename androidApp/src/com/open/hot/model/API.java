package com.open.hot.model;

public class API {

	public static String API_DOMAIN = "http://www.we-links.com/";// http://www.we-links.com/

	/**
	 * http://images2.we-links.com/
	 */
	public static String DOMAIN_COMMONIMAGE = "http://images2.we-links.com/";
	/**
	 * http://images3.we-links.com/
	 */
	public static String DOMAIN_OSS_THUMBNAIL = "http://images3.we-links.com/";
	// account
	/**
	 * http://www.we-links.com/api2/account/modify
	 */
	public static String ACCOUNT_MODIFY = API_DOMAIN + "api2/account/modify";
	/**
	 * http://www.we-links.com/api2/account/modifypassword
	 */
	public static String ACCOUNT_MODIFYPASSWORD = API_DOMAIN + "api2/account/modifypassword";
	/**
	 * http://www.we-links.com/api2/account/auth
	 */
	public static String ACCOUNT_AUTH = API_DOMAIN + "api2/account/auth";
	/**
	 * http://www.we-links.com/api2/account/verifycode
	 */
	public static String ACCOUNT_VERIFYCODE = API_DOMAIN + "api2/account/verifycode";
	/**
	 * http://www.we-links.com/api2/account/verifyphone
	 */
	public static String ACCOUNT_VERIFYPHONE = API_DOMAIN + "api2/account/verifyphone";
	/**
	 * http://www.we-links.com/api2/account/get
	 */
	public static String ACCOUNT_GET = API_DOMAIN + "api2/account/get";
	/**
	 * http://www.we-links.com/api2/account/modifylocation
	 */
	public static String ACCOUNT_MODIFYLOCATION = API_DOMAIN + "api2/account/modifylocation";
	// group
	/**
	 * http://www.we-links.com/api2/group/create
	 */
	public static String GROUP_CREATE = API_DOMAIN + "api2/group/create";
	/**
	 * http://www.we-links.com/api2/group/getgroupmembers
	 */
	public static String GROUP_GETGROUPMEMBERS = API_DOMAIN + "api2/group/getgroupmembers";
	/**
	 * http://www.we-links.com/api2/group/addmembers
	 */
	public static String GROUP_ADDMEMBERS = API_DOMAIN + "api2/group/addmembers";
	/**
	 * http://www.we-links.com/api2/group/removemembers
	 */
	public static String GROUP_REMOVEMEMBERS = API_DOMAIN + "api2/group/removemembers";
	/**
	 * http://www.we-links.com/api2/group/modify
	 */
	public static String GROUP_MODIFY = API_DOMAIN + "api2/group/modify";
	/**
	 * http://www.we-links.com/api2/group/modifysequence
	 */
	public static String GROUP_MODIFYGROUPSEQUENCE = API_DOMAIN + "api2/group/modifysequence";
	/**
	 * http://www.we-links.com/api2/group/get
	 */
	public static String GROUP_GET = API_DOMAIN + "api2/group/get";
	/**
	 * http://www.we-links.com/api2/group/getallmembers
	 */
	public static String GROUP_GETALLMEMBERS = API_DOMAIN + "api2/group/getallmembers";

	// share
	/**
	 * http://www.we-links.com/api2/share/sendshare
	 */
	public static String SHARE_SENDSHARE = API_DOMAIN + "api2/share/sendshare";
	/**
	 * http://www.we-links.com/api2/share/getgroupshares
	 */
	public static String SHARE_GETSHARES = API_DOMAIN + "api2/share/getgroupshares";
	/**
	 * http://www.we-links.com/api2/share/addpraise
	 */
	public static String SHARE_ADDPRAISE = API_DOMAIN + "api2/share/addpraise";
	/**
	 * http://www.we-links.com/api2/share/addcomment
	 */
	public static String SHARE_ADDCOMMENT = API_DOMAIN + "api2/share/addcomment";
	/**
	 * http://www.we-links.com/api2/share/delete
	 */
	public static String SHARE_DELETE = API_DOMAIN + "api2/share/delete";
	// message
	/**
	 * http://www.we-links.com/api2/message/send
	 */
	public static String MESSAGE_SEND = API_DOMAIN + "api2/message/send";
	/**
	 * http://www.we-links.com/api2/message/get
	 */
	public static String MESSAGE_GET = API_DOMAIN + "api2/message/get";

	// session
	/**
	 * http://www.we-links.com/api2/session/event
	 */
	public static String SESSION_EVENT = API_DOMAIN + "api2/session/event";

	// relation
	/**
	 * http://www.we-links.com/api2/relation/modifyalias
	 */
	public static String RELATION_MODIFYALIAS = API_DOMAIN + "api2/relation/modifyalias";
	/**
	 * http://www.we-links.com/api2/relation/deletefriend
	 */
	public static String RELATION_DELETEFRIEND = API_DOMAIN + "api2/relation/deletefriend";
	/**
	 * http://www.we-links.com/api2/relation/addfriend
	 */
	public static String RELATION_ADDFRIEND = API_DOMAIN + "api2/relation/addfriend";
	/**
	 * http://www.we-links.com/api2/relation/modifysequence
	 */
	public static String RELATION_MODIFYCIRCLESEQUENCE = API_DOMAIN + "api2/relation/modifysequence";

	public static String RELATION_GETINTIMATEFRIENDS = API_DOMAIN + "api2/relation/intimatefriends";
	/**
	 * http://www.we-links.com/api2/relation/getaskfriends
	 */
	public static String RELATION_GETASKFRIENDS = API_DOMAIN + "api2/relation/getaskfriends";
	/**
	 * http://www.we-links.com/api2/relation/addfriendagree
	 */
	public static String RELATION_ADDFRIENDAGREE = API_DOMAIN + "api2/relation/addfriendagree";

	// circle
	/**
	 * http://www.we-links.com/api2/circle/modify
	 */
	public static String CIRCLE_MODIFY = API_DOMAIN + "api2/circle/modify";
	/**
	 * http://www.we-links.com/api2/circle/delete
	 */
	public static String CIRCLE_DELETE = API_DOMAIN + "api2/circle/delete";
	/**
	 * http://www.we-links.com/api2/circle/addcircle
	 */
	public static String CIRCLE_ADDCIRCLE = API_DOMAIN + "api2/circle/addcircle";

	// image

	/**
	 * http://www.we-links.com/image/checkfileexist
	 */
	public static String IMAGE_CHECKFILEEXIST = API_DOMAIN + "image/checkfileexist";
	/**
	 * http://www.we-links.com/image/uploadfilename
	 */
	public static String IMAGE_UPLOADFILENAME = API_DOMAIN + "image/uploadfilename";
	// lbs
	public static String LBS_DATA_CREATE = "http://yuntuapi.amap.com/datamanage/data/create";
	public static String LBS_DATA_UPDATA = "http://yuntuapi.amap.com/datamanage/data/update";
	public static String LBS_DATA_SEARCH = "http://yuntuapi.amap.com/datamanage/data/list";
	public static String LBS_DATA_DELETE = "http://yuntuapi.amap.com/datamanage/data/delete";

}
