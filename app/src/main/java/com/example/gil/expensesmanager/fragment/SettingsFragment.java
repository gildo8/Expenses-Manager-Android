package com.example.gil.expensesmanager.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gil.expensesmanager.FragManager;
import com.example.gil.expensesmanager.R;
import com.example.gil.expensesmanager.model.Item;
import com.example.gil.expensesmanager.model.Model;
import com.example.gil.expensesmanager.model.User;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Gil on 16/05/2016.
 */
public class SettingsFragment extends Fragment{
    String uid ,uidForSharing;
    boolean isBudgetSet = false;
    TextView budget;
    FragManager fragManager;
    User user;

    public SettingsFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.settings_fragment, container, false);
        final Button setBudgetBtn = (Button) rootView.findViewById(R.id.set_budget);
        Button mySharedList = (Button) rootView.findViewById(R.id.share_list);
        Button whoSharedWithMe = (Button) rootView.findViewById(R.id.who_shared);

        budget = (TextView) rootView.findViewById(R.id.budget);

        Bundle bundle = getArguments();
        uid = bundle.getString("UID");
        setUid(uid);

        mySharedList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragManager = (FragManager) getActivity();
                fragManager.MySharedListFragment(uid);
            }
        });

        whoSharedWithMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragManager = (FragManager) getActivity();
                fragManager.WhoSharedWithMe(uid);
            }
        });

        Model.instance().GetSingleUser(uid, new Model.GetSingleUserListener() {
            @Override
            public void onResult(User result) {
                user = result;
                if(result.getBudget() != 0){
                    setBudgetSet(true);
                }

                if(isBudgetSet){
                    budget.setTextColor(Color.GREEN);
                    setBudgetBtn.setEnabled(false);
                    String budgetStr = Double.toString(result.getBudget());
                    budget.setText(budgetStr);
                }else{
                    setBudgetBtn.setEnabled(true);
                    budget.setTextColor(0xFFFF0101);
                    setBudgetBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onCreateDialog().show();
                        }
                    });
                }
            }

            @Override
            public void onCancel() {

            }
        });

        return rootView;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUidForSharing() {
        return uidForSharing;
    }

    public void setUidForSharing(String uidForSharing) {
        this.uidForSharing = uidForSharing;
    }

    public boolean isBudgetSet() {
        return isBudgetSet;
    }

    public void setBudgetSet(boolean budgetSet) {
        isBudgetSet = budgetSet;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Dialog onCreateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View budgetView = inflater.inflate(R.layout.price_menu_layout, null);

        builder.setTitle("Enter Budget:");
        builder.setView(budgetView);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final EditText budgetTxt = (EditText) budgetView.findViewById(R.id.price);
                final String budgetStr = budgetTxt.getText().toString();

                Model.instance().UpdateStartingBudget(uid,budgetStr, new Model.FireBaseStringListener() {
                    @Override
                    public void onResult(String result) {
                        budget.setText(budgetStr);
                        budget.setTextColor(Color.GREEN);
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
            }
        });




        return builder.create();
    }
}
