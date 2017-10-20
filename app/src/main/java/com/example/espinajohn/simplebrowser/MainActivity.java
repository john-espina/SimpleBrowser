package com.example.espinajohn.simplebrowser;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Selection;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebBackForwardList;
import android.webkit.WebHistoryItem;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {

    EditText url;
    ImageButton home;
    ImageButton bookmark;
    Spinner menu;
    WebView webview;
    String urlInputted;
    String newUrlString;
    Intent homePage;
    String URL_ID;
    ProgressBar progress;
    TextView historyText;
    ImageButton backFromHistory;
    String TAG;
    ArrayList<String> historyList;





    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(URL_ID, webview.getUrl());
        super.onSaveInstanceState(outState);
        Log.d("url", webview.getUrl().toString());

    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        urlInputted = savedInstanceState.getString(URL_ID);
        super.onRestoreInstanceState(savedInstanceState);
        Log.d("onRestore", urlInputted);
    }



    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                urlInputted = null;
            } else {
                urlInputted = extras.getString("url");
            }
        }

        setContentView(R.layout.activity_main);



        url = (EditText) findViewById(R.id.url_main_activity);
        url.setText(urlInputted);
        home = (ImageButton) findViewById(R.id.home_button);
        bookmark = (ImageButton) findViewById(R.id.bookmark_button);
        progress = (ProgressBar)findViewById(R.id.progressBar);

        webview = (WebView) findViewById(R.id.web);

        webview.setWebViewClient(new WebViewClient(){

            public void onPageStarted(WebView view, String urlString, Bitmap favicon) {
                progress.setVisibility(view.VISIBLE);
                super.onPageStarted(view, urlString, favicon);
            }


            public  void  onPageFinished(WebView view, String urlString){
                progress.setVisibility(view.GONE);
                url.setText(webview.getUrl());
                super.onPageFinished(view, urlString);
            }

//            @Override
//            public  boolean shouldOverrideUrlLoading (WebView view, String urlString ){
//                if (urlString.contains("mailto:")){
//                    Intent i = new Intent(Intent.ACTION_SEND, Uri.parse(urlString));
//                    startActivity(i);
//                } else  {
//                    view.loadUrl(urlString);
//                }
//
//                return true;
//            }
        });

        final WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webview.loadUrl(urlInputted);

        homePage= (Intent) new Intent(this, HomePage.class);



        menu = (Spinner) findViewById(R.id.menu_main);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.menu_array));

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        menu.setAdapter(adapter);

        menu.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                        int selected = menu.getSelectedItemPosition();

                        if (selected == 1) {
                            if (webview.canGoForward() == true) {
                                webview.goForward();
                            }
                        }
                        if (selected == 2) {
                            setContentView(R.layout.bookmarks);

                        }
                        if (selected == 3) {

                            setContentView(R.layout.history);

                            historyText = (TextView) findViewById(R.id.historyXML);

                            backFromHistory = (ImageButton) findViewById(R.id.back_from_history);

                            backFromHistory.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {


                                }
                            });

                            getHistory(webview, historyText);



                        }

                        if (menu.getSelectedItem().toString().equalsIgnoreCase("Home")) {
                            startActivity(homePage);
                        }
                    }



                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

        /*
         Setting the action that happens after typing the website
         so the user does not have to click the go button
          */
        url.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    newUrlString = url.getText().toString();
                    String enteredURL = HomePage.checkURL(newUrlString);
                    Toast.makeText(MainActivity.this, enteredURL, Toast.LENGTH_SHORT).show();
                    webview.loadUrl(enteredURL);
                }
                return true;
            }
        });

        url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url.setSelection(url.getText().length(), 0);
            }

        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(homePage);
            }
        });


    }

    @Override
    public void onBackPressed() {
        if (webview.canGoBack() == true) {
            webview.goBack();
        }

        else {
            finish();
            super.onBackPressed();
        }
    }






    protected void getHistory(WebView view, TextView tv){

        WebBackForwardList history = view.copyBackForwardList();
        historyList = new ArrayList<>();
        for (int i=0; i<history.getSize();i++){
            WebHistoryItem item = history.getItemAtIndex(i);
            String urlTitle = item.getTitle();
            String urlInHistory = item.getUrl();
            tv.append(urlTitle + "\n" + urlInHistory + "\n\n");

        }
    }





    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "On Start:");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "On Resume:");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "On Pause:");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "On Stop:");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "On Destroy:");
    }
}
