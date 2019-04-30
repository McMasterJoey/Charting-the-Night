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
        			//System.out.println("upper left");
        			adjustObserverLocationByClick(1,10);
        		} else if (x < 0 && y > 0) {
        			//System.out.println("lower left");
        			adjustObserverLocationByClick(3,10);
        		} else if (x > 0 && y < 0) {
        			//System.out.println("upper right");
        			adjustObserverLocationByClick(2,10);
        		} else if (x > 0 && y > 0) {
        			//System.out.println("lower right");
        			adjustObserverLocationByClick(4,10);
        		}
        		chartTheStars();
        	} else {
        		input.showAndWait();
        	}
        });
        		
		// Display it!
        
        //privateTests();
        
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
		controller = new CTS_Controller(data[0],data[1],(int) data[2], (int) data[3],(int) data[4],(int) data[5], (int) data[6], (int) data[7]); // Only using latitude and longitude
		boolean succesful = controller.setConstellationType(userSelectedConstellationFileName);
		// strokeText(String text, double x, double y, double maxWidth)
		gc.setStroke(usercolors[7]);
		gc.strokeText("Latitude: " + data[0], 10, 10, 190);
		gc.strokeText("Longitude: " + data[1], 10, 25, 190);
		ArrayList<CTS_Star> n = controller.getModel().getStarList();
		ArrayList<CTS_DeepSkyObject> d = controller.getModel().getDSOlist();
		double azi = 0, alt = 0, mag = 0;
		boolean[] plottingstatus = getCheckBoxes();
		if (plottingstatus[0]) {
			// Plot stars
			for(int x = 0; x < n.size(); x++) {
				CTS_Star star = n.get(x);
				try {
					alt = star.getAltitude();
					azi = star.getAzimuth();
					mag = star.getMagnitude();
	                if (alt >= 0 && mag < -20) {
	                	// The sun is the only star with mag < -20
	                	// Make it yellow
	                    double cupcake = magnitudeToRadius(mag);
	                    drawSpaceObject(star, cupcake,Color.YELLOW);
	                }
	                else if (alt >= 0 && mag < 6) {
						// I named it cupcake because when I first tested it
						// The result in the GUI was a cupcake!
						double cupcake = magnitudeToRadius(mag);
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
					//System.err.println("An object that attempted to be drawn triggered an execption");
				}
			}
		}
		if (plottingstatus[1]) {
			// Plot DSOs
			for(int x = 0; x < d.size(); x++) {
				CTS_DeepSkyObject dso = d.get(x);
				try {
					alt = dso.getAltitude();
					azi = dso.getAzimuth();
					mag = dso.getMagnitude();
					if (alt >= 0 && mag < 6) {
						drawSpaceObject(dso,1,usercolors[3]);
					}
				} catch(IllegalArgumentException e) {
					//System.err.println("An object that attempted to be drawn triggered an execption");
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
							//System.out.println(from[0] + " " + from[1] + " " + to[0] + " " + to[1]);
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
			// TODO Plot planets
			for (CTS_Planet x : controller.getModel().getPlanetList()) {
				drawSpaceObject(x , 4 , Color.PINK);
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
	public void drawCircleOutline(double radius, double x, double y, Color color) {
		double xx = x - radius;
		double yy = y - radius;
		if (xx < 0.0 || yy < 0.0 || xx > (VIEWING_AREA_WIDTH -  radius)|| yy > (VIEWING_AREA_HEIGHT - radius)) {
			throw new IllegalArgumentException("drawCircle: invalid set of radius and x/y cordnates");
		}
		gc.setStroke(color);
		gc.strokeOval(xx,yy, radius * 2, radius * 2);
	}
	public void drawLine(double startx, double starty, double endx, double endy, Color color) {
		gc.setStroke(color);
		gc.strokeLine(startx,starty,endx,endy);
	}
	/**
	 * Wrapper function to draw space objects.
	 * @param obj The space object to be drawn.
	 * @param radius Its radius
	 * @param color Its color
	 * @throws IllegalArgumentException When something that is inputed causes an issue with displaying.
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
	 * @throws IllegalArgumentException When something that is inputed causes an issue with displaying.
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
		TextField lat = new TextField("0");
		box0.getChildren().add(new Label("Latitude: "));
		box0.getChildren().add(lat);
		
		TextField lon = new TextField("0");
		HBox box1 = new HBox(5);
		box1.getChildren().add(new Label("Longitude: "));
		box1.getChildren().add(lon);
		
		HBox box2 = new HBox(5);
		TextField date = new TextField(d.toString());
		box2.getChildren().add(new Label("Date: "));
		box2.getChildren().add(date);
		TextField time = new TextField(t.toString());
		HBox box3 = new HBox(5);
		box3.getChildren().add(new Label("Time: "));
		box3.getChildren().add(time);
		
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
		box5.getChildren().add(c1);
		box5.getChildren().add(c2);
		box5.getChildren().add(c3);
		box5.getChildren().add(c4);
		// Color Picker
		// 0 = Star color, 1 = Low mag star, 2 = Very low mag star.
		// 3 = DSO, 4 = Sky background, 5 = Circle around skybackgrund
		// 6 = Overall background, 7 = Lat/long txt color, 8 = Constelation line color.
		HBox box6 = new HBox(5);
		TextField colorSet = new TextField("255,255,255,1");
		Button setcolorbutton = new Button("Submit Color");
		box6.getChildren().add(new Label("Color to Set: "));
		box6.getChildren().add(colorSet);
		box6.getChildren().add(setcolorbutton);
		box6.getChildren().add(new Label("[PREVIEW COLOR]"));
		setcolorbutton.setOnAction((event) -> {
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
        });
        MenuBar mainmenu = new MenuBar();
        Menu colorpickermenu = new Menu("Set Custom Colors");
        MenuItem opt0 = new MenuItem("Star Color");
        opt0.setOnAction((event) -> {
        	colorSetterId = 0;
        });
        MenuItem opt1 = new MenuItem("Low Magnituide Star Color");
        opt1.setOnAction((event) -> {
        	colorSetterId = 1;
        });
        MenuItem opt2 = new MenuItem("Very Low Magnituide Star Color");
        opt2.setOnAction((event) -> {
        	colorSetterId = 2;
        });
        MenuItem opt3 = new MenuItem("Deep Space Object Color");
        opt3.setOnAction((event) -> {
        	colorSetterId = 3;
        });
        MenuItem opt4 = new MenuItem("Sky Background Color");
        opt4.setOnAction((event) -> {
        	colorSetterId = 4;
        });
        MenuItem opt5 = new MenuItem("Circle Around Sky Background Color");
        opt5.setOnAction((event) -> {
        	colorSetterId = 5;
        });
        MenuItem opt6 = new MenuItem("Overall Background Color");
        opt6.setOnAction((event) -> {
        	colorSetterId = 6;
        });
        MenuItem opt7 = new MenuItem("Latitude/Longitude Text Color");
        opt7.setOnAction((event) -> {
        	colorSetterId = 7;
        });
        MenuItem opt8 = new MenuItem("Constellation Line Color");
        opt8.setOnAction((event) -> {
        	colorSetterId = 8;
        });
        // Add the button the menu
        colorpickermenu.getItems().add(opt0);
        colorpickermenu.getItems().add(opt1);
        colorpickermenu.getItems().add(opt2);
        colorpickermenu.getItems().add(opt3);
        colorpickermenu.getItems().add(opt4);
        colorpickermenu.getItems().add(opt5);
        colorpickermenu.getItems().add(opt6);
        colorpickermenu.getItems().add(opt7);
        colorpickermenu.getItems().add(opt8);
        mainmenu.getMenus().add(colorpickermenu);
        
        Menu constellationsetpickermenu = new Menu("Constellation Set 1");
        MenuItem _opt0 = new MenuItem("Western");
        _opt0.setOnAction((event) -> {
        	userSelectedConstellationFileName = "western.fab";
        });
        MenuItem _opt1 = new MenuItem("Arabic");
        _opt1.setOnAction((event) -> {
        	userSelectedConstellationFileName = "arabic.fab";
        });
        MenuItem _opt2 = new MenuItem("Armintxe");
        _opt2.setOnAction((event) -> {
        	userSelectedConstellationFileName = "armintxe.fab";
        });
        MenuItem _opt3 = new MenuItem("Aztec");
        _opt3.setOnAction((event) -> {
        	userSelectedConstellationFileName = "aztec.fab";
        });
        MenuItem _opt4 = new MenuItem("Belarusian");
        _opt4.setOnAction((event) -> {
        	userSelectedConstellationFileName = "belarusian.fab";
        });
        MenuItem _opt5 = new MenuItem("Boorong");
        _opt5.setOnAction((event) -> {
        	userSelectedConstellationFileName = "boorong.fab";
        });
        MenuItem _opt6 = new MenuItem("Chinese");
        _opt6.setOnAction((event) -> {
        	userSelectedConstellationFileName = "chinese.fab";
        });
        MenuItem _opt7 = new MenuItem("Contemporary Chinese");
        _opt7.setOnAction((event) -> {
        	userSelectedConstellationFileName = "contemporary_chinese.fab";
        });
        MenuItem _opt8 = new MenuItem("Dakota");
        _opt8.setOnAction((event) -> {
        	userSelectedConstellationFileName = "dakota.fab";
        });
        MenuItem _opt9 = new MenuItem("Egyptian");
        _opt9.setOnAction((event) -> {
        	userSelectedConstellationFileName = "egyptian.fab";
        });
        MenuItem _opt10 = new MenuItem("Hawaiian");
        _opt10.setOnAction((event) -> {
        	userSelectedConstellationFileName = "hawaiian_starlines.fab";
        });
        MenuItem _opt11 = new MenuItem("Indian");
        _opt11.setOnAction((event) -> {
        	userSelectedConstellationFileName = "indian.fab";
        });
        MenuItem _opt12 = new MenuItem("Inuit");
        _opt12.setOnAction((event) -> {
        	userSelectedConstellationFileName = "inuit.fab";
        });
        MenuItem _opt13 = new MenuItem("Japanese");
        _opt13.setOnAction((event) -> {
        	userSelectedConstellationFileName = "japanese_moon_stations.fab";
        });
        MenuItem _opt14 = new MenuItem("Kamilaroi");
        _opt14.setOnAction((event) -> {
        	userSelectedConstellationFileName = "kamilaroi.fab";
        });
        
        constellationsetpickermenu.getItems().add(_opt0);
        constellationsetpickermenu.getItems().add(_opt1);
        constellationsetpickermenu.getItems().add(_opt2);
        constellationsetpickermenu.getItems().add(_opt3);
        constellationsetpickermenu.getItems().add(_opt4);
        constellationsetpickermenu.getItems().add(_opt5);
        constellationsetpickermenu.getItems().add(_opt6);
        constellationsetpickermenu.getItems().add(_opt7);
        constellationsetpickermenu.getItems().add(_opt8);
        constellationsetpickermenu.getItems().add(_opt9);
        constellationsetpickermenu.getItems().add(_opt10);
        constellationsetpickermenu.getItems().add(_opt11);
        constellationsetpickermenu.getItems().add(_opt12);
        constellationsetpickermenu.getItems().add(_opt13);
        constellationsetpickermenu.getItems().add(_opt14);
        mainmenu.getMenus().add(constellationsetpickermenu);
        
        Menu constellationsetpickermenu2 = new Menu("Constellation Set 2");
        MenuItem __opt0 = new MenuItem("Korean");
        __opt0.setOnAction((event) -> {
        	userSelectedConstellationFileName = "korean.fab";
        });
        MenuItem __opt1 = new MenuItem("Lokono");
        __opt1.setOnAction((event) -> {
        	userSelectedConstellationFileName = "lokono.fab";
        });
        MenuItem __opt2 = new MenuItem("Macedonian");
        __opt2.setOnAction((event) -> {
        	userSelectedConstellationFileName = "macedonian.fab";
        });
        MenuItem __opt3 = new MenuItem("Maori");
        __opt3.setOnAction((event) -> {
        	userSelectedConstellationFileName = "maori.fab";
        });
        MenuItem __opt4 = new MenuItem("Maya");
        __opt4.setOnAction((event) -> {
        	userSelectedConstellationFileName = "maya.fab";
        });
        MenuItem __opt5 = new MenuItem("Medieval Chinese");
        __opt5.setOnAction((event) -> {
        	userSelectedConstellationFileName = "medieval_chinese.fab";
        });
        MenuItem __opt6 = new MenuItem("Mongolian");
        __opt6.setOnAction((event) -> {
        	userSelectedConstellationFileName = "mongolian.fab";
        });
        MenuItem __opt7 = new MenuItem("Mul Apin");
        __opt7.setOnAction((event) -> {
        	userSelectedConstellationFileName = "mul_apin.fab";
        });
        MenuItem __opt8 = new MenuItem("Navajo");
        __opt8.setOnAction((event) -> {
        	userSelectedConstellationFileName = "navajo.fab";
        });
        MenuItem __opt9 = new MenuItem("Norse");
        __opt9.setOnAction((event) -> {
        	userSelectedConstellationFileName = "norse.fab";
        });
        MenuItem __opt10 = new MenuItem("Northern Andes");
        __opt10.setOnAction((event) -> {
        	userSelectedConstellationFileName = "northern_andes.fab";
        });
        MenuItem __opt11 = new MenuItem("Ojibwe");
        __opt11.setOnAction((event) -> {
        	userSelectedConstellationFileName = "ojibwe.fab";
        });
        MenuItem __opt12 = new MenuItem("Romanian");
        __opt12.setOnAction((event) -> {
        	userSelectedConstellationFileName = "romanian.fab";
        });
        MenuItem __opt13 = new MenuItem("Sami");
        __opt13.setOnAction((event) -> {
        	userSelectedConstellationFileName = "sami.fab";
        });
        MenuItem __opt14 = new MenuItem("Sardinian");
        __opt14.setOnAction((event) -> {
        	userSelectedConstellationFileName = "sardinian.fab";
        });
        constellationsetpickermenu2.getItems().add(__opt0);
        constellationsetpickermenu2.getItems().add(__opt1);
        constellationsetpickermenu2.getItems().add(__opt2);
        constellationsetpickermenu2.getItems().add(__opt3);
        constellationsetpickermenu2.getItems().add(__opt4);
        constellationsetpickermenu2.getItems().add(__opt5);
        constellationsetpickermenu2.getItems().add(__opt6);
        constellationsetpickermenu2.getItems().add(__opt7);
        constellationsetpickermenu2.getItems().add(__opt8);
        constellationsetpickermenu2.getItems().add(__opt9);
        constellationsetpickermenu2.getItems().add(__opt10);
        constellationsetpickermenu2.getItems().add(__opt11);
        constellationsetpickermenu2.getItems().add(__opt12);
        constellationsetpickermenu2.getItems().add(__opt13);
        constellationsetpickermenu2.getItems().add(__opt14);
        mainmenu.getMenus().add(constellationsetpickermenu2);
        
        Menu constellationsetpickermenu3 = new Menu("Constellation Set 3");
        MenuItem ___opt0 = new MenuItem("Seleucid");
        ___opt0.setOnAction((event) -> {
        	userSelectedConstellationFileName = "seleucid.fab";
        });
        MenuItem ___opt1 = new MenuItem("Siberian");
        ___opt1.setOnAction((event) -> {
        	userSelectedConstellationFileName = "siberian.fab";
        });
        MenuItem ___opt2 = new MenuItem("Tongan");
        ___opt2.setOnAction((event) -> {
        	userSelectedConstellationFileName = "tongan.fab";
        });
        MenuItem ___opt3 = new MenuItem("Tukano");
        ___opt3.setOnAction((event) -> {
        	userSelectedConstellationFileName = "tukano.fab";
        });
        MenuItem ___opt4 = new MenuItem("Tupi");
        ___opt4.setOnAction((event) -> {
        	userSelectedConstellationFileName = "tupi.fab";
        });
        constellationsetpickermenu3.getItems().add(___opt0);
        constellationsetpickermenu3.getItems().add(___opt1);
        constellationsetpickermenu3.getItems().add(___opt2);
        constellationsetpickermenu3.getItems().add(___opt3);
        constellationsetpickermenu3.getItems().add(___opt4);
        mainmenu.getMenus().add(constellationsetpickermenu3);
		HBox box4 = new HBox(5);
		Button but = new Button("Cancel");
		but.setPadding(new Insets(5));
		Button but2 = new Button("Submit");
		but2.setPadding(new Insets(5));
		box4.getChildren().add(but);
		box4.getChildren().add(but2);
		uicontrols.getChildren().add(box0);
		uicontrols.getChildren().add(box1);
		uicontrols.getChildren().add(box2);
		uicontrols.getChildren().add(box3);
		uicontrols.getChildren().add(box5);
		uicontrols.getChildren().add(box6);
		uicontrols.getChildren().add(box4);
		
		but.setOnAction((event) -> {
        	input.close();
        });
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
			return "Time input format incorrect!\nValid times go <hour>-<miniute>-<second>\nValid hour values are 0 - 23 and valid miniute and second values are 0 - 59";
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
	private void adjustObserverLocationByClick(int dir, double offset) {
		HBox n0 = (HBox) uicontrols.getChildren().get(0);
		HBox n1 = (HBox) uicontrols.getChildren().get(1);
		TextField t0 = (TextField) n0.getChildren().get(1); // Lat
		TextField t1 = (TextField) n1.getChildren().get(1); // Long
		double latitude = Double.parseDouble(t0.getText());
		double longitude = Double.parseDouble(t1.getText());
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
		return retval;
	}
	/**
	 * Fetches the status of the check boxes in the GUI.
	 * @return
	 */
	private boolean[] getCheckBoxes() {
		HBox n4 = (HBox) uicontrols.getChildren().get(4);
		CheckBox b1 = (CheckBox) n4.getChildren().get(0);
		CheckBox b2 = (CheckBox) n4.getChildren().get(1);
		CheckBox b3 = (CheckBox) n4.getChildren().get(2);
		CheckBox b4 = (CheckBox) n4.getChildren().get(3);
		boolean[] boxes = new boolean[4];
		boxes[0] = b1.isSelected();
		boxes[1] = b2.isSelected();
		boxes[2] = b3.isSelected();
		boxes[3] = b4.isSelected();
		return boxes;
	}
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
	 * Determines where to plot space objects on the graph
	 * @param obj A space object with defined azimuth and alititude feilds.
	 * @return A set coordinates for the graph.
	 */
	private double[] getPositionOfSpaceObject(CTS_SpaceObject obj) {
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
	
	/** Matt testing function to print relevant info for online calculator
	 * 
	 */
	private void mattTest(CTS_SpaceObject obj, double lati, double longi) {
		System.out.println(obj);
		double dec = obj.getDeclination();
		double ra = obj.getRightAscension();
		
		
		int decdeg = (int) dec;
		
		if (dec > 1) {
			while (dec>1) {
				dec--;
			}
		} else {
			while (dec<-1) {
				dec++;
			}
		}
		double decmin = Math.abs(dec*60);
		
	
		double rahour = ra/15;

		int rah = (int) rahour;

		
		
		double ramin = Math.abs((rahour-rah)*60);
		
		System.out.print("DEC: " + decdeg +"deg, "+decmin+" min,   RA: " +rah+" hours, "+ ramin + " mins...\n\n");
		
		
	}
	
	/**
	 * Joeys speific testing function to test, inprogress
	 * Varrious draw functions. 
	 */
	private void privateTests() {
		// Test lines
		//drawLine(5,20,20,580,580,Color.RED);
		//drawLine(5,20,580,580,20,Color.RED);
		//drawLine(8,250,100,350,10,Color.BLUE);
		//drawLine(1,1,1,4,500,Color.GREEN);
		CTS_Star n1 = new CTS_Star(0, "Test", 0, 0, 0,90,50);
		
		CTS_Star n5 = new CTS_Star(0, "Test", 0, 0, 0,10,0);
		CTS_Star n = new CTS_Star(0, "Test", 0, 0, 0,10,90);
		CTS_Star n6 = new CTS_Star(0, "Test", 0, 0, 0,10,180);
		CTS_Star n2 = new CTS_Star(0, "Test", 0, 0, 0,10,270);
		CTS_Star n3 = new CTS_Star(0, "Test", 0, 0, 0,45,300);
		CTS_Star n4 = new CTS_Star(0, "Test", 0, 0, 0,45,10); //Incorrect
		
		// CTS_Star(int Id, String name, double magnitude, double rightAcension, double declination, 
		// double altitude, double azimuth)
		drawSpaceObject(n, 2, Color.RED);
		drawSpaceObject(n1,2,Color.YELLOW);
		drawSpaceObject(n2,2,Color.BLUE);
		drawSpaceObject(n3,10,Color.GREEN);
		drawSpaceObject(n4,20,Color.PINK);
		drawSpaceObject(n5,2,Color.ORANGE);
		drawSpaceObject(n6,2,Color.AQUA);
	}
	private void resetSkyDrawing() {
		gc = canvas.getGraphicsContext2D();
		gc.setFill(usercolors[6]);
		gc.fillRect(0, 0,  VIEWING_AREA_WIDTH, VIEWING_AREA_HEIGHT);
		drawCircle(VIEWING_AREA_WIDTH / 2, VIEWING_AREA_WIDTH / 2, VIEWING_AREA_HEIGHT / 2, usercolors[4]);
		drawCircleOutline(VIEWING_AREA_WIDTH / 2, VIEWING_AREA_HEIGHT / 2 ,VIEWING_AREA_WIDTH / 2, usercolors[5]);
	}
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
}
