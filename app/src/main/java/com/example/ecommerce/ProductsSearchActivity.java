package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.widget.AppCompatButton;
import android.view.View;
import android.text.TextUtils;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ecommerce.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.example.ecommerce.Model.Products;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


public class ProductsSearchActivity extends AppCompatActivity {

private    Button mysearchBtn;
    private EditText inputText;
    private RecyclerView searchList;
    private String searchInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_search);

      mysearchBtn=findViewById(R.id.btn_search);
        inputText = (EditText)findViewById(R.id.edit_search);
        searchList =findViewById(R.id.product_search_list);
        searchList.setHasFixedSize(true);

       mysearchBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.i("Some tag", "S");



                onStart();

            }
        });


        searchList.setLayoutManager(new LinearLayoutManager(ProductsSearchActivity.this));
    }

    @Override
    protected void onStart() {
        super.onStart();

if(inputText.getText().toString().equals("")){



    searchList.setVisibility(View.INVISIBLE);
    }
else
{
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");
    FirebaseRecyclerOptions<Products> options =
            new FirebaseRecyclerOptions.Builder<Products>()
                    .setQuery(reference.orderByChild("pname").startAt(inputText.getText().toString().toLowerCase()).endAt(inputText.getText().toString().toLowerCase() + "\uf8ff"), Products.class)
                    .build();
    FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
            new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options){

                @NonNull
                @Override
                public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                    ProductViewHolder holder = new ProductViewHolder(view);
                    return holder;
                }

                @Override
                protected void onBindViewHolder(@NonNull ProductViewHolder holder, int i, @NonNull final Products model) {
                    holder.txtProductName.setText(model.getPname());
                    holder.txtProductDescription.setText(model.getDescription());
                    holder.txtProductPrice.setText("Price = " + model.getPrice() + "mru");
                    Picasso.get().load(model.getImage()).into(holder.imageView);

                    holder.imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent =new Intent(ProductsSearchActivity.this,ProductDetailsActivity.class);
                            intent.putExtra("pid",model.getPid());
                            startActivity(intent);

                        }
                    });
                }
            };

    searchList.setAdapter(adapter);
    adapter.startListening();
    searchList.setVisibility(View.VISIBLE);
}
    }
    
}
