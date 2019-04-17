package View;

import Controller.CTS_Controller;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

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
	private HBox uicontrols;
	//private CTS_Controller _controller;
	public CTS_GUI(String[] args) {
		launch(args);
	}
	public CTS_GUI() {
	}
	@Override
	public void start(Stage stage) throws Exception {
		stage.setTitle("Charting The Stars");
		stage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
		BorderPane mainpane = new BorderPane();
		Canvas canvas = new Canvas(VIEWING_AREA_WIDTH, VIEWING_AREA_HEIGHT);
		mainpane.setCenter(canvas);
		gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.LIGHTGREY);
		gc.fillRect(0, 0,  VIEWING_AREA_WIDTH, VIEWING_AREA_HEIGHT);
		drawCircle(VIEWING_AREA_WIDTH / 2, VIEWING_AREA_WIDTH / 2, VIEWING_AREA_HEIGHT / 2, Color.BLACK);
		// Set up Buttons
		uicontrols = new HBox();
		TextField timetext = new TextField("0:00");
		TextField location = new TextField("Tuscon Arizona");
		Button b = new Button("Update");
		b.setOnAction((event) -> {
			chartTheStars();
        });
		uicontrols.getChildren().add(new Label("Time"));
		uicontrols.getChildren().add(timetext);
		uicontrols.getChildren().add(new Label("Location"));
		uicontrols.getChildren().add(location);
		uicontrols.getChildren().add(b);
        mainpane.setTop(uicontrols);
		// Display it!
        
        //privateTests();
        
		Scene scene = new Scene(mainpane, VIEWING_AREA_WIDTH , VIEWING_AREA_HEIGHT + 30);
        stage.setScene(scene);
        stage.show();
	}
	/**
	 * Updates the GUI with the star chart of the inputed time and date.
	 */
	public void chartTheStars() {
		TextField time = (TextField) uicontrols.getChildren().get(1);
		TextField location = (TextField) uicontrols.getChildren().get(3);
		System.out.println(time.getText());
		System.out.println(location.getText());
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
	 * Draws a line of inputed thickness between 2 points.
	 * @param thickness The thickness of the line in pixels
	 * @param startx The starting x coordinate of the line.
	 * @param starty The starting y coordinate of the line.
	 * @param endx The ending x coordinate of the line.
	 * @param endy The ending y coordinate of the line.
	 * @param color The color of the line.
	 * @throws IllegalArgumentException If the line can not be fully displayed on the graphics context or if the thickness is less than 1.
	 */
	public void drawLine(int thickness, int startx, int starty, int endx, int endy, Color color) {
		if (thickness < 1) {
			throw new IllegalArgumentException("drawLine: thickness must be 1 or greater");
		}
		double offset = thickness / 2.0;
		//System.out.println(offset);
		double[] xcords = new double[4];
		double[] ycords = new double[4];
		boolean skiprest = false;
		if (startx - endx == 0) {
			// Case of verticle line.
			skiprest = true;
			xcords[0] = startx + offset;
			xcords[1] = startx - offset;
			xcords[2] = startx - offset;
			xcords[3] = startx + offset;
			ycords[0] = starty;
			ycords[1] = starty;
			ycords[2] = endy;
			ycords[3] = endy;
		}
		double changey = endy - starty;
		double changex = endx - startx;
		double slope = changey / changex;
		if (slope == 0.0 && !skiprest) {
			// Case of horitontal line
			skiprest = true;
			xcords[0] = startx;
			xcords[1] = startx;
			xcords[2] = endx;
			xcords[3] = endx;
			ycords[0] = starty + offset;
			ycords[1] = starty - offset;
			ycords[2] = starty - offset;
			ycords[3] = starty + offset;
			//System.out.println("Case 2!");
		}
		if (!skiprest) {
			double invslope = (1 / slope) * -1;
			ycords[0] = starty + (invslope * offset);
			ycords[1] = starty - (invslope * offset);
			ycords[2] = endy - (invslope * offset);
			ycords[3] = endy + (invslope * offset);
			
			xcords[0] = startx + offset;
			xcords[1] = startx - offset;
			xcords[2] = endx - offset;
			xcords[3] = endx + offset;
			
		}
		// Check for errors
		for(int x = 0; x < 4; x++) {
			if (ycords[x] < 0.0 || ycords[x] > VIEWING_AREA_HEIGHT) {
				throw new IllegalArgumentException("drawLine: Line goes off screen.");
			}
			if (xcords[x] < 0.0 || xcords[x] > VIEWING_AREA_WIDTH) {
				throw new IllegalArgumentException("drawLine: Line goes off screen.");
			}
		}
		gc.setFill(color);
		gc.fillPolygon(xcords, ycords, 4);
	}
	/**
	 * Joeys speific testing function to test, inprogress
	 * Varrious draw functions. 
	 */
	private void privateTests() {
		// Test lines
		drawLine(5,20,20,580,580,Color.RED);
		drawLine(5,20,580,580,20,Color.RED);
		drawLine(8,250,100,350,10,Color.BLUE);
		drawLine(1,1,1,4,500,Color.GREEN);
	}
}
