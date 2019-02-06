package com.example.wesley.idoso;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class Lista extends AppCompatActivity implements ListaHandler.AppReceiver, ServiceConnection {

    private ListaHandler handler;
    Intent itService;
    TextView status;
    NewBinder nb;
    //TextView mensagem;
    ListView comodosList;
    String [][] comodos = new String[0][0];
    Button stop;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!DadosService.started){
            Intent itMain = new Intent(this, Lista.class);
            startActivity(itMain);
            finish();
        }

        setContentView(R.layout.activity_conecta);

        Intent it = getIntent();
        String ip = it.getStringExtra("ip");
        String port = it.getStringExtra("port");
        String user = it.getStringExtra("user");

        this.status = (TextView) findViewById(R.id.status);



        handler = new ListaHandler(this);

        itService = new Intent(this,DadosService.class);
        itService.putExtra("handler", new Messenger(handler));
        bindService(itService,this,Context.BIND_AUTO_CREATE);

        comodosList = (ListView) findViewById(R.id.listaComodos);

        comodosList = (ListView) findViewById(R.id.listaComodos);


        stop = (Button) findViewById(R.id.stop);

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nb.stop();
                finish();
            }

        });


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceiveResult(Message message) {




        JSONObject obj = null;
        int tipo = 0;
        int estado = 0;
        String comodo = null;
        ArrayList<String> comodosItens;


        switch (message.what) {
            case DadosService.STATUS_RUNNING:
                this.status.setText((String)message.obj);
                break;
            case DadosService.STATUS_FINISHED:

                    this.finish();
                break;
            case DadosService.STATUS_ERROR:

                break;
            case DadosService.STATUS_STATUS:
                obj = (JSONObject) message.obj;

                try {
                    tipo = obj.getInt("tipo");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                if(tipo == DadosService.MENSAGEM_STATUS){
                    try {
                        comodo = obj.getString("comodo");
                        estado = obj.getInt("estado");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    for(int i = 0; i < comodos.length; i++){
                        if(comodos[i][0].equals(comodo)){
                            switch (estado){
                                case DadosService.COMODO_VAZIO:
                                    comodos[i][1] = "Comodo vazio";
                                    break;
                                case DadosService.OBJETO_NO_COMODO:
                                    comodos[i][1] = "Objeto no comodo";

                                    break;
                                case DadosService.PESSOA_NO_COMODO:
                                    comodos[i][1] = "Pessoa no comodo";

                                    break;
                                case DadosService.ALERTA_RISCO:
                                    comodos[i][1] = "SOCORRO!";


                                    break;
                                case DadosService.SAIU_DA_CASA:
                                    comodos[i][1] = "Saiu de casa";

                                    break;
                                case DadosService.PODE_ESTAR_NO_BANHEIRO:
                                    comodos[i][1] = "Pode estar no banheiro";

                                    break;
                            }
                        }
                    }






                }else if(tipo == DadosService.MENSAGEM_COMODOS){
                    String[] preComodos = new String[0];
                    try {
                        preComodos = (obj.getString("comodos")).split(",");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    comodos = new String[preComodos.length][];

                    for(int i = 0; i < preComodos.length; i++){
                        comodos[i] = new String [] {preComodos[i],""};
                    }




                }


                comodosItens = new ArrayList<String>();
                for(String[] c : comodos){

                    comodosItens.add(c[0]+" - "+c[1]);
                }


                ArrayAdapter adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, comodosItens);
                comodosList.setAdapter(adaptador);

                if(estado == DadosService.ALERTA_RISCO && !Alerta.started){

                    Intent resultIntent = new Intent(this, Alerta.class);
                    startActivity(resultIntent);
                }

                break;

        }
    }


    @Override
    public void onStop() {
        super.onStop();


    }


    @Override
    public void onDestroy() {
        super.onDestroy();



        if (this != null) {
            unbindService(this);
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        DadosService.LocalBinder c = (DadosService.LocalBinder) service;
        nb = c.getConexao();
        nb.changeHandler(new ListaHandler(this));
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }
}
