package com.erns.appsample.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MqttBroadcastReceiver extends BroadcastReceiver {
    public static final String ACTION_SUB = "com.erns.mqtt.action_sub";
    public static final String MESSAGE_SUB = "com.erns.mqtt.message_sub";
    private MqttHelperListener mqttHelperListener;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String result = intent.getStringExtra(MESSAGE_SUB);
        Log.d("-----",result);
        if (action.equals(ACTION_SUB)) {
            mqttHelperListener.callback(result);
        }
    }

    public void setListener(MqttHelperListener mqttHelperListener){
        this.mqttHelperListener=mqttHelperListener;
    }

}
