package com.ganesh.library;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import cz.msebera.android.httpclient.Header;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences sp;
    private BluetoothAdapter bluetoothAdapter;
    public static final String MAC = "00:21:13:02:63:93";
    public static final int REQUEST_ENABLE_BT = 9;
    public static final UUID mod_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    BluetoothSocket socket;
    BluetoothDevice device;
    ConnectThread connectThread;
    Button btSend;
    String rollno;
    ListView lv;
    boolean deviceConnected = false;
    TextView status,fine;
    Book[] books;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sp = this.getPreferences(Context.MODE_PRIVATE);

        if (sp.contains("name")) {
            ActionBar a = getSupportActionBar();
            if (a != null)
                a.setTitle(sp.getString("name", ""));
            rollno = sp.getString("rollno", "");
        } else {
            Intent i = new Intent(this, LoginActivity.class);
            startActivityForResult(i, 2);
        }
        checkBtAvailable();

        btSend = findViewById(R.id.sendbt);
        lv = findViewById(R.id.books);
        status = findViewById(R.id.status);
        fine = findViewById(R.id.fine);

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                connectThread.write("ping".getBytes());
//                Log.d("bluetooth:",rollno);
//                IssueBook("102");
                ShowBooks();
            }
        });


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!deviceConnected) {
                    ShowAlert("Error", "Device Not Found !");
                } else if (books.length >= 5) {
                    ShowAlert("Error", "Book Issue Limit Exceed");
                } else {
                    Intent i = new Intent(MainActivity.this, QrScanActivity.class);
                    startActivityForResult(i, 1);
                }
            }
        });

        if (bluetoothAdapter.isEnabled()) {
            initBt();
        } else {
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(i, REQUEST_ENABLE_BT);
        }

        ShowBooks();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
            initBt();
        }

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            String result = data.getStringExtra("QR_RESULT");
            IssueBook(result);
        }

        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            String name = data.getStringExtra("LOGINRESULT"),
                    rollno = data.getStringExtra("LOGINROLLNO");
            SharedPreferences.Editor ed = sp.edit();
            ed.putString("name", name);
            ed.putString("rollno", rollno);
            Toast.makeText(this, rollno, Toast.LENGTH_SHORT).show();
            ed.commit();
        }
    }

    // function checks wether device supports bluetooth
    void checkBtAvailable() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            ShowAlert("Error", "Bluetooth is not available in this device.");
            finish();
        }
    }

    Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == ConnectThread.RESP_MESSAGE) {
                String txt = (String) msg.obj;
                Log.d("bluettoth",txt);
                if(txt.length() == 13 || txt.length() == 14){
                    try{
                        JSONObject obj = new JSONObject(txt);
                        boolean o = obj.getBoolean("done");
                        if(o)
                            ShowAlert("Success","Book Issued");
                        else
                            ShowAlert("Cannot Issue Book","Book is either issued or not available");
                    }catch (JSONException jex){
                        jex.printStackTrace();
                    }
                    ShowBooks();
                }
            }
        }
    };

    public void IssueBook(String bookid) {
        if(deviceConnected){
            String s = "rollno="+rollno+"&bookid="+bookid;
            connectThread.write(s.getBytes());
        }
//        AsyncHttpClient client = new AsyncHttpClient();
//        RequestParams rp = new RequestParams();
//        rp.add("rollno", rollno);
//        rp.add("bookid", bookid);
//        client.post("http://dgilibraryautomation.000webhostapp.com/issuebook.php", rp, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                try {
//                    JSONObject serverResp = new JSONObject(response.toString());
//                    boolean obj = serverResp.getBoolean("done");
//                    String title = "Success", message = "Book Issued !";
//                    if (!obj) {
//                        title = "Cannot Issue Book";
//                        message = "Book is either issued or not available";
//                    } else
//                        ShowBooks();
//                    ShowAlert(title, message);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }


    @Override
    public void onBackPressed() {
        if (connectThread != null)
            connectThread.cancel();
        super.onBackPressed();
    }

    private void initBt() {
        if (bluetoothAdapter.isEnabled()) {
            device = bluetoothAdapter.getRemoteDevice(MAC);
            try {
                Thread.sleep(600);
            } catch (Exception ex) {
            }
            BluetoothSocket sock = null;
            try {
                sock = device.createInsecureRfcommSocketToServiceRecord(mod_UUID);
                this.socket = sock;
                this.socket.connect();
                Log.d("Bluetooth", "Connected to " + device.getName());
                if (device != null)
                    deviceConnected = true;

            } catch (Exception e) {
                try {
                    this.socket.close();
                } catch (Exception ex) {
                }
            }

            connectThread = new ConnectThread(this.socket, handler);
            connectThread.start();
            if (deviceConnected)
                status.setText("Device Connected");

        }
    }

    ProgressDialog progressDialog;


    private void ShowBooks() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setTitle("Showing Books");
        progressDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams rp = new RequestParams();
        rp.add("rollno", rollno);
        client.post("http://dgilibraryautomation.000webhostapp.com/getissue.php", rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                progressDialog.dismiss();
                books = new Book[response.length()];
                long ls = 0;
                try {
                    for (int i = 0; i < response.length(); ++i) {
                        JSONObject jsonObject = response.getJSONObject(i);
//                        books.add((i + 1) + " " + jsonObject.getString("name"));
                        books[i] = new Book(
                                jsonObject.getString("name"),
                                jsonObject.getString("issue_date")
                        );
                        ls += books[i].calcFine();
                    }
                } catch (Exception e) {
                }
                BookAdapter adapter = new BookAdapter(MainActivity.this, R.layout.list_book_row, books);
                lv.setAdapter(adapter);
                fine.setText("Fine : "+ls+" RS");
            }


        });
    }

    private void ShowAlert(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                    }
                })
                .setIcon(android.R.drawable.stat_notify_error)
                .show();
    }

    public class ConnectThread extends Thread {
        Handler handler;
        BluetoothSocket socket;

        InputStream inputStream;
        OutputStream outputStream;
        public static final int RESP_MESSAGE = 10;

        public ConnectThread(BluetoothSocket socket, Handler handler) {
            this.socket = socket;
            this.handler = handler;

            InputStream is = null;
            OutputStream os = null;
            try {
                is = socket.getInputStream();
                os = socket.getOutputStream();
            } catch (Exception ex) {
                Log.d("Bluetooth", ex.getMessage());
            }
            this.inputStream = is;
            this.outputStream = os;
            try {
                outputStream.flush();
            } catch (Exception ex) {
                return;
            }
            Log.d("Bluetooth", "Io ");
        }

        @Override
        public void run() {
            super.run();
            bluetoothAdapter.cancelDiscovery();

            if (inputStream != null)
                Log.d("Bluetooth", "not null");

            while (true) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    int i = this.inputStream.available();
                    if (i > 0)
                        Log.d("Bluetooth", "" + i);

                    StringBuilder tmp = new StringBuilder();
                    for (int j = 0; j < i; ++j)
                        tmp.append((char) this.inputStream.read());
                    if (tmp.length() > 0) {
                        Message msg = new Message();
                        msg.what = RESP_MESSAGE;
                        msg.obj = tmp.toString();
                        this.handler.sendMessage(msg);
                    }
                } catch (Exception ex) {
                    Log.d("Bluetooth", ex.getMessage());
                    break;
                }
            }
            Log.d("Bluetooth", "Outside loop");
        }

        public void write(byte[] ms) {
            try {
                Log.d("Bluetooth", "Writing Bytes");
                this.outputStream.write(ms);
            } catch (Exception e) {
            }
        }

        public void cancel() {
            try {
                this.socket.close();
            } catch (Exception ex) {
            }
        }
    };


}
