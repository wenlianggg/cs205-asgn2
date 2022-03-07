package resource;

import java.time.LocalDateTime;

import food.Burger;
import food.Food;
import food.Hotdog;
import util.Printer;

public class CommonPool {
    
    private Object hotdogMutex = new Object();
    private Object burgerMutex = new Object();

    public volatile LocalDateTime lastAction;

    // Circular buffer
    private Food[] buffer;
    private int capacity;
    private volatile int size;
    private volatile int front;
    private volatile int back;
    private Object bufferMutex = new Object();
    private Object full = new Object();
    private Object empty = new Object();

    // Keeping track of which thread has hotdog priority
    private Object priorityMutex = new Object();
    private volatile long currentHotdogThreadPriority = -1;


    public CommonPool(int bufferSize) {
        lastAction = LocalDateTime.now();
        buffer = new Food[bufferSize];
        capacity = bufferSize;
        front = -1;
        back = -1;
    }

    /* Checking on the status of the pool */
    private boolean isFull() {
        synchronized (bufferMutex) {
            return (size == capacity);
        }
    }

    private boolean isEmpty() {
        synchronized (bufferMutex) {
            return (size == 0);
        }
    }
    
    private boolean isNextHotdog() {
        synchronized (bufferMutex) {
            return (front != -1 && buffer[front] instanceof Hotdog);
        }
    }

    private boolean isNextBurger() {
        synchronized (bufferMutex) {
            return (front != -1 && buffer[front] instanceof Burger);
        }
    }

    // Producer
    public void add(Food food, String source) throws InterruptedException {
        
        synchronized (full) {
            while (isFull()) {
                Printer.debugln("Common pool is full, blocking " + source + " " + Thread.currentThread().getName());
                full.wait();
            }
        }

        synchronized (bufferMutex) {
            if (size == 0) {
                front = 0;
            }
            back = (back + 1) % capacity;
            buffer[back] = food;
            lastAction = LocalDateTime.now();
            size++;
            Printer.debugln(this);
            Thread.sleep(1000);
        }

        synchronized(hotdogMutex) {
            hotdogMutex.notify();
        }

        synchronized(burgerMutex) {
            burgerMutex.notify();
        }

        synchronized (empty) {
            // Printer.println("notifying empty");
            empty.notify();
        }
    }

    // Consumer
    private <T> Food remove(Class<T> clazz) throws InterruptedException {
        Food food = null;
        boolean done = false;

        while(!done) {
            while (isEmpty()) {
                synchronized(empty) {
                    Printer.debugln("Common pool is empty, blocking");
                    empty.wait();
                }
            }

            
            synchronized (bufferMutex) {
                if (!clazz.isInstance(buffer[front])) {
                    continue;
                }

                food = buffer[front];
                buffer[front] = null;
                if (size == 0) {
                    front = -1;
                    back = -1;
                } else {
                    front = (front + 1) % capacity;
                    size--;
                }
                Printer.debugln("Removing " + food.getId() + " by " + Thread.currentThread().getName());
                Printer.debugln(this);
                lastAction = LocalDateTime.now();
                Thread.sleep(1000);
            }
    
            synchronized (full) {
                full.notify();
            }
            done = true;
        }
        return food;
    }



    /**
     * Removes the first hotdog
     * @return
     * @throws InterruptedException
     */
    public Hotdog removeFirstHotdog() throws InterruptedException {

        Hotdog ret = null;
        while (!isNextHotdog()) {
            if (Thread.currentThread().isInterrupted())
                throw new InterruptedException();
        };

        while (currentHotdogThreadPriority != -1) {
            synchronized (hotdogMutex) {
                hotdogMutex.wait(1000);
            }
        }
        Thread.currentThread().setPriority(10);

        synchronized (priorityMutex) {
            currentHotdogThreadPriority = Thread.currentThread().getId();
        }

        while (!isNextHotdog()) {
            synchronized (hotdogMutex) {
                hotdogMutex.wait();
            }
        }

        ret = (Hotdog) this.remove(Hotdog.class);
        
        synchronized (hotdogMutex) {
            hotdogMutex.notify();
        }

        return ret;
    }

    
    /**
     * Removes the second hotdog
     * @return
     * @throws InterruptedException
     */
    public Hotdog removeSecondHotdog() throws InterruptedException {
        Hotdog ret = null;
        
        while (!isNextHotdog()) {
            if (Thread.currentThread().isInterrupted())
                throw new InterruptedException();
        };

        ret = (Hotdog) this.remove(Hotdog.class);

        synchronized(hotdogMutex) {
            hotdogMutex.notify();
            currentHotdogThreadPriority = -1;
            Thread.currentThread().setPriority(5);
        }

        return ret;
    }
    


    /**
     * Removes a burger from the common pool
     * @return
     * @throws InterruptedException
     */
    public Burger removeBurger() throws InterruptedException {

        Burger ret = null;
        while (!isNextBurger()) {
            if (Thread.currentThread().isInterrupted())
                throw new InterruptedException();
        };

        ret = (Burger) this.remove(Burger.class); 

        synchronized (hotdogMutex) {
            hotdogMutex.notify();
        }
        
        return ret;
    }

    @Override
    public String toString() {
        String s = "[";
        for(int i = front; i != back; i = (++i) % capacity) {
            s += buffer[i] + " ";
        }
        if (!isEmpty()) {
            s += buffer[back];
        }
        s += "]";
        return s;
    }

}
