package com.example.designerclub;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.designerclub.Models.CartModel;
import com.example.designerclub.Models.ProductsModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class cartFragment extends Fragment {
    RecyclerView recyclerView;
    DatabaseReference myCartRefrence, adminViewCartRefrence;
    DatabaseReference totoalPriceRefrence;
    TextView tvTotalPrice;
    Button btnNext;
    LinearLayout linearLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_cart, container, false);
        initViews(mView);
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        getTotalPrice();
        getCartList();

        //clicklistener on next button
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String price = tvTotalPrice.getText().toString();
                Intent intent = new Intent(getContext(), Orders.class);
                intent.putExtra("price", price);
                getContext().startActivity(intent);
                Toast.makeText(getContext(), price, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //UserView init
    private void initViews(View mView) {
        recyclerView = (RecyclerView) mView.findViewById(R.id.recyclerviewMyCart);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        tvTotalPrice = (TextView) mView.findViewById(R.id.tvTotalPrice);
        linearLayout = (LinearLayout) mView.findViewById(R.id.linearLayoutCart);
        myCartRefrence = FirebaseDatabase.getInstance().getReference("Cart List").child("User View")
                .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("Products");
        adminViewCartRefrence = FirebaseDatabase.getInstance().getReference("Cart List").child("Admin View")
                .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("Products");
        totoalPriceRefrence = myCartRefrence;
        btnNext = (Button) mView.findViewById(R.id.btnNext);
    }

    //Calculating total price or productes
    private void getTotalPrice() {
        totoalPriceRefrence.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    int pPrice = 0;
                    int totalPrice = 0;
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        pPrice = Integer.valueOf(ds.child("productPrice").getValue(String.class));
                        totalPrice = totalPrice + pPrice;
                    }
                    tvTotalPrice.setText("Rs " + totalPrice);
                } else {
                    btnNext.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    //Retrieving products that have been added to the cart using firebase recyclerAdaptor.
    private void getCartList() {
        //FirebaserrecyclerOption
        FirebaseRecyclerOptions<CartModel> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<CartModel>()
                .setQuery(myCartRefrence, CartModel.class).build();
        FirebaseRecyclerAdapter<CartModel, ProductViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<CartModel, ProductViewHolder>(firebaseRecyclerOptions) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ProductViewHolder productViewHolder, int i, @NonNull CartModel cartModel) {
                        productViewHolder.productName.setText(cartModel.getProductName());
                        productViewHolder.productPrice.setText("Rs " + cartModel.getProductPrice());
                        productViewHolder.productBrand.setText(cartModel.getBrand());
                        productViewHolder.tvproductQuantity.setText(cartModel.getQuantity());
                        String imageurl = cartModel.getImageUrl();
                        final String nodeInfo = cartModel.getDateAndTime();
                        Picasso.with(getContext()).load(imageurl).fit().into(productViewHolder.imageView);
                        productViewHolder.tvMenu.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                PopupMenu popupMenu = new PopupMenu(getContext(), productViewHolder.tvMenu);
                                popupMenu.inflate(R.menu.option_menu);
                                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        if (item.getItemId() == R.id.menu_delete) {
                                            myCartRefrence.child(nodeInfo).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    adminViewCartRefrence.child(nodeInfo).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Snackbar.make(linearLayout, "Deleted from Cart", Snackbar.LENGTH_LONG).show();
                                                            getFragmentManager().beginTransaction().detach(cartFragment.this).commitNow();
                                                            getFragmentManager().beginTransaction().attach(cartFragment.this).commitNow();
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                        return false;
                                    }
                                });
                                popupMenu.show();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(getContext()).inflate(R.layout.cart_layout, parent, false);
                        ProductViewHolder productViewHolder = new ProductViewHolder(view);
                        return productViewHolder;
                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
        firebaseRecyclerAdapter.notifyDataSetChanged();

    }

    //prduct Views  holder ki class ha jo product layout ko define or initialize kr rhi ha
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, productBrand, tvMenu,tvproductQuantity;
        ImageView imageView;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = (TextView) itemView.findViewById(R.id.tvCetListProductName);
            productPrice = (TextView) itemView.findViewById(R.id.tvcartProductPrice);
            productBrand = (TextView) itemView.findViewById(R.id.tvCartListProductBrand);
            tvMenu = (TextView) itemView.findViewById(R.id.tvMenuIcon);
            imageView = (ImageView) itemView.findViewById(R.id.cartImageView);
            tvproductQuantity = (TextView) itemView.findViewById(R.id.tvproductQuantity);
        }
    }
}
