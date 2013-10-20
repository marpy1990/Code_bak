package monitoring.sensor;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class ProbeClassLoader extends URLClassLoader {
	public ProbeClassLoader() throws MalformedURLException {
		super(new URL[]{new URL("file:probe" + File.separator)});
		addURL(new URL("file:probe/sigar.jar"));
	}
}
