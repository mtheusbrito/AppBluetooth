package com.matheus.appbluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by matheus on 16/04/18.
 */

public class MainActivity extends AppCompatActivity {
    private Button buttonConectar, buttonBuzzerOn, buttonBuzzerOff;

    BluetoothAdapter meuBluetoothAdapter = null;
    private static final int SOLICITA_ATIVACAO = 1;
    private static final int SOLICITA_CONEXAO = 2;
    private boolean conectado = false;
    ConnectedThread connectedThread;

    private static String MAC = null;
    private BluetoothDevice device = null;
    private BluetoothSocket meuSocket = null;
    private UUID meuUuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        //checando se o bluetooth esta ativo
        estadoBluetooth();


        buttonConectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (conectado) {

                    //desconectar
                    try {
                        meuSocket.close();
                        conectado = false;
                        buttonConectar.setText("Conectar");
                        Toast.makeText(MainActivity.this, "Bluetooth foi desconectado!", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Ocorreu um erro: " + e.toString(), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    //conectar

                    Intent abreLista = new Intent(MainActivity.this, ListaDispositivos.class);
                    startActivityForResult(abreLista, SOLICITA_CONEXAO);
                }

            }
        });

       buttonBuzzerOn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(conectado){

                   connectedThread.enviar("f");
               }else {
                   Toast.makeText(MainActivity.this, "Bluetooth não esta conectado!", Toast.LENGTH_SHORT).show();
               }
           }
       });
       buttonBuzzerOff.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(conectado){

                   connectedThread.enviar("b");
               }else {
                   Toast.makeText(MainActivity.this, "Bluetooth não esta conectado!", Toast.LENGTH_SHORT).show();
               }
           }
       });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SOLICITA_ATIVACAO:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "O Bluetooth foi ativado!", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this, "O Bluetooth não foi ativado, o app será encerrado!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case SOLICITA_CONEXAO:
                if (resultCode == Activity.RESULT_OK) {
                    MAC = data.getExtras().getString(ListaDispositivos.ENDERECO_MAC);
                    //Toast.makeText(this, "MAC FINAL:" +MAC, Toast.LENGTH_SHORT).show();

                    device = meuBluetoothAdapter.getRemoteDevice(MAC);
                    try {

                        meuSocket = device.createRfcommSocketToServiceRecord(meuUuid);
                        meuSocket.connect();
                        conectado = true;
                        connectedThread = new ConnectedThread(meuSocket);
                        connectedThread.start();


                        buttonConectar.setText("Desconectar");
                        Toast.makeText(this, "Voce foi conectado com:" + MAC, Toast.LENGTH_SHORT).show();

                    } catch (IOException e) {
                        conectado = false;
                        e.printStackTrace();
                        Toast.makeText(this, "Ocorreu um erro:" + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                } else {

                    Toast.makeText(this, "Falha ao obter o MAC", Toast.LENGTH_SHORT).show();
                }

        }

    }

    private void initView() {
        buttonConectar = findViewById(R.id.button_conectar);
        buttonBuzzerOn = findViewById(R.id.button_buzzer_on);
        buttonBuzzerOff = findViewById(R.id.button_buzzer_off);


    }

    private void estadoBluetooth() {
        meuBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (meuBluetoothAdapter == null) {

            Toast.makeText(this, "Seu dispositivo não possui bluetooth", Toast.LENGTH_SHORT).show();
        } else if (!meuBluetoothAdapter.isEnabled()) {
            Intent ativaBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(ativaBluetooth, SOLICITA_ATIVACAO);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            connectedThread.enviar("b");
        } catch (Exception e) {

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            connectedThread.enviar("b");
        } catch (Exception e) {

        }
    }

    private class ConnectedThread extends Thread {

        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            meuSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }


        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
           /* while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    // mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                    //  .sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }*/
        }

        /* Call this from the main activity to send data to the remote device */
        public void enviar(String dadosEnviar) {
            byte[] msgBuffer = dadosEnviar.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {

            }
        }

        /* Call this from the main activity to shutdown the connection */
        /*public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }*/
    }
}
