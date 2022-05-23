package com.example.cakesbeans;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.BlendMode;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.cakesbeans.adapter.MyOrderAdapter;
import com.example.cakesbeans.eventbus.MyUpdateCartEvent;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        if(EventBus.getDefault().hasSubscriberForEvent(MyUpdateCartEvent.class));
            EventBus.getDefault().removeStickyEvent(MyUpdateCartEvent.class);
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void onUpdateCart(MyUpdateCartEvent event)
    {
        countCartItem();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        ImageView btnback;
        btnback = findViewById(R.id.btnback);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent next= new Intent(MainMenu.this,Menu.class);
                startActivity(next);
                finish();
            }
        });
        ImageView btncart;
        btncart = findViewById(R.id.cartButton);
        btncart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent next= new Intent(MainMenu.this,CartActivity.class);
                startActivity(next);
                finish();
            }
        });

        init();
        loadOrderFromFirebase();
        countCartItem();;
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

        btnCart.setOnClickListener(view -> startActivity(new Intent(this,CartActivity.class)));
    }

    @Override
    public void onOrderLoadSuccess(List<OrderModel> orderModelList) {
        MyOrderAdapter adapter = new MyOrderAdapter(this, orderModelList,cartLoadListener);
        recycler_menu.setAdapter(adapter);
    }

    @Override
    public void onOrderLoadFailed(String message) {
        Snackbar.make(mainLayout, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onCartLoadSuccess(List<CartModel> cartModelList) {
        int cartSum = 0;
        for(CartModel cartModel: cartModelList)
            cartSum += cartModel.getQuantity();
        badge.setNumber(cartSum);
    }

    @Override
    public void onCartLoadFailed(String message) {
        Snackbar.make(mainLayout,message,Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        countCartItem();
    }

    private void countCartItem() {
        List<CartModel> cartModels = new ArrayList<>();
        FirebaseDatabase
                .getInstance().getReference("Cart")
                .child("UNIQUE_USER_ID")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot cartSnapshot : snapshot.getChildren()) {
                            CartModel cartModel = cartSnapshot.getValue(CartModel.class);
                            cartModel.setKey(cartSnapshot.getKey());
                            cartModels.add(cartModel);
                        }
                        cartLoadListener.onCartLoadSuccess(cartModels);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        cartLoadListener.onCartLoadFailed(error.getMessage());
                    }
                });
    }
}