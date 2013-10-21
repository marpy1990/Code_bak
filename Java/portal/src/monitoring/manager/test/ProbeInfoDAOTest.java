package monitoring.manager.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import monitoring.manager.ProbeDefinitionDAO;
import monitoring.sensor.ProbeInfo;

import org.junit.BeforeClass;
import org.junit.Test;

public class ProbeInfoDAOTest {
	static ProbeDefinitionDAO dao;
	
	@BeforeClass
	public static void init() {
		dao = new ProbeDefinitionDAO("probe-info.json");
		dao.deleteAll();
	}
	@Test
	public void testInsert() {
		ProbeInfo pi = new ProbeInfo();
		pi.setAuthor("Yu Cheng");
		pi.setClassName("cn.edu.sjtu.cep.rule.Filter");
		pi.setDescription("Basic Filter Implementation");
		pi.setId("filter");
		pi.setName("Filter");
		dao.insert(pi);
		
		pi = new ProbeInfo();
		pi.setAuthor("Yu Cheng");
		pi.setClassName("cn.edu.sjtu.cep.rule.Splitter");
		pi.setDescription("Basic Splitter Implementation");
		pi.setId("splitter");
		pi.setName("Splitter");
		dao.insert(pi);
		assertTrue(dao.findAll().size()==2);
	}
	@Test
	public void testReload() {
		ProbeDefinitionDAO dao2 = new ProbeDefinitionDAO("probe-info.json");
		assertTrue(dao2.findAll().size()==2);
	}
	
	@Test
	public void testUpdate() {
		ProbeInfo pi = new ProbeInfo();
		pi.setAuthor("Gerrard");
		pi.setClassName("cn.edu.sjtu.cep.rule.Splitter");
		pi.setDescription("Basic Splitter Implementation");
		pi.setId("splitter");
		pi.setName("Splitter");
		dao.update(pi);
		ProbeInfo pi2 = dao.findById("splitter");
		assertEquals("Gerrard", pi2.getAuthor());
	}
	
	@Test
	public void testRemove() {
		dao.delete("splitter");
		assertEquals(1, dao.findAll().size());
		assertEquals("filter", dao.findById("filter").getId());
	}

}
