package pubsub.test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pubsub.MessageHandler;
import pubsub.RouteTable;
import pubsub.filter.Filter;
import pubsub.msg.AbstractMessage;
import pubsub.msg.Subscription;

public class FilterRouteTableTest {
	

	@Test
	public void test() {
		RouteTable rt = new RouteTable(null);
		MessageHandler h = new MessageHandler() {
			@Override
			public void handleMessage(AbstractMessage msg) {
				System.out.println(msg);
			}
		};
		Subscription sub1 = new Subscription("a", new Filter(Filter.EQUALS, "A", 1));
		sub1.setID("sub1");
		Subscription sub2 = new Subscription("a", new Filter(Filter.EQUALS, "A", 2));
		sub2.setID("sub2");
		rt.addSubscriptionFromLocal(sub1, h);
		rt.addSubscriptionFromLocal(sub2, h);
		assertEquals(2, rt.getLocalSubTable().keySet().size());
		rt.removeSubscriptionFromLocal("sub2");
		assertEquals(1, rt.getLocalSubTable().keySet().size());
	}

}
