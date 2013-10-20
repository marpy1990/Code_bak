package monitoring.message;

import java.util.Map;

import monitoring.manager.Node;
import monitoring.sensor.ProbeInfo;
import pubsub.msg.P2PMessage;

import com.google.gson.Gson;

public class InitConfigMessage extends P2PMessage {

	public InitConfigMessage(Node node, Map<String, ProbeInfo> piList) {
		Gson gson = new Gson();
		this.put("type", "init");
		this.put("cep", gson.toJson(node.getCepRuleConfigList()));
		this.put("probe", gson.toJson(node.getProbeRuntimeConfigList()));
		this.put("probe-info", gson.toJson(piList));
		this.put("sensor-id", node.getInfo().getId());
	}
}
