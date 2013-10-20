package prediction;

import java.util.ArrayList;
import java.util.HashMap;

public class TimeSeries {

	static HashMap<String, TimeSeries> data = new HashMap<String, TimeSeries>();

	public int timeStart; // epoch
	public int timeStep;
	public ArrayList<Serie> series;

	public TimeSeries(String id, int timeStart, int timeStep , ArrayList<Serie> series) {
		this.timeStart = timeStart;
		this.timeStep = timeStep;
		this.series = series;
		TimeSeries.data.put(id, this);
	}

	public ArrayList<double[][]> getPlotData(){
		ArrayList<double[][]> result = new ArrayList<double[][]>();
		for (Serie s : series) {		
			double[][] m = new double[series.get(0).value.length][2];
			for (int i = 0; i < m.length; i++) {
				m[i][0] = timeStart+i*timeStep;
				m[i][1] = s.value[i];
			}
			result.add(m);
		}
		return result;		
	}

	public Serie getSerie(String serieName){
		for(Serie s :series){
			if(s.name == serieName)
				return s;
		}
		return null;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("TimeSeries \t timestart=");
		sb.append(timeStart);
		sb.append("\t timestep=");
		sb.append(timeStep);
		sb.append("\n");
		for(Serie s :series){
			sb.append("\t");
			sb.append(s);
			sb.append("\n");
		}
		return sb.toString();
	}
}
