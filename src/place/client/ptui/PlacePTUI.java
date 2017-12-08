package place.client.ptui;

import place.PlaceException;
import place.client.PlaceClient;
import place.client.PlaceClientModel;

import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

public class PlacePTUI implements Observer, PlaceClient {

    Scanner userIn;
    private PlaceClientModel model;

    public PlacePTUI(String host, int port, String username) {
       model = new PlaceClientModel(host, port, username);
       model.addObserver(this);
       userIn = new Scanner(System.in);
        try {
            model.login();
        } catch (PlaceException pe) {
            System.out.println("Unable to login with username: " + username + " at " + host + ":" + port);
        }
        model.talkToServer();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof PlaceClientModel) {
            display();
        }
    }

    @Override
    public void display() {
        System.out.println(model.getBoard());
    }

    @Override
    public void getUserInput(String prompt) {
        System.out.print(prompt);
        String input = userIn.nextLine();
    }
}
