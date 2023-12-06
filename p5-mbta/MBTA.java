import java.util.*;
import java.io.Reader;
import java.io.FileReader;
import java.lang.Exception;
import java.lang.RuntimeException;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MBTA {
    // Keeps track of where the trains can go, where they are, and who they have
    private Map<Train, List<Station>>      trainLines        = new HashMap<Train, List<Station>>();
    private Map<Train, Integer>            trainLocations    = new HashMap<Train, Integer>();
    private Map<Train, List<Passenger>>    onTrainPassengers = new HashMap<Train, List<Passenger>>();

    // Keeps track of what passengers and trains are at each station
    private Map<Station, List<Passenger>>  stationPassengers = new HashMap<Station, List<Passenger>>();
    private Map<Station, Train>            stationTrains     = new HashMap<Station, Train>();

    // Keeps track of what the next station to go to is and where the passenger is
    private Map<Passenger, Queue<Station>> boardingPlans     = new HashMap<Passenger, Queue<Station>>();
    private Map<Passenger, Entity>         passengerLocation = new HashMap<Passenger, Entity>();

    private Map<Station, Lock>             stationLocks      = new HashMap<Station, Lock>();
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
            
            if (!this.stationLocks.containsKey(stop))
                this.stationLocks.put(stop, new ReentrantLock());
        }
            
        
        // Puts the "backwards" part of the path
        for (int i = stations.size() - 2; i > 0; i--)
            stops.add(Station.make(stations.get(i)));
        
        this.trainLines.put(train, stops);
        this.trainLocations.put(train, 0);
        this.onTrainPassengers.put(train, new LinkedList<Passenger>());
        
        this.stationTrains.put(Station.make(stations.get(0)), train); // Put the train at the start
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

            if (!this.stationLocks.containsKey(stop))
                this.stationLocks.put(stop, new ReentrantLock());
        }

        Station start = stops.remove();

        this.stationPassengers.get(start).add(passenger);

        this.boardingPlans.put(passenger, stops);
        this.passengerLocation.put(passenger, start);
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
        stationTrains.clear();
        passengerLocation.clear();
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

    // ADDED HELPER FUNCTIONS //

    /* 
     *  Purpose: Moves the train to the next station
     *  Params : (Train) train := The train to move in the MBTA simulation
     *  Return : (Station) Returns the new station that the train is at, or null if the train can't go to the next station
     */
    public Station moveTrain(Train train) {
            // NOTE: There is a bit of overhead, but put there for future use (if updated)
        int     nextStationIndex = trainNextIndex(train);
        Station nextStation      = trainNext(train);

        // If the next train is still there don't move
        if (!canMoveToNextStation(train)) return null; 

        // Leave the old station 
        Station oldStation = this.trainAt(train);
        stationTrains.put(oldStation, null); 

        // Move to the next station
        trainLocations.put(train, nextStationIndex);

        // Enter the new station
        stationTrains.put(nextStation, train);
        return nextStation;
    }

    /* 
     *  Purpose: Gets if the next station is open
     *  Params : (Train) train := The train to check the next station of
     *  Return : (boolean) Returns whether the next station is open
     */
    public boolean canMoveToNextStation(Train train) {
        return stationTrains.get(trainNext(train)) == null;
    }

    /* 
     *  Purpose: Gets the current station of the train
     *  Params : (Train) train := The train to check the station of
     *  Return : (Station) Returns the station that the train is at
     */
    public Station trainAt(Train train) {
        return trainLines.get(train).get(trainLocations.get(train));
    }
    
    /* 
     *  Purpose: Gets the next station of the train
     *  Params : (Train) train := The train to check the station of
     *  Return : (Station) Returns the next station the train will go to
     */
    public Station trainNext(Train train) {
        return trainLines.get(train).get(trainNextIndex(train));
    }

    /* 
     *  Purpose: Gets the next station index of the train
     *  Params : (Train) train := The train to check the station of
     *  Return : (int) Returns the next station index that the train will go to
     */
    private int trainNextIndex(Train train) {
        return (trainLocations.get(train) + 1) % trainLines.get(train).size();
    }

    /* 
     *  Purpose: Takes a single passenger and boards the train
     *  Params : (Passenger) passenger := The passenger waiting at the station to board the train there
     *  Return : (Train) Returns the train the passenger got on, or null if there is no train to board
     *  Notes  : Will throw an error if the passenger is not currently at a station
     */
    public Train boardTrain(Passenger passenger) {
        
        Station station = passengerAtStation(passenger);
        // Not at a station, so something went wrong
        if (station == null) throw new RuntimeException("The passenger was not at a station");
        Train train = stationTrains.get(station);

        boolean canBoard = boardTrainAtStation(passenger, train, station);
        if (!canBoard) return null;
        return train;
    }

    /* 
     *  Purpose: Takes a single passenger and deboards the train
     *  Params : (Passenger) passenger := The passenger on the station
     *           (Train)     train     := The train the passenger is getting on
     *           (Station)   station   := The station the passenger is boarding at
     *  Return : (boolean) Returns whether the passenger can get on the train at the station or not
     *  Notes  : Will throw an error if the passenger is not at a station
     */
    public boolean boardTrainAtStation(Passenger passenger, Train train, Station station) {
        if (passenger == null || train == null || station == null) return false;
        
        // Only board if it can board the train
        if (!canBoardTrain(passenger, train)) return false;

        // Add passenger to list of people on the train
        onTrainPassengers.get(train).add(passenger);
        // Remove passenger from list of people at the station
        stationPassengers.get(station).remove(passenger);
        // Put the location of the passenger to be the train
        passengerLocation.put(passenger, train);
        
        return true;
    }

    /* 
     *  Purpose: Takes a single passenger and deboards the train
     *  Params : (Passenger) passenger := The passenger on the train at the station
     *  Return : (Station) Returns the station the passenger got off at
     *  Notes  : Will throw an error if the passenger is not on a train
     */
    public Station deboardTrain(Passenger passenger) {
        
        Train train = passengerOnTrain(passenger);
        if (train == null) throw new RuntimeException("The passenger was not on a train");
        Station station = trainAt(train);

        boolean canDeboard = deboardTrainFromStation(passenger, train, station);
        if (!canDeboard) return null;
        return station;
    }

    /* 
     *  Purpose: Takes a single passenger and deboards the train
     *  Params : (Passenger) passenger := The passenger on the train
     *           (Train)     train     := The train the passenger is on
     *           (Station)   station   := The station the passenger is deboarding to
     *  Return : (boolean) Returns whether the passenger can get off the train at the station or not
     *  Notes  : Will throw an error if the passenger is not on a train
     */
    public boolean deboardTrainFromStation(Passenger passenger, Train train, Station station) {
        if (passenger == null || train == null || station == null) return false;

         // Only deboards if the station is the next destination of the passenger
        if (station != nextDestination(passenger)) return false;
        boardingPlans.get(passenger).remove();

        // If the passenger is done, remove from the simulation
        if (boardingPlans.get(passenger).isEmpty()) {
            boardingPlans.remove(passenger);
            return true;
        }

        // Add passenger to list of people at the station
        stationPassengers.get(station).add(passenger);
        // Remove passenger from list of people on the train
        onTrainPassengers.get(train).remove(passenger);
        // Put the location of the passenger to be the station
        passengerLocation.put(passenger, station);
        return true;
    }

    /* 
     *  Purpose: The Station that the passenger is at
     *  Params : (Passenger) passenger := The passenger waiting at the station
     *  Return : (Station) Returns the station that the passenger is at, or null if the passenger is not at a station
     */
    public Station passengerAtStation(Passenger passenger) {
        if (passengerLocation.get(passenger) instanceof Station station) 
            return station;
        return null;
    }

    /* 
     *  Purpose: The Train that the passenger is on
     *  Params : (Passenger) passenger := The passenger on the train
     *  Return : (Station) Returns the tran that the passenger is on, or null if the passenger is not on a train
     */
    public Train passengerOnTrain(Passenger passenger) {
        if (passengerLocation.get(passenger) instanceof Train train) 
            return train;
        return null;
    }

    /* 
     *  Purpose: Checks what the next station the passenger is trying to go to
     *  Params : (Passenger) passenger := The passenger with the destination to investigate
     *  Return : (Station) The next station the train is trying to go to
     */
    public Station nextDestination(Passenger passenger) {
        return boardingPlans.get(passenger).peek();
    }

    /* 
     *  Purpose: Checks if the train will take the passenger to the next station on their trip
     *  Params : (Passenger) passenger := The passenger who is trying to the next station
     *           (Train)     train     := The train to check if they should board
     *  Return : (Station) The next station the train is trying to go to, or null if the train is null
     */
    public boolean canBoardTrain(Passenger passenger, Train train) {
        if (train == null) return false;
        return trainLines.get(train).contains(nextDestination(passenger));
    }

    /* 
     *  Purpose: Gets the list of all the passengers in the MBTA system
     *  Params : None 
     *  Return : (List<Passenger>) A copy of the list of passengers
     */
    public List<Passenger> passengers() {
        return new ArrayList<Passenger>(boardingPlans.keySet());
    }

    /* 
     *  Purpose: Gets the list of all the stations in the MBTA system
     *  Params : None 
     *  Return : (List<Station>) A copy of the list of stations
     */
    public List<Station> stations() {
        return new ArrayList<Station>(stationTrains.keySet());
    }

    /* 
     *  Purpose: Gets the list of all the trains in the MBTA system
     *  Params : None 
     *  Return : (List<Train>) A copy of the list of trains
     */
    public List<Train> trains() {
        return new ArrayList<Train>(trainLines.keySet());
    }

    /* 
     *  Purpose: Gets if the given passenger has finished their trip on the MBTA
     *  Params : (Passenger) passenger := The passenger to check if still riding
     *  Return : (boolean) Whether the passenger is finished riding (defaults to true if the passenger was not in the system)
     */
    public boolean isPassengerFinished(Passenger passenger) {
        return !boardingPlans.containsKey(passenger);
    }

    /* 
     *  Purpose: Gets if the sim is finished running
     *  Params : None
     *  Return : (boolean) Whether all of the passengers have finished their trips or not
     */
    public boolean isSimFinished() {
        return boardingPlans.isEmpty();
    }

    /* 
     *  Purpose: Gets the lock of the given station
     *  Params : (Station) station := The station to get the corresponding lock of
     *  Return : (Lock) The lock corresponding to the given lock in the MBTA
     */
    public Object getStationLock(Station station) {
        return stationLocks.get(station);
    }
}