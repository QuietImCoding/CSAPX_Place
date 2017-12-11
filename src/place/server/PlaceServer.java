package place.server;


import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;
import place.PlaceBoard;
import place.PlaceException;
import place.PlaceTile;
import place.network.PlaceRequest;

import java.io.*;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//:)

public class PlaceServer implements Closeable{
    private Map<String, ObjectOutputStream> clients;
    private ServerSocket server;
    private static int dim;
    private PlaceBoard board;
    private PlaceRequest<?> req;
    public PlaceServer(int port) throws PlaceException {
        try {
            this.server = new ServerSocket(port);
            clients = new HashMap<>();
            board = new PlaceBoard(dim);


        } catch (IOException e) {
            throw new PlaceException(e);
        }

    }

    /**
     * accepts connections and starts a thread
     */
    public void run() {
            while (true) {
                try{
                    new Thread( new PlaceClientThread(server.accept(), "", this)).start();
                }catch(Exception e){

                }

            }

    }

    /**
     * is called when the thread receives a login request
     * @param username: username requested by the client
     * @param o: output stream of the PlaceClientThread
     * @return: returns true if logging in was successful, false if unsuccessful (there is already someone online with that name)
     */
    public synchronized boolean login(String username, ObjectOutputStream o) {
        for(String key : clients.keySet()){
            if (key.equals(username))
                try{
                PlaceRequest<?> err = new PlaceRequest<>(PlaceRequest.RequestType.ERROR, "Login failed. There is another user with that name.");
                    o.writeObject(new PlaceRequest<>(PlaceRequest.RequestType.ERROR, "Login failed. There is another user with that name."));
                    o.flush();
                    System.out.println(err + (username));
                    return false;
                }catch(Exception e){

                }

        }
        clients.put(username,o);
        System.out.println("Success! Current clients: "+ clients.keySet());
        try{
            o.writeObject(new PlaceRequest<>(PlaceRequest.RequestType.LOGIN_SUCCESS,"Welcome "+username));
            o.flush();
        }catch (Exception e){

        }
        return true;
    }
    @Override
    public void close() throws IOException {
        try {
            this.server.close();
        } catch (IOException ioe) {
        }
    }

    /**
     *
     * @param args: the port of the server and the dimensions of the board
     * @throws PlaceException
     */
    public static void main(String[] args) throws PlaceException {
        if (args.length != 2) {
            System.exit(1);
        }
        dim = Integer.parseInt(args[1]);
        try (PlaceServer server = new PlaceServer(Integer.parseInt(args[0]))) {
            server.run();
        } catch (Exception e) {
            System.err.println("Failed to start server!");
            e.printStackTrace();
        }
    }

    /**
     * called when a Tile_Change request comes in from the client
     * adds the time the change is made to the tile, changes the tile, then calls sendTile
     * @param tile: the tile that is being changed
     */
    public synchronized void tileChange(PlaceTile tile){
        tile.setTime(System.currentTimeMillis());
        board.setTile(tile);
        System.out.println("tile changed");
        sendTile(tile);
    }

    /**
     * called when the thread's board needs to be updated
     * @return the server board
     */
    public PlaceBoard getBoard(){
        return board;
    }

    /**
     * updates the clients map when a client is loggin off
     * @param username: client to be removed from the clients map
     */
    public synchronized void updateClients(String username){
        clients.remove(username);
        System.out.println(username + " has logged off. Online: "+ clients.keySet());
    }

    /**
     * is called after a tile has been changed
     * sends everyone in client app the changed tile
     * @param tile: the tile that has been changed
     */
    public void sendTile(PlaceTile tile){
        for (String username:clients.keySet()
             ) {
            System.out.println("Sending " + tile + " to " + username);
            try {
                clients.get(username).writeObject(new PlaceRequest<>(PlaceRequest.RequestType.TILE_CHANGED, tile));
                clients.get(username).flush();
            }catch(Exception e){

        }
        }
    }

}