package com.mahe.chat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by hp on 23-11-2017.
 */

public class ChatFragment extends Fragment {
    RecyclerView recyclerView;
    MyDB db;
    public ChatFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.chatfragment, container, false);

        db=new MyDB(getContext());

        final ArrayList<String> l=db.getDistinctChats();
//        Toast.makeText(getContext(),l.get(0)+"",Toast.LENGTH_LONG).show();
        recyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerchafragment);

        ChatFragmentRecyclerAdapter adp=new ChatFragmentRecyclerAdapter(l,getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adp);

        return rootView;



    }
}
