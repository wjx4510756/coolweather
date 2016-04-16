package com.example.coolweather.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by wjx4510756 on 2016/4/13.
 */
public class HttpUtil {
    public static void sendHttpRequest(final String address,final HttpCallBackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try{
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine())!= null)
                        response.append(line);
                    if (listener != null)
                        listener.onFinish(response.toString());
                } catch (Exception e) {
                    if (listener != null)
                        listener.onError(e);
                }
                finally {
                    if (connection != null)
                        connection.disconnect();
                }
            }
        }).start();
    }
}
