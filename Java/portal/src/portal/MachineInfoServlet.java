package portal;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import monitoring.manager.Manager;
import monitoring.manager.Node;
import monitoring.manager.SensorInfo;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class MachineInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MachineInfoServlet() {
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
		SensorInfo si = n.getInfo();
		JsonArray a = new JsonArray();
		
		JsonObject o = new JsonObject();
		o.addProperty("key", "Machine ID");
		o.addProperty("value", id);
		o.addProperty("type", "basic");
		a.add(o);
		
		o = new JsonObject();
		o.addProperty("key", "IP");
		o.addProperty("value", si.getIp());
		o.addProperty("type", "basic");
		a.add(o);
		
		o = new JsonObject();
		o.addProperty("key", "Port");
		o.addProperty("value", si.getPort());
		o.addProperty("type", "basic");
		a.add(o);
		
		o = new JsonObject();
		o.addProperty("key", "Location");
		o.addProperty("value", si.getLocation());
		o.addProperty("type", "basic");
		a.add(o);
		
		o = new JsonObject();
		o.addProperty("key", "Machine Name");
		o.addProperty("value", si.getMachineName());
		o.addProperty("type", "basic");
		a.add(o);
		
		o = new JsonObject();
		o.addProperty("key", "Operating System");
		o.addProperty("value", si.getOs());
		o.addProperty("type", "basic");
		a.add(o);
		
		out.print(a.toString());
	}

}
