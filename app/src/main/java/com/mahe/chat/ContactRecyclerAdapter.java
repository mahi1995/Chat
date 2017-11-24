package com.mahe.chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by hp on 23-11-2017.
 */

public class ContactRecyclerAdapter extends RecyclerView.Adapter<ContactRecyclerAdapter.ViewHolder> {

    private List<String> values;
    Context ctx;

    public ContactRecyclerAdapter(List<String> myDataset,Context c) {
        values = myDataset;
        ctx=c;
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

        holder.ContactName.setText(name[0]);
        holder.ContactNumber.setText(name[1]);
        holder.rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ctx,holder.ContactNumber.getText(),Toast.LENGTH_LONG).show();
            }
        });


    }



    @Override
    public int getItemCount() {

        return values==null?0: values.size();
    }


}
