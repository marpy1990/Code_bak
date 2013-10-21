package portal;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pubsub.ext.ReporterServer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class NetworkGraphServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Gson gson = new Gson();
		ServletOutputStream out = response.getOutputStream();
		JsonObject r = new JsonObject();
		Map<String, List<String>> network = ReporterServer.getBrokers();
		r.addProperty("nbUpdated", ReporterServer.brokerUpdated);
		r.add("nb", gson.toJsonTree(network));
		r.add("msgPaths", gson.toJsonTree(ReporterServer.getMsgPaths()));
		out.print(r.toString());
	}
}
