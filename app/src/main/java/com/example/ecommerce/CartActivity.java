package com.example.ecommerce;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.ecommerce.Model.Cart;
import com.example.ecommerce.Model.Products;
import com.example.ecommerce.Prevalent.Prevalent;
import com.example.ecommerce.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CartActivity extends AppCompatActivity {
private RecyclerView recyclerView;
private RecyclerView.LayoutManager layoutManager;
private Button processbtn;
private TextView txtTotalAmount,txtmsg1;
private  int overTotalPrice =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView =findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager =new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        processbtn =(Button)findViewById(R.id.next_process_btn);
        txtTotalAmount =(TextView) findViewById(R.id.total_price);
        txtmsg1 =(TextView) findViewById(R.id.msg1);

        processbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtTotalAmount.setText("Total Price = "+ String.valueOf(overTotalPrice));
                Intent intent = new Intent(CartActivity.this, ConfirmFinalOrderActivity.class);
                intent.putExtra("Total Price",String.valueOf(overTotalPrice));
                startActivity(intent);
                finish();
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        CheckOrderState();
        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<Cart> options= new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef.child("User View")
                .child(Prevalent.currentOnlineUser.getPhone())
                        .child("Products"),Cart.class)
                .build();
        FirebaseRecyclerAdapter<Cart , CartViewHolder> adapter=
                new FirebaseRecyclerAdapter<Cart , CartViewHolder>(options){
                    @Override
                    public void onBindViewHolder(@NonNull CartViewHolder holder, int position ,@NonNull Cart model) {
                        holder.txtProductQuantity.setText("Quantity "+model.getQuantity());
                        holder.txtProductPrice.setText("Price "+model.getPrice());
                        holder.txtProductName.setText(model.getPname());

                        int onTypeProductPrice = ((Integer.valueOf(model.getPrice())))*Integer.valueOf(model.getQuantity());
                        overTotalPrice =overTotalPrice+onTypeProductPrice;

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[]=new CharSequence[]
                                {
                                    "Edit",
                                    "Remove"
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                                builder.setTitle("Cart Options");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(which == 0){
                                            Intent intent = new Intent(CartActivity.this, ProductDetailsActivity.class);
                                            intent.putExtra("pid",model.getPid());
                                            startActivity(intent);
                                        }
                                       else if(which == 1){
                                            cartListRef.child("User View")
                                                    .child(Prevalent.currentOnlineUser.getPhone())
                                                    .child("Products")
                                                    .child(model.getPid())
                                                    .removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                Toast.makeText(CartActivity.this, "Item removed succefully", Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(CartActivity.this, HomeActivity.class);
                                                                startActivity(intent);
                                                            }
                                                        }
                                                    });
                                        }

                                    }
                                });
                                builder.show();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout,parent,false);
                        CartViewHolder holder = new CartViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
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
                    String userName =snapshot.child("name").toString();

                    if(shippingState.equals("shipped"))
                    {
                        txtTotalAmount.setText("Dear "+ userName+ "\n order is shipped");
                        recyclerView.setVisibility(View.GONE);
                        txtmsg1.setVisibility(View.VISIBLE);
                        txtmsg1.setText("Congurtulation, your final order has placed successfuly");
                        processbtn.setVisibility(View.GONE);
                        Toast.makeText(CartActivity.this, "YOU CAN PURCHASE MORE ORDERS", Toast.LENGTH_SHORT).show();
                    }
                    else if(shippingState.equals("Not shipped"))
                    {
                        txtTotalAmount.setText("Shipping State = Not shipped");
                        recyclerView.setVisibility(View.GONE);
                        txtmsg1.setVisibility(View.VISIBLE);
                        processbtn.setVisibility(View.GONE);
                        Toast.makeText(CartActivity.this, "we are sorry", Toast.LENGTH_SHORT).show();
                    }
                    }
                }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}