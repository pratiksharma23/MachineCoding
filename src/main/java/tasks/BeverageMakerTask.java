package tasks;

import inventory.InventoryManager;
import model.Beverage;
/**This class represents an atomic task to make any Beverage.
 * Uses Runnable interface to support multithreading */

public class BeverageMakerTask implements Runnable {
    private Beverage beverage;

    BeverageMakerTask(Beverage beverage) {
        this.beverage = beverage;
    }

    @Override
    public void run() {
        if (InventoryManager.getInstance().checkAndUpdateInventory(beverage)) {
            System.out.println(beverage.getName() + " is prepared");
        }

    }

    @Override
    public String toString() {
        return beverage.getName();
    }
}
