package resource;

import java.time.LocalDateTime;

import food.Burger;
import food.Food;
import food.Hotdog;
import util.Printer;

public class CommonPool {
    
    public volatile LocalDateTime lastAction;

    // Circular buffer
    CircularQueue queue;
    private volatile long prioritisedThread = -1;
    private Object priorityLock = new Object();

    public CommonPool(int bufferSize) {
        lastAction = LocalDateTime.now();
        queue = new CircularQueue(bufferSize);
    }

    public void add(Food food, String source) throws InterruptedException {
        lastAction = LocalDateTime.now();
        queue.put(food);
    }

    public Hotdog removeHotdog(int nth) throws InterruptedException {
        
        Hotdog ret = null;

        synchronized(priorityLock) {
            while (true) {
                if (nth == 1 && prioritisedThread == -1) {
                    prioritisedThread = Thread.currentThread().getId();
                    break;
                } else if (nth == 2 && prioritisedThread == Thread.currentThread().getId()) {
                    break;
                } else {
                    priorityLock.wait();
                }
            }

            lastAction = LocalDateTime.now();
            ret = (Hotdog) queue.get(Hotdog.class);

            if (nth == 2) {
                prioritisedThread = -1;
            }

            priorityLock.notifyAll();
        }

        return ret;
    }

    public Burger removeBurger() throws InterruptedException {
        lastAction = LocalDateTime.now();
        Burger ret = null;
        ret = (Burger) queue.get(Burger.class);
        return ret;
    }

}

// -----------------------------------------------------

// // Consumer
// private <T> Food remove(Class<T> clazz) throws InterruptedException {
//     Food food = null;
//     boolean done = false;

//     while(!done) {
//         while (isEmpty()) {
//             synchronized(empty) {
//                 Printer.debugln("Common pool is empty, blocking");
//                 empty.wait();
//             }
//         }

        
//         synchronized (bufferMutex) {
//             if (!clazz.isInstance(buffer[front])) {
//                 continue;
//             }

//             food = buffer[front];
//             buffer[front] = null;
//             if (size == 0) {
//                 front = -1;
//                 back = -1;
//             } else {
//                 front = (front + 1) % capacity;
//                 size--;
//             }
//             Printer.debugln("Removing " + food.getId() + " by " + Thread.currentThread().getName());
//             Printer.debugln(this);
//             lastAction = LocalDateTime.now();
//             Thread.sleep(1000);
//         }

//         synchronized (full) {
//             full.notify();
//         }
//         done = true;
//     }
//     return food;
// }



// /**
//  * Removes the first hotdog
//  * @return
//  * @throws InterruptedException
//  */
// public Hotdog removeFirstHotdog() throws InterruptedException {

//     Hotdog ret = null;
//     while (!isNextHotdog()) {
//         if (Thread.currentThread().isInterrupted())
//             throw new InterruptedException();
//     };

//     while (currentHotdogThreadPriority != -1) {
//         synchronized (hotdogMutex) {
//             hotdogMutex.wait(1000);
//         }
//     }
//     Thread.currentThread().setPriority(10);

//     synchronized (priorityMutex) {
//         currentHotdogThreadPriority = Thread.currentThread().getId();
//     }

//     while (!isNextHotdog()) {
//         synchronized (hotdogMutex) {
//             hotdogMutex.wait();
//         }
//     }

//     ret = (Hotdog) this.remove(Hotdog.class);
    
//     synchronized (hotdogMutex) {
//         hotdogMutex.notify();
//     }

//     return ret;
// }

// // Producer
// public void add(Food food, String source) throws InterruptedException {

//     synchronized (full) {
//         while (isFull()) {
//             Printer.debugln("Common pool is full, blocking " + source + " " + Thread.currentThread().getName());
//             full.wait();
//         }
//     }

//     synchronized (bufferMutex) {
//         if (size == 0) {
//             front = 0;
//         }
//         back = (back + 1) % capacity;
//         buffer[back] = food;
//         lastAction = LocalDateTime.now();
//         size++;
//         Printer.debugln(this);
//         Thread.sleep(1000);
//     }

//     synchronized(hotdogMutex) {
//         hotdogMutex.notify();
//     }

//     synchronized(burgerMutex) {
//         burgerMutex.notify();
//     }

//     synchronized (empty) {
//         // Printer.println("notifying empty");
//         empty.notify();
//     }
// }