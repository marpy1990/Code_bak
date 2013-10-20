package portal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import monitoring.manager.CepRuleConfigWithSource;
import monitoring.manager.CepRuleDefinitionDAO;
import monitoring.manager.CepRuleSubscriptionRequest;
import monitoring.manager.Manager;
import monitoring.manager.NetworkDAO;
import monitoring.manager.Node;
import monitoring.manager.PortalSubscriptionBean;
import cep.CepRuleConfig;
import cep.CepRuleDefinition;
import cep.CepRuleParamDefinition;

import com.google.gson.Gson;

public class CepServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CepServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletOutputStream out = response.getOutputStream();
		String id = request.getParameter("mid");
		if (id==null) return;
		NetworkDAO dao = Manager.getInstance().getNetworkDao();
		Node node = dao.getNetwork().getNode(id);
		if (node == null) return;
		String type = request.getParameter("type");
		if (type == null) {
			// get table
			Collection<CepRuleConfigWithSource> eplList = node.getCepRuleConfigList();
			out.print(new Gson().toJson(eplList));
			return;
		} else if (type.equals("add")) {
			// add rule
			String ruleName = request.getParameter("ruleName");
			CepRuleConfig crc = new CepRuleConfig();
			String ceTypeName = request.getParameter("complexEventType");
			PortalSubscriptionBean sb = new PortalSubscriptionBean(
					ceTypeName, "id=" + id);
			Manager.getInstance().createPortalSubscription(sb);
			
			crc.setComplexEventType(ceTypeName);
			
			
			crc.setRuleName(ruleName);
			crc.setClassName(Manager.getInstance().lookupCepRuleClassName(ruleName));
			crc.setRunning(true);
			
			Map<String, Object> params = new HashMap<String, Object>();
			
			CepRuleDefinitionDAO defDao = Manager.getInstance().getCepRuleDefinitionDao();
			CepRuleDefinition def = defDao.findByName(ruleName);
			for (CepRuleParamDefinition paramDef : def.getParamDefinitions()) {
				String paramName = paramDef.getParamName();
				String paramValue = request.getParameter(paramName);
				if (paramValue == null) continue;
				if (paramDef.getType().equals("string")) {
					params.put(paramName, paramValue);
				} else if (paramDef.getType().equals("number")) {
					double doubleValue = Double.parseDouble(paramValue);
					params.put(paramName, doubleValue);
				} else if (paramDef.getType().equals("timeunit")) {
					long timeValue = (new Double(paramValue)).longValue();
					int tu = new Integer(request.getParameter(paramName+"tu"));
					params.put(paramName, timeValue * tu);
				}
			}
			
			crc.setParams(params);
			
			String[] sourceTypes = request.getParameterValues("eventType");
			String[] sourceFilters = request.getParameterValues("sourceFilter");
			List<CepRuleSubscriptionRequest> subReqList = new ArrayList<CepRuleSubscriptionRequest>();
			for (int i=0; i<sourceTypes.length; i++) {
				subReqList.add(new CepRuleSubscriptionRequest(sourceTypes[i], sourceFilters[i]));
			}
			CepRuleConfigWithSource r = new CepRuleConfigWithSource(crc, subReqList);
			
			// modify database
			node.putCepRuleConfig(r);
			
			Manager.getInstance().sendCepRuleConfigChange("add", r, id);
			dao.save();
		} else if (type.equals("stop")) {
			// stop rule
			String ruleId = request.getParameter("id");
			Manager.getInstance().sendCepRuleStopCommand(ruleId, id);
			
			// modify database
			node.findCepRuleConfigById(ruleId).getCrc().setRunning(false);
			dao.save();
		} else if (type.equals("delete")) {
			// stop rule
			String ruleId = request.getParameter("id");
			Manager.getInstance().sendCepRuleStopCommand(ruleId, id);
			
			// modify database
			node.removeCepRuleConfig(ruleId);
			dao.save();
		} else if (type.equals("edit")) {
			String ruleId = request.getParameter("id");
			
		} else if (type.equals("start")) {
			// start rule
			String ruleId = request.getParameter("id");
			CepRuleConfigWithSource r = node.findCepRuleConfigById(ruleId);
			r.getCrc().setRunning(true);
			Manager.getInstance().sendCepRuleConfigChange("add",
					r, id);
			
			// modify database
			dao.save();
		}
	}
}
