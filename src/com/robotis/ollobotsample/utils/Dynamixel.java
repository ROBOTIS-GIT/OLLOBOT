package com.robotis.ollobotsample.utils;

import java.util.Arrays;

import android.util.Log;

public class Dynamixel
{
	public static final int STATUS_PACKET_MIN_LENGTH = 11;
	
	public static enum PKT {
		HEADER0(0),
		HEADER1(1),
		HEADER2(2),
		RESERVED(3),
		ID(4),
		LENGTH_L(5),
		LENGTH_H(6),
		INSTRUCTION(7),
		PARAMETER(8);
		
		public int idx;
		PKT(int value) {
			this.idx = value;
		}
	};

	public static final int INST_READ = 2;
	public static final int INST_WRITE = 3;
	
	static final int DEFAULT_STATUS_PACKET_LENTH = 11; // lenth of single status packet includes param0;
	
	public static byte getLowByte(int a) {// return 1 byte
		return (byte) (a & 0xff);
	}
	public static byte getHighByte(int a) {// return 1 byte
		return (byte) ((a >> 8) & 0xff);
	}
	
	public static int getLowWord(long a) {// return 2 byte
		return (int) (a & 0xffff);
	}
	public static int getHighWord(long a) {// return 2 byte
		return (int) ((a >> 16) & 0xffff);
	}
	public static int makeWord(byte a, byte b) {// 4 -> 1 -> return 2 byte
		return (int) ((a & 0xff) | ((b & 0xff) << 8));
	}
	
	public static byte[] packetWriteByte(int id, int address, int value) {
		byte[] buffer = new byte[13];
		buffer[PKT.HEADER0.idx]       	= (byte) 0xff;
		buffer[PKT.HEADER1.idx]       	= (byte) 0xff;
		buffer[PKT.HEADER2.idx]       	= (byte) 0xfd;
		buffer[PKT.RESERVED.idx]			= (byte) 0x00;
		buffer[PKT.ID.idx]            		= (byte) id;
		buffer[PKT.LENGTH_L.idx]      		= (byte) 0x06;
		buffer[PKT.LENGTH_H.idx]      	= (byte) 0x00;
		buffer[PKT.INSTRUCTION.idx]   	= (byte) INST_WRITE;
		buffer[PKT.PARAMETER.idx]     	= (byte) getLowByte(address);
		buffer[PKT.PARAMETER.idx+1]   	= (byte) getHighByte(address);
		buffer[PKT.PARAMETER.idx+2]   	= (byte) value;
	    
	    int crc = CRC16.update_crc(0, buffer, makeWord(buffer[PKT.LENGTH_L.idx], buffer[PKT.LENGTH_H.idx])+PKT.LENGTH_H.idx+1-2);  // -2 : except CRC16
	    buffer[PKT.PARAMETER.idx+3]   	= (byte) getLowByte(crc);
	    buffer[PKT.PARAMETER.idx+4]   	= (byte) getHighByte(crc);
		return buffer;
	}

	public static byte[] packetWriteWord(int id, int address, int value) {
		byte[] buffer = new byte[14];
		buffer[PKT.HEADER0.idx]       	= (byte) 0xff;
		buffer[PKT.HEADER1.idx]       	= (byte) 0xff;
		buffer[PKT.HEADER2.idx]       	= (byte) 0xfd;
		buffer[PKT.RESERVED.idx]			= (byte) 0x00;
		buffer[PKT.ID.idx]            		= (byte) id;
		buffer[PKT.LENGTH_L.idx]      		= (byte) 0x07;
		buffer[PKT.LENGTH_H.idx]      	= (byte) 0x00;
		buffer[PKT.INSTRUCTION.idx]   	= (byte) INST_WRITE;
		buffer[PKT.PARAMETER.idx]     	= (byte) getLowByte(address);
		buffer[PKT.PARAMETER.idx+1]   	= (byte) getHighByte(address);
		buffer[PKT.PARAMETER.idx+2]   	= (byte) getLowByte(value);;
		buffer[PKT.PARAMETER.idx+3]   	= (byte) getHighByte(value);
	    
	    int crc = CRC16.update_crc(0, buffer, makeWord(buffer[PKT.LENGTH_L.idx], buffer[PKT.LENGTH_H.idx])+PKT.LENGTH_H.idx+1-2);  // -2 : except CRC16
	    buffer[PKT.PARAMETER.idx+4]   	= (byte) getLowByte(crc);
	    buffer[PKT.PARAMETER.idx+5]   	= (byte) getHighByte(crc);
		return buffer;
	}

	public static byte[] packetWriteDWord(int id, int address, long value) {
		byte[] buffer = new byte[16];
		buffer[PKT.HEADER0.idx]       	= (byte) 0xff;
		buffer[PKT.HEADER1.idx]       	= (byte) 0xff;
		buffer[PKT.HEADER2.idx]       	= (byte) 0xfd;
		buffer[PKT.RESERVED.idx]			= (byte) 0x00;
		buffer[PKT.ID.idx]            		= (byte) id;
		buffer[PKT.LENGTH_L.idx]      		= (byte) 0x09;
		buffer[PKT.LENGTH_H.idx]      	= (byte) 0x00;
		buffer[PKT.INSTRUCTION.idx]   	= (byte) INST_WRITE;
		buffer[PKT.PARAMETER.idx]     	= (byte) getLowByte(address);
		buffer[PKT.PARAMETER.idx+1]   	= (byte) getHighByte(address);
		buffer[PKT.PARAMETER.idx+2]   	= (byte) getLowByte(getLowWord(value));
		buffer[PKT.PARAMETER.idx+3]   	= (byte) getHighByte(getLowWord(value));
		buffer[PKT.PARAMETER.idx+4]   	= (byte) getLowByte(getHighWord(value));
		buffer[PKT.PARAMETER.idx+5]   	= (byte) getHighByte(getHighWord(value));
	    
	    int crc = CRC16.update_crc(0, buffer, makeWord(buffer[PKT.LENGTH_L.idx], buffer[PKT.LENGTH_H.idx])+PKT.LENGTH_H.idx+1-2);  // -2 : except CRC16
	    buffer[PKT.PARAMETER.idx+6]   	= (byte) getLowByte(crc);
	    buffer[PKT.PARAMETER.idx+7]   	= (byte) getHighByte(crc);
		return buffer;
	}

	public static byte[] packetRead(int id, int address, int lenthToRead) {
		byte[] buffer = new byte[14];
	    buffer[PKT.HEADER0.idx]       	= (byte) 0xff;
	    buffer[PKT.HEADER1.idx]       	= (byte) 0xff;
	    buffer[PKT.HEADER2.idx]       	= (byte) 0xfd;
	    buffer[PKT.RESERVED.idx]			= (byte) 0x00;
	    buffer[PKT.ID.idx]            		= (byte) id;
	    buffer[PKT.LENGTH_L.idx]      		= (byte) 0x07;
	    buffer[PKT.LENGTH_H.idx]      	= (byte) 0x00;
	    buffer[PKT.INSTRUCTION.idx]   	= (byte) INST_READ;
	    buffer[PKT.PARAMETER.idx]     	= (byte) getLowByte(address);
	    buffer[PKT.PARAMETER.idx+1]   	= (byte) getHighByte(address);
	    buffer[PKT.PARAMETER.idx+2]   	= (byte) getLowByte(lenthToRead);
	    buffer[PKT.PARAMETER.idx+3]   	= (byte) getHighByte(lenthToRead);
	    
	    int crc = CRC16.update_crc(0, buffer, makeWord(buffer[PKT.LENGTH_L.idx], buffer[PKT.LENGTH_H.idx])+PKT.LENGTH_H.idx+1-2);  // -2 : except CRC16
	    buffer[PKT.PARAMETER.idx+4]   	= (byte) getLowByte(crc);
	    buffer[PKT.PARAMETER.idx+5]   	= (byte) getHighByte(crc);
		return buffer;
	}
	
	public static void log (byte[] buffer) {
		String buf = "";
	    for (int b : buffer) {
	        buf += String.format("%02x", (0xFF & b)).toUpperCase() + " ";
	    }
	    Log.i("ROBOTIS", "" + buf);
//	    Log.i("ROBOTIS", "" + Arrays.toString(buffer));
	    
	    int[] bufferInt = new int[buffer.length];
	    for (int index = 0; index <  buffer.length; index++) {
	    	bufferInt[index] = buffer[index] & 0xff;
	    }
	    Log.i("ROBOTIS", "" + Arrays.toString(bufferInt));
	}
	
	public static String packetToString(byte[] buffer) {
		String buf = "";
	    for (int b : buffer) {
	        buf += String.format("%02x", (0xFF & b)).toUpperCase() + " ";
	    }
	    return buf;
	}
	
	public static void log (int[] buffer) {
		Log.i("ROBOTIS", "" + Arrays.toString(buffer));
	}
}