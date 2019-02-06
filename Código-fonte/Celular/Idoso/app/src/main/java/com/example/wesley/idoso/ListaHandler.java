package com.example.wesley.idoso;

import android.os.Handler;
import android.os.Message;


import java.io.Serializable;
import java.util.logging.LogRecord;


public class ListaHandler extends Handler {
    private AppReceiver appReceiver;
    public ListaHandler() {

    }
    public ListaHandler(AppReceiver receiver) {
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
