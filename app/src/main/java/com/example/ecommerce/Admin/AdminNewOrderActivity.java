package com.example.ecommerce.Admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.ecommerce.Model.AdminOrders;
import com.example.ecommerce.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class AdminNewOrderActivity extends AppCompatActivity {
private RecyclerView orderlist;
private DatabaseReference orderRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_order);

        orderRef= FirebaseDatabase.getInstance().getReference().child("Orders");
        orderlist = findViewById(R.id.order_list);
        orderlist.setLayoutManager(new LinearLayoutManager(this ));

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<AdminOrders> options=
                new FirebaseRecyclerOptions.Builder<AdminOrders>()
                .setQuery(orderRef,AdminOrders.class)
                .build();
        FirebaseRecyclerAdapter<AdminOrders,AdminOrdersViewHolder> adapter = new FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AdminOrdersViewHolder holder, final int position, @NonNull AdminOrders model) {

                holder.userName.setText("Name :"+model.getName());
                holder.userPhoneNumber.setText("Phone :"+model.getPhone());
                holder.userTotalPrice.setText("Total is :"+model.getTotalAmount());
                holder.userDateTime.setText("Order at :"+model.getDate() +" "+ model.getTime());
                holder.usershippinAddress.setText("Shipping Address :"+model.getAddress()+","+model.getCity());

                holder.showOrderBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String uID =getRef(position).getKey();
                        Intent intent = new Intent(AdminNewOrderActivity.this, AdminUserProductsActivity.class);
                        intent.putExtra("uid",model.getPhone());
                        startActivity(intent);
                    }
                });
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence options[] =new  CharSequence[]
                        {
                            "yes",
                                "no"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewOrderActivity.this);
                        builder.setTitle("have you shipped this order Product ?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if( which == 0){
                                    String uID =getRef(position).getKey();
                                    removerOrder(uID);
                                }
                                else {
                                  finish();
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout,parent,false);
            return  new AdminOrdersViewHolder(view);
            }
        };
        orderlist.setAdapter(adapter);
        adapter.startListening();

    }

    private void removerOrder(String uID) {
        orderRef.child(uID).removeValue();
    }

    public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder {
        public TextView userName ,userPhoneNumber,userTotalPrice,userDateTime,usershippinAddress;
        public Button showOrderBtn;

        public AdminOrdersViewHolder( View itemView) {
            super(itemView);
            userName =itemView.findViewById(R.id.order_user_name);
            userPhoneNumber =itemView.findViewById(R.id.order_phone_number);
            userTotalPrice =itemView.findViewById(R.id.order_total_price);
            userDateTime =itemView.findViewById(R.id.order_date);
            usershippinAddress =itemView.findViewById(R.id.order_address_city);
            showOrderBtn =itemView.findViewById(R.id.show_all_products_btn);
        }
    }
}