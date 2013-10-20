package prediction;

public class PoolMember implements Comparable<PoolMember>{
	
	public PredictionAlgorithm predictionAlgorithm;
	public PredictionScorer predictionScorer;
	private TimeSeries prediction;
	
	public PoolMember(PredictionAlgorithm algo, PredictionScorer scorer) {
		this.predictionAlgorithm = algo;
		this.predictionScorer = scorer;
	}

	@Override
	public int compareTo(PoolMember that) {
		double diff = this.predictionScorer.score() - that.predictionScorer.score();
		if(diff < 0)
			return -1;
		if(diff > 0)
			return 1;
		return 0;
	}
	
	public void job(TimeSeries ts, int startIndex, int nbOfStepsToBePredicted){
		prediction = predictionAlgorithm.effectuerUnePrediction(ts, startIndex, nbOfStepsToBePredicted);
	}
	
	public TimeSeries getPrediction() {
		return prediction;
	}

	public void setPrediction(TimeSeries prediction) {
		this.prediction = prediction;
	}

	public boolean mature(){
		return predictionScorer.mature() && predictionAlgorithm.mature();
	}
	
	@Override
	public String toString() {
		return predictionAlgorithm.toString() ;
	}
}
