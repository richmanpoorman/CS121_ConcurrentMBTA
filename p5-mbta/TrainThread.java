import java.util.concurrent.locks.Lock;

public class TrainThread extends Thread {
    Train train;
    MBTA  mbta;
    Log   log;

    public TrainThread(Train train, MBTA mbta, Log log) {
        this.train = train;
        this.mbta  = mbta;
        this.log   = log;
    }

    public void run() {
        // Keeps running the train until everything is donee
        while (!mbta.isSimFinished()) {
            Station currStation = mbta.trainAt(train);
            Station nextStation = mbta.trainNext(train);

            // Lock nextStationLock = mbta.getStationLock(nextStation);

            synchronized(nextStation) { // nextStationLock) {
                // Move to the next station when open
                while (mbta.moveTrain(train) == null) {
                    try {
                        // nextStationLock.wait();
                        nextStation.wait();
                    } catch (Exception e) { }
                }

                // Then, log moving to the next station
                log.train_moves(train, currStation, nextStation);

                // Then alert the arrived at station that there was a change
                // nextStationLock.notifyAll();
                nextStation.notifyAll();

                // Sleep for 10 ms
                try {
                    sleep(10);
                } catch (Exception e) { }
                
            }
        }   
    }
}
