package org.spigot.reticle.supporter;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.spigot.reticle.storage;
import org.spigot.reticle.botfactory.mcbot;
import org.spigot.reticle.settings.team_struct;

import jerklib.Channel;
import jerklib.ConnectionManager;
import jerklib.Profile;
import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.IRCEvent.Type;
import jerklib.events.JoinCompleteEvent;
import jerklib.events.MessageEvent;
import jerklib.events.NickListEvent;
import jerklib.events.QuitEvent;
import jerklib.listeners.IRCEventListener;

public class supportconnector extends Thread implements IRCEventListener {
	private boolean connected = false;
	public String username;
	private ConnectionManager manager;
	private mcbot bot;
	private Session session;
	private Channel chan;
	private List<String> Tablist = new ArrayList<String>();
	private HashMap<String, String> emptymap1 = new HashMap<String, String>();
	private HashMap<String, team_struct> emptymap2 = new HashMap<String, team_struct>();
	private HashMap<String, String> Tablist_names = new HashMap<String, String>();
	private String connectnick;

	public supportconnector(mcbot bot) {
		this.bot = bot;
		Connect();
	}

	@SuppressWarnings("deprecation")
	public void Disconnect() {
		if (session != null) {
			session.close("");
		}
		bot.resettablist();
		sendmsg("Disconnected");
		this.connected = false;
		storage.changemenuitems();
		storage.getInstance().support.killsupportconnector();
		this.stop();
	}

	public boolean isConnected() {
		return connected;
	}

	@Override
	public void run() {
		sendmsg("Connecting to support server...");
		manager = new ConnectionManager(new Profile(connectnick));
		session = manager.requestConnection(storage.supportserver);
		session.addIRCEventListener(this);
	}

	public void Connect() {
		bot.resettablist();
		Tablist = new ArrayList<String>();
		this.username = storage.getSupportNick();
		this.connectnick = storage.randomString(15);
		this.connected = true;
		Tablist = new ArrayList<String>();
		Tablist_names = new HashMap<String, String>();
		this.start();
	}

	@Override
	public void receiveEvent(IRCEvent event) {
		Type type = event.getType();
		if (type == Type.CONNECT_COMPLETE) {
			event.getSession().join(storage.supportchannel);
		} else if (type == Type.JOIN_COMPLETE) {
			sendmsg("Welcome to �1�nReticle support");
			sendmsg("�4�l�nThis server has no authentication so please do not post personal data here!");
			chan = ((JoinCompleteEvent) event).getChannel();
			sendnickrequests();
			sendmynick();
			bot.setTabSize(15, 2);
		} else if (type == Type.CHANNEL_MESSAGE || type == Type.PRIVATE_MESSAGE) {
			MessageEvent chatevent = (MessageEvent) event;
			try {
				String msgr = deobfuscatemessage(chatevent.getMessage());
				if (msgr.length() > 0) {
					processchat(chatevent.getUserName(), msgr);
				}
			} catch (ArrayIndexOutOfBoundsException e) {
			}
		} else if (type == Type.NICK_LIST_EVENT) {
			NickListEvent nlevent = (NickListEvent) event;
			for (String nick : nlevent.getNicks()) {
				addToTablist(nick, false);
			}
			refreshtablist();
		} else if (type == Type.QUIT || type == Type.KICK_EVENT) {
			QuitEvent quitevent = (QuitEvent) event;
			if (isResolved(quitevent.getUserName())) {
				sendmsg("�0User �n" + trytranslation(quitevent.getUserName()) + "�r�0 has left support server");
			}
			removeFromTablist(quitevent.getUserName(), true);
		} else if (type == Type.CONNECTION_LOST) {
			event.getSession().close("");
			Disconnect();
		}
	}

	private String trytranslation(String nick) {
		if (this.Tablist_names.containsKey(nick)) {
			return this.Tablist_names.get(nick);
		} else {
			return "�0�mUnresolved";
		}
	}

	private boolean isResolved(String nick) {
		return this.Tablist_names.containsKey(nick);
	}

	private boolean isMe(String nick) {
		return nick.equals(connectnick);

	}

	private void removeFromTablist(String nick, boolean update) {
		if (Tablist.contains(nick)) {
			Tablist.remove(nick);
			Tablist_names.remove(nick);
			if (update) {
				refreshtablist();
			}
		}
	}

	private void processchat(String nick, String chat) {
		String op = chat.substring(0, 1);
		if (op.equals(CHATOP.CHAT.id)) {
			String msg = chat.substring(1);
			sendchatmsg(trytranslation(nick) + ": " + msg);
		} else if (op.equals(CHATOP.GET_NICK.id)) {
			sendmynick();
		} else if (op.equals(CHATOP.NICK_RESPONSE.id)) {
			String msg = chat.substring(1);
			resolvenickresponse(nick, msg);
		} else {
			sendmsg(trytranslation(nick) + ": " + chat);
		}

	}

	private void sendnickrequests() {
		sendtoserverraw(this.obfuscatemessage(CHATOP.GET_NICK.id + ""));
	}

	private void sendmynick() {
		sendtoserverraw(this.obfuscatemessage(CHATOP.NICK_RESPONSE.id + username));
	}

	private void resolvenickresponse(String user, String nick) {
		this.addToTablist(user, nick, true);
	}

	private void addToTablist(String nick, String user, boolean update) {
		addToTablist(nick, user, update, false);
	}

	private void addToTablist(String nick, String user, boolean update, boolean silent) {
		if (nick.equals(connectnick)) {
			user = username;
		}
		if (nick.length() > 0) {
			if (!Tablist.contains(nick)) {
				if (!silent) {
					sendmsg("�0User �n" + user + "�r�0 has joined support server");
				}
				Tablist.add(nick);
			}
			Tablist_names.put(nick, user);
			if (update) {
				refreshtablist();
			}
		}
	}

	private void addToTablist(String nick, boolean update) {
		if (isResolved(nick) || isMe(nick)) {
			String user = trytranslation(nick);
			addToTablist(nick, user, update, true);
		}
	}

	private void refreshtablist() {
		bot.refreshtablist(Tablist, Tablist_names, emptymap1, emptymap2);
	}

	private String base64decode(String message) {
		try {
			return new String(DatatypeConverter.parseBase64Binary(message), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return new String(DatatypeConverter.parseBase64Binary(message));
		}
	}

	private String base64encode(String message) {
		try {
			return new String(DatatypeConverter.printBase64Binary(message.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			return new String(DatatypeConverter.printBase64Binary(message.getBytes()));
		}
	}

	private String obfuscatemessage(String origmsg) {
		String res;
		res = base64encode(reverse(base64encode(origmsg)));
		return res;
	}

	private String deobfuscatemessage(String message) {
		String res;
		res = base64decode(reverse(base64decode(message)));
		return res;
	}

	private String reverse(String str) {
		return new StringBuilder(str).reverse().toString();
	}

	public void SendMessage(String message) {
		sendrawchatmsg(message);
		sendtoserverraw(obfuscatemessage(CHATOP.CHAT.id + message));
	}

	private void sendtoserverraw(String message) {
		if (session.isConnected()) {
			session.sayChannel(message, chan);
		}
	}

	private void sendrawchatmsg(String msg) {
		if (!msg.equals("")) {
			sendrawmsg("[CHAT] " + username + ": " + msg);
		}
	}

	private void sendchatmsg(String msg) {
		if (!msg.equals("")) {
			sendrawmsg("[CHAT] " + msg);
		}
	}

	private void sendmsg(String msg) {
		sendrawmsg("[Support] " + msg);
	}

	private void sendrawmsg(String msg) {
		bot.logmsg(msg);
	}

	private enum CHATOP {
		CHAT(Character.toString((char) 1)), GET_NICK(Character.toString((char) 16)), NICK_RESPONSE(Character.toString((char) 17));
		public String id;

		CHATOP(String i) {
			this.id = i;
		}
	}

}
