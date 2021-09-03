package com.example.gil.expensesmanager.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
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
import com.example.gil.expensesmanager.model.Item;
import com.example.gil.expensesmanager.model.Model;
import com.example.gil.expensesmanager.model.User;

import java.util.List;

/**
 * Created by gildo on 15/06/2016.
 */
public class ListFragment extends Fragment {

    List<Item> itemList;
    MyAdapter adapter;
    FragManager fragManager;
    String uid,toShareUid ;
    ProgressBar progressBar;
    ListView list;
    TextView startFraglbl,userlbl;

    public ListFragment() {}

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_fragment, container, false);

        Bundle bundle = getArguments();
        uid = bundle.getString("UID");
        toShareUid = bundle.getString("UIDTOSHARE");
        setUid(uid);
        setToShareUid(toShareUid);

        list = (ListView) rootView.findViewById(R.id.items_shared_listview);
        progressBar = (ProgressBar) rootView.findViewById(R.id.listProgressBar);
        startFraglbl = (TextView) rootView.findViewById(R.id.start_fragment);
        userlbl = (TextView) rootView.findViewById(R.id.user_items);
        progressBar.setVisibility(View.INVISIBLE);

        Model.instance().GetSingleUser(uid, new Model.GetSingleUserListener() {
            @Override
            public void onResult(User result) {
                userlbl.setText(result.getName());
            }

            @Override
            public void onCancel() {

            }
        });

        Model.instance().GetAllSharedItemsAsynch(uid, toShareUid, new Model.FireBaseItemsListListener() {
            @Override
            public void onResult(List<Item> result) {
                if(result.size() > 0) {
                    startFraglbl.setVisibility(View.INVISIBLE);
                    list.setVisibility(View.VISIBLE);
                    setItemList(result);
                    adapter = new MyAdapter();
                    list.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }else{
                    startFraglbl.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
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
                fragManager = (FragManager) getActivity();
                fragManager.itemDetailsFragment(getUid(), itemList.get(position).getId());
            }
        });
        return rootView;

    }

    public class MyAdapter extends BaseAdapter {

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
                Log.d("ListFragment", "Create View: " + position);
            } else {
                Log.d("ListFragment", "Use Convert View: " + position);
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public ListView getList() {
        return list;
    }

    public void setList(ListView list) {
        this.list = list;
    }

    public String getToShareUid() {
        return toShareUid;
    }

    public void setToShareUid(String toShareUid) {
        this.toShareUid = toShareUid;
    }
}
