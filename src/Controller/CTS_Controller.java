	package Controller;

import Model.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import static java.lang.Math.*;


/**
 * Place holder controller for the project
 * Please edit once we do something meaningful with it.
 * @author Joey McMaster
 * @author Nicholas Fiegel
 * @author Matt Theisen
 * @author Jackson
 *
 */
public class CTS_Controller {

	CTS_Model model;
	
	
	
	public CTS_Controller() {
		model = new CTS_Model();
		updateAzimuthAndAltitude();
		
	}
	
	// Constructor used for JUnit testing allowing a model to be passed in
	public CTS_Controller(CTS_Model model) {
		this.model = model;
		updateAzimuthAndAltitude();
	}
	
	
	public CTS_Controller(double latitude, double longitude, int year, int month, int day, int hour, int minutes, int seconds) {
		model = new CTS_Model(latitude, longitude, 0, 0);
		model.calcDaysSinceStandard(year, month, day, hour, minutes, seconds);
		updateAzimuthAndAltitude();
	}
	// Placeholder
	public HashMap<CTS_Star, ArrayList<CTS_Star>> getConstellations() {
		return null;
	}
	private void updateAzimuthAndAltitude() {
		// Update azimuth and altitude for all stars
		ArrayList<CTS_Star> starList = model.getStarList();		
		for (CTS_Star star : starList) {
			calcAzimuthAndAltitude(star);
		}
		
		// Update azimuth and altitude for all DSOs
		ArrayList<CTS_DeepSkyObject> DSOlist = model.getDSOlist();
		for (CTS_DeepSkyObject dso : DSOlist) {
			calcAzimuthAndAltitude(dso);
		}	
	}
	
	
	public CTS_Model getModel() {
		return model;
	}

	/**
	 * Calculates and sets the azimuth and altitude for a star.
	 * USES: getHourAngle, so transitively the assumptions in getLocalSiderialTime
	 * are in full affect for this method.
	 */
	public void calcAzimuthAndAltitude(CTS_SpaceObject star) {
		// Convert these values from degrees to radians prior to using math libs
		double declination = Math.toRadians(star.getDeclination());
		double latitude = Math.toRadians(model.getLatitude());
		double longitude = Math.toRadians(model.getLongitude());
		double ha = Math.toRadians(getHourAngle(star));
		double azimuth;
		
		// Calc altitude	
		double sinOfAlt = Math.sin(declination) * Math.sin(latitude) + Math.cos(declination) * Math.cos(latitude) * Math.cos(ha);
		double altitude = Math.asin(sinOfAlt);
		// Convert altitude back to degrees and set it
		star.setAltitude(Math.toDegrees(altitude));
		
		
		// Calc azimuth		
		double sinDec = Math.sin(declination);
		double cosDec = Math.cos(declination);
		double sinHA = Math.sin(ha);
		double cosHA = Math.cos(ha);
		double sinLat  = Math.sin(latitude);
		double cosLat = Math.cos(latitude);
		double sinAlt = Math.sin(altitude);
		double cosAlt = Math.cos(altitude);

		// Formula: http://www.convertalot.com/celestial_horizon_co-ordinates_calculator.html
		double cosA = (sinDec - sinAlt*sinLat)/(cosAlt*cosLat);
		double A = Math.toDegrees(Math.acos(cosA));
		
		if (sinHA < 0) {
			azimuth = A;
		} else {
			azimuth = 360-A;
		}
		
		star.setAzimuth(azimuth);
		

	}
	
	public double[] calcMoonPosition() {
		double dss = model.getDaysSinceStandard();
		
		// Obliquity of the ecliptic:
		// Formula: http://glossary.ametsoc.org/wiki/Obliquity_of_the_ecliptic
		// o = 23deg, 27min, 8.26sec - in Decimal
		double o = 23.4522944444;
		double currentYear = 2000 + (dss/365.25);
		double obliquity = o - .4684*(currentYear-1900)/3600;  // in degrees
		
		// RA/Dec Formula: https://aa.quae.nl/en/reken/hemelpositie.html
		// Geocentric ecliptic longitude compared to equinox of the date (in degrees)
		double LL = 218.316 + (13.176396 * dss);
		// Mean anomaly (in degrees)
		double M = 134.963 + (13.064993 * dss);
		// Mean distance of the moon from its ascending node (in degrees)
		double F = 93.272 + (13.229350 * dss);
		
		// Mod these values by 360 degrees to get angles (in degrees)
		LL %= 360;
		M %= 360;
		F %= 360;
		
		// Geocentric ecliptic coordinates:
		double ecLongitude = LL + 6.289 * sin(toRadians(M));
		double ecLatitude = 5.128 * sin(toRadians(F));	
		// normalize ecLongitude to 0-360 deg.
		while (ecLongitude < 0) {
			ecLongitude += 360;
		}
		
		double oblRad = toRadians(obliquity);
		double L = toRadians(ecLongitude);
		double B = toRadians(ecLatitude);

		// VALUES TEST GOOD TO THIS POINT
		
		// Convert ecliptic latitude and longitude to right ascension RA
		// and declination delta	
		double T = dss/36525;
		double eps = 23 + 26/60 + 21.448/3600 - (46.8150*T+.00059*T*T-.001813*T*T*T)/3600;
		System.out.println("EPS: "+eps);
		double epsRad = toRadians(eps);
		double X = cos(B)*cos(L);
		double Y = cos(epsRad)*cos(B)*sin(L) - sin(epsRad)*sin(B);
		double Z = sin(epsRad)*cos(B)*sin(L) + cos(epsRad)*sin(B);
		double R = sqrt(1-Z*Z);
		
		double delta = atan2(Z,R);
		double deltaDeg = toDegrees(delta);
		double RA = (24/PI)*atan2(Y,(X+R)); // in hours
		System.out.println("FIRST RA CALC (hrs): "+RA);
				
		// Compute sidereal time (deg) at Greenwich
		double lst = model.getLocalSiderialTime();
		
		// Local hour angle
		double tau = lst - (RA*15);
		while (tau < 0) {tau+=360;}
		while (tau > 360) {tau-=360;}
		tau = toRadians(tau); // hour angle in rads
		
		// Convert (tau, delta) to horizon coordinates (h,az) of
		// the observer
		double beta = toRadians(ecLatitude);
		double sinOfAlt = sin(beta)*sin(delta)+cos(beta)*cos(delta)*cos(tau);
		double tanOfAzi = -sin(tau)/(cos(beta)*tan(delta)-sin(beta)*cos(tau));
		// Parallax and refraction are disregarded here
		double alt = toDegrees(asin(sinOfAlt));
		double azi = toDegrees(atan(tanOfAzi));
		
		
		System.out.println("\nOBLIQUITY :"+obliquity);
		System.out.println("Ecliptic Lat: "+ecLatitude);
		System.out.println("Ecliptic Long: "+ecLongitude);
		System.out.println("\nDelta(deg): "+deltaDeg);
		System.out.println("RA: "+RA);
		System.out.println("HourAngle: "+toDegrees(tau));
		System.out.println("ALT: "+alt);
		System.out.println("AZI: "+azi);


		/*  
		// Declination:
		double dec = toDegrees(asin( sin(latRad)*cos(oblRad) + (cos(latRad)*sin(oblRad)*sin(longRad)) ) );
		
		// Right Ascension, given in hours:
		double RA = toDegrees(atan2(sin(longRad)*cos(oblRad)-tan(latRad)*sin(oblRad), cos(longRad)));
		// convert RA from degrees to hours:
		RA /= 15;
		*/
	
		//System.out.println("MOON_RA: "+RA+",  MOON_DEC:"+dec);
		
		// Returns [RA,Dec] of moon
		return new double[] {RA,toDegrees(delta)};
	}
	
	private void calcEclipticLongitude() {
		
	}
	
	
	/**
	 * Calculates and returns the hour angle of a star.
	 * USES: getLocalSiderialTime, so the assumptions in getLocalSiderialTime
	 * are in full affect for this method.
	 * @return a double representing the hour angle
	 */
	public double getHourAngle(CTS_SpaceObject star) {
		double lst = model.getLocalSiderialTime();
		double ha = lst - star.getRightAscension();

		while (ha < 0) {
			ha += 360;
		}

		while (ha > 360) {
			ha -= 360;
		}

		return ha;
	}


	
}
