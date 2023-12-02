import java.util.Map;
import java.util.HashMap;

public class Passenger extends Entity {
    private static Map<String, Passenger> passengerPool = new HashMap<String, Passenger>();

    private Passenger(String name) { super(name); }

    public static Passenger make(String name) {
        if (passengerPool.containsKey(name))
            return passengerPool.get(name);
        
        Passenger passenger = new Passenger(name);
        passengerPool.put(name, passenger);
        return passenger;
    }
    

}
