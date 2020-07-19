package tasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Multimap;
import inventory.InventoryManager;
import model.Beverage;
import model.CoffeeMachineDetails;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

import static java.lang.Thread.sleep;


/**
 * Assumptions:
 * 1) The input JSON will not have duplicate beverage names. (Workaround would be to use MultiMap, custom deserialiser. Skipping it as it might be out of scope given time constraints)
 * 2) The input JSON contains correct input. Keeping check on the range of values seems to be out of scope given time constraints(Workaround would be add  javax validations in the input Models itself).
 * <p>
 * Algorithm:
 * A multithreaded system with n threads is invoked to represent n nozzled coffee machine.
 * It queues up all the requests from the input and tries to create the beverages. Importance has been given to thread safety to ensure two drinks do not use same ingredient.
 * Feature to add new ingredient in our inventory is given, as well as to add new Beverage Requests at any given point of time.
 */

/**Represents a physical Coffee Machine, which can serve PARALLELY, using multi threading.
 * Singleton Class to simulate a CoffeeMachine
 * Supports adding beverage requests, with a maximum pending queue size = MAX_QUEUED_REQUEST*/

public class CoffeeMachine {
    private static final Logger logger = LoggerFactory.getLogger(CoffeeMachine.class);

    private static CoffeeMachine coffeeMachine;
    public CoffeeMachineDetails coffeeMachineDetails;
    public InventoryManager inventoryManager;
    private static final int MAX_QUEUED_REQUEST = 100;
    private ThreadPoolExecutor executor;

    /**
     * makes class singleton in nature
     * will return CoffeeMachine.INSTANCE is it already exits else creates one
     * @return
     * @throws IOException
     */
    public static CoffeeMachine getInstance(final String jsonInput) throws IOException {
        if (coffeeMachine == null) {
            coffeeMachine = new CoffeeMachine(jsonInput);
        }
        return coffeeMachine;
    }

    private CoffeeMachine(String jsonInput) throws IOException {
        System.out.println("New Machine");
        this.coffeeMachineDetails = new ObjectMapper().readValue(jsonInput, CoffeeMachineDetails.class);
        int outlet = coffeeMachineDetails.getMachine().getOutlets().getCount();
        executor = new ThreadPoolExecutor(outlet, outlet, 5000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(MAX_QUEUED_REQUEST));
        executor.setRejectedExecutionHandler(new RejectedTaskHandler());
    }

    public void process() {
        this.inventoryManager = InventoryManager.getInstance();

        Map<String, Integer> ingredients = coffeeMachineDetails.getMachine().getIngredientQuantityMap();

        for (String key : ingredients.keySet()) {
            inventoryManager.addInventory(key, ingredients.get(key));
        }

        HashMap<String, HashMap<String, Integer>> beverages = coffeeMachineDetails.getMachine().getBeverages();
        for (String key : beverages.keySet()) {
            Beverage beverage = new Beverage(key, beverages.get(key));
            coffeeMachine.addBeverageRequest(beverage);
        }
    }

    public void addBeverageRequest(Beverage beverage) {
        BeverageMakerTask task = new BeverageMakerTask(beverage);
        executor.execute(task);
    }

    public void stopMachine() {
        executor.shutdown();
    }

    /**Resetting inventory and stopping coffee machine. This is only used for testing. In real world, no need for resetting unless machine is stopped.*/
    public void reset() {
        //System.out.println("Resetting\n\n");
        logger.info("Resetting");
        this.stopMachine();
        this.inventoryManager.resetInventory();
    }
}
