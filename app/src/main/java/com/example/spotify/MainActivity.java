package com.example.spotify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import android.os.AsyncTask;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    /*
    * API_KEY 31fb0b043a3778bdbd4c04419643b0f2
    *
    * LAST FM Credentials
    *
    * username: tracker12321
    * password: Serres123@
    *
    * */
    private static String TOP_ARTIST_URL = "http://ws.audioscrobbler.com/2.0/?method=chart.gettopartists&api_key=31fb0b043a3778bdbd4c04419643b0f2&format=json";
    private static String POST_FAV_ARTIST_URL = "http://10.0.2.2:3000/favorite-artist";
    private static String DEL_FAV_ARTIST_URL = "http://10.0.2.2:3000/favorite-artist";
    private static String GET_FAV_ARTIST_URL = "http://10.0.2.2:3000/favorite-artist";

    List<ArtistModel> artistList;
    RecyclerView recyclerView;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        artistList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        requestQueue = VolleySingleton.getmInstance(this).getRequestQueue();
        artistList = new ArrayList<>();
//        fetchArtists();
        getFavoriteArtist();
    }

    private void fetchArtists(JSONArray savedFavoriteArtists) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, TOP_ARTIST_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jsonObject = response.getJSONObject("artists");
                    JSONArray artistArray = jsonObject.getJSONArray("artist");
                    Log.i("FAV ARTS", savedFavoriteArtists.toString());

                    for (int i = 0 ; i < artistArray.length(); i ++) {
                        JSONObject jsonObject1 = artistArray.getJSONObject(i);

                        ArtistModel model = new ArtistModel();
                        String fetchedMbid = jsonObject1.getString("mbid");
                        model.setMbid(fetchedMbid);
                        model.setName(jsonObject1.getString("name"));
                        model.setFavorite(false);

                        // check if fetched artist is favorite
                        for (int j = 0; j < savedFavoriteArtists.length(); j ++) {
                            JSONObject artist = savedFavoriteArtists.getJSONObject(j);
                            String mbid = artist.getString("fav_artist_mbid");
                            if (new String(fetchedMbid).equals(mbid)) {
                                model.setFavorite(true);
                                break;
                            }
                        }

                        // read image array
                        JSONArray imgArray = jsonObject1.getJSONArray("image");
                        JSONObject smallImage = imgArray.getJSONObject(0);
                        model.setImg(smallImage.getString("#text"));

                        artistList.add(model);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                FavoriteAdapter favoriteAdapter = new FavoriteAdapter(MainActivity.this, artistList, new ClickListener() {
                    @Override
                    public void onPositionClicked(int position) {
                        String name = artistList.get(position).getName();
                        String mbid = artistList.get(position).getMbid();

                        if (artistList.get(position).getFavorite()) {
                            deleteFavoriteArtist(mbid, name);
                        } else {
                            postFavoriteArtist(mbid, name);
                        }

                    }
                });
                recyclerView.setAdapter(favoriteAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    private void getFavoriteArtist() {
      JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, GET_FAV_ARTIST_URL, null, new Response.Listener<JSONObject>() {
          @Override
          public void onResponse(JSONObject response) {
              try {
                  JSONArray favArtists = response.getJSONArray("artists");

                  fetchArtists(favArtists);

              } catch (JSONException e) {
                  e.printStackTrace();
              }
          }
      }, new Response.ErrorListener() {
          @Override
          public void onErrorResponse(VolleyError error) {
              Log.e("Error", "fetching favorite artits");
          }
      });
      requestQueue.add(jsonObjectRequest);
    }

    private void postFavoriteArtist(String mbid, String name) {
        RequestQueue requestQueue = VolleySingleton.getmInstance(this).getRequestQueue();

        JSONObject postData = new JSONObject();

        try {
            postData.put("fav_artist_mbid", mbid);
            postData.put("fav_artist_name", name);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, POST_FAV_ARTIST_URL, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getBaseContext(), "Artist saved", Toast.LENGTH_SHORT).show();
                Log.i("[+] save: ", name);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("[-] save failed: ",  name + " " + error);
                Toast.makeText(getBaseContext(), "Save failed", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);

    }

    private void deleteFavoriteArtist(String mbid, String name) {
        RequestQueue requestQueue = VolleySingleton.getmInstance(this).getRequestQueue();

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, DEL_FAV_ARTIST_URL + '/' + mbid, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getBaseContext(), "Deleted!", Toast.LENGTH_SHORT).show();
                Log.i("[+] Delete: ", name);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();
                Log.i("[-] Delete error: ", name);
            }
        });
        requestQueue.add(stringRequest);

    }
}