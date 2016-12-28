package com.robotis.ollobotsample.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;

// AccessibilityService get title only. can't get message on API level 18+. 
// Use NotificationListenerService if API level is higher than 18. 
public class OllobotAccessibilityService extends AccessibilityService {
	
	public static final String ACTION_ACCESSIBILITY = "com.robotis.ollobotsample.ACTION_ACCESSIBILITY";
	public static final String EXTRA_MSG = "msg";
	
    private String getEventType(AccessibilityEvent event) {
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                return "TYPE_NOTIFICATION_STATE_CHANGED";
//            case AccessibilityEvent.TYPE_VIEW_CLICKED:
//                return "TYPE_VIEW_CLICKED";
//            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
//                return "TYPE_VIEW_FOCUSED";
//            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
//                return "TYPE_VIEW_LONG_CLICKED";
//            case AccessibilityEvent.TYPE_VIEW_SELECTED:
//                return "TYPE_VIEW_SELECTED";
//            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
//                return "TYPE_WINDOW_STATE_CHANGED";
//            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
//                return "TYPE_VIEW_TEXT_CHANGED";
        }
        return null;
    }

    private String getEventText(AccessibilityEvent event) {
        StringBuilder sb = new StringBuilder();
        for (CharSequence s : event.getText()) {
            sb.append(s);
        }
        return sb.toString();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    	if (getEventType(event) != null) {
	    	String eventText = getEventText(event);
//	    	Log.i("ROBOTIS", "# onAccessibilityEvent : [" + eventText + "]");
//	    	Log.i("ROBOTIS", "# onAccessibilityEvent : [" + event + "]");
	    	if (eventText != null && eventText.trim().length() > 0) {
	    		int startPos = eventText.indexOf(":");
	    		if (startPos >= 0) {
		    		String msg = eventText.substring(startPos + 1, eventText.length()).trim();
		    		
			        Intent intent = new Intent(ACTION_ACCESSIBILITY);
		    		intent.putExtra(EXTRA_MSG, msg.toLowerCase());
		    		sendBroadcast(intent);
	    		}
	    	}
    	}
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.flags = AccessibilityServiceInfo.DEFAULT;
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        setServiceInfo(info);
        
    	/*<accessibility-service
        xmlns:android="http://schemas.android.com/apk/res/android"
        android:accessibilityEventTypes="typeNotificationStateChanged"
        android:accessibilityFeedbackType="feedbackSpoken"
        android:notificationTimeout="100"
        android:accessibilityFlags="flagDefault"
        android:canRetrieveWindowContent="true" />*/
    }
}