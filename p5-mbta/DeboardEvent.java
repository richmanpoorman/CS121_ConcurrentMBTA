import java.util.*;
import java.lang.RuntimeException;

public class DeboardEvent implements Event {
    public final Passenger p; public final Train t; public final Station s;
    public DeboardEvent(Passenger p, Train t, Station s) {
        this.p = p; this.t = t; this.s = s;
    }
    public boolean equals(Object o) {
        if (o instanceof DeboardEvent e) {
        return p.equals(e.p) && t.equals(e.t) && s.equals(e.s);
        }
        return false;
    }
    public int hashCode() {
        return Objects.hash(p, t, s);
    }
    public String toString() {
        return "Passenger " + p + " deboards " + t + " at " + s;
    }
    public List<String> toStringList() {
        return List.of(p.toString(), t.toString(), s.toString());
    }
    public void replayAndCheck(MBTA mbta) {
        Train deboardTrain = mbta.passengerOnTrain(p);
        if (deboardTrain != t)
            throw new RuntimeException("The passenger " + 
                                        (p == null ? "null" : p.toString()) + 
                                        " was on the train " + 
                                        (deboardTrain == null ? "null" : deboardTrain.toString()) + 
                                        " not on the expected train " + 
                                        (t == null ? "null" : t.toString())
                                      );
        Station deboardedStation = mbta.deboardTrain(p);
        if (deboardedStation != s)
            throw new RuntimeException("The passenger " + 
                                        (p == null ? "null" : p.toString()) + 
                                        " was at the station " + 
                                        (deboardedStation == null ? "null" : deboardedStation.toString()) + 
                                        " and did not enter the expected station " + 
                                        (s == null ? "null" : s.toString())
                                      );
    }
}
