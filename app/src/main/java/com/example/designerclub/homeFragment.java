package com.example.designerclub;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.designerclub.Models.ProductsModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class homeFragment extends Fragment {
    DatabaseReference databaseRefrence;
    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    CircularImageView imageViewgants, imageviewLadies, imageviewKids;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_home, container, false);
        initViews(mView);
        fadeAnimate();
        return mView;
    }

    private void fadeAnimate() {

    }

    @Override
    public void onStart() {
        fetchProduct("Gents");
        super.onStart();
        // fetching gents product
        imageViewgants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nodeRefrence = "Gents";
                fetchProduct(nodeRefrence);
            }
        });
        //eftching ladies product
        imageviewLadies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nodeRefrence = "Ladies";
                fetchProduct(nodeRefrence);
            }
        });
        //fetching gants product
        imageviewKids.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nodeRefrence = "Kids";
                fetchProduct(nodeRefrence);
            }
        });
    }

    //product ko firebase sy show krwany ka method
    private void fetchProduct(String nodeRefrence) {
        FirebaseRecyclerOptions<ProductsModel> firebaseRecyclerOptions
                = new FirebaseRecyclerOptions.Builder<ProductsModel>()
                .setQuery(databaseRefrence.child(nodeRefrence), ProductsModel.class).build();
        FirebaseRecyclerAdapter<ProductsModel, ProductViewHolder> adapter
                = new FirebaseRecyclerAdapter<ProductsModel, ProductViewHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i, @NonNull ProductsModel productsModel) {
                productViewHolder.productname.setText(productsModel.getProductName());
                productViewHolder.productprice.setText(productsModel.getProductPrice());
                productViewHolder.productbrand.setText(productsModel.getBrand());
                productViewHolder.productservices.setText(productsModel.getServices());
                final String imageurl = productsModel.getImageUrl();
                Picasso.with(getContext()).load(imageurl).fit().into(productViewHolder.urlimage);
                final String name = productsModel.getProductName();
                final String price = productsModel.getProductPrice();
                final String brand = productsModel.getBrand();
                final String service = productsModel.getServices();
                final String key = productsModel.getProductId();
                final String description = productsModel.getDescription();
                productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(getContext(), productreviews.class);
                        intent.putExtra("name", name);
                        intent.putExtra("price", price);
                        intent.putExtra("brand", brand);
                        intent.putExtra("service", service);
                        intent.putExtra("image", imageurl);
                        intent.putExtra("key", key);
                        intent.putExtra("description", description);
                        getContext().startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.product_layout, parent, false);
                ProductViewHolder productViewHolder = new ProductViewHolder(view);
                return productViewHolder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    //prduct Views  holder ki class ha jo product layout ko define or initialize kr rhi ha
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productname, productprice, productid, productservices, productbrand;
        ImageView urlimage;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productname = (TextView) itemView.findViewById(R.id.tvProductName);
            productprice = (TextView) itemView.findViewById(R.id.tvProductPrice);
            productbrand = (TextView) itemView.findViewById(R.id.tvBrand);
            productservices = (TextView) itemView.findViewById(R.id.tvService);
            urlimage = (ImageView) itemView.findViewById(R.id.imageUrl);

        }
    }

    // user views ko define kea
    private void initViews(View mView) {
        databaseRefrence = FirebaseDatabase.getInstance().getReference("Products");
        firebaseAuth = FirebaseAuth.getInstance();
        recyclerView = (RecyclerView) mView.findViewById(R.id.recyclerview);
        imageViewgants = (CircularImageView) mView.findViewById(R.id.gants);
        imageviewLadies = (CircularImageView) mView.findViewById(R.id.ladies);
        imageviewKids = (CircularImageView) mView.findViewById(R.id.kids);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
