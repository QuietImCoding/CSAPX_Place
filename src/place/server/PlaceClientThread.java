package place.server;

import jdk.nashorn.internal.runtime.ECMAException;
import place.PlaceBoard;
import place.PlaceException;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class PlaceClientThread implements Runnable, Closeable {
    private Socket sock;
    private String username;
    private PrintStream printer;
    private Scanner scanner;
    private BufferedWriter writer;
    private PlaceBoard board;
    public PlaceClientThread(String hostname, int port, PlaceBoard model){

        try{
            this.sock = new Socket( hostname, port );
            this.scanner = new Scanner( sock.getInputStream() );
            this.printer = new PrintStream( sock.getOutputStream() );
            this.board = model;
            printer.print( "Connected to server " + this.sock );
            Thread netThread = new Thread( () -> this.run() );
            netThread.start();
        }catch(Exception e){

        }
    }
    public void changed(){

    }
    public void run(){
        try{

        }catch(Exception e){}
    }

    public void close(){
        try{
            this.sock.close();
        }catch(Exception e){
        }
    }
}
