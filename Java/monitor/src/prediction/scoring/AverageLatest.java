package prediction.scoring;

import java.util.LinkedList;

import prediction.PredictionScorer;

public class AverageLatest implements PredictionScorer{

	private int evalMemorySize = 10;
	private LinkedList<Double> eval = new LinkedList<Double>();
	
	public AverageLatest() {
		super();
	}

	public AverageLatest(int number) {
		this.evalMemorySize = number;
	}
	
	public void addEvaluation(double eval) {
		this.eval.add(eval);
		if (this.eval.size() > evalMemorySize) {
			this.eval.removeFirst();
		}
	}

	public double score() {
		double score = 0;
		for(double v:eval){
			score += v;
		}
		score /= eval.size();
		return score;
	}

	@Override
	public boolean mature() {
		return eval.size() == evalMemorySize;
	}
}
