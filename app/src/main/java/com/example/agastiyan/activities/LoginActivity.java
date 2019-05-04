package com.example.agastiyan.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.agastiyan.R;
import com.example.agastiyan.appconfig.AppSingleton;
import com.example.agastiyan.connection.AppConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    private static final String TAG = "LoginActivity";
    private Button stringRequestButton;
    private View showDialogView;
    private TextView alertTextView,outputTextView;
    private EditText E_mail,Password;
    private ImageView outputImageView;

    public static final String alert = "Username or Password not valid";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(this);

        stringRequestButton = (Button)findViewById(R.id.bt_login);
        E_mail=(EditText)findViewById(R.id.et_email);
        Password=(EditText)findViewById(R.id.et_pass);

        alertTextView=(TextView)findViewById(R.id.tv_alert);

        stringRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                volleyStringRequest();
                /*Intent intent =new Intent(LoginActivity.this,MainActivity.class);
                intent.putExtra("for_user","kingsley");
                intent.putExtra("for_id","5523946417946624");

                startActivity(intent);
                finish();*/
            }
        });

    }

    public void volleyStringRequest(){

        String  REQUEST_TAG = "Activities.LoginActivity";
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        HashMap<String,String> loginparam = new HashMap<String,String>();
        loginparam.put("username",E_mail.getText().toString());
        loginparam.put("password",Password.getText().toString());

        JsonObjectRequest jobjReq = new JsonObjectRequest(Request.Method.POST, AppConnection.URL_LOGIN,new JSONObject(loginparam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try {
                            Boolean valid = response.getBoolean("success");

                            if(valid){

                                // String message = response.getString("message");
                                String data = response.getString("username");
                                String uId = response.get("key").toString();
                                Toast.makeText(getApplicationContext(),"success",
                                        Toast.LENGTH_LONG).show();

                                Intent intent =new Intent(LoginActivity.this,MainActivity.class);
                                intent.putExtra("for_user",data);

                                SharedPreferences pref = getSharedPreferences("MyPref", 0); // 0 - for private mode
                                SharedPreferences.Editor editor = pref.edit();

                                editor.putString("user_key", uId); // Storing string
                                editor.commit();

                                startActivity(intent);
                                finish();
                            }
                            else if(!valid){

                                alertTextView.setText(alert);
                            }
                        }
                        catch (JSONException je){
                            je.printStackTrace();
                        }

                        progressDialog.hide();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "This is a message " + error.getMessage(),
                        Toast.LENGTH_LONG);

                toast.show();
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                progressDialog.hide();
            }
        });
        // Adding String request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jobjReq, REQUEST_TAG);
    }

    public void volleyCacheRequest(String url){

        Cache cache = AppSingleton.getInstance(getApplicationContext()).getRequestQueue().getCache();
        Cache.Entry entry = cache.get(url);

        if(entry != null){
            try {
                String data = new String(entry.data, "UTF-8");
                // handle data, like converting it to xml, json, bitmap etc.,
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        else{

        }
    }

    public void volleyInvalidateCache(String url){
        AppSingleton.getInstance(getApplicationContext()).getRequestQueue().getCache().invalidate(url, true);
    }

    public void volleyDeleteCache(String url){
        AppSingleton.getInstance(getApplicationContext()).getRequestQueue().getCache().remove(url);
    }

    public void volleyClearCache(){
        AppSingleton.getInstance(getApplicationContext()).getRequestQueue().getCache().clear();
    }
}
