package com.ganesh.library;

import androidx.appcompat.app.AppCompatActivity;
import cz.msebera.android.httpclient.Header;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private Button signin;
    private EditText rollno, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        signin = findViewById(R.id.signin);
        rollno = findViewById(R.id.rollno);
        pass = findViewById(R.id.passwd);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String roll = rollno.getText().toString();
                String passwd = pass.getText().toString();
                if (!validRollno(roll)) {
                    rollno.setError("Invalid Roll no");
                    return;
                }
                if (!validPass(passwd)) {
                    pass.setError("Password should be more than 7");
                    return;
                }
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams rp = new RequestParams();
                rp.add("rollno", roll);
                rp.add("pass", passwd);
                client.post("http://dgilibraryautomation.000webhostapp.com/verify.php", rp, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            JSONObject serverResp = new JSONObject(response.toString());
                            boolean b = serverResp.getBoolean("login");
                            if (b) {
                                String name = serverResp.getString("name");
//                                Toast.makeText(LoginActivity.this, name, Toast.LENGTH_SHORT).show();
                                Intent i = new Intent();
                                i.putExtra("LOGINRESULT",name);
                                i.putExtra("LOGINROLLNO",""+roll);
                                setResult(Activity.RESULT_OK,i);
                                finish();
                            } else {
                                pass.setError("Invalid Email or Password");
                                rollno.setError("Invalid Email or Password");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    boolean validRollno(String roll) {
        if (roll == null || roll.length() != 5) return false;

        try {
            Integer.parseInt(roll);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    boolean validPass(String pass) {
        return pass == null || pass.trim().length() >= 8;
    }

    @Override
    public void onBackPressed() {
        //        super.onBackPressed();
    }
}
