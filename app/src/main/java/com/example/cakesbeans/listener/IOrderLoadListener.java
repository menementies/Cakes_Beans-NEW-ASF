package com.example.cakesbeans.listener;

import com.example.cakesbeans.model.OrderModel;

import java.util.List;

public interface IOrderLoadListener {
    void onOrderLoadSuccess(List<OrderModel> orderModelList);
    void onOrderLoadFailed(String message);
}
