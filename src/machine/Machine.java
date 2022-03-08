package machine;

import java.util.concurrent.TimeUnit;

public interface Machine extends Runnable {
    
    String getExitMessage();
    
    String getMachineId();

    public static void simulateWork(int milliseconds) throws InterruptedException{
        TimeUnit.MILLISECONDS.sleep(milliseconds);
    }
}


