package cep;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class CepRuleClassLoader extends URLClassLoader {

	public CepRuleClassLoader() throws MalformedURLException {
		super(new URL[]{new URL("file:cep" + File.separator)});
	}

}
