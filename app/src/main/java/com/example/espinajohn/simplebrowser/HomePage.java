package com.example.espinajohn.simplebrowser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class HomePage extends AppCompatActivity {

    EditText url;
    Button go;
    String urlString;
    String TEXT_ID;
    Spinner menu;


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(TEXT_ID, url.getText().toString());
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage_activity);

        // Setting up the Menu Spinner
        menu = (Spinner) findViewById(R.id.menu);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(HomePage.this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.menu_array_home));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        menu.setAdapter(adapter);
        menu.setOnItemSelectedListener(

                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(menu.getSelectedItem().toString().equalsIgnoreCase("Bookmarks")){
                            setContentView(R.layout.bookmarks);
                        }
                        if (menu.getSelectedItem().toString().equalsIgnoreCase("History")){
                            setContentView(R.layout.bookmarks);
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

        // Setting up the url box
        url = (EditText) findViewById(R.id.url_editText);

        // Go Button to process the web
       // go = (Button) findViewById(R.id.go_button);

        final Intent main = new Intent(this, MainActivity.class);

        if (savedInstanceState != null) {
            url.setText(savedInstanceState.getString(TEXT_ID, ""));
        }

        // Setting the action for the GO button
//        go.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                urlString = url.getText().toString();
//                String enteredURL = new String (checkURL(urlString));
//                String urlStringID = "url";
//                Toast.makeText(HomePage.this, enteredURL, Toast.LENGTH_SHORT).show();
//                main.putExtra(urlStringID,enteredURL);
//                startActivity(main);
//            }
//        });

        // Setting the action after typing the website so user does not have to click the button
        url.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    urlString = url.getText().toString();
                    String enteredURL = new String (checkURL(urlString));
                    String urlStringID = "url";
                    main.putExtra(urlStringID,enteredURL);
                    startActivity(main);
                    Toast.makeText(HomePage.this, enteredURL, Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });


    }

    protected static String checkURL(String url){
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
}
