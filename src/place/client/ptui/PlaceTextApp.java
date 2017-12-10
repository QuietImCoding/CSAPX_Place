package place.client.ptui;

import place.PlaceColor;
import place.PlaceException;
import place.client.PlaceClient;
import place.client.PlaceClientModel;

import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

public class PlaceTextApp implements Observer, PlaceClient {

    private final Scanner userIn;
    private final PlaceClientModel model;

    public PlaceTextApp(String host, int port, String username) {
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

    private boolean sendMove(int row, int col, int color) throws PlaceException{
        model.sendTileChange(row, col, PlaceColor.values()[color]);
        return true;
    }

    @Override
    public void update(Observable o, Object arg) {

    }

    @Override
    public void display() {
        System.out.println(model.getBoard());
    }

    @Override
    public boolean getUserInput() {
        System.out.print("What would you like to do? (view / move) ");
        String input = userIn.nextLine();
        String[] tokens = input.split("\\s");
        if (tokens[0].equals("view")) {
            display();
            System.out.println();
            return true;
        } else if (tokens[0].equals("move")) {
            try {
                return sendMove(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]));
            } catch (PlaceException pe) {
                System.out.println(pe.getMessage());
            }
        }
        return false;
    }
}
