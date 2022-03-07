package machine;
public interface Machine extends Runnable {
    String getExitMessage();
    String getMachineId();
}