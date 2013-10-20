package portal;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import monitoring.manager.Manager;
import monitoring.manager.Node;
import monitoring.sensor.ProbeRuntimeConfig;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ProbeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ProbeServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletOutputStream out = response.getOutputStream();
		String id = request.getParameter("mid");
		if (id==null) return;
		Node n = Manager.getInstance().getNetworkDao().getNetwork().getNode(id);
		if (n == null) return;
		Collection<ProbeRuntimeConfig> prcList = n.getProbeRuntimeConfigList();
		JsonArray a = new JsonArray();
		JsonObject o;
		for (ProbeRuntimeConfig prc : prcList) {
			o = new JsonObject();
			o.addProperty("probeID", prc.getProbeId());
			o.addProperty("interval", prc.getInterval());
			o.addProperty("timeunit", prc.getTimeunit().toString());
			o.addProperty("running", true);
			a.add(o);
		}
		out.print(a.toString());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
