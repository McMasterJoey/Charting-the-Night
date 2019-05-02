package Model;

import java.lang.Math;
/**
 * The Planet object used by the project.
 * Stores an instance of a planet.
 * @author Joey McMaster
 * @author Nicholas Fiegel
 * @author Matt Theisen
 * @author Jackson
 *
 */
public class CTS_Planet extends CTS_SpaceObject{
	
	/**
	 * The constructor of the object. Inits it and sets it up.
	 * @param nameIn Something.
	 * @param setID Something.
	 * @param M Something.
	 * @param e Something.
	 * @param a Something.
	 * @param N Something.
	 * @param w Something.
	 * @param i Something.
	 * @param ecl Something.
	 */
	public CTS_Planet(String nameIn, int setID, double M, double e, double a, double N, double w, double i, double ecl) {
		super (setID, nameIn, 0.0, 0.0);
		
		M = Math.toRadians(M);
		double E = M + e * Math.sin(M) * (1.0 + e * Math.cos(M));
		//Anomaly convergence 
		if (e > 0.05) {
			double E0 = E;
			double E1 = E0 - (E0 - e * Math.sin(E0) - M) / (1 -e * Math.cos(E0));
			while (Math.abs(E0 - E1) > 0.06) {
				E0 = E1;
				E1 = E0 - (E0 - e * Math.sin(E0) - M) / (1 -e * Math.cos(E0));
			}
			E = E0;
		}
		
		double xv = a * (Math.cos(E) - e);
		double yv = a * (Math.sqrt(1.0 - e*e) * Math.sin(E));
		
		double v = Math.atan2(yv, xv);
		double r = Math.sqrt(xv*xv + yv*yv);
		
		double xh = r * (Math.cos(N) * Math.cos(v+w) - Math.sin(N) * Math.sin(v+w) * Math.cos(i));
		double yh = r * ( Math.sin(N) * Math.cos(v+w) + Math.cos(N) * Math.sin(v+w) * Math.cos(i) );
		double zh = r * ( Math.sin(v+w) * Math.sin(i) );
		
		double lonecl = Math.atan2( yh, xh );
		double latecl = Math.atan2( zh, Math.sqrt(xh*xh+yh*yh) );
		
		xh = r * Math.cos(lonecl) * Math.cos(latecl);
		yh = r * Math.sin(lonecl) * Math.cos(latecl);
		zh = r * Math.sin(latecl);
		
		double xg, yg, zg;
		
		if (nameIn.equals("Moon")) {
			xg = xh;
			yg = yh;
			zg = zh;
		} else {
			double lonsun = v + w;
			double xs = r * Math.cos(lonsun);
			double ys = r * Math.sin(lonsun);
			
			xg = xh + xs;
			yg = yh + ys;
			zg = zh;
		}
		
		double xe = xg;
		double ye = yg * Math.cos(ecl) - zg * Math.sin(ecl);
		double ze = yg * Math.sin(ecl) + zg * Math.cos(ecl);
		
		this.rightAscension = Math.toDegrees(Math.atan2(ye, xe));
		this.declination = Math.toDegrees(Math.atan2(ze, Math.sqrt(xe*xe+ye*ye))); 
		
		
	}
}
