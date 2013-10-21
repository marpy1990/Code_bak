package cep;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Main {
	
	private static CepEngineRunner runner = new CepEngineRunner();

	public static void main(String[] args) {
		CepRuleConfig crc = new CepRuleConfig();
		
		crc.setClassName("cn.edu.sjtu.cep.rule.UnbalancedClusterDetector");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("incomingEventType", "Cpu");
		params.put("complexEventType", "UnbalancedCluster");
		params.put("attribute", "user");
		params.put("imbalanceThreshold", 0.1);
		params.put("timeWindow", 10000);
		crc.setParams(params);
		crc.setRunning(true);
		
		runner.addListener(new ComplexEventListener() {
			
			@Override
			public void update(Event e) {
				System.out.println(e);
				runner.match(e);
			}
		});
		
		runner.changeSingleCepRuleConfig("1",crc);
		
//		crc = new CepRuleConfig();
//		crc.setClassName("cn.edu.sjtu.cep.rule.Filter");
//		params = new HashMap<String, Object>();
//		params.put("incomingEventType", "Memory");
//		params.put("complexEventType", "HighMemory");
//		params.put("attribute", "usedPerc");
//		params.put("lowerLimit", 0.5);
//		crc.setParams(params);
//		crc.setRunning(true);
//		
//		runner.changeSingleCepRuleConfig("2",crc);
//		
//		crc = new CepRuleConfig();
//		crc.setClassName("cn.edu.sjtu.cep.rule.Filter");
//		params = new HashMap<String, Object>();
//		params.put("incomingEventType", "HighCpu");
//		params.put("complexEventType", "MediumCpu");
//		params.put("attribute", "user");
//		params.put("upperLimit", 0.8);
//		crc.setParams(params);
//		crc.setRunning(true);
//		
//		runner.changeSingleCepRuleConfig("3",crc);
		
		new Thread(runner).start();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				int i=0;
				while (true) {
					
					for (int j=1; j<=5; j++) {
						Event e = new Event("Cpu");
						e.setSource("127.0.0.1:2000" + j);
						double value = 0.3;
						if (j==5) value = 0.8;
						e.setAttribute("user", value);
						e.setTime(System.currentTimeMillis());
						runner.match(e);
					}
					
					
					if (++i==10) i=0;
					
//					Event m = new Event("Memory");
//					m.setAttribute("usedPerc", 0.55);
//					runner.match(m);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						continue;
					}
				}
			}
		}).start();
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}
}
