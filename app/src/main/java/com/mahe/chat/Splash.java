package com.mahe.chat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Splash extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    Button btnLogin;
    AlertDialog alert;
    LayoutInflater inflater;
    View v;
    EditText txtPhone;
    String mPhoneNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        askPermission();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    void askPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        }
        else {
            try {
                TelephonyManager tMgr = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                mPhoneNumber = tMgr.getLine1Number();
            }catch (Exception ex){
                // Toast.makeText(getApplicationContext(),ex.getMessage(),Toast.LENGTH_LONG).show();
            }
            login();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if(ConstantsCollection.CHANNEL.isEmpty()){
                    try {
                        TelephonyManager tMgr = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                        mPhoneNumber = tMgr.getLine1Number();
                    }catch (Exception ex){
                        // Toast.makeText(getApplicationContext(),ex.getMessage(),Toast.LENGTH_LONG).show();
                    }
                    login();
                }

            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }
    }



    void login(){
        if (alert == null)
            alert = getBuilder().create();
        alert.setCanceledOnTouchOutside(false);
        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                dialogInterface.dismiss();
                Splash.this.finish();
            }
        });
        alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                dialogInterface.dismiss();
                Splash.this.finish();

            }
        });
        alert.show();

    }

    @NonNull
    private AlertDialog.Builder getBuilder() {
        final AlertDialog.Builder alert1 = new AlertDialog.Builder(this);
        inflater = Splash.this.getLayoutInflater();
        if (v == null) {
            v = inflater.inflate(R.layout.login, null, false);
        } else {
            ((ViewGroup) v.getParent()).removeView(v);
        }
        alert1.setView(v);

        txtPhone = (EditText) v.findViewById(R.id.txtphone);
        btnLogin = (Button) v.findViewById(R.id.btn_login);
        txtPhone.setText(mPhoneNumber);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!txtPhone.getText().toString().isEmpty()&&txtPhone.getText().length()==10){

                    alert.dismiss();
                    ConstantsCollection.CHANNEL=txtPhone.getText().toString();
                    ConstantsCollection.UUID=txtPhone.getText().toString();
                    startActivity(new Intent(getApplicationContext(),Home.class));
                   // startActivity(new Intent(getApplicationContext(),Messages.class));
                    finish();

                }else {
                    Snackbar snack = Snackbar.make(v, "Please Enter Your Phone Number", Snackbar.LENGTH_LONG);
                    snack.show();
                }
            }
        });
        return alert1;
    }


}
