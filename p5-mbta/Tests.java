import static org.junit.Assert.*;
import org.junit.*;

import java.util.List;
import java.util.Arrays;

public class Tests {
    @Test public void testTransferingLines() {
        assertTrue("true should be true", true);
    }

    @Test public void verifyTransfer() {
        // Set up //
        MBTA mbta = new MBTA();
        
        List<String> lineA = Arrays.asList("1", "2", "3");
        List<String> lineB = Arrays.asList("1", "3", "5");
        List<String> lineC = Arrays.asList("5", "4");

        List<String> riderA = Arrays.asList("1", "5", "4");
        List<String> riderB = Arrays.asList("2", "1", "5", "4");
        List<String> riderC = Arrays.asList("5", "3", "1", "2");

        Station s1 = Station.make("1");
        Station s2 = Station.make("2");
        Station s3 = Station.make("3");
        Station s4 = Station.make("4");
        Station s5 = Station.make("5");

        Train trainA = Train.make("LineA");
        Train trainB = Train.make("LineB");
        Train trainC = Train.make("LineC");

        mbta.addLine("LineA", lineA);
        mbta.addLine("LineB", lineB);
        mbta.addLine("LineC", lineC);

        Passenger a = Passenger.make("A");
        Passenger b = Passenger.make("B");
        Passenger c = Passenger.make("C");

        mbta.addJourney("A", riderA);
        mbta.addJourney("B", riderB);
        mbta.addJourney("C", riderC);

        // Journey //
        Log logger = new Log();
        logger.passenger_boards(a, trainB, s1);   // "a" gets on the trainB at "1"
        logger.train_moves(trainB, s1, s3);       // The trainB goes from "1" to "3"
        logger.train_moves(trainA, s1, s2);       // The trainA goes from "1" to "2"
        logger.train_moves(trainA, s2, s3);       // The trainA goes from "2" to "3"
        logger.train_moves(trainA, s3, s2);       // The trainA goes from "3" to "2"
        logger.train_moves(trainB, s3, s5);       // The trainB goes from "3" to "5"
        logger.passenger_deboards(a, trainB, s5); // "a" gets off of the trainB at "5"
        logger.passenger_boards(c, trainB, s5);   // "c" gets on the trainB at "5"
        logger.passenger_boards(b, trainA, s2);   // "b" gets on the trainA at "2"
        logger.train_moves(trainA, s2, s1);       // The trainA goes from "2" to "1"
        logger.passenger_boards(a, trainC, s5);   // "a" getss on the trainC at "5"
        logger.train_moves(trainB, s5, s3);       // The trainB goes from "5" to "3"
        logger.passenger_deboards(c, trainB, s3); // "c" gets off of the trainB at "3"
        logger.passenger_boards(c, trainB, s3);   // "c" gets on the trainB at "3"
        logger.train_moves(trainB, s3, s1);       // The trainB goes from "3" to "1"
        logger.passenger_deboards(c, trainB, s1); // "c" gets off of the trainB at "1"
        logger.passenger_boards(b, trainB, s1);   // "b" gets on the the trainB at "1"
        logger.passenger_boards(c, trainA, s1);   // "c" gets on the trainA at "1"
        logger.train_moves(trainB, s1, s3);       // The trainB goes from "1" to "3"
        logger.train_moves(trainA, s1, s2);       // The trainA goes from "1" to "2"
        logger.train_moves(trainB, s3, s5);       // The trainB goes from "3" to "5"
        logger.passenger_deboards(b, trainB, s5); // "b" gets off of the trainB at "5"
        logger.passenger_deboards(c, trainA, s2); // "c" gets off of the trainA at "2" (DONE)
        logger.passenger_boards(b, trainC, s5);   // "b" gets on the trainC at "5"
        logger.train_moves(trainC, s5, s4);       // The trainC goes from "5" to "4"
        logger.passenger_deboards(b, trainC, s4); // "b" gets off of the trainC at "4" (DONE)
        logger.passenger_deboards(a, trainC, s4); // "a" gets off of the trainC at "4" (DONE)
    
        Verify.verify(mbta, logger);
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
        Passenger p3 = Passenger.make("2");
        Passenger p4 = Passenger.make("2");
        assertTrue("Same exact passenger 2", p3 == p4);

        Station s1 = Station.make("C");
        Station s2 = Station.make("C");
        assertTrue("Same exact station", s1 == s2);

        
    }

    @Test public void testDuplicateLines() {
        MBTA mbta = new MBTA();

        List<String> lineA = Arrays.asList("1", "2", "4", "6");
        List<String> lineB = Arrays.asList("1", "3", "5");
        List<String> lineC = Arrays.asList("5", "7");
        List<String> lineD = Arrays.asList("1", "2", "3", "4");

        mbta.addLine("a", lineA);
        mbta.addLine("b", lineB);
        mbta.addLine("c", lineC); 
        mbta.addLine("d", lineD);

        List<String> lineBPassenger       = Arrays.asList("1", "5");                     // Only 1 path
        List<String> lineAOrDPassenger    = Arrays.asList("1", "4");                     // Multiple paths
        List<String> lineBToCPassenger    = Arrays.asList("1", "5", "7");                // Transfer between trains
        List<String> longPathPassenger    = Arrays.asList("7", "5", "3", "1", "4", "6"); // Multiple transfers with multiple path
        List<String> backToStartPassenger = Arrays.asList("4", "1");                     // Go in reverse to start

        mbta.addJourney("Line B Passenger"       , lineBPassenger);
        mbta.addJourney("Line A Or D Passenger"  , lineAOrDPassenger);
        mbta.addJourney("Line B To C Passenger"  , lineBToCPassenger);
        mbta.addJourney("Long Path Passenger"    , longPathPassenger);
        mbta.addJourney("Back To Start Passenger", backToStartPassenger);

        Log logger = new Log();
        Sim.run_sim(mbta, logger);
        Verify.verify(mbta, logger);
    }
}
