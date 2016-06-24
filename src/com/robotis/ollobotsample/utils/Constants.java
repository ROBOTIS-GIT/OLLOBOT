package com.robotis.ollobotsample.utils;

public class Constants {

	// Service handler message key
	public static final String SERVICE_HANDLER_MSG_KEY_DEVICE_NAME = "device_name";
	public static final String SERVICE_HANDLER_MSG_KEY_DEVICE_ADDRESS = "device_address";
	public static final String SERVICE_HANDLER_MSG_KEY_TOAST = "toast";
    
    // Preference
	public static final String PREFERENCE_NAME = "Pref";
	public static final String PREFERENCE_CONN_INFO_ADDRESS = "device_address";
	public static final String PREFERENCE_CONN_INFO_NAME = "device_name";
	public static final String PREFERENCE_IFTTT_KEY = "ifttt_key";
	public static final String PREFERENCE_IFTTT_EVENT = "ifttt_event";
	
    // Message types sent from Service to Activity
    public static final int MESSAGE_CMD_ERROR_NOT_CONNECTED = -50;
    
    public static final int MESSAGE_BT_STATE_INITIALIZED = 1;
    public static final int MESSAGE_BT_STATE_LISTENING = 2;
    public static final int MESSAGE_BT_STATE_CONNECTING = 3;
    public static final int MESSAGE_BT_STATE_CONNECTED = 4;
    public static final int MESSAGE_BT_STATE_ERROR = 10;
    
    public static final int MESSAGE_STATUS_PACKET = 20;
    
	// Intent request codes
	public static final int REQUEST_CONNECT_DEVICE = 1;
	public static final int REQUEST_ENABLE_BT = 2;
	
}
