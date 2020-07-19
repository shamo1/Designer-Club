package com.example.designerclub;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.designerclub.Models.ProductsModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class searchFragment extends Fragment {
    Spinner spinner;
    EditText edtSearch;
    ArrayList<String> list;
    ArrayAdapter<String> arrayAdapter;
    DatabaseReference searchRefrence;
    RecyclerView recyclerView;
    Query query;
    CoordinatorLayout coordinatorLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        viewsinit(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        search();
    }

    private void viewsinit(View view) {
        spinner = (Spinner) view.findViewById(R.id.spinnerCategory);
        edtSearch = (EditText) view.findViewById(R.id.edtSearch);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerviewSearch);
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinatorLayout);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        searchRefrence = FirebaseDatabase.getInstance().getReference("Products");
        list = new ArrayList<>();
        list.add("Select Category");
        list.add("Gents");
        list.add("Ladies");
        list.add("Kids");
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, list);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
    }

    //serach method
    private void search() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!edtSearch.getText().toString().isEmpty()) {
                    setfirebaseAdaptors();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    //setting adaptors..
    private void setfirebaseAdaptors() {
        String search = edtSearch.getText().toString();
        String category = spinner.getSelectedItem().toString();
        if (category.equals("Select Category")) {
            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Please Select Category", Snackbar.LENGTH_LONG);
            View view = snackbar.getView();
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) view.getLayoutParams();
            params.gravity = Gravity.TOP;
            view.setLayoutParams(params);
            snackbar.show();
        } else {
            query = searchRefrence.child(category).
                    orderByChild("ProductName").startAt(search).
                    endAt(search + "\uf8ff");
            FirebaseRecyclerOptions<ProductsModel> firebaseRecyclerOptions
                    = new FirebaseRecyclerOptions.Builder<ProductsModel>().
                    setQuery(query, ProductsModel.class).build();

            FirebaseRecyclerAdapter<ProductsModel, homeFragment.ProductViewHolder> adapter
                    = new FirebaseRecyclerAdapter<ProductsModel,
                    homeFragment.ProductViewHolder>
                    (firebaseRecyclerOptions) {
                @Override
                protected void onBindViewHolder(@NonNull homeFragment.ProductViewHolder productViewHolder, int i, @NonNull ProductsModel productsModel) {
                    productViewHolder.productname.setText(productsModel.getProductName());
                    productViewHolder.productprice.setText("Rs :" + productsModel.getProductPrice());
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
                            intent.putExtra("image", imageurl);
                            intent.putExtra("key", key);
                            intent.putExtra("description", description);
                            getContext().startActivity(intent);
                        }
                    });
                }

                @NonNull
                @Override
                public homeFragment.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(getContext()).inflate(R.layout.product_layout, parent, false);
                    homeFragment.ProductViewHolder productViewHolder = new homeFragment.ProductViewHolder(view);
                    return productViewHolder;
                }
            };
            recyclerView.setAdapter(adapter);
            adapter.startListening();
        }
    }

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
}
