package com.example.agastiyan.scan.activities;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.example.agastiyan.R;
import com.example.agastiyan.appconfig.AppSingleton;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProductResultActivity extends AppCompatActivity {

    private static final String TAG = ProductResultActivity.class.getSimpleName();

    // url to search barcode
    // format
    /*
    {"size":"500sqX600sq",
     "name":"Store room",
     "model":"2009",
     "error":false,
     "url":"https://storage.googleapis.com/bookshelfimage-223504.appspot.com/productImages/product_2.jpg",
     "manufacturer":"agastiyan"}*/
    private static final String URL = "https://agastiyan-219316.appspot.com/getScanDetails";

    private TextView txtName, txtSize, txtManufacturer, txtModel, txtColour, txtError;
    private ImageView imgPoster;
    private Button btnBuy;
    private ProgressBar progressBar;
    private ProductView ticketView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_activity_product_result);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtName = findViewById(R.id.name);
        txtSize = findViewById(R.id.pr_size);
        txtManufacturer = findViewById(R.id.pr_manufactured_by);
        txtModel = findViewById(R.id.pr_model);
        imgPoster = findViewById(R.id.poster);
        txtColour = findViewById(R.id.pr_colour);
        btnBuy = findViewById(R.id.btn_buy);
        imgPoster = findViewById(R.id.poster);
        txtError = findViewById(R.id.txt_error);
        ticketView = findViewById(R.id.layout_ticket);
        progressBar = findViewById(R.id.progressBar);

        String code = getIntent().getStringExtra("code");
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
        // making volley's json request
        StringRequest sr = new StringRequest(Request.Method.POST,
                URL, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, "Product response: " + response.toString());

                        // check for success status
                        if (!response.isEmpty()) {

                            try {
                                JSONObject out = new JSONObject(response);
                                renderProduct(out);
                            } catch(JSONException je) {
                                je.printStackTrace();
                            }

                        } else {
                            // no movie found
                            showNoTicket();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                showNoTicket();
            }

        }){
            @Override
            protected Map<String, String> getParams()
            {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();

                params.put("type", "2316");
                params.put("serial", code);
                return params;
            }

        };

        // Adding String request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(sr, TAG);
    }

    private void showNoTicket() {

        txtError.setVisibility(View.VISIBLE);
        ticketView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void renderProduct(JSONObject response) {

        Product pr = new Gson().fromJson(response.toString(), Product.class);

        if(pr!=null) {

            txtName.setText(pr.getName());
            txtSize.setText(pr.getSize());
            txtManufacturer.setText(pr.getManufacturer());
            txtModel.setText(pr.getModel());
            txtColour.setText(pr.getColour());

            Glide.with(this).load(pr.getUrl()).into(imgPoster);

            if(true) {

                btnBuy.setText(getString(R.string.btn_description));
                btnBuy.setTextColor(ContextCompat.getColor(this, R.color.btn_disabled));
            }

            ticketView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }

    }
    /**
     * Rendering movie details on the ticket
     */
    private void renderMovie(JSONObject response) {
        try {
/*{
    "name": "Dunkirk",
    "poster": "https://api.androidhive.info/barcodes/dunkirk.jpg",
    "duration": "1hr 46min",
    "rating": 4.6,
    "released": true,
    "genre": "Action",
    "price": "₹200",
    "director": "Christopher Nolan"
}*/
            // converting json to movie object
            Movie movie = new Gson().fromJson(response.toString(), Movie.class);

            if (movie != null) {

                txtName.setText(movie.getName());
                // txtDirector.setText(movie.getDirector());
                // txtDuration.setText(movie.getDuration());
                // txtGenre.setText(movie.getGenre());
                // txtRating.setText("" + movie.getRating());
                // txtPrice.setText(movie.getPrice());

                Glide.with(this).load(movie.getPoster()).into(imgPoster);

                if (movie.isReleased()) {
                    btnBuy.setText(getString(R.string.btn_buy_now));
                    btnBuy.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                } else {
                    btnBuy.setText(getString(R.string.btn_description));
                    btnBuy.setTextColor(ContextCompat.getColor(this, R.color.btn_disabled));
                }

                ticketView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            } else {
                // movie not found
                showNoTicket();
            }
        } catch (JsonSyntaxException e) {

            Log.e(TAG, "JSON Exception: " + e.getMessage());
            showNoTicket();
            Toast.makeText(getApplicationContext(), "Error occurred. Check your LogCat for full report", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // exception
            showNoTicket();
            Toast.makeText(getApplicationContext(), "Error occurred. Check your LogCat for full report", Toast.LENGTH_SHORT).show();
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

    private class Movie {

        String name;
        String director;
        String poster;
        String duration;
        String genre;
        String price;
        float rating;

        @SerializedName("released")
        boolean isReleased;

        public String getName() {
            return name;
        }

        public String getDirector() {
            return director;
        }

        public String getPoster() {
            return poster;
        }

        public String getDuration() {
            return duration;
        }

        public String getGenre() {
            return genre;
        }

        public String getPrice() {
            return price;
        }

        public float getRating() {
            return rating;
        }

        public boolean isReleased() {
            return isReleased;
        }
    }
}
