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
		CTS_Star star = cos.getConnections().keySet().iterator().next();
		// [NAME: 65The Aql, MAG: 3.24, RA: 302.826195, DEC: -0.821461, ALT: 29.37366182948049, AZI: 225.86179890378565]
		double mag = star.getMagnitude();
		double ra = Math.floor(star.getRightAscension());
		double dec = Math.floor(star.getDeclination());
		double alt = Math.floor(star.getAltitude());
		double azi = Math.floor(star.getAzimuth());
		System.out.println(star);
		assertTrue(mag == 3.24);
		assertTrue(ra == 302.0);
		assertTrue(dec == -1.0);
		assertTrue(alt == 29);
		assertTrue(azi == 225);
	}
	@Test
	void modelTest1() {
		CTS_Model m = new CTS_Model();
		m.build_constellationList("seleucid.fab");
	}
}
