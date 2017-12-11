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

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * The GUI client
 */
public class PlaceGUI extends Application implements Observer {

    private PlaceClientModel model;
    private PlaceBoard board;
    private int boardDim;
    private PlaceColor curColor;
    private int dim;
    private double tileWidth;
    private GraphicsContext gc;
    private boolean holdingTile;
    private double mouseX, mouseY;

    /**
     * Initializes variables
     */
    @Override
    public void init() {
        List<String> args = getParameters().getRaw();
        String host = args.get(0);
        int port = Integer.parseInt(args.get(1));
        String username = args.get(2);
        curColor = PlaceColor.WHITE;
        holdingTile = false;
        // Tries to set up model
        try {
            model = new PlaceClientModel(host, port, username);
            model.addObserver(this);
            dim = 512;
            model.login();
            board = model.getBoard();
            boardDim = board.DIM;
            tileWidth = (double)dim / (double)boardDim;
            model.talkToServer();
        } catch (PlaceException pe) {
            System.out.println("Connection issue. Shutting down...");
            System.exit(1);
        }
    }

    /**
     * Draws the frame by redrawing the model and adding the infobox if neccessary
     * also draws the tile if you're holding it
     */
    private void drawFrame() {
        drawModel();
        if (holdingTile) {
            gc.setFill(convertColorToRGB(curColor));
            gc.fillRect(mouseX - (tileWidth / 2), mouseY - (tileWidth / 2), tileWidth, tileWidth);
            setUpColorPicker();
        } else if (mouseY < dim) {
            showInfoBox();
        }
    }

    /**
     * Displays the box with information about the tile you're hovering over
     */
    private void showInfoBox() {
        double infoBoxX;
        double infoBoxWidth = dim / 4;
        double infoBoxHeight = dim / 8;
        double infoBoxY = dim - infoBoxHeight;
        if (mouseX > 3 * dim / 4) {
            infoBoxX = 0;
        } else {
            infoBoxX = dim - infoBoxWidth;
        }
        gc.setFill(Color.rgb(100, 100, 100));
        gc.fillRect(infoBoxX, infoBoxY, infoBoxWidth, infoBoxHeight);
        int[] tileTarget = findTileTarget(mouseX, mouseY);
        PlaceTile toInspect = board.getTile(tileTarget[0], tileTarget[1]);
        gc.setFill(convertColorToRGB(toInspect.getColor()));
        gc.fillRect(infoBoxX + (infoBoxWidth / 8), infoBoxY + (infoBoxHeight / 4), infoBoxHeight / 2, infoBoxHeight / 2);
        gc.setFill(convertColorToRGB(PlaceColor.WHITE));
        gc.fillText(toInspect.getOwner(), infoBoxX + (5 * infoBoxWidth / 12), infoBoxY + (infoBoxHeight / 4), infoBoxWidth / 2);
        gc.fillText("( " + Integer.toString(toInspect.getCol()) + ", " + Integer.toString(toInspect.getRow()) + " ) " + toInspect.getColor().name(), infoBoxX + (5 * infoBoxWidth / 12), infoBoxY + (infoBoxHeight / 2), infoBoxWidth / 2);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        gc.fillText(dateFormat.format(toInspect.getTime()), infoBoxX + (5 * infoBoxWidth / 12), infoBoxY + (3 * infoBoxHeight / 4), infoBoxWidth / 2);
    }

    /**
     * Converts a place color to an RGB paint color
     * @param c the placecolor to convert
     * @return a paint color usable by the graphicscontext
     */
    private Color convertColorToRGB(PlaceColor c) {
        return Color.rgb(c.getRed(), c.getGreen(), c.getBlue());
    }

    /**
     * What to do when we get an update from the model
     * @param o the model
     * @param arg the argument if it's passed by the model
     */
    @Override
    public void update(Observable o, Object arg) {
        if (gc != null && o instanceof PlaceClientModel && arg instanceof PlaceTile) {
            drawTile((PlaceTile)arg);
            board = model.getBoard();
        }
    }

    /**
     * Draws the tile on the screen
     * @param t the placetile to draw
     */
    private void drawTile(PlaceTile t) {
        PlaceColor fill = t.getColor();
        gc.setFill(Color.rgb(fill.getRed(), fill.getGreen(), fill.getBlue()));
        gc.fillRect(t.getCol() * tileWidth, t.getRow() * tileWidth, tileWidth, tileWidth);
    }

    /**
     * Draws the entire place board
     */
    private void drawModel() {
        for (int x = 0; x < boardDim; x++) {
            for (int y = 0; y < boardDim; y++) {
                PlaceTile toDraw = board.getTile( y,  x);
                PlaceColor fill = toDraw.getColor();
                gc.setFill(Color.rgb(fill.getRed(), fill.getGreen(), fill.getBlue()));
                gc.fillRect(x * tileWidth, y * tileWidth, tileWidth, tileWidth);
            }
        }
    }

    /**
     * Sends a tile to the server
     * @param row the row to create the tile at
     * @param col the colum to create the tile at
     */
    private void sendTile(int row, int col) {
        model.sendTileChange(row, col, curColor);
    }

    /**
     * Figures out where in the board to send tiles to based on mouse x and y
     * Surprisingly worked the first time
     * @param x the x coordinate in the canvas
     * @param y the y coordinate in the canvas
     * @return an integer array with the indecies in board
     */
    private int[] findTileTarget(double x, double y) {
        int tileX = (int) Math.floor(x / tileWidth);
        int tileY = (int) Math.floor(y / tileWidth);
        return new int[]{tileY, tileX};
    }

    /**
     * draws the color picker at the bottom of the window
     */
    private void setUpColorPicker() {
        double pickWidth = dim / 16;
        for (int x = 0; x < PlaceColor.TOTAL_COLORS; x++) {
            PlaceColor fillColor = PlaceColor.values()[x];
            gc.setFill(Color.rgb(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue()));
            gc.fillRect(x * pickWidth, dim, pickWidth, dim / PlaceColor.TOTAL_COLORS);
        }
    }

    /**
     * Picks a color based on the x coordinate
     * @param x the x coordinate of the mouse presumably
     */
    private void pickColor(double x) {
        float pickWidth = dim / 16;
        int colorNum = (int) Math.floor(x / pickWidth);
        curColor = PlaceColor.values()[colorNum];
    }

    /**
     * Starts the application
     * @param primaryStage ¯\_(ツ)_/¯
     * @throws Exception ¯\_(ツ)_/¯
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Drawing Operations Test");
        Group root = new Group();
        Canvas canvas = new Canvas(dim, dim + 32);
        gc = canvas.getGraphicsContext2D();

        drawModel();
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED,
                t -> {
                    if (t.getY() < dim && holdingTile) {
                        int[] target = findTileTarget(t.getX(), t.getY());
                        sendTile(target[0], target[1]);
                        holdingTile = false;
                    } else if (t.getY() > dim){
                        pickColor(t.getX());
                        holdingTile = !holdingTile;
                    }
                });
        canvas.addEventHandler(MouseEvent.MOUSE_MOVED,
                t -> {
                    mouseX = t.getX();
                    mouseY = t.getY();
                    try {
                        drawFrame();
                    } catch (ArrayIndexOutOfBoundsException ignored) {

                    }
                }
        );
        setUpColorPicker();
        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    /**
     * Logoff when window closed
     */
    @Override
    public void stop() {
        model.logoff();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
