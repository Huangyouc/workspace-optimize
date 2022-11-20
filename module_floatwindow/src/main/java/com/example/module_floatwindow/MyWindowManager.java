package com.example.module_floatwindow;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MyWindowManager {
	private static Context mContext;
	/** * 小悬浮窗View的实例 */
	private static FloatWindowSmallView smallWindow;
	/** * 大悬浮窗View的实例 */
	private static FloatWindowBigView bigWindow;
	/** * 小悬浮窗View的参数 */
	private static LayoutParams smallWindowParams;
	/** * 大悬浮窗View的参数 */
	private static LayoutParams bigWindowParams;
	/** * 用于控制在屏幕上添加或移除悬浮窗 */
	private static WindowManager mWindowManager;
	/** * 用于获取手机可用内存 */
	private static ActivityManager mActivityManager;

	/** * 用于在线程中创建或移除悬浮窗。 */
	private static Handler handler = new Handler();
	/** * 定时器，定时进行检测当前应该创建还是移除悬浮窗。 */
	private static Timer timer;

	/** * 创建一个小悬浮窗。初始位置为屏幕的右部中间位置。 * * @param context * 必须为应用程序的Context. */
	public static void createSmallWindow(Context context) {
		WindowManager windowManager = getWindowManager(context);
		int screenWidth = windowManager.getDefaultDisplay().getWidth();
		int screenHeight = windowManager.getDefaultDisplay().getHeight();
		if (smallWindow == null) {
			smallWindow = new FloatWindowSmallView(context);
			if (smallWindowParams == null) {
				smallWindowParams = new LayoutParams();
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
					smallWindowParams.type = LayoutParams.TYPE_APPLICATION_OVERLAY;
				} else {
					smallWindowParams.type = LayoutParams.TYPE_PHONE;
				}
				smallWindowParams.format = PixelFormat.RGBA_8888;
				smallWindowParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
						| LayoutParams.FLAG_NOT_FOCUSABLE;
				smallWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
				smallWindowParams.width = FloatWindowSmallView.viewWidth;
				smallWindowParams.height = FloatWindowSmallView.viewHeight;
				smallWindowParams.x = screenWidth;
				smallWindowParams.y = screenHeight / 2;
			}
			smallWindow.setParams(smallWindowParams);
			try {
				windowManager.addView(smallWindow, smallWindowParams);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/** * 将小悬浮窗从屏幕上移除。 * * @param context * 必须为应用程序的Context. */
	public static void removeSmallWindow(Context context) {
		if (smallWindow != null) {
			try {
				WindowManager windowManager = getWindowManager(context);
				windowManager.removeView(smallWindow);
				smallWindow = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/** * 创建一个大悬浮窗。位置为屏幕正中间。 * * @param context * 必须为应用程序的Context. */
	public static void createBigWindow(Context context) {
		WindowManager windowManager = getWindowManager(context);
		int screenWidth = windowManager.getDefaultDisplay().getWidth();
		int screenHeight = windowManager.getDefaultDisplay().getHeight();
		if (bigWindow == null) {
			bigWindow = new FloatWindowBigView(context);
			if (bigWindowParams == null) {
				bigWindowParams = new LayoutParams();
				bigWindowParams.x = screenWidth / 2
						- FloatWindowBigView.viewWidth / 2;
				bigWindowParams.y = screenHeight / 4
						- FloatWindowBigView.viewHeight / 2;
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
					bigWindowParams.type = LayoutParams.TYPE_APPLICATION_OVERLAY;
				} else {
					bigWindowParams.type = LayoutParams.TYPE_PHONE;
				}
				bigWindowParams.format = PixelFormat.RGBA_8888;
				bigWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
				bigWindowParams.width = FloatWindowBigView.viewWidth;
				bigWindowParams.height = FloatWindowBigView.viewHeight;
			}
			windowManager.addView(bigWindow, bigWindowParams);
		}
	}

	/** * 将大悬浮窗从屏幕上移除。 * * @param context * 必须为应用程序的Context. */
	public static void removeBigWindow(Context context) {
		if (bigWindow != null) {
			try {
				WindowManager windowManager = getWindowManager(context);
				windowManager.removeView(bigWindow);
				bigWindow = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/** * 是否有悬浮窗(包括小悬浮窗和大悬浮窗)显示在屏幕上。 * * @return 有悬浮窗显示在桌面上返回true，没有的话返回false。 */
	public static boolean isWindowShowing() {
		return smallWindow != null || bigWindow != null;
	}

	/**
	 * * 如果WindowManager还未创建，则创建一个新的WindowManager返回。否则返回当前已创建的WindowManager。 * * @param
	 * context * 必须为应用程序的Context. * @return WindowManager的实例，用于控制在屏幕上添加或移除悬浮窗。
	 */
	private static WindowManager getWindowManager(Context context) {
		if (mWindowManager == null) {
			mWindowManager = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
		}
		return mWindowManager;
	}

	/**
	 * * 如果ActivityManager还未创建，则创建一个新的ActivityManager返回。
	 * 否则返回当前已创建的ActivityManager。 * * @param context * 可传入应用程序上下文。 * @return
	 * ActivityManager的实例，用于获取手机可用内存。
	 */
	private static ActivityManager getActivityManager(Context context) {
		if (mActivityManager == null) {
			mActivityManager = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
		}
		return mActivityManager;
	}

	private static class RefreshTask extends TimerTask {
		@Override
		public void run() {
			// 当前界面是桌面
			if (isHome()) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						MyWindowManager
								.removeSmallWindow(mContext.getApplicationContext());
						MyWindowManager
								.removeBigWindow(mContext.getApplicationContext());
					}
				});
			} else if (isAppOnForeground()) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						MyWindowManager
								.createSmallWindow(mContext.getApplicationContext());
					}
				});

			} else {
				handler.post(new Runnable() {
					@Override
					public void run() {
						MyWindowManager
								.removeSmallWindow(mContext.getApplicationContext());
						MyWindowManager
								.removeBigWindow(mContext.getApplicationContext());
					}
				});
			}
		}
	}

	public static void startThread(Context context){
		mContext = context;
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 开启定时器，每隔0.5秒刷新一次
				if (timer == null) {
					timer = new Timer();
					timer.scheduleAtFixedRate(new RefreshTask(), 0, 500);
				}
			}
		}).start();
	}

	/** * 判断当前界面是否是桌面 */
	private static boolean isHome() {
		// return true;
		ActivityManager mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
		if(rti.isEmpty()){
			return  true;
		}
		return getHomes().contains(rti.get(0).topActivity.getPackageName());
	}

	/** * 获得属于桌面的应用的应用包名称 * * @return 返回包含所有包名的字符串列表 */
	private static List<String> getHomes() {
		try {
			List<String> names = new ArrayList<String>();
			PackageManager packageManager = mContext.getPackageManager();
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(
					intent, PackageManager.MATCH_DEFAULT_ONLY);
			for (ResolveInfo ri : resolveInfo) {
				names.add(ri.activityInfo.packageName);
			}
			return names;
		}catch (Exception e){
			e.printStackTrace();
		}

		return new ArrayList<>();
	}

	/**
	 * 程序是否在前台运行
	 *
	 * @return
	 */
	public static boolean isAppOnForeground() {
		// Returns a list of application processes that are running on the
		// device
		ActivityManager mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> appProcesses = mActivityManager
				.getRunningAppProcesses();
		if (appProcesses == null)
			return false;
		PackageInfo pinfo;
		try {
			pinfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
			for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
				// The name of the process that this object is associated with.
				if (appProcess.processName.equals(pinfo.packageName)
						&& appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
					return true;
				}
			}
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}
}
