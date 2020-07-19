package com.example.designerclub;

import android.os.Bundle;
import android.transition.Fade;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.designerclub.Adaptors.ProductReviewAdaptor;
import com.example.designerclub.Models.CartModel;
import com.example.designerclub.Models.ProductReviewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class productreviews extends AppCompatActivity implements View.OnClickListener {

    ImageView imageView;
    TextView tvproductName, tvProductPrice, tvProductBrand, tvDescription, tvProductService, tvQuantity;
    RatingBar ratingBar;
    LinearLayout commentLayout;
    DatabaseReference productReview, databaseRefrence, addtoCartREfrence;
    FirebaseAuth firebaseAuth;
    String currentUserId, userName;
    Button sendComment, btnIncrease, btnDecrease;
    FloatingActionButton btnAddtoCart;
    LinearLayout mainlayout;
    EditText commentBox;
    RecyclerView recyclerView;
    ArrayList<ProductReviewModel> productReviewModelsList;
    ProductReviewAdaptor productReviewAdaptor;
    Spinner spinner;
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;
    int count = 1;
    String productName, productPrice, productBrand, productImageUrl, productService, productKey, productDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productreviews);

        initViews();
        // intent extras k zriya sy pchli activity sy data get kea
        productName = getIntent().getStringExtra("name");
        productPrice = getIntent().getStringExtra("price");
        productBrand = getIntent().getStringExtra("brand");
        productImageUrl = getIntent().getStringExtra("image");
        productService = getIntent().getStringExtra("service");
        productKey = getIntent().getStringExtra("key");
        productDescription = getIntent().getStringExtra("description");
        // calling method set product Details
        setProductDetail(productImageUrl, productName, productPrice, productBrand, productDescription, productService);

        // calling method fetch Comments
        fetchComments(productKey);

        //Current User Ka name hasil kr rhy
        databaseRefrence.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    userName = dataSnapshot.child("userName").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        // Comments add kr rhy hn
        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> reviewMap = new HashMap<>();
                reviewMap.put("Comment", commentBox.getText().toString());
                reviewMap.put("Rating", ratingBar.getRating());
                reviewMap.put("Name", userName);
                if (commentBox.getText().toString().isEmpty()) {
                    commentBox.setError("Please Type your Comment");
                } else {
                    productReview.child(productKey).push().setValue(reviewMap).
                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Snackbar.make(mainlayout, "Comment Added", Snackbar.LENGTH_LONG).show();
                                        commentBox.setText("");
                                    } else {
                                        Snackbar.make(mainlayout, "Comment Not Added", Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
        //increaseing Quantity

        // product ko cart ma add kr rhy hn Add to cart button py click listener lgaya ha
        btnAddtoCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Current time get kr rhy jab Cart ma product Add kia tha.
                Calendar calanderInstance = Calendar.getInstance();
                SimpleDateFormat currentDate = new SimpleDateFormat("MM dd,yyyy");
                String date = currentDate.format(calanderInstance.getTime());
                SimpleDateFormat mCurrentTime = new SimpleDateFormat("HH:mm:ss a");
                String CurrentTime = mCurrentTime.format(calanderInstance.getTime());
                final String mTimeAndDate = date + " " + CurrentTime;
                final String size = spinner.getSelectedItem().toString();
                if (size.equals("Select Size")) {
                    Snackbar.make(commentLayout, "Please Select Size", Snackbar.LENGTH_LONG).show();
                    return;
                }
                //cartmodel class ka instance bna rhy or phr us ma data set kr rhy hn
                final CartModel cartModel = new CartModel();
                cartModel.setProductName(productName);
                cartModel.setProductPrice(tvProductPrice.getText().toString());
                cartModel.setProductId(productKey);
                cartModel.setBrand(productBrand);
                cartModel.setImageUrl(productImageUrl);
                cartModel.setDescription(productDescription);
                cartModel.setSize(size);
                cartModel.setTime(CurrentTime);
                cartModel.setDate(date);
                cartModel.setDateAndTime(mTimeAndDate);
                cartModel.setQuantity(tvQuantity.getText().toString());

                // Jo data model class ma set kia tha usy firebase ma upload kr rhy hn
                addtoCartREfrence = FirebaseDatabase.getInstance().getReference("Cart List");
                addtoCartREfrence.child("User View").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                        .child("Products").child(mTimeAndDate).
                        setValue(cartModel).addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    addtoCartREfrence.child("Admin View").child(FirebaseAuth.getInstance().getCurrentUser()
                                            .getPhoneNumber()).child("Products").child(mTimeAndDate).setValue(cartModel).
                                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                      @Override
                                                                      public void onComplete(@NonNull Task<Void> task) {
                                                                          if (task.isSuccessful()) {
                                                                              addtoCartREfrence.child("Order History").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                                                                                      .child("Products").child(mTimeAndDate).setValue(cartModel).addOnCompleteListener(
                                                                                      new OnCompleteListener<Void>() {
                                                                                          @Override
                                                                                          public void onComplete(@NonNull Task<Void> task) {
                                                                                              if (task.isSuccessful()) {
                                                                                                  Snackbar.make(commentLayout, "Product Added to the cart", Snackbar.LENGTH_LONG).show();
                                                                                              }
                                                                                          }
                                                                                      });
                                                                          }
                                                                      }
                                                                  }
                                            );
                                }
                            }
                        }
                );
            }
        });

    }


    // Setting deltails for Product previews
    private void setProductDetail(String productImageUrl, String productName,
                                  String productPrice,
                                  String productBrand,
                                  String productDescription, String productService) {
        Picasso.with(this).load(productImageUrl).fit().into(imageView);
        tvproductName.setText(productName);
        tvProductPrice.setText(productPrice);
        tvDescription.setText(productDescription);
        tvProductBrand.setText(productBrand);
        tvProductService.setText(productService);
    }

    //Reviews or comments ko show krwa rhy hn yha
    private void fetchComments(String productKey) {
        productReview.child(productKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        ProductReviewModel productReviewModel = ds.getValue(ProductReviewModel.class);
                        productReviewModelsList.add(productReviewModel);
                    }
                    productReviewAdaptor = new ProductReviewAdaptor(productreviews.this, productReviewModelsList);
                    recyclerView.setAdapter(productReviewAdaptor);
                    productReviewAdaptor.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    //Initializing user views
    private void initViews() {
        imageView = (ImageView) findViewById(R.id.reviewImage);
        tvproductName = (TextView) findViewById(R.id.tvProductReviewName);
        tvProductPrice = (TextView) findViewById(R.id.tvProductReviewPrice);
        tvDescription = (TextView) findViewById(R.id.tvDescription);
        tvProductBrand = (TextView) findViewById(R.id.brand);
        tvProductService = (TextView) findViewById(R.id.service);
        ratingBar = (RatingBar) findViewById(R.id.rattingBar);
        commentLayout = (LinearLayout) findViewById(R.id.CommetnPortion);

        productReview = FirebaseDatabase.getInstance().getReference("Product Reviews");
        databaseRefrence = FirebaseDatabase.getInstance().getReference("Customer");
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        sendComment = (Button) findViewById(R.id.btnsendComment);
        commentBox = (EditText) findViewById(R.id.edtcomment);
        mainlayout = (LinearLayout) findViewById(R.id.mainlayout2);
        recyclerView = (RecyclerView) findViewById(R.id.productReviewRecyclerView);
        btnAddtoCart = (FloatingActionButton) findViewById(R.id.btnAddtoCart);
        btnIncrease = (Button) findViewById(R.id.btnIncrease);
        btnDecrease = (Button) findViewById(R.id.btndecrese);
        tvQuantity = (TextView) findViewById(R.id.tvQuantity);

        productReviewModelsList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        spinner = (Spinner) findViewById(R.id.SpinnerSize);
        arrayList = new ArrayList<>();
        arrayList.add("Select Size");
        arrayList.add("Small");
        arrayList.add("Medium");
        arrayList.add("Large");
        arrayList.add("XL");
        arrayList.add("XXL");
        arrayAdapter = new ArrayAdapter<String>(productreviews.this, android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        btnIncrease.setOnClickListener(this);
        btnDecrease.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int price, total;
        if (v == btnIncrease) {
            count++;
            tvQuantity.setText(String.valueOf(count));
            total = Integer.valueOf(productPrice) * count;
            tvProductPrice.setText(String.valueOf(total));
        }
        if (v == btnDecrease) {

            if (count > 1) {
                count--;
                tvQuantity.setText(String.valueOf(count));
                total = Integer.valueOf(productPrice) * count;
                tvProductPrice.setText(String.valueOf(total));
            }
        }
    }
}
