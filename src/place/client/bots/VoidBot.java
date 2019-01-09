package place.client.bots;

import place.PlaceBoard;
import place.PlaceColor;
import place.PlaceException;
import place.client.PlaceClientModel;

import java.util.ArrayList;

import static java.lang.Thread.sleep;


public class VoidBot {



    public static void main(String[] args) {
        if (args.length < 2) {
            return;
        }
        try {
            ArrayList<PlaceClientModel> voidBots = new ArrayList<>();
            PlaceClientModel model = new PlaceClientModel(args[0], Integer.parseInt(args[1]), "VoidBot");
            model.login();
            PlaceBoard b = model.getBoard();
            model.logoff();
            for (int i = 0; i < b.DIM; i++) {
                try {
                    System.out.println("Preparing VoidBot" + i);
                    PlaceClientModel voidBot = new PlaceClientModel(args[0], Integer.parseInt(args[1]), "VoidBoot" + i);
                    sleep(1000);
                    voidBots.add(voidBot);
                    voidBot.login();
                } catch (PlaceException | InterruptedException eee) {
                    System.out.println("VoidBot" + i + " is out of the running");
                }
            }
            for (int row = 0; row < b.DIM; row++) {
                for (int i = 0; i < voidBots.size(); i++) {
                    voidBots.get(i).sendTileChange(row, i, PlaceColor.BLACK);
                }
                System.out.println("VoidBots are at row " + row);
            }
            for (PlaceClientModel voidBot : voidBots) {
                voidBot.logoff();
            }
        } catch (PlaceException pe) {
            System.out.println("initial board fetch failed");
        }
    }


}
