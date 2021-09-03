package com.example.gil.expensesmanager.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
 * Created by gildo on 03/06/2016.
 */
public class UsersFragment extends Fragment {

    List<User> userList;
    FragmentAdapter adapter;
    FragManager fragManager;
    String uid ,uidForSharing,userName;
    ProgressBar progressBar;
    boolean isEmpty = false;
    User user ;
    ImageView addBtn;
    public UsersFragment(){}

    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.users_fragment, container, false);

        Bundle bundle = getArguments();
        uid = bundle.getString("UID");
        setUid(uid);

        final ListView list = (ListView) rootView.findViewById(R.id.users_listview);
        progressBar = (ProgressBar) rootView.findViewById(R.id.usersProgressBar);


        Model.instance().GetAllUsers(uid, new Model.FireBaseUsersListListener() {
            @Override
            public void onResult(List<User> result) {
                if(result.size() > 0) {
                    list.setVisibility(View.VISIBLE);
                    setUserList(result);
                    adapter = new FragmentAdapter();
                    list.setAdapter(adapter);

                }else{
                    Toast.makeText(getActivity().getApplicationContext(),"No Users To Show.",Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    isEmpty = true;
                    list.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancel() {

            }
        });



        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setUser(userList.get(position));
                onCreateDialog().show();
            }
        });

        return rootView;
    }

    public class FragmentAdapter extends BaseAdapter {

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
                Log.d("UsersFragment", "Create View: " + position);
            } else {
                Log.d("UsersFragment", "Use Convert View: " + position);
            }

            final User user = getUserList().get(position);
            TextView userNamelbl = (TextView) convertView.findViewById(R.id.name);
            TextView userBudgetlbl = (TextView) convertView.findViewById(R.id.budget);
            ImageView addBtn = (ImageView) convertView.findViewById(R.id.add_user_share);
            setAddBtn(addBtn);
            final ProgressBar progress = (ProgressBar) convertView.findViewById(R.id.rowImageProgressBar);

            String budget = Double.toString(user.getCurrentBudget());
            setUidForSharing(user.getId());
            userNamelbl.setText(user.getName());
            userBudgetlbl.setText(budget);
            progressBar.setVisibility(View.GONE);

            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setUser(getUserList().get(position));
                    setUserName(user.getName());
                    onCreateDialog().show();
                }
            });

            return convertView;
        }
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
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

    public Dialog onCreateDialog() {
        final User user = getUser();
        AlertDialog.Builder builder = new
                AlertDialog.Builder(getActivity());
        builder.setTitle("Share List");
        builder.setMessage("Add " + user.getName() + " To Share List?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                Model.instance().AddUserToShareList(getUid(),user.getId(),getUserName(), new Model.FireBaseStringListener() {
                    @Override
                    public void onResult(String result) {
                        fragManager = (FragManager) getActivity();
                        fragManager.MySharedListFragment(uid);
                    }

                    @Override
                    public void onCancel() {
                        dialog.dismiss();
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ImageView getAddBtn() {
        return addBtn;
    }

    public void setAddBtn(ImageView addBtn) {
        this.addBtn = addBtn;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
