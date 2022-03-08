
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import machine.Machine;
import machine.Maker;
import machine.MakerBurger;
import machine.MakerHotdog;
import machine.PackerBurger;
import machine.PackerHotdog;
import resource.CommonPool;
import resource.TodoList;
import util.Printer;

public class FoodManager {

    int hotdogPackers;
    int burgerPackers;

    CommonPool commonPool;
    TodoList todoList;

    Thread checkStopThread;

    List<Machine> machines = new ArrayList<>();
    List<Thread> machineThreads = new ArrayList<>();

    public FoodManager(int hotdogsToMake, int burgersToMake, int commonPoolCapacity, int hotdogMakers, int burgerMakers, int hotdogPackers, int burgerPackers) {

        // Print args
        Printer.println("Hotdogs: " + hotdogsToMake);
        Printer.println("Burgers: " + burgersToMake);

        Printer.println("Capacity: " + commonPoolCapacity);

        Printer.println("Hotdog Makers: " + hotdogMakers);
        Printer.println("Burger Makers: " + burgerMakers);
        Printer.println("Hotdog Packers: " + hotdogPackers);
        Printer.println("Burger Packers: " + burgerPackers);

        // Create dependencies
        todoList = new TodoList(hotdogsToMake, burgersToMake);
        commonPool = new CommonPool(commonPoolCapacity);

        // Create mapping
        final Map<Machine, Thread> map = new HashMap<Machine, Thread>();

        // Create the machines
        for (int i = 0; i < hotdogMakers; i++) {
            var machine = new MakerHotdog(i, todoList, commonPool);
            machines.add(machine);
        }

        for (int i = 0; i < burgerMakers; i++) {
            var machine = new MakerBurger(i, todoList, commonPool);
            machines.add(machine);
        }

        for (int i = 0; i < hotdogPackers; i++) {
            var machine = new PackerHotdog(i, commonPool);
            machines.add(machine);
        }

        for (int i = 0; i < burgerPackers; i++) {
            var machine = new PackerBurger(i, commonPool);
            machines.add(machine);
        }

        for (Machine m : machines) {
            Thread t = new Thread(m);
            t.setName(m.getMachineId());
            if (m instanceof Maker) {
                t.setPriority(Thread.NORM_PRIORITY - 1);
            }
            machineThreads.add(t);
            map.put(m, t);
        }

        Runnable checkStop = () -> {
            boolean killed = false;

            while (!killed) {
                try { TimeUnit.SECONDS.sleep(2); } catch (InterruptedException e) {};
                LocalDateTime cutoff = LocalDateTime.now().minusSeconds(9);
                if (Printer.lastAction.isBefore(cutoff)) {
                    Printer.println("Summary:");
                    try { TimeUnit.SECONDS.sleep(2); } catch (InterruptedException e) {};
                    for (Map.Entry<Machine, Thread> entry : map.entrySet()) {
                        entry.getValue().interrupt();
                    }
                    killed = true;
                }
            }
        };
        
        checkStopThread = new Thread(checkStop);
    }

    void start() {
        for (Thread t : machineThreads) {
            t.start();
        }
        checkStopThread.start();

        for (Thread t : machineThreads) {
            try { t.join(); } catch (InterruptedException e) {}
        }

        for (Machine m : machines) {
            Printer.println(m.getExitMessage());
        }

        try { checkStopThread.join(); } catch(InterruptedException e) {}
    }

    public static void main(String[] args) {
        
        if (args.length < 7) {
            Printer.println("Insufficient arguments");
            return;
        }

        // Parse the command line inputs
        int hotdogsToMake = Integer.parseInt(args[0]);
        int burgersToMake = Integer.parseInt(args[1]);
        int commonPoolCapacity = Integer.parseInt(args[2]);
        int hotdogMakers = Integer.parseInt(args[3]);
        int burgerMakers = Integer.parseInt(args[4]);
        int hotdogPackers = Integer.parseInt(args[5]);
        int burgerPackers = Integer.parseInt(args[6]);

        FoodManager manager = new FoodManager(
            hotdogsToMake,
            burgersToMake,
            commonPoolCapacity,
            hotdogMakers,
            burgerMakers,
            hotdogPackers,
            burgerPackers
        );

        manager.start();
        System.exit(0);
    }

}