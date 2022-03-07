package machine;

import food.Hotdog;
import resource.CommonPool;
import resource.TodoList;
import util.Printer;

public class MakerHotdog implements Machine {
    
    int machineId;

    // Source
    TodoList todoList;

    // Destination
    CommonPool commonPool;

    // Completed count
    int completed = 0;

    // Exit status
    String exitMessage = "Not exited";

    public MakerHotdog(int machineId, TodoList todoList, CommonPool commonPool) {
       this.machineId = machineId;
       this.todoList = todoList;
       this.commonPool = commonPool;
    }

    @Override
    public void run() {
        int currentId = -1;
        while ((currentId = todoList.takeHotdogOrder()) != -1) {
            try {
                Thread.sleep(3000);
                Hotdog hotdog = new Hotdog(currentId, getMachineId());
                commonPool.add(hotdog, getMachineId()); 
                Printer.printf(
                    "%s puts hotdog id:%d",
                    getMachineId(), hotdog.getId()
                );
            } catch (InterruptedException e) {
                return;
            }
            completed++;
        }
        exitMessage = String.format("%s makes %d", getMachineId(), completed);
    }

    @Override
    public String getExitMessage() {
        return exitMessage;
    }

    public String getMachineId() {
        return "hm" + machineId;
    }

}
