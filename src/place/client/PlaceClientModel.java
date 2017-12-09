package place.client;

import place.PlaceBoard;
import place.PlaceColor;
import place.PlaceException;
import place.PlaceTile;
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
            System.out.println("Setting up sockets");
            System.out.println("Getting input stream...");
            in = new ObjectInputStream(conn.getInputStream());
            System.out.println("Getting output stream...");
            out = new ObjectOutputStream(conn.getOutputStream());
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }


    public void login() throws PlaceException {
        System.out.println("Trying to login");
        try {
            System.out.println("Sending login request");
            out.writeObject(new PlaceRequest<>(PlaceRequest.RequestType.LOGIN, username));
            out.flush();
            PlaceRequest<?> confirm = (PlaceRequest<?>) in.readObject();
            if (confirm.getType() == PlaceRequest.RequestType.LOGIN_SUCCESS) {
                PlaceRequest<?> boardData = (PlaceRequest<?>) in.readObject();
                if (boardData.getType() == PlaceRequest.RequestType.BOARD) {
                    board = (PlaceBoard)boardData.getData();
                    System.out.println("Successfully logged in!");
                }
            }
        } catch (IOException | ClassNotFoundException ioe) {
            throw new PlaceException("Login failed");
        }
    }

    public void talkToServer() {
        Thread netThread = new Thread(this::run);
        netThread.start();
    }

    public void logoff() {
        try {
            in.close();
            out.close();
            conn.close();
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    public void sendTileChange(int row, int col, PlaceColor color) {
        PlaceTile toPlace = new PlaceTile(row, col, username, color);
        PlaceRequest<PlaceTile> tileChange = new PlaceRequest<>(PlaceRequest.RequestType.CHANGE_TILE, toPlace);
        try {
            out.writeObject(tileChange);
            out.flush();
        } catch (IOException ioe) {
            System.out.println("Failed to write tile");
        }
    }

    private void run() {
        try {
            PlaceRequest<?> req = (PlaceRequest<?>) in.readObject();
            while (conn.isConnected() && req.getType() != PlaceRequest.RequestType.ERROR) {
                if (req.getType() == PlaceRequest.RequestType.TILE_CHANGED) {
                    PlaceTile changedTile = (PlaceTile) req.getData();
                    board.setTile(changedTile);
                    setChanged();
                    notifyObservers(changedTile);
                }
                PlaceRequest<?> newReq = (PlaceRequest<?>) in.readObject();
                if (newReq.getType() == PlaceRequest.RequestType.TILE_CHANGED) {
                    req = newReq;
                }
            }
        } catch (IOException | ClassNotFoundException ioe) {
            System.out.println(ioe);
        }
    }

    public PlaceBoard getBoard() {
        return board;
    }

}
