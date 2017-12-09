package place.client.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import place.PlaceColor;
import place.PlaceException;
import place.client.PlaceClient;
import place.client.PlaceClientModel;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class PlaceGUI extends Application implements Observer, PlaceClient {

    private PlaceClientModel model;

    public PlaceGUI() {
    }

    @Override
    public void init() {
        List<String> args = getParameters().getRaw();
        String host = args.get(0);
        int port = Integer.parseInt(args.get(1));
        String username = args.get(2);
        model = new PlaceClientModel(host, port, username);
        model.addObserver(this);
        try {
            model.login();
        } catch (PlaceException pe) {
            System.out.println("Unable to login with username: " + username + " at " + host + ":" + port);
        }
        model.talkToServer();
    }

    @Override
    public void update(Observable o, Object arg) {

    }

    @Override
    public void display() {

    }

    @Override
    public boolean getUserInput() {
        return true;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane border = new BorderPane();

        Label gameStatus = new Label("Game is on");
        HBox middle = new HBox();
        middle.getChildren().add(gameStatus);
        border.setCenter(middle);

        primaryStage.setTitle("heck");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(border));
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
