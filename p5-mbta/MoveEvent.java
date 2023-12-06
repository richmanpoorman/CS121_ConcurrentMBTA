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
        if (t == null) throw new RuntimeException("The given train was null!");
        if (s1 == null) throw new RuntimeException("The given start station was null!");
        if (s2 == null) throw new RuntimeException("The given end station was null!");

        Station currentStation = mbta.trainAt(t);

        if (currentStation == null) throw new RuntimeException("The train " + t + " does not start at station " + s1 + " going to station " + s2);
        if (currentStation != s1) throw new RuntimeException("The train " + t + " starts at station " + currentStation + " not station " + s1 + " going to station " + s2);

        Station nextStation = mbta.moveTrain(t);

        if (nextStation == null) throw new RuntimeException("The train " + t + " could not go to station " + s2 + " from station " + s1);
        if (nextStation != s2) throw new RuntimeException("The train " + t + " went to station " + nextStation + " instead of station " + s2 + " from station " + s1);
        
    }
}
