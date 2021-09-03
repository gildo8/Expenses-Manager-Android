package com.example.gil.expensesmanager.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gil.expensesmanager.FragManager;
import com.example.gil.expensesmanager.R;
import com.example.gil.expensesmanager.model.Item;
import com.example.gil.expensesmanager.model.Model;
import com.example.gil.expensesmanager.model.User;

import java.util.List;

/**
 * Created by Gil on 16/05/2016.
 */
public class BudgetFragment extends Fragment {
    FragManager fragManager;
    public BudgetFragment(){}
    private String uidForMenu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.budget_fragment, container, false);
        setHasOptionsMenu(true);

        Bundle bundle = getArguments();
        final String uid = bundle.getString("UID");
        setUidForMenu(uid);

        final TextView startingBudgetlbl = (TextView) rootView.findViewById(R.id.starting_budget);
        final TextView currentBudgetlbl = (TextView) rootView.findViewById(R.id.current_budget);

        Model.instance().GetSingleUser(uid, new Model.GetSingleUserListener() {
            @Override
            public void onResult(final User user) {
                String budToString;
                String curBudToString;
                budToString = Double.toString(user.getBudget());
                startingBudgetlbl.setText(budToString);
                curBudToString = Double.toString(user.getCurrentBudget());
                currentBudgetlbl.setText(curBudToString);
            }

            @Override
            public void onCancel() {

            }
        });
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.budget_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
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

}
