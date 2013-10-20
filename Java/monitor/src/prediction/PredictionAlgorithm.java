package prediction;

public interface PredictionAlgorithm {
		
	public PredictionAlgorithm meiose();
	
	public TimeSeries effectuerUnePrediction(TimeSeries ts, int startIndex, int nbOfStepsToBePredicted);
	
	public boolean mature();
}
