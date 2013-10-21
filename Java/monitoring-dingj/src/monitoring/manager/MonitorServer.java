package monitoring.manager;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;


public class MonitorServer {

	private DBHelper db = DBHelper.INSTANCE;
	
	private MonitorServer() {
		
	}


	public static void main(String[] args) {
		org.eclipse.jetty.server.Server webServer = new Server();
		Connector connector = new SelectChannelConnector();
		connector.setPort(40000);
		webServer.addConnector(connector);

		WebAppContext context = new WebAppContext();
		context.setContextPath("/");
		context.setDescriptor("./WebContent/WEB-INF/web.xml");
		context.setResourceBase("./WebContent");
		context.setDefaultsDescriptor("./lib/webdefault.xml");
		context.setParentLoaderPriority(true);

		webServer.setHandler(context);
		webServer.setStopAtShutdown(true);
		webServer.setSendServerVersion(true);
		try {
			webServer.start();
		} catch (Exception e) {
			// logger.severe("Can't start web server.");
			System.exit(0);
		}
	}
}
