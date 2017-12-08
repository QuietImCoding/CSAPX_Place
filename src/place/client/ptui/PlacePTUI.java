package place.client.ptui;

import place.PlaceColor;
import place.PlaceException;
import place.client.PlaceClient;
import place.client.PlaceClientModel;

import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

public class PlacePTUI implements Observer, PlaceClient {

    private final Scanner userIn;
    private final PlaceClientModel model;

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

    private boolean sendMove(String in) throws PlaceException{
        String[] tokens = in.split("\\s");
        if (tokens.length < 3) {
            throw new PlaceException("Invalid User Input");
        } else if (tokens[0].equals("-1")){
            return false;
        }
        model.sendTileChange(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]), PlaceColor.values()[Integer.parseInt(tokens[2])]);
        return true;
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
    public boolean getUserInput(String prompt) {
        System.out.print(prompt);
        String input = userIn.nextLine();
        try {
            return sendMove(input);
        } catch (PlaceException pe) {
            System.out.println(pe.getMessage());
        }
        return false;
    }
}
