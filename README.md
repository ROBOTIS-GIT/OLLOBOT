#Example

// bluetooth 2.0, connection pin number '0000'

// stop

Dynamixel.writeDWordPacket(200, 200, 0)


// turn left 90 degree (1024/8 = 45 degree, PORT_1_SERVO_POSITION = 156)

Dynamixel.writeDWordPacket(200, 156, (1024/8) + ((1024/8) << 16))


// turn right 90 degree (1024/8 = 45 degree)

Dynamixel.writeDWordPacket(200, 156, -(1024/8) + (-(1024/8) << 16))


// keep moving

// from -100 to 100, - value for left(x) and back(y), + value for right(x) and forward(y)

// CONTROLLER_X_AXIS_VALUE = 112

Dynamixel.writeWordPacket(200, 112, x + (y << 8))
