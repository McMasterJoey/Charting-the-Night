package Testing;
import static org.junit.jupiter.api.Assertions.*;

import Model.CTS_Constellation;
import Model.CTS_Model;
import Model.CTS_Star;
import org.junit.jupiter.api.Test;

import Controller.CTS_Controller;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * Contains all automated Junit tests used as a test suite.
 * @author Joey McMaster
 * @author Nicholas Fiegel
 * @author Matt Theisen
 * @author Jackson
 *
 */
public class JUnitTests {

	@Test
	void controllerTest1() {
		CTS_Controller c = new CTS_Controller();
		assertTrue(c.getConstellations().size() == 88);
		CTS_Controller c1 = new CTS_Controller(50,50,2019,5,1,4,50,50);
		assertTrue(c1.getConstellations().size() == 88);
	}
	@Test 
	void controllerTest2() {
		CTS_Controller c = new CTS_Controller(50,50,2019,5,1,4,50,50);
		CTS_Constellation cos = c.getConstellations().get(0);
		assertTrue(cos.getConnections().size() == 5);
		c.getModel().build_constellationList("seleucid.fab");
		CTS_Star star = c.getModel().getStarByHip(736);
		//[NAME: UNNAMED STAR, MAG: 9.2, RA: 2.26398, DEC: -59.280974, ALT: -20.575535485780453, AZI: 168.86856287504375]
		double mag = star.getMagnitude();
		double ra = star.getRightAscension();
		double dec = star.getDeclination();
		double alt = star.getAltitude();
		double azi = star.getAzimuth();
		System.out.println(star);
		assertEquals(9.200, mag, .01);
		assertEquals(2.26398, ra, .01);
		assertEquals(-59.280974, dec, 0.01);
		assertEquals(-20.575535485780453, alt, .01);
		assertEquals(168.86856287504375, azi, 0.01);
	}
	@Test
	void modelTest1() {
		CTS_Model m = new CTS_Model();
		m.build_constellationList("seleucid.fab");
	}
	@Test
	void starTest1() {
		CTS_Star s = new CTS_Star(0,"test",-2,3,4);
		CTS_Star s1 = new CTS_Star(1,"Test2",-1,5,6,1,3);
	}
}
