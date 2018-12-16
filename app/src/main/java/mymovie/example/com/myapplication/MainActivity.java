package mymovie.example.com.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mymovie.example.com.myapplication.model.MovieLIst;


public class MainActivity extends AppCompatActivity {

    static final String X_NAVER_ID = "X-Naver-Client-Id";
    static final String X_NAVER_SECRET = "X-Naver-Client-Secret";
    static final String CLIENT_ID = "Ek3Ih2RKgHQfa4GrJpiB";
    static final String CLIENT_SECRET = "Y8sbTuh1Vy";

    RecyclerView recyclerView;
    EditText searchText;
    ProgressDialog dialog;
    boolean netStat;
    boolean isFirstSearchRequest = true;
    int loop;
    int count;
    MovieListAda adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchText = findViewById(R.id.editText);
        Button button = findViewById(R.id.button);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        settingAdapter();
        settingButtonListener(button);
    }

    public void settingAdapter() {
        adapter = new MovieListAda(getApplicationContext());
        adapter.setOnItemClickListener(new MovieListAda.OnItemClickListener() {
            @Override
            public void onItemClick(MovieListAda.ViewHolder holder, View view, int position) {
                MovieLIst.MovieContent item = adapter.getItem(position);

                netStat = NetStat.getConnectivityStatus(getApplicationContext());
                if (netStat == true) {
                    String url = item.getLink();
                    startWebView(url);
                } else
                    Toast.makeText(getApplicationContext(), "인터넷 연결 안대요", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(adapter);
    }

    public void settingButtonListener(Button button) {
        final Activity activity = this;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFirstSearchRequest = true;
                loop = 0;
                count = 0;
                netStat = NetStat.getConnectivityStatus(getApplicationContext());

                if (netStat == true) {
                    String searchWord = searchText.getText().toString();
                    if (searchWord.equals(""))
                        Toast.makeText(getApplicationContext(), "검색어 없어요", Toast.LENGTH_SHORT).show();
                    else {
                        showProgressDialog();
                        requestSearchWord(searchWord, 1);
                    }
                    hideKeyboard(activity);
                } else {
                    Toast.makeText(getApplicationContext(), "인터넷 연결 확인해보세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void requestSearchWord(String searchWord, int sno) {
        final Map<String, String> headers = new HashMap<>();
        headers.put(X_NAVER_ID, CLIENT_ID);
        headers.put(X_NAVER_SECRET, CLIENT_SECRET);
        String targetUrl = "https://openapi.naver.com/v1/search/movie.json?query=" + searchWord + "&display=100";

        if (sno != 0) targetUrl += "&start=" + sno;

        StringRequest request = new StringRequest(Request.Method.GET, targetUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        processResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse response = error.networkResponse;
                        if (response != null) {
                            switch (response.statusCode) {

                                case 404:
                                case 422:
                                case 401:
                                    try {

                                        HashMap<String, String> result = new Gson().fromJson(new String(response.data),
                                                new TypeToken<Map<String, String>>() {
                                                }.getType());

                                        if (result != null && result.containsKey("error")) {
                                            Toast.makeText(getApplicationContext(), result.get("error"), Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    Toast.makeText(getApplicationContext(), ((VolleyError) error).getMessage(), Toast.LENGTH_SHORT).show();

                                default:
                                    Toast.makeText(getApplicationContext(), "시간초과", Toast.LENGTH_SHORT).show();
                            }
                        }

                        Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }
        };

        request.setShouldCache(false);
        HttpHelper.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    public void processResponse(String response) {
        Gson gson = new Gson();
        MovieLIst movieList = gson.fromJson(response, MovieLIst.class);


        if (isFirstSearchRequest) adapter.items.clear();


        if (0 < Integer.parseInt(movieList.total)) {
            isFirstSearchRequest = false;

            adapter.addItems(movieList.items);
            loop = Integer.parseInt(movieList.total) / 100;

            if (0 < loop && count < loop) {
                count++;
                if (count < 10) requestSearchWord(searchText.getText().toString(), count * 100 + 1);
            }

        } else {
            Toast.makeText(getApplicationContext(), "결과가 없어요", Toast.LENGTH_SHORT).show();
        }
        adapter.notifyDataSetChanged();
        dialog.cancel();
    }


    public void startWebView(String url) {
        Bitmap arrowIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_arrow_back);
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(getColor(R.color.colorPrimary));
        builder.setCloseButtonIcon(arrowIcon);

        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(url));
    }


    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    public void showProgressDialog() {
        dialog = new ProgressDialog(this);

        SpannableString message = new SpannableString("인내의시간");
        message.setSpan(new RelativeSizeSpan(1.5f), 0, message.length(), 0);
        dialog.setMessage(message);
        dialog.show();

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = 900;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(params);
    }
}
