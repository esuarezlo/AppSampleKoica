package com.erns.appsample.controller;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttHelper {
    private final static String TAG = "MqttHelper";
    private MqttAsyncClient mqttAndroidClient;
    private static MqttHelper instance;
    private Context context;

    public static MqttHelper getInstance(String Host, Context context) {
        if (instance == null) {
            instance = new MqttHelper(Host, context);
            Log.d(TAG, "starting MqttHelper ... ");
        }
        return instance;
    }

    private MqttHelper(String Host, Context context) {
        try {
            this.context = context;
            String clientId = MqttAsyncClient.generateClientId();
            mqttAndroidClient = new MqttAsyncClient(Host, clientId, new MemoryPersistence());
            connect();
            mqttAndroidClient.setCallback(mqttCallback);

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void connect() {
        try {
            MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setAutomaticReconnect(true);
            mqttConnectOptions.setCleanSession(false);
            //mqttConnectOptions.setUserName(USERNAME);
            //mqttConnectOptions.setPassword(PASSWORD.toCharArray());
            mqttConnectOptions.setMaxInflight(10);
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "connect to: onSuccess");
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    //subscribeToTopic();
                    try {
                        mqttAndroidClient.subscribe("test/sub", 0);
                    } catch (MqttException e) {
                        throw new RuntimeException(e);
                    }

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG, "Failed to connect to: " + exception.toString());
                }
            });


        } catch (MqttException ex) {
            ex.printStackTrace();
        }

    }

    public void publish(String topic, String message, int QOS) {
        try {
            if (!mqttAndroidClient.isConnected()) {
                mqttAndroidClient.connect();
            }

            byte[] objAsBytes = message.getBytes("UTF-8");

            mqttAndroidClient.publish(topic, objAsBytes, QOS, false);

            Log.d(TAG, "enviado mensaje");


        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    private MqttCallback mqttCallback = new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {
            Log.d(TAG, "MqttCallback connectionLost");
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) {
            try {

                String msg = new String(message.getPayload());
                Intent intent = new Intent();
                intent.setAction(MqttBroadcastReceiver.ACTION_SUB);
                intent.putExtra(MqttBroadcastReceiver.MESSAGE_SUB, msg);
                context.sendBroadcast(intent);

                Log.d(TAG, msg);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            Log.d(TAG, "MqttCallback deliveryComplete");
        }
    };

    public void close() {
        try {
            mqttAndroidClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
