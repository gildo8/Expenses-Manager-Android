package com.example.gil.expensesmanager.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by gildo on 27/05/2016.
 */
public class ItemSql {
    private static final String ITEMS_TABLE = "items";

    private static final String ITEM_ID = "id";
    private static final String ITEM_UID = "itemuid";
    private static final String ITEM_TITLE = "itemtitle";
    private static final String ITEM_PLACE = "itemplace";
    private static final String ITEM_PRICE = "itemprice";
    private static final String ITEM_DAYP = "itemdayp";
    private static final String ITEM_MONTHP = "itemonthp";
    private static final String ITEM_YEARP = "itemyearp";
    private static final String ITEM_CATEGORY = "itemcat";
    private static final String ITEM_DESCRIPTION = "itemdes";
    private static final String ITEM_IMAGE = "itemimg";
    private static final String ITEM_LAST_UPDATE = "itemupdate";

    public static void addItem(SQLiteDatabase db, Item item) {

        ContentValues values = new ContentValues();

        values.put(ITEM_ID, item.getId());
        values.put(ITEM_UID, item.getUid());
        values.put(ITEM_TITLE, item.getTitle());
        values.put(ITEM_PLACE, item.getPlace());
        values.put(ITEM_PRICE, item.getPrice());
        values.put(ITEM_DAYP, item.getDayPurchase());
        values.put(ITEM_MONTHP, item.getMonthPurchase());
        values.put(ITEM_YEARP, item.getYearPurchase());
        values.put(ITEM_CATEGORY, item.getCategory());
        values.put(ITEM_DESCRIPTION, item.getDescription());
        values.put(ITEM_IMAGE, item.getImage());
        values.put(ITEM_LAST_UPDATE, item.getLastUpdated());

        db.insertWithOnConflict(ITEMS_TABLE,ITEM_ID,values,SQLiteDatabase.CONFLICT_REPLACE);
        Log.d("ItemSql"," AddItem " + item.getTitle());
    }


    public static Item getSingleItem(SQLiteDatabase db, String uid,String itemId){
        String[] params = new String[2];

        params[0] = uid;
        params[1] = itemId;

        Cursor cursor = db.query(ITEMS_TABLE, null, ITEM_UID + " = ?" + " AND " + ITEM_ID + " = ?", params, null,null, null);

        if (cursor.moveToFirst()) {
            int Id = cursor.getColumnIndex("id");
            int itemUid = cursor.getColumnIndex("itemuid");
            int itemTitle = cursor.getColumnIndex("itemtitle");
            int itemPlace = cursor.getColumnIndex("itemplace");
            int itemPrice = cursor.getColumnIndex("itemprice");
            int itemDay = cursor.getColumnIndex("itemdayp");
            int itemMonth = cursor.getColumnIndex("itemonthp");
            int itemYear = cursor.getColumnIndex("itemyearp");
            int itemCategory = cursor.getColumnIndex("itemcat");
            int itemDescription = cursor.getColumnIndex("itemdes");
            int itemImg = cursor.getColumnIndex("itemimg");
            int itemLastUpdate = cursor.getColumnIndex("itemupdate");

            String id = cursor.getString(Id);
            String iUid = cursor.getString(itemUid);
            String title = cursor.getString(itemTitle);
            String place = cursor.getString(itemPlace);
            double price = cursor.getDouble(itemPrice);
            int day = cursor.getInt(itemDay);
            int month = cursor.getInt(itemMonth);
            int year = cursor.getInt(itemYear);
            String category = cursor.getString(itemCategory);
            String desc = cursor.getString(itemDescription);
            String image = cursor.getString(itemImg);
            String lastUpdate = cursor.getString(itemLastUpdate);

            Item item = new Item(id,iUid,title,place,price,day,month,year,category,desc,image,lastUpdate);

            return item;
        }
        return null;
    }


    public static List<Item> getAllItems(SQLiteDatabase db) {

        Cursor cursor = db.query(ITEMS_TABLE, null, null , null, null, null, null);
        List<Item> itemsList = new LinkedList<Item>();

        if (cursor.moveToFirst()) {
            int Id = cursor.getColumnIndex("id");
            int itemUid = cursor.getColumnIndex("itemuid");
            int itemTitle = cursor.getColumnIndex("itemtitle");
            int itemPlace = cursor.getColumnIndex("itemplace");
            int itemPrice = cursor.getColumnIndex("itemprice");
            int itemDay = cursor.getColumnIndex("itemdayp");
            int itemMonth = cursor.getColumnIndex("itemonthp");
            int itemYear = cursor.getColumnIndex("itemyearp");
            int itemCategory = cursor.getColumnIndex("itemcat");
            int itemDescription = cursor.getColumnIndex("itemdes");
            int itemImg = cursor.getColumnIndex("itemimg");
            int itemLastUpdate = cursor.getColumnIndex("itemupdate");
            do {
                String id = cursor.getString(Id);
                String iUid = cursor.getString(itemUid);
                String title = cursor.getString(itemTitle);
                String place = cursor.getString(itemPlace);
                double price = cursor.getDouble(itemPrice);
                int day = cursor.getInt(itemDay);
                int month = cursor.getInt(itemMonth);
                int year = cursor.getInt(itemYear);
                String category = cursor.getString(itemCategory);
                String desc = cursor.getString(itemDescription);
                String image = cursor.getString(itemImg);
                String lastUpdate = cursor.getString(itemLastUpdate);

                Item item = new Item(id,iUid,title,place,price,day,month,year,category,desc,image,lastUpdate);
                itemsList.add(item);
            } while (cursor.moveToNext());
        }
        Log.d("ItemSql"," GetAllItems");
        return itemsList;
    }

    public static List<Item> getItemsByUid(SQLiteDatabase db,String uid) {

//        Cursor cursor = db.query(ITEMS_TABLE, null, null , null, null, null, null);
        List<Item> itemsList = new LinkedList<Item>();
        Cursor cursor = db.query(ITEMS_TABLE, null, ITEM_UID + "=?", new String[]{ uid }, null, null, null);

        if (cursor.moveToFirst()) {
            int Id = cursor.getColumnIndex("id");
            int itemUid = cursor.getColumnIndex("itemuid");
            int itemTitle = cursor.getColumnIndex("itemtitle");
            int itemPlace = cursor.getColumnIndex("itemplace");
            int itemPrice = cursor.getColumnIndex("itemprice");
            int itemDay = cursor.getColumnIndex("itemdayp");
            int itemMonth = cursor.getColumnIndex("itemonthp");
            int itemYear = cursor.getColumnIndex("itemyearp");
            int itemCategory = cursor.getColumnIndex("itemcat");
            int itemDescription = cursor.getColumnIndex("itemdes");
            int itemImg = cursor.getColumnIndex("itemimg");
            int itemLastUpdate = cursor.getColumnIndex("itemupdate");
            do {
                String id = cursor.getString(Id);
                String iUid = cursor.getString(itemUid);
                String title = cursor.getString(itemTitle);
                String place = cursor.getString(itemPlace);
                double price = cursor.getDouble(itemPrice);
                int day = cursor.getInt(itemDay);
                int month = cursor.getInt(itemMonth);
                int year = cursor.getInt(itemYear);
                String category = cursor.getString(itemCategory);
                String desc = cursor.getString(itemDescription);
                String image = cursor.getString(itemImg);
                String lastUpdate = cursor.getString(itemLastUpdate);

                Item item = new Item(id,iUid,title,place,price,day,month,year,category,desc,image,lastUpdate);
                itemsList.add(item);
            } while (cursor.moveToNext());
        }
        Log.d("ItemSql"," GetAllItems");
        return itemsList;
    }
    
    public static void create(SQLiteDatabase db) {
        db.execSQL("create table " +
                ITEMS_TABLE + " (" +
                ITEM_ID + " TEXT PRIMARY KEY," +
                ITEM_UID + " TEXT," +
                ITEM_TITLE + " TEXT," +
                ITEM_PLACE + " TEXT," +
                ITEM_PRICE + " TEXT," +
                ITEM_DAYP + " TEXT," +
                ITEM_MONTHP + " TEXT," +
                ITEM_YEARP + " TEXT," +
                ITEM_CATEGORY + " TEXT," +
                ITEM_DESCRIPTION + " TEXT," +
                ITEM_IMAGE + " TEXT," +
                ITEM_LAST_UPDATE + " TEXT);");
        Log.d("ItemSql"," CreateTable");
    }

    public static void drop(SQLiteDatabase db) {
        db.execSQL("drop table " + ITEMS_TABLE);
        Log.d("ItemSql"," DropTable");
    }

    public static  void delete(SQLiteDatabase db,String itemId){
        db.delete(ITEMS_TABLE,ITEM_ID + "=?",new String[]{ itemId });
        Log.d("ItemSql"," Delete " + itemId);
    }

    public static String getLastUpdateDate(SQLiteDatabase db){
        Log.d("ItemSql"," GetLastUpdate");
        return LastUpdateSql.getLastUpdate(db,ITEM_ID);
    }
    public static void setLastUpdateDate(SQLiteDatabase db, String date){
        Log.d("ItemSql"," SetLastUpdate" + date);
        LastUpdateSql.setLastUpdate(db,ITEM_ID, date);
    }
}
