package com.example.gil.expensesmanager.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gil.expensesmanager.FragManager;
import com.example.gil.expensesmanager.R;
import com.example.gil.expensesmanager.model.Item;
import com.example.gil.expensesmanager.model.Model;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gil on 16/05/2016.
 */
public class HistoryFragment extends Fragment {


    List<Item> itemList;
    FragmentAdapter adapter;
    FragManager fragManager;
    String uid ;
    ProgressBar progressBar;
    boolean isEmpty = false;
    String monthSelected,categorySelected;
    int priceSelected;
    TextView choiceLbl,startFraglbl;
    ListView list;
    String choiceMenu;

    public HistoryFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.history_fragment, container, false);
        setHasOptionsMenu(true);

        Bundle bundle = getArguments();
        uid = bundle.getString("UID");
        list = (ListView) rootView.findViewById(R.id.items_listview);
        progressBar = (ProgressBar) rootView.findViewById(R.id.historyProgressBar);
        choiceLbl = (TextView) rootView.findViewById(R.id.selected);
        startFraglbl = (TextView) rootView.findViewById(R.id.start_fragment);
        progressBar.setVisibility(View.INVISIBLE);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fragManager = (FragManager) getActivity();
                fragManager.itemDetailsFragment(uid, itemList.get(position).getId());
            }
        });

        return rootView;
    }



    public class FragmentAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return getItemList().size();
        }

        @Override
        public Object getItem(int position) {
            return getItemList().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                convertView = inflater.inflate(R.layout.item_list_row_layout, null);
                Log.d("HistoryFragment", "Create View: " + position);
            } else {
                Log.d("HistoryFragment", "Use Convert View: " + position);
            }

            Item item = getItemList().get(position);
            String day = Integer.toString(item.getDayPurchase());
            String month = Integer.toString(item.getMonthPurchase());
            String year = Integer.toString(item.getYearPurchase());
            String price = Double.toString(item.getPrice());

            final ImageView itemImage = (ImageView) convertView.findViewById(R.id.itemImage);
            TextView itemTitlelbl = (TextView) convertView.findViewById(R.id.itemTitle);
            TextView itemPricelbl = (TextView) convertView.findViewById(R.id.itemPrice);
            TextView itemCategorylbl = (TextView) convertView.findViewById(R.id.itemCategory);
            final TextView itemDaylbl = (TextView) convertView.findViewById(R.id.itemDay);
            final TextView itemMonthlbl = (TextView) convertView.findViewById(R.id.itemMonth);
            final TextView itemYearlbl= (TextView) convertView.findViewById(R.id.itemYear);
            final ProgressBar progress = (ProgressBar) convertView.findViewById(R.id.rowImageProgressBar);

            if(!item.getImage().isEmpty()) {
                progress.setVisibility(View.VISIBLE);
                Model.instance().loadImage(item.getImage(), new Model.LoadImageListener() {
                    @Override
                    public void onResult(Bitmap imageBmp) {
                        itemImage.setImageBitmap(imageBmp);
                        progress.setVisibility(View.INVISIBLE);
                    }
                });
            }

            if(item.getCategory() != null){
                itemCategorylbl.setText(item.getCategory());
            }else{
                itemCategorylbl.setText(R.string.no_category);
            }

            itemTitlelbl.setText(item.getTitle());
            itemPricelbl.setText(price);
            itemDaylbl.setText(day);
            itemMonthlbl.setText(month);
            itemYearlbl.setText(year);

            progressBar.setVisibility(View.GONE);
            return convertView;
        }
    }



    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }

    public Dialog onCreateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final String[] dataMonth = {"All", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        final String[] dataCategory = {"All","Health","Food And Drink","Leisure","Transportation","Other"};


        switch (getChoiceMenu()){
            case "month":
                getStartFraglbl().setVisibility(View.INVISIBLE);
                final boolean[] selected = new boolean[dataMonth.length];
                builder.setTitle(R.string.select_month)
                        .setItems(dataMonth, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                selected[which] = true;
                                String month = "";
                                for (int i = 0; i < selected.length; i++) {
                                    if (selected[i]) {
                                        month = dataMonth[i];
                                    }
                                }
                                setMonthSelected(month);

                                Model.instance().GetItemsByMonth(uid, getMonthSelected(), new Model.FireBaseItemsListListener() {
                                    @Override
                                    public void onResult(List<Item> result) {
                                        if (result.size() > 0) {
                                            double totalPrice = 0 ;
                                            for (Item item : result) {
                                                totalPrice += item.getPrice();
                                            }
                                            String totalStr = Double.toString(totalPrice);
                                            totalStr = new DecimalFormat("##.##").format(totalPrice);

                                            list.setVisibility(View.VISIBLE);
                                            setItemList(result);
                                            setSelectedLbl(getMonthSelected() + " / Total: " + totalStr);
                                            getSelectedLbl().setVisibility(View.VISIBLE);
                                            progressBar.setVisibility(View.INVISIBLE);
                                            adapter = new FragmentAdapter();
                                            list.setAdapter(adapter);

                                        } else {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            getStartFraglbl().setVisibility(View.VISIBLE);
                                            setSelectedLbl(getMonthSelected());
                                            isEmpty = true;
                                            list.setVisibility(View.INVISIBLE);
                                        }
                                    }

                                    @Override
                                    public void onCancel() {

                                    }
                                });

                            }
                        });

                break;

            case "category":
                getStartFraglbl().setVisibility(View.INVISIBLE);
                final boolean[] selectedCat = new boolean[dataCategory.length];
                builder.setTitle(R.string.select_category)
                        .setItems(dataCategory, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                selectedCat[which] = true;
                                String cat = "";
                                for (int i = 0; i < selectedCat.length; i++) {
                                    if (selectedCat[i]) {
                                        cat = dataCategory[i];
                                    }
                                }
                                setCategorySelected(cat);

                                Model.instance().GetItemsByCat(uid, getCategorySelected(), new Model.FireBaseItemsListListener() {
                                    @Override
                                    public void onResult(List<Item> result) {
                                        if (result.size() > 0) {
                                            double totalPrice = 0 ;
                                            for (Item item : result) {
                                                totalPrice += item.getPrice();
                                            }
                                            String totalStr = Double.toString(totalPrice);
                                            totalStr = new DecimalFormat("##.##").format(totalPrice);
                                            list.setVisibility(View.VISIBLE);
                                            setItemList(result);
                                            setSelectedLbl(getCategorySelected() + " / Total: " + totalStr);
                                            getSelectedLbl().setVisibility(View.VISIBLE);
                                            progressBar.setVisibility(View.INVISIBLE);
                                            adapter = new FragmentAdapter();
                                            list.setAdapter(adapter);

                                        } else {
                                            getStartFraglbl().setVisibility(View.VISIBLE);
                                            progressBar.setVisibility(View.INVISIBLE);
                                            setSelectedLbl(getCategorySelected());
                                            isEmpty = true;
                                            list.setVisibility(View.INVISIBLE);
                                        }
                                    }

                                    @Override
                                    public void onCancel() {

                                    }
                                });

                            }
                        });
                break;
            case "price":
                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View priceView = inflater.inflate(R.layout.price_menu_layout, null);
                getStartFraglbl().setVisibility(View.INVISIBLE);
                builder.setTitle(R.string.select_price);
                builder.setView(priceView);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final EditText priceTxt = (EditText) priceView.findViewById(R.id.price);
                        final String priceStr = priceTxt.getText().toString();
                        int price = Integer.parseInt(priceStr);
                        Model.instance().GetItemsByPrice(uid,price, new Model.FireBaseItemsListListener() {
                            @Override
                            public void onResult(List<Item> result) {
                                list.setVisibility(View.VISIBLE);
                                setItemList(result);
                                setSelectedLbl("Order by at least: " + priceStr + "₪");
                                getSelectedLbl().setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.INVISIBLE);
                                adapter = new FragmentAdapter();
                                list.setAdapter(adapter);
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        getStartFraglbl().setVisibility(View.VISIBLE);
                    }
                });

                break;
        }


        return builder.create();
    }

    public int getPriceSelected() {
        return priceSelected;
    }

    public void setPriceSelected(int priceSelected) {
        this.priceSelected = priceSelected;
    }

    public String getMonthSelected() {
        return monthSelected;
    }

    public void setMonthSelected(String monthSelected) {
        this.monthSelected = monthSelected;
    }

    public String getCategorySelected() {
        return categorySelected;
    }

    public void setCategorySelected(String categorySelected) {
        this.categorySelected = categorySelected;
    }

    public TextView getSelectedLbl() {
        return choiceLbl;
    }

    public void setSelectedLbl(String monthLbl) {
        this.choiceLbl.setText(monthLbl);
    }

    public TextView getStartFraglbl() {
        return startFraglbl;
    }

    public void setStartFraglbl(TextView startFraglbl) {
        this.startFraglbl = startFraglbl;
    }

    public String getChoiceMenu() {
        return choiceMenu;
    }

    public void setChoiceMenu(String choiceMenu) {
        this.choiceMenu = choiceMenu;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.history_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.select_month:
                setChoiceMenu("month");
                onCreateDialog().show();
                return true;
            case R.id.select_category:
                setChoiceMenu("category");
                onCreateDialog().show();
                return true;
            case R.id.select_price:
                setChoiceMenu("price");
                onCreateDialog().show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
