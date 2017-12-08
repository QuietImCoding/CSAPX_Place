package place.client;

import place.PlaceBoard;
import place.PlaceColor;
import place.PlaceTile;
import place.network.PlaceRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

public class PlaceClientModel extends Observable {

    private PlaceBoard board;
    private Socket conn;
    private String username;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public PlaceClientModel(String host, int port, String username) {
        this.username = username;
        try {
            conn = new Socket(host, port);
            in = new ObjectInputStream(conn.getInputStream());
            out = new ObjectOutputStream(conn.getOutputStream());
        } catch (IOException ioe) {
            System.out.println();
        }
    }


    public boolean login() {
        try {
            out.writeObject(new PlaceRequest<>(PlaceRequest.RequestType.LOGIN, username));
            PlaceRequest<?> confirm = (PlaceRequest<?>) in.readObject();
            if (confirm.getType() == PlaceRequest.RequestType.LOGIN_SUCCESS) {
                PlaceRequest<?> boardData = (PlaceRequest<?>) in.readObject();
                if (boardData.getType() == PlaceRequest.RequestType.BOARD) {
                    board = (PlaceBoard)boardData.getData();
                    return true;
                }
            }
        } catch (IOException ioe) {
            System.out.println("YOU MESSED UP SOMETHING");
        } catch (ClassNotFoundException cnfe) {
            System.out.println("I have never seen this exception in my lief");
        }
        return false;
    }

    public void talkToServer() {
        Thread netThread = new Thread( () -> this.run() );
    }

    public void sendTileChange(int row, int col, PlaceColor color) {
        PlaceTile toPlace = new PlaceTile(row, col, username, color);
        PlaceRequest<PlaceTile> tileChange = new PlaceRequest<>(PlaceRequest.RequestType.CHANGE_TILE, toPlace);
        try {
            out.writeObject(tileChange);
        } catch (IOException ioe) {
            System.out.println("Failed to write tile");
        }
    }

    public void run() {
        try {
            PlaceRequest<?> req = (PlaceRequest<?>) in.readObject();
            while (req.getType() != PlaceRequest.RequestType.ERROR) {
                if (req.getType() == PlaceRequest.RequestType.TILE_CHANGED) {
                    board.setTile((PlaceTile) req.getData());
                    setChanged();
                    notifyObservers();
                }
            }
        } catch (IOException ioe) {
            System.out.println("YOU MESSED UP SOMETHING");
        } catch (ClassNotFoundException cnfe) {
            System.out.println("I have never seen this exception in my lief");
        }
    }

    public PlaceBoard getBoard() {
        return board;
    }

}
