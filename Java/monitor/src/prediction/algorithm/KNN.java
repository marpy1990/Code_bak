package prediction.algorithm;

import java.util.ArrayList;
import java.util.PriorityQueue;

import prediction.PatternStructKNN;
import prediction.PredictionAlgorithm;
import prediction.Serie;
import prediction.TimeSeries;

public class KNN implements PredictionAlgorithm{

	public int K = 5;
	public int patternLen = 50;

	public KNN meiose(){
		KNN cel = new KNN();
		double xray = Math.random();
		if(xray < 1/3){
			double proba = 0.5 - 0.5 / (1 + Math.exp(-0.3*K+4)); // sigmoid
			cel.K = (Math.random() < proba) ? K+1 : K-1;
		}else if(xray < 2/3){
			double proba = 0.5 - 0.5 / (1 + Math.exp(-0.2*patternLen+4)); // sigmoid
			cel.patternLen = (Math.random() < proba) ? patternLen+1 : patternLen-1;
		}else{			
			double proba = 0.5 - 0.5 / (1 + Math.exp(-0.3*K+4)); // sigmoid
			cel.K = (Math.random() < proba) ?  K+1 : K-1;
			proba = 0.5 - 0.5 / (1 + Math.exp(-0.2*patternLen+4)); // sigmoid
			cel.patternLen = (Math.random() < proba) ? patternLen+1 : patternLen-1;
		}
		return cel;
	}



	public TimeSeries effectuerUnePrediction(TimeSeries ts, int startIndex, int nbOfStepsToBePredicted) {

		PatternStructKNN[] neighboors = euclidKNN(ts, startIndex);
		ArrayList<Serie> seriesPred = new ArrayList<Serie>(); 

		for(Serie s : ts.series){
			double[] result = new double[nbOfStepsToBePredicted+1];
			result[0] = s.value[startIndex];
			for(int i = 1 ; i <= nbOfStepsToBePredicted ; i++){
				result[i] = 0; 
				for(PatternStructKNN n : neighboors){					
					result[i] += s.value[n.getStartIndex()+i];
				}
				result[i] /= K;
			}
			seriesPred.add(new Serie(s.name, result));
		}
		int decalage = ts.timeStart + startIndex*ts.timeStep;
		return new TimeSeries("KNN prediction", decalage, ts.timeStep, seriesPred);
	}

	private PatternStructKNN[] euclidKNN(TimeSeries ts, int startIndex){

		int patternStart = startIndex - patternLen;
		PriorityQueue<PatternStructKNN> pq = new PriorityQueue<PatternStructKNN>(10);
		for(int i=0; i<patternStart; i++){
			PatternStructKNN tmp = new PatternStructKNN(i,
					distance(ts,i,patternStart));
			pq.add(tmp);
		}
		PatternStructKNN[] kNearest = new PatternStructKNN[K];
		for(int i = 0; i < K; i++){
			kNearest[i] = pq.poll();
		}
		return kNearest;

	}

	private double distance(TimeSeries ts, int index1, int index2) {
		double sum = 0;
		for (Serie s : ts.series){
			for (int i = 0; i < patternLen; i++) {
				sum += (Math.pow((s.value[index1+i] - s.value[index2+i]), 2));
			}
		}
		return sum;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj.getClass() == KNN.class){
			KNN o2 = (KNN) obj;
			if(o2.K == this.K && o2.patternLen == this.patternLen)
				return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "KNN \t K="+K+"\t patLen="+patternLen;
	}



	@Override
	public boolean mature() {
		return true;
	}
}
