import java.util.*;

import java.lang.RuntimeException;

public class MoveEvent implements Event {
    public final Train t; public final Station s1, s2;
    public MoveEvent(Train t, Station s1, Station s2) {
        this.t = t; this.s1 = s1; this.s2 = s2;
    }
    public boolean equals(Object o) {
        if (o instanceof MoveEvent e) {
        return t.equals(e.t) && s1.equals(e.s1) && s2.equals(e.s2);
        }
        return false;
    }
    public int hashCode() {
        return Objects.hash(t, s1, s2);
    }
    public String toString() {
        return "Train " + t + " moves from " + s1 + " to " + s2;
    }
    public List<String> toStringList() {
        return List.of(t.toString(), s1.toString(), s2.toString());
    }

    // Checks if the move event can actually occur on this MBTA
    public void replayAndCheck(MBTA mbta) {
        Station currentStation = mbta.trainAt(t);
        if (currentStation != s1) 
            throw new RuntimeException("The train " + 
                                        (t == null ? "null" : t.toString()) + 
                                        " was at station " + 
                                        (currentStation == null ? "null" : currentStation.toString()) + 
                                        " and not the correct station " + 
                                        (s1 == null ? "null" : s1.toString())
                                      );
        Station nextStation = mbta.moveTrain(t);
        if (nextStation != s2) 
            throw new RuntimeException("The train " + 
                                        (t == null ? "null" : t.toString()) + 
                                        " has next station " + 
                                        (nextStation == null ? "null" : nextStation.toString()) + 
                                        " and was not the expected station " + 
                                        (s2 == null ? "null" : s2.toString())
                                      );
    }
}
