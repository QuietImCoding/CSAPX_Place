package place.client.ptui;

import place.PlaceColor;
import place.PlaceException;
import place.client.PlaceClientModel;

import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

/**
 * This class goes observes the model.
 * Couldn't set observer from static main
 */
public class PlaceTextApp implements Observer {

    private final Scanner userIn;
    private final PlaceClientModel model;

    /**
     * Constructs the app with host port and username
     * @param host hostname to connect to
     * @param port port to connect to
     * @param username username to try to connect with
     * @throws PlaceException ¯\_(ツ)_/¯
     */
    PlaceTextApp(String host, int port, String username) throws PlaceException{
        try {
            model = new PlaceClientModel(host, port, username);
            model.addObserver(this);
            userIn = new Scanner(System.in);
            model.login();
            model.talkToServer();
        } catch (PlaceException pe) {
            if (pe.getMessage().equals("Login Failed")) {
                System.out.println("Unable to login with username: " + username + " at " + host + ":" + port);
                System.out.println("Consider a different username");
            } else if (pe.getMessage().equals("Connection Failed")) {
                System.out.println("Connection failed");
            }
            throw new PlaceException("Initialization Failed");
        }
    }

    /**
     * Sends the move to the server
     * converts all the strings to numbers
     * @param row row to send
     * @param col column to send
     * @param color color index in values of place
     * @throws PlaceException ¯\_(ツ)_/¯
     */
    private void sendMove(String row, String col, String color) throws PlaceException{
        try {
            int colorIndex = Integer.parseInt(color);
            model.sendTileChange(Integer.parseInt(row), Integer.parseInt(col), PlaceColor.values()[colorIndex]);
        } catch (NumberFormatException nfe) {
            System.out.println("Colors should be numbers 0 - 15");
        }
    }

    /**
     * Logs off of the server to quit
     */
    void quit() {
        model.logoff();
    }

    /**
     * Kind of useless because of the way the app is designed
     * @param o the model
     * @param arg ¯\_(ツ)_/¯
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    /**
     * prints out the board
     */
    private void display() {
        System.out.println(model.getBoard());
    }

    /**
     * Called in a loop by the PTUI
     * gets user input
     * @return false to exit and break out of loop
     */
    boolean getUserInput() {
        System.out.print("What would you like to do? ");
        String input = userIn.nextLine();
        String[] tokens = input.split("\\s");
        if (tokens[0].equals("view")) {
            display();
            System.out.println();
            return true;
        } else if (tokens[0].equals("move") && tokens.length == 4) {
            try {
                sendMove(tokens[1], tokens[2], tokens[3]);
                return true;
            } catch (PlaceException pe) {
                System.out.println(pe.getMessage());
            }
        } else if (!tokens[0].equals("exit")) {
            return true;
        }
        return false;
    }
}
