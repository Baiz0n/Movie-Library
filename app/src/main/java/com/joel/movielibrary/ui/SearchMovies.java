package com.joel.movielibrary.ui;

import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.joel.movielibrary.R;
import com.joel.movielibrary.Tools.ImageConverter;
import com.joel.movielibrary.Tools.RecycleAdapter;
import com.joel.movielibrary.bloc.sql.data.RowData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchMovies extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static final String TAG = "SearchMovie";

    private String key = "a1b476d51d114952874783834cd7b9c2";
    private String urlDiscover = "https://api.themoviedb.org/3/discover/movie?api_key="+key+"&sort_by=popularity.desc";
    private String urlSearch = "https://api.themoviedb.org/3/search/movie?api_key="+key+"&query=";
    private String urlImage = "https://image.tmdb.org/t/p/w500";

    ArrayList<String> mTitles;
    ArrayList<String> mDescs;
    ArrayList<Bitmap> mImages;
    ArrayList<String> mImgUrl;
    RecyclerView recyclerView;
    ProgressBar progress;
    TextView text;
    private boolean list = false;
    private boolean bar = true;

    private boolean loadFinish = false;
    private boolean stopSearch = false;

    private RowData rowData;
    private boolean init = false;

    String transferQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_movies);

        text = findViewById(R.id.loadingText);
        progress = findViewById(R.id.progressBar);
        progress.setVisibility(View.GONE);
        recyclerView = findViewById(R.id.recycle_view);

        if ( savedInstanceState == null ) {

            fetchData(urlDiscover, "");
        }

    } // end onCreate

    private void fetchData(String url, String query) {

        stopSearch = true;

        preview(bar);

        query.replaceAll(" ", "+");

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url + query)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

                Log.e(TAG, "FAILED ON RESPONSE");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress.setVisibility(View.GONE);
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) {


                try {
                    String jsonData = response.body().string();

                    if (response.isSuccessful()) {

                        loading(5,0,0);

                        mTitles = new ArrayList<>();
                        mDescs = new ArrayList<>();
                        mImages = new ArrayList<>();
                        mImgUrl = new ArrayList<>();

                        String title;
                        String desc;
                        String imagePath;
                        Bitmap image;

                        int stringLimit = 76;;

                        JSONObject content = new JSONObject(jsonData);
                        JSONArray result = content.getJSONArray("results");

                        JSONObject thisResult;

                        loading(7,result.length(),0);

                        int temp = 93/result.length();
                        int loader = temp+7;

                        for ( int i = 0; i < result.length(); i++ ) {

                            thisResult = result.getJSONObject(i);
                            title = thisResult.getString("title");
                            desc = thisResult.getString("overview");

                            imagePath = urlImage + thisResult.getString("backdrop_path");
                            image = ImageConverter.getUrlImage(imagePath);

                            mImgUrl.add(imagePath);
                            mTitles.add(title);
                            mDescs.add(desc);
                            mImages.add(image);

                            loading(7+loader, result.length(), i);
                            loader += temp;
                        }

                        rowData = new RowData(mTitles,mDescs,mImages,mImgUrl);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                preview(list);
                            }
                        });

                        stopSearch = false;

                    }
                } catch(Exception e) {
                    Log.e(TAG,"Exception caught: " + e);
                }

            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search,menu);

        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        if ( stopSearch ) {

            Toast.makeText(this,"Search is disabled while loading", Toast.LENGTH_SHORT).show();

        } else {

            setTitle("Searched: "+query);
            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
            );
            fetchData(urlSearch, query);
        }

        transferQuery = query;
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        return false;
    }

    @Override
    public boolean onSearchRequested() {

        return super.onSearchRequested();
    }

    public void initRecyclerView(ArrayList<String> mTitles, ArrayList<String> mDescs, ArrayList<Bitmap> mImages, ArrayList<String> mImgUrl) {
        Log.d(TAG, "Initted!");

        RecycleAdapter adapter = new RecycleAdapter(mTitles, mDescs, mImages, this, false, mImgUrl);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        init = true;
    }

    private void loading(final int loader,final int result, final int i) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progress.setProgress(loader);
                text.setText("Loaded " + i + "/"+ result + " movies");
            }
        });

    }

    private void preview(boolean show) {

        if (show) {

            if (loadFinish) {

                recyclerView.setVisibility(View.INVISIBLE);

            } loadFinish = true;

            progress.setProgress(0);
            progress.setVisibility(View.VISIBLE);

        } else {

            progress.setVisibility(View.GONE);
            initRecyclerView(mTitles,mDescs,mImages,mImgUrl);
            recyclerView.setVisibility(View.VISIBLE);
            text.setText("");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if ( transferQuery != null ) {

            outState.putString("query", transferQuery);

        } else {

            outState.putString("query", "");
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        try {

            String query = Objects.requireNonNull(savedInstanceState.getString("query"));

            if(!query.equals("")) {

                fetchData(urlSearch, query);

            } else {

                fetchData(urlDiscover, query);
            }

        } catch ( NullPointerException e ) {

            Log.e( TAG , "ERROR CAUGHT! ===>  " + e);

            fetchData(urlDiscover,"");
        }

    }
}
