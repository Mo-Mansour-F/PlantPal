package com.mmf.plantpal.repository;

import com.mmf.plantpal.models.CartItem;
import com.mmf.plantpal.models.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataRepository {
    private static List<String> speciesList = new ArrayList<>();
    private static List<String> typesList = new ArrayList<>();
    private static Map<String, CartItem> itemList;


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


    public static Map<String, CartItem> getItemList() {
        if (itemList == null){
            itemList = new HashMap<>();
        }
        return itemList;
    }

    public static void setItemList(Map<String, CartItem> itemList) {
        DataRepository.itemList = itemList;
    }


    public static List<CartItem> getItemsCart(){
        List<CartItem> items = new ArrayList<>();
        for (Map.Entry<String, CartItem> itemEntry : itemList.entrySet()) {
            items.add(itemEntry.getValue());
        }
        return items;
    }





    public static void addItemCart(CartItem cartItem) {

        if (itemList.containsKey(cartItem.getReferenceId())){
            CartItem item = itemList.get(cartItem.getReferenceId());
            assert item != null;
            item.setCount(item.getCount() + 1);
            getItemList().put(cartItem.getReferenceId(), item);
        }else {
            cartItem.setCount(1);
            getItemList().put(cartItem.getReferenceId(), cartItem);
        }
    }

}
