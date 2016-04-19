package br.com.ztecnologia.vambora;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;

import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.appevents.AppEventsLogger;

import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;


import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import br.com.ztecnologia.vambora.Util.FacebookAPI;
import br.com.ztecnologia.vambora.model.Evento;
import br.com.ztecnologia.vambora.model.FBObjeto;
import br.com.ztecnologia.vambora.model.Preferencias;
import br.com.ztecnologia.vambora.model.Usuario;
import br.com.ztecnologia.vambora.view.PreferenciasActivity;


public class MainActivity extends AppCompatActivity {

    private LoginButton btLoginFB;
    private CallbackManager callbackManager;

    private GoogleApiClient client;

    private Button btTeste;

    private Usuario usuario;
    private ArrayList<Evento> eventos;
    private ArrayList<String> likes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO Acertar cores de acordo com o horario

        //Iniciando o FB
        FacebookSdk.sdkInitialize(this.getApplicationContext());

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        callbackManager = CallbackManager.Factory.create();

        btLoginFB = (LoginButton) findViewById(R.id.login_button);

        //TODO Lista de permissões
       btLoginFB.setReadPermissions("user_friends");
       btLoginFB.setReadPermissions("user_actions.music");
       btLoginFB.setReadPermissions("user_location");
       btLoginFB.setReadPermissions("user_events");
       btLoginFB.setReadPermissions("user_likes");
       btLoginFB.setReadPermissions("user_about_me");

        obtemDadosLogin(btLoginFB);

        btTeste = (Button) findViewById(R.id.button);
        btTeste.setText("Dados do Usuario");
        btTeste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphRequest request = GraphRequest.newMeRequest(
                        AccessToken.getCurrentAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                // Application code
                                try {
                                    usuario = new Usuario(Long.valueOf(object.getString("id")), object.getString("name"));
//                                    usuario.setGenero(object.getString("gender"));
                                    Intent i = new Intent(MainActivity.this, PreferenciasActivity.class);
                                    i.putExtra("usuarioID",String.valueOf(usuario.getId()));
                                    i.putExtra("usuarioNome",usuario.getNome());

                                    startActivity(i);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "name,birthday,gender,location");
                request.setParameters(parameters);
                request.executeAsync();

                likes = new ArrayList<String>();
                FacebookAPI.getLikes(likes);
                Log.i("LIKES",likes.toString());

            }
        });



        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();




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


    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(this, "Clicou no botão", Toast.LENGTH_SHORT);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://br.com.ztecnologia.vambora/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://br.com.ztecnologia.vambora/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    public void obtemDadosLogin(LoginButton lb)
    {
        lb.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                new GraphRequest(
                        loginResult.getAccessToken(),
                        "/me?fields=id,name,birthday,gender,location",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                Log.i("response", response.getRawResponse()); //find the summary 'total_count' in the response
                            }
                        }
                ).executeAsync();
            }

            @Override
            public void onCancel() {
                // code for cancellation
            }

            @Override
            public void onError(FacebookException exception) {
                //  code to handle error
            }
        });
    }




}

