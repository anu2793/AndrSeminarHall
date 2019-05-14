package vn.hanelsoft.utils;

import android.content.Context;

/**
 * @author lamnd
 * 
 */
public class HSSLog {
	public static String TAG = "HSSLog";

	/**
	 * Set <b>TAG</b> of Logcat is main Package </br> <b>default</b> is
	 * <i>HSSLog</i>
	 */
	public static void init(Context context) {
		TAG = context.getPackageName();
	}

	public static void i(Object message) {
		android.util.Log.i(TAG, message.toString());
	}

	public static void i(String tag, Object message) {
		android.util.Log.i(tag, message.toString());
	}

	public static void e(Object message) {
		android.util.Log.e(TAG, message.toString());
	}

	public static void e(String tag, Object message) {
		android.util.Log.e(tag, message.toString());
	}

	public static void d(Object message) {
		android.util.Log.d(TAG, message.toString());
	}

	public static void d(String tag, Object message) {
		android.util.Log.d(tag, message.toString());
	}
	
	public static void w(Object message) {
		android.util.Log.w(TAG, message.toString());
	}

	public static void w(String tag, Object message) {
		android.util.Log.w(tag, message.toString());
	}
	
	public static void v(Object message) {
		android.util.Log.d(TAG, message.toString());
	}

	public static void v(String tag, Object message) {
		android.util.Log.d(tag, message.toString());
	}
}
