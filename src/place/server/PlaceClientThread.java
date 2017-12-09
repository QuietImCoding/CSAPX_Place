package place.server;

import jdk.nashorn.internal.runtime.ECMAException;
import place.PlaceBoard;
import place.PlaceException;
import place.PlaceTile;
import place.network.PlaceRequest;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class PlaceClientThread implements Runnable, Closeable {
    private Socket sock;
    private String username;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private PlaceBoard board;
    private PlaceServer server;
//    public PlaceClientThread(String hostname, int port, PlaceBoard model){
//
//        try{
//            this.sock = new Socket( hostname, port );
//            this.scanner = new Scanner( sock.getInputStream() );
//            this.printer = new PrintStream( sock.getOutputStream() );
//            this.board = model;
//            printer.print( "Connected to server " + this.sock );
//            Thread netThread = new Thread( () -> this.run() );
//            netThread.start();
//        }catch(Exception e){
//
//        }
//    }

    public PlaceClientThread(Socket socket, String username, PlaceServer server){
        this.sock = socket;
        this.username = username;
        System.out.println("User " + username +" connected on socket "+sock);
        this.board = board;
        this.server = server;
        try {
            this.out = new ObjectOutputStream(sock.getOutputStream());
            this.in = new ObjectInputStream(sock.getInputStream());
        }
        catch (IOException e) {
        }
    }
    public void addBoard(PlaceBoard board){
        this.board = board;
    }
    public void changed(PlaceTile tile){
        PlaceRequest<PlaceTile> tileChange = new PlaceRequest<>(PlaceRequest.RequestType.CHANGE_TILE, tile);
        try{
            out.writeObject(tileChange);
            out.flush();
        }catch(IOException e){

        }
    }

    public ObjectOutputStream getOut() {
        return out;
    }


    public void run(){
        try{
            boolean listening = true;
            PlaceRequest<?> req;
            while(listening){
                try{
                    req = (PlaceRequest<?>)in.readObject();

                System.out.println(req);
                PlaceRequest.RequestType type = req.getType();
                System.out.println(type);
                switch(type) {
                    case LOGIN: {
                        username = (String) req.getData();
                        server.login(username, out);
                        req = new PlaceRequest<>(PlaceRequest.RequestType.BOARD, board);
                        out.writeObject(req);
                        out.flush();
                    }
                    case CHANGE_TILE: {
                        server.tileChange((PlaceTile) req.getData());
                        board = server.getBoard();


                    }
                    case ERROR:{
                        server.updateClients(username);
                        close();
                    }
                    default: {
                        req = new PlaceRequest<>(PlaceRequest.RequestType.ERROR, "???");
                        out.writeObject(req);
                        out.flush();
                    }
                }

                }catch(Exception e){

                }
            }
        }catch(Exception e){}
    }
    public void start(){
        this.run();
    }
    public void close(){
        try{
            this.sock.close();
        }catch(Exception e){
        }
    }
}
