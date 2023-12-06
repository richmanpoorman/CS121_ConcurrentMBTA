import java.util.concurrent.locks.Lock;
import java.lang.Exception;
import java.lang.RuntimeException;

public class PassengerThread extends Thread {
    Passenger passenger;
    MBTA      mbta;
    Log       log;

    public PassengerThread(Passenger passenger, MBTA mbta, Log log) {
        this.passenger = passenger;
        this.mbta      = mbta;
        this.log       = log;
    }

    public void run() {
        
        while (!mbta.isPassengerFinished(passenger)) {
            
            // Waiting for train to board //
            Station currStation = mbta.passengerAtStation(passenger);

            Object currStationLock = mbta.getStationLock(currStation);

            synchronized(currStationLock) { // currStationLock) {
                // Wait until it can board the train
                Train boardTrain = mbta.boardTrain(passenger);
                while (boardTrain == null) { 
                    waitFor(currStationLock);
                    boardTrain = mbta.boardTrain(passenger);
                }
                log.passenger_boards(passenger, boardTrain, currStation);

                currStationLock.notifyAll();
            }

            // Getting off of the train // 
            Station nextStation = mbta.nextDestination(passenger);
            Train   onTrain     = mbta.passengerOnTrain(passenger);
            Object  nextStationLock = mbta.getStationLock(nextStation);
            
            synchronized(nextStationLock) { // nextStationLock) {
                // Wait until it can board the train
                Station deboardStation = mbta.deboardTrain(passenger);
                while (deboardStation == null) { 
                    waitFor(nextStationLock);
                    deboardStation = mbta.deboardTrain(passenger);
                }

                log.passenger_deboards(passenger, onTrain, nextStation);

                nextStationLock.notifyAll();
            }
        }
    }

    private void waitFor(Object lock) {
        try {
            lock.wait();
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
    }
}
