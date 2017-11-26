package com.mahe.chat;

import android.content.ComponentName;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by hp on 23-11-2017.
 */

public class ContactFragment extends Fragment {

    ContentProviderClient mCProviderClient;
    private RecyclerView recyclerView;
    FloatingActionButton fabCreateGroup;
    MyDB db;
    PubNubService pubNubService;
    boolean isBoubd=false;


    public ContactFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContentResolver cResolver=getContext().getContentResolver();
         mCProviderClient = cResolver.acquireContentProviderClient(ContactsContract.Contacts.CONTENT_URI);
        db=new MyDB(getContext());
        pubNubService=new PubNubService();
        Intent i=new Intent(getContext(),PubNubService.class);
        getContext().bindService(i, serviceConnection,getContext().BIND_AUTO_CREATE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.contactfragment, container, false);

        final ArrayList<String> l=fetchContactsCProviderClient();
//        Toast.makeText(getContext(),l.get(0)+"",Toast.LENGTH_LONG).show();
        recyclerView = (RecyclerView)rootView.findViewById(R.id.recyclercintacts);
        fabCreateGroup=(FloatingActionButton)rootView.findViewById(R.id.fabCreategroup);

        final ContactRecyclerAdapter adp=new ContactRecyclerAdapter(l,getContext(), new ToggleFabInterface() {
            @Override
            public void onChangeState(Boolean val) {
                if(val) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        fabCreateGroup.setImageDrawable(getResources().getDrawable(R.drawable.tick, null));
                    } else {
                        fabCreateGroup.setImageDrawable(getResources().getDrawable(R.drawable.tick));
                    }
                }else {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        fabCreateGroup.setImageDrawable(getResources().getDrawable(R.drawable.group, null));

                    } else {
                        fabCreateGroup.setImageDrawable(getResources().getDrawable(R.drawable.group));
                    }
                    fabCreateGroup.setScaleType(ImageView.ScaleType.CENTER);

                }
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adp);


        fabCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adp.getSelectedItems().size()<=0)
                    Snackbar.make(rootView,"Please long press  contacts to select",Snackbar.LENGTH_LONG).show();
                else if(adp.getSelectedItems().size()>1){
                    String hash="";
                    Collections.sort(adp.getSelectedContacts());
                    for (String s:adp.getSelectedContacts()){
                        hash+=s;
                    }
                    hash=sha1(hash);
                    long res=db.addChannel(hash);
                    if(res>0){
                        Snackbar.make(rootView,"Group Created",Snackbar.LENGTH_LONG).show();

                        Message m=new Message(
                                hash,
                                "Group has been created",
                                DateFormat.getDateTimeInstance().format(new Date()).toString(),
                                1,
                                true
                        );
                        db.insertChat(m);
                        pubNubService.publishMessageAll(adp.getSelectedContacts(),m);

                        adp.clearSelcetion();
                        recyclerView.getAdapter().notifyDataSetChanged();

                    }else
                    {
                        Snackbar.make(rootView,"Group Exixts",Snackbar.LENGTH_LONG).show();
                    }

                }
            }
        });





        return rootView;


    }

    public String sha1(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


    private ArrayList<String> fetchContactsCProviderClient()
    {
        ArrayList<String> mContactList = new ArrayList<>();
        try
        {
            final ArrayList<String> finalMContactList = mContactList;
            new Runnable() {
                @Override
                public void run() {
                    ContentResolver cr =getContext().getContentResolver();
                    Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                            null, null,ContactsContract.Contacts.DISPLAY_NAME);
                    if (cur.getCount() > 0) {
                        while (cur.moveToNext()) {
                            String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                            String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                            // Log.i("Names", name);
                            if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                            {
                                Cursor phones =getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,null, null);
                                while (phones.moveToNext()) {
                                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                    String s=name;
                                    s+=","+phoneNumber;
                                    finalMContactList.add(s);
                                }
                                phones.close();

                            }

                        }
                    }


                }
            }.run();
            java.util.Collections.sort(finalMContactList);
            return finalMContactList;
        }
        catch (Exception e)
        {
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            mContactList = null;
        }
       // java.util.Collections.sort(mContactList);
        return mContactList;

    }

    interface ToggleFabInterface{
        void onChangeState(Boolean v);
    }


    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            pubNubService=((PubNubService.LocalBinder) service).getBinder();
            isBoubd=true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBoubd=false;
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unbindService(serviceConnection);
    }


}
