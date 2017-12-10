package place.client.gui;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import place.PlaceBoard;
import place.PlaceColor;
import place.PlaceException;
import place.PlaceTile;
import place.client.PlaceClientModel;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class PlaceGUI extends Application implements Observer {

    private PlaceClientModel model;
    private int boardDim;
    private PlaceColor curColor;
    private int dim;
    private double tileWidth;
    private GraphicsContext gc;
    private boolean holdingTile;
    private double mouseX, mouseY;

    @Override
    public void init() {
        List<String> args = getParameters().getRaw();
        String host = args.get(0);
        int port = Integer.parseInt(args.get(1));
        String username = args.get(2);
        curColor = PlaceColor.WHITE;
        holdingTile = false;
        model = new PlaceClientModel(host, port, username);
        model.addObserver(this);
        dim = 512;
        try {
            model.login();
        } catch (PlaceException pe) {
            System.out.println("Unable to login with username: " + username + " at " + host + ":" + port);
        }
        model.talkToServer();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof PlaceClientModel && arg instanceof PlaceTile) {
            drawTile((PlaceTile) arg);
        }
    }

    private void drawTile(PlaceTile t) {
        tileWidth = dim / boardDim;
        PlaceColor fill = t.getColor();
        gc.setFill(Color.rgb(fill.getRed(), fill.getGreen(), fill.getBlue()));
        gc.fillRect(t.getCol() * tileWidth, t.getRow() * tileWidth, tileWidth, tileWidth);
    }

    private void drawModel() {
        PlaceBoard board = model.getBoard();
        boardDim = board.DIM;
        tileWidth = dim / boardDim;
        for ( int x = 0; x < boardDim; x++ ) {
            for ( int y = 0; y < boardDim; y++) {
                PlaceTile toDraw = board.getTile(x, y);
                PlaceColor fill = toDraw.getColor();
                gc.setFill(Color.rgb(fill.getRed(), fill.getGreen(), fill.getBlue()));
                gc.fillRect(y * tileWidth, x * tileWidth, tileWidth, tileWidth);
            }
        }
    }

    private void sendTile(int col, int row) {
        model.sendTileChange(row, col, curColor);
    }

    private int[] findTileTarget(double x, double y) {
        float width = dim / boardDim;
        int tileX = (int)Math.floor(x / tileWidth);
        int tileY = (int)Math.floor(y / tileWidth);
        return new int[] {tileX, tileY};
    }

    private void setUpColorPicker() {
        float pickWidth = dim / 16;
        for (int x = 0; x < PlaceColor.values().length; x++) {
            PlaceColor fillColor = PlaceColor.values()[x];
            gc.setFill(Color.rgb(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue()));
            gc.fillRect(x * pickWidth, dim, pickWidth, 32);
        }
    }

    private void pickColor(double x) {
        float pickWidth = dim / 16;
        int colorNum = (int)Math.floor(x / pickWidth);
        curColor = PlaceColor.values()[colorNum];
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Drawing Operations Test");
        Group root = new Group();
        Canvas canvas = new Canvas(dim , dim + 32);
        gc = canvas.getGraphicsContext2D();

        drawModel();
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED,
                t -> {
                    if (t.getY() < dim) {
                        int[] target = findTileTarget(t.getX(), t.getY());
                        sendTile(target[0], target[1]);
                    }
                    else {
                        pickColor(t.getX());
                    }
                });
        canvas.addEventHandler(MouseEvent.MOUSE_MOVED,
            t -> {
                mouseX = t.getX();
                mouseY = t.getY();
            }
        );
        setUpColorPicker();
        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    @Override
    public void stop() {
        model.logoff();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
