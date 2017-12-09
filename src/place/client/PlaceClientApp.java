package place.client;

import place.client.ptui.PlacePTUI;

public class PlaceClientApp {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Input should be formatted java PlaceClientApp <host> <port> <username>");
            return;
        }
        PlaceClient client = new PlacePTUI(args[0], Integer.parseInt(args[1]), args[2]);
        System.out.println("Welcome to Place!");
        System.out.println("When asked what to do, you can either type 'view' or 'move <row> <col> <color>'");
        System.out.println("Typing 'view' will print out the current board");
        System.out.println("Typing 'move <row> <col> <color> will place a tile at that spot with that color");
        System.out.println("Colors should be numbers 0 - 15");

        boolean running = true;
        while (running) {
            running = client.getUserInput();
        }
    }
}
