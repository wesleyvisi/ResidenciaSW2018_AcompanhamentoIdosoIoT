package com.example.wesley.idoso;

import android.os.Handler;
import android.os.Message;


public class DadosHandler extends Handler {
    private AppReceiver appReceiver;
    public DadosHandler() {

    }
    public DadosHandler(AppReceiver receiver) {
        appReceiver = receiver;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        appReceiver.onReceiveResult(msg);
    }

    public void mudar(AppReceiver receiver) {
        appReceiver = receiver;
    }


    public interface AppReceiver {
        void onReceiveResult(Message message);
    }
}
