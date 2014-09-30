package com.open.hot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.open.hot.model.Data;
import com.open.hot.model.Parser;

public class LaunchActivity extends Activity {

	public boolean isDebug = false;
	public Data data = Data.getInstance();

	String tag = "MainActivity";
	public Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		Log.d(tag, "hello world!");

		Parser parser = Parser.getInstance();
		parser.initialize(context);

		data = Data.getInstance();

		startActivity(new Intent(LaunchActivity.this, HotActivity.class));
		LaunchActivity.this.finish();
		// parser.parse();
		// parser.saveDataToLocal();
		// parser.readSdFileToData();

		// getLocalInformation();
		// if (isDebug) {
		// startActivity(new Intent(LaunchActivity.this, TestListActivity.class));
		// } else {
		// startActivity(new Intent(LaunchActivity.this, LoginActivity.class));
		// }

	}

	public void getLocalInformation() {
		// LocalConfig localConfig = data.userInformation.localConfig;
		//
		// TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		// localConfig.deviceid = telephonyManager.getDeviceId();
		// localConfig.line1Number = telephonyManager.getLine1Number();
		// localConfig.imei = telephonyManager.getSimSerialNumber();
		// localConfig.imsi = telephonyManager.getSubscriberId();
	}
}
