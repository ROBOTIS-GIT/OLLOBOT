package com.robotis.ollobotsample.utils;

public class OLLOBOT {
	public static final int ID = 200;
	
	public class Address {
		// Bluetooth 2.0 connection pin number is '0000'.
		// The first packet is ignored after the controller is turned on.
		
		// About the packet. see the below link.
		// http://support.robotis.com/en/techsupport_eng.htm#product/actuator/dynamixel_pro/communication.htm
		
		public static final int GREEN_LED = 79; 							// 0 ~ 1
		public static final int BLUE_LED = 80;								// 0 ~ 1
		public static final int INPUT_POWER_VOLTAGE = 97;
		
		// About axis_value.
		// - X for left, - Y for backward.
		// + X for right, + Y for forward.
		// Speed will be set automatically by the value.
		public static final int CONTROLLER_X_AXIS_VALUE = 112;	// -100 ~ 100
		public static final int CONTROLLER_Y_AXIS_VALUE = 113;	// -100 ~ 100
		
		// About Motor_speed.
		// + for clockwize, - for counterclockwise.
		public static final int PORT_1_MOTOR_SPEED = 136;			// -1024 ~ 1024
		public static final int PORT_2_MOTOR_SPEED = 138;			// -1024 ~ 1024
		
		// About Servo_position value.
		// (+,-)1024 is 360 degree. So (+,-)2048 will spin the wheel twice.
		// 1024/8 is 45 degree.  You can rotate both wheel by 45 degrees or rotate a wheel by 90 degree to turn left or right.		
		public static final int PORT_1_SERVO_POSITION = 156;		// -10240 ~ 10240, 
		public static final int PORT_2_SERVO_POSITION = 158;		// -10240 ~ 10240
		
//		public static final int PORT_1_SERVO_MODE = 128; 
//		public static final int PORT_2_SERVO_MODE = 129; 		
	}
	
	public class Length {
		public static final int GREEN_LED = 1;
		public static final int BLUE_LED = 1;
		public static final int INPUT_POWER_VOLTAGE = 1;
		public static final int CONTROLLER_X_AXIS_VALUE = 1;
		public static final int CONTROLLER_Y_AXIS_VALUE = 1;
		public static final int PORT_1_SERVO_MODE = 1;
		public static final int PORT_2_SERVO_MODE = 1;
		public static final int PORT_1_MOTOR_SPEED = 2;
		public static final int PORT_2_MOTOR_SPEED = 2;
		public static final int PORT_1_SERVO_POSITION = 2;
		public static final int PORT_2_SERVO_POSITION = 2;
	}		
}
