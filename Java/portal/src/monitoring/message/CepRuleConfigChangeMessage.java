package monitoring.message;

import monitoring.manager.CepRuleConfigWithSource;
import pubsub.msg.P2PMessage;

import com.google.gson.Gson;

public class CepRuleConfigChangeMessage extends P2PMessage {
	
	private static final long serialVersionUID = -6027065264535159152L;
	public CepRuleConfigChangeMessage(String type, CepRuleConfigWithSource r) {
		super();
		this.put("type", type);
		this.put("config", new Gson().toJson(r));
	}
	
	public CepRuleConfigChangeMessage(String type, String id) {
		super();
		this.put("type", type);
		this.put("id", id);
	}
	
}
