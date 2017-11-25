package com.mahe.chat;

import android.content.Context;
import android.content.Intent;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by hp on 25-11-2017.
 */

public class ChatFragmentRecyclerAdapter extends RecyclerView.Adapter<ChatFragmentRecyclerAdapter.ViewHolder> {
    private List<String> values;
    Context ctx;

    public ChatFragmentRecyclerAdapter(List<String> l,Context c){
        ctx=c;
        values=l;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(ctx).inflate(R.layout.contactresource,parent,false);
        ChatFragmentRecyclerAdapter.ViewHolder vh = new ChatFragmentRecyclerAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.latMessage.setText(".............................");
        if(values.get(position).length()>15) {
            holder.ContactName.setText("Group");
        }else
            holder.ContactName. setText(values.get(position));
       // Toast.makeText(ctx,values.get(position),Toast.LENGTH_LONG).show();
        holder.rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(ctx,Messages.class);

                i.putExtra("phone",values.get(position));
                ctx.startActivity(i);
            }
        });

    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView ContactName;
        public TextView latMessage;
        public RelativeLayout rl;

        public ViewHolder(View v) {
            super(v);

            ContactName = (TextView) v.findViewById(R.id.ContactName);
             latMessage= (TextView) v.findViewById(R.id.ContactNumber);
            rl=(RelativeLayout)v.findViewById(R.id.relativeContactSource);

        }
    }

    @Override
    public int getItemCount() {
        return  values==null?0:values.size();
    }
}
