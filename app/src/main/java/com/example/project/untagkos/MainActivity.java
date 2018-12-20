package com.example.project.untagkos;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.NetworkError;
import com.android.volley.error.NoConnectionError;
import com.android.volley.error.ParseError;
import com.android.volley.error.ServerError;
import com.android.volley.error.TimeoutError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import control.AppController;
import control.Link;
import control.RequestPermissionHandler;
import fragment.FHome;
import fragment.FListKos;
import model.ColHomeDetail;
import session.SessionManager;

import static android.view.Gravity.START;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private SessionManager session;
    private HashMap<String, Object> user;
    private Object sid,snama,sstatus,sNamaLengkap,sTelp,sEmail;
    private DrawerLayout drawer;
    private ArrayList<ColHomeDetail> columnlist= new ArrayList<ColHomeDetail>();
    private String getData	="getListAllKos.php";
    private boolean doubleBackToExitPressedOnce = false;
    private LocationManager locationManager ;
    private boolean GpsStatus ;
    private RequestPermissionHandler mRequestPermissionHandler;
    private List<String> listPermissionsNeeded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mRequestPermissionHandler = new RequestPermissionHandler();
            checkAndRequestPermissions();
            openPermission();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session =new SessionManager(getApplicationContext());
        try {
            user = session.getUserDetails();
            sid		= user.get(SessionManager.sid);
            snama	= user.get(SessionManager.sUserName);
            sstatus = user.get(SessionManager.sStatusLogin);
            sNamaLengkap = user.get(SessionManager.sNamaLengkap);
            sTelp = user.get(SessionManager.sTelp);
            sEmail = user.get(SessionManager.sEmail);
        } catch (Exception e) {
            e.getMessage();
        }

        if(sstatus.equals("USER")){
            menuUser();
            FirebaseMessaging.getInstance().subscribeToTopic(String.valueOf(sid)+"U");
        }else{
            menuCust();
            FirebaseMessaging.getInstance().subscribeToTopic(String.valueOf(sid)+"O");
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(sstatus==null){
            session.logoutUser();
        }else if(sstatus.equals("USER")){
            changeFragment2ListUploadUserKriteria(new FHome(), String.valueOf(sid), "ALL");
        }else if(sstatus.equals("CUST")){
            //changeFragment(new FAwalCust());
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }else{
            if(sstatus.equals("USER")){
                changeFragment2ListUploadUserKriteria(new FHome(), String.valueOf(sid), "ALL");
            }
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            /*Intent i = new Intent(this, SearchKriteriaKos.class);
            startActivityForResult(i,2);*/
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.masterKos) {
            //changeFragmentListUploadKriteria(new FHomeKos(), String.valueOf(sid), "1");
        } else if (id == R.id.menuHomeAwal) {
            changeFragment2ListUploadUserKriteria(new FHome(), String.valueOf(sid), "ALL");
        } else if (id == R.id.bookingKos) {
            //changeFragmentListUploadKriteria(new FHomeKos(), String.valueOf(sid), "2");
        } else if (id == R.id.masterbank) {
            //changeFragmentListCustKriteria(new FCustRekening(), String.valueOf(sid));
        } else if (id == R.id.dataSewaKos) {//List penyewa
            //changeFragmentListUploadKriteria(new FHomeKos(), String.valueOf(sid), "3");
        } else if (id == R.id.menuListKos) {
            changeFragment2ListUploadUserKriteria(new FListKos(), String.valueOf(sid), "ALL");
        } else if (id == R.id.menuBooking) {
            //changeFragmentListUploadUserKriteria(new FListBookingUser(), String.valueOf(sid));
        } else if (id == R.id.menuMap) {
            CheckGpsStatus();
            if(GpsStatus == true){
                getDataUpload(Link.FilePHP+getData);
            }else {
                Toast.makeText(MainActivity.this,
                        "GPS harap diaktifkan terlebih dahulu!", Toast.LENGTH_LONG)
                        .show();
            }
        } else if(id == R.id.menuLogout){
            if(sstatus.equals("USER")){
                FirebaseMessaging.getInstance().unsubscribeFromTopic(String.valueOf(sid)+"U");
            }else{
                FirebaseMessaging.getInstance().unsubscribeFromTopic(String.valueOf(sid)+"O");
            }
            finish();
            session.logoutUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void CheckGpsStatus(){
        locationManager = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void menuCust(){
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.menuHomeAwal).setVisible(false);
        nav_Menu.findItem(R.id.menuListKos).setVisible(false);
        nav_Menu.findItem(R.id.menuListImage).setVisible(false);
        nav_Menu.findItem(R.id.menuBooking).setVisible(false);
        nav_Menu.findItem(R.id.menuMap).setVisible(false);

        nav_Menu.findItem(R.id.masterKos).setVisible(true);
        nav_Menu.findItem(R.id.bookingKos).setVisible(true);
        nav_Menu.findItem(R.id.masterbank).setVisible(true);
        //list penyewa
        nav_Menu.findItem(R.id.dataSewaKos).setVisible(true);
    }

    private void menuUser(){
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.menuHomeAwal).setVisible(true);
        nav_Menu.findItem(R.id.menuListKos).setVisible(true);
        nav_Menu.findItem(R.id.menuListImage).setVisible(false);
        nav_Menu.findItem(R.id.menuBooking).setVisible(true);
        nav_Menu.findItem(R.id.menuMap).setVisible(true);

        nav_Menu.findItem(R.id.masterKos).setVisible(false);
        nav_Menu.findItem(R.id.bookingKos).setVisible(false);
        nav_Menu.findItem(R.id.masterbank).setVisible(false);
        nav_Menu.findItem(R.id.dataSewaKos).setVisible(false);
    }

    private void changeFragment(Fragment targetFragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.FrmMainMenu, targetFragment)
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .setCustomAnimations(R.anim.blink, R.anim.fade_in)
                .commit();
        drawer.closeDrawer(START);
    }

    private void changeFragmentListUploadKriteria(Fragment targetFragment, String idCust, String menu){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.FrmMainMenu, targetFragment)
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .setCustomAnimations(R.anim.blink, R.anim.fade_in)
                .commit();
        Bundle extras = new Bundle();
        extras.putString("idCust", idCust);
        extras.putString("status", menu);
        targetFragment.setArguments(extras);
        drawer.closeDrawer(START);
    }

    private void changeFragmentListCustKriteria(Fragment targetFragment, String idCust){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.FrmMainMenu, targetFragment)
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .setCustomAnimations(R.anim.blink, R.anim.fade_in)
                .commit();
        Bundle extras = new Bundle();
        extras.putString("idCust", idCust);
        targetFragment.setArguments(extras);
        drawer.closeDrawer(START);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        /*if ((keyCode == KeyEvent.KEYCODE_BACK)) {

            //finish();
        }*/
        return super.onKeyDown(keyCode, event);
    }

    private void changeFragmentListUploadUserKriteria(Fragment targetFragment, String idUser){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.FrmMainMenu, targetFragment)
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .setCustomAnimations(R.anim.blink, R.anim.fade_in)
                .commit();
        Bundle extras = new Bundle();
        extras.putString("idUser", idUser);
        targetFragment.setArguments(extras);
        drawer.closeDrawer(START);
    }

    private void changeFragment2ListUploadUserKriteria(Fragment targetFragment, String idUser, String sql){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.FrmMainMenu, targetFragment)
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .setCustomAnimations(R.anim.blink, R.anim.fade_in)
                .commit();
        Bundle extras = new Bundle();
        extras.putString("idUser", idUser);
        extras.putString("sql", sql);
        targetFragment.setArguments(extras);
        drawer.closeDrawer(START);
    }

    private void getDataUpload(String Url){
        JsonObjectRequest jsonget = new JsonObjectRequest(Request.Method.GET, Url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int sucses= response.getInt("success");
                            if (sucses==1){
                                JSONArray JsonArray = response.getJSONArray("kriteriaKos");
                                for (int i = 0; i < JsonArray.length(); i++) {
                                    JSONObject object = JsonArray.getJSONObject(i);
                                    ColHomeDetail colums 	= new ColHomeDetail();
                                    colums.setId_kos(object.getInt("id_kos"));
                                    colums.setId_cust(object.getInt("id_cust"));
                                    colums.setId_sewaStatus(object.getInt("id_sewaStatus"));
                                    colums.setNamaKos(object.getString("namaKos"));
                                    colums.setAlamat(object.getString("alamat"));
                                    colums.setGambar(object.getString("gambar"));
                                    colums.setLebar(object.getInt("lebar"));
                                    colums.setPanjang(object.getInt("panjang"));
                                    colums.setStatusListrik(object.getString("statusListrik"));
                                    colums.setJmlhKamar(object.getInt("jmlhKamar"));
                                    colums.setFasilitas(object.getString("fasilitas"));
                                    colums.setLatitude(object.getDouble("latitude"));
                                    colums.setLongtitude(object.getDouble("longtitude"));
                                    colums.setHarga(object.getInt("harga"));
                                    colums.setNamaCust(object.getString("namaCust"));
                                    colums.setTlpCust(object.getString("tlpCust"));
                                    colums.setEmailCust(object.getString("emailCust"));
                                    colums.setGambar2(object.getString("gambar2"));
                                    colums.setGambar3(object.getString("gambar3"));
                                    colums.setGambar4(object.getString("gambar4"));
                                    colums.setGambar5(object.getString("gambar5"));
                                    colums.setKodeKota(object.getString("kodeKota"));
                                    colums.setSisa(object.getInt("sisa"));
                                    colums.setRating(object.getDouble("total_rating"));
                                    colums.setCountUser(object.getInt("count_user"));
                                    // list gmbar bl
                                    columnlist.add(colums);
                                }
                                /*Gson gson = new Gson();
                                Intent i = new Intent(getApplication(), MapAllKos.class);
                                String jsonString = gson.toJson(columnlist);
                                i.putExtra("list", jsonString);
                                i.putExtra("idUser", String.valueOf(sid))
                                startActivityForResult(i,3);*/
                            }else{
                                Toast.makeText(getApplicationContext(),"Data tidak ada!", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(),"Check Koneksi Internet Anda", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(getApplicationContext(),"AuthFailureError", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(getApplicationContext(),"Check ServerError", Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(getApplicationContext(),"Check NetworkError", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(getApplicationContext(),"Check ParseError", Toast.LENGTH_LONG).show();
                }
            }
        }){
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("querysql", "ALL");
                return params;
            }
            @Override
            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                java.util.Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/json");
                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().invalidate(Url, true);
        AppController.getInstance().addToRequestQueue(jsonget);
    }

    private void checkAndRequestPermissions() {
        int writeExtStorage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readExtStorage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        listPermissionsNeeded = new ArrayList<>();
        if (writeExtStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (readExtStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    private void openPermission(){
        if (!listPermissionsNeeded.isEmpty()) {
            mRequestPermissionHandler.requestPermission(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    123, new RequestPermissionHandler.RequestPermissionListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(MainActivity.this, "Request permission success", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailed() {
                            Toast.makeText(MainActivity.this, "Request permission failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if(resultCode == RESULT_OK) {
                changeFragment2ListUploadUserKriteria(new FListKos(), String.valueOf(sid), data.getStringExtra("sql"));
            }
        }else if(requestCode == 3){
            if(resultCode == RESULT_OK) {
                changeFragment2ListUploadUserKriteria(new FListKos(), String.valueOf(sid), "ALL");
            }
        }
    }
}
