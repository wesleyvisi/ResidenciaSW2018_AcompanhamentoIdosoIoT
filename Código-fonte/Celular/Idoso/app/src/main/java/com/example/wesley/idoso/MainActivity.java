package com.example.wesley.idoso;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.content.res.AppCompatResources;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        Button conecta = (Button) findViewById(R.id.conecta);
        conecta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent itService = new Intent(view.getContext(), DadosService.class);
                    itService.putExtra("ip", ((EditText) findViewById(R.id.ip)).getText().toString());
                    itService.putExtra("port", ((EditText) findViewById(R.id.port)).getText().toString());
                    itService.putExtra("user", ((EditText) findViewById(R.id.user)).getText().toString());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(itService);
                    } else {
                        startService(itService);
                    }


                    Intent itLista = new Intent(view.getContext(), Lista.class);
                    itLista.putExtra("ip", ((EditText) findViewById(R.id.ip)).getText().toString());
                    itLista.putExtra("port", ((EditText) findViewById(R.id.port)).getText().toString());
                    itLista.putExtra("user", ((EditText) findViewById(R.id.user)).getText().toString());
                    startActivity(itLista);


                }
        });









    }


    @Override
    protected void onResume() {
        if(DadosService.started){
            Intent itConecta = new Intent(this, Lista.class);
            startActivity(itConecta);
        }
        super.onResume();
    }
}
