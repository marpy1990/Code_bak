package monitoring.manager;

import java.util.ArrayList;
import java.util.List;

import cep.CepRuleConfig;

public class CepRuleConfigWithSource {
	public CepRuleConfigWithSource(CepRuleConfig crc,
			List<CepRuleSubscriptionRequest> subReq) {
		super();
		this.crc = crc;
		this.subReq = subReq;
	}
	private String id;
	private CepRuleConfig crc;
	private List<CepRuleSubscriptionRequest> subReq;
	public CepRuleConfig getCrc() {
		return crc;
	}
	public void setCrc(CepRuleConfig crc) {
		this.crc = crc;
	}
	public List<CepRuleSubscriptionRequest> getSubReq() {
		return subReq;
	}
	public void setSubReq(List<CepRuleSubscriptionRequest> subReq) {
		this.subReq = subReq;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CepRuleConfigWithSource [crc=");
		builder.append(crc);
		builder.append(", subReq=");
		builder.append(subReq);
		builder.append("]");
		return builder.toString();
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
