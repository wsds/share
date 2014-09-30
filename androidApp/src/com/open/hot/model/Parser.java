package com.open.hot.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.open.hot.model.Data.Hot;
import com.open.hot.utils.StreamParser;
import com.open.lib.MyLog;

public class Parser {
	String tag = "Parser";
	public MyLog log = new MyLog(tag, false);

	public static Parser parser;

	public static Parser getInstance() {
		if (parser == null) {
			parser = new Parser();
		}
		return parser;

	}

	public Context context;
	public Gson gson;

	public void initialize(Context context) {
		this.context = context;
		if (gson == null) {
			this.gson = new Gson();
		}
	}

	public Data parse() {
		Data data = Data.getInstance();
		if (gson == null) {
			this.gson = new Gson();
		}
		try {
			String hotMapStr = getFromAssets("hotMap.js");
			data.hotMap = gson.fromJson(hotMapStr, new TypeToken<HashMap<String, Hot>>() {
			}.getType());
			String meStr = getFromAssets("me.js");
			data.me = gson.fromJson(meStr, Hot.class);
		} catch (Exception e) {
			log.e(tag, "**************Gson parse error!**************");
			data = null;
		}

		return data;
	}

	public String getFromAssets(String fileName) {
		String result = null;
		try {
			InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
			BufferedReader bufReader = new BufferedReader(inputReader);
			String line = "";
			result = "";
			while ((line = bufReader.readLine()) != null) {
				result += line;
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			result = null;
		}
		return result;
	}

	public void saveToSD(File forder, String fileName, String content) {
		try {
			File file = new File(forder, fileName);
			FileOutputStream userInformationFileOutputStream = new FileOutputStream(file);
			byte[] buffer = content.getBytes();
			userInformationFileOutputStream.write(buffer);
			userInformationFileOutputStream.flush();
			userInformationFileOutputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveToUserForder(String phone, String fileName, String content) {
		File sdFile = Environment.getExternalStorageDirectory();
		File userForder = new File(sdFile, "welinks/" + phone);

		if (!userForder.exists()) {
			userForder.mkdirs();
		}

		saveToSD(userForder, fileName, content);
	}

	public void saveToRootForder(String fileName, String content) {
		File sdFile = Environment.getExternalStorageDirectory();
		File rootForder = new File(sdFile, "welinks/");

		if (!rootForder.exists()) {
			rootForder.mkdirs();
		}

		saveToSD(rootForder, fileName, content);
	}

	public String getFromSD(File forder, String fileName) {

		String result = null;
		try {
			File file = new File(forder, fileName);
			if (!file.exists()) {
				return null;
			}
			FileInputStream fileInputStream = new FileInputStream(file);
			byte[] bytes = StreamParser.parseToByteArray(fileInputStream);
			result = new String(bytes);

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			result = null;
		}
		return result;
	}

	public String getFromUserForder(String phone, String fileName) {
		String result = null;
		File sdFile = Environment.getExternalStorageDirectory();
		File userForder = new File(sdFile, "welinks/" + phone);

		if (!userForder.exists()) {
			userForder.mkdirs();
		}

		result = getFromSD(userForder, fileName);

		if (result == null) {
			result = getFromAssets(fileName);
		}

		return result;
	}

	public String getFromRootForder(String fileName) {
		String result = null;
		File sdFile = Environment.getExternalStorageDirectory();
		File rootForder = new File(sdFile, "welinks/");

		result = getFromSD(rootForder, fileName);

		if (result == null) {
			result = getFromAssets(fileName);
		}

		return result;
	}

	public Data check() {
		Data data = Data.getInstance();
		if (gson == null) {
			this.gson = new Gson();
		}

		return data;
	}

	public List<String> checkKeyValue(List<String> list, Map map) {
		List<String> errorList = new ArrayList<String>();
		for (int i = 0; i < list.size(); i++) {
			String key = list.get(i);
			Object object = map.get(key);
			if (object == null) {
				errorList.add(key);
			}
		}
		if (errorList.size() > 0) {
			list.removeAll(errorList);
		}
		return list;
	}

	public void deleteFile(String phone, String fileName) {
		File sdFile = Environment.getExternalStorageDirectory();
		File userForder = new File(sdFile, "welinks/" + phone);

		if (!userForder.exists()) {
			userForder.mkdirs();
		}
		File file = new File(userForder, fileName);
		if (file.exists()) {
			file.delete();
		}
	}

	public void save() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				log.e(tag, "**************saveDataToSD!**************");
				saveDataToSD();
			}
		}).start();
	}

	public void saveDataToSD() {
		Data data = Data.getInstance();

	}

}
