package prediction;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;



public enum PredictorProvider {
	INSTANCE;
	
	private final String predictorsPath = "bin/prediction/predictor";
	public HashMap<String,Predictor> predictors = new HashMap<String, Predictor>();
		
	private PredictorProvider() {
		File folder = new File(predictorsPath);
		String[] predictorsNames = folder.list();
		URL[] urls = new URL[predictorsNames.length];
		
		for(int i=0 ; i<predictorsNames.length ; i++){
			try {
				urls[i] = new URL("file:"+predictorsPath+predictorsNames[i]);
			} catch (MalformedURLException e) {
				System.err.println("Cannot make URL with " + predictorsNames.toString());
				e.printStackTrace();
			}
		}
		
		URLClassLoader cl = new URLClassLoader(urls);
//		try {
////			cl.close();
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
		
		for(String predictorName : predictorsNames){			
			String nom = predictorName.split(".class")[0];
			try {				
				predictors.put(nom, (Predictor) Class.forName("prediction.predictor."+nom).newInstance());
			} catch (InstantiationException e) {
				System.err.println("Cannot instantiate " + predictorName);
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				System.err.println("Cannot access " + predictorName);
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				System.err.println("Class not loaded " + predictorName);
				e.printStackTrace();
			}
		}
	}
	
	// add, delete predictors and other object load&creation matters
	
}
