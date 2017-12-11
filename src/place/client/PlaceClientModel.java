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
import static java.lang.Thread.sleep;

/**
 * Sets up, stores, and updates the model.
 * Also implements methods to converse with the server
 */
public class PlaceClientModel extends Observable {

    private PlaceBoard board;
    private Socket conn;
    private String username;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    /**
     * Where to set up communications from
     * @param host the host to connect to
     * @param port the port to connect to
     * @param username the username to try to connect to
     * @throws PlaceException When connection fails
     */
    public PlaceClientModel(String host, int port, String username) throws PlaceException {
        this.username = username;
        try {
            conn = new Socket(host, port);
            System.out.println("Setting up socket");
            System.out.println("Getting input stream...");
            out = new ObjectOutputStream(conn.getOutputStream());
            in = new ObjectInputStream(conn.getInputStream());
            System.out.println("Getting output stream...");

        } catch (IOException ioe) {
            throw new PlaceException("Connection Failed");
        }
    }

    /**
     * Login function sets up the board from the server
     * @throws PlaceException when something goes wrong
     */
    public void login() throws PlaceException {
        System.out.println("Trying to login");
        try {
            System.out.println("Sending login request");
            out.writeUnshared(new PlaceRequest<>(PlaceRequest.RequestType.LOGIN, username));
            out.flush();
            PlaceRequest<?> confirm = (PlaceRequest<?>) in.readUnshared();
            if(confirm.getType() == PlaceRequest.RequestType.ERROR) {
                throw new PlaceException("Login Failed");
            }
            if (confirm.getType() == PlaceRequest.RequestType.LOGIN_SUCCESS) {
                PlaceRequest<?> boardData = (PlaceRequest<?>) in.readUnshared();
                if (boardData.getType() == PlaceRequest.RequestType.BOARD) {
                    board = (PlaceBoard)boardData.getData();
                    System.out.println("Successfully logged in!");
                }
            }
        } catch (IOException | ClassNotFoundException ioe) {
            throw new PlaceException("Login Failed");
        }
    }

    /**
     * Starts the thread that reads from the server
     */
    public void talkToServer() {
        Thread netThread = new Thread(this::run);
        netThread.start();
    }

    /**
     * Writes the disconnect message from the server and closes the network connections
     */
    public void logoff() {
        try {
            PlaceRequest<String> logout = new PlaceRequest<>(PlaceRequest.RequestType.ERROR, "log off" + username);
            out.writeUnshared(logout);
            in.close();
            out.close();
            conn.close();
        } catch (IOException ioe) {
            System.err.println(ioe);
        }
    }

    /**
     * Sends the tile change to the server
     * @param row the row to create the tile at
     * @param col the colum to create the tile at
     * @param color the color of the tile
     */
    public void sendTileChange(int row, int col, PlaceColor color) {
        PlaceTile toPlace = new PlaceTile(row, col, username, color);
        board.setTile(toPlace);
        PlaceRequest<PlaceTile> tileChange = new PlaceRequest<>(PlaceRequest.RequestType.CHANGE_TILE, toPlace);
        try {
            out.writeUnshared(tileChange);
            out.flush();
            sleep(500);
        } catch (IOException ioe) {
            System.err.println("Failed to write tile: " + ioe.getMessage());
        } catch (InterruptedException ie) {
            System.err.println("Sleep failed");
        }

    }

    /**
     * The loop that gets the updates from the server and notifies observers
     */
    private void run() {
        PlaceRequest<?> req;
        try {
            req = (PlaceRequest<?>) in.readUnshared();
            while (conn.isConnected() && req.getType() != PlaceRequest.RequestType.ERROR) {
                if (req.getType() == PlaceRequest.RequestType.TILE_CHANGED) {
                    PlaceTile changedTile = (PlaceTile) req.getData();
                    board.setTile(changedTile);
                    setChanged();
                    notifyObservers(changedTile);
                }
                PlaceRequest<?> newReq = (PlaceRequest<?>) in.readUnshared();
                if (newReq.getType() == PlaceRequest.RequestType.TILE_CHANGED) {
                    req = newReq;
                }
            }
        } catch (IOException | ClassNotFoundException ioe) {
            if (conn.isClosed()) {
                System.out.println("Successfully logged out " + username);
            }
        }
    }

    /**
     * Returns the board.
     * @return the. board.
     */
    public PlaceBoard getBoard() {
        return board;
    }

}
