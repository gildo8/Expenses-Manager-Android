package com.example.gil.expensesmanager.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;


import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by gildo on 20/05/2016.
 */
public class ModelFirebase {
    Firebase myFirebaseRef;
    User user = new User();

    ModelFirebase(Context context){
        Firebase.setAndroidContext(context);
        myFirebaseRef = new Firebase("https://resplendent-heat-4292.firebaseio.com/");
    }

    public void RegisterUser(String email, String pass, final String name, final SQLiteDatabase db, final Model.GetSingleUserListener listener){
        myFirebaseRef.createUser(email,pass, new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                Log.d("ModelFireBase"," Register User " + name);
                String uid = result.get("uid").toString();
                Firebase frRef = myFirebaseRef.child("Users").child(uid);
                AddNewUserToData(frRef,uid,name);

                user.setId(uid);
                user.setName(name);
                UserSql.addNewUser(db,user);
                listener.onResult(user);
            }
            @Override
            public void onError(FirebaseError firebaseError) {
                listener.onCancel();
            }
        });
    }

    public void LogInUser(final String email, String pass, final Model.FireBaseStringListener listener){
        myFirebaseRef.authWithPassword(email,pass, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                Log.d("ModelFireBase"," User Log in " + email);
                listener.onResult(authData.getUid().toString());
            }
            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                listener.onCancel();
            }
        });
    }

    public void AddNewUserToData(Firebase frRef,String uid,String name){
        User user = new User();
        user.setName(name);
        user.setId(uid);
        frRef.setValue(user);
    }

    public String CheckLoggedIn(){
        AuthData authData = myFirebaseRef.getAuth();
        if(authData != null){
            Log.d("ModelFireBase"," User LoggedIn!");
            String newUid = authData.getUid().toString();
            return newUid;
        }
        else{
            return null;
        }
    }

    public void LogOutUser(){
        Log.d("ModelFireBase"," User LogOff");
        myFirebaseRef.unauth();
    }

    public void GetSingleUserFireBase(String uid,final Model.GetSingleUserListener listener){
        Firebase usRef = myFirebaseRef.child("Users").child(uid);
        usRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Log.d("ModelFireBase"," GetUser " + user.getName());
                listener.onResult(user);
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
                listener.onCancel();
            }
        });
    }

    public void GetSingleItemFireBase(String uid,String itemId,final Model.GetSingleItemListener listener){
        String itemIdString = itemId;
        
        Firebase usRef = myFirebaseRef.child("Items").child(uid).child(itemIdString);
        usRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Item item = snapshot.getValue(Item.class);
                Log.d("ModelFireBase"," GetItem " + item.getTitle());
                listener.onResult(item);
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
                listener.onCancel();
            }
        });
    }

    public void AddItem(final String uid, final Item item,final Model.AddItemListener listener){
        String itemIdString = item.getId();
        final 
        Firebase itemRef = myFirebaseRef.child("Items").child(uid).child(itemIdString);
        final Firebase usRef = myFirebaseRef.child("Users").child(uid);

        itemRef.setValue(item,new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                Log.d("ModelFireBase"," AddItem" + item.getTitle());

                Model.instance().GetSingleUser(uid, new Model.GetSingleUserListener() {
                    @Override
                    public void onResult(User result) {
                        double curBudg = result.getCurrentBudget();
                        double newItem = item.getPrice();
                        double finalCurBudg = (curBudg - newItem);
                        String curBudgetFinalStr;

                        curBudgetFinalStr = new DecimalFormat("##.##").format(finalCurBudg);
                        Map<String, Object> currentBudget = new HashMap<String, Object>();
                        currentBudget.put("currentBudget",curBudgetFinalStr);

                        usRef.updateChildren(currentBudget, new Firebase.CompletionListener() {
                            @Override
                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                listener.onResult(uid,item.getId());
                            }
                        });
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                listener.onResult(uid,item.getId());
            }
        });
    }

    public void DeleteItem(final String uid, String itemId,final double price, final Model.FireBaseStringListener listener){
        String itemIdString = itemId;
        final Firebase usRef = myFirebaseRef.child("Users").child(uid);
        Firebase itRef = myFirebaseRef.child("Items").child(uid).child(itemIdString);
        itRef.removeValue(new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                Log.d("ModelFireBase"," Delete Item");
                Model.instance().GetSingleUser(uid, new Model.GetSingleUserListener() {
                    @Override
                    public void onResult(User result) {
                        double curBudg = result.getCurrentBudget();
                        double newItem = price;
                        double finalCurBudg = (curBudg + newItem);
                        Map<String, Object> currentBudget = new HashMap<String, Object>();
                        currentBudget.put("currentBudget", finalCurBudg);

                        usRef.updateChildren(currentBudget, new Firebase.CompletionListener() {
                            @Override
                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                listener.onResult("Good");
                            }
                        });
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });
    }

    public void GetAllUsers(String uid ,final Model.FireBaseUsersListListener listener){
        Firebase usRef = null;
        final List<User> userList;
        try {
            usRef = myFirebaseRef.child("Users");
            userList = new LinkedList<User>();
            usRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot itSnapshot : snapshot.getChildren()) {
                        Log.d("ModelFireBase"," GetUsersList");
                        User user = itSnapshot.getValue(User.class);
                        userList.add(user);
                    }
                    listener.onResult(userList);
                }
                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.out.println("The read failed: " + firebaseError.getMessage());
                    listener.onCancel();
                }
            });
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public void AddUserToShareList(final String uid , final String uidForSharing,final String userName, final Model.FireBaseStringListener listener){
        Firebase userRef = myFirebaseRef.child("ShareList").child(uid).child(uidForSharing);

        userRef.setValue(userName,new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                Log.d("ModelFireBase"," AddUserToShareList");
                listener.onResult("Done.");
            }
        });
    }

    //Return The List of the specific uid shared with.
    public void GetMySharedList(final String uid, final Model.FireBaseStringListListener listener) {
        Firebase usRef = null;
        final List<String> userSharedList;
        try {
            usRef = myFirebaseRef.child("ShareList").child(uid);
            userSharedList = new LinkedList<String>();

            usRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Log.d("ModelFireBase"," GetSharedList");
                    for (DataSnapshot itSnapshot : snapshot.getChildren()) {
                        String user = itSnapshot.getKey();
                        userSharedList.add(user);
                    }
                    listener.onResult(userSharedList);
                }
                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.out.println("The read failed: " + firebaseError.getMessage());
                    listener.onCancel();
                }
            });
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    //Return the list of specific uid that users share with him.
    public void GetWhoSharedWithMe(final String uid, final Model.FireBaseStringListListener listener){
        Firebase usRef = null;
        final List<String> userList;
        try {
            usRef = myFirebaseRef.child("ShareList");
            userList = new LinkedList<String>();
            usRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Log.d("ModelFireBase"," WhoSharedWithMe");
                    for (DataSnapshot itSnapshot : snapshot.getChildren()) {
                        if (itSnapshot.hasChildren()) {
                            for (DataSnapshot it : itSnapshot.getChildren()) {
                                if (it.getKey().equals(uid)) {
                                    String user = itSnapshot.getKey();
                                    userList.add(user);
                                }
                            }
                        }
                    }

                    listener.onResult(userList);

                }
                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.out.println("The read failed: " + firebaseError.getMessage());
                    listener.onCancel();
                }
            });
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public void GetItemsByMonth(String uid,String month, final Model.FireBaseItemsListListener listener){
        Firebase usRef = null;
        int monthToInt = 0;

        switch (month){
            case "January":
                monthToInt = 1;
                break;
            case "February":
                monthToInt = 2;
                break;
            case "March":
                monthToInt = 3;
                break;
            case "April":
                monthToInt = 4;
                break;
            case "May":
                monthToInt = 5;
                break;
            case "June":
                monthToInt = 6;
                break;
            case "July":
                monthToInt = 7;
                break;
            case "August":
                monthToInt = 8;
                break;
            case "September":
                monthToInt = 9;
                break;
            case "October":
                monthToInt = 10;
                break;
            case "November":
                monthToInt = 11;
                break;
            case "December":
                monthToInt = 12;
                break;
            case "All":
                monthToInt = 13;
                break;
        }
        final List<Item> itemList;
        final Query queryRef;
        try {
            usRef = myFirebaseRef.child("Items").child(uid);
            if((monthToInt > 0) && (monthToInt < 13)) {
                queryRef = usRef.orderByChild("monthPurchase").equalTo(monthToInt);
                Log.d("ModelFireBase"," Get Items by Month: " +  monthToInt + " ");
            }else{
                Log.d("ModelFireBase"," Get Items by Month: All");
                queryRef = myFirebaseRef.child("Items").child(uid);
            }
            itemList = new LinkedList<Item>();
            queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot itSnapshot : snapshot.getChildren()) {
                        Item item = itSnapshot.getValue(Item.class);
                        itemList.add(item);
                    }
                    listener.onResult(itemList);
                }
                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.out.println("The read failed: " + firebaseError.getMessage());
                    listener.onCancel();
                }
            });
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public void GetItemsByCat(String uid,String category, final Model.FireBaseItemsListListener listener){
        Firebase usRef = null;
        final List<Item> itemList;
        final Query queryRef;
        try {
            usRef = myFirebaseRef.child("Items").child(uid);
            if(!category.equals("All")) {
                Log.d("ModelFireBase"," Get Items by Category: " + category);
                queryRef = usRef.orderByChild("category").equalTo(category);
            }else{
                Log.d("ModelFireBase"," Get Items by Category: All");
                queryRef = myFirebaseRef.child("Items").child(uid);
            }
            itemList = new LinkedList<Item>();
            queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot itSnapshot : snapshot.getChildren()) {
                        Item item = itSnapshot.getValue(Item.class);
                        itemList.add(item);
                    }
                    listener.onResult(itemList);
                }
                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.out.println("The read failed: " + firebaseError.getMessage());
                    listener.onCancel();
                }
            });
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public void GetItemsByPrice(String uid,int price, final Model.FireBaseItemsListListener listener){
        Firebase usRef = null;
        final List<Item> itemList;
        final Query queryRef;
        try {
            usRef = myFirebaseRef.child("Items").child(uid);
            if(price >= 0) {
                queryRef = usRef.orderByChild("price").startAt(price);
                Log.d("ModelFireBase"," Get Items by Price: " + price + " ");
            }else{
                queryRef = myFirebaseRef.child("Items").child(uid);
                Log.d("ModelFireBase"," Get Items by Price: All");
            }
            itemList = new LinkedList<Item>();
            queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot itSnapshot : snapshot.getChildren()) {
                        Item item = itSnapshot.getValue(Item.class);
                        itemList.add(item);
                    }
                    listener.onResult(itemList);
                }
                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.out.println("The read failed: " + firebaseError.getMessage());
                    listener.onCancel();
                }
            });
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public void UpdateStartingBudget(String uid, Object object , final Model.FireBaseStringListener listener){
        final Firebase usRef = myFirebaseRef.child("Users").child(uid);
        Map<String, Object> startingBudget = new HashMap<String, Object>();
        final Map<String, Object> currentBudget = new HashMap<String, Object>();

        startingBudget.put("budget", object);
        currentBudget.put("currentBudget", object);

        usRef.updateChildren(startingBudget, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                Log.d("ModelFireBase"," UpdateBudget...");
                usRef.updateChildren(currentBudget, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        listener.onResult("Good");
                    }
                });
            }
        });
    }

    public void GetAllSharedItemsAsynch(final Model.FireBaseItemsListListener listener, String uid,String toShareUid,String lastUpdate){
        final List<Item> itemList;
        try {
            Firebase toUidRef = myFirebaseRef.child("Items").child(uid);
            Query queryRef = toUidRef.orderByChild("lastUpdated").startAt(lastUpdate);

            itemList = new LinkedList<Item>();
            queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Log.d("ModelFireBase"," GetAllSharedItems");
                    for (DataSnapshot itSnapshot : snapshot.getChildren()) {
                        Item item = itSnapshot.getValue(Item.class);
                        itemList.add(item);
                    }
                    listener.onResult(itemList);
                }
                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.out.println("The read failed: " + firebaseError.getMessage());
                    listener.onCancel();
                }
            });
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public void UidListToUserList(final List<String> uid, final Model.FireBaseUsersListListener listener){
        final List<User> users = new LinkedList<User>();

        Firebase usRef = myFirebaseRef.child("Users");
        usRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot itSnapshot : snapshot.getChildren()) {
                    User user = itSnapshot.getValue(User.class);
                    if(uid.contains(user.getId())){
                        users.add(user);
                    }

                }
                listener.onResult(users);
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
                listener.onCancel();
            }
        });
    }

    public void RemoveFromSharedList(String uid, String uidToRemove ,final Model.FireBaseStringListener listener){
        Firebase userRef = myFirebaseRef.child("ShareList").child(uid).child(uidToRemove);
        userRef.removeValue(new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                Log.d("ModelFireBase"," RemoveFromSharedList");
                listener.onResult("Good");
            }
        });
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
