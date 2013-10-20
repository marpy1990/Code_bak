package prediction.predictor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import prediction.AlgorithmProvider;
import prediction.PoolComparator;
import prediction.PoolMember;
import prediction.PredictionAlgorithm;
import prediction.Predictor;
import prediction.Serie;
import prediction.TimeSeries;
import prediction.scoring.AverageLatest;

public class EnsemblePredict implements Predictor{

	int poolSizes = 3;
	List<List<PoolMember>> pools = new ArrayList<List<PoolMember>>();
	PoolComparator bestBasedComparator = new PoolComparator();

	private double cumulatedError = 0;
	private int nbOfPredictionErrSummed = 0;
	private HashMap<String, Double> poolCumulatedError;
	private HashMap<String, Integer> compteurs;

	public EnsemblePredict() {
		buildPools();
	}

	private void buildPools() {
		poolCumulatedError = new HashMap<String, Double>();
		compteurs = new HashMap<String, Integer>();
		for(PredictionAlgorithm a : AlgorithmProvider.INSTANCE.algorithms){
			try {
				List<PoolMember> pool = new ArrayList<PoolMember>();
				PredictionAlgorithm algo = a.getClass().newInstance();
				pool.add(new PoolMember(algo, new AverageLatest(1)));
				while (pool.size() < poolSizes) {
					algo = algo.meiose();
					if (algo == null) break;
					boolean doublon = false;
					for (PoolMember dejaLa : pool) 
						if (algo.equals(dejaLa.predictionAlgorithm)) 
							doublon = true;
					if (!doublon) {
						pool.add(new PoolMember(algo, new AverageLatest(1)));
					}
				}
				pools.add(pool);
				String keyName = pool.get(0).predictionAlgorithm.getClass().getSimpleName();
				poolCumulatedError.put(keyName, 0.0);
				compteurs.put(keyName, 0);
			} catch (Exception e) {
				System.err.println(a.getClass().getName() + " failed and will not be used");
			}
		}
	}
	@Override
	public TimeSeries predict(TimeSeries ts, int startIndex, int nbOfStepsToBePredicted) {
		TreeMap<TreeMap<PoolMember, TimeSeries>,TimeSeries> trustBasedDataStructure = new TreeMap<TreeMap<PoolMember, TimeSeries>,TimeSeries>(bestBasedComparator);
		// SORTING 
		// within pools and the pools themself
		for (List<PoolMember> pool : pools) {
			TreeMap<PoolMember, TimeSeries> poolPredictions = new TreeMap<PoolMember, TimeSeries>();
			for (PoolMember pm : pool) {
				poolPredictions.put(pm, pm.getPrediction());
			}
			TimeSeries pred = poolPredictions.firstEntry().getValue();
			trustBasedDataStructure.put(poolPredictions, pred);			
		}
		// TRANSFORMATION
		// change some predictors behavior, add/remove predictors
		for (List<PoolMember> pool : pools) {
			boolean change = true;
			for (PoolMember pm : pool) {
				if(!pm.mature()){
					change = false;
					break;
				}
			}
			Collections.sort(pool, Collections.reverseOrder());
			if(change & (poolSizes>1)){
				pool.set(0, new PoolMember(pool.get(pool.size()-1).predictionAlgorithm.meiose(), new AverageLatest(10)));
			}			
		}
		// PREDICTION
		// run all the predictors
		for (List<PoolMember> pool : pools) {
			for (PoolMember pm : pool) {
				pm.job(ts, startIndex, nbOfStepsToBePredicted);
			}
		}
		// OUTPUT CRAFTING
		// apply a strategy to build output
		PoolMember trusted = trustBasedDataStructure.firstEntry().getKey().firstKey();

		// TRUST EVAL
		for (List<PoolMember> pool : pools) {
			for (PoolMember pm : pool) {
				pm.predictionScorer.addEvaluation(evaluate(ts, pm.getPrediction()));
			}
		}
		double err = evaluate(ts, trusted.getPrediction());
		if (!Double.isNaN(err)) {
			cumulatedError += err;
			nbOfPredictionErrSummed++;
		}
		for(TreeMap<PoolMember, TimeSeries> pool : trustBasedDataStructure.descendingKeySet()){
			double evalPool = evaluate(ts, pool.firstKey().getPrediction());
			if (!Double.isNaN(evalPool)) {
				String keyName = pool.firstKey().predictionAlgorithm.getClass().getSimpleName();
				poolCumulatedError.put(keyName, poolCumulatedError.get(keyName)+evalPool);
				compteurs.put(keyName, compteurs.get(keyName)+1);
			}
		}
		// TODO une fonction qui fait la moyenne de timeseries			
		// TODO interface web capable de choisir les scorers et parametres :)
		//System.out.println(cumulatedError + " \t this round we trusted " +  trusted);
		System.out.println("This round we trust " +  trusted);
		return trusted.getPrediction();
	}

	public int getPoolSizes() {
		return poolSizes;
	}

	public void setPoolSizes(int poolSizes) {
		this.poolSizes = poolSizes;
	}

	private double evaluate(TimeSeries ts, TimeSeries prediction){
		if (prediction == null) return Double.NaN;
		double erreur = 0;
		for (Serie s: prediction.series) {
			int basets = (prediction.timeStart - ts.timeStart+1)/ts.timeStep;
			Serie real = ts.getSerie(s.name);
			for(int i = 1 ; i < s.value.length ; i++){ // the first is exclude because not part of the prediction
				erreur +=  Math.abs((s.value[i] - real.value[i+basets])/real.value[i+basets]);
			}
		}
		erreur /= prediction.series.size()*(prediction.series.get(0).value.length - 1);
		return erreur;
	}

	public void reset() {
		pools.clear();
		buildPools();
	}

	@Override
	public HashMap<String, Double> getStrategiesEfficiencies() {
		HashMap<String, Double> result = new HashMap<String, Double>();
		for (List<PoolMember> pool : pools) {
			int idx = pools.indexOf(pool);
			String poolName = pool.get(0).predictionAlgorithm.getClass().getSimpleName();
			result.put(poolName + " pool", poolCumulatedError.get(poolName)/compteurs.get(poolName));
		}
		result.put("ensemble",cumulatedError/nbOfPredictionErrSummed);
		return result;
	}
}
