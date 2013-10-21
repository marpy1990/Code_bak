package pubsub;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import pubsub.msg.ConnectAck;
import pubsub.msg.ConnectRequest;
import pubsub.msg.ConnectResponse;
public class BioMessenger implements Messenger {
	
	private ExecutorService ex = Executors.newFixedThreadPool(1);
	private Broker broker;
	private int port;
	private LinkedBlockingQueue<Object> msgQueue = new LinkedBlockingQueue<Object>();
	private Logger log = Logger.getLogger("Messenger");
	
	public BioMessenger(int port, Broker broker) {
		log.setLevel(Level.WARNING);
		this.port = port;
		this.broker = broker;
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
				if (o instanceof ConnectRequest) {
					ConnectResponse resp = new ConnectResponse(broker.getLocalAddr(), System.nanoTime());
					ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
					out.writeObject(resp);
					Object ack = in.readObject();
					if (ack instanceof ConnectAck) {
						broker.addNb(((ConnectAck) ack).getClientAddr(), ((ConnectAck) ack).getLatency());
					}
				} else {
					try {
						msgQueue.put(o);
					} catch (InterruptedException e) {}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					s.close();
				} catch (IOException e) {}
			}
		}
		
	}

	@Override
	public void run() {
		
		Thread listenerNotifier = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					try {
						Object msg = msgQueue.take();
						broker.handleMessage(msg);
					} catch (InterruptedException e) {
						continue;
					}
				}
			}
		});
		listenerNotifier.setName("MessengerNotifier");
		listenerNotifier.start();
		
		ServerSocket ss;
		try {
			ss = new ServerSocket(port);
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		};
		while (true) {
			try {
				Socket s = ss.accept();
				ex.submit(new MessageReader(s));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void send(Serializable msg, InetSocketAddress target)
			throws IOException {
		Socket s = new Socket();
		s.connect(target);
		ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
		out.writeObject(msg);
		
		log.info("Successfully send " + msg +" from " + broker.getBrokerID() 
				+ " to " + target);
	}

	@Override
	public void connect(InetSocketAddress local, InetSocketAddress neighbor)
			throws IOException {
		Socket s = new Socket();
		s.connect(neighbor);
		long connectTime = System.nanoTime();
		ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
		out.writeObject(new ConnectRequest(local, connectTime));
		ObjectInputStream in = new ObjectInputStream(s.getInputStream());
		Object o;
		try {
			o = in.readObject();
			if (o instanceof ConnectResponse) {
				ConnectResponse resp = (ConnectResponse)o;
				int latency = (int) ((System.nanoTime() - resp.getTimestamp())/1000000);
				out.writeObject(new ConnectAck(local, latency));
				broker.addNb(resp.getServerAddr(), latency);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
