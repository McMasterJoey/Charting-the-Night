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

	private CTS_Model model;
	
	/**
	 * The constructor without any args.
	 * Default constructor.
	 */
	public CTS_Controller() {
		model = new CTS_Model();
		updateAzimuthAndAltitude();
		
	}
	/**
	 * The constructor for speific instance of time.
	 * @param latitude The latitude of the origin being observed from.
	 * @param longitude The longitude of the origin being observed from.
	 * @param year The year that be viewed from.
	 * @param month The month that is being viewed from.
	 * @param day The day that is being viewed from.
	 * @param hour The hour that is being viewed from.
	 * @param minutes The minute that is being viewed from.
	 * @param seconds The second that is being viewed from.
	 */
	public CTS_Controller(double latitude, double longitude, int year, int month, int day, int hour, int minutes, int seconds) {
		model = new CTS_Model(latitude, longitude, 0, 0);
		model.calcDaysSinceStandard(year, month, day, hour, minutes, seconds);
		updateAzimuthAndAltitude();
	}
	/**
	 * Fetches the constellations from the model.
	 * @return The constellations.
	 */
	public ArrayList<CTS_Constellation> getConstellations() {
		return model.getConstellations();
	}
	/**
	 * Sets the constellation data base to be used.
	 * @param type The file name to be used.
	 * @return If it succeeded or not.
	 */
	public boolean setConstellationType(String type) {
		return model.build_constellationList(type);
	}
	/**
	 * Updates the Azimuth and Altitude of all objects in that data bases.
	 */
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
		
		ArrayList<CTS_Planet> planets = model.getPlanetList();
		for (CTS_Planet planet : planets) {
			calcAzimuthAndAltitude(planet);
		}
	}
	
	/**
	 * Returns a pointer to the model.
	 * @return A pointer to the model.
	 */
	public CTS_Model getModel() {
		return model;
	}

	/**
	 * Calculates and sets the azimuth and altitude for a star.
	 * USES: getHourAngle, so transitively the assumptions in getLocalSiderialTime
	 * are in full affect for this method.
	 * @param star The star to the calculate the Azimuth and Altitude of.
	 */
	public void calcAzimuthAndAltitude(CTS_SpaceObject star) {
		// Convert these values from degrees to radians prior to using math libs
		double declination = Math.toRadians(star.getDeclination());
		double latitude = Math.toRadians(model.getLatitude());
		double ha = Math.toRadians(getHourAngle(star));
		double azimuth;
		
		// Calc altitude	
		double sinOfAlt = Math.sin(declination) * Math.sin(latitude) + Math.cos(declination) * Math.cos(latitude) * Math.cos(ha);
		double altitude = Math.asin(sinOfAlt);
		// Convert altitude back to degrees and set it
		star.setAltitude(Math.toDegrees(altitude));
		
		// Calc azimuth		
		double sinDec = Math.sin(declination);
		double sinHA = Math.sin(ha);
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
		
		
		if (star instanceof CTS_Planet) {
			System.out.println("The altitude of " + star.getName() + " is " + star.getAltitude()  + " and the Azimuth is " + star.getAzimuth());
		}

	}
	
	
	
	/**
	 * Calculates and returns the hour angle of a star.
	 * USES: getLocalSiderialTime, so the assumptions in getLocalSiderialTime
	 * are in full affect for this method.
	 * @param star A star to get the hour angle of.
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


	/**
	 * Getter to return the constellationDBs HashMap.
	 * @return A data structure containing the list of constellation
	 * databases by a string with the associated culture.
	 */
	public HashMap<String, String> getModelConstellationDBs() {
		return model.getConstellationDBs();
	}
	
}
