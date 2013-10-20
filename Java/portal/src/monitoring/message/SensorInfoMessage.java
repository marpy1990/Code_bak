package monitoring.message;

import monitoring.manager.SensorInfo;
import pubsub.msg.AbstractMessage;

public class SensorInfoMessage extends AbstractMessage {

	public SensorInfoMessage(SensorInfo info) {
		super("sensor");
		put("sensorInfo", info);
	}

}
