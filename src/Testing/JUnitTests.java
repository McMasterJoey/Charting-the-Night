package Testing;
import static org.junit.jupiter.api.Assertions.*;

import Model.CTS_Star;
import org.junit.jupiter.api.Test;

import java.math.RoundingMode;
import java.text.DecimalFormat;

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
	void dummyTest() {
		System.out.println("Ran dummyTest()");
	}

	@Test
	void test_Star_calcDaysSinceStandard() {
		CTS_Star star = new CTS_Star("test", 1, 0, 0);
		DecimalFormat round4 = new DecimalFormat("#.####");

		round4.setRoundingMode(RoundingMode.HALF_UP);
		star.calcDaysSinceStandard(1998, 8, 10, 23, 10, 0);
		assertEquals("-508.5347", round4.format(star.getDaysSinceStandard()));
		star.calcDaysSinceStandard(2008, 4, 4, 15, 30, 0);
		assertEquals("3016.1458", round4.format(star.getDaysSinceStandard()));
	}
}
