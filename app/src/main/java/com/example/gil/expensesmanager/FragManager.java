package com.example.gil.expensesmanager;

/**
 * Created by Gil on 17/05/2016.
 */
public interface FragManager {
    public void addItemFragment(String uid);
    public void budgetFragment(String uid);
    public void itemDetailsFragment(String uid,String itemId);
    public void statisticsFragment(String uid);
    public void historyFragment(String uid);
    public void settingsFragment(String uid);
    public void MySharedListFragment(String uid);
    public void usersFragment(String uid);
    public void listFragment(String uid,String toUidShare);
    public void WhoSharedWithMe(String uid);
}
