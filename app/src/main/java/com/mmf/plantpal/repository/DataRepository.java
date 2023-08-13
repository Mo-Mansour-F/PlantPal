package com.mmf.plantpal.repository;

import com.mmf.plantpal.models.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataRepository {
    private static List<String> speciesList = new ArrayList<>();
    private static List<String> typesList = new ArrayList<>();
    private static Map<String, Item> itemList = new HashMap<>();


    public static List<String> getSpeciesList() {
        return speciesList;
    }


    public static List<String> getTypesList() {
        return typesList;
    }

    public static void addSpecies(String species){
        if (!speciesList.contains(species)){
            speciesList.add(species);
        }
    }



    public static void addType(String type){
        if (!typesList.contains(type)){
            typesList.add(type);
        }
    }


    public static Map<String, Item> getItemList() {
        return itemList;
    }

    public static void setItemList(Map<String, Item> itemList) {
        DataRepository.itemList = itemList;
    }


    public static List<Item> getItemsCart(){
        List<Item> items = new ArrayList<>();
        for (Map.Entry<String, Item> itemEntry : itemList.entrySet()) {
            items.add(itemEntry.getValue());
        }
        return items;
    }
}
