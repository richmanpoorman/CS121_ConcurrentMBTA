import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.lang.RuntimeException;

public class Train extends Entity {
    // The Cache for trains
    private static Map<String, Train> trainPool = new HashMap<String, Train>();

    // Station List
    private List<Station> stops = new ArrayList<Station>();
    // Keeps track of which station to go to
    private int     trainPosition = 0;
    private boolean isForward = true;

    private Train(String name) { super(name); }

    public static Train make(String name) {
        if (trainPool.containsKey(name))
            return trainPool.get(name);

        Train train = new Train(name);
        trainPool.put(name, train);
        return train;
    }

    // ADD FUNCTIONS //
    public void setStations(List<String> stations) {
        if (stations.empty()) throw new RuntimeException();
        this.stops = new ArrayList<Station>();
        for (String station : stations) 
            stops.add(Station.make(station));
        
        trainPosition = 0;
        isForward = true;
    }

    public boolean canGetTo(Station goTo) {
        return stops.contains(goTo);
    }

    public Station goToNextStation() {
        trainPosition += isForward ? 1 : -1;
        if (trainPosition < 0) {
            trainPosition = 1;
            isFoward = true;
        }

        if (trainPosition >= stops.size()) {
            trainPosition = stops.size() - 1;
            isFoward = false;
        }

        return stops.get(trainPosition);
    }
}
