package com.example.gil.expensesmanager.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gil.expensesmanager.FragManager;
import com.example.gil.expensesmanager.MainActivity;
import com.example.gil.expensesmanager.R;
import com.example.gil.expensesmanager.SignInActivity;
import com.example.gil.expensesmanager.model.Item;
import com.example.gil.expensesmanager.model.Model;
import com.example.gil.expensesmanager.model.ModelSql;
import com.example.gil.expensesmanager.model.User;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment {

    FragManager fragManager;
    private String uidForMenu;
    TextView monthLbl;
    TextView currentBdg;
    HorizontalBarChart barChart;
    BarData data;
    BarDataSet dataSet;
    ProgressBar progressBar;

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.home_fragment, container, false);
        setHasOptionsMenu(true);


        Bundle bundle = getArguments();
        final String uid = bundle.getString("UID");
        setUidForMenu(uid);

        progressBar = (ProgressBar) rootView.findViewById(R.id.homeProgressBar);
        setProgressBar(progressBar);
        barChart = (HorizontalBarChart) rootView.findViewById(R.id.chart);

        monthLbl = (TextView) rootView.findViewById(R.id.month);
        currentBdg = (TextView) rootView.findViewById(R.id.current_budget);

        final Typeface face= Typeface.createFromAsset(getActivity().getAssets(), "fonts/BOOKOS.TTF");
        currentBdg.setTypeface(face);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
        String month_name = month_date.format(calendar.getTime());
        int daylbl = calendar.get(Calendar.DAY_OF_MONTH);
        int yearlbl = calendar.get(Calendar.YEAR);
        String yearToString = Integer.toString(yearlbl);
        String dayToString = Integer.toString(daylbl);

        monthLbl.setText(dayToString + " " + month_name + " " + yearToString);
        Model.instance().GetSingleUser(uid, new Model.GetSingleUserListener() {
            @Override
            public void onResult(User result) {
                String budget = Double.toString(result.getCurrentBudget());
                if(!budget.equals("0.0")){

                    if(budget.contains("-")){
                        currentBdg.setTextColor(Color.RED);
                        currentBdg.setText(budget + " ₪");
                    }
                    else {
                        currentBdg.setText(budget + " ₪");
                    }

                }else{
                    onCreateDialog().show();
                }
            }

            @Override
            public void onCancel() {

            }
        });


        final ArrayList<String> categoryLabels = new ArrayList<String>();
        final ArrayList<BarEntry> categoryEntries = new ArrayList<>();

        Model.instance().GetItemsByCat(uid,"All", new Model.FireBaseItemsListListener() {
            @Override
            public void onResult(List<Item> result) {
                int healthNum = 0;
                int fAdNum = 0;
                int leisureNum = 0;
                int transNum = 0;
                int otherNum = 0;
                for(Item item : result){
                    if(item.getCategory() != null) {
                        switch (item.getCategory()) {
                            case "Health":
                                healthNum++;
                                break;
                            case "Food And Drink":
                                fAdNum++;
                                break;
                            case "Leisure":
                                leisureNum++;
                                break;
                            case "Transportation":
                                transNum++;
                                break;
                            case "Other":
                                otherNum++;
                                break;
                        }
                    }
                }
                if(healthNum > 0){
                    categoryLabels.add("Health");
                    categoryEntries.add(new BarEntry(healthNum,0)); // Health
                }
                if(fAdNum > 0){
                    categoryLabels.add("Food And Drink");
                    categoryEntries.add(new BarEntry(fAdNum,1)); // Food And Drink
                }
                if(leisureNum > 0){
                    categoryLabels.add("Leisure");
                    categoryEntries.add(new BarEntry(leisureNum,2)); // Leisure
                }
                if(transNum > 0){
                    categoryLabels.add("Transportation");
                    categoryEntries.add(new BarEntry(transNum,3)); // Transportation
                }
                if(otherNum > 0){
                    categoryLabels.add("Other");
                    categoryEntries.add(new BarEntry(otherNum,4)); // Other
                }

                if(categoryLabels.size() > 0) {
                    setDataSet(new BarDataSet(categoryEntries, ""));
                    setData(new BarData(categoryLabels, getDataSet()));
                    getDataSet().setColors(ColorTemplate.COLORFUL_COLORS);
                    getBarChart().setData(getData());
                    getDataSet().setValueTextSize(7f);
                    getBarChart().setDescription("");
                    getBarChart().getLegend().setEnabled(false);
                    getBarChart().getAxisLeft().setDrawLabels(false);
                    getBarChart().getAxisRight().setDrawLabels(false);

                    getBarChart().animateY(1000);
                    getProgressBar().setVisibility(View.INVISIBLE);
                }else{
                    getProgressBar().setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onCancel() {

            }
        });


        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout_menu:
                Model.instance().LogOutUser();
                Intent intent = new Intent(getActivity().getApplicationContext(), SignInActivity.class);
                Toast.makeText(getActivity().getApplicationContext(),R.string.logged_out,Toast.LENGTH_LONG).show();
                startActivity(intent);
                getActivity().getFragmentManager().beginTransaction().remove(this).commit();
                getActivity().finish();
                return true;

            case R.id.add_item:
                fragManager = (FragManager) getActivity();
                fragManager.addItemFragment(getUidForMenu());
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



    public HorizontalBarChart getBarChart() {
        return barChart;
    }

    public void setBarChart(HorizontalBarChart barChart) {
        this.barChart = barChart;
    }

    public BarData getData() {
        return data;
    }

    public void setData(BarData data) {
        this.data = data;
    }

    public BarDataSet getDataSet() {
        return dataSet;
    }

    public void setDataSet(BarDataSet dataSet) {
        this.dataSet = dataSet;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }


    public Dialog onCreateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View priceView = inflater.inflate(R.layout.price_menu_layout, null);
        builder.setTitle("Set Budget");
        builder.setView(priceView);
        builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final EditText priceTxt = (EditText) priceView.findViewById(R.id.price);
                final String priceStr = priceTxt.getText().toString();
                int price = Integer.parseInt(priceStr);

                Model.instance().UpdateStartingBudget(getUidForMenu(), price, new Model.FireBaseStringListener() {
                    @Override
                    public void onResult(String result) {
                        Toast.makeText(getActivity().getApplicationContext(),"Budget Changed!",Toast.LENGTH_LONG).show();
                        fragManager = (FragManager) getActivity();
                        fragManager.budgetFragment(getUidForMenu());
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });
        builder.setNegativeButton("Later", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        return builder.create();
    }
}
