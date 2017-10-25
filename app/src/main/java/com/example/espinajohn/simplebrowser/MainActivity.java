package com.example.espinajohn.simplebrowser;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebBackForwardList;
import android.webkit.WebHistoryItem;
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

public class MainActivity extends AppCompatActivity {

    private EditText urlBox;
    private ImageButton homeImageButton;
    private ImageButton bookmarkImageButton;
    private Spinner menuSpinner;
    private WebView webview;
    private String urlString;
    private String newUrlString;
    private String URL_ID;
    private ProgressBar progressIcon;
    private ImageButton backFromHistory;
    private TextView historyTextView;
    private TextView historyTextViewHeader;
    private LayoutInflater inflater;
    private Boolean outsideWebview;
    private View mainPage;
    private ImageButton refreshButton;
    private TextView clearHistory;
    private ImageButton deleteHistoryButton;
    private ArrayList<String> bookmarkList;
    private Boolean bookmarked;
    private ImageButton starred;
    private TextView dialogBox;
    private Button yes;
    private Button cancel;



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(getURL_ID(), getWebview().getUrl());
        getWebview().saveState(outState);
        super.onSaveInstanceState(outState);


    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        setUrlString(savedInstanceState.getString(getURL_ID()));
        getWebview().restoreState(savedInstanceState);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        /*
        * Inflating each layouts so each can easily be called and used later
        * */
        setInflater(getLayoutInflater());
        setMainPage(getInflater().inflate(R.layout.activity_main, null));
        final View historyPage = getInflater().inflate(R.layout.history, null);
        final View bookmarksPage = getInflater().inflate(R.layout.bookmarks, null);
        ConstraintLayout.LayoutParams default_layout_params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);

        /*
        * Adding the views altogether
        * */
        addContentView(getMainPage(), default_layout_params);
        addContentView(historyPage, default_layout_params);
        addContentView(bookmarksPage, default_layout_params);

        setContentView(getMainPage());

        setOutsideWebview(false);
        setBookmarkList(new ArrayList<String>());

        // Setting the objects inside the Main Page
        setUrlBox((EditText) findViewById(R.id.url_main_activity));
        setHomeImageButton((ImageButton) findViewById(R.id.home_button));
        setBookmarkImageButton((ImageButton) findViewById(R.id.bookmark_button));
        setProgressIcon((ProgressBar)findViewById(R.id.progressBar));
        setRefreshButton((ImageButton) findViewById(R.id.refresh_button));
        setStarred((ImageButton)findViewById(R.id.starred));

        /*
        * setting up the webview
        * */
        setWebview((WebView) findViewById(R.id.web));
        final WebSettings webSettings = getWebview().getSettings();
        webSettings.setJavaScriptEnabled(true); // This enables javascript powered site to be rendered in the webview
        webSettings.setBuiltInZoomControls(true); // This will allow the webview to be zoomed in and out
        webSettings.setDisplayZoomControls(false);


        getWebview().setWebViewClient(new WebViewClient(){

            public void onPageStarted (WebView view, String urlString, Bitmap faveicon){
                getRefreshButton().setVisibility(View.GONE);
                getProgressIcon().setVisibility(View.VISIBLE);
                checkIfBookmarked();
            }

            public  void  onPageFinished(WebView view, String urlString){
                getProgressIcon().setVisibility(view.GONE);
                String currentURL = getWebview().getUrl();
                getUrlBox().setText(currentURL);
                getRefreshButton().setVisibility(View.VISIBLE);
            }


            @Override
            public  boolean shouldOverrideUrlLoading (WebView view, String urlString ){

                // This will open the phones email app
                if (urlString.contains("mailto:")){
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse(urlString));
                    startActivity(emailIntent);

                // This will call and open the phone app
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


        /*
        * The following sets up the Spinner which will be used as
        * the menu holder
        * */
        setMenuSpinner((Spinner) findViewById(R.id.menu_main));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.menu_array));

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        getMenuSpinner().setAdapter(adapter);

        getMenuSpinner().setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        int selected = getMenuSpinner().getSelectedItemPosition();

                        if (selected == 1) {

                            if (getWebview().canGoForward() == true) {
                                checkIfBookmarked();
                                getWebview().goForward();
                                getWebview().loadUrl(getWebview().getUrl());
                            }

                        } else if (selected == 2) {

                            /*
                            * This changes the layout using the Bookmark Layout
                            * Objects found inside the bookmark layout will need to be defined
                            * */
                            setContentView(bookmarksPage);
                            setOutsideWebview(true); // this is important boolean to be used when pressing the back button inside the webview
                            TextView bookmarkText = (TextView)findViewById(R.id.bookmarkXML);
                            TextView bookmarkHeader = (TextView)findViewById(R.id.bookmarktitle);
                            ImageButton goBack = (ImageButton) findViewById(R.id.back_from_bookmark);

                            if (getWebview().canGoForward()){
                               getWebview().goForward();

                            } else {
                                String b = "";
                                for (int x = 0; x < getBookmarkList().size(); x++) {
                                    b += getBookmarkList().get(x) + "\n";
                                }

                                bookmarkText.setText(b);
                            }

                            goBack.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    setContentView(getMainPage());
                                    setOutsideWebview(false);

                                }
                            });



                        } else if (selected ==3){

                            /**
                             * This will change the layout using the History Layout
                             *
                             */
                            setContentView(historyPage);
                            setOutsideWebview(true);

                            // Defining each ojects inside the History Layout
                            setHistoryTextViewHeader((TextView) findViewById(R.id.historyXML));
                            setBackFromHistory((ImageButton) findViewById(R.id.back_from_history));
                            setHistoryTextView((TextView)findViewById(R.id.historyXML));
                            setClearHistory((TextView)findViewById(R.id.clear_history));
                            setDeleteHistoryButton((ImageButton)findViewById(R.id.delete_button));
                            setDialogBox((TextView)findViewById(R.id.dialog));
                            setYes((Button)findViewById(R.id.yes_button));
                            setCancel((Button)findViewById(R.id.no_button));

                            getDialogBox().setVisibility(View.GONE);
                            getCancel().setVisibility(View.GONE);
                            getYes().setVisibility(View.GONE);

                            getHistory();

                            getBackFromHistory().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    setContentView(getMainPage());
                                    setOutsideWebview(false);

                                }
                            });


                            getDeleteHistoryButton().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    // Improvised dialog box using TextView and two buttons
                                    setDialogBox((TextView)findViewById(R.id.dialog));
                                    setYes((Button)findViewById(R.id.yes_button));
                                    setCancel((Button)findViewById(R.id.no_button));

                                    getDialogBox().setVisibility(View.VISIBLE);
                                    getYes().setVisibility(View.VISIBLE);
                                    getCancel().setVisibility(View.VISIBLE);

                                    getYes().setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            getWebview().clearHistory();
//                                            Intent newWebView = new Intent(MainActivity.this, MainActivity.class);
//                                            startActivity(newWebView);
                                            Toast.makeText(MainActivity.this, "Browsing history cleared", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    getCancel().setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            getDialogBox().setVisibility(View.GONE);
                                            getYes().setVisibility(View.GONE);
                                            getCancel().setVisibility(View.GONE);
                                        }
                                    });

                                }
                            });
                        }
                        getMenuSpinner().setSelection(0);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

        /*
         Setting the action that happens after typing the website
         so the user does not have to click the go button
          */
        getUrlBox().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                    setNewUrlString(getUrlBox().getText().toString());
                    String enteredURL = checkURL(getNewUrlString());
                    Toast.makeText(MainActivity.this, enteredURL, Toast.LENGTH_SHORT).show();
                    getWebview().loadUrl(enteredURL);

                }
                return true;
            }
        });

        getUrlBox().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUrlBox().setSelection(getUrlBox().getText().length(), 0);
            }

        });

        getHomeImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setContentView(getMainPage());
                getWebview().loadUrl("http://www.google.com");
            }
        });

        getRefreshButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWebview().loadUrl(getWebview().getUrl());


            }
        });

        getBookmarkImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String selectedURL = getWebview().getUrl();

                    setBookmarked(true);
                    getBookmarkImageButton().setVisibility(View.GONE);
                    getStarred().setVisibility(View.VISIBLE);
                    getBookmarkList().add(selectedURL);
                    getBookmarkList().add("\n");

                    Toast.makeText(MainActivity.this, "Bookmark added " + selectedURL, Toast.LENGTH_SHORT).show();

            }
        });


        getStarred().setOnClickListener(new View.OnClickListener() {
            String selectedURL = getWebview().getUrl();
            @Override
            public void onClick(View v) {
                setBookmarked(false);
                getBookmarkImageButton().setVisibility(View.VISIBLE);
                getStarred().setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "This page is now removed from your Bookmarks", Toast.LENGTH_SHORT).show();
                getBookmarkList().remove(selectedURL);

            }
        });
    }

    @Override
    public void onBackPressed() {

        if (getWebview().canGoBack() == true && getOutsideWebview() == false) {
            getWebview().goBack();

        /*This allows the user to go back to the Main page from the history or bookmark page
         * instead of exiting the application
         */

        } else if (getOutsideWebview() ==true){

            setContentView(getMainPage());
            setOutsideWebview(false);

        } else {
            finish();

        }
    }


    /**
     * This method will get the back and forward history of the webview
     */
    protected void getHistory(){

        WebBackForwardList history = getWebview().copyBackForwardList();

        for (int i=0; i<history.getSize();i++){
            WebHistoryItem item = history.getItemAtIndex(i);
            String urlTitle = item.getTitle();
            String urlInHistory = item.getUrl();
            getHistoryTextView().append(urlTitle + "\n" + urlInHistory + "\n\n");

        }
    }

    /**
     * This method will check/verify the inputted url
     * @param url
     * @return String
     */
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

    /**
     * This method chechs if a url is bookmarked or not
     * If it is, the star icon appears,
     * If it is not, the bookmark icon will appear
     * Each of these icons is connected to its own function and clickListener
     */
    public void checkIfBookmarked(){
        if (getBookmarkList().contains(getWebview().getUrl())){
            setBookmarked(true);
           getStarred().setVisibility(View.VISIBLE);
            getBookmarkImageButton().setVisibility(View.GONE);


        }else {
            setBookmarked(false);
            getStarred().setVisibility(View.GONE);
            getBookmarkImageButton().setVisibility(View.VISIBLE);


        }
    }


    // Getters and Setters

    public EditText getUrlBox() {
        return urlBox;
    }

    public void setUrlBox(EditText urlBox) {
        this.urlBox = urlBox;
    }

    public ImageButton getHomeImageButton() {
        return homeImageButton;
    }

    public void setHomeImageButton(ImageButton homeImageButton) {
        this.homeImageButton = homeImageButton;
    }

    public ImageButton getBookmarkImageButton() {
        return bookmarkImageButton;
    }

    public void setBookmarkImageButton(ImageButton bookmarkImageButton) {
        this.bookmarkImageButton = bookmarkImageButton;
    }

    public Spinner getMenuSpinner() {
        return menuSpinner;
    }

    public void setMenuSpinner(Spinner menuSpinner) {
        this.menuSpinner = menuSpinner;
    }

    public WebView getWebview() {
        return webview;
    }

    public void setWebview(WebView webview) {
        this.webview = webview;
    }

    public String getUrlString() {
        return urlString;
    }

    public void setUrlString(String urlString) {
        this.urlString = urlString;
    }

    public String getNewUrlString() {
        return newUrlString;
    }

    public void setNewUrlString(String newUrlString) {
        this.newUrlString = newUrlString;
    }

    public String getURL_ID() {
        return URL_ID;
    }

    public void setURL_ID(String URL_ID) {
        this.URL_ID = URL_ID;
    }

    public ProgressBar getProgressIcon() {
        return progressIcon;
    }

    public void setProgressIcon(ProgressBar progressIcon) {
        this.progressIcon = progressIcon;
    }

    public ImageButton getBackFromHistory() {
        return backFromHistory;
    }

    public void setBackFromHistory(ImageButton backFromHistory) {
        this.backFromHistory = backFromHistory;
    }

    public TextView getHistoryTextView() {
        return historyTextView;
    }

    public void setHistoryTextView(TextView historyTextView) {
        this.historyTextView = historyTextView;
    }

    public TextView getHistoryTextViewHeader() {
        return historyTextViewHeader;
    }

    public void setHistoryTextViewHeader(TextView historyTextViewHeader) {
        this.historyTextViewHeader = historyTextViewHeader;
    }

    public LayoutInflater getInflater() {
        return inflater;
    }

    public void setInflater(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    public Boolean getOutsideWebview() {
        return outsideWebview;
    }

    public void setOutsideWebview(Boolean outsideWebview) {
        this.outsideWebview = outsideWebview;
    }

    public View getMainPage() {
        return mainPage;
    }

    public void setMainPage(View mainPage) {
        this.mainPage = mainPage;
    }

    public ImageButton getRefreshButton() {
        return refreshButton;
    }

    public void setRefreshButton(ImageButton refreshButton) {
        this.refreshButton = refreshButton;
    }

    public TextView getClearHistory() {
        return clearHistory;
    }

    public void setClearHistory(TextView clearHistory) {
        this.clearHistory = clearHistory;
    }

    public ImageButton getDeleteHistoryButton() {
        return deleteHistoryButton;
    }

    public void setDeleteHistoryButton(ImageButton deleteHistoryButton) {
        this.deleteHistoryButton = deleteHistoryButton;
    }

    public ArrayList<String> getBookmarkList() {
        return bookmarkList;
    }

    public void setBookmarkList(ArrayList<String> bookmarkList) {
        this.bookmarkList = bookmarkList;
    }

    public Boolean getBookmarked() {
        return bookmarked;
    }

    public void setBookmarked(Boolean bookmarked) {
        this.bookmarked = bookmarked;
    }

    public ImageButton getStarred() {
        return starred;
    }

    public void setStarred(ImageButton starred) {
        this.starred = starred;
    }

    public TextView getDialogBox() {
        return dialogBox;
    }

    public void setDialogBox(TextView dialogBox) {
        this.dialogBox = dialogBox;
    }

    public Button getYes() {
        return yes;
    }

    public void setYes(Button yes) {
        this.yes = yes;
    }

    public Button getCancel() {
        return cancel;
    }

    public void setCancel(Button cancel) {
        this.cancel = cancel;
    }
}
