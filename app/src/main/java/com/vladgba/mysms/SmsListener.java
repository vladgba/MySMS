package com.vladgba.mysms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class SmsListener extends BroadcastReceiver {
    private final String USERKEY = "abcdef123456789";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            String msg_from = "";
            if (bundle == null) return;
            try {
                Object[] pdus = (Object[]) bundle.get("pdus");
                SmsMessage[] msgs = new SmsMessage[pdus.length];
                StringBuilder msgBody = new StringBuilder();
                for (int i = 0; i < msgs.length; i++) {
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    msg_from = msgs[i].getOriginatingAddress();
                    msgBody.append(msgs[i].getMessageBody());
                }
                send(msg_from, msgBody.toString());
            } catch (Exception e) {
                Log.d("Exception caught", e.getMessage());
            }
        }
    }

    public void send(String sender, String msg) {
        OkHttpClient okHttpClient = new OkHttpClient();
        storeMsg(sender, msg);
        RequestBody body = RequestBody.create(JSON, getAllMsg());
        Request request = new Request.Builder()
                .url("https://zcxv.icu/sms.php")
                .post(body)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, final Response response) {
                if (response.isSuccessful()) {
                    clearAll();
                    Log.d("success", response.message());
                    try {
                        Log.d("SE", response.body().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    Log.d("not-success", response.message());
                }
            }

            @Override
            public void onFailure(Call call, final IOException e) {
                Log.d("fail", e.getMessage());
            }
        });
    }


    public SharedPreferences getPrefs() {
        Context c = MainActivity.getAppContext();
        return c.getSharedPreferences(c.getPackageName() + "_preferences", Context.MODE_PRIVATE);
    }

    public void storeMsg(String sender, String msg) {
        SharedPreferences.Editor editor = getPrefs().edit();
        JSONArray a = getRawMsg();
        JSONObject arr = new JSONObject();
        try {
            arr.put("sender", sender);
            arr.put("msg", msg);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        a.put(arr);
        editor.putString("sms", a.toString());
        editor.apply();
    }

    private void clearAll() {
        SharedPreferences.Editor editor = getPrefs().edit();
        editor.remove("sms");
        editor.apply();
    }

    public String getAllMsg() {
        JSONArray a = getRawMsg();
        return a.toString();
    }

    public JSONArray getRawMsg() {
        String json = getPrefs().getString("sms", "[]");
        try {
            return new JSONArray(json);
        } catch (JSONException e) {
            Log.e("getRawMsg", e.getMessage());
            return new JSONArray();
        }
    }
}
