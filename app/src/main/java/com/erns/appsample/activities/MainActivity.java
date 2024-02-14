package com.erns.appsample.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.erns.appsample.R;
import com.erns.appsample.controller.MqttBroadcastReceiver;
import com.erns.appsample.controller.MqttHelper;
import com.erns.appsample.controller.MqttHelperListener;

public class MainActivity extends AppCompatActivity
{

    private MqttBroadcastReceiver mqttBroadcastReceiver;
    private Button btnConnMqtt;
    private Button btnSendMessage;
    private EditText edtMessage;
    private TextView txtSensorMessage;
    private String TOPIC = "test/pub";
    private MqttHelper mqttHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mqttBroadcastReceiver=new MqttBroadcastReceiver();
        mqttBroadcastReceiver.setListener(mqttHelperListener);

        btnConnMqtt = findViewById(R.id.btnConnectMqtt);
        btnSendMessage = findViewById(R.id.btnSendMessage);
        edtMessage = findViewById(R.id.editMessage);
        txtSensorMessage = findViewById(R.id.txtSensorMessage);


        btnConnMqtt.setOnClickListener(view -> {
            mqttHelper = MqttHelper.getInstance("tcp://10.50.33.128:1883",this);
        });

        btnSendMessage.setOnClickListener(view -> {
            String message = edtMessage.getText().toString();
            mqttHelper.publish(TOPIC, message, 0);
        });

    }

   private final MqttHelperListener mqttHelperListener = new MqttHelperListener() {
        @Override
        public void callback(String message) {
            txtSensorMessage.setText(message);
        }
    };

    @Override
    protected void onResume() {
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(MqttBroadcastReceiver.ACTION_SUB);
        registerReceiver(mqttBroadcastReceiver,intentFilter);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mqttHelper.close();
        unregisterReceiver(mqttBroadcastReceiver);
        super.onDestroy();
    }

}