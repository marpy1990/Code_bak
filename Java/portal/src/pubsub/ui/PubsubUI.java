package pubsub.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.net.InetSocketAddress;
import java.util.Enumeration;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.BadLocationException;

import pubsub.Broker;
import pubsub.BrokerRef;
import pubsub.LocalSubTableChangeListener;
import pubsub.MessageHandler;
import pubsub.NbTableChangeListener;
import pubsub.RouteTableChangeListener;
import pubsub.msg.AbstractMessage;
import pubsub.msg.Subscription;
import pubsub.msg.TextMessage;

public class PubsubUI {
	MainFrame mainFrame;
	JFrame loginFrame;
	Broker broker;
	
	private int parsePortNumber(String text) {
		try {
			int res = Integer.parseInt(text);
			if (res > 65535) return -1;
			return res;
		} catch (NumberFormatException e) {
			return -1;
		}
	}
	private final class NbTableModel extends AbstractTableModel implements NbTableChangeListener {
		private Object[] nbTable;
		
		public NbTableModel() {
			nbTable = broker.getNbSet();
			broker.addNbTableChangeListener(this);
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public int getRowCount() {
			return nbTable.length;
		}
		@Override
		public String getColumnName(int columnIndex) {
			switch (columnIndex) {
			case 0: return "Neighbour Address";
			case 1: return "Latency";
			default: return "";
			}
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			BrokerRef b = ((BrokerRef)nbTable[rowIndex]);
			if (columnIndex == 0) return b.getAddress().getAddress().getHostAddress() + ":" + b.getAddress().getPort();
			else return b.getLatency();
		}

		@Override
		public void onNbTableUpdated() {
			SwingUtilities.invokeLater(new Runnable(){
				@Override
				public void run() {
					nbTable = broker.getNbSet();
					fireTableDataChanged();
				}
			});
		}
	}

	private final class RouteTableModel extends AbstractTableModel implements RouteTableChangeListener {
		private static final long serialVersionUID = 1L;
//		private ConcurrentMap<Subscription, Set<InetSocketAddress>> table;
		private Entry<Subscription, InetSocketAddress>[] table;
		public RouteTableModel() {
			Set<Entry<Subscription, InetSocketAddress>> entrySet = 
					broker.getRouteTable().getTable().entrySet();
			table = new Entry[entrySet.size()];
			entrySet.toArray(table);
			broker.getRouteTable().addChangeListener(this);
		}
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Entry<Subscription, InetSocketAddress> en = table[rowIndex];
			switch (columnIndex) {
			case 0: return en.getKey().getID();
			case 1: return en.getKey().getTopic();
			case 2: return en.getValue().toString();
			default: return "";
			}
		}

		@Override
		public int getRowCount() {
			return table.length;
		}

		@Override
		public String getColumnName(int columnIndex) {
			switch (columnIndex) {
			case 0: return "Sub ID";
			case 1: return "Topic";
			case 2: return "Next Hops";
			default: return "";
			}
		}

		@Override
		public int getColumnCount() {
			return 3;
		}

		@Override
		public void onRouteTableUpdated() {
			SwingUtilities.invokeLater(new Runnable(){
				@Override
				public void run() {
					Set<Entry<Subscription, InetSocketAddress>> entrySet = 
							broker.getRouteTable().getTable().entrySet();
					table = new Entry[entrySet.size()];
					entrySet.toArray(table);
					fireTableDataChanged();
				}
			});
		}
	}
	private class MessageInput extends JPanel {
		JTextField topicInput;
		JTextArea contentInput;
		JButton sendButton;
		public MessageInput() {
			topicInput = new JTextField();
//			titleInput.setPreferredSize(new Dimension(300, 30));
			contentInput = new JTextArea();
			contentInput.setBorder(BorderFactory.createEtchedBorder());
//			contentInput.setPreferredSize(new Dimension(300, 100));
			sendButton = new JButton("Send");
			GridBagLayout g = new GridBagLayout();
			setLayout(g);
			GridBagConstraints c = new GridBagConstraints();
			c.insets = new Insets(5, 5, 5, 5);
			c.fill = GridBagConstraints.BOTH;
			JLabel topicLabel = new JLabel("Topic: ");
			g.setConstraints(topicLabel, c);
			add(topicLabel);
			c.weightx = 1.0;
			g.setConstraints(topicInput, c);
			add(topicInput);
			c.weightx = 0.0;
			c.weighty = 1.0;
			c.gridx = 2;
			c.gridy = 0;
			c.gridheight = 2;
			g.setConstraints(sendButton, c);
			add(sendButton);
			c.gridx = 0;
			c.gridy = 1;
			c.gridheight = 1;
			JLabel contentLabel = new JLabel("Content: ");
			g.setConstraints(contentLabel, c);
			add(contentLabel);
			c.weightx = 1.0;
			c.gridx = 1;
			g.setConstraints(contentInput, c);
			add(contentInput);
			
			sendButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					broker.publish(new TextMessage(topicInput.getText(), contentInput.getText()));
				}
			});
		}
	}
	private class MainFrame extends JFrame {
		JSplitPane mainSplitPane, rightSplitPane;
		JTable routeTable, nbTable, lsTable;
		JTextPane logViewer;
		MessageInput input;
		public MainFrame() {
			super(broker.getBrokerID() + " - Pub/Sub Demo System ");
			setIconImage(new ImageIcon("images/icon.gif").getImage());
			setDefaultCloseOperation(EXIT_ON_CLOSE);
//			setLocationRelativeTo(null);
			setBounds(100, 100, 900, 600);
			Container pane = getContentPane();
			pane.add(new ConnectToolbar(), BorderLayout.NORTH);
			
			rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
			mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
			mainSplitPane.setRightComponent(rightSplitPane);
			mainSplitPane.setResizeWeight(0.4);
			pane.add(mainSplitPane, BorderLayout.CENTER);
			
			routeTable = new JTable();
			
			routeTable.setModel(new RouteTableModel());
			routeTable.setFillsViewportHeight(true);
			
			nbTable = new JTable();
			nbTable.setModel(new NbTableModel());
			nbTable.setFillsViewportHeight(true);
			nbTable.setBorder(null);
			routeTable.setBorder(null);
			
			lsTable = new JTable();
			lsTable.setModel(new LocalSubTableModel());
			lsTable.setFillsViewportHeight(true);
			lsTable.setBorder(null);
			JPanel left = new JPanel();
			left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
			left.add(new JScrollPane(nbTable));
			left.add(new JScrollPane(lsTable));
			left.add(new JScrollPane(routeTable));
			mainSplitPane.setLeftComponent(left);
			
			logViewer = new JTextPane();
			rightSplitPane.setResizeWeight(0.6);
			rightSplitPane.setTopComponent(logViewer);
			rightSplitPane.setBottomComponent(new MessageInput());
			rightSplitPane.setDividerSize(0);
			rightSplitPane.setBorder(null);
			addComponentListener(new ComponentAdapter() {
				private void resetDividers() {
					mainSplitPane.setDividerLocation(0.4);
					rightSplitPane.setDividerLocation(0.8);
				}
				@Override
				public void componentShown(ComponentEvent e) {
					resetDividers();
				}
			});
			
		}
	}
	private class UIMsgHandler implements MessageHandler {
		@Override
		public void handleMessage(AbstractMessage msg) {
			TextMessage t = (TextMessage)msg;
			int length = mainFrame.logViewer.getDocument().getLength();
			try {
				mainFrame.logViewer.getDocument().insertString(length, "\n["+t.getTopic()+"] "+t.getContent() + " path:" + t.getPath(), null);
			} catch (BadLocationException e) {
				// do nothing...
			}
		}
	}
	
	private class LoginFrame extends JFrame {
		private JButton login;
		private JTextField ip;
		private JLabel infoLabel;

		private void info(final String info) {
			infoLabel.setText(info);
		}

		public LoginFrame() {
			// no border
			this.setUndecorated(true);
			// locate at desktop center
			setLocationRelativeTo(null);
			// label
			JLabel portLabel = new JLabel("Start pub/sub service on port:");
			portLabel.setForeground(Color.WHITE);
			portLabel.setBounds(25, 150, 200, 30);

			infoLabel = new JLabel();
			infoLabel.setForeground(Color.WHITE);
			infoLabel.setBounds(25, 120, 250, 30);
			// background image
			ImageIcon img = new ImageIcon("images/login.jpg");
			JLabel bgicon = new JLabel(img);
			bgicon.setBounds(0, 0, img.getIconWidth(), img.getIconHeight());
			JLayeredPane pane = this.getLayeredPane();
			// absolute layout
			pane.setLayout(null);
			// add bg image at bottom
			pane.add(bgicon, Integer.MIN_VALUE);
			// set JFrame size to size of the image
			setSize(img.getIconWidth(), img.getIconHeight());

			// the start button
			login = new ImageButton("connect");
			login.setLocation(105, 190);
			login.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					new Thread(new Runnable() {

						@Override
						public void run() {
							info("Starting service...");
							int port = parsePortNumber(ip.getText());
							if (port==-1) {
								info("Invalid port number");
								return;
							}
							try {
								broker = new Broker(port);
							} catch (Exception e1) {
								info(e1.getMessage());
								return;
							}
							mainFrame = new MainFrame();
							loginFrame.setVisible(false);
							mainFrame.setVisible(true);
						}
					}).start();
				}
			});

			// the textfield for port number
			ip = new JTextField("20000");
			ip.setBounds(210, 150, 50, 30);
			pane.add(infoLabel, 0);
			pane.add(ip, 0);
			pane.add(portLabel, 0);
			pane.add(login, 0);
		}
	}

	
	private class ConnectToolbar extends JPanel {
		private JTextField addr, port, topic, unsubID;
		private JLabel info;
		private JButton connect, subscribe, unsubscribe;
		public ConnectToolbar() {
			setLayout(new FlowLayout(FlowLayout.LEFT));
			addr = new JTextField("localhost");
			addr.setPreferredSize(new Dimension(60, 30));
			port = new JTextField("20000");
			port.setPreferredSize(new Dimension(50, 30));
			connect = new JButton("Connect");
			info = new JLabel("");
			info.setPreferredSize(new Dimension(50, 30));
			topic = new JTextField();
			topic.setPreferredSize(new Dimension(50, 30));
			subscribe = new JButton("Subscribe");
			unsubID = new JTextField();
			unsubID.setPreferredSize(new Dimension(100, 30));
			unsubscribe = new JButton("Unsubscribe");
			add(new JLabel("Neighbor address: "));
			add(addr);
			add(new JLabel("Port: "));
			add(port);
			add(connect);
			add(info);
			add(new JLabel("Topic: "));
			add(topic);
			add(subscribe);
			add(new JLabel("Unsub ID:"));
			add(unsubID);
			add(unsubscribe);
			connect.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					SwingUtilities.invokeLater(new Runnable() {
						
						@Override
						public void run() {
							info.setText("Connecting...");
							int portNumber = parsePortNumber(port.getText());
							if (portNumber == -1) {
								info.setText("Invalid port number.");
								return;
							}
							try {
								broker.connect(new InetSocketAddress(addr.getText(), portNumber));
							} catch (Exception e) {
								info.setText("Failed.");
								return;
							}
							info.setText("OK");
						}
					});
				}
			});
			subscribe.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					broker.subscribe(new Subscription(topic.getText()), new UIMsgHandler());
				}
			});
			
			unsubscribe.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					broker.unsubscribe(unsubID.getText());
				}
			});
		}
	}

	private final class LocalSubTableModel extends AbstractTableModel implements LocalSubTableChangeListener {
		private static final long serialVersionUID = 1L;
//		private ConcurrentMap<Subscription, Set<InetSocketAddress>> table;
		private Entry<Subscription, MessageHandler>[] table;
		public LocalSubTableModel() {
			Set<Entry<Subscription, MessageHandler>> entrySet = 
					broker.getRouteTable().getLocalSubTable().entrySet();
			table = new Entry[entrySet.size()];
			entrySet.toArray(table);
			broker.getRouteTable().addLocalSubTableChangeListener(this);
		}
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Entry<Subscription, MessageHandler> en = table[rowIndex];
			switch (columnIndex) {
			case 0: return en.getKey().getID();
			case 1: return en.getKey().getTopic();
			case 2: return en.getValue().toString();
			default: return "";
			}
		}

		@Override
		public int getRowCount() {
			return table.length;
		}

		@Override
		public String getColumnName(int columnIndex) {
			switch (columnIndex) {
			case 0: return "Sub ID";
			case 1: return "Topic";
			case 2: return "Handler";
			default: return "";
			}
		}

		@Override
		public int getColumnCount() {
			return 3;
		}

		@Override
		public void onLocalSubTableUpdated() {
			SwingUtilities.invokeLater(new Runnable(){
				@Override
				public void run() {
					Set<Entry<Subscription, MessageHandler>> entrySet = 
							broker.getRouteTable().getLocalSubTable().entrySet();
					table = new Entry[entrySet.size()];
					entrySet.toArray(table);
					fireTableDataChanged();
				}
			});
		}
	}

	public static void initGlobalFontSetting(Font fnt) {
		FontUIResource fontRes = new FontUIResource(fnt);
		for (Enumeration<Object> keys = UIManager.getDefaults().keys(); keys
				.hasMoreElements();) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof FontUIResource)
				UIManager.put(key, fontRes);
		}
	}

	public static void main(String[] args) {
//		try {
//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//		} catch (Exception e) {
//		}
//		initGlobalFontSetting(new Font("Î¢ÈíÑÅºÚ", Font.PLAIN, 12));

		PubsubUI ui = new PubsubUI();
		
		ui.loginFrame = ui.new LoginFrame();
		ui.loginFrame.setVisible(true);
		ui.loginFrame.setIconImage(new ImageIcon("images/icon.gif").getImage());
	}
}