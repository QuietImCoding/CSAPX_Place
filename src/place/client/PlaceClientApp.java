package place.client;

import place.client.gui.PlaceGUI;
import place.client.ptui.PlacePTUI;

public class PlaceClientApp {
    public static void main(String[] args) {
        if (args.length < 4) {
            System.out.println("Input should be formatted java PlaceClientApp <host> <port> <username> (GUI / PTUI)");
            return;
        }
        PlaceClient client;
        if (args[3].toLowerCase().equals("GUI")) {
            client = new PlaceGUI(args[0], Integer.parseInt(args[1]), args[2]);
        } else {
            client = new PlacePTUI(args[0], Integer.parseInt(args[1]), args[2]);
        }

        boolean running = true;
        while (running) {
            running = client.getUserInput("Where do you want to place tile? ");
        }
    }
}
