package inventory;

import model.Beverage;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a thread-safe singleton implementation for an inventory management system.
 * This class takes care of handling all the inventory.
 */

public class InventoryManager {
    public HashMap<String, Integer> inventory = new HashMap<>();

    private InventoryManager() {
    }

    //Using Holder pattern ensures thread safe initialisation of the object,
    private static class InventoryManagerHolder {
        public static final InventoryManager instance = new InventoryManager();
    }

    public static InventoryManager getInstance() {
        return InventoryManagerHolder.instance;
    }

    //Making this thread safe by synchronizing
    public synchronized boolean checkAndUpdateInventory(Beverage beverage) {
        Map<String, Integer> requiredIngredientMap = beverage.getIngredientQuantityMap();
        boolean isPossible = true;

        for (String ingredient : requiredIngredientMap.keySet()) {
            int ingredientInventoryCount = inventory.getOrDefault(ingredient, -1);
            if (ingredientInventoryCount == -1 || ingredientInventoryCount == 0) {
                System.out.println(beverage.getName() + " cannot be prepared because " + ingredient + " is not available");
                isPossible = false;
                break;
            } else if (requiredIngredientMap.get(ingredient) > ingredientInventoryCount) {
                System.out.println(beverage.getName() + " cannot be prepared because " + ingredient + " is not sufficient");
                isPossible = false;
                break;
            }
        }

        if (isPossible) {
            for (String ingredient : requiredIngredientMap.keySet()) {
                int existingInventory = inventory.getOrDefault(ingredient, 0);
                inventory.put(ingredient, existingInventory - requiredIngredientMap.get(ingredient));
            }
        }

        return isPossible;
    }

    public void addInventory(String ingredient, int quantity) {
        int existingInventory = inventory.getOrDefault(ingredient, 0);
        inventory.put(ingredient, existingInventory + quantity);
    }

    //Used only for testing
    public void resetInventory() {
        inventory.clear();
    }
}
