import java.io.*;

import java.util.List;
import java.util.ArrayList;

public class Sim {

    public static void run_sim(MBTA mbta, Log log) {
        List<Passenger> passengers = mbta.passengers();
        List<Train>     trains     = mbta.trains();
        List<Station>   stations   = mbta.stations();

        List<PassengerThread> passengerThreads = new ArrayList<PassengerThread>();
        List<TrainThread>     trainThreads     = new ArrayList<TrainThread>();

        for (Passenger passenger : passengers) {
            PassengerThread thread = new PassengerThread(passenger, mbta, log);
            passengerThreads.add(thread);
            thread.start();
        }

        for (Train train : trains) {
            TrainThread thread = new TrainThread(train, mbta, log);
            trainThreads.add(thread);
            thread.start();
        }

        try {
            for (PassengerThread thread : passengerThreads) 
                thread.join();
            
            for (Station station : stations) {
                Object stationLock = mbta.getStationLock(station);
                synchronized(stationLock) {
                    stationLock.notifyAll();
                }
            }

            for (TrainThread thread : trainThreads)
                thread.join();
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); } 
        
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("usage: ./sim <config file>");
            System.exit(1);
        }

        MBTA mbta = new MBTA();
        mbta.loadConfig(args[0]);

        Log log = new Log();

        run_sim(mbta, log);

        String s = new LogJson(log).toJson();
        PrintWriter out = new PrintWriter("log.json");
        out.print(s);
        out.close();

        mbta.reset();
        mbta.loadConfig(args[0]);
        Verify.verify(mbta, log);
    }
}
