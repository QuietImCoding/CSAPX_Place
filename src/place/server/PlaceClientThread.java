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

    public PlaceClientThread(Socket socket){
        this.sock = sock;
        try {
            this.in = new ObjectInputStream(sock.getInputStream());
            this.out = new ObjectOutputStream(sock.getOutputStream());
        }
        catch (IOException e) {
        }
    }

    public void changed(PlaceTile tile){
        PlaceRequest<PlaceTile> tileChange = new PlaceRequest<>(PlaceRequest.RequestType.CHANGE_TILE, tile);
        try{
            out.writeObject(tileChange);
        }catch(IOException e){

        }
    }
    public void run(){
        try{
            boolean listening = true;
            while(listening){
                PlaceRequest<?> req = (PlaceRequest<?>)in.readObject();
                if(req.getType() == PlaceRequest.RequestType.LOGIN){
                    username = (String)req.getData();
                    login();
                }


            }
        }catch(Exception e){}
    }
    public String getUserName(){
        return username;
    }
    public void login(){
        //ask server to log in
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
