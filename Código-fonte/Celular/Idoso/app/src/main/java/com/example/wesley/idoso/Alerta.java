package com.example.wesley.idoso;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;


public class Alerta extends AppCompatActivity {

    int notifyID = 2;
    String CHANNEL_ID = "ALERTA_RISCO";
    CharSequence name = CHANNEL_ID;


    public static boolean started = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        started = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerta);


        int importancee = NotificationManager.IMPORTANCE_HIGH;

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder notificationc = new NotificationCompat.Builder(getApplicationContext());
        Intent intent = new Intent(this, Alerta.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent p = PendingIntent.getActivity(this, 0, intent, 0);

        notificationc.setSmallIcon(R.drawable.ic_iconfinder_grandmother_3231123)
                .setContentTitle("ALERTA DE RISCO")
                .setContentIntent(p)
                .setColor(Color.parseColor("#FF0000"))
                .setContentText("Verifique como está o seu idoso");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel mChannell = new NotificationChannel(CHANNEL_ID, name, importancee);
            mNotificationManager.createNotificationChannel(mChannell);

            notificationc.setChannelId(CHANNEL_ID);
        }

        Notification notification = notificationc.build();

        mNotificationManager.notify(notifyID , notification);


        try {
            Thread.sleep(1100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        final Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        long[] milliseconds = {0, 1500, 1000};
        vibrator.vibrate(milliseconds,0);


        final MediaPlayer mp = MediaPlayer.create(Alerta.this, R.raw.alert);
        mp.setLooping(true);
        mp.start();


        Button botao_img = findViewById(R.id.alertButton);

        botao_img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                started = false;
                vibrator.cancel();
                mp.stop();



                int importancee = NotificationManager.IMPORTANCE_LOW;


                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                NotificationCompat.Builder notificationc = new NotificationCompat.Builder(getApplicationContext());




                notificationc.setSmallIcon(R.drawable.ic_iconfinder_grandmother_3231123)
                        .setContentTitle("ALERTA DE RISCO")
                        .setColor(Color.parseColor("#FF0000"))
                        .setAutoCancel(true)
                        .setContentIntent(null)
                        .setContentText("Verifique como está o seu idoso");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    NotificationChannel mChannell = new NotificationChannel(CHANNEL_ID, name, importancee);
                    mNotificationManager.createNotificationChannel(mChannell);

                    notificationc.setChannelId(CHANNEL_ID);
                }

                Notification notification = notificationc.build();

                mNotificationManager.notify(notifyID , notification);


                finish();

            }
        });
    }
}
