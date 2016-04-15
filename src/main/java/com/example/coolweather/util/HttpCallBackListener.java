package com.example.coolweather.util;

/**
 * Created by wjx4510756 on 2016/4/13.
 */
public interface HttpCallBackListener {
    void onFinish(String response);
    void onError(Exception e);
}
