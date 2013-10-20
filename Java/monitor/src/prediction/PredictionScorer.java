package prediction;

public interface PredictionScorer {

	public void addEvaluation(double eval);
	
	public double score();
	
	public boolean mature();
	
}
