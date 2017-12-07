package place.server;


import place.PlaceBoard;
import place.PlaceException;
import place.TestClient;
import place.network.PlaceRequest;
import place.server.PlaceClientThread;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;

public class PlaceServer implements Closeable{
    private ServerSocket server;
    private static int dim;
    private Map<String, ObjectOutputStream> clientMap;
    private PlaceBoard board;
    public PlaceServer(int port) throws PlaceException {
        try {
            this.server = new ServerSocket(port);
        } catch (IOException e) {
            throw new PlaceException(e);
        }

    }
    public void sendBoard(){
        Iterator it = clientMap.keySet().iterator();
        while(it.hasNext()){
            try{
                clientMap.get(it.next()).writeObject(new PlaceRequest<>(PlaceRequest.RequestType.BOARD, board) );
            }catch(Exception e){

            }

        }
    }
    public void run(){
        board = new PlaceBoard(dim);
        try{
            Socket clientSocket = server.accept();
            try {
                TestClient client = new TestClient(clientSocket);
                PlaceRequest<PlaceBoard> boardReq = new PlaceRequest<>(PlaceRequest.RequestType.BOARD, board);
                ObjectInputStream in;
                PlaceRequest<?> req = new PlaceRequest<?> (in.readObject());
            }catch(Exception e){

            }

        }catch(Exception e){

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
}