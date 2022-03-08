package machine;

import food.Hotdog;
import resource.CommonPool;
import util.Printer;

public class PackerHotdog implements Packer {

    int machineId;

    // Source
    CommonPool commonPool;

    // Completed count
    int completed = 0;

    // Exit status
    String exitMessage = "Not exited";

    public PackerHotdog(int machineId, CommonPool commonPool) {
        this.machineId = machineId;
        this.commonPool = commonPool;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Machine.simulateWork(2000);
                Hotdog hotdog1 = commonPool.removeHotdog(1);
                Hotdog hotdog2 = commonPool.removeHotdog(2);
                Printer.printf(
                        "%s gets hotdogs id:%d from %s and id:%d from %s",
                        getMachineId(), hotdog1.getId(), hotdog1.getMaker(), hotdog2.getId(), hotdog2.getMaker());
                completed += 2;
            }
        } catch (InterruptedException e) { }
        exitMessage = String.format("%s packs %d", getMachineId(), completed);
    }

    @Override
    public String getExitMessage() {
        return exitMessage;
    }

    public String getMachineId() {
        return "hc" + machineId;
    }

}
