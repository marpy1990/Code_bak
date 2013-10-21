package prediction;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

public enum AlgorithmProvider {
	INSTANCE;
	
	private final String algorithmsPath = "bin/prediction/algorithm";
	public Set<PredictionAlgorithm> algorithms = new HashSet<PredictionAlgorithm>();
		
	private AlgorithmProvider() {
		File folder = new File(algorithmsPath);
		String[] algorithmsNames = folder.list();
		URL[] urls = new URL[algorithmsNames.length];
		
		for(int i=0 ; i<algorithmsNames.length ; i++){
			try {
				urls[i] = new URL("file:"+algorithmsPath+algorithmsNames[i]);
			} catch (MalformedURLException e) {
				System.err.println("Cannot make URL with " + algorithmsNames.toString());
				e.printStackTrace();
			}
		}
		
		URLClassLoader cl = new URLClassLoader(urls);
//		try {
//			cl.close();
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
		
		for(String algorithmName : algorithmsNames){			
			String nom = algorithmName.split(".class")[0];
			try {				
				algorithms.add((PredictionAlgorithm) Class.forName("prediction.algorithm."+nom).newInstance());
			} catch (InstantiationException e) {
				System.err.println("Cannot instantiate " + algorithmName);
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				System.err.println("Cannot access " + algorithmName);
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				System.err.println("Class not loaded " + algorithmName);
				e.printStackTrace();
			}
		}
	}		
}
