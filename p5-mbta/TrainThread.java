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

            Object nextStationLock = mbta.getStationLock(nextStation);
            Object currStationLock = mbta.getStationLock(currStation);
            synchronized(nextStationLock) { // nextStationLock) {
                while (!mbta.canMoveToNextStation(train)) {
                    if (mbta.isSimFinished()) return;
                        // System.out.println(train + " could not move to " + nextStation + " from " + currStation);
                        waitFor(nextStationLock);
                }
                synchronized(currStationLock) {
                    // Move to the next station when open
                    mbta.moveTrain(train);
                    
                    // Then, log moving to the next station
                    log.train_moves(train, currStation, nextStation);
                    
                    // Then alert the arrived at station that there was a change
                    // nextStationLock.notifyAll();
                    nextStationLock.notifyAll();

                    // Let the current station know that they can actually come in
                    currStationLock.notifyAll();

                    
                }
            }
            
            // Sleep for 10 ms
            sleepFor(TIME);
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
