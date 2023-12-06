import java.util.*;
import java.lang.RuntimeException;

public class BoardEvent implements Event {
    public final Passenger p; public final Train t; public final Station s;
    public BoardEvent(Passenger p, Train t, Station s) {
        this.p = p; this.t = t; this.s = s;
    }
    public boolean equals(Object o) {
        if (o instanceof BoardEvent e) {
        return p.equals(e.p) && t.equals(e.t) && s.equals(e.s);
        }
        return false;
    }
    public int hashCode() {
        return Objects.hash(p, t, s);
    }
    public String toString() {
        return "Passenger " + p + " boards " + t + " at " + s;
    }
    public List<String> toStringList() {
        return List.of(p.toString(), t.toString(), s.toString());
    }
    public void replayAndCheck(MBTA mbta) {
        if (p == null) throw new RuntimeException("The given passenger was null!");
        if (t == null) throw new RuntimeException("The given train was null!");
        if (s == null) throw new RuntimeException("The given station was null!");

        Station boardStation = mbta.passengerAtStation(p);

        if (boardStation == null) throw new RuntimeException("The passenger " + p + " failed to exit the station " + s + " to train " + t);
        if (boardStation != s) throw new RuntimeException("The passenger " + p + " exited the station " + boardStation + " instead of station " + s);
        
        Train boardedTrain = mbta.boardTrain(p);

        if (boardedTrain == null) throw new RuntimeException("The passenger " + p + " could not board the train " + t + " at station " + s);
        if (boardedTrain != t) throw new RuntimeException("The passenger " + p + " boarded the train " + boardedTrain + " instead of the train " + t);
    }
}
