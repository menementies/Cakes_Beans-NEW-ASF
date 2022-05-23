package com.example.cakesbeans.listener;

import com.example.cakesbeans.model.CartModel;
import com.example.cakesbeans.model.OrderModel;

import java.util.List;

public interface ICartLoadListener {
    void onCartLoadSuccess(List<CartModel> cartModelList);
    void onCartLoadFailed(String message);
}
