package pubsub;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import pubsub.msg.ConnectAck;
import pubsub.msg.ConnectRequest;
import pubsub.msg.ConnectResponse;

/**
 * This is the <code>Messenger</code> implementation with <code>java.nio</code>
 * utilities, i.e. <code>ServerSocketChannel</code>s and <code>SocketChannel</code>s.
 * This implementation is not suitable for local simulation mode, because it leads to 
 * nearly 100% CPU usage, especially in a short time since the simulation starts.
 * Currently it is not used and a <code>BioMessenger</code> is used instead.
 * @author Yu Cheng
 *
 */
public class NioMessenger implements Messenger{
	private int port;
	private Broker broker;
	private LinkedBlockingQueue<Object> msgQueue = new LinkedBlockingQueue<Object>();
	
	private ServerSocketChannel serverSocketChannel;
	private Selector selector;
	private Logger log = Logger.getLogger("Messenger");
	private ByteBuffer inBuffer = ByteBuffer.allocate(4096);

	public NioMessenger(int port, Broker broker) throws IOException {
		this.port = port;
		this.broker = broker;
		
		selector = Selector.open();
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.socket().setReuseAddress(true);
		serverSocketChannel.socket().bind(new InetSocketAddress(port));
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		log.info("Messenger started on port " + port);
	}
	
	public void service() throws IOException {
		selector.select();
		Iterator<SelectionKey> keyItr = selector.selectedKeys().iterator();
		while (keyItr.hasNext()) {
			SelectionKey key = keyItr.next();
			keyItr.remove();
			handleKey(key);
		}
	}
	
	private void handleKey(SelectionKey key) throws IOException {
		SocketChannel sc;
		if (key.isAcceptable()) {
			sc = ((ServerSocketChannel)key.channel()).accept();
			sc.configureBlocking(false);
			sc.register(selector, SelectionKey.OP_READ);
		} else if (key.isReadable()) {
			sc = (SocketChannel)key.channel();
			Object o = readObject(sc);
			if (o instanceof ConnectRequest) {
				ConnectResponse resp = new ConnectResponse(((ConnectRequest) o).getAddr(), System.nanoTime());
				writeObject(sc, resp);
			} else if (o instanceof ConnectAck) {
				broker.addNb(((ConnectAck) o).getClientAddr(), ((ConnectAck) o).getLatency());
			} else {
				try {
					msgQueue.put(o);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} else if (key.isWritable()) {
			
		}
	}

	public void run() {
		// This thread will read from message queue and notify the listerner.
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
		
		while (true) {
			try {
				service();
			} catch (IOException e) {
			}
		}
	}
	/* (non-Javadoc)
	 * @see pubsub.Messenger#send(java.io.Serializable, java.net.InetSocketAddress)
	 */
	public void send(Serializable message, InetSocketAddress target) throws IOException {
		SocketChannel sc = SocketChannel.open();
		sc.configureBlocking(true);
		sc.connect(target);
		writeObject(sc, message);
		sc.close();
	}

	/* (non-Javadoc)
	 * @see pubsub.Messenger#connect(java.net.InetSocketAddress, java.net.InetSocketAddress)
	 */
	public void connect(InetSocketAddress localAddr, InetSocketAddress target) throws IOException {
		SocketChannel sc = SocketChannel.open();
		sc.configureBlocking(true);
		sc.connect(target);
		long connectTime = System.nanoTime();
		writeObject(sc, new ConnectRequest(localAddr, connectTime));
		Object o = readObject(sc);
		if (o instanceof ConnectResponse) {
			ConnectResponse resp = (ConnectResponse)o;
			int latency = (int) ((System.nanoTime() - resp.getTimestamp())/1000000);
			writeObject(sc, new ConnectAck(localAddr, latency));
			broker.addNb(resp.getServerAddr(), latency);
		}
	}
	
	
	private Object readObject(SocketChannel sc) throws IOException {
		inBuffer.clear();
		int count = sc.read(inBuffer);
		if (count>0) {
			ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(inBuffer.array()));
			try {
				Object o = in.readObject();
				return o;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		System.out.println(count);
		return null;
	}
	
	private void writeObject(SocketChannel sc, Object o) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(baos);
		out.writeObject(o);
		sc.write(ByteBuffer.wrap(baos.toByteArray()));
	}
}
