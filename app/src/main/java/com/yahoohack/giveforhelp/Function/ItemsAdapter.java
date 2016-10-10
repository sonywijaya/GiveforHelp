package com.yahoohack.giveforhelp.Function;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yahoohack.giveforhelp.R;

import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.MyViewHolder> {

    private List<Item> mItemList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView userName, userContact, postType, itemName, itemDescription;

        public MyViewHolder(View view) {
            super(view);
            userName = (TextView) view.findViewById(R.id.userName);
            userContact = (TextView) view.findViewById(R.id.userContact);
            postType = (TextView) view.findViewById(R.id.postType);
            itemName = (TextView) view.findViewById(R.id.itemName);
            itemDescription = (TextView) view.findViewById(R.id.itemDescription);
        }
    }


    public ItemsAdapter(List<Item> itemList) {
        this.mItemList = itemList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Item item = mItemList.get(position);
        holder.userName.setText(item.getUserName());
        holder.userContact.setText(item.getUserContact());
        holder.postType.setText(item.getPostType());
        holder.itemName.setText(item.getItemName());
        holder.itemDescription.setText(item.getItemDescription());
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }
}
