package com.example.ordi.dbpediaimagebrowser;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    EditText editText;
    String title;
    JsonParser js =new JsonParser();
    String params[]=new String[2];
    String SPARQL1="http://demo.openlinksw.com/sparql?default-graph-uri=http%3A%2F%2Fdbpedia.org%2Fdata%2F";//uses openlinks
    String SPARQL2=".rdf&query=PREFIX+foaf%3A++<http%3A%2F%2Fxmlns.com%2Ffoaf%2F0.1%2F>%0D%0APREFIX+dbo%3A+<http%3A%2F%2Fdbpedia.org%2Fontology%2F>%0D%0ASELECT+%3Fname%0D%0AWHERE+%7B%0D%0A++++%3Fperson+dbo%3Athumbnail++%3Fname+.%0D%0A%7D&should-sponge=soft&format=application%2Fsparql-results%2Bjson&timeout=20000";
    String SEARCH1="https://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=";
    String SEARCH2="&continue&format=json";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        editText=(EditText)findViewById(R.id.edittext);
        imageView =(ImageView)findViewById(R.id.imageview);

        editText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                Log.d("isPressed",KeyEvent.keyCodeToString(66));
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    params[1]="search";
                     String q = String.valueOf(editText.getText());
                    try {
                        params[0] = SEARCH1 + URLEncoder.encode(q, "UTF-8") + SEARCH2;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } LongOperation op = new LongOperation();
                    op.execute(params);
                    return true;
                }
                return false;
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }












    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String aString = null;
            try {
                URL url = new URL(urls[0]);
                Log.d("url", "URLS@ :" + urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    String inputStreamString = new Scanner(in, "UTF-8").useDelimiter("\\A").next();
                    aString = inputStreamString;

                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                Log.d("prob", e.toString());
            }


            return aString;
        }


        @Override
        protected void onPostExecute(String result) {
            if (params[1].equals("imageput")) {

                Glide.with(getApplicationContext()).load(js.getdbpediaimage(result)+"&width=600").into(imageView);


            }
            if(params[1].equals("search")){


                if(js.suggestion(result).equals("nosuggestion")) {//uses the mediawiki api


                    params[1]="imageput";
                    title=js.gettitle(result);
                    try {
                        params[0]=SPARQL1+URLEncoder.encode(title.replaceAll(" ","_"),"UTF-8").replaceAll("%2B","_")+SPARQL2;
                        Log.d("params",js.suggestion(result));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }


                }else {

                    try {

                        params[0] =SEARCH1+ URLEncoder.encode(js.suggestion(result).replaceAll(" ","_"), "UTF-8").replaceAll("%2B","_")+SEARCH2;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                new LongOperation().execute(params);

            }
        }
    }





}
