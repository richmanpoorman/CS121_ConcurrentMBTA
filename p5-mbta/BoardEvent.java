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
        Station boardStation = mbta.passengerAtStation(p);
        if (boardStation != s)
            throw new RuntimeException("The passenger " + 
                                        (p == null ? "null" : p.toString()) + 
                                        " was not at the station " + 
                                        (boardStation == null ? "null" : boardStation.toString()) + 
                                        " and not the expected station " + 
                                        (s == null ? "null" : s.toString())
                                       );
        Train boardedTrain = mbta.boardTrain(p);
        if (boardedTrain != t)
            throw new RuntimeException("The passenger " + 
                                        (p == null ? "null" : p.toString()) + 
                                        " boarded the train " + 
                                        (boardedTrain == null ? "null" : boardedTrain.toString()) + 
                                        " did not board the expected train " + 
                                        (t == null ? "null" : t.toString())
                                      );
    }
}
