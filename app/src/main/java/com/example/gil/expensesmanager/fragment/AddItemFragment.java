package com.example.gil.expensesmanager.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.gil.expensesmanager.FragManager;
import com.example.gil.expensesmanager.R;
import com.example.gil.expensesmanager.model.CategoryEditText;
import com.example.gil.expensesmanager.model.DateEditText;
import com.example.gil.expensesmanager.model.Item;
import com.example.gil.expensesmanager.model.Model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Gil on 17/05/2016.
 */
public class AddItemFragment extends Fragment {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    Bitmap imageBitmap = null;
    FragManager fragManager;
    String uid, imageFileName = null;
    String itemId;
    boolean dateChanged, picture = false;
    Item itemToSave = new Item();
    boolean isAvailable = false;

    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");

    private EditText itemTitle;
    private EditText itemPlace;
    private EditText itemPrice;
    private DateEditText itemDate;
    private CategoryEditText itemCategory;
    private EditText itemDesc;
    private ImageView itemImg;
    Fragment thisFrag = this;
    public AddItemFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_item_fragment, container, false);
        setHasOptionsMenu(true);
        thisFrag = this;
        Calendar calendar = new GregorianCalendar();
        TimeZone timeZone = calendar.getTimeZone();
        calendar.setTimeZone(timeZone);

        Date updateTime = calendar.getInstance(timeZone).getTime();
        String currentTime = formatter.format(updateTime);

        Bundle bundle = getArguments();
        setUid(bundle.getString("userId"));
        setItemId(currentTime);

        itemTitle = (EditText) rootView.findViewById(R.id.itemTitle);
        itemPlace = (EditText) rootView.findViewById(R.id.itemPlace);
        itemPrice = (EditText) rootView.findViewById(R.id.itemPrice);
        itemDate = (DateEditText) rootView.findViewById(R.id.itemDate);
        itemCategory = (CategoryEditText) rootView.findViewById(R.id.itemCategory);
        itemDesc = (EditText) rootView.findViewById(R.id.itemDescription);
        itemImg = (ImageView) rootView.findViewById(R.id.itemImage);


        itemDate.setOnDateSetListener(new DateEditText.OnDateSetListener() {
            @Override
            public void dateSet(int year, int month, int day) {
                dateChanged = true;
                itemToSave.setDayPurchase(day);
                itemToSave.setMonthPurchase(month);
                itemToSave.setYearPurchase(year);
            }
        });

        itemCategory.setOnCategorySetListener(new CategoryEditText.OnCategorySetListener() {
            @Override
            public void categorySet(String choice) {
                itemToSave.setCategory(choice);
            }
        });

        itemImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takingPicture();
                picture = true;
            }
        });

        return rootView;
    }

    private void takingPicture(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            Toast.makeText(getActivity().getApplicationContext(),R.string.pic_saved,Toast.LENGTH_LONG).show();

            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            itemImg.setImageBitmap(imageBitmap);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_item_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_item:

                if(getItemTitle().getText().toString().isEmpty()){
                    Toast.makeText(getActivity().getApplicationContext(),R.string.fill_title,Toast.LENGTH_LONG).show();
                }
                else{
                    if(getItemPrice().getText().toString().isEmpty()){
                        Toast.makeText(getActivity().getApplicationContext(),R.string.fill_price,Toast.LENGTH_LONG).show();
                    }else{
                        //All Good
                        double price = Double.parseDouble(itemPrice.getText().toString());
                        itemToSave.setId(getItemId());
                        itemToSave.setUid(getUid());
                        itemToSave.setTitle(itemTitle.getText().toString());
                        itemToSave.setPlace(itemPlace.getText().toString());
                        itemToSave.setPrice(price);
                        itemToSave.setDescription(itemDesc.getText().toString());

                        if(picture){
                            String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
                            imageFileName = itemTitle.getText().toString()+ "_" + timeStamp + ".jpg";
                            Model.instance().saveImage(imageBitmap,imageFileName);

                            itemToSave.setImage(imageFileName);
                        }
                        else{
                            itemToSave.setImage("");
                        }

                        if(!dateChanged){
                            Calendar calendar = new GregorianCalendar();
                            int year = calendar.get(Calendar.YEAR);
                            int month = calendar.get(Calendar.MONTH);
                            int day = calendar.get(Calendar.DAY_OF_MONTH);
                            itemToSave.setDayPurchase(day);
                            itemToSave.setMonthPurchase(month+1);
                            itemToSave.setYearPurchase(year);
                        }

                        Model.instance().AddItem(getUid(),itemToSave, new Model.AddItemListener() {
                            @Override
                            public void onResult(String uid, String itemId) {
                                getActivity().getFragmentManager().beginTransaction().remove(thisFrag).commit();
                                fragManager = (FragManager) getActivity();
                                fragManager.itemDetailsFragment(getUid(),getItemId());
                            }

                            @Override
                            public void onCancel() {

                            }
                        });


                    }
                }

                return true;
            case R.id.cancel_item:
                fragManager = (FragManager) getActivity();
                fragManager.historyFragment(getUid());
        }

        return super.onOptionsItemSelected(item);
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }


    public EditText getItemTitle() {
        return itemTitle;
    }

    public EditText getItemPlace() {
        return itemPlace;
    }

    public EditText getItemPrice() {
        return itemPrice;
    }

    public DateEditText getItemDate() {
        return itemDate;
    }

    public CategoryEditText getItemCategory() {
        return itemCategory;
    }

    public EditText getItemDesc() {
        return itemDesc;
    }

    public ImageView getItemImg() {
        return itemImg;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
