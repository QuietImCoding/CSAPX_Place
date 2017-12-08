package place.client.ptui;

import place.client.PlaceClientModel;

import java.util.Observable;
import java.util.Observer;

public class PlacePTUI implements Observer {

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Should be run with args: host port username");
            return;
        }
        PlaceClientModel model = new PlaceClientModel(args[0], Integer.parseInt(args[1]), args[2]);
        model.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
