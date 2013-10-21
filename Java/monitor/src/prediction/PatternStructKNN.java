package prediction;

public class PatternStructKNN implements Comparable<PatternStructKNN>{

	private int startIndex;
	private double distance;

	public PatternStructKNN(int index, double d) {
		setStartIndex(index);
		setDistance(d);
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	@Override
	public int compareTo(PatternStructKNN that) {
		double diff = this.getDistance() - that.getDistance();
		if(diff > 0)
			return 1;
		if(diff < 0)
			return -1;
		return 0;			
	}



}
