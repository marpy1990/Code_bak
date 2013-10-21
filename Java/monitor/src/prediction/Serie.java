package prediction;

public class Serie {	
	public String name;
	public double[] value;
	private double mean = Double.POSITIVE_INFINITY;
	private double sigma = Double.POSITIVE_INFINITY;
	private double[] normalizedValue = null;

	public Serie(String name, int nbrOfValues) {
		this.name = name;
		this.value = new double[nbrOfValues]; 
	}

	public Serie(String name, double[] value) {
		this.name = name;
		this.value = value; 
	}

	public double mean(){
		if(mean != Double.POSITIVE_INFINITY){
			double sum = 0;
			for (double d : value) {
				sum += d;
			}
			mean = sum/value.length;
		}	
		return mean;
	}

	public double sigma(){
		if(sigma != Double.POSITIVE_INFINITY){
			double mean = mean();	
			double sum = 0;
			for (double d : value) {
				double ecart = d-mean;
				sum += ecart*ecart;
			}
			sigma = Math.sqrt(sum/value.length);
		}
		return sigma;
	}

	public double[] getNormalizedValue() {
		if (normalizedValue == null) {
			normalizedValue = new double[value.length];
			double mean = mean();
			double sigma = sigma();
			for (int i = 0 ; i < value.length ; i++) {
				normalizedValue[i] = (value[i] - mean)/sigma;
			}				
		}
		return normalizedValue;
	}

	@Override
	public String toString() {
		return name + " serie of length " + value.length;
	}
}
