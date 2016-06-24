/*
 * Copyright (C) 2016 ROBOTIS OLLOBOT Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.robotis.ollobotsample.service;

import java.nio.ByteBuffer;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.robotis.ollobotsample.bluetooth.BluetoothManager;
import com.robotis.ollobotsample.bluetooth.ConnectionInfo;
import com.robotis.ollobotsample.bluetooth.TransactionBuilder;
import com.robotis.ollobotsample.bluetooth.TransactionReceiver;
import com.robotis.ollobotsample.utils.Constants;
import com.robotis.ollobotsample.utils.Dynamixel;
import com.robotis.ollobotsample.utils.Dynamixel.PKT;


public class BTConnectionService extends Service {
	private static final String TAG = "ROBOTIS.BTConnectionService";
	
	// Context, System
	private Context mContext = null;
	private static Handler mActivityHandler = null;
	private ServiceHandler mServiceHandler = new ServiceHandler();
	private final IBinder mBinder = new ServiceBinder();
	
	// Bluetooth
	private BluetoothAdapter mBluetoothAdapter = null;		// local Bluetooth adapter managed by Android Framework
	private BluetoothManager mBtManager = null;
	private ConnectionInfo mConnectionInfo = null;		// Remembers connection info when BT connection is made 
	
	private TransactionBuilder mTransactionBuilder = null;
	private TransactionReceiver mTransactionReceiver = null;
	
	private ByteBuffer mReceivedBuffer = null;
	
	/*****************************************************
	 *	Overrided methods
	 ******************************************************/
	@Override
	public void onCreate() {
		Log.d(TAG, "# Service - onCreate() starts here");
		
		mContext = getApplicationContext();
		initialize();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "# Service - onStartCommand() starts here");
		
		// If service returns START_STICKY, android restarts service automatically after forced close.
		// At this time, onStartCommand() method in service must handle null intent.
		return Service.START_STICKY;
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig){
		// This prevents reload after configuration changes
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "# Service - onBind()");
		return mBinder;
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(TAG, "# Service - onUnbind()");
		return true;
	}
	
	@Override
	public void onDestroy() {
		Log.d(TAG, "# Service - onDestroy()");
		finalizeService();
	}
	
	@Override
	public void onLowMemory (){
		Log.d(TAG, "# Service - onLowMemory()");
		// onDestroy is not always called when applications are finished by Android system.
		finalizeService();
	}

	
	/*****************************************************
	 *	Private methods
	 ******************************************************/
	private void initialize() {
		Log.d(TAG, "# Service : initialize ---");
		
		// Get connection info instance
		mConnectionInfo = ConnectionInfo.getInstance(mContext);
		
		// Get local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
			return;
		}
		
		if (!mBluetoothAdapter.isEnabled()) {
			// BT is not on, need to turn on manually.
			// Activity will do this.
		} else {
			if(mBtManager == null) {
				setupBT();
			}
		}
		
		mReceivedBuffer = ByteBuffer.allocate(256);
		mReceivedBuffer.clear();
	}
	
	/**
	 * Send message to device.
	 * @param message		message to send
	 */
	private void sendMessageToDevice(String message) {
		if(message == null || message.length() < 1)
			return;
		
		TransactionBuilder.Transaction transaction = mTransactionBuilder.makeTransaction();
		transaction.begin();
		transaction.setMessage(message);
		transaction.settingFinished();
		transaction.sendTransaction();
	}
	
	private void sendMessageToDevice(byte[] message) {
		if(message == null || message.length < 1)
			return;
		
		TransactionBuilder.Transaction transaction = mTransactionBuilder.makeTransaction();
		transaction.begin();
		transaction.setMessage(message);
		transaction.settingFinished();
		transaction.sendTransaction();
	}
	
	
	/*****************************************************
	 *	Public methods
	 ******************************************************/
	public void finalizeService() {
		Log.d(TAG, "# Service : finalize ---");
		
		// Stop the bluetooth session
		mBluetoothAdapter = null;
		if (mBtManager != null)
			mBtManager.stop();
		mBtManager = null;
	}
	
	public void disconnectDevice() {
		if (mBtManager != null)
			mBtManager.stop();
	}
	
	/**
	 * Setting up bluetooth connection
	 * @param h
	 */
	public void setupService(Handler h) {
		mActivityHandler = h;
		
		// Double check BT manager instance
		if(mBtManager == null)
			setupBT();
		
		// Initialize transaction builder & receiver
		if(mTransactionBuilder == null)
			mTransactionBuilder = new TransactionBuilder(mBtManager, mActivityHandler);
		if(mTransactionReceiver == null)
			mTransactionReceiver = new TransactionReceiver(mActivityHandler);
		
		// If ConnectionInfo holds previous connection info,
		// try to connect using it.
		if(mConnectionInfo.getDeviceAddress() != null && mConnectionInfo.getDeviceName() != null) {
			connectDevice(mConnectionInfo.getDeviceAddress());
		} 
		// or wait in listening mode
		else {
			if (mBtManager.getState() == BluetoothManager.STATE_NONE) {
				// Start the bluetooth service
				mBtManager.start();
			}
		}
	}
	
    /**
     * Setup and initialize BT manager
     */
	public void setupBT() {
		Log.d(TAG, "Service - setupBT()");

        // Initialize the BluetoothManager to perform bluetooth connections
        if(mBtManager == null)
        	mBtManager = new BluetoothManager(this, mServiceHandler);
    }
	
    /**
     * Check bluetooth is enabled or not.
     */
	public boolean isBluetoothEnabled() {
		if(mBluetoothAdapter==null) {
			Log.e(TAG, "# Service - cannot find bluetooth adapter. Restart app.");
			return false;
		}
		return mBluetoothAdapter.isEnabled();
	}
	
    /**
     * Initiate a connection to a remote device.
     * @param address  Device's MAC address to connect
     */
	public void connectDevice(String address) {
		Log.d(TAG, "Service - 1.connect to " + address + " / " + mBluetoothAdapter);
		
		// Get the BluetoothDevice object
//		if (mBluetoothAdapter == null) {
//			initialize();
//		}
		
		if(mBluetoothAdapter != null) {
			BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
			
			if(device != null && mBtManager != null) {
				mBtManager.connect(device);
			}
		}
	}
	
    /**
     * Connect to a remote device.
     * @param device  The BluetoothDevice to connect
     */
	public void connectDevice(BluetoothDevice device) {
		Log.d(TAG, "Service - 2.connect to " + device.getAddress());
//		if (mBtManager == null) {
//			initialize();
//		}
		
		if(device != null && mBtManager != null) {
			mBtManager.connect(device);
		}
	}

	/**
	 * Get connected device name
	 */
	public String getDeviceName() {
		return mConnectionInfo.getDeviceName();
	}
	
	public int getBtStatus() {
		if (mBtManager != null) {
			return mBtManager.getState();
		}
		return -1;
	}

	/**
	 * Send message to remote device using Bluetooth
	 */
	public void sendMessageToRemote(String message) {
		sendMessageToDevice(message);
	}
	
	public void sendMessageToRemote(byte[] message) {
		sendMessageToDevice(message);
	}
	
	/*****************************************************
	 *	Handler, Listener, Timer, Sub classes
	 ******************************************************/
	public class ServiceBinder extends Binder {
		public BTConnectionService getService() {
			return BTConnectionService.this;
		}
	}
	
    /**
     * Receives messages from bluetooth manager
     */
	class ServiceHandler extends Handler
	{
		@Override
		public void handleMessage(Message msg) {
			
			switch(msg.what) {
			// Bluetooth state changed
			case BluetoothManager.MESSAGE_STATE_CHANGE:
				// Bluetooth state Changed
				Log.d(TAG, "Service - MESSAGE_STATE_CHANGE: " + msg.arg1);
				
				switch (msg.arg1) {
				case BluetoothManager.STATE_NONE:
					mActivityHandler.obtainMessage(Constants.MESSAGE_BT_STATE_INITIALIZED).sendToTarget();
					break;
					
				case BluetoothManager.STATE_LISTEN:
					mActivityHandler.obtainMessage(Constants.MESSAGE_BT_STATE_LISTENING).sendToTarget();
					break;
					
				case BluetoothManager.STATE_CONNECTING:
					mActivityHandler.obtainMessage(Constants.MESSAGE_BT_STATE_CONNECTING).sendToTarget();
					break;
					
				case BluetoothManager.STATE_CONNECTED:
					mActivityHandler.obtainMessage(Constants.MESSAGE_BT_STATE_CONNECTED).sendToTarget();
					break;
				}
				break;

			// If you want to send data to remote
			case BluetoothManager.MESSAGE_WRITE:
				Log.d(TAG, "Service - MESSAGE_WRITE: ");
				break;

			// Received packets from remote
			case BluetoothManager.MESSAGE_READ:
//				Log.d(TAG, "Service - MESSAGE_READ: ");
				
				byte[] readBuf = (byte[]) msg.obj;
				int readCount = msg.arg1;
//				Dynamixel.log(readBuf);
				
				try {
					for (int i = 0; i < readCount; i++) {
						mReceivedBuffer.put(readBuf[i]);
					}
				} catch (Exception e) {
					mReceivedBuffer.clear();
				}
				
				int startPos = -1;
				try {
					if (mReceivedBuffer.position() >= Dynamixel.STATUS_PACKET_MIN_LENGTH) {
						for (int i = 0; i < mReceivedBuffer.limit(); i++) {
							// header check
							if (mReceivedBuffer.get(i + PKT.HEADER0.idx) == (byte) 0xFF 
									&& mReceivedBuffer.get(i + PKT.HEADER1.idx) == (byte) 0xFF 
									&& mReceivedBuffer.get(i + PKT.HEADER2.idx) == (byte) 0xFD) {
								startPos = i;
								mReceivedBuffer.position(i);
								break;
							}
						}

						// Status packet for WRITE returns 4. 
						// [instruction(0x55)] [Error] [CRC_L] [CRC_H]
						
						// Status packet for READ returns over 4. 
						// [instruction(0x55)] [Error] [param1] ... [paramN] [CRC_L] [CRC_H]. 
						// params are value that arranged low to high byte.
						// Use "Dynamixel.makeWord(param1, param2)" to get int value from 2 bytes.
						int length = Dynamixel.makeWord(mReceivedBuffer.get(startPos + PKT.LENGTH_L.idx), mReceivedBuffer.get(startPos + PKT.LENGTH_H.idx)); 
						
						if (startPos >= 0) {
							byte[] statusPacket = new byte[PKT.LENGTH_H.idx + 1 + length];
							try {
								mReceivedBuffer.get(statusPacket);
							} catch (Exception e) {
								e.printStackTrace();
							}
							mReceivedBuffer.clear();
							
//							Dynamixel.log(statusPacket);
							mActivityHandler.obtainMessage(Constants.MESSAGE_STATUS_PACKET, statusPacket).sendToTarget();
						}
					}
				} catch (IndexOutOfBoundsException e) {
					e.printStackTrace();
				}
				
				// construct commands from valid bytes in the buffer
				if(mTransactionReceiver != null) {
					// TODO: Do something with incoming data					
					//mTransactionReceiver.setByteArray(readBuf, readCount);
					//Object obj = mTransactionReceiver.getObject();
				}
				break;
				
			case BluetoothManager.MESSAGE_DEVICE_NAME:
				Log.d(TAG, "Service - MESSAGE_DEVICE_NAME: ");
				
				// save connected device's name and notify using toast
				String deviceAddress = msg.getData().getString(Constants.SERVICE_HANDLER_MSG_KEY_DEVICE_ADDRESS);
				String deviceName = msg.getData().getString(Constants.SERVICE_HANDLER_MSG_KEY_DEVICE_NAME);
				
				Log.i("ROBOTIS", "# " + deviceName + " / " + deviceAddress);
				if(deviceName != null && deviceAddress != null) {
					// Remember device's address and name
					mConnectionInfo.setDeviceAddress(deviceAddress);
					mConnectionInfo.setDeviceName(deviceName);
					
					Toast.makeText(getApplicationContext(), 
							"Connected to " + deviceName, Toast.LENGTH_SHORT).show();
				}
				break;
				
			case BluetoothManager.MESSAGE_TOAST:
				Log.d(TAG, "Service - MESSAGE_TOAST: ");
				
				Toast.makeText(getApplicationContext(), 
						msg.getData().getString(Constants.SERVICE_HANDLER_MSG_KEY_TOAST), 
						Toast.LENGTH_SHORT).show();
				break;
				
			}	// End of switch(msg.what)
			
			super.handleMessage(msg);
		}
	}	// End of class MainHandler
}
