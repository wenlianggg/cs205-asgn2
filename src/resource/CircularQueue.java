package resource;

import food.Food;
import machine.Machine;
import util.Printer;

public class CircularQueue {

    private Food[] buffer;
    private int front = 0;
    private int back = 0;
    public int item_count = 0;

    public CircularQueue(int size){
        buffer = new Food[size];
    }

    public synchronized void put(Food food) throws InterruptedException {
        while (item_count == buffer.length){
            this.wait();
        }

        Machine.simulateWork(1000);
        buffer[back] = food;
        back = (back + 1) % buffer.length;
        item_count++;

        Printer.debugln(this);
        this.notifyAll();
    }

    public synchronized <T> Food get(Class<T> type) throws InterruptedException {
        while (item_count == 0 || !type.isInstance(buffer[front])){
            this.wait();
        }

        Machine.simulateWork(1000);
        Food food = buffer[front];
        front = (front + 1) % buffer.length;
        item_count--;

        Printer.debugln(this);
        this.notifyAll();
        return food;
    }

    public synchronized Food peek() throws InterruptedException {
        this.wait();
        Food food = buffer[front];
        this.notifyAll();
        return food;
    }




    
    /**
     * Generate a printable version of the current buffer
     */
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
