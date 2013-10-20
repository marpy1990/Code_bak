package pubsub.ext;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Reporter {
	private String serverAddr = "localhost";
	private int serverPort = 30000;
	private Logger log = Logger.getLogger("Reporter");
	
	public Reporter() {
		log.setLevel(Level.WARNING);
	}

	public void notifyNbChanged(String string, List<String> nbs) {
		NbChangeMessage msg = new NbChangeMessage(string, nbs);
		try {
			Socket s = new Socket(serverAddr, serverPort);
			ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
			out.writeObject(msg);
			log.info("Nb of " + string + " is " + nbs);
		} catch (Exception e) {
			log.warning("Failed to send " + msg + ", caused by " + e.getMessage());
		}
	}

	public void notifyMsgReceived(String id, LinkedHashSet<String> path) {
		MsgRecvMessage msg = new MsgRecvMessage(id, path);
		try {
			Socket s = new Socket(serverAddr, serverPort);
			ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
			out.writeObject(msg);
			log.info("Send: " + msg);
		} catch (Exception e) {
			log.warning("Failed to send " + msg + ", caused by " + e.getMessage());
		}
	}
}
