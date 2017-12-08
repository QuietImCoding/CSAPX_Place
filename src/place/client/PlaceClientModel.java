package place.client;

import javafx.beans.InvalidationListener;
import place.PlaceBoard;
import place.network.PlaceRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Observable;

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


    public void login() {
        try {
            out.writeObject(new PlaceRequest<String>(PlaceRequest.RequestType.LOGIN, username));
            PlaceRequest<?> confirm = (PlaceRequest<?>) in.readObject();
            if (confirm.getType() == PlaceRequest.RequestType.LOGIN_SUCCESS) {
                PlaceRequest<?> boardData = (PlaceRequest<?>) in.readObject();
                if (boardData.getType() == PlaceRequest.RequestType.BOARD) {
                    board = (PlaceBoard)boardData.getData();
                    notifyObservers();
                }
            }
        } catch (IOException ioe) {
            System.out.println("YOU MESSED UP SOMETHING");
        } catch (ClassNotFoundException cnfe) {
            System.out.println("I have never seen this exception in my lief");
        }
    }

    public void update() {
        notifyObservers();
    }

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java ReversiServer #_rows #_cols port");
            return;
        }
        int port = Integer.parseInt(args[0]);
    }
}
