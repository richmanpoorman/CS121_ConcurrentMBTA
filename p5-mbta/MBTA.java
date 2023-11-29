import java.util.*;

public class MBTA {
    private Map<String, Train>     lineTrains = new HashMap<String, Train>();
    private Map<String, Passenger> passengers = new HashMap<String, Passenger>();
    
    // Creates an initially empty simulation
    public MBTA() { }

    // Adds a new transit line with given name and stations
    public void addLine(String name, List<String> stations) {
        Train train = Train.make(name);
        train.setStations(stations);
        lineTrains.put(name, train);
    }

    // Adds a new planned journey to the simulation
    public void addJourney(String name, List<String> stations) {
        Passenger passenger = Passenger.make(name);
        passenger.setJourney(stations);
        passengers.put(name, passenger);
    }

    // Return normally if initial simulation conditions are satisfied, otherwise
    // raises an exception
    public void checkStart() {
    }

    // Return normally if final simulation conditions are satisfied, otherwise
    // raises an exception
    public void checkEnd() {
    }

    // reset to an empty simulation
    public void reset() {
        lineTrains.clear();
        passengers.clear();
    }

    // adds simulation configuration from a file
    public void loadConfig(String filename) {
        throw new UnsupportedOperationException();
    }
}
