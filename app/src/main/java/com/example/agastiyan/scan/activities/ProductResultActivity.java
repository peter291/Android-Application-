package com.example.agastiyan.scan.activities;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.example.agastiyan.R;
import com.example.agastiyan.appconfig.AppSingleton;
import com.example.agastiyan.connection.AppConnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProductResultActivity extends AppCompatActivity {

    private static final String TAG = ProductResultActivity.class.getSimpleName();

    SharedPreferences prefs;

    // url to search barcode
    // format
    /*
    {"size":"500sqX600sq",
     "name":"Store room",
     "model":"2009",
     "error":false,
     "url":"https://storage.googleapis.com/bookshelfimage-223504.appspot.com/productImages/product_2.jpg",
     "manufacturer":"agastiyan"}*/

    // "https://agasti-cloud-api.appspot.com/rest/product/5629499534213120/scan";

    // private TextView txtName, txtSize, txtManufacturer, txtModel, txtColour, txtError;
    private TextView txtSize, txtColor, txtPrice, txtDesc, txtError;
    private TextView descSize, descColor, descPrice, descDesc, descError;
    private LinearLayout linear_image_layout, linear_video_layout;
    private ImageView imgPoster;
    private VideoView videoView;
    // private Button btnBuy;
    private ProgressBar progressBar;
    // private ProductView ticketView;
    private String scan_url;
    private String code;
    private MediaController mediacontroller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        prefs = getSharedPreferences("MyPref", 0); // 0 - for private mode

        // Toolbar toolbar = findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // txtName = findViewById(R.id.name);
        txtSize = findViewById(R.id.product_size);
        txtPrice = findViewById(R.id.product_price);
        txtColor = findViewById(R.id.product_color);
        txtDesc = findViewById(R.id.product_description);

        descSize = findViewById(R.id.textSize);
        descPrice = findViewById(R.id.textPrice);
        descColor = findViewById(R.id.textColor);
        descDesc = findViewById(R.id.textDesc);

        linear_image_layout = (LinearLayout) findViewById(R.id.image_layout);
        linear_video_layout = (LinearLayout) findViewById(R.id.video_layout);

        // txtManufacturer = findViewById(R.id.pr_manufactured_by);
        // txtModel = findViewById(R.id.pr_model);
        //imgPoster = findViewById(R.id.poster);
        // txtColour = findViewById(R.id.pr_colour);
        imgPoster = findViewById(R.id.imageView4);
        videoView = findViewById(R.id.videoView4);
        txtError = findViewById(R.id.txt_error);
        // ticketView = findViewById(R.id.layout_ticket);
        progressBar = findViewById(R.id.progressBar);

        scan_url = AppConnection.MAIN_API + prefs.getString("user_key","") + "/scan";

        code = getIntent().getStringExtra("code");

        Toast.makeText(getApplicationContext(), code, Toast.LENGTH_LONG).show();

        // close the activity in case of empty barcode
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(getApplicationContext(), "Barcode is empty!", Toast.LENGTH_LONG).show();
            finish();
        }

        // search the barcode
        searchBarcode(code);
    }

    /**
     * Searches the barcode by making http call
     * Request was made using Volley network library but the library is
     * not suggested in production, consider using Retrofit
     */
    private void searchBarcode(final String code) {

        HashMap<String,String> scanparam = new HashMap<String, String >();
        scanparam.put("product",code);
        // making volley's json request
        JsonObjectRequest jobjReq = new JsonObjectRequest(Request.Method.POST,
                scan_url, new JSONObject(scanparam),new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try{
                            if (!response.getBoolean("error")) {

                                renderProduct(response);

                            } else {
                                // no movie found
                                showNoTicket();
                            }
                        } catch(JSONException je) {

                            je.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                showNoTicket();
            }

        });

        // Adding String request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jobjReq, TAG);
    }

    private void showNoTicket() {

        txtError.setVisibility(View.VISIBLE);
        // ticketView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void renderProduct(JSONObject response) throws JSONException{

        // Product pr = new Gson().fromJson(response.toString(), Product.class);
        /*
    {"size":"500sqX600sq",
     "name":"Store room",
     "model":"2009",
     "error":false,
     "url":"https://storage.googleapis.com/bookshelfimage-223504.appspot.com/productImages/product_2.jpg",
     "manufacturer":"agastiyan"}*/
        if(!response.getBoolean("error")) {

            JSONObject data = new JSONObject(response.getString("data"));

            // txtName.setText(code);
            descSize.setText("");
            descDesc.setText("");
            descPrice.setText("");
            descColor.setText("");

            try {

                linear_image_layout.setVisibility(View.GONE);

                if(false) {

                    Glide.with(this).load(data.getString("img_url")).into(imgPoster);
                }

                linear_video_layout.setVisibility(View.VISIBLE);
                mediacontroller = new MediaController(this);
                mediacontroller.setAnchorView(videoView);

                videoView.setMediaController(mediacontroller);
                videoView.setVideoURI(Uri.parse(data.getString("img_url")));
                videoView.start();
                videoView.requestFocus();

                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        Toast.makeText(getApplicationContext(), "Video over", Toast.LENGTH_SHORT).show();
                    }
                });

                videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        Log.d("API123", "What " + what + " extra " + extra);
                        return false;
                    }
                });

            } catch (JSONException e) {

                e.printStackTrace();
            }

            if(false) {

                // btnBuy.setText(getString(R.string.btn_description));
                // btnBuy.setTextColor(ContextCompat.getColor(this, R.color.btn_disabled));
            }

            // ticketView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private class Product {

        String name;
        String url;
        String size;
        String model;
        String colour;
        String manufacturer;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getColour() {
            return colour;
        }

        public void setColour(String colour) {
            this.colour = colour;
        }

        public String getManufacturer() {
            return manufacturer;
        }

        public void setManufacturer(String maufacturer) {
            this.manufacturer = maufacturer;
        }
    }

}
