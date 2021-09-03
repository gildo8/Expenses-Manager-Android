package com.example.gil.expensesmanager.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gil.expensesmanager.FragManager;
import com.example.gil.expensesmanager.R;
import com.example.gil.expensesmanager.model.Item;
import com.example.gil.expensesmanager.model.Model;
import com.example.gil.expensesmanager.model.User;

/**
 * Created by gildo on 27/05/2016.
 */
public class ItemDetailsFragment extends Fragment {
    FragManager fragManager;
    private String uidForMenu;
    private String itemIdForMenu;
    private double price;
    String title;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_details_fragment, container, false);
        setHasOptionsMenu(true);

        Bundle bundle = getArguments();
        final String uid = bundle.getString("uid");
        final String itemId = bundle.getString("itemId");

        setUidForMenu(uid);
        setItemIdForMenu(itemId);

        final TextView itemTitle = (TextView) rootView.findViewById(R.id.itemTitle);
        final TextView itemPlace = (TextView) rootView.findViewById(R.id.itemPlace);
        final TextView itemPrice = (TextView) rootView.findViewById(R.id.itemPrice);
        final TextView itemDay = (TextView) rootView.findViewById(R.id.itemDay);
        final TextView itemMonth = (TextView) rootView.findViewById(R.id.itemMonth);
        final TextView itemYear = (TextView) rootView.findViewById(R.id.itemYear);
        final TextView itemCategory = (TextView) rootView.findViewById(R.id.itemCategory);
        final TextView itemDesc = (TextView) rootView.findViewById(R.id.itemDescription);
        final ImageView itemImg = (ImageView) rootView.findViewById(R.id.itemImage);
        final ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.destailsProgressBar);

        progressBar.setVisibility(View.VISIBLE);
        Model.instance().GetSingleItem(uid,itemId, new Model.GetSingleItemListener() {
            @Override
            public void onResult(Item result) {
                setPrice(result.getPrice());
                String price = Double.toString(result.getPrice());
                String day = Integer.toString(result.getDayPurchase());
                String month = Integer.toString(result.getMonthPurchase());
                String year = Integer.toString(result.getYearPurchase());

                if(!result.getImage().isEmpty()) {
                    Model.instance().loadImage(result.getImage(), new Model.LoadImageListener() {
                        @Override
                        public void onResult(Bitmap imageBmp) {
                            itemImg.setImageBitmap(imageBmp);
                        }
                    });
                }

                if(!result.getPlace().isEmpty()){
                    itemPlace.setText(result.getPlace());
                }else{
                    itemPlace.setText(R.string.no_place);
                }

                if(!(result.getCategory() == null)){
                    itemCategory.setText(result.getCategory());
                }else{
                    itemCategory.setText(R.string.no_category);
                }
                if(!result.getDescription().isEmpty()) {
                    itemDesc.setText(result.getDescription());
                }else{
                    itemDesc.setText(R.string.no_desc);
                }
                itemTitle.setText(result.getTitle());
                setTitle(result.getTitle());
                itemPrice.setText(price);
                itemDay.setText(day);
                itemMonth.setText(month);
                itemYear.setText(year);

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancel() {

            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.details_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_item:
                onCreateDialog().show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String getUidForMenu() {
        return uidForMenu;
    }

    public void setUidForMenu(String uidForMenu) {
        this.uidForMenu = uidForMenu;
    }

    public String getItemIdForMenu() {
        return itemIdForMenu;
    }

    public void setItemIdForMenu(String itemIdForMenu) {
        this.itemIdForMenu = itemIdForMenu;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Dialog onCreateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete "+ getTitle());
        builder.setMessage("Delete The Item?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                Model.instance().DeleteItem(getUidForMenu(), getItemIdForMenu(),getPrice(), new Model.FireBaseStringListener() {
                    @Override
                    public void onResult(String uid) {
                        fragManager = (FragManager) getActivity();
                        fragManager.historyFragment(getUidForMenu());
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
