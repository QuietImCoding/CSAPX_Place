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


        } catch (IOException e) {
            throw new PlaceException(e);
        }

    }
    public void run() {
            board = new PlaceBoard(dim);
            boolean listening = true;
            while (listening) {
                try{
                    PlaceClientThread client = new PlaceClientThread(server.accept(), "", this);
                    client.addBoard(board);
                    client.start();
                }catch(Exception e){

                }

            }

    }
    public synchronized void login(String username, ObjectOutputStream o){
        Iterator it = clients.keySet().iterator();
        while(it.hasNext()) {
            if (it.next() == username)
                try{
                    o.writeObject(new PlaceRequest<>(PlaceRequest.RequestType.ERROR, "Login failed. There is another user with that name."));
                    o.flush();
                }catch(Exception e){

                }
        }
        clients.put(username,o);
        System.out.println("Success!");
        try{
            o.writeObject(new PlaceRequest<>(PlaceRequest.RequestType.LOGIN_SUCCESS,"Welcome "+username));
            o.flush();
        }catch (Exception e){

        }
    }
    @Override
    public void close() throws IOException {
        try {
            this.server.close();
        } catch (IOException ioe) {
        }
    }
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
    public synchronized void tileChange(PlaceTile tile){
        board.setTile(tile);
        System.out.println("tile changed");
        sendTile(tile);
    }
    public PlaceBoard getBoard(){
        return board;
    }
    public synchronized void updateClients(String username){
        clients.remove(username);
    }
    public void sendTile(PlaceTile tile){
        Iterator it = clients.keySet().iterator();
        while(it.hasNext()) {
            try {
                String username = (String)it.next();
                System.out.println("Sending " + tile + " to " + username);
                clients.get(username).writeObject(new PlaceRequest<>(PlaceRequest.RequestType.TILE_CHANGED, tile));
                clients.get(username).flush();
            }
            catch(Exception e){

        }
        }
    }

}