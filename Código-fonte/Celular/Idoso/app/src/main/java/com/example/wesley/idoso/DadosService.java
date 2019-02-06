package com.example.wesley.idoso;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;


public class DadosService extends Service implements NewBinder, DadosHandler.AppReceiver {

    public static boolean started = false;

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;
    public static final int STATUS_STATUS = 3;

    public static final int COMODO_VAZIO = 0;
    public static final int OBJETO_NO_COMODO = 1;
    public static final int PESSOA_NO_COMODO = 2;
    public static final int ALERTA_RISCO = 3;
    public static final int SAIU_DA_CASA = 4;
    public static final int PODE_ESTAR_NO_BANHEIRO = 5;

    public static final int MENSAGEM_COMODOS = 0;
    public static final int MENSAGEM_STATUS = 1;


    String estado;
    JSONObject comodos = null;
    JSONObject ultimaAtualizacao = null;
    Intent itService;
    String message;
    Messenger handler = null;
    ListaHandler cHandler;
    int lastShownNotificationId = 1;





    private LocalBinder mBinder = new DadosService.LocalBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onReceiveResult(Message message) {
        Message msg = new Message();
        msg.obj = message.obj;
        msg.what = message.what;
        JSONObject json;
        int tipo = -1;

        if(msg.what == STATUS_RUNNING){
            this.estado = msg.obj.toString();
        }

        if(msg.what == STATUS_STATUS){
            json = (JSONObject) msg.obj;

            try {
                tipo = json.getInt("tipo");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(tipo == MENSAGEM_COMODOS){
                this.comodos = (JSONObject) msg.obj;
            }else if(tipo == MENSAGEM_STATUS){
                this.ultimaAtualizacao = (JSONObject) msg.obj;
            }
        }


        if(this.handler != null){
            try {
                this.handler.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }


    public class LocalBinder extends Binder {
        public NewBinder getConexao() {
            // Return this instance of LocalService so clients can call public methods
            return DadosService.this;
        }

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        createAndShowForegroundNotification(this,1);


        String ip = intent.getStringExtra("ip");
        String port = intent.getStringExtra("port");
        String user = intent.getStringExtra("user");

        this.estado = "Conectando a: "+user+"  -  "+ip+" : "+port;


        itService = new Intent(this, ConectaService.class);
        itService.putExtra("ip", ip);
        itService.putExtra("port", port);
        itService.putExtra("user", user);

        itService.putExtra("handler", new Messenger(new DadosHandler(this)));
        startService(itService);

        started = true;


        return super.onStartCommand(intent, flags, startId);
    }






    public void changeHandler(ListaHandler ha){

        this.cHandler = ha;
        this.handler = new Messenger(this.cHandler);

        Message msg = new Message();
        msg.obj = this.estado;
        msg.what = STATUS_RUNNING;

        try {
            this.handler.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }


        if(this.comodos != null) {
            msg = new Message();
            msg.obj = this.comodos;
            msg.what = STATUS_STATUS;

            try {
                this.handler.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }




        if(this.ultimaAtualizacao != null) {
            msg = new Message();
            msg.obj = this.ultimaAtualizacao;
            msg.what = STATUS_STATUS;
            try {
                this.handler.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void stop() {
        this.started = false;

        this.stopSelf();

        try {
            this.finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }


    @Override
    public void onDestroy() {
        started = false;
        stopService(itService);
        super.onDestroy();
    }











    private void createAndShowForegroundNotification(Service yourService, int notificationId) {

        final NotificationCompat.Builder builder = getNotificationBuilder(yourService,
                "com.example.Idoso.notification.CHANNEL_ID_FOREGROUND", // Channel id
                NotificationManagerCompat.IMPORTANCE_NONE); //Low importance prevent visual appearance for this notification channel on top

        Intent notificationIntent = new Intent(this, Lista.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        builder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_iconfinder_grandmother_3231123)
//                .setContentTitle("Titulo")
                .setContentIntent(pendingIntent)
                .setContentText("Estamos Monitorando seu idoso.");

        Notification notification = builder.build();


        yourService.startForeground(notificationId, notification);

        if (notificationId != lastShownNotificationId) {
            // Cancel previous notification
            final NotificationManager nm = (NotificationManager) yourService.getSystemService(Activity.NOTIFICATION_SERVICE);
            nm.cancel(lastShownNotificationId);
        }
        lastShownNotificationId = notificationId;
    }

    public static NotificationCompat.Builder getNotificationBuilder(Context context, String channelId, int importance) {
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            prepareChannel(context, channelId, importance);
            builder = new NotificationCompat.Builder(context, channelId);
        } else {
            builder = new NotificationCompat.Builder(context);
        }
        return builder;
    }

    @TargetApi(26)
    private static void prepareChannel(Context context, String id, int importance) {
        final String appName = context.getString(R.string.app_name);
        String description = "channel description";
        final NotificationManager nm = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);

        if(nm != null) {
            NotificationChannel nChannel = nm.getNotificationChannel(id);

            if (nChannel == null) {
                nChannel = new NotificationChannel(id, appName, importance);
                nChannel.setDescription(description);
                nm.createNotificationChannel(nChannel);
            }
        }
    }

}






















