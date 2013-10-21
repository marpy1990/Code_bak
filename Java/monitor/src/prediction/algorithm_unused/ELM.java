package prediction.algorithm_unused;

import java.util.ArrayList;

import Jama.Matrix;
import Jama.SingularValueDecomposition;

import prediction.PredictionAlgorithm;
import prediction.Serie;
import prediction.TimeSeries;

public class ELM implements PredictionAlgorithm {

	static int meioseCount = 0;

	public int patternLen = 30;
	public int hiddenLayerSize = 50;

	private boolean trained = false;

	private double[] randomInputBiais;
	private Matrix randomInputWeights;
	private Matrix optimizedHiddenWeights;

	private void train(TimeSeries ts, int stopIndex, int nbOfStepsToBePredicted) {
		System.out.println("train");
		
		// build the data

		int nbOfSeries = ts.series.size();
		int inputSize = patternLen * nbOfSeries;
		int hiddenLayerSize = inputSize*10;
		int ouputSize = nbOfStepsToBePredicted * nbOfSeries;

		int debut = patternLen+1;
		int rowNumber = stopIndex-nbOfStepsToBePredicted-1-debut;
		double[][] inputs = new double[rowNumber][inputSize];
		double[][] outputs = new double[rowNumber][ouputSize];
		for(int i = debut ; i < stopIndex-nbOfStepsToBePredicted-1 ; i++) {
			for(int j = 0 ; j < nbOfSeries ; j++) {
				System.arraycopy(ts.series.get(j).getNormalizedValue(), i-debut, inputs[i-debut], j*patternLen, patternLen);
				System.arraycopy(ts.series.get(j).getNormalizedValue(), i+1, outputs[i-debut], j*nbOfStepsToBePredicted, nbOfStepsToBePredicted);
			}
		}
		System.out.println("train 1");
		randomInputWeights = Matrix.random(inputSize, hiddenLayerSize);
		randomInputBiais = new double[hiddenLayerSize];
		for (int i = 0 ; i < hiddenLayerSize ; i++) {
			randomInputBiais[i] = Math.random();
		}	
		
		// actually train 
		System.out.println("train check : \n\t"
				+ new Matrix(inputs).getRowDimension()  + " " + new Matrix(inputs).getColumnDimension()
				+ "\n\t" + randomInputWeights.getRowDimension() + " " + randomInputWeights.getColumnDimension());		
		Matrix H = new Matrix(inputs).times(randomInputWeights);
		for(double[] row : H.getArray()){
			for(int i = 0 ; i < hiddenLayerSize ; i++){ // Sigmoid transfer function
				row[i] = 1/(1 + Math.exp(-(row[i] + randomInputBiais[i])));
			}
		}
		System.out.println("train 2");
		Matrix Hinv = pinv(H);
		optimizedHiddenWeights = Hinv.times(new Matrix(outputs));
		trained = true;
		System.out.println("train 3");
	}


	public TimeSeries effectuerUnePrediction(TimeSeries ts, int startIndex, int nbOfStepsToBePredicted) {
		System.out.println("effectuerUnePrediction");
		if(!trained) {
			train(ts, startIndex, nbOfStepsToBePredicted);
		}
		int nbOfSeries = ts.series.size();
		double[] input = new double[nbOfSeries*patternLen];
		for(int j = 0 ; j < nbOfSeries ; j++) {
			System.arraycopy(ts.series.get(j).getNormalizedValue(), startIndex-patternLen, input, j*patternLen, patternLen);
		}
		System.out.println("effectuerUnePrediction 1");
		Matrix temp = new Matrix(input,1).times(randomInputWeights);
		for(double[] row : temp.getArray()){
			for(int i = 0 ; i < hiddenLayerSize ; i++){ // Sigmoid transfer function
				row[i] = 1/(1 + Math.exp(-(row[i] + randomInputBiais[i])));
			}
		}
		double[] ouput = temp.times(optimizedHiddenWeights).getColumnPackedCopy();
		ArrayList<Serie> seriesPred = new ArrayList<Serie>();
		for(int j = 0 ; j < nbOfSeries ; j++) {
			double[] val = new double[nbOfStepsToBePredicted];
			System.arraycopy(ouput, j*nbOfStepsToBePredicted, val, 0, nbOfStepsToBePredicted);
			double mean = ts.series.get(j).mean();
			double sigma = ts.series.get(j).sigma();
			System.out.println("mean = "+ mean + " \t sigma = " + sigma);
			for (int k = 0 ; k < val.length ; k++) {
				val[k] *= sigma;
				val[k] += mean;
			}
			Serie s = new Serie(ts.series.get(j).name, val); 
			seriesPred.add(s);
		}
		int decalage = ts.timeStart + startIndex*ts.timeStep;
		System.out.println("effectuerUnePrediction fin");
		return new TimeSeries("prediction", decalage, ts.timeStep, seriesPred);
	}

	@Override
	public boolean equals(Object obj) {
		return false;
		/*
		if(obj.getClass() == ELM.class){
			ELM o2 = (ELM) obj;
			if(	o2.hiddenLayerSize == this.hiddenLayerSize && 
					o2.patternLen == this.patternLen)
				return true;
		}
		return false;*/
	}

	@Override
	public ELM meiose() {
		if(!trained) return null;
		meioseCount++;
		ELM cel = new ELM();
		// TODO
		return cel;
	}

	@Override
	public String toString() {
		return "NeuroNet \t hiddenLayerSize="+hiddenLayerSize+"\t patLen="+patternLen;
	}


	@Override
	public boolean mature() {
		return false;
	}

	/**
	 * The difference between 1 and the smallest exactly representable number
	 * greater than one. Gives an upper bound on the relative error due to
	 * rounding of floating point numbers.
	 */
	private static double MACHEPS = 2E-16;

	/**
	 * Updates MACHEPS for the executing machine.
	 */
	private static void updateMacheps() {
		MACHEPS = 1;
		do
			MACHEPS /= 2;
		while (1 + MACHEPS / 2 != 1);

	}

	/**
	 * Computes the Moore–Penrose pseudoinverse using the SVD method.
	 * The Moore–Penrose pseudoinverse computation time complexity is O(n^3) + O(r^3)
	 * with n is here the training set size and r the rank of the second dimension. (build of the nodes inputs) 
	 * Thanks to Kim van der Linde and Ahmed Adbelkader for their work.
	 */
	private static Matrix pinv(Matrix x) {
		updateMacheps(); // TODO: should be run only once.
		if (x.rank() < 1)
			return null;
		if (x.getColumnDimension() > x.getRowDimension())
			return pinv(x.transpose()).transpose();
		SingularValueDecomposition svdX = new SingularValueDecomposition(x);

		double[] singularValues = svdX.getSingularValues();
		double tol = Math.max(x.getColumnDimension(), x.getRowDimension()) * singularValues[0] * MACHEPS;
		double[] singularValueReciprocals = new double[singularValues.length];

		for (int i = 0; i < singularValues.length; i++)
			singularValueReciprocals[i] = Math.abs(singularValues[i]) < tol ? 0 : (1.0 / singularValues[i]);
		double[][] u = svdX.getU().getArray();
		double[][] v = svdX.getV().getArray();

		int min = Math.min(x.getColumnDimension(), u[0].length);
		double[][] inverse = new double[x.getColumnDimension()][x.getRowDimension()];
		for (int i = 0; i < x.getColumnDimension(); i++)
			for (int j = 0; j < u.length; j++)
				for (int k = 0; k < min; k++)
					inverse[i][j] += v[i][k] * singularValueReciprocals[k] * u[j][k];
		return new Matrix(inverse);
	}
}