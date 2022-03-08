package resource;

import food.Burger;
import food.Food;
import food.Hotdog;
import machine.Machine;
import util.Printer;

/**
 * Shared interface for interacting with producers, consumers, and underlying buffer.
 */
public class CommonPool {
    
    CircularQueue queue;
    private volatile long prioritisedThread = -1;
    private Object priorityLock = new Object();

    public CommonPool(int bufferSize) {
        queue = new CircularQueue(bufferSize);
    }

    /**
     * Add a food to the underlying shared buffer
     * @param food Item to be inserted into the shared buffer
     * @param source The label of where the food came from
     * @throws InterruptedException
     */
    public void add(Food food, String source) throws InterruptedException {
        // Update the last action time
        queue.enqueue(food);
    }

    /**
     * Remove a hotdog from the underlying shared buffer
     * @param nth Either '1' or '2' to signify priority
     * @return The hotdog from the front of the queue
     * @throws InterruptedException
     */
    public Hotdog removeHotdog(int nth) throws InterruptedException {
        
        Hotdog ret = null;

        synchronized(priorityLock) {
            while (true) {
                if (nth == 1 && prioritisedThread == -1) {
                    // If trying to get first hotdog and no one else has gotten
                    prioritisedThread = Thread.currentThread().getId();
                    break;
                } else if (nth == 2 && prioritisedThread == Thread.currentThread().getId()) {
                    // If already obtained first hotdog and is obtaining second now
                    break;
                } else {
                    // All other cases, block.
                    priorityLock.wait();
                }
            }

            // Update the last action time
            ret = (Hotdog) queue.dequeue(Hotdog.class);

            // Release the priority so that another hotdog maker can take
            if (nth == 2) {
                prioritisedThread = -1;
            }

            priorityLock.notifyAll();
        }

        return ret;
    }

    /**
     * Remove a burger from the underlying shared buffer
     * @return The burger from the front of the queue
     * @throws InterruptedException
     */
    public Burger removeBurger() throws InterruptedException {
        Burger ret = null;
        ret = (Burger) queue.dequeue(Burger.class);
        return ret;
    }

}


/**
 * Underlying shared buffer
 */
class CircularQueue {

    private Food[] buffer;
    private volatile int front = 0;
    private volatile int back = 0;
    public volatile int item_count = 0;

    public CircularQueue(int size){
        buffer = new Food[size];
    }

    /**
     * Insert an instance of the food into the shared buffer
     * @param food The food to be inserted into the shared buffer
     * @throws InterruptedException
     */
    public synchronized void enqueue(Food food) throws InterruptedException {
        
        while (item_count == buffer.length){
            // Buffer is full, block
            this.wait();
        }

        Machine.simulateWork(1000);
        buffer[back] = food;
        back = (back + 1) % buffer.length;
        item_count++;

        Printer.debugln(this);
        this.notifyAll();

    }

    /**
     * Removes a food from the shared buffer, if it is of a specified type
     * @param <T> The type of food
     * @param type The class of the food to be retrieved
     * @return The actual food removed from the shared buffer
     * @throws InterruptedException
     */
    public synchronized <T extends Food> Food dequeue(Class<T> type) throws InterruptedException {

        while (item_count == 0 || !type.isInstance(buffer[front])){
            this.wait(); // Buffer is empty or of wrong type, block
        }

        Machine.simulateWork(1000);
        Food food = buffer[front];
        front = (front + 1) % buffer.length;
        item_count--;

        Printer.debugln(this);
        this.notifyAll();
        return food;
    }

    /**
     * Peek into the front of the shared buffer
     * @return The item at the front of the shared buffer
     * @throws InterruptedException
     */
    public synchronized Food peek() throws InterruptedException {
        this.wait();
        Food food = buffer[front];
        this.notifyAll();
        return food;
    }
    
    @Override
    public synchronized String toString() {
        String s = "[";
        for(int i = 0; i < item_count; i++) {
            int idx = (front + i) % buffer.length;
            s += buffer[idx] + " ";
        }
        s += "]";
        return s;
    }

}