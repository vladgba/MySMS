package com.vladgba.mysms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;
import okhttp3.*;

import java.io.IOException;

public class SmsListener extends BroadcastReceiver {
    private String USERKEY = "abcdef123456789";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = null;
            String msg_from = "";
            if (bundle == null) return;
            try {
                Object[] pdus = (Object[]) bundle.get("pdus");
                msgs = new SmsMessage[pdus.length];
                String msgBody = "";
                for (int i = 0; i < msgs.length; i++) {
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    msg_from = msgs[i].getOriginatingAddress();
                    msgBody += msgs[i].getMessageBody();
                }
                send(msg_from, msgBody);
            } catch (Exception e) {
                Log.d("Exception caught", e.getMessage());
            }
        }
    }

    public void send(String from, String body) {
        OkHttpClient okHttpClient = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("sender", from)
                .add("message", body)
                .build();

        Request.Builder builder = new Request.Builder();

        Request request = builder
                .url("http://zcxv.icu/sms.php?key=" + USERKEY)
                .post(formBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, final Response response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.getAppContext(),
                            "Successful",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.getAppContext(),
                            "NOT successful",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call call, final IOException e) {
                Toast.makeText(MainActivity.getAppContext(),
                        "Fail",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
