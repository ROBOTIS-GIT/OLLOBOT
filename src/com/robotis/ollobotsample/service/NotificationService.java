package com.robotis.ollobotsample.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

@SuppressLint("NewApi")
public class NotificationService extends NotificationListenerService {

	public static final String ACTION_NOTI_RECEIVED = "com.robotis.ollobotsample.ActionNotificationReceived";
	
//	private static final String TITLE = "android.title";
	private static final String TEXT = "android.text";
//	private static final String TEXT_LINES = "android.textLines";

	private Notification mNotification;
	private Bundle mBundle;
	public static NotificationService mNotificationService;

	@Override
	public void onCreate() {
		mNotificationService = this;
		Log.d("ROBOTIS", "NotificationService onCreate");
	}

	@Override
	public void onNotificationPosted(StatusBarNotification sbn) {
		if (isServiceRunningCheck() /*&& "com.ifttt.ifttt".equals(sbn.getPackageName())*/) {
			mNotification = sbn.getNotification();
			mBundle = mNotification.extras;
//			String title = mBundle.getString(TITLE);
			CharSequence text = mBundle.getCharSequence(TEXT);
//			CharSequence[] textLines = mBundle.getCharSequenceArray(TEXT_LINES);
			
//			Log.i("ROBOTIS", "# packageName : " + sbn.getPackageName());
//			Log.i("ROBOTIS", "# title : " + title);
//			Log.i("ROBOTIS", "# text : " + text);
//			Log.i("ROBOTIS", "# textLines : " + textLines.toString());
			
			if (text != null && text.length() > 0) {
				Intent intent = new Intent(ACTION_NOTI_RECEIVED);
				intent.putExtra("text", text);
				sendBroadcast(intent);
			}
		}
	}

	@Override
	public void onNotificationRemoved(StatusBarNotification sbn) {
		Log.d("ROBOTIS", "NotificationService onNotificationRemoved");

	}

	@Override
	public StatusBarNotification[] getActiveNotifications() {
		return super.getActiveNotifications();
	}

	public boolean isServiceRunningCheck() {
//		ActivityManager manager = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
//		for (RunningServiceInfo service : manager
//				.getRunningServices(Integer.MAX_VALUE)) {
//			if ("com.robotis.ollobotsample.service.NotificationService"
//					.equals(service.service.getClassName())) {
//				return true;
//			}
//		}
//		return false;
		return true;
	}
}