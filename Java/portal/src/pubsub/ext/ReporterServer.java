package pubsub.ext;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class ReporterServer implements Runnable {

	private ExecutorService ex = Executors.newFixedThreadPool(1);

	private LinkedBlockingQueue<Object> msgQueue = new LinkedBlockingQueue<Object>();

	// addr maps to nbSet
	public static Map<String, List<String>> brokers = new ConcurrentHashMap<String, List<String>>();
	public static boolean brokerUpdated;

	// msgID maps to addr set
	public static List<MsgRecvMessage> msgPaths = new CopyOnWriteArrayList<MsgRecvMessage>();

	public static Map<String, List<String>> getBrokers() {
		brokerUpdated = false;
		return brokers;
	}

	public static List<MsgRecvMessage> getMsgPaths() {
		List<MsgRecvMessage> old = msgPaths;
		msgPaths = new CopyOnWriteArrayList<MsgRecvMessage>();
		return old;
	}

	public void run() {
		ServerSocket ss;
		try {
			ss = new ServerSocket(30000);
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		new Thread(new ReportHandler()).start();
		
		while (true) {
			try {
				Socket s = ss.accept();
				ex.submit(new MessageReader(s));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private class MessageReader implements Runnable {
		private Socket s;
		MessageReader(Socket s) {
			this.s = s;
		}

		@Override
		public void run() {
			try {
				ObjectInputStream in = new ObjectInputStream(s.getInputStream());
				Object o = in.readObject();
				try {
					msgQueue.put(o);
				} catch (InterruptedException e) {
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					s.close();
				} catch (IOException e) {
				}
			}
		}

	}

	private class ReportHandler implements Runnable {
		public void run() {
			while(true) {
				Object o;
				try {
					o = msgQueue.take();
				} catch (InterruptedException e) {
					continue;
				}
				if (o instanceof MsgRecvMessage) {
					handleMsgReceived((MsgRecvMessage) o);
				} else if (o instanceof NbChangeMessage) {
					handleNbChanged((NbChangeMessage) o);
				}
			}
		}

		private void handleNbChanged(NbChangeMessage o) {
			o.getLocal();
			brokers.put(o.getLocal(), o.getNbs());
			brokerUpdated = true;
		}

		private void handleMsgReceived(MsgRecvMessage o) {
			msgPaths.add(o);
		}
	}
}
