package place.client;

public interface PlaceClient {

    /**
     * Both clients need a way to display themselves
     */
    void display();

    /**
     * Get the user to input something
     * @param prompt Telling the user what to do
     */
    void getUserInput(String prompt);

}
