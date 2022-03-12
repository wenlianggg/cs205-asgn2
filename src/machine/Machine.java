package machine;

import java.util.concurrent.TimeUnit;

public interface Machine extends Runnable {
    
    public static final double TIME_MULTIPLIER = 0.05;

    String getExitMessage();
    
    String getMachineId();

    public static void simulateWork(int milliseconds) throws InterruptedException{
        TimeUnit.MILLISECONDS.sleep((int) (milliseconds * TIME_MULTIPLIER));
    }
}

