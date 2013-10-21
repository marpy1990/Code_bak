package pubsub.test;

import pubsub.Broker;
import pubsub.MessageHandler;
import pubsub.msg.*;

public class PubSubBasicTest {
	public static void main(String[] args) throws Exception {
		final Broker a = new Broker(20000);
		final Broker b = new Broker(20001);
		final Broker c = new Broker(20002);
		a.connect(c.getLocalAddr());
		b.connect(c.getLocalAddr());
		Thread.sleep(1000);
		String key = c.subscribe(new Subscription("hello"), new MessageHandler() {
			
			@Override
			public void handleMessage(AbstractMessage msg) {
				System.out.println("c receives: " + ((TextMessage)msg).getContent());
			}
		});
		Thread.sleep(1000);
		a.publish(new TextMessage("hello", "hello1"));
		a.publish(new TextMessage("hello", "hello2"));
		b.publish(new TextMessage("hello", "hellofromb"));
		Thread.sleep(1000);
		c.unsubscribe(key);
		Thread.sleep(1000);
		a.publish(new TextMessage("hello", "afterunsub1"));
		a.publish(new TextMessage("hello", "afterunsub2"));
	}
}
