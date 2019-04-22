package Model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The CTS_Constellation is a class that holds all the associated data with a constellation. 
 * @author Joey McMaster
 * @author Nicholas Fiegel
 * @author Matt Theisen
 * @author Jackson
 *
 */
public class CTS_Constellation extends CTS_SpaceObject {
	private List<CTS_Star> stars;
	private List<CTS_Constellation_Line> lines;
	public CTS_Constellation(String name, double rightAcension, double declination) {
		super(0,name, rightAcension, declination);
		this.stars = new ArrayList<CTS_Star>();
	}
	public CTS_Constellation(String name, double rightAcension, double declination, double altitude, double azimuth, Collection<CTS_Star> stars) {
		super(0,name, rightAcension, declination, altitude, azimuth);
		this.stars = new ArrayList<CTS_Star>();
	}
	/**
	 * A new method to add a star to this constellation
	 * @param newStar The star in question to be added
	 */
	public void add(CTS_Star newStar) {
		this.stars.add(newStar);
	}
	/**
	 * test function, delete later
	 */
	public void prinStars() {
		for (CTS_Star x : this.stars) {
			System.out.print(x.name + " ");
		}
	}
}
