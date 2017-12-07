package place;

import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

public class TestClient implements Observer {
    private Socket me;
    public TestClient(Socket me){
        this.me = me;
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
