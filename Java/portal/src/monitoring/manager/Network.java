package monitoring.manager;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

/**
 * The network contains information about all Nodes.
 * @author Yu Cheng
 *
 */
public class Network {
	private Map<String, Node> nodes;
	private String configFileName;
	public Network() {
		nodes = new HashMap<String, Node>();
	}
	public Node getNode(String nodeId) {
		return nodes.get(nodeId);
	}
	public Map<String, Node> getNodes() {
		return nodes;
	}
	public void addNode(Node node) {
		nodes.put(node.getInfo().getId(), node);
	}
	
	public void login(String id) {
		nodes.get(id).setOnline(true);
	}
}
