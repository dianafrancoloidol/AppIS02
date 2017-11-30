package fpuna.com.py.appis02;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class HijosActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{

    private LinearLayout Prof_Section;
    private Button SignOut;
    private TextView Name, Email;
    private ImageView Prof_pic;
    private GoogleApiClient googleApiClient;
    private static final int REQ_CODE = 9001;

    private RecuperarHijos recuperarHijos = null;
    private String resultado = "";
    private int pUsuarioId = 0;
    private TableLayout tablaHijos;
    JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hijos);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Prof_Section = (LinearLayout)findViewById(R.id.prof_section);
        SignOut = (Button)findViewById(R.id.btn_logout_);
        Name = (TextView) findViewById(R.id.name_);
        Email = (TextView) findViewById(R.id.email_);
        Prof_pic = (ImageView)findViewById(R.id.prof_pic_);
        SignOut.setOnClickListener(this);
        Bundle datos = this.getIntent().getExtras();
        Name.setText(datos.getString("name"));
        Email.setText(datos.getString("email"));
        pUsuarioId = datos.getInt("usuarioId");
        Glide.with(this).load(datos.getString("foto")).into(Prof_pic);
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions).build();

        tablaHijos = (TableLayout) findViewById(R.id.tabla_hijos);
        recuperarHijos = new RecuperarHijos();
        recuperarHijos.execute();
    }

    protected void cargarTablaHijos(){
        if(tablaHijos.getChildCount() > 1){
            int filas = tablaHijos.getChildCount();

            tablaHijos.removeViews(1, filas-1);
        }

        Integer count=0;
        // Create the table row
        if(jsonArray.length() != 0){
            for (int i=0; i<jsonArray.length(); i++){
                try {
                    JSONObject json = jsonArray.getJSONObject(i);

                    TableRow tr = new TableRow(this);
                    //if(count%2!=0){ tr.setBackgroundColor(Color.WHITE);}else{tr.setBackgroundColor(Color.GREEN);};
                    tr.setId(100+count);
                    tr.setLayoutParams(new TableLayout.LayoutParams(
                            TableRow.LayoutParams.FILL_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT));
                    //Create columns to add as table data
                    // Create a TextView to add date
                    TextView textCedula = new TextView(this);
                    textCedula.setId(200+count);
                    textCedula.setText(json.getString("cedula"));
                    textCedula.setPadding(2, 0, 5, 0);
                    textCedula.setTextColor(Color.BLACK);
                    tr.addView(textCedula);

                    TextView textNombre = new TextView(this);
                    textNombre.setId(200+count);
                    textNombre.setText(json.getString("nombre"));
                    textNombre.setPadding(2, 0, 5, 0);
                    textNombre.setTextColor(Color.BLACK);
                    tr.addView(textNombre);

                    TextView textApellido = new TextView(this);
                    textApellido.setId(200+count);
                    textApellido.setText(json.getString("apellido"));
                    textApellido.setPadding(2, 0, 5, 0);
                    textApellido.setTextColor(Color.BLACK);
                    tr.addView(textApellido);

                    SimpleDateFormat parseador = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");

                    Date date = parseador.parse(json.getString("fechaNacimiento"));
                    TextView textNacimiento = new TextView(this);
                    textNacimiento.setId(200+count);
                    textNacimiento.setText(formateador.format(date));
                    textNacimiento.setPadding(2, 0, 5, 0);
                    textNacimiento.setTextColor(Color.BLACK);
                    tr.addView(textNacimiento);

                    TextView textSexo = new TextView(this);
                    textSexo.setId(200+count);
                    textSexo.setText(json.getInt("sexo") == 0? "M" : "F");
                    textSexo.setPadding(2, 0, 5, 0);
                    textSexo.setTextColor(Color.BLACK);
                    tr.addView(textSexo);

                    TextView textAlergia = new TextView(this);
                    textAlergia.setId(200+count);
                    textAlergia.setText(json.getString("alergia"));
                    textAlergia.setPadding(2, 0, 5, 0);
                    textAlergia.setTextColor(Color.BLACK);
                    tr.addView(textAlergia);
                    // finally add this to the table row
                    tablaHijos.addView(tr, new TableLayout.LayoutParams(
                            TableRow.LayoutParams.FILL_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT));
                    count++;
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }else{
            Toast.makeText(getApplicationContext() , "No tiene hijos.", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onClick(View view) {
        new MainActivity().signOut();
    }

    public void signOut(){
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                Intent i = new Intent(HijosActivity.this,MainActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class RecuperarHijos extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet get = new HttpGet("http://10.0.2.2:8080/AppRestIS2WS/services/usuarios/hijos/"+pUsuarioId);
            try {
                HttpResponse resp = httpClient.execute(get);
                resultado = EntityUtils.toString(resp.getEntity());
            }catch (Exception ex){
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if(!success){
                Toast.makeText(getApplicationContext() , "Error: no se recuperaron hijos para este usuario", Toast.LENGTH_LONG).show();
                cargarTablaHijos();
            }
            else{
                try {
                    jsonArray = new JSONArray(resultado);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                cargarTablaHijos();
            }
        }

        @Override
        protected void onCancelled() {
            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
        }
    }

}
