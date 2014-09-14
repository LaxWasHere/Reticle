package org.spigot.reticle.packets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.sql.rowset.serial.SerialException;

public class packet {
	protected ByteBuffer input;
	private ByteBuffer output;
	protected int version = 4;
	public InputStream sockinput;
	public static int MAXPACKETID = 0x40;
	public List<Integer> ValidPackets = new ArrayList<Integer>();
	private OutputStream sockoutput;
	private boolean encrypted = false;
	private CipherInputStream cis;
	private CipherOutputStream cos;

	public enum SIZER {
		BOOLEAN(1), BYTE(1), SHORT(2), INT(4), LONG(8), FLAT(4), DOUBLE(8);
		public int size;

		SIZER(int siz) {
			this.size = siz;
		}
	}

	public void setEncryptedStreams(CipherInputStream cis, CipherOutputStream cos) {
		this.cis = cis;
		this.cos = cos;
	}

	public void setEncrypted() {
		this.encrypted = true;
	}

	public packet() {

	}

	public packet(InputStream inputStream, OutputStream outputs) {
		this.sockinput = inputStream;
		this.sockoutput = outputs;
	}

	public packet(InputStream inputStream) {
		this.sockinput = inputStream;
	}

	public packet(int len, ByteBuffer input) throws IOException {
		// sock = s;
		this.input = input;
		int vcount = getVarntCount(len);
		this.output = ByteBuffer.allocate(len + vcount);
		this.output.order(ByteOrder.BIG_ENDIAN);
		writeVarInt(len);
	}

	public String readUUID() throws SerialException, IOException {
		return this.readLong() + "" + this.readLong();
	}

	public int[] readNext() throws IOException, SerialException {
		int[] res = new int[2];
		// The length of the packet
		res[0] = readInnerVarInt();
		// Id of packet
		res[1] = readInnerVarInt();
		return res;
	}

	private byte[] readInnerBytes(int len) throws IOException {
		byte[] b = new byte[len];
		if (this.encrypted) {
			this.cis.read(b, 0, len);
		} else {
			this.sockinput.read(b, 0, len);
		}
		return b;
	}

	public int getCompressedLen(byte[] packer) {
		return packer.length;
	}

	public int getCompressedID(byte[] packer) throws SerialException, IOException {
		return packet.readCompressedVarInt(packer);
	}

	public byte[] readNextCompressed() throws SerialException, IOException, DataFormatException {
		int plen = readInnerVarInt();
		int len = readInnerVarInt();
		int reslen = plen - getVarntCount(len);
		byte[] out = this.readInnerBytes(reslen);
		System.out.println("Len: " + len);
		if (len == 0) {
			return out;
		}
		Inflater decompresser = new Inflater();
		decompresser.setInput(out, 0, reslen);
		byte[] result = new byte[len];
		try {
			decompresser.inflate(result);
		} catch (DataFormatException e) {
			return new byte[] { 0x3, 0x0b, 0x00, 0x00 };
		}
		decompresser.end();
		return result;
	}

	public byte[] readArray() throws Exception {
		byte len = readByte();
		if (len >= Byte.MAX_VALUE) {
			throw new Exception("Byte array error (" + len + " <= " + Short.MAX_VALUE + ")");
		}
		byte[] ret = readBytes(len);
		return ret;
	}

	public byte[] readBytes(int len) throws IOException {
		byte[] b = new byte[len];
		this.input.get(b, 0, len);
		return b;
	}

	public void writeBytes(byte[] b) {
		this.output.put(b);
	}

	public int getStringLength(String s) throws IOException {
		return s.getBytes("UTF-8").length + (getVarntCount(s.getBytes("UTF-8").length));
	}

	public void Send() throws IOException {
		output.position(0);
		if (encrypted) {
			cos.write(output.array());
		} else {
			sockoutput.write(output.array());
		}

	}

	protected void setOutputStream(int len) throws IOException {
		int vcount = getVarntCount(len);
		this.output = ByteBuffer.allocate(len + vcount);
		writeVarInt(len);
	}

	protected void readAndIgnore(int length) throws IOException {
		// input.position(input.position()+length);
		if (encrypted) {
			cis.skip(length);
		} else {
			sockinput.skip(length);
		}
	}

	protected int readInnerVarInt() throws SerialException, IOException {
		int out = 0;
		int bytes = 0;
		byte in;
		while (true) {
			int ir;
			if (encrypted) {
				ir = this.cis.read();
			} else {
				ir = this.sockinput.read();
			}
			if (ir == -1) {
				throw new SerialException();
			} else {
				in = (byte) ir;
			}
			out |= (in & 0x7F) << (bytes++ * 7);
			if (bytes > 5) {
				throw new RuntimeException("VarInt too big");
			}
			if ((in & 0x80) != 0x80) {
				break;
			}
		}
		return out;
	}

	protected static int readCompressedVarInt(byte[] byter) throws IOException, SerialException {
		int out = 0;
		int bytes = 0;
		byte in;
		int i = 0;
		while (true) {
			in = byter[i];
			out |= (in & 0x7F) << (bytes++ * 7);
			if (bytes > 5) {
				throw new RuntimeException("VarInt too big");
			}
			if ((in & 0x80) != 0x80) {
				break;
			}
			i++;
		}
		return out;
	}

	protected int readVarInt() throws IOException, SerialException {
		int out = 0;
		int bytes = 0;
		byte in;
		while (true) {
			in = readByte();
			out |= (in & 0x7F) << (bytes++ * 7);
			if (bytes > 5) {
				throw new RuntimeException("VarInt too big");
			}
			if ((in & 0x80) != 0x80) {
				break;
			}
		}
		return out;

		/*
		 * int value = 0; int i = 0; int b; while (((b = readByte()) & 0x80) !=
		 * 0) { value |= (b & 0x7F) << i; i += 7; if (i > 35) { throw new
		 * IllegalArgumentException("Variable length quantity is too long"); } }
		 * return value | (b << i);
		 */
	}

	protected int readInt() throws IOException, SerialException {
		return (readByte() << 24) + (readByte() << 16) + (readByte() << 8) + readByte();

	}

	protected void writeInt(int i) throws IOException {
		output.putInt(i);
	}

	protected byte readByte() throws IOException, SerialException {
		int byter = input.get();
		return (byte) byter;
	}

	protected void writeByte(byte b) throws IOException {
		output.put((byte) b);
	}

	protected short readShort() throws IOException {
		return input.getShort();
	}

	protected String readInnerString() throws IOException, SerialException {
		int len = readInnerVarInt();
		if (len > 10240) {
			System.err.println("Can't read " + len);
			new IOException().printStackTrace();
			throw new IOException();
		}
		byte[] b = new byte[len];
		if (encrypted) {
			cis.read(b, 0, len);
		} else {
			sockinput.read(b, 0, len);
		}
		return new String(b, "UTF-8");
	}

	protected String readString() throws IOException, SerialException {
		int len = readVarInt();
		if (len > 10240) {
			System.err.println("Can't read " + len);
			new IOException().printStackTrace();
			throw new IOException();
		}
		byte[] b = new byte[len];
		try {
			input.get(b, 0, len);
		} catch (BufferUnderflowException e) {
		}
		return new String(b, "UTF-8");
	}

	protected void writeString(String str) throws IOException {
		byte[] utfstr = str.getBytes("UTF-8");
		writeVarInt(utfstr.length);
		output.put(str.getBytes("UTF-8"));
	}

	protected void writeShort(short b) throws IOException {
		output.putShort(b);
	}

	protected long readLong() throws IOException, SerialException {
		return (((long) readInt()) << 16) + ((long) readInt());
	}

	protected void writeLong(long l, OutputStream output) throws IOException {
		writeInt((int) l >> 4 * 8);
		writeInt((int) l & 0xFFFFFFFF);
	}

	protected boolean readBoolean() throws IOException, SerialException {
		int i = readByte();
		return (i != 0);
	}

	protected void writeBoolean(boolean b) throws IOException {
		if (b) {
			writeByte((byte) 1);
		} else {
			writeByte((byte) 0);
		}
	}

	protected float readFloat() throws IOException {
		return input.getFloat();
	}

	protected void writeFloat(float f) throws IOException {
		output.putFloat(f);
		// new DataOutputStream(output).writeFloat(f);
	}

	protected double readDouble() throws IOException {
		return input.getDouble();
	}

	protected void writeDouble(double d) throws IOException {
		output.putDouble(d);
		// new DataOutputStream(output).writeDouble(d);
	}

	protected void writeVarInt(int value) throws IOException {
		if (value == 0) {
			writeByte((byte) 0);
			return;
		}
		int part;
		while (true) {
			part = value & 0x7F;
			value >>>= 7;
			if (value != 0) {
				part |= 0x80;
			}
			output.put((byte) part);
			if (value == 0) {
				break;
			}
		}
	}

	public int getVarntCount(int value) {
		int i = 0;
		while (true) {
			value >>>= 7;
			i++;
			if (value == 0) {
				break;
			}
		}
		return i;
	}

}
