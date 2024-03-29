package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sql.rowset.serial.SerialException;

import org.spigot.reticle.events.PlayerListEvent;
import org.spigot.reticle.sockets.connector;

public class PlayerListItemPacket extends AbstractPacket {
	public static final int ID = 0x38;

	private packet reader;

	public PlayerListItemPacket(ByteBuffer sock, packet reader) {
		this.reader = reader;
		this.reader.input = sock;

	}

	public PlayerListEvent Read(List<String> tablist, HashMap<String, String> tablist_nicks) throws IOException, SerialException {
		String name = null;
		boolean online = false;
		List<String> UUIDS = null;
		List<String> Nicks = null;
		List<Boolean> Onlines = null;
		List<Boolean> Changed = null;
		if (reader.ProtocolVersion >= 47) {
			UUIDS = new ArrayList<String>();
			Nicks = new ArrayList<String>();
			Onlines = new ArrayList<Boolean>();
			Changed = new ArrayList<Boolean>();
			int action = reader.readVarInt();
			// Length
			int len = reader.readVarInt();
			String uuid = reader.readUUID();
			for (int o = 0; o < len; o++) {
				Changed.add(o, false);
				Onlines.add(o, true);
				UUIDS.add(o, uuid);
				Onlines.add(o, true);
				if (action == 0) {
					// Add nick
					Nicks.add(o, connector.parsechat(reader.readString()));
					int props = reader.readVarInt();
					for (int i = 0; i < props; i++) {
						// prop name
						reader.readString();
						// prop value
						reader.readString();
						// is signed
						reader.readBoolean();
						// signature
						reader.readString();
					}
					// gamemode
					reader.readVarInt();
					// ping
					reader.readVarInt();
					// has display name
					boolean hasdname = reader.readBoolean();
					Changed.add(o, true);
					if (hasdname) {
						// display name
						Nicks.add(o, connector.parsechat(reader.readString()));
					}

				} else if (action == 1) {
					// update gamemode
					reader.readVarInt();
					Nicks.add(o, null);
				} else if (action == 2) {
					// ping update
					reader.readVarInt();
					Nicks.add(o, null);
				} else if (action == 3) {
					Nicks.add(o, null);
					// display name update
					boolean hasdname = reader.readBoolean();
					if (hasdname) {
						// display name
						Changed.add(o, true);
						Nicks.add(o, connector.parsechat(reader.readString()));
					}
				} else if (action == 4) {
					Nicks.add(o, null);
					// remove player
					Onlines.add(o, false);
				} else {
					Nicks.add(o, null);
				}
			}
		} else {
			name = reader.readString();
			online = reader.readBoolean();
			// Ping
			reader.readShort();
		}
		return new PlayerListEvent(reader.bot, name, online, UUIDS, Nicks, Onlines, Changed,tablist,tablist_nicks);
	}

	/*
	 * public boolean Serve(List<String> tablist, HashMap<String, String>
	 * tablistnick) { if (reader.ProtocolVersion >= 47) { boolean ret = false;
	 * for (int i = 0, o = UUIDS.size(); i < o; i++) { String xUUID =
	 * UUIDS.get(i); if (!Onlines.get(o)) { if (tablist.contains(xUUID)) {
	 * tablist.remove(xUUID); } if (tablistnick.containsKey(xUUID)) {
	 * tablistnick.remove(xUUID); } } }
	 * 
	 * for (int i = 0, o = UUIDS.size(); i < o; i++) { String xUUID =
	 * UUIDS.get(i); String xname = Nicks.get(i); boolean xonline =
	 * Onlines.get(i); boolean xchanged = Changed.get(i); if
	 * (tablist.contains(xUUID)) { // Already in tablist if (xchanged) { //
	 * Display name changed tablistnick.put(xUUID, xname); ret = true; } else if
	 * (!xonline) { // Remove us tablist.remove(xUUID); if
	 * (tablistnick.containsKey(xUUID)) { tablistnick.remove(xUUID); } ret =
	 * true; } } else { // We are not in tablist yet if (xchanged) {
	 * tablist.add(xUUID); tablistnick.put(xUUID, xname); ret = true; } } }
	 * return ret; } else { if (tablist.contains(name)) { // We are already in
	 * tablist if (online) { // And online (Correct) } else { // Bot not online
	 * (Suicide) tablist.remove(name); return true; } } else { // We are not in
	 * tablist yet if (online) { // But online (Must add) tablist.add(name);
	 * return true; } else { // And not online (correct) } } } return false; }
	 */
}
