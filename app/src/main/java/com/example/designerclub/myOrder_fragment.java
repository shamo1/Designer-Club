package com.example.designerclub;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.designerclub.Models.OrderModel;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class myOrder_fragment extends Fragment {

    RecyclerView recyclerView;
    DatabaseReference databaseRefrence, queryRefrence;
    ArrayList<OrderModel> list;
    ArrayList<String> arraylsit;
    TextView mDate, mAddress, mPrice, tvShippedStatus, tvShippedDate, UserName;
    CardView cardViewmYorder;
    Query query;
    Button btnRecieved, btncreatePDF;
    ProgressBar progressbar;
    LinearLayout lLayout;
    WebView webView;
    String shippedDate, shippedStatus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mview = inflater.inflate(R.layout.fragment_my_order_fragment, container, false);
        initViews(mview);
        //fetchData();
        return mview;
    }

    @Override
    public void onStart() {
        super.onStart();
        progressbar.setVisibility(View.VISIBLE);
        lLayout.setVisibility(View.GONE);
        databaseRefrence.keepSynced(true);
        databaseRefrence.child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            /*OrderModel orderlist = dataSnapshot.getValue(OrderModel.class);
                            list.add(orderlist);
                            adaptor = new MyOrdersAdaptor(getContext(), list);
                            recyclerView.setAdapter(adaptor);
                            adaptor.notifyDataSetChanged();*/

                            progressbar.setVisibility(View.GONE);
                            lLayout.setVisibility(View.VISIBLE);
                            String date = dataSnapshot.child("date").getValue(String.class);
                            String address = dataSnapshot.child("address").getValue(String.class);
                            String price = dataSnapshot.child("totalprice").getValue(String.class);
                            String name = dataSnapshot.child("name").getValue(String.class);
                            UserName.setText(name);
                            mDate.setText(date);
                            mAddress.setText(address);
                            mPrice.setText(price);
                            createPdf(name, date, address, price);

                        } else {

                            Snackbar.make(lLayout, "You have not placed any order", Snackbar.LENGTH_LONG).show();
                            progressbar.setVisibility(View.GONE);
                            lLayout.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    shippedStatus = "Order Shipped";
                    tvShippedStatus.setText(shippedStatus);
                    shippedDate = dataSnapshot.child("date").getValue(String.class);
                    tvShippedDate.setText(shippedDate);
                } else {
                    shippedStatus = "Order not Shipped";
                    tvShippedStatus.setText(shippedStatus);
                    tvShippedDate.setText("Order Is Not yet Shipped");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnRecieved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseRefrence.child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "History Removed..", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        //save pdf button event listener to create PDF file
        btncreatePDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getContext();
                //printer manager to get printerview and save pdf
                PrintManager printManager = (PrintManager) getContext().getSystemService(context.PRINT_SERVICE);
                PrintDocumentAdapter adapter = null;
                //checking the current device build version....
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    adapter = webView.createPrintDocumentAdapter();
                }
                String JobName = getString(R.string.app_name) + "Document";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    PrintJob printJob = printManager.print(JobName.replace("", "invoice"), adapter, new PrintAttributes.Builder().build());
                }
            }
        });
    }

    //method to create PDF Ui to save document as PDF
    private void createPdf(String name, String date, String address, String price) {
        Calendar calanderInstance = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd,yyyy");
        String mdate = currentDate.format(calanderInstance.getTime());
        String html = "<!doctype html>\n" +
                "<html lang=\"en\">\n" +
                "  <head>\n" +
                "    <title>Title</title>\n" +
                "    <!-- Required meta tags -->\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\">\n" +
                "\n" +
                "    <!-- Bootstrap CSS -->\n" +
                "    <link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css\" integrity=\"sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T\" crossorigin=\"anonymous\">\n" +
                "  </head>\n" +
                "  <body>\n" +
                "      <div class=\"container\">\n" +
                "          <h2 class=\" text-center\">Invoice</h2>\n" +
                "          <h5 class=\"text-center\">Designer Club Order History </h5>\n" +
                "          <h3 >" + name + "</h3>\n" +
                "          <P>Service Details and shipped status</p>\n" +
                "         <div class=\"container row\">\n" +
                "              <p style=\"font-weight:bold; margin-right:10px;\">Date:</p><p>" + mdate + "</p>\n" +
                "         </div>\n" +
                "        <table class=\"table table-bordered table-sm table-striped m-10px\">\n" +
                "            <thead class=\"thead-inverse\">\n" +
                "                <tr>\n" +
                "                    <th>Order Date</th>\n" +
                "                    <th>Address</th>\n" +
                "                    <th>Shipped Status</th>\n" +
                "                    <th>Shipped Date</th>\n" +
                "                    <th>Payment</th>\n" +
                "                </tr>\n" +
                "                </thead>\n" +
                "                <tbody>\n" +
                "                    <tr>\n" +
                "                        <td>" + date + "</td>\n" +
                "                        <td>" + address + "</td>\n" +
                "                        <td>" + shippedStatus + "</td>\n" +
                "                        <td>" + shippedDate + "</td>\n" +
                "                        <td>Cash On delevary</td>\n" +
                "                    </tr>\n" +
                "                </tbody>\n" +
                "        </table>\n" +
                "        \n" +
                "                <h4 class=\"text-right\" style=\"font-weight:bold;\">Total</h4>\n" +
                "                <h4 class=\"text-right\">" + price + "</h4>\n" +
                "            \n" +
                "      </div>\n" +
                "    <!-- Optional JavaScript -->\n" +
                "    <!-- jQuery first, then Popper.js, then Bootstrap JS -->\n" +
                "    <script src=\"https://code.jquery.com/jquery-3.3.1.slim.min.js\" integrity=\"sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo\" crossorigin=\"anonymous\"></script>\n" +
                "    <script src=\"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js\" integrity=\"sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1\" crossorigin=\"anonymous\"></script>\n" +
                "    <script src=\"https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js\" integrity=\"sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM\" crossorigin=\"anonymous\"></script>\n" +
                "  </body>\n" +
                "</html>";
        webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
    }

    /*
            private void fetchData() {
                FirebaseRecyclerOptions<OrderModel> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<OrderModel>()
                        .setQuery(databaseRefrence.child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()), OrderModel.class).build();
                FirebaseRecyclerAdapter<OrderModel,ProductViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<OrderModel, ProductViewHolder>(firebaseRecyclerOptions) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i, @NonNull OrderModel orderModel) {
                        productViewHolder.mDate.setText(orderModel.getDate());
                        productViewHolder.mPrice.setText(orderModel.getTotalprice());
                        productViewHolder.mAddres.setText(orderModel.getAddress());
                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View mView = LayoutInflater.from(getContext()).inflate(R.layout.layout_myorders,parent,false);
                        ProductViewHolder productViewHolder = new ProductViewHolder(mView);
                        return productViewHolder;
                    }
                };
                recyclerView.setAdapter(firebaseRecyclerAdapter);
                firebaseRecyclerAdapter.startListening();
            }

            public  static class ProductViewHolder extends RecyclerView.ViewHolder {
                TextView mDate,mPrice,mAddres;
                public ProductViewHolder(@NonNull View itemView) {
                    super(itemView);
                    mDate = (TextView) itemView.findViewById(R.id.tvMOrderDate);
                    mPrice = (TextView) itemView.findViewById(R.id.tvMOrderPrice);
                    mAddres = (TextView) itemView.findViewById(R.id.tvMorderAddress);

                }
            }

           */

    //init views
    private void initViews(View mview) {
        /* recyclerView = (RecyclerView) mview.findViewById(R.id.myorder_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        list = new ArrayList<>();
        arraylsit = new ArrayList<>();*/
        databaseRefrence = FirebaseDatabase.getInstance().getReference().child("Order History");
        queryRefrence = FirebaseDatabase.getInstance().getReference("Shipped order");
        query = queryRefrence.child("nodePhone").equalTo(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        mDate = (TextView) mview.findViewById(R.id.tvMOrderDate);
        mPrice = (TextView) mview.findViewById(R.id.tvMOrderPrice);
        mAddress = (TextView) mview.findViewById(R.id.tvMorderAddress);
        tvShippedStatus = (TextView) mview.findViewById(R.id.tvOrderStatus);
        tvShippedDate = (TextView) mview.findViewById(R.id.tvordershippedDate);
        UserName = (TextView) mview.findViewById(R.id.tvmOr5derorderUserName);
        webView = (WebView) mview.findViewById(R.id.webView);
        btnRecieved = (Button) mview.findViewById(R.id.btnRecieved);
        btncreatePDF = (Button) mview.findViewById(R.id.btnSavePdf);
        progressbar = (ProgressBar) mview.findViewById(R.id.progbarMyorder);
        lLayout = (LinearLayout) mview.findViewById(R.id.lLayoutmOrder);
    }
}
