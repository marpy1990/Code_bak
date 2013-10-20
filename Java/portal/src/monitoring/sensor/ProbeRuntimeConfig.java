package monitoring.sensor;

import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;

public class ProbeRuntimeConfig {
	private String prcId, probeId;
	private int interval;
	private TimeUnit timeunit;
	private boolean running;
	private String eventType;
	
	public int getInterval() {
		return interval;
	}
	public void setInterval(int interval) {
		this.interval = interval;
	}
	public TimeUnit getTimeunit() {
		return timeunit;
	}
	public void setTimeunit(TimeUnit timeunit) {
		this.timeunit = timeunit;
	}
	public boolean isRunning() {
		return running;
	}
	public void setRunning(boolean running) {
		this.running = running;
	}
	/**
	 * Check if the new config is equivalent to this one
	 * @param config
	 * @return
	 */
	public boolean isChanged(ProbeRuntimeConfig config) {
		return (probeId.equals(config.probeId) && interval == config.interval
				&& timeunit.equals(config.timeunit) && running == config.running);
	}
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public void setPrcId(String prcId) {
		this.prcId = prcId;
	}
	public String getPrcId() {
		return prcId;
	}
	public String getProbeId() {
		return probeId;
	}
	public void setProbeId(String probeId) {
		this.probeId = probeId;
	}
	public static void main(String[] args) {
		ProbeRuntimeConfig prc = new ProbeRuntimeConfig();
		prc.setEventType("e1");
		prc.setInterval(1);
		prc.setPrcId("0");
		prc.setProbeId("p1");
		prc.setRunning(true);
		prc.setTimeunit(TimeUnit.SECONDS);
		System.out.println(new Gson().toJson(prc));
	}
}
