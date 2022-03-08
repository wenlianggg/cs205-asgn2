package machine;

import food.Burger;
import resource.CommonPool;
import resource.TodoList;
import util.Printer;

public class MakerBurger implements Maker {
    
    int machineId;

    // Source
    TodoList todoList;

    // Destination
    CommonPool commonPool;

    // Completed count
    int completed = 0;

    // Exit status
    String exitMessage = "Not exited";

    public MakerBurger(int machineId, TodoList todoList, CommonPool commonPool) {
       this.machineId = machineId;
       this.todoList = todoList;
       this.commonPool = commonPool;
    }

    @Override
    public void run() {
        int currentId = -1;
        try {
            while ((currentId = todoList.takeBurgerOrder()) != -1) {
                Machine.simulateWork(8000);
                Burger burger = new Burger(currentId, getMachineId());
                commonPool.add(burger, getMachineId()); 
                Printer.printf("%s puts burger id:%d", getMachineId(), burger.getId());
                completed++;
            }
        } catch (InterruptedException e) { }
        exitMessage = String.format("%s makes %d", getMachineId(), completed);
    }

    @Override
    public String getExitMessage() {
        return exitMessage;
    }

    public String getMachineId() {
        return "bm" + machineId;
    }

}
