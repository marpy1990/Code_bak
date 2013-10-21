package prediction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import prediction.predictor.EnsemblePredict;


/**
 * DirectWebRemoting singleton providing the services
 * related with the prediction of time series
 * @author heapoverflow
 */
public class PredictionServices {

	private static final PredictionServices INSTANCE = new PredictionServices();

	private PredictionServices() {
		if (INSTANCE != null) {
			throw new IllegalStateException("Already instantiated");
		}
	}

	public static PredictionServices getInstance() {
		return INSTANCE;
	}

	public Set<String> getAvailablePredictorsNames() {
		return PredictorProvider.INSTANCE.predictors.keySet();
	}
	
	public Set<String> getAvailableScorersNames() {
		return ScorerProvider.INSTANCE.scorers.keySet();
	}

	public ArrayList<double[][]> predict(String predictorName, String dataName, int startPredictionIdx, int length){
		Predictor p = PredictorProvider.INSTANCE.predictors.get(predictorName);
		TimeSeries ts = TimeSeries.data.get(dataName);
		if(ts != null && p != null){
			TimeSeries prediction = p.predict(ts, startPredictionIdx , length);
			return prediction.getPlotData();
		}else{
			System.err.println("prediction.PredictionService : makePrediction call error");
		}
		return null;
	}
	
	public void setPoolSizes(int value) {
		EnsemblePredict p = (EnsemblePredict) PredictorProvider.INSTANCE.predictors.get("EnsemblePredict");
		p.setPoolSizes(value);
	}
	
	public HashMap<String, Double> getStrategiesEfficiencies(){
		HashMap<String, Double> hm = new HashMap<String, Double>();
		for(Entry<String, Predictor> e : PredictorProvider.INSTANCE.predictors.entrySet()){
			hm.putAll(e.getValue().getStrategiesEfficiencies());
		}
		return hm;
	}
	
	public void reset(){
		for(Predictor p : PredictorProvider.INSTANCE.predictors.values()) {
			p.reset();
		}
	}
}
