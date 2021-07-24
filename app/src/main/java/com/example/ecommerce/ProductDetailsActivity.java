package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.ecommerce.Model.Products;
import com.example.ecommerce.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {

    private Button addToCartBtn;
    private ImageView productImage;
    private ElegantNumberButton numberButton;
    private TextView productPrice,productDescription,productName;
    private  String productId="",state ="Not shipped";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        productId = getIntent().getStringExtra("pid");

        addToCartBtn = (Button) findViewById(R.id.pd_add_to_cart_button);
        numberButton = (ElegantNumberButton) findViewById(R.id.number_btn);
        productImage = (ImageView) findViewById(R.id.product_image_details);
        productPrice = (TextView) findViewById(R.id.product_price_details);
        productDescription = (TextView) findViewById(R.id.product_description_details);
        productName = (TextView) findViewById(R.id.product_name_details);

        getProductDetails(productId);
        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(state.equals("Order placed")||state.equals("Order shipped")) {
                    Toast.makeText(ProductDetailsActivity.this, "you can buy more products", Toast.LENGTH_LONG).show();
                }
                    else {
                        addingToCartList();
                    }

                }
            
        });
    }

            private void addingToCartList() {
                String saveCurrentTimes, saveCurrentDate;

                Calendar calForDate =Calendar.getInstance();
                SimpleDateFormat currentDate =new SimpleDateFormat("MMM dd, yyyy");
                saveCurrentDate =currentDate.format(calForDate.getTime());
                SimpleDateFormat currentTime =new SimpleDateFormat("HH:mm:ss a");
                saveCurrentTimes=currentTime.format(calForDate.getTime());

                DatabaseReference CartListRef =FirebaseDatabase.getInstance().getReference().child("Cart List");
                final HashMap<String,Object> cartMap =new HashMap<>();
                cartMap.put("pid",productId);
                cartMap.put("pname",productName.getText().toString());
                cartMap.put("price",productPrice.getText().toString());
                cartMap.put("date",saveCurrentDate);
                cartMap.put("time",saveCurrentTimes);
                cartMap.put("quantity",numberButton.getNumber());
                cartMap.put("discount","");
                CartListRef.child("User View").child(Prevalent.currentOnlineUser.getPhone())
                        .child("Products").child(productId)
                        .updateChildren(cartMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    CartListRef.child("Admin View").child(Prevalent.currentOnlineUser.getPhone())
                                        .child("Products").child(productId)
                                        .updateChildren(cartMap)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(ProductDetailsActivity.this, "Added to cart list", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(ProductDetailsActivity.this, HomeActivity.class);
                                                        startActivity(intent);
                                                    }
                                                }

                                            });}
                            }
                        });

            }


    @Override
    protected void onStart() {
        super.onStart();
        CheckOrderState();
    }

    private void getProductDetails(String productId) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        productRef.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Products products= snapshot.getValue(Products.class);

                    productName.setText(products.getPname());
                    productDescription.setText(products.getDescription());
                    productPrice.setText(products.getPrice());
                    Picasso.get().load(products.getImage()).into(productImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private  void CheckOrderState()
    {
        DatabaseReference orderRef ;
        orderRef =FirebaseDatabase.getInstance().getReference()
                .child("Orders").child(Prevalent.currentOnlineUser.getPhone());
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String shippingState =snapshot.child("state").toString();


                    if(shippingState.equals("shipped"))
                    {
                     state ="Order shipped";
                    }
                    else if(shippingState.equals("Not shipped"))
                    {
                        state ="Order placed";
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}