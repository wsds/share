package com.open.hot.model;

import java.io.File;

import android.os.Environment;

public class FileHandlers {
	public String tag = "FileHandlers";

	public static FileHandlers fileHandlers;

	public static FileHandlers getInstance() {
		if (fileHandlers == null) {
			fileHandlers = new FileHandlers();
		}
		return fileHandlers;
	}

	public File sdcard;
	public File sdcardFolder;
	public File sdcardImageFolder;

	public FileHandlers() {
		sdcard = Environment.getExternalStorageDirectory();
		if (!sdcard.exists()) {
			// sdcard = Environment.getDataDirectory();
			// sdcard = Environment.getRootDirectory();
			// sdcard = MainActivity.instance.getFilesDir();
			System.out.println(sdcard.getAbsolutePath() + "---Memory");
		}
		sdcardFolder = new File(sdcard, "welinks");
		System.out.println(sdcardFolder.getAbsolutePath() + "---Memory1");
		if (!sdcardFolder.exists()) {
			boolean falg = sdcardFolder.mkdirs();
			System.out.println(sdcardFolder.exists() + "--" + falg + "-Memory3");
		}
		System.out.println(sdcardFolder.exists() + "---Memory2");
		sdcardImageFolder = new File(sdcardFolder, "images");
		if (!sdcardImageFolder.exists()) {
			sdcardImageFolder.mkdirs();
		}
	}
}
