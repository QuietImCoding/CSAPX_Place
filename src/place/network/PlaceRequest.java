package place.network;

import java.io.Serializable;

/**
 * This is the class that represents the requests that are transmitted between
 * PlaceExchange.
 *
 * @param <E> the data type (depends on the request type):<br>
 *      BOARD: PlaceBoard object<br>
 *      CHANGE_TILE: PlaceTile object<br>
 *      ERROR: String<br>
 *      LOGIN: String<br>
 *      LOGIN_SUCCESS: String<br>
 *      TILE_CHANGED: PlaceTile object<br>
 *
 * @author Sean Strout @ RIT CS
 */
public class PlaceRequest<E extends Serializable> implements Serializable {
    public enum RequestType {
        /**
         * After a successful place.client login, the place.server will send the current
         * Board to the place.client.  This is only sent once - afterwards the
         * only information transmitted are the tile changes.
         */
        BOARD,

        /**
         * A place.client's request to the place.server to change a tile.  It will contain
         * a Tile object.  It is important to note that the place.client should not
         * change the tile in their board until it is acknowledged by the place.server
         * via the TILE_CHANGED request.
         */
        CHANGE_TILE,

        /**
         * Used for the server to tell the client there was an error.  It will
         * contain a message about the error. One place this is used is to tell
         * the client a login failed (because the username already exists).  It
         * is also used to indicate the server is shutting down, or any other
         * unusual things happen.
         */
        ERROR,

        /**
         * Used by the place.client to login to the place.server.  It will contain a string
         * that is the desired username for the place.client.
         */
        LOGIN,

        /**
         * Used by the place.server to indicate to the place.client the login succeeded.
         * It will contain a string indicating this.
         */
        LOGIN_SUCCESS,

        /**
         * Used by the place.server to indicate to all clients that a tile has
         * officially been changed.  It will contain the new Tile object.
         * The clients should update their view of the board each time
         * a tile change arrives.
         */
        TILE_CHANGED
    }

    /** The request type */
    private RequestType type;
    /** The data associated with the request */
    private E data;

    /**
     * Create a new request.
     *
     * @param type request type
     * @param data the data
     */
    public PlaceRequest(RequestType type, E data) {
        this.type = type;
        this.data = data;
    }

    /**
     * Get the type of request.
     *
     * @return request type
     */
    public RequestType getType() { return type; }

    /**
     * Get the data associated with the request.
     *
     * @return the data
     */
    public E getData() { return data; }

    /**
     * Utility method for debugging only.
     *
     * @return the tile as a string
     */
    @Override
    public String toString() {
        return "PlaceRequest{" +
                "type=" + type +
                ", data=" + data +
                '}';
    }
}
