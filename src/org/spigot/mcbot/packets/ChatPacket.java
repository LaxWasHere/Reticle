package org.spigot.mcbot.packets;

import java.io.IOException;
import java.net.Socket;

public class ChatPacket extends packet {
	private Socket sock;

	public ChatPacket(Socket sock) throws IOException {
		this.sock = sock;
	}
	
	public String Read() throws IOException {
		super.input=sock.getInputStream();
		String chat=super.readString();
		super.readByte();
		return chat;
	}
	
	public void Write(String message)  throws IOException {
		super.setOutputStream(message.length()+1);
		super.writeVarInt(1);
		super.writeString(message);
		super.Send(sock.getOutputStream());
	}
	
}