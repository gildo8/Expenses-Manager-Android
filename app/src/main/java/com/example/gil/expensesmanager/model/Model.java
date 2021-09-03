package com.example.gil.expensesmanager.model;


import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.example.gil.expensesmanager.MyApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Gil on 17/05/2016.
 */
public class Model {
    private final static Model instance = new Model();
    Context context;
    ModelFirebase modelFirebase;
    ModelCloudinary modelCloudinary;
    ModelSql modelSql;

    private Model(){
        context = MyApplication.getAppContext();
        modelFirebase = new ModelFirebase(MyApplication.getAppContext());
        modelCloudinary = new ModelCloudinary();
        modelSql = new ModelSql(MyApplication.getAppContext());
//        modelSql.DropDb(modelSql.getWritableDB());
//        modelSql.CreateDb(modelSql.getWritableDB());
    }

    public static Model instance(){
        return instance;
    }

    public interface FireBaseStringListener{
        public void onResult(String result);
        public void onCancel();
    }

    public interface GetSingleUserListener{
        public void onResult(User result);
        public void onCancel();
    }

    public interface GetSingleItemListener{
        public void onResult(Item result);
        public void onCancel();
    }

    public interface FireBaseItemsListListener{
        public void onResult(List<Item> result);
        public void onCancel();
    }

    public interface FireBaseUsersListListener{
        public void onResult(List<User> result);
        public void onCancel();
    }

    public interface FireBaseStringListListener{
        public void onResult(List<String> result);
        public void onCancel();
    }

    public interface AddItemListener{
        public void onResult(String uid, String itemId);
        public void onCancel();
    }

    public void RegisterUser(String email, String pass, String name, GetSingleUserListener listener){
        modelFirebase.RegisterUser(email, pass, name,modelSql.getWritableDB(),listener);
    }

    public void LogInUser(String email, String pass, FireBaseStringListener listener){
        modelFirebase.LogInUser(email,pass,listener);
    }

    public String CheckLoggedIn(){
        return modelFirebase.CheckLoggedIn();
    }

    public void LogOutUser(){
        modelFirebase.LogOutUser();
    }

    public void GetSingleUser(String uid,GetSingleUserListener listener){
        modelFirebase.GetSingleUserFireBase(uid,listener);
    }

    public void GetSingleItem(String uid,String itemId,GetSingleItemListener listener){
        modelFirebase.GetSingleItemFireBase(uid,itemId,listener);
    }

    public void AddItem(String uid,Item item, AddItemListener listener){
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = new GregorianCalendar();
        TimeZone timeZone = calendar.getTimeZone();
        calendar.setTimeZone(timeZone);
        Date updateTime = calendar.getInstance(timeZone).getTime();
        String date = null;
        date =  dateFormatGmt.format(updateTime);
        item.setLastUpdated(date);

        modelFirebase.AddItem(uid,item,listener);
    }

    public void DeleteItem(String uid, String itemId,double price, FireBaseStringListener listener){
        modelFirebase.DeleteItem(uid,itemId,price,listener);
        ItemSql.delete(modelSql.getWritableDB(),itemId);
    }

    public void GetAllUsers(String uid, FireBaseUsersListListener listener){
        modelFirebase.GetAllUsers(uid,listener);
    }

    public void AddUserToShareList(String uid, String uidForSharing,String userName, FireBaseStringListener listener){
        modelFirebase.AddUserToShareList(uid,uidForSharing,userName,listener);
    }

    public void GetMySharedList(String uid , FireBaseStringListListener listener){
        modelFirebase.GetMySharedList(uid,listener);
    }

    public void GetItemsByMonth(String uid,String month, FireBaseItemsListListener listener){
        modelFirebase.GetItemsByMonth(uid,month,listener);
    }

    public void GetItemsByCat(String uid,String category, FireBaseItemsListListener listener){
        modelFirebase.GetItemsByCat(uid,category,listener);
    }

    public void GetItemsByPrice(String uid,int price, FireBaseItemsListListener listener){
        modelFirebase.GetItemsByPrice(uid,price,listener);
    }

    public void UpdateStartingBudget(String uid, Object object ,FireBaseStringListener listener){
        modelFirebase.UpdateStartingBudget(uid,object,listener);
    }

    public void GetAllSharedItemsAsynch(final String uid, String toUidShare, final FireBaseItemsListListener listener){
        final String lastUpdate = ItemSql.getLastUpdateDate(modelSql.getReadbleDB());

        modelFirebase.GetAllSharedItemsAsynch(new FireBaseItemsListListener() {
            @Override
            public void onResult(List<Item> result) {
                if(result != null && result.size() > 0){
                    //update the local DB
                    String recentUpdate = lastUpdate;
                    for(Item item : result){
                        ItemSql.addItem(modelSql.getWritableDB(),item);
                        if (recentUpdate == null || item.getLastUpdated().compareTo(recentUpdate) > 0) {
                            recentUpdate = item.getLastUpdated();
                        }
                    }
                    ItemSql.setLastUpdateDate(modelSql.getWritableDB(),recentUpdate);
                }

                List<Item> itemList = ItemSql.getItemsByUid(modelSql.getReadbleDB(),uid);
                listener.onResult(itemList);
            }

            @Override
            public void onCancel() {

            }
        },uid,toUidShare,lastUpdate);
    }

    public void GetWhoSharedWithMe(String uid, FireBaseStringListListener listener){
        modelFirebase.GetWhoSharedWithMe(uid,listener);
    }

    public void UidListToUserList(List<String> uid,FireBaseUsersListListener listener){
        modelFirebase.UidListToUserList(uid,listener);
    }

    public void RemoveFromSharedList(String uid ,String uidToRemove, FireBaseStringListener listener){
        modelFirebase.RemoveFromSharedList(uid,uidToRemove,listener);
    }

    public void saveImage(final Bitmap imageBitmap, final String imageName){
        saveImageToFile(imageBitmap,imageName); // synchronously save image locally
        Thread d = new Thread(new Runnable() {  // asynchronously save image to parse
            @Override
            public void run() throws java.lang.NullPointerException{
                modelCloudinary.saveImage(imageBitmap,imageName);
            }
        });
        d.start();
    }

    public void saveImageToFile(Bitmap imageBitmap, String imageFileName){
        FileOutputStream fos;
        OutputStream out = null;
        try {
            //File dir = context.getExternalFilesDir(null);
            File dir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            if (!dir.exists()) {
                dir.mkdir();
            }
            File imageFile = new File(dir,imageFileName);
            imageFile.createNewFile();

            out = new FileOutputStream(imageFile);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();

            //add the picture to the gallery so we dont need to manage the cache size
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(imageFile);
            mediaScanIntent.setData(contentUri);
            context.sendBroadcast(mediaScanIntent);
            Log.d("SaveImageModel"," Add image to cache: " + imageFileName);

        }
//        catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
        catch (IOException f) {
            f.printStackTrace();
        }
        catch (NullPointerException h){
            h.printStackTrace();
        }
    }

    public interface LoadImageListener{
        public void onResult(Bitmap imageBmp);
    }

    public void loadImage(final String imageName, final LoadImageListener listener) {
        AsyncTask<String,String,Bitmap> task = new AsyncTask<String, String, Bitmap >() {
            @Override
            protected Bitmap doInBackground(String... params) {
                try {
                    Bitmap bmp = loadImageFromFile(imageName);              //first try to fin the image on the device
                    if (bmp == null) {                                      //if image not found - try downloading it from parse
                        bmp = modelCloudinary.loadImage(imageName);
                        if (bmp != null)
                            saveImageToFile(bmp, imageName);    //save the image locally for next time
                    }
                    return bmp;
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                listener.onResult(result);
            }
        };
        task.execute();
    }

    public Bitmap loadImageFromFile(String imageFileName){
        String str = null;
        Bitmap bitmap = null;
        try {
            File dir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            File imageFile = new File(dir,imageFileName);

//            File dir = context.getExternalFilesDir(null);
            InputStream inputStream = new FileInputStream(imageFile);
            bitmap = BitmapFactory.decodeStream(inputStream);
            Log.d("LoadImageModel"," Got image from cache: " + imageFileName);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
//        catch (FileNotFoundException f) {
//            f.printStackTrace();
//        }
        return bitmap;
    }
}
