package model;

import lombok.Getter;

import java.util.Map;

@Getter
public class Beverage {
    private String name;

    public Beverage(String name, Map<String, Integer> ingredientQuantityMap) {
        this.name = name;
        this.ingredientQuantityMap = ingredientQuantityMap;
    }

    private Map<String, Integer> ingredientQuantityMap;
}
