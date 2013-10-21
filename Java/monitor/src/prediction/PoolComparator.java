package prediction;

import java.util.Comparator;
import java.util.TreeMap;

public class PoolComparator implements Comparator<TreeMap<PoolMember, TimeSeries>> {

	@Override
	public int compare(TreeMap<PoolMember, TimeSeries> o1,
			TreeMap<PoolMember, TimeSeries> o2) {
		double diff = o1.firstKey().predictionScorer.score() - o2.firstKey().predictionScorer.score();
		if (diff < 0) {
			return -1;
		}
		if (diff > 0) {
			return 1;
		}
		return 0;
	}
}
