package com.example.gil.expensesmanager.model;

import java.util.List;

/**
 * Created by Gil on 16/05/2016.
 */
public class User {
    private String id;  //FireBase
    private String name;
    private double startingBudget;
    private double currentBudget;
    private List<User> userShareList;
    private List<Item> itemList;

    public User(){}

    public User(String id, String name, float startingBudget, float currentBudget) {
        this.id = id;
        this.name = name;
        this.startingBudget = startingBudget;
        this.currentBudget = currentBudget;
    }

    public User(User user){
        this.id = user.getId();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBudget() {
        return startingBudget;
    }

    public void setBudget(double startingBudget) {
        this.startingBudget = startingBudget;
    }

    public List<User> getUserShareList() {
        return userShareList;
    }

    public void setUserShareList(List<User> userShareList) {
        this.userShareList = userShareList;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }

    public void setCurrentBudget(double currentBudget) {
        this.currentBudget = currentBudget;
    }

    public double getCurrentBudget() {
        return currentBudget;
    }

    public double getCurrentBudget(User user) {
        return user.getCurrentBudget();
    }

    public double getItemsSumPrice(List<Item> items){
        double sum = 0;
        for(Item item : items){
            sum += item.getPrice();
        }
        return sum;
    }
}
