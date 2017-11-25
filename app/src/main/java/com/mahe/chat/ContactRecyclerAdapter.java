package com.mahe.chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 23-11-2017.
 */

public class ContactRecyclerAdapter extends RecyclerView.Adapter<ContactRecyclerAdapter.ViewHolder> {

    private List<String> values;
    Context ctx;
    private SparseBooleanArray  selectedItems;
    List<String> selectedContacts;
    ContactFragment.ToggleFabInterface toggleFabInterface;


    public ContactRecyclerAdapter(List<String> myDataset, Context c, ContactFragment.ToggleFabInterface ti) {
        values = myDataset;
        ctx=c;

        toggleFabInterface=ti;
        selectedItems=new SparseBooleanArray();
        selectedContacts=new ArrayList<>();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView ContactName;
        public TextView ContactNumber;
        public RelativeLayout rl;

        public ViewHolder(View v) {
            super(v);

            ContactName = (TextView) v.findViewById(R.id.ContactName);
            ContactNumber = (TextView) v.findViewById(R.id.ContactNumber);
            rl=(RelativeLayout)v.findViewById(R.id.relativeContactSource);

        }
    }

    public  void clearSelcetion(){
        selectedItems=new SparseBooleanArray();
        selectedContacts=new ArrayList<>();
    }

    public SparseBooleanArray getSelectedItems() {
        return selectedItems;
    }

    public void setSelectedItems(SparseBooleanArray selectedItems) {
        this.selectedItems = selectedItems;
    }

    public void setSelectedContacts(List<String> selectedContacts) {
        this.selectedContacts = selectedContacts;
    }

    public List<String> getSelectedContacts() {
        return selectedContacts;
    }

    public void add(int position, String item) {
        values.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        values.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public ContactRecyclerAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

      View v=LayoutInflater.from(ctx).inflate(R.layout.contactresource,parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final String []name = values.get(position).split(",");
        boolean isGroup=false;
        holder.rl.setSelected(selectedItems.get(position, false));

        holder.ContactName.setText(name[0]);
        holder.ContactNumber.setText(name[1]);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            holder.rl.setBackground(ContextCompat.getDrawable(ctx, R.drawable.recyclerback));
        } else {
            holder.rl.setBackgroundDrawable(ContextCompat.getDrawable(ctx, R.drawable.recyclerback));

        }


        holder.rl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onGroupItemsSelectionToggle(holder,position);
                return true;
            }
        });

        holder.rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedItems.size()<=0){
                    Intent i=new Intent(ctx,Messages.class);
                    i.putExtra("phone",name[1]);
                    ctx.startActivity(i);
                }else
                     onGroupItemsSelectionToggle(holder,position);

            }
        });


    }


    void onGroupItemsSelectionToggle(ViewHolder holder,int position){

        if (selectedItems.get(position, false)) {
            selectedItems.delete(position);
            holder.rl.setSelected(false);
            selectedContacts.remove(holder.ContactNumber.getText().toString());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.rl.setBackground(ContextCompat.getDrawable(ctx, R.drawable.recyclerback));
            } else {
                holder.rl.setBackgroundDrawable(ContextCompat.getDrawable(ctx, R.drawable.recyclerback));

            }
        } else {
            selectedItems.put(position, true);
            holder.rl.setSelected(true);
            holder.rl.setBackgroundColor(Color.GRAY);
            selectedContacts.add(holder.ContactNumber.getText().toString());
        }

        if(selectedItems.size()>0){
            toggleFabInterface.onChangeState(true);
        }
        else {
            toggleFabInterface.onChangeState(false);
        }


    }



    @Override
    public int getItemCount() {

        return values==null?0: values.size();
    }


}
