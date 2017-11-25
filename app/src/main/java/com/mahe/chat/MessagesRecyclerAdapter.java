package com.mahe.chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by hp on 23-11-2017.
 */

public class MessagesRecyclerAdapter extends RecyclerView.Adapter<MessagesRecyclerAdapter.ViewHolder> {

    private List<Message> values;
    Context ctx;

    public MessagesRecyclerAdapter(List<Message> myDataset, Context c) {
        values = myDataset;
        ctx=c;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtInfo;
        public TextView txtMessage;
        public LinearLayout linearLayout,content;

        public ViewHolder(View v) {
            super(v);

            txtInfo = (TextView) v.findViewById(R.id.txtchatmessageInfo);
            txtMessage = (TextView) v.findViewById(R.id.txtchatmessageMessage);
            linearLayout=(LinearLayout) v.findViewById(R.id.contentWithBackground);
            content=(LinearLayout) v.findViewById(R.id.ChatMessagecontent);


        }
    }

    public void add(int position, Message item) {
        values.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        values.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public MessagesRecyclerAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

      View v=LayoutInflater.from(ctx).inflate(R.layout.chatmessage,parent,false);
        ViewHolder vh = new ViewHolder(v);


        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Message chatMessage=values.get(position);
        setAlignment(holder, chatMessage.getIsMe());
        holder.txtMessage.setText(chatMessage.getMessage());
        holder.txtInfo.setText(chatMessage.getTime());
    }


    private void setAlignment(ViewHolder holder, int isMe) {
        if (isMe==1) {
            holder.linearLayout.setBackgroundResource(R.drawable.inmsg);

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.linearLayout.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;

            holder.linearLayout.setLayoutParams(layoutParams);
            holder.linearLayout.setPadding(50,20,50,20);

            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT,0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.content.setLayoutParams(lp);
            layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.txtMessage.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.txtInfo.setLayoutParams(layoutParams);
        } else {
            holder.linearLayout.setBackgroundResource(R.drawable.outmsg);

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.linearLayout.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.linearLayout.setLayoutParams(layoutParams);
            holder.linearLayout.setPadding(50,20,50,20);


            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            holder.content.setLayoutParams(lp);
            layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.txtMessage.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.txtInfo.setLayoutParams(layoutParams);
        }
    }



    @Override
    public int getItemCount() {

        return values==null?0: values.size();
    }


}
