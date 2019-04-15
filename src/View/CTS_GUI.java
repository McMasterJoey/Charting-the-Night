package View;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Place holder GUI class for the project
 * Please edit once we do something meaningful with it.
 * @author Joey McMaster
 * @author Nicholas Fiegel
 * @author Matt Theisen
 * @author Jackson
 *
 */
public class CTS_GUI extends Application {

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
		System.out.println("Display!");
		Scene scene = new Scene(mainpane, 600,600);
        stage.setScene(scene);
        stage.show();
	}

}
