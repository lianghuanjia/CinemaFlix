package com.example.samflix;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MovieDetailPage extends AppCompatActivity {
    private ImageView poster;
    private ImageView backDrop;
    private TextView title;
    private TextView director;
    private TextView rating;
    private TextView year;
    private TextView genres;
    private TextView stars;
    private TextView description;
    private Button backButton;
    private String movieID;
    private String imageBaseUrl;
    private String backDropBaseUrl;

    //For sending request:
    private static final String TAG = SearchActivity.class.getName();
    private RequestQueue searchRequestQueue;
    private StringRequest stringRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        this.movieID = intent.getStringExtra("movieID");
        //Toast.makeText(this, "movieTitle is: " + movieID, Toast.LENGTH_LONG).show();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail_page);
        setUpUIView();
        getMovieDetail();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MovieDetailPage.this, SearchActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setUpUIView() {
        poster = findViewById(R.id.tvMovieDetailPoster); //What is R??
        backDrop = findViewById(R.id.tvMovieDetailBackDrop);
        title = findViewById(R.id.tvMovieDetailTitle);
        title.setMovementMethod(new ScrollingMovementMethod());
        director = findViewById(R.id.tvMovieDetailDirector);
        director.setMovementMethod(new ScrollingMovementMethod());
        rating = findViewById(R.id.tvMovieDetailRating);
        year = findViewById(R.id.tvMovieDetailYear);
        genres = findViewById(R.id.tvMovieDetailGenres);
        genres.setMovementMethod(new ScrollingMovementMethod());
        stars = findViewById(R.id.tvMovieDetailStars);
        stars.setMovementMethod(new ScrollingMovementMethod());
        description = findViewById(R.id.tvMovieDetailDescription);
        description.setMovementMethod(new ScrollingMovementMethod());
        backButton = findViewById(R.id.btnMovieDetailBackButton);
        imageBaseUrl = "https://image.tmdb.org/t/p/w185";
        backDropBaseUrl = "https://image.tmdb.org/t/p/w780";
    }

    private void getMovieDetail(){
        searchRequestQueue = Volley.newRequestQueue(this);
        String modifiedUrl;
        String url = "http://10.0.2.2:5014/api/movies/get/";
        modifiedUrl = url + movieID;
        stringRequest = new StringRequest(Request.Method.GET, modifiedUrl, new Response.Listener<String>() {
            //If success, execute code inside of onResponse.
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "Response: " + response);
                try {
                    JSONObject reader = new JSONObject(response);
                    if(reader.getInt("resultCode") == 210) {
                        JSONObject movie = reader.getJSONObject("movie");
                        title.setText(movie.getString("title"));
                        System.out.println("Finish title");
                        if(!movie.isNull("director")) {
                            director.setText("Director: " + movie.getString("director"));
                        }
                        else{
                            director.setText("Director: Not found");
                        }
                        System.out.println("Finish director");
                        if(!movie.isNull("rating")) {
                            rating.setText("Rating: " + movie.getString("rating"));
                        }
                        else{
                            rating.setText("Rating: Not found");
                        }
                        System.out.println("Finish rating");
                        if(!movie.isNull("year")) {
                            year.setText("Year:" + movie.getString("year"));
                        }else{
                            year.setText("Year: Not found");
                        }
                        System.out.println("Finish year");
                        if(!movie.isNull("overview")) {
                            description.setText("Overview: "+movie.getString("overview"));
                        }else{
                            description.setText("Overview: Not found");
                        }
                        System.out.println("Finish overview");
                        String genresText = "Genres:\n";
                        if(!movie.isNull("genres")) {
                            JSONArray genreArray = movie.getJSONArray("genres");
                            for (int i = 0; i < genreArray.length(); i++) {
                                JSONObject genreModel = genreArray.getJSONObject(i);
                                genresText += genreModel.getString("name") + "; ";
                            }
                            genres.setText(genresText);
                        }
                        else{
                            genres.setText(genresText + "Not found");
                        }
                        String starsText = "Stars:\n";
                        if(!movie.isNull("stars")) {
                            JSONArray starArray = movie.getJSONArray("stars");
                            for (int i = 0; i < starArray.length(); i++) {
                                JSONObject starModel = starArray.getJSONObject(i);
                                starsText += starModel.getString("name") + "\n";
                            }
                        }
                        else {
                            starsText += "Not found";
                        }
                        stars.setText(starsText);
                        if(!movie.isNull("backdrop_path")) {
                            String backDropFullUrl = backDropBaseUrl + movie.getString("backdrop_path");
                            Picasso.with(getApplicationContext()).load(backDropFullUrl).into(backDrop);
                        }
                        if(!movie.isNull("poster_path")) {
                            String imageFullUrl = imageBaseUrl + movie.getString("poster_path");
                            Picasso.with(getApplicationContext()).load(imageFullUrl).into(poster);
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "No movie found", Toast.LENGTH_LONG).show();
                    }

                }
                catch (JSONException e){
                    System.out.println(e.toString());
                    System.out.println("JsonException occurred in MovieDetailPage.getMovieDetail");
                }
            }
        }, new Response.ErrorListener() {
            //If fail, execute code inside of onErrorResponse.
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "Error: " +error.toString());
            }
        });

        searchRequestQueue.add(stringRequest);
    }
}
