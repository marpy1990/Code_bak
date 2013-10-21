package prediction;

import java.util.HashMap;


public interface Predictor {
	TimeSeries predict(TimeSeries ts, int startIndex, int nbOfStepsToBePredicted);	
	HashMap<String, Double> getStrategiesEfficiencies();
	void reset();
}
