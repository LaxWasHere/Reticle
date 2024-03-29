package org.spigot.reticle.settings;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;

import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;

import org.spigot.reticle.storage;
import org.spigot.reticle.sockets.Authenticator;
import org.spigot.reticle.sockets.Authenticator.accounts;
import org.spigot.reticle.sockets.Authenticator.profile;

import say.swing.JFontChooser;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPasswordField;
import javax.swing.JList;

public class settings extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textcurtabname;
	private JTextField txtservername;
	private JTextField textserverip;
	private JTextField textserverport;
	private JTextField txtNick;
	private JPanel panel_3;
	private JCheckBox checkactiv;
	private JCheckBox checkdisccom;
	private JCheckBox checkconcom;
	private JCheckBox checkafkcom;
	private JTextArea textlogoutcom;
	private JTextArea textlogincom;
	private JTextArea textafkcom;
	private JCheckBox checkBox;

	public settings thisobj;
	private JTextField textantiafkdelay;
	private JTextField textreconnectdelay;
	private JCheckBox checkautoreconnect;
	private JTextArea textignore;
	private JComboBox<String> protocolversion;
	private JPasswordField mpassword;
	private JCheckBox checkmena;
	private JTextField textmusername;
	private JComboBox<String> mojangusername;

	private botsettings BOT;
	private String accesstoken;
	private String playertoken;
	private HashMap<String, String> mojangusernamelist;
	private String mcurrentusername;
	private String mojangloginusernameid;
	private JTextField textmessagedelay;
	private JTextField textmaxlines;
	private JTextField textfont;
	private JTextField textField;

	private Font font;
	private List<JCheckBox> boxes = new ArrayList<JCheckBox>();
	private JTextField textproxyip;
	private JTextField textproxyport;

	/**
	 * Init settings window Not safe for anyone to mess with this method
	 * 
	 * @param set
	 */
	public settings(final botsettings set) {
		this.mojangloginusernameid = set.mojangloginusernameid;
		this.accesstoken = set.maccesstoken;
		this.playertoken = set.mplayertoken;
		this.mojangusernamelist = set.mojangusernamelist;
		this.mcurrentusername = set.mcurrentusername;
		this.BOT = set;
		this.font = set.font;
		EventQueue.invokeLater(new Runnable() {

			private JCheckBox checkspas;
			private JCheckBox checksacc;

			public void run() {
				addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent arg0) {
						storage.getInstance().winobj = null;
						dispose();
					}
				});
				setResizable(false);
				setType(Type.POPUP);
				setTitle("Settings");
				setBounds(100, 100, 534, 524);
				contentPane = new JPanel();
				contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
				contentPane.setLayout(new BorderLayout(0, 0));
				setContentPane(contentPane);

				JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
				contentPane.add(tabbedPane, BorderLayout.CENTER);

				JPanel panel = new JPanel();
				tabbedPane.addTab("General", null, panel, null);
				panel.setLayout(new MigLayout("", "[][][grow][][]", "[][][][][][][][][][][]"));

				JLabel lblNewLabel = new JLabel("Current tab name:");
				panel.add(lblNewLabel, "cell 1 0,alignx trailing");

				textcurtabname = new JTextField();
				textcurtabname.setEditable(false);
				panel.add(textcurtabname, "cell 2 0,growx");
				textcurtabname.setColumns(10);

				JButton btnNewButton = new JButton("Save settings");
				btnNewButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						// New settings from user
						botsettings bs;
						try {
							// TODO: Save
							bs = storage.getInstance().setobj.getsettings(accesstoken, playertoken, mojangusernamelist, mcurrentusername, mojangloginusernameid, font, getEnabledPlugins(boxes));
						} catch (NumberFormatException e) {
							storage.alert("Settings error", "One or more numeric fields\ncontains one or more\nillegal characters!");
							return;
						}
						// Old bot name used to identification
						String acti = set.getTabName();
						if (storage.verifysettings(acti, bs)) {
							// Tab index
							int anum = storage.gettabbyname(acti);
							storage.resetset(bs, acti, anum);
							storage.getInstance().winobj = null;
							dispose();
						}
					}
				});
				panel.add(btnNewButton, "cell 1 2");

				JButton btnNewButton_1 = new JButton("Restore settings");
				btnNewButton_1.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						String toupdate = BOT.getTabName();
						botsettings nset = storage.getInstance().settin.settings.get(toupdate);
						storage.getInstance().setobj.setsettings(nset);
					}
				});
				panel.add(btnNewButton_1, "cell 2 2");

				JButton btnNewButton_2 = new JButton("Remove this bot");
				btnNewButton_2.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						int n = JOptionPane.showConfirmDialog(contentPane, "Are you sure you want to do this?", "Warning", JOptionPane.YES_NO_OPTION);
						if (n == JOptionPane.YES_OPTION) {
							storage.getInstance().winobj = null;
							dispose();
							String toremovename = BOT.getTabName();
							storage.removebotbytabname(toremovename);
						}
					}
				});
				panel.add(btnNewButton_2, "cell 1 10");

				JPanel panel_1 = new JPanel();
				tabbedPane.addTab("Server", null, panel_1, null);
				panel_1.setLayout(new MigLayout("", "[][][grow]", "[][][][][][][][][][][][][][][][][][]"));

				JLabel lblNewLabel_5 = new JLabel("Server name:");
				panel_1.add(lblNewLabel_5, "cell 1 0,alignx trailing");

				txtservername = new JTextField();
				txtservername.setText("Unnamed Server");
				panel_1.add(txtservername, "cell 2 0,growx");
				txtservername.setColumns(10);

				JLabel lblsvrip = new JLabel("Server ip:");
				panel_1.add(lblsvrip, "cell 1 1,alignx trailing");

				textserverip = new JTextField();
				panel_1.add(textserverip, "cell 2 1,growx");
				textserverip.setColumns(10);

				JLabel lblNewLabel_1 = new JLabel("Server port:");
				panel_1.add(lblNewLabel_1, "cell 1 2,alignx trailing");

				textserverport = new JTextField();
				textserverport.setText("25565");
				panel_1.add(textserverport, "cell 2 2,growx");
				textserverport.setColumns(10);

				JLabel lblNewLabel_2 = new JLabel("Connect automatically:");
				panel_1.add(lblNewLabel_2, "cell 1 3,alignx right");

				checkBox = new JCheckBox("");
				panel_1.add(checkBox, "cell 2 3");

				JLabel lblNewLabel_3 = new JLabel("Nickname:");
				panel_1.add(lblNewLabel_3, "cell 1 4,alignx trailing");

				txtNick = new JTextField();
				txtNick.setText("Reticle");
				txtNick.setColumns(10);
				panel_1.add(txtNick, "cell 2 4,growx");

				mojangusername = new JComboBox<String>();
				mojangusername.setModel(new DefaultComboBoxModel<String>(new String[] { storage.default_online_nick }));
				mojangusername.setEnabled(false);

				panel_1.add(mojangusername, "cell 2 4,growx");

				JPanel panel_2 = new JPanel();
				tabbedPane.addTab("Plugins", null, panel_2, null);
				panel_2.setLayout(new MigLayout("", "[grow][][]", "[][grow][]"));

				JLabel lblEnabledPlugins = new JLabel("Enabled plugins:");
				panel_2.add(lblEnabledPlugins, "flowx,cell 0 0");

				final JButton tb = new JButton();
				tb.setText("TEST");

				CheckBoxList list = new CheckBoxList();

				List<PluginSelectionItem> plugins = new ArrayList<PluginSelectionItem>();
				for (String pl : set.plugins) {
					plugins.add(new PluginSelectionItem(pl, true));
				}
				List<String> plugs = storage.pluginManager.getAllPluginNames();
				for (PluginSelectionItem item : plugins) {
					if (plugs.contains(item.getName())) {
						plugs.remove(item.getName());
					}
					boxes.add(item.getBox());
				}
				for (String pl : plugs) {
					boxes.add(new PluginSelectionItem(pl, false).getBox());
				}
				list.setListData(boxes.toArray());
				panel_2.add(list, "cell 0 1,grow");

				JLabel lblthisOnlyAffects = new JLabel("(This only affects event dispatching)");
				lblthisOnlyAffects.setEnabled(false);
				panel_2.add(lblthisOnlyAffects, "cell 0 0");

				panel_3 = new JPanel();
				tabbedPane.addTab("Behavior", null, panel_3, null);
				panel_3.setLayout(new MigLayout("", "[][][grow]", "[][][][][][][][][][][][][][47.00]"));

				JLabel lblActivateTabOn = new JLabel("Activate tab on new message:");
				panel_3.add(lblActivateTabOn, "cell 1 0,alignx right");

				checkactiv = new JCheckBox("");
				panel_3.add(checkactiv, "cell 2 0");

				JLabel lblUseLogoutCommands = new JLabel("Use logout commands upon disconnect:");
				panel_3.add(lblUseLogoutCommands, "cell 1 1,alignx right");

				checkdisccom = new JCheckBox("");
				panel_3.add(checkdisccom, "cell 2 1");

				JLabel lblSendLoginCommands = new JLabel("Send login commands upon connect:");
				panel_3.add(lblSendLoginCommands, "cell 1 2,alignx right");

				checkconcom = new JCheckBox("");
				panel_3.add(checkconcom, "cell 2 2");

				JLabel lblSendAntiafkCommands = new JLabel("Send anti-afk commands periodically:");
				panel_3.add(lblSendAntiafkCommands, "cell 1 3,alignx right");

				checkafkcom = new JCheckBox("");
				panel_3.add(checkafkcom, "cell 2 3");

				JLabel lblMaxLinesTo = new JLabel("Max lines to be displayed (-1 to disable):");
				panel_3.add(lblMaxLinesTo, "cell 1 4,alignx trailing");

				textmaxlines = new JTextField();
				panel_3.add(textmaxlines, "cell 2 4,growx");
				textmaxlines.setColumns(10);

				JLabel lblEnableChatLogger = new JLabel("Enable Chat Logger:");
				panel_3.add(lblEnableChatLogger, "cell 1 5,alignx right");

				JCheckBox checkBox_1 = new JCheckBox("");
				panel_3.add(checkBox_1, "cell 2 5");

				JLabel lblMessageDelay = new JLabel("Message delay (0 to disable):");
				panel_3.add(lblMessageDelay, "cell 1 6,alignx trailing");

				textmessagedelay = new JTextField();
				panel_3.add(textmessagedelay, "cell 2 6,growx");
				textmessagedelay.setColumns(10);

				JLabel lblAntiafkCommandsPeriod = new JLabel("Anti-afk commands period:");
				panel_3.add(lblAntiafkCommandsPeriod, "cell 1 7,alignx trailing");

				textantiafkdelay = new JTextField();
				panel_3.add(textantiafkdelay, "cell 2 7,growx");
				textantiafkdelay.setColumns(10);

				JLabel lblReconnectAutomatically = new JLabel("Reconnect automatically:");
				panel_3.add(lblReconnectAutomatically, "cell 1 8,alignx right");

				checkautoreconnect = new JCheckBox("");
				panel_3.add(checkautoreconnect, "cell 2 8");

				JLabel lblReconnectDelay = new JLabel("Reconnect delay:");
				panel_3.add(lblReconnectDelay, "cell 1 9,alignx trailing");

				textreconnectdelay = new JTextField();
				panel_3.add(textreconnectdelay, "cell 2 9,growx");
				textreconnectdelay.setColumns(10);

				JLabel lblNewLabel_4 = new JLabel("Logout commands:");
				panel_3.add(lblNewLabel_4, "cell 1 10,alignx right,aligny top");

				textlogoutcom = new JTextArea();
				JScrollPane scrollPane = new JScrollPane(textlogoutcom);
				scrollPane.setMinimumSize(new Dimension(21, 50));
				panel_3.add(scrollPane, "cell 2 10,grow");

				JLabel lblNewLabel_6 = new JLabel("Login commands:");
				panel_3.add(lblNewLabel_6, "cell 1 11,alignx trailing,aligny top");

				JScrollPane scrollPane_1 = new JScrollPane((Component) null);
				scrollPane_1.setMinimumSize(new Dimension(21, 50));
				panel_3.add(scrollPane_1, "cell 2 11,grow");

				textlogincom = new JTextArea();
				scrollPane_1.setViewportView(textlogincom);

				JLabel lblNewLabel_7 = new JLabel("Anti-afk commands:");
				panel_3.add(lblNewLabel_7, "cell 1 12,alignx right,aligny top");

				JScrollPane scrollPane_2 = new JScrollPane((Component) null);
				scrollPane_2.setMinimumSize(new Dimension(21, 50));
				panel_3.add(scrollPane_2, "cell 2 12,grow");

				textafkcom = new JTextArea();
				scrollPane_2.setViewportView(textafkcom);

				JLabel lblIgnoredMessages = new JLabel("Ignored Messages");
				panel_3.add(lblIgnoredMessages, "cell 1 13,alignx right,aligny top");

				JScrollPane scrollPane_3 = new JScrollPane((Component) null);
				scrollPane_2.setMinimumSize(new Dimension(21, 50));

				textignore = new JTextArea();
				scrollPane_3.setViewportView(textignore);
				panel_3.add(scrollPane_3, "cell 2 13,grow");
				for (Frame frame : getFrames()) {
					frame.setIconImage(storage.winicon.getImage());
				}

				JLabel lblProtocolVersion = new JLabel("Protocol version:");
				panel_1.add(lblProtocolVersion, "cell 1 5,alignx trailing");

				protocolversion = new JComboBox<String>();
				protocolversion.setModel(new DefaultComboBoxModel<String>(new String[] { "4 (1.7.1/2/3/4/5)", "5(1.7.6/7/8/9/10)", "47(1.8)" }));
				panel_1.add(protocolversion, "cell 2 5,growx");

				JLabel lblNewLabel_9 = new JLabel("Mojang authentication:");
				panel_1.add(lblNewLabel_9, "cell 1 6,alignx right");

				checkmena = new JCheckBox("");
				checkmena.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						if (((JCheckBox) arg0.getSource()).isSelected()) {
							mojangusername.setEnabled(true);
							txtNick.setEnabled(false);
						} else {
							mojangusername.setEnabled(false);
							txtNick.setEnabled(true);
						}
					}
				});
				panel_1.add(checkmena, "cell 2 6");

				JLabel lblUsernameemail = new JLabel("Username/Email:");
				panel_1.add(lblUsernameemail, "cell 1 7,alignx trailing");

				textmusername = new JTextField();
				panel_1.add(textmusername, "cell 2 7,growx");
				textmusername.setColumns(10);

				JLabel lblNewLabel_8 = new JLabel("Password:");
				panel_1.add(lblNewLabel_8, "cell 1 8,alignx trailing");

				mpassword = new JPasswordField();
				panel_1.add(mpassword, "cell 2 8,growx");

				JLabel lblSavePasswordraw = new JLabel("Save password (RAW!):");
				panel_1.add(lblSavePasswordraw, "cell 1 9,alignx right");

				checkspas = new JCheckBox("");
				panel_1.add(checkspas, "cell 2 9");

				JLabel lblSaveAccessToken = new JLabel("Save access token (RAW!):");
				panel_1.add(lblSaveAccessToken, "cell 1 10,alignx right");

				checksacc = new JCheckBox("");
				panel_1.add(checksacc, "cell 2 10");

				JButton btnAuthenticate = new JButton("Authenticate");
				btnAuthenticate.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent arg0) {
						Authenticator auth = Authenticator.fromUsernameAndPassword(textmusername.getText(), new String(mpassword.getPassword()));
						accounts profiles = auth.getProfiles();
						if (profiles != null) {
							// Process list of usernames
							List<profile> prof = profiles.getAllProfiles();
							int profs = prof.size();
							String[] users = new String[profs];
							mojangusernamelist = new HashMap<String, String>();
							for (int i = 0; i < profs; i++) {
								String id = prof.get(i).getID();
								String username = users[i] = prof.get(i).getUsername();
								BOT.mojangusernamelist.put(username, id);
							}
							mojangusername.setModel(new DefaultComboBoxModel<String>(users));
							mojangusername.setSelectedItem(profiles.getSelectedProfile());
							mcurrentusername = profiles.getSelectedProfile().getUsername();
							accesstoken = profiles.getAccessToken();
							playertoken = profiles.getClientToken();
							mojangloginusernameid = profiles.getSelectedProfile().getID();
						}
					}
				});
				panel_1.add(btnAuthenticate, "cell 2 11");

				JLabel lblSavingPasswordIs = new JLabel("Saving password is really not recommended");
				lblSavingPasswordIs.setEnabled(false);
				panel_1.add(lblSavingPasswordIs, "cell 1 12 2 1,alignx center");

				JLabel lblNewLabel_10 = new JLabel(" because it is stored in settings file as text");
				lblNewLabel_10.setEnabled(false);
				panel_1.add(lblNewLabel_10, "cell 1 13 2 1,alignx center");

				JPanel panel_4 = new JPanel();
				tabbedPane.addTab("Appearance", null, panel_4, null);
				panel_4.setLayout(new MigLayout("", "[][][grow]", "[][][][]"));

				JLabel lblFont = new JLabel("Text Font:");
				panel_4.add(lblFont, "cell 1 1,alignx trailing");

				textfont = new JTextField();
				textfont.setEditable(false);
				panel_4.add(textfont, "cell 2 1,alignx left");
				textfont.setColumns(10);
				textfont.setText(set.font.getFamily());
				JLabel lblTextSize = new JLabel("Text size:");
				panel_4.add(lblTextSize, "cell 1 2,alignx trailing");

				textField = new JTextField();
				textField.setEditable(false);
				panel_4.add(textField, "cell 2 2,alignx left");
				textField.setColumns(10);
				textField.setText(set.font.getSize() + "");
				JButton btnChange = new JButton("Change");
				btnChange.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent arg0) {
						JFontChooser fontChooser = new JFontChooser();
						fontChooser.setSelectedFont(font);
						int result = fontChooser.showDialog((Component) arg0.getSource());
						if (result == JFontChooser.OK_OPTION) {
							Font fonter = fontChooser.getSelectedFont();
							font = new Font(fonter.getFamily(), Font.PLAIN, fonter.getSize());
							textField.setText(font.getSize() + "");
							textfont.setText(font.getFamily() + "");
						}
					}
				});
				panel_4.add(btnChange, "cell 1 3");

				JLabel lblRestartApplicationTo = new JLabel("Restart application to change font");
				lblRestartApplicationTo.setEnabled(false);
				panel_4.add(lblRestartApplicationTo, "cell 2 3");
				
				JLabel lblUserProxy = new JLabel("Use proxy:");
				panel_1.add(lblUserProxy, "cell 1 15,alignx right");

				JCheckBox checkproxy = new JCheckBox("");
				checkproxy.setSelected(set.useproxy);
				panel_1.add(checkproxy, "cell 2 15");

				JLabel lblProxyAddress = new JLabel("Proxy Address:");
				panel_1.add(lblProxyAddress, "cell 1 16,alignx trailing");

				textproxyip = new JTextField();
				textproxyip.setText(set.proxyip);
				panel_1.add(textproxyip, "cell 2 16,growx");
				textproxyip.setColumns(10);

				JLabel lblProxyPort = new JLabel("Proxy Port:");
				panel_1.add(lblProxyPort, "cell 1 17,alignx trailing");

				textproxyport = new JTextField();
				textproxyport.setText(set.proxyport+"");
				panel_1.add(textproxyport, "cell 2 17,growx");
				textproxyport.setColumns(10);

				pack();
				setVisible(true);

				set.font = font;

				set_obj_struct sobj = storage.getsettingsobj();
				sobj.protocolversion = protocolversion;
				sobj.txtservername = txtservername;
				sobj.textserverip = textserverip;
				sobj.textserverport = textserverport;
				sobj.checkBox = checkBox;
				sobj.txtNick = txtNick;
				sobj.checkactiv = checkactiv;
				sobj.checkconcom = checkconcom;
				sobj.checkdisccom = checkdisccom;
				sobj.checkafkcom = checkafkcom;
				sobj.textlogincom = textlogincom;
				sobj.textlogoutcom = textlogoutcom;
				sobj.textafkcom = textafkcom;
				sobj.textcurtabname = textcurtabname;
				sobj.textantiafkdelay = textantiafkdelay;
				sobj.checkautoreconnect = checkautoreconnect;
				sobj.textreconnectdelay = textreconnectdelay;
				sobj.textignore = textignore;
				sobj.textmpassword = mpassword;
				sobj.musernames = mojangusername;
				sobj.checkmoj = checkmena;
				sobj.savemojpass = checkspas;
				sobj.saveaccess = checksacc;
				sobj.textmusername = textmusername;
				sobj.messagedelay = textmessagedelay;
				sobj.chatlog = checkBox_1;
				sobj.textmaxlines = textmaxlines;
				sobj.checkproxy = checkproxy;
				sobj.proxyip = textproxyip;
				sobj.proxyport = textproxyport;
				storage.getInstance().setobj.setsettings(set);
			}
		});
	}

	private List<String> getEnabledPlugins(List<JCheckBox> boxes) {
		List<String> enabled = new ArrayList<String>();
		for (JCheckBox box : boxes) {
			if (box.isSelected()) {
				enabled.add(box.getText());
			}
		}
		return enabled;
	}

}

class CheckBoxList extends JList<Object> {
	private static final long serialVersionUID = 1L;
	protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

	public CheckBoxList() {
		setCellRenderer(new CellRenderer());

		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int index = locationToIndex(e.getPoint());

				if (index != -1) {
					JCheckBox checkbox = (JCheckBox) getModel().getElementAt(index);
					checkbox.setSelected(!checkbox.isSelected());
					repaint();
				}
			}
		});

		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	protected class CellRenderer implements ListCellRenderer<Object> {
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			JCheckBox checkbox = (JCheckBox) value;
			checkbox.setBackground(isSelected ? getSelectionBackground() : getBackground());
			checkbox.setForeground(isSelected ? getSelectionForeground() : getForeground());
			checkbox.setEnabled(isEnabled());
			checkbox.setFont(getFont());
			checkbox.setFocusPainted(false);
			checkbox.setBorderPainted(true);
			checkbox.setBorder(isSelected ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);
			return checkbox;
		}
	}
}
