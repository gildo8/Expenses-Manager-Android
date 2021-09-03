package com.example.gil.expensesmanager.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gil.expensesmanager.R;
import com.example.gil.expensesmanager.model.Item;
import com.example.gil.expensesmanager.model.Model;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gil on 16/05/2016.
 */
public class StatisticsFragment extends Fragment {

    String choiceMenu,uid;
    ArrayList<Entry> entries = new ArrayList<>();
    ArrayList<String> labels = new ArrayList<String>();
    PieChart pieChart;
    PieDataSet dataset;
    PieData data;
    ProgressBar progressBar;

    public StatisticsFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.statistics_fragment, container, false);
        setHasOptionsMenu(true);

        Bundle bundle = getArguments();
        final String uid = bundle.getString("UID");
        setUid(uid);
        pieChart = (PieChart) rootView.findViewById(R.id.chart);
        setPieChart(pieChart);
        progressBar = (ProgressBar) rootView.findViewById(R.id.statisticsProgressBar);
        setProgressBar(progressBar);
        getProgressBar().setVisibility(View.INVISIBLE);
        getPieChart().setVisibility(View.VISIBLE);

        return rootView;
    }


    public String getChoiceMenu() {
        return choiceMenu;
    }

    public void setChoiceMenu(String choiceMenu) {
        this.choiceMenu = choiceMenu;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public ArrayList<Entry> getEntries() {
        return entries;
    }

    public void setEntries(ArrayList<Entry> entries) {
        this.entries = entries;
    }

    public ArrayList<String> getLabels() {
        return labels;
    }

    public void setLabels(ArrayList<String> labels) {
        this.labels = labels;
    }

    public PieChart getPieChart() {
        return pieChart;
    }

    public void setPieChart(PieChart pieChart) {
        this.pieChart = pieChart;
    }

    public PieData getData() {
        return data;
    }

    public void setData(PieData data) {
        this.data = data;
    }

    public PieDataSet getDataset() {
        return dataset;
    }

    public void setDataset(PieDataSet dataset) {
        this.dataset = dataset;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.statistics_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.select_month:
                getProgressBar().setVisibility(View.VISIBLE);
                getPieChart().setVisibility(View.INVISIBLE);
                setChoiceMenu("month");

                final ArrayList<String> monthLabels = new ArrayList<String>();
                final ArrayList<Entry> monthEntries = new ArrayList<>();

                Model.instance().GetItemsByMonth(getUid(), "All", new Model.FireBaseItemsListListener() {
                    @Override
                    public void onResult(List<Item> result) {

                        int januaryNum = 0;
                        int februaryNum = 0;
                        int marchNum = 0;
                        int aprilNum = 0;
                        int mayNum = 0;
                        int juneNum = 0;
                        int julyNum = 0;
                        int augustNum = 0;
                        int sepNum = 0;
                        int octNum = 0;
                        int novNum = 0;
                        int decNum = 0;

                        for(Item item : result){
                            if(item.getMonthPurchase() != 0){
                                switch (item.getMonthPurchase()){
                                    case 1:
                                        januaryNum++;
                                        break;
                                    case 2:
                                        februaryNum++;
                                        break;
                                    case 3:
                                        marchNum++;
                                        break;
                                    case 4:
                                        aprilNum++;
                                        break;
                                    case 5:
                                        mayNum++;
                                        break;
                                    case 6:
                                        juneNum++;
                                        break;
                                    case 7:
                                        julyNum++;
                                        break;
                                    case 8:
                                        augustNum++;
                                        break;
                                    case 9:
                                        sepNum++;
                                        break;
                                    case 10:
                                        octNum++;
                                        break;
                                    case 11:
                                        novNum++;
                                        break;
                                    case 12:
                                        decNum++;
                                        break;
                                }
                            }
                        }

                        if(januaryNum != 0){
                            monthLabels.add("January");
                            monthEntries.add(new Entry(januaryNum,0));
                        }
                        if(februaryNum != 0){
                            monthLabels.add("February");
                            monthEntries.add(new Entry(februaryNum,1));
                        }
                        if(marchNum != 0){
                            monthLabels.add("March");
                            monthEntries.add(new Entry(marchNum,2));
                        }
                        if(aprilNum != 0){
                            monthLabels.add("April");
                            monthEntries.add(new Entry(aprilNum,3));
                        }
                        if(mayNum != 0){
                            monthLabels.add("May");
                            monthEntries.add(new Entry(mayNum,4));
                        }
                        if(juneNum != 0){
                            monthLabels.add("June");
                            monthEntries.add(new Entry(juneNum,5));
                        }
                        if(julyNum != 0){
                            monthLabels.add("July");
                            monthEntries.add(new Entry(julyNum,6));
                        }
                        if(augustNum != 0){
                            monthLabels.add("August");
                            monthEntries.add(new Entry(augustNum,7));
                        }
                        if(sepNum != 0){
                            monthLabels.add("September");
                            monthEntries.add(new Entry(sepNum,8));
                        }
                        if(octNum != 0){
                            monthLabels.add("October");
                            monthEntries.add(new Entry(octNum,9));
                        }
                        if(novNum != 0){
                            monthLabels.add("November");
                            monthEntries.add(new Entry(novNum,10));
                        }
                        if(decNum != 0){
                            monthLabels.add("December");
                            monthEntries.add(new Entry(decNum,11));
                        }

                        setLabels(monthLabels);
                        setEntries(monthEntries);

                        setDataset(new PieDataSet(getEntries(), "# of Calls"));
                        setData(new PieData(getLabels(), getDataset()));
                        getDataset().setColors(ColorTemplate.COLORFUL_COLORS);
                        getDataset().setValueTextSize(10f);
                        getPieChart().setDescription("");
                        getPieChart().setData(getData());
                        getPieChart().setCenterText("Months");
                        getPieChart().animateY(1000);
                        getPieChart().getLegend().setEnabled(false);

                        getProgressBar().setVisibility(View.INVISIBLE);
                        getPieChart().setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onCancel() {

                    }
                });

                return true;
            case R.id.select_category:
                getProgressBar().setVisibility(View.VISIBLE);
                getPieChart().setVisibility(View.INVISIBLE);
                setChoiceMenu("category");

                final ArrayList<String> categoryLabels = new ArrayList<String>();
                final ArrayList<Entry> categoryEntries = new ArrayList<>();

                Model.instance().GetItemsByCat(getUid(),"All", new Model.FireBaseItemsListListener() {
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
                            categoryEntries.add(new Entry(healthNum,0)); // Health
                        }
                        if(fAdNum > 0){
                            categoryLabels.add("Food And Drink");
                            categoryEntries.add(new Entry(fAdNum,1)); // Food And Drink
                        }
                        if(leisureNum > 0){
                            categoryLabels.add("Leisure");
                            categoryEntries.add(new Entry(leisureNum,2)); // Leisure
                        }
                        if(transNum > 0){
                            categoryLabels.add("Transportation");
                            categoryEntries.add(new Entry(transNum,3)); // Transportation
                        }
                        if(otherNum > 0){
                            categoryLabels.add("Other");
                            categoryEntries.add(new Entry(otherNum,4)); // Other
                        }

                        setLabels(categoryLabels);
                        setEntries(categoryEntries);

                        setDataset(new PieDataSet(getEntries(), "# of Calls"));
                        setData(new PieData(getLabels(), getDataset()));
                        getDataset().setColors(ColorTemplate.COLORFUL_COLORS);
                        getDataset().setValueTextSize(10f);
                        getPieChart().setDescription("");
                        getPieChart().setData(getData());
                        getPieChart().setCenterText("Categories");
                        getPieChart().animateY(1000);
                        getPieChart().getLegend().setEnabled(false);

                        getProgressBar().setVisibility(View.INVISIBLE);
                        getPieChart().setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }
}
