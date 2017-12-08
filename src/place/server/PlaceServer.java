package place.server;


import place.PlaceBoard;
import place.PlaceException;
import place.network.PlaceRequest;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.util.Iterator;
import java.util.Map;

public class PlaceServer implements Closeable{
    private Map<String, ObjectOutputStream> clients;
    private ServerSocket server;
    private static int dim;
    private PlaceBoard board;
    public PlaceServer(int port) throws PlaceException {
        try {
            this.server = new ServerSocket(port);

            board = new PlaceBoard(dim);
            boolean listening = true;
            while(listening){
                PlaceClientThread client = new PlaceClientThread(server.accept());
                client.start();

            }
        } catch (IOException e) {
            throw new PlaceException(e);
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
        } catch (Exception e) {
            System.err.println("Failed to start server!");
            e.printStackTrace();
        }
    }
    public synchronized void sendBoard(){
        Iterator it = clients.keySet().iterator();
        while(it.hasNext()){
            try{
                clients.get(it.next()).writeObject(new PlaceRequest<>(PlaceRequest.RequestType.BOARD, board) );
            }catch(Exception e){

            }

        }
    }
}