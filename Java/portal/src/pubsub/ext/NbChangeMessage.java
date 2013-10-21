package pubsub.ext;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Set;

public class NbChangeMessage implements Serializable {
	private static final long serialVersionUID = -7606272497694659002L;
	private String local;
	private List<String> nbs;
	public NbChangeMessage(String string, List<String> nbs2) {
		super();
		this.local = string;
		this.nbs = nbs2;
	}
	public String getLocal() {
		return local;
	}
	public List<String> getNbs() {
		return nbs;
	}
	@Override
	public String toString() {
		return "NbChangeMessage [local=" + local + ", nbs=" + nbs + "]";
	}
}
