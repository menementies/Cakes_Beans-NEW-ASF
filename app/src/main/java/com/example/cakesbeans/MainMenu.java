package com.example.cakesbeans;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.example.cakesbeans.adapter.MyOrderAdapter;
import com.example.cakesbeans.listener.ICartLoadListener;
import com.example.cakesbeans.listener.IOrderLoadListener;
import com.example.cakesbeans.model.CartModel;
import com.example.cakesbeans.model.OrderModel;
import com.example.cakesbeans.utils.SpaceItemDecoration;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainMenu extends AppCompatActivity implements IOrderLoadListener, ICartLoadListener {
    @BindView(R.id.recycler_menu)
    RecyclerView recycler_menu;
    @BindView(R.id.mainLayout)
    RelativeLayout mainLayout;
    @BindView(R.id.badge)
    NotificationBadge badge;
    @BindView(R.id.btnCart)
    FrameLayout btnCart;

    IOrderLoadListener orderLoadListener;
    ICartLoadListener cartLoadListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        init();
        loadOrderFromFirebase();
    }

    private void loadOrderFromFirebase() {
        List<OrderModel> orderModels = new ArrayList<>();
        FirebaseDatabase.getInstance()
                .getReference("Order")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for(DataSnapshot orderSnapshot:snapshot.getChildren()){
                                OrderModel orderModel = orderSnapshot.getValue(OrderModel.class);
                                orderModel.setKey(orderSnapshot.getKey());
                                orderModels.add(orderModel);
                            }
                            orderLoadListener.onOrderLoadSuccess(orderModels);
                        }else
                            orderLoadListener.onOrderLoadFailed("Can't find Product");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        orderLoadListener.onOrderLoadFailed(error.getMessage());
                    }
                });
    }

    private void init(){
        ButterKnife.bind(this);

        orderLoadListener = this;
        cartLoadListener = this;

        GridLayoutManager gridLayoutManager =  new GridLayoutManager(this, 2);
        recycler_menu.setLayoutManager(gridLayoutManager);
        recycler_menu.addItemDecoration(new SpaceItemDecoration());
    }

    @Override
    public void onOrderLoadSuccess(List<OrderModel> orderModelList) {
        MyOrderAdapter adapter = new MyOrderAdapter(this, orderModelList);
        recycler_menu.setAdapter(adapter);
    }

    @Override
    public void onOrderLoadFailed(String message) {
        Snackbar.make(mainLayout, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onCartLoadSuccess(List<CartModel> cartModelList) {

    }

    @Override
    public void onCartLoadFailed(String message) {

    }
}