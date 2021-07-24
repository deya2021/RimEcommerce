package com.example.ecommerce.Admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.Model.Cart;
import com.example.ecommerce.R;
import com.example.ecommerce.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class AdminUserProductsActivity extends AppCompatActivity {
    private RecyclerView productslist;
    RecyclerView.LayoutManager layoutManager;
    private  DatabaseReference cartlistsRef;
    private String userId= "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_products);

        userId =getIntent().getStringExtra("uid");
        productslist=findViewById(R.id.product_list5);
        productslist.setHasFixedSize(true);
        layoutManager =new LinearLayoutManager(this);
        productslist.setLayoutManager(layoutManager);

        cartlistsRef = FirebaseDatabase.getInstance().getReference().child("Cart List")
        .child("Admin View").child(userId).child("Products");

}

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartlistsRef,Cart.class)
                .build();
        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int i, @NonNull Cart model) {

                holder.txtProductQuantity.setText("Quantity "+model.getQuantity());
                holder.txtProductPrice.setText("Price "+model.getPrice());
                holder.txtProductName.setText("Name "+model.getPname());

            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout,parent,false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };
        productslist.setAdapter(adapter);
        adapter.startListening();
    }
}