package prediction.algorithm;

import java.util.ArrayList;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

import prediction.AlgorithmProvider;
import prediction.PredictionAlgorithm;
import prediction.Serie;
import prediction.TimeSeries;

import weka.attributeSelection.PrincipalComponents;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class NeuroNet implements PredictionAlgorithm {
	
	static int meioseCount = 0;

	public int patternLen = 35;
	public int hiddenLayerSize = 50;

	private boolean trained = false;
	private Instances dataSet;
	private PrincipalComponents pca;
	private BasicNetwork network;
	private double[][] outputs;

	private void train(TimeSeries ts, int stopIndex, int nbOfStepsToBePredicted) {
		if (dataSet == null) {
			int nbOfSeries = ts.series.size();
			int originalInputLayerSize = patternLen * nbOfSeries;
			int ouputLayerSize = nbOfStepsToBePredicted * nbOfSeries;

			ArrayList<Attribute> atts = new ArrayList<Attribute>(2);
			for(int i=0 ; i<originalInputLayerSize ; i++){
				atts.add(new Attribute("aide"+i));
			}
			dataSet = new Instances("TestInstances",atts,0);
			int debut = patternLen+1;
			outputs = new double[stopIndex-nbOfStepsToBePredicted-1-debut][ouputLayerSize];
			for(int i = debut ; i < stopIndex-nbOfStepsToBePredicted-1 ; i++) {
				double[] input = new double[nbOfSeries*patternLen];
				for(int j = 0 ; j < nbOfSeries ; j++) {
					System.arraycopy(ts.series.get(j).value, i-debut, input, j*patternLen, patternLen);
					System.arraycopy(ts.series.get(j).value, i+1, outputs[i-debut], j*nbOfStepsToBePredicted, nbOfStepsToBePredicted);
				}
				dataSet.add(new DenseInstance(1.0,input));
			}
		}else{
			int nbOfSeries = ts.series.size();
			int ouputLayerSize = nbOfStepsToBePredicted * nbOfSeries;

			int debut = patternLen+1;
			outputs = new double[dataSet.size()][ouputLayerSize];
			for(int i = debut ; i < stopIndex-nbOfStepsToBePredicted-1 ; i++) {
				for(int j = 0 ; j < nbOfSeries ; j++) {
					System.arraycopy(ts.series.get(j).value, i+1, outputs[i-debut], j*nbOfStepsToBePredicted, nbOfStepsToBePredicted);
				}
			}
		}
		train();
	}


	private void train(){
		trained = true;
		pca = new PrincipalComponents();
		Instances apres = null;
		try {
			pca.setVarianceCovered(0.99);
			pca.buildEvaluator(dataSet);
			apres = pca.transformedData(dataSet);	
		} catch (Exception e) {
			System.err.println("PCA fail, neuroNet kicked out of the game");
			AlgorithmProvider.INSTANCE.algorithms.remove(NeuroNet.class);
		}	
		int inputSize = apres.get(0).numValues();

		// create a neural network, without using a factory
		network = new BasicNetwork();
		//network.addLayer(new BasicLayer(null,true,originalInputLayerSize));
		// TODO : ActivationSigmoid is a shitty implementation of a sigmoid, 
		// it need to be re-implemented to allow changing the slope.
		network.addLayer(new BasicLayer(null,true,inputSize));
		network.addLayer(new BasicLayer(new ActivationSigmoid(),true,hiddenLayerSize));
		network.addLayer(new BasicLayer(new ActivationSigmoid(),false,outputs[0].length));
		network.getStructure().finalizeStructure();
		network.reset();		
		// create training data
		int rowNumber = apres.size();
		double[][] inputs = new double[rowNumber][];
		for (int i = 0 ; i < rowNumber ; i++) {
			inputs[i] = apres.get(i).toDoubleArray();
		}
		MLDataSet trainingSet = new BasicMLDataSet(inputs, outputs);

		// train the neural network
		final ResilientPropagation train = new ResilientPropagation(network, trainingSet);
		int epoch = 0;
		double previousError = 0;
		double diff;
		double learningRate = Math.pow(10,-8);
		do {
			train.iteration();
			diff = Math.abs(previousError - train.getError())/previousError;
			previousError = train.getError();
			System.out.println("Epoch #" + epoch + " Error:" + train.getError());
			epoch++;			
		} while(diff > learningRate & epoch < 2*rowNumber);
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
		dataSet.add(new DenseInstance(1.0, input));
		try {			
			Instance instance = pca.convertInstance(dataSet.lastInstance());
			double[] ouput = new double[nbOfSeries*nbOfStepsToBePredicted];
			network.compute(instance.toDoubleArray(),ouput);
			ArrayList<Serie> seriesPred = new ArrayList<Serie>();
			for(int j = 0 ; j < nbOfSeries ; j++) {
				double[] val = new double[nbOfStepsToBePredicted];
				System.arraycopy(ouput, j*nbOfStepsToBePredicted, val, 0, nbOfStepsToBePredicted);
				Serie s = new Serie(ts.series.get(j).name, val); 
				seriesPred.add(s);
			}
			int decalage = ts.timeStart + startIndex*ts.timeStep;
			return new TimeSeries("NeuroNet prediction", decalage, ts.timeStep, seriesPred);
		} catch (Exception e) {
			System.out.println("ERREUR : unable to predict !");
			return null;
		}			
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
		if(obj.getClass() == NeuroNet.class){
			NeuroNet o2 = (NeuroNet) obj;
			if(o2.outputs.length == this.outputs.length && 
				o2.hiddenLayerSize == this.hiddenLayerSize && 
				o2.patternLen == this.patternLen)
				return true;
		}
		return false;
	}

	@Override
	public NeuroNet meiose() {
		if(!trained) return null;
		meioseCount++;
		NeuroNet cel = new NeuroNet();
		// AdaBoost
		int outputSize = outputs[0].length;
		double[] errors = new double[outputs.length];  
		for (int i = 0 ; i < outputs.length ; i++) {
			Instance instanceInitial = dataSet.get(i);
			Instance instanceShort;
			try {
				instanceShort = pca.convertInstance(instanceInitial);
			} catch (Exception e) {
				System.err.println("NeuroNet.meiose() fail !");
				return null;
			}
			double[] prediction = new double[outputSize];
			network.compute(instanceShort.toDoubleArray(), prediction);
			// compute error
			double error = 0;
			for (int j = 0 ; j<prediction.length ; j++) {
				error += Math.abs(prediction[j]-outputs[i][j]);
			}
			errors[i] = error;
		}
		double errorSum = 0;
		for (double d : errors) {
			errorSum += d;
		}
		double avg = errorSum/errors.length;
		for (int i = 0 ; i < errors.length ; i++) {
			Instance instance = dataSet.get(i);
			if (errors[i] > avg) {
				instance.setWeight(instance.weight()*2);
			}else{
				instance.setWeight(instance.weight()/2);
			}
		}
		cel.dataSet = this.dataSet;
		/*double uv = Math.random();
		double xray = Math.random();
		int lissage = 4;
		if(uv<1/2){
			cel.hiddenLayerSize = this.hiddenLayerSize;
			cel.patternLen = this.patternLen;
		}
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
		}*/
		return cel;
	}

	@Override
	public String toString() {
		return "NeuroNet \t hiddenLayerSize="+hiddenLayerSize+"\t patLen="+patternLen;
	}


	@Override
	public boolean mature() {
		if (!trained) return false;
		if (dataSet.size() - outputs.length > 10 && meioseCount < 2) return true;
		return false;
	}
}
