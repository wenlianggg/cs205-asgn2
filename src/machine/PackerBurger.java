package machine;

import food.Burger;
import resource.CommonPool;
import util.Printer;

public class PackerBurger implements Machine {
    
    int machineId;

    // Source
    CommonPool commonPool;

    // Completed count
    int completed = 0;

    // Exit status
    String exitMessage = "Not exited";

    public PackerBurger(int machineId, CommonPool commonPool) {
       this.machineId = machineId;
       this.commonPool = commonPool;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Burger burger = commonPool.removeBurger();
                Printer.printf(
                    "%s gets burger id:%d from %s",
                    getMachineId(), burger.getId(), burger.getMaker()
                );
                completed++;
            }
        }  catch (InterruptedException e) {
            exitMessage = String.format("%s packs %d", getMachineId(), completed);
        }
    }

    @Override
    public String getExitMessage() {
        return exitMessage;
    }

    public String getMachineId() {
        return "bc" + machineId;
    }

}
