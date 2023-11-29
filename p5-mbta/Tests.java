import static org.junit.Assert.*;
import org.junit.*;

public class Tests {
    @Test public void testPass() {
        assertTrue("true should be true", true);
    }

    @Test public void takeFromPool() {
        Train t1 = Train.make("A");
        Train t2 = Train.make("A");
        assertTrue("Same exact train 1", t1 == t2);
        Train t3 = Train.make("1");
        Train t4 = Train.make("1");
        assertTrue("Same exact train 2", t3 == t4);

        assertTrue("Different trains", t1 != t3);

        Passenger p1 = Passenger.make("B");
        Passenger p2 = Passenger.make("B");
        assertTrue("Same exact passenger 1", p1 == p2);
        Passenger p1 = Passenger.make("2");
        Passenger p2 = Passenger.make("2");
        assertTrue("Same exact passenger 2", p3 == p4);

        Station s1 = Station.make("C");
        Station s2 = Station.make("C");
        assertTrue("Same exact station", s1 == s2);

        
    }
}
