package org.spigot.mcbot.botfactory;

import java.awt.Color;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.spigot.mcbot.storage;
import org.spigot.mcbot.settings.botsettings;

public class mcbot {
	private Socket sock;
	private JTextPane chatlog;
	private JTable tablist;
	private JPanel panel;
	private botsettings rawbot;
	public boolean isconnected = false;
	public boolean autoconnect = false;
	public boolean exists = false;
	public boolean ismain = false;
	private HashMap<String, Style> styles;

	public mcbot(botsettings bot, boolean ismain) {
		this.ismain = ismain;
		this.rawbot = bot;
		initwin();
	}
	

	public mcbot(botsettings bot) {
		this.rawbot = bot;
		initwin();
	}

	public void initwin() {
		botfactory.makenewtab(this);
		if (ismain) {
			seticon(ICONSTATE.MAIN);
		} else {
			seticon(ICONSTATE.DISCONNECTED);
		}
	}

	private int gettabid() {
		JTabbedPane cpanel = storage.gettabbedpane();
		int len = cpanel.getComponentCount();
		int i = 0;
		for (; i < len; i++) {
			if (cpanel.getTitleAt(i).equals(this.gettabname())) {
				break;
			}
		}
		return i;
	}

	public void seticon(ICONSTATE state) {
		storage.gettabbedpane().setIconAt(gettabid(), state.icon);
	}

	public void setconfig(JTextPane chatlog, JTable tablist, JPanel panel) {
		this.chatlog = chatlog;
		this.tablist = tablist;
		this.panel = panel;
		this.exists = true;
	}

	public String gettabname() {
		return this.rawbot.bottabname;
	}

	public Style getstyle(String combo) {
		combo = combo.toLowerCase();
		if (styles.containsKey(combo)) {
			return styles.get(combo);
		} else {
			Style style = this.chatlog.addStyle(combo, null);
			if (combo.contains("l")) {
				StyleConstants.setBold(style, true);
			} else if (combo.contains("m")) {
				StyleConstants.setStrikeThrough(style, true);
			} else if (combo.contains("n")) {
				StyleConstants.setUnderline(style, true);
			} else if (combo.contains("o")) {
				StyleConstants.setItalic(style, true);
			} else if (combo.contains("0")) {
				StyleConstants.setForeground(style, new Color(0, 0, 0));
			} else if (combo.contains("1")) {
				StyleConstants.setForeground(style, new Color(0, 0, 170));
			} else if (combo.contains("2")) {
				StyleConstants.setForeground(style, new Color(0, 170, 0));
			} else if (combo.contains("3")) {
				StyleConstants.setForeground(style, new Color(0, 170, 170));
			} else if (combo.contains("4")) {
				StyleConstants.setForeground(style, new Color(170, 0, 0));
			} else if (combo.contains("5")) {
				StyleConstants.setForeground(style, new Color(170, 0, 170));
			} else if (combo.contains("6")) {
				StyleConstants.setForeground(style, new Color(255, 170, 0));
			} else if (combo.contains("7")) {
				StyleConstants.setForeground(style, new Color(170, 170, 170));
			} else if (combo.contains("8")) {
				StyleConstants.setForeground(style, new Color(85, 85, 85));
			} else if (combo.contains("9")) {
				StyleConstants.setForeground(style, new Color(85, 85, 255));
			} else if (combo.contains("a")) {
				StyleConstants.setForeground(style, new Color(85, 255, 85));
			} else if (combo.contains("b")) {
				StyleConstants.setForeground(style, new Color(85, 255, 255));
			} else if (combo.contains("c")) {
				StyleConstants.setForeground(style, new Color(255, 85, 85));
			} else if (combo.contains("d")) {
				StyleConstants.setForeground(style, new Color(255, 85, 255));
			} else if (combo.contains("e")) {
				StyleConstants.setForeground(style, new Color(255, 255, 85));
			} else if (combo.contains("f")) {
				StyleConstants.setForeground(style, new Color(255, 255, 255));
			}
			styles.put(combo, style);
			chatlog.addStyle(combo, style);
			return style;
		}
	}

	public void connect() {
		if (this.rawbot.serverip != null && this.sock==null) {
			try {
				sock=new Socket(this.rawbot.serverip,this.rawbot.serverport);
				sock.getOutputStream();
			} catch (UnknownHostException e) {
				sock=null;
				e.printStackTrace();
			} catch (IOException e) {
				sock=null;
				e.printStackTrace();
			}
		}
	}

	public enum ICONSTATE {
		CONNECTED(storage.icon_on), DISCONNECTED(storage.icon_off), CONNECTING(storage.icon_con), MAIN(storage.icon_dis);

		public Icon icon;

		ICONSTATE(Icon ico) {
			this.icon = ico;
		}
	}

	public synchronized void logmsg(String message) {
		if (message.length() > 0) {
			if (message.startsWith("�")) {
				message = message.substring(1);
			}
			String bold = "";
			String underline = "";
			String strike = "";
			String italic = "";
			String color = "f";
			StyledDocument doc = this.chatlog.getStyledDocument();
			String[] msgs = message.split("�");
			for (String msg : msgs) {
				String tmg = msg.substring(0, 1).toLowerCase();
				if (tmg.equals("l")) {
					bold = "l";
				} else if (tmg.equals("m")) {
					strike = "m";
				} else if (tmg.equals("n")) {
					underline = "n";
				} else if (tmg.equals("o")) {
					italic = "o";
				} else if (tmg.equals("r")) {
					bold = "";
					strike = "";
					underline = "";
					italic = "";
					color = "f";
				} else {
					color = tmg;
				}
				String combo = bold + strike + underline + italic + color;
				Style style = getstyle(combo);
				try {
					doc.insertString(doc.getLength(), combo + msg.substring(1), style);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
