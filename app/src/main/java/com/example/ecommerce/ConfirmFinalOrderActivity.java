package com.example.ecommerce;

import android.content.Intent;
import android.os.Bundle;

import com.example.ecommerce.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity {

    private EditText nameEdittext,phoneEdittext,addressEdittext,cityEdittext;
    private Button confirmorderbtn;
    private  String TotalAmount ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        TotalAmount =getIntent().getStringExtra("Total Price");
        Toast.makeText(ConfirmFinalOrderActivity.this,"Total Price is = "+TotalAmount,Toast.LENGTH_SHORT).show();

        confirmorderbtn= (Button)findViewById(R.id.confirm_final_order_btn);
        nameEdittext = (EditText)findViewById(R.id.shipment_name);
        phoneEdittext = (EditText)findViewById(R.id.shipment_phone_number);
        addressEdittext = (EditText)findViewById(R.id.shipment_address);
        cityEdittext = (EditText)findViewById(R.id.shipment_city);

        confirmorderbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             Check();
            }


        });

    }
    private void Check() {
       if(TextUtils.isEmpty(nameEdittext.getText().toString())){
           Toast.makeText(ConfirmFinalOrderActivity.this,"please provide your full name",Toast.LENGTH_SHORT).show();
       }
        if(TextUtils.isEmpty(phoneEdittext.getText().toString())){
            Toast.makeText(ConfirmFinalOrderActivity.this,"please provide your phone number",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(addressEdittext.getText().toString())){
            Toast.makeText(ConfirmFinalOrderActivity.this,"please provide your address",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(cityEdittext.getText().toString())){
            Toast.makeText(ConfirmFinalOrderActivity.this,"please provide your city",Toast.LENGTH_SHORT).show();
        }
        else
        {
            confirmOrder();
        }
    }

    private void confirmOrder() {
        final String  saveCurrentDate, saveCurrentTimes ;
        Calendar calForDate =Calendar.getInstance();
        SimpleDateFormat currentDate =new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate =currentDate.format(calForDate.getTime());
        SimpleDateFormat currentTime =new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTimes=currentTime.format(calForDate.getTime());

        final DatabaseReference OrderRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(Prevalent.currentOnlineUser.getPhone());
        final HashMap<String,Object> ordertMap =new HashMap<>();
        ordertMap.put("totalAmount",TotalAmount);
        ordertMap.put("name",nameEdittext.getText().toString());
        ordertMap.put("phone",phoneEdittext.getText().toString());
        ordertMap.put("address",addressEdittext.getText().toString());
        ordertMap.put("city",cityEdittext.getText().toString());
        ordertMap.put("date",saveCurrentDate);
        ordertMap.put("time",saveCurrentTimes);
        ordertMap.put("state","Not shipped");

        OrderRef.updateChildren(ordertMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference().child("Cart List")
                            .child("User View")
                            .child(Prevalent.currentOnlineUser.getPhone())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ConfirmFinalOrderActivity.this,"please provide your city",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ConfirmFinalOrderActivity.this, HomeActivity.class);
                                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK|intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        }
                    });
                }

            }
        });

    }
}