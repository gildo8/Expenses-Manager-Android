package com.example.gil.expensesmanager.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gil.expensesmanager.FragManager;
import com.example.gil.expensesmanager.R;
import com.example.gil.expensesmanager.model.Model;
import com.example.gil.expensesmanager.model.User;

import java.util.List;

/**
 * Created by gildo on 15/06/2016.
 */
public class MySharedListFragment extends Fragment{

    FragManager fragManager;
    ListAdapter adapter;
    List<String> userListStr;
    List<User> userList;
    String uid ;
    ProgressBar progressBar;
    TextView startFraglbl;
    ListView list;

    public MySharedListFragment() {}

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.my_shared_list_fragment, container, false);
        setHasOptionsMenu(true);

        Bundle bundle = getArguments();
        uid = bundle.getString("UID");
        setUid(uid);

        list = (ListView) rootView.findViewById(R.id.shared_listview);
        progressBar = (ProgressBar) rootView.findViewById(R.id.sharedListProgressBar);
        startFraglbl = (TextView) rootView.findViewById(R.id.start_fragment);

        Model.instance().GetMySharedList(uid, new Model.FireBaseStringListListener() {
            @Override
            public void onResult(List<String> result) {

                Model.instance().UidListToUserList(result, new Model.FireBaseUsersListListener() {
                    @Override
                    public void onResult(List<User> result) {
                        if(result.size() > 0) {
                            list.setVisibility(View.VISIBLE);
                            setUserList(result);
                            adapter = new ListAdapter();
                            list.setAdapter(adapter);

                        }else{
                            progressBar.setVisibility(View.INVISIBLE);
                            startFraglbl.setVisibility(View.VISIBLE);
                            list.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                });

            }

            @Override
            public void onCancel() {

            }
        });

        return rootView;
    }

    public class ListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return getUserList().size();
        }

        @Override
        public Object getItem(int position) {
            return getUserList().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                convertView = inflater.inflate(R.layout.users_list_row_layout, null);
                Log.d("MySharedList", "Create View: " + position);
            } else {
                Log.d("MySharedList", "Use Convert View: " + position);
            }

            ImageView userImage = (ImageView) convertView.findViewById(R.id.add_user_share);
            TextView userNamelbl = (TextView) convertView.findViewById(R.id.name);
            TextView userBudgetlbl = (TextView) convertView.findViewById(R.id.budget);

            userImage.setVisibility(View.INVISIBLE);

            final User user = getUserList().get(position);

            userImage.setImageResource(R.drawable.x);
            userNamelbl.setText(user.getName());

            progressBar.setVisibility(View.GONE);
            startFraglbl.setVisibility(View.INVISIBLE);
            userImage.setVisibility(View.VISIBLE);
            userImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Model.instance().RemoveFromSharedList(getUid(), user.getId(), new Model.FireBaseStringListener() {
                        @Override
                        public void onResult(String result) {
                            Toast.makeText(getActivity().getApplicationContext(),"User " + user.getName() +  " is Deleted from Shared.",Toast.LENGTH_LONG).show();
                            fragManager = (FragManager) getActivity();
                            fragManager.settingsFragment(getUid());
                        }

                        @Override
                        public void onCancel() {

                        }
                    });

                }
            });
            return convertView;
        }
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<String> getUserListStr() {
        return userListStr;
    }

    public void setUserListStr(List<String> userListStr) {
        this.userListStr = userListStr;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.shared_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_user_menu:
                fragManager = (FragManager) getActivity();
                fragManager.usersFragment(uid);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
