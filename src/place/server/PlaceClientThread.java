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
        this.board = server.getBoard();
        this.server = server;
        try {
            this.out = new ObjectOutputStream(sock.getOutputStream());
            this.in = new ObjectInputStream(sock.getInputStream());
        }
        catch (IOException e) {
        }
    }



    public void run(){
        try{
            PlaceRequest<?> req;
            PlaceRequest<?> serv;
            while(true){
                try{
                    req = (PlaceRequest<?>)in.readObject();

                    System.out.println(req);
                    PlaceRequest.RequestType type = req.getType();
                    switch(type) {
                        case LOGIN: {
                            username = (String) req.getData();
                            if(server.login(username, out)) {
                                board = server.getBoard();
                                serv = new PlaceRequest<>(PlaceRequest.RequestType.BOARD, board);
                                out.writeObject(serv);
                                out.flush();
                            }
                            else{
                                sock.close();
                            }

                            break;
                        }
                        case CHANGE_TILE: {
                            server.tileChange((PlaceTile) req.getData());
                            board = server.getBoard();
                            Thread.sleep(500);
                            break;


                        }
                        case ERROR:{
                           close();
                        }
//                        default: {
//                            req = new PlaceRequest<>(PlaceRequest.RequestType.ERROR, "???");
//                            System.out.println("to "+username+": " +req);
//                            out.writeObject(req);
//                            out.flush();
//                        }
                    }

                }catch(Exception e){

                }
                if(!sock.isConnected()){
                    close();
                }
            }
        }catch(Exception e){}
    }
    public void close(){
        try{

            server.updateClients(username);
            this.sock.close();
        }catch(Exception e){
        }
    }
}
