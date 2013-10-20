package prediction;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;



public enum ScorerProvider {
	INSTANCE;
	
	private final String scorersPath = "bin/prediction/scoring";
	public HashMap<String,PredictionScorer> scorers = new HashMap<String, PredictionScorer>();
		
	private ScorerProvider() {
		File folder = new File(scorersPath);
		String[] scorersNames = folder.list();
		URL[] urls = new URL[scorersNames.length];
		
		for(int i=0 ; i<scorersNames.length ; i++){
			try {
				urls[i] = new URL("file:"+scorersPath+scorersNames[i]);
			} catch (MalformedURLException e) {
				System.err.println("Cannot make URL with " + scorersNames.toString());
				e.printStackTrace();
			}
		}
		
		URLClassLoader cl = new URLClassLoader(urls);
//		try {
////			cl.close();
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
		
		for(String scorerName : scorersNames){			
			String nom = scorerName.split(".class")[0];
			try {				
				System.out.println(nom);
				scorers.put(nom, (PredictionScorer) Class.forName("prediction.scoring."+nom).newInstance());
			} catch (InstantiationException e) {
				System.err.println("Cannot instantiate " + scorerName);
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				System.err.println("Cannot access " + scorerName);
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				System.err.println("Class not loaded " + scorerName);
				e.printStackTrace();
			}
		}
	}	
}
