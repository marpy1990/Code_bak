package pubsub.ext;

import java.io.Serializable;
import java.util.LinkedHashSet;

public class MsgRecvMessage implements Serializable {
	private static final long serialVersionUID = -8915557577331788105L;
	private String msgId;
	private LinkedHashSet<String> path;
	public MsgRecvMessage(String msgId, LinkedHashSet<String> path) {
		super();
		this.msgId = msgId;
		this.path = path;
	}
	public String getMsgId() {
		return msgId;
	}
	public LinkedHashSet<String> getPath() {
		return path;
	}
	@Override
	public String toString() {
		return "MsgRecvMessage [msgId=" + msgId + ", path=" + path + "]";
	}
}
