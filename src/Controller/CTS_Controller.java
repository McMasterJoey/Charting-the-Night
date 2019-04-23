	package Controller;

import Model.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


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
		
		// Calc altitude	
		double sinOfAlt = Math.sin(declination) * Math.sin(latitude) + Math.cos(declination) * Math.cos(latitude) * Math.cos(ha);
		double altitude = Math.asin(sinOfAlt);
		// Convert altitude back to degrees and set it
		star.setAltitude(Math.toDegrees(altitude));
		
		
		// Calc azimuth
		double cosDec = Math.cos(declination);
		double sinHA = Math.sin(ha);
		double sinDec = Math.sin(declination);
		double cosLat = Math.cos(latitude);
		double cosHA = Math.cos(ha);
		double sinLat  = Math.sin(latitude);
		
		double azimuth = Math.atan(-(cosDec*sinHA)/(sinDec*cosLat - cosDec*cosHA*sinLat));
		// Convert azimuth back to degrees
		azimuth = Math.toDegrees(azimuth);
		
		// Azimuth will be in the interval [0,360)
		azimuth = azimuth % 360;

		// If azimuth is given as negative, find the positive coterminal angle
		if (azimuth < 0) {
			azimuth = (360+azimuth);
		}
		star.setAzimuth(azimuth);

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
