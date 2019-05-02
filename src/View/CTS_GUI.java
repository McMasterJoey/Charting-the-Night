package View;

import java.util.*;

import Controller.CTS_Controller;
import Model.CTS_Constellation;
import Model.CTS_SpaceObject;
import Model.CTS_Star;
import Model.CTS_DeepSkyObject;
import Model.CTS_Planet;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import static java.lang.Math.*;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * The GUI of the project.
 * Generates a window that the user can interact with.
 * @author Joey McMaster
 * @author Nicholas Fiegel
 * @author Matt Theisen
 * @author Jackson
 *
 */
public class CTS_GUI extends Application {
	public static final int VIEWING_AREA_WIDTH = 600;
	public static final int VIEWING_AREA_HEIGHT = 600;
	private GraphicsContext gc;
	private VBox uicontrols;
	private CTS_GUI_Dialoguebox input;
	private Canvas canvas;
	private Alert GUIerrorout;
	private CTS_Controller controller;
	private Color[] usercolors;
	private int colorSetterId = 0;
	private String userSelectedConstellationFileName = "western.fab";
	public CTS_GUI(String[] args) {
		launch(args);
	}
	public CTS_GUI() { 
		// 0 = Star color, 1 = Low mag star, 2 = Very low mag star.
		// 3 = DSO, 4 = Sky background, 5 = Circle around skybackgrund
		// 6 = Overall background, 7 = Lat/long txt color, 8 = Constelation line color.
		usercolors = new Color[9];
		usercolors[0] = Color.WHITE;
		usercolors[1] = Color.GREY;
		usercolors[2] = Color.DARKGREY;
		usercolors[3] = Color.BROWN;
		usercolors[4] = Color.rgb(0,0,0,.4);
		usercolors[5] = Color.rgb(0,0,125,.5);
		usercolors[6] = Color.MIDNIGHTBLUE;
		usercolors[7] = Color.LIME;
		usercolors[8] = Color.rgb(255,255,255,.25);
	}
	@Override
	/**
	 * The starting point of the app.
	 */
	public void start(Stage stage) throws Exception {
		controller = new CTS_Controller();
		stage.setTitle("Charting The Stars");
		//stage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
		BorderPane mainpane = new BorderPane();
		canvas = new Canvas(VIEWING_AREA_WIDTH, VIEWING_AREA_HEIGHT);
		mainpane.setCenter(canvas);
		gc = canvas.getGraphicsContext2D();
		gc.setFill(usercolors[6]);
		gc.fillRect(0, 0,  VIEWING_AREA_WIDTH, VIEWING_AREA_HEIGHT);
		drawCircle(VIEWING_AREA_WIDTH / 2, VIEWING_AREA_WIDTH / 2, VIEWING_AREA_HEIGHT / 2, usercolors[4]);
		// Setup Dialog box
        setUpDialoguebox();
        // Plot right off the bat
        chartTheStars();
        // Add a click handler so when the GUI is clicked, the settings menu comes up.
        mainpane.setOnMouseClicked((event) -> {
        	double distance = distanceToCenterOfDisplay(event.getX(), event.getY());
        	double centerx = VIEWING_AREA_WIDTH / 2;
        	if (distance > centerx) {
        		double x = event.getX() - centerx;
        		double y = event.getY() - centerx;
        		if (x < 0 && y < 0) {
        			adjustObserverLocationByClick(1,10);
        		} else if (x < 0 && y > 0) {
        			adjustObserverLocationByClick(3,10);
        		} else if (x > 0 && y < 0) {
        			adjustObserverLocationByClick(2,10);
        		} else if (x > 0 && y > 0) {
        			adjustObserverLocationByClick(4,10);
        		}
        		chartTheStars();
        	} else {
        		input.showAndWait();
        	}
        });
		// Display it!
		Scene scene = new Scene(mainpane, VIEWING_AREA_WIDTH , VIEWING_AREA_HEIGHT);
        stage.setScene(scene);
        stage.show();
	}
	/**
	 * Updates the GUI with the star chart of the inputed time and date.
	 */
	public void chartTheStars() {
		resetSkyDrawing();
		double[] data = getUserInputFromUIControls(); // ASSUMES IT IS VALID.
		controller = new CTS_Controller(data[0],data[1],(int) data[2], (int) data[3], (int) data[4],(int) data[5], (int) data[6], (int) data[7]); // Only using latitude and longitude
		boolean succesful = controller.setConstellationType(userSelectedConstellationFileName);
		// strokeText(String text, double x, double y, double maxWidth)
		gc.setStroke(usercolors[7]);
		gc.strokeText("Latitude: " + data[0], 10, 15, 190);
		gc.strokeText("Longitude: " + data[1], 10, 30, 190);
		gc.strokeText("Date: " + String.format("%.0f", data[2]) + "-" + String.format("%.0f", data[3]) + "-" + String.format("%.0f", data[4]),
				10,	45, 190);
		gc.strokeText("Time: " + String.format("%.0f", data[5]) + ":" + String.format("%.0f", data[6]) + ":" + String.format("%.0f", data[7]) ,
				10,	60, 190);
		gc.strokeText("Constellation set: " + upperCaseFL(userSelectedConstellationFileName).substring(0,userSelectedConstellationFileName.length() - 4),
				10,	(VIEWING_AREA_HEIGHT - 5), 210);
		ArrayList<CTS_Star> n = controller.getModel().getStarList();
		ArrayList<CTS_DeepSkyObject> d = controller.getModel().getDSOlist();
		double alt = 0, mag = 0;
		boolean[] plottingstatus = getCheckBoxes();
		if (plottingstatus[0]) {
			// Plot stars
			for(int x = 0; x < n.size(); x++) {
				CTS_Star star = n.get(x);
				try {
					alt = star.getAltitude();
					mag = star.getMagnitude();
					// I named it cupcake because when I first tested it
					// The result in the GUI was a cupcake!
					double cupcake = magnitudeToRadius(mag);
	                if (alt >= 0 && mag < -20) {
	                	// The sun is the only star with mag < -20, color it yellow.
	                    drawSpaceObject(star, cupcake,Color.YELLOW);
	                }
	                else if (alt >= 0 && mag < 6) {
						drawSpaceObject(star, cupcake,usercolors[0]);
					} else if (alt >= 0 && mag < 9) {
						if (mag < 7) {
							drawSpaceObject(star, .2,usercolors[1]);
						} else if (mag < 8){
							drawSpaceObject(star, .14,usercolors[2]);
						} else {
							drawSpaceObject(star, .09,usercolors[2]);
						}
					}
				} catch(IllegalArgumentException e) {
				}
			}
		}
		if (plottingstatus[1]) {
			// Plot DSOs
			for(int x = 0; x < d.size(); x++) {
				CTS_DeepSkyObject dso = d.get(x);
				try {
					alt = dso.getAltitude();
					mag = dso.getMagnitude();
					if (alt >= 0 && mag < 6) {
						drawSpaceObject(dso,1,usercolors[3]);
					}
				} catch(IllegalArgumentException e) {
				}
			}
		}
		if (plottingstatus[2] && succesful) {
			// Plot Constellations
			ArrayList<CTS_Constellation> constellations = controller.getConstellations();
			if (constellations == null) {
				return;
			}
			for (CTS_Constellation constellation : constellations) {
				HashMap<CTS_Star, ArrayList<CTS_Star>> connections = constellation.getConnections();
				HashSet<CTS_Star> keys = new HashSet<>(connections.keySet());
				for (CTS_Star fromStar : keys) {
					double[] from = getPositionOfSpaceObject(fromStar);
					for (CTS_Star toStar : connections.get(fromStar)) {
						double[] to = getPositionOfSpaceObject(toStar);
						if (from != null && to != null) {
							drawLine(from[0], from[1], to[0], to[1], usercolors[8]);
						}
					}
				}
			}
		}
		if (plottingstatus[2] && !succesful) {
			GUIerrorout = new Alert(AlertType.ERROR, "Internal Application Error!\nConstellation files did not load in correctly.\nCheck to verify the resources package has all the needed .fab files.");
			GUIerrorout.showAndWait();
		}
		if (plottingstatus[3]) {
			try {
				for (CTS_Planet x : controller.getModel().getPlanetList()) {
					if (x.getAltitude() >= 0 && x.getAltitude() <= 90) {
						if (x.getId() == 200000) { // Mercury 
							drawSpaceObject(x , 4, Color.BROWN);
						} else if (x.getId() == 200001) { // Venus
							drawSpaceObject(x , 5, Color.TOMATO);
						} else if (x.getId() == 200002) { // Moon
							drawSpaceObject(x , 7, Color.DARKSLATEGREY);
						} else if (x.getId() == 200003) { // Mars
							drawSpaceObject(x , 6, Color.DARKRED);
						} else if (x.getId() == 200004) { // Jupiter
							drawSpaceObject(x , 5, Color.TAN);
						} else if (x.getId() == 200005) { // Saturn
							drawSpaceObject(x , 4, Color.BEIGE);
						} else if (x.getId() == 200006) { // uranus
							drawSpaceObject(x , 3, Color.LIGHTBLUE);
						} else if (x.getId() == 200007) { // neptune
							drawSpaceObject(x , 2, Color.DARKBLUE);
						}
					}
				}
			} catch(IllegalArgumentException e) {
			}
			
		}
	}
	/**
	 * Draws a circle of the given radius, at the specific x,y location on the graphics context. 
	 * x and y are absolute, relative to the upper left side of the rectangular view. 
	 * @param radius The radius of the circle
	 * @param x The horizontal coordinate of the grid to put the circle center. 
	 * @param y The vertical coordinate of the grid to put the circle center.
	 * @param color The color of the circle.
	 * @throws IllegalArgumentException If the circle can not be fully displayed on the graphics context.
	 */
	public void drawCircle(int radius, int x, int y, Color color) {
		int xx = x - radius;
		int yy = y - radius;
		if (xx < 0 || yy < 0 || xx > (VIEWING_AREA_WIDTH -  radius)|| yy > (VIEWING_AREA_HEIGHT - radius)) {
			throw new IllegalArgumentException("drawCircle: invalid set of radius and x/y cordnates");
		}
		gc.setFill(color);
		gc.fillOval(xx,yy, radius * 2, radius * 2);
	}
	/**
	 * Draws a circle of the given radius, at the specific x,y location on the graphics context. 
	 * x and y are absolute, relative to the upper left side of the rectangular view. 
	 * @param radius The radius of the circle
	 * @param x The horizontal coordinate of the grid to put the circle center. 
	 * @param y The vertical coordinate of the grid to put the circle center.
	 * @param color The color of the circle.
	 * @throws IllegalArgumentException If the circle can not be fully displayed on the graphics context.
	 */
	public void drawCircle(double radius, double x, double y, Color color) {
		double xx = x - radius;
		double yy = y - radius;
		if (xx < 0.0 || yy < 0.0 || xx > (VIEWING_AREA_WIDTH -  radius)|| yy > (VIEWING_AREA_HEIGHT - radius)) {
			throw new IllegalArgumentException("drawCircle: invalid set of radius and x/y cordnates");
		}
		gc.setFill(color);
		gc.fillOval(xx,yy, radius * 2, radius * 2);
	}
	/**
	 * draws a circle outline of the given radius at the inputed coornates of the given color.
	 * @param radius The radius of the circle
	 * @param x The horizontal coordinate of the grid to put the circle center. 
	 * @param y The vertical coordinate of the grid to put the circle center.
	 * @param color The color of the circle.
	 * @throws IllegalArgumentException If the circle can not be fully displayed on the graphics context.
	 */
	public void drawCircleOutline(double radius, double x, double y, Color color) {
		double xx = x - radius;
		double yy = y - radius;
		if (xx < 0.0 || yy < 0.0 || xx > (VIEWING_AREA_WIDTH -  radius)|| yy > (VIEWING_AREA_HEIGHT - radius)) {
			throw new IllegalArgumentException("drawCircle: invalid set of radius and x/y cordnates");
		}
		gc.setStroke(color);
		gc.strokeOval(xx,yy, radius * 2, radius * 2);
	}
	/**
	 * draws a line between the two points of the inputed color.
	 * @param startx The x of the start of the line.
	 * @param starty The y of the start of the line.
	 * @param endx The x of the end of the line.
	 * @param endy The y of the end of the line.
	 * @param color The color of the line.
	 */
	public void drawLine(double startx, double starty, double endx, double endy, Color color) {
		gc.setStroke(color);
		gc.strokeLine(startx,starty,endx,endy);
	}
	/**
	 * Wrapper function to draw space objects.
	 * @param obj The space object to be drawn.
	 * @param radius Its radius
	 * @param color Its color
	 * @throws IllegalArgumentException When something that is input causes an issue with displaying.
	 */
	public void drawSpaceObject(CTS_SpaceObject obj, int radius, Color color) {
		double[] result = getPositionOfSpaceObject(obj);
		if (result == null) {
			//System.err.println("getPostionOfSpaceObject returned null!");
			throw new IllegalArgumentException("Something went wrong");
		}
		drawCircle(radius, (int) result[0], (int) result[1], color);
	}
	/**
	 * Wrapper function to draw space objects.
	 * @param obj The space object to be drawn.
	 * @param radius Its radius
	 * @param color Its color
	 * @throws IllegalArgumentException When something that is input causes an issue with displaying.
	 */
	public void drawSpaceObject(CTS_SpaceObject obj, double radius, Color color) {
		double[] result = getPositionOfSpaceObject(obj);
		if (result == null) {
			//System.err.println("getPostionOfSpaceObject returned null!");
			throw new IllegalArgumentException("Something went wrong");
		}
		drawCircle(radius, result[0], result[1], color);
	}
	/**
	 * Sets up the Dialoguebox that is used to take inputs
	 */
	private void setUpDialoguebox() {
		input = new CTS_GUI_Dialoguebox();
		input.setTitle("Settings");
		BorderPane pane = new BorderPane();
		LocalTime t = LocalTime.now();
		LocalDate d = LocalDate.now();
		uicontrols = new VBox(10);
		HBox box0 = new HBox(5);
		box0.getChildren().addAll(new Label("Latitude: "),new TextField("0"));
		HBox box1 = new HBox(5);
		box1.getChildren().addAll(new Label("Longitude: "),new TextField("0"));
		HBox box2 = new HBox(5);
		box2.getChildren().addAll(new Label("Date: "),new TextField(d.toString()));
		TextField time = new TextField(t.getHour() + ":" + t.getMinute() + ":" + (int) floor(t.getSecond()));
		HBox box3 = new HBox(5);
		box3.getChildren().addAll(new Label("Time: "),time);
		// Check boxes
		HBox box5 = new HBox(5);
		CheckBox c1 = new CheckBox("Plot Stars");
		c1.setSelected(true);
		CheckBox c2 = new CheckBox("Plot DSOs");
		c2.setSelected(true);
		CheckBox c3 = new CheckBox("Plot Constellations");
		c3.setSelected(true);
		CheckBox c4 = new CheckBox("Plot Planets");
		c4.setSelected(true);
		box5.getChildren().addAll(c1,c2,c3,c4);
		// Color Picker
		// 0 = Star color, 1 = Low mag star, 2 = Very low mag star.
		// 3 = DSO, 4 = Sky background, 5 = Circle around sky background
		// 6 = Overall background, 7 = Lat/long txt color, 8 = Constellation line color.
		HBox box6 = new HBox(5);
		TextField colorSet = new TextField("255,255,255,1");
		Button setcolorbutton = new Button("Submit Color");
		box6.getChildren().addAll(new Label("Color to Set: "),colorSet,setcolorbutton,new Label("[PREVIEW COLOR]"));
		setcolorbutton.setOnAction((event) -> { colorPickerHander(); });
        MenuBar mainmenu = new MenuBar();
        Menu colorpickermenu = new Menu("Set Custom Colors");
        String[] colorstrs = {"Star Color","Low Magnituide Star Color","Very Low Magnituide Star Color", "Deep Space Object Color","Sky Background Color",
        		"Circle Around Sky Background Color","Overall Background Color", "Latitude/Longitude Text Color","Constellation Line Color"};
        for(int x = 0; x < colorstrs.length; x++) {
        	MenuItem opt = new MenuItem(colorstrs[x]);
        	int id = x;
        	opt.setOnAction((event) -> { colorSetterId = id; });
        	colorpickermenu.getItems().add(opt);
        }
        Menu cspm = new Menu("Constellation Set 1");
        Menu cspm2 = new Menu("Constellation Set 2");
        Menu cspm3 = new Menu("Constellation Set 3");
        String[] filenames = {"western","arabic","armintxe","belarusian","boorong","chinese","contemporary_chinese","dakota","egyptian","hawaiian_starlines","indian"};
        for(int x = 0; x < filenames.length; x++) {
        	String datafile = filenames[x] + ".fab";
        	filenames[x].replace('_', ' ');
        	MenuItem opt = new MenuItem(upperCaseFL(filenames[x]));
        	opt.setOnAction((event) -> { userSelectedConstellationFileName = datafile; });
        	cspm.getItems().add(opt);
        }
        String[] filenames2 = {"inuit","japanese_moon_stations","kamilaroi","korean","lokono","macedonian", "maori","maya","medieval_chinese","mongolian","mul_apin","navajo"};
        for(int x = 0; x < filenames2.length; x++) {
        	String datafile = filenames2[x] + ".fab";
        	filenames2[x].replace('_', ' ');
        	MenuItem opt = new MenuItem(upperCaseFL(filenames2[x]));
        	opt.setOnAction((event) -> { userSelectedConstellationFileName = datafile; });
        	cspm2.getItems().add(opt);
        }
        String[] filenames3 = {"norse","northern_andes","ojibwe","romanian","sami","sardinian","seleucid","siberian", "tongan","tukano","tupi"};
        for(int x = 0; x < filenames3.length; x++) {
        	String datafile = filenames3[x] + ".fab";
        	filenames3[x].replace('_', ' ');
        	MenuItem opt = new MenuItem(upperCaseFL(filenames3[x]));
        	opt.setOnAction((event) -> { userSelectedConstellationFileName = datafile; });
        	cspm3.getItems().add(opt);
        }
        mainmenu.getMenus().addAll(colorpickermenu,cspm,cspm2,cspm3);
		HBox box4 = new HBox(5);
		Button but = new Button("Cancel");
		but.setPadding(new Insets(5));
		Button but2 = new Button("Submit");
		but2.setPadding(new Insets(5));
		box4.getChildren().addAll(but,but2);
		uicontrols.getChildren().addAll(box0,box1,box2,box3,box5,box6,box4);
		but.setOnAction((event) -> { input.close(); });
		but2.setOnAction((event) -> {
			long i = validateInput();
			if (i == 0) {
				chartTheStars();
				input.close();
			} else {
				GUIerrorout = new Alert(AlertType.ERROR, getGUIErrorMsg(i));
				GUIerrorout.showAndWait();
			}
        });
		pane.setTop(mainmenu);
		pane.setCenter(uicontrols);
		pane.setPadding(new Insets(10));
		Scene scene = new Scene(pane, 520, 280);
		input.setScene(scene);
	}
	/**
	 * Returns a String representing an error message.
	 * @param errorcode The id of the error.
	 * @return The error message.
	 */
	private String getGUIErrorMsg(long errorcode) {
		if (errorcode == 0) {
			return "This popup shouldn't have launched!";
		} else if (errorcode == 9) {
			return "Latitude input format incorrect!\nValid Latitude values are -90 to 90 with negative values being south Latitude.";
		} else if (errorcode == 10) {
			return "Longitude input format incorrect!\nValid Longitude values are -180 to 180 with negative values being west latitude.";
		} else if (errorcode == 11) {
			return "Date input format incorrect!\nValid dates go <year>-<month>-<day>\nValid month values are 1- 12 and valid day values are 1 - 31.";
		} else if (errorcode == 12) {
			return "Time input format incorrect!\nValid times go <hour>-<minute>-<second>\nValid hour values are 0 - 23 and valid minute and second values are 0 - 59";
		} else if (errorcode == 1) {
			return "Invalid latitude!\nValid Latitude values are -90 to 90 with negative values being south Latitude.";
		} else if (errorcode == 2) {
			return "Invalid Longitude!\nValid Longitude values are -180 to 180 with negative values being west latitude.";
		} else if (errorcode == 3) {
			return "Invalid Year!\nValid year values are " + Integer.MIN_VALUE + " - " + Integer.MAX_VALUE + ".";
		} else if (errorcode == 4) {
			return "Invalid Month!\nValid month values are 1 - 12";
		} else if (errorcode == 5) {
			return "Invalid Day!\nValid day values are 1 - 31";
		} else if (errorcode == 6) {
			return "Invalid Hour!\nValid hour values are 0 - 23";
		} else if (errorcode == 7) {
			return "Invalid Minute!\nValid minute values are 0 - 59";
		} else if (errorcode == 8) {
			return "Invalid Second!\nValid second values are 0 - 59";
		} 
		return "Undefined Error Message!";
	}
	/**
	 * Takes a direction int, (1 - 4) and offset value. Moves the latitude and longitude.
	 * If dir is invalid, resets the latitude and longitiude to 0. 
	 * @param dir (1 - 4) indicates direction. 
	 * @param offset The amout to be moved latitude wise (longitude is moved 4x this). 
	 */
	private void adjustObserverLocationByClick(int dir, double offset) {
		HBox n0 = (HBox) uicontrols.getChildren().get(0);
		HBox n1 = (HBox) uicontrols.getChildren().get(1);
		TextField t0 = (TextField) n0.getChildren().get(1); // Lat
		TextField t1 = (TextField) n1.getChildren().get(1); // Long
		double latitude = 0.0;
		double longitude = 0.0;
		try {
			latitude = Double.parseDouble(t0.getText());
			longitude = Double.parseDouble(t1.getText());
		} catch(NumberFormatException e) {
		}
		if (dir == 1) { // NW
			latitude += offset;
			longitude -= (offset * 4);
			if (latitude > 89.99999) {
				latitude = 89.99999;
			}
			if (longitude < -180.0) {
				longitude += 180.0;
				longitude = 180.0 + longitude;
			}
		} else if (dir == 2) { // NE
			latitude += offset;
			longitude += (offset * 4);
			if (latitude > 89.99999) {
				latitude = 89.99999;
			}
			if (longitude > 180.0) {
				longitude -= 180.0;
				longitude = -180.0 + longitude;
			}
		} else if (dir == 3) { // SW
			latitude -= offset;
			longitude -= (offset * 4);
			if (latitude < -89.99999) {
				latitude = -89.99999;
			}
			if (longitude < -180.0) {
				longitude += 180.0;
				longitude = 180.0 + longitude;
			}
		} else if (dir == 4) { // SE
			latitude -= offset;
			longitude += (offset * 4);
			if (latitude < -89.99999) {
				latitude = -89.99999;
			}
			if (longitude > 180.0) {
				longitude -= 180.0;
				longitude = -180.0 + longitude;
			}
		}
		t0.setText("" + latitude);
		t1.setText("" + longitude);
	}
	/**
	 * Grabs info stored in the UI controls and translates to a series of doubles.
	 * @return An array of length 9 with the first 2 values being latitude and longitude
	 * The 3,4 and 5 being Year,Month,Day
	 * The 6,7 and 8 being Hour,Minute,Second
	 * 9 being an error code: 0 = NO ERROR
	 * 9 = Bad latitude, 10 = Bad longitude, 11 = bad date, 12 = bad time
	 */
	private double[] getUserInputFromUIControls() {
		HBox n0 = (HBox) uicontrols.getChildren().get(0);
		HBox n1 = (HBox) uicontrols.getChildren().get(1);
		HBox n2 = (HBox) uicontrols.getChildren().get(2);
		HBox n3 = (HBox) uicontrols.getChildren().get(3);
		TextField t0 = (TextField) n0.getChildren().get(1);
		TextField t1 = (TextField) n1.getChildren().get(1);
		TextField t2 = (TextField) n2.getChildren().get(1);
		TextField t3 = (TextField) n3.getChildren().get(1);
		double[] retval = new double[9];
		retval[8] = 9;
		try {
			retval[0] = Double.parseDouble(t0.getText());
			retval[8] = 10.0;
			retval[1] = Double.parseDouble(t1.getText());
		} catch(Exception e) {
			return retval;
		}
		String[] date = t2.getText().split("-");
		if (date.length != 3) {
			retval[8] = 11.0;
			return retval;
		}
		try {
			retval[2] = floor(Double.parseDouble(date[0]));
			retval[3] = floor(Double.parseDouble(date[1]));
			retval[4] = floor(Double.parseDouble(date[2]));
		} catch(Exception e) {
			retval[8] = 11.0;
			return retval;
		}
		String[] time = t3.getText().split(":");
		if (time.length != 3) {
			retval[8] = 12.0;
			return retval;
		}
		try {
			retval[5] = floor(Double.parseDouble(time[0]));
			retval[6] = floor(Double.parseDouble(time[1]));
			retval[7] = floor(Double.parseDouble(time[2]));
		} catch(Exception e) {
			retval[8] = 12.0;
			return retval;
		}
		retval[8] = 0.0;
		if (retval[0] == 90.0) {
			retval[0] = 89.9999999;
		} else if (retval[0] == -90.0) {
			retval[0] = -89.9999999;
		}
		return retval;
	}
	/**
	 * Fetches the status of the check boxes in the GUI.
	 * @return A boolean array indicating if CheckBoxes are ticked or not.
	 */
	private boolean[] getCheckBoxes() {
		HBox n4 = (HBox) uicontrols.getChildren().get(4);
		CheckBox b1 = (CheckBox) n4.getChildren().get(0);
		CheckBox b2 = (CheckBox) n4.getChildren().get(1);
		CheckBox b3 = (CheckBox) n4.getChildren().get(2);
		CheckBox b4 = (CheckBox) n4.getChildren().get(3);
		boolean[] boxes = { b1.isSelected(),b2.isSelected(),b3.isSelected(),b4.isSelected()};
		return boxes;
	}
	/**
	 * Validates input found in the main settings menu input feilds. 
	 * @return The error code. 0 is no error. 
	 */
	private long validateInput() {
		double[] inputs = getUserInputFromUIControls();
		if (inputs == null) {
			return 9;
		}
		if (inputs[8] != 0.0) {
			return (long) inputs[8];
		}
		if (inputs[0] > 90.0 || inputs[0] < -90.0) {
			return 1;
		}
		if (inputs[1] > 180.0 || inputs[1] < -180.0) {
			return 2;
		}
		// Year can be any number that can be fit within an integer.
		if (inputs[2] > Integer.MAX_VALUE || inputs[2] < Integer.MIN_VALUE) {
			return 3;
		}
		if (inputs[3] > 12.0 || inputs[3] < 1.0) {
			return 4;
		}
		if (inputs[4] > 31.0 || inputs[4] < 1.0) {
			return 5;
		}
		if (inputs[5] > 23.0 || inputs[5] < 0.0) {
			return 6;
		}
		if (inputs[6] > 59.0 || inputs[6] < 0.0) {
			return 7;
		}
		if (inputs[7] > 59.0 || inputs[7] < 0.0) {
			return 8;
		}
		return 0;
	}
	/**
	 * Determines where to plot space objects on the graph. Returns null if fails. 
	 * @param obj A space object with defined azimuth and altitude fields.
	 * @return A set coordinates for the graph.
	 */
	private double[] getPositionOfSpaceObject(CTS_SpaceObject obj) {
		if (obj == null) {
			//System.out.println("null obj passed to getPositionOfSpaceObject!");
			return null;
		}
		double azimuth = obj.getAzimuth();
		double altitude = obj.getAltitude();
		if (altitude < 0 || altitude > 90) {
			return null;
		}
		double radAlt = toRadians(altitude);
		double r = VIEWING_AREA_WIDTH / 2;
		double x_val, y_val, distanceFromCenter;
		distanceFromCenter = abs(r*cos(radAlt));
		// x_val, y_val are coords relative to the center of the viewing area
		// being considered 0,0 on the Cartesian plane
		x_val = distanceFromCenter*(sin(toRadians(azimuth)));
		y_val = distanceFromCenter*(cos(toRadians(azimuth)));
		// view_x, view_y are the actual JavaFX coordinates to draw at
		double view_x = 0, view_y = 0;
		if (azimuth < 90) {
			view_x = x_val + 299;
			view_y = max(0,(299-y_val));
		} else if (azimuth < 180) {
			view_x = x_val + 299;
			view_y = min(599,(299-y_val));
		} else if (azimuth < 270) {
			view_x = max(0,(299+x_val));
			view_y = min(599,(299-y_val));
		} else {
			view_x = max(0,(299+x_val));
			view_y = max(0,(299-y_val));
		}
		double[] retval = { view_x, view_y};
		return retval;
	}
	/**
	 * Resets the state of the graphics context for redrawing.
	 */
	private void resetSkyDrawing() {
		gc = canvas.getGraphicsContext2D();
		gc.setFill(usercolors[6]);
		gc.fillRect(0, 0,  VIEWING_AREA_WIDTH, VIEWING_AREA_HEIGHT);
		drawCircle(VIEWING_AREA_WIDTH / 2, VIEWING_AREA_WIDTH / 2, VIEWING_AREA_HEIGHT / 2, usercolors[4]);
		drawCircleOutline(VIEWING_AREA_WIDTH / 2, VIEWING_AREA_HEIGHT / 2 ,VIEWING_AREA_WIDTH / 2, usercolors[5]);
	}
	/**
	 * Converts an objects magnitude to a radius to be displayed at
	 * @param mag The magnitude of the object.
	 * @return The radius it will apear as on screen.
	 */
	private double magnitudeToRadius(double mag) {
		if (mag > 6.0) {
			return 0.25;
		} else if (mag > 5.0) {
			return 0.3;
		} else if (mag > 4.0) {
			return 0.4;
		} else if (mag > 3.0) {
			return 0.55;
		} else if (mag > 2.0) {
			return 0.7;
		} else if (mag > 1.0) {
			return 0.9;
		} else if (mag > 0.5) {
			return 1.0;
		} else if (mag > .5) {
			return 1.20;
		} else if (mag < -2.0) {
			return 5.0;
		}
		return 1.75;
	}
	/**
	 * Determines the distance from the given x,y values to the center of screen.
	 * @param x The x postion of the start point.
	 * @param y The y postion of the start point.
	 * @return The distance between the x,y pair and the x,y pair at the center of screen.
	 */
	private double distanceToCenterOfDisplay(double x, double y) {
		// A^2 + B^2 = C^2
		double centerx = VIEWING_AREA_WIDTH / 2;
		double centery = VIEWING_AREA_HEIGHT / 2;
		double diffx = abs(centerx - x);
		double diffy = abs(centery - y);
		diffx *= diffx;
		diffy *= diffy;
		return sqrt(diffx + diffy);
	}
	/**
	 * Handles the fetching and processing of the color setter.
	 */
	private void colorPickerHander() {
		boolean validvalues = true;
		HBox n0000 = (HBox) uicontrols.getChildren().get(5);
		TextField t0000 = (TextField) n0000.getChildren().get(1);
		Label l0000 = (Label) n0000.getChildren().get(3);
		String txt = t0000.getText();
		String[] nums = txt.split(",");
		if (nums.length != 4) {
			validvalues = false;
			GUIerrorout = new Alert(AlertType.ERROR, "Invalid Color Format!\nFormat: <0-255>,<0-255>,<0-255>,<0-1> \n (red),(green),(blue),(alpha)");
			GUIerrorout.showAndWait();
		}
		if (validvalues) {
			int[] colorvalues = new int[3];
			double alphavalue = 1.0;
			try {
				for(int x = 0; x < 3; x++) {
					colorvalues[x] = Integer.parseInt(nums[x]);
				}
				alphavalue = Double.parseDouble(nums[3]);
			} catch(Exception e) {
				validvalues = false;
				GUIerrorout = new Alert(AlertType.ERROR, "Invalid Color Format!\nFormat: <0-255>,<0-255>,<0-255>,<0-1> \n (red),(green),(blue),(alpha)");
				GUIerrorout.showAndWait();
			}
			if (alphavalue <= 1.0 && alphavalue >= 0.0 && colorvalues[0] >= 0 && colorvalues[0] < 256 && colorvalues[1] >= 0 && colorvalues[1] < 256 && colorvalues[2] >= 0 && colorvalues[2] < 256){
				if (validvalues) {
					l0000.setTextFill(Color.rgb(colorvalues[0],colorvalues[1],colorvalues[2],alphavalue));
					usercolors[colorSetterId] = Color.rgb(colorvalues[0],colorvalues[1],colorvalues[2],alphavalue);
				}
			} else {
				GUIerrorout = new Alert(AlertType.ERROR, "Invalid Color Format!\nFormat: <0-255>,<0-255>,<0-255>,<0-1> \n (red),(green),(blue),(alpha)");
				GUIerrorout.showAndWait();
			}
		}
	}
	/**
	 * Takes a string, and sets its first letter to upper case.
	 * @param in A string of length 1 or higher. Does not error check.
	 * @return The string with its first letter capitalized.
	 */
	private String upperCaseFL(String in) {
		return in.substring(0,1).toUpperCase() + in.substring(1,in.length());
	}
}
