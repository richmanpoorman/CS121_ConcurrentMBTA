import java.util.Map;
import java.util.HashMap;

public class Station extends Entity {
    private static Map<String, Station> stationPool = new HashMap<String, Station>();

    private Station(String name) { super(name); }

    public static Station make(String name) {
        if (stationPool.containsKey(name))
            return stationPool.get(name);
        
        Station station = new Station(name);
        stationPool.put(name, station);
        return station;
    }
}
