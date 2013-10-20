package prediction.algorithm_unused;

import java.util.ArrayList;
import java.util.LinkedList;

import prediction.AlgorithmProvider;
import prediction.PredictionAlgorithm;
import prediction.Serie;
import prediction.TimeSeries;
import weka.attributeSelection.PrincipalComponents;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.M5P;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class DecisionTree implements PredictionAlgorithm {

	private boolean trained = false;
	private PrincipalComponents pca;


	public int patternLen = 6;
	public int hiddenLayerSize = 10;

	static int evalMemorySize = 10;
	public LinkedList<Double> eval = new LinkedList<Double>();

	private void train(TimeSeries ts, int stopIndex, int nbOfStepsToBePredicted) {
		trained = true;
		int nbOfSeries = ts.series.size();
		int originalInputLayerSize = patternLen * nbOfSeries;
		int ouputLayerSize = nbOfStepsToBePredicted * nbOfSeries;

		ArrayList<Attribute> atts = new ArrayList<Attribute>(2);
		for(int i=0 ; i<originalInputLayerSize ; i++){
			atts.add(new Attribute("aide"+i));
		}
		Instances initialInput = new Instances("TestInstances",atts,0);
		double[][] outputs = new double[stopIndex-nbOfStepsToBePredicted-1-patternLen][];
		for(int i = patternLen ; i < stopIndex-nbOfStepsToBePredicted-1 ; i++) {
			double[] input = new double[nbOfSeries*patternLen];
			double[] output = new double[nbOfSeries*nbOfStepsToBePredicted];
			for(int j = 0 ; j < nbOfSeries ; j++) {
				System.arraycopy(ts.series.get(j).value, i-patternLen, input, j*patternLen, patternLen);
				System.arraycopy(ts.series.get(j).value, i+1, output, j*nbOfStepsToBePredicted, nbOfStepsToBePredicted);
			}
			initialInput.add(new DenseInstance(1.0,input));
			outputs[i-patternLen] = output;			
		}
		System.out.println("dimensions avant réduction : " + initialInput.numAttributes());
		pca = new PrincipalComponents();
		Instances apres = null;
		try {
			pca.setVarianceCovered(0.8);
			pca.buildEvaluator(initialInput);
			apres = pca.transformedData(initialInput);			

			System.out.println("dimensions après réduction : " + apres.numAttributes());
			int inputSize = apres.get(0).numValues();
			M5P tree = new M5P();
			tree.setOptions(weka.core.Utils.splitOptions("-R "));
			tree.buildClassifier(apres);
		} catch (Exception e) {
			System.err.println("PCA fail, neuroNet kicked out of the game");
			AlgorithmProvider.INSTANCE.algorithms.remove(DecisionTree.class);
		}	
	}

	public TimeSeries effectuerUnePrediction(TimeSeries ts, int startIndex, int nbOfStepsToBePredicted) {
		if(!trained) {
			train(ts, startIndex, nbOfStepsToBePredicted);
		}
		int nbOfSeries = ts.series.size();
		double[] input = new double[nbOfSeries*patternLen];
		for(int j = 0 ; j < nbOfSeries ; j++) {
			System.arraycopy(ts.series.get(j).value, startIndex-patternLen, input, j*patternLen, patternLen);
		}
		try {
			Instance instance = pca.convertInstance(new DenseInstance(1.0, input));
			// TODO
			double[] networkOutput = null;
			ArrayList<Serie> seriesPred = new ArrayList<Serie>();
			for(int j = 0 ; j < nbOfSeries ; j++) {
				double[] val = new double[nbOfStepsToBePredicted];
				System.arraycopy(networkOutput, j*nbOfStepsToBePredicted, val, 0, nbOfStepsToBePredicted);
				Serie s = new Serie(ts.series.get(j).name, val); 
				seriesPred.add(s);
			}
			int decalage = ts.timeStart + startIndex*ts.timeStep;
			return new TimeSeries("NeuroNet prediction", decalage, ts.timeStep, seriesPred);
		} catch (Exception e) {
			return null;
		}
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

	/*
	@Override
	public int compareTo(NeuroNet that) {
		int view = Math.min(eval.size(),that.eval.size()); 

		if(view < evalMemorySize) // check for maturity
			return 0;

		double cmp = 0;
		for (int i = 0; i < evalMemorySize; i++) {
			cmp += that.eval.get(i) - this.eval.get(i);
		}
		if(cmp < 0)
			return 1; // this bigger than that
		if(cmp > 0)
			return -1; // that bigger than this
		return 0;
	}
	 */

	@Override
	public boolean equals(Object obj) {
		if(obj.getClass() == DecisionTree.class){
			DecisionTree o2 = (DecisionTree) obj;
			if(o2.hiddenLayerSize == this.hiddenLayerSize && o2.patternLen == this.patternLen)
				return true;
		}
		return false;
	}


	public boolean mature() {
		return (eval.size() == evalMemorySize) ? true : false;
	}


	@Override
	public DecisionTree meiose() {
		DecisionTree cel = new DecisionTree();
		double xray = Math.random();
		int lissage = 4;
		if(xray < 1/3){
			double proba = 0.5 - 0.5 / (1 + Math.exp(-hiddenLayerSize/lissage+lissage*3)); // sigmoid
			cel.hiddenLayerSize = (Math.random() < proba) ? hiddenLayerSize+1 : hiddenLayerSize-1;
		}else if(xray < 2/3){
			double proba = 0.5 - 0.5 / (1 + Math.exp(-0.2*patternLen+4)); // sigmoid
			cel.patternLen = (Math.random() < proba) ? patternLen+1 : patternLen-1;
		}else{			
			double proba = 0.5 - 0.5 / (1 + Math.exp(-hiddenLayerSize/lissage+lissage*3)); // sigmoid
			cel.hiddenLayerSize = (Math.random() < proba) ? hiddenLayerSize+1 : hiddenLayerSize-1;
			proba = 0.5 - 0.5 / (1 + Math.exp(-0.2*patternLen+4)); // sigmoid
			cel.patternLen = (Math.random() < proba) ? patternLen+1 : patternLen-1;
		}
		return cel;
	}
}
