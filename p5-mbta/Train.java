import java.util.Map;
import java.util.HashMap;

public class Train extends Entity {
    private static Map<String, Train> trainPool = new HashMap<String, Train>();
    private Train(String name) { super(name); }

    public static Train make(String name) {
        if (trainPool.containsKey(name))
            return trainPool.get(name);

        Train train = new Train(name);
        trainPool.put(name, train);
        return train;
  }
}
