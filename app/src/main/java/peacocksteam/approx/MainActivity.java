package peacocksteam.approx;

import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebResourceRequest;


import android.net.Uri;
import android.webkit.GeolocationPermissions;


import android.util.Log;

import android.content.Context;

import android.content.DialogInterface.OnClickListener;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.Intent;
import android.widget.TextView;

import android.annotation.TargetApi;
import android.os.Build;
import android.graphics.Bitmap;
import android.widget.Toast;

import java.lang.String;

import static android.widget.Toast.LENGTH_SHORT;
//import static com.example.albertosil.provageohtml.R.styleable.View;
import static java.lang.System.currentTimeMillis;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import android.view.View;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import static java.lang.System.currentTimeMillis;



public class MainActivity extends AppCompatActivity {

    /**
     * WebViewClient subclass loads all hyperlinks in the existing WebView
     */
    public class GeoWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // When user clicks a hyperlink, load in the existing WebView
            view.loadUrl(url);
            return true;
        }
    }//end GeoWebViewClient

    /**
     * WebChromeClient subclass handles UI-related calls
     * Note: think chrome as in decoration, not the Chrome browser
     */
    public class GeoWebChromeClient extends WebChromeClient {
        private static final String TAG = "MyActivity";
        @Override
        public void onGeolocationPermissionsShowPrompt(final String origin, final GeolocationPermissions.Callback callback) {
            Log.i(TAG, "onGeolocationPermissionsShowPrompt()");

            final boolean remember = false;
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Permís geolocalització");
            builder.setMessage("Dones permís per consultar ubicació? ")
                    .setCancelable(true).setPositiveButton("Permetre", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // origin, allow, remember
                    callback.invoke(origin, true, remember);
                }//end onClick
            }).setNegativeButton("No permetre", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // origin, allow, remember
                    callback.invoke(origin, false, remember);
                }//end onClick
            });//end setNavigation...
            AlertDialog alert = builder.create();
            alert.show();
        }//end GeolocationPermissions...
    }//end GeoWebCHrome...


    WebView mWebView;
    TextView txt_sup_cat;
    TextView txt_sup_cas;
    TextView txt_inf_cat;
    TextView txt_inf_cas;
    private boolean isTouch = false;

    String url= "https://www.puertasmarcam.es/prueba/index.html";

    /** Called when the activity is first created. */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt_inf_cas = (TextView)findViewById(R.id.TXT_inf_cas);
        txt_sup_cas = (TextView)findViewById(R.id.TXT_sup_cas);
        txt_inf_cat = (TextView)findViewById(R.id.TXT_inf_cat);
        txt_sup_cat = (TextView)findViewById(R.id.TXT_sup_cat);

        mWebView = (WebView) findViewById(R.id.webView);
        // Brower niceties -- pinch / zoom, follow links in place
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.setWebViewClient(new GeoWebViewClient());
        // Below required for geolocation
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setGeolocationEnabled(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setDatabaseEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        mWebView.setWebChromeClient(new GeoWebChromeClient());
        // Load google.com
        boolean permissionGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if(permissionGranted) {
            mWebView.loadUrl("https://www.puertasmarcam.es/prueba/index.html");

        }//end if
        else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 200);
            //mWebView.loadUrl("https://www.puertasmarcam.es/prueba/index.html");
        }//end else

                //funció per detectar si es toca el webview
        mWebView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.w("myApp2", "no network");
                       // Log.w("myApp23", mWebView.getTitle());
                        if(isNetDisponible()==false){
                            mWebView.setVisibility(WebView.GONE);
                        }//end if

                        url = mWebView.getUrl();

                        //condicional per mostra el missatge en l'idioma de la web
                        if(url.contains("cas/index.html")){

                            Log.w("myApp24", "no network");

                            txt_sup_cat.setVisibility(TextView.GONE);
                            txt_inf_cat.setVisibility(TextView.GONE);
                            txt_sup_cas.setVisibility(TextView.VISIBLE);
                            txt_inf_cas.setVisibility(TextView.VISIBLE);

                        }//end if
                        else {
                            txt_sup_cat.setVisibility(TextView.VISIBLE);
                            txt_inf_cat.setVisibility(TextView.VISIBLE);
                            txt_sup_cas.setVisibility(TextView.GONE);
                            txt_inf_cas.setVisibility(TextView.GONE);
                            Log.w("myApp24", "no network");
                        }

                        //no hi ha else perquè sino hi ha wevbiew no pot detectar que es toca
                        //else { myWebView.setVisibility(WebView.VISIBLE);}//end else
                        break;
                    case MotionEvent.ACTION_UP:
                        if (!v.hasFocus()) {
                            v.requestFocus();
                        }//end if
                        break;
                }//end switch
                return false;
            }
        });//end setOntouchListener

    }//end onCreate

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 200: {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // {Some Code}
                    //Log.w("hola", "result");
                    mWebView.loadUrl("https://www.puertasmarcam.es/prueba/index.html");
                }//end if
            }//end case
        }//end switch
    }//end onRequest...


    //funció per detectar si hi ha moviment (amb el webview per sobra no capta moviment
    @Override
    public boolean onTouchEvent (MotionEvent event){

        //  myWebView = (WebView) findViewById(R.id.webView);

        int X = (int) event.getX();
        int Y = (int) event.getY();
        int eventaction = event.getAction();

        switch (eventaction) {
            case MotionEvent.ACTION_DOWN:
                //Toast.makeText(this, "ACTION_DOWN AT COORDS "+"X: "+X+" Y: "+Y, Toast.LENGTH_SHORT).show();
                Log.w("myApp", "no network");
                isNetDisponible();
                Log.e("netHabilitada", Boolean.toString(isNetDisponible()));
                // Log.e("accInternet", Boolean.toString(isOnlineNet()));
                if(isNetDisponible()==true){
                    mWebView.setVisibility(WebView.VISIBLE);
                }//end if
                isTouch = true;
                break;

        }//end switch
        return true;
    }//end onTouch

    //Funció per detectar si les xarxes estan habilitades
    private boolean isNetDisponible() {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo actNetInfo = connectivityManager.getActiveNetworkInfo();

        return (actNetInfo != null && actNetInfo.isConnected());
    }// end isNetDisponible

    @Override
    public void onBackPressed() {
        // Pop the browser back stack or exit the activity
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        }//end if
        else {
            super.onBackPressed();
        }//end else
    }//end onBackPressed
}//end Class
