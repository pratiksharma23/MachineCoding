package model;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import java.util.HashMap;

@Getter
public class Machine {

    private Outlet outlets;

    @JsonProperty("total_items_quantity")
    private HashMap<String, Integer> ingredientQuantityMap;

    @JsonProperty("beverages")
    private HashMap<String, HashMap<String, Integer>> beverages;
}
