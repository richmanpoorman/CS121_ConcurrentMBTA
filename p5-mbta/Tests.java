import static org.junit.Assert.*;
import org.junit.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class Tests {
    @After public void putSpaceAfter() {
        System.out.println();
    }
    @Test public void testTransferingLines() {
        assertTrue("true should be true", true);
    }
    // EDGE CASE TESTS
    @Test 
    public void getOnSecondStop() {
        MBTA    mbta = new MBTA();
        Train   line = Train.make("line");
        Station s1   = Station.make("1");
        Station s2   = Station.make("2");
        Station s3   = Station.make("3");
        Station s4   = Station.make("4");

        List<String> lineStations = Arrays.asList("1", "2", "3", "4");

        Passenger p = Passenger.make("test");
        
        List<String> passengerPath = Arrays.asList("2", "3", "1");
        
        mbta.addLine("line", lineStations);
        mbta.addJourney("test", passengerPath);

        Log log = new Log();
        log.train_moves(line, s1, s2);       // Train goes from s1 to s2
        log.passenger_boards(p, line, s2);   //     p gets ON at s2
        log.train_moves(line, s2, s3);       // Train goes from s2 to s3
        log.passenger_deboards(p, line, s3); //     p gets OFF at s3
        log.train_moves(line, s3, s4);       // Train goes from s3 to s4
        log.train_moves(line, s4, s3);       // Train goes from s4 to s3
        log.passenger_boards(p, line, s3);   //     p gets ON at s3
        log.train_moves(line, s3, s2);       // Train goes from s3 to s2
        log.train_moves(line, s2, s1);       // Train goes from s2 to s1
        log.passenger_deboards(p, line, s1); //     p gets OFF at s1
        log.train_moves(line, s1, s2);       // Train goes from s1 to s2
        log.train_moves(line, s2, s3);       // Train goes from s2 to s3

        Verify.verify(mbta, log);
    }

    @Test 
    public void parallelMBTA() {
        List<MBTA> mbtaLines = new ArrayList<MBTA>();
        
        int numParallel = 100;
        for (int i = 0; i < numParallel; i++) mbtaLines.add(new MBTA());

        Train   line = Train.make("line");
        Station s1   = Station.make("1");
        Station s2   = Station.make("2");
        Station s3   = Station.make("3");
        Station s4   = Station.make("4");

        List<String> lineStations = Arrays.asList("1", "2", "3", "4");

        Passenger p = Passenger.make("test");
        
        List<String> passengerPath = Arrays.asList("4", "1");

        for (MBTA mbta : mbtaLines) mbta.addLine("line", lineStations);
        for (MBTA mbta : mbtaLines) mbta.addJourney("test", passengerPath);

        List<Log> loggers = new ArrayList<Log>();
        for (int i = 0; i < numParallel; i++) loggers.add(new Log());

        // Test //
        for (Log log : loggers) log.train_moves(line, s1, s2);
        for (Log log : loggers) log.train_moves(line, s2, s3);
        for (Log log : loggers) log.train_moves(line, s3, s4);
        
        for (Log log : loggers) log.passenger_boards(p, line, s4);

        for (Log log : loggers) log.train_moves(line, s4, s3);
        for (Log log : loggers) log.train_moves(line, s3, s2);
        for (Log log : loggers) log.train_moves(line, s2, s1);

        for (Log log : loggers) log.passenger_deboards(p, line, s1);
        
            // AFTER TRIP
        for (Log log : loggers) log.train_moves(line, s1, s2);
        for (Log log : loggers) log.train_moves(line, s2, s3);
        for (Log log : loggers) log.train_moves(line, s3, s4);
        for (Log log : loggers) log.train_moves(line, s4, s3);
        for (Log log : loggers) log.train_moves(line, s3, s2);
        for (Log log : loggers) log.train_moves(line, s2, s1);

        for (int i = 0; i < numParallel; i++) Verify.verify(mbtaLines.get(i), loggers.get(i));
    }
    
    // GENERAL TESTS //
    @Test 
    public void test1() {
        MBTA mbta = new MBTA();
        Train rLine = Train.make("red");
        Station davis = Station.make("Davis");
        Station harvard = Station.make("Harvard");
        Station kendall = Station.make("Kendall");

        Passenger bob = Passenger.make("Bob");
        
        mbta.addLine("red", List.of("Davis", "Harvard", "Kendall"));
        mbta.addJourney("Bob", List.of("Davis", "Kendall"));
        
        Log log = new Log();
        
        log.passenger_boards(bob, rLine, davis);
        log.train_moves(rLine, davis, harvard);
        
        Verify.verify(mbta, log);
    }
    @Test
    public void completeTrip() {
        MBTA mbta = new MBTA();
        Log log = new Log();
    
        Station tufts = Station.make("Tufts");
        Station ball = Station.make("Ball");
        Station magoun = Station.make("Magoun");
        Station gilman =  Station.make("Gilman");
        Passenger me = Passenger.make("Ian");
        Train greenLine = Train.make("green");
    
        mbta.addLine("green", List.of("Tufts", "Ball", "Magoun", "Gilman"));
        mbta.addJourney("Ian", List.of("Tufts", "Ball", "Magoun"));
    
        // ok
        log.passenger_boards(me, greenLine, tufts);
        // ok
        log.train_moves(greenLine, tufts, ball);
        log.passenger_deboards(me, greenLine, ball);
        log.passenger_boards(me, greenLine, ball);
        // ok
        log.train_moves(greenLine, ball, magoun);
        
        log.passenger_deboards(me, greenLine, magoun);
        Verify.verify(mbta, log);

        MBTA mbta2 = new MBTA();
        Log  log2  = new Log();
        mbta2.addLine("green", List.of("Tufts", "Ball", "Magoun", "Gilman"));
        mbta2.addJourney("Ian", List.of("Tufts", "Ball", "Magoun"));
        // n/a
        log2.train_moves(greenLine, tufts, gilman);
        // n/a
        log2.passenger_deboards(me, greenLine, gilman);
        
        try {
            Verify.verify(mbta2, log2);
            assertTrue("This should have failed", false);
        } catch (Exception e) { }
        
    }

    @Test 
    public void testOrange() {
        MBTA mbta = new MBTA();
        Train orangeLine = Train.make("Orange");
        Station forest_hills = Station.make("Forest Hills");
        Station green_st = Station.make("Green Street");
        Station stony_brook = Station.make("Stony Brook");
        Station jackson_sq = Station.make("Jackson Square");
        Station roxbury_xing = Station.make("Roxbury Crossing");

        Passenger john_doe = Passenger.make("jd1");
        
        mbta.addLine("Orange", 
            List.of("Forest Hills", "Green Street", "Stony Brook", 
            "Jackson Square", "Roxbury Crossing"));
        mbta.addJourney("jd1", List.of("Forest Hills", "Jackson Square"));
        
        Log log = new Log();
        
        log.passenger_boards(john_doe, orangeLine, forest_hills);
        log.train_moves(orangeLine, forest_hills, green_st);
        
        Verify.verify(mbta, log);
    }

    @Test 
    public void test2() {
        MBTA mbta = new MBTA();
        Train gLine = Train.make("green");
        Station s1 = Station.make("s1");
        Station s2 = Station.make("s2");
        Station s3 = Station.make("s3");
        Station s4 = Station.make("s4");
        Passenger p1 = Passenger.make("p1");
        mbta.addLine("green", List.of("s1", "s2", "s3", "s4"));
        mbta.addJourney("p1", List.of("s1", "s3"));
        Log log = new Log(List.of(new BoardEvent(p1, gLine, s1), 
                                new MoveEvent(gLine, s1, s2),
                                new MoveEvent(gLine, s2, s3),
                                new DeboardEvent(p1, gLine, s3),
                                new MoveEvent(gLine, s3, s4)));
        Verify.verify(mbta, log);
    }

    @Test public void verifyTransfer() {
        // Set up //
        MBTA mbta = new MBTA();
        
        List<String> lineA = Arrays.asList("1", "2", "3");
        List<String> lineB = Arrays.asList("0", "1", "3", "5");
        List<String> lineC = Arrays.asList("5", "4");

        List<String> riderA = Arrays.asList("1", "5", "4");
        List<String> riderB = Arrays.asList("2", "1", "5", "4");
        List<String> riderC = Arrays.asList("5", "3", "1", "2");

        Station s0 = Station.make("0");
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
        logger.train_moves(trainA, s1, s2);       // The trainA goes from "1" to "2"
        logger.train_moves(trainB, s0, s1);       // The trainB goes from "0" to "1"
        logger.passenger_boards(a, trainB, s1);   // "a" gets on the trainB at "1"
        logger.train_moves(trainB, s1, s3);       // The trainB goes from "1" to "3"
        logger.train_moves(trainC, s5, s4);       // The trainC goes from "5" to "4"
        logger.train_moves(trainB, s3, s5);       // The trainB goes from "3" to "5"
        logger.train_moves(trainA, s2, s3);       // The trainA goes from "2" to "3"
        logger.train_moves(trainA, s3, s2);       // The trainA goes from "3" to "2"
        logger.passenger_deboards(a, trainB, s5); // "a" gets off of the trainB at "5"
        logger.passenger_boards(c, trainB, s5);   // "c" gets on the trainB at "5"
        logger.passenger_boards(b, trainA, s2);   // "b" gets on the trainA at "2"
        logger.train_moves(trainA, s2, s1);       // The trainA goes from "2" to "1"
        logger.passenger_deboards(b, trainA, s1); // "b" gets oof of the trainA at "1"
        logger.train_moves(trainB, s5, s3);       // The trainB goes from "5" to "3"
        logger.train_moves(trainC, s4, s5);       // The trainC goes from "4" to "5"
        logger.passenger_boards(a, trainC, s5);   // "a" gets on the trainC at "5"
        logger.passenger_deboards(c, trainB, s3); // "c" gets off of the trainB at "3"
        logger.passenger_boards(c, trainB, s3);   // "c" gets on the trainB at "3"
        logger.train_moves(trainA, s1, s2);       // The trainA goes from "1" to "2"
        logger.train_moves(trainB, s3, s1);       // The trainB goes from "3" to "1"
        logger.train_moves(trainA, s2, s3);       // The trainA goes from "2" to "3"
        logger.train_moves(trainA, s3, s2);       // The trainA goes from "3" to "2"
        logger.passenger_deboards(c, trainB, s1); // "c" gets off of the trainB at "1"
        logger.passenger_boards(b, trainB, s1);   // "b" gets on the the trainB at "1"
        logger.train_moves(trainB, s1, s0);       // The trainB goes from "1" to "0"
        logger.train_moves(trainB, s0, s1);       // The trainB goes from "0" to "1"
        logger.train_moves(trainB, s1, s3);       // The trainB goes from "1" to "3"
        logger.train_moves(trainA, s2, s1);       // The trainA goes from "2" to "1"
        logger.passenger_boards(c, trainA, s1);   // "c" gets on the trainA at "1"
        logger.train_moves(trainA, s1, s2);       // The trainA goes from "1" to "2"
        logger.train_moves(trainC, s5, s4);       // The trainC goes from "5" to "4"
        logger.train_moves(trainB, s3, s5);       // The trainB goes from "3" to "5"
        logger.passenger_deboards(b, trainB, s5); // "b" gets off of the trainB at "5"
        logger.train_moves(trainB, s5, s3);       // The trainB goes from "5" to "3"
        logger.passenger_deboards(c, trainA, s2); // "c" gets off of the trainA at "2" (DONE)
        logger.train_moves(trainC, s4, s5);       // The trainC goes from "4" to "5"
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
    
    @Test 
    public void testVerificationTrip1() {
      MBTA mbta = new MBTA();
      Passenger jeff = Passenger.make("Jeff");
      Station newyorkPenn = Station.make("New York");
      Station eastOrange = Station.make("East Orange");
      Station brickChurch = Station.make("Brick Church");
      Station southOrange = Station.make("South Orange");
      Station maplewood = Station.make("Maplewood");
      Train morrisEssex = Train.make("Morris Essex");

      List<String> morrisessexline = new ArrayList<>();
      morrisessexline.add("New York");
      morrisessexline.add("East Orange");
      morrisessexline.add("Brick Church");
      morrisessexline.add("South Orange");
      morrisessexline.add("Maplewood");

      List<String> jeffJourney = new ArrayList<>();
      jeffJourney.add("East Orange");
      jeffJourney.add("Maplewood");

      mbta.addLine("Morris Essex", morrisessexline);
      mbta.addJourney("Jeff", jeffJourney);

      List<Event> events = new ArrayList<>();
      events.add(new MoveEvent(morrisEssex, newyorkPenn, eastOrange));
      events.add(new BoardEvent(jeff, morrisEssex, eastOrange));
      events.add(new MoveEvent(morrisEssex, eastOrange, brickChurch));
      events.add(new MoveEvent(morrisEssex, brickChurch, southOrange));
      events.add(new MoveEvent(morrisEssex, southOrange, maplewood));
      events.add(new DeboardEvent(jeff, morrisEssex, maplewood));

      Log e = new Log(events);

      Verify.verify(mbta, e);
    }

    @Test 
    public void testBasicSim() {
        MBTA mbta = new MBTA();

        List<String> line = Arrays.asList("1", "2", "3");
        mbta.addLine("line", line);

        List<String> path1 = Arrays.asList("1", "3");
        List<String> path2 = Arrays.asList("3", "1");
        mbta.addJourney("A", path1);
        mbta.addJourney("B", path2);

        Log log = new Log();

        Sim.run_sim(mbta, log);

        mbta.reset();
        mbta.addLine("line", line);
        mbta.addJourney("A", path1);
        mbta.addJourney("B", path2);
        Verify.verify(mbta, log);
    }

    @Test
    public void sharedEndStation() {
        List<String> line1 = Arrays.asList("1", "2");
        List<String> line2 = Arrays.asList("3", "2");

        List<String> path = Arrays.asList("1", "2", "3");

        MBTA mbta = new MBTA();
        mbta.addLine("Line 1", line1);
        mbta.addLine("Line 2", line2);
        mbta.addJourney("Path", path);

        Log log = new Log();
        Sim.run_sim(mbta, log);

        mbta.reset();
        mbta.addLine("Line 1", line1);
        mbta.addLine("Line 2", line2);
        mbta.addJourney("Path", path);

        Verify.verify(mbta, log);
    }
    /* 
    @Test 
    public void completeCircle() {

        List<String> line1 = Arrays.asList("1", "2", "3", "4", "5", "6");
        List<String> line2 = Arrays.asList("2", "3", "4", "5", "1", "7");
        List<String> line3 = Arrays.asList("3", "4", "5", "1", "2", "8");
        List<String> line4 = Arrays.asList("4", "5", "1", "2", "3", "9");

        List<String> path1 = Arrays.asList("1", "5");
        List<String> path2 = Arrays.asList("2", "1");
        List<String> path3 = Arrays.asList("3", "2");
        List<String> path4 = Arrays.asList("4", "3");

        MBTA mbta = new MBTA();
        mbta.addLine("Line 1", line1);
        mbta.addLine("Line 2", line2);
        mbta.addLine("Line 3", line3);
        mbta.addLine("Line 4", line4);

        mbta.addJourney("A", path1);
        mbta.addJourney("B", path2);
        mbta.addJourney("C", path3);
        mbta.addJourney("D", path4);

        Log log = new Log();
        Sim.run_sim(mbta, log);

        mbta.reset();
        mbta.addLine("Line 1", line1);
        mbta.addLine("Line 2", line2);
        mbta.addLine("Line 3", line3);
        mbta.addLine("Line 4", line4);

        mbta.addJourney("A", path1);
        mbta.addJourney("B", path2);
        mbta.addJourney("C", path3);
        mbta.addJourney("D", path4);
        Verify.verify(mbta, log);
    }
    */
}
