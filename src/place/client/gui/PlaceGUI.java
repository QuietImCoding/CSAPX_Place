package place.client.gui;

import place.client.PlaceClient;

import java.util.Observable;
import java.util.Observer;

public class PlaceGUI implements Observer, PlaceClient {

    public PlaceGUI(String host, int port, String username) {
        System.out.println("Oh hai");
    }
    @Override
    public void update(Observable o, Object arg) {

    }

    @Override
    public void display() {

    }

    @Override
    public boolean getUserInput(String prompt) {
        return true;
    }
}
