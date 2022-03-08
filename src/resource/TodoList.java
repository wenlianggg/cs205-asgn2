package resource;

public class TodoList {
    
    private int hotdogsToMake;
    private int currentHotdogId;

    private int burgersToMake;
    private int currentBurgerId;

    public TodoList(int hotdogsToMake, int burgersToMake) {
        this.hotdogsToMake = hotdogsToMake;
        this.burgersToMake = burgersToMake;
    }

    public synchronized int takeHotdogOrder() {
        if (hotdogsToMake > 0) {
            hotdogsToMake--;
            return currentHotdogId++;
        }
        return -1;
    }

    public synchronized int takeBurgerOrder() {
        if (burgersToMake > 0) {
            burgersToMake--;
            return currentBurgerId++;
        }
        return -1;
    }

    public int getHotdogsToMake() {
        return hotdogsToMake;
    }

    public void setHotdogsToMake(int hotdogsToMake) {
        this.hotdogsToMake = hotdogsToMake;
    }

    public int getCurrentHotdogId() {
        return currentHotdogId;
    }

    public void setCurrentHotdogId(int currentHotdogId) {
        this.currentHotdogId = currentHotdogId;
    }

    public int getBurgersToMake() {
        return burgersToMake;
    }

    public void setBurgersToMake(int burgersToMake) {
        this.burgersToMake = burgersToMake;
    }

    public int getCurrentBurgerId() {
        return currentBurgerId;
    }

    public void setCurrentBurgerId(int currentBurgerId) {
        this.currentBurgerId = currentBurgerId;
    }

    

}
