package com.example.gil.expensesmanager.model;


/**
 * Created by Gil on 16/05/2016.
 */
public class Item {
    private String id; //Date + Time
    private String uid;
    private String title;
    private String place;
    private double price;
    private int dayPurchase;
    private int monthPurchase;
    private int yearPurchase;
    private String category;
    private String description;
    private String image;
    private String lastUpdated;

    public Item(){}

    public Item(String id, String uid, String title, String place, double price, int dayPurchase, int monthPurchase, int yearPurchase,
                String category, String description, String image, String lastUpdated) {
        this.id = id;
        this.uid = uid;
        this.title = title;
        this.place = place;
        this.price = price;
        this.dayPurchase = dayPurchase;
        this.monthPurchase = monthPurchase;
        this.yearPurchase = yearPurchase;
        this.category = category;
        this.description = description;
        this.image = image;
        this.lastUpdated = lastUpdated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getDayPurchase() {
        return dayPurchase;
    }

    public void setDayPurchase(int dayPurchase) {
        this.dayPurchase = dayPurchase;
    }

    public int getMonthPurchase() {
        return monthPurchase;
    }

    public void setMonthPurchase(int monthPurchase) {
        this.monthPurchase = monthPurchase;
    }

    public int getYearPurchase() {
        return yearPurchase;
    }

    public void setYearPurchase(int yearPurchase) {
        this.yearPurchase = yearPurchase;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id='" + id + '\'' +
                ", uid='" + uid + '\'' +
                ", title='" + title + '\'' +
                ", place='" + place + '\'' +
                ", price=" + price +
                ", dayPurchase=" + dayPurchase +
                ", monthPurchase=" + monthPurchase +
                ", yearPurchase=" + yearPurchase +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", lastUpdated='" + lastUpdated + '\'' +
                '}';
    }
}
