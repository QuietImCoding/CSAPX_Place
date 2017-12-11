package place.client.bots;

import place.PlaceBoard;
import place.PlaceColor;
import place.PlaceException;
import place.client.PlaceClientModel;

import java.util.ArrayList;


public class VoidBot {



    public static void main(String[] args) {
        if (args.length < 2) {
            return;
        }
        try {
            PlaceClientModel model = new PlaceClientModel(args[0], Integer.parseInt(args[1]), "VoidBot");
            model.login();
            PlaceBoard b = model.getBoard();
            int row = 0;
            int col = 0;
            while (true) {
                if (row == b.DIM) {
                    col++;
                    row = 0;
                }
                if (col == b.DIM) {
                    col = 0;
                }
                model.sendTileChange(row, col, PlaceColor.BLACK);
                row++;
                System.out.println("VoidBot is at row " + row + " and col " + col);
            }
        } catch (PlaceException pe) {
            System.out.println("Connection failed. VoidBot is sad");
        }
    }


}
