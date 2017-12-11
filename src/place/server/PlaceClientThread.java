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

    /**
     * sets up the input and output streams as well as the board and username
     * @param socket: the client socket
     * @param username: the client's username
     * @param server: the server
     */
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


    /**
     * receives all the requests from the client and informs the of server what to do
     */
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
                    }

                }catch(Exception e){

                }
                if(!sock.isConnected()){
                    close();
                }
            }
        }catch(Exception e){}
    }
    /**
     * tells the server to remove the client's username from the list of usernames then closes the socket
     */
    public void close(){
        try{

            server.updateClients(username);
            this.sock.close();
        }catch(Exception e){
        }
    }
}
