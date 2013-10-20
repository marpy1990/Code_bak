package portal;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import monitoring.manager.CepRuleConfigWithSource;
import monitoring.manager.Manager;
import monitoring.manager.Network;
import monitoring.manager.Node;
import monitoring.sensor.ProbeRuntimeConfig;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class NetworkStructureServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletOutputStream out = response.getOutputStream();
		String nodeId = request.getParameter("node");
		Network n = Manager.getInstance().getNetworkDao().getNetwork();
		if (nodeId == null || nodeId.equals("startpage")) {
			// generates functional tree
			JsonArray systemArray = new JsonArray();
			
			JsonObject eventViewer = new JsonObject();
			eventViewer.addProperty("text", "Event Viewer");
			eventViewer.addProperty("id", "eventviewer");
			eventViewer.addProperty("leaf", true);
			eventViewer.addProperty("type", "function");
			eventViewer.addProperty("iconCls", "event-viewer");
			systemArray.add(eventViewer);
			
			JsonObject pubsubInsight = new JsonObject();
			pubsubInsight.addProperty("text", "Monitored Network");
			pubsubInsight.addProperty("id", "pubsub");
			pubsubInsight.addProperty("type", "function");
			pubsubInsight.addProperty("leaf", true);
			pubsubInsight.addProperty("iconCls", "pubsub");
			systemArray.add(pubsubInsight);
			
			JsonObject systemConfiguration = new JsonObject();
			systemConfiguration.addProperty("text", "System Configuration");
			systemConfiguration.addProperty("id", "sysconf");
			systemConfiguration.addProperty("type", "function");
			systemConfiguration.addProperty("leaf", false);
			systemConfiguration.addProperty("iconCls", "sysconf");
			systemConfiguration.addProperty("expanded", true);
			systemArray.add(systemConfiguration);
			
			JsonArray sysconfArray = new JsonArray();
			systemConfiguration.add("children", sysconfArray);
			
			JsonObject subscriptionManager = new JsonObject();
			subscriptionManager.addProperty("text", "Subscriptions");
			subscriptionManager.addProperty("id", "subman");
			subscriptionManager.addProperty("type", "function");
			subscriptionManager.addProperty("leaf", true);
			subscriptionManager.addProperty("iconCls", "sub-man");
			sysconfArray.add(subscriptionManager);
			
			JsonObject eventTypeManager = new JsonObject();
			eventTypeManager.addProperty("text", "Event Types");
			eventTypeManager.addProperty("id", "etman");
			eventTypeManager.addProperty("type", "function");
			eventTypeManager.addProperty("leaf", true);
			eventTypeManager.addProperty("iconCls", "et-man");
			sysconfArray.add(eventTypeManager);
			
			JsonObject cepOperatorDefinition = new JsonObject();
			cepOperatorDefinition.addProperty("text", "CEP Operator Definition");
			cepOperatorDefinition.addProperty("id", "cepdef");
			cepOperatorDefinition.addProperty("type", "function");
			cepOperatorDefinition.addProperty("leaf", true);
			cepOperatorDefinition.addProperty("iconCls", "cep-edit");
			sysconfArray.add(cepOperatorDefinition);
			
			JsonObject probeDefinition = new JsonObject();
			probeDefinition.addProperty("text", "Probe Definition");
			probeDefinition.addProperty("id", "probedef");
			probeDefinition.addProperty("type", "function");
			probeDefinition.addProperty("leaf", true);
			probeDefinition.addProperty("iconCls", "probe-edit");
			sysconfArray.add(probeDefinition);
			
			JsonObject network = new JsonObject();
			network.addProperty("text", "Machines");
			network.addProperty("id", "network");
			network.addProperty("leaf", false);
			network.addProperty("type", "function");
			network.addProperty("iconCls", "network");
			network.addProperty("expanded", true);
			systemArray.add(network);
			
			JsonArray networkArray = new JsonArray();
			
			for (Node m : n.getNodes().values()) {
				JsonObject mo = new JsonObject();
				mo.addProperty("text", m.getInfo().getId());
				mo.addProperty("id", m.getInfo().getId());
				mo.addProperty("type", "node");
				mo.addProperty("leaf", false);
				mo.addProperty("iconCls", "machine");
				networkArray.add(mo);
			}
			network.add("children", networkArray);
			out.println(systemArray.toString());
		} else if (nodeId.equals("network")) {
			Gson gson = new Gson();
			out.print('[');
			boolean first = true;
			for (Node node : n.getNodes().values()) {
				if (first) first = false;
				else out.print(',');
				out.print(gson.toJson(node.getInfo()));
			}
			out.println(']');
		} else if (nodeId.equals("machineCombo")) {
			JsonArray idArray = new JsonArray();
			for (String id : n.getNodes().keySet()) {
				JsonObject o = new JsonObject();
				o.addProperty("id", id);
				idArray.add(o);
			}
			out.println(idArray.toString());
		} else {
			// for a single node
			Node node = n.getNode(nodeId);
			if (node==null) {
				out.println("[]");
				return;
			}
			JsonArray array = new JsonArray();
			JsonObject probeFolder = new JsonObject();
			probeFolder.addProperty("text", "Deployed Probes");
			probeFolder.addProperty("mid", nodeId);
			probeFolder.addProperty("type", "probeOverview");
			probeFolder.addProperty("leaf", true);
			probeFolder.addProperty("iconCls", "probe");
			probeFolder.addProperty("expanded", true);
			
//			JsonArray probeArray = new JsonArray();
//			for (ProbeRuntimeConfig prc : node.getProbeRuntimeConfigList()) {
//				JsonObject probeItem = new JsonObject();
//				probeItem.addProperty("text", prc.getProbeId());
//				probeItem.addProperty("mid", nodeId);
//				probeItem.addProperty("iid", prc.getPrcId());
//				probeItem.addProperty("type", "probe");
//				probeItem.addProperty("leaf", true);
//				probeItem.addProperty("iconCls", "probe");
//				probeArray.add(probeItem);
//			}
//			probeFolder.add("children", probeArray);
			
			JsonObject cepFolder = new JsonObject();
			cepFolder.addProperty("text", "Deployed CEP Operators");
			cepFolder.addProperty("mid", nodeId);
			cepFolder.addProperty("type", "cepOverview");
			cepFolder.addProperty("leaf", true);
			cepFolder.addProperty("iconCls", "cep");
			cepFolder.addProperty("expanded", true);
			
//			JsonArray cepArray = new JsonArray();
//			for (CepRuleConfigWithSource r :node.getCepRuleConfigList()) {
//				JsonObject cepItem = new JsonObject();
//				cepItem.addProperty("text", r.getCrc().getRuleName());
//				cepItem.addProperty("iid", r.getId());
//				cepItem.addProperty("mid", nodeId);
//				cepItem.addProperty("type", "cep");
//				cepItem.addProperty("leaf", true);
//				cepItem.addProperty("iconCls", "cep");
//				cepArray.add(cepItem);
//			}
//			cepFolder.add("children", cepArray);
			array.add(probeFolder);
			array.add(cepFolder);
			
			
			out.println(array.toString());
		}
	}

}
