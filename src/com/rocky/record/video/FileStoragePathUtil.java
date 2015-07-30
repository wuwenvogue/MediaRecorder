package com.rocky.record.video;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

/**
 * 
 * @author Rocky
 *
 * @Time 2015年7月30日 下午3:45:23
 *
 * @description
 */
public class FileStoragePathUtil {
	private static final String TAG = "FileStoragePathUtil";
	private static final File parentPath = Environment
			.getExternalStorageDirectory();
	private static String storagePath = "";

	/**
	 * 初始化保存路径
	 * 
	 * @return
	 */
	private static String initPath() {
		if (storagePath.equals("")) {
			storagePath = parentPath.getAbsolutePath();
			File f = new File(storagePath);
			if (!f.exists()) {
				f.mkdir();
			}
		}
		return storagePath;
	}

	/**
	 * 保存Bitmap到sdcard
	 * 
	 * @param b
	 */
	public static void saveBitmap(Bitmap b) {

		String path = initPath();
		long dataTake = System.currentTimeMillis();
		String jpegName = path + "/" + dataTake + "rocky_demo.jpg";
		try {
			FileOutputStream fout = new FileOutputStream(jpegName);
			BufferedOutputStream bos = new BufferedOutputStream(fout);
			b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
			Log.i(TAG, "save Bitmap success");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i(TAG, "save Bitmap failed");
			e.printStackTrace();
		}

	}

}