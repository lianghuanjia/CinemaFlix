package com.example.samflix;

import android.content.Intent;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    private EditText movieTitle;
    private Button searchButton;
    private Integer offset;
    private Button prevButton;
    private Button nextButton;

    //For sending request:
    private static final String TAG = SearchActivity.class.getName();
    private RequestQueue searchRequestQueue;
    private StringRequest stringRequest;
    private String url = "http://10.0.2.2:5014/api/movies/search";

    //ListView
    private ArrayList<MovieModel> movieList; // = new ArrayList<>()
    //private ListView listView = findViewById(R.id.SearchListView);
    //private CustomAdapter customAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        String registerMsg = intent.getStringExtra("LogInSuccessMessage");
        Toast.makeText(getApplicationContext(), registerMsg, Toast.LENGTH_LONG).show();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setUpUIView();
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showSearchMovieTitle();
                offset = 0;
                sendRequestAndGetResponse();
            }
        });
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(offset == 0){
                    System.out.println("No previous page");
                    Toast.makeText(getApplicationContext(), "There is no previous page", Toast.LENGTH_LONG).show();
                }
                else{
                    offset = offset - 10;
                    System.out.println("Previous");
                    System.out.println("Offset: " + offset);
                    sendRequestAndGetResponse();
                }
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                offset = offset+10;
                System.out.println("Next");
                System.out.println("Offset: " + offset);
                sendRequestAndGetResponse();
            }
        });
    }

    private void setUpUIView(){
        movieTitle = findViewById(R.id.etSearchMovieTitle);
        searchButton = findViewById(R.id.btnSearchButton);
        offset = 0;
        prevButton = findViewById(R.id.btnSearchPreviousButton);
        nextButton = findViewById(R.id.btnSearchNextButton);
    }


    private void sendRequestAndGetResponse(){
        movieList = new ArrayList<>();
        searchRequestQueue = Volley.newRequestQueue(this);
        String modifiedUrl;
        if(movieTitle.getText().toString().isEmpty()){
            modifiedUrl = url+"?offset="+offset;
        }
        else{
            modifiedUrl = url + "?title=" + movieTitle.getText().toString() +"&offset="+offset;
        }
        System.out.println("url: " + modifiedUrl);
        stringRequest = new StringRequest(Request.Method.GET, modifiedUrl, new Response.Listener<String>() {
            //If success, execute code inside of onResponse.
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "Response: " + response);
                try {
                    JSONObject reader = new JSONObject(response);
                    if(reader.getInt("resultCode")==210) {
                        JSONArray movies = reader.getJSONArray("movies");
                        for (int i = 0; i < movies.length(); i++) {
                            JSONObject movie = movies.getJSONObject(i);
                            MovieModel mm = new MovieModel();
                            mm.setMovieID(movie.getString("movieId"));
                            mm.setTitle(movie.getString("title"));
                            mm.setRating(movie.getString("rating"));
                            mm.setNumVotes(movie.getString("numVotes"));
                            movieList.add(mm);
                        }
                        ListView listView = findViewById(R.id.SearchListView);
                        CustomAdapter customAdapter = new CustomAdapter();
                        listView.setAdapter(customAdapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Intent intent = new Intent(SearchActivity.this, MovieDetailPage.class);
                                intent.putExtra("movieID", movieList.get(i).getMovieID());
                                startActivity(intent);
                            }
                        });
                    }
                    else if(reader.getInt("resultCode") == 211){
                        Toast.makeText(getApplicationContext(), reader.getString("message"), Toast.LENGTH_LONG).show();
                    }
                }
                catch (JSONException e){
                    System.out.println(e.toString());
                    System.out.println("JsonException occurred in SearchActivity.sendRequestAndGetResponse");
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

    //ListView:

    class CustomAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return movieList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.customlayout, null);
            TextView tvTitle = view.findViewById(R.id.CustomLayoutMovieTitle);
            TextView tvRating = view.findViewById(R.id.CustomLayoutRating);
            TextView tvNumVotes = view.findViewById(R.id.CustomLayoutNumVotes);

            tvTitle.setText("Title: "+movieList.get(i).getTitle());
            tvRating.setText("Rating: "+movieList.get(i).getRating());
            tvNumVotes.setText("NumVotes: "+movieList.get(i).getNumVotes());
            return view;
        }
    }
}
