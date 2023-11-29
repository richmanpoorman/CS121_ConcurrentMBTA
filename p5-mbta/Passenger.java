import java.util.Map;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Passenger extends Entity {
    private static Map<String, Passenger> passengerPool = new HashMap<String, Passenger>();

    // Keeps track of which stations to go to
    private Queue<Station> path = new ConcurrentLinkedQueue<Station>(); 
    private Passenger(String name) { super(name); }

    public static Passenger make(String name) {
        if (passengerPool.containsKey(name))
            return passengerPool.get(name);
        
        Passenger passenger = new Passenger(name);
        passengerPool.put(name, passenger);
        return passenger;
    }

    public void setJourney(List<String> journey) {
        for (String station : journey) 
            path.add(Station.make(station));
    }

    public Station nextStop() {
        return path.peek();
    }

    public boolean isFinished() {
        return path.isEmpty();
    }
}
