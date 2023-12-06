import java.util.concurrent.locks.Lock;
import java.lang.Exception;
import java.lang.RuntimeException;

public class TrainThread extends Thread {
    private static final int TIME = 10;
    private Train train;
    private MBTA  mbta;
    private Log   log;

    public TrainThread(Train train, MBTA mbta, Log log) {
        this.train = train;
        this.mbta  = mbta;
        this.log   = log;
    }

    public void run() {
        // Keeps running the train until everything is done
        
        while (!mbta.isSimFinished()) {
            Station currStation = mbta.trainAt(train);
            Station nextStation = mbta.trainNext(train);

            // Lock nextStationLock = mbta.getStationLock(nextStation);
            synchronized(nextStation) { // nextStationLock) {

                // Move to the next station when open
                while (mbta.moveTrain(train) == null) {
                    if (mbta.isSimFinished()) return;
                    System.out.println(train + " could not move to " + nextStation + " from " + currStation);
                    waitFor(nextStation);
                }
                
                // Then, log moving to the next station
                log.train_moves(train, currStation, nextStation);
                
                // Then alert the arrived at station that there was a change
                // nextStationLock.notifyAll();
                nextStation.notifyAll();

                // Sleep for 10 ms
                sleepFor(TIME);

                // Let the current station know that they can actually come in
                synchronized(currStation) {
                    currStation.notifyAll();
                }
            }
            
            
        }   
    }

    private void waitFor(Object lock) {
        try {
            lock.wait();
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
    }

    private void sleepFor(int time) {
        try {
            sleep(TIME);
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
    }
}
