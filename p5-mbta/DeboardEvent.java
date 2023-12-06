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
        if (p == null) throw new RuntimeException("The given passenger was null!");
        if (t == null) throw new RuntimeException("The given train was null!");
        if (s == null) throw new RuntimeException("The given station was null!");

        Train deboardTrain = mbta.passengerOnTrain(p);

        if (deboardTrain == null) throw new RuntimeException("The passenger " + p + " could not deboard from train " + t + " at station " + s);
        if (deboardTrain != t) throw new RuntimeException("The passenger " + p + " deboarded from train " + deboardTrain + " instead of train " + t);

        Station deboardedStation = mbta.deboardTrain(p);

        if (deboardedStation == null) throw new RuntimeException("The passenger " + p + " failed to enter the station " + s + " from train " + t);
        if (deboardedStation != s) throw new RuntimeException("The passenger " + p + " entered the station " + deboardedStation + " instead of station " + s);
    }
}
