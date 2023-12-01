import java.util.*;
import java.io.Reader;
import java.io.FileReader;
import java.lang.Exception;
import java.lang.RuntimeException;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

public class MBTA {
    // Keeps track of each of the passengers
    private Map<Train, List<Station>>      trainLines        = new HashMap<Train, List<Station>>();
    private Map<Train, Integer>            trainLocations    = new HashMap<Train, Integer>();
    private Map<Train, List<Passenger>>    onTrainPassengers = new HashMap<Train, List<Passenger>>();

    private Map<Station, List<Passenger>>  stationPassengers = new HashMap<Station, List<Passenger>>();
    private Map<Passenger, Queue<Station>> boardingPlans     = new HashMap<Passenger, Queue<Station>>();
    
    // Creates an initially empty simulation
    public MBTA() { }

    // Adds a new transit line with given name and stations
    public void addLine(String name, List<String> stations) {
        Train train = Train.make(name);
        
        // Contains both the forwards and backwards part, 
        // so only need to increment in one direction
        // aka no need to keep track of the running direction
        List<Station> stops = new LinkedList<Station>();
        // Puts the "forwards" part of the path 
        for (String station : stations) {
            Station stop = Station.make(station);
            stops.add(stop);
            if (!this.stationPassengers.containsKey(stop))
                this.stationPassengers.put(stop, new LinkedList<Passenger>());
        }
            
        
        // Puts the "backwards" part of the path
        for (int i = stations.size() - 2; i > 0; i--)
            stops.add(Station.make(stations.get(i)));
        
        this.trainLines.put(train, stops);
        this.trainLocations.put(train, 0);
        this.onTrainPassengers.put(train, new LinkedList<Passenger>());
    }

    // Adds a new planned journey to the simulation
    public void addJourney(String name, List<String> stations) {
        Passenger passenger = Passenger.make(name);

        Queue<Station> stops = new LinkedList<Station>();
        // Puts the "forwards" part of the path 
        for (String station : stations) {
            Station stop = Station.make(station);
            stops.add(stop);
            if (!this.stationPassengers.containsKey(stop))
                this.stationPassengers.put(stop, new LinkedList<Passenger>());
        }

        Station start = stops.remove();

        this.stationPassengers.get(start).add(passenger);
        this.boardingPlans.put(passenger, stops);
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
        trainLines.clear();
        trainLocations.clear();
        stationPassengers.clear();
        onTrainPassengers.clear();
        boardingPlans.clear();
    }

    // adds simulation configuration from a file
    public void loadConfig(String filename) {
        Map<String, Map<String, List<String>>> jsonData = new HashMap<String, Map<String, List<String>>>();
        try {
            Reader jsonFile   = new FileReader(filename);
            Gson   jsonReader = new Gson();

            TypeToken<Map<String, Map<String, List<String>>>> mapType = new TypeToken<Map<String, Map<String, List<String>>>>() {};
            jsonData = jsonReader.fromJson(jsonFile, mapType);
        } catch (Exception e) {
            throw new RuntimeException(e.getCause());
        }

        if (!jsonData.containsKey("lines") || !jsonData.containsKey("trips"))
            throw new RuntimeException("The JSON is not in the right format!");
        
        Map<String, List<String>> lines = jsonData.get("lines");
        for (String line : lines.keySet()) 
            this.addLine(line, lines.get(line));

        Map<String, List<String>> trips = jsonData.get("trips");
        for (String passenger : trips.keySet()) 
            this.addJourney(passenger, trips.get(passenger));
    }
}
