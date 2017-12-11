package place.client.ptui;

import place.PlaceException;

public class PlacePTUI {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Input should be formatted java PlacePTUI <host> <port> <username>");
            return;
        }
        try {
            PlaceTextApp client = new PlaceTextApp(args[0], Integer.parseInt(args[1]), args[2]);
            System.out.println("Welcome to Place!");
            System.out.println("When asked what to do, you can either type 'view', 'exit', or 'move <row> <col> <color>'");
            System.out.println("Typing 'view' will print out the current board");
            System.out.println("Typing 'move <row> <col> <color> will place a tile at that spot with that color");
            System.out.println("Colors should be numbers 0 - 15");

            boolean running = true;
            while (running) {
                running = client.getUserInput();
            }
            client.quit();
        } catch (PlaceException pe) {
            System.out.println("Unexpected failure");
        }
    }
}
