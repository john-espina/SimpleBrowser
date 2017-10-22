package com.example.espinajohn.simplebrowser;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebHistoryItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;



import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText urlBox;
    ImageButton homeImageButton;
    ImageButton bookmarkImageButton;
    Spinner menuSpinner;
    WebView webview;
    String urlString;
    String newUrlString;
    String URL_ID;
    ProgressBar progressIcon;
    ImageButton backFromHistory;
    TextView historyTextView;
    TextView historyTextViewHeader;
    String TAG;
    LayoutInflater inflater;
    Boolean outsideWebview;
    View mainPage;
    ImageButton refreshButton;
    TextView clearHistory;
    ImageButton deleteHistoryButton;
    ArrayList<String> bookmarkList;
    Boolean bookmarked;
    ImageButton starred;
    TextView dialogBox;
    Button yes;
    Button cancel;
    String booleanKey;



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(URL_ID, webview.getUrl());
        webview.saveState(outState);
        outState.putBoolean(booleanKey, bookmarked);
        super.onSaveInstanceState(outState);


    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        urlString = savedInstanceState.getString(URL_ID);
        webview.restoreState(savedInstanceState);
        bookmarked = savedInstanceState.getBoolean(booleanKey);
        super.onRestoreInstanceState(savedInstanceState);
    }


    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        inflater = getLayoutInflater();
        mainPage = inflater.inflate(R.layout.activity_main, null);
        final View historyPage = inflater.inflate(R.layout.history, null);
        final View bookmarksPage = inflater.inflate(R.layout.bookmarks, null);
        ConstraintLayout.LayoutParams default_layout_params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
        addContentView(mainPage, default_layout_params);
        addContentView(historyPage, default_layout_params);
        addContentView(bookmarksPage, default_layout_params);

        setContentView(mainPage);

        outsideWebview = false;
        bookmarkList = new ArrayList<>();

        // Main Page Objects
        urlBox = (EditText) findViewById(R.id.url_main_activity);
        homeImageButton = (ImageButton) findViewById(R.id.home_button);
        bookmarkImageButton = (ImageButton) findViewById(R.id.bookmark_button);
        progressIcon = (ProgressBar)findViewById(R.id.progressBar);
        refreshButton = (ImageButton) findViewById(R.id.refresh_button);
        starred = (ImageButton)findViewById(R.id.starred);


        webview = (WebView) findViewById(R.id.web);
        final WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);





        webview.setWebViewClient(new WebViewClient(){
            public void onPageStarted (WebView view, String urlString, Bitmap faveicon){
                refreshButton.setVisibility(View.GONE);
                progressIcon.setVisibility(View.VISIBLE);
                checkIfBookmarked();

            }


            public  void  onPageFinished(WebView view, String urlString){
                progressIcon.setVisibility(view.GONE);
                String currentURL = webview.getUrl();
                urlBox.setText(currentURL);
                refreshButton.setVisibility(View.VISIBLE);
            }


            @Override
            public  boolean shouldOverrideUrlLoading (WebView view, String urlString ){
                if (urlString.contains("mailto:")){
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse(urlString));
                    startActivity(emailIntent);

                } else if (urlString.startsWith("tel:")) {
                    Intent phoneIntent = new Intent(Intent.ACTION_DIAL,
                            Uri.parse(urlString));
                    startActivity(phoneIntent);

                } else  {
                    view.loadUrl(urlString);
                }
                return true;
            }
        });



        menuSpinner = (Spinner) findViewById(R.id.menu_main);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.menu_array));

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        menuSpinner.setAdapter(adapter);

        menuSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        int selected = menuSpinner.getSelectedItemPosition();

                        if (selected == 1) {

                            if (webview.canGoForward() == true) {
                                webview.goForward();
                            }

                        } else if (selected == 2) {

                            setContentView(bookmarksPage);
                            outsideWebview = true;
                            TextView bookmarkText = (TextView)findViewById(R.id.bookmarkXML);
                            TextView bookmarkHeader = (TextView)findViewById(R.id.bookmarktitle);
                            ImageButton goBack = (ImageButton) findViewById(R.id.back_from_bookmark);



                            if (webview.canGoForward()){
                               webview.goForward();
                            }
                            String b = "";
                            for (int x = 0; x < bookmarkList.size(); x++) {
                                b += bookmarkList.get(x) + "\n";
                            }

                            bookmarkText.setText(b);

                            goBack.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    setContentView(mainPage);
                                    outsideWebview=false;

                                }
                            });



                        } else if (selected ==3){

                            setContentView(historyPage);


                            outsideWebview = true;

                            historyTextViewHeader = (TextView) findViewById(R.id.historyXML);
                            backFromHistory = (ImageButton) findViewById(R.id.back_from_history);
                            historyTextView = (TextView)findViewById(R.id.historyXML);
                            clearHistory = (TextView)findViewById(R.id.clear_history);
                            deleteHistoryButton =(ImageButton)findViewById(R.id.delete_button);
                            dialogBox = (TextView)findViewById(R.id.dialog);
                            yes = (Button)findViewById(R.id.yes_button);
                            cancel = (Button)findViewById(R.id.no_button);

                            dialogBox.setVisibility(View.GONE);
                            cancel.setVisibility(View.GONE);
                            yes.setVisibility(View.GONE);

                            getHistory();

                            backFromHistory.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    setContentView(mainPage);
                                    outsideWebview=false;

                                }
                            });
                            deleteHistoryButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    dialogBox = (TextView)findViewById(R.id.dialog);
                                    yes = (Button)findViewById(R.id.yes_button);
                                    cancel = (Button)findViewById(R.id.no_button);

                                    dialogBox.setVisibility(View.VISIBLE);
                                    yes.setVisibility(View.VISIBLE);
                                    cancel.setVisibility(View.VISIBLE);

                                    yes.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent newWebView = new Intent(MainActivity.this, MainActivity.class);
                                            startActivity(newWebView);
                                            Toast.makeText(MainActivity.this, "Browsing history cleared", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    cancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialogBox.setVisibility(View.GONE);
                                            yes.setVisibility(View.GONE);
                                            cancel.setVisibility(View.GONE);
                                        }
                                    });




                                }
                            });
                        }
                        menuSpinner.setSelection(0);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

        /*
         Setting the action that happens after typing the website
         so the user does not have to click the go button
          */
        urlBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    newUrlString = urlBox.getText().toString();
                    String enteredURL = checkURL(newUrlString);
                    Toast.makeText(MainActivity.this, enteredURL, Toast.LENGTH_SHORT).show();
                    webview.loadUrl(enteredURL);


                }
                return true;
            }
        });

        urlBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                urlBox.setSelection(urlBox.getText().length(), 0);
            }

        });

        homeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setContentView(mainPage);
                webview.loadUrl("http://www.google.com");
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webview.loadUrl(webview.getUrl());


            }
        });

        bookmarkImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String selectedURL = webview.getUrl();

                    bookmarked = true;
                    bookmarkImageButton.setVisibility(View.GONE);
                    starred.setVisibility(View.VISIBLE);
                    bookmarkList.add(selectedURL);
                    bookmarkList.add("\n");

                    Toast.makeText(MainActivity.this, "Bookmark added " + selectedURL, Toast.LENGTH_SHORT).show();

            }
        });


        starred.setOnClickListener(new View.OnClickListener() {
            String selectedURL = webview.getUrl();
            @Override
            public void onClick(View v) {
                bookmarked = false;
                bookmarkImageButton.setVisibility(View.VISIBLE);
                starred.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "This page is now removed from your Bookmarks", Toast.LENGTH_SHORT).show();
                bookmarkList.remove(selectedURL);

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (webview.canGoBack() == true && outsideWebview == false) {
            webview.goBack();

        } else if (outsideWebview==true){

            setContentView(mainPage);
            outsideWebview= false;

        } else {
            finish();

        }
    }



    protected void getHistory(){

        WebBackForwardList history = webview.copyBackForwardList();

        for (int i=0; i<history.getSize();i++){
            WebHistoryItem item = history.getItemAtIndex(i);
            String urlTitle = item.getTitle();
            String urlInHistory = item.getUrl();
            historyTextView.append(urlTitle + "\n" + urlInHistory + "\n\n");

        }
    }

    protected  String checkURL(String url){
        if (url.startsWith("http://")){
            url = url;
        }

        if (url.startsWith("www")){
            url = "http://" + url;
        }

        if (!url.contains(".") && (!url.equalsIgnoreCase("google"))){
            url = "http://www.google.com/#q=" + url;return url;
        }

        if (!url.startsWith("www") && (!url.startsWith("http")) && (!url.equalsIgnoreCase("google"))){
            url = "http://www." + url;
        }
        if (url.equalsIgnoreCase("google")){
            url = "http://www." + url + ".com";
        }

        return url;
    }

    public void checkIfBookmarked(){
        if (bookmarkList.contains(webview.getUrl())){
            bookmarked = true;
           starred.setVisibility(View.VISIBLE);
            bookmarkImageButton.setVisibility(View.GONE);


        }else {
            bookmarked = false;
            starred.setVisibility(View.GONE);
            bookmarkImageButton.setVisibility(View.VISIBLE);


        }
    }
}
